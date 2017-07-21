package math;

import datamodel.CurveValues;

public interface ProcessInterface {

//    public abstract void initFilter(String type, int length);

    public abstract CurveValues runFilter(CurveValues curveValues);

//    public abstract void initFft(int number, int winHalfLen);

    public abstract CurveValues runFft(CurveValues curveValues);

    public abstract CurveValues runInterpolation(CurveValues curveValues);
    
}
