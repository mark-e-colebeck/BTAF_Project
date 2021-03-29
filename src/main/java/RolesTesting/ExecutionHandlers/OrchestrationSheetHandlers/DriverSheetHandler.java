package RolesTesting.ExecutionHandlers.OrchestrationSheetHandlers;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static RolesTesting.ExcelApplicationHandlers.ExcelHandlers.SheetHandler.getTableFromSheet;

public class DriverSheetHandler {

    public static List<Row> getRowsToExecute(String workBookName, String sheetIndex, String flagColumnName) throws IOException{
        Table table = getTableFromSheet(workBookName, sheetIndex);
        List<Row> rowsToRun = new ArrayList<>();
        int rowsCount = table.rowCount()-1;
        for(int i=0; i<=rowsCount; i++){
            Row row = table.row(i);
            if (row.getText(flagColumnName).equals("Y")) {
                rowsToRun.add(row);
            }
        }
        return rowsToRun;
    }
}
