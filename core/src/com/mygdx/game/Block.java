package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.badlogic.gdx.math.Polygon;

import java.util.Arrays;


import static com.badlogic.gdx.math.GeometryUtils.polygonCentroid;

public class Block extends Actor {
    Polygon polygon;
    PolygonSprite polygonSprite ;
    float [] vertices ;
    Vector2 centroid;


    Block(float [] vertices, Texture texture, BlockSpriteFactory spriteFactory) {
        this.vertices = Arrays.copyOf(vertices,vertices.length) ;
        polygon = new Polygon(vertices) ;
        polygonSprite = spriteFactory.createBlockSprite(vertices,texture) ;
        updateCenter() ;
        this.setRotation(0);
        this.polygonSprite.setRotation(0);
        this.polygon.setRotation(0);
        //polygonSprite.setOrigin(centroid.x,centroid.y);
        //polygon.setOrigin(centroid.x,centroid.y);
    }


    private void updateCenter() {
        calcBaricenter();
        polygon.setOrigin(centroid.x,centroid.y);
        polygonSprite.setOrigin(centroid.x,centroid.y);
    }

    private void calcBaricenter() {
        centroid = new Vector2();
        polygonCentroid(vertices,0,vertices.length,centroid);
    }

    public PolygonSprite getPolygonSprite() {
        return polygonSprite;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    @Override
    public void setPosition (float x,float y)    {
        super.setPosition(x,y);
        polygonSprite.setPosition(x,y);
        polygon.setPosition(x,y);
       // updateCenter();
    }

    @Override
    public void setScale (float width,float height)    {
        super.setScale(width,height);
        polygonSprite.setScale(width,height);
        polygon.setScale(width,height);
        //updateCenter();
    }

    @Override
    public void setOrigin (float x,float y)    {
        super.setOrigin(x,y);
        polygonSprite.setOrigin(x,y);
        polygon.setOrigin(x,y);
        //updateCenter();
    }


    @Override
    public void rotateBy(float amountInDegrees) {
        super.rotateBy(amountInDegrees);
        polygonSprite.rotate(amountInDegrees);
        polygon.rotate(amountInDegrees);
    }

    public Vector2 getCentroid() {
        return centroid;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public void setPolygonSprite(PolygonSprite polygonSprite) {
        this.polygonSprite = polygonSprite;
    }

    @Override
    public  void setRotation(float degrees) {
        super.setRotation(degrees);
        polygon.setRotation(degrees);
        polygonSprite.setRotation(degrees);
    }
}
