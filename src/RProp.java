import java.util.LinkedList;

/**
 * @author Philip Akkerman
 * studentID 5479613
 * email pa13xb@brocku.ca
 *
 * This class implements Rprop. It borrows most of its code from the BackPropagation class, and only changes in the
 * updating of weights outside of the recursive call.
 */
public class RProp implements LearningRule {

    private double increaseRate;
    private double decreaseRate;
    private double maxStep;
    private double minStep;
    private int maxEpochs;
    private double errorGoal;
    private ActivationFunction activationFunction;

    RProp(){
        this.activationFunction = new Sigmoid();
        maxEpochs = -1;
        errorGoal = -1;
    }

    @Override
    public void setLearningRate(double learningRate) {  }

    @Override
    public void setMomentum(double momentum) { }

    @Override
    public void setIncreaseRate(double increaseRate) {  this.increaseRate = increaseRate;  }

    @Override
    public void setDecreaseRate(double decreaseRate) {  this.decreaseRate = decreaseRate;  }

    @Override
    public void setMaxStep(double maxStep) {  this.maxStep = maxStep;  }

    @Override
    public void setMinStep(double minStep) {  this.minStep = minStep;  }

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

    @Override
    public double[][][] selfTrain(double[][][] weightsAndBiases, int movesPerEpoch) {
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
            for (int exampleNum = 0; exampleNum < movesPerEpoch; exampleNum++) { //iterate through each training example
                SnakeGame snakeGame = new SnakeGame(12);
                while (exampleNum < movesPerEpoch) {
                    double[] trainingRow = snakeGame.getTrainingRow();
                    boolean display = false;
                    if (exampleNum == 0) display = true;
                    double[][] activationLayers = getActivationLayers(trainingRow, weightsAndBiases);
                    double[] desiredOutput = snakeGame.calculateBestMove(1,1,1); //initialize desiredOutput for the output layer
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
            }
            error = error / movesPerEpoch;
            if (epoch % 50 == 0) System.out.println("Epoch " + epoch + ", Error: " + error);
            epoch++;
        }//epochs
        return weightsAndBiases;
    }//train

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

    //This calculates the error between desired and actual outputs.
    private double calculateError(double[] actualOutput, double[] desiredOutput){
        double error = 0;
        for(int i = 0; i < desiredOutput.length; i++){
            error += (desiredOutput[i] - actualOutput[i])*(desiredOutput[i] - actualOutput[i]);
        }
        return error;
    }//calculateError
}

