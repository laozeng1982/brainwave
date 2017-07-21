package utilities;

import datamodel.SingleCurveInfo;
import math.FFT;
import math.DataType;
import math.TaperWindows;
import edu.mines.jtk.dsp.BandPassFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import tools.utilities.Logs;


/**
 * Class to invoke ever signal process function and get data
 *
 * @author JianGe
 */
public class DataGenerator {

    FileOperator fileOperator;
    static String inFileName;
    static String outFileName;

    static int numberLines; // total lines of the raw data file
    static String[] mRecordTime;
    static float[] mRawFloatData;  //the orignal float type data
    static double[] mRawDoubleData; // the orignal double type data
    static float[] mRawChosenData;
    static float[] mBandpassFilterData;  //data after filter operation
    static double[] mAverageFilterData;
    static double[] mMdileFilterData;
    static int mTotalLenToProcess; // total number to process
    static int mBeginToProcess; // the location of the begining to start process

    ArrayList<Double> mInterpData = new ArrayList<>();
    ArrayList<Long> mMsArrayList = new ArrayList<>();
    int mCountInterp;

    //FFT process parameters
    FFT mFFT;
    static int mNnumToFFT; // number of data for each FFT runing
    static int mWinLenFFT; // Windows length
    TaperWindows mTaperWindows;
    static String mWinName;

    static double[] mWindow;
    static int mDt;
    double mDf;

    double[] re;
    double[] im;
    double[] windData;
    double[] specData;
    double totalEnergy;
    static double[] mDeltaEnergy;
    static double[] mThetaEnergy;
    static double[] mAlphaEnergy;
    static double[] mBetaEnergy;
    String[] mSaveData;

    // Bandpass Filter parameters
    static boolean isNeedFiter;
    BandPassFilter mBandPassFilter;
    static double kLower;
    static double kUpper;
    static double kWidth;
    static double kAerror;

    //square wave parameter
    static final int LEVEL = 5;
    static double steps = 1.0 / LEVEL;

    //
    double mDeltaFreq;
    double mThetaFreq;
    double mAlphaFreq;
    double mBeta1Freq;
    double mBeta2Freq;
    boolean mIsInitFreqLine = false;

    //
    final int AVERAGRE_LEN = 3600; //60*60

    ArrayList<SingleCurveInfo> mAListProcessedSignal = new ArrayList<>();

    static float costTime;

    public DataGenerator(String inputfile, String outputfile) {
        // TODO Auto-generated constructor stub
        inFileName = inputfile;
        outFileName = outputfile;
        fileOperator = new FileOperator(inFileName, outFileName);
        numberLines = fileOperator.mOrgCount;


        costTime = 0.0f;
    }

