package FootballAIGame.AI.FSM.UserClasses.SteeringBehaviors;

import FootballAIGame.AI.FSM.CustomDataTypes.Vector;
import FootballAIGame.AI.FSM.UserClasses.Entities.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SteeringBehaviorsManager {
    
    private Player player;
    
    private TreeMap<Integer, List<SteeringBehavior>> steeringBehaviors;
    
    public SteeringBehaviorsManager(Player player) {
        steeringBehaviors = new TreeMap<Integer, List<SteeringBehavior>>();
        this.player = player;
    }
    
    public void addBehavior(SteeringBehavior behavior) {
        List<SteeringBehavior> list;
        list = steeringBehaviors.get(behavior.priority);
        
        if (list != null)
            list.add(behavior);
        else {
            list = new LinkedList<SteeringBehavior>();
            list.add(behavior);
            steeringBehaviors.put(behavior.priority, list);
        }
    }
    
    public void removeBehavior(SteeringBehavior behavior) {
        List<SteeringBehavior> list = steeringBehaviors.get(behavior.priority);
        if (list != null)
            list.remove(behavior);
    }
    
    public <T> void removeAllbehaviorsOfType(Class<T> typeClass) {
        for (Map.Entry<Integer, List<SteeringBehavior>> entry : steeringBehaviors.entrySet()) {
            
            List<SteeringBehavior> list = entry.getValue();
            LinkedList<SteeringBehavior> steeringBehaviorsToBeRemoved = new LinkedList<SteeringBehavior>();
            
            for (SteeringBehavior steeringBehavior : list) {
                if (steeringBehavior.getClass() == typeClass) {
                    steeringBehaviorsToBeRemoved.add(steeringBehavior);
                }
            }
            
            list.removeAll(steeringBehaviorsToBeRemoved);
        }
    }
    
    public <T> List<SteeringBehavior> getAllbehaviorsOfType(Class<T> typeClass) {
        List<SteeringBehavior> result = new LinkedList<SteeringBehavior>();
        
        for (Map.Entry<Integer, List<SteeringBehavior>> entry : steeringBehaviors.entrySet()) {
            List<SteeringBehavior> list = entry.getValue();
            
            for (SteeringBehavior steeringBehavior : list) {
                if (steeringBehavior.getClass() == typeClass) {
                    result.add(steeringBehavior);
                }
            }
        }
        
        return result;
    }
    
    public Vector calculateAccelerationVector() {
        // Weighted Prioritized Truncated Sum method used
        
        Vector acceleration = new Vector(0, 0);
        double accelerationRemaining = player.maxAcceleration();
        
        for (Map.Entry<Integer, List<SteeringBehavior>> keyValuePair : steeringBehaviors.entrySet()) {
            List<SteeringBehavior> list = keyValuePair.getValue();
            
            for (SteeringBehavior steeringbehavior : list) {
                Vector behaviorAccel = steeringbehavior.calculateAccelerationVector();
                behaviorAccel.multiply(steeringbehavior.weight);
                
                if (accelerationRemaining - behaviorAccel.length() < 0)
                    behaviorAccel.resize(accelerationRemaining);
                accelerationRemaining -= behaviorAccel.length();
                
                acceleration = Vector.sum(acceleration, behaviorAccel);
                
                if (accelerationRemaining <= 0)
                    break;
            }
            
            if (accelerationRemaining <= 0)
                break;
        }
    
        Vector nextMovement = Vector.sum(player.movement, acceleration);
        nextMovement.truncate(player.maxSpeed());
        acceleration = Vector.difference(nextMovement, player.movement);
        
        return acceleration;
    }
    
    public void reset() {
        steeringBehaviors = new TreeMap<Integer, List<SteeringBehavior>>();
    }
    
}