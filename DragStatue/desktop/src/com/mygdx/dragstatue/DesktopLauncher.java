package com.mygdx.dragstatue;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.dragstatue.DragStatue;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Drag Statue");
		config.setWindowedMode(600, 600);
		config.useVsync(true);
		config.setForegroundFPS(60);
		new Lwjgl3Application(new DragStatue(), config);
	}
}
