package com.jacobsu.game;

import com.jacobsu.gui.GuiScreen;
import com.jacobsu.gui.LeaderboardsPanel;
import com.jacobsu.gui.MainMenuPanel;
import com.jacobsu.gui.PlayPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Game extends JPanel implements KeyListener, MouseListener, MouseMotionListener,Runnable {

    public static final long serialVersionUID=1L;
    public static final int WIDTH=500;//宽度
    public static final int HEIGHT=600;//高度
    public static final Font main =new Font("Bebas Neue Regular",Font.PLAIN,28);//字体相关
    private Thread game;//线程
    private boolean running;
    private BufferedImage image=new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
    private GuiScreen screen;

    private long startTime;
    private long elapsed;
    private boolean set;

    public Game(){
        setFocusable(true);//当前控件可以获得焦点
        setPreferredSize(new Dimension(WIDTH,HEIGHT));//设计尺寸
        addKeyListener(this);//键盘监听
        addMouseListener(this);//鼠标监听
        addMouseMotionListener(this);//鼠标移动监听

        screen = GuiScreen.getInstance();//初始化屏幕
        screen.add("Menu",new MainMenuPanel());//添加控件并进入该控件功能
        screen.add("Play",new PlayPanel());
        screen.add("Leaderboards", new LeaderboardsPanel());
        screen.setCurrentPanel("Menu");//添加标题

    }

    private  void update(){
        screen.update();//界面更新
        Keyboard.update();
    }
    private void render(){
        Graphics2D g=(Graphics2D)image.getGraphics();
        g.setColor(Color.WHITE);//设置背景颜色
        g.fillRect(0,0,WIDTH,HEIGHT);//背景坐标及宽高
        screen.render(g);

        //render board
        g.dispose();//关闭窗口方法

        Graphics2D g2d =(Graphics2D)getGraphics();
        g2d.drawImage(image,0,0,null);//绘制界面位置
        g2d.dispose();//关闭界面方法
    }

    //运行游戏
    @Override
    public void run() {

        int fps=0;
        int updates=0;
        long fpsTimer =System.currentTimeMillis();//获得当前计算机时间
        double nsPerUpdate =1000000000.0/60;

        //上次更新时间
        double then =System.nanoTime();//系统初始时间

        double unprocessed=0;

        while (running) {
            boolean shouldRender=false;
            double now =System.nanoTime();//系统现在时间
            unprocessed+=(now-then)/nsPerUpdate;
            then=now;//更新初始时间


            //更新队列
            while (unprocessed >= 1) {
                updates++;
                update();
                unprocessed--;
                shouldRender = true;
            }

            //render
            if (shouldRender) {
                fps++;
                render();
                shouldRender = false;

            } else {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //FPS Timer
        if(System.currentTimeMillis()-fpsTimer>1000){
            System.out.printf("%d fps %d updates",fps,updates);
            System.out.println();
            fps=0;
            updates=0;
            fpsTimer+=1000;
        }
    }
    //游戏开始
    public synchronized void start(){
        if(running) return;
        running=true;
        game=new Thread(this,"game");
        game.start();
    }
    //游戏结束
    public synchronized void stop(){
        if(!running) return;
        running=false;
        System.exit(0);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Keyboard.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Keyboard.keyReleased(e);
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        screen.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        screen.mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        screen.mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        screen.mouseMoved(e);
    }
}
