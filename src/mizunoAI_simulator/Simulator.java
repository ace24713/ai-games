package mizunoAI_simulator;

import java.util.Deque;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import structs.FrameData;
import structs.GameData;
import structs.CharacterData;
import structs.MotionData;
import enumerate.Action;
import fighting.Attack;

public class Simulator {
	/** simulation time length*/
	private final static int SIMULATE_LIMIT = 60;
	
	private GameData gameData;
	
	private FrameData frameData;
	
	private boolean player;
	
	/** create GameData for simulation*/
	public Simulator(GameData gameData,boolean player){
		this.gameData = gameData;
		this.player = player;
	}
	
	/** set FrameData*/
	public void setFrameData(FrameData frameData){
		this.frameData = frameData;
	}
	
	/** simulate and calculate evaluation value when an AI conducts the myAction against the oppAction*/
	public int simulate(Action myAction,Action oppAction){

		CharacterData myCharacter;
		CharacterData oppCharacter;
		
		Vector<MotionData> myMotionData;
		Vector<MotionData> oppMotionData;
		
		int mySaveHp;
		int oppSaveHp;
		
		mizunoAI_simulator.SimCharacter simMyCharacter;
		mizunoAI_simulator.SimCharacter simOppCharacter;
		mizunoAI_simulator.SimFighting simFighting;
		
		// copy
		if(player){
			myCharacter = frameData.getP1();
			myMotionData = gameData.getPlayerOneMotion();
			
			oppCharacter = frameData.getP2();
			oppMotionData = gameData.getPlayerTwoMotion();
		}else{
			myCharacter = frameData.getP2();
			myMotionData = gameData.getPlayerTwoMotion();
			
			oppCharacter = frameData.getP1();
			oppMotionData = gameData.getPlayerOneMotion();
		}
	
		// if my character's energy is shortage
		if(myCharacter.energy + myMotionData.elementAt(myAction.ordinal()).attackStartAddEnergy < 0) return -1000;
		
		// set AI's HP before simulation
		mySaveHp = myCharacter.getHp();
		oppSaveHp = oppCharacter.getHp();
		
		// create CharacterData for simulation
		simMyCharacter = new mizunoAI_simulator.SimCharacter(myCharacter,myMotionData,player);
		simOppCharacter = new mizunoAI_simulator.SimCharacter(oppCharacter,oppMotionData,!player);
		// simAttack is the projectileData of two characters
		Deque<mizunoAI_simulator.SimAttack> simAttack = new LinkedList<mizunoAI_simulator.SimAttack>();
		int size = frameData.getAttack().size();
		// copy attackData to simAttack
		for(int i = 0 ; i < size ; i++){
			Attack temp = frameData.getAttack().pop();
			SimAttack tempSimAttack = new SimAttack(temp);
			simAttack.addLast(tempSimAttack);
			frameData.getAttack().add(temp);
		}
		
		// initialize FightingData
		if(player){
			simFighting = new mizunoAI_simulator.SimFighting(simMyCharacter,simOppCharacter,simAttack,myAction,oppAction);
		}
		else{
			simFighting = new mizunoAI_simulator.SimFighting(simOppCharacter,simMyCharacter,simAttack,oppAction,myAction);
		}
		
		// simulate the game for SIMULATE_LIMIT frames
		for(int i = 0 ; i < SIMULATE_LIMIT ; i++){
			simFighting.processingFight();
		}
		
		// calculate the evaluation value of the myAction
		return (simMyCharacter.getHp() - mySaveHp) - (simOppCharacter.getHp() - oppSaveHp) ;
	}
	
	/** not in use*/
	public int[][] allSimulate(){
		
		int actionSize = EnumSet.allOf(Action.class).size();
		int[][] result = new int[actionSize][actionSize];
		
		for(int i = 0 ; i < actionSize ; i++){
			for(int j = 0; j < actionSize ; j++){
				result[i][j] = 0;
			}
		}
		
		for(Action myAct : Action.values()){
			for(Action oppAct : Action.values()){
				int resultTemp = this.simulate(myAct,oppAct);
				result[myAct.ordinal()][oppAct.ordinal()] = resultTemp;
			}
		}
		
		return result;
		
	}
	
	/** execute simulation using myActData and oppActData*/
	public Action simulate(Deque<Action> myActData,Deque<Action> oppActData,int[] check){		
		int myActionSize = myActData.size();
		int[][] result = new int[myActionSize][2];
		int my=0;
		int resultMax = -1000;
		int actNum = 9;
		
		// initialize array
		for(int i = 0 ; i < myActionSize ; i++){
			result[i][0] = 0;
		}
		// set ordinal of each myActData to array
		for(Iterator<Action> myAct = myActData.iterator();myAct.hasNext();my++){
			result[my][1] = myAct.next().ordinal();
		}
		
		my = 0;
		
		// execute simulation by a round robin
		for(Iterator<Action> i = myActData.iterator();i.hasNext();my++){
			Action myAct = i.next();
			for(Iterator<Action> j = oppActData.iterator();j.hasNext();){
				Action oppAct = j.next();
				int resultTemp = this.simulate(myAct,oppAct);
				result[my][0] += resultTemp*check[oppAct.ordinal()];
			}
			
		}
		
		// search the maximum evaluation value and set the ordinal
		for(int i = 0 ; i < myActionSize ; i++){
			if(resultMax < result[i][0]){
				resultMax = result[i][0];
				actNum = result[i][1];
			}
		}
		
		// if all evaluation value are negative value, an AI conducts CROUCH_GUARD
		if(resultMax < 0) return Action.CROUCH_GUARD;
		
		// return the Action with the maximum evaluation value
		return Action.values()[actNum];
	}	
}
