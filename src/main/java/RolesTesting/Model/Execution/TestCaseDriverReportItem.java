package Model.Execution;

public class TestCaseDriverReportItem {
    Integer rowNumber;
    String resultFlag;
    String testCaseName;

    public TestCaseDriverReportItem(Integer rowNumber, String resultFlag, String testCaseName) {
        this.rowNumber = rowNumber;
        this.resultFlag = resultFlag;
        this.testCaseName = testCaseName;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public String getResultFlag() {
        return resultFlag;
    }

    public String getTestCaseName() {
        return testCaseName;
    }
}
