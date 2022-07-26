package com.mygdx.dragstatue;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Array;

import org.w3c.dom.css.Rect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;

public class GameScreen implements Screen {
	final DragStatue game;

    SpriteBatch batch;
	Texture playerTexture;
    Texture statueTexture;
    Texture connectionTexture;
    Texture badguysTexture;

    Rectangle player;
    Rectangle statue;

    float connectionLength;
    float connectionAngle;

    Array<Rectangle> badguys;

    long lastSpawnTime;
    long lastTeleportTime;
	
	public GameScreen(final DragStatue passed_game) {
        game = passed_game;

		batch = new SpriteBatch();
		playerTexture = new Texture("lilguy.png");
        statueTexture = new Texture("bigguy.png");
        connectionTexture = new Texture("connection.png");
        badguysTexture = new Texture("lilbadguys.png");

        player = new Rectangle();
        player.width = 64;
        player.x = ((600 - player.width)*3)/4;
        player.y = (600 - player.width)/2;

        statue = new Rectangle();
        statue.width = 64;
        statue.x = ((600 - statue.width)*1)/4;
        statue.y = (600 - statue.width)/2;

        lastTeleportTime = TimeUtils.nanoTime();

        badguys = new Array<Rectangle>();
        spawnBadguy();
	}

	@Override
	public void render(float delta) {
		//clear the screen, currently with a nice grassy green :)
        ScreenUtils.clear(0.5f, 0.7f, 0.3f, 1);

        connectionLength = (float) Math.sqrt(Math.pow(statue.x - player.x, 2) + Math.pow(statue.y - player.y, 2)); //distance between statue and player
        if (statue.x - player.x > 0)
            connectionLength = -connectionLength;
        connectionAngle = (float) Math.toDegrees(Math.asin((player.y - statue.y)/connectionLength)); //angle between statue and player

        batch.begin();
        //draws the connection line between the statue and the player
        batch.draw(connectionTexture, statue.x+32, statue.y+32, 0, 0, connectionLength, 2, 1, 1, connectionAngle, 0, 0, 64, 64, false, false);
		batch.draw(playerTexture, player.x, player.y); //draws the player
        batch.draw(statueTexture, statue.x, statue.y); //draws the statue
        for (Rectangle badguy : badguys) {
            batch.draw(badguysTexture, badguy.x, badguy.y);
        }
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

        //update all the enemies
        for (Rectangle badguy : badguys) {
            float badguyAngle = (float) Math.atan2(player.y - badguy.y, player.x - badguy.x);
            badguy.y += Math.sin(badguyAngle)*50*Gdx.graphics.getDeltaTime();
            badguy.x += Math.cos(badguyAngle)*50*Gdx.graphics.getDeltaTime();
            if (badguy.x < 0 || badguy.x > 600)
                badguy.x = 300;
            if (badguy.y < 0 || badguy.y > 600)
                badguy.y = 300;
        }

        //check for enemy spawn
        if (TimeUtils.nanoTime() - lastSpawnTime > 1000000000 && badguys.size <= 10)
            spawnBadguy();
	}
	
    private void spawnBadguy() {
        Rectangle badguy = new Rectangle();
        badguy.width = 64;
        badguy.height = 64;

        int spawnCoord = MathUtils.random(0, 600+600);
        if (spawnCoord < 600){
            badguy.x = spawnCoord;
            badguy.y = 600;
        }
        else {
            badguy.x = 600;
            badguy.y = spawnCoord - 600;
        }

        badguys.add(badguy);
        lastSpawnTime = TimeUtils.nanoTime();
    }

	@Override
	public void dispose() {
		playerTexture.dispose();
        statueTexture.dispose();
        connectionTexture.dispose();
        badguysTexture.dispose();
        batch.dispose();
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
