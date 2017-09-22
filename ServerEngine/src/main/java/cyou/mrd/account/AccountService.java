package cyou.mrd.account;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.service.Service;

public class AccountService implements Service {

	public static final Logger log = LoggerFactory.getLogger(AccountService.class);

	@Override
	public String getId() {
		return "AccountService";
	}

	@Override
	public void startup() throws Exception {

	}

	@Override
	public void shutdown() throws Exception {

	}
	
	public Account getAccountByMid(String mid) {
		Account account;
		List<Account> list = Platform.getEntityManager().limitQuery("from Account where mid = ?", 0, 1, mid);
		if(list.isEmpty()) {
			account = null;
			log.info("[AccountService] getAccountByMid(mid:{}), find from db Account:null ", mid);
		}else {
			account = list.get(0);
			log.info("[AccountService] getAccountByMid(mid:{}), find from db Account:{} ", mid, account);
		}
		
		return account;
	}
	
	public Account getAccountById(int id){
		log.info("[AccountService] getAccountById( id:{})", id);
		Account account = Platform.getEntityManager().find(Account.class, id);
		return account;
	}
	
	public Account getAccountByMid(String mid, int id) {
		log.info("[AccountService] getAccountByMid( accountId:{} mid:{})", id, mid);
		Account account = Platform.getEntityManager().find(Account.class, id);
		if (account == null) {
			// error log !收到攻击
			log.error("[getAccountByMid] [error] [attack] not find account! Account:{} mid:{}", id, mid);
		} else {
			if (account.getMid().equals(mid)) {
				account.setLastLoginTime(new Date());
				Platform.getEntityManager().updateSync(account);
			} else {
				// error 应该是虚假登录信息
				log.info("[getAccountByMid][error] Account find but mid not match. find by: accountId:{} accountMid:{} != loginMid:{}",
						new Object[] { id, account.getMid(), mid });
				return null;
			}
		}
		log.info("[getAccountByMid] return[account:{}]", account == null ? "null" : account.getId());
		return account;
	}

	public Account createAccount(String mid) {
		log.info("[AccountService] create account mid:{}", mid);
		Account account = new Account();
		account.setMid(mid);
		account.setCreateTime(new Date());
		account.setLastLoginTime(new Date());
		Platform.getEntityManager().createSync(account);
		log.info("[AccountService] create account accountId:{}", account == null ? "null" : account.getId());
		return account;
	}

	// 通过sns类型 和 sns账号 查找好友
	public List<AccountSNS> getAccountBySNS(String type, String snsId) {
		log.info("[AccountService] getAccountBySNS(type:{}, snsId:{})", type, snsId);
		String snsTypeName = AccountSNS.getSNSTypeNameByType(type);
		if (snsTypeName == null || snsTypeName.equals(""))
			return null;
		String hql = "from AccountSNS where " + snsTypeName + "= ?";
		List<AccountSNS> friends = Platform.getEntityManager().limitQuery(hql, 0, 100, snsId);
		log.info("[getAccountBySNS] return[friendsSize:{}]", friends == null ? "0" : friends.size());
		return friends;
	}

}
