package cyou.mrd.projectdata;

/**
 * txt配置文件模板接口
 * @author mengpeng
 */
public interface  Template {
	
	public Template initTemplateByTxtLine(String[] txtLineinfo) throws TextDataInitException;
	
	public int getId();
}
