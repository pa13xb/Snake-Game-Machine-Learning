import java.util.LinkedList;
/**
 * @author Philip Akkerman
 * studentID 5479613
 * email pa13xb@brocku.ca
 *
 * This interface specifies a learning rule, and accomodates backprop as well as rprop
 */
public interface LearningRule {

    void setLearningRate(double learningRate);

    void setMomentum(double momentum);

    void setIncreaseRate(double increaseRate);

    void setDecreaseRate(double decreaseRate);

    void setMaxStep(double maxStep);

    void setMinStep(double minStep);

    void setMaxEpochs(int maxEpochs);

    void setErrorGoal(double errorGoal);

    void setActivationFunction(ActivationFunction activationFunction);

    double[][][] train(double[][][] weightsAndBiases, LinkedList<double[]> trainingSet, LinkedList<double[]> desiredResults);
}
