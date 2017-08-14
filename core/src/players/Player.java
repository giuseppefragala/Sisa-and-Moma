package players;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import helpers.GameInfo;
import helpers.GameManager;

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
        super(new Texture("Hulk_" + GameManager.getInstance().getPlayer() + ".png"));
        playerDead = new Texture("Hulk_Dead.png");

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

        CircleShape shape = new CircleShape();
        shape.setRadius((getHeight() / 2f) / GameInfo.PPM);

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
        TextureAtlas playerAtlas = new TextureAtlas("Hulk_" + GameManager.getInstance().getPlayer() + ".atlas");
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