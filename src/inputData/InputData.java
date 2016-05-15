package inputData;

import java.util.EnumSet;

import enumerate.SubAction;
import structs.Key;

public class InputData {

	final static int INPUT_MAX = 3;
	
	// Key data
	private Key[][] data;
	// A number of the data of Key that is not a dummy.
	private int[] number;
	
	public InputData(){
		// get a number of SubAction from Enumerate.SubAction
		int actionSize = EnumSet.allOf(SubAction.class).size();

		data = new Key[actionSize][INPUT_MAX];
		number = new int[actionSize];
		
		for(int i = 0; i < actionSize ; i++){
			for(int j = 0 ; j < INPUT_MAX ; j++){
				data[i][j] = new Key();
			}
		}
		
		// NEUTRAL:dummy
		number[SubAction.NEUTRAL.ordinal()] = 0;

		// FORWARDWALK
		data[SubAction.FORWARD_WALK.ordinal()][0].R = true;
		number[SubAction.FORWARD_WALK.ordinal()] = 1;
		// DASH
		data[SubAction.DASH.ordinal()][0].R = true;
		data[SubAction.DASH.ordinal()][2].R = true;
		number[SubAction.DASH.ordinal()] = 3;
		// BACK_STEP
		data[SubAction.BACK_STEP.ordinal()][0].L = true;
		data[SubAction.BACK_STEP.ordinal()][2].L = true;
		number[SubAction.BACK_STEP.ordinal()] = 3;
		// CROUCH
		data[SubAction.CROUCH.ordinal()][0].D = true;
		number[SubAction.CROUCH.ordinal()] = 1;
		// JUMP
		data[SubAction.JUMP.ordinal()][0].U = true;
		number[SubAction.JUMP.ordinal()] = 1;
		// FOR_JUMP
		data[SubAction.FOR_JUMP.ordinal()][0].U = true;
		data[SubAction.FOR_JUMP.ordinal()][0].R = true;
		number[SubAction.FOR_JUMP.ordinal()] = 1;
		// BACK_JUMP
		data[SubAction.BACK_JUMP.ordinal()][0].U = true;
		data[SubAction.BACK_JUMP.ordinal()][0].L = true;
		number[SubAction.BACK_JUMP.ordinal()] = 1;

		// STAND_GUARD
		data[SubAction.STAND_GUARD.ordinal()][0].L = true;
		number[SubAction.STAND_GUARD.ordinal()] = 1;
		// CROUCH_GUARD
		data[SubAction.CROUCH_GUARD.ordinal()][0].L = true;
		data[SubAction.CROUCH_GUARD.ordinal()][0].D = true;
		number[SubAction.CROUCH_GUARD.ordinal()] = 1;
		// AIR_GUARD
		data[SubAction.AIR_GUARD.ordinal()][0].L = true;
		number[SubAction.AIR_GUARD.ordinal()] = 1;

		// THROW_A
		data[SubAction.THROW_A.ordinal()][0].A = true;
		data[SubAction.THROW_A.ordinal()][0].L = true;
		number[SubAction.THROW_A.ordinal()] = 1;
		// THROW_B
		data[SubAction.THROW_B.ordinal()][0].B = true;
		data[SubAction.THROW_B.ordinal()][0].L = true;
		number[SubAction.THROW_B.ordinal()] = 1;

		// STAND_A
		data[SubAction.STAND_A.ordinal()][0].A = true;
		number[SubAction.STAND_A.ordinal()] = 1;
		// STAND_B	
		data[SubAction.STAND_B.ordinal()][0].B = true;
		number[SubAction.STAND_B.ordinal()] = 1;
		// CROUCH_A
		data[SubAction.CROUCH_A.ordinal()][0].D = true;
		data[SubAction.CROUCH_A.ordinal()][0].A = true;
		number[SubAction.CROUCH_A.ordinal()] = 1;
		// CROUCH_B
		data[SubAction.CROUCH_B.ordinal()][0].D = true;
		data[SubAction.CROUCH_B.ordinal()][0].B = true;
		number[SubAction.CROUCH_B.ordinal()] = 1;
		// AIR_A
		data[SubAction.AIR_A.ordinal()][0].A = true;
		number[SubAction.AIR_A.ordinal()] = 1;
		// AIR_B
		data[SubAction.AIR_B.ordinal()][0].B = true;
		number[SubAction.AIR_B.ordinal()] = 1;

		// AIR_DA
		data[SubAction.AIR_DA.ordinal()][0].D = true;
		data[SubAction.AIR_DA.ordinal()][0].A = true;
		number[SubAction.AIR_DA.ordinal()] = 1;
		// AIR_DB
		data[SubAction.AIR_DB.ordinal()][0].D = true;
		data[SubAction.AIR_DB.ordinal()][0].B = true;
		number[SubAction.AIR_DB.ordinal()] = 1;
		// STAND_FA
		data[SubAction.STAND_FA.ordinal()][0].R = true;
		data[SubAction.STAND_FA.ordinal()][0].A = true;
		number[SubAction.STAND_FA.ordinal()] = 1;
		// STAND_FB
		data[SubAction.STAND_FB.ordinal()][0].R = true;
		data[SubAction.STAND_FB.ordinal()][0].B = true;
		number[SubAction.STAND_FB.ordinal()] = 1;
		// CROUCH_FA
		data[SubAction.CROUCH_FA.ordinal()][0].R = true;
		data[SubAction.CROUCH_FA.ordinal()][0].D = true;
		data[SubAction.CROUCH_FA.ordinal()][0].A = true;
		number[SubAction.CROUCH_FA.ordinal()] = 1;
		// CROUCH_FB
		data[SubAction.CROUCH_FB.ordinal()][0].R = true;
		data[SubAction.CROUCH_FB.ordinal()][0].D = true;
		data[SubAction.CROUCH_FB.ordinal()][0].B = true;
		number[SubAction.CROUCH_FB.ordinal()] = 1;
		// AIR_FA
		data[SubAction.AIR_FA.ordinal()][0].R = true;
		data[SubAction.AIR_FA.ordinal()][0].A = true;
		number[SubAction.AIR_FA.ordinal()] = 1;
		// AIR_FB
		data[SubAction.AIR_FB.ordinal()][0].R = true;
		data[SubAction.AIR_FB.ordinal()][0].B = true;
		number[SubAction.AIR_FB.ordinal()] = 1;
		// AIR_UA
		data[SubAction.AIR_UA.ordinal()][0].U = true;
		data[SubAction.AIR_UA.ordinal()][0].A = true;
		number[SubAction.AIR_UA.ordinal()] = 1;
		// AIR_UB
		data[SubAction.AIR_UB.ordinal()][0].U = true;
		data[SubAction.AIR_UB.ordinal()][0].B = true;
		number[SubAction.AIR_UB.ordinal()] = 1;
		
		// STAND_D_DF_FA
		data[SubAction.STAND_D_DF_FA.ordinal()][0].D = true;
		data[SubAction.STAND_D_DF_FA.ordinal()][1].D = true;
		data[SubAction.STAND_D_DF_FA.ordinal()][1].R = true;
		data[SubAction.STAND_D_DF_FA.ordinal()][2].R = true;
		data[SubAction.STAND_D_DF_FA.ordinal()][2].A = true;
		number[SubAction.STAND_D_DF_FA.ordinal()] = 3;
		
		// STAND_D_DF_FB
		data[SubAction.STAND_D_DF_FB.ordinal()][0].D = true;
		data[SubAction.STAND_D_DF_FB.ordinal()][1].D = true;
		data[SubAction.STAND_D_DF_FB.ordinal()][1].R = true;
		data[SubAction.STAND_D_DF_FB.ordinal()][2].R = true;
		data[SubAction.STAND_D_DF_FB.ordinal()][2].B = true;
		number[SubAction.STAND_D_DF_FB.ordinal()] = 3;
		
		// STAND_F_D_DFA
		data[SubAction.STAND_F_D_DFA.ordinal()][0].R = true;
		data[SubAction.STAND_F_D_DFA.ordinal()][1].D = true;
		data[SubAction.STAND_F_D_DFA.ordinal()][2].R = true;
		data[SubAction.STAND_F_D_DFA.ordinal()][2].D = true;
		data[SubAction.STAND_F_D_DFA.ordinal()][2].A = true;
		number[SubAction.STAND_F_D_DFA.ordinal()] = 3;

		// STAND_F_D_DFB
		data[SubAction.STAND_F_D_DFB.ordinal()][0].R = true;
		data[SubAction.STAND_F_D_DFB.ordinal()][1].D = true;
		data[SubAction.STAND_F_D_DFB.ordinal()][2].R = true;
		data[SubAction.STAND_F_D_DFB.ordinal()][2].D = true;
		data[SubAction.STAND_F_D_DFB.ordinal()][2].B = true;
		number[SubAction.STAND_F_D_DFB.ordinal()] = 3;
		
		// STAND_D_DB_BA
		data[SubAction.STAND_D_DB_BA.ordinal()][0].D = true;
		data[SubAction.STAND_D_DB_BA.ordinal()][1].D = true;
		data[SubAction.STAND_D_DB_BA.ordinal()][1].L = true;
		data[SubAction.STAND_D_DB_BA.ordinal()][2].L = true;
		data[SubAction.STAND_D_DB_BA.ordinal()][2].A = true;
		number[SubAction.STAND_D_DB_BA.ordinal()] = 3;
		
		// STAND_D_DB_BB
		data[SubAction.STAND_D_DB_BB.ordinal()][0].D = true;
		data[SubAction.STAND_D_DB_BB.ordinal()][1].D = true;
		data[SubAction.STAND_D_DB_BB.ordinal()][1].L = true;
		data[SubAction.STAND_D_DB_BB.ordinal()][2].L = true;
		data[SubAction.STAND_D_DB_BB.ordinal()][2].B = true;
		number[SubAction.STAND_D_DB_BB.ordinal()] = 3;

		// AIR_D_DF_FA
		data[SubAction.AIR_D_DF_FA.ordinal()][0].D = true;
		data[SubAction.AIR_D_DF_FA.ordinal()][1].D = true;
		data[SubAction.AIR_D_DF_FA.ordinal()][1].R = true;
		data[SubAction.AIR_D_DF_FA.ordinal()][2].R = true;
		data[SubAction.AIR_D_DF_FA.ordinal()][2].A = true;
		number[SubAction.AIR_D_DF_FA.ordinal()] = 3;
		
		// AIR_D_DF_FB
		data[SubAction.AIR_D_DF_FB.ordinal()][0].D = true;
		data[SubAction.AIR_D_DF_FB.ordinal()][1].D = true;
		data[SubAction.AIR_D_DF_FB.ordinal()][1].R = true;
		data[SubAction.AIR_D_DF_FB.ordinal()][2].R = true;
		data[SubAction.AIR_D_DF_FB.ordinal()][2].B = true;
		number[SubAction.AIR_D_DF_FB.ordinal()] = 3;

		// AIR_F_D_DFA
		data[SubAction.AIR_F_D_DFA.ordinal()][0].R = true;
		data[SubAction.AIR_F_D_DFA.ordinal()][1].D = true;
		data[SubAction.AIR_F_D_DFA.ordinal()][2].R = true;
		data[SubAction.AIR_F_D_DFA.ordinal()][2].D = true;
		data[SubAction.AIR_F_D_DFA.ordinal()][2].A = true;
		number[SubAction.AIR_F_D_DFA.ordinal()] = 3;

		// AIR_F_D_DFB
		data[SubAction.AIR_F_D_DFB.ordinal()][0].R = true;
		data[SubAction.AIR_F_D_DFB.ordinal()][1].D = true;
		data[SubAction.AIR_F_D_DFB.ordinal()][2].R = true;
		data[SubAction.AIR_F_D_DFB.ordinal()][2].D = true;
		data[SubAction.AIR_F_D_DFB.ordinal()][2].B = true;
		number[SubAction.AIR_F_D_DFB.ordinal()] = 3;

		// AIR_D_DB_BA
		data[SubAction.AIR_D_DB_BA.ordinal()][0].D = true;
		data[SubAction.AIR_D_DB_BA.ordinal()][1].D = true;
		data[SubAction.AIR_D_DB_BA.ordinal()][1].L = true;
		data[SubAction.AIR_D_DB_BA.ordinal()][2].L = true;
		data[SubAction.AIR_D_DB_BA.ordinal()][2].A = true;
		number[SubAction.AIR_D_DB_BA.ordinal()] = 3;

		// AIR_D_DB_BB
		data[SubAction.AIR_D_DB_BB.ordinal()][0].D = true;
		data[SubAction.AIR_D_DB_BB.ordinal()][1].D = true;
		data[SubAction.AIR_D_DB_BB.ordinal()][1].L = true;
		data[SubAction.AIR_D_DB_BB.ordinal()][2].L = true;
		data[SubAction.AIR_D_DB_BB.ordinal()][2].B = true;
		number[SubAction.AIR_D_DB_BB.ordinal()] = 3;

		// STAND_D_DF_FC
		data[SubAction.STAND_D_DF_FC.ordinal()][0].D = true;
		data[SubAction.STAND_D_DF_FC.ordinal()][1].D = true;
		data[SubAction.STAND_D_DF_FC.ordinal()][1].R = true;
		data[SubAction.STAND_D_DF_FC.ordinal()][2].R = true;
		data[SubAction.STAND_D_DF_FC.ordinal()][2].C = true;
		number[SubAction.STAND_D_DF_FC.ordinal()] = 3;
		
	}
	
	public Key getKey(SubAction action,int number){
		try{
			return data[action.ordinal()][number];
		}catch(ArrayIndexOutOfBoundsException e){
			//System.out.println(e +  " caused.");
			
			Key error = new Key();
			return error;
			
		}
	}
	
	public int getNumber(SubAction action){
		return number[action.ordinal()];
	}
}
