package hud;

/**
 * Created by Giuseppe on 23/02/2018.
 */



import com.badlogic.gdx.Gdx;
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
import javafx.scene.control.ColorPicker;
import scenes.GamePlay;
import scenes.MainMenu;

public class UIHud {

    private GameMain game;

    private Stage stage;
    private Viewport gameViewport;
    private Label scoreLabel;
    private ImageButton retryBtn, quitBtn;
    private int score;

    public UIHud(GameMain game) {
        this.game = game;
        gameViewport = new FitViewport(GameInfo.WIDTH, GameInfo.HIGHT, new OrthographicCamera());
        stage = new Stage(gameViewport,game.getBatch());
        createLabel();
        stage.addActor(scoreLabel);
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
        scoreLabel = new Label(String.valueOf(score), new Label.LabelStyle(font, new Color(204f/255f, 65f/255f, 65f/255f, 1f)));
        scoreLabel.setPosition(GameInfo.WIDTH / 2f - scoreLabel.getWidth() / 2f - 330, GameInfo.HIGHT / 2F + 180, Align.left);
    } // createLabel()

    public void createButtons() {
        retryBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Retry.png"))));
        quitBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Quit.png"))));

        retryBtn.setPosition(GameInfo.WIDTH / 2f - (retryBtn.getWidth() / 2f) - 100f, GameInfo.HIGHT / 2f - 55f);
        quitBtn.setPosition(GameInfo.WIDTH / 2f - (quitBtn.getWidth() / 2f) + 100f, GameInfo.HIGHT / 2f - 50f);

        retryBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GamePlay(game));
                stage.dispose();
            }
        });

        quitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenu(game));
                stage.dispose();
            }
        });

        stage.addActor(retryBtn);
        stage.addActor(quitBtn);
    } // createButtons()

    public void incrementScore() {
        score++;
        scoreLabel.setText(String.valueOf(score));
    }

    public void showScore() {
        scoreLabel.setText(String.valueOf(score));
        stage.addActor(scoreLabel);
    }

    public int getScore() {
        return score;
    }

    public Stage getStage() {
        return this.stage;
    }



} // UIHud
