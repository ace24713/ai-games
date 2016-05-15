package mizunoAI_simulator;

import java.util.Vector;

import setting.Properties;
import structs.MotionData;
import structs.CharacterData;
import enumerate.Action;
import enumerate.State;
import mizunoAI_simulator.SimAttack;

public class SimCharacter {
	/**
	 * Hit point is decreasing point by attacking so it have no minimum value.<br>
	 */
	private int hp;
	/**
	 * This value would be used using special skill.<br>
	 */
	private int energy;
	/**
	 * The x-coordinate of the character.br>
	 */
	private int x;
	/**
	 * The y-coordinate of the character.<br>
	 */
	private int y;
	/**
	 * Character's horizontal moving speed.<br>
	 */
	private int speedX;
	/**
	 * Character's vertical moving speed.<br>
	 */
	private int speedY;
	/**
	 * Character have four state stand, crouch, air and down.<br>
	 */
	private State state;
	/**
	 * Character can use action like as jump or dash.<br>
	 */
	private Action action;
	/**
	 * Attack hit confirm.<br>
	 */
	private boolean hitConfirm;
	/**
	 * Number of attack hit for now action.<br>
	 */
	private int hitNumber;
	/**
	 * This boolean value has information of side which character face to.<br>
	 */
	private boolean front;
	/**
	 * According to remainingFrame(and other information) of character be able to control.<br>
	 */
	private boolean control;
	/**
	 * This value have frame number that character not be able to control.
	 */
	private int remainingFrame;
	/**
	 * Character can use attack.<br>
	 */
	private SimAttack attack;
	
	/**
	 * player side`s flag
	 */
	private boolean playerNumber;

	/**
	 * Value of max energy.
	 */
	private int maxEnergy;
	/**
	 * MotionData of this character.
	 * One motion can be access with method playerOneMotion.elementAt(index).
	 * ex. playerOneMotion.elementAt(Action.STAND_A.ordinal()).getSpeedX();
	 */
	private Vector<MotionData> motionVector;
	/**
	 * game setting properties
	 */
	private Properties prop;
	
	public SimCharacter(CharacterData characterData,Vector<MotionData> motionData,boolean player){
		this.x = characterData.getX();
		this.y = characterData.getY();
		this.hp = characterData.getHp();
		this.energy = characterData.getEnergy();
		this.speedX = characterData.getSpeedX();
		this.speedY = characterData.getSpeedY();
		this.state = characterData.getState();
		this.action = characterData.getAction();
		this.hitConfirm = false;
		this.hitNumber = 0;
		this.front = characterData.isFront();
		this.control = characterData.isControl();
		this.remainingFrame = characterData.getRemainingFrame();
		if(characterData.getAttack() != null)this.attack = new SimAttack(characterData.getAttack());
		else this.attack = null;
		this.playerNumber = player;
		this.maxEnergy = 1000;
		this.motionVector = motionData;
		this.prop = new Properties();
		}
	
