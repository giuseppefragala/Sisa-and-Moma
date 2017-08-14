package helpers;

/**
 * Created by Giuseppe on 11/08/2017.
 */

public class GameManager {
    private static final GameManager ourInstance = new GameManager();

    private final String[] players = {"Blue", "Green", "Red"};
    private int index = 1;


    private GameManager() {
    }

    public void incrementIndex() {
        index++;
        if(index == players.length){
            index = 0;
        }
    }

    public String getPlayer() {
        return  players[index];
    }

    public static GameManager getInstance() {
        return ourInstance;
    }
}