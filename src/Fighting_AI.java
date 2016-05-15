/**
 * AI built using MCTS AI model and code by author Taichi from ICE_Fighting
 *
 * Created by Jonathan on 4/8/2016.
 */

import com.sun.org.apache.bcel.internal.generic.NEW;
import enumerate.Action;
import enumerate.State;
import gameInterface.AIInterface;

import java.util.LinkedList;
import java.util.Vector;

import simulator.Simulator;
import structs.CharacterData;
import structs.FrameData;
import structs.GameData;
import structs.Key;
import structs.MotionData;

import commandcenter.CommandCenter;

public class Fighting_AI implements AIInterface {

    private Simulator simulator;
    private Key key;
    private boolean playerNumber;
    private CommandCenter commandCenter;
    private GameData gameData;

    private FrameData frameData;

    /* Frame data delayed by FRAME_AHEAD */
    private FrameData simulatorAheadFrameData;

    /* Actions AI can perform */
    private LinkedList<Action> myActions;

    /* All actions the opponent can perform */
    private LinkedList<Action> oppActions;

    /* AI character information */
    private CharacterData myCharacter;

    /* Opponent character information */
    private CharacterData oppCharacter;

    /* Adjustment frame time delay */
    private static final int FRAME_AHEAD = 14;

    private Vector<MotionData> myMotion;

    private Vector<MotionData> oppMotion;

    private Action[] actionAir;

    private Action[] actionGround;

    private Action spSkill;

    private Node rootNode;

    /* Debug mode activation boolean. If true, constructs output log. */
    public static final boolean DEBUG_MODE = false;

    @Override
    public void close() { }

    @Override
    /* This method is for deciding which character to use among ZEN, GARNET, LUD, and KFM,
     and it returns one of the following values, which must be specified after "return"
      for the competition: CHARACTER_ZEN, CHARACTER_GARNET, CHARACTER_LUD, and CHARACTER_KFM */
    public String getCharacter() { return CHARACTER_ZEN; }

    @Override
    public void getInformation( FrameData frameData ) {
        this.frameData = frameData;
        this.commandCenter.setFrameData( this.frameData, playerNumber );

        if (playerNumber) {
            myCharacter = frameData.getP1();
            oppCharacter = frameData.getP2();
        }

        else {
            myCharacter = frameData.getP2();
            oppCharacter = frameData.getP1();
        }
    }



    @Override
    public int initialize( GameData gameData, boolean playerNumber ) {
        this.playerNumber = playerNumber;
        this.gameData = gameData;

        this.key = new Key();
        this.frameData = new FrameData();
        this.commandCenter = new CommandCenter();

        this.myActions = new LinkedList<Action>();
        this.oppActions = new LinkedList<Action>();

        simulator = gameData.getSimulator();

        /* create list of actions that can take place in the air */
        actionAir =
                new Action[] {Action.AIR_GUARD, Action.AIR_A, Action.AIR_B, Action.AIR_DA, Action.AIR_DB,
                        Action.AIR_FA, Action.AIR_FB, Action.AIR_UA, Action.AIR_UB, Action.AIR_D_DF_FA,
                        Action.AIR_D_DF_FB, Action.AIR_F_D_DFA, Action.AIR_F_D_DFB, Action.AIR_D_DB_BA,
                        Action.AIR_D_DB_BB};

        /* create list of actions that can take place on the ground */
        actionGround =
                new Action[] {Action.STAND_D_DB_BA, Action.BACK_STEP, Action.FORWARD_WALK, Action.DASH,
                        Action.JUMP, Action.FOR_JUMP, Action.BACK_JUMP, Action.STAND_GUARD,
                        Action.CROUCH_GUARD, Action.THROW_A, Action.THROW_B, Action.STAND_A, Action.STAND_B,
                        Action.CROUCH_A, Action.CROUCH_B, Action.STAND_FA, Action.STAND_FB, Action.CROUCH_FA,
                        Action.CROUCH_FB, Action.STAND_D_DF_FA, Action.STAND_D_DF_FB, Action.STAND_F_D_DFA,
                        Action.STAND_F_D_DFB, Action.STAND_D_DB_BB};

        /* Special Skill (costs most energy and deals most damage) */
        spSkill = Action.STAND_D_DF_FC;

        /* Conditional operator used for initialization. Is this.playerNumber = true ? do this : else this */
        myMotion = this.playerNumber ? gameData.getPlayerOneMotion() : gameData.getPlayerTwoMotion();
        oppMotion = this.playerNumber ? gameData.getPlayerTwoMotion() : gameData.getPlayerOneMotion();

        return 0;
    }

