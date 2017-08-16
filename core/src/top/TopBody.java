package top;


import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import helpers.GameInfo;

/**
 * Created by Giuseppe on 16/08/2017.
 */

public class TopBody {

    private World world;
    private Body body;

    public TopBody(World world, Sprite ground) {
        this.world = world;
        createTopBody(ground);
    }

    void createTopBody(Sprite top) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(top.getWidth() / GameInfo.PPM, (GameInfo.HIGHT + 10f) / GameInfo.PPM);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(top.getWidth() / GameInfo.PPM, -5f / GameInfo.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GameInfo.TOP;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(GameInfo.TOP_USERDATA);

        shape.dispose();
    }

}
