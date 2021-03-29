package FunctionalTesting.Util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigProperties {

    static String result = "";
    static InputStream inputStream;

    //Gets values from the config.properties file
    public static String getProperty(String key) throws IOException {

        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            //inputStream = new FileInputStream("src/main/resources/config.properties");
            //inputStream = new FileInputStream(String.valueOf(ConfigProperties.class.getResourceAsStream("/config.properties")));
            inputStream = ConfigProperties.class.getResourceAsStream("/config.properties");

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
