package com.ningyq.shoot;

import java.util.Random;

/**
 * 飞机
 */
public class Airplane extends FlyingObject implements Enemy {
    private int speed = 2;

    /**初始化数据*/
    public Airplane() {
        this.image = ShootGame.airplane;
        width = image.getWidth();
        height = image.getHeight();
        y = -height;
        Random rand = new Random();
        x = rand.nextInt(ShootGame.WIDTH - width);

    }

    public int getScore() {
        return 0;
    }

    @Override
    public void step() {
        y += speed;
    }
}
