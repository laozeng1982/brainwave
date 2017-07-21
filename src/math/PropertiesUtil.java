/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Read and write Properties ,default in classpath/config.properties file
 *
 * @author Zhuzhijie QQ:695520848
 */
public class PropertiesUtil {

    // file path
    private String configPath = null;

    private Properties props = null;

    /**
     * used by shell, auto find config.properties under classpath
     */
    public PropertiesUtil() throws IOException {
        InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream("config.properties");
        props = new Properties();
        props.load(in);
        
        in.close();
    }

    /**
     * Read properties according to key  Jun 26, 2010 9:15:43 PM
     *
     * @author Zhuzhijie
     * @param key key value
     * @return value
     * @throws IOException
     */
    public String readValue(String key) throws IOException {
        return props.getProperty(key);
    }

    /**
     * Read all properties information Jun 26, 2010 9:21:01 PM
     *
     * @author Zhuzhijie
     * @throws FileNotFoundException, if file not found
     * @throws IOException, if load error or close resource error
     *
     */
    public Map<String, String> readAllProperties() throws FileNotFoundException, IOException {
        //Read and return all key-value
        Map<String, String> map = new HashMap<String, String>();
        Enumeration en = props.propertyNames();
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            String Property = props.getProperty(key);
            map.put(key, Property);
        }
        return map;
    }

    /**
     * set a key-value and save to file Jun 26, 2010 9:15:43 PM
     *
     * @author Zhuzhijie 
     * @param key key
     * @return key 
     * @throws IOException
     */
    public void setValue(String key, String value) throws IOException {
        Properties prop = new Properties();
        InputStream fis = new FileInputStream(this.configPath);
        // load from a InputStream
        prop.load(fis);
        // invoke Hashtable  put method, using getProperty method
        OutputStream fos = new FileOutputStream(this.configPath);
        prop.setProperty(key, value);
        // Save Properties 
        prop.store(fos, "last update");

        fis.close();
        fos.close();
    }

    public static void main(String[] args) {
        PropertiesUtil p;
        try {
            p = new PropertiesUtil();
            System.out.println(p.readAllProperties());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
