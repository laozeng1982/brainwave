/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JianGe
 */
public class DataType {

    public static List<String> CurveName;

    public static class RUN_TYPE {

        public static final String NO_FILTER = "NoFilter";

        public static final String BANDPASS_FILTER = "Bandpass";
        public static final String MEDIAN_FILTER = "Median";
        public static final String AVERAGE_FILTER = "Average";
        public static final String OTHER_FILTER = "Other";

        public static final String INTERPLOATION = "Interploation";

        public static final String FFT = "FFT";

    }

    public static class WINDOWS_TYPE {

        public static final String BLACKMAN_WINDOW = "Blackman";
        public static final String HAMMING_WINDOW = "Hamming";
        public static final String RECTANGLE_WINDOW = "Rectangle";
        public static final String HANNING_WINDOW = "Hanning";
        public static final String GAUSS_WINDOW = "Gauss";

    }

    public static class FREQ_LINES {

        public static ArrayList<Integer> linesList = new ArrayList<Integer>();


        //default constructor
        public FREQ_LINES() {

        }

        public FREQ_LINES(ArrayList<Integer> lines) {
            FREQ_LINES.linesList = lines;

        }

        public static ArrayList<Integer> getLinesList() {
            return linesList;
        }

        public static void setLinesList(ArrayList<Integer> linesList) {
            FREQ_LINES.linesList = linesList;
        }

    }

}
