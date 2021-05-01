import javax.swing.*;
import java.awt.*;

/**
 * @author Philip Akkerman
 * studentID 5479613
 * email pa13xb@brocku.ca
 *
 * @author David Hasler
 * studentID 6041321
 * email dh15pd@brocku.ca
 *
 * This is the main class which runs the program and allows the user to select menu options.
 */
class Display extends JPanel {

    private int[][] gameBoard; //the gameboard of tiles represented by integers
    private int numTiles; //the number of tiles wide and high of the gameboard
    private int tileSize; //the size in pixels of each tile
    private int score; //the score of the game to display on game over
    private int timeSurvived; //the time survived to display on game over
    private boolean gameOver; //a state variable indicating game over

    public Display(int numTiles, int tileSize){
        gameOver = false;
        score = 0;
        timeSurvived = 0;
        this.tileSize = tileSize;
        this.numTiles = numTiles;
        setSize(numTiles*tileSize, numTiles*tileSize);
        setBounds(0,0,numTiles*tileSize+1, numTiles*tileSize+1);
        setVisible(true);
    }//constructor

    /**
     * The default paint function which is called on repaint()
     *
     * @param graphics the default graphics object used to draw things
     */
    @Override
    public void paint(Graphics graphics) {
        for(int row = 0; row < numTiles; row++){
            for(int col = 0; col < numTiles; col++){
                Color c = null;
                if(gameBoard[row][col] == 0) c = Color.black; //the background
                else if(gameBoard[row][col] == 1) c = Color.green; //the head of the snake
                else if(gameBoard[row][col] == 2) c = Color.yellow; //the tail of the snake
                else c = Color.red; //the apple
                graphics.setColor(c);
                graphics.fillRect(col*tileSize,row*tileSize,tileSize,tileSize);
                graphics.setColor(Color.white);
                graphics.drawRect(col*tileSize,row*tileSize,tileSize,tileSize);
            }
        }
        if(gameOver){
            graphics.setColor(Color.black);
            graphics.fillRect(tileSize*3 + 1,tileSize * 4 + 1, tileSize * 6 - 1, tileSize * 4 - 1);
            graphics.setColor(Color.white);
            graphics.setFont(new Font("Arial", Font.PLAIN, 40));
            graphics.drawString("Game Over!", (tileSize*3+tileSize/3), (numTiles / 2) * tileSize - tileSize + 12);
            graphics.setFont(new Font("Arial", Font.PLAIN, 30));
            graphics.drawString("Score = "+score, tileSize*3+tileSize/3, ((numTiles / 2) * tileSize)+ 12);
            graphics.setFont(new Font("Arial", Font.PLAIN, 22));
            graphics.drawString("Time Survived = "+timeSurvived, tileSize*3+tileSize/3, ((numTiles / 2) * tileSize) + tileSize + 12);
        }
    }//paint

    /**
     * Sets the state of the gameboard, which includes snake and apple positions
     * @param gameBoard the gameboard, represented by a 2D integer array
     */
    void setGameBoard(int[][] gameBoard) {
        this.gameBoard = gameBoard;
    }//setGameBoard

    /**
     * Sets the display state to game over
     * @param score the score achieved by the agent
     * @param timeSurvived the time survived in the game
     */
    void setGameOver(int score, int timeSurvived){
        gameOver = true;
        this.score = score;
        this.timeSurvived = timeSurvived;
    }//setGameOver

    /**
     * Resets the state of the display to before game over
     */
    void reset(){
        gameOver = false;
        score = 0;
        timeSurvived = 0;
    }//reset
}
