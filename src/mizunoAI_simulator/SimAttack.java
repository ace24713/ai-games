package mizunoAI_simulator;

import structs.HitArea;
import fighting.Attack;

public class SimAttack {
	/**
	 * refresh the information per frame,and check the result of attack
	 */
	private HitArea hitAreaNow;

	/**
	 * the number of frame
	 */
	private	int nowFrame;
	
	/**
	 * player side`s flag
	 */
	private	boolean playerNumber;

	/**
	 * HitArea`s information and position
	 */
	private	HitArea hitAreaSetting;

	/**
	 * attack action's moving value
	 */
	private int settingSpeedX, settingSpeedY;

	/**
	 * attack action's moving value
	 */
	private int speedX, speedY;

	/**
	 * attack effect start sign (per frame)
	 */
	private	int startUp;

	/**
	 * continuation attack interval 
	 */
	private	int interval;
	
	/**
	 * number of times of a continuation attack 
	 */
	private	int repeat;

	/**
	 * attack action`s active time
	 */
	private	int active;

	/**
	 * attack action`s damage
	 */
	private	int hitDamage, guardDamage;

	/**
	 * ExEnergy value
	 */
	private	int startAddEnergy, hitAddEnergy, guardAddEnergy, giveEnergy;

	/**
	 * feedback value
	 */
	private	int impactX,impactY;

	/**
	 * recovertime
	 */
	private	int giveGuardRecov;

	/**
	 * knockback value
	 */
	private	int knockBack;

	/**
	 * hitstop time
	 */
	private	int hitStop;

	/**
	 * attack`s typeA 1=high 2=mid 3=low
	 */
	private	int attackType;

	/**
	 * down flag , 1 = can push down 0=normal hit
	 */
	private	boolean downProperty;

	public SimAttack() {
		active = 0;
		this.hitAreaNow = new HitArea();
	}
	
	public SimAttack(mizunoAI_simulator.SimAttack attack){
		if(attack == null){}
		else{
			hitAreaNow = new HitArea();
			hitAreaNow.setParameters(attack.getHitAreaNow());
			playerNumber = attack.isPlayerNumber();
			hitAreaSetting = attack.getHitAreaSetting();
			settingSpeedX = attack.getSettingSpeedX();
			settingSpeedY = attack.getSettingSpeedY();
			speedX = attack.getSpeedX();
			speedY = attack.getSpeedY();
			startUp = attack.getStartUp();
			interval = attack.getInterval();
			repeat = attack.getRepeat();
			active = attack.getActive();
			hitDamage = attack.getHitDamage();
			guardDamage = attack.getGuardDamage();
			startAddEnergy = attack.getStartAddEnergy();
			hitAddEnergy = attack.getHitAddEnergy();
			guardAddEnergy = attack.getGuardAddEnergy();
			giveEnergy = attack.getGiveEnergy();
			impactX = attack.getImpactX();
			impactY = attack.getImpactY();
			giveGuardRecov = attack.getGiveGuardRecov();
			knockBack = attack.getKnockBack();
			hitStop = attack.getHitStop();
			attackType = attack.getAttackType();
			downProperty = attack.isDownProperty();
		}
	}
	
	public SimAttack(Attack attack){
		if(attack == null){}
		else{
			hitAreaNow = new HitArea();
			hitAreaNow.setParameters(attack.getHitAreaNow());
			playerNumber = attack.isPlayerNumber();
			hitAreaSetting = attack.getHitAreaSetting();
			settingSpeedX = attack.getSettingSpeedX();
			settingSpeedY = attack.getSettingSpeedY();
			speedX = attack.getSpeedX();
			speedY = attack.getSpeedY();
			startUp = attack.getStartUp();
			interval = attack.getInterval();
			repeat = attack.getRepeat();
			active = attack.getActive();
			hitDamage = attack.getHitDamage();
			guardDamage = attack.getGuardDamage();
			startAddEnergy = attack.getStartAddEnergy();
			hitAddEnergy = attack.getHitAddEnergy();
			guardAddEnergy = attack.getGuardAddEnergy();
			giveEnergy = attack.getGiveEnergy();
			impactX = attack.getImpactX();
			impactY = attack.getImpactY();
			giveGuardRecov = attack.getGiveGuardRecov();
			knockBack = attack.getKnockBack();
			hitStop = attack.getHitStop();
			attackType = attack.getAttackType();
			downProperty = attack.isDownProperty();
		}
	}
	
