package Model.Execution;

import java.util.List;

public class InitialExecutionKeys {
    private  String connection;
    private  List<String> roleFolders;
    private  String workSheetToSelect;

    public String getConnection() {
        return connection;
    }

    public List<String> getRoleFolders() {
        return roleFolders;
    }

    public String getWorkSheetToSelect() {
        return workSheetToSelect;
    }



    public InitialExecutionKeys(String connection, List<String> roleFolders, String workSheetWithSteps) {
        this.connection = connection;
        this.roleFolders = roleFolders;
        this.workSheetToSelect = workSheetWithSteps;
    }
}
