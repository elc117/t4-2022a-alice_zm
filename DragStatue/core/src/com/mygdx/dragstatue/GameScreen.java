package com.mygdx.dragstatue;

import java.util.Iterator;

import javax.lang.model.type.IntersectionType;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer.Random;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;

import org.w3c.dom.Text;
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
    Texture lilbadguysTexture;
    Texture bigbadguysTexture;
    Texture circleTexture;
    Texture badcircleTexture;
    Texture backgroundTexture;
    Texture towerTexture;

    Rectangle player;
    Rectangle statue;

    Array<Rectangle> lilbadguys;
    Array<Rectangle> bigbadguys;

    long lastSpawnTime;
    long lastTeleportTime;
    boolean tpOnline;

    int score = 0;
	
	public GameScreen(final DragStatue passed_game) {
        game = passed_game;

		batch = new SpriteBatch();
		playerTexture = new Texture("lilguy.png");
        statueTexture = new Texture("bigguy.png");
        connectionTexture = new Texture("connection.png");
        lilbadguysTexture = new Texture("lilbadguys.png");
        bigbadguysTexture = new Texture("bigbadguy.png");
        circleTexture = new Texture("magiccircle.png");
        badcircleTexture = new Texture("magiccirclebad.png");
        backgroundTexture = new Texture("background.png");
        towerTexture = new Texture("tower.png");

        player = new Rectangle();
        player.width = 32;
        player.height = 64;
        player.x = ((600 - player.width)*3)/4;
        player.y = (600 - player.width)/2;

        statue = new Rectangle();
        statue.width = 64;
        statue.x = ((600 - statue.width)*1)/4;
        statue.y = (600 - statue.width)/2;

        lastTeleportTime = TimeUtils.nanoTime();

        lilbadguys = new Array<Rectangle>();
        bigbadguys = new Array<Rectangle>();
        spawnBadguy();
	}

	@Override
	public void render(float delta) {
		//clear the screen, currently with a nice grassy green :)
        ScreenUtils.clear(0.5f, 0.7f, 0.3f, 1);

        //check if the statue is able to teleport
        if (TimeUtils.nanoTime() - lastTeleportTime > 1000000000)
            tpOnline = true;
        else
            tpOnline = false;

        //Vector2 connection = new Vector2(statue.x - player.x+16, statue.y - player.y);
        Circle explosion = new Circle(statue.x+(statue.width/2), statue.y+(statue.width/2), 96);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0); //draws the background

        //draws the connection line between the statue and the player
        //batch.draw(connectionTexture, statue.x+32, statue.y+32, 0, 0, connection.len(), 2, 1, 1, connection.angleDeg()+180, 0, 0, 64, 64, false, false);
        
        batch.draw(statueTexture, statue.x, statue.y); //draws the statue
        
        if (tpOnline && !(Intersector.overlaps(explosion, player))) //if the statue can teleport, draw a magic circle around it
            batch.draw(circleTexture, explosion.x-explosion.radius, explosion.y-explosion.radius);
        else if (tpOnline) //if teleport is off cooldown but the player is too close, draw the red magic circle
            batch.draw(badcircleTexture, explosion.x-explosion.radius, explosion.y-explosion.radius);

        batch.draw(playerTexture, player.x, player.y); //draws the player

        //draw all the badguys
        for (Rectangle badguy : lilbadguys) {
            batch.draw(lilbadguysTexture, badguy.x, badguy.y);
        }
        for (Rectangle badguy : bigbadguys) {
            batch.draw(bigbadguysTexture, badguy.x, badguy.y);
        }

        batch.draw(towerTexture, 0, 0); //draws the tower in the foreground
        game.font.draw(batch, "Score: " + score, 10, 590);
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

        //move statue to player if space is pressed and has been over 1 second since last teleport and the player is not to close
        if (tpOnline && !(Intersector.overlaps(explosion, player)) && Gdx.input.isKeyPressed(Keys.SPACE)){
            //check every enemy to see if they are in the explosion circle, if so remove them
            Iterator<Rectangle> iter = lilbadguys.iterator();
            while(iter.hasNext()){
                Rectangle badguy = iter.next();
                if (Intersector.overlaps(explosion, badguy)){
                    iter.remove();
                    score++;
                }   
            }
            iter = bigbadguys.iterator();
            while(iter.hasNext()){
                Rectangle badguy = iter.next();
                if (Intersector.overlaps(explosion, badguy)){
                    iter.remove();
                    score++;
                }   
            }
            //move the statue to the player
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
        for (Rectangle badguy : lilbadguys) {
            //end the game if any small enemy is touching the player
            if (player.overlaps(badguy)){
                gameOver();
            }
            //move each small enemy towards the player
            float badguyAngle = (float) Math.atan2(player.y - badguy.y, player.x - badguy.x);
            badguy.y += Math.sin(badguyAngle)*50*Gdx.graphics.getDeltaTime();
            badguy.x += Math.cos(badguyAngle)*50*Gdx.graphics.getDeltaTime();
        }
        for (Rectangle badguy : bigbadguys) {
            //end the game if any big enemy is touching the statue
            if (statue.overlaps(badguy)){
                gameOver();
            }
            //move each big enemy towards the statue
            float badguyAngle = (float) Math.atan2(statue.y - badguy.y, statue.x - badguy.x);
            badguy.y += Math.sin(badguyAngle)*50*Gdx.graphics.getDeltaTime();
            badguy.x += Math.cos(badguyAngle)*50*Gdx.graphics.getDeltaTime();
        }

        //check for enemy spawn
        if (TimeUtils.nanoTime() - lastSpawnTime > 1000000000)
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
        if (MathUtils.random(0, 6) == 1 && bigbadguys.size <= 2){
            bigbadguys.add(badguy);
            lastSpawnTime = TimeUtils.nanoTime();
        }
        else if (lilbadguys.size <= 10){
            lilbadguys.add(badguy);
            lastSpawnTime = TimeUtils.nanoTime();
        }
    }

    private void gameOver(){
        game.setScreen(new GameOverScreen(game,score));
        dispose();
    }

	@Override
	public void dispose() {
		playerTexture.dispose();
        statueTexture.dispose();
        connectionTexture.dispose();
        lilbadguysTexture.dispose();
        bigbadguysTexture.dispose();
        circleTexture.dispose();
        badcircleTexture.dispose();
        backgroundTexture.dispose();
        towerTexture.dispose();
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