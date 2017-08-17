package ak.player;

import cyou.mrd.projectdata.Template;
import cyou.mrd.projectdata.TextDataInitException;

public class GameDefaultProcessTemplate implements Template {
	private int id;
	private String value;

	@Override
	public Template initTemplateByTxtLine(String[] txtLineinfo) throws TextDataInitException {
		GameDefaultProcessTemplate mt = new GameDefaultProcessTemplate();
		mt.id = Integer.parseInt(txtLineinfo[0]);
		mt.value = txtLineinfo[1];
		return mt;
	}

	@Override
	public int getId() {
		return id;
	}

	public String getValue() {
		return this.value;
	}

}
