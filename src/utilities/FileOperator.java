package utilities;

import java.io.*;
import java.util.ArrayList;
import tools.utilities.Logs;

/**
 * Created by JianGe on 2016/5/7.
 */
public class FileOperator {

    static String inPutFilePath;
    static String outPutFilePath;

    static String inPutFileName;
    static String outPutFileName;

    boolean isFloatType = true;
    private String mParameters;

    public int mOrgCount = 0;
    public int mProcCount = 0;

    public FileOperator(String inFilePath, String outFilePath) {
        if (inFilePath == null) {
            Logs.d("Error File, Please check!");
        } else {
            inPutFilePath = inFilePath;
            inPutFileName = getOnlyNameFromPath(inPutFilePath);
        }

        if (outFilePath != null) {
            outPutFilePath = outFilePath;
            outPutFileName = getOnlyNameFromPath(outPutFilePath);
        }
    }

    public String getOnlyNameFromPath(String path) {
        String[] arr = path.split(File.separator);
        return arr[arr.length - 1];
    }

  

    public String getinitParaSets(String initParaString) {
        return initParaString;
    }

    public static void saveToFile(String parameters, String[] saveData) {
        FileOutputStream out;
        BufferedWriter writer = null;

        try {
            out = new FileOutputStream(outPutFilePath);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            if (parameters != null) {
                writer.write(parameters);
            }
//            Logs.d("in FileOp: "+outPutFilePath);
            for (int i = 0; i < saveData.length; i++) {
//                Logs.d(saveData[i]);
                if (saveData[i] != null) {
                    writer.write(saveData[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveArrayListToFile(String parameters, ArrayList<String> saveData) {
        FileOutputStream out;
        BufferedWriter writer = null;

        try {
            out = new FileOutputStream(outPutFilePath);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            if (parameters != null) {
                writer.write(parameters);
            }
//            Logs.d("in FileOp: "+outPutFilePath);
            for (int i = 0; i < saveData.size(); i++) {
//                Logs.d(saveData[i]);
                if (saveData.get(i) != null) {
                    writer.write(saveData.get(i));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getInPutFileName() {
        return inPutFilePath;
    }

    public static void setInPutFileName(String inPutFileName) {
        FileOperator.inPutFilePath = inPutFileName;
    }

    public static String getOutPutFileName() {
        return outPutFilePath;
    }

    public static void setOutPutFileName(String outPutFileName) {
        FileOperator.outPutFilePath = outPutFileName;
    }

    public boolean isIsFloatType() {
        return isFloatType;
    }

    public String getParameters() {
        return mParameters;
    }

    public void setParameters(String mParameters) {
        this.mParameters = mParameters;
    }

    public int getOrgCount() {
        return this.mOrgCount;
    }

    public int getProcCount() {
        return this.mProcCount;
    }

}
