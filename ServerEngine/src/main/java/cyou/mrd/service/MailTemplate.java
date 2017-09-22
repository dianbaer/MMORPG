package cyou.mrd.service;

import cyou.mrd.Platform;
import cyou.mrd.projectdata.Template;
import cyou.mrd.projectdata.TextDataInitException;

public class MailTemplate implements Template {

	private int id;
	private String language1;//1. 汉语
	private String language2;//2. 英语
	private String language3;
	private String language4;
	private String language5;
	private String language6;
	private String language7;
	private String language8;
	private String language9;
	private String language10;
	private String language11;
	private String language12;
	private String language13;

	@Override
	public int getId() {
		return id;
	}

	@Override
	public Template initTemplateByTxtLine(String[] txtLineinfo) throws TextDataInitException {
		MailTemplate mt = new MailTemplate();
		mt.id = Integer.parseInt(txtLineinfo[0]);
		mt.language1 = txtLineinfo[1];
//		mt.language2 = txtLineinfo[2];
//		mt.language3 = txtLineinfo[3];
//		mt.language4 = txtLineinfo[4];
//		mt.language5 = txtLineinfo[5];
//		mt.language6 = txtLineinfo[6];
//		mt.language7 = txtLineinfo[7];
//		mt.language8 = txtLineinfo[8];
//		mt.language9 = txtLineinfo[9];
//		mt.language10 = txtLineinfo[10];
//		mt.language11 = txtLineinfo[11];
//		mt.language12 = txtLineinfo[12];
//		mt.language13 = txtLineinfo[13];
		return mt;
	}

	public String getLanguage1() {
		return language1;
	}

	public void setLanguage1(String language1) {
		this.language1 = language1;
	}

	public String getLanguage2() {
		return language2;
	}

	public void setLanguage2(String language2) {
		this.language2 = language2;
	}

	public String getLanguage3() {
		return language3;
	}

	public void setLanguage3(String language3) {
		this.language3 = language3;
	}

	public String getLanguage4() {
		return language4;
	}

	public void setLanguage4(String language4) {
		this.language4 = language4;
	}

	public String getLanguage5() {
		return language5;
	}

	public void setLanguage5(String language5) {
		this.language5 = language5;
	}

	public String getLanguage6() {
		return language6;
	}

	public void setLanguage6(String language6) {
		this.language6 = language6;
	}

	public String getLanguage7() {
		return language7;
	}

	public void setLanguage7(String language7) {
		this.language7 = language7;
	}

	public String getLanguage8() {
		return language8;
	}

	public void setLanguage8(String language8) {
		this.language8 = language8;
	}

	public String getLanguage9() {
		return language9;
	}

	public void setLanguage9(String language9) {
		this.language9 = language9;
	}

	public String getLanguage10() {
		return language10;
	}

	public void setLanguage10(String language10) {
		this.language10 = language10;
	}

	public String getLanguage11() {
		return language11;
	}

	public void setLanguage11(String language11) {
		this.language11 = language11;
	}

	public String getLanguage12() {
		return language12;
	}

	public void setLanguage12(String language12) {
		this.language12 = language12;
	}

	public String getLanguage13() {
		return language13;
	}

	public void setLanguage13(String language13) {
		this.language13 = language13;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String toString() {
		return this.id + " Chinese: "+ this.language1 + "   English: " + this.language2;
	}
	
	public String getDefaultLanguage(){
		if(Platform.getDefaultLanguage() == 1){ //汉语
			return this.language1;
		}else if(Platform.getDefaultLanguage() == 2){  //英语
			return this.language2;
		}else if(Platform.getDefaultLanguage() == 3){
			return this.language3;
		}else if(Platform.getDefaultLanguage() == 4){
			return this.language4;
		}else if(Platform.getDefaultLanguage() == 5){
			return this.language5;
		}else if(Platform.getDefaultLanguage() == 6){
			return this.language6;
		}else if(Platform.getDefaultLanguage() == 7){
			return this.language7;
		}else if(Platform.getDefaultLanguage() == 8){
			return this.language8;
		}else if(Platform.getDefaultLanguage() == 9){
			return this.language9;
		}else if(Platform.getDefaultLanguage() == 10){
			return this.language10;
		}else if(Platform.getDefaultLanguage() == 11){
			return this.language11;
		}else{
			return this.language1;
		}
	}

}
