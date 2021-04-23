import java.util.LinkedList;

/**
 * @author Philip Akkerman
 * studentID 5479613
 * email pa13xb@brocku.ca
 *
 * This class implements a multi-layer perceptron neural network
 */
public class NeuralNet {

    private LinkedList<double[]> trainingSet;   //rows of training data
    private LinkedList<double[]> desiredResults;//desired results for training data rows
    private double[][][] weightsAndBiases; //weights in biases (last entry of each row is for biases)
    private int[] layers;
    private int inputNeurons;
    private int outputNeurons;
    private LearningRule learningRule;
    private ActivationFunction activationFunction;

    public NeuralNet(int[] layers){
        this.layers = layers;
        inputNeurons = layers[0];
        outputNeurons = layers[layers.length-1];
        activationFunction = new Sigmoid();
        trainingSet = new LinkedList<>();
        desiredResults = new LinkedList<>();
        learningRule = new BackProp();
        weightsAndBiases = new double[layers.length-1][][];
        for(int layer = 0; layer < layers.length - 1; layer++){
            weightsAndBiases[layer] = new double[layers[layer+1]][layers[layer]+1];//last entry for biases (hence +1)
        }
        randomizeWeightsAndBiases();
    }//Constructor

    public void randomizeWeightsAndBiases(){
        for(int layer = 0; layer < layers.length - 1; layer++) {
            for (int node = 0; node < layers[layer + 1]; node++) {
                for (int connection = 0; connection <= layers[layer]; connection++) {
                    weightsAndBiases[layer][node][connection] = (Math.random() - 0.5) * 2;
                }
            }
        }
    }//randomizeWeights

    public boolean addTrainingRow(double[] row, double[] desiredResult){
        if(row.length == inputNeurons && desiredResult.length == outputNeurons){
            trainingSet.add(row);
            desiredResults.add(desiredResult);
            return true;
        }
        else return false;
    }//addTrainingRow

    public void clearTrainingSets(){
        trainingSet.clear();
        desiredResults.clear();
    }//clearTrainingSet

    public void train() {
        weightsAndBiases = learningRule.train(weightsAndBiases, trainingSet, desiredResults);
    }//train

    public void selfTrain(int movesPerEpoch){
        weightsAndBiases = learningRule.selfTrain(weightsAndBiases, movesPerEpoch);
    }//selfTrain

    public double[] calculate(double[] testingRow){
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

    public void setLearningRule(LearningRule learningRule) {
        this.learningRule = learningRule;
    }//setLearningRule

    public void setActivationFunction(ActivationFunction actFunc){
        this.activationFunction = actFunc;
        learningRule.setActivationFunction(actFunc);
    }//setActivationFunction
}