	public SimAttack runMotion(Action action){
		if(getAction() != action){
			setRemainingFrame(motionVector.elementAt(action.ordinal()).getFrameNumber());
			setHitConfirm(false);
			setAttack(null);
			setHitNumber(0);
			setEnergy(getEnergy()+motionVector.elementAt(action.ordinal()).getAttackStartAddEnergy());
		}
		setAction(action);
		setState(motionVector.elementAt(action.ordinal()).getState());
		if(motionVector.elementAt(action.ordinal()).getSpeedX() != 0){
			if(isFront()){
				setSpeedX(motionVector.elementAt(action.ordinal()).getSpeedX());
			}
			else{
				setSpeedX(-motionVector.elementAt(action.ordinal()).getSpeedX());
			}
		}
		setSpeedY(getSpeedY() + motionVector.elementAt(action.ordinal()).getSpeedY());
		setControl(motionVector.elementAt(action.ordinal()).isControl());
		SimAttack attack;
		
		//System.out.println(action.name());
		
		if(motionVector.elementAt(action.ordinal()).getAttackType() != 0){
		
			attack = new SimAttack(motionVector.elementAt(action.ordinal()).getAttackHit(),
					motionVector.elementAt(action.ordinal()).getAttackSpeedX(),motionVector.elementAt(action.ordinal()).getAttackSpeedY(),
					motionVector.elementAt(action.ordinal()).getAttackStartUp(),motionVector.elementAt(action.ordinal()).getAttackInterval(),
					motionVector.elementAt(action.ordinal()).getAttackRepeat(),motionVector.elementAt(action.ordinal()).getAttackActive(),
					motionVector.elementAt(action.ordinal()).getAttackHitDamage(),motionVector.elementAt(action.ordinal()).getAttackGuardDamage(),
					motionVector.elementAt(action.ordinal()).getAttackStartAddEnergy(),motionVector.elementAt(action.ordinal()).getAttackHitAddEnergy(),motionVector.elementAt(action.ordinal()).getAttackGuardAddEnergy(),
					motionVector.elementAt(action.ordinal()).getAttackGiveEnergy(),
					motionVector.elementAt(action.ordinal()).getAttackImpactX(),motionVector.elementAt(action.ordinal()).getAttackImpactY(),
					motionVector.elementAt(action.ordinal()).getAttackGiveGuardRecov(),
					motionVector.elementAt(action.ordinal()).getAttackKnockBack(),
					motionVector.elementAt(action.ordinal()).getAttackHitStop(),
					motionVector.elementAt(action.ordinal()).getAttackType(),
					motionVector.elementAt(action.ordinal()).isAttackDownProperty());
		}
		else{
			attack = null;
		}
		
		return attack;
	}
	
	public void createAttackInstance(){
		if(invokeDecision())
		{	
			SimAttack attack = new SimAttack(motionVector.elementAt(action.ordinal()).getAttackHit(),
					motionVector.elementAt(action.ordinal()).getAttackSpeedX(),motionVector.elementAt(action.ordinal()).getAttackSpeedY(),
					motionVector.elementAt(action.ordinal()).getAttackStartUp(),motionVector.elementAt(action.ordinal()).getAttackInterval(),
					motionVector.elementAt(action.ordinal()).getAttackRepeat(),motionVector.elementAt(action.ordinal()).getAttackActive(),
					motionVector.elementAt(action.ordinal()).getAttackHitDamage(),motionVector.elementAt(action.ordinal()).getAttackGuardDamage(),
					motionVector.elementAt(action.ordinal()).getAttackStartAddEnergy(),motionVector.elementAt(action.ordinal()).getAttackHitAddEnergy(),motionVector.elementAt(action.ordinal()).getAttackGuardAddEnergy(),
					motionVector.elementAt(action.ordinal()).getAttackGiveEnergy(),
					motionVector.elementAt(action.ordinal()).getAttackImpactX(),motionVector.elementAt(action.ordinal()).getAttackImpactY(),
					motionVector.elementAt(action.ordinal()).getAttackGiveGuardRecov(),
					motionVector.elementAt(action.ordinal()).getAttackKnockBack(),
					motionVector.elementAt(action.ordinal()).getAttackHitStop(),
					motionVector.elementAt(action.ordinal()).getAttackType(),
					motionVector.elementAt(action.ordinal()).isAttackDownProperty());
			SimAttack obj = new SimAttack(attack);
			obj.materialise(playerNumber,x,y,front);
			setAttack(obj);
		}
	}
	
	public void destroyAttackInstance(){
		setAttack(null);
	}
	
	public void	hitAttack(){
		setHitConfirm(true);
		setHitNumber(getHitNumber()+1);
		setAttack(null);
	}
	
