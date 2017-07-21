/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

import java.util.ArrayList;
import utilities.Parameters;
import datamodel.SingleCurveInfo;
import datamodel.SingleFileCurveSets;
import tools.utilities.Logs;

/**
 *
 * @author Dell
 */
public class SpectralEnergy {

    FFT mFFT;
    TaperWindows mTaperWindows;
    Interpolation interpolation;
    static String mWinType;
    static int mNumToFFT; // number of data for each FFT runing
    static int mWinLenFFT; // Windows length
    static int totalNum;    //Total number to process
    static double[] mWindow;

    boolean isAverage = false;

    double mDt;
    DataType.FREQ_LINES mFreq_Lines;

    ArrayList<String> curveNameList = new ArrayList<>();
    ArrayList<Double> dataToProcess = new ArrayList<Double>();

    private static int AVERAGRE_LEN = 3600;

    //Create a curve holder
    String inputFileName;
    String inputCurveName;
    SingleFileCurveSets allOutput;
    int outputCurveNum = 0;

    public SpectralEnergy(SingleCurveInfo inputCurveInfo, Parameters parameters, boolean isAverage, int averageLen) {

        mNumToFFT = parameters.getFft_ProcLength();
        mWinLenFFT = parameters.getFft_WinLength();
        mWinType = parameters.getFft_WinType();

        this.isAverage = isAverage;
        this.AVERAGRE_LEN = averageLen;

        totalNum = inputCurveInfo.getCurveValueList().size();
        for (int i = 0; i < totalNum; i++) {
            dataToProcess.add(inputCurveInfo.getCurveValueList().getValueList().get(i).toDouble());

        }

        mFFT = new FFT(mNumToFFT, mWinLenFFT);
        mTaperWindows = new TaperWindows(mWinLenFFT, mWinType);
        mWindow = mTaperWindows.getWindow();

        //????
        this.mDt = 1.0 / parameters.getFft_Samplerate();
        mFreq_Lines = parameters.getFreq_Lines();

        inputFileName = inputCurveInfo.getFileName();
        inputCurveName = inputCurveInfo.getCurveName();

        allOutput = new SingleFileCurveSets(inputFileName);

        //Add curve 
        for (int idx = 0; idx < mFreq_Lines.getLinesList().size(); idx++) {
            Logs.e("Line " + idx + " is: " + mFreq_Lines.getLinesList().get(idx));
            if (mFreq_Lines.getLinesList().get(idx) > 0) {
                if (idx == 0) {
                    curveNameList.add(inputCurveName + "_f0_" + mFreq_Lines.getLinesList().get(idx));
                    allOutput.addCurve(new SingleCurveInfo(curveNameList.get(idx)));
                } else if (0 < idx && idx < mFreq_Lines.getLinesList().size()) {
                    curveNameList.add(inputCurveName + "_f" + mFreq_Lines.getLinesList().get(idx - 1) + "_" + mFreq_Lines.getLinesList().get(idx));
                    allOutput.addCurve(new SingleCurveInfo(curveNameList.get(idx)));
                }
                outputCurveNum++;
            } else {
                curveNameList.add(inputCurveName + "_f" + mFreq_Lines.getLinesList().get(idx - 1));
                allOutput.addCurve(new SingleCurveInfo(curveNameList.get(idx)));
                outputCurveNum++;
                break;
            }
        }

    }

