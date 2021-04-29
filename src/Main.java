import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    private int numTiles = 12;
    private int tileSize = 40;
    private String variables = "";

    private Main() {
        Scanner scanner = new Scanner(System.in);
        boolean quit = false;
        while (!quit) {
            System.out.println("1: Play a game (human)");
            System.out.println("2: Let the evaluation function play a game");
            System.out.println("3: Train AI");
            System.out.println("4: Play a game with a saved AI");
            System.out.println("5: Run autonomous experiments");
            System.out.println("0: Quit");
            try {
                int input = scanner.nextInt();
                Display display;
                switch (input) {
                    case 0:
                        quit = true;
                        break;
                    case 1:
                        display = new Display(numTiles, tileSize);
                        for (; ; ) {
                            SnakeGame snakeGame = new SnakeGame(numTiles, tileSize, display, true);
                            System.out.println("1: Play another game");
                            System.out.println("0: Quit");
                            input = scanner.nextInt();
                            snakeGame.closeDisplay();
                            display.reset();
                            if (input == 0) {
                                quit = true;
                                break;
                            }
                        }
                        break;
                    case 2:
                        display = new Display(numTiles, tileSize);
                        for (; ; ) {
                            SnakeGame snakeGame = new SnakeGame(numTiles, tileSize, display, false);
                            System.out.println("1: Play another game");
                            System.out.println("0: Quit");
                            input = scanner.nextInt();
                            snakeGame.closeDisplay();
                            display.reset();
                            if (input == 0) {
                                quit = true;
                                break;
                            }
                        }
                        break;
                    case 3:
                        NeuralNet neuralNet = trainAI(scanner);
                        System.out.println("Parameters used: \n"+variables);
                        System.out.println("Save neural network?\n1: yes\n2: no");
                        input = scanner.nextInt();
                        if (input == 1) saveNeuralNetwork(neuralNet, scanner, "", true);
                        playAIGames(neuralNet, scanner);
                        break;
                    case 4: //test an AI
                        neuralNet = loadNeuralNetwork(scanner);
                        playAIGames(neuralNet, scanner);
                        break;
                    case 5: //run autonomous experiments
                        runExperiments();
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Input must be an integer, try again");
            }
        }
    }

    private NeuralNet trainAI(Scanner scanner) {
        for (; ; ) {
            try {
                variables = "";
                int[] layers;
                System.out.println("Choose number of hidden layers");
                int hiddenLayers = scanner.nextInt();
                layers = new int[hiddenLayers + 2];
                layers[0] = 12;
                layers[layers.length - 1] = 3;
                variables = variables.concat("Layers & Nodes:\n");
                for (int i = 1; i <= hiddenLayers; i++) {
                    System.out.println("Choose number of nodes in hidden layer " + i);
                    layers[i] = scanner.nextInt();
                    variables = variables.concat(i+"\t"+layers[i]+"\n");
                }
                NeuralNet neuralNet = new NeuralNet(layers);
                variables = variables.concat("Learning rule: \trProp\n");
                RProp rProp = new RProp();
                System.out.println("Set IncreaseRate (e.g. 1.2)");
                double increaseRate = scanner.nextDouble();
                rProp.setIncreaseRate(increaseRate);
                variables = variables.concat("Increase rate:\t"+increaseRate+"\n");
                System.out.println("Set DecreaseRate (e.g. 0.5)");
                double decreaseRate = scanner.nextDouble();
                rProp.setDecreaseRate(decreaseRate);
                variables = variables.concat("Decrease rate:\t"+decreaseRate+"\n");
                System.out.println("Set max step (e.g. 50.0)");
                double maxStep = scanner.nextDouble();
                rProp.setMaxStep(maxStep);
                variables = variables.concat("Max step:\t"+maxStep+"\n");
                System.out.println("Set min step (e.g. 0.0001)");
                double minStep = scanner.nextDouble();
                rProp.setMinStep(minStep);
                variables = variables.concat("Min step:\t"+minStep+"\n");
                System.out.println("Set max epochs (e.g. 1000)");
                int maxEpochs = scanner.nextInt();
                variables = variables.concat("Max epochs:\t"+maxEpochs+"\n");
                rProp.setMaxEpochs(maxEpochs);
                System.out.println("Set moves per epoch (e.g. 1000)");
                int movesPerEpoch = scanner.nextInt();
                variables = variables.concat("Moves per epoch:\t"+movesPerEpoch+"\n");
                System.out.println("Choose a random seed (-1 for Math.random)");
                long randomSeed = scanner.nextLong();
                variables = variables.concat("Random Seed (-1 for randoms):\t"+randomSeed+"\n");
                neuralNet.setLearningRule(rProp);
                neuralNet.setActivationFunction(new Sigmoid());
                neuralNet.selfTrain(movesPerEpoch, randomSeed);
                return neuralNet;
            } catch (InputMismatchException e) {
                System.out.println("Wrong type of input.");
                scanner.next();
            }
        }
    }

    private void playAIGames(NeuralNet neuralNet, Scanner scanner) {
        for (; ; ) {
            try {
                System.out.println("Choose a random seed (-1 for Math.random)");
                long randomSeed = scanner.nextLong();
                Display display = new Display(numTiles, tileSize);
                SnakeGame snakeGame = new SnakeGame(numTiles, tileSize, display, neuralNet, true, randomSeed);
                System.out.println("Play another game?\n1: Yes\n2: Quit");
                int input = scanner.nextInt();
                if (input == 2) {
                    snakeGame.closeDisplay();
                    break;
                } else if (input != 1) throw new InputMismatchException();
                else snakeGame.closeDisplay();
            } catch (InputMismatchException e) {
                System.out.print("Input error caught");
                scanner.next();
            }
        }
    }

    private void saveNeuralNetwork(NeuralNet neuralNet, Scanner scanner, String filePath, boolean humanInput) {
        if(humanInput) {
            System.out.println("Choose file path");
            filePath = scanner.next();
        }
        double[][][] weightsAndBiases = neuralNet.getWeightsAndBiases();
        try {
            File outputFile = new File(filePath);
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.writeObject(weightsAndBiases);
            outputStream.flush();
        } catch (IOException e) {
            System.out.println("File not found.");
        }
    }

    private void saveTextFile(String filePath, String contents) {
        try {
            PrintWriter outputFile = new PrintWriter(filePath);
            outputFile.print(contents);
            outputFile.flush();
            outputFile.close();
        } catch (IOException e) {
            System.out.println("File not found.");
        }
    }

    private NeuralNet loadNeuralNetwork(Scanner scanner) {
        System.out.println("Choose file path");
        String filePath = scanner.next();
        try{
            File inputFile = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(inputFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            double[][][] weightsAndBiases =  (double[][][])objectInputStream.readObject();
            return new NeuralNet(weightsAndBiases);
        } catch (IOException|ClassNotFoundException e){
            System.out.println("File not found.");
        }
        return null;
    }

    private void runExperiments(){
        int numExperiments = 12;
        int runsPerExperiment = 30;
        int maxEpochs = 5;
        int movesPerEpoch = 3;
        double[] increaseRates = {1.1, 1.6, 1.2, 1.2, 1.2, 1.2, 1.2, 1.2, 1.2, 1.2, 1.2, 1.2};
        double[] decreaseRates = {0.5, 0.5, 0.7, 0.2, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5};
        double[] maxSteps =  {50.0, 50.0, 50.0, 50.0, 100.0, 10.0, 50.0, 50.0, 50.0, 50.0, 50.0, 50.0};
        double[] minSteps = {0.0001,0.0001,0.0001,0.0001,0.0001,0.0001,0.01,0.000001,0.0001,0.0001,0.0001,0.0001};
        long[] randomSeeds = {0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0};
        int[][] hiddenLayers = {{100,50},{100,50},{100,50},{100,50},{100,50},{100,50},{100,50},{100,50},{100,50},{},{100},{1000,500}};
        for(int expNum = 0; expNum < numExperiments; expNum++){
            double increaseRate = increaseRates[expNum];
            double decreaseRate = decreaseRates[expNum];
            double maxStep = maxSteps[expNum];
            double minStep = minSteps[expNum];
            long randomSeed = randomSeeds[expNum];
            int[] layers = new int[hiddenLayers.length + 2];
            layers[0] = 12;
            layers[layers.length - 1] = 3;
            for(int i = 0; i < hiddenLayers[expNum].length; i++){
                layers[i + 1] = hiddenLayers[expNum][i];
            }
            String experimentResults = "";
            NeuralNet bestNetwork = null;
            double lowestError = Double.MAX_VALUE;
            for(int runNum = 0; runNum < runsPerExperiment; runNum++){
                NeuralNet neuralNet = new NeuralNet(layers);
                RProp rProp = new RProp();
                rProp.setMaxEpochs(maxEpochs);
                rProp.setIncreaseRate(increaseRate);
                rProp.setDecreaseRate(decreaseRate);
                rProp.setMaxStep(maxStep);
                rProp.setMinStep(minStep);
                neuralNet.setLearningRule(rProp);
                neuralNet.setActivationFunction(new Sigmoid());
                neuralNet.selfTrain(movesPerEpoch,randomSeed);
                if(neuralNet.getError() < lowestError){
                    lowestError = neuralNet.getError();
                    bestNetwork = neuralNet;
                }
                experimentResults = experimentResults.concat(neuralNet.getExperimentResults()+"\n");
            }
            int roundedError = (int)(lowestError*1000000);
            double rounded = (double)roundedError/1000000;
            String fileName = "ExpNum_"+expNum+"_Lowest_Error_"+rounded;
            String filePath = "C:\\Users\\phili\\Google_Drive\\Brock_Computer_Science_Degree\\COSC_4P76_Machine_Learning\\SavedNeuralNetworks\\"+fileName;
            saveNeuralNetwork(bestNetwork, null, filePath, false);
            fileName = "ExpNum_"+expNum+"_Lowest_Error_"+rounded+".txt";
            filePath = "C:\\Users\\phili\\Google_Drive\\Brock_Computer_Science_Degree\\COSC_4P76_Machine_Learning\\SavedNeuralNetworks\\"+fileName;
            saveTextFile(filePath, experimentResults);
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
