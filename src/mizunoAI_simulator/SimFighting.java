package mizunoAI_simulator;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import structs.Key;
import structs.KeyData;
import transform.Transform;
import enumerate.Action;
import enumerate.State;
import mizunoAI_simulator.SimCharacter;
import mizunoAI_simulator.SimAttack;

public class SimFighting {

	private static final int INPUT_LIMIT = 30;
	private final static int SIMULATE_LIMIT = 60;
	private int stageMaxX;
	private int stageMaxY;
	private SimCharacter playerOneCharacter;
	private SimCharacter playerTwoCharacter;
	private Deque<SimAttack> attackDeque;
	private Deque<Action> one;
	private Deque<Action> two;
	
	public SimFighting(mizunoAI_simulator.SimCharacter P1,mizunoAI_simulator.SimCharacter P2,Deque<SimAttack> attackDeque,Action one ,Action two){
		playerOneCharacter = P1;
		playerTwoCharacter = P2;
		this.attackDeque = attackDeque;
		this.one = new LinkedList<Action>();
		this.two = new LinkedList<Action>();
		for(int i = 0;i<3;i++){
			this.one.add(one);
			this.two.add(two);
		}
		stageMaxX = 960;
		stageMaxY = 640;
	}

	public void processingFight(){
		
		updateCharacter();

		if(!one.isEmpty()){
			if(ableAction(playerOneCharacter,one.getFirst())){
				if(one.getFirst() != Action.STAND_D_DF_FC || playerOneCharacter.getEnergy() >= 300) playerOneCharacter.runMotion(one.pop());
			}
				
		}
		if(!two.isEmpty()){
			if(ableAction(playerTwoCharacter,two.getFirst())){
				if(two.getFirst() != Action.STAND_D_DF_FC || playerTwoCharacter.getEnergy() >= 300) playerTwoCharacter.runMotion(two.pop());
			}
		}
		
		calculationAttackParameter();

		calculationHit();
		
	}
	
	/**
	 * This methods calculate parameter of Attack object's.
	 */
	private void calculationAttackParameter(){
		// update coordinate of Attacks(short distance)
		if(playerOneCharacter.getAttack() != null) {
			if(!playerOneCharacter.getAttack().update(playerOneCharacter)) playerOneCharacter.destroyAttackInstance();
		}
		if(playerTwoCharacter.getAttack() != null) {
			if(!playerTwoCharacter.getAttack().update(playerTwoCharacter)) playerTwoCharacter.destroyAttackInstance();
		}
		
		// update coordinate of Attacks(long distance)
		for(int i = 0 ; i < attackDeque.size() ; i++){
			// if attack's nowFrame reach end of duration, remove it.
			if(attackDeque.getFirst().update()){
				attackDeque.addLast(attackDeque.removeFirst());
			}
			else attackDeque.removeFirst();
		}		
	}

	/**
	 * This method calculates collision effect.
	 */
	private void calculationHit(){
		boolean p1AttackCheck = false;
		boolean p2AttackCheck = false;
		SimAttack p1Attack, p2Attack;
		
		p1Attack = new SimAttack(playerOneCharacter.getAttack());
		p2Attack = new SimAttack(playerTwoCharacter.getAttack());
		
		// long distance attack
				// see deque element in order and calculation collision effect
				for(int i = 0 ; i < attackDeque.size() ; i++){
					// attack = 2P suffer = 1P
					if(!attackDeque.getFirst().isPlayerNumber())
					{
						// if attack hit character, run guard or hit motion and remove this attack. 
						if(detectionHit(playerOneCharacter , attackDeque.getFirst())) playerOneCharacter.hitAttackObject(playerTwoCharacter, attackDeque.removeFirst());
						else attackDeque.addLast(attackDeque.removeFirst());
					}
					// attack = 1P suffer = 2P
					else
					{
						// if attack hit character, run guard or hit motion and remove this attack. 
						if(detectionHit(playerTwoCharacter , attackDeque.getFirst())) playerTwoCharacter.hitAttackObject(playerOneCharacter, attackDeque.removeFirst());
						else attackDeque.addLast(attackDeque.removeFirst());
					}
				}	

				// short distance attack
				// Is attack presence?
				if(playerOneCharacter.getAttack() != null){
					if(detectionHit(playerTwoCharacter , playerOneCharacter.getAttack()))
					{
						p1AttackCheck = true; 
					}
				}

				if(playerTwoCharacter.getAttack() != null){
					if(detectionHit(playerOneCharacter , playerTwoCharacter.getAttack()))
					{
						p2AttackCheck = true; 
					}
				}
				
				if(p1AttackCheck){
					// if attack hit character, run guard or hit motion and remove this attack. 
					playerTwoCharacter.hitAttackObject(playerOneCharacter,p1Attack);
					// run effect of attacker
					playerOneCharacter.hitAttack();
				}
				
				if(p2AttackCheck){
					// if attack hit character, run guard or hit motion and remove this attack. 
					playerOneCharacter.hitAttackObject(playerTwoCharacter,p2Attack);
					// run effect of attacker
					playerTwoCharacter.hitAttack();
				}
	}
	
