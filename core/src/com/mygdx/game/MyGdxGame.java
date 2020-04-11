package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.DelaunayTriangulator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.utils.ShortArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


//import org.lwjgl.util.Color;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jdk.nashorn.internal.objects.NativeJava;

import static com.badlogic.gdx.math.GeometryUtils.polygonCentroid;


public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
	PolygonSpriteBatch polyBatch;
	Texture textureSolid;
	Texture intersecSolid;
	private OrthographicCamera camera;
	private Viewport viewport;
	BodyEditorLoader loader ;
	Intersector is;
	ShapeRenderer shapeRenderer;
	Vector2 touchPoint = null ;
	Vector2 newTouchedOrigin ;

	enum GameState  {DRAGGING, NONE} ;
	GameState gameState ;

	LinkedList<Block> blocks;
	Block touchedBlock = null;

	Block actor1 ;
	Block actor2 ;
	Block actor3 ;
	BlockSpriteFactory spriteFactory ;

	final float XSIZE = 10f ;
    final float YSIZE = 10f ;

    final float XMARGIN = XSIZE*0.1f ;
    final float YMARGIN = YSIZE*0.1f ;


	@Override
	public void create () {
		//Box2D.init();
		shapeRenderer = new ShapeRenderer();
		loader = new BodyEditorLoader(Gdx.files.internal("debris.pm"));
		camera = new OrthographicCamera(XSIZE, YSIZE);
		viewport = new ExtendViewport(XSIZE, YSIZE, camera);
		polyBatch = new PolygonSpriteBatch();
		is =  new Intersector() ;
		gameState = GameState.NONE ;
		blocks = new LinkedList<Block>() ;
        createTextures();
        ///////////////////////////
        //createPolygons();      //
		///////////////////////////
		loadPolygons ();
		Gdx.input.setInputProcessor(this);
	}

    private void createTextures() {
		Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pix.setColor(0xF00000FF);
		pix.fill();
		intersecSolid = new Texture(pix);

		pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pix.setColor(0xDECDBFFF);
		pix.fill();
		textureSolid = new Texture(pix);
	}

	private void createPolygons(){
		float f[] = {0,0,1,1,2,0} ;
		actor1 = new Block(f,textureSolid,spriteFactory);
		actor1.setPosition(0.0f,0.0f);
		actor1.setScale(1,1);

		actor2 = new Block(f,textureSolid,spriteFactory);
		actor2.setPosition(0.5f,0.5f);
		actor2.setScale(2,2);

		float f2[] = {0,0,0,1,1,1,1,0} ;
		actor3 = new Block(f2,textureSolid,spriteFactory);
		actor3.setPosition(-2.0f,-2.0f);
		actor3.setScale(1,1);
		actor3.rotateBy(90);

		//blocks.add(actor1);
		//blocks.add(actor2);
		blocks.add(actor3);
	}


	private void loadPolygons () {
		//loader.getInternalModel().rigidBodies.forEach((key,value) -> System.out.println(key + " = " + value));

		float j = -2f;
		float n = 0 ;
		Iterator<Entry<String, BodyEditorLoader.RigidBodyModel>> it = loader.getInternalModel().rigidBodies.entrySet().iterator();
		//it.next();
		//it.next();
		while (it.hasNext()) {
			Map.Entry<String, BodyEditorLoader.RigidBodyModel> pair = (Map.Entry<String, BodyEditorLoader.RigidBodyModel>) it.next();
			BodyEditorLoader.RigidBodyModel rbm = pair.getValue() ;
			for(int i=0;i<rbm.shapes.size();++i) {
				BodyEditorLoader.ShapeModel pm = rbm.shapes.get(i);
				float [] f = pm.toFloatArray() ;

				float [] blockVertices = Arrays.copyOf(f,f.length) ;

				Block block = new Block(blockVertices,textureSolid,spriteFactory) ;

				block.setPosition(j+n*2, j+n*2);
				block.setScale(3,3);

				//block.rotateBy(45);
				++n ;

				//System.out.println("polygon vertices="+Arrays.toString(f)) ;
				//System.out.println("block vertices="+Arrays.toString(blockVertices)) ;

				blocks.add(block);
			}
			//break;
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		polyBatch.setProjectionMatrix(camera.combined);
		polyBatch.begin();
		for (int i = 0; i < blocks.size(); ++i) {
			blocks.get(i).getPolygonSprite().draw(polyBatch);
		}

		for(int i = 0;i<blocks.size()-1;++i) {
			Polygon pMain = blocks.get(i).getPolygon();
			for (int j = i + 1; j < blocks.size(); ++j) {
				Polygon pSlave = blocks.get(j).getPolygon();
				Polygon overlap = new Polygon();
				boolean partIntersets = false;
				partIntersets = Intersector.intersectPolygons(pMain, pSlave, overlap);
				if (partIntersets) {
					PolygonSprite ps = spriteFactory.createBlockSprite(overlap.getTransformedVertices(), intersecSolid);
					ps.draw(polyBatch);
				}
			}
		}

        polyBatch.end();

		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(1,0,0,1);

		for(int j = 0;j<blocks.size();++j) {
			Block thisBlock = blocks.get(j) ;
			float [] f = thisBlock.getPolygon().getTransformedVertices();
			for(int i=0;i<f.length-1;i+=2) {
				shapeRenderer.circle(f[i], f[i + 1], 0.1f, 10);
			}
			//shapeRenderer.circle(thisBlock.getCentroid().x, thisBlock.getCentroid().y, 0.1f, 10);
			thisBlock.rotateBy(0.1f);
		}
//		shapeRenderer.polygon(actor3.getPolygon().getVertices());
		shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
		polyBatch.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector3 v = new Vector3() ;
		v.x=screenX;
		v.y=screenY;
		v.z = 0;
		camera.unproject(v);
		touchPoint = new Vector2(v.x,v.y) ;
		for(int i = 0;i<blocks.size();++i) {
			Block thisBlock = blocks.get(i);
			float [] pa = thisBlock.getPolygon().getTransformedVertices() ;
			if (is.isPointInPolygon(pa,0,pa.length,v.x,v.y) )  {
				touchedBlock = thisBlock ;
				gameState = GameState.DRAGGING ;
				newTouchedOrigin = new Vector2(touchedBlock.getPolygon().getX(),touchedBlock.getPolygon().getY()) ;
				System.out.println("i="+i);
				break ;
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		gameState = GameState.NONE ;
		return false;
	}


	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Vector3 v = new Vector3() ;
		v.x=screenX;
		v.y=screenY;
		v.z = 0;
		camera.unproject(v);
		Vector2 dragPoint = new Vector2(v.x,v.y) ;

		if (dragPoint.x > XSIZE/2-XMARGIN) {dragPoint.x = XSIZE/2-XMARGIN;}
		if (dragPoint.x < -XSIZE/2+XMARGIN) {dragPoint.x = -XSIZE/2+XMARGIN;}

		if (dragPoint.y > YSIZE/2-XMARGIN) {dragPoint.y = YSIZE/2-YMARGIN;}
		if (dragPoint.y < -YSIZE/2+XMARGIN) {dragPoint.y = -YSIZE/2+YMARGIN;}

		Vector2 diff = new Vector2(dragPoint) ;
		diff.sub(touchPoint) ;

		if (gameState == GameState.DRAGGING) {
			//System.out.println("dragging poly");
			Vector2 tmp = new Vector2(newTouchedOrigin) ;
			tmp.add(diff) ;
			touchedBlock.setPosition(tmp.x,tmp.y);
			//System.out.println(diff);
		}
		return false;
	}


	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	public void resize(int width, int height) {
		viewport.update(width, height);
		camera.update();
	}
}
