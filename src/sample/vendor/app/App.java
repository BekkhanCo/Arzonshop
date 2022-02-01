package sample.vendor.app;

import sample.vendor.FinalVars;

import java.io.FileInputStream;
import java.io.IOException;

public class App {
    public static boolean has(String key, String value){
        return getProperty(key).equals(value);
    }
    public static String getProperty(String key){
        try {
            FileInputStream fileInputStream = new FileInputStream(FinalVars.APP_PROPERTY_FILE);
            FinalVars.APP_PROPERTIES.clear();
            FinalVars.APP_PROPERTIES.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FinalVars.APP_PROPERTIES.getProperty(key) == null ? "":FinalVars.APP_PROPERTIES.getProperty(key);
    }







}
