package eg.edu.guc.met.agent;
/*
 * Created by Tim Russell
 */

import java.util.LinkedList;
import java.util.Random;

import org.rlcommunity.rlglue.codec.types.Observation;

public class ActionChooser {
    private LinkedList<Integer> prevMovements = new LinkedList<>();
    private Random random = new Random();
    private int turnAround = 2;

    /**
     * @return Next action for agent (1|2|3|4 not implemented|5|6|7 not used)
     */
    public int chooseAction(Observation observation, boolean goldCollected) {
        int ret;
        if (observation.intArray[3] == 1) { // if bump, turn L or R with 50/50 probability
            prevMovements.add(9); // add bump to movement array for return path correction
            double rand = random.nextDouble();
            ret = (rand < 0.5) ? 2 : 3;
        }
        else if (goldCollected) {
            ret = goldCollectedAction();
        }
        else if (observation.intArray[2] == 1) { // if glitter return grab
            ret = 5;
        }
        else {
            ret = chooseRandomMovement();
        }
        // add to list if movement command, stop adding to list when gold is collected
        if (!goldCollected && (ret <= 3)) {
            prevMovements.add(ret);
        }
        return ret;
    }

    private int goldCollectedAction() {
        // if agent just collected gold, turn around
        if (turnAround > 0) {
            turnAround--;
            return 2;
        }
        else if(!prevMovements.isEmpty()) {
            int action = prevMovements.pollLast();

            // if a bump was detected, delete the forward command that must be next,
            // then continue operation with next movement in list
            if (action == 9) {
                prevMovements.pollLast();
                action = prevMovements.pollLast();
            }

            // return forward if corresponding action is forward
            // (as agent is now pointing in opposite direction)
            if (action == 1) { return 1; }

            // else return opposite of left/right move
            return (action == 2) ? 3 : 2;
        }
        return 6; // When prevMovements is empty, agent is at [1,1] with the gold, so climb!
    }

    public int chooseRandomMovement() {
        double rand = random.nextDouble();

        if (rand < 0.6) { return 1; }
        if (rand < 0.8) { return 2; }
        else { return 3; }
    }

    public void printPrevMovements() {
        System.out.println(prevMovements);
    }
}