	public void hitAttackObject(SimCharacter other , SimAttack attackObject){
		boolean guardCheck;
		int hitDirection;

		if((other.getHitAreaL()+other.getHitAreaR())/2 <= (getHitAreaL() + getHitAreaR())/2){
			hitDirection = 1;
		}
		else{
			hitDirection = -1;
		}
		
		switch(getAction()){
		
		case STAND_GUARD:
			if((attackObject.getAttackType() == 1) || (attackObject.getAttackType() == 2)){
				runMotion(Action.STAND_GUARD_RECOV);
				guardCheck = true;
			}
			else guardCheck = false;
			break;
			
		case CROUCH_GUARD:
			if((attackObject.getAttackType() == 1) || (attackObject.getAttackType() == 3)){
				runMotion(Action.CROUCH_GUARD_RECOV);
				guardCheck = true;
			}
		else guardCheck = false;
		break;
		
		case AIR_GUARD:
			if((attackObject.getAttackType() == 1) || (attackObject.getAttackType() == 2)){
				runMotion(Action.AIR_GUARD_RECOV);
				guardCheck = true;
			}
		else guardCheck = false;
		break;
		
		case STAND_GUARD_RECOV:
			runMotion(Action.STAND_GUARD_RECOV);
			guardCheck = true;
			break;
			
		case CROUCH_GUARD_RECOV:
			runMotion(Action.CROUCH_GUARD_RECOV);
			guardCheck = true;
			break;
			
		case AIR_GUARD_RECOV:
			runMotion(Action.AIR_GUARD_RECOV);
			guardCheck = true;
			break;
			
		default:
			guardCheck = false;
			break;
		}
		
		if(guardCheck){
			setHp(getHp() - attackObject.getGuardDamage());
			setEnergy(getEnergy() + attackObject.getGiveEnergy());
			setSpeedX(hitDirection*attackObject.getKnockBack());
			other.setEnergy(other.getEnergy()+attackObject.getGuardAddEnergy());
			setRemainingFrame(attackObject.getGiveGuardRecov());
		}else{
			if((attackObject.getAttackType() == 4)){
				if((getState() != State.AIR) && (this.getState() != State.DOWN)){
					runMotion(Action.THROW_SUFFER);
					other.runMotion(Action.THROW_HIT);
					setHp(getHp() - attackObject.getHitDamage());
					setEnergy(getEnergy() + attackObject.getGiveEnergy());
					other.setEnergy(other.getEnergy()+attackObject.getHitAddEnergy());
				}
			}else{
				setHp(getHp() - attackObject.getHitDamage());
				setEnergy(getEnergy() + attackObject.getGiveEnergy());
				setSpeedX(hitDirection*attackObject.getImpactX());
				setSpeedY(attackObject.getImpactY());
				other.setEnergy(other.getEnergy()+attackObject.getHitAddEnergy());

				if(attackObject.isDownProperty()){
					runMotion(Action.CHANGE_DOWN);
					setRemainingFrame(motionVector.elementAt(action.ordinal()).getFrameNumber());
				}else{
				
					switch(getState())
					{
					case STAND:
						runMotion(Action.STAND_RECOV);
						break;
					case CROUCH:
						runMotion(Action.CROUCH_RECOV);
						break;
					case AIR:
						runMotion(Action.AIR_RECOV);
						break;
					default:
						break;

					}
				}
			}
		}
	}

	public void frontDecision(int selfX,int otherX){
		if(selfX<otherX) setFront(true);
		else setFront(false);
	}
	
	public boolean invokeDecision(){
		if((getMotionVector().elementAt(getAction().ordinal()).getFrameNumber() - getMotionVector().elementAt(getAction().ordinal()).getAttackStartUp()) == getRemainingFrame()) return true;
		else return false;
	}
	
	public boolean durationDecision(){
		if((getMotionVector().elementAt(getAction().ordinal()).getFrameNumber() - getMotionVector().elementAt(getAction().ordinal()).getAttackActive()) >= getRemainingFrame()) return true;
		else return false;
	}
	
	public void update(){
		moveX(getSpeedX());
		moveY(getSpeedY());
		
		frictionEffect();
		gravityEffect();
		
		setRemainingFrame(getRemainingFrame()-1);
		
		if(getEnergy() > getMaxEnergy()) setEnergy(getMaxEnergy());
		
		if(getY() >= 320){
			if(motionVector.elementAt(getAction().ordinal()).isLandingFlag()){
				runMotion(Action.LANDING);
				setSpeedY(0);
			}
			setY(320);
		}
		
		createAttackInstance();
		
		if(getRemainingFrame() <= 0){
			if(getAction() == Action.CHANGE_DOWN){
				runMotion(Action.DOWN);
			}
			else if(getAction() == Action.DOWN){
				runMotion(Action.RISE);
			}
			else if(getState() == State.AIR || getY() < 320){
				runMotion(Action.AIR);
			}
			else{
				runMotion(Action.STAND);
			}
		}
	}
	
