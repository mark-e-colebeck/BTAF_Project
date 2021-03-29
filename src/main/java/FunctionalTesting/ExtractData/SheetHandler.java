package FunctionalTesting.ExtractData;

import FunctionalTesting.DependencyCustomHandlers.XlsxReaderTablesaw;
import mmarquee.automation.AutomationException;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.pattern.PatternNotFoundException;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.ss.util.cellwalk.CellWalk;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.ReadOptions;
import tech.tablesaw.io.xlsx.XlsxReadOptions;
import tech.tablesaw.io.xlsx.XlsxReader;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

public class SheetHandler extends ReadOptions {
    private static Logger logger = Logger.getLogger(SheetHandler.class);
    protected SheetHandler(Builder builder) {
        super(builder);
    }

    public static Sheet getSheetFromWorkBook(Workbook workbook, String sheetName) {

        return workbook.getSheet(sheetName);
//        logger.info("");
    }

    //Returns Excel sheet by index from a workbook
    public static Sheet getSheetFromWorkBook(Workbook workbook, int sheetIndex) {
        return workbook.getSheetAt(sheetIndex);
    }

    public static Cell[][] getDataInCellRangeFromSheet(Sheet sheet, String range) {
        CellRangeAddress cellRange = CellRangeAddress.valueOf(range);
        CellWalk cellWalk = new CellWalk(sheet, cellRange);
        Cell[][] data = new Cell[cellRange.getLastRow() + 1][cellRange.getLastColumn() + 1];
        cellWalk.traverse(((cell, cellWalkContext) -> {
            data[cellWalkContext.getRowNumber()][cellWalkContext.getColumnNumber()] = cell;
        }));
        return data;
    }

    //Returns a cell using the cell address
    public static Cell getDataInCellFromSheet(Sheet sheet, String address) {
        Cell[] cellWithData = new Cell[1];
        CellRangeAddress cellRange = CellRangeAddress.valueOf(address);
        CellWalk cellWalk = new CellWalk(sheet, cellRange);
        Cell[][] data = new Cell[cellRange.getLastRow() + 1][cellRange.getLastColumn() + 1];
        cellWalk.traverse((cell, ctx) -> cellWithData[0] = cell);
        return cellWithData[0];
    }

    public static Table getDataInCellRangeFromSheet(Table table, String range) {
        CellRangeAddress cellRange = CellRangeAddress.valueOf(range);
        return table.inRange(cellRange.getFirstRow(), cellRange.getLastRow());
    }

//    public static Table getTableInCellRangeFromSheet(String workbookPath, String sheetName, String range) throws Exception {
//        XlsxReadOptions options = XlsxReadOptions.builder(workbookPath)
//                .sheetIndex(getNameAndIndexMap(workbookPath).get(sheetName))
//                .header(true)
//                .build();
//        XlsxReaderTablesaw xlsxReader = new XlsxReaderTablesaw(workbookPath, sheetName);
//        CellRangeAddress cellRange = CellRangeAddress.valueOf(range);
//        Table table = xlsxReader.read(options, cellRange.getFirstRow(), cellRange.getLastRow(), cellRange.getFirstColumn(), cellRange.getLastColumn());
//        return table;
//    }

    //Returns a table with data in the cell range from an Excel sheet
    public static Table getTableInCellRangeFromSheet(String workbookPath, String sheetName, String range) throws Exception {
        XlsxReadOptions options = XlsxReadOptions.builder(workbookPath)
                .sheetIndex(getNameAndIndexMap(workbookPath).get(sheetName))
                .header(true)
                .build();
        XlsxReaderTablesaw xlsxReader = new XlsxReaderTablesaw(workbookPath, sheetName);
        CellRangeAddress cellRange = CellRangeAddress.valueOf(range);
        Table table = xlsxReader.read(options, cellRange.getFirstRow(), cellRange.getLastRow(), cellRange.getFirstColumn(), cellRange.getLastColumn());
        return table;
    }

    public static String getTextFromColumn(Row row, String columnName) {
        return row.getText(columnName);
    }

