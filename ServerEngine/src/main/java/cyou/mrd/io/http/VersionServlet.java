package cyou.mrd.io.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;

public class VersionServlet  extends HttpServlet{

	private static final long serialVersionUID = 1L;


	//没更新一个版本     需要更新这个字符串
	public static final String CURRENT_VERSION = Platform.getConfiguration().getString("serverVersion");;
	
	
	protected static final Logger log = LoggerFactory.getLogger(VersionServlet.class);
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("request server version");
		
		response.getOutputStream().write(CURRENT_VERSION.getBytes());
	}
	
	
	
	
	
	
	
}
