package cyou.mrd.service;

import cyou.mrd.projectdata.Template;
import cyou.mrd.projectdata.TextDataInitException;

public class HarmoniousWordTemplate implements Template {
	
	private int id;
	private String str;
	private int type;
	

	@Override
	public Template initTemplateByTxtLine(String[] txtLineinfo) throws TextDataInitException {
		HarmoniousWordTemplate word = new HarmoniousWordTemplate();
		word.id = Integer.parseInt(txtLineinfo[0]);
		word.str = txtLineinfo[1];
		word.type = Integer.parseInt(txtLineinfo[2]);
		return word;
	}

	@Override
	public int getId() {
		return id;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setId(int id) {
		this.id = id;
	}
	

}
