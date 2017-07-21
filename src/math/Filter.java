/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

/**
 *
 * @author Dell
 */
public class Filter {

    private int totalNum;
    private int filterOperatorNum;
    private int beginPosition;
    private String filterType;

    private double[] rawData;

    //Output data
    private double[] medianFilterData;
    private double[] averageFilterData;

    Filter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public double[] getMedianFilterData() {
        return medianFilterData;
    }

    public double[] getAverageFilterData() {
        return averageFilterData;
    }

    /**
     * Filter Constructor
     *
     * @param filterOperatorNum
     * @param beginPosition
     * @param mRawDoubleData
     */
    public Filter(int filterOperatorNum, int beginPosition, double[] rawData, String filterType) {
        this.totalNum = rawData.length;
        this.filterOperatorNum = filterOperatorNum;
        this.beginPosition = beginPosition;
        this.rawData = rawData;
        this.filterType = filterType;
    }

    //After contruct sucessfully, do this runFilter
    public double[] runFilter() {
        if (this.filterType == DataType.RUN_TYPE.MEDIAN_FILTER) {
            medianFilter(this.filterOperatorNum);
            return medianFilterData;
        } else if (this.filterType == DataType.RUN_TYPE.AVERAGE_FILTER) {
            averageFilter(this.filterOperatorNum);
            return averageFilterData;
        } else {
            return rawData;
        }
    }

    public void medianFilter(int filterOperatorNum) {
        int count;
        double[] value_buf = new double[filterOperatorNum];
        medianFilterData = new double[totalNum];
        for (int index = 0; index < totalNum; index++) {
            double sum = 0.0;
            if (index < totalNum - beginPosition - filterOperatorNum) {
                for (count = 0; count < filterOperatorNum; count++) {
                    value_buf[count] = rawData[index + count + beginPosition];
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
                medianFilterData[index] = (sum - max - min) / (filterOperatorNum - 2);
            } else {
                // The last N sample use average
                for (int idx = 0; idx < totalNum - index; idx++) {
                    sum += rawData[index + idx];
                }
                medianFilterData[index] = sum / (totalNum - index);
            }
        }
    }

    public void averageFilter(int filterOperatorNum) {
        averageFilterData = new double[totalNum];
        for (int index = 0; index < totalNum; index++) {
            double sum = 0.0;
            if (index < totalNum - beginPosition - filterOperatorNum) {
                for (int idx = 0; idx < filterOperatorNum; idx++) {
                    sum += rawData[idx + index + beginPosition];
                }
                averageFilterData[index] = sum / filterOperatorNum;
            } else {
                for (int idx = index; idx < totalNum; idx++) {
                    sum += rawData[idx + beginPosition];
                }
                averageFilterData[index] = sum / (totalNum - index);
            }
        }
    }

}
