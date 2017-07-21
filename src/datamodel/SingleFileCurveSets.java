/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel;

import java.util.ArrayList;
import math.SingleCurveProcessUtils;
import tools.utilities.Logs;

/**
 * A class hold information for a single file, maybe contains many
 * curves(SingleCurveInfo) by @curveInfoList
 *
 * @author JianGe
 */
public class SingleFileCurveSets extends SingleCurveProcessUtils {

    private String fileName;

    private ArrayList<SingleCurveInfo> curveInfoList = new ArrayList<>();

//    public SingleFileCurveSets() {
//
//    }
    public SingleFileCurveSets(String name) {
        this.fileName = name;
        Logs.e("Created: " + this.fileName);
    }

    public void addCurve(SingleCurveInfo curveInfo) {

        if (curveInfoList != null && !curveInfoList.isEmpty()) {
            for (int i = 0; i < curveInfoList.size(); i++) {
                if (curveInfoList.get(i).getCurveName().equals(curveInfo.getCurveName())) {
                    this.curveInfoList.remove(i);
                    this.curveInfoList.add(curveInfo);
//                curveInfoList.set(i, curveInfo);
                    break;
                }
            }
        } else {
//            Logs.e("null");
        }

        this.curveInfoList.add(curveInfo);
    }

    public void removeCurve(String curveName) {
        int removeID = getSelectedCurveDataID(curveName);
        this.curveInfoList.remove(removeID);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ArrayList<SingleCurveInfo> getCurveInfoList() {
        return this.curveInfoList;
    }

    public void setCurveInfoList(ArrayList<SingleCurveInfo> curveInforList) {
        this.curveInfoList = curveInforList;
    }

    public SingleCurveInfo getSelectedCurveData(String curveName) {
        int id = getSelectedCurveDataID(curveName);
        if (id >= 0) {
            return this.curveInfoList.get(id);
        } else {
            return null;
        }
    }

    public SingleCurveInfo getTimeCurveData() {
        int id = getSelectedCurveDataID("TIME");
        if (id >= 0) {
            return this.curveInfoList.get(id);
        } else {
            return null;
        }
    }

    public int getSelectedCurveDataID(String curveName) {
        int id = -1;
        if (this.curveInfoList.size() > 0) {
            for (int i = 0; i < this.curveInfoList.size(); i++) {
                if (this.curveInfoList.get(i).getCurveName().equals(curveName)) {
                    id = i;
                    break;
                }
            }
        } else {
            return id;
        }

        return id;
    }

    public SingleCurveInfo getLastAddCurveInfo() {
        return this.curveInfoList.get(curveInfoList.size() - 1);
    }

    public ArrayList<String> toLineDataSets() {
        ArrayList<String> lineDataList = new ArrayList<>();
        StringBuilder databBuffer = new StringBuilder();

        for (int idx = 0; idx < curveInfoList.get(0).size(); idx++) {
            for (int i = 0; i < curveInfoList.size(); i++) {
                databBuffer.append(curveInfoList.get(i).getCurveValueList().getValueList().get(idx).toString()).append("     ");

            }
            databBuffer.append("\n");
            lineDataList.add(databBuffer.toString());
            databBuffer.setLength(0);
        }
        return lineDataList;
    }

    public int cuvreNumber() {
        return this.curveInfoList.size();
    }

    public void clear() {
        this.curveInfoList.clear();
    }

}
