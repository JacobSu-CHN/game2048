package com.jacobsu.gui;

import com.jacobsu.game.DrawUtils;
import com.jacobsu.game.Game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuPanel extends GuiPanel{

    private Font titleFont= Game.main.deriveFont(100f);//设置主界面“2048“大小
    private Font creatorFont=Game.main.deriveFont(24f);//设置控件大小
    private String title="2048";
    private String creator="By Jacob Su";
    private int buttonWidth=220;
    private int spacing=90;
    private int buttonHeight=60;

    public MainMenuPanel(){
        super();

        //设置主界面控件大小位置
        GuiButton playButton=new GuiButton(Game.WIDTH/2-buttonWidth/2,220,buttonWidth,buttonHeight);
        GuiButton scoresButton=new GuiButton(Game.WIDTH/2-buttonWidth/2,playButton.getY()+spacing,buttonWidth,buttonHeight);
        GuiButton quitButton=new GuiButton(Game.WIDTH/2-buttonWidth/2,scoresButton.getY()+spacing,buttonWidth,buttonHeight);

        //添加文本
        playButton.setText("Play");
        scoresButton.setText("Scores");
        quitButton.setText("Quit");

        //设置响应
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                GuiScreen.getInstance().setCurrentPanel("Play");
            }
        });

        scoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                GuiScreen.getInstance().setCurrentPanel("Leaderboards");
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        //添加至主界面
        add(playButton);
        add(scoresButton);
        add(quitButton);
    }

    public  void render(Graphics2D g){
        super.render(g);

        g.setFont(titleFont);
        g.setColor(Color.BLACK);
        g.drawString(title,Game.WIDTH/2- DrawUtils.getMessageWidth(title,titleFont,g)/2,150);
        g.setFont(creatorFont);
        g.drawString(creator,20,Game.HEIGHT-10);


    }
}
