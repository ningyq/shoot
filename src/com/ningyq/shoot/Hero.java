package com.ningyq.shoot;

import java.awt.image.BufferedImage;

/**
 * 英雄机:飞行物
 */
public class Hero extends FlyingObject {
    protected BufferedImage[] images = {};
    protected int index = 0;

    private int doubleFire;
    private int life;

    public Hero() {
        life = 3;
        doubleFire = 0;
        this.image = ShootGame.hero0;
        images = new BufferedImage[]{ShootGame.hero0,ShootGame.hero1};
        width = image.getWidth();
        height = image.getHeight();
        x = 150;
        y = 400;
    }

    public Bullet[] shoot() {  //发射子弹
        int xStep = width/4;
        int yStep = 20;
        if (doubleFire > 0) {
            Bullet[] bullets = new  Bullet[2];
            bullets[0] = new Bullet(x + xStep,y + yStep);
            bullets[1] = new Bullet(x + 3 * xStep,y - yStep);
            doubleFire -= 2;
            return bullets;
        }else {  //单倍
            Bullet[] bullets = new Bullet[1];
            //y-yStep(子弹到飞机的位置)
            bullets[0] = new Bullet(x + 2 * xStep,y - yStep);
            return bullets;
        }
    }

    /**
     * 当前物体移动一下，相对距离，x，y鼠标位置
     */
    public void moveTo(int x,int y) {
        this.x = x - width / 2;
        this.y = y - height / 2;
    }

    public void setDoubleFire(int doubleFire) {
        this.doubleFire = doubleFire;
    }

    public void addDoubleFire() {   //双倍火力
        doubleFire += 40;
    }

    public void addLife() {  //加命
        life++;
    }

    public int getLife() {
        return life;
    }

    public void subtractLife() {   //减命
        life--;
    }

    @Override
    public void step() {
        if (images.length > 0) {
            image = images[index++ / 10 % images.length];
        }
    }

    @Override
    public boolean outOFBounds() {
        return false;
    }

    public boolean hit(FlyingObject other) {
        int x1 = other.x - this.width / 2;
        int x2 = other.x + other.width + this.width / 2;
        int y1 = other.y - this.width / 2;
        int y2 = other.y + other.width + this.width / 2;
        return this.x + this.width / 2 > x1 && this.x +this.width / 2 < x2 && this.y + this.height / 2 > y1 && this.y +this.width / 2 < y2;
    }
}
