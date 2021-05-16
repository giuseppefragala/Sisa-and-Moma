package com.sisamoma.sam.ground;

/**
 * Created by Giuseppe on 23/02/2018.
 */

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.sisamoma.sam.helpers.GameInfo;


public class GroundBody {

    private World world;
    private Body body;

    public GroundBody(World world, Sprite ground) {
        this.world = world;
        createGroundBody(ground);
    }

    private void createGroundBody(Sprite ground) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(ground.getWidth() / GameInfo.PPM, (-ground.getHeight() / 2f - 30) / GameInfo.PPM);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(ground.getWidth() / GameInfo.PPM, ground.getHeight() / GameInfo.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GameInfo.GROUND;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(GameInfo.GROUND_USERDATA);

        shape.dispose();
    }

} // GroundBody