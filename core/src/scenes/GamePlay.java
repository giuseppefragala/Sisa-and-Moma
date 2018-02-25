package scenes;

/**
 * Created by Giuseppe on 23/02/2018.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import static com.badlogic.gdx.Gdx.app;

import com.sisamoma.sam.GameMain;
import coins.Coins;
import ground.GroundBody;
import helpers.GameInfo;
import hud.UIHud;
import pipes.Pipes;
import players.Player;
import top.TopBody;

public class GamePlay implements Screen, ContactListener {
    private GameMain game;
    private World world;

    private OrthographicCamera mainCamera;
    private Viewport gameViewport;

    private OrthographicCamera debugCamera;
    private Box2DDebugRenderer debugRenderer;

    private Array<Sprite> backgrounds = new Array<Sprite>();
    private Array<Sprite> grounds = new Array<Sprite>();

    private Player player;
    private GroundBody groundBody;
    private TopBody topBody;

    private UIHud hud;

    private boolean firstTouch;

    private Array<Pipes> pipesArray = new Array<Pipes>();
    private Array<Coins> coinsArray = new Array<Coins>();

    private Sound scoreSound, coinSound, playerDiedSound, playerFlapSound;

    Music backgroundMusic;

    int count = 0;

    public GamePlay(GameMain game){

        this.game = game;

        mainCamera = new OrthographicCamera(GameInfo.WIDTH,GameInfo.HIGHT);
        mainCamera.position.set(GameInfo.WIDTH / 2f,GameInfo.HIGHT / 2f, 0);
        gameViewport = new FitViewport(GameInfo.WIDTH, GameInfo.HIGHT, mainCamera);

        debugCamera = new OrthographicCamera();
        debugCamera.setToOrtho(false, GameInfo.WIDTH / GameInfo.PPM, GameInfo.HIGHT / GameInfo.PPM);
        debugCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HIGHT / 2f, 0);
        debugRenderer = new Box2DDebugRenderer();

        hud = new UIHud(game);

        createBackgrounds();
        createGrounds();

        world = new World(new Vector2(0, GameInfo.GAMEPLAY_WORLD_G_ACCELERATION), true);
        world.setContactListener(this);

        player = new Player(world, GameInfo.WIDTH / 2f - 30f, GameInfo.HIGHT / 2f);
        groundBody = new GroundBody(world, grounds.get(0));
        topBody = new TopBody(world,grounds.get(0));

        scoreSound = Gdx.audio.newSound(Gdx.files.internal("Score.mp3"));
        coinSound = Gdx.audio.newSound(Gdx.files.internal("Coin.mp3"));
        playerDiedSound = Gdx.audio.newSound(Gdx.files.internal("Dead.mp3"));
        playerFlapSound = Gdx.audio.newSound(Gdx.files.internal("Fly.mp3"));

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("game.mp3"));
        backgroundMusic.setLooping(true);
        Preferences prefs = app.getPreferences("Data");
        boolean soundStatus = prefs.getBoolean("SoundStatus");

        if(soundStatus) {
            backgroundMusic.play();
        }

    } // GamePlay

    void checkForFirstTouch() {
        if(!firstTouch) {
            if(Gdx.input.justTouched()) {
                firstTouch = true;
                player.activatePlayer();
                createAllPipes();
            }
        }

    } // checkForFirstTouch()

    void update(float dt) {

        checkForFirstTouch();

        if(player.getAlive()) {
            moveBackgrounds();
            moveGrounds();
            playerSwim();

            updatePipes();
            movePipes();

            updateCoin();
            moveCoin();

        }
    } // update

    void createAllPipes() {
        RunnableAction run = new RunnableAction();
        run.setRunnable(new Runnable() {
            @Override
            public void run() {
                //put custom code
                createPipes();

                count++;
                if(count >= Math.floor(5 + 5 * Math.random())) {
                    createCoin();
                    count = 0;
                }

            }
        });

        SequenceAction sa = new SequenceAction();
        sa.addAction(Actions.delay(GameInfo.PIPES_SPAWN_DELAY));
        sa.addAction(run);

        hud.getStage().addAction(Actions.forever(sa));
    } // createAllPipes()

    void playerSwim(){
        if(Gdx.input.justTouched()) {
            Preferences prefs = app.getPreferences("Data");
            boolean soundStatus = prefs.getBoolean("SoundStatus");
            if(soundStatus) {
                playerFlapSound.play();
            }
            player.playerSwim();
        }
    } // playerSwim()

    void stopPlayer() {
        player.stopPlayer();
        world.setGravity(new Vector2(0, -0.2f * GameInfo.GAMEPLAY_WORLD_G_ACCELERATION));
    }

    void createBackgrounds() {
        for (int i = 0; i < 3; i++) {
            Sprite background = new Sprite(new Texture("backgroung_game.png"));
            background.setPosition(i * background.getWidth(), 0);
            backgrounds.add(background);
        }
    } // createBackgrounds()

    void createGrounds() {
        for (int i = 0; i < 3; i++) {
            Sprite ground = new Sprite(new Texture("ground.png"));
            ground.setPosition(i * ground.getWidth(), 0);
            grounds.add(ground);
        }
    }


    void drawBackgrounds(SpriteBatch batch){
        for(Sprite s : backgrounds){
            batch.draw(s, s.getX(), s.getY());
        }
    }

    void drawGrounds(SpriteBatch batch){
        for(Sprite s : grounds){
            batch.draw(s, s.getX(), s.getY());
        }
    }

    void moveBackgrounds() {
        for(Sprite background : backgrounds){
            float x1 = background.getX() - GameInfo.BACKGROUND_SPEED;
            background.setPosition(x1, background.getY());

            if(background.getX() + GameInfo.WIDTH + (background.getWidth() / 2f ) < mainCamera.position.x){
                float x2 = background.getX() + background.getWidth() * backgrounds.size;
                background.setPosition(x2, background.getY());
            }

        }
    } // moveBAckgrounds()

    void moveGrounds() {
        for(Sprite ground : grounds){
            float x1 = ground.getX() - GameInfo.GROUND_SPEED;
            ground.setPosition(x1, ground.getY());

            if(ground.getX() + GameInfo.WIDTH + (ground.getWidth() / 2f ) < mainCamera.position.x){
                float x2 = ground.getX() + ground.getWidth() * backgrounds.size;
                ground.setPosition(x2, ground.getY());
            }

        }
    } // moveGrounds()


    // PIPES -------------------------------------------------------------------------------
    void createPipes() {
        Pipes pipe = new Pipes(world, GameInfo.WIDTH + GameInfo.DISTANCE_BETWEEN_PIPES);
        pipe.setMainCamera(mainCamera);
        pipesArray.add(pipe);
    }

    void drawPipes(SpriteBatch batch){
        for(Pipes pipe : pipesArray){
            pipe.drawPipes(batch);
        }
    }

    void updatePipes(){
        for(Pipes pipe : pipesArray){
            pipe.updatePipes();
        }
    }

    void movePipes(){
        for(Pipes pipe : pipesArray){
            pipe.movePipes();
        }
    }

    void stopPipes(){
        for(Pipes pipe : pipesArray){
            pipe.stopPipes();
        }
    }
    // END PIPES --------------------------------------------------------------------------


    // COINS -------------------------------------------------------------------------------------
    void createCoin() {
        Coins coin = new Coins(world, GameInfo.WIDTH + GameInfo.DISTANCE_BETWEEN_PIPES);
        coin.setMainCamera(mainCamera);
        coinsArray.add(coin);
    }

    void drawCoin(SpriteBatch batch){
        for(Coins coin : coinsArray){
            coin.animateCoin(batch);
        }
    }

    void updateCoin(){
        for(Coins coin : coinsArray){
            coin.updateCoin();
        }
    }

    void moveCoin(){
        for(Coins coin : coinsArray){
            coin.moveCoin();
        }
    }

    void stopCoin(){
        for(Coins coin : coinsArray){
            coin.stopCoin();
        }
    }
    // END COINS ------------------------------------------------------------------------------------------------



    void playerDied() {
        backgroundMusic.stop();
        player.setAlive(false);
        stopPlayer();
        player.playerDied();

        stopPipes();
        stopCoin();

        hud.getStage().clear();
        hud.showScore();

        Preferences prefs = app.getPreferences("Data");
        int highScore = prefs.getInteger("Score");

        if(highScore < hud.getScore()) {
            prefs.putInteger("Score", hud.getScore());
            prefs.flush();
        }

        hud.createButtons();
        Gdx.input.setInputProcessor(hud.getStage());
    } // playerDied()

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        update(delta);

        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().begin();

        drawBackgrounds(game.getBatch());
        //drawGrounds(game.getBatch());
        player.drawIdle(game.getBatch());
        player.animatePlayer(game.getBatch());

        //drawing the pipes
        drawPipes(game.getBatch());

        //drawing the coins
        drawCoin(game.getBatch());

        game.getBatch().end();

        // comment this to hide debugrender shape's line
        //debugRenderer.render(world, debugCamera.combined);

        game.getBatch().setProjectionMatrix(hud.getStage().getCamera().combined);
        hud.getStage().draw();
        hud.getStage().act();

        player.updatePlayer();

        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
    } //render

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width,height);
    }

    @Override
    public void pause() {
        System.out.println("In pausa");
    }

    @Override
    public void resume() {
        System.out.println("Riesumato");
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        for (Sprite background : backgrounds){
            background.getTexture().dispose();
        }

        for (Sprite ground : grounds){
            ground.getTexture().dispose();
        }

        for (Pipes pipe : pipesArray){
            pipe.disposeAll();
        }

        for (Coins coin : coinsArray){
            coin.disposeAll();
        }

        scoreSound.dispose();
        coinSound.dispose();
        playerDiedSound.dispose();
        playerFlapSound.dispose();

        world.dispose();

    } // dispose()

    @Override
    public void beginContact(Contact contact) {

        Preferences prefs = app.getPreferences("Data");
        boolean soundStatus = prefs.getBoolean("SoundStatus");

        Fixture body1, body2;
        if(contact.getFixtureA().getUserData() == "Player") {
            body1 = contact.getFixtureA();
            body2 = contact.getFixtureB();
        }else {
            body1 = contact.getFixtureB();
            body2 = contact.getFixtureA();
        }
        if(body1.getUserData() == "Player" && body2.getUserData() == "Pipe") {
            if(player.getAlive()) {
                if(soundStatus) {
                    playerDiedSound.play();
                }
                playerDied();
            }
        }


        if(body1.getUserData() == "Player" && body2.getUserData() == "Coin") {
            if(soundStatus) {
                coinSound.play();
            }
            hud.incrementScore();

            /*
            //Non funziona !
            for (Coins coin : coinsArray){
                coin.scale(0.01f);
            }
            */

            //Funziona
            coinsArray.clear();
        }

        if(body1.getUserData() == "Player" && body2.getUserData() == "Ground") {
            if(player.getAlive()) {
                if(soundStatus) {
                    playerDiedSound.play();
                }
                playerDied();
            }
        }

        if(body1.getUserData() == "Player" && body2.getUserData() == "Top") {
            if(player.getAlive()) {
                if(soundStatus) {
                    playerDiedSound.play();
                }
                playerDied();
            }
        }


        if(body1.getUserData() == "Player" && body2.getUserData() == "Score") {
            if(soundStatus) {
                scoreSound.play();
            }
            hud.incrementScore();
        }
    } // beginContact

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}

