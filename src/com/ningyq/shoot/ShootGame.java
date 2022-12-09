package com.ningyq.shoot;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.*;

public class ShootGame extends JPanel {
    public static final int WIDTH = 450;    // 面板宽
    public static final int HEIGHT = 700;    // 面板高

    /**
     * 游戏当前状态START RUNNING PAUSE GAME_OVER
     */
    private State state = State.START;

    private int score = 0;    // 得分
    private final int intervel = 1000 / 100;    // 时间间隔(毫秒)

    public static Map<String, BufferedImage> bufferedImageMap = new HashMap<>();

    private FlyingObject[] flyings;   // 敌机数组
    private Bullet[] bullets;         // 子弹数组
    private Hero hero = new Hero();        // 英雄机

    public ShootGame() {
        // 初始化一只蜜蜂和一架飞机
        flyings = new FlyingObject[2];
        flyings[0] = new Airplane();
        flyings[1] = new Bee();
        // 初始化一颗子弹
        bullets = new Bullet[1];
        bullets[0] = new Bullet(200, 350);
    }

    static {
        URL image = ShootGame.class.getResource("image");

        Optional.ofNullable(image).map(URL::getFile).ifPresent(path -> {
            File file = new File(path);
            File[] files = file.listFiles((dir, name) -> name.endsWith(".png"));
            Optional.ofNullable(files).ifPresent(fs -> {
                for (File f : fs) {
                    try {
                        bufferedImageMap.put(f.getName().replaceAll(".png", ""), ImageIO.read(f));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(bufferedImageMap.get("background"), 0, 0, null);  // 画背景图
        paintHero(g);   // 画英雄机
        paintBullets(g);   // 画子弹
        paintFlyingObjects(g);   // 画飞行物
        paintScore(g);    // 画分数
        paintState(g);    // 画游戏状态
    }

    /**
     * 画英雄机
     */
    private void paintHero(Graphics g) {
        g.drawImage(hero.getImage(), hero.getX(), hero.getY(), null);
    }

    /**
     * 画子弹
     */
    private void paintBullets(Graphics g) {
        for (Bullet b : bullets) {
            g.drawImage(b.getImage(), b.getX(), b.getY(), null);
        }
    }

    /**
     * 画飞行物
     */
    private void paintFlyingObjects(Graphics g) {
        for (FlyingObject f : flyings) {
            g.drawImage(f.getImage(), f.getX(), f.getY(), null);
        }
    }

    /**
     * 画游戏状态
     */
    public void paintState(Graphics g) {
        switch (state) {
            case START:
                g.drawImage(bufferedImageMap.get("start"), 0, 0, null);
                break;
            case PAUSE:
                g.drawImage(bufferedImageMap.get("pause"), 0, 0, null);
                break;
            case GAME_OVER:
                g.drawImage(bufferedImageMap.get("gameover"), 0, 0, null);
                break;
        }
    }

    /**
     * 画分数
     */
    public void paintScore(Graphics g) {
        int x = 10;
        int y = 25;
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 14);
        g.setColor(new Color(0x000000));
        g.setFont(font);         // 设置字体
        g.drawString("SCORE:" + score, x, y);   // 画分数
        y += 20;
        g.drawString("LIFE:" + hero.getLife(), x, y);    // 画命
    }

    /**
     * 随机生成飞行物
     *
     * @return 飞行物对象
     */
    public static FlyingObject next() {
        Random random = new Random();
        int type = random.nextInt(20);
        if (type % 7 == 0) {
            return new Bee();
        } else {
            return new Airplane();
        }
    }

    int flyEnteredIndex = 0;   // 飞行物入场计数

    /**
     * 飞行物入场
     */
    public void enterAction() {
        flyEnteredIndex++;
        if (flyEnteredIndex % 40 == 0) {
            FlyingObject obj = next();    // 随机生成一个飞行物
            flyings = Arrays.copyOf(flyings, flyings.length + 1);   // 扩容
            flyings[flyings.length - 1] = obj;   // 放到最后一位

        }
    }

    public void stepAction() {
        // 飞行物走一步
        for (FlyingObject f : flyings) {
            f.step();
        }

        // 子弹走一步
        for (Bullet b : bullets) {
            b.step();
        }
        hero.step();
    }

    /**
     * 删除越界物体
     */
    public void outOfBoundsAction() {
        int index = 0;
        // 储存存在的飞行物
        FlyingObject[] flyingLives = new FlyingObject[flyings.length];
        for (FlyingObject f : flyings) {
            if (!f.outOfBounds()) {
                flyingLives[index++] = f;   // 不越界的保留
            }
        }
        flyings = Arrays.copyOf(flyingLives, index);    // 不越界的保留

        index = 0;     //
        Bullet[] bulletLives = new Bullet[bullets.length];
        for (Bullet b : bullets) {
            if (!b.outOfBounds()) {
                bulletLives[index++] = b;
            }
        }
        bullets = Arrays.copyOf(bulletLives, index);   // 不越界的保留
    }

    int shootIndex = 0;     // 射击计数

    /**
     * 射击
     */
    public void shootAction() {
        shootIndex++;
        if (shootIndex % 5 == 0) {       // 100毫秒发一颗子弹
            Bullet[] bs = hero.shoot();   // 打出子弹
            bullets = Arrays.copyOf(bullets, bullets.length + bs.length);   // 扩容
            System.arraycopy(bs, 0, bullets, bullets.length - bs.length, bs.length);  // 追加数组
        }
    }

    public void action() {// 启动执行代码

        // 鼠标监听事件
        MouseAdapter l = new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {  // 鼠标移动
                if (state == State.RUNNING) {  // 运行时移动英雄机
                    int x = e.getX();
                    int y = e.getY();
                    hero.moveTo(x, y);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {  // 鼠标进入

                if (state == State.PAUSE) {
                    state = State.RUNNING;    // 暂停时运行
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {   // 鼠标退出

                if (state != State.GAME_OVER) {
                    state = State.PAUSE;      // 游戏未结束，则设置其为暂停
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {  // 鼠标点击
                // 游戏结束，清理现场
                switch (state) {
                    case START:
                        state = State.RUNNING;
                        break;
                    case GAME_OVER:
                        flyings = new FlyingObject[0];
                        bullets = new Bullet[0];
                        hero = new Hero();
                        score = 0;
                        state = State.START;
                        break;
                }
            }
        };
        this.addMouseListener(l);       // 处理鼠标点击操作

        this.addMouseMotionListener(l);   // 处理鼠标滑动操作

        // 定时器
        Timer timer = new Timer();   // 主流程控制
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (state == State.RUNNING) {
                    enterAction();   // 飞机物入场
                    stepAction();    // 走一步
                    shootAction();   // 射击
                    bangAction();    // 检查
                    outOfBoundsAction();    // 删除越界物体
                    checkGameOverAction();   // 检查游戏是否结束
                }

                repaint();       // 重绘,调用paint()方法
            }
        }, intervel, intervel);
    }

    /**
     * 子弹与飞行物碰撞检测
     */
    public void bangAction() {
        for (Bullet b : bullets) {
            bang(b);
        }
    }

    /**
     * 弹与飞行物之间碰撞检查
     */
    private void bang(Bullet bullet) {
        int index = -1;    // 击中飞机标记
        for (int i = 0; i < flyings.length; i++) {
            FlyingObject obj = flyings[i];
            if (obj.shootBy(bullet)) {   // 判断是否击中
                index = i;    // 记录击中飞机标记
                break;
            }
        }
        if (index != -1) {   // 有击中飞机
            FlyingObject one = flyings[index];   //  记录被击中的飞行物
            FlyingObject temp = flyings[index];  // 被击中的飞行物与最后一个飞行物交换

            flyings[index] = flyings[flyings.length - 1];
            flyings[flyings.length - 1] = temp;
            // 删除最后一个飞行物（即被击中的）
            flyings = Arrays.copyOf(flyings, flyings.length - 1);

            // 检查one的类型  如果是敌人  加分
            if (one instanceof Enemy) {   // 检查类型，如果是敌人  加分
                Enemy e = (Enemy) one;     // 强制类型转化
                score += e.getScore();    // 加分
            }
            if (one instanceof Award) {    // 若为奖励，设置奖励
                Award a = (Award) one;
                int type = a.getType();      // 获得奖励类型
                switch (type) {
                    case Award.DOUBLE_FIRE:
                        hero.addDoubleFire();    // 设置双倍活力
                        break;
                    case Award.LIFE:
                        hero.addLife();         // 设置加命
                        break;
                }
            }
        }
    }

    /**
     * 检查游戏是否结束
     */
    public boolean isGameOver() {
        for (int i = 0; i < flyings.length; i++) {
            int index = -1;
            FlyingObject obj = flyings[i];
            if (hero.hit(obj)) {   //
                hero.subtractLife();
                hero.setDoubleFire(0);
                index = i;
            }
            if (index != -1) {
                FlyingObject t = flyings[index];
                flyings[index] = flyings[flyings.length - 1];
                flyings[flyings.length - 1] = t;
                flyings = Arrays.copyOf(flyings, flyings.length - 1);
            }
        }
        return hero.getLife() <= 0;
    }

    /**
     * 检查游戏结束
     */
    public void checkGameOverAction() {
        if (isGameOver()) {
            state = State.GAME_OVER;  // 改变状态
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Fly Game");
        ShootGame game = new ShootGame();   // 面板对象
        frame.add(game);                // 将面版添加到JFrame
        frame.setSize(WIDTH, HEIGHT);    // 大小
        frame.setAlwaysOnTop(true);     // 保证面板总在最上
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   // 默认关闭操作
        frame.setLocationRelativeTo(null);        // 设置窗体初始位置
        frame.setVisible(true);            // 尽快调用paint

        game.action();   // 启动执行
    }
}
