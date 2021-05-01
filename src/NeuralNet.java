import java.util.LinkedList;

/**
 * @author Philip Akkerman
 * studentID 5479613
 * email pa13xb@brocku.ca
 *
 * @author David Hasler
 * studentID 6041321
 * email dh15pd@brocku.ca
 *
 * This class implements a multi-layer perceptron neural network
 *
 * Note: this code was modified from the neural network created by Philip Akkerman for
 * assignment 1.
 */
class NeuralNet {

    private LinkedList<double[]> trainingSet;   //rows of training data
    private LinkedList<double[]> desiredResults;//desired results for training data rows
    private double[][][] weightsAndBiases; //weights in biases (last entry of each row is for biases)
    private int[] layers; //holds number of nodes per layer
    private int inputNeurons; //the number of nodes in the input layer
    private int outputNeurons; //the number of nodes in the output layer
    private LearningRule learningRule; //the learning rule used by the neural network
    private ActivationFunction activationFunction; //the activation function used by the neural network
    private String experimentResults = ""; //the error values recorded during training
    private double finalError = 0; //the final error value recorded during training

    /**
     * Creates a new neural network based on a given architecture
     * @param layers the architecture of layers and nodes
     */
    NeuralNet(int[] layers){
        this.layers = layers;
        inputNeurons = layers[0];
        outputNeurons = layers[layers.length-1];
        activationFunction = new Sigmoid();
        trainingSet = new LinkedList<>();
        desiredResults = new LinkedList<>();
        learningRule = new RProp();
        weightsAndBiases = new double[layers.length-1][][];
        for(int layer = 0; layer < layers.length - 1; layer++){
            weightsAndBiases[layer] = new double[layers[layer+1]][layers[layer]+1];//last entry for biases (hence +1)
        }
        randomizeWeightsAndBiases();
    }//Constructor

    /**
     * Restores a neural network based on a given set of weights and biases
     * @param weightsAndBiases the weights and biases of the neural network
     */
    NeuralNet(double[][][] weightsAndBiases){
        inputNeurons = weightsAndBiases[0][0].length - 1;
        outputNeurons = weightsAndBiases[weightsAndBiases.length - 1].length;
        activationFunction = new Sigmoid();
        trainingSet = new LinkedList<>();
        desiredResults = new LinkedList<>();
        learningRule = new RProp();
        this.weightsAndBiases = weightsAndBiases;
        layers = new int[weightsAndBiases.length + 1];
        layers[0] = inputNeurons;
        for(int i = 0; i < layers.length - 1; i++){
            layers[i + 1] = weightsAndBiases[i].length;
        }
        System.out.println("Layers.length = "+layers.length+", weights&Biases.length = "+weightsAndBiases.length);
        System.out.println("inputNeurons = "+inputNeurons+", outputNeurons = "+outputNeurons);
    }//Constructor

    //Original code from assignment 1
    private void randomizeWeightsAndBiases(){
        for(int layer = 0; layer < layers.length - 1; layer++) {
            for (int node = 0; node < layers[layer + 1]; node++) {
                for (int connection = 0; connection <= layers[layer]; connection++) {
                    weightsAndBiases[layer][node][connection] = (Math.random() - 0.5) * 2;
                }
            }
        }
    }//randomizeWeights

    //Original code from assignment 1
    public boolean addTrainingRow(double[] row, double[] desiredResult){
        if(row.length == inputNeurons && desiredResult.length == outputNeurons){
            trainingSet.add(row);
            desiredResults.add(desiredResult);
            return true;
        }
        else return false;
    }//addTrainingRow

    //Original code from assignment 1
    public void clearTrainingSets(){
        trainingSet.clear();
        desiredResults.clear();
    }//clearTrainingSet

    //Original code from assignment 1
    public void train() {
        weightsAndBiases = learningRule.train(weightsAndBiases, trainingSet, desiredResults);
    }//train

    /**
     * Trains a neural network using no pre-set input rows or desired results, but only real Snake games.
     * @param movesPerEpoch the number of Snake game moves to train on per epoch
     * @param randomSeed the random seed to use for each game (-1 for random)
     */
    void selfTrain(int movesPerEpoch, long randomSeed){
        weightsAndBiases = learningRule.selfTrain(weightsAndBiases, movesPerEpoch, randomSeed);
        experimentResults = learningRule.getExperimentResults();
        finalError = learningRule.getFinalError();
    }//selfTrain

    //Original code from assignment 1 - calculates the neural network's output
    double[] calculate(double[] testingRow){
        double[] prevLayer = testingRow.clone();
        double[] output = null;
        for(int layer = 0; layer < layers.length - 1; layer++) {
            output = new double[layers[layer + 1]];
            for(int node = 0; node < layers[layer + 1]; node++){
                double sumProduct = 0;
                for(int connection = 0; connection < layers[layer]; connection++){
                    sumProduct += prevLayer[connection] * weightsAndBiases[layer][node][connection];
                }
                double bias = weightsAndBiases[layer][node][layers[layer]];
                output[node] = activationFunction.calculate(sumProduct + bias);
            }
            prevLayer = output.clone();
        }
        return output;
    }//calculate

    void setLearningRule(LearningRule learningRule) {
        this.learningRule = learningRule;
    }//setLearningRule

    //Original code from assignment 1
    void setActivationFunction(ActivationFunction actFunc){
        this.activationFunction = actFunc;
        learningRule.setActivationFunction(actFunc);
    }//setActivationFunction

    /**
     * Getter for weights and biases
     * @return the weights and biases of the network
     */
    double[][][] getWeightsAndBiases() {
        return weightsAndBiases;
    }//getWeightsAndBiases

    /**
     * Getter for the final error value from training
     * @return the final error value in training
     */
    double getError() {
        return finalError;
    }//getError

    /**
     * Getter for experimental results, the error values during training
     * @return the error values from training
     */
    String getExperimentResults(){
        return experimentResults;
    }//getExperimentalResults
}