    @Override
    public Key input() {
        return key;
    }

    /* AI determines ability to act */
    public boolean canProcessing() { return !frameData.getEmptyFlag() && frameData.getRemainingTime() > 0; }

    @Override
    public void processing() {
        /* AI determines ability to act */
        if ( canProcessing() ) {

            /* Checks input's state of a command */
            if ( commandCenter.getskillFlag() ) {

                /* Gets the current Key data */
                key = commandCenter.getSkillKey();
            }

            else {
                /* This method resets all keys to false, or not pressed */
                key.empty();

                /* Empties skillData and sets skillFlag to false */
                commandCenter.skillCancel();

                /* Perform in preparation of MCTS*/
                mctsPrepare();
                rootNode =
                        new Node( simulatorAheadFrameData, null, myActions, oppActions, gameData, playerNumber, commandCenter );
                rootNode.createNode();

                /* Execute MCTS */
                Action bestAction = rootNode.mcts();
                if ( Fighting_AI.DEBUG_MODE ) {
                    rootNode.printNode(rootNode);
                }

                /* Perform selected action chosen by MCTS */
                commandCenter.commandCall(bestAction.name());
            }
        }
    }

    public void setMyAction() {
        myActions.clear();

        int energy = myCharacter.getEnergy();

        if ( myCharacter.getState() == State.AIR ) {
            for ( int i =0; i < actionAir.length; i++ ) {
                if ( Math.abs( myMotion.elementAt( Action.valueOf( actionAir[i].name() ).ordinal() ).getAttackStartAddEnergy() ) <= energy ) {
                    myActions.add( actionAir[i] );
                }
            }
        }

        else {
            if ( Math.abs( myMotion.elementAt( Action.valueOf( spSkill.name() ).ordinal() ).getAttackStartAddEnergy() ) <= energy ) {
                myActions.add( spSkill );
            }

            for ( int i = 0; i < actionGround.length; i++ ) {
                if ( Math.abs( myMotion.elementAt( Action.valueOf( actionGround[i].name() ).ordinal() ).getAttackStartAddEnergy() ) <= energy ) {
                    myActions.add( actionGround[i] );
                }
            }
        }
    }

    public void setOppAction() {
        oppActions.clear();

        int energy = oppCharacter.getEnergy();

        if ( oppCharacter.getState() == State.AIR ) {
            for ( int i =0; i < actionAir.length; i++ ) {
                if ( Math.abs( oppMotion.elementAt( Action.valueOf( actionAir[i].name() ).ordinal() ).getAttackStartAddEnergy() ) <= energy ) {
                    myActions.add( actionAir[i] );
                }
            }
        }

        else {
            if ( Math.abs( oppMotion.elementAt( Action.valueOf( spSkill.name() ).ordinal() ).getAttackStartAddEnergy() ) <= energy ) {
                oppActions.add( spSkill );
            }

            for ( int i = 0; i < actionGround.length; i++ ) {
                if ( Math.abs( oppMotion.elementAt( Action.valueOf( actionGround[i].name() ).ordinal() ).getAttackStartAddEnergy() ) <= energy ) {
                    oppActions.add( actionGround[i] );
                }
            }
        }
    }

    /* Get advance frame data to prepare MCTS*/
    public  void mctsPrepare() {
        simulatorAheadFrameData = simulator.simulate( frameData, playerNumber, null, null, FRAME_AHEAD );

        myCharacter = playerNumber ? simulatorAheadFrameData.getP1() : simulatorAheadFrameData.getP2();
        oppCharacter = playerNumber ? simulatorAheadFrameData.getP2() : simulatorAheadFrameData.getP1();

        setMyAction();
        setOppAction();
    }
}