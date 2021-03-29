package RolesTesting.ExcelApplicationHandlers.ExcelHandlers;
import RolesTesting.Util.ConfigProperties;
import com.aventstack.extentreports.ExtentReports;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Variant;
import mmarquee.automation.AutomationException;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.pattern.PatternNotFoundException;
import tech.tablesaw.api.Row;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class JacobBase {
    ActiveXComponent app;
    ActiveXComponent workbooks;
    ActiveXComponent workbook;
    ActiveXComponent worksheet;
    ActiveXComponent object;
    ActiveXComponent temp;
    public String dataSet;
    public String sheetName;
    String filePath;
    int workbooksCount;
    public int workbookIndex = 1;
    public int lastRow;
    public int lastColumn;
    String startRange;
    String endRange;


    public JacobBase() {

    }

    public JacobBase(int workbookIndex) {
        this.setUp(workbookIndex);
    }

    public void initializeExcel() {
        ComThread.InitSTA();
        this.app = ActiveXComponent.connectToActiveInstance("Excel.Application");
        if (this.app == null) {
            this.app = new ActiveXComponent("Excel.Application");
        }

        this.app.setProperty("visible", true);
    }

    public void setUp(String filePath) {
        if (!filePath.equalsIgnoreCase(this.filePath)) {
            this.initializeExcel();
            this.workbooks = this.app.invokeGetComponent("workbooks");
            this.workbook = this.workbooks.invokeGetComponent("open", new Variant[]{new Variant(filePath)});
            this.worksheet = this.workbook.invokeGetComponent("Activesheet");
            this.lastRow = this.getLastRow();
            this.lastColumn = this.getLastColumn();
            this.workbookIndex = this.getWorkbookIndex(filePath);
            this.filePath = filePath;
        }

    }

    public void setUp(int workbookIndex) {
        this.initializeExcel();
        this.workbook = this.getWorkbook(workbookIndex);
        this.worksheet = this.workbook.invokeGetComponent("Activesheet");
        this.lastRow = this.getLastRow();
        this.lastColumn = this.getLastColumn();
    }

    public ActiveXComponent getWorkbook(int index) {
        return this.app.invokeGetComponent("workbooks", new Variant[]{new Variant(index)});
    }

    public int getWorkbooksCount() {
        return this.workbooks.getProperty("count").getInt();
    }

    public String getWorkbookName(int index) {
        return this.getWorkbook(index).getProperty("name").toString();
    }

    public String getWorkbookPath(int index) {
        return this.getWorkbook(index).getProperty("fullname").toString();
    }

    public int getWorkbookIndex(String filePath) {
        int i;
        int n;
        if (!filePath.contains(File.separator)) {
            i = 1;

            for (n = this.getWorkbooksCount(); i <= n; ++i) {
                if (filePath.equalsIgnoreCase(System.getProperty("user.dir") + File.separator + "data" + File.separator + this.getWorkbookName(i))) {
                    return i;
                }
            }
        } else {
            i = 1;

            for (n = this.getWorkbooksCount(); i <= n; ++i) {
                if (filePath.equalsIgnoreCase(this.getWorkbookPath(i))) {
                    return i;
                }
            }
        }

        return 0;
    }

    public ActiveXComponent getWorksheet(String sheetName) {
        return this.workbook.invokeGetComponent("sheets", new Variant[]{new Variant(sheetName)});
    }

    public void assignSheet(String worksheetName) {
        if (!worksheetName.equalsIgnoreCase(this.sheetName)) {
            this.workbook = this.getWorkbook(this.workbookIndex);
            this.worksheet = this.getWorksheet(worksheetName);
            this.lastRow = this.getLastRow();
            this.lastColumn = this.getLastColumn();
            this.sheetName = worksheetName;
        }

    }

    public void assignSheet(int workbookIndex, String worksheetName) {
        this.workbookIndex = workbookIndex;
        this.assignSheet(worksheetName);
    }

    public int getLastRow() {
        return this.worksheet.invokeGetComponent("usedrange").invokeGetComponent("rows").getProperty("count").getInt();
    }

    public int getLastColumn() {
        return this.worksheet.invokeGetComponent("usedrange").invokeGetComponent("columns").getProperty("count").getInt();
    }

    public void getRange(String startRange, String endRange) {
        this.dataSet = this.worksheet.invokeGetComponent("range", new Variant[]{new Variant(startRange), new Variant(endRange)}).getProperty("Value2").toString();
    }

    public void writeCell(int rowIndex, int columnIndex, String value) {
        this.object = this.worksheet.invokeGetComponent("cells", new Variant[]{new Variant(rowIndex), new Variant(columnIndex)});
        this.object.setProperty("Value2", value);
    }

    public void writeCell(String cellAddress, String value) {
        this.object = this.worksheet.invokeGetComponent("range", new Variant[]{new Variant(cellAddress)});
        this.object.setProperty("Value2", value);
    }

    public void writeCellByHeader(int rowIndex, String headerName, String value) {
        for (int columnIndex = 1; columnIndex <= this.lastColumn; ++columnIndex) {
            if (this.readCell(1, columnIndex).equalsIgnoreCase(headerName)) {
                this.writeCell(rowIndex, columnIndex, value);
                break;
            }
        }

    }

    public void writeCellByRowHeader(int columnIndex, String headerName, String value) {
        for (int rowIndex = 1; rowIndex <= this.lastRow; ++rowIndex) {
            if (this.readCell(rowIndex, 1).equalsIgnoreCase(headerName)) {
                this.writeCell(rowIndex, columnIndex, value);
                break;
            }
        }

    }

    public String readCell(int rowIndex, int columnIndex) {
        this.object = this.worksheet.invokeGetComponent("cells", new Variant[]{new Variant(rowIndex), new Variant(columnIndex)});
        String value = this.object.getProperty("Value2").toString();
        return value.equalsIgnoreCase("null") ? "" : value;
    }

    public String readCell(String cellAddress) {
        this.object = this.worksheet.invokeGetComponent("range", new Variant[]{new Variant(cellAddress)});
        String value = this.object.getProperty("Value2").toString();
        return value.equalsIgnoreCase("null") ? "" : value;
    }

    public String readCellByHeader(int rowIndex, String headerName) {
        for (int columnIndex = 1; columnIndex <= this.lastColumn; ++columnIndex) {
            if (this.readCell(1, columnIndex).equalsIgnoreCase(headerName)) {
                return this.readCell(rowIndex, columnIndex);
            }
        }

        return "";
    }

    public String readCellByRowHeader(int columnIndex, String headerName) {
        for (int rowIndex = 1; rowIndex <= this.lastRow; ++rowIndex) {
            if (this.readCell(rowIndex, 1).equalsIgnoreCase(headerName)) {
                return this.readCell(rowIndex, columnIndex);
            }
        }

        return "";
    }

    public int getColumnIndex(String headerName) {
        for (int columnIndex = 1; columnIndex <= this.lastColumn; ++columnIndex) {
            if (this.readCell(1, columnIndex).equalsIgnoreCase(headerName)) {
                return columnIndex;
            }
        }

        return 0;
    }

    public void jacobSave(UIAutomation automation) throws AWTException, PatternNotFoundException, AutomationException {
        this.app.invoke("Quit", new Variant[]{});
        automation.getDesktopWindow("Microsoft Excel").getButton("Save").click();
    }

    public void jacobQuit() {
        this.app.invoke("Quit", new Variant[]{});
        this.app.safeRelease();
        ComThread.Release();
    }

    public static void jacobWriteToCell(Row row, ExtentReports extent) throws IOException {
        String workSheetName = row.getString(ConfigProperties.getProperty("testCaseFlow.parameter1"));
        String cellAddress = row.getString(ConfigProperties.getProperty("testCaseFlow.parameter2"));
        String input = row.getString(ConfigProperties.getProperty("testCaseFlow.parameter3"));

        JacobBase write = new JacobBase();
        write.initializeExcel();
        write.setUp(1);
        write.getWorkbook(1);
        write.getWorksheet(workSheetName);
        write.assignSheet(1, workSheetName);
        write.writeCell(cellAddress, input);

    }

    public static void jacobWriteToCellAndSave(UIAutomation automation, Row row, ExtentReports extent) throws IOException, AWTException, PatternNotFoundException, AutomationException {
        String workSheetName = row.getString(ConfigProperties.getProperty("testCaseFlow.parameter1"));
        String cellAddress = row.getString(ConfigProperties.getProperty("testCaseFlow.parameter2"));
        String input = row.getString(ConfigProperties.getProperty("testCaseFlow.parameter3"));

        JacobBase write = new JacobBase();
        write.initializeExcel();
        write.setUp(1);
        write.getWorkbook(1);
        write.getWorksheet(workSheetName);
        write.assignSheet(1, workSheetName);
        write.writeCell(cellAddress, input);
        write.jacobSave(automation);
    }
}