    public static Table getTableFromSheet(String workbookName, String sheetIndex) throws IOException {
        XlsxReader reader = new XlsxReader();
        XlsxReadOptions options = XlsxReadOptions.builder(workbookName)
                .sheetIndex(Integer.parseInt(sheetIndex)).build();
        return reader.read(options);
    }
    public static void copyTableToSheet(Table table, Workbook workbook){
        Sheet s = workbook.createSheet();

        for (int i = 0; i<table.rowCount(); i++){
            s.createRow(i);

            for (int j = 0; j<table.columnCount(); j++){
                if (table.get(i,j) == null){
                    s.getRow(i).createCell(j).setCellValue("");

                }
                else{
                    s.getRow(i).createCell(j).setCellValue(table.get(i,j).toString());

                }


            }
        }
    }

//    public static void writeToCell(String workBookName, String sheetName, List<String> momRangeAndValue, List<String> adjRangeAndValue) throws IOException, InvalidFormatException {
//        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(workBookName)));
//        XSSFSheet spreadsheet = (XSSFSheet) createSheetInWorkBook(workbook, sheetName);
//        CellAddress cellAddress = new CellAddress(momRangeAndValue.get(0));
//        int row = cellAddress.getRow();
//        int column = cellAddress.getColumn();
//        XSSFCell cell = spreadsheet.getRow(row).getCell(column);
//        cell.setCellValue(momRangeAndValue.get(1));
//        cell.setCellStyle(spreadsheet.getRow(row).getCell(19).getCellStyle());
//        cellAddress = new CellAddress(adjRangeAndValue.get(0));
//        row = cellAddress.getRow();
//        column = cellAddress.getColumn();
//        spreadsheet.getRow(row).createCell(column, CellType.FORMULA).setCellValue(adjRangeAndValue.get(1));
//        FileOutputStream fileOutputStream = new FileOutputStream(new File(workBookName));
//        workbook.write(fileOutputStream);
//        fileOutputStream.close();
//    }

//    public static void writeToCell(String workBookName, String sheetName, List<String> momRangeAndValue) throws IOException, InterruptedException, AutomationException {
//        ActionExecutors.waitForWindowWithTitle(FilenameUtils.getBaseName(workBookName) + " - Excel");
//        assert momRangeAndValue.size()>=2;
//        ComThread.InitSTA();
//        ActiveXComponent xl = ActiveXComponent.connectToActiveInstance("Excel.Application");
//        ActiveXComponent workbooks = xl.invokeGetComponent("workbooks");
//        ActiveXComponent workbook = workbooks.invokeGetComponent("open",new Variant(workBookName));
//        ActiveXComponent sheets = workbook.invokeGetComponent("sheets",new Variant(SheetHandler.getNameAndIndexMap(workBookName).get(sheetName)));
//        CellAddress cellAddress = new CellAddress(momRangeAndValue.get(0));
//        sheets.invokeGetComponent("cells",
//                new Variant(cellAddress.getRow()+1),
//                new Variant(cellAddress.getColumn()+1)).setProperty("value", momRangeAndValue.get(1));
//        xl.safeRelease();
//        ComThread.Release();
//    }

//    public static void writeToSheet(String workBookName, String sheetName, TestCaseFlowReportItem testCaseFlowReportItem) throws IOException, InvalidFormatException {
//        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(workBookName)));
//        XSSFSheet spreadsheet = (XSSFSheet) createSheetInWorkBook(workbook, sheetName);
//        XSSFRow row = spreadsheet.createRow(testCaseFlowReportItem.getRowNumber());
//        row.createCell(0).setCellValue(testCaseFlowReportItem.getTestCaseName());
//        row.createCell(1).setCellValue(testCaseFlowReportItem.getAction());
//        row.createCell(2).setCellValue(testCaseFlowReportItem.getDescription());
//        row.createCell(3).setCellValue(testCaseFlowReportItem.getResultFlag());
//        Date date = Calendar.getInstance().getTime();
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        String strDate = dateFormat.format(date);
//        row.createCell(4).setCellValue(strDate);
//        FileOutputStream fileOutputStream = new FileOutputStream(new File(workBookName));
//        workbook.write(fileOutputStream);
//        fileOutputStream.close();
//    }

//    public static void writeToSheet(String workBookName, String sheetName, TestCaseDriverReportItem testCaseDriverReportItem) throws IOException, InvalidFormatException {
//        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(workBookName)));
//        XSSFSheet spreadsheet = (XSSFSheet) createSheetInWorkBook(workbook, sheetName);
//        XSSFRow row = spreadsheet.createRow(testCaseDriverReportItem.getRowNumber());
//        row.createCell(0).setCellValue(testCaseDriverReportItem.getTestCaseName());
//        row.createCell(1).setCellValue(testCaseDriverReportItem.getResultFlag());
//        Date date = Calendar.getInstance().getTime();
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        String strDate = dateFormat.format(date);
//        row.createCell(2).setCellValue(strDate);
//        FileOutputStream fileOutputStream = new FileOutputStream(new File(workBookName));
//        workbook.write(fileOutputStream);
//        fileOutputStream.close();
//    }

//    public static void inputDataIntoCell(String sheetName, String workBookName, String cellAddress, String valueToInput) throws IOException, InvalidFormatException, AutomationException, InterruptedException {
//        SheetHandler.writeToCell(workBookName,
//                sheetName,
//                Arrays.asList(cellAddress, valueToInput));
//    }

//    public static void runMacro(String macroName, String workBookName, UIAutomation automation, Row row) throws AutomationException, InterruptedException, IOException, AWTException, InvalidFormatException, PatternNotFoundException {
//        ActionExecutors.waitForWindowWithTitle(FilenameUtils.getBaseName(workBookName) + " - Excel");
//        ComThread.InitSTA();
//        ActiveXComponent xl = ActiveXComponent.connectToActiveInstance("Excel.Application");
//        //ActiveXComponent workbooks = xl.invokeGetComponent("workbooks");
//        xl.invoke("Run",new Variant(macroName));
//        //ActionExecutors.selectRegion(automation, row);
//        xl.safeRelease();
//        ComThread.Release();
//    }

