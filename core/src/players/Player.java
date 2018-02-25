package players;

/**
 * Created by Giuseppe on 23/02/2018.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import helpers.GameInfo;
import helpers.GameManager;

public class Player extends Sprite {
    private World world;
    private Body body;
    private boolean isAlive;
    private Texture playerDead;
    private Animation<TextureRegion> animation;
    private float elapsedTime;
    private String playerName;
    private String playerKoName;
    private String playerAtlasName;

    public Player(World world, float x, float y){
        // In the play screen, when starting, shows the choosen player from the menu screen
        super(new Texture(GameManager.getInstance().getPlayer()));

        playerName = GameManager.getInstance().getPlayer();
        playerKoName = playerName.substring(0, playerName.length() - 4) + "Ko.png";
        // Set the picture for the player when get down
        playerDead = new Texture(playerKoName);

        this.world = world;
        setPosition(x, y);
        createBody();
        createAnimation();
    }

    void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(getX() / GameInfo.PPM, (getY()  ) / GameInfo.PPM);

        body = world.createBody(bodyDef);
        body.setFixedRotation(true);

        Vector2[] vertexArray = new Vector2[6];

        vertexArray[0] = new Vector2(-(0.9f * 1/2f) * getWidth() / GameInfo.PPM, (0.9f * 1/2f) * getHeight()/ GameInfo.PPM); //A
        vertexArray[1] = new Vector2((0.9f * 1/4f) * getWidth() / GameInfo.PPM, (0.9f * 1/2f) * getHeight() / GameInfo.PPM); //B
        vertexArray[2] = new Vector2((0.9f * 1/2f) * getWidth() / GameInfo.PPM,(0.9f * 1/6f) * getHeight() / GameInfo.PPM); //C
        vertexArray[3] = new Vector2((0.9f * 1/2f) * getWidth() / GameInfo.PPM, -(0.9f * 1/6f) * getHeight() / GameInfo.PPM); //D
        vertexArray[4] = new Vector2((0.9f * 1/4f) * getWidth() / GameInfo.PPM, -(0.9f * 1/2f) * getHeight()/ GameInfo.PPM); //E
        vertexArray[5] = new Vector2(-(0.9f * 1/2f) * getWidth() / GameInfo.PPM, -(0.9f * 1/2f) * getHeight()/ GameInfo.PPM); //F

        //CircleShape shape = new CircleShape();
        //shape.setRadius((getHeight() / 2f) / GameInfo.PPM);

        PolygonShape shape = new PolygonShape();
        shape.set(vertexArray);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.filter.categoryBits = GameInfo.PLAYER;
        fixtureDef.filter.maskBits = GameInfo.GROUND | GameInfo.PIPE | GameInfo.SCORE;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(GameInfo.PLAYER_USERDATA);

        shape.dispose();
        body.setActive(false);
    }

    public void activatePlayer() {
        isAlive = true;
        body.setActive(true);
    }

    public void playerSwim() {
        body.setLinearVelocity(GameInfo.PLAYER_LINEAR_VELOCITY_X, GameInfo.PLAYER_LINEAR_VELOCITY_Y);
    }

    public void stopPlayer() {
        body.setLinearVelocity(0f, 0f);
    }

    public void drawIdle(SpriteBatch batch) {
        if(!isAlive) {
            batch.draw(this, getX() - getWidth() / 2f, getY() - getHeight() / 2f);
        }
    }

    public void animatePlayer(SpriteBatch batch) {
        if(isAlive) {
            elapsedTime += Gdx.graphics.getDeltaTime();
            batch.draw(animation.getKeyFrame(elapsedTime,true), getX() - (getWidth() / 2f), getY() - (getHeight() / 2f) );
        }
    }

    public void updatePlayer() {
        setPosition(body.getPosition().x * GameInfo.PPM, body.getPosition().y * GameInfo.PPM);
    }

    void createAnimation() {

        // Select the atlase of the choosen player
        playerAtlasName = playerName.substring(0, playerName.length() - 4) + ".atlas";
        TextureAtlas playerAtlas = new TextureAtlas(playerAtlasName);

        // Create the animation of the player based on its atlas
        animation = new Animation<TextureRegion>(1f/5f, playerAtlas.getRegions()); // 1f/7f = 7 frame al secondo
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public boolean getAlive() {
        return isAlive;
    }

    public void playerDied() {
        this.setTexture(playerDead);
    }
}
