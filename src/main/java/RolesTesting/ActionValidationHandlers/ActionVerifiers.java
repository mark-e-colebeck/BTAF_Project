package RolesTesting.ActionValidationHandlers;

import RolesTesting.ExcelApplicationHandlers.ExcelHandlers.SheetHandler;
import RolesTesting.ExcelApplicationHandlers.ExcelHandlers.WorkbookHandler;
import RolesTesting.ReportHandlers.ReportWriter;
import RolesTesting.Util.ConfigProperties;
import com.google.common.base.Splitter;
import mmarquee.automation.AutomationException;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.AutomationTreeViewItem;
import mmarquee.automation.pattern.PatternNotFoundException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import tech.tablesaw.api.Row;

import java.io.IOException;
import java.util.ArrayList;

public class ActionVerifiers {
    public static void verifyAreaSelected(Row row, String workBookName) throws IOException, InvalidFormatException {
        String expectedArea = null;
        String actualArea = null;
        try {
            Workbook workbookWithSheet = (WorkbookHandler.getWorkbookObject(workBookName));
            String sheetName = row.getString(ConfigProperties.getProperty("testCaseFlow.verifyAreaSelected.sheetName"));
            Sheet sheetToOpen = SheetHandler.getSheetFromWorkBook(workbookWithSheet, sheetName);
            String areaCellAddress = row.getString(ConfigProperties.getProperty("testCaseFlow.verifyAreaSelected.areaCellAddress"));
            expectedArea= row.getString(ConfigProperties.getProperty("testCaseFlow.verifyAreaSelected.expectedArea"));
            actualArea = SheetHandler.getDataInCellFromSheet(sheetToOpen, areaCellAddress).getStringCellValue();
            ReportWriter.assertStepAndPrint("Verify the area selected", expectedArea, actualArea, row);
        }
        catch (Exception e){
            ReportWriter.assertStepAndPrint("Verify the area selected", expectedArea, actualArea, row);
        }
    }

    public static void verifyConnection(Row row, String workBookName) throws IOException, InvalidFormatException {
        String expectedConnectionEnvironment = null;
        String actualConnectionEnvironment = null;
        try {
            Workbook workbookWithSheet = (WorkbookHandler.getWorkbookObject(workBookName));
            String sheetName = row.getString(ConfigProperties.getProperty("testCaseFlow.verifyConnection.sheetName"));
            Sheet sheetToOpen = SheetHandler.getSheetFromWorkBook(workbookWithSheet, sheetName);
            String connectionCellAddress = row.getString(ConfigProperties.getProperty("testCaseFlow.verifyConnection.connectionCellAddress"));
            expectedConnectionEnvironment = row.getString(ConfigProperties.getProperty("testCaseFlow.verifyConnection.expectedConnectionEnvironment"));
            actualConnectionEnvironment = SheetHandler.getDataInCellFromSheet(sheetToOpen, connectionCellAddress).getStringCellValue();
            ReportWriter.assertStepAndPrint("Verify the Connection Environment", expectedConnectionEnvironment, actualConnectionEnvironment, row);
        }
        catch (Exception e){
            ReportWriter.assertStepAndPrint("Verify the Connection Environment", expectedConnectionEnvironment, actualConnectionEnvironment, row);
        }
    }

    public static void verifyPeriods(Row row, String workBookName) throws IOException, InvalidFormatException {
        Workbook workbookWithSheet = (WorkbookHandler.getWorkbookObject(workBookName));
        String sheetName = row.getString(ConfigProperties.getProperty("testCaseFlow.verifyConnection.sheetName"));
        Sheet sheetToOpen = SheetHandler.getSheetFromWorkBook(workbookWithSheet, sheetName);
        String areaCellAddress = row.getString(ConfigProperties.getProperty("testCaseFlow.verifyConnection.areaCellAddress"));
        String expectedPeriods = row.getString(ConfigProperties.getProperty("testCaseFlow.parameter3"));
        CellReference ref = new CellReference(areaCellAddress);
        int rownr = ref.getRow();
        int colnr = ref.getCol();
        Splitter splitter = Splitter.on(';').omitEmptyStrings().trimResults();
        Iterable<String> tokens2 = splitter.split(expectedPeriods);
        XSSFRow rowPoi = (XSSFRow) sheetToOpen.getRow(rownr);
        rowPoi.getCell(colnr);
        XSSFCell cell;
        for (String token : tokens2) {
            rowPoi = (XSSFRow) sheetToOpen.getRow(rownr);
            cell = rowPoi.getCell(colnr);
            colnr++;
            ReportWriter.assertStepAndPrint("Verify the Connection Environment", cell.getStringCellValue(), token.toUpperCase(), row);
        }
    }

    public static void verifyNoUsRegion(UIAutomation automation) throws PatternNotFoundException, AutomationException {
        ArrayList<AutomationTreeViewItem> checkUS = new ArrayList<>();
        try{
            checkUS.add(automation.getDesktopWindow("Select Member").getTreeViewByAutomationId("mTreeView").getItem("US"));
        }
        catch (Exception ignored){
        }
        if(checkUS.isEmpty()){
            System.out.println("Pass: US not in Input Prompts Screen");
        }
        else{
            System.out.println("Fail: US is in Input Prompts Screen");
        }

    }
}
