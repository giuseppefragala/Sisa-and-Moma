package com.sisamoma.sam;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sisamoma.sam.scenes.MainMenu;

/**
 * Created by Giuseppe on 11/08/2017.
 */

public class GameMain extends Game {
	private SpriteBatch batch;



	@Override
	public void create() {
		batch = new SpriteBatch();
		setScreen(new MainMenu(this));
	}

	@Override
	public void render() {
		super.render();
	}

	public SpriteBatch getBatch() {
		return this.batch;
	}
}
