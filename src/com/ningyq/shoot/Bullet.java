package com.ningyq.shoot;

/**
 * 子弹类:飞行物
 */
public class Bullet extends FlyingObject {
    private int speed = 3;     //  移动速度

    public Bullet(int x,int y) {
        this.x = x;
        this.y = y;
        this.image = ShootGame.bullet;
    }

    @Override
    public void step() {      //移动方法
        y -= speed;
    }

    @Override
    public boolean outOFBounds() {
        return y < -height;
    }
}
