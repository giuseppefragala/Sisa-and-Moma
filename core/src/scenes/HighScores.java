package scenes;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
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

/**
 * Created by Giuseppe on 11/08/2017.
 */

public class HighScores implements Screen {

    private GameMain game;
    private Stage stage;

    private Texture background;

    private OrthographicCamera mainCamera;
    private Viewport gameViewport;
    private ImageButton backBtn;

    private Label scoreLabel;

    public HighScores(GameMain game){
        this.game = game;

        mainCamera = new OrthographicCamera();
        mainCamera.setToOrtho(false, GameInfo.WIDTH, GameInfo.HIGHT);
        mainCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HIGHT / 2f, 0);

        gameViewport = new FitViewport(GameInfo.WIDTH, GameInfo.HIGHT, mainCamera);
        stage = new Stage(gameViewport, game.getBatch());

        background = new Texture("background_hs.jpg");

        createAndPositionButtons();
        stage.addActor(backBtn);
        showScore();

        Gdx.input.setInputProcessor(stage);
    }


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

    }

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

    }

    @Override
    public void dispose() {
        background.dispose();
    }

    void showScore() {

        if(scoreLabel != null) {
            return;
        }
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("GILLUBCD.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 80;
        BitmapFont font = fontGenerator.generateFont(parameter);

        Preferences prefs = Gdx.app.getPreferences("Data");
        scoreLabel = new Label(String.valueOf(prefs.getInteger("Score")), new Label.LabelStyle(font, Color.CORAL));
        scoreLabel.setPosition(GameInfo.WIDTH / 2f, GameInfo.HIGHT /2f + 50f, Align.center);
        stage.addActor(scoreLabel);

    }

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
    }

} // HighScores