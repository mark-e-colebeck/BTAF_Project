package FunctionalTesting.DataValidator;

import FunctionalTesting.DataModel.DependencyWbData;
import FunctionalTesting.DataModel.TestCaseDetails;
import FunctionalTesting.ExtractData.ExtractTable;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import tech.tablesaw.api.Table;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ValidateMappingDocument {
    ExtractTable extractTable =  new ExtractTable();

    DependencyWbData dependencyWbData =  null;
    List<String> dependencyWsList = null;
    List<DependencyWbData> dependencyList = null;

    //Reads in the data from the InputFormReport Mapping Master.xlsx workbook in the Report Mappings Folder of the BTAF Framework
    //Validates the correct input form is loaded and gets the data using the correct mapping sheet
    public Map<String, List<DependencyWbData>> getMappingIndex(TestCaseDetails testCaseDetails) throws IOException, InvalidFormatException {
        String userName = System.getProperty("user.name");
        String directoryPath = System.getProperty("user.dir")+"/temp";
        // a File instance for the directory:
        File workingDirFile = new File(directoryPath);
        String filePath = "C:\\Users\\"+ userName +"\\Documents\\BTAF Framework";
        Table mappingMaster = extractTable.convertExcelToTable(filePath+"\\Report Mappings\\InputFormReport Mapping Master.xlsx", "Master");
        Table filteredTable = mappingMaster.where(mappingMaster.stringColumn("Report Workbook to validate").isEqualTo(testCaseDetails.getReportWbName().trim()).and(mappingMaster.stringColumn("Report Worksheet to Validate").isEqualTo(testCaseDetails.getReportWsName().trim())));
        int rowIndex = 0;
        Map<File, List<String>> dependencyDataMap;
        Map<String, List<DependencyWbData>> mappingDataMap = null;

        while(rowIndex < filteredTable.rowCount()) {
            mappingDataMap = new HashMap<>();
            dependencyDataMap = new HashMap<>();
            dependencyWsList = new ArrayList<>();
            dependencyList = new ArrayList<>();
            int dependencyWbCount = 0;
            final int[] matchingCount = {0};
            for (int i = 3; i < filteredTable.columnCount(); i += 2) {
                boolean isDependencyFileExists = false;
                dependencyWbCount ++;
                String dependencyWbName = filteredTable.column(i).getString(rowIndex);
                String dependencyWsName = filteredTable.column(i+1).getString(rowIndex);
                File dependencyFile = null;
                File[] dir_contents = workingDirFile.listFiles();
                for (File eachFile : dir_contents) {
                    String fileName = eachFile.getName().contains("(") ? eachFile.getName().split("\\(")[0]
                            : eachFile.getName().split("\\.")[0];
                    if (fileName.trim().equals(dependencyWbName)) {
                        isDependencyFileExists = true;
                        dependencyFile = eachFile;
                        break;
                    }
                }
                if(isDependencyFileExists) {
                    List<String> worksheets = extractTable.getWorksheetsFromWorkbook(dependencyFile);
                    if(worksheets.contains(dependencyWsName)) {
                        if((dependencyDataMap != null || dependencyDataMap.size() > 0) && dependencyDataMap.containsKey(dependencyFile)) {
                            dependencyDataMap.get(dependencyFile).add(dependencyWsName);
                        } else {
                            dependencyWsList = new ArrayList<>();
                            dependencyWsList.add(dependencyWsName);
                        }
                        matchingCount[0]++;
                        dependencyDataMap.put(dependencyFile,dependencyWsList);
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
            if(matchingCount[0] == dependencyWbCount) {
                dependencyDataMap.entrySet().forEach(e-> {
                    dependencyWbData = new DependencyWbData();
                    dependencyWbData.setDependencyWb(e.getKey());
                    dependencyWbData.setDependencyWbName(e.getKey().getName().split("\\.")[0]);
                    dependencyWbData.setDependencyWsNamesList(e.getValue());
                    dependencyList.add(dependencyWbData);
                });
                mappingDataMap.put(filteredTable.column(0).getString(rowIndex),dependencyList);
                break;
            }
            rowIndex ++;
        }
        return mappingDataMap;
    }

}
