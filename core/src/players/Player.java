package players;


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
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;


import com.badlogic.gdx.physics.box2d.Shape;

import helpers.GameInfo;
import helpers.GameManager;

import static com.badlogic.gdx.Gdx.app;

/**
 * Created by Giuseppe on 11/08/2017.
 */

public class Player extends Sprite {
    private World world;
    private Body body;
    private boolean isAlive;

    private Texture playerDead;

    private Animation<TextureRegion> animation;
    private float elapsedTime;


    public Player(World world, float x, float y){
        super(new Texture(GameManager.getInstance().getPlayer()));
        //super(new Texture("fish1.png"));

        playerDead = new Texture("fish_dead.png");

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
        body.setFixedRotation(false);

        //Array<Vector2> vertexArray = new Array<Vector2>();

        Vector2[] vertexArray = new Vector2[6];

        vertexArray[0] = new Vector2(-(1/2f) * getWidth() / GameInfo.PPM, (1/2f) * getHeight()/ GameInfo.PPM); //A
        vertexArray[1] = new Vector2((1/4f) * getWidth() / GameInfo.PPM, (1/2f) * getHeight() / GameInfo.PPM); //B
        vertexArray[2] = new Vector2((1/2f) * getWidth() / GameInfo.PPM,(1/6f) * getHeight() / GameInfo.PPM); //C
        vertexArray[3] = new Vector2((1/2f) * getWidth() / GameInfo.PPM, -(1/6f) * getHeight() / GameInfo.PPM); //D
        vertexArray[4] = new Vector2((1/4f) * getWidth() / GameInfo.PPM, -(1/2f) * getHeight()/ GameInfo.PPM); //E
        vertexArray[5] = new Vector2(-(1/2f) * getWidth() / GameInfo.PPM, -(1/2f) * getHeight()/ GameInfo.PPM); //F

        vertexArray[0].x = 0.9f * vertexArray[0].x;
        vertexArray[1].x = 0.9f * vertexArray[1].x;
        vertexArray[2].x = 0.9f * vertexArray[2].x;
        vertexArray[3].x = 0.9f * vertexArray[3].x;
        vertexArray[4].x = 0.9f * vertexArray[4].x;
        vertexArray[5].x = 0.9f * vertexArray[5].x;

        vertexArray[0].y = 0.9f * vertexArray[0].y;
        vertexArray[1].y = 0.9f * vertexArray[1].y;
        vertexArray[2].y = 0.9f * vertexArray[2].y;
        vertexArray[3].y = 0.9f * vertexArray[3].y;
        vertexArray[4].y = 0.9f * vertexArray[4].y;
        vertexArray[5].y = 0.9f * vertexArray[5].y;










        //CircleShape shape = new CircleShape();
        //shape.setRadius((getHeight() / 2f) / GameInfo.PPM);

        app.log("Numero vertici vertexArray: ","" + vertexArray.length);
        PolygonShape shape = new PolygonShape();
        shape.set(vertexArray);
        app.log("Numero vertici shape: ","" + shape.getVertexCount());




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

    public void playerFlap() {
        body.setLinearVelocity(GameInfo.PLAYER_LINEAR_VELOCITY_X, GameInfo.PLAYER_LINEAR_VELOCITY_Y);
    }

    public void stopPlayer() {
        body.setLinearVelocity(-2f, 0f);
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
        //TextureAtlas playerAtlas = new TextureAtlas("Hulk_" + GameManager.getInstance().getPlayer() + ".atlas");
        TextureAtlas playerAtlas = new TextureAtlas("fish.atlas");
        animation = new Animation<TextureRegion>(1f/6f, playerAtlas.getRegions()); // 1f/7f = 7 frame al secondo
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