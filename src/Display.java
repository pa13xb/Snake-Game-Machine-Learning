import javax.swing.*;
import java.awt.*;

class Display extends JPanel {

    private int[][] gameBoard;
    private int numTiles;
    private int tileSize;
    private int score;
    private int timeSurvived;
    private boolean gameOver;

    public Display(int numTiles, int tileSize){
        gameOver = false;
        score = 0;
        timeSurvived = 0;
        this.tileSize = tileSize;
        this.numTiles = numTiles;
        setSize(numTiles*tileSize, numTiles*tileSize);
        setBounds(0,0,numTiles*tileSize+1, numTiles*tileSize+1);
        setVisible(true);
    }

    @Override
    public void paint(Graphics graphics) {
        for(int row = 0; row < numTiles; row++){
            for(int col = 0; col < numTiles; col++){
                Color c = null;
                if(gameBoard[row][col] == 0) c = Color.black;
                else if(gameBoard[row][col] == 1) c = Color.green;
                else if(gameBoard[row][col] == 2) c = Color.yellow;
                else c = Color.red;
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
    }

    void setGameBoard(int[][] gameBoard) {
        this.gameBoard = gameBoard;
    }

    void setGameOver(int score, int timeSurvived){
        gameOver = true;
        this.score = score;
        this.timeSurvived = timeSurvived;
    }

    void reset(){
        gameOver = false;
        score = 0;
        timeSurvived = 0;
    }
}
