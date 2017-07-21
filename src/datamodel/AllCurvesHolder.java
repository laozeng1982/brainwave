/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel;

import java.util.ArrayList;
import tools.utilities.Logs;

/**
 *
 * @author JianGe
 */
public class AllCurvesHolder {

    private final ArrayList<SingleFileCurveSets> allCurveSets = new ArrayList<>();
    
    public ArrayList<SingleFileCurveSets> getAllCurveSetses(){
        return this.allCurveSets;
    }

    public SingleFileCurveSets getSelectedFileCurveSets(String fileName) {
        int id = getSelectedFileCurveSetsID(fileName);
        if (id >= 0) {
            return this.allCurveSets.get(id);
        } else {
            return null;
        }
    }

    public SingleFileCurveSets getLastFileCurveSet() {
        return this.allCurveSets.get(this.allCurveSets.size() - 1);
    }

    public int getSelectedFileCurveSetsID(String fileName) {
        int id = -1;
        if (this.allCurveSets.size() > 0) {
            for (int i = 0; i < this.allCurveSets.size(); i++) {
//                Logs.e(this.allCurveSets.get(i).getFileName());
                if (this.allCurveSets.get(i).getFileName().equals(fileName)) {
                    id = i;
                    break;
                }
            }
        } else {
            return id;
        }

        return id;
    }

//    @Override
    public boolean addFileCurveSets(SingleFileCurveSets singleFileCurveSets) {
        int id = getSelectedFileCurveSetsID(singleFileCurveSets.getFileName());

        if (id >= 0) {
            for (int i = 0; i < singleFileCurveSets.getCurveInfoList().size(); i++) {
                Logs.e("New Curve Add to " + singleFileCurveSets.getFileName() + "!   " + singleFileCurveSets.getCurveInfoList().get(i).getCurveName());
                this.allCurveSets.get(id).clear();
                this.allCurveSets.get(id).addCurve(singleFileCurveSets.getCurveInfoList().get(i));
            }
        } else {
            Logs.e("New File Curve Add!   " + singleFileCurveSets.getFileName());
            this.allCurveSets.add(singleFileCurveSets);
        }

        return true;
    }

    public void removeFile(String fileName) {
        int removeID = getSelectedFileCurveSetsID(fileName);
        this.allCurveSets.remove(removeID);
    }
    
    public void clearAll() {
        this.allCurveSets.clear();
    }
}
