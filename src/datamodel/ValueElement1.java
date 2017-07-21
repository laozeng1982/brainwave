/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel;

/**
 * Single Curve Element, which hold just a point value Also needs methods to
 * convert String value to double, integer, etc.
 *
 * @author JianGe
 */
public class ValueElement1 extends Object {

    private String timeString;
    private String valueString;

    public ValueElement1(String timeString, String valueString) {
        this.timeString = timeString;
        this.valueString = valueString;
    }

    public ValueElement1(Number time, Number valueInt) {
        this.timeString = String.valueOf(time);
        this.valueString = String.valueOf(valueInt);
    }

//    public ValueElement(float time, int valueInt) {
//        this.timeString = String.valueOf(time);
//        this.valueString = String.valueOf(valueInt);
//    }
//
//    public ValueElement(float time, float valueFloat) {
//        this.timeString= String.valueOf(time);
//        this.valueString = String.valueOf(valueFloat);
//    }
//
//    public ValueElement(double valueDouble) {
//        this.valueString = String.valueOf(valueDouble);
//    }
    public float getTimeFloat() {
        return Float.valueOf(timeString);
    }

    public String getTimeString() {
        return this.timeString;
    }

    public int toInt() {
        return Integer.valueOf(valueString);
    }

    public float toFloat() {
        return Float.valueOf(valueString);
    }

    public double toDouble() {
        return Double.valueOf(valueString);
    }

    @Override
    public String toString() {
        return this.valueString;
    }
}
