package cyou.mrd.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.projectdata.Template;
import cyou.mrd.projectdata.TextDataService;

public class HarmoniousService implements Service {
	private static final Logger log = LoggerFactory.getLogger(HarmoniousService.class);
	private Set<String> fullyMatchWords = new HashSet<String>();
	/*
	 * 敏感词汇过滤树的根节点
	 */
	protected KeyWordsState root = new KeyWordsState();
	
	@Override
	public String getId() {
		return "HarmoniousService";
	}

	@Override
	public void shutdown() throws Exception {

	}

	@Override
	public void startup() throws Exception {
		Map<Integer, Template> items = Platform.getAppContext().get(TextDataService.class).getTemplates(HarmoniousWordTemplate.class);
		if (items != null) {
			Set<Entry<Integer, Template>> itemSet = items.entrySet();
			int initSize = itemSet.size() / 10;
			for (Entry<Integer, Template> entry : itemSet) {
				HarmoniousWordTemplate temp = (HarmoniousWordTemplate) entry.getValue();
				if (temp.getType() == 1) {
					addString(temp.getStr());
				} else if (temp.getType() == 2) {
					fullyMatchWords.add(temp.getStr());
				}
			}
			init(root);
			log.info("[HarmoniousService] items[{}]", items.size());
		}
	}
 

	static class KeyWordsState {
		private int id; // just for testing purpose
		private static int availableId = 0;

		// the parent node
		KeyWordsState parent = null;
		// the target stage if the current character failed.
		KeyWordsState failState = null;
		// If the stage is the final
		boolean finalState = false;
		HashMap<Character, KeyWordsState> nextState = new HashMap<Character, KeyWordsState>();
		Character character = null;

		public KeyWordsState() {
			id = availableId++;
		}

		public KeyWordsState(Character c) {
			id = availableId++;
			character = c;
		}

		KeyWordsState addState(Character c) {
			KeyWordsState obj = nextState.get(c);
			if (obj == null) {
				KeyWordsState s = new KeyWordsState(c);
				s.parent = this;
				nextState.put(c, s);
				return s;
			}
			return obj;
		}

		KeyWordsState getState(Character c) {
			Object obj = nextState.get(c);
			if (obj == null) {
				return null;
			}
			return (KeyWordsState) obj;
		}
	}
	
	/*
	 * 把一个字符串加入到敏感词汇过滤状态树中去。
	 */
	private void addString(String s) {
		KeyWordsState state = root;
		for (int i = 0; i < s.length(); i++) {
			state = state.addState(new Character(s.charAt(i)));
		}
		state.finalState = true;
	}
	
	/*
	 * 查找字符串中的所有敏感词汇。
	 * 
	 * @return 返回所有查找结果，key是起始位置，value是长度
	 */
	private HashMap<Integer, Integer> match(String target) {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		int i = 0;
		int n = target.length();
		KeyWordsState state = root;
		int count = 0;

		while (i < n) {
			count++;
			Character c = new Character(target.charAt(i));
			KeyWordsState nS = state.getState(c);
			if (nS == null) {
				if (state == root) {
					i++;
				} else {
					int temp = reg(map, state, i);
					if (temp == -1) {
						state = state.failState;
					} else {
						i = temp;
						state = root;
						c = new Character(target.charAt(i));
					}
					i++;
					state = findNextNode(map, state, c, i);
				}
			} else {
				i++;
				state = nS;
			}
		}
		reg(map, state, n);
		return map;
	}
	
	/*
	 * 状态树过滤算法。
	 */
    private KeyWordsState findNextNode(HashMap<Integer, Integer> map, KeyWordsState state,
			Character c, int i) {
		KeyWordsState tempState = state.getState(c);
		if (tempState == null) {
			if (state == root) {
				return state;
			}
			reg(map, state, i);
			state = state.failState;
			return findNextNode(map, state, c, i);
		} else {
			return tempState;
		}
	}   
    
    /*
	 * 状态树过滤算法。
	 */
    private int reg(HashMap<Integer, Integer> map, KeyWordsState state, int n) {
        int t = 0;
        while (state != root) {
            if (state.finalState) {
                t = n;
                String s = "";
                while (state != root) {
                    s = state.character + s;
                    state = state.parent;
                    n--;
                }
                map.put(n, s.length());
                return t;
            }
            state = state.parent;
            n--;
        }
        return -1;
    }
    
    /*
	 * 初始化过滤状态树数据结构。
	 */
	private void init(KeyWordsState state) {
		Iterator<KeyWordsState> ite = state.nextState.values().iterator();
		while (ite.hasNext()) {
			KeyWordsState s1 = ite.next();
			KeyWordsState s2 = state.failState;
			while (true) {
				if (s2 == null) {
					s1.failState = root;
					break;
				}
				KeyWordsState s3 = s2.getState(s1.character);
				if (s3 != null) {
					s1.failState = s3;
					break;
				}
				s2 = s2.failState;
			}
			init(s1);
		}
	}
	public String filterBadWords(String str) {
		if(str == null ) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		if(fullyMatchWords.contains(str)) {
			for (int i = 0; i < str.length(); i++) {
				buffer.append('X');
			}
			return buffer.toString();//全匹配
		}
		
		HashMap<Integer, Integer> map = match(str);
		Object[] keys = map.keySet().toArray();
		
		int size = keys.length;
		char[] chars = str.toCharArray();
		int length = chars.length;
		int j = 0;
		Integer index, strLength;

		// 排序
		if (size != 1) {
			List list = Arrays.asList(keys);
			Collections.sort(list);
			keys = list.toArray();
		}
		if (size > 0) {
			index = (Integer) keys[j];
			strLength = map.get(index);
			for (int i = 0; i < length; i++) {
				if (i == index.intValue() && (j < size)) {
					for (int m = 0; m < strLength.intValue(); m++) {
						buffer.append('X');
					}
					i = i + strLength.intValue() - 1;
					j++;
					if (j < size) {
						index = (Integer) keys[j];
					}
					strLength = map.get(index);
				} else {
					buffer.append(chars[i]);
				}
			}
			str = buffer.toString();
		}
		return str;
	}


	public static void main(String[] args) throws Exception {
		HarmoniousService s = new HarmoniousService();
		s.addString("人权");
		s.addString("自由");
		s.addString("fuck");
		s.init(s.root);
		log.info(s.filterBadWords("和谐世界里的人权和自由!"));
		
	}
}
