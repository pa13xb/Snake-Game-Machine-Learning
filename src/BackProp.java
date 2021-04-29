import java.util.LinkedList;

/**
 * @author Philip Akkerman
 * studentID 5479613
 * email pa13xb@brocku.ca
 *
 * This class implements backpropagation (almost vanilla, except that I update weights after each epoch instead of each
 * training example. This is also why I do not have data shuffling, since all examples use the same weights and biases
 * anyway, and they are updated after the epoch is done)
 */
public class BackProp implements LearningRule {

    private double learningRate;
    private double momentum;
    private int maxEpochs;
    private double errorGoal;
    private ActivationFunction activationFunction;

    BackProp(){
        this.activationFunction = new Sigmoid();
        learningRate = 0.1;
        momentum = 0;
        maxEpochs = -1;
        errorGoal = -1;
    }

    @Override
    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    @Override
    public void setMomentum(double momentum) {
        this.momentum = momentum;
    }

    @Override
    public void setIncreaseRate(double increaseRate) {    }

    @Override
    public void setDecreaseRate(double decreaseRate) {    }

    @Override
    public void setMaxStep(double maxStep) {    }

    @Override
    public void setMinStep(double minStep) {    }

    @Override
    public void setMaxEpochs(int maxEpochs){
        this.maxEpochs = maxEpochs;
    }

    @Override
    public void setErrorGoal(double errorGoal){
        this.errorGoal = errorGoal;
    }

    @Override
    public void setActivationFunction(ActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
    }

    @Override
    public double[][][] train(double[][][] weightsAndBiases, LinkedList<double[]> trainingSet, LinkedList<double[]> desiredResults) {
        if(maxEpochs == -1 && errorGoal == -1) errorGoal = 0.1;
        double[][][] prevChanges = new double[weightsAndBiases.length][][]; //Create Prev Changes: create the layers array
        for(int layer = 0; layer < weightsAndBiases.length; layer++){
            prevChanges[layer] = new double[weightsAndBiases[layer].length][]; //create the nodes arrays
            for (int node = 0; node < weightsAndBiases[layer].length; node++) {
                prevChanges[layer][node] = new double[weightsAndBiases[layer][node].length]; //create the connections arrays
                for (int prevNode = 0; prevNode < weightsAndBiases[layer][node].length; prevNode++) {
                    prevChanges[layer][node][prevNode] = 0; //initialize the connections
                }
            }
        }//create prev changes
        int epoch = 0;
        double error = Double.MAX_VALUE; //temporary value to get through the first while loop
        //End conditions: #epochs or errorGoal reached. -1 indicates that the other method is being used (both is applicable too)
        while((epoch < maxEpochs || epoch == -1) && (errorGoal == -1 || error > errorGoal)) { //Start of each epoch
            error = 0;
            double[][][] weightsAndBiasChanges = new double[weightsAndBiases.length][][]; //create the layers array
            for(int layer = 0; layer < weightsAndBiases.length; layer++){
                weightsAndBiasChanges[layer] = new double[weightsAndBiases[layer].length][]; //create the nodes arrays
                for (int node = 0; node < weightsAndBiases[layer].length; node++) {
                    weightsAndBiasChanges[layer][node] = new double[weightsAndBiases[layer][node].length]; //create the connections arrays
                    for (int prevNode = 0; prevNode < weightsAndBiases[layer][node].length; prevNode++) {
                        weightsAndBiasChanges[layer][node][prevNode] = 0; //initialize the connections
                    }
                }
            }//initialize weightsAndBiasChanges
            for (int exampleNum = 0; exampleNum < trainingSet.size(); exampleNum++) { //iterate through each training example
                double[] trainingRow = trainingSet.get(exampleNum);
                double[][] activationLayers = getActivationLayers(trainingRow, weightsAndBiases);
                double[] desiredOutput = desiredResults.get(exampleNum); //initialize desiredOutput for the output layer
                error += calculateError(activationLayers[activationLayers.length - 1],desiredOutput);
                //THE RECURSIVE FUNCTION CALL
                trainRecursive(weightsAndBiases.length - 1, weightsAndBiases, weightsAndBiasChanges, activationLayers, desiredOutput);
            }
            for(int layer = 0; layer < weightsAndBiases.length; layer++){
                for (int node = 0; node < weightsAndBiases[layer].length; node++) {
                    for (int prevNode = 0; prevNode < weightsAndBiases[layer][node].length; prevNode++) {
                        weightsAndBiasChanges[layer][node][prevNode] = (weightsAndBiasChanges[layer][node][prevNode] / trainingSet.size()) * learningRate + momentum * prevChanges[layer][node][prevNode];
                        prevChanges[layer][node][prevNode] = weightsAndBiasChanges[layer][node][prevNode];
                        weightsAndBiases[layer][node][prevNode] -= weightsAndBiasChanges[layer][node][prevNode];
                    }
                }
            }
            error = error / trainingSet.size();
            if (epoch % 50 == 0) System.out.println("Epoch " + epoch + ", Error: " + error);
            epoch++;
        }//epochs
        return weightsAndBiases;
    }//train

