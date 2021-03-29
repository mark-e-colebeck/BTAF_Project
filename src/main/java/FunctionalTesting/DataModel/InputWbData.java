package FunctionalTesting.DataModel;

import java.io.File;
import java.util.List;

//Used to read the report workbook data and the dependency workbook and worksheet data to
//perform the validations by choosing the files as per the mapping master

public class InputWbData {
    private File reportWb;
    private File keyWordSheet;
    private List<DependencyWbData> dependencyWbData;
    private TestCaseDetails testCaseDetails;

    public File getKeyWordSheet() {
        return keyWordSheet;
    }

    public void setKeyWordSheet(File keyWordSheet) {
        this.keyWordSheet = keyWordSheet;
    }

    public List<DependencyWbData> getDependencyWbData() {
        return dependencyWbData;
    }

    public void setDependencyWbData(List<DependencyWbData> dependencyWbData) {
        this.dependencyWbData = dependencyWbData;
    }

    public File getReportWb() {
        return reportWb;
    }

    public void setReportWb(File reportWb) {
        this.reportWb = reportWb;
    }

    public TestCaseDetails getTestCaseDetails() {
        return testCaseDetails;
    }

    public void setTestCaseDetails(TestCaseDetails testCaseDetails) {
        this.testCaseDetails = testCaseDetails;
    }
}
