/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

import tools.utilities.Logs;



/**
 *
 * @author SeisAki
 */
public class TaperWindows {

    int winHalfLen;
    String winName;
    double[] winData;

    public TaperWindows(int winHalfLen, String winName) {
        this.winHalfLen = winHalfLen;
        this.winName = winName;
        makeWindow();
    }

    public void makeWindow() {
        winData = new double[winHalfLen * 2 + 1];
        double t;
        if (winName != null) {
            switch (winName) {
                case DataType.WINDOWS_TYPE.BLACKMAN_WINDOW:
                    for (int i = 0; i < winData.length; i++) {
                        t = (double) (i - winHalfLen) / (double) winHalfLen * 0.5;
                        winData[i] = 0.42 + 0.5 * Math.cos(2 * Math.PI * t)
                                + 0.08 * Math.cos(4 * Math.PI * t);
                    }
                    Logs.e("Blackman window  used!");
                    break;

                case DataType.WINDOWS_TYPE.HAMMING_WINDOW:
                    for (int i = 0; i < winData.length; i++) {
                        t = (double) (i - winHalfLen) / (double) winHalfLen * 0.5;
                        winData[i] = 0.54 + 0.46 * Math.cos(2 * Math.PI * t);
                    }
                    Logs.e("Hamming window  used!");
                    break;

                case DataType.WINDOWS_TYPE.RECTANGLE_WINDOW:
                    for (int i = 0; i < winData.length; i++) {
                        winData[i] = 1.0;
                    }
                    Logs.e("Rectangle window  used!");
                    break;

                case DataType.WINDOWS_TYPE.HANNING_WINDOW:
                    for (int i = 0; i < winData.length; i++) {
                        t = (double) (i - winHalfLen) / (double) winHalfLen * 0.5;
                        winData[i] = Math.pow(Math.cos(Math.PI * t), 2.0);
                    }
                    Logs.e("Hanning window  used!");
                    break;

                case DataType.WINDOWS_TYPE.GAUSS_WINDOW:
                    //default gauss window
                    double alpha = 18.0;
                    double t2;
                    for (int i = 0; i < winData.length; i++) {
                        t = (double) (i - winHalfLen) / (double) winHalfLen * 0.5;
                        t2 = Math.pow(t, 2.0);
                        winData[i] = Math.exp(-alpha * t2);
                    }
                    Logs.e("Gauss window  used!");
                    break;

                default:
                    break;
            }
        }

    }

    public double[] getWindow() {
        return winData;
    }
}
