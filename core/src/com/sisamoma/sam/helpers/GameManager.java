package com.sisamoma.sam.helpers;


/**
 * Created by Giuseppe on 23/02/2018.
 */

public class GameManager {
    private static final GameManager instance = new GameManager();

    private final String[] playersImageNameArray = {"sisa.png", "moma.png"};
    private int index = 1;

    private float randomY;

    private boolean gameStatus;
    private boolean shieldActivated;

    private GameManager() {
        this.gameStatus = true;
        this.shieldActivated = false;
    }

    public static GameManager getInstance() {
        return instance;
    }

    public void incrementIndex() {
        index++;
        if (index == playersImageNameArray.length) {
            index = 0;
        }
    }

    public String getPlayerImageName() {
        return playersImageNameArray[index];
    }

    public float getRandomY() {
        return randomY;
    }

    public void setRandomY(Float randomY) {
        this.randomY = randomY;
    }

    public boolean getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(boolean status) {
        gameStatus = status;
    }

    public boolean getPlayerShield() {
        return shieldActivated;
    }

    public void setPlayerShield(boolean shield) {
        shieldActivated = shield;
    }


}
