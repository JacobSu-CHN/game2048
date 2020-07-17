package com.jacobsu.game;

import com.jacobsu.gui.PlayPanel;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

public class GameBoard {

    public static final int ROWS = 4;
    public static final int COLS = 4;

    private final int startingTiles = 2;
    private Tile[][] board;
    private boolean dead;
    private boolean won;
    private BufferedImage gameBoard;
    private BufferedImage finalBoard;//maybe
    private int x;
    private int y;
    //private int score=0;
    //private int highScore=0;
    //private Font scoreFont;

    private static int SPACING = 10;
    public static int BOARD_WIDTH = (COLS + 1) * SPACING + COLS * Tile.WIDTH;
    public static int BOARD_HEIGHT = (ROWS + 1) * SPACING + ROWS * Tile.HEIGHT;


    private long elapseMS;
    //private long fastestMS;
    private long startTime;
    private boolean hasStared;
    //private String formattedTime="00:00:000";
    private int saveCount;

    //Saving
    private String saveDataPath;
    private String fileName = "SaveData";

    private AudioHandler audio;
    private ScoreManager scores;
    private Leaderboards lBoard;
    private PlayPanel playPanel;

    public GameBoard(int x, int y) {
        /*try {
            saveDataPath=GameBoard.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            //saveDataPath=System.getProperty("user.home")+"\\foldername";
        }catch (Exception e){
            e.printStackTrace();
        }
        scoreFont=Game.main.deriveFont(24f);

         */
        this.x = x;
        this.y = y;
        board = new Tile[ROWS][COLS];
        //设置游戏界面样式
        gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
        finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
        //startTime=System.nanoTime();

        //loadHighScore();
        createBoardImage();
        //start();

        audio = AudioHandler.getInstance();//获取实例
        //加载音乐
        audio.load("bgm.mp3", "BGM");
        audio.load("click.wav", "click");

        //设置背景音乐音量并循环播放
        audio.adjustVolume("BGM", -10);
        audio.play("BGM", Clip.LOOP_CONTINUOUSLY);


        lBoard = Leaderboards.getInstance();//获取实例
        lBoard.loadScores();
        scores = new ScoreManager(this);
        scores.loadGame();
        scores.setBestTime(lBoard.getFastestTime());
        scores.setCurrentTopScore(lBoard.getHighScore());
        if (scores.newGame()) {
            start();
            scores.saveGame();
        } else {
            for (int i = 0; i < scores.getBoard().length; i++) {
                if (scores.getBoard()[i] == 0) {
                    continue;
                }
                spawn(i / ROWS, i % COLS, scores.getBoard()[i]);
            }

            dead = checkDead();
            won = checkWon();
        }
    }

    /*
    private void createSaveData(){
        try {
            File file=new File(saveDataPath,fileName);

            FileWriter output =new FileWriter(file);
            BufferedWriter writer=new BufferedWriter(output);
            writer.write(""+0);
            writer.newLine();
            writer.write(""+Integer.MAX_VALUE);
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void loadHighScore() {
        try {
            File f=new File(saveDataPath,fileName);
            if(!f.isFile()){
                createSaveData();
            }
            BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            highScore=Integer.parseInt(reader.readLine());
            fastestMS=Long.parseLong(reader.readLine());
            reader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void setHighScore(){
        FileWriter output =null;

        try {
            File f =new File(saveDataPath,fileName);
            output=new FileWriter(f);
            BufferedWriter writer=new BufferedWriter(output);

            writer.write(""+highScore);
            writer.newLine();

            if (elapseMS<=fastestMS&&won){
                writer.write(""+elapseMS);
            }else {
                writer.write(""+fastestMS);
            }

            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    */
    public void reset() {
        board = new Tile[ROWS][COLS];
        start();
        scores.saveGame();
        dead = false;
        won = false;
        hasStared = false;
        startTime = System.nanoTime();
        elapseMS = 0;
        saveCount = 0;
    }
    private void start() {
        for (int i = 0; i < startingTiles; i++) {
            spawnRandom();
        }
       /*
        spawn(0,0,2);
        spawn(0,1,2);
        spawn(0,2,2);
        spawn(0,3,2);
        */
    }
    public void spawn(int row, int col, int value) {
        board[row][col] = new Tile(value, getTileX(col), getTileY(row));
    }