	public SimAttack(HitArea hitAreaInput,int SpeedXInput,int SpeedYInput,int InvokeInput,int IntervalInput,int RepeatInput,int activeInput,
			int HDamegeInput,int GDamageInput,int SAddEnergyInput,int HAddEnergyInput,int GAddEnergyInput,int GiveEnergyInput,
			int ImpactXInput,int ImpactYInput,int GiveGuardRecovInput,int KnockBackInput, int HitStopInput,
			int AttackTypeInput,boolean DownPropInput)
	{
		this.hitAreaNow = new HitArea();
		hitAreaSetting = hitAreaInput;
		settingSpeedX = SpeedXInput;
		settingSpeedY = SpeedYInput;
		startUp = InvokeInput;
		interval = IntervalInput;
		repeat = RepeatInput;
		active = activeInput;
		hitDamage = HDamegeInput;
		guardDamage = GDamageInput;
		startAddEnergy = SAddEnergyInput;
		hitAddEnergy = HAddEnergyInput;
		guardAddEnergy = GAddEnergyInput;
		giveEnergy = GiveEnergyInput;
		impactX = ImpactXInput;
		impactY = ImpactYInput;
		giveGuardRecov = GiveGuardRecovInput;
		knockBack = KnockBackInput;
		hitStop = HitStopInput;
		attackType = AttackTypeInput;
		downProperty = DownPropInput;
	}
	
	/**
	 * This method receives the player side's flag, player's position and player's direction. 
	 * It sets the parameters of a new attack instance. 
	 * @param PlayerNum the player side's flag 
	 * @param CharacterX the player's position of the x-coordinate
	 * @param CharacterY the player's position of the y-coordinate
	 * @param CharacterDirection the player's direction
	 */
	public void materialise(boolean PlayerNum,int CharacterX , int CharacterY , boolean CharacterDirection)
	{
		int L,R,T,B;
		playerNumber = PlayerNum;
		if(CharacterDirection)
		{
			L = CharacterX + hitAreaSetting.getL();
			R = CharacterX + hitAreaSetting.getR();
			speedX = settingSpeedX;
			speedY = settingSpeedY;
		
		}
		else
		{
			L = CharacterX + 256 - hitAreaSetting.getR();
			R = CharacterX + 256 - hitAreaSetting.getL();
			speedX = -settingSpeedX;
			speedY = settingSpeedY;
		}
		T = CharacterY + hitAreaSetting.getT();
		B = CharacterY + hitAreaSetting.getB();
		
		{
			HitArea hit = new HitArea(L,R,T,B);
			hitAreaNow.setParameters(hit);
		}
		nowFrame = 0;
		
	}
	
	/**
	 * Checks whether this attack is a projectile or not. 
	 * @return true if it is a projectile, otherwise false 
	 */
	public boolean checkProjectile(){
		if((settingSpeedX+settingSpeedY)==0) return false;
		else return true;
	}
	
	/**
	 * Updates the image and location per frame for the projectile. 
	 * @return true if this attack is in the active stage, otherwise false
	 */
	public boolean update()
	{
		if(speedX != 0 || speedY != 0){
			hitAreaNow.move(speedX, speedY);
		}
		nowFrame++;

		if(nowFrame > active) return false;

		return true;
	}

