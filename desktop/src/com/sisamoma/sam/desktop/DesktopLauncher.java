package com.sisamoma.sam.desktop;


import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.sisamoma.sam.GameMain;

import com.sisamoma.sam.helpers.GameInfo;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Pino's Best Game Ever";
		//config.useGL30 = true;

		config.width = GameInfo.WIDTH;
		config.height = GameInfo.HIGTH;
		//config.fullscreen=true;

		new LwjglApplication(new GameMain(), config);
	}
}
