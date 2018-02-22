package coins;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

import static com.badlogic.gdx.Gdx.app;
import static com.badlogic.gdx.math.MathUtils.random;


/**
 * Created by Giuseppe on 16/08/2017.
 */

public class Coins extends Sprite {

    private World world;
    private Body body;

    private Animation<TextureRegion> animation;
    private float elapsedTime;

    private OrthographicCamera mainCamera;

    public Coins(World world, float x){
        //super(new Texture("coin1.png"));
        this.world = world;
        createCoin(x, GameManager.getInstance().getRandomY());
    }

    void createCoin(float x, float y) {

        createAnimation();

        setPosition(x, y );

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(getX() / GameInfo.PPM, (getY()  ) / GameInfo.PPM);

        body = world.createBody(bodyDef);
        body.setFixedRotation(false);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((this.getWidth() / 2.5f) / GameInfo.PPM, (this.getHeight() / 2.0f) / GameInfo.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.filter.categoryBits = GameInfo.COIN;
        fixtureDef.isSensor = true;
        //fixtureDef.filter.maskBits = GameInfo.GROUND | GameInfo.PIPE | GameInfo.SCORE;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(GameInfo.COIN_USERDATA);

        shape.dispose();
        //body.setActive(false);
    }

    public void animateCoin(SpriteBatch batch) {
        //if(isAlive) {
            elapsedTime += Gdx.graphics.getDeltaTime();
            batch.draw(animation.getKeyFrame(elapsedTime,true), getX() - (getWidth() / 2f), getY() - (getHeight() / 2f) );
        //}
    }

    void createAnimation() {
        TextureAtlas coinAtlas = new TextureAtlas("coins.atlas");
        animation = new Animation<TextureRegion>(1f/12f, coinAtlas.getRegions()); // 1f/7f = 7 frame al secondo
    }

    public void updateCoin (){
        setPosition(body.getPosition().x * GameInfo.PPM, body.getPosition().y * GameInfo.PPM);
    }

    public void moveCoin() {
        body.setLinearVelocity(-1, 0);
        if(this.getX() + (GameInfo.WIDTH / 2) + 160 < mainCamera.position.x){
            body.setActive(false);
        }
    }

    public void setMainCamera(OrthographicCamera mainCamera) {
        this.mainCamera = mainCamera;
    }

    public void stopCoin() {
        body.setLinearVelocity(0, 0);
    }

    public void disposeAll() {
        this.getTexture().dispose();
        world.dispose();
    }


    // Sostituito da GameManager.getInstance().getRandomY();
/*    float getRandomY() {
        float max = (GameInfo.HIGHT)/ 2f + 20;
        float min = (GameInfo.HIGHT)/ 2f - 20;
        return random.nextFloat() * (max - min) + min;
    }*/


} //Coins



