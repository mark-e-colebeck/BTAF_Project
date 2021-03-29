package FunctionalTesting.DataModel;

import java.util.List;
import java.util.Map;

//This class maintains the test case details as per the input provided by the user
//using getters and setters and also maintains the information required for the
//generation of the report

public class TestCaseDetails {
    private String testCaseName;
    private String testCaseDescription;
    private String testAction;
    private String testCaseID;
    private String reportWbName;
    private String reportWsName;
    private String executionStartTime;
    private String executionEndTime;
    private String elapsedTime;
    private String message;
    private String masterIndex;
    private String executionEnvironment;
    private String loginType;
    private String userName;
    private String password;
    private String roleType;
    private String testType;
    private List<String> dependencyWbNames;

    public String getExecutionEnvironment() {
        return executionEnvironment;
    }

    public void setExecutionEnvironment(String executionEnvironment) {
        this.executionEnvironment = executionEnvironment;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getMasterIndex() {
        return masterIndex;
    }

    public void setMasterIndex(String masterIndex) {
        this.masterIndex = masterIndex;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReportWbName() {
        return reportWbName;
    }

    public void setReportWbName(String reportWbName) {
        this.reportWbName = reportWbName;
    }

    public String getReportWsName() {
        return reportWsName;
    }

    public void setReportWsName(String reportWsName) {
        this.reportWsName = reportWsName;
    }

    private Map<String, List<String>> inputWbAndWs;

    public String getTestCaseName() {
        return testCaseName;
    }

    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    public String getTestCaseDescription() {
        return testCaseDescription;
    }

    public void setTestCaseDescription(String testCaseDescription) {
        this.testCaseDescription = testCaseDescription;
    }

    public String getTestAction() {
        return testAction;
    }

    public void setTestAction(String testAction) {
        this.testAction = testAction;
    }

    public String getTestCaseID() {
        return testCaseID;
    }

    public void setTestCaseID(String testCaseID) {
        this.testCaseID = testCaseID;
    }

    public Map<String, List<String>> getInputWbAndWs() {
        return inputWbAndWs;
    }

    public void setInputWbAndWs(Map<String, List<String>> inputWbAndWs) { this.inputWbAndWs = inputWbAndWs; }

    public String getExecutionStartTime() {
        return executionStartTime;
    }

    public void setExecutionStartTime(String executionStartTime) {
        this.executionStartTime = executionStartTime;
    }

    public String getExecutionEndTime() {
        return executionEndTime;
    }

    public void setExecutionEndTime(String executionEndTime) {
        this.executionEndTime = executionEndTime;
    }

    public String getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public void setDependencyWbNames(List<String> dependencyWbNames) {
        this.dependencyWbNames = dependencyWbNames;
    }

    public List<String> getDependencyWbNames() {
        return dependencyWbNames;
    }
}
