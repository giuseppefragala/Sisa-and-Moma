package com.sisamoma.sam.scenes;

/*
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

    private final GameMain game;
    private final Texture background;
    private final OrthographicCamera mainCamera;
    private final Viewport gameViewport;
    private final MainMenuButtons mainMenuButtons;
    private final Music backgroundMusic;

    public MainMenu(GameMain game) {
        /*
            Il parametro game (una classe di tipo GameMain e quindi Game)
            serve per ottenere il riferimento del suo Spritebatch, tramite game.getBatch()
            del quale si utilizza il metodo game.getBatch().draw(), per disegnare lo sfondo
            ed impostarne la ProjectionMatrix
        */
        this.game = game;

        mainCamera = new OrthographicCamera();
        mainCamera.setToOrtho(false, GameInfo.WIDTH, GameInfo.HEIGHT);
        mainCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f, 0);

        gameViewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, mainCamera);

        background = new Texture("Scenes/Background_menu.png");
        mainMenuButtons = new MainMenuButtons(game);

        Gdx.input.setInputProcessor(mainMenuButtons.getStage());

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds/menu.mp3"));
        backgroundMusic.setLooping(true);
    } //MainMenu

    private void checkMusic() {

        //Gdx.app.getPreferences("Data") corrisponde ad un file, in questo caso chiamato Data.xml
        //che ha una struttura del genere:
        //------------------------------------------------------------------------------------------
        //<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
        //    <map>
        //        <int name="Score" value="25" />
        //        <boolean name="SoundStatus" value="false" />
        //         <boolean name="MusicStatus" value="false" />
        //     </map>
        //------------------------------------------------------------------------------------------

        Preferences prefs = Gdx.app.getPreferences("Data");
        boolean musicStatus = prefs.getBoolean("MusicStatus");

        if (!musicStatus) {
            backgroundMusic.stop();
        } else {
            backgroundMusic.play();
        }
    } // checkSound()

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // delta corrisponde ad un intervallo di 1/60 secondi (0,016666666667 secondi)

        //When you draw things on the screen you dont draw them directly.
        // Instead they are first drawn to a so called "back-buffer".
        // This is a block of memory (a buffer) that contains four bytes for every pixel of the screen,
        // one byte for each color component (red, green, blue and alpha) of each pixel.
        // When you are ready drawing (when your render method finishes) this buffer is presented at once on the screen.
        //The existing value of the buffer is important.
        // For example when you draw an image on the screen and then draw a semi transparent image on top of that,
        // then the result is a mix of the two images.
        // The first image is drawn to the back-buffer causing the memory of the back-buffer
        // to contain the pixel data of that image.
        // Next the second image is drawn and is blended on top of the existing data of the back-buffer.
        //Each byte of the block of memory always has a value, e.g. 0 for black, 255 for white, etc.
        // Even if you havent drawn anything to the buffer it has to have some value.
        // Calling glClear(GL20.GL_COLOR_BUFFER_BIT) instructs the GPU to fill the entire back buffer
        // with some specified value (color). This value can be set using the call to glClearColor.
        // Note that you don't have to call glClearColor each time glClear is called,
        // the driver will remember the previous value.

        //Resetta lo schermo:
        // 1) prima imposta il colore da assegnare al back-buffer
        Gdx.gl.glClearColor(0, 0, 0, 1);
        // 2) poi esegue il flush del buffer riempiendolo con il colore definito prima
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().begin();
        //disegna lo sfondo, in posizione 0, 0
        game.getBatch().draw(background, 0, 0);
        game.getBatch().end();

        //combined è una matrice

        // public abstract class Camera {
        // .
        // .
        // .
        // /** the combined projection and view matrix **/
        // public final Matrix4 combined = new Matrix4();
        // .
        // .
        // .
        // }
        game.getBatch().setProjectionMatrix(mainMenuButtons.getStage().getCamera().combined);

        //Stage:
        //A 2D scene graph containing hierarchies of {@link Actor actors}. (array di Actor)
        // Stage handles the viewport and distributes input events.
        //(Stage si disegna da solo ? Non ha bisogno del batch?)
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

