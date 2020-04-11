package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.DelaunayTriangulator;
import com.badlogic.gdx.utils.ShortArray;

public class BlockSpriteFactory {

    //private static BlockSpriteFactory instance;
    private static DelaunayTriangulator triangulator = new DelaunayTriangulator();

    private BlockSpriteFactory() {}

   /* public static BlockSpriteFactory getInstance(){
        if(instance == null){
            instance = new BlockSpriteFactory();
        }
        return instance;
    }*/

    public static PolygonSprite createBlockSprite (float [] vertices, Texture texture) {
        ShortArray sh = triangulator.computeTriangles(vertices,false);
        //System.out.println(sh.toString());
        PolygonRegion polyReg = new PolygonRegion(new TextureRegion(texture),
                vertices,
                sh.toArray()
        );
        PolygonSprite polygonSprite = new PolygonSprite(polyReg);
        //setBounds sets position(x,y) and scale(width,height)
        polygonSprite.setBounds(0,0,1,1);
        polygonSprite.setOrigin(polygonSprite.getWidth()/2,polygonSprite.getHeight()/2);
        return polygonSprite ;
    }
}
