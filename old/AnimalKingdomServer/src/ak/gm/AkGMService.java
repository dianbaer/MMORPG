package ak.gm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ak.player.PlayerServiceEx;

import com.cyou.mrd.GMNetLibJni;

import cyou.mrd.Platform;
import cyou.mrd.service.Service;

public class AkGMService implements Service {
	private static final Logger log = LoggerFactory.getLogger(AkGMService.class);
	private GMNetLibJni _jni;
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "AkGMService";
	}

	@Override
	public void startup() throws Exception {
		Thread gmThread = new Thread(new GMWork(), "gmThread[AkGMService]");
		gmThread.start();

	}
	class GMWork implements Runnable {
		public void run() {
			try {
				_jni = new GMNetLibJni(
						Platform.getConfiguration().getString("GM.syscenter_url"), 
						Platform.getConfiguration().getInt("GM.syscenter_port"),
						Long.parseLong(Platform.getConfiguration().getString("GM.appkey")),
						Platform.getConfiguration().getString("GM.gid"),
						Platform.getConfiguration().getInt("GM.cid"));
				if (_jni.initGmTool() < 0)
				{
					System.out.println("init error,may be cann't connect syscenter");
					return;
				}
				while (true)
				{
					int ret = _jni.tick();
					System.out.println("tick ret:" + ret);
				}
			} catch (Throwable e) {
				log.error("error",e);
			}
			
													   
			
		}
	}
	@Override
	public void shutdown() throws Exception {
		// TODO Auto-generated method stub

	}

}
