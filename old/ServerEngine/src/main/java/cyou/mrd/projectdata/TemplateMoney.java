package cyou.mrd.projectdata;



public class TemplateMoney implements Template{
	
	private int id;
	
	private String name;
	
	private int money;
	
	private int yb;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMoney() {
		return money;
	}
	public void setMoney(int money) {
		this.money = money;
	}
	public int getYb() {
		return yb;
	}
	public void setYb(int yb) {
		this.yb = yb;
	}
	@Override
	public Template initTemplateByTxtLine(String[] txtLineinfo) throws TextDataInitException{
		if(txtLineinfo.length != 4) {
			throw new TextDataInitException("format error. Lineinfo.lenght!=4");
		}
		TemplateMoney template = new TemplateMoney();
		try {
			template.setId(Integer.parseInt(txtLineinfo[0]));
			template.setName(txtLineinfo[1]);
			template.setMoney(Integer.parseInt(txtLineinfo[2]));
			template.setYb(Integer.parseInt(txtLineinfo[3]));
		}catch(NumberFormatException ne) {
			throw new TextDataInitException("TXT NumberFormatException.");
		}
		return template;
	}
}
