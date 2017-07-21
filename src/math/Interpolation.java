/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

import java.util.ArrayList;

public class Interpolation {
//???¨æ??ç®?????çº¿æ?§æ???¼ç??æ³?ï¼?å¦???????¼ç???°æ?????¨æ?µç??ï¼????§ä?????????¼ï??±ä????¨ä??¨æ???°ç?ï¼?è¾??ºç???¶å??ï¼????¨ç????ArrayList
//Y=(Y1-Y0)/(X1-X0)*(X-X0)+Y0
//ä¸ºä??²æ??X0=X1ï¼?X?????¶é?´å???äº???ä¸????·ç??

    ArrayList<Double> mInterpData = new ArrayList<>();
    ArrayList<Long> mMsArrayList = new ArrayList<>();
    int mCountInterp;
    private final double eps = 10E-5;

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

    public void runInterpolation(long[] xVal, double[] yVal, long dt) {
        long firstTime = xVal[0];
        long firstTimeInt = firstTime + firstTime % dt;
        //æ¯?æ¬¡è?»å??ä¸????????°æ??ï¼?mark???????¶é?´ç??
        int index = 0;
        long resample, currenTime, nexTime;

        int indexMs = 0;
        resample = firstTimeInt;
        int N = xVal.length;
//        System.out.println("Total lines " + N);
        //Log.d(TAG, String.valueOf(N));
        while (index < N - 1) {
            //?¹æ???¶é?´ç????¨ç??ä½?ç½?
            currenTime = xVal[index];
            nexTime = xVal[index + 1];

            double y0 = yVal[index];
            double y1 = yVal[index + 1];

            while (resample >= currenTime && resample <= nexTime) {
                double val = 0.0;
                //private double pointVal(double x0, double y0, double x1, double y1, double x,double dt)                
                resample = firstTime + dt * indexMs;
                indexMs = indexMs + 1;
                val = pointVal((double) currenTime, y0, (double) nexTime, y1, (double) resample, dt);
                mMsArrayList.add(resample);
                mInterpData.add(val);
                //     System.out.println("time===" + mMsArrayList.get(indexMs-1) + " Val==   " + mInterpData.get(indexMs-1));
            }
            index = index + 1;
            // System.out.println("secData"+index);
        }
        mCountInterp = mInterpData.size();
        //System.out.println("Total Data: " + mCountInterp);
    }

}