	public void moveX(int relativePosition){
		setX(getX() + relativePosition);
	}
	
	public void moveY(int relativePosition){
		setY(getY() + relativePosition);		
	}
	
	public void frictionEffect(){
		if(!(getY() < 320)){
			if(getSpeedX() > 0){
				setSpeedX(getSpeedX()-prop.FRICTION);
			}
			else if(getSpeedX() < 0){
				setSpeedX(getSpeedX()+prop.FRICTION);
			}
		}
	}

	public void gravityEffect(){
		if(getHitAreaB() >= 320 + 256){
			setSpeedY(0);
		}
		else
		{
			setSpeedY(getSpeedY()+prop.GRAVITY);
		}
	}
	
	private void setHp(int hp) {
		this.hp = hp;
	}
	private void setEnergy(int energy) {
		this.energy = energy;
	}
	private void setX(int x) {
		this.x = x;
	}
	private void setY(int y) {
		this.y = y;
	}
	private void setSpeedX(int speedX) {
		this.speedX = speedX;
	}
	public void reversalSpeedX(){
		this.speedX = -(this.speedX/2);
	}
	private void setSpeedY(int speedY) {
		this.speedY = speedY;
	}
	private void setState(State state) {
		this.state = state;
	}
	private void setAction(Action action) {
		this.action = action;
	}
	public void setHitConfirm(boolean hitConfirm) {
		this.hitConfirm = hitConfirm;
	}
	public void setFront(boolean front) {
		this.front = front;
	}
	private void setControl(boolean control) {
		this.control = control;
	}
	private void setRemainingFrame(int remainingFrame) {
		this.remainingFrame = remainingFrame;
	}
	
	public int getHp() {
		return hp;
	}
	public int getEnergy() {
		return energy;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getSpeedX() {
		return speedX;
	}
	public int getSpeedY() {
		return speedY;
	}
	public State getState() {
		return state;
	}
	public Action getAction() {
		return action;
	}
	public boolean isHitConfirm(){
		return hitConfirm;
	}
	public int getHitNumber() {
		return hitNumber;
	}
	public void setHitNumber(int hitNumber) {
		this.hitNumber = hitNumber;
	}
	public boolean isFront() {
		return front;
	}
	public boolean isControl() {
		return control;
	}
	public int getRemainingFrame() {
		return remainingFrame;
	}
	public boolean isPlayerNumber() {
		return playerNumber;
	}
	public int getMaxEnergy() {
		return maxEnergy;
	}
	public int getHitAreaR(){
		if(this.isFront())
		{
			return motionVector.elementAt((this.getAction()).ordinal()).getHit().getR() + x;
		}
		else
		{
			return 255 - motionVector.elementAt((this.getAction()).ordinal()).getHit().getL() + x;
		}
	}
	public int getHitAreaL(){
		if(this.isFront())
		{
			return motionVector.elementAt((this.getAction()).ordinal()).getHit().getL() + x;
		}
		else
		{
			return 255 - motionVector.elementAt((this.getAction()).ordinal()).getHit().getR() + x;
		}
	}
	public int getHitAreaT(){
		return motionVector.elementAt((this.getAction()).ordinal()).getHit().getT() + y;		
	}
	public int getHitAreaB(){
		return motionVector.elementAt((this.getAction()).ordinal()).getHit().getB() + y;		
	}
	public Vector<MotionData> getMotionVector() {
		return motionVector;
	}
	public SimAttack getAttack() {
		return attack;
	}
	public void setAttack(SimAttack attack) {
		if(attack != null){
			SimAttack temp = new SimAttack(attack);
			this.attack = temp;
		}
		else{
			this.attack = null;
		}
	}
}
