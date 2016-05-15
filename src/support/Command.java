package support;

import java.util.Vector;

import structs.FrameData;
import structs.CharacterData;
import structs.Key;
/**
 * This class is used for a calling command by AI.<br> 
 *
 */
public class Command {
	/**
	 * Sign showing whether a skill is being input.<br>
	 */	
	private boolean skillFlag;
	/**
	 * The index of vcKey.<br>
	 */
	private int skillIndex;
	/**
	 * vcKey's size<br>
	 */
	private int skillEndIndex;
	/**
	 * A temporary array containing the input sequence<br>
	 */
	private String skillData[];
	/**
	 * The frame data sent from Game. Note that this information is 15 frames delayed.<br>
	 */
	private FrameData frameData;
	/**
	 * player's number<br>
	 */
	private boolean playerNumber;
	/**
	 * An array of a Key sequence corresponding to the input sequence<br>
	 */
	private Vector<Key> vcKey;
	
	public Command(){
		this.frameData = new FrameData();
		this.skillFlag = false;
		this.skillIndex = 0;
		this.skillEndIndex = 0;
		this.skillData = "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0".split(" ");
		vcKey = new Vector<Key>();
	}
	

	/**
	 * Checks the argument of commandCall is an action's name.
	 */
	private void checkMotionString(){
		String str1 = new String("");
		for(int i=0; i<skillData.length; i++){
			str1 = skillData[i];
			if(str1.equals("0")||str1.equals("1")||str1.equals("2")||str1.equals("3")||str1.equals("4")||str1.equals("5")||str1.equals("6")||str1.equals("7")||str1.equals("8")||str1.equals("9")
					||str1.equals("A")||str1.equals("B")||str1.equals("C")){continue;}
			else{
				changeStringToCommand(str1, i);
			}
		}
	}
	
	/**
	 * Changes the action name to the corresponding command's input sequence.
	 * 
	 * @param command command is Motion`s name like as THROW_A or DASH.
	 * @param index arrays index of skillData.
	 */
	private void changeStringToCommand(String command, int index){
			switch(command){
			case	"NEUTRAL"	:
				break;
			case	"STAND"	:
				break;
			case	"FORWARD_WALK"	:
				recommandCall("6",index);
				break;
			case	"DASH"	:
				recommandCall("6 5 6",index);
				break;
			case	"BACK_STEP"	:
				recommandCall("4 5 4",index);
				break;
			case	"CROUCH"	:
				recommandCall("2",index);
				break;
			case	"JUMP"	:
				recommandCall("8",index);
				break;
			case	"FOR_JUMP"	:
				recommandCall("9",index);
				break;
			case	"BACK_JUMP"	:
				recommandCall("7",index);
				break;
			case	"AIR"	:
				break;
			case	"STAND_GUARD"	:
				recommandCall("4",index);
				break;
			case	"CROUCH_GUARD"	:
				recommandCall("1",index);
				break;
			case	"AIR_GUARD"	:
				recommandCall("7",index);
				break;
			case	"STAND_GUARD_RECOV"	:
				break;
			case	"CROUCH_GUARD_RECOV"	:
				break;
			case	"AIR_GUARD_RECOV"	:
				break;
			case	"STAND_HIT"	:
				break;
			case	"CROUCH_HIT"	:
				break;
			case	"AIR_HIT"	:
				break;
			case	"CHANGE_DOWN"	:
				break;
			case	"DOWN"	:
				break;
			case	"RISE"	:
				break;
			case	"LANDING"	:
				break;
			case	"THROW_A"	:
				recommandCall("4 _ A",index);
				break;
			case	"THROW_B"	:
				recommandCall("4 _ B",index);
				break;
			case	"THROW_HIT"	:
				break;
			case	"THROW_SUFFER"	:
				break;
			case	"STAND_A"	:
				recommandCall("A",index);
				break;
			case	"STAND_B"	:
				recommandCall("B",index);
				break;
			case	"CROUCH_A"	:
				recommandCall("2 _ A",index);
				break;
			case	"CROUCH_B"	:
				recommandCall("2 _ B",index);
				break;
			case	"AIR_A"	:
				recommandCall("8 _ A",index);
				break;
			case	"AIR_B"	:
				recommandCall("8 _ B",index);
				break;
			case	"AIR_DA"	:
				recommandCall("8 5 2 _ A",index);
				break;
			case	"AIR_DB"	:
				recommandCall("8 5 2 _ B",index);
				break;
			case	"STAND_FA"	:
				recommandCall("6 _ A",index);
				break;
			case	"STAND_FB"	:
				recommandCall("6 _ B",index);
				break;
			case	"CROUCH_FA"	:
				recommandCall("3 _ A",index);
				break;
			case	"CROUCH_FB"	:
				recommandCall("3 _ B",index);
				break;
			case	"AIR_FA"	:
				recommandCall("9 _ A",index);
				break;
			case	"AIR_FB"	:
				recommandCall("9 _ B",index);
				break;
			case	"AIR_UA"	:
				recommandCall("8 5 8 _ A",index);
				break;
			case	"AIR_UB"	:
				recommandCall("8 5 8 _ B",index);
				break;
			case	"STAND_D_DF_FA"	:
				recommandCall("2 3 6 _ A",index);
				break;
			case	"STAND_D_DF_FB"	:
				recommandCall("2 3 6 _ B",index);
				break;
			case	"STAND_F_D_DFA"	:
				recommandCall("6 2 3 _ A",index);
				break;
			case	"STAND_F_D_DFB"	:
				recommandCall("6 2 3 _ B",index);
				break;
			case	"STAND_D_DB_BA"	:
				recommandCall("2 1 4 _ A",index);
				break;
			case	"STAND_D_DB_BB"	:
				recommandCall("2 1 4 _ B",index);
				break;
			case	"AIR_D_DF_FA"	:
				recommandCall("8 2 3 6 _ A",index);
				break;
			case	"AIR_D_DF_FB"	:
				recommandCall("8 5 2 3 6 _ B",index);
				break;
			case	"AIR_F_D_DFA"	:
				recommandCall("8 5 6 2 3 _ A",index);
				break;
			case	"AIR_F_D_DFB"	:
				recommandCall("8 5 6 2 3 _ B",index);
				break;
			case	"AIR_D_DB_BA"	:
				recommandCall("8 5 2 1 4 _ A",index);
				break;
			case	"AIR_D_DB_BB"	:
				recommandCall("8 5 2 1 4 _ B",index);
				break;
			case	"STAND_D_DF_FC"	:
				recommandCall("2 3 6 _ C",index);
				break;
			}
		
	}
	
