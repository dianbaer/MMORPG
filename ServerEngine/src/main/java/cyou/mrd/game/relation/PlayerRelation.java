package cyou.mrd.game.relation;

import java.io.Serializable;

public class PlayerRelation implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	
	private RelationList friends;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RelationList getFriends() {
		return friends;
	}

	public void setFriends(RelationList friends) {
		this.friends = friends;
	}
	
}
