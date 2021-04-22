/**
 * @author Philip Akkerman
 * studentID 5479613
 * email pa13xb@brocku.ca
 *
 * This class implements the Logistic (aka sigmoid) activation function
 */
public class Sigmoid implements ActivationFunction {
    @Override
    public double calculate(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    @Override
    public double calcDerivative(double x){
        return calculate(x) * (1 - calculate(x));
    }


    @Override
    public double calcDerivativeWActivation(double x) { return x * (1 - x); }
}
