/**
 * AI built using MCTS AI model and code by author Taichi from ICE_Fighting
 *
 * Created by Jonathan on 4/8/2016.
 */

import com.sun.org.apache.bcel.internal.generic.NEW;
import enumerate.Action;
import enumerate.State;
import gameInterface.AIInterface;

import java.util.*;

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

    /* Action Ai as Teacher can take*/
    private LinkedList<Action> teacherActions;

    /* Indicator of times opponent has done an action and if they've mastered it */
    public enum ActionCount {
        ZERO, ONCE, TWICE, THRICE, FOURTH, FIFTH, SIXTH, SEVENTH;
    }

    public ActionCount updateCount( Map<Action, ActionCount> oppActHistory, Action oppAction ) {
        ActionCount oldCount = oppActHistory.get(oppAction);

        if (oldCount == ActionCount.ZERO)
            return ActionCount.ONCE;

        if (oldCount == ActionCount.ONCE)
            return ActionCount.TWICE;

        if (oldCount == ActionCount.TWICE)
            return ActionCount.THRICE;

        if (oldCount == ActionCount.THRICE)
            return ActionCount.FOURTH;

        if (oldCount == ActionCount.FOURTH)
            return ActionCount.FIFTH;

        if (oldCount == ActionCount.FIFTH)
            return ActionCount.SIXTH;

        if (oldCount == ActionCount.SIXTH)
            return ActionCount.SEVENTH;

        return oldCount;
    }


    /* History of Opponents understanding of different actions */
    private Map<Action, ActionCount> oppActHistory;


    private Vector<MotionData> myMotion;

    private Vector<MotionData> oppMotion;

    private Action[] actionAir;

    private Action[] actionGround;

    private Action[] basicAction;

    private Action[] noviceAction;

    private Action[] plannedAction;

    private Action[] complexAction;

    private Action[] expertAction;

    private LinkedList<Action> allowedActions;

    private Action spSkill;

    private Node rootNode;

    private KNNPlayerModel opponentModel;

    /* Debug mode activation boolean. If true, constructs output log. */
    public static final boolean DEBUG_MODE = true;

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

        opponentModel.getInformation(frameData);
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
        this.oppActHistory = new HashMap<>();
        this.allowedActions = new LinkedList<>();

        this.opponentModel = new KNNPlayerModel();
        opponentModel.initialize(gameData, playerNumber);

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

        /* Single Input and no energy needed */
        basicAction =
                new Action[] {Action.BACK_STEP, Action.FORWARD_WALK, Action.DASH, Action.JUMP, Action.FOR_JUMP, Action.BACK_JUMP, Action.STAND_GUARD, Action.CROUCH_GUARD, Action.AIR_GUARD, Action.AIR_A, Action.AIR_B, Action.STAND_A, Action.STAND_B, Action.CROUCH_A, Action.CROUCH_B};

        /* Two input and no energy needed */
        noviceAction =
                new Action[] {Action.AIR_FA, Action.AIR_FB, Action.AIR_UA, Action.AIR_UB, Action.STAND_FA, Action.STAND_FB, Action.CROUCH_FA, Action.CROUCH_FB};

        /* Requires energy but no more than two inputs */
        plannedAction =
                new Action[] {Action.THROW_A, Action.THROW_B, Action.AIR_DA, Action.AIR_DB};

        /* More than two inputs and no energy needed */
        complexAction =
                new Action[] {Action.STAND_D_DF_FA, Action.STAND_F_D_DFA, Action.STAND_D_DB_BA, Action.AIR_D_DF_FA};

        /* More than two inputs and energy needed */
        expertAction =
                new Action[] {Action.AIR_D_DF_FB, Action.AIR_F_D_DFA, Action.AIR_F_D_DFB, Action.AIR_D_DB_BA, Action.AIR_D_DB_BB, Action.STAND_D_DF_FB, Action.STAND_D_DF_FC, Action.STAND_F_D_DFB, Action.STAND_D_DB_BB};


        for (Action act1: actionGround) {
            oppActHistory.put(act1, ActionCount.ZERO);
        }

        for (Action act2 : actionGround) {
            oppActHistory.put(act2, ActionCount.ZERO);
        }

        oppActHistory.put(spSkill, ActionCount.ZERO);

        for (Action act: basicAction) {
            allowedActions.add(act);
        }

        for (Action act: plannedAction) {
            allowedActions.add(act);
        }

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

            Action oppAction = oppCharacter.getAction();
            oppActHistory.replace(oppAction, oppActHistory.get(oppAction), updateCount(oppActHistory, oppAction));

            for (Map.Entry<Action, ActionCount> entry : oppActHistory.entrySet())
            {
                if (Arrays.asList(basicAction).contains(entry.getKey())) {
                    if(entry.getValue() == ActionCount.SEVENTH && !allowedActions.contains(Action.AIR_FA)) {
                        for (Action act: noviceAction) {
                            allowedActions.add(act);
                        }
                    }
                }

                if (Arrays.asList(noviceAction).contains(entry.getKey())) {
                    if(entry.getValue() == ActionCount.SEVENTH && !allowedActions.contains(Action.STAND_F_D_DFA)) {
                        for (Action act: complexAction) {
                            allowedActions.add(act);
                        }
                    }
                }


                if (Arrays.asList(plannedAction).contains(entry.getKey())) {
                    if(entry.getValue() == ActionCount.SEVENTH && !allowedActions.contains(spSkill)) {
                        for (Action act: expertAction) {
                            allowedActions.add(act);
                        }
                }
                }

                if(DEBUG_MODE){
                    System.out.println(entry.getKey() + "/" + entry.getValue());
                }
            }

            if(DEBUG_MODE){
                for (Action act:allowedActions) {
                    System.out.println("Allowed Action: " + act);
                }
            }

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

                opponentModel.updateKNN();
                opponentModel.processing();

                /* Execute MCTS */
                Action bestAction = rootNode.mcts(opponentModel);
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
                if ( Math.abs( myMotion.elementAt( Action.valueOf( actionAir[i].name() ).ordinal() ).getAttackStartAddEnergy() ) <= energy  && allowedActions.contains(actionAir[i])) {
                    myActions.add( actionAir[i] );
                }
            }
        }

        else {
            if ( Math.abs( myMotion.elementAt( Action.valueOf( spSkill.name() ).ordinal() ).getAttackStartAddEnergy() ) <= energy && allowedActions.contains(spSkill)) {
                myActions.add( spSkill );
            }

            for ( int i = 0; i < actionGround.length; i++ ) {
                if ( Math.abs( myMotion.elementAt( Action.valueOf( actionGround[i].name() ).ordinal() ).getAttackStartAddEnergy() ) <= energy && allowedActions.contains(actionGround[i])) {
                    myActions.add( actionGround[i] );
                }
            }
        }

        if ( DEBUG_MODE ) {
            System.out.println( "Number of possible myActions: " + myActions.size() );

            int n = 0;

            for ( Action act : myActions) {
                System.out.println( "Action " + n + " :" + act );
                n++;
            }
        }
    }

    public void setOppAction() {
        oppActions.clear();

        int energy = oppCharacter.getEnergy();

        if ( oppCharacter.getState() == State.AIR ) {
            for ( int i =0; i < actionAir.length; i++ ) {
                if ( Math.abs( oppMotion.elementAt( Action.valueOf( actionAir[i].name() ).ordinal() ).getAttackStartAddEnergy() ) <= energy ) {
                    oppActions.add( actionAir[i] );
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

        if ( DEBUG_MODE ) {
            System.out.println( "Number of possible oppActions: " + oppActions.size() );

            int n = 0;

            for ( Action act : oppActions) {
                System.out.println( "Action " + n + " :" + act );
                n++;
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