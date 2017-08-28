package eg.edu.guc.met.agent;
/*
 * Created by Tim Russell
 */

import java.awt.*;
import java.util.LinkedList;
import java.util.Random;

import org.rlcommunity.rlglue.codec.types.Observation;

public class ActionChooser {
    private LinkedList<Integer> prevMovements = new LinkedList<>();
    private Random random = new Random();

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
            parsePrevMoments();
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

    private void parsePrevMoments() {
        // if a bump was detected, delete the forward command that must be next
        for (int i = prevMovements.size()-1; i >= 0; i--) {
            if (prevMovements.get(i) == 9) {
                prevMovements.remove(i);
                prevMovements.remove(i-1);
            }
        }
        // add a 'turn around' action to start
        prevMovements.add(2);
        prevMovements.add(2);

        // find latest index at 0,0
        int origin = findOrigin();

        // remove all steps before that point
        for (int i = origin; i > 0; i--) {
            prevMovements.remove(i-1);
        }
    }

    private int findOrigin() {
        char heading = 2; // 1=N 2=E 3=S 4=W
        Point location = new Point(0,0);
        int latestOrigin = 0;

        // Move forward through movements calculating location
        // record when at 0,0
        for (int i = 0; i < prevMovements.size(); i++) {
            if (location.getX() == 0 && location.getY() == 0) {
                latestOrigin = i;
            }
            if (prevMovements.get(i) == 1) {
                switch (heading) {
                    case 1: location.y++;
                            break;
                    case 2: location.x++;
                            break;
                    case 3: location.y--;
                            break;
                    case 4: location.x--;
                            break;
                }
            }
        }
        return latestOrigin;
    }

    private int goldCollectedAction() {
        if(!prevMovements.isEmpty()) {
            int action = prevMovements.pollLast();

            // return forward if corresponding action is forward
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
