import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

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
    private Random randomSeed;
    private double hunger = 1.0;

    SnakeGame(int numTiles, long randomSeed) {
        if(randomSeed == -1) randomSeed = (long)(Math.random()*Long.MAX_VALUE);
        this.randomSeed = new Random(randomSeed);
        this.numTiles = numTiles;
        gameBoard = new int[numTiles][numTiles];
        resetGameBoard(gameBoard);
        showDisplay = false;
    }//constructor

    SnakeGame(int numTiles, int tileSize, Display display, boolean human) {
        long seed = (long)(Math.random()*Long.MAX_VALUE);
        randomSeed = new Random(seed);
        System.out.println("Random seed = " + seed);
        this.numTiles = numTiles;
        gameBoard = new int[numTiles][numTiles];
        resetGameBoard(gameBoard);
        showDisplay = true;
        jFrame = new JFrame("Snake Game");
        jFrame.setSize(tileSize * numTiles + 7, tileSize * numTiles + 36);
        jFrame.setAlwaysOnTop(true);
        jFrame.setDefaultCloseOperation(3);
        jFrame.setLocation(650, 240);
        this.display = display;
        display.setGameBoard(gameBoard);
        display.repaint();
        jFrame.add(display);
        jFrame.setResizable(false);
        jFrame.setVisible(true);
        jFrame.addKeyListener(getKeyListener());
        if (human) humanPlayGame();
        else functionPlayGame(false, null);
    }//constructor

    SnakeGame(int numTiles, int tileSize, Display display, NeuralNet neuralNet, boolean showDisplay, long seed){
        if(seed == -1)seed = (long)(Math.random()*Long.MAX_VALUE);
        randomSeed = new Random(seed);
        this.numTiles = numTiles;
        gameBoard = new int[numTiles][numTiles];
        resetGameBoard(gameBoard);
        this.showDisplay = showDisplay;
        if(showDisplay){
            jFrame = new JFrame("Snake Game");
            jFrame.setSize(tileSize * numTiles + 7, tileSize * numTiles + 36);
            jFrame.setAlwaysOnTop(true);
            jFrame.setDefaultCloseOperation(3);
            jFrame.setLocation(650, 240);
            this.display = display;
            display.setGameBoard(gameBoard);
            display.repaint();
            jFrame.add(display);
            jFrame.setResizable(false);
            jFrame.setVisible(true);
            jFrame.addKeyListener(getKeyListener());
            functionPlayGame(true, neuralNet);
        } else{
            //do testing experiments here
        }
    }

    private KeyListener getKeyListener() {
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == 38) {//up
                    begin = true;
                    if (prevOrientation != 2) snakeOrientation = 0;
                } else if (keyCode == 39) {//right
                    begin = true;
                    if (prevOrientation != 3) snakeOrientation = 1;
                } else if (keyCode == 40) {//down
                    begin = true;
                    if (prevOrientation != 0) snakeOrientation = 2;
                } else if (keyCode == 37) {//left
                    begin = true;
                    if (prevOrientation != 1) snakeOrientation = 3;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };
        return keyListener;
    }

    private void resetGameBoard(int[][] gameBoard) {
        score = 0;
        timeSurvived = 0;
        gameOver = false;
        for (int row = 0; row < numTiles; row++) {
            for (int col = 0; col < numTiles; col++) {
                gameBoard[row][col] = 0;
            }
        }
        snake = new Node(numTiles / 2, numTiles / 2, null, null);
        snake.next = new Node(numTiles / 2, numTiles / 2 - 1, null, snake);
        snake.next.next = new Node(numTiles / 2, numTiles / 2 - 2, null, snake.next);
        gameBoard[numTiles / 2][numTiles / 2] = 1;
        gameBoard[numTiles / 2][numTiles / 2 - 1] = 2;
        gameBoard[numTiles / 2][numTiles / 2 - 2] = 2;
        snakeOrientation = 1;
        prevOrientation = 1;
        snakeSize = 3;
        generateApple();
    }//resetGameBoard

    private void humanPlayGame() {
        long startTime = System.currentTimeMillis();
        while (!gameOver) {
            if (System.currentTimeMillis() - startTime > animationDelay) {
                startTime = System.currentTimeMillis();
                if (begin) {
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
        display.setGameOver(score, timeSurvived);
        System.out.println("Gameover = " + gameOver);
    }//humanPlayGame

    private void functionPlayGame(boolean AI, NeuralNet neuralNet) {
        long startTime = System.currentTimeMillis();
        hunger = 1.0;
        while (!gameOver) {
            if (System.currentTimeMillis() - startTime > animationDelay / 2) {
                startTime = System.currentTimeMillis();
                int moveRow = snake.row;
                int moveCol = snake.col;
                //if(!AI) {//old way
                    int move = 0;
                    double[] moves;
                    if(AI) moves = neuralNet.calculate(getTrainingRow());
                    else moves = calculateBestMove(0, snakeOrientation, false);
                    double bestScore = -2000000;
                    for (int i = 0; i < 3; i++) {
                        if (moves[i] > bestScore) {
                            bestScore = moves[i];
                            move = i;
                        }
                    }
                    if (snakeOrientation == 0) {//0 = North
                        if (move == 0) moveCol--;
                        else if (move == 1) moveRow--;
                        else moveCol++;
                    } else if (snakeOrientation == 1) {//1 = East
                        if (move == 0) moveRow--;
                        else if (move == 1) moveCol++;
                        else moveRow++;
                    } else if (snakeOrientation == 2) {//2 = South
                        if (move == 0) moveCol++;
                        else if (move == 1) moveRow++;
                        else moveCol--;
                    } else {//3 = West
                        if (move == 0) moveRow++;
                        else if (move == 1) moveCol--;
                        else moveRow--;
                    }
                    if (moveRow < snake.row) snakeOrientation = 0; //north
                    else if (moveCol > snake.col) snakeOrientation = 1; //east
                    else if (moveRow > snake.row) snakeOrientation = 2; //south
                    else snakeOrientation = 3; //west
                //}//old way
                /*else{
                    double[] outputLayer = neuralNet.calculate(getTrainingRow()); //N,E,S,W
                    double max = -2000000;
                    int orientation = 0;
                    for(int i = 0; i < outputLayer.length; i++){
                        if(outputLayer[i] > max){
                            max = outputLayer[i];
                            orientation = i;
                        }
                    }
                    if (orientation == 0) {//0 = North
                        if(snakeOrientation == 2) moveRow++;
                        else {
                            moveRow--;
                            snakeOrientation = orientation;
                        }
                    } else if (orientation == 1) {//1 = East
                        if(snakeOrientation == 3) moveCol--;
                        else {
                            moveCol++;
                            snakeOrientation = orientation;
                        }
                    } else if (orientation == 2) {//2 = South
                        if(snakeOrientation == 0) moveRow--;
                        else {
                            moveRow++;
                            snakeOrientation = orientation;
                        }
                    } else {//3 = West
                        if(snakeOrientation == 1) moveCol++;
                        else {
                            moveCol--;
                            snakeOrientation = orientation;
                        }
                    }
                }*///old way
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
                        hunger = 1.0;
                        score++;
                    }
                    timeSurvived++;
                } else gameOver = true;
                display.repaint();
                hunger += 0.01;
            }
        }
        display.setGameOver(score, timeSurvived);
        System.out.println("Gameover = " + gameOver);
    }//AIPlayGame

    double[] getTrainingRow(){
        /* Old way:
        double[] trainingRow = new double[numTiles*numTiles+2+4];
        int index = 0;
        for(int row = 0; row < numTiles; row++){
            for(int col = 0; col < numTiles; col++){
                trainingRow[index] = gameBoard[row][col];
                index++;
            }
        }
        trainingRow[index] = (snake.row * numTiles + snake.col);
        index++;
        trainingRow[index] = (appleRow * numTiles + appleCol);
        index++;
        for(int i = 0; i < 4; i++){
            if(snakeOrientation == i) trainingRow[index] = 1;
            else trainingRow[index] = 0;
            index++;
        }*/
        return calculateBestMove(0,snakeOrientation,true);
    }

    double[] playAIMove() {
        double[] moves = calculateBestMove(0, snakeOrientation, false);
        double bestScore = -2000000;
        int move = 0;
        for (int i = 0; i < 3; i++) {
            if (moves[i] > bestScore) {
                bestScore = moves[i];
                move = i;
            }
        }
        int moveRow = snake.row;
        int moveCol = snake.col;
        if (snakeOrientation == 0) {//0 = North
            if (move == 0) moveCol--;
            else if (move == 1) moveRow--;
            else moveCol++;
        } else if (snakeOrientation == 1) {//1 = East
            if (move == 0) moveRow--;
            else if (move == 1) moveCol++;
            else moveRow++;
        } else if (snakeOrientation == 2) {//2 = South
            if (move == 0) moveCol++;
            else if (move == 1) moveRow++;
            else moveCol--;
        } else {//3 = West
            if (move == 0) moveRow++;
            else if (move == 1) moveCol--;
            else moveRow--;
        }
        if (moveRow < snake.row) snakeOrientation = 0; //north
        else if (moveCol > snake.col) snakeOrientation = 1; //east
        else if (moveRow > snake.row) snakeOrientation = 2; //south
        else snakeOrientation = 3; //west
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
                hunger = 1.0;
                score++;
            }
            timeSurvived++;
        } else gameOver = true;
        hunger += 0.01;
        /*double[] moveOutput = {0.0, 0.0, 0.0, 0.0};
        moveOutput[snakeOrientation] = 1.0;*/
        double[] moveOutput = {0.0, 0.0, 0.0};
        moveOutput[move] = 1.0;
        return moveOutput;
    }//playAIMove

    double[] calculateBestMove(int depth, int snakeOrientation, boolean AI) {
        //0 = Left, 1 = Straight, 2 = Right
        double[] moveScores = {0, 0, 0};
        double[] inputRow = new double[12]; //spaces available[0-2], distance to apple[3-5], death[6-8], apple eaten[9-11]
        for (int i = 0; i < 3; i++) {
            int moveRow = snake.row;
            int moveCol = snake.col;
            int prevOrientation = snakeOrientation;
            if (snakeOrientation == 0) {//0 = North
                if (i == 0) moveCol--;
                else if (i == 1) moveRow--;
                else moveCol++;
            } else if (snakeOrientation == 1) {//1 = East
                if (i == 0) moveRow--;
                else if (i == 1) moveCol++;
                else moveRow++;
            } else if (snakeOrientation == 2) {//2 = South
                if (i == 0) moveCol++;
                else if (i == 1) moveRow++;
                else moveCol--;
            } else {//3 = West
                if (i == 0) moveRow++;
                else if (i == 1) moveCol--;
                else moveRow--;
            }
            if (moveRow < snake.row) snakeOrientation = 0; //north
            else if (moveCol > snake.col) snakeOrientation = 1; //east
            else if (moveRow > snake.row) snakeOrientation = 2; //south
            else snakeOrientation = 3; //east

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
                if (depth < 1) { // calculate future moves
                    if(!AI) {
                        double[] moves = calculateBestMove(depth + 1, snakeOrientation, AI);
                        int bestMove = 0;
                        double bestScore = Double.MIN_VALUE;
                        for (int j = 0; j < 3; j++) {
                            if (moves[j] > bestScore) {
                                bestScore = moves[j];
                                bestMove = j;
                            }
                        }
                        moveScores[i] = moves[bestMove];
                        if (moveScores[i] == -1000000) moveScores[i] = -500000;
                        if (appleEaten) {
                            moveScores[i] = moveScores[i] + 10;
                        }
                    }
                    else {
                        double[] inputs = calculateBestMove(depth + 1, snakeOrientation, AI);
                        double bestSpaces = 0;
                        double bestDistance = 1.0;
                        double futureAppleEaten = 0;
                        double death = 1.0;
                        for (int j = 0; j < 3; j++) {
                            if (inputs[j + 6] != 1.0) {
                                if (bestSpaces < inputs[i]) bestSpaces = inputs[i];
                                if (bestDistance > inputs[i + 3]) bestDistance = inputs[i + 3];
                                if (inputs[i + 9] == 1.0) futureAppleEaten = 1.0;
                                death = 0.0;
                            }
                        }
                        if (appleEaten && death != 1.0) futureAppleEaten = 1.0;
                        inputRow[i] = bestSpaces;
                        inputRow[i + 3] = bestDistance;
                        inputRow[i + 6] = death;
                        inputRow[i + 9] = futureAppleEaten;
                    }
                }// calculate future moves
                else {
                    if(!AI){
                        if (appleEaten) {
                            moveScores[i] = moveScores[i] + 10;
                        } else inputRow[i+9] = 0.0;
                        //calculate available spaces here//
                        int[][] countedSpaces = new int[numTiles][numTiles];
                        for (int row = 0; row < numTiles; row++) {
                            for (int col = 0; col < numTiles; col++) {
                                countedSpaces[row][col] = 0;
                            }
                        }
                        countedSpaces[snake.row][snake.col] = 1;
                        //Gameboard spaces available:
                        double availableSpaces = calcSpacesAvailable(countedSpaces, snake.row, snake.col);
                        moveScores[i] = moveScores[i] + 4 * availableSpaces;
                        //Distance to apple:
                        double distance = Math.sqrt(Math.pow((snake.row - appleRow), 2) + Math.pow((snake.col - appleCol), 2)); //Euclidean Distance
                        //double maxDistance = Math.sqrt(Math.pow((0 - 11), 2) + Math.pow((0 - 11), 2)); //Euclidean Distance
                        moveScores[i] = moveScores[i] - hunger * distance;//moveScores[i] = moveScores[i] - hunger * (Math.abs(snake.row - appleRow) + Math.abs(snake.col - appleCol)); //Manhatten Distance


                    }
                    else {
                        inputRow[i + 6] = 0.0;
                        if (appleEaten) {
                            inputRow[i + 9] = 1.0;
                        } else inputRow[i + 9] = 0.0;
                        //calculate available spaces here//
                        int[][] countedSpaces = new int[numTiles][numTiles];
                        for (int row = 0; row < numTiles; row++) {
                            for (int col = 0; col < numTiles; col++) {
                                countedSpaces[row][col] = 0;
                            }
                        }
                        countedSpaces[snake.row][snake.col] = 1;
                        //Gameboard spaces available:
                        double availableSpaces = calcSpacesAvailable(countedSpaces, snake.row, snake.col);
                        inputRow[i] = availableSpaces / 141.0; //normalize spaces
                        //Distance to apple:
                        double distance = Math.sqrt(Math.pow((snake.row - appleRow), 2) + Math.pow((snake.col - appleCol), 2)); //Euclidean Distance
                        //double maxDistance = Math.sqrt(Math.pow((0 - 11), 2) + Math.pow((0 - 11), 2)); //Euclidean Distance
                        inputRow[i + 3] = (hunger * distance) / 15.55634918; //normalize distance (before hunger)
                    }
                } //calculate score
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
                if(!AI) moveScores[i] = -1000000;
                else {
                    inputRow[i] = 0.0; //spaces available
                    inputRow[i + 3] = 1.0; //distance to apple
                    inputRow[i + 6] = 1.0; //death
                    inputRow[i + 9] = 0.0; //apple eaten
                }
            }
            snakeOrientation = prevOrientation;
        }
        if(AI) return inputRow;
        else return moveScores;
    }

    private int calcSpacesAvailable(int[][] countedSquares, int row, int col) {
        int spaces = 0;
        int moveRow;
        int moveCol;
        int[] rowMoves = {row - 1, row + 1, row, row};
        int[] colMoves = {col, col, col - 1, col + 1};
        for (int i = 0; i < 4; i++) {
            moveRow = rowMoves[i];
            moveCol = colMoves[i];
            if (moveRow >= 0 && moveCol >= 0 && moveRow < numTiles && moveCol < numTiles) {
                if (countedSquares[moveRow][moveCol] != 1 && gameBoard[moveRow][moveCol] != 2) {
                    countedSquares[moveRow][moveCol] = 1;
                    spaces++;
                    spaces += calcSpacesAvailable(countedSquares, moveRow, moveCol);
                }
            }
        }
        return spaces;
    }//calcRecursive

    private boolean collision(int moveRow, int moveCol) {
        if (moveRow < 0 || moveRow >= numTiles) return true;
        else if (moveCol < 0 || moveCol >= numTiles) return true;
        else return gameBoard[moveRow][moveCol] == 2;
    }//collision

    private void generateApple() {
        for (; ; ) {
            int randRow = (int) (randomSeed.nextDouble() * numTiles);
            int randCol = (int) (randomSeed.nextDouble() * numTiles);
            if (gameBoard[randRow][randCol] == 0) { //empty
                gameBoard[randRow][randCol] = 3;
                appleRow = randRow;
                appleCol = randCol;
                break;
            }
        }
    }//generateApple

    void closeDisplay() {
        jFrame.dispose();
    }

    boolean isGameOver(){
        return gameOver;
    }
}
