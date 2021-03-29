package RolesTesting.Roles_Based.ReportHandlers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CapturingScreenshot {
    public static String capture(String screenShotName) throws IOException, AWTException {
        //create a string variable which will be unique always
        String df = new SimpleDateFormat("yyyyMMddhhss").format(new Date());
        Robot r = new Robot();
        String userName = System.getProperty("user.name");
        String path = "C:\\Users\\"+userName+"\\Documents\\Reports\\Pictures\\" + screenShotName + df + ".png";
        Rectangle capture = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage Image = r.createScreenCapture(capture);
        ImageIO.write(Image, "png", new File(path));
        System.out.println("Screenshot saved");
        return path;
    }
}
