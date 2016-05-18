/**
 * AI built using MCTS AI model and code by author Taichi from ICE_Fighting
 *
 * Created by Jonathan on 5/3/2016.
 */

import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;

import simulator.Simulator;
import structs.CharacterData;
import structs.FrameData;
import structs.GameData;

import commandcenter.CommandCenter;

import enumerate.Action;

public class Node {

    /* Execution time of Upper Confidence Bound 1 applied to the tree (UCT) */
    public static final int UCT_TIME = 165 * 100000;

    /* Upper Confidence Bound 1 Value for constant C */
    public static final double UCB_C = 3;

    /*Depth of tree to search  */
    public static final int UCT_TREE_DEPTH = 2;

    /* Threshold to generate a node */
    public static final int UCT_CREATE_NODE_THRESHOLD = 10;

    /* Time to perform simulation */
    public static final int SIMULATION_TIME = 60;

    /* Use when using the random number */
    private Random rnd;

    /* Parent Node */
    private Node parent;

    /* Child Node */
    private Node[] children;

    /* Depth of Node */
    private int depth;

    /* Times a Node has been searched */
    private int games;

    /* Upper Confidence Bound Value */
    private double ucb;

    /* Evaluation Value */
    private double score;

    /* Actions AI can perform */
    private LinkedList<Action> myActions;

    /* All actions the opponent can perform */
    private LinkedList<Action> oppActions;

    /* Used for simulations */
    private Simulator simulator;

    /* Action(s) selected */
    private LinkedList<Action> selectedMyActions;

    /* Hp prior to simulation */
    private int myOriginalHp;
    private int oppOriginalHp;

    private FrameData frameData;
    private boolean playerNumber;
    private CommandCenter commandCenter;
    private GameData gameData;

    private boolean isCreateNode;

    Deque<Action> mAction;
    Deque<Action> oppAction;

    public Node( FrameData frameData, Node parent, LinkedList<Action> myActions, LinkedList<Action> oppActions, GameData gameData, boolean playerNumber, CommandCenter commandCenter) {
        this.frameData = frameData;
        this.parent = parent;
        this.myActions = myActions;
        this.oppActions = oppActions;
        this.gameData = gameData;
        this.simulator = new Simulator(gameData);
        this.playerNumber = playerNumber;
        this.commandCenter = commandCenter;

        this.selectedMyActions = new LinkedList<Action>();

        this.rnd = new Random();
        this.mAction = new LinkedList<Action>();
        this.oppAction = new LinkedList<Action>();

        CharacterData myCharacter = playerNumber ? frameData.getP1() : frameData.getP2();
        CharacterData oppCharacter = playerNumber ? frameData.getP2() : frameData.getP1();
        myOriginalHp = myCharacter.getHp();
        oppOriginalHp = oppCharacter.getHp();

        if (this.parent != null) {
            this.depth = this.parent.depth + 1;
        }

        else {
            this.depth = 0;
        }
    }

    public Node( FrameData frameData, Node parent, LinkedList<Action> myActions, LinkedList<Action> oppActions, GameData gameData, boolean playerNumber, CommandCenter commandCenter, LinkedList<Action> selectedMyActions ) {
        this( frameData, parent, myActions, oppActions, gameData, playerNumber, commandCenter );

        this.selectedMyActions = selectedMyActions;
    }

    /* Execute MCTS within UCT time frame */
    public Action mcts(KNNPlayerModel opponentModel) {
        long start = System.nanoTime();
        for ( ; System.nanoTime() - start <= UCT_TIME; ) {
            uct(opponentModel);
        }

        return getBestVisitAction();
    }

    /* Run simulation and return evaluation value as results */
    public double playout(KNNPlayerModel opponentModel) {

        mAction.clear();
        oppAction.clear();

        for ( int i = 0; i < selectedMyActions.size(); i++ ) {
            mAction.add( selectedMyActions.get(i) );
        }

        for ( int i = 0; i < 5 - selectedMyActions.size(); i++ ) {
            mAction.add( myActions.get( rnd.nextInt( myActions.size() ) ) );
        }

        // ACE: this looks like where i'd put the opponent prediction for MCTS roll-out... but where for probability?

        for (int i = 0; i < 5; i++)
        {
            float val = rnd.nextFloat();
            for (ActionProbability apr : opponentModel.getOppProbabilities())
            {
                val -= apr.probability;
                if (val <= 0)
                {
                    oppAction.add(apr.action);
                    if(Fighting_AI.DEBUG_MODE){
                        System.out.println("Probable Opponent Action: " + apr.action + ", Probability: " + apr.probability);
                    }
                    break;
                }
            }
        }
        FrameData nFrameData = simulator.simulate(frameData, playerNumber, mAction, oppAction, SIMULATION_TIME);

        return getScore( nFrameData );
    }

