package FunctionalTesting.DataModel;

//POJO class to read the data from the data intersection model to run to run the queries as mentioned in the DIM
//Each getter/setter is used to read the values from the respective columns to construct the query to extract data from the table

public class BackendReportValidationData {
    private String select;
    private String column1Value;
    private String column1Name;
    private String column2Value;
    private String column2Name;
    private long reportData;
    private long inputFormData;
    private String status;
    private String tableName;

    public String getColumn1Name() {
        return column1Name;
    }

    public void setColumn1Name(String column1Name) {
        this.column1Name = column1Name;
    }

    public String getColumn2Name() {
        return column2Name;
    }

    public void setColumn2Name(String column2Name) {
        this.column2Name = column2Name;
    }
    public BackendReportValidationData() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public String getColumn1Value() {
        return column1Value;
    }

    public void setColumn1Value(String column1Value) {
        this.column1Value = column1Value;
    }

    public String getColumn2Value() {
        return column2Value;
    }

    public void setColumn2Value(String column2Value) {
        this.column2Value = column2Value;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
