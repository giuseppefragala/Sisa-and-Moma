package scenes;


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
import com.sisamoma.sam.GameMain;

import ground.GroundBody;
import helpers.GameInfo;
import hud.UIHud;
import pipes.Pipes;
import players.Player;

/**
 * Created by Giuseppe on 11/08/2017.
 */

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

    private UIHud hud;

    private boolean firstTouch;

    private Array<Pipes> pipesArray = new Array<Pipes>();

    private Sound scoreSound, playerDiedSound, playerFlapSound;

    Music backgroundMusic;

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

        world = new World(new Vector2(0, -9.81f), true);
        world.setContactListener(this);

        player = new Player(world, GameInfo.WIDTH / 2f - 30f, GameInfo.HIGHT / 2f);
        groundBody = new GroundBody(world, grounds.get(0));

        scoreSound = Gdx.audio.newSound(Gdx.files.internal("Score.mp3"));
        playerDiedSound = Gdx.audio.newSound(Gdx.files.internal("Dead.mp3"));
        playerFlapSound = Gdx.audio.newSound(Gdx.files.internal("Fly.mp3"));

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Avenger.ogg"));
        backgroundMusic.setLooping(true);
        Preferences prefs = Gdx.app.getPreferences("Data");
        boolean soundStatus = prefs.getBoolean("SoundStatus");

        if(soundStatus) {
            backgroundMusic.play();
        }

    }

    void checkForFirstTouch() {
        if(!firstTouch) {
            if(Gdx.input.justTouched()) {
                firstTouch = true;
                player.activatePlayer();
                createAllPipes();
            }
        }

    }

    void update(float dt) {

        checkForFirstTouch();

        if(player.getAlive()) {
            moveBackgrounds();
            moveGrounds();
            playerFlap();
            updatePipes();
            movePipes();
        }
    }

    void createAllPipes() {
        RunnableAction run = new RunnableAction();
        run.setRunnable(new Runnable() {
            @Override
            public void run() {
                //put custom code
                createPipes();
            }
        });

        SequenceAction sa = new SequenceAction();
        sa.addAction(Actions.delay(GameInfo.PIPES_SPAWN_DELAY));
        sa.addAction(run);

        hud.getStage().addAction(Actions.forever(sa));
    }

    void playerFlap(){
        if(Gdx.input.justTouched()) {
            Preferences prefs = Gdx.app.getPreferences("Data");
            boolean soundStatus = prefs.getBoolean("SoundStatus");
            if(soundStatus) {
                playerFlapSound.play();
            }
            player.playerFlap();
        }
    }

    void stopPlayer() {
        player.stopPlayer();
    }

    void createBackgrounds() {
        for (int i = 0; i < 3; i++) {
            Sprite background = new Sprite(new Texture("background_01.jpg"));
            background.setPosition(i * background.getWidth(), 0);
            backgrounds.add(background);
        }
    }

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
    }

    void moveGrounds() {
        for(Sprite ground : grounds){
            float x1 = ground.getX() - GameInfo.GROUND_SPEED;
            ground.setPosition(x1, ground.getY());

            if(ground.getX() + GameInfo.WIDTH + (ground.getWidth() / 2f ) < mainCamera.position.x){
                float x2 = ground.getX() + ground.getWidth() * backgrounds.size;
                ground.setPosition(x2, ground.getY());
            }

        }
    }

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

    void playerDied() {
        backgroundMusic.stop();
        stopPlayer();
        player.setAlive(false);
        player.playerDied();
        stopPipes();
        hud.getStage().clear();
        hud.showScore();

        Preferences prefs = Gdx.app.getPreferences("Data");
        int highScore = prefs.getInteger("Score");

        if(highScore < hud.getScore()) {
            prefs.putInteger("Score", hud.getScore());
            prefs.flush();
        }

        hud.createButtons();
        Gdx.input.setInputProcessor(hud.getStage());
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().begin();

        drawBackgrounds(game.getBatch());
        drawGrounds(game.getBatch());
        player.drawIdle(game.getBatch());
        player.animatePlayer(game.getBatch());

        //drawin the pipes
        drawPipes(game.getBatch());

        game.getBatch().end();

        // comment this to not show debugrender shape's line
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

        scoreSound.dispose();
        playerDiedSound.dispose();
        playerFlapSound.dispose();

        world.dispose();

    }

    @Override
    public void beginContact(Contact contact) {

        Preferences prefs = Gdx.app.getPreferences("Data");
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

        if(body1.getUserData() == "Player" && body2.getUserData() == "Ground") {
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
    }

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
