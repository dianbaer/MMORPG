package ak.sdk;

import cyou.mrd.sdk.SdkRoot;
import cyou.mrd.service.Service;

public class SdkService implements Service {

	@Override
	public String getId() {
		
		return "SdkService";
	}

	@Override
	public void startup() throws Exception {
		SdkRoot.init();

	}

	@Override
	public void shutdown() throws Exception {
		

	}

}
