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
public class ValueElement extends Object {

    private String valueString;

    public ValueElement(String valueString) {

        this.valueString = valueString;
    }

    public ValueElement(Number valueInt) {
        this.valueString = String.valueOf(valueInt);
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
