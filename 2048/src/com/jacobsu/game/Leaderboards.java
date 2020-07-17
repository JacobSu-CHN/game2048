package com.jacobsu.game;

import java.io.*;
import java.util.ArrayList;

//设置排行榜
public class Leaderboards {


    private static Leaderboards lBoard;//实例
    private String filePath;//文件名
    private String highScores;//最高分

    //All time leaderboards
    private ArrayList<Integer>topScores;//最高分
    private ArrayList<Integer>topTiles;//最高贴片
    private ArrayList<Long>topTimes;//最快时间

    private Leaderboards(){

        try {
            filePath = new File("").getAbsolutePath();
            System.out.println(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        highScores="Scores";

        topScores=new ArrayList<Integer>();
        topTiles=new ArrayList<Integer>();
        topTimes=new ArrayList<Long>();
    }

    public static Leaderboards getInstance(){
        if (lBoard==null){
            lBoard=new Leaderboards();
        }
        return lBoard;
    }

    //添加分数
    public void addScore(int score){
        for (int i=0;i<topScores.size();i++){
            if (score>=topScores.get(i)){
                topScores.add(i,score);
                topScores.remove(topScores.size()-1);
                return;
            }
        }
    }
    //添加贴片
    public void addTile(int tileValue){
        for (int i=0;i<topTiles.size();i++){
            if (tileValue>=topTiles.get(i)){
                topTiles.add(i,tileValue);
                topTiles.remove(topTiles.size()-1);
                return;
            }
        }
    }
    //添加时间
    public void addTime(long millis){
        for (int i=0;i<topTimes.size();i++){
            if (millis<=topTimes.get(i)){
                topTimes.add(i,millis);
                topTimes.remove(topTimes.size()-1);
                return;
            }
        }
    }

    //加载分数
    public void loadScores(){
        try {
            File f=new File(filePath,highScores);
            if (!f.isFile()){
                createSaveData();
            }

            BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(f)));

            //清空
            topScores.clear();
            topTiles.clear();
            topTimes.clear();

            //读取
            String[] scores=reader.readLine().split("-");
            String[] tiles=reader.readLine().split("-");
            String[] times=reader.readLine().split("-");


            for (int i=0;i<scores.length;i++){
                topScores.add(Integer.parseInt(scores[i]));
            }
            for (int i=0;i<tiles.length;i++){
                topTiles.add(Integer.parseInt(tiles[i]));
            }
            for (int i=0;i<times.length;i++){
                topTimes.add(Long.parseLong(times[i]));
            }
            reader.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //保存分数
    public void saveScores(){
        FileWriter output=null;

        try {
            File f=new File(filePath,highScores);
            output =new FileWriter(f);
            BufferedWriter writer=new BufferedWriter(output);

            writer.write(topScores.get(0)+"-"+topScores.get(1)+"-"+topScores.get(2)+"-"+topScores.get(3)+"-"+topScores.get(4));
            writer.newLine();
            writer.write(topTiles.get(0)+"-"+topTiles.get(1)+"-"+topTiles.get(2)+"-"+topTiles.get(3)+"-"+topTiles.get(4));
            writer.newLine();
            writer.write(topTimes.get(0)+"-"+topTimes.get(1)+"-"+topTimes.get(2)+"-"+topTimes.get(3)+"-"+topTimes.get(4));
            writer.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //创建保留数据
    private void createSaveData(){
        FileWriter output=null;

        try {
            File f=new File(filePath,highScores);
            output =new FileWriter(f);
            BufferedWriter writer=new BufferedWriter(output);

            writer.write("0-0-0-0-0");
            writer.newLine();
            writer.write("0-0-0-0-0");
            writer.newLine();
            writer.write(Integer.MAX_VALUE+"-"+Integer.MAX_VALUE+"-"+Integer.MAX_VALUE+"-"+Integer.MAX_VALUE+"-"+Integer.MAX_VALUE);
            writer.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getHighScore(){
        return topScores.get(0);
    }

    public int getHighTile(){
        return topTiles.get(0);
    }

    public long getFastestTime(){
        return topTimes.get(0);
    }

    public ArrayList<Integer> getTopScores() {
        return topScores;
    }

    public ArrayList<Integer> getTopTiles() {
        return topTiles;
    }

    public ArrayList<Long> getTopTimes() {
        return topTimes;
    }

}