    public static Sheet createSheetInWorkBook(XSSFWorkbook wb, String workSheetName) throws IOException, InvalidFormatException {
        Sheet sheet;
        try {
            sheet = wb.getSheet(workSheetName);
        } catch (Exception e) {
            sheet = wb.createSheet(WorkbookUtil.createSafeSheetName(workSheetName));
        }
        return sheet;
    }

    //Returns the Excel sheet name and index
    public static LinkedHashMap<String, Integer> getNameAndIndexMap(String workbookPath) throws IOException {
        LinkedHashMap<String, Integer> sheetNameAndIndexMap = new LinkedHashMap<>();
        Workbook workbook = new XSSFWorkbook(workbookPath);

        for(int i=0; i<workbook.getNumberOfSheets(); i++){
            sheetNameAndIndexMap.putIfAbsent(workbook.getSheetName(i), i+1);
        }
        return sheetNameAndIndexMap;
    }

    public static Table compareTables(Table table1, Table table2){
        NumberFormat myFormat = NumberFormat.getInstance();
        Table diffCells = Table.create("Different Cells").addColumns(StringColumn.create("Original Value"),StringColumn.create("New Value"),StringColumn.create("Cell Reference"));
        int counter = 0;
        for(int i = 0; i < table1.columnCount(); i++){
            for(int j = 0; j< table1.rowCount(); j++){
                if(table1.get(j,i) == null){
                }
                else if(table1.get(j,i).equals(table2.get(j,i))){
                }
                else{
                    CellReference ref = new CellReference(j,i);
                    diffCells.appendRow().setString("Original Value",myFormat.format(table1.get(j,i)));
                    diffCells.row(counter).setString("New Value",myFormat.format(table2.get(j,i)));
                    diffCells.row(counter).setString("Cell Reference",ref.formatAsString());
                    counter++;
                }
            }
        }
        return diffCells;
    }

    public static void selectSheet(UIAutomation automation, String workBookName, String workSheetName) throws PatternNotFoundException, AutomationException, InterruptedException, IOException {
        automation.getDesktopWindow(workBookName + " - Excel").getTabByAutomationId(workBookName + ".xlsm").selectTabPage(workSheetName);
    }

//    public static void clickCalculate(Row row) throws InterruptedException, AWTException, IOException, InvalidFormatException, AutomationException {
//        try{
//            ActionExecutors.waitForWindowWithTitle(ControlType.Image, "ACR MOM Calculate");
//            Thread.sleep(5000);
//            Robot robot = new Robot();
//            robot.mouseMove(352, 483);
//            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
//            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
//            ReportWriter.assertStepAndPrint("Click Calculate", true, true, row);
//            System.out.println(ActionExecutors.waitForWindowWithTitle("Prompts") + "PROMPTS FOUND");
//        }catch(Exception e){
//            ReportWriter.assertStepAndPrint("Click Calculate", true, false, row);
//        }
//    }

//    public static void clickSave() throws InterruptedException, AWTException, IOException, InvalidFormatException, AutomationException {
//        try{
//            ActionExecutors.waitForWindowWithTitle(ControlType.Image, "Save");
//            Thread.sleep(5000);
//            Robot robot = new Robot();
//            robot.mouseMove(129, 329);
//            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
//            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
//            //ReportWriter.assertStepAndPrint("Click Save", true, true, row);
//            //System.out.println(ActionExecutors.waitForWindowWithTitle("Prompts") + "PROMPTS FOUND");
//        }catch(Exception e){
////            ReportWriter.assertStepAndPrint("Click Refresh", true, false, row);
//        }
//    }
}

