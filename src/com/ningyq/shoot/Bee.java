package com.ningyq.shoot;

import java.util.Random;

/**蜜蜂*/
public class Bee extends FlyingObject implements Award {
    private int xSpeed = 1;    //x坐标移动速度
    private int ySpeed = 2;    //y坐标移动速度
    private int awardType;     //奖励类型

    public Bee() {
        this.image = ShootGame.bee;
        width = image.getWidth();
        height = image.getHeight();
        y = -height;
        Random rand = new Random();
        x = rand.nextInt(ShootGame.WIDTH - width);

        awardType = rand.nextInt(2);
    }

    public int getType() {
        return awardType;
    }

    @Override
    public void step() {    //可斜飞
        x += xSpeed;
        y += ySpeed;
        if (x > ShootGame.WIDTH - width) {
            xSpeed = -1;
        }
        if (x < 0) {
            xSpeed = 1;
        }
    }

    @Override
    public boolean outOFBounds() {
        return y > ShootGame.HEIGHT;
    }
}
