package hud;

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
import helpers.GameInfo;
import helpers.GameManager;
import scenes.GamePlay;
import scenes.HighScores;


public class MainMenuButtons {

    private GameMain game;

    private Stage stage;
    private Viewport gameViewport;
    private ImageButton playBtn, scoreBtn, changePlayerBtn, soundBtn;
    private boolean playSound;
    private Label scoreLabel;
    private Sound changePlayerSound;

    public MainMenuButtons(GameMain game) {
        this.game = game;

        gameViewport = new FitViewport(GameInfo.WIDTH, GameInfo.HIGHT, new OrthographicCamera());
        stage = new Stage(gameViewport, game.getBatch());

        createAndPositionButtons();

        stage.addActor(playBtn);
        stage.addActor(scoreBtn);

        changePlayerSound = Gdx.audio.newSound(Gdx.files.internal("changePlayer.mp3"));
        createLabel();
        changePlayer();
        changeSoundBtn();
        stage.addActor(scoreLabel);


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

    void changeSoundBtn() {
        if(soundBtn != null){
            soundBtn.remove();
        }

        final Preferences prefs = Gdx.app.getPreferences("Data");
        boolean soundStatus = prefs.getBoolean("SoundStatus");

        if(soundStatus) {
            soundBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Sound-on.png"))));
        }else {
            soundBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Sound-off.png"))));
        }

        soundBtn.setPosition(GameInfo.WIDTH - 75, 75, Align.center);

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

        if(scoreLabel.getText().toString().contains("Sisa")){
            scoreLabel.setText("Moma");
        }else{
            scoreLabel.setText("Sisa");
        }

        changePlayerBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture(GameManager.getInstance().getPlayer()))));
        changePlayerBtn.setPosition(GameInfo.WIDTH - 75, GameInfo.HIGHT - 75, Align.center);

        changePlayerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                // increment index of the players' array. That is, get the next player
                GameManager.getInstance().incrementIndex();

                //call changePlayer to change the player
                changePlayerSound.play();
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
        scoreLabel = new Label("Sisa", new Label.LabelStyle(font, new Color(204f/255f, 65f/255f, 65f/255f, 1f)));
        scoreLabel.setPosition(GameInfo.WIDTH - 375, GameInfo.HIGHT - 75, Align.center);
    } // createLabel()



} // main menu buttons