    @Override
    public double[][][] selfTrain(double[][][] weightsAndBiases, int movesPerEpoch, long randomSeed) {
        return new double[0][][];
    }

    @Override
    public String getExperimentResults() {
        return null;
    }

    @Override
    public double getFinalError() {
        return 0;
    }

    private void trainRecursive(int layer, double[][][] weightsAndBiases, double[][][] weightsAndBiasChanges, double[][] activationLayers, double[] desiredOutput){
        double[] currActivationLayer = activationLayers[layer + 1];
        double[] prevActivationLayer = activationLayers[layer];
        double[] desiredInput = new double[prevActivationLayer.length];//new double[prevActivationLayer.length]; //desired input for back propagation
        for(int i = 0; i < desiredInput.length; i++) desiredInput[i] = 0;
        for (int node = 0; node < currActivationLayer.length; node++) { //iterate through each node in the current layer
            double activation =  currActivationLayer[node]; //get activation
            double y = desiredOutput[node]; //y = desired output for this node
            double dCostDActivation = 2 * (activation - y); //i.e. 2 * error (aka cost)
            double dActivationDSumProduct = activationFunction.calcDerivativeWActivation(activation); //derivative of activation over sumProduct
            for (int prevNode = 0; prevNode < prevActivationLayer.length; prevNode++) {
                double dSumProductDWeight = prevActivationLayer[prevNode]; //derivative of sumProduct re: weight = Previous node's activation
                double dCostDWeight = dCostDActivation * dActivationDSumProduct * dSumProductDWeight; //the derivative of cost over weight
                weightsAndBiasChanges[layer][node][prevNode] += dCostDWeight; //put weight change in its spot
                double dCostDPrevActivation = dCostDActivation * dActivationDSumProduct * weightsAndBiases[layer][node][prevNode];
                desiredInput[prevNode] = desiredInput[prevNode] - dCostDPrevActivation; //put prev activation derivative into desired input
            }
            double dSumProductDBias = 1; //derivative of sumProduct re: bias = 1
            double dCostDBias = dCostDActivation * dActivationDSumProduct * dSumProductDBias; //the derivative of cost over bias
            weightsAndBiasChanges[layer][node][prevActivationLayer.length] += dCostDBias; //put bias change in its spot
        }
        if(layer > 0){
            for(int prevNode = 0; prevNode < prevActivationLayer.length; prevNode++) desiredInput[prevNode] = desiredInput[prevNode] / currActivationLayer.length;
            trainRecursive(layer-1, weightsAndBiases, weightsAndBiasChanges, activationLayers, desiredInput.clone());
        }
    }//trainRecursive

    private double[][] getActivationLayers(double[] trainingRow, double[][][] weightsAndBiases){
        double[][] activationLayers = new double[weightsAndBiases.length + 1][];
        activationLayers[0] = trainingRow.clone();
        for(int layer = 1; layer < activationLayers.length; layer++){
            activationLayers[layer] = new double[weightsAndBiases[layer - 1].length];
            for (int node = 0; node < activationLayers[layer].length; node++) {
                activationLayers[layer][node] = 0;
                for (int prevNode = 0; prevNode < activationLayers[layer - 1].length; prevNode++) {
                    activationLayers[layer][node] += weightsAndBiases[layer - 1][node][prevNode] * activationLayers[layer - 1][prevNode];
                }
                activationLayers[layer][node] += (weightsAndBiases[layer - 1][node][activationLayers[layer - 1].length]); //getBias
                activationLayers[layer][node] = activationFunction.calculate(activationLayers[layer][node]); //sigmoid
            }
        }
        return activationLayers;
    }

    //This calculates the error between desired and actual outputs.
    private double calculateError(double[] actualOutput, double[] desiredOutput){
        double error = 0;
        for(int i = 0; i < desiredOutput.length; i++){
            error += (desiredOutput[i] - actualOutput[i])*(desiredOutput[i] - actualOutput[i]);
        }
        return error;
    }//calculateError
}
