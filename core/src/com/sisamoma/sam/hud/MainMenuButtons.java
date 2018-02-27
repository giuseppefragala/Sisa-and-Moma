package com.sisamoma.sam.hud;

/**
 * Created by Giuseppe on 23/02/2018.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.sisamoma.sam.GameMain;
import com.sisamoma.sam.helpers.GameInfo;
import com.sisamoma.sam.helpers.GameManager;
import com.sisamoma.sam.scenes.GamePlay;
import com.sisamoma.sam.scenes.HighScores;


public class MainMenuButtons {

    private GameMain game;

    private Stage stage;
    private Viewport gameViewport;
    private ImageButton playBtn, scoreBtn, changePlayerBtn, musicBtn, soundBtn;
    private boolean playMusic, playSound;
    private Label playerLabel;
    private Sound changePlayerSound;
    private Preferences prefs;
    private boolean musicStatus;
    private boolean soundStatus;

    public MainMenuButtons(GameMain game) {
        this.game = game;
        prefs = Gdx.app.getPreferences("Data");


        gameViewport = new FitViewport(GameInfo.WIDTH, GameInfo.HIGHT, new OrthographicCamera());
        stage = new Stage(gameViewport, game.getBatch());

        createAndPositionButtons();

        stage.addActor(playBtn);
        stage.addActor(scoreBtn);

        changePlayerSound = Gdx.audio.newSound(Gdx.files.internal("changePlayer.mp3"));
        createLabel();
        changePlayer();
        changeMusicBtn();
        changeSoundBtn();
        stage.addActor(playerLabel);


    } // MainMenuButtons

    void createAndPositionButtons() {
        playBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Play.png"))));
        scoreBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Score.png"))));

        playBtn.setPosition(75, GameInfo.HIGHT / 2f , Align.center);
        scoreBtn.setPosition(GameInfo.WIDTH - 75, GameInfo.HIGHT / 2f , Align.center);

        playBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GamePlay(game));
                stage.dispose();
            }
        });

        scoreBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new HighScores(game));
                //showScore();
            }
        });
    } // createAndPositionButtons()

    void changeMusicBtn() {
        if(musicBtn != null){
            musicBtn.remove();
        }

        musicStatus = prefs.getBoolean("MusicStatus");
        if(musicStatus) {
            musicBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("musicOn.png"))));
        }else {
            musicBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("musicOff.png"))));
        }

        musicBtn.setPosition(GameInfo.WIDTH - 75, 75, Align.center);

        musicBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playMusic = !playMusic;
                prefs.putBoolean("MusicStatus", playMusic);
                prefs.flush();
                changeMusicBtn();
            }
        });
        stage.addActor(musicBtn);

    } // changeMusicBtn()


    void changeSoundBtn() {
        if(soundBtn != null){
            soundBtn.remove();
        }

        soundStatus = prefs.getBoolean("SoundStatus");
        if(soundStatus) {
            soundBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("soundOn.png"))));
        }else {
            soundBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("soundOff.png"))));
        }

        soundBtn.setPosition(75, 75, Align.center);

        soundBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playSound = !playSound;
                prefs.putBoolean("SoundStatus", playSound);
                prefs.flush();
                changeSoundBtn();
            }
        });
        stage.addActor(soundBtn);

    } // changeSoundBtn()



    void changePlayer() {

        if(changePlayerBtn != null){
            changePlayerBtn.remove();
        }

        playerLabel.setText(GameManager.getInstance().getPlayer().substring(0,4).toUpperCase());

        changePlayerBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture(GameManager.getInstance().getPlayer()))));
        changePlayerBtn.setPosition(GameInfo.WIDTH - 75, GameInfo.HIGHT - 75, Align.center);

        changePlayerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                // increment index of the com.sisamoma.sam.players' array. That is, get the next player
                GameManager.getInstance().incrementIndex();

                soundStatus = prefs.getBoolean("SoundStatus");
                //Check if can run change player sound
                if(soundStatus){
                    changePlayerSound.play();
                }

                //call changePlayer to change the player
                changePlayer();
            }
        });

        stage.addActor(changePlayerBtn);
    } // changePlayer()


    public Stage getStage() {
        return this.stage;
    }


    void createLabel() {
        // html version doesn't work with freetype
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("GILSANUB.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 80;
        BitmapFont font = fontGenerator.generateFont(parameter);


        /* this code in case freetype doesn't work
        BitmapFont font = new BitmapFont(Gdx.files.internal("myfont.fnt"));
        */
        playerLabel = new Label("Sisa", new Label.LabelStyle(font, new Color(204f/255f, 65f/255f, 65f/255f, 1f)));
        playerLabel.setPosition(120, GameInfo.HIGHT - 75, Align.center);
    } // createLabel()



} // main menu buttons