    /**
     * Initiate data to do filter.
     *
     * @param begin, the begin location to run
     * @param processLen
     * @return
     */
    public boolean initDataToProcess(int begin, int processLen) {

        try {
            if (begin < 0) {
                Logs.d("Wrong input begin range!");
                return false;
            } else if (begin >= 0 && (processLen + begin) <= numberLines) {
                mBeginToProcess = begin;
                mTotalLenToProcess = processLen;
            } else if (begin >= 0 && (processLen + begin) > numberLines) {
                mBeginToProcess = begin;
                mTotalLenToProcess = numberLines - mBeginToProcess;
            }

            mSaveData = new String[mTotalLenToProcess];
            mRawChosenData = new float[mTotalLenToProcess];
            mBandpassFilterData = new float[mTotalLenToProcess];

            mAverageFilterData = new double[mTotalLenToProcess];
            mMdileFilterData = new double[mTotalLenToProcess];

            for (int i = mBeginToProcess; i < mBeginToProcess + mTotalLenToProcess; i++) {
                mRawChosenData[i - mBeginToProcess] = mRawFloatData[i];
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public void averageFilter(int N) {
        //checkLength();
        for (int index = 0; index < mTotalLenToProcess; index++) {
            double sum = 0.0;
            if (index < mTotalLenToProcess - N) {
                for (int i = 0; i < N; i++) {
                    sum += mRawDoubleData[i + index + mBeginToProcess];
                }
            } else {
                for (int i = index; i < mTotalLenToProcess; i++) {
                    sum += mRawDoubleData[i + mBeginToProcess];
                }
            }

            mAverageFilterData[index] = sum / N;
        }

    }

    public void midleFilter(int N) {
        int count;

        double[] value_buf = new double[N];
        for (int index = 0; index < mTotalLenToProcess; index++) {

            double sum = 0.0;
            if (index < mTotalLenToProcess - N) {
                for (count = 0; count < N; count++) {
                    value_buf[count] = mRawDoubleData[index + count + mBeginToProcess];
                }
                double max = 0.0;
                double min = 0.0;
                for (count = 0; count < N; count++) {
                    max = max > value_buf[count] ? value_buf[count] : max;
                    min = min < value_buf[count] ? value_buf[count] : min;
                }
                for (count = 0; count < N; count++) {
                    sum += value_buf[count];
                }

                mMdileFilterData[index] = (sum - max - min) / (N - 2);
            } else {
                // The last N sample use average
                for (int i = 0; i < mTotalLenToProcess - index; i++) {
                    sum += mRawDoubleData[index + i + mBeginToProcess];
                }
                mMdileFilterData[index] = sum / (mTotalLenToProcess - index);
            }
        }

    }

    /**
     *
     * @param klower
     * @param kupper
     * @param kwidth
     * @param aerror
     */
    public void initBpFileter(double klower, double kupper, double kwidth, double aerror) {
        kLower = klower;
        kUpper = kupper;
        kWidth = kwidth;
        kAerror = aerror;

    }

    /**
     * Applies this filter. Input and output arrays may be the same array.
     *
     */
    public void runBpFilter() {
        mBandPassFilter = new BandPassFilter(kLower, kUpper, kWidth, kAerror);
        mBandPassFilter.apply(mRawFloatData, mBandpassFilterData);
    }

    private double pointVal(double x0, double y0, double x1, double y1, double x, double dt) {
        final double eps = 10E-5;
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

    public void runInterpolation() {
        long firstTime =0;

        int dt = 5;
        long firstTimeInt = firstTime + firstTime % dt;
        //每次读取一分钟的数据，mark最后的时间点
        int index = 0;
        long resample, currenTime, nexTime;

        int indexMs = 0;
        resample = firstTimeInt;
        int N = fileOperator.getOrgCount();
        Logs.d("Total lines: " + N);
        while (index < N - 1) {
            //根据时间秒所在的位置，读取其值，用于判断比如27s和28s的区别，


//
//            double y0 = FileOperator.mRawDoubleData[index];
//            double y1 = FileOperator.mRawDoubleData[index + 1];

//            while (resample >= currenTime && resample <= nexTime) {
//                double val = 0.0;        
//                resample = firstTime + dt * indexMs;
//
//                indexMs = indexMs + 1;
//                val = pointVal((double) currenTime, y0, (double) nexTime, y1, (double) resample, dt);
//                mMsArrayList.add(resample);
//                mInterpData.add(val);
//                Logs.d("time===" + mMsArrayList.get(indexMs - 1) + " Val==   " + mInterpData.get(indexMs - 1));
//            }
            index = index + 1;

        }

        mCountInterp = mInterpData.size();
        Logs.d("Total Data: " + mCountInterp);
    }

    /**
     * Initiate the parameter to run FFT function!
     *
     * @param number
     * @param winLength
     *
     */
    public void initFFT(int number, int winLength, String winName, double sample) {

        mNnumToFFT = number;
        mWinLenFFT = winLength;
        mWinName = winName;

        mFFT = new FFT(mNnumToFFT, mWinLenFFT);
        mTaperWindows = new TaperWindows(mWinLenFFT, mWinName);
        mWindow = mTaperWindows.getWindow();
//        mDt = 0.002;
//        mDt = 0.016;
//        mDt = (int) (1.0 / sample * 1000);
        mDt = 5;
        mDf = 1.0 / (mDt / 1000.0) / mNnumToFFT;

        re = new double[mNnumToFFT];
        im = new double[mNnumToFFT];
        windData = new double[mNnumToFFT];
        specData = new double[mNnumToFFT / 2];
        totalEnergy = 0.0;
        mDeltaEnergy = new double[mCountInterp];
        mThetaEnergy = new double[mCountInterp];
        mAlphaEnergy = new double[mCountInterp];
        mBetaEnergy = new double[mCountInterp];
    }

    // Have to initiate frequency line before run FFT;
    public boolean initFreqLine(double deltaLine, double thetaLine, double alphaLine, double beta1Line, double beta2Line) {
        if (deltaLine == 0.0) {
            mDeltaFreq = 2.0;
        } else {
            mDeltaFreq = deltaLine;
        }

        if (thetaLine == 0.0) {
            mThetaFreq = 6.0;
        } else {
            mThetaFreq = thetaLine;
        }

        if (alphaLine == 0.0) {
            mAlphaFreq = 13.0;
        } else {
            mAlphaFreq = alphaLine;
        }

        if (beta1Line == 0.0) {
            mBeta1Freq = 20.0;
        } else {
            mBeta1Freq = beta1Line;
        }
        if (beta2Line == 0.0) {
            mBeta2Freq = 30.0;
        } else {
            mBeta2Freq = beta2Line;
        }

        boolean isReasonableSequnce = mDeltaFreq < mThetaFreq && mThetaFreq < mAlphaFreq && mAlphaFreq < mBeta1Freq && mBeta1Freq < mBeta2Freq;
        boolean isReasonableValue = mDeltaFreq != 0.0 && mThetaFreq != 0.0 && mAlphaFreq != 0.0 && mBeta1Freq != 0.0 && mBeta2Freq != 0.0;

        Logs.d("deta: " + mDeltaFreq);
        Logs.d("theta: " + mThetaFreq);
        Logs.d("alpha: " + mAlphaFreq);
        Logs.d("beta1: " + mBeta1Freq);
        Logs.d("beta2: " + mBeta2Freq);
        Logs.d("" + isReasonableSequnce);
        Logs.d("" + isReasonableValue);
        if (isReasonableSequnce && isReasonableValue) {
            mIsInitFreqLine = true;
            return true;
        } else {
            mIsInitFreqLine = false;
            return false;
        }

    }

    public void runFFT(String filterType, boolean classfied) {

        if (!mIsInitFreqLine) {
            Logs.d("Frequency line has not been initiate!");
            return;
        }

        long startTime = System.currentTimeMillis();
        for (int index = 0; index < mCountInterp - mNnumToFFT; index++) {
            // ??濮????版??
            switch (filterType) {
                case DataType.RUN_TYPE.BANDPASS_FILTER:
                    for (int i = 0; i < mNnumToFFT; i++) {
                        re[i] = mBandpassFilterData[i + index];
                        im[i] = 0;
                        windData[i] = 0.0;
                    }
                    break;
                case DataType.RUN_TYPE.AVERAGE_FILTER:
                    for (int i = 0; i < mNnumToFFT; i++) {
                        re[i] = mAverageFilterData[i + index];
                        im[i] = 0;
                        windData[i] = 0.0;
                    }
                    break;
                case DataType.RUN_TYPE.MEDIAN_FILTER:
                    for (int i = 0; i < mNnumToFFT; i++) {
                        re[i] = mMdileFilterData[i + index];
                        im[i] = 0;
                        windData[i] = 0.0;
                    }
                    break;
                case DataType.RUN_TYPE.NO_FILTER:
                    for (int i = 0; i < mNnumToFFT; i++) {
                        re[i] = mInterpData.get(i + index + mBeginToProcess);
                        im[i] = 0;
                        windData[i] = 0.0;
                    }
                    break;
                default:
                    break;
            }

            // Calculate windows 
            double meanData = 0.0;
            for (int k = 0; k < mWindow.length; k++) {
                windData[k] = re[k] * mWindow[k];
                meanData = meanData + windData[k];
            }
//            meanData = meanData / mWindow.length;
//
//            for (int k = 0; k < mWindow.length; k++) {
//                windData[k] = windData[k] - meanData;
//            }

            // Fourier Transform
            mFFT.fft(windData, im);
            // Calculate Specdcom
            totalEnergy = 0.0;
            for (int l = 0; l < specData.length; l++) {
                specData[l] = windData[l] * windData[l] + im[l] * im[l];
                totalEnergy = totalEnergy + specData[l];
            }
            //

            int indexDelta = (int) (mDeltaFreq / mDf);
            double temp = 0.0;
            for (int iDelta = 0; iDelta < indexDelta; iDelta++) {
                temp = temp + specData[iDelta];

            }
            double deltaEnergy = mDeltaEnergy[index] = temp / totalEnergy;
//            double deltaEnergy = determinLevel(mDeltaEnergy[index] = temp / totalEnergy);

            int indexTheta = (int) (mThetaFreq / mDf);
            temp = 0.0;
            for (int iTheta = indexDelta; iTheta < indexTheta; iTheta++) {
                temp = temp + specData[iTheta];

            }
            double thetaEnergy = mThetaEnergy[index] = temp / totalEnergy;
//            double thetaEnergy = determinLevel(mThetaEnergy[index] = temp / totalEnergy);

            int indexAlpha = (int) (mAlphaFreq / mDf);
            temp = 0.0;
            for (int iAlpha = indexTheta; iAlpha < indexAlpha; iAlpha++) {
                temp = temp + specData[iAlpha];

            }
            double alphaEnergy = mAlphaEnergy[index] = temp / totalEnergy;
//            double alphaEnergy = determinLevel(mAlphaEnergy[index] = temp / totalEnergy);

            temp = 0.0;
            for (int iBeta = indexAlpha; iBeta < specData.length; iBeta++) {
                temp = temp + specData[iBeta];

            }
            double betaEnergy = mBetaEnergy[index] = temp / totalEnergy;
        }

        double[] mAveageDeltaEnergy = new double[mCountInterp];
        double[] mAveageThetaEnergy = new double[mCountInterp];
        double[] mAveageAlphaEnergy = new double[mCountInterp];
        double[] mAveageBetaEnergy = new double[mCountInterp];

        mAveageDeltaEnergy = getAverage(mDeltaEnergy);
        mAveageThetaEnergy = getAverage(mThetaEnergy);
        mAveageAlphaEnergy = getAverage(mAlphaEnergy);
        mAveageBetaEnergy = getAverage(mBetaEnergy);

        for (int index = 0; index < mCountInterp; index++) {
            if (classfied) {
                SingleCurveInfo curves = new SingleCurveInfo();


                mAListProcessedSignal.add(curves);

            } else {
                SingleCurveInfo curves = new SingleCurveInfo();

                mAListProcessedSignal.add(curves);

                DecimalFormat mFormat = new DecimalFormat("####.####");
                mSaveData[index] = mRecordTime[index] + "\t"
                        + mFormat.format(mRawChosenData[index]) + "\t"
                        + mFormat.format(mBandpassFilterData[index]) + "\t"
                        + mFormat.format(mAverageFilterData[index]) + "\t"
                        + mFormat.format(mMdileFilterData[index]) + "\t"
                        + mFormat.format(mDeltaEnergy[index]) + "\t"
                        + mFormat.format(mThetaEnergy[index]) + "\t"
                        + mFormat.format(mAlphaEnergy[index]) + "\t"
                        + mFormat.format(mBetaEnergy[index]) + "\n";
            }
        }

        // save to file
        Logs.d("output file is: " + FileOperator.getOutPutFileName());
//        if (!outFileName.contains(null)) {
//            Logs.d(MainPlotFrame.getParameterList());
//            FileOperator.saveToFile(MainPlotFrame.getParameterList() + "\n", mSaveData);
//        }

        long endTime = System.currentTimeMillis();

        costTime = (float) (endTime - startTime) / 1000;

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
        for (int idx = 0; idx < count; idx++) {
            double sum = 0.0;
            for (int j = 0; j < AVERAGRE_LEN; j++) {
                sum += sig[idx * AVERAGRE_LEN + j];
            }
            for (int j = 0; j < AVERAGRE_LEN; j++) {
                output[idx * AVERAGRE_LEN + j] = sum / AVERAGRE_LEN;
            }
        }
        double sum = 0.0;
        for (int idx = count * AVERAGRE_LEN; idx < sig.length; idx++) {
            sum += sig[idx];
        }
        for (int idx = count * AVERAGRE_LEN; idx < sig.length; idx++) {
            output[idx] = sum / (sig.length - count * AVERAGRE_LEN);
        }
        return output;
    }

    public static double[] getRawData() {
        return mRawDoubleData;
    }

    public static void setRawData(double[] rawData) {
        DataGenerator.mRawDoubleData = rawData;
    }

    public static int getNumToProcess() {
        return mNnumToFFT;
    }

    public static void setNumToProcess(int numToProcess) {
        DataGenerator.mNnumToFFT = numToProcess;
    }

    public static int getWinLen() {
        return mWinLenFFT;
    }

    public static void setWinLen(int winLen) {
        DataGenerator.mWinLenFFT = winLen;
    }

    public static int getTotalLen() {
        return mTotalLenToProcess;
    }

    public static void setTotalLen(int totalLen) {
        DataGenerator.mTotalLenToProcess = totalLen;
    }

    public static int getBegin() {
        return mBeginToProcess;
    }

    public static void setBegin(int begin) {
        DataGenerator.mBeginToProcess = begin;
    }

    public static double[] getWindow() {
        return mWindow;
    }

    public static void setWindow(double[] window) {
        DataGenerator.mWindow = window;
    }

    public static double getDt() {
        return mDt;
    }

    public static void setDt(int dt) {
        DataGenerator.mDt = dt;
    }

    public static int getNumberLines() {
        return numberLines;
    }

    public static double[] getDeltaEnergy() {
        return mDeltaEnergy;
    }

    public static double[] getThetaEnergy() {
        return mThetaEnergy;
    }

    public static double[] getAlphaEnergy() {
        return mAlphaEnergy;
    }

    public ArrayList<SingleCurveInfo> getAListProcessedSignal() {
        return mAListProcessedSignal;
    }

    public static float getCostTime() {
        return costTime;
    }

}
