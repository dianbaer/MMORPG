package cyou.mrd.io.tcp;

import cyou.mrd.service.Service;


public interface ClientSessionService extends Service{
	public void addClientSession(ClientSession session);
	public void removeClientSession(ClientSession session);
	public String getAddress();
	public int getPort();
}