	/**
	 * Calculate collision.
	 * @param characterObj
	 * @param attackOjb
	 * @return hit or not
	 */
	private boolean detectionHit(SimCharacter characterObj, SimAttack attackOjb){
		if(characterObj.getState() == State.DOWN){
			return false;
		}else if(characterObj.getHitAreaL() < attackOjb.getHitAreaNow().getR() && characterObj.getHitAreaR() > attackOjb.getHitAreaNow().getL() && characterObj.getHitAreaT() < attackOjb.getHitAreaNow().getB() && characterObj.getHitAreaB() > attackOjb.getHitAreaNow().getT()){
			return true;
		}else if(characterObj.getHitAreaL() < attackOjb.getHitAreaNow().getR() && characterObj.getHitAreaT() < attackOjb.getHitAreaNow().getB() && characterObj.getHitAreaR() > attackOjb.getHitAreaNow().getL() && characterObj.getHitAreaB() > attackOjb.getHitAreaNow().getT()){
			return true;
		}else{
			return false;
		}	
	}
	
	/**
	 * @param character
	 * @param action
	 * @return Character is able to act or not.
	 */
	private boolean ableAction(SimCharacter character, Action action){
		boolean checkFrame = (character.getMotionVector().elementAt(character.getAction().ordinal()).getCancelAbleFrame() <= character.getMotionVector().elementAt(character.getAction().ordinal()).getFrameNumber()-character.getRemainingFrame());

		boolean checkAction	= (character.getMotionVector().elementAt(character.getAction().ordinal()).getCancelAbleMotionLevel()>=character.getMotionVector().elementAt(action.ordinal()).getMotionLevel());
		if(character.isControl())
		{
			return true;
		}else if((character.isHitConfirm() && checkFrame && checkAction )){
			return true;
		}
		else return false;
	}

	/**
	 * Update character's parameter
	 */
	private void updateCharacter(){
		// update each character.
		playerOneCharacter.update();
		playerTwoCharacter.update();

		// enque object attack if the data is missile decision
		if(playerOneCharacter.getAttack() != null) {
			if(playerOneCharacter.getAttack().checkProjectile()){
				SimAttack obj = new SimAttack(playerOneCharacter.getAttack());
				attackDeque.addLast(obj);
				playerOneCharacter.destroyAttackInstance();
			}
		}
		if(playerTwoCharacter.getAttack() != null) {
			if(playerTwoCharacter.getAttack().checkProjectile()){
				SimAttack obj = new SimAttack(playerTwoCharacter.getAttack());
				attackDeque.addLast(obj);
				playerTwoCharacter.destroyAttackInstance();
			}
		}

		// change player's direction
		if(playerOneCharacter.isControl())
			playerOneCharacter.frontDecision(	playerOneCharacter.getHitAreaL()+(playerOneCharacter.getHitAreaR()-playerOneCharacter.getHitAreaL())/2,
												playerTwoCharacter.getHitAreaL()+(playerTwoCharacter.getHitAreaR()-playerTwoCharacter.getHitAreaL())/2);
		
		if(playerTwoCharacter.isControl())
			playerTwoCharacter.frontDecision(	playerTwoCharacter.getHitAreaL()+(playerTwoCharacter.getHitAreaR()-playerTwoCharacter.getHitAreaL())/2,
												playerOneCharacter.getHitAreaL()+(playerOneCharacter.getHitAreaR()-playerOneCharacter.getHitAreaL())/2);

		// run pushing effect
		detectionPush(playerOneCharacter,playerTwoCharacter);
		// run collision of first and second character.
		detectionFusion(playerOneCharacter,playerTwoCharacter);
		// run effect when character's are in the end of stage.
		decisionEndStage();
	}
	
