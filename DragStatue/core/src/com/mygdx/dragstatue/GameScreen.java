package com.mygdx.dragstatue;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Region;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import org.w3c.dom.css.Rect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;

public class GameScreen implements Screen {
	final DragStatue game;

    SpriteBatch batch;
	Texture playerTexture;
    Texture statueTexture;

    Rectangle player;
    Rectangle statue;

    long lastTeleportTime;
	
	public GameScreen(final DragStatue passed_game) {
        game = passed_game;

		batch = new SpriteBatch();
		playerTexture = new Texture("lilguy.png");
        statueTexture = new Texture("bigguy.png");

        player = new Rectangle();
        player.width = 64;
        player.x = ((600 - player.width)*3)/4;
        player.y = (600 - player.width)/2;

        statue = new Rectangle();
        statue.width = 64;
        statue.x = ((600 - statue.width)*1)/4;
        statue.y = (600 - statue.width)/2;

        lastTeleportTime = TimeUtils.nanoTime();
	}

	@Override
	public void render(float delta) {
		//clear the screen, currently with a nice grassy green :)
        ScreenUtils.clear(0.5f, 0.7f, 0.3f, 1);
		
        batch.begin();
		batch.draw(playerTexture, player.x, player.y);
        batch.draw(statueTexture, statue.x, statue.y);
		batch.end();

        //receive input to move player
        if (Gdx.input.isKeyPressed(Keys.LEFT)) 
            player.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) 
            player.x += 200 * Gdx.graphics.getDeltaTime();
            if (Gdx.input.isKeyPressed(Keys.DOWN)) 
            player.y -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.UP)) 
            player.y += 200 * Gdx.graphics.getDeltaTime();
        //move statue to player if space is pressed and has been over 1 second since last teleport
        if (Gdx.input.isKeyPressed(Keys.SPACE) && (TimeUtils.nanoTime() - lastTeleportTime > 1000000000) ){
            statue.x = player.x;
            statue.y = player.y;
            lastTeleportTime = TimeUtils.nanoTime();
        }

        //keep player in bounds
        if (player.x < 0) 
            player.x = 0;
        if (player.x > 600 - player.width) 
            player.x = 600 - player.width;
        if (player.y < 0) 
            player.y = 0;
        if (player.y > 600 - player.width) 
            player.y = 600 - player.width;
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		playerTexture.dispose();
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
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}