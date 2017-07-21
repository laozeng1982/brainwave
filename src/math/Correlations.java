/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

import tools.utilities.Logs;

/**
 *
 * @author JianGe
 */
public class Correlations {

    double[] x;
    double[] y;
    int winLength;  //The length of move windows
    double[] correlations;  //Correlation curve
    double singleCorrelation;   //Correlation of two curves

    public Correlations(double[] x, double[] y) {
        if (x.length != y.length) {
            Logs.e("wrong input");
            return;
        }
        this.x = x;
        this.y = y;
    }
    
        public Correlations(double[] x, double[] y, int winlen) {
            this(x,y);
            this.winLength =winlen;
    }

    public void runCorrelations() {
        double xMean = ArrayMath.meanVal(x);
        double yMean = ArrayMath.meanVal(y);
        double upper = 0.0;
        double lower1 = 0.0;
        double lower2 = 0.0;
        for (int i = 0; i < x.length; i++) {
            upper += (x[i] - xMean) * (y[i] - yMean);
            lower1 += (x[i] - xMean) * (x[i] - xMean);
            lower2 += (y[i] - yMean) * (y[i] - yMean);
        }
        singleCorrelation = upper / Math.sqrt(lower1 * lower2);
    }

    public double[] getX() {
        return x;
    }

    public void setX(double[] x) {
        this.x = x;
    }

    public double[] getY() {
        return y;
    }

    public void setY(double[] y) {
        this.y = y;
    }

    public double[] getCorrelations() {
        return correlations;
    }

    public void setCorrelations(double[] correlations) {
        this.correlations = correlations;
    }

}
