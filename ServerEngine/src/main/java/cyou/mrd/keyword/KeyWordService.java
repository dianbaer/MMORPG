package cyou.mrd.keyword;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.data.Data;
import cyou.mrd.data.DataKeys;
import cyou.mrd.service.Service;

public class KeyWordService implements Service{
	private static final Logger log = LoggerFactory.getLogger(KeyWordService.class);
	private Map<String,Integer> keyWords = new HashMap<String,Integer>();
	
	public static int DEFAULT_KEY = 0;

	public String getId() {
		return "KeyWordService";
	}

	public void startup() throws Exception {
		loadKeyWords();
	}

	public void shutdown() throws Exception {
		
	}
	
	private void loadKeyWords(){
		List<KeyWord> list = Platform.getEntityManager().query("from KeyWord");
		if(list != null && list.size() > 0){
			log.info("[KeyWord] load[{}]", list.size());
			long t1 = System.currentTimeMillis();
			for(KeyWord k:list){
				Data data = Platform.dataCenter().getData(DataKeys.keyWordKey(k.getWord()));
				if(data != null){
					int id = (Integer)data.value;
					keyWords.put(k.getWord(), id);
					log.info("load keyword from memCache, keyWord:{},id:{}",k.getWord(),id);
				}else{
					int id = 0;
					if(k.getSpecialId() != DEFAULT_KEY){
						id = k.getSpecialId();
					}else{
						id = k.getId();
					}
					boolean ret = Platform.dataCenter().sendNewData(DataKeys.keyWordKey(k.getWord()), id);
					if(!ret){
						Data newData = Platform.dataCenter().getData(DataKeys.keyWordKey(k.getWord()));
						if(newData != null){
							int newId = (Integer)newData.value;
							keyWords.put(k.getWord(), newId);
							log.info("load keyword from memcache, keyWord:{},id:{}",k.getWord(),newId);
						}else{
							keyWords.put(k.getWord(), id);
							log.info("load keyword form meccache error, keyWord:{},id:{}",k.getWord(),id);
						}
					}else{
						keyWords.put(k.getWord(), id);
						log.info("send keyword to memcache, keyWord:{},id:{}",k.getWord(),id);
					}
					log.info("load keyword from DB, keyWord:{},id:{}",k.getWord(),id);
				}
			}
			log.info("load keyWord wast time:{}ms",System.currentTimeMillis() - t1);
		}
	}
	
	public int getKeyByWord(String word){
		if(word == null || word.equals("")) {
			return 0;
		}
		if(keyWords.containsKey(word)){//现在缓存中取
			return keyWords.get(word);
		}else{
			Data data = Platform.dataCenter().getData(DataKeys.keyWordKey(word));
			if(data != null){//在memcache中取
				int id = (Integer)data.value;
				keyWords.put(word, id);
				log.info("get keyword form memCache,key:{},id:{}",word,id);
				return id;
			}else{//存到memcache中 存到缓存中
				KeyWord kw = new KeyWord();
				kw.setWord(word);
				Platform.getEntityManager().createSync(kw);
				Platform.dataCenter().sendNewData(DataKeys.keyWordKey(word), kw.getId());
				keyWords.put(kw.getWord(), kw.getId());
				log.info("create new keyWord,key:{},id:{}",word,kw.getId());
				return kw.getId();
			}
		}
	}
	

}
