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
 * This code implements the R-Prop training algorithm.
 *
 * Note: this code was modified from the neural network created by Philip Akkerman for
 * assignment 1.
 */
public class RProp implements LearningRule {

    private double increaseRate; //increase rate for R-Prop
    private double decreaseRate; //decrease rate for R-Prop
    private double maxStep; //max step for R-Prop
    private double minStep; //min step for R-Prop
    private int maxEpochs; //max training epochs
    private double errorGoal; //end condition based on error
    private ActivationFunction activationFunction; //the activation function to use
    private String experimentResults = ""; //the error per epoch trend
    private double finalError = 0; //the final error calculated

    RProp(){
        this.activationFunction = new Sigmoid();
        maxEpochs = -1;
        errorGoal = -1;
    }//constructor

    @Override
    //Original code from assignment 1
    public void setLearningRate(double learningRate) {  }

    @Override
    //Original code from assignment 1
    public void setMomentum(double momentum) { }

    @Override
    //Original code from assignment 1
    public void setIncreaseRate(double increaseRate) {  this.increaseRate = increaseRate;  }

    @Override
    //Original code from assignment 1
    public void setDecreaseRate(double decreaseRate) {  this.decreaseRate = decreaseRate;  }

    @Override
    //Original code from assignment 1
    public void setMaxStep(double maxStep) {  this.maxStep = maxStep;  }

    @Override
    //Original code from assignment 1
    public void setMinStep(double minStep) {  this.minStep = minStep;  }

    @Override
    //Original code from assignment 1
    public void setMaxEpochs(int maxEpochs){
        this.maxEpochs = maxEpochs;
    }

    @Override
    //Original code from assignment 1
    public void setErrorGoal(double errorGoal){
        this.errorGoal = errorGoal;
    }

    @Override
    //Original code from assignment 1
    public void setActivationFunction(ActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
    }