	/**
	 * Characters push each other
	 * @param Player1
	 * @param Player2
	 */
	private void detectionPush(SimCharacter Player1, SimCharacter Player2){
		// whether the conflict of first and second player or not?
		if(Player1.getHitAreaL() < Player2.getHitAreaR() && Player1.getHitAreaT() < Player2.getHitAreaB() && Player1.getHitAreaR() > Player2.getHitAreaL() && Player1.getHitAreaB() > Player2.getHitAreaT()){
			// P1��P2��
			if(Player1.isFront()){
				if(Player1.getSpeedX() > -Player2.getSpeedX()){
					Player2.moveX(pushMovement(Player1.getSpeedX(), Player2.getSpeedX()));
				}
				else if(Player1.getSpeedX() < -Player2.getSpeedX()){
					Player1.moveX(pushMovement(Player2.getSpeedX(), Player1.getSpeedX()));
				}
				else{
					Player1.moveX(Player2.getSpeedX());
					Player2.moveX(Player1.getSpeedX());
				}
			}
			else{
				if(-Player1.getSpeedX() > Player2.getSpeedX()){
					Player2.moveX(pushMovement(Player1.getSpeedX(), Player2.getSpeedX()));
				}
				else if(-Player1.getSpeedX() < Player2.getSpeedX()){
					Player1.moveX(pushMovement(Player2.getSpeedX(), Player1.getSpeedX()));
				}
				else{
					Player1.moveX(Player2.getSpeedX());
					Player2.moveX(Player1.getSpeedX());
				}
			}
		}
	}

	/**
	 * A determination is made in case of a state such as that overlap almost character to move the character.
	 * @param Player1
	 * @param Player2
	 */
	private void detectionFusion(SimCharacter Player1, SimCharacter Player2){
		// whether the conflict of first and second player or not?
		if( Player1.getHitAreaL() < Player2.getHitAreaR() && Player1.getHitAreaT() < Player2.getHitAreaB() && Player1.getHitAreaR() > Player2.getHitAreaL() && Player1.getHitAreaB() > Player2.getHitAreaT()){
			// if first player is left
			if((Player1.getHitAreaL() + (Player1.getHitAreaR() - Player1.getHitAreaL()) / 2) 
					< (Player2.getHitAreaL() + (Player2.getHitAreaR()-Player2.getHitAreaL()) / 2)){
				Player1.moveX(-2);
				Player2.moveX(2);
			// if second player is left 
			}else if((Player1.getHitAreaL() + (Player1.getHitAreaR() - Player1.getHitAreaL()) / 2) 
					> (Player2.getHitAreaL() + (Player2.getHitAreaR()-Player2.getHitAreaL()) / 2)){
				Player1.moveX(2);
				Player2.moveX(-2);
			}else{
				if(Player1.isFront()){
					Player1.moveX(-2);
					Player2.moveX(2);
				}
				else{
					Player1.moveX(2);
					Player2.moveX(-2);
				}
			}
		}
	}
	
	/**
	 * Effect when characters are in the end of stage.
	 */
	private void decisionEndStage(){
		// if action is down, character will be rebound.
		// first player's effect
		if(playerOneCharacter.getHitAreaR()>stageMaxX){

			if(playerOneCharacter.getAction() == Action.DOWN){
				playerOneCharacter.reversalSpeedX();
			}
			playerOneCharacter.moveX(-playerOneCharacter.getHitAreaR()+stageMaxX);
		}
		else if(playerOneCharacter.getHitAreaL() < 0){
			if(playerOneCharacter.getAction() == Action.DOWN){
				playerOneCharacter.reversalSpeedX();
			}
			playerOneCharacter.moveX(-playerOneCharacter.getHitAreaL());			
		}
		// second player's effect
		if(playerTwoCharacter.getHitAreaR()>stageMaxX){
			if(playerTwoCharacter.getAction() == Action.DOWN){
				playerTwoCharacter.reversalSpeedX();
			}
			playerTwoCharacter.moveX(-playerTwoCharacter.getHitAreaR()+stageMaxX);
		}
		else if(playerTwoCharacter.getHitAreaL() < 0){
			if(playerTwoCharacter.getAction() == Action.DOWN){
				playerTwoCharacter.reversalSpeedX();
			}
			playerTwoCharacter.moveX(-playerTwoCharacter.getHitAreaL());			
		}
	}
	
	/**
	 * Calculate the amount of movement
	 * @param P1spd
	 * @param P2spd
	 * @return The amount of movement
	 */
	private int pushMovement(int P1spd, int P2spd){
		return (P1spd-P2spd);	
	}

	private Deque<KeyData> deepCopyInput(Deque<KeyData> inputKeyData){
		
		Deque<KeyData> copy = new LinkedList<KeyData>();
		for(Iterator<KeyData> i = inputKeyData.iterator();i.hasNext();){
			KeyData temp = new KeyData(i.next());
			copy.addLast(temp);
		}
		
		return copy;
	}
	
}
