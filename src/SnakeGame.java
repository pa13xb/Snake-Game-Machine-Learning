import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

/**
 * @author Philip Akkerman
 * studentID 5479613
 * email pa13xb@brocku.ca
 *
 * @author David Hasler
 * studentID 6041321
 * email dh15pd@brocku.ca
 *
 * This is the SnakeGame class that creates a game of Snake. It also has different constructors which allow different
 * methods of playing the game. A human, evaluation function, or neural network can play the Snake game.
 */
class SnakeGame {

    private int numTiles;
    private int[][] gameBoard; //0: empty, 1: head, 2: body, 3: apple
    private int appleRow;
    private int appleCol;
    private int score;
    private int timeSurvived;
    private int animationDelay = 200;
    private int gameOverTimer = 0;
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
    boolean nextMove = false;

    /**
     * SnakeGame constructor for creating the Snake game of a specific size with a random seed.
     *
     * @param numTiles the number of tiles for each row and column of the game board
     * @param randomSeed random seed used to randomize apple locations
     */
    SnakeGame(int numTiles, long randomSeed) {
        if(randomSeed == -1) randomSeed = (long)(Math.random()*Long.MAX_VALUE);
        this.randomSeed = new Random(randomSeed);
        this.numTiles = numTiles;
        gameBoard = new int[numTiles][numTiles];
        resetGameBoard(gameBoard);
        showDisplay = false;
    }//constructor

    /**
     * SnakeGame constructor for creating a game of snake that the human or evaluation function can play.
     *
     * @param numTiles the number of tiles for each row and column of the game board
     * @param tileSize the size of a tile in the game board
     * @param display display used to display the snake game
     * @param human if true then human is playing, false the evaluation function plays
     */
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

