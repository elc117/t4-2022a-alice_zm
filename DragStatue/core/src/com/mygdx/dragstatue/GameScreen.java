package com.mygdx.dragstatue;

import java.util.Iterator;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class GameScreen implements Screen {
	final DragStatue game;

    SpriteBatch batch;
	Texture playerTexture;
    Texture statueTexture;
    Texture lilbadguysTexture;
    Texture bigbadguysTexture;
    Texture circleTexture;
    Texture badcircleTexture;
    Texture backgroundTexture;
    Texture towerTexture;
    Sound tpSound;
    Music gameMusic;

    Rectangle player;
    Rectangle statue;

    Circle tpRadius;

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
        lilbadguysTexture = new Texture("lilbadguys.png");
        bigbadguysTexture = new Texture("bigbadguy.png");
        circleTexture = new Texture("magiccircle.png");
        badcircleTexture = new Texture("magiccirclebad.png");
        backgroundTexture = new Texture("background.png");
        towerTexture = new Texture("tower.png");
        tpSound = Gdx.audio.newSound(Gdx.files.internal("impact.mp3"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("battle.mp3"));

        gameMusic.setLooping(true);
        gameMusic.play();

        player = new Rectangle();
        player.width = 32;
        player.height = 64;
        player.x = ((600 - player.width)*3)/4;
        player.y = (600 - player.width)/2;

        statue = new Rectangle();
        statue.width = 64;
        statue.height = 64;
        statue.x = ((600 - statue.width)*1)/4;
        statue.y = (600 - statue.width)/2;

        tpRadius = new Circle(statue.x+(statue.width/2), statue.y+(statue.width/2), 96);

        lastTeleportTime = TimeUtils.nanoTime();

        lilbadguys = new Array<Rectangle>();
        bigbadguys = new Array<Rectangle>();
        spawnBadguy();
	}

	@Override
	public void render(float delta) {
		//clear the screen, currently with a nice grassy green :)
        //not technically necessary given we'll render a background texture over it later, but it's good to help cover any gaps
        ScreenUtils.clear(0.5f, 0.7f, 0.3f, 1);

        //check if the statue is able to teleport
        if (TimeUtils.nanoTime() - lastTeleportTime > 1000000000)
            tpOnline = true;
        else
            tpOnline = false;
        

        batch.begin();
        batch.draw(backgroundTexture, 0, 0); //draws the background

        batch.draw(statueTexture, statue.x, statue.y); //draws the statue

        if (tpOnline && !(Intersector.overlaps(tpRadius, player))) //if the statue can teleport, draw a magic circle around it
            batch.draw(circleTexture, tpRadius.x-tpRadius.radius, tpRadius.y-tpRadius.radius);
        else if (tpOnline) //if teleport is off cooldown but the player is too close, draw the red magic circle
            batch.draw(badcircleTexture, tpRadius.x-tpRadius.radius, tpRadius.y-tpRadius.radius);
        
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

        //update the player (move, keep in bounds)
        updatePlayer();

        //if space bar is pressed, try to teleport
        if (Gdx.input.isKeyPressed(Keys.SPACE))
            teleportStatue();

        //update all the enemies
        updateBadguys(lilbadguys, player);
        updateBadguys(bigbadguys, statue);

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
    

    private void updatePlayer(){
        //receive input to move player
        if (Gdx.input.isKeyPressed(Keys.LEFT)) 
            player.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) 
            player.x += 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.DOWN)) 
            player.y -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.UP)) 
            player.y += 200 * Gdx.graphics.getDeltaTime();     

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

    private void updateBadguys(Array<Rectangle> badguys, Rectangle target){
        for (Rectangle badguy : badguys) {
            //end the game if any enemy is touching the center of their target
            if (badguy.contains(target.x+target.width/2, target.y+target.height/2)){
                gameOver();
            }
            //move each enemy towards their target
            float badguyAngle = (float) Math.atan2(target.y - badguy.y, target.x - badguy.x);
            badguy.y += Math.sin(badguyAngle)*50*Gdx.graphics.getDeltaTime();
            badguy.x += Math.cos(badguyAngle)*50*Gdx.graphics.getDeltaTime();
        }
    }

    private void teleportStatue(){
        //if teleport is on cooldown or the player is in the teleport radius, the teleport fails
        if(!tpOnline || Intersector.overlaps(tpRadius, player))
            return;
        
            //check every enemy to see if they are in the tpRadius circle, if so remove them
        Iterator<Rectangle> iter = lilbadguys.iterator();
        while(iter.hasNext()){
            Rectangle badguy = iter.next();
            if (Intersector.overlaps(tpRadius, badguy)){
                iter.remove();
                score++;
            }   
        }
        iter = bigbadguys.iterator();
        while(iter.hasNext()){
            Rectangle badguy = iter.next();
            if (Intersector.overlaps(tpRadius, badguy)){
                iter.remove();
                score++;
            }   
        }
        //move the statue to the player, and the tpradius around it
        statue.x = player.x;
        statue.y = player.y;
        tpRadius.setPosition(statue.x+(statue.width/2), statue.y+(statue.width/2));
        
        //plays the teleport sound effect
        tpSound.play(); 

        //resets lastTeleportTime
        lastTeleportTime = TimeUtils.nanoTime();   
    }

    private void gameOver(){
        game.setScreen(new GameOverScreen(game,score));
        dispose();
    }

	@Override
	public void dispose() {
		playerTexture.dispose();
        statueTexture.dispose();
        lilbadguysTexture.dispose();
        bigbadguysTexture.dispose();
        circleTexture.dispose();
        badcircleTexture.dispose();
        backgroundTexture.dispose();
        towerTexture.dispose();
        batch.dispose();
        tpSound.dispose();
        gameMusic.dispose();
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