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
import org.newdawn.slick.Music;
import org.newdawn.slick.MusicListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

public class Week8 extends BasicGame implements MusicListener {
	
	Image azcan;
	Image azbottle;
	Image jt;
	Random rng = new Random();
	
	float teaAngle = (float) Math.toRadians(35);
	
	List<Actor> jts = new ArrayList<Actor>();
	List<Actor> bullets = new ArrayList<Actor>();
	
	HashMap<String, Image> drawsames = new HashMap<String, Image>();

	Vector2f playerpos = new Vector2f(700,700);
	
	float playerangle = 0.0f;
	float spawntimer = 0.0f;
	float shootcd = 0.0f;
	
	public class Actor {
		Vector2f pos;
		float rotation;
		float life = 0.0f;
		public Actor(int x, int y, float rotation) {
			this.rotation = rotation;
			pos = new Vector2f(x, y);
		}
	}
	
	public Week8(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new Week8("Arizona vs Jasmine"));
		app.setDisplayMode(800, 800, false);
		app.start();
		app.setShowFPS(false);
	}
	
	boolean playeralive = true;
	
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		// TODO Auto-generated method stub
		for(int i = 0; i < jts.size(); i++) {
			Actor cjt = jts.get(i);
			g.pushTransform();
			g.translate(cjt.pos.x, jts.get(i).pos.y);
			jt.draw();
			g.popTransform();
			cjt.pos.x += 0.05*(Math.cos(teaAngle));
			cjt.pos.y += 0.05*(Math.sin(teaAngle));
			// check collision
			int jtw = jt.getWidth(), jth = jt.getHeight();
			Shape thisguy = new Rectangle(cjt.pos.x, cjt.pos.y, jtw, jth);
			if(thisguy.intersects(new Rectangle(playerpos.x, playerpos.y, azcan.getWidth(), azcan.getHeight())))
				playeralive = false;
		}
		if(playeralive) {
			if(bullets.size() > 0) {
				for(int i = 0; i < bullets.size(); i++) {
					Actor bullet = bullets.get(i);
					g.pushTransform();
					g.translate(bullet.pos.x, bullet.pos.y);
					azbottle.rotate(bullet.rotation);
					azbottle.draw();
					
					g.popTransform();
					if(bullet.pos.x < 0 - azbottle.getWidth() || bullet.pos.x - azbottle.getHeight() > 800 || bullet.pos.y < 0 - azbottle.getHeight() || bullet.pos.y - azbottle.getHeight() > 800) {
						bullets.remove(i);
					} else if(bullet.life >=15f) {
						bullets.remove(i);
					}
					else {
						double ang = bullet.rotation;
						bullet.pos.x += 0.5*(Math.cos(ang));
						bullet.pos.y += 0.5*(Math.sin(ang));
						bullet.life+= 0.01f;
						Shape thisguy = new Rectangle(bullet.pos.x, bullet.pos.y, azbottle.getWidth(), azbottle.getHeight());
						// check collision
						int jtw = jt.getWidth(), jth = jt.getHeight();
						for(int j = 0; j < jts.size(); j++) {
							if(thisguy.intersects(new Rectangle(jts.get(j).pos.x, jts.get(j).pos.y, jtw, jth))){
								jts.remove(j);
							}
						}
					}
				}
			}
			g.pushTransform();
			g.translate(playerpos.x, playerpos.y);
			azcan.draw();
			g.popTransform();
		} else {
			g.drawString("For a trillion million years Jasmine Tea rules the world with an iron fist.\n\t\tYou have lost!", 20, 400);
		}
	}

	@Override
	public void init(GameContainer arg0) throws SlickException {
		// TODO Auto-generated method stub
		azcan = new Image("data/azcan.jpg");
		azcan = azcan.getScaledCopy(100, 150);
		azbottle = new Image("data/azbottle.jpg");
		azbottle = azbottle.getScaledCopy(30, 60);
		jt = new Image("data/jt.jpg");
		jt = jt.getScaledCopy(150, 150);
		jts.add(new Actor(100,100, rng.nextInt(360)));
		
		Music BGM = new Music("data/azsong.ogg");
		BGM.addListener(this);
		BGM.play();
		
	}

	@Override
	public void update(GameContainer gc, int arg1) throws SlickException {
		// TODO Auto-generated method stub
		spawntimer -= 0.01f;
//		System.out.println(((gc.getInput().getMouseX()/SCALE_X)-cameraX) +"," + ((gc.getInput().getMouseY()/SCALE_Y)-cameraY));
		if(gc.getInput().isMouseButtonDown(0) && shootcd <= 0) {
			float ang = (float) Math.atan2(gc.getInput().getMouseY() - playerpos.getY(), gc.getInput().getMouseX() - playerpos.x);
			bullets.add(new Actor((int)(playerpos.x - 30 * Math.cos(ang)), (int)(playerpos.y - 30 * Math.sin(ang)),
					ang));
			shootcd = 20;
			Sound shootSound = new Sound("data/pew.ogg");
			shootSound.play();
		} else {
			shootcd -= 0.1f;
		}
		// spawn a random tea
		if(spawntimer<=0.0f) {
			jts.add(new Actor(200, rng.nextInt(360) - 80, teaAngle));
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
