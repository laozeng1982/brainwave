package datamodel;

import java.awt.Color;
import java.util.Random;

public final class SingleCurveInfo {
    //Single curve information container 

    private String fileName;    //holder, the curve belongs to which file
    private String curveName;   //curve name
    private String fatherCurveName; //father curve name, if this curve was calculated

    private CurveValues curveValues = new CurveValues();

    private Color curveColor;

    public SingleCurveInfo() {
        // TODO Auto-generated constructor stub
        Random ra = new Random();
        curveColor = new Color(ra.nextInt(255), ra.nextInt(255), ra.nextInt(255));
//        Logs.e("Color is " + curveColor);

    }

    public SingleCurveInfo(String curveName) {
        // TODO Auto-generated constructor stub
        Random ra = new Random();
        curveColor = new Color(ra.nextInt(255), ra.nextInt(255), ra.nextInt(255));
        this.curveName = curveName;
//        Logs.e("Color is " + curveColor);

    }

    public int size() {
        return this.curveValues.size();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCurveName() {
        return curveName;
    }

    public void setCurveName(String curveName) {
        this.curveName = curveName;
    }

    public String getFatherCurveName() {
        return fatherCurveName;
    }

    public void setFatherCurveName(String fatherCurveName) {
        this.fatherCurveName = fatherCurveName;
    }

//    public ArrayList<String> getCurveValueList() {
    public CurveValues getCurveValueList() {
        if (this.curveValues != null ) {
            //&& !this.curveValues.isEmpty()
            return this.curveValues;
        } else {
            return null;
        }

    }

//    public void setCurveValueList(ArrayList<String> curveValue) {
    public void setCurveValueList(CurveValues curveValue) {
        this.curveValues = curveValue;
    }

    public Color getCurveColor() {
        return curveColor;
    }

    public void setCurveColor(Color curveColor) {
        this.curveColor = curveColor;
    }

    public int sampleCount() {
         return this.curveValues.size();
    }
    
    public void clear() {
        this.curveValues.clear();
    }

}
