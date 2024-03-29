package com.sisamoma.sam.hud;

/*
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

    private final GameMain game;

    private final Stage stage;
    private final Viewport gameViewport;
    private ImageButton playBtn, scoreBtn, changePlayerBtn, musicBtn, soundBtn, exitBtn;
    private boolean playMusic, playSound;
    private Label playerLabel;
    private final Sound changePlayerSound;
    private final Preferences prefs;
    private boolean musicStatus;
    private boolean soundStatus;

    public MainMenuButtons(GameMain game) {
        this.game = game;
        prefs = Gdx.app.getPreferences("Data");


        gameViewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, new OrthographicCamera());
        stage = new Stage(gameViewport, game.getBatch());

        createAndPositionButtons();

        stage.addActor(playBtn);
        stage.addActor(scoreBtn);
        stage.addActor(exitBtn);

        changePlayerSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/changePlayer.mp3"));
        createLabel();
        changePlayer();
        changeMusicBtn();
        changeSoundBtn();
        stage.addActor(playerLabel);


    } // MainMenuButtons

    private void createAndPositionButtons() {
        playBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/playButton.png"))));
        scoreBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/showScoreButton.png"))));
        exitBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/exitButton.png"))));


        playBtn.setPosition(75, GameInfo.HEIGHT / 2f, Align.center);
        scoreBtn.setPosition(GameInfo.WIDTH - 75, GameInfo.HEIGHT / 2f, Align.center);
        exitBtn.setPosition(GameInfo.WIDTH / 2f, 75, Align.center);

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
                stage.dispose();
            }
        });

        exitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

    } // createAndPositionButtons()

    private void changeMusicBtn() {
        if (musicBtn != null) {
            musicBtn.remove();
        }

        musicStatus = prefs.getBoolean("MusicStatus");
        if (musicStatus) {
            musicBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/musicOnButton.png"))));
        } else {
            musicBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/musicOffButton.png"))));
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


    private void changeSoundBtn() {
        if (soundBtn != null) {
            soundBtn.remove();
        }

        soundStatus = prefs.getBoolean("SoundStatus");
        if (soundStatus) {
            soundBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/soundOnButton.png"))));
        } else {
            soundBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/soundOffButton.png"))));
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


    private void changePlayer() {
        if (changePlayerBtn != null) {
            changePlayerBtn.remove();
        }
        changePlayerBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture(GameManager.getInstance().getPlayerImageName()))));
        changePlayerBtn.setPosition(GameInfo.WIDTH - 75, GameInfo.HEIGHT - 75, Align.center);

        playerLabel.setText(GameManager.getInstance().getPlayerImageName().substring(0, 4).toUpperCase());

        changePlayerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                // increment index of the com.sisamoma.sam.players' array. That is, get the next player
                GameManager.getInstance().incrementIndex();

                soundStatus = prefs.getBoolean("SoundStatus");
                //Check if can run change player sound
                if (soundStatus) {
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


    private void createLabel() {
        // html version doesn't work with freetype

        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/Mali-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 80;
        parameter.shadowColor = Color.BLUE;
        parameter.shadowOffsetX = 10;
        parameter.shadowOffsetY = 6;
        BitmapFont font = fontGenerator.generateFont(parameter);


        //* this code in case freetype doesn't work
        //BitmapFont font = new BitmapFont(Gdx.files.internal("Fonts/myfont.fnt"));
        //*/
        playerLabel = new Label("Sisa", new Label.LabelStyle(font, new Color(204f / 255f, 65f / 255f, 65f / 255f, 1f)));
        playerLabel.setPosition(120, GameInfo.HEIGHT - 75, Align.center);
    } // createLabel()

} // main menu buttons


