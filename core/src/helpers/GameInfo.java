package helpers;

/**
 * Created by Giuseppe on 11/08/2017.
 */

public class GameInfo {
    public static final int WIDTH = 800;
    public static final int HIGHT = 480;
    public static final float PPM = 100f;
    public static final float BACKGROUND_SPEED = 2f;
    public static final float GROUND_SPEED = 2f;

    public static final float PIPES_SPAWN_DELAY = 3.8F;
    public static final float PLAYER_LINEAR_VELOCITY_X = 0f; //0.02f;
    public static final float PLAYER_LINEAR_VELOCITY_Y = -2f; //2f;
    public static final float GAMEPLAY_WORLD_G_ACCELERATION = 9.81f;

    public static final float DISTANCE_BETWEEN_PIPES = 280f;

    public static final short PLAYER = 2;
    public static final short GROUND = 4;
    public static final short PIPE = 6;
    public static final short SCORE = 8;
    public static final short TOP = 10;
    public static final short COIN = 12;

    public static final String PLAYER_USERDATA = "Player";
    public static final String GROUND_USERDATA = "Ground";
    public static final String PIPE_USERDATA = "Pipe";
    public static final String SCORE_USERDATA = "Score";
    public static final String TOP_USERDATA = "Top";
    public static final String COIN_USERDATA = "Coin";

    public static final int SCORE_WIDTH = 3;
}
