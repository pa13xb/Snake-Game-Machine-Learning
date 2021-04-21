import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    private int numTiles = 12;
    private int tileSize = 40;

    private Main(){
        Scanner scanner = new Scanner(System.in);
        boolean quit = false;
        while (!quit) {
            System.out.println("1: Play a game (human)");
            System.out.println("2: Let the AI play a game");
            System.out.println("3: Run experiments");
            System.out.println("0: Quit");
            try {
                int input = scanner.nextInt();
                Display display = null;
                switch (input) {
                    case 0:
                        quit = true;
                        break;
                    case 1:
                        display = new Display(numTiles,tileSize);
                        for(;;){
                            SnakeGame snakeGame = new SnakeGame(numTiles, tileSize, display,true);
                            System.out.println("1: Play another game");
                            System.out.println("0: Quit");
                            input = scanner.nextInt();
                            snakeGame.closeDisplay();
                            display.reset();
                            if(input == 0) {
                                quit = true;
                                break;
                            }
                        }
                        break;
                    case 2:
                        display = new Display(numTiles,tileSize);
                        for(;;){
                            SnakeGame snakeGame = new SnakeGame(numTiles, tileSize, display,false);
                            System.out.println("1: Play another game");
                            System.out.println("0: Quit");
                            input = scanner.nextInt();
                            snakeGame.closeDisplay();
                            display.reset();
                            if(input == 0) {
                                quit = true;
                                break;
                            }
                        }
                        break;
                    case 3:
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Input must be an integer, try again");
            }
        }
    }

    public static void main(String[] args){new Main();}
}
