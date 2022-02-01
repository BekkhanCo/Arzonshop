package sample.vendor;

import java.io.File;
import java.util.Properties;

public class FinalVars {
//    APP PROPERTIES PATH
    private String path;
    public static final String APP_PROPERTY_FILE_PATH = "./settings.prop";
    public static final File APP_PROPERTY_FILE = new File(APP_PROPERTY_FILE_PATH);
    public static Properties APP_PROPERTIES;
}
