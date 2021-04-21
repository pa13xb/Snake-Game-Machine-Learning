import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class SnakeGame {

    private int numTiles;
    private int[][] gameBoard; //0: empty, 1: head, 2: body, 3: apple
    private int appleRow;
    private int appleCol;
    private int score;
    private int timeSurvived;
    private int animationDelay = 200;
    private Node snake;
    private int snakeSize;
    private boolean gameOver;
    private int prevOrientation;
    private int snakeOrientation; //N,S,E,W = 0,1,2,3
    private boolean begin = false;
    private boolean showDisplay;
    private JFrame jFrame = null;
    private Display display = null;

    SnakeGame(int numTiles){
        this.numTiles = numTiles;
        gameBoard = new int[numTiles][numTiles];
        resetGameBoard(gameBoard);
        showDisplay = false;
    }//constructor

    SnakeGame(int numTiles, int tileSize, Display display, boolean human){
        this.numTiles = numTiles;
        gameBoard = new int[numTiles][numTiles];
        resetGameBoard(gameBoard);
        showDisplay = true;
        jFrame = new JFrame("Snake Game");
        jFrame.setSize(tileSize*numTiles + 19, tileSize*numTiles+48);
        jFrame.setAlwaysOnTop(true);
        jFrame.setDefaultCloseOperation(3);
        jFrame.setLocation(650,240);
        this.display = display;
        display.setGameBoard(gameBoard);
        display.repaint();
        jFrame.add(display);
        jFrame.setVisible(true);
        jFrame.addKeyListener(getKeyListener());
        if(human) humanPlayGame();
        else AIPlayGame();
    }//constructor

    private KeyListener getKeyListener(){
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if(keyCode == 38){//up
                    begin = true;
                    if(prevOrientation != 2) snakeOrientation = 0;
                }
                else if(keyCode == 39){//right
                    begin = true;
                    if(prevOrientation != 3) snakeOrientation = 1;
                }
                else if(keyCode == 40){//down
                    begin = true;
                    if(prevOrientation != 0) snakeOrientation = 2;
                }
                else if(keyCode == 37){//left
                    begin = true;
                    if(prevOrientation != 1) snakeOrientation = 3;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };
        return keyListener;
    }

    private void resetGameBoard(int[][] gameBoard){
        score = 0;
        timeSurvived = 0;
        gameOver = false;
        for(int row = 0; row < numTiles; row++){
            for(int col = 0; col < numTiles; col++){
                gameBoard[row][col] = 0;
            }
        }
        snake = new Node(numTiles/2, numTiles/2, null);
        snake.next = new Node(numTiles/2, numTiles/2 - 1, null);
        snake.next.next = new Node(numTiles/2, numTiles/2 - 2, null);
        gameBoard[numTiles/2][numTiles/2] = 1;
        gameBoard[numTiles/2][numTiles/2 - 1] = 2;
        gameBoard[numTiles/2][numTiles/2 - 2] = 2;
        snakeOrientation = 1;
        prevOrientation = 1;
        snakeSize = 3;
        generateApple();
    }//resetGameBoard

    private void humanPlayGame(){
        long startTime = System.currentTimeMillis();
        while(!gameOver){
            if(System.currentTimeMillis() - startTime > animationDelay) {
                startTime = System.currentTimeMillis();
                if(begin) {
                    int moveRow = snake.row;
                    int moveCol = snake.col;
                    if (snakeOrientation == 0) moveRow--;
                    else if (snakeOrientation == 1) moveCol++;
                    else if (snakeOrientation == 2) moveRow++;
                    else moveCol--;
                    prevOrientation = snakeOrientation;
                    if (!collision(moveRow, moveCol)) {
                        boolean appleEaten = false;
                        if (gameBoard[moveRow][moveCol] == 3) appleEaten = true;
                        int prevRow = snake.row;
                        int prevCol = snake.col;
                        snake.row = moveRow;
                        snake.col = moveCol;
                        gameBoard[moveRow][moveCol] = 1; //head
                        Node prevNode = snake;
                        for (; ; ) {
                            Node curNode = prevNode.next;
                            int nextRow = curNode.row;
                            int nextCol = curNode.col;
                            curNode.row = prevRow;
                            curNode.col = prevCol;
                            gameBoard[prevRow][prevCol] = 2; //body
                            prevRow = nextRow;
                            prevCol = nextCol;
                            if (curNode.next == null) {
                                if (appleEaten) {
                                    curNode.next = new Node(prevRow, prevCol, null);
                                    gameBoard[prevRow][prevCol] = 2; //body
                                } else gameBoard[prevRow][prevCol] = 0; //empty
                                break;
                            }
                            prevNode = curNode;
                        }
                        if (appleEaten) {
                            generateApple();
                            score++;
                        }
                        timeSurvived++;
                    } else gameOver = true;
                    display.repaint();
                }
            }
        }
        display.setGameOver(score,timeSurvived);
        System.out.println("Gameover = "+gameOver);
    }//humanPlayGame

    private void AIPlayGame(){
        long startTime = System.currentTimeMillis();
        while(!gameOver){
            if(System.currentTimeMillis() - startTime > animationDelay/2) {
                startTime = System.currentTimeMillis();
                getAIMove();
                int moveRow = snake.row;
                int moveCol = snake.col;
                if(snakeOrientation == 0) moveRow --;
                else if(snakeOrientation == 1) moveCol ++;
                else if(snakeOrientation == 2) moveRow ++;
                else moveCol--;
                prevOrientation = snakeOrientation;
                if (!collision(moveRow,moveCol)) {
                    boolean appleEaten = false;
                    if(gameBoard[moveRow][moveCol] == 3) appleEaten = true;
                    int prevRow = snake.row;
                    int prevCol = snake.col;
                    snake.row = moveRow;
                    snake.col = moveCol;
                    gameBoard[moveRow][moveCol] = 1; //head
                    Node prevNode = snake;
                    for(;;){
                        Node curNode = prevNode.next;
                        int nextRow = curNode.row;
                        int nextCol = curNode.col;
                        curNode.row = prevRow;
                        curNode.col = prevCol;
                        gameBoard[prevRow][prevCol] = 2; //body
                        prevRow = nextRow;
                        prevCol = nextCol;
                        if(curNode.next == null) {
                            if (appleEaten) {
                                curNode.next = new Node(prevRow, prevCol,null);
                                gameBoard[prevRow][prevCol] = 2; //body
                            }
                            else gameBoard[prevRow][prevCol] = 0; //empty
                            break;
                        }
                        prevNode = curNode;
                    }
                    if(appleEaten){
                        generateApple();
                        score++;
                    }
                    timeSurvived++;
                }
                else gameOver = true;
                display.repaint();
            }
        }
        display.setGameOver(score,timeSurvived);
        System.out.println("Gameover = "+gameOver);
    }//AIPlayGame

    private void getAIMove(){
        int randomMove = (int)(Math.random() * 4);
        if(randomMove == 0){//up
            if(prevOrientation != 2) snakeOrientation = 0;
        }
        else if(randomMove == 1){//right
            if(prevOrientation != 3) snakeOrientation = 1;
        }
        else if(randomMove == 2){//down
            if(prevOrientation != 0) snakeOrientation = 2;
        }
        else if(randomMove == 3){//left
            if(prevOrientation != 1) snakeOrientation = 3;
        }
    }

    private boolean collision(int moveRow, int moveCol) {
        if(moveRow < 0 || moveRow >= numTiles) return true;
        else if (moveCol < 0 || moveCol >= numTiles) return true;
        else return gameBoard[moveRow][moveCol] == 2;
    }//collision

    private void generateApple(){
        for(;;){
            int randRow = (int)(Math.random() * numTiles);
            int randCol = (int)(Math.random() * numTiles);
            if(gameBoard[randRow][randCol] == 0){ //empty
                gameBoard[randRow][randCol] = 3;
                appleRow = randRow;
                appleCol = randCol;
                break;
            }
        }
    }//generateApple

    void closeDisplay(){
        jFrame.dispose();
    }
}
