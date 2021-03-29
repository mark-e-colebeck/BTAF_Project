package FunctionalTesting.DataModel;

//Maintains the getters and setters for writing the results to the HTML report
//for report validation

public class ValidationDataResults {
    private String status;
    private String tableName;
    private long reportData;

    public long getReportData() {
        return reportData;
    }

    public void setReportData(long reportData) {
        this.reportData = reportData;
    }

    public long getInputFormData() {
        return inputFormData;
    }

    public void setInputFormData(long inputFormData) {
        this.inputFormData = inputFormData;
    }

    private long inputFormData;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
