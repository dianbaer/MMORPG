package cyou.mrd.projectdata;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.service.Service;
import cyou.mrd.util.Utils;

/**
 * txt配置文件管理
 * @author mengpeng
 */
public class TextDataService implements Service{
	
	public static final Logger log = LoggerFactory.getLogger(TextDataService.class);

	//数据缓存
	public Hashtable<Class<Template>,Hashtable<Integer,Template>> type2TemplateData = new Hashtable<Class<Template>,Hashtable<Integer,Template>>();
	
    // 支持标记的对象类
	public static Class<Template>[] supportDataClasses;
    
    // 不同类型对象对应的txt文件相对路径
    private static String[] dataFiles;
    
	public String getId() {
		return "DataService";
	}

	@SuppressWarnings("unchecked")
	public void startup() throws Exception {
		String datadir = Platform.getConfiguration().getString("datadir");
		if(!datadir.endsWith("/") && !datadir.endsWith("\\")) {
			datadir += "/";
		}
		log.info("[TextDataService] Data dir:{}",datadir);
		String confFile = datadir+ "data_config.xml";
		XMLConfiguration txtCon = new XMLConfiguration(confFile);
		List<SubnodeConfiguration> configs = txtCon.configurationsAt("txtConfigs.txtConfig");
		TextDataService.supportDataClasses = new Class[configs.size()];
		TextDataService.dataFiles = new String[configs.size()];
		for(int i = 0;i < configs.size();i++){
			Configuration c = configs.get(i);
			TextDataService.supportDataClasses[i] = (Class<Template>)Class.forName(c.getString("class"));
			TextDataService.dataFiles[i] = c.getString("path");
		}
		for(int i = 0;i < dataFiles.length;i++){
			List<String> templates = Utils.getLinesFormTXT(datadir + dataFiles[i]);
			if(templates != null && templates.size() > 0){
				Hashtable<Integer,Template> tem = new Hashtable<Integer,Template>();
				for(int j = 0;j < templates.size();j ++){
					String[] info = ((String)templates.get(j)).split("\t");
					try{
						Template t = ((Template)supportDataClasses[i].newInstance()).initTemplateByTxtLine(info);
						tem.put(t.getId(), t);
					}catch(Exception e){
						log.error(e.getMessage() + ". data id:" + j, e);
					}
				}
				type2TemplateData.put(supportDataClasses[i], tem);
			}
		}
		log.info("TextDataService startup OK,support class size:{}",type2TemplateData.size());
	}

	@Override
	public void shutdown() throws Exception {
		
	}
	
	//根据模板类型取得模板集合
	public Hashtable<Integer,Template> getTemplates(Class<? extends Template> clazz){
		return type2TemplateData.get(clazz);
	}

	//根据模板类型和id 取得模板对象
	public Template getTemplate(Class<? extends Template> clazz,int id){
		if(type2TemplateData.get(clazz)==null){
			log.info("get template by id is null,Class:{},id:{}",clazz,id);
			return null;
		}
		Template template = type2TemplateData.get(clazz).get(id);
		log.info("get template by id success,Class:{},id:{}",clazz,id);
		return template;
	}
	
	
}
