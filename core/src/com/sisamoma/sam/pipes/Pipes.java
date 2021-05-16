package com.sisamoma.sam.pipes;

/**
 * Created by Giuseppe on 23/02/2018.
 */


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.sisamoma.sam.helpers.GameInfo;
import com.sisamoma.sam.helpers.GameManager;

import java.util.Random;


public class Pipes {
    private World world;
    private Body body1, body2, body3;

    private Sprite pipe1, pipe2;

    private Random random = new Random();

    private OrthographicCamera mainCamera;

    private float randomY;
    private boolean isSensor;

    public Pipes(World world, float x, boolean isSensor) {
        this.world = world;
        this.isSensor = isSensor;
        randomY = getRandomY();
        GameManager.getInstance().setRandomY(randomY);

        createPipes(x, randomY);
        //Gdx.app.log("Check pipes", "Creato Pipes a x: " + x + " - y: " + randomY);
    }

    private void createPipes(float x, float y) {

        pipe1 = new Sprite(new Texture("stone1.png"));
        pipe2 = new Sprite(new Texture("stone2.png"));

        pipe1.setPosition(x, y + GameInfo.DISTANCE_BETWEEN_PIPES);
        pipe2.setPosition(x, y - GameInfo.DISTANCE_BETWEEN_PIPES);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;

        // creating body for pipe1
        bodyDef.position.set(pipe1.getX() / GameInfo.PPM, pipe1.getY() / GameInfo.PPM);
        body1 = world.createBody(bodyDef);
        body1.setFixedRotation(false);

        // creating body for pipe2
        bodyDef.position.set(pipe2.getX() / GameInfo.PPM, pipe2.getY() / GameInfo.PPM);
        body2 = world.createBody(bodyDef);
        body2.setFixedRotation(false);

        //create body for score
        bodyDef.position.set(pipe1.getX() / GameInfo.PPM, y / GameInfo.PPM);
        body3 = world.createBody(bodyDef);
        body3.setFixedRotation(false);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((pipe1.getWidth() / 2.5f) / GameInfo.PPM, (pipe1.getHeight() / 2.0f) / GameInfo.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GameInfo.PIPE;
        fixtureDef.isSensor = isSensor;
        //Gdx.app.log("FixtureDef_Pipes", "isSensor: " + isSensor);

        Fixture fixture1 = body1.createFixture(fixtureDef);
        fixture1.setUserData(GameInfo.PIPE_USERDATA);

        Fixture fixture2 = body2.createFixture(fixtureDef);
        fixture2.setUserData(GameInfo.PIPE_USERDATA);

        shape.setAsBox(GameInfo.SCORE_WIDTH / GameInfo.PPM, pipe1.getHeight() / 2f / GameInfo.PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GameInfo.SCORE;
        fixtureDef.isSensor = true;

        Fixture fixture3 = body3.createFixture(fixtureDef);
        fixture3.setUserData(GameInfo.SCORE_USERDATA);

        shape.dispose();
    }

    public void drawPipes(SpriteBatch batch) {
        batch.draw(pipe1, pipe1.getX() - pipe1.getWidth() / 2f, pipe1.getY() - pipe1.getHeight() / 2f);
        batch.draw(pipe2, pipe2.getX() - pipe2.getWidth() / 2f, pipe2.getY() - pipe2.getHeight() / 2f);

        //Gdx.app.log("Check pipes", "Draw pipe1 a x: " + pipe1.getX() + " - y: " + pipe1.getY());
        //Gdx.app.log("Check pipes", "Draw pipe2 a x: " + pipe2.getX() + " - y: " + pipe2.getY());
    }

    public void updatePipes() {
        pipe1.setPosition(body1.getPosition().x * GameInfo.PPM, body1.getPosition().y * GameInfo.PPM);
        pipe2.setPosition(body2.getPosition().x * GameInfo.PPM, body2.getPosition().y * GameInfo.PPM);

        //Gdx.app.log("Check pipes", "Update pipe1 a x: " + pipe1.getX() + " - y: " + pipe1.getY());
        //Gdx.app.log("Check pipes", "Update pipe2 a x: " + pipe2.getX() + " - y: " + pipe2.getY());
    }

    public void movePipes() {
        body1.setLinearVelocity(-1f, 0);
        body2.setLinearVelocity(-1f, 0);
        body3.setLinearVelocity(-1f, 0);
        if (pipe1.getX() + (GameInfo.WIDTH / 2f) + 160f < mainCamera.position.x) {
            body1.setActive(false);
            body2.setActive(false);
            body3.setActive(false);
        }
    }

    public void stopPipes() {
        body1.setLinearVelocity(0, 0);
        body2.setLinearVelocity(0, 0);
        body3.setLinearVelocity(0, 0);
    }

    public void setMainCamera(OrthographicCamera mainCamera) {
        this.mainCamera = mainCamera;
    }

    private float getRandomY() {
        float max = (GameInfo.HIGTH) / 2f + 100f;
        float min = (GameInfo.HIGTH) / 2f - 100f;
        return random.nextFloat() * (max - min) + min;
    }

    public void disposeAll() {
        pipe1.getTexture().dispose();
        pipe2.getTexture().dispose();
        world.dispose();
    }

} // Pipes
