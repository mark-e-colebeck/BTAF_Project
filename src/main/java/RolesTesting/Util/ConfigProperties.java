package RolesTesting.Util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigProperties {

    static String result = "";
    static InputStream inputStream;

    public static String getProperty(String key) throws IOException {

        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";
            inputStream = FunctionalTesting.Util.ConfigProperties.class.getResourceAsStream("/config.properties");

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            result = prop.getProperty(key);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
        return result;
    }

}
