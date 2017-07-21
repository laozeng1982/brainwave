/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

import utilities.Parameters;
import datamodel.CurveValues;

/**
 *
 * @author JianGe
 */
public class ProcessUtilsOnlyCurveData implements ProcessInterface {

    //Parameters, which was used to calculated this curve if it was calculated
    public Parameters parameters;
    public int mBeginToProcess;
    private static final double eps = 10E-5;

    public ProcessUtilsOnlyCurveData() {
        this.parameters = new Parameters();
    }

    public ProcessUtilsOnlyCurveData(Parameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public CurveValues runFilter(CurveValues curveValues) {
        if (parameters.getFilterType().equals("me")) {
            return medianFilter(parameters.getFilterLength(), curveValues);
        } else if (true) {
            return averageFilter(parameters.getFilterLength(), curveValues);
        } else {
            return curveValues;
        }
    }

    @Override
    public CurveValues runFft(CurveValues curveValues) {
        double[] x = new double[curveValues.size()];
        double[] y = new double[curveValues.size()];
        CurveValues fft_curveValues = new CurveValues();
        for (int i = 0; i < curveValues.size(); i++) {
            x[i] = curveValues.getValueList().get(i).toDouble();
        }
        //run fft function
        fft(x, y);
        for (int i = 0; i < y.length; i++) {
            fft_curveValues.add(y[i]);
        }
        return fft_curveValues;
    }

    @Override
    public CurveValues runInterpolation(CurveValues curveValues) {
        return null;
    }

    //Filter part
    //--------------------------------------------------------------------------------------------------------------------------
    public CurveValues medianFilter(int filterOperatorNum, CurveValues curveValues) {
        int count;
        double[] value_buf = new double[filterOperatorNum];
        int totalNum = curveValues.size();
        CurveValues medianFilterData = new CurveValues();

        for (int index = 0; index < totalNum; index++) {
            double sum = 0.0;
            if (index < totalNum - filterOperatorNum) {
                for (count = 0; count < filterOperatorNum; count++) {
                    value_buf[count] = curveValues.getValueList().get(index + count + mBeginToProcess).toDouble();
                }
                double max = 0.0;
                double min = 0.0;
                for (count = 0; count < filterOperatorNum; count++) {
                    max = max > value_buf[count] ? value_buf[count] : max;
                    min = min < value_buf[count] ? value_buf[count] : min;
                }
                for (count = 0; count < filterOperatorNum; count++) {
                    sum += value_buf[count];
                }

                medianFilterData.add((sum - max - min) / (filterOperatorNum - 2));
            } else {
                // The last N sample use average
                for (int i = 0; i < totalNum - index; i++) {
                    sum += curveValues.getValueList().get(index + i + totalNum).toDouble();
                }
                medianFilterData.add(sum / (totalNum - index));
            }
        }
        return medianFilterData;
    }

    public CurveValues averageFilter(int filterOperatorNum, CurveValues curveValue) {
        CurveValues mAverageFilterData = new CurveValues();
        int totalNum = curveValue.size();
        for (int index = 0; index < totalNum; index++) {
            double sum = 0.0;
            if (index < totalNum - filterOperatorNum) {
                for (int i = 0; i < filterOperatorNum; i++) {
                    sum += curveValue.getValueList().get(i + index + mBeginToProcess).toDouble();
                }
                mAverageFilterData.add(sum / filterOperatorNum);
            } else {
                for (int i = index; i < totalNum; i++) {
                    sum += curveValue.getValueList().get(i + mBeginToProcess).toDouble();
                }
                mAverageFilterData.add(sum / (totalNum - index));
            }
        }
        return mAverageFilterData;
    }

    //Interplotation part
    //--------------------------------------------------------------------------------------------------------------------------
    /**
     * Two point interpolation
     *
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param x
     * @param dt
     * @return
     */
    private double pointVal(double x0, double y0, double x1, double y1, double x, double dt) {
        double a;
        double poitY;
        if (Math.abs(x1 - x0) > eps) {
            a = (y1 - y0) / (x1 - x0);
        } else {
            a = (y1 - y0) / (x1 - x0 + dt / 2.0);
        }
        poitY = y0 + a * (x - x0);
        return poitY;
    }

    //FFT part
    //--------------------------------------------------------------------------------------------------------------------------
    public void fft(double[] x, double[] y) {
        int i, j, k, n1, n2, a;
        double c, s, e, t1, t2;

        int totalNumber = parameters.getFft_ProcLength();
        int Mi = (int) (Math.log(totalNumber) / Math.log(2));
        double[] cos;
        double[] sin;

        // Make sure totalNum is a power of 2
        if (totalNumber != (1 << Mi)) {
            throw new RuntimeException("FFT length must be power of 2");
        }

        // precompute tables
        cos = new double[totalNumber / 2];
        sin = new double[totalNumber / 2];

        for (i = 0; i < totalNumber / 2; i++) {
            cos[i] = Math.cos(-2 * Math.PI * i / totalNumber);
            sin[i] = Math.sin(-2 * Math.PI * i / totalNumber);
        }

        // Bit-reverse
        j = 0;
        n2 = totalNumber / 2;
        for (i = 1; i < totalNumber - 1; i++) {
            n1 = n2;
            while (j >= n1) {
                j = j - n1;
                n1 = n1 / 2;
            }
            j = j + n1;

            if (i < j) {
                t1 = x[i];
                x[i] = x[j];
                x[j] = t1;
                t1 = y[i];
                y[i] = y[j];
                y[j] = t1;
            }
        }

        // FFT
        n1 = 0;
        n2 = 1;

        for (i = 0; i < Mi; i++) {
            n1 = n2;
            n2 = n2 + n2;
            a = 0;

            for (j = 0; j < n1; j++) {
                c = cos[a];
                s = sin[a];
                a += 1 << (Mi - i - 1);

                for (k = j; k < totalNumber; k = k + n2) {
                    t1 = c * x[k + n1] - s * y[k + n1];
                    t2 = s * x[k + n1] + c * y[k + n1];
                    x[k + n1] = x[k] - t1;
                    y[k + n1] = y[k] - t2;
                    x[k] = x[k] + t1;
                    y[k] = y[k] + t2;
                }
            }
        }
    }

}
