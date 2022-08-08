package com.mygdx.dragstatue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenuScreen implements Screen {
	final DragStatue game;
	static private int WIDTH = 600;
	static private int HEIGHT = 600;
	
	OrthographicCamera camera;

	SpriteBatch batch;
	Texture mainMenuTexture;
	
	public MainMenuScreen(final DragStatue passed_game) {
		game = passed_game;
		
		batch = new SpriteBatch();
		mainMenuTexture = new Texture("mainmenu.png");

		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		
		batch.begin();
			batch.draw(mainMenuTexture, 0, 0);
		batch.end();

		game.batch.begin();
		game.font.draw(game.batch, "Click anywhere to begin!", 210, 300);
		game.batch.end();
		
		// If player activates the game, dispose of this menu.
		if (Gdx.input.isTouched()) {
			game.setScreen(new GameScreen(game));
			dispose();
		}
	}


	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		mainMenuTexture.dispose();
		batch.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}
}