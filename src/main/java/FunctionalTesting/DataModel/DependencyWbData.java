package FunctionalTesting.DataModel;

import java.io.File;
import java.util.List;
//This class is used to maintain the data from the Input Mapping Master sheet to
//load and validate if the required workbooks have been uploaded to perform the validations

public class DependencyWbData {
    private File dependencyWb;
    private String uploadStatus;
    private String dependencyWbName;
    private List<String> dependencyWsNamesList;


    public DependencyWbData() {}
    public DependencyWbData(String workbookName, String uploadStatus) {
        this.dependencyWbName = workbookName;
        this.uploadStatus = uploadStatus;
    }



    public File getDependencyWb() {
        return dependencyWb;
    }

    public void setDependencyWb(File dependencyWb) {
        this.dependencyWb = dependencyWb;
    }

    public String getDependencyWbName() {
        return dependencyWbName;
    }

    public void setDependencyWbName(String dependencyWbName) {
        this.dependencyWbName = dependencyWbName;
    }

    public List<String> getDependencyWsNamesList() {
        return dependencyWsNamesList;
    }

    public void setDependencyWsNamesList(List<String> dependencyWsNamesList) {
        this.dependencyWsNamesList = dependencyWsNamesList;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }
}
