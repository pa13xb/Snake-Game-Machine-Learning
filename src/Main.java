import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;

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
 *
 * It uses the command prompt to receive and display information.
 *
 * Options are as follows:
 * 1: Allows a human user to play a randomly generated game
 * 2: Allows the evaluation function user to play a randomly generated game
 * 3: Allows choosing of training parameters to train an AI, save it to an output file, and play test games with it
 * 4: Allows loading of a saved AI to then play games with using specified seeds (-1 for random)
 * 5: Runs pre-made training experiments
 * 6: Allows loading of a saved AI to run 1000 random testing games, printing out scores and seeds used
 */
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
            System.out.println("5: Run training experiments");
            System.out.println("6: Run testing experiments");
            System.out.println("0: Quit");
            try {
                int input = scanner.nextInt();
                Display display;
                switch (input) {
                    case 0:
                        quit = true;
                        break;
                    case 1: //Play the game yourself
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
                    case 2: //Let the evaluation function play a game
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
                    case 3: //Train an AI
                        NeuralNet neuralNet = trainAI(scanner);
                        System.out.println("Parameters used: \n" + variables);
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
                        runTrainingExperiments();
                        break;
                    case 6:
                        runTestingExperiments(scanner);
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Input must be an integer, try again");
            }
        }
    }//constructor

    /**
     * Handles receiving training parameters for R-Prop and training a neural network.
     *
     * @param scanner the input Java Scanner
     * @return the trained neural network
     */
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
                    variables = variables.concat(i + "\t" + layers[i] + "\n");
                }
                NeuralNet neuralNet = new NeuralNet(layers);
                variables = variables.concat("Learning rule: \trProp\n");
                RProp rProp = new RProp();
                System.out.println("Set IncreaseRate (e.g. 1.2)");
                double increaseRate = scanner.nextDouble();
                rProp.setIncreaseRate(increaseRate);
                variables = variables.concat("Increase rate:\t" + increaseRate + "\n");
                System.out.println("Set DecreaseRate (e.g. 0.5)");
                double decreaseRate = scanner.nextDouble();
                rProp.setDecreaseRate(decreaseRate);
                variables = variables.concat("Decrease rate:\t" + decreaseRate + "\n");
                System.out.println("Set max step (e.g. 50.0)");
                double maxStep = scanner.nextDouble();
                rProp.setMaxStep(maxStep);
                variables = variables.concat("Max step:\t" + maxStep + "\n");
                System.out.println("Set min step (e.g. 0.0001)");
                double minStep = scanner.nextDouble();
                rProp.setMinStep(minStep);
                variables = variables.concat("Min step:\t" + minStep + "\n");
                System.out.println("Set max epochs (e.g. 1000)");
                int maxEpochs = scanner.nextInt();
                variables = variables.concat("Max epochs:\t" + maxEpochs + "\n");
                rProp.setMaxEpochs(maxEpochs);
                System.out.println("Set moves per epoch (e.g. 1000)");
                int movesPerEpoch = scanner.nextInt();
                variables = variables.concat("Moves per epoch:\t" + movesPerEpoch + "\n");
                System.out.println("Choose a random seed (-1 for Math.random)");
                long randomSeed = scanner.nextLong();
                variables = variables.concat("Random Seed (-1 for randoms):\t" + randomSeed + "\n");
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

    /**
     * Allows the user to watch an AI play games with a specified random seed (-1 for random)
     *
     * @param neuralNet the neural network to play the game(s)
     * @param scanner the input Java Scanner
     */
    private void playAIGames(NeuralNet neuralNet, Scanner scanner) {
        if (neuralNet != null) {
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
    }

    /**
     * saveNeuralNetwork is a method used to save a NeuralNet for later testing experiments.
     *
     * @param neuralNet the neural network to save
     * @param scanner the input Java Scanner
     * @param filePath the filepath to save to (in the case of automatic experiments)
     * @param humanInput a flag to determine if path is specified by human or not
     */
    private void saveNeuralNetwork(NeuralNet neuralNet, Scanner scanner, String filePath, boolean humanInput) {
        if (humanInput) {
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

    /**
     * saveTextFile is a method that takes in a String for the file path and contents, and then saves the contents into
     * a File with the filePath.
     *
     * @param filePath the Windows path of the text file to save
     * @param contents the contents of the text to save (String)
     */
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

    /**
     * loadNeuralNetwork is a method that allows a user to load a neural network from a file that has a saved neural
     * network in it.
     *
     * @param scanner the input Java Scanner
     * @return a NeuralNet
     */
    private NeuralNet loadNeuralNetwork(Scanner scanner) {
        System.out.println("Choose file path");
        String filePath = scanner.next();
        try {
            File inputFile = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(inputFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            double[][][] weightsAndBiases = (double[][][]) objectInputStream.readObject();
            return new NeuralNet(weightsAndBiases);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("File not found.");
        }
        return null;
    }

    /**
     * runTrainingExperiments will allow the user to hard code experiment parameters and then train neural networks and
     * then save the best neural networks from each experiment.
     */
    private void runTrainingExperiments() {
        int numExperiments = 12;
        int runsPerExperiment = 1;
        int maxEpochs = 1000;
        int movesPerEpoch = 1000;
        double[] increaseRates = {1.1, 1.6, 1.2, 1.2, 1.2, 1.2, 1.2, 1.2, 1.2, 1.2, 1.2, 1.2};
        double[] decreaseRates = {0.5, 0.5, 0.7, 0.2, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5};
        double[] maxSteps = {50.0, 50.0, 50.0, 50.0, 100.0, 10.0, 50.0, 50.0, 50.0, 50.0, 50.0, 50.0};
        double[] minSteps = {0.0001, 0.0001, 0.0001, 0.0001, 0.0001, 0.0001, 0.01, 0.000001, 0.0001, 0.0001, 0.0001, 0.0001};
        long[] randomSeeds = {0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0};
        int[][] hiddenLayers = {{100, 50}, {100, 50}, {100, 50}, {100, 50}, {100, 50}, {100, 50}, {100, 50}, {100, 50}, {100, 50}, {}, {100}, {1000, 500}};
        for (int expNum = 11; expNum < numExperiments; expNum++) {
            System.out.println("Beginning exp_num " + expNum + "\n\n");
            double increaseRate = increaseRates[expNum];
            double decreaseRate = decreaseRates[expNum];
            double maxStep = maxSteps[expNum];
            double minStep = minSteps[expNum];
            long randomSeed = randomSeeds[expNum];
            int[] layers = new int[hiddenLayers[expNum].length + 2];
            layers[0] = 12;
            layers[layers.length - 1] = 3;
            for (int i = 0; i < hiddenLayers[expNum].length; i++) {
                layers[i + 1] = hiddenLayers[expNum][i];
            }
            String experimentResults = "";
            NeuralNet bestNetwork = null;
            double lowestError = Double.MAX_VALUE;
            for (int runNum = 0; runNum < runsPerExperiment; runNum++) {
                System.out.println("Beginning runNum " + runNum);
                NeuralNet neuralNet = new NeuralNet(layers);
                RProp rProp = new RProp();
                rProp.setMaxEpochs(maxEpochs);
                rProp.setIncreaseRate(increaseRate);
                rProp.setDecreaseRate(decreaseRate);
                rProp.setMaxStep(maxStep);
                rProp.setMinStep(minStep);
                neuralNet.setLearningRule(rProp);
                neuralNet.setActivationFunction(new Sigmoid());
                /*System.out.println("maxEpochs: "+maxEpochs);
                System.out.println("increaseRate: "+increaseRate);
                System.out.println("decreaseRate: "+decreaseRate);
                System.out.println("maxStep: "+maxStep);
                System.out.println("minStep: "+minStep);
                System.out.println("maxEpochs: "+maxEpochs);
                System.out.println("movesPerEpoch: "+movesPerEpoch);
                System.out.println("layers.length: "+layers.length);
                System.out.println("layer 0: "+layers[0]);
                System.out.println("layer 1: "+layers[1]);
                System.out.println("layer 2: "+layers[2]);
                System.out.println("layer 3: "+layers[3]);*/
                neuralNet.selfTrain(movesPerEpoch, randomSeed);
                if (neuralNet.getError() < lowestError) {
                    lowestError = neuralNet.getError();
                    bestNetwork = neuralNet;
                }
                experimentResults = experimentResults.concat(neuralNet.getExperimentResults() + "\n");
            }
            int roundedError = (int) (lowestError * 1000000);
            double rounded = (double) roundedError / 1000000;
            String fileName = "ExpNum_" + expNum + "_Lowest_Error_" + rounded;
            String filePath = "C:\\Users\\phili\\Google_Drive\\Brock_Computer_Science_Degree\\COSC_4P76_Machine_Learning\\SavedNeuralNetworks\\" + fileName;
            saveNeuralNetwork(bestNetwork, null, filePath, false);
            fileName = "ExpNum_" + expNum + "_Lowest_Error_" + rounded + ".txt";
            filePath = "C:\\Users\\phili\\Google_Drive\\Brock_Computer_Science_Degree\\COSC_4P76_Machine_Learning\\SavedNeuralNetworks\\" + fileName;
            saveTextFile(filePath, experimentResults);
        }
    }

    /**
     * runTestingExperiments allows a user to run multiple testing experiments, playing games, on a saved neural network.
     *
     * @param scanner the input Java Scanner
     */
    private void runTestingExperiments(Scanner scanner) {
        NeuralNet neuralNet = loadNeuralNetwork(scanner);
        if (neuralNet != null) {
            int numExperiments = 1000;
            long[] randomSeeds = new long[numExperiments];
            int[] scores = new int[numExperiments];
            for (int i = 0; i < numExperiments; i++) {
                long randomSeed = (long) (Math.random() * Long.MAX_VALUE);
                randomSeeds[i] = randomSeed;
                SnakeGame snakeGame = new SnakeGame(numTiles, tileSize, null, neuralNet, false, randomSeed);
                scores[i] = snakeGame.getScore();
                System.out.println("Finished test "+i);
            }
            for (int i = 0; i < numExperiments; i++) {
                System.out.println("Score = " + scores[i] + ", seed = " + randomSeeds[i]);
            }
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