    public void runFFT() {

        // Calculate windows 
        double[] re = new double[mNumToFFT];
        double[] im = new double[mNumToFFT];
        double[] windData = new double[mNumToFFT];
        double[][] outArray = new double[outputCurveNum][totalNum];
        for (int outLoop = 0; outLoop < totalNum; outLoop++) {
            for (int i = 0; i < mNumToFFT; i++) {
                re[i] = dataToProcess.get(i);
                im[i] = 0;
                windData[i] = 0.0;
            }
            for (int ii = 0; ii < mWindow.length; ii++) {
                if (ii + outLoop < totalNum) {
                    re[ii] = dataToProcess.get(outLoop + ii);
                } else {
                    re[ii] = dataToProcess.get(2 * totalNum - outLoop - ii - 2);  //mirror data
                }
            }
//Logs.e(outLoop + "   " + re[0]);
            double meanData = 0.0;
            for (int k = 0; k < mWindow.length; k++) {
                windData[k] = re[k] * mWindow[k];
            }
            meanData = ArrayMath.meanVal(windData);
            //removel the DC coomponent
            for (int k = 0; k < mWindow.length; k++) {
                windData[k] = windData[k] - meanData;
            }

            // Fourier Transform
            mFFT.fft(windData, im);
            // Calculate Specdcom
            double mDf = 1.0 / mDt / mNumToFFT;
            double[] specData = new double[mNumToFFT / 2];
            double totalEnergy = 0.0;

            for (int idx = 0; idx < specData.length; idx++) {
                specData[idx] = windData[idx] * windData[idx] + im[idx] * im[idx];
                totalEnergy = totalEnergy + specData[idx];

            }

            // Calculate Spectral Energy
            int[] indexLine = new int[outputCurveNum + 1];
            indexLine[0] = 0;
            indexLine[outputCurveNum] = specData.length;
            // Get Frequence Lines
            for (int idx = 0; idx < outputCurveNum; idx++) {
                if (idx + 1 < outputCurveNum) {
                    indexLine[idx + 1] = (int) (mFreq_Lines.getLinesList().get(idx) / mDf);
                }

                double temp = 0.0;
                for (int i = indexLine[idx]; i < indexLine[idx + 1]; i++) {
                    temp = temp + specData[i];
                }

                outArray[idx][outLoop] = temp / totalEnergy;
//                Logs.e(idx+",  " +outLoop+",  "+outArray[idx][outLoop] );
//                allOutput.getSelectedCurveData(curveNameList.get(idx)).getCurveValueList().add(temp / totalEnergy);
            }
        }

        //
        Number[] aaa = new Number[outArray.length];
        if (isAverage) {
            for (int idx = 0; idx < outArray.length; idx++) {
//                allOutput.getSelectedCurveData(curveNameList.get(idx)).getCurveValueList().add(aaa[idx], getAverage(outArray[idx]));
            }
        } else {
            for (int idx = 0; idx < outArray.length; idx++) {
//                allOutput.getSelectedCurveData(curveNameList.get(idx)).getCurveValueList().add(0, outArray[idx]);
            }
        }

//        for (int i = 0; i < outArray[1].length; i++) {
//            for (int idx = 0; idx < outArray.length; idx++) {
//                Logs.e(i+",  " +idx+",  "+allOutput.getSelectedCurveData(curveNameList.get(idx)).getCurveValueList().get(i).toString());
//
//            }
//        }
    }

    public static double determinLevel(double value) {
        if (value >= 0 && value <= 0.2) {
            return 1.0;
        } else if (value > 0.2 && value <= 0.4) {
            return 2.0;
        } else if (value > 0.4 && value <= 0.6) {
            return 3.0;
        } else if (value > 0.6 && value <= 0.8) {
            return 4.0;
        } else if (value > 0.8 && value <= 1.0) {
            return 5.0;
        }
        return 0.0;
    }

    public double[] getAverage(double[] sig) {
        int count = sig.length / AVERAGRE_LEN;
        double[] output = new double[sig.length];
        //the data 
        for (int index = 0; index < count; index++) {
            double sum = 0.0;
            for (int idx = 0; idx < AVERAGRE_LEN; idx++) {
                sum += sig[index * AVERAGRE_LEN + idx];
            }
            for (int idx = 0; idx < AVERAGRE_LEN; idx++) {
                output[index * AVERAGRE_LEN + idx] = sum / AVERAGRE_LEN;
            }
        }
        //the data after "count * AVERAGRE_LEN"

        for (int index = count * AVERAGRE_LEN; index < sig.length; index++) {
            double sum = 0.0;
            for (int idx = index; idx < sig.length; idx++) {
                sum += sig[idx];
            }
            output[index] = sum / (sig.length - index);
//            Logs.e(index + ",  " + sum + ",  " + output[index]);
//            for (int idx = index; idx < sig.length; idx++) {
            output[index] = sum / (sig.length - index);
//            }
        }

        return output;
    }

    public SingleFileCurveSets getAllOutput() {
        return allOutput;
    }

}