	/**
	 * Updates the image and location per frame. 
	 * @param character the character using this attack
	 * @return true if this attack is in the active stage, otherwise false
	 */
	public boolean update(SimCharacter character)
	{
		int L,R,T,B;
		
		if(character.isFront())
		{
			L = character.getX() + hitAreaSetting.getL();
			R = character.getX() + hitAreaSetting.getR();
			speedX = settingSpeedX;
			speedY = settingSpeedY;

		}
		
		else
		{
			L = character.getX() + 256 - hitAreaSetting.getR();
			R = character.getX() + 256 - hitAreaSetting.getL();
			speedX = -settingSpeedX;
			speedY = settingSpeedY;
		}
		T = character.getY() + hitAreaSetting.getT();
		B = character.getY() + hitAreaSetting.getB();

		{
			HitArea hit = new HitArea(L,R,T,B);
			hitAreaNow.setParameters(hit);
		}
		
		nowFrame++;

		if(nowFrame > active) return false;

		return true;
	}
	
	/**
	 * Returns HitArea's setting information.
	 * @return HitArea's setting information
	 */
	public HitArea getHitAreaSetting() {
		return hitAreaSetting;
	}

	/**
	 * Sets HitArea's setting information.
	 * @param hitAreaSetting HitArea's setting information 
	 */
	public void setHitAreaSetting(HitArea hitAreaSetting) {
		this.hitAreaSetting = hitAreaSetting;
	}
	
	/**
	 * Returns HitArea's information of this attack hit box in the current frame.
	 * @return HitArea's information of this attack hit box in the current frame 
	 */
	public HitArea getHitAreaNow() {
		return hitAreaNow;
	}
	
	/**
	 * Sets HitArea's information of this attack hit box in the current frame.
	 * @param hitAreaNow HitArea's information of this attack hit box in the current frame 
	 */
	public void setHitAreaNow(HitArea hitAreaNow) {
		this.hitAreaNow = hitAreaNow;
	}

	/**
	 * Returns the number of frames since this attack was used.
	 * @return the number of frames since this attack was used 
	 */
	public int getNowFrame() {
		return nowFrame;
	}

	/**
	 * Sets the number of frames since this attack was used. 
	 * @param nowFrame the number of frames since this attack was used 
	 */
	public void setNowFrame(int nowFrame) {
		this.nowFrame = nowFrame;
	}

	/**
	 * Returns the player side's flag.
	 * @return the player side's flag 
	 */
	public boolean isPlayerNumber() {
		return playerNumber;
	}

	/**
	 * Sets the player side's flag.
	 * @param playerNumber the player side's flag
	 */
	public void setPlayerNumber(boolean playerNumber) {
		this.playerNumber = playerNumber;
	}

	/**
	 * Returns the horizontal speed of the attack hit box (minus when moving left and plus when moving right).
	 * @return the horizontal speed of the attack hit box (minus when moving left and plus when moving right) 
	 */
	public int getSpeedX() {
		return speedX;
	}

	/**
	 * Sets the horizontal speed of the attack hit box (minus when moving left and plus when moving right).
	 * @param speed_x the horizontal speed of the attack hit box (minus when moving left and plus when moving right) 
	 */
	public void setSpeedX(int speed_x) {
		this.speedX = speed_x;
	}

	/**
	 * Returns the vertical speed of the attack hit box (minus when moving up and plus when moving down).
	 * @return the vertical speed of the attack hit box (minus when moving up and plus when moving down) 
	 */
	public int getSpeedY() {
		return speedY;
	}

	/**
	 * Sets the vertical speed of the attack hit box (minus when moving up and plus when moving down).
	 * @param speed_y the vertical speed of the attack hit box (minus when moving up and plus when moving down) 
	 */
	public void setSpeedY(int speed_y) {
		this.speedY = speed_y;
	}

	/**
	 * Returns the number of frames in Startup.
	 * @return the number of frames in Startup
	 */
	public int getStartUp() {
		return startUp;
	}

	/**
	 * Sets the number of frames in Startup.
	 * @param startUp the number of frames in Startup
	 */
	public void setStartUp(int startUp) {
		this.startUp = startUp;
	}
	
	/**
	 * not in use!
	 * @return not in use!
	 */
	public int getInterval() {
		return interval;
	}
	
	/**
	 * not in use!
	 * @param interval not in use! 
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}

	/**
	 * not in use!
	 * @return not in use! 
	 */
	public int getRepeat() {
		return repeat;
	}
	
