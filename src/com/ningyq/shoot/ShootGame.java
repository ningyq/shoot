package com.ningyq.shoot;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.TimerTask;;

public class ShootGame extends JPanel {
    public static final int WIDTH = 400;    //面板宽
    public static final int HEIGHT = 650;    //面板高

    public static BufferedImage background;
    public static BufferedImage start;
    public static BufferedImage airplane;
    public static BufferedImage bee;
    public static BufferedImage bullet;
    public static BufferedImage hero0;
    public static BufferedImage hero1;
    public static BufferedImage pause;
    public static BufferedImage gameover;

    private FlyingObject[] flyings = {};   //敌机数组
    private Bullet[] bullets = {};         //子弹数组
    private Hero hero = new Hero();        //英雄机

    public ShootGame() {
        //初始化一只蜜蜂和一架飞机
        flyings = new FlyingObject[2];
        flyings[0] = new Airplane();
        flyings[1] = new Bee();
        //初始化一颗子弹
        bullets = new Bullet[1];
        bullets[0] = new Bullet(200,350);
    }

    static {
        try {
            background = ImageIO.read(ShootGame.class.getResource("background"));
            start = ImageIO.read(ShootGame.class.getResource("start"));
            airplane = ImageIO.read(ShootGame.class.getResource("airplane"));
            bee = ImageIO.read(ShootGame.class.getResource("bee"));
            bullet = ImageIO.read(ShootGame.class.getResource("bullet"));
            hero0 = ImageIO.read(ShootGame.class.getResource("hero0"));
            hero1 = ImageIO.read(ShootGame.class.getResource("hero1"));
            pause = ImageIO.read(ShootGame.class.getResource("pause"));
            gameover = ImageIO.read(ShootGame.class.getResource("gameover"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(background,0,0,null);  //画背景图
        paintHero(g);   //画英雄机
        paintBullets(g);   // 画子弹
        paintFlyingObjects(g);   //画飞行物
    }

    /**画英雄机*/
    private void paintHero(Graphics g) {
        g.drawImage(hero.getImage(),hero.getX(),hero.getY(),null);
    }

    /**画子弹*/
    private void paintBullets(Graphics g) {
        for (int i = 0; i < bullets.length; i++) {
            Bullet b = bullets[i];
            g.drawImage(b.getImage(),b.getX(),b.getY(),null);
        }
    }

    /**画飞行物*/
    private void paintFlyingObjects(Graphics g) {
        for (int i = 0; i < flyings.length; i++) {
            FlyingObject f = flyings[i];
            g.drawImage(f.getImage(),f.getX(),f.getY(),null);
        }
    }

    /**
     * 随机生成飞行物
     *
     * @return 飞行物对象
     */
    public static FlyingObject nextone() {
        Random random = new Random();
        int type = random.nextInt(20);
        if (type == 0) {
            return new Bee();
        }else {
            return new Airplane();
        }
    }

    int flyEnteredIndex = 0;   //飞行物入场计数
    /**飞行物入场*/
    public void enterAction() {
        flyEnteredIndex++;
        if (flyEnteredIndex % 40 == 0) {
            FlyingObject obj = nextone();    //随机生成一个飞行物
            flyings = Arrays.copyOf(flyings,flyings.length+1);   //扩容
            flyings[flyings.length - 1] = obj;   //放到最后一位

        }
    }

    public void stepAction() {
        /**飞行物走一步*/
        for (int i = 0; i < flyings.length; i++) {
            FlyingObject f = flyings[i];
            f.step();
        }

        /**子弹走一步*/
        for (int i = 0; i < bullets.length; i++) {
            Bullet b = bullets[i];
            b.step();
        }
        hero.step();
    }

    int shootIndex = 0;     //射击计数
    /**射击*/
    public void shootAction(){
        shootIndex++;
        if (shootIndex % 30 == 0) {       //100毫秒发一颗子弹
            Bullet[] bs = hero.shoot();   //打出子弹
            bullets = Arrays.copyOf(bullets,bullets.length + bs.length);   //扩容
            System.arraycopy(bs,0,bullets,bullets.length - bs.length,bs.length);  //追加数组
        }
    }

    private Timer timer;    //定时器
    private int intervel = 1000/100;    //时间间隔(毫秒)

    public void action() {//启动执行代码
        timer = new Timer();   //主流程控制
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                enterAction();   // 飞机物入场
                stepAction();    //走一步
                shootAction();   //射击
                repaint();       //重绘,调用paint()方法
            }
        },intervel,intervel);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Fly Game");
        ShootGame game = new ShootGame();   // 面板对象
        frame.add(game);                //将面版添加到JFrame
        frame.setSize(WIDTH,HEIGHT);    //大小
        frame.setAlwaysOnTop(true);     //保证面板总在最上
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   //默认关闭操作
        frame.setLocationRelativeTo(null);        //设置窗体初始位置
        frame.setVisible(true);            //尽快调用paint
        
        game.action();   // 启动执行
    }
}
