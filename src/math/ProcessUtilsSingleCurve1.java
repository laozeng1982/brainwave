/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

import datamodel.ValueElement;
import utilities.Parameters;
import datamodel.SingleCurveInfo;
import tools.utilities.Logs;

/**
 *
 * @author JianGe
 */
public class ProcessUtilsSingleCurve1 extends Object {

    //Parameters, which was used to calculated this curve if it was calculated
    public Parameters parameters;
    public int beginPostion = 0;
    public int processLength = 0;
    public int endPosition;
    private String suffix = "";
    private static final double eps = 10E-5;

    public ProcessUtilsSingleCurve1() {
        this.parameters = new Parameters();
    }

    public ProcessUtilsSingleCurve1(Parameters parameters) {
        this.parameters = parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public void setBeginPostion(int beginToProcess) {
        this.beginPostion = beginToProcess;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void autoSetSuffix(String string) {

    }

    private String getCurveName(SingleCurveInfo curveInfo, String type) {
        //inner class
        String suffix = "";
        if (this.suffix == "") {
            switch (type) {
                case DataType.RUN_TYPE.BANDPASS_FILTER:
                    break;
                case DataType.RUN_TYPE.MEDIAN_FILTER:
                    suffix = parameters.getFilterType().toString() + parameters.getFilterLength();
                    break;
                case DataType.RUN_TYPE.AVERAGE_FILTER:
                    suffix = parameters.getFilterType().toString() + parameters.getFilterLength();
                    break;
//                case DataType.RUN_TYPE.INTERPLOATION:
//                    break;
                case DataType.RUN_TYPE.FFT:
                    suffix = parameters.getFft_WinType() + parameters.getFft_WinLength();
                    break;
                default:
                    suffix = this.suffix;
                    break;
            }
        } else {
            suffix = this.suffix;
        }
        return curveInfo.getCurveName() + "_" + suffix;
    }

    //Mathmatic functions
    //------------------------------------------------------------------------------------------------------
    public SingleCurveInfo runFilter(SingleCurveInfo curveInfo) {

        if (parameters.getFilterType().equals("Median")) {
            return medianFilter(parameters.getFilterLength(), curveInfo);
        } else if (parameters.getFilterType().equals("Average")) {
            return averageFilter(parameters.getFilterLength(), curveInfo);
        } else {
            return curveInfo;
        }
    }

    public SingleCurveInfo runFft(SingleCurveInfo curveInfo) {
        Logs.e("FFT length is " + parameters.getFft_WinLength());
        double[] x = new double[curveInfo.size()];
        double[] y = new double[curveInfo.size()];
        SingleCurveInfo fft_curveInfo = new SingleCurveInfo(getCurveName(curveInfo, DataType.RUN_TYPE.FFT));
        for (int i = 0; i < curveInfo.size(); i++) {
            x[i] = curveInfo.getCurveValueList().getValueList().get(i).toDouble();
        }
        //run fft function
        fft(x, y);
        for (int i = 0; i < y.length; i++) {
            fft_curveInfo.getCurveValueList().add(y[i]);
        }
        return fft_curveInfo;
    }

    public SingleCurveInfo runInterpolation(SingleCurveInfo curveValues) {
        return null;
    }

    //Filter part
    //--------------------------------------------------------------------------------------------------------------------------
    public SingleCurveInfo medianFilter(int filterOperatorNum, SingleCurveInfo curveInfo) {
        int count;
        double[] value_buf = new double[filterOperatorNum];
        int totalNum = curveInfo.size();
        SingleCurveInfo medianFilterData = new SingleCurveInfo(getCurveName(curveInfo, DataType.RUN_TYPE.MEDIAN_FILTER));

        for (int index = 0; index < totalNum; index++) {
            double sum = 0.0;
            if (index < totalNum - filterOperatorNum) {
                for (count = 0; count < filterOperatorNum; count++) {
                    value_buf[count] = curveInfo.getCurveValueList().getValueList().get(index + count + beginPostion).toDouble();
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

                medianFilterData.getCurveValueList().add((sum - max - min) / (filterOperatorNum - 2));
            } else {
                // The last N sample use average
                for (int i = 0; i < totalNum - index; i++) {
                    sum += curveInfo.getCurveValueList().getValueList().get(index + i + beginPostion).toDouble();
                }
                medianFilterData.getCurveValueList().add((sum / (totalNum - index)));
            }
        }
        return medianFilterData;
    }

    public SingleCurveInfo averageFilter(int filterOperatorNum, SingleCurveInfo curveInfo) {
        SingleCurveInfo mAverageFilterData = new SingleCurveInfo(getCurveName(curveInfo, DataType.RUN_TYPE.AVERAGE_FILTER));
        int totalNum = curveInfo.size();
        for (int index = 0; index < totalNum; index++) {
            double sum = 0.0;
            if (index < totalNum - filterOperatorNum) {
                for (int i = 0; i < filterOperatorNum; i++) {
                    sum += curveInfo.getCurveValueList().getValueList().get(i + index + beginPostion).toDouble();
                }
                mAverageFilterData.getCurveValueList().add((sum / filterOperatorNum));
            } else {
                for (int i = index; i < totalNum; i++) {
                    sum += curveInfo.getCurveValueList().getValueList().get(i + beginPostion).toDouble();
                }
                mAverageFilterData.getCurveValueList().add((sum / (totalNum - index)));
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

    public void spectralEnergy(int number, int winLength, String winName, double sample, double dt, int mCountInterp,
            DataType.FREQ_LINES freq_Lines) {

    }

}
