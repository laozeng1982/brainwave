/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import tools.utilities.Logs;

/**
 *
 * @author JianGe
 */
//List to hold data, using String, and must by convert to numeric
public class CurveValues {

    private double[] arrayValues;
    private double absMax;
    private double absMin;
    private double max;
    private double min;
    private boolean hasMinusVal;
    private ArrayList<ValueElement> vauleList = new ArrayList<>();

    public double getAbsMax() {
        detectMaxMin();
        return absMax;
    }

    public double getAbsMin() {
        detectMaxMin();
        return absMin;
    }

    public double getMax() {
        detectMaxMin();
        return max;
    }

    public double getMin() {
        detectMaxMin();
        return min;
    }

    public boolean hasMinusVal() {
        for (int i = 0; i < this.vauleList.size(); i++) {
            if (this.vauleList.get(i).toDouble() < 0) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<ValueElement> getValueList() {
        return vauleList;
    }

    public void setCurveList(ArrayList<ValueElement> curveList) {
        this.vauleList = curveList;
    }

    private void detectMaxMin() {
        for (int i = 0; i < this.vauleList.size(); i++) {
            absMax = absMax < Math.abs(this.vauleList.get(i).toDouble()) ? Math.abs(this.vauleList.get(i).toDouble()) : absMax;
            absMin = absMin > Math.abs(this.vauleList.get(i).toDouble()) ? Math.abs(this.vauleList.get(i).toDouble()) : absMin;
            max = max < this.vauleList.get(i).toDouble() ? this.vauleList.get(i).toDouble() : max;
            min = min > this.vauleList.get(i).toDouble() ? this.vauleList.get(i).toDouble() : min;
        }
    }

    public double[] getArrayValues() {
        if (this.vauleList.size() > 0) {
            arrayValues = new double[this.vauleList.size()];
            for (int i = 0; i < this.vauleList.size(); i++) {
                arrayValues[i] = this.vauleList.get(i).toDouble();
            }
            return arrayValues;
        } else {
            return null;
        }
    }
    
        public Date[] getArrayDateValues() {
        if (this.vauleList.size() > 0) {
           Date[] arrayValues = new Date[this.vauleList.size()];
            SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss");
            for (int i = 0; i < this.vauleList.size(); i++) {
                try {
                    arrayValues[i] = sdf.parse(this.vauleList.get(i).toString());
                    Logs.e(arrayValues[i].toString());
                } catch (Exception e) {
                }
                
            }
            return arrayValues;
        } else {
            return null;
        }
    }

    public void add(String value) {
        this.vauleList.add(new ValueElement(value));

    }

//    public void add(short value) {
//        this.vauleList.add(new ValueElement(value));
//
//    }
//
//    public void add(int value) {
//        this.vauleList.add(new ValueElement(value));
//
//    }
//
//    public void add(float value) {
//        this.vauleList.add(new ValueElement(value));
//
//    }
//
//    public void add(double value) {
//        this.vauleList.add(new ValueElement(value));
//
//    }
    public void add(Number value) {
        this.vauleList.add(new ValueElement(value));

    }

    public void add(Number[] values) {
        for (int i = 0; i < values.length; i++) {
            this.vauleList.add(new ValueElement(String.valueOf(values[i])));

        }
    }

    public int size() {
        return this.vauleList.size();
    }

    public boolean isEmpty() {
        return this.vauleList.isEmpty();
    }

    public void clear() {
        this.vauleList.clear();
    }
}
