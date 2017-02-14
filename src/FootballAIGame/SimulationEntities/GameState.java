package FootballAIGame.SimulationEntities;

import FootballAIGame.CustomDataTypes.Vector;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class GameState {
   
    public FootballPlayer[] footballPlayers;
    
    public Ball ball;
    
    public int step;
    
    public static GameState parse(byte[] data) throws IllegalArgumentException {
    
        float[] floatData = new float[92];
        int step;
        
        if (data.length / 4 != floatData.length + 1)
            throw new IllegalArgumentException("Invalid game state data.");
    
        ByteBuffer byteBuffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        step = byteBuffer.asIntBuffer().get(0); // parse step
    
        data = Arrays.copyOfRange(data, 4, data.length);
        byteBuffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.asFloatBuffer().get(floatData); // parse vectors
    
        FootballPlayer[] players = new FootballPlayer[22];
        for (int i = 0; i < 22; i++) {
            players[i] = new FootballPlayer();
        }
        
        Ball ball = new Ball();
        ball.position = new Vector(floatData[0], floatData[1]);
        ball.movement = new Vector(floatData[2], floatData[3]);
    
    
        for (int i = 0; i < 22; i++) {
            players[i].position = new Vector(floatData[4 + 4 * i], floatData[4 + 4 * i + 1]);
            players[i].movement = new Vector(floatData[4 + 4 * i + 2], floatData[4 + 4 * i + 2]);
        }
        
        GameState state = new GameState();
        state.ball = ball;
        state.footballPlayers = players;
        state.step = step;
        
        return state;
    }
}
