package structs;

import enumerate.Action;

public class ActData {
	private int x;
	private int y;
	private int distance;
	private Action act;
	
	public ActData(int x,int y,Action act){
		this.x = x;
		this.y = y;
		distance = -1;
		this.act = act;
	}
	
	public ActData(ActData act){
		this.x = act.getX();
		this.y = act.getY();
		this.distance = act.getDistance();
		this.act = act.getAct();
	}
	
	public void setMember(ActData act){
		this.x = act.getX();
		this.y = act.getY();
		this.distance = act.getDistance();
		this.act = act.getAct();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public Action getAct() {
		return act;
	}
}