    /**
     * SnakeGame constructor for creating a game that the neural network would train and test on.
     *
     * @param numTiles the number of tiles for each row and column of the game board
     * @param tileSize the size of a tile in the game board
     * @param display display used to display the snake game
     * @param neuralNet if true then neural net is playing, false the evaluation function plays
     * @param showDisplay if true then it will show the game of Snake being played
     * @param seed random seed used to randomize apple locations
     */
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
        }
        functionPlayGame(true, neuralNet);
    }

    /**
     * getKeyListener returns a key when it is the right key pressed. For human player input of snake.
     *
     * @return key typed
     */
    private KeyListener getKeyListener() {
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == 38) {//up
                    begin = true;
                    if (prevOrientation != 2) snakeOrientation = 0;
                    nextMove = true;
                } else if (keyCode == 39) {//right
                    begin = true;
                    if (prevOrientation != 3) snakeOrientation = 1;
                    nextMove = true;
                } else if (keyCode == 40) {//down
                    begin = true;
                    if (prevOrientation != 0) snakeOrientation = 2;
                    nextMove = true;
                } else if (keyCode == 37) {//left
                    begin = true;
                    if (prevOrientation != 1) snakeOrientation = 3;
                    nextMove = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) { }
        };
        return keyListener;
    }//getKeyListener

    /**
     * resetGameBoard will reset board that the Snake game is played on.
     *
     * @param gameBoard the board which everything is placed on
     */
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

    /**
     *  A method which allows the human player to play the Snake game.
     */
    private void humanPlayGame() {
        nextMove = false;
        long startTime = System.currentTimeMillis();
        while (!gameOver) {
            if (System.currentTimeMillis() - startTime > animationDelay) {
            //if(nextMove){
                nextMove = false;
                startTime = System.currentTimeMillis();
                if (begin) {
                    int moveRow = snake.row;
                    int moveCol = snake.col;
                    if (snakeOrientation == 0) moveRow--;
                    else if (snakeOrientation == 1) moveCol++;
                    else if (snakeOrientation == 2) moveRow++;
                    else moveCol--;
                    prevOrientation = snakeOrientation;
                    if (collision(moveRow, moveCol)) {
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

    /**
     * functionPlayGame is a method which allows the evaluation function to play the Snake game, or it will also let the
     * neural net train on the evaluation function playing the Snake game.
     *
     * @param AI if true then neural net is playing, false the evaluation function plays
     * @param neuralNet Neural Net which will be used to train
     */
    private void functionPlayGame(boolean AI, NeuralNet neuralNet) {
        long startTime = System.currentTimeMillis();
        hunger = 1.0;
        while (!gameOver) {
            if (!showDisplay || System.currentTimeMillis() - startTime > animationDelay / 2) {
                startTime = System.currentTimeMillis();
                int moveRow = snake.row;
                int moveCol = snake.col;
                //if(!AI) {//old way
                    int move = 0;
                    double[] moves;
                    if(AI) {
                        moves = neuralNet.calculate(getTrainingRow());
                    }
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
                if (collision(moveRow, moveCol)) {
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
                        gameOverTimer = 0;
                    } else gameOverTimer++;
                    if(gameOverTimer > 1000) gameOver = true;
                    timeSurvived++;
                } else gameOver = true;
                if(showDisplay) display.repaint();
                hunger += 0.01;
            }
        }
        if(showDisplay) display.setGameOver(score, timeSurvived);
    }//AIPlayGame

    /**
     * getTrainingRow will calculate a training row to be used as input for the neural network.
     *
     * @return a training row for the neural network
     */
    double[] getTrainingRow(){
        return calculateBestMove(0,snakeOrientation,true);
    }//getTrainingRow

    /**
     *  playAIMove is a method that will use the neural network to play a move in the Snake game.
     *
     * @return returns an array of the move made
     */
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
        if (collision(moveRow, moveCol)) {
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

    /**
     * calculateBestMove will check the left, straight, and right moves to see and return back the best possible score
     * in that direction.
     *
     * @param depth recursive depth to make sure Snake does not do too many recursive calls
     * @param snakeOrientation current orientation of the snake
     * @param AI if using AI then it will change neural network input calculation
     * @return array of ints of best moves
     */
    private double[] calculateBestMove(int depth, int snakeOrientation, boolean AI) {
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

            if (collision(moveRow, moveCol)) {
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
                if (depth < 1) { // calculate future moves (only to a depth of 1)
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
                    else { //evaluation function
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
                    else { //evaluation function
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
                        //System.out.println("Orientation = "+i+", emptySpaces = "+inputRow[i]+", distance = "+inputRow[i+3]);
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

        /*System.out.println("inputRow:");
        for(int i = 0; i < 12; i++){
            System.out.println(i+" = "+inputRow[i]);
        }*/
        if(AI) return inputRow;
        else return moveScores;
    }//calculateBestMove

    /**
     * calcSpacesAvailable is a method that returns the spaces available for the snake.
     *
     * @param countedSquares keeps track of squares counted
     * @param row current row checked
     * @param col current col checked
     * @return number of spaces available
     */
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

    /**
     * collision is a method that returns whether the snake has collided with an object that would end the game.
     *
     * @param moveRow the row to test
     * @param moveCol the column to test
     * @return false if there was a collision
     */
    private boolean collision(int moveRow, int moveCol) {
        if (moveRow < 0 || moveRow >= numTiles) return false;
        else if (moveCol < 0 || moveCol >= numTiles) return false;
        else return gameBoard[moveRow][moveCol] != 2;
    }//collision

    /**
     * generateApple creates a new apple on the game board using the random seed for the snake game
     */
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

    /**
     * closeDisplay closes the current display being used to display the snake game.
     *
     */
    void closeDisplay() {
        jFrame.dispose();
    }//closeDisplay

    /**
     * isGameOver returns whether the game has ended.
     *
     * @return gameOver
     */
    boolean isGameOver(){
        return gameOver;
    }//isGameOver

    /**
     * getScore returns the score of the game.
     *
     * @return score
     */
    int getScore(){
        return score;
    }//getScore
}//SnakeGame