	/**
	 * not in use!
	 * @param repeat not in use! 
	 */
	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}
	
	/**
	 * Returns the number of frames in Active.
	 * @return the number of frames in Active 
	 */
	public int getActive() {
		return active;
	}

	/**
	 * Sets the number of frames in Active.
	 * @param active the number of frames in Active 
	 */
	public void setActive(int active) {
		this.active = active;
	}

	/**
	 * Returns the damage value to the unguarded opponent hit by this skill.
	 * @return the damage value to the unguarded opponent hit by this skill  
	 */
	public int getHitDamage() {
		return hitDamage;
	}

	/**
	 * Sets the damage value to the unguarded opponent hit by this skill.
	 * @param hitDamage the damage value to the unguarded opponent hit by this skill 
	 */
	public void setHitDamage(int hitDamage) {
		this.hitDamage = hitDamage;
	}

	/**
	 * Returns the damage value to the guarded opponent hit by this skill.
	 * @return the damage value to the guarded opponent hit by this skill
	 */
	public int getGuardDamage() {
		return guardDamage;
	}

	/**
	 * Sets the damage value to the guarded opponent hit by this skill.
	 * @param guardDamage the damage value to the guarded opponent hit by this skill
	 */
	public void setGuardDamage(int guardDamage) {
		this.guardDamage = guardDamage;
	}
	
	/**
	 * Returns the value of the energy added to the character when it uses this skill.
	 * @return the value of the energy added to the character when it uses this skill
	 */
	public int getStartAddEnergy() {
		return startAddEnergy;
	}

	/**
	 * Sets the value of energy added to the character when it uses this skill.
	 * @param startAddEnergy the value of the energy added to the character when it uses this skill
	 */
	public void setStartAddEnergy(int startAddEnergy) {
		this.startAddEnergy = startAddEnergy;
	}
	
	/**
	 * Returns the value of the energy added to the character when this skill hits the opponent.
	 * @return the value of the energy added to the character when this skill hits the opponent
	 */
	public int getHitAddEnergy() {
		return hitAddEnergy;
	}

	/**
	 * Sets the value of the energy added to the character when this skill hits the opponent.
	 * @param hitAddEnergy the value of the energy added to the character when this skill hits the opponent
	 */
	public void setHitAddEnergy(int hitAddEnergy) {
		this.hitAddEnergy = hitAddEnergy;
	}

	/**
	 * Returns the value of the energy added to the character when this skill is blocked by the opponent.
	 * @return the value of the energy added to the character when this skill is blocked by the opponent 
	 */
	public int getGuardAddEnergy() {
		return guardAddEnergy;
	}

	/**
	 * Sets the value of the energy added to the character when this skill is blocked by the opponent.
	 * @param guardAddEnergy the value of the energy added to the character when this skill is blocked by the opponent 
	 */
	public void setGuardAddEnergy(int guardAddEnergy) {
		this.guardAddEnergy = guardAddEnergy;
	}

	/**
	 * Returns the value of the energy added to the opponent when it is hit by this skill.
	 * @return the value of the energy added to the opponent when it is hit by this skill
	 */
	public int getGiveEnergy() {
		return giveEnergy;
	}

	/**
	 * Sets the value of the energy added to the opponent when it is hit by this skill.
	 * @param giveEnergy the value of the energy added to the opponent when it is hit by this skill
	 */
	public void setGiveEnergy(int giveEnergy) {
		this.giveEnergy = giveEnergy;
	}

	/**
	 * Returns the change in the horizontal speed of the opponent when it is hit by this skill.
	 * @return the change in the horizontal speed of the opponent when it is hit by this skill
	 */
	public int getImpactX() {
		return impactX;
	}

	/**
	 * Sets the change in the horizontal speed of the opponent when it is hit by this skill.
	 * @param impactX the change in the horizontal speed of the opponent when it is hit by this skill
	 */
	public void setImpactX(int impactX) {
		this.impactX = impactX;
	}

	/**
	 * Returns the change in the vertical speed of the opponent when it is hit by this skill.
	 * @return the change in the vertical speed of the opponent when it is hit by this skill
	 */
	public int getImpactY() {
		return impactY;
	}

	/**
	 * Sets the change in the vertical speed of the opponent when it is hit by this skill.
	 * @param impactY the change in the vertical speed of the opponent when it is hit by this skill
	 */
	public void setImpactY(int impactY) {
		this.impactY = impactY;
	}

	/**
	 * Returns the number of frames that the guarded opponent takes to resume to his normal status after it is hit by this skill.
	 * @return the number of frames that the guarded opponent takes to resume to his normal status after it is hit by this skill
	 */
	public int getGiveGuardRecov() {
		return giveGuardRecov;
	}

	/**
	 * Sets the number of frames that the guarded opponent takes to resume to his normal status after it is hit by this skill.
	 * @param giveGuardRecov the number of frames that the guarded opponent takes to resume to his normal status after it is hit by this skill
	 */
	public void setGiveGuardRecov(int giveGuardRecov) {
		this.giveGuardRecov = giveGuardRecov;
	}

	/**
	 * not in use!
	 * @return not in use! 
	 */
	public int getKnockBack() {
		return knockBack;
	}

	/**
	 * not in use!
	 * @param knockBack not in use! 
	 */
	public void setKnockBack(int knockBack) {
		this.knockBack = knockBack;
	}

	/**
	 * not in use!
	 * @return not in use! 
	 */
	public int getHitStop() {
		return hitStop;
	}

	/**
	 * not in use!
	 * @param hitStop not in use! 
	 */
	public void setHitStop(int hitStop) {
		this.hitStop = hitStop;
	}

	/**
	 * Returns the value of the attack's type:
	 * 1 = high 
	 * 2 = middle 
	 * 3 = low 
	 * 4 = throw
	 * @return the value of the attack's type
	 */
	public int getAttackType() {
		return attackType;
	}

	/**
	 * Sets the value of the attack's type:
	 * 1 = high 
	 * 2 = middle 
	 * 3 = low 
	 * 4 = throw
	 * @param attackType the value of the attack's type
	 */
	public void setAttackType(int attackType) {
		this.attackType = attackType;
	}

	/**
	 * Returns the flag whether this skill can push down the opponent when hit.
	 * @return the flag whether this skill can push down the opponent when hit
	 */
	public boolean isDownProperty() {
		return downProperty;
	}

	/**
	 * Sets the flag whether this skill can push down the opponent when hit.
	 * @param downProperty the flag whether this skill can push down the opponent when hit
	 */
	public void setDownProperty(boolean downProperty) {
		this.downProperty = downProperty;
	}

	/**
	 * Returns the absolute value of the horizontal speed of the attack hit box (zero means the attack hit box will track the character).
	 * @return the absolute value of the horizontal speed of the attack hit box (zero means the attack hit box will track the character)
	 */
	public int getSettingSpeedX() {
		return settingSpeedX;
	}

	/**
	 * Sets the absolute value of the horizontal speed of the attack hit box (zero means the attack hit box will track the character).
	 * @param settingSpeedX the absolute value of the horizontal speed of the attack hit box (zero means the attack hit box will track the character)
	 */
	public void setSettingSpeedX(int settingSpeedX) {
		this.settingSpeedX = settingSpeedX;
	}

	/**
	 * Returns the absolute value of the vertical speed of the attack hit box (zero means the attack hit box will track the character).
	 * @return the absolute value of the vertical speed of the attack hit box (zero means the attack hit box will track the character) 
	 */
	public int getSettingSpeedY() {
		return settingSpeedY;
	}

	/**
	 * Sets the absolute value of the vertical speed of the attack hit box (zero means the attack hit box will track the character).
	 * @param settingSpeedY the absolute value of the vertical speed of the attack hit box (zero means the attack hit box will track the character)
	 */
	public void setSettingSpeedY(int settingSpeedY) {
		this.settingSpeedY = settingSpeedY;
	}
}
