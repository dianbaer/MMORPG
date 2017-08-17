package ak.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.configuration.XMLConfiguration;

import cyou.mrd.DefaultAppContext;
import cyou.mrd.Platform;
import cyou.mrd.account.AccountService;
import cyou.mrd.game.actor.Actor;
import cyou.mrd.persist.EntityManagerImpl;
import cyou.mrd.sns.SnsService;
import cyou.mrd.util.RunTimeMonitor;
import cyou.mrd.util.Utils;

public class Test {

	public static void main(String[] args) throws Throwable {
		RunTimeMonitor rt = new RunTimeMonitor();
		rt.knock("start");
		Random rdm = new Random();
		List<String> friendIds = new ArrayList<String>(400);
		for (int i = 0; i < 500; i++) {
			friendIds.add("500" + rdm.nextInt(i+1));
		}
		friendIds.add("123");
		Properties pro = null;
		InputStream stream = null;
		File res = new File("D:\\workspace\\AnimalKingdomServer\\custom.properties");
		stream = new FileInputStream(res);
		pro = new Properties();
		pro.load(stream);
		pro.putAll(System.getProperties());
		System.setProperties(pro);

		XMLConfiguration conf = new XMLConfiguration("D:\\workspace\\AnimalKingdomServer\\src\\config.xml");
		Utils.resolvePlaceHolders(conf);
		Platform.setConfiguration(conf);
		Platform.setEntityManager(new EntityManagerImpl());
		Platform.setAppContext(new DefaultAppContext());
		Platform.getAppContext().create(SnsService.class, SnsService.class);
		Platform.getAppContext().create(AccountService.class, AccountService.class);
		
		SnsService snsService = Platform.getAppContext().get(SnsService.class);
//		rt.knock("ready");
//		List<Actor> ret = snsService.getSnsFriends("weibo", friendIds);
		rt.knock("ok"); 
		List<Actor> ret1 = snsService.getSnsFriendsFast("weibo", friendIds);
		rt.knock("ok1"); 
//		List<Actor> ret2 = snsService.getSnsFriendsFast1("weibo", friendIds);
//		rt.knock("ok2");
//		System.out.println(Arrays.toString(ret.toArray()));
		System.out.println(Arrays.toString(ret1.toArray()));
//		System.out.println(Arrays.toString(ret2.toArray()));
		System.out.println(rt.toString(1));
	}

}
