package com.sisamoma.sam.scenes;

/*
 * Created by Giuseppe on 23/02/2018. BRANCH DEVELOP
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
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
import com.sisamoma.sam.GameMain;
import com.sisamoma.sam.coins.Coins;
import com.sisamoma.sam.ground.GroundBody;
import com.sisamoma.sam.helpers.GameInfo;
import com.sisamoma.sam.helpers.GameManager;
import com.sisamoma.sam.hud.UIHud;
import com.sisamoma.sam.pipes.Pipes;
import com.sisamoma.sam.players.Player;
import com.sisamoma.sam.top.TopBody;

import static com.badlogic.gdx.Gdx.app;


public class GamePlay implements Screen, ContactListener {
    private final GameMain game;
    private final World world;

    private final OrthographicCamera mainCamera;
    private final Viewport gameViewport;

    private final OrthographicCamera debugCamera;
    private final Box2DDebugRenderer debugRenderer;

    private final Array<Sprite> backgrounds = new Array<Sprite>();
    private final Array<Sprite> grounds = new Array<Sprite>();

    private final Player player;
    private final GroundBody groundBody;
    private final TopBody topBody;

    private final UIHud hud;

    private boolean firstTouch;
    private boolean justPaused;
    private final Preferences prefs;
    private final boolean musicStatus;
    private final boolean soundStatus;
    private final Array<Pipes> pipesArray = new Array<Pipes>();
    private final Array<Coins> coinsArray = new Array<Coins>();

    private final Sound scoreSound;
    private final Sound coinSound;
    private final Sound playerDiedSound;
    private final Sound playerBubbleSound;

    private final Music backgroundMusic;

    private int count = 0;

    private final SequenceAction sa;
    private final RunnableAction run;

    private boolean canRestartSa;
    private final boolean isSensor = false;

    private final ParticleEffect pe;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (GameManager.getInstance().getGameStatus()) {
                createPipes();
                count++;
                //generate a new coin after 5 to 10 pipe
                if (count >= Math.floor(5 + 5 * Math.random())) {
                    createCoin();
                    count = 0;
                }
            }
        }
    };


    public GamePlay(GameMain game) {

        this.game = game;

        pe = new ParticleEffect();
        pe.load(Gdx.files.internal("Scenes/Particles.p"), Gdx.files.internal(""));
        pe.getEmitters().first().setPosition(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f);
        pe.start();

        sa = new SequenceAction();
        run = new RunnableAction();

        mainCamera = new OrthographicCamera(GameInfo.WIDTH, GameInfo.HEIGHT);
        mainCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f, 0);
        gameViewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, mainCamera);

        debugCamera = new OrthographicCamera();
        debugCamera.setToOrtho(false, GameInfo.WIDTH / GameInfo.PPM, GameInfo.HEIGHT / GameInfo.PPM);
        debugCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f, 0);
        debugRenderer = new Box2DDebugRenderer();

        hud = new UIHud(game);

        createBackgrounds();
        createGrounds();

        world = new World(new Vector2(0, GameInfo.GAMEPLAY_WORLD_G_ACCELERATION), true);
        world.setContactListener(this);

        player = new Player(world, GameInfo.WIDTH / 2f - 30f, GameInfo.HEIGHT / 2f);
        groundBody = new GroundBody(world, grounds.get(0));
        topBody = new TopBody(world, grounds.get(0));

        scoreSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/Score.mp3"));
        coinSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/Coin.mp3"));
        playerDiedSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/Dead.mp3"));
        playerBubbleSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/Bubble.mp3"));

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds/game.mp3"));
        backgroundMusic.setLooping(true);
        prefs = app.getPreferences("Data");
        musicStatus = prefs.getBoolean("MusicStatus");
        soundStatus = prefs.getBoolean("SoundStatus");
        if (musicStatus) {
            backgroundMusic.play();
        }

        hud.showPlayButtons();
        Gdx.input.setInputProcessor(hud.getStage());
    } // GamePlay

    private void checkForFirstTouch() {
        if (!firstTouch) {
            if (Gdx.input.justTouched()) {
                firstTouch = true;
                player.activatePlayer();
                createAllPipes();
            }
        }
    } // checkForFirstTouch()

    private void createAllPipes() {
        //thread = new Thread(runnable);
        run.setRunnable(runnable);
        sa.addAction(Actions.delay(GameInfo.PIPES_SPAWN_DELAY));
        sa.addAction(run);
        hud.getStage().addAction(Actions.forever(sa));
    } // createAllPipes()

    private void playerSwim() {
        if (Gdx.input.justTouched()) {
            if (soundStatus) {
                playerBubbleSound.play();
            }
            player.playerSwim();
        }
    } // playerSwim()

    private void stopPlayer() {
        player.stopPlayer();
        world.setGravity(new Vector2(0, -0.2f * GameInfo.GAMEPLAY_WORLD_G_ACCELERATION));
    }

    private void createBackgrounds() {
        for (int i = 0; i < 3; i++) {
            Sprite background = new Sprite(new Texture("Scenes/background_game.png"));
            background.setPosition(i * background.getWidth(), 0);
            backgrounds.add(background);
        }
    } // createBackgrounds()

    private void createGrounds() {
        for (int i = 0; i < 3; i++) {
            Sprite ground = new Sprite(new Texture("Scenes/ground.png"));
            ground.setPosition(i * ground.getWidth(), 0);
            grounds.add(ground);
        }
    }

    private void drawBackgrounds(SpriteBatch batch) {
        for (Sprite s : backgrounds) {
            batch.draw(s, s.getX(), s.getY());
        }
    }

    void drawGrounds(SpriteBatch batch) {
        for (Sprite s : grounds) {
            batch.draw(s, s.getX(), s.getY());
        }
    }

    private void moveBackgrounds() {
        for (Sprite background : backgrounds) {
            float x1 = background.getX() - GameInfo.BACKGROUND_SPEED;
            background.setPosition(x1, background.getY());
            if (background.getX() + GameInfo.WIDTH + (background.getWidth() / 2f) < mainCamera.position.x) {
                float x2 = background.getX() + background.getWidth() * backgrounds.size;
                background.setPosition(x2, background.getY());
            }

        }
    } // moveBackgrounds()

    void moveGrounds() {
        for (Sprite ground : grounds) {
            float x1 = ground.getX() - GameInfo.GROUND_SPEED;
            ground.setPosition(x1, ground.getY());
            if (ground.getX() + GameInfo.WIDTH + (ground.getWidth() / 2f) < mainCamera.position.x) {
                float x2 = ground.getX() + ground.getWidth() * backgrounds.size;
                ground.setPosition(x2, ground.getY());
            }
        }
    } // moveGrounds()


    // PIPES ---------------------------------------------------------------------------------------
    private void createPipes() {
        //Gdx.app.log("createPipes", "isSensor: " + isSensor);
        Pipes pipe = new Pipes(world, GameInfo.WIDTH + GameInfo.DISTANCE_BETWEEN_PIPES, isSensor);
        pipe.setMainCamera(mainCamera);
        pipesArray.add(pipe);
    }

    private void drawPipes(SpriteBatch batch) {
        for (Pipes pipe : pipesArray) {
            pipe.drawPipes(batch);
        }
    }

    private void updatePipes() {
        for (Pipes pipe : pipesArray) {
            pipe.updatePipes();
        }
    }

    private void movePipes() {
        for (Pipes pipe : pipesArray) {
            pipe.movePipes();
        }
    }

    private void stopPipes() {
        for (Pipes pipe : pipesArray) {
            pipe.stopPipes();
        }
    }
    // END PIPES -----------------------------------------------------------------------------------

    // COINS ---------------------------------------------------------------------------------------
    private void createCoin() {
        Coins coin = new Coins(world, GameInfo.WIDTH + GameInfo.DISTANCE_BETWEEN_PIPES);
        coin.setMainCamera(mainCamera);
        coinsArray.add(coin);
    }

    private void drawCoin(SpriteBatch batch) {
        for (Coins coin : coinsArray) {
            coin.animateCoin(batch);
        }
    }

    private void updateCoin() {
        for (Coins coin : coinsArray) {
            coin.updateCoin();
        }
    }

    private void moveCoin() {
        for (Coins coin : coinsArray) {
            coin.moveCoin();
        }
    }

    private void stopCoin() {
        for (Coins coin : coinsArray) {
            coin.stopCoin();
        }
    }
    // END COINS ------------------------------------------------------------------------------------------------

    private void playerDied() {
        backgroundMusic.stop();
        player.setAlive(false);
        stopPlayer();
        player.playerDied();

        stopPipes();
        stopCoin();

        pe.reset();

        hud.getStage().clear();
        hud.showScore();

        int highScore = prefs.getInteger("Score");

        if (highScore < hud.getScore()) {
            prefs.putInteger("Score", hud.getScore());
            prefs.flush();
        }

        hud.createButtons();
        Gdx.input.setInputProcessor(hud.getStage());
    } // playerDied()

    @Override
    public void show() {

    }

    private void update(float dt) {

        checkForFirstTouch();

        if (player.getAlive()) {
            moveBackgrounds();
            //moveGrounds();
            playerSwim();

            updatePipes();
            movePipes();

            updateCoin();
            moveCoin();

            pe.update(dt);

        }

    } // update

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().begin();
        //--------------------------------------------------------------------------------------
        //drawGrounds(game.getBatch());
        drawBackgrounds(game.getBatch());

        player.drawIdle(game.getBatch());
        player.animatePlayer(game.getBatch());

        //drawing the com.sisamoma.sam.pipes
        drawPipes(game.getBatch());

        //drawing the com.sisamoma.sam.coins
        drawCoin(game.getBatch());

        pe.draw(game.getBatch());
        pe.setPosition(player.getX() + player.getWidth() * (0.7f), player.getY() + 50f);
        //--------------------------------------------------------------------------------------
        game.getBatch().end();

        if (pe.isComplete()) {
            pe.reset();
        }

        if (GameManager.getInstance().getGameStatus()) {
            player.updatePlayer();
            update(delta);
            world.step(Gdx.graphics.getDeltaTime(), 6, 2);
            game.getBatch().setProjectionMatrix(hud.getStage().getCamera().combined);
        }
        hud.getStage().draw();
        hud.getStage().act();

        // comment this to hide debugrender shape's line
        //debugRenderer.render(world, debugCamera.combined);

    } //render

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height);
    }

    @Override
    public void pause() {
        canRestartSa = true;
    }

    @Override
    public void resume() {
        if (canRestartSa) {
            sa.restart();
            canRestartSa = false;
        }
        //GameManager.getInstance().setPlayerShield(true);
        //isSensor = !isSensor;
        //player.getBody().getFixtureList().first().setSensor(true);
        //player.getBody().getFixtureList().first().refilter();


    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        for (Sprite background : backgrounds) {
            background.getTexture().dispose();
        }

        for (Sprite ground : grounds) {
            ground.getTexture().dispose();
        }

        for (Pipes pipe : pipesArray) {
            pipe.disposeAll();
        }

        for (Coins coin : coinsArray) {
            coin.disposeAll();
        }

        scoreSound.dispose();
        coinSound.dispose();
        playerDiedSound.dispose();
        playerBubbleSound.dispose();
        pe.dispose();

        world.dispose();

    } // dispose()

    @Override
    public void beginContact(Contact contact) {
        if (!GameManager.getInstance().getPlayerShield()) {
            Fixture body1, body2;
            if (contact.getFixtureA().getUserData() == "Player") {
                body1 = contact.getFixtureA();
                body2 = contact.getFixtureB();
            } else {
                body1 = contact.getFixtureB();
                body2 = contact.getFixtureA();
            }
            if (body1.getUserData() == "Player" && body2.getUserData() == "Pipe") {
                if (player.getAlive()) {
                    if (soundStatus) {
                        playerDiedSound.play();
                    }
                    playerDied();
                }
            }

            if (body1.getUserData() == "Player" && body2.getUserData() == "Coin") {
                if (coinsArray.size > 0) { //this to avoid "contacting" the same coin twice
                    if (soundStatus) {
                        coinSound.play();
                    }
                    hud.incrementScore();
                    coinsArray.clear();
                }
            }

            if (body1.getUserData() == "Player" && body2.getUserData() == "Ground") {
                if (player.getAlive()) {
                    if (soundStatus) {
                        playerDiedSound.play();
                    }
                    playerDied();
                }
            }

            if (body1.getUserData() == "Player" && body2.getUserData() == "Top") {
                if (player.getAlive()) {
                    if (soundStatus) {
                        playerDiedSound.play();
                    }
                    playerDied();
                }
            }


            if (body1.getUserData() == "Player" && body2.getUserData() == "Score") {
                if (soundStatus) {
                    scoreSound.play();
                }
                hud.incrementScore();
            }
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

