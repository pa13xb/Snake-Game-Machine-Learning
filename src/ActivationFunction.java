/**
 * @author Philip Akkerman
 * studentID 5479613
 * email pa13xb@brocku.ca
 *
 * This interface specifies activation functions
 */
public interface ActivationFunction {
    double calculate(double x);

    double calcDerivative(double x);

    double calcDerivativeWActivation(double x);
}
