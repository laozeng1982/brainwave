/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

/**
 *
 * @author JianGe
 */
public class MathExtrem {

    /**
     * Get the max value
     *
     * @param array
     * @return
     */
    public static double getMax(double[] array) {
        double max = 0.0;
        for (int i = 0; i < array.length; i++) {
            max = max < array[i] ? array[i] : max;
        }
        return max;
    }

    /**
     * Get the min value
     *
     * @param array
     * @return
     */
    public static double getMin(double[] array) {
        double min = 0.0;
        for (int i = 0; i < array.length; i++) {
            min = min < array[i] ? min : array[i];
        }
        return min;
    }

    /**
     * Get the max abs value
     *
     * @param array
     * @return the abs max value of input
     */
    public static double getAbxMax(double[] array) {
        double absMax = 0.0;
        for (int i = 0; i < array.length; i++) {
            absMax = absMax < Math.abs(array[i]) ? Math.abs(array[i]) : absMax;
        }
        return absMax;
    }

    /**
     * Get the min abs value
     *
     * @param array
     * @return the abs min value of input
     */
    public static double getAbxMin(double[] array) {
        double absMin = 0.0;
        for (int i = 0; i < array.length; i++) {
            absMin = absMin < Math.abs(array[i]) ? absMin : Math.abs(array[i]);
        }
        return absMin;
    }
}
