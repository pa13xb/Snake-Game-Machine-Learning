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
        else functionPlayGame();
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
        snake = new Node(numTiles/2, numTiles/2, null, null);
        snake.next = new Node(numTiles/2, numTiles/2 - 1, null, snake);
        snake.next.next = new Node(numTiles/2, numTiles/2 - 2, null, snake.next);
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
                                    curNode.next = new Node(prevRow, prevCol, null, curNode);
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

    private void functionPlayGame(){
        long startTime = System.currentTimeMillis();
        while(!gameOver){
            if(System.currentTimeMillis() - startTime > animationDelay/2) {
                startTime = System.currentTimeMillis();
                int move = calculateBestMove();
                int moveRow = snake.row;
                int moveCol = snake.col;
                if(snakeOrientation == 0) {//0 = North
                    if(move == 0) moveCol--;
                    else if(move == 1) moveRow--;
                    else moveCol++;
                }
                else if(snakeOrientation == 1) {//1 = East
                    if(move == 0) moveRow--;
                    else if(move == 1) moveCol++;
                    else moveRow++;
                }
                else if(snakeOrientation == 2) {//2 = South
                    if(move == 0) moveCol++;
                    else if(move == 1) moveRow++;
                    else moveCol--;
                }
                else {//3 = West
                    if(move == 0) moveRow++;
                    else if(move == 1) moveCol--;
                    else moveRow--;
                }
                if(moveRow < snake.row) snakeOrientation = 0; //north
                else if(moveCol > snake.col) snakeOrientation = 1; //east
                else if(moveRow > snake.row) snakeOrientation = 2; //south
                else snakeOrientation = 3; //east
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
                                curNode.next = new Node(prevRow, prevCol,null, curNode);
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

    int calculateBestMove(){
        //0 = Left, 1 = Straight, 2 = Right
        double[] moveScores = {0,0,0};
        for(int i = 0; i < 3; i++) {
            int moveRow = snake.row;
            int moveCol = snake.col;
            if(snakeOrientation == 0) {//0 = North
                if(i == 0) moveCol--;
                else if(i == 1) moveRow--;
                else moveCol++;
            }
            else if(snakeOrientation == 1) {//1 = East
                if(i == 0) moveRow--;
                else if(i == 1) moveCol++;
                else moveRow++;
            }
            else if(snakeOrientation == 2) {//2 = South
                if(i == 0) moveCol++;
                else if(i == 1) moveRow++;
                else moveCol--;
            }
            else {//3 = West
                if(i == 0) moveRow++;
                else if(i == 1) moveCol--;
                else moveRow--;
            }

            if (!collision(moveRow, moveCol)) {
                boolean appleEaten = false;
                if (gameBoard[moveRow][moveCol] == 3) appleEaten = true;
                int tailRow = 0; //record tail's position
                int tailCol = 0; //record tail's position
                {//move snake
                    int prevRow = snake.row;
                    int prevCol = snake.col;
                    snake.row = moveRow;
                    snake.col = moveCol;
                    gameBoard[moveRow][moveCol] = 1; //head
                    Node prevNode = snake;
                    for (; ; ) {
                        Node curNode = prevNode.next;
                        if (curNode.next == null) {
                            tailRow = curNode.row;
                            tailCol = curNode.col;
                        }
                        int nextRow = curNode.row;
                        int nextCol = curNode.col;
                        curNode.row = prevRow;
                        curNode.col = prevCol;
                        gameBoard[prevRow][prevCol] = 2; //body
                        prevRow = nextRow;
                        prevCol = nextCol;
                        if (curNode.next == null) {
                            if (appleEaten) {
                                curNode.next = new Node(prevRow, prevCol, null, curNode);
                                gameBoard[prevRow][prevCol] = 2; //body
                            } else gameBoard[prevRow][prevCol] = 0; //empty
                            break;
                        }
                        prevNode = curNode;
                    }
                }//move snake

                //Calculate scores based on gameboard:
                if (appleEaten) {
                    moveScores[i] = moveScores[i] + 10;
                }
                //calculate available spaces here//
                int[][] countedSpaces = new int[numTiles][numTiles];
                for(int row = 0; row < numTiles; row++){
                    for(int col = 0; col < numTiles; col++){
                        countedSpaces[row][col] = 0;
                    }
                }
                countedSpaces[snake.row][snake.col] = 1;
                //Gameboard spaces available:
                moveScores[i] = moveScores[i] + calcRecursive(countedSpaces,snake.row,snake.col);
                //Distance to apple:
                moveScores[i] = moveScores[i] - Math.sqrt(Math.pow((snake.row - appleRow),2) + Math.pow((snake.col - appleCol),2));

                { //Revert snake back to original position
                    Node tail = null;
                    Node curNode = snake;
                    for (; ; ) {
                        if (curNode.next == null) {
                            tail = curNode;
                            break;
                        } else {
                            curNode = curNode.next;
                        }
                    }
                    if (appleEaten) { // if an apple was eaten then tail should shrink because it grew
                        Node temp = tail.prev;
                        temp.next = null;
                        tail = temp;
                    }
                    int prevRow = tail.row;
                    int prevCol = tail.col;
                    tail.row = tailRow;
                    tail.col = tailCol;
                    gameBoard[tailRow][tailCol] = 2; //body
                    Node prevNode = tail;
                    for (; ; ) {
                        curNode = prevNode.prev;
                    /*if(curNode.prev == null){
                        tailRow = curNode.row;
                        tailCol = curNode.col;
                    }*/
                        int nextRow = curNode.row;
                        int nextCol = curNode.col;
                        curNode.row = prevRow;
                        curNode.col = prevCol;
                        prevRow = nextRow;
                        prevCol = nextCol;
                        if (curNode.prev == null) { //curNode == head
                            //System.out.println("null prev node found");
                            gameBoard[prevRow][prevCol] = 1; //move head
                            gameBoard[nextRow][nextCol] = 0; //remove old head
                            gameBoard[appleRow][appleCol] = 3;
                            break;
                        }
                        prevNode = curNode;
                    }
                } //Revert snake back to original position
            } else {
                moveScores[i] = -1000000;
            }
        }
        int bestMove = 0;
        double bestScore = Double.MIN_VALUE;
        for(int i = 0; i < 3; i++){
            if(moveScores[i] > bestScore){
                bestScore = moveScores[i];
                bestMove = i;
            }
        }
        return bestMove;
    }

    private int calcRecursive(int[][] countedSquares, int row, int col){
        int spaces = 0;
        int moveRow;
        int moveCol;
        int[] rowMoves = {row - 1, row + 1, row, row};
        int[] colMoves = {col, col, col - 1, col + 1};
        for(int i = 0; i < 4; i++) {
            moveRow = rowMoves[i];
            moveCol = colMoves[i];
            if (moveRow >= 0 && moveCol >= 0 && moveRow < numTiles && moveCol < numTiles) {
                if (countedSquares[moveRow][moveCol] != 1 && gameBoard[moveRow][moveCol] != 2) {
                    countedSquares[moveRow][moveCol] = 1;
                    spaces++;
                    spaces += calcRecursive(countedSquares, moveRow, moveCol);
                }
            }
        }
        return spaces;
    }//calcRecursive

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