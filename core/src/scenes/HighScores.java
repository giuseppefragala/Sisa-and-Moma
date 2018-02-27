package scenes;

/**
 * Created by Giuseppe on 23/02/2018.
 */




import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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

import static com.badlogic.gdx.Gdx.app;

public class HighScores implements Screen {

    private GameMain game;
    private Stage stage;
    private Texture background;
    private OrthographicCamera mainCamera;
    private Viewport gameViewport;
    private ImageButton backBtn;
    private Label scoreLabel;
    Music backgroundMusic;

    public HighScores(GameMain game){
        this.game = game;

        mainCamera = new OrthographicCamera();
        mainCamera.setToOrtho(false, GameInfo.WIDTH, GameInfo.HIGHT);
        mainCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HIGHT / 2f, 0);
        gameViewport = new FitViewport(GameInfo.WIDTH, GameInfo.HIGHT, mainCamera);
        stage = new Stage(gameViewport, game.getBatch());
        background = new Texture("background_hs.jpg");
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("highscore.mp3"));
        backgroundMusic.setLooping(true);

        createAndPositionButtons();
        stage.addActor(backBtn);
        showScore();

        Gdx.input.setInputProcessor(stage);
    } // HighScores


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

        game.getBatch().setProjectionMatrix(stage.getCamera().combined);
        stage.draw();

        Preferences prefs = app.getPreferences("Data");
        boolean soundStatus = prefs.getBoolean("SoundStatus");
        if(soundStatus) {
            backgroundMusic.play();
        }


    } // render

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

    void showScore() {

        if(scoreLabel != null) {
            return;
        }

        // freetype doesn't work in html version
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("GILLUBCD.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 80;
        BitmapFont font = fontGenerator.generateFont(parameter);


        /* this lines of code are used in case freetype doesn't work
        Color color =  new Color(204.0f, 65.0f, 65.0f, 1.0f);
        BitmapFont font = new BitmapFont(Gdx.files.internal("myfont.fnt"));
        */
        Preferences prefs = Gdx.app.getPreferences("Data");
        scoreLabel = new Label(String.valueOf(prefs.getInteger("Score")), new Label.LabelStyle(font, new Color(204f/255f, 65f/255f, 65f/255f, 1f)));
        scoreLabel.setPosition(GameInfo.WIDTH / 2f, GameInfo.HIGHT /2f + 50f, Align.center);
        stage.addActor(scoreLabel);

    } // showScore()

    void createAndPositionButtons() {
        backBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Back.png"))));
        backBtn.setPosition(GameInfo.WIDTH / 2f , GameInfo.HIGHT / 2f - 150f, Align.center);

        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenu(game));
                //showScore();
            }
        });
    } // createAndPositionButtons()

} // HighScores
