package com.jacobsu.game;



import javax.swing.*;
import java.awt.*;

public class Start {
    public static void main(String[] args) {
        Game game=new Game();//游戏页面

        JFrame window =new JFrame();//游戏窗体
        ImageIcon title=new ImageIcon("res/2048.png");
        window.setIconImage(title.getImage());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//使用System.exit()方法退出应用程序
        window.setResizable(false);//窗口大小不可改变
        window.add(game);//加入游戏内容
        window.pack();//自动适配组件大小
        window.setLocationRelativeTo(null);//使窗口在桌面中央
        window.setVisible(true);//使窗口可见
        game.start();//游戏开始
    }
}
