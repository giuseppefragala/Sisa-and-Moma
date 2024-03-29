package com.sisamoma.sam.scenes;

/*
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
import com.sisamoma.sam.helpers.GameInfo;

import static com.badlogic.gdx.Gdx.app;

public class HighScores implements Screen {

    private final GameMain game;
    private final Stage stage;
    private final Texture background;
    private final OrthographicCamera mainCamera;
    private final Viewport gameViewport;
    private ImageButton backBtn;
    private Label scoreLabel;
    private final Music backgroundMusic;

    public HighScores(GameMain game) {
        this.game = game;

        mainCamera = new OrthographicCamera();
        mainCamera.setToOrtho(false, GameInfo.WIDTH, GameInfo.HEIGHT);
        mainCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f, 0);
        gameViewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, mainCamera);
        stage = new Stage(gameViewport, game.getBatch());
        background = new Texture("Scenes/backgroundHighScores.jpg");
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds/highscore.mp3"));
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
        boolean musicStatus = prefs.getBoolean("MusicStatus");
        if (musicStatus) {
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

    private void showScore() {

        if (scoreLabel != null) {
            return;
        }

        // freetype doesn't work in html version
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/Mali-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 80;
        parameter.shadowColor = Color.BLUE;
        parameter.shadowOffsetX = 10;
        parameter.shadowOffsetY = 6;
        BitmapFont fontScoreLabel = fontGenerator.generateFont(parameter);

        //* this lines of code are used in case freetype doesn't work
        //Color color =  new Color(204.0f, 65.0f, 65.0f, 1.0f);
        //BitmapFont font = new BitmapFont(Gdx.files.internal("Fonts/myfont.fnt"));
        //*/
        scoreLabel = new Label("High Scores", new Label.LabelStyle(fontScoreLabel, new Color(204f / 255f, 65f / 255f, 65f / 255f, 1f)));
        scoreLabel.setPosition(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f + 150f, Align.center);
        stage.addActor(scoreLabel);

        //parameter.size = 150;
        //BitmapFont fontScoreValue = fontGenerator.generateFont(parameter);
        Preferences prefs = Gdx.app.getPreferences("Data");
        scoreLabel = new Label(String.valueOf(prefs.getInteger("Score")), new Label.LabelStyle(fontScoreLabel, new Color(204f / 255f, 65f / 255f, 65f / 255f, 1f)));
        scoreLabel.setPosition(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f, Align.center);
        stage.addActor(scoreLabel);

    } // showScore()

    private void createAndPositionButtons() {
        backBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/backButton.png"))));
        backBtn.setPosition(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f - 150f, Align.center);

        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenu(game));
                //Ritorna al menu principale

                stage.dispose();
                dispose();
            }
        });
    } // createAndPositionButtons()

} // HighScores
