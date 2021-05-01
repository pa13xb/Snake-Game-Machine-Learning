/**
 * @author Philip Akkerman
 * studentID 5479613
 * email pa13xb@brocku.ca
 *
 * This class implements the Logistic (aka sigmoid) activation function and its derivative
 *
 * Note: this code was modified from the neural network created by Philip Akkerman for
 * assignment 1.
 */
public class Sigmoid implements ActivationFunction {
    //Original code from assignment 1
    @Override
    public double calculate(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    //Original code from assignment 1
    @Override
    public double calcDerivative(double x){
        return calculate(x) * (1 - calculate(x));
    }

    //Original code from assignment 1
    @Override
    public double calcDerivativeWActivation(double x) { return x * (1 - x); }
}
