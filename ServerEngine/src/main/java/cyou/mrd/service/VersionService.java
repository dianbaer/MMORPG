package cyou.mrd.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;

public class VersionService implements Service {

	private static final Logger log = LoggerFactory.getLogger(VersionService.class);

	//private static  String SERVER_VERSION = null;

	//private String serverVersion;
	private int version_1;
	private int version_2;
	private int version_3;
	private int version_4;

	@Override
	public String getId() {
		return "VersionService";
	}

	@Override
	public void startup() throws Exception {
		// load vision data with config
		// File updateFile = new File(Platform.getWebRootPath() +
		// "/update.ru.enc");
		// if(updateFile.exists()) {
		// FileInputStream fin = new FileInputStream(updateFile);
		// byte[] content = null;
		// byte[] buf = new byte[1024];
		// while(fin.read(buf) != -1) {
		// if(content == null) {
		// content = buf;
		// }else {
		// byte[] contentExpand = new byte[content.length + buf.length];
		// System.arraycopy(content, 0, contentExpand, 0, content.length);
		// System.arraycopy(buf, 0, contentExpand, contentExpand.length -
		// buf.length, buf.length);
		// }
		// }
		// log.info(new String(content));
		// HTTPEncodeUtil.fileEncode(content, content.length);
		// log.info(new String(content));
		//
		// }
		//serverVersion = Platform.getConfiguration().getString("clientVersion");
		String[] clientversion = Platform.getConfiguration().getString("clientVersion").split("\\.");
		version_1 = Integer.parseInt(clientversion[0]);
		version_2 = Integer.parseInt(clientversion[1]);
		version_3 = Integer.parseInt(clientversion[2]);
		version_4 = Integer.parseInt(clientversion[3]);
		//log.info("VersionService startup success");
		//SERVER_VERSION = version_1 + "." + version_2 + "." + version_3 + "." + version_4 + ".x";
	}

	@Override
	public void shutdown() throws Exception {
	}

	public boolean allowAbleVision(final String vision) {
		try {
//			log.info("[allowAbleVision] vision[{}]", vision);
			//log.info("[allowAbleVision] serverVision[{}] clientVision[{}]", serverVersion, vision);
			String[] visions = vision.split("\\.");
			int v_1 = Integer.parseInt(visions[0]);
			int v_2 = Integer.parseInt(visions[1]);
			int v_3 = Integer.parseInt(visions[2]);
			int v_4 = Integer.parseInt(visions[3]);

			if (v_1 > version_1) {
				return true;
			} else if (v_1 == version_1) {
				if (v_2 > version_2) {
					return true;
				} else if (v_2 == version_2) {
					if (v_3 > version_3) {
						return true;
					} else if (v_3 == version_3) {
						if (v_4 >= version_4) {
							return true;
						}
					}
				}
			}

			//log.info("notAllowAbleVision() version[{}], server[{}]: support vision", vision, SERVER_VERSION);
			return false;
		} catch (Throwable a) {
			log.error("VersionService.allowAbleVision:error[Throwable]", a);
			return false;
		}
	}

	public boolean notAllowAbleVision(String vision) {
		 return !allowAbleVision(vision);
	}
	
}
