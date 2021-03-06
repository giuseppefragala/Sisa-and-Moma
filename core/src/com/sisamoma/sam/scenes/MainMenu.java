package com.sisamoma.sam.scenes;

/**
 * Created by Giuseppe on 23/02/2018.
 */




import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sisamoma.sam.GameMain;


import com.sisamoma.sam.helpers.GameInfo;
import com.sisamoma.sam.hud.MainMenuButtons;



public class MainMenu implements Screen {

    GameMain game;
    private Texture background;
    private OrthographicCamera mainCamera;
    private Viewport gameViewport;
    private MainMenuButtons mainMenuButtons;
    Music backgroundMusic;

    public MainMenu(GameMain game) {
        this.game = game;

        mainCamera = new OrthographicCamera();
        mainCamera.setToOrtho(false, GameInfo.WIDTH, GameInfo.HIGHT);
        mainCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HIGHT / 2f, 0);

        gameViewport = new FitViewport(GameInfo.WIDTH, GameInfo.HIGHT, mainCamera);

        background = new Texture("Background_menu.png");
        mainMenuButtons = new MainMenuButtons(game);

        Gdx.input.setInputProcessor(mainMenuButtons.getStage());

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("menu.mp3"));
        backgroundMusic.setLooping(true);
    } //MainMenu


    public void checkMusic(){
        Preferences prefs = Gdx.app.getPreferences("Data");
        boolean musicStatus = prefs.getBoolean("MusicStatus");

        if(!musicStatus) {
            backgroundMusic.stop();
        }else{
            backgroundMusic.play();
        }
    } // checkSound()

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().begin();
        game.getBatch().draw(background, 0, 0);
        game.getBatch().end();

        game.getBatch().setProjectionMatrix(mainMenuButtons.getStage().getCamera().combined);
        mainMenuButtons.getStage().draw();

        checkMusic();
    } //render

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        backgroundMusic.stop();
    }

    @Override
    public void dispose() {
        background.dispose();
        backgroundMusic.dispose();
    }
}

