import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.MusicListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

public class Week9 extends BasicGame implements MusicListener {
	
	Image squirrel;
	Image acorn;
	Image asteroid;
	Random rng = new Random();
	
	float teaAngle = (float) Math.toRadians(35);
	
	
	public static Graphics GRAPHICS;
	public static GameContainer GAME_CONTAINER;
	
	List<Bullet> asteroids = new ArrayList<Bullet>();
	List<Bullet> acorns = new ArrayList<Bullet>();
//	List<Actor> acorns_free = new ArrayList<Actor>();
	
	HashMap<String, Image> drawsames = new HashMap<String, Image>();

	Vector2f playerpos = new Vector2f(350,350);
	
	Vector2f playerVelocity = new Vector2f(0,0);
	
	float playerangle = 0.0f;
	float spawntimer = 0.0f;
	float shootcd = 0.0f;
	
	public class Actor {
		Rectangle dims;
		float rotation;
		public Actor(int x, int y, int w, int h, float rotation) {
			this.rotation = rotation;
			dims = new Rectangle(x, y, w, h);
		}
	}
	
	public class Bullet {
		public float rotation;
		public Rectangle dims;
		public float speed;
		public float life; 
		public Bullet(int x, int y, int w, int h, float speed, float life, float rotation) {
			this.rotation = rotation;
			dims = new Rectangle(x, y, w, h);
			this.speed = speed;
			this.life = life;
		}
		
		public void moveInDirection(float dtms) {
			dims.setX((float) (dims.getX() + Math.cos(rotation) * speed * dtms));
			dims.setY((float) (dims.getY() + Math.sin(rotation) * speed * dtms));
		}
		
	}
	