	/**
	 * Stores the input sequence into SkillData.
	 * 
	 * @param command command is Motion`s name like as THROW_A or DASH.
	 * @param index arrays index of skillData.
	 */
	private void recommandCall(String command, int index){
		String[] cmdbuf = command.split(" ");
		for(int i=0; i<cmdbuf.length;i++,index++){

			skillData[index] = new String(cmdbuf[i]);
		}		
	}
	
	/**
	 * Checks if the command has to be reversed.
	 * @return
	 */
	private boolean localization(){
		int reverseCount = 0;
		if(this.playerNumber == false) reverseCount++;
		if(!frameData.getP1().isFront()) reverseCount++;			
		if(reverseCount%2 == 0)return false;
		return true;
	}

	/**
	 * Reverses SkillData's elements' left or right directions.
	 */
	private void reverseKey(){
		int index = 0;
		while(skillData[index].equals("0")==false) index++;
		for(int i = 0; i<index; i++){
			if(skillData[i].equals("L") || skillData[i].equals("4")){
				skillData[i] = "6";
				continue;
			}
			if(skillData[i].equals("R") || skillData[i].equals("6")){
				skillData[i] = "4";
				continue;
				}
			if(skillData[i].equals("LD") || skillData[i].equals("1")){
				skillData[i] = "3";
				continue;
			}
			if(skillData[i].equals("LU") || skillData[i].equals("7")){
				skillData[i] = "9";
				continue;
			}
			if(skillData[i].equals("RD") || skillData[i].equals("3")){
				skillData[i] = "1";
				continue;
			}
			if(skillData[i].equals("RU") || skillData[i].equals("9")){
				skillData[i] = "7";
				continue;
			}
			if(skillData[i].equals("_")){
				skillData[i] = "_";
				continue;
			}
				
		}
		
	}
	

	/**
	 * Checks if the command input is done.
	 */
	private void skillEndCheck(){
		if(skillIndex == skillEndIndex) {
			skillFlag = false;
			skillIndex = 0;
			skillEndIndex = 0;
			this.skillData = "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0".split(" ");
			vcKey.clear();
		}
	}
	

	/**
	 * Uses SkillData to create vcKey.
	 */
	private void createKeyData(){
		Key buf;
		int index = 0;
		while(skillData[index].equals("0")==false){
			buf = new Key();
			buf.empty();
			if(skillData[index].equals("_")){
				buf = vcKey.get(index-1);
				vcKey.remove(index-1);
				index++;
			}
			if(skillData[index].equals("5")){
				buf.empty();
			}
			if(skillData[index].equals("L") || skillData[index].equals("4")){
				buf.L=true;
			}
			if(skillData[index].equals("R") || skillData[index].equals("6")){
				buf.R=true;
			}
			if(skillData[index].equals("D") || skillData[index].equals("2")){
				buf.D=true;
			}
			if(skillData[index].equals("U") || skillData[index].equals("8")){
				buf.U=true;
			}
			if(skillData[index].equals("LD") || skillData[index].equals("1")){
				buf.L=true;
				buf.D=true;
			}
			if(skillData[index].equals("LU") || skillData[index].equals("7")){
				buf.L=true;
				buf.U=true;
			}
			if(skillData[index].equals("RD") || skillData[index].equals("3")){
				buf.R=true;
				buf.D=true;
			}
			if(skillData[index].equals("RU") || skillData[index].equals("9")){
				buf.R=true;
				buf.U=true;
			}
			if(skillData[index].equals("A")){
				buf.A=true;
			}
			if(skillData[index].equals("B")){
				buf.B=true;
			}
			if(skillData[index].equals("C")){
				buf.C=true;
			}
			this.vcKey.addElement(buf);
			index++;
		}
		skillEndIndex = vcKey.size();
	}	
	

	/**
	 * If a command is being input, then returns true, otherwise false.
	 */
	public boolean getskillFlag(){
		skillEndCheck();
		return this.skillFlag;
	}
	
	/**
	 * Sets the frame data.
	 * 
	 * @param frameData frame data in this frame
	 * @param playerNumber playerNumber of self
	 */
	public void setFrameData(FrameData frameData, boolean playerNumber){
		this.frameData = frameData;
		this.playerNumber = playerNumber;
	}
	
	/**
	 * Uses "command" to create SkillData, an array containing the input sequence.
	 * 
	 * @param String the direction number or motion name
	 */
	public void commandCall(String command){
		if(skillFlag == false){
			String buf[] = command.split(" ");
			for(int i=0; i<buf.length; i++){
				skillData[i] = buf[i];
			}
			skillFlag = true;
			checkMotionString();
			if(localization()) reverseKey();
			createKeyData();
			
		}
	}	
	
	/**
	 * Empties skillData and sets skillFlag to false.
	 */
	public void skillCancel(){
		skillFlag = false;
		skillIndex = 0;
		skillEndIndex = 0;
		skillData = "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0".split(" ");
		vcKey.clear();
		
	}
	
	/**
	 * Gets the current Key data.
	 */
	public Key getSkillKey(){
		Key buf = new Key();
		skillEndCheck();
		if(this.skillFlag) return vcKey.get(skillIndex++);
		else return buf;
	}
	
	/**
	 * Gets the hit point of self.
	 */
	public int getMyHP(){
		if(this.playerNumber == true)return this.frameData.getP1().getHp();
		return this.frameData.getP2().getHp();
	}
	/**
	 * Gets the energy value of self.
	 */
	public int getMyEnergy(){
		if(this.playerNumber == true)return this.frameData.getP1().getEnergy();
		return this.frameData.getP2().getEnergy();
	}
	/**
	 * Gets the hit point of the opponent.
	 */
	public int getEnemyHP(){
		if(this.playerNumber == true)return this.frameData.getP2().getHp();
		return this.frameData.getP1().getHp();
	}
	/**
	 * Gets the energy value of the opponent.
	 */
	public int getEnemyEnergy(){
		if(this.playerNumber == true)return this.frameData.getP2().getEnergy();
		return this.frameData.getP1().getEnergy();
	}
	/**
	 * Gets self's x-coordinate.
	 */
	public int getMyX(){
		if(this.playerNumber==true)return this.frameData.getP1().getX();
		return this.frameData.getP2().getX();
	}
	/**
	 * Gets self's y-coordinate.
	 */
	public int getMyY(){
		if(this.playerNumber==true)return this.frameData.getP1().getY();
		return this.frameData.getP2().getY();
	}
	/**
	 * Gets the opponent's x-coordinate.
	 */
	public int getEnemyX(){
		if(this.playerNumber==true)return this.frameData.getP2().getX();
		return this.frameData.getP1().getX();
	}
	/**
	 * Gets the opponent's y-coordinate.
	 */
	public int getEnemyY(){
		if(this.playerNumber==true)return this.frameData.getP2().getY();
		return this.frameData.getP1().getY();
	}
	/**
	 * Gets the horizontal distance between the two characters.
	 */
	public int getDistanceX(){
		return Math.abs(getMyX()-getEnemyX());
	}
	/**
	 * Gets the vertical distance between the two characters.
	 */
	public int getDistanceY(){
		return Math.abs(getMyY()-getEnemyY());
	}
	/**
	 * Gets the character data of self.
	 */
	public CharacterData getMyCharacter(){
		if(this.playerNumber == true)return this.frameData.getP1();
		else return this.frameData.getP2();
	}
	/**
	 * Gets the character data of the opponent.
	 */
	public CharacterData getEnemyCharacter(){
		if(this.playerNumber == true)return this.frameData.getP2();
		else return this.frameData.getP1();
	}
	
}
