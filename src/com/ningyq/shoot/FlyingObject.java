package com.ningyq.shoot;

import java.awt.image.BufferedImage;

public abstract class FlyingObject {
    protected int x;    //x坐标
    protected int y;    //y坐标
    protected int width;   //宽
    protected int height;  //高
    protected BufferedImage image; //图片

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * 飞行物移动一步
     */
    public abstract void step();
}
