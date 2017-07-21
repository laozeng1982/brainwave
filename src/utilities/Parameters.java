/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import math.DataType;

/**
 *
 * @author JianGe
 */
public class Parameters {
    //initial parameters

    private String filterType;
    private int filterLength;
    private int fft_ProcLength;
    private int fft_Samplerate;
    private int fft_WinLength;
    private String fft_WinType;
    
    private int averageLen;

    private DataType.FREQ_LINES freq_Lines;

    //default constructor!
    public Parameters() {
        filterType = DataType.RUN_TYPE.NO_FILTER;
        filterLength = 5;
        fft_ProcLength = 1024;
        fft_Samplerate = 101;
        fft_WinLength = 250;
        fft_WinType = "Gauss";
        freq_Lines = new DataType.FREQ_LINES();
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        switch (filterType) {
            case "NoFilter":
                this.filterType = DataType.RUN_TYPE.NO_FILTER;
            case "Average":
                this.filterType = DataType.RUN_TYPE.AVERAGE_FILTER;
            case "Median":
                this.filterType = DataType.RUN_TYPE.MEDIAN_FILTER;
            default:
                break;
        }

    }

    public int getFilterLength() {
        return filterLength;
    }

    public void setFilterLength(int filterLength) {
        this.filterLength = filterLength;
    }

    public int getFft_ProcLength() {
        return fft_ProcLength;
    }

    public void setFft_ProcLength(int fft_ProcLength) {
        this.fft_ProcLength = fft_ProcLength;
    }

    public int getFft_Samplerate() {
        return fft_Samplerate;
    }

    public void setFft_Samplerate(int fft_Samplerate) {
        this.fft_Samplerate = fft_Samplerate;
    }

    public int getFft_WinLength() {
        return fft_WinLength;
    }

    public void setFft_WinLength(int fft_WinLength) {
        this.fft_WinLength = fft_WinLength;
    }

    public String getFft_WinType() {
        return fft_WinType;
    }

    public void setFft_WinType(String fft_WinType) {
        this.fft_WinType = fft_WinType;
    }

    public int getAverageLen() {
        return averageLen;
    }

    public void setAverageLen(int averageLen) {
        this.averageLen = averageLen;
    }
    

    public DataType.FREQ_LINES getFreq_Lines() {
        return freq_Lines;
    }

    public void setFreq_Lines(DataType.FREQ_LINES freq_Lines) {
        this.freq_Lines = freq_Lines;
    }

}
