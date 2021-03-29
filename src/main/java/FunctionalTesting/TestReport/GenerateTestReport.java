package FunctionalTesting.TestReport;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GenerateTestReport {
    private static ExtentSparkReporter htmlReporter;
    private static ExtentReports extent;

    //Creates an HTML report with the results of the test
    public static ExtentReports initializeValues(String testName) {
        String filePath = System.getProperty("user.dir");
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        htmlReporter = new ExtentSparkReporter(filePath + "/test-output/"+testName+timeStamp+".html");
        extent = new ExtentReports();  //create object of ExtentReports
        extent.attachReporter(htmlReporter);
        htmlReporter.config().setDocumentTitle("Automation Report"); // Tittle of Report
        htmlReporter.config().setReportName("Role Based Driver"); // Name of the report
        htmlReporter.config().setTheme(Theme.STANDARD);
        return extent;
    }
}