    /* Perform UCT and return evaluation value */
    public double uct(KNNPlayerModel opponentModel) {

        Node selectedNode = null;
        double bestUcb;

        bestUcb = -99999;

        for ( Node child : this.children ) {
            if ( child.games == 0 ) {
                child.ucb = 9999 + rnd.nextInt(50);
            }

            else {
                // ACE: implement probability of action happening into UCB... somehow
                child.ucb = getUcb( child.score / child.games, games, child.games );
            }

            if ( bestUcb < child.ucb ) {
                selectedNode = child;
                bestUcb = child.ucb;
            }
        }

        // ACE: need two separate measurements, one for probability which determines what to explore, and one UCB
        // atm both are the same

        double score = 0;

        if ( selectedNode.games == 0 ) {
            score = selectedNode.playout(opponentModel);
        }

        // ACE: okay, the actual problem is that there is no "2 player" implementation. It treats the game as a single player game where the opponent retroactively acts randomly. which is NOT RIGHT, as far as I can tell

        else {
            if ( selectedNode.children == null ) {
                if ( selectedNode.depth < UCT_TREE_DEPTH ) {
                    if ( UCT_CREATE_NODE_THRESHOLD <= selectedNode.games ) {
                        selectedNode.createNode();
                        selectedNode.isCreateNode = true;
                        score = selectedNode.uct(opponentModel);
                    }

                    else {
                        score = selectedNode.playout(opponentModel);
                    }
                }

                else {
                    score = selectedNode.playout(opponentModel);
                }
            }

            else {
                if ( selectedNode.depth < UCT_TREE_DEPTH ) {
                    score = selectedNode.uct(opponentModel);
                }

                else {
                    selectedNode.playout(opponentModel);
                }
            }
        }

        selectedNode.games++;
        selectedNode.score += score;

        if ( depth == 0 ) {
            games++;
        }

        return score;
    }

    /* Generate Node */
    public void createNode() {

        this.children = new Node[myActions.size()];

        for ( int i = 0; i < children.length; i++ ) {

            LinkedList<Action> my = new LinkedList<Action>();
            for ( Action act : selectedMyActions ) {
                my.add(act);
            }

            my.add(myActions.get(i));

            children[i] = new Node( frameData, this, myActions, oppActions, gameData, playerNumber, commandCenter, my );
        }
    }

    /* Return Evaluation Value using frame data */
    public int getScore( FrameData fd ) {
        return playerNumber ? ( fd.getP1().hp - myOriginalHp ) - ( fd.getP2().hp - oppOriginalHp ) : ( fd.getP2().hp - myOriginalHp ) - ( fd.getP1().hp - oppOriginalHp );
    }

    /* Return Action of Node most visited */
    public Action getBestVisitAction() {

        int selected = -1;
        double bestGames = -9999;

        for ( int i = 0; i < children.length; i++ ) {

            if (Fighting_AI.DEBUG_MODE) {
                System.out.println("Evaluation Value:" + children[i].score / children[i].games + ", Number of Trials:" + children[i].games + ", UCB:" + children[i].ucb + ", Action:" + myActions.get(i) );
            }

            if ( bestGames < children[i].games ) {
                bestGames = children[i].games;
                selected = i;
            }
        }

        if (Fighting_AI.DEBUG_MODE) {
            System.out.println( myActions.get( selected ) + ", Number of Trials:" + games );
            System.out.println( "" );
        }

        return this.myActions.get( selected );
    }

    /* Return Node of Action with highest score */
    public Action getBestScoreAction() {

        int selected = -1;
        double bestScore = -9999;

        for ( int i = 0; i < children.length; i++ ) {

            System.out.println( "Evaluation Value:" + children[i].score / children[i].games + ", Number of Trials:" + children[i].games + ", UCB:" + children[i].ucb + ",Action:" + myActions.get(i) );

            double meanScore = children[i].score / children[i].games;

            if ( bestScore < meanScore ) {
                bestScore = meanScore;
                selected = i;
            }
        }

        System.out.println( myActions.get( selected ) + ", Number of Trials:" + games );
        System.out.println( "" );

        return this.myActions.get( selected );
    }

    /* Return Upper Confidence Bound 1 Value using: Evaluation Value(score), Number of All Simulations(n), Number of attempts concerning action of simulation(ni) */
    public double getUcb( double score, int n, int ni ) {
        return score + UCB_C * Math.sqrt( ( 2 * Math.log(n) ) / ni );
    }

    public void printNode( Node node ) {
        System.out.println( "Number of Trials:" + node.games );
        for (int i = 0; i < node.children.length; i++) {
            System.out.println( i + ", Frequency of Recurrence:" + node.children[i].games + ", Tree Depth Location:" + node.children[i].depth + ", Score:" + node.children[i].score / node.children[i].games + ", UCB:" + node.children[i].ucb + ", Number of Possible Actions:" + node.children[i].myActions.size() );
        }

        System.out.println( "" );

        for ( int i = 0; i < node.children.length; i++ ) {
            if ( node.children[i].isCreateNode ) {
                printNode( node.children[i] );
            }
        }
    }
}
