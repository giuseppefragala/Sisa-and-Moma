package com.sisamoma.sam.helpers;


/**
 * Created by Giuseppe on 23/02/2018.
 */


import static com.badlogic.gdx.math.MathUtils.random;


public class GameManager {
    private static final GameManager instance = new GameManager();

    private final String[] players = {"sisa.png", "moma.png"};
    private int index = 1;

    private float randomY;

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

    public float getRandomY() {
        return randomY;
    }

    public void setRandomY(Float randomY) {
        this.randomY = randomY;
    }

    public static GameManager getInstance() {
        return instance;
    }
}
