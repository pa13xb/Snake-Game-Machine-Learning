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
            System.out.println("2: Let the evaluation function play a game");
            System.out.println("3: Train AI");
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
                        NeuralNet neuralNet = trainAI(scanner);
                        playAIGames(neuralNet, scanner);
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Input must be an integer, try again");
            }
        }
    }

    private NeuralNet trainAI(Scanner scanner){
        try{
            int[] layers;
            System.out.println("Choose number of hidden layers");
            int hiddenLayers = scanner.nextInt();
            layers = new int[hiddenLayers + 2];
            layers[0] = numTiles*numTiles+2+4;
            layers[layers.length-1] = 3;
            for(int i = 1; i <= hiddenLayers; i++){
                System.out.println("Choose number of nodes in hidden layer "+i);
                layers[i] = scanner.nextInt();
            }
            NeuralNet neuralNet = new NeuralNet(layers);
            RProp rProp = new RProp();
            rProp.setIncreaseRate(1.2);
            rProp.setDecreaseRate(0.5);
            rProp.setMaxStep(50);
            rProp.setMinStep(0.0001);
            rProp.setMaxEpochs(100);
            neuralNet.setLearningRule(rProp);
            neuralNet.setActivationFunction(new Sigmoid());
            neuralNet.selfTrain(1000);
            return neuralNet;
        } catch(InputMismatchException e){
            System.out.println("Wrong type of input.");
        }
        return null;
    }

    private void playAIGames(NeuralNet neuralNet, Scanner scanner){
        for(;;){
            try{
                System.out.println("Enter any key to play a game");
                scanner.next();
                //SnakeGame snakeGame = new SnakeGame();
            } catch(InputMismatchException e){
                System.out.print("Input error caught");
            }
        }
    }

    public static void main(String[] args){new Main();}
}
