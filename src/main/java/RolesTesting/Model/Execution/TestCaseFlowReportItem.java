package Model.Execution;

public class TestCaseFlowReportItem {
    Integer rowNumber;
    String testCaseName;
    String action;
    String description;
    String resultFlag;

    public TestCaseFlowReportItem(Integer rowNumber, String testCaseName, String action, String description, String resultFlag) {
        this.rowNumber = rowNumber;
        this.testCaseName = testCaseName;
        this.action = action;
        this.description = description;
        this.resultFlag = resultFlag;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    public String getAction() {
        return action;
    }

    public String getDescription() {
        return description;
    }

    public String getResultFlag() {
        return resultFlag;
    }
}