    private void createBoardImage() {
        Graphics2D g = (Graphics2D) gameBoard.getGraphics();
        g.setColor(Color.darkGray);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        g.setColor(Color.lightGray);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int x = SPACING + SPACING * col + Tile.WIDTH * col;
                int y = SPACING + SPACING * row + Tile.HEIGHT * row;
                g.fillRoundRect(x, y, Tile.WIDTH, Tile.HEIGHT, Tile.ARC_WIDTH, Tile.ARC_HEIGHT);
            }
        }
    }

    public void update() {
        saveCount++;
        if (saveCount >= 120) {
            saveCount = 0;
            scores.saveGame();
        }
        if (!won && !dead) {
            if (hasStared) {
                elapseMS = (System.nanoTime() - startTime) / 1000000;
                //formattedTime= formatTime(elapseMS);
                scores.setTime(elapseMS);
            } else {
                startTime = System.nanoTime();
            }
        }

        checkKeys();
        /*
        if(score>=highScore){
            highScore=score;
        }

         */
        if (scores.getCurrentScore() > scores.getCurrentTopScore()) {
            scores.setCurrentTopScore(scores.getCurrentScore());
        }

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Tile current = board[row][col];
                if (current == null) {
                    continue;
                }
                current.update();
                resetPosition(current, row, col);
                if (current.getValue() == 2048) {
                    //won = true;
                    setWon(true);
                }
            }
        }
    }
    public void render(Graphics2D g) {
        BufferedImage finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) finalBoard.getGraphics();
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        g2d.drawImage(gameBoard, 0, 0, null);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Tile current = board[row][col];
                if (current == null) {
                    continue;
                }
                current.render(g2d);
            }
        }

        g.drawImage(finalBoard, x, y, null);
        g2d.dispose();


        /*
        g.setColor(Color.lightGray);
        g.setFont(scoreFont);
        g.drawString(""+score,30,40);
        g.setColor(Color.PINK);
        g.drawString("Best: "+highScore,Game.WIDTH-DrawUtils.getMessageWidth("Best: "+highScore,scoreFont,g)-20,40);
        g.setColor(Color.lightGray);
        g.drawString("Time: "+formattedTime,30,90);
        g.setColor(Color.PINK);
        g.drawString("Fastest: "+formatTime(fastestMS),Game.WIDTH-DrawUtils.getMessageWidth("Fastest: "+formatTime(fastestMS),scoreFont,g)-20,90);
        */
    }
    private void resetPosition(Tile current, int row, int col) {
        if (current == null) {
            return;
        }
        int x = getTileX(col);
        int y = getTileY(row);

        int distX = current.getX() - x;
        int distY = current.getY() - y;

        if (Math.abs(distX) < Tile.SLIDE_SPEED) {
            current.setX(current.getX() - distX);
        }
        if (Math.abs(distY) < Tile.SLIDE_SPEED) {
            current.setY(current.getY() - distY);
        }

        if (distX < 0) {
            current.setX(current.getX() + Tile.SLIDE_SPEED);
        }
        if (distY < 0) {
            current.setY(current.getY() + Tile.SLIDE_SPEED);
        }
        if (distX > 0) {
            current.setX(current.getX() - Tile.SLIDE_SPEED);
        }
        if (distY > 0) {
            current.setY(current.getY() - Tile.SLIDE_SPEED);
        }

    }
    public int getTileX(int col) {
        return SPACING + col * Tile.WIDTH + col * SPACING;
    }

    public int getTileY(int row) {
        return SPACING + row * Tile.HEIGHT + row * SPACING;
    }


    private boolean checkOutOfBounds(Direction dir, int row, int col) {
        if (dir == Direction.LEFT) {
            return col < 0;
        } else if (dir == Direction.RIGHT) {
            return col > COLS - 1;
        } else if (dir == Direction.UP) {
            return row < 0;
        } else if (dir == Direction.DOWN) {
            return row > ROWS - 1;
        }
        return false;
    }
    private boolean move(int row, int col, int horizontalDirection, int verticalDirection, Direction dir) {
        boolean canMove = false;

        Tile current = board[row][col];
        if (current == null) {
            return false;
        }
        boolean move = true;
        int newCol = col;
        int newRow = row;
        while (move) {
            newCol += horizontalDirection;
            newRow += verticalDirection;
            if (checkOutOfBounds(dir, newRow, newCol)) {
                break;
            }
            if (board[newRow][newCol] == null) {
                board[newRow][newCol] = current;
                board[newRow - verticalDirection][newCol - horizontalDirection] = null;
                board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
                canMove = true;
            } else if (board[newRow][newCol].getValue() == current.getValue() && board[newRow][newCol].canCombine()) {
                board[newRow][newCol].setCanCombine(false);
                board[newRow][newCol].setValue(board[newRow][newCol].getValue() * 2);
                canMove = true;
                board[newRow - verticalDirection][newCol - horizontalDirection] = null;
                board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
                board[newRow][newCol].setCombineAnimation(true);
                //score+=board[newRow][newCol].getValue();
                scores.setCurrentScore(scores.getCurrentScore() + board[newRow][newCol].getValue());
            } else {
                move = false;
            }
        }

        return canMove;
    }



    public void moveTiles(Direction dir) {
        boolean canMove = false;
        int horizontalDirection = 0;
        int verticalDirection = 0;

        if (dir == Direction.LEFT) {
            horizontalDirection = -1;
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (!canMove) {
                        canMove = move(row, col, horizontalDirection, verticalDirection, dir);
                    } else {
                        move(row, col, horizontalDirection, verticalDirection, dir);
                    }
                }
            }
        } else if (dir == Direction.RIGHT) {
            horizontalDirection = 1;
            for (int row = 0; row < ROWS; row++) {
                for (int col = COLS - 1; col >= 0; col--) {
                    if (!canMove) {
                        canMove = move(row, col, horizontalDirection, verticalDirection, dir);
                    } else {
                        move(row, col, horizontalDirection, verticalDirection, dir);
                    }
                }
            }
        } else if (dir == Direction.UP) {
            verticalDirection = -1;
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (!canMove) {
                        canMove = move(row, col, horizontalDirection, verticalDirection, dir);
                    } else {
                        move(row, col, horizontalDirection, verticalDirection, dir);
                    }
                }
            }
        } else if (dir == Direction.DOWN) {
            verticalDirection = 1;
            for (int row = ROWS - 1; row >= 0; row--) {
                for (int col = 0; col < COLS; col++) {
                    if (!canMove) {
                        canMove = move(row, col, horizontalDirection, verticalDirection, dir);
                    } else {
                        move(row, col, horizontalDirection, verticalDirection, dir);
                    }
                }
            }
        } else {
            System.out.println(dir + "is not a valid direction.");
        }
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Tile current = board[row][col];
                if (current == null) {
                    continue;
                }
                current.setCanCombine(true);
            }
        }
        if (canMove) {
            audio.play("click", 0);
            spawnRandom();
            //checkDead();
            setDead(checkDead());
        }
    }
    private boolean checkDead() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == null) {
                    return false;
                }
                boolean canCombine = checkSurroundingTiles(row, col, board[row][col]);
                if (canCombine) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkWon() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == null) {
                    continue;
                }
                if (board[row][col].getValue() >= 2048) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkSurroundingTiles(int row, int col, Tile current) {
        if (row > 0) {
            Tile check = board[row - 1][col];
            if (check == null) {
                return true;
            }
            if (current.getValue() == check.getValue()) {
                return true;
            }
        }
        if (row < ROWS - 1) {
            Tile check = board[row + 1][col];
            if (check == null) {
                return true;
            }
            if (current.getValue() == check.getValue()) {
                return true;
            }
        }
        if (col > 0) {
            Tile check = board[row][col - 1];
            if (check == null) {
                return true;
            }
            if (current.getValue() == check.getValue()) {
                return true;
            }
        }
        if (col < COLS - 1) {
            Tile check = board[row][col + 1];
            if (check == null) {
                return true;
            }
            if (current.getValue() == check.getValue()) {
                return true;
            }
        }
        return false;
    }

    private void spawnRandom() {
        Random random = new Random();
        boolean notValid = true;

        while (notValid) {
            int location = random.nextInt(ROWS * COLS);
            int row = location / ROWS;
            int col = location % COLS;
            Tile current = board[row][col];
            if (current == null) {
                int value = random.nextInt(10) < 9 ? 2 : 4;
                Tile tile = new Tile(value, getTileX(col), getTileY(row));
                board[row][col] = tile;
                notValid = false;
            }
        }
    }




    /*
    private String formatTime(long millis){
        String formattedTime;

        String hourFormat="";
        int hours=(int)(millis / 3600000);
        if (hours>=1){
            millis-=hours*3600000;
            if (hours<10){
                hourFormat="0"+hours;
            }else {
                hourFormat=""+hours;
            }
            hourFormat+=":";
        }


        String minuteFormat;
        int minutes=(int)(millis/60000);
        if(minutes>=1){
            millis-=minutes*60000;
            if (minutes<10){
                minuteFormat="0"+minutes;
            }else {
                minuteFormat=""+minutes;
            }
        }else {
            minuteFormat="00";
        }
        String secondFormat;
        int seconds=(int)(millis/1000);
        if(seconds>=1){
            millis-=seconds*1000;
            if (seconds<10){
                secondFormat="0"+seconds;
            }else {
                secondFormat=""+seconds;
            }
        }else {
            secondFormat="00";
        }

        String milliFormat;
        if (millis>99){
            milliFormat=""+millis;
        }else if(millis>9){
            milliFormat="0"+millis;
        }else{
            milliFormat="00"+millis;
        }

        formattedTime=hourFormat+minuteFormat+":"+secondFormat+":"+milliFormat;
        return formattedTime;
    }

     */





    /*
        private void checkDead() {
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if(board[row][col]==null){
                        return;
                    }
                    if(checkSurroundingTiles(row,col,board[row][col])){
                        return;
                    }
                }
            }

            dead=true;
            if (score>=highScore){
                highScore=score;
            }
            setHighScore();
        }

     */

    /*
        private void checkKeys() {
            if (Keyboard.typed(KeyEvent.VK_LEFT)) {
                moveTiles(Direction.LEFT);
                if (!hasStared) {
                    hasStared = true;
                }
            }
            if (Keyboard.typed(KeyEvent.VK_RIGHT)) {
                moveTiles(Direction.RIGHT);
                if (!hasStared) {
                    hasStared = true;
                }
            }
            if (Keyboard.typed(KeyEvent.VK_UP)) {
                moveTiles(Direction.UP);
                if (!hasStared) {
                    hasStared = true;
                }
            }
            if (Keyboard.typed(KeyEvent.VK_DOWN)) {
                moveTiles(Direction.DOWN);
                if (!hasStared) {
                    hasStared = true;
                }
            }
        }

     */
    private void checkKeys() {
        if (Keyboard.typed(KeyEvent.VK_LEFT)) {
            moveTiles(Direction.LEFT);
            if (!hasStared) {
                hasStared = !dead;
            }
        }
        if (Keyboard.typed(KeyEvent.VK_RIGHT)) {
            moveTiles(Direction.RIGHT);
            if (!hasStared) {
                hasStared = !dead;
            }
        }
        if (Keyboard.typed(KeyEvent.VK_UP)) {
            moveTiles(Direction.UP);
            if (!hasStared) {
                hasStared = !dead;
            }
        }
        if (Keyboard.typed(KeyEvent.VK_DOWN)) {
            moveTiles(Direction.DOWN);
            if (!hasStared) {
                hasStared = !dead;
            }
        }
    }

    public int getHighestTileValue(){
        int value=2;
        for (int row=0;row<ROWS;row++){
            for (int col=0;col<COLS;col++){
                if(board[row][col]==null){
                    continue;
                }
                if (board[row][col].getValue()>value){
                    value=board[row][col].getValue();
                }
            }
        }
        return value;
    }

    public boolean isDead(){
        return dead;
    }
    public void setDead(boolean dead){
        if (!this.dead&&dead){
            lBoard.addTile(getHighestTileValue());
            lBoard.addScore(scores.getCurrentScore());
            lBoard.saveScores();
        }
        this.dead=dead;
    }

    public boolean isWon(){
        return won;
    }
    public void setWon(boolean won){
        if (!this.won&&won){
            lBoard.addTime(scores.getTime());
            lBoard.saveScores();
        }
        this.won=won;
    }

    public ScoreManager getScores(){
        return scores;
    }

    public Tile[][] getBoard() {
        return board;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