	public Week9(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new Week9("Acorns & Asteroids"));
		app.setDisplayMode(800, 800, false);
		app.start();
		app.setShowFPS(false);
	}
	
	boolean playeralive = true;
	
	public void updateObstacles() {
		
	}
	
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		// TODO Auto-generated method stub
		for(int i = 0; i < asteroids.size(); i++) {
			Bullet cjt = asteroids.get(i);
			g.pushTransform();
			g.translate(cjt.dims.getX(), asteroids.get(i).dims.getY());
			asteroid.draw();
			g.popTransform();
//			cjt.dims.setX((float) (cjt.dims.getX() + 0.05*(Math.cos(teaAngle))));
//			cjt.dims.y += 0.05*(Math.sin(teaAngle));
		}
		if(playeralive) {
			if(acorns.size() > 0) {
				for(int i = 0; i < acorns.size(); i++) {
					Bullet bullet = acorns.get(i);
					g.pushTransform();
					g.translate(bullet.dims.getX(), bullet.dims.getY());
					acorn.rotate(bullet.rotation);
					acorn.draw();
					
					g.popTransform();
				}
			}
			g.pushTransform();
			g.translate(playerpos.x, playerpos.y);
			squirrel.draw();
			g.popTransform();
		} else {
			g.drawString("You lose! Press R to restart!", 20, 400);
		}
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		GAME_CONTAINER = gc;
		GRAPHICS = gc.getGraphics();
		// TODO Auto-generated method stub
		squirrel = new Image("data/squirrel.png");
		squirrel = squirrel.getScaledCopy(25, 40);
		acorn = new Image("data/acorn.png");
		acorn = acorn.getScaledCopy(15, 15);
		asteroid = new Image("data/asteroid.png");
		asteroid = asteroid.getScaledCopy(70, 70);
		for(int i = 0; i < 15; i++) {
			Vector2f randomLoc = getRandomScreenLocation(asteroid.getWidth(), asteroid.getHeight());
			asteroids.add(new Bullet((int)randomLoc.x,(int)randomLoc.y,asteroid.getWidth(), asteroid.getHeight(), 10.0f, -1.0f, rng.nextInt(360)));
		}
		Music BGM = new Music("data/music.ogg");
		BGM.addListener(this);
		BGM.play();
		BGM.loop();
		
	}
	
	public Vector2f getRandomScreenLocation() {
		return getRandomScreenLocation(0,0);
	}
	public Vector2f getRandomScreenLocation(int width, int height) {
		return new Vector2f(rng.nextInt(GAME_CONTAINER.getScreenWidth() - width) + width, rng.nextInt(GAME_CONTAINER.getScreenHeight() - height) + height);
	}
	
	public boolean inScreenBounds(int x, int y, int w, int h) {
		if(x+w < 0 || x-w > GAME_CONTAINER.getScreenWidth() || y+h < 0 || y-h < GAME_CONTAINER.getScreenHeight()) {
			return true;
		}
		return false;
	}
	
	public Vector2f getActualVelocity(Vector2f position, Vector2f deltaV, Vector2f dims) {
		if(deltaV.x + position.x < 0 || deltaV.x + position.x + dims.x > GAME_CONTAINER.getWidth())
			deltaV.x *= -1;
		if(deltaV.y + position.y < 0 || deltaV.y + position.y + dims.y > GAME_CONTAINER.getHeight())
			deltaV.y *= -1;
		return deltaV;
	}

	@Override
	public void update(GameContainer gc, int dt) throws SlickException {
		// TODO Auto-generated method stub
		Input inp = gc.getInput();
		float dtms = (float)(dt)/1000f;
		spawntimer -= dtms;
//		System.out.println(((gc.getInput().getMouseX()/SCALE_X)-cameraX) +"," + ((gc.getInput().getMouseY()/SCALE_Y)-cameraY));
		if(gc.getInput().isMouseButtonDown(0) && shootcd <= 0 && playeralive) {
			float ang = (float) Math.atan2(gc.getInput().getMouseY() - playerpos.getY(), gc.getInput().getMouseX() - playerpos.x);
			acorns.add(new Bullet((int)(playerpos.x + 30 * Math.cos(ang)), (int)(playerpos.y + 30 * Math.sin(ang)), acorn.getWidth(), acorn.getHeight(), 300.0f, 15.0f,
					ang));
			shootcd = 1;
			Sound shootSound = new Sound("data/pew.ogg");
			shootSound.play();
		} else {
			shootcd -= dtms;
		}
		updateBullets(dtms);
		// spawn a random tea
		if(spawntimer<=0.0f) {
//			asteroids.add(new Actor(200, rng.nextInt(360) - 80, teaAngle));
		}
		
		if(inp.isKeyDown(inp.KEY_W)) {
			playerVelocity.y -= 5f * dtms;
		}
		else if(inp.isKeyDown(inp.KEY_S)) {
			playerVelocity.y += 5f * dtms;
		}
		if(inp.isKeyDown(inp.KEY_A)) {
			playerVelocity.x -= 5f * dtms;
		}
		else if(inp.isKeyDown(inp.KEY_D)) {
			playerVelocity.x += 5f * dtms;
		}
		
		playerVelocity = getActualVelocity(playerpos, playerVelocity, new Vector2f(squirrel.getWidth(), squirrel.getHeight()));
		
		playerpos.x += playerVelocity.x;
		playerpos.y += playerVelocity.y;
		
		updateObstacles(dtms);
	}
	
	public boolean colliding(Rectangle a, Rectangle b) {
		return a.intersects(b);
	}
	
	public boolean offscreen(Rectangle a) {
		return (a.getX() - a.getWidth() > GAME_CONTAINER.getScreenWidth() || a.getX() + a.getWidth() < 0 || a.getY() - a.getHeight() > GAME_CONTAINER.getScreenHeight() || a.getY() + a.getHeight() < 0);
	}
	
	public void updateObstacles(float dtms) {
		if(asteroids.size() > 0) {
			for(int i = 0; i < asteroids.size(); i++) {
				Bullet bullet = asteroids.get(i);
				if(offscreen(bullet.dims)) {
					asteroids.remove(i);
				}
				else {
					asteroids.get(i).moveInDirection(dtms);
					if(bullet.dims.intersects(new Rectangle(playerpos.x, playerpos.y, squirrel.getWidth(), squirrel.getHeight())))
						playeralive = false;
				}
			}
		}
	}
	
	public void updateBullets(float dtms) {

		if(playeralive) {
			if(acorns.size() > 0) {
				for(int i = 0; i < acorns.size(); i++) {
					Bullet bullet = acorns.get(i);
					if(offscreen(bullet.dims)) {
						acorns.remove(i);
					} else if(bullet.life == 0.0f) {
						acorns.remove(i);
					}
					else {
						acorns.get(i).moveInDirection(dtms);
						for(int j = 0; j < asteroids.size(); j++) {
							if(bullet.dims.intersects(asteroids.get(j).dims)) {
								asteroids.remove(j);
								acorns.remove(i);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void musicEnded(Music arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void musicSwapped(Music arg0, Music arg1) {
		// TODO Auto-generated method stub
		
	}
	

}
