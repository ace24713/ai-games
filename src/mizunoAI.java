/*
 * mizunoAI
 * Version 1.01
 * Last update on 1st April. 2015
 * Made compatible with FightingICE version 1.03
 */

import java.util.Deque;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import support.Command;
import enumerate.Action;
import enumerate.AttackAction;
import enumerate.Position;
import enumerate.State;
import mizunoAI_simulator.Simulator;
import structs.*;
import gameInterface.AIInterface;

public class mizunoAI implements AIInterface {
	/** a specific distance used in k-nn*/
	private static final int K_DISTANCE = 50;
	private static final double K_THRESHOLD = 0.3;
	/** data number referred by k-nn*/
	private static final int THRESHOLD = 3;
	/** delay frames number*/
	private static final double DELAY = 14;
	/** player's boolean value*/
	boolean p;

	GameData gd;
	Key inputKey;
	FrameData fd;
	
	/** my CharacterData*/
	CharacterData my;
	/** opponent's CharacterData*/
	CharacterData opp;
	/** the position relationship between two characters*/
	Position pos;
	
	/** deque retaining attack action performed by opponent*/
	Deque<ActData> oppActData_GG;
	Deque<ActData> oppActData_GA;
	Deque<ActData> oppActData_AG;
	Deque<ActData> oppActData_AA;
	
	/** a deque retaining ground action*/
	Deque<Action> G_Act;
	/** a deque retaining air action*/
	Deque<Action> A_Act;
	/** a deque retaining actions based on the current my position*/
	Deque<Action> myAct;
	/** an opponent's action predicted by k-nn*/
	Deque<Action> oppAct;
	/** counts the number of the k-nearest data every attack action*/
	int[] checkAct;
	
	Command cc;
	/** use simulator package*/
	Simulator simulator;
	
	/** a previous opponent's action*/
	Action preOppAct;
	/** a current opponent's action*/
	Action nowOppAct;
	/** a previous round number*/
	int preRound;
	/** a current round number*/
	int nowRound;
	
	long time;
	
	@Override
	public synchronized int initialize(GameData gameData, boolean playerNumber) {

		gd = gameData;
		p = playerNumber;
		
		inputKey = new Key();
		fd = new FrameData();
		cc = new Command();
		
		preOppAct = Action.NEUTRAL;
		nowOppAct = Action.NEUTRAL;
		
		preRound = 0;
		nowRound = 0;

		simulator = new Simulator(gd, this.p);
		this.oppActData_GG = new LinkedList<ActData>();
		this.oppActData_GA = new LinkedList<ActData>();
		this.oppActData_AG = new LinkedList<ActData>();
		this.oppActData_AA = new LinkedList<ActData>();
		this.myAct = new LinkedList<Action>();
		this.oppAct = new LinkedList<Action>();
		checkAct = new int[EnumSet.allOf(Action.class).size()];
		
		setAirGroundAction();
		
		return 0;
	}

	@Override
	public synchronized void getInformation(FrameData frameData) {
		time = System.currentTimeMillis();
		fd = frameData;
		cc.setFrameData(fd, p);
		if(p){
			my = fd.getP1();
			opp = fd.getP2();
		}else{
			my = fd.getP2();
			opp = fd.getP1();
		}
		
		for(int i = 0 ; i < EnumSet.allOf(Action.class).size() ; i++){
			checkAct[i] = 0;
		}
		
		nowRound = fd.getRound();
		if(nowRound != preRound && nowRound != 0 && nowRound%3 == 0){
			oppActData_GG.clear();
			oppActData_GA.clear();
			oppActData_AG.clear();
			oppActData_AA.clear();
		}}

	@Override
	public synchronized void processing() {
		boolean temp;
		
		inputKey.empty();
		if(!fd.getEmptyFlag()){	
			if(fd.getRemainingTime() > 0){
				
				nowOppAct = opp.getAction();
				Vector<MotionData> oppMotion = this.p? gd.getPlayerTwoMotion():gd.getPlayerOneMotion();
				
				// record an opponent's attack data each time an opponent performs an attack action
				if(oppMotion.elementAt(opp.getAction().ordinal()).getFrameNumber() == opp.getRemainingFrame()){
					try{
							AttackAction.valueOf(nowOppAct.name());
							
							if(my.isFront()){
								ActData act = new ActData(opp.getX()-my.getX(),opp.getY()-my.getY(),nowOppAct);
								setOppAttackData(act);
							}else{
								ActData act = new ActData(my.getX()-opp.getX(),my.getY()-opp.getY(),nowOppAct);
								setOppAttackData(act);
							}
						}catch (Exception e){
					}
				}
					
				{
					if(my.getX() < opp.getX()) temp=true;
					else temp=false;
				}
				
				// update the frameData by 15 frames to predict the positions after delay
				update(my);
				update(opp);
				simulator.setFrameData(fd);
				setPosition();
				setMyAct();

				if(cc.getskillFlag()){
					inputKey = cc.getSkillKey();
				}else{
					if(cc.getMyCharacter().isControl() || my.getRemainingFrame() <= 0){
						// default action
						Action act = Action.CROUCH_GUARD;
						
						// predict a next opponent's attack action using k-nn and conduct simulation against the predicted action
						if(calculateActDistance(getOppAttackData(),opp.getX()-my.getX(),opp.getY()-my.getY())){
							act = simulator.simulate(myAct,oppAct,checkAct);
						}
						
						if(my.getAction() == Action.DOWN) my.setFront(temp);
						cc.commandCall(act.name());
						inputKey = cc.getSkillKey();
					}
				}
			}
		}
		
		fin();
	}

	@Override
	public synchronized Key input() {
		time = -time + System.currentTimeMillis();
		System.out.println(time);
		return inputKey;
	}

	@Override
	public synchronized void close() {
		oppActData_GG.clear();
		oppActData_GA.clear();
		oppActData_AG.clear();
		oppActData_AA.clear();
	}
	
	/** update round and opponent's action data*/
	private synchronized void fin(){
		preOppAct = nowOppAct;
		preRound = nowRound;
		oppAct.clear();
	}
	
	/** count the number of the recorded data within a specific distance from the current relative position*/
	private synchronized boolean calculateActDistance(Deque<ActData> actData , int x, int y){
		int threshold = (int)(actData.size()*K_THRESHOLD + 1);
		Deque<ActData> temp = new LinkedList<ActData>();
		ActData[] array;
		
		// calculate distance from the current relative position to all recorded data
		for(Iterator<ActData> i = actData.iterator() ; i.hasNext() ; ){
			ActData act = new ActData(i.next());
			if(my.isFront()) act.setDistance((int)Math.sqrt((act.getX()-x)*(act.getX()-x)+(act.getY()-y)*(act.getY()-y)));
			else act.setDistance((int)Math.sqrt((act.getX()+x)*(act.getX()+x)+(act.getY()+y)*(act.getY()+y)));
			if( act.getDistance() < K_DISTANCE) temp.add(act);
		}
		
		if(temp.size() < Math.min(threshold,THRESHOLD)) return false;		
		
		array = new ActData[temp.size()];
		// execute sort method
		array = actSort(temp);
		// predict the next opponent's action using k-nn
		setOppAct(array, Math.min(threshold,THRESHOLD));
		
		return true;
	}
	
	/** execute sort method and return array of ActData type*/
	private synchronized ActData[] actSort(Deque<ActData> actData){
		ActData[] array = new ActData[actData.size()];
		
		for(int i = 0 ; i < array.length ; i ++){
			array[i] = new ActData(actData.pop());
		}
		sort(array);
		
		return array;
	}
	
	/** execute the merge algorithm*/
	private synchronized void merge(ActData[] a1,ActData[] a2,ActData[] a){
		int i=0,j=0;
		while(i<a1.length || j<a2.length){
			if(j>=a2.length || (i<a1.length && a1[i].getDistance()<a2[j].getDistance())){
				a[i+j].setMember(a1[i]);
				i++;
			}
			else{
				a[i+j].setMember(a2[j]);
				j++;
			}
		}
	}
	
	/** execute the merge sort algorithm*/
	private synchronized void mergeSort(ActData[] a){
		if(a.length>1){
			int m=a.length/2;
			int n=a.length-m;
			ActData[] a1=new ActData[m];
			ActData[] a2=new ActData[n];
			for(int i=0;i<m;i++) a1[i] = new ActData(a[i]);
			for(int i=0;i<n;i++) a2[i] = new ActData(a[m+i]);
			mergeSort(a1);
			mergeSort(a2);
			merge(a1,a2,a);
		}
	}

	/** execute the sort algorithm*/
	private synchronized void sort(ActData[] a){
		mergeSort(a);
	}
	
	/** set my action to a deque based on the current my position*/
	private synchronized void setMyAct(){
		if(my.getState() == State.AIR) myAct = A_Act;
		else myAct = G_Act;		
	}
	
	/** set the opponent's next action using k-nn*/
	private synchronized void setOppAct(ActData[] array,int threshold){
		Action[] subAct = Action.values();
		int max = 1;
		
		// count the number of the data every action type 
		for(int i = 0 ; i < threshold ; i++){
			checkAct[array[i].getAct().ordinal()] ++;
		}

		// ACE: here I would compute a probability of all actions taken by the opponent instead of simply returning a vector of most occuring moves in the checkAct array
		// predict the opponent's next action by k-nn
		for(int i = 0 ; i < EnumSet.allOf(Action.class).size() ; i++){
			if(checkAct[i] > max){
				oppAct.clear();
				oppAct.add(subAct[i]);
				max = checkAct[i];
			}
			else if(checkAct[i] == max){
				oppAct.add(subAct[i]);
			}
		}
	}
	
	/** set the current position relationship*/
	private synchronized void setPosition(){
		if(my.getState() == State.AIR){
			if(opp.getState() == State.AIR) pos = Position.Air_Air;
			else pos = Position.Air_Ground;
		}else{
			if(opp.getState() == State.AIR) pos = Position.Ground_Air;
			else pos = Position.Ground_Ground;
		}
	}
	
	/** get the deque based on the current position relationship*/
	private synchronized Deque<ActData> getOppAttackData(){
		switch(pos){
		case Air_Air: return oppActData_AA;
		case Air_Ground: return oppActData_AG;
		case Ground_Air: return oppActData_GA;
		case Ground_Ground: return oppActData_GG;
		}
		return oppActData_GG;
	}
	
	/** set data to the deque based on the current position relationship*/
	private synchronized void setOppAttackData(ActData act){
		switch(pos){
		case Air_Air: oppActData_AA.add(act); break;
		case Air_Ground: oppActData_AG.add(act); break;
		case Ground_Air: oppActData_GA.add(act); break;
		case Ground_Ground :oppActData_GG.add(act);break;
		}
	}
	
	/** set air and ground action to each deque*/
	private synchronized void setAirGroundAction(){
		this.G_Act = new LinkedList<Action>();
		this.A_Act = new LinkedList<Action>();
		
		G_Act.add(Action.CROUCH_FA);
		G_Act.add(Action.STAND_FA);
		G_Act.add(Action.CROUCH_A);
		G_Act.add(Action.STAND_A);
		G_Act.add(Action.THROW_B);
		G_Act.add(Action.THROW_A);
		G_Act.add(Action.FOR_JUMP);
		G_Act.add(Action.JUMP);
		G_Act.add(Action.BACK_STEP);
		G_Act.add(Action.STAND_D_DF_FA);
		G_Act.add(Action.STAND_D_DF_FB);
		G_Act.add(Action.STAND_F_D_DFA);
		G_Act.add(Action.STAND_F_D_DFB);
		G_Act.add(Action.STAND_D_DB_BA);
		G_Act.add(Action.STAND_D_DB_BB);
		G_Act.add(Action.STAND_D_DF_FC);
		
		A_Act.add(Action.AIR_GUARD);
		A_Act.add(Action.AIR_A);
		A_Act.add(Action.AIR_DA);
		A_Act.add(Action.AIR_FA);
		A_Act.add(Action.AIR_UA);
		A_Act.add(Action.AIR_D_DF_FA);
		A_Act.add(Action.AIR_F_D_DFA);
		A_Act.add(Action.AIR_D_DB_BA);	
		
	}
	
	/** update character's state after 15 frames*/
	public synchronized void update(CharacterData one){
		
		if(one.getAction() == Action.DOWN) one.setRemainingFrame(one.getRemainingFrame()+6);
		
		for(int i = 0 ; i < DELAY ; i++)
		{
			one.setRemainingFrame(one.getRemainingFrame()-1);
			one.setX(one.getX() + one.getSpeedX());
			one.setY(one.getY() + one.getSpeedY());
			frictionEffect(one);
			gravityEffect(one);
			if(one.getY() >= 320){
				one.setY(320);
				one.setState(State.STAND);
			}
		}
	}
	
	/** reflect friction effect on x-direction speed*/
	public synchronized void frictionEffect(CharacterData one){
		if(!(one.getY() < 320)){
			if(one.getSpeedX() > 0){
				one.setSpeedX(one.getSpeedX()-1);
			}
			else if(one.getSpeedX() < 0){
				one.setSpeedX(one.getSpeedX()+1);
			}
		}
	}

	/** reflect gravity effect on y-direction speed*/
	public synchronized void gravityEffect(CharacterData one){
		if(one.getBottom() >= 320 + 256){
			one.setSpeedY(0);
		}
		else
		{
			one.setSpeedY(one.getSpeedY()+1);
		}
	}

	@Override
	public String getCharacter() {
		return CHARACTER_ZEN;
	}
}
