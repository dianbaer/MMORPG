package cyou.mrd.game.actor;

import cyou.mrd.Platform;

public class ActorDAO {
	public Actor getActor(int id) {
		return Platform.getEntityManager().fetch("from Actor where id=? and exist=0", id);
	} 
}
