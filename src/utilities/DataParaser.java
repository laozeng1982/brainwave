/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import datamodel.AllCurvesHolder;
import datamodel.SingleCurveInfo;
import datamodel.SingleFileCurveSets;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import tools.utilities.Logs;
import static utilities.FileOperator.inPutFileName;

/**
 *
 * @author deep
 */
public class DataParaser {
    // read data files

    /**
     *
     * @param input data read from a ASCII file
     * @return
     */
    public static SingleFileCurveSets paraseArrayList(String fileName, ArrayList<String> input) {
        SingleFileCurveSets singleFileCurveSets = new SingleFileCurveSets(fileName.split("\\.")[0]);

        if (input.size() > 0) {

            //make header, just once
            String[] subStrings = tools.utilities.StringUtils.removeAllSpaceAndTabs(input.get(0)).split(" ");
            for (String subString : subStrings) {
                singleFileCurveSets.addCurve(new SingleCurveInfo(subString));
//                Logs.e(subString);
            }

            for (int line = 1; line < input.size(); line++) {

                subStrings = tools.utilities.StringUtils.removeAllSpaceAndTabs(input.get(line)).split(" ");
                for (int col = 0; col < subStrings.length; col++) {
                   
                    singleFileCurveSets.getCurveInfoList().get(col).getCurveValueList().add(subStrings[col]);

                }
            }
        } else {
            Logs.e("Input file has no data!");
            return null;
        }

        return singleFileCurveSets;
    }

//    public void readFile(AllCurvesHolder allCurveHolder) {
//        try {
////            AllCurveSets.clear();
//
//            // Clear and Reread data
//            String fileName = inPutFileName;
//
//            int selectedFileId = allCurveHolder.getSelectedFileCurveSetsID(fileName);
//            if (selectedFileId != -1) {
//                allCurveHolder.getSelectedFileCurveSets(fileName).getTimeCurveData().clear();
//            } else {
//                allCurveHolder.addFileCurveSets(new SingleFileCurveSets(fileName));
//                selectedFileId = allCurveHolder.getAllCurveSetses().size() - 1; //new ...
//            }
//
//            FileReader fileReader = new FileReader(inPutFilePath);
//            BufferedReader bf = new BufferedReader(fileReader);
//            String line;
//
//            boolean isFisrtLine = true;
//            while ((line = bf.readLine()) != null) {
//                if (line.contains("Method")) {
//                    setParameters(line);
//                    continue;
//                }
//                if (line.contains(" 9999")) {
//                    continue;
//                }
//                String removeTabs;
//                String removeBlanks;
//                if (line.contains("\t")) {
//                    removeTabs = line.replaceAll("\t+", " ");
//                } else {
//                    removeTabs = line;
//                }
//                removeBlanks = removeTabs.replaceAll(" +", " ");
//
//                String[] values = removeBlanks.split(" ");
//                //Fisrt time read, allocate 
//                if (isFisrtLine) {
//                    for (int i = 0; i < values.length; i++) {
//                        allCurveHolder.getSelectedFileCurveSets(fileName).getCurveInfoList().add(new SingleCurveInfo());
//                        allCurveHolder.getSelectedFileCurveSets(fileName).setFileName(getOnlyNameFromPath(inPutFilePath));
//                        allCurveHolder.getSelectedFileCurveSets(fileName).getCurveInfoList().get(i).setFileName(getOnlyNameFromPath(inPutFilePath));
//                        allCurveHolder.getSelectedFileCurveSets(fileName).getCurveInfoList().get(i).setCurveName(values[i]);
//
//                    }
//                    isFisrtLine = false;
//                    continue;
//                }
//
//                for (int i = 0; i < values.length; i++) {
//                    allCurveHolder.getSelectedFileCurveSets(fileName).getCurveInfoList().get(i).getCurveValueList().add(values[i]);
//                }
//            }
//        } catch (IOException e) {
//        }
//    }
}