    @Override
    //Original code from assignment 1
    public double[][][] train(double[][][] weightsAndBiases, LinkedList<double[]> trainingSet, LinkedList<double[]> desiredResults) {
        if(maxEpochs == -1 && errorGoal == -1) errorGoal = 0.1;
        double[][][] prevChanges = new double[weightsAndBiases.length][][]; //Create Prev Changes: create the layers array
        for(int layer = 0; layer < weightsAndBiases.length; layer++){
            prevChanges[layer] = new double[weightsAndBiases[layer].length][]; //create the nodes arrays
            for (int node = 0; node < weightsAndBiases[layer].length; node++) {
                prevChanges[layer][node] = new double[weightsAndBiases[layer][node].length]; //create the connections arrays
                for (int prevNode = 0; prevNode < weightsAndBiases[layer][node].length; prevNode++) {
                    prevChanges[layer][node][prevNode] = 1; //initialize the connections
                }
            }
        }
        int epoch = 0;
        double error = Double.MAX_VALUE; //temporary value to get through the first while loop
        //End conditions: #epochs or errorGoal reached. -1 indicates that the other method is being used (both is applicable too)
        while((epoch < maxEpochs || epoch == -1) && (errorGoal == -1 || error > errorGoal)) { //Start of each epoch
            error = 0;
            double[][][] weightsAndBiasChanges = new double[weightsAndBiases.length][][]; //Create W&BChanges: create the layers array
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
                boolean display = false;
                if(exampleNum == 0) display = true;
                double[][] activationLayers = getActivationLayers(trainingRow, weightsAndBiases);
                double[] desiredOutput = desiredResults.get(exampleNum); //initialize desiredOutput for the output layer
                error += calculateError(activationLayers[activationLayers.length - 1],desiredOutput);
                //THE RECURSIVE FUNCTION CALL
                trainRecursive(weightsAndBiases.length - 1, weightsAndBiases, weightsAndBiasChanges, activationLayers, desiredOutput, display);
            }
            /*Diagnostics*///String pattern = "#.#####";
            /*Diagnostics*///DecimalFormat format = new DecimalFormat(pattern);
            for(int layer = 0; layer < weightsAndBiases.length; layer++){
                for (int node = 0; node < weightsAndBiases[layer].length; node++) {
                    for (int prevNode = 0; prevNode < weightsAndBiases[layer][node].length; prevNode++) {
                        double currentGradient = weightsAndBiasChanges[layer][node][prevNode];
                        double previousGradient = prevChanges[layer][node][prevNode];
                        /*Diagnostics*///System.out.print("CurGradient = "+format.format(currentGradient)+"\tPrevGradient = "+format.format(previousGradient));
                        double step = 0;
                        if(currentGradient < 0) { //current gradient is negative
                            if(currentGradient * previousGradient < 0){ //Switched from positive to negative
                                step = -previousGradient * decreaseRate; //take negative of last step, decreased
                                if(step > -minStep) step = -minStep; //step can't be smaller (more positive) than -minStep
                            } else{ //was negative, still more negative
                                step = previousGradient * increaseRate; //negative gradient is now more negative
                                if(step < -maxStep) step = -maxStep; //step can't be greater (more negative) than -maxStep
                            }
                        }
                        else if(currentGradient > 0) { //current gradient is positive
                            if(currentGradient * previousGradient < 0){ //Switched from negative to positive
                                step = -previousGradient * decreaseRate; //take negative of last step, decreased
                                if(step < minStep) step = minStep; //step can't be smaller (more negative) than minStep
                            } else{ //was positive, still more positive
                                step = previousGradient * increaseRate; //positive gradient is now more positive
                                if(step > maxStep) step = maxStep; //step can't be greater (more positive) than maxStep
                            }
                        }
                        /*Diagnostics*/// System.out.print("\tStep = "+format.format(step)+"\tweightsAndBiases["+layer+"]["+node+"]["+prevNode+"] = "+format.format(weightsAndBiases[layer][node][prevNode])+"\n");
                        weightsAndBiases[layer][node][prevNode] -= step; //apply change to weight
                        prevChanges[layer][node][prevNode] = step; //record change to prevChanges
                    }
                }
            }
            error = error / trainingSet.size();
            if (epoch % 50 == 0) System.out.println("Epoch " + epoch + ", Error: " + error);
            epoch++;
        }//epochs
        return weightsAndBiases;
    }//train

    /**
     * This class handles the self training of the neural network using Snake games. It creates new Snake
     * games and plays them using the SnakeGame's evaluation function. For each move made, the AI's suggested move
     * is also calculated. The move suggested by the current AI is compared to the move from the evaluation function to
     * determine the error used for the R-Prop algorithm.
     *
     * Training parameters must be set before calling this function.
     *
     * @author Philip Akkerman
     * studentID 5479613
     * email pa13xb@brocku.ca
     *
     * @param weightsAndBiases The neural network weights and biases to train
     * @param movesPerEpoch The number of moves to train on per epoch
     * @param randomSeed the random seed to use for each new game (-1 for random)
     * @return the trained weights and biases representing the neural network
     */
    @Override
    public double[][][] selfTrain(double[][][] weightsAndBiases, int movesPerEpoch, long randomSeed) {
        if(maxEpochs == -1 && errorGoal == -1) errorGoal = 0.1;
        double[][][] prevChanges = new double[weightsAndBiases.length][][]; //Create Prev Changes: create the layers array
        for(int layer = 0; layer < weightsAndBiases.length; layer++){
            prevChanges[layer] = new double[weightsAndBiases[layer].length][]; //create the nodes arrays
            for (int node = 0; node < weightsAndBiases[layer].length; node++) {
                prevChanges[layer][node] = new double[weightsAndBiases[layer][node].length]; //create the connections arrays
                for (int prevNode = 0; prevNode < weightsAndBiases[layer][node].length; prevNode++) {
                    prevChanges[layer][node][prevNode] = 1; //initialize the connections
                }
            }
        }
        int epoch = 0;
        double error = Double.MAX_VALUE; //temporary value to get through the first while loop
        //End conditions: #epochs or errorGoal reached. -1 indicates that the other method is being used (both is applicable too)
        double previousError1 = Double.MAX_VALUE;
        double previousError2 = Double.MAX_VALUE;
        double previousError3 = Double.MAX_VALUE;
        double previousError4 = Double.MAX_VALUE;
        String results = "";
        while((epoch < maxEpochs || epoch == -1) && (errorGoal == -1 || error > errorGoal)) { //Start of each epoch
            error = 0;
            double[][][] weightsAndBiasChanges = new double[weightsAndBiases.length][][]; //Create W&BChanges: create the layers array
            for(int layer = 0; layer < weightsAndBiases.length; layer++){
                weightsAndBiasChanges[layer] = new double[weightsAndBiases[layer].length][]; //create the nodes arrays
                for (int node = 0; node < weightsAndBiases[layer].length; node++) {
                    weightsAndBiasChanges[layer][node] = new double[weightsAndBiases[layer][node].length]; //create the connections arrays
                    for (int prevNode = 0; prevNode < weightsAndBiases[layer][node].length; prevNode++) {
                        weightsAndBiasChanges[layer][node][prevNode] = 0; //initialize the connections
                    }
                }
            }//initialize weightsAndBiasChanges
            for (int exampleNum = 0; exampleNum < movesPerEpoch; exampleNum++) { //iterate through each training example
                SnakeGame snakeGame = new SnakeGame(12, randomSeed);
                while (exampleNum < movesPerEpoch) {
                    double[] trainingRow = snakeGame.getTrainingRow();
                    boolean display = false;
                    if (exampleNum == 0) display = true;
                    double[][] activationLayers = getActivationLayers(trainingRow, weightsAndBiases);
                    double[] desiredOutput = snakeGame.playAIMove();
                    if(snakeGame.isGameOver()) break;
                    error += calculateError(activationLayers[activationLayers.length - 1], desiredOutput);
                    //THE RECURSIVE FUNCTION CALL
                    trainRecursive(weightsAndBiases.length - 1, weightsAndBiases, weightsAndBiasChanges, activationLayers, desiredOutput, display);
                    exampleNum++;
                }
            }
            for(int layer = 0; layer < weightsAndBiases.length; layer++){
                for (int node = 0; node < weightsAndBiases[layer].length; node++) {
                    for (int prevNode = 0; prevNode < weightsAndBiases[layer][node].length; prevNode++) {
                        double currentGradient = weightsAndBiasChanges[layer][node][prevNode];
                        double previousGradient = prevChanges[layer][node][prevNode];
                        double step = 0;
                        if(currentGradient < 0) { //current gradient is negative
                            if(currentGradient * previousGradient < 0){ //Switched from positive to negative
                                step = -previousGradient * decreaseRate; //take negative of last step, decreased
                                if(step > -minStep) step = -minStep; //step can't be smaller (more positive) than -minStep
                            } else{ //was negative, still more negative
                                step = previousGradient * increaseRate; //negative gradient is now more negative
                                if(step < -maxStep) step = -maxStep; //step can't be greater (more negative) than -maxStep
                            }
                        }
                        else if(currentGradient > 0) { //current gradient is positive
                            if(currentGradient * previousGradient < 0){ //Switched from negative to positive
                                step = -previousGradient * decreaseRate; //take negative of last step, decreased
                                if(step < minStep) step = minStep; //step can't be smaller (more negative) than minStep
                            } else{ //was positive, still more positive
                                step = previousGradient * increaseRate; //positive gradient is now more positive
                                if(step > maxStep) step = maxStep; //step can't be greater (more positive) than maxStep
                            }
                        }
                        weightsAndBiases[layer][node][prevNode] -= step; //apply change to weight
                        prevChanges[layer][node][prevNode] = step; //record change to prevChanges
                    }
                }
            } //Rprop algorithm
            error = error / movesPerEpoch;
            results = results.concat(error+"\t");
            if(epoch % 50 == 0 || epoch == maxEpochs - 1) System.out.println("Epoch:"+epoch+" Error:"+error);
            finalError = error;
            if(Math.abs(error - previousError1) < 0.0000001) break;
            if(Math.abs(error - previousError2) < 0.0000001) break;
            if(Math.abs(error - previousError3) < 0.0000001) break;
            if(Math.abs(error - previousError4) < 0.0000001) break;
            previousError4 = previousError3;
            previousError3 = previousError2;
            previousError2 = previousError1;
            previousError1 = error;
            epoch++;
        }//epochs
        experimentResults = results;
        return weightsAndBiases;
    }//train

    //Original code from assignment 1: this performs the recursive back propagation part of the algorithm
    private void trainRecursive(int layer, double[][][] weightsAndBiases, double[][][] weightsAndBiasChanges, double[][] activationLayers, double[] desiredOutput, boolean display){
        double[] currActivationLayer = activationLayers[layer + 1];
        double[] prevActivationLayer = activationLayers[layer];
        double[] desiredInput = new double[prevActivationLayer.length];//new double[prevActivationLayer.length]; //desired input for back propagation
        for(int i = 0; i < desiredInput.length; i++) desiredInput[i] = 0;
        for (int node = 0; node < currActivationLayer.length; node++) { //iterate through each node in the current layer
            double activation =  currActivationLayer[node]; //put sum-product through activation function (e.g. sigmoid)
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
            trainRecursive(layer-1, weightsAndBiases, weightsAndBiasChanges, activationLayers, desiredInput.clone(), display);
        }
    }//trainRecursive

    //Original code from assignment 1: this calculates node activations for the feed forward part of the algorithm
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
    }//getActivationLayers

    //Original code from assignment 1: This calculates the error between desired and actual outputs.
    private double calculateError(double[] actualOutput, double[] desiredOutput){
        double error = 0;
        for(int i = 0; i < desiredOutput.length; i++){
            //System.out.println("actualOutput["+i+"] = "+actualOutput[i]+", desiredOutput["+i+"] = "+desiredOutput[i]);
            error += (desiredOutput[i] - actualOutput[i])*(desiredOutput[i] - actualOutput[i]);
        }
        return error;
    }//calculateError

    /**
     * Getter for experiment results
     * @return the error values for each epoch
     */
    public String getExperimentResults() {
        return experimentResults;
    }//getExperimentResults

    /**
     * Getter for final error value
     * @return the last error value calculated during training
     */
    public double getFinalError(){
        return finalError;
    }//getFinalError
}

