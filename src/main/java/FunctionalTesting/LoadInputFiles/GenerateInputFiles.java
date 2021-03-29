package FunctionalTesting.LoadInputFiles;

import FunctionalTesting.DataModel.DependencyWbData;
import FunctionalTesting.DataModel.InputWbData;
import FunctionalTesting.DataModel.TestCaseDetails;
import FunctionalTesting.DataValidator.PerformValidation;
import FunctionalTesting.DataValidator.ValidateMappingDocument;
import FunctionalTesting.ExecutionHandlers.ActionExecutors;
import FunctionalTesting.ExecutionHandlers.WindowsSyncCheck;
import FunctionalTesting.ExtractData.CustomExcelReader;
import FunctionalTesting.ExtractData.ExtractTable;
import FunctionalTesting.ExtractData.SheetHandler;
import FunctionalTesting.ExtractData.TablesawReader;
import FunctionalTesting.Jacob.JacobBase;
import FunctionalTesting.Util.ConfigProperties;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import javafx.scene.control.Alert;
import mmarquee.automation.AutomationElement;
import mmarquee.automation.AutomationException;
import mmarquee.automation.ControlType;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.AutomationBase;
import mmarquee.automation.controls.AutomationButton;
import mmarquee.automation.controls.AutomationWindow;
import mmarquee.automation.controls.menu.AutomationMenuItem;
import mmarquee.automation.pattern.PatternNotFoundException;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.xlsx.XlsxReadOptions;
import tech.tablesaw.io.xlsx.XlsxReader;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Map;

import FunctionalTesting.Constants.Constant;

public class GenerateInputFiles {
    WindowsSyncCheck windowsSyncCheck = new WindowsSyncCheck();
    UIAutomation automation = UIAutomation.getInstance();
    ExtractTable extractTable = new ExtractTable();
    String path = null;
    boolean isExecSuccessful = false;
    String fileName = null;
    ValidateMappingDocument validateMappingDocument = new ValidateMappingDocument();
    PerformValidation performValidation;
    Table fileDataTable = null;
    String nameWithDate = null;
    ExtentTest test;
    //int successfulStepCount;
    //int stepCount;
    boolean isPrevStepSuccessful = true;
    boolean hasError = false;
    String wbBook1Name = null;

    //TODO
    //Reads the test steps from the input file and invokes the keyword methods
    public boolean runTestAction(InputWbData inputWbData, ExtentReports extentReports) throws Exception {
        test = extentReports.createTest(inputWbData.getTestCaseDetails().getTestCaseName());
        File keywordSheet = inputWbData.getKeyWordSheet();
        XSSFWorkbook wb = new XSSFWorkbook(keywordSheet);

        XSSFSheet sheetToOpen = (XSSFSheet) SheetHandler.getSheetFromWorkBook(wb, inputWbData.getTestCaseDetails().getRoleType());
        int endRow = sheetToOpen.getLastRowNum();
        int endColumn = sheetToOpen.getRow(0).getLastCellNum();
        XlsxReadOptions options = XlsxReadOptions.builder(keywordSheet)
                .sheetIndex(extractTable.getNameAndIndexMap(keywordSheet, inputWbData.getTestCaseDetails().getRoleType(), true))
                .header(true)
                .build();
        TablesawReader xlsxReader = new TablesawReader(keywordSheet, inputWbData.getTestCaseDetails().getRoleType());
        boolean isKeyWordSheet = true;
        fileDataTable = xlsxReader.read(options, 0, endRow, 0,endColumn-1, isKeyWordSheet);
        //successfulStepCount = 0;
        //stepCount = 0;
        if (fileDataTable != null && fileDataTable.rowCount() > 0) {

            for (Row eachRow : fileDataTable) {
                String testCaseDesc = eachRow.getString("Test Case Description");
                inputWbData.getTestCaseDetails().setTestCaseDescription(testCaseDesc);
                String runFlag = eachRow.getString("Run Flag");
                if (!(testCaseDesc.equals("") || runFlag.equals(""))) {
                    test.log(Status.INFO, "******************Test Case Description: " + testCaseDesc + "*********************");
                    path = createDirectory();
                }
                String testAction = eachRow.getString("Action");
                inputWbData.getTestCaseDetails().setTestAction(testAction);

                if (runFlag.equals("Y")) {
                    //stepCount++;
                    switch (testAction) {
                        case "OpenWorkbook":
                            test.log(Status.INFO, MarkupHelper.createLabel("Open Workbook", ExtentColor.BLUE));
                            List<Object> result = openWorkbook(eachRow, inputWbData.getTestCaseDetails());
                            fileName = (String) result.get(1);
                            isPrevStepSuccessful = (boolean) result.get(0);
                            //successfulStepCount++;
                            break;
                        case "RunVariant":
                            test.log(Status.INFO, MarkupHelper.createLabel("Run Variant", ExtentColor.BLUE));
                            if (isPrevStepSuccessful) {
                                isPrevStepSuccessful = runVariant(eachRow);
                                //successfulStepCount++;
                            }
                            break;
                        case "expandHierarchy":
                            test.log(Status.INFO, MarkupHelper.createLabel("Expand Hierarchy", ExtentColor.BLUE));
                            if (isPrevStepSuccessful) {
                                isPrevStepSuccessful = expandHierarchy(fileName,eachRow);
                                System.out.println("---------------------------"+isPrevStepSuccessful);
                                //successfulStepCount++;
                            }
                            break;
                        case "SaveWorkbook":
                            test.log(Status.INFO, MarkupHelper.createLabel("Save Workbook", ExtentColor.BLUE));
                            if (isPrevStepSuccessful) {
                                isPrevStepSuccessful = saveFile(fileName);
                                //successfulStepCount++;
                            }
                            break;
                        case "ValidateReport":
                            test.log(Status.INFO, MarkupHelper.createLabel("Validate Report", ExtentColor.BLUE));
                            if (isPrevStepSuccessful) {
                                if (validateReport(eachRow, inputWbData, test)) {
                                    isPrevStepSuccessful = true;
                                    //successfulStepCount++;
                                }
                            }
                            break;
                        case "VerifyAreaSelected":
                            test.log(Status.INFO, MarkupHelper.createLabel("VerifyAreaSelected", ExtentColor.BLUE));
                            if (isPrevStepSuccessful) {
                                if (verifyAreaSelected(eachRow, inputWbData)) {
                                    isPrevStepSuccessful = true;
                                    //successfulStepCount++;
                                }
                            }
                            break;
                        case "VerifyACR_MOM":
                            test.log(Status.INFO, MarkupHelper.createLabel("VerifyACR_MOM", ExtentColor.BLUE));
                            if (isPrevStepSuccessful) {
                                if (calculateACR_MOM(eachRow, test)) {
                                    isPrevStepSuccessful = true;
                                    //successfulStepCount++;
                                }
                            }
                            break;
                        case "WriteToCell":
                            test.log(Status.INFO, MarkupHelper.createLabel("WriteToCell", ExtentColor.BLUE));
                            if (isPrevStepSuccessful) {
                                if (writeToCell(eachRow)) {
                                    isPrevStepSuccessful = true;
                                    //successfulStepCount++;
                                }
                            }
                            break;
                    }
                }
            }
        }
       /* System.out.println(stepCount+ "-----------------");
        if (stepCount > 0 && successfulStepCount == stepCount)
            isExecSuccessful = true;*/

        return true;
    }

    //Validates the data in the report matches the data in the input form
    private boolean validateReport(Row tableRow, InputWbData inputWbData, ExtentTest test) throws Exception {
        performValidation = new PerformValidation();
        String acrReportWbName = tableRow.getString("Parameter 1");
        inputWbData.getTestCaseDetails().setReportWbName(acrReportWbName);
        String acrReportWsName = tableRow.getString("Parameter 2");
        inputWbData.getTestCaseDetails().setReportWsName(acrReportWsName + " ");
        // a File instance for the directory:
        File workingDirFile = new File(path);
        File[] dir_contents = workingDirFile.listFiles();
        for (File eachFile : dir_contents) {
            String fileName = eachFile.getName().contains("(") ? eachFile.getName().split("\\(")[0]
                    : eachFile.getName().split("\\.")[0];
            if (fileName.trim().equals(inputWbData.getTestCaseDetails().getReportWbName())) {
                inputWbData.setReportWb(eachFile);
                break;
            }
        }
        Map<String, List<DependencyWbData>> mappingData = validateMappingDocument.getMappingIndex(inputWbData.getTestCaseDetails());
        if (mappingData != null && mappingData.size() > 0) {
            inputWbData.getTestCaseDetails().setMasterIndex(mappingData.entrySet().stream().findFirst().get().getKey());
            inputWbData.setDependencyWbData(mappingData.entrySet().stream().findFirst().get().getValue());
        }
        if (inputWbData.getTestCaseDetails().getMasterIndex() != null) {
            try {
                performValidation.validateData(inputWbData,test);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Validation is successful for: " + inputWbData.getTestCaseDetails().getTestCaseName());
                alert.showAndWait();
                return true;
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Validation is failed for: " + inputWbData.getTestCaseDetails().getTestCaseName());
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                throw new Exception("Validation is failed for: " + inputWbData.getTestCaseDetails().getTestCaseName() +": "+ e);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setHeaderText("No mapping exists for the given validation input");
            alert.showAndWait();
            throw new Exception("Validation is failed for: " + inputWbData.getTestCaseDetails().getTestCaseName());
        }
    }

    //Logs into SAP using either Single-Sign-On or credentials
    public boolean verifyLogin(TestCaseDetails testCaseDetails) throws IOException, PatternNotFoundException, AutomationException, AWTException, InterruptedException {
        String userName = System.getProperty("user.name");
        String filePath = "C:\\Users\\"+ userName +"\\Documents\\BTAF Framework";
        File file = new File(filePath+"\\Book1.xlsx");
        Desktop.getDesktop().open(file);
        AutomationElement element = windowsSyncCheck.checkForWindows(ControlType.Window, "Book1.xlsx - Excel", 5);
        AutomationWindow window = null;
        if (element != null) {
            window = automation.getDesktopWindow("Book1.xlsx - Excel");
            wbBook1Name = "Book1.xlsx";
        } else {
            ActionExecutors.waitForWindowWithTitle(ControlType.Window, "Book1 - Excel");
            window = automation.getDesktopWindow("Book1 - Excel");
            wbBook1Name = "Book1";
        }
        //ActionExecutors.waitForWindowWithTitle(ControlType.Window, "Book1.xlsx - Excel");
        window.maximize();
        window.getButtonByAutomationId("FileTabButton").click();
        window.getListByAutomationId("NavBarMenu").getItem(2).select();
        AutomationMenuItem menuItem = (AutomationMenuItem) window.getControlByClassName("Open Workbook", "NetUIAnchor");
        menuItem.expand();
        AutomationMenuItem menuItem1 = (AutomationMenuItem) window.getControlByClassName("Open a workbook from the SAP Business Warehouse Platform.", "NetUITWBtnMenuItem");
        menuItem1.click();
        ActionExecutors.waitForWindowWithTitle(ControlType.Window, "Open Document");
        AutomationWindow window1 = automation.getDesktopWindow("Open Document");
        String executionEnvName = testCaseDetails.getExecutionEnvironment();
        window1.getListByAutomationId("_connectionListView").getItem(executionEnvName).select();
        //TODO: Fragile, need to find better solution
        // window1.getButtonByAutomationId("mNextButton").click();
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        AutomationElement selectUserElement = windowsSyncCheck.checkForWindows(ControlType.Window, "Select user", 2);
        if (selectUserElement != null) {
            window.getButtonByAutomationId("CancelButton").click();
        }

        String loginWindowName = "Logon to System " + executionEnvName;
        ActionExecutors.waitForWindowWithTitle(ControlType.Window, loginWindowName);
        AutomationWindow loginWindow = automation.getDesktopWindow(loginWindowName);
        if (testCaseDetails.getLoginType() == "SSO") {
            hasError = ActionExecutors.checkForErrorMessage(automation, testCaseDetails);
        } else if (testCaseDetails.getLoginType() == "Credential") {
            Thread.sleep(7000);
            ActionExecutors.credentialsLogin(automation, executionEnvName, testCaseDetails);
            hasError = ActionExecutors.checkForErrorMessage(automation, testCaseDetails);
        }
        return hasError;
    }

    //Opens the excel workbook specified in the test case flow input file test step
    public List<Object> openWorkbook(Row tableRow, TestCaseDetails testCaseDetails) throws Exception {
        List<Object> result = new ArrayList<>();
        String baseFolderName = tableRow.getString("Parameter 1");
        String subFolderName = tableRow.getString("Parameter 2");
        String workBookName = tableRow.getString("Parameter 3");
        if (!verifyLogin(testCaseDetails)) {
            try {
                ActionExecutors.clickOnRoleFolderNames(automation, baseFolderName, subFolderName, workBookName);
                ActionExecutors.openPromptsWindow(automation, workBookName + ".xlsm - Excel");
                result.add(true);
                result.add(workBookName);
            } catch (PatternNotFoundException | AutomationException | InterruptedException | IOException e) {
                throw new Exception(e);
            }
        } else {
            throw new Exception("User login failed");
        }
        return result;
    }

    //Runs the variant specified in the test case flow input file test step
    public boolean runVariant(Row tableRow) throws Exception {
        String variantName = tableRow.getString("Parameter 1");
        try {
            ActionExecutors.selectVariantTest(automation, variantName);
        } catch (PatternNotFoundException | AutomationException | AWTException | InterruptedException | IOException e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return true;
    }

   //Saves the Excel workbook with the given name
    public boolean saveFile(String workbookName) throws PatternNotFoundException, AutomationException, InterruptedException, AWTException, IOException {
        AutomationWindow window = automation.getDesktopWindow(workbookName + ".xlsm - Excel");
        window.getButtonByAutomationId("FileTabButton").click();
        while (true) {
            try {
                window.getListByAutomationId("NavBarMenu").getItem("Save As").select();
                AutomationButton button1 = (AutomationButton) window.getControlByClassName("Browse", "NetUISimpleButton");
                button1.click();
                break;
            } catch (PatternNotFoundException | AutomationException e) {
                Thread.sleep(2000);
            }
        }
        Thread.sleep(3000);
        window.getToolBar("Address band toolbar").getButton("Previous Locations").click();
        window.getEditBox("Address").setValue(path);
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        Thread.sleep(2000);
        window.getButton("Save").click();
        automation.getDesktopWindow(wbBook1Name + " - Excel").getPanel("Ribbon").getButton("Close").click();
        Thread.sleep(5000);
        File workingDirFile = new File(path);
        File[] dir_contents = workingDirFile.listFiles();
        for (File eachFile : dir_contents) {
            String fileName = eachFile.getName().contains("(") ? eachFile.getName().split("\\(")[0]
                    : eachFile.getName().split("\\.")[0];
            if (fileName.trim().equals(workbookName)) {
                nameWithDate = eachFile.getName();
                break;
            }
        }
        automation.getDesktopWindow(nameWithDate + " - Excel").getPanel("Ribbon").getButton("Close").click();
        Thread.sleep(5000);
        String fileName = nameWithDate.split("\\(")[0].trim();
        AutomationElement element = windowsSyncCheck.checkForWindows(ControlType.Window, "Microsoft Excel", 2);
        if (element != null) {
            for (AutomationBase e : automation.getDesktopWindow(nameWithDate + " - Excel").getChildren(true)) {
                try {
                    if (e.getName().equals("Microsoft Excel")) {
                        for (AutomationBase e1 : e.getChildren(true)) {
                            try {
                                if (e1.getClassName().equals("NetUINetUIDialog")) {
                                    e1.getElement().setFocus();
                                    Robot robotSave = new Robot();
                                    robotSave.keyPress(KeyEvent.VK_ENTER);
                                    robotSave.keyRelease(KeyEvent.VK_ENTER);
                                    break;
                                }
                            } catch (AutomationException | AWTException exp) {
                                exp.printStackTrace();
                                return false;
                            }
                        }
                        break;
                    }
                } catch (AutomationException | PatternNotFoundException automationException) {
                    automationException.printStackTrace();
                    return false;
                }
            }
        }
        System.out.println("File saved successfully");
        return true;
    }

    //Expands the hierarchy of the ACR Summary Report
    public boolean expandHierarchy(String workbookName, Row tableRow) throws PatternNotFoundException, AutomationException, AWTException, InterruptedException {
        AutomationWindow window = automation.getDesktopWindow(workbookName + ".xlsm - Excel");
        String sheetName = tableRow.getString("Parameter 1");
        String address = tableRow.getString("Parameter 2");
        String hierarchyLevel = tableRow.getString("Parameter 3");
        boolean isHierarchyEnabled = false;
        boolean isSheetOpen = false;
        for (AutomationBase e : window.getChildren(true)) {
            String name = e.getName();
            if (e.getName().startsWith(workbookName)) {
                window.getTab(name).selectTabPage(sheetName);
                for(int i=0;i<5 ;i++) {
                    Thread.sleep(5000);
                }
                isSheetOpen = true;
                break;
            }
        }

        if(isSheetOpen) {
            for (AutomationBase eachChild : window.getChildren(true)) {
                if (eachChild.getElement().getAutomationId().equals("13")) {
                    window.getEditBox("Name Box").setValue(address);
                    Robot robot = new Robot();
                    robot.keyPress(KeyEvent.VK_ENTER);
                    robot.keyRelease(KeyEvent.VK_ENTER);
                    Thread.sleep(5000);
                    isHierarchyEnabled = true;
                    break;
                }
            }
            if(isHierarchyEnabled) {
                window.getTab("Ribbon Tabs").selectTabPage("Analysis");
                AutomationMenuItem menuItem = (AutomationMenuItem) window.getControlByClassName("Hierarchy","NetUIAnchor");
                menuItem.expand();
                AutomationMenuItem menuItem1 = (AutomationMenuItem) window.getControlByClassName("Expand to Level", "NetUIAnchor");
                menuItem1.expand();
                Robot robot1 = new Robot();
                if(hierarchyLevel.equals("Level 05")) {
                    for(int i=0; i<4; i++) {
                        robot1.keyPress(KeyEvent.VK_DOWN);
                        robot1.keyRelease(KeyEvent.VK_DOWN);
                    }
                } else if(hierarchyLevel.equals("Level 03")) {
                    for(int i=0; i<2; i++) {
                        robot1.keyPress(KeyEvent.VK_DOWN);
                        robot1.keyRelease(KeyEvent.VK_DOWN);
                    }
                }
                robot1.keyPress(KeyEvent.VK_ENTER);
                robot1.keyRelease(KeyEvent.VK_ENTER);
                Thread.sleep(9000);
                return true;
            }
        }
        return false;
    }

    //Creates a temp folder to store the workbooks for performing validation calculations
    private String createDirectory() throws IOException {
        String dirPath = System.getProperty("user.dir");
        File newDirectory = new File(dirPath + "/temp");
        boolean isCreated = newDirectory.mkdirs();
        String path = null;
        if (isCreated) {
            path = newDirectory.getPath();
        } else if (newDirectory.exists()) {
            FileUtils.forceDelete(newDirectory);
            newDirectory.mkdirs();
            path = newDirectory.getPath();
        }
        return path;
    }

    private int titleToNumber(String s)
    {
        // This process is similar to
        // binary-to-decimal conversion
        int result = 0;
        for (int i = 0; i < s.length(); i++)
        {
            result *= 26;
            result += s.charAt(i) - 'A' + 1;
        }
        return result;
    }

    //Verifies the area selected for the excel sheet matches the value in the test case flow input workbook
    public boolean verifyAreaSelected(Row tableRow, InputWbData inputWbData) throws IOException, InvalidFormatException {
        String expectedArea = null;
        String actualArea = null;
        String workBookName = tableRow.getString("Parameter 1");
        String sheetName = tableRow.getString("Parameter 2");
        String areaCellAddress = tableRow.getString("Parameter 3");
        expectedArea = tableRow.getString("Parameter 4");

        // a File instance for the directory:
        File workingDirFile = new File(path);
        File[] dir_contents = workingDirFile.listFiles();

        for (File eachFile : dir_contents) {
            Workbook wb = WorkbookFactory.create(new File(eachFile.getAbsolutePath()));
            Sheet sheetToOpen = SheetHandler.getSheetFromWorkBook(wb, sheetName);
            actualArea = SheetHandler.getDataInCellFromSheet(sheetToOpen, areaCellAddress).getStringCellValue();
            wb.close();
            if (expectedArea.equals(actualArea)) {
                System.out.println("Area Verified Successfully");
                return true;
            } else {
                System.out.println("Area Verification Failed");
                return false;
            }
        }
        return false;
    }

    //ToDo Maintenance: Formulas for calculating ACR$ and MOM% are in this method
   // public LinkedHashMap calculateACR_MOM(Row row, String workBookName, ExtentReports extent) throws Exception {

    //Performs the validation calculations for the ACR_MOM sheet
    public boolean calculateACR_MOM(Row tableRow, ExtentTest test) throws Exception {
        //Create test in HTML report
       // test = extent.createTest("Calculate_Field_Segments_Test");
        test.log(Status.INFO, MarkupHelper.createLabel("Started execution for Test " + "Calculate_Field_Segments_Test" +
                " with description " + "Description_Test" +
                " with run flag " + "Run_Flag_Test", ExtentColor.BLUE));


        String sheetName = tableRow.getString("Parameter 1");
        String tableHeaderName = tableRow.getString("Parameter 2");
        String columnName = tableRow.getString("Parameter 3");


        // a File instance for the directory:
        File workingDirFile = new File(path);
        File[] dir_contents = workingDirFile.listFiles();
        for (File eachFile : dir_contents) {

            String workBookName = eachFile.getAbsolutePath();

            Workbook wb = WorkbookFactory.create(new File(eachFile.getAbsolutePath()));

         //   String sheetName = row.getString(ConfigProperties.getProperty("testCaseFlow.ACR_MOM_SheetName"));
            XSSFSheet sheet = (XSSFSheet) SheetHandler.getSheetFromWorkBook(wb, sheetName);
            int numberOfColumns = Integer.parseInt(ConfigProperties.getProperty("testCaseFlow.ACR_MOM_Columns"));
            int numberOfRowsFieldSegment = Integer.parseInt(ConfigProperties.getProperty("testCaseFlow.ACR_MOM_Rows_Per_Field_Segment"));
            final int numberOfRowsConstant = numberOfRowsFieldSegment;

            //Gets the cell with the String input
            XSSFCell tableHeader = CustomExcelReader.getCellByString(sheet, tableHeaderName);

            //Gets the last cell in the table based on the number of columns
            XSSFCell lastColumnHeader = CustomExcelReader.getCellContentByIndex(sheet, tableHeader.getRowIndex(), tableHeader.getColumnIndex() + numberOfColumns - 1);

            //Cell Reference of the first cell in the table
            String fieldSegmentReference = tableHeader.getReference();

            //Calls getArrayListFieldSegments method to create ArrayList of Field Segment Cells
            ArrayList<XSSFCell> fieldSegments = getArrayListFieldSegments(workBookName, sheetName, tableHeader, numberOfRowsFieldSegment, numberOfRowsConstant);

            //Get last cell in the ArrayList (Last Field Segment in Table)
            XSSFCell lastFieldSegment = fieldSegments.get(fieldSegments.size() - 1);

            //Get Last Cell in the last column of the table
            XSSFCell lastCell = CustomExcelReader.getCellContentByIndex(sheet, lastFieldSegment.getRowIndex() + numberOfRowsConstant - 1, lastColumnHeader.getColumnIndex());

            //Cell Reference of the last cell in the sheet
            String lastCellReference = lastCell.getReference();

            //Create Table using Cell References
            Table fieldSegmentTable = SheetHandler.getTableInCellRangeFromSheet(workBookName, sheetName, fieldSegmentReference + ":" + lastCellReference);

            //Remove Adj$ and ACR YoY% rows from table
            Table modifiedTable = removeTwoRowsFromFieldSegment(fieldSegmentTable);

            //Remove columns from table
            removeColumns(modifiedTable, "FY 2020;FY 2021");

            //Calculate values for MOM% and ACR$
            int startingColumnIndex = modifiedTable.columnIndex(columnName);

            //Create LinkedHashMp to store field Segment calculation results tables
            LinkedHashMap<String, Table> acrReports = new LinkedHashMap<String, Table>();
            //ArrayList<Table> acrReports = new ArrayList<>();

            Table acrReport = null;
            for (int j = 0; j < modifiedTable.rowCount(); j += 2) {
                //Create TableSaw Table to store calculation results
                String[] rowNames = {"ACR $: Expected", "ACR $: Actual", "MOM%: Expected", "MOM% Actual", "ACR $: Result", "MOM%: Result"};
                acrReport = Table.create(modifiedTable.column(0).get(j).toString());
                acrReport.addColumns(StringColumn.create("Field Segment: ", rowNames));


                for (int i = startingColumnIndex; i < modifiedTable.columnCount(); i++) {
                    //Get Days in month for current month and 6 months past
                    Integer pastMonth = Constant.getMonths().get(modifiedTable.columnNames().get(i - 1).substring(0, 3));
                    int pastYear = Integer.parseInt(modifiedTable.columnNames().get(i - 1).split(",")[1].trim());
                    Integer past6Month = Constant.getMonths().get(modifiedTable.columnNames().get(i - 7).substring(0, 3));
                    int past6Year = Integer.parseInt(modifiedTable.columnNames().get(i - 7).split(",")[1].trim());
                    Integer currentMonth = Constant.getMonths().get(modifiedTable.columnNames().get(i).substring(0, 3));
                    int currentYear = Integer.parseInt(modifiedTable.columnNames().get(i).split(",")[1].trim());

                    YearMonth yearMonthObject1 = YearMonth.of(pastYear, pastMonth);
                    int daysInMonth1 = yearMonthObject1.lengthOfMonth();
                    YearMonth yearMonthObject2 = YearMonth.of(past6Year, past6Month);
                    int daysInMonth2 = yearMonthObject2.lengthOfMonth();
                    YearMonth yearMonthObject3 = YearMonth.of(currentYear, currentMonth);
                    int daysInMonth3 = yearMonthObject3.lengthOfMonth();

                    //ToDo Maintenance: Calculations for expected MOM%
                    //Get the ACR$ amount of previous month
                    double ACR$Past = Math.round(Double.parseDouble((modifiedTable.get(j, i - 1)).toString()) * 1000000.0) / 1000000.0;

                    //Get the ACR$ amount of previous 6 month
                    double ACR$Past6 = Math.round(Double.parseDouble((modifiedTable.get(j, i - 7)).toString()) * 1000000.0) / 1000000.0;

                    //Temporary value for rounding
                    double tempValue = Math.round(((ACR$Past / daysInMonth1) / (ACR$Past6 / daysInMonth2)) * 1000000.0) / 1000000.0;

                    //Calculate Expected MOM% value
                    double expectedMOM_Per = Math.pow(tempValue, 0.16666666666) - 1;

                    //Get the actual MOM% value for current month
                    double actualMOM_Per = Math.round(Double.parseDouble((modifiedTable.get(j + 1, i)).toString()) * 1000000.0) / 1000000.0;

                    //ToDo Maintenance: Calculations for expected ACR$ amount
                    double tempRoundUntoSixDecimals = Math.round((1.00 + actualMOM_Per) * 1000000.0) / 1000000.0;
                    double tempRoundSixMulLastValueRatio = Math.round(((ACR$Past / daysInMonth1) * tempRoundUntoSixDecimals) * 1000000.0) / 1000000.0;
                    double tempRound = Math.round(tempRoundSixMulLastValueRatio * 1000000.0) / 1000000.0;
                    double expectedAcr_Dol = tempRound * daysInMonth3;

                    String fieldSegment = "Field Segment: " + modifiedTable.column(0).get(j);
                    acrReport.column(0).setName(fieldSegment);
                    String acrResult;
                    String momResult;

                    //Round expected and actual values to 4 decimals
                    double roundedExpectedACR$ = Math.round(expectedAcr_Dol * 10000.0) / 10000.0;
                    double roundedActualACR$ = Math.round(Double.parseDouble((modifiedTable.get(j, i)).toString()) * 10000.0) / 10000.0;
                    double roundedExpectedMOM_Per = Math.round(expectedMOM_Per * 10000.0) / 10000.0;
                    double roundedActualMoM_Per = Math.round(Double.parseDouble((modifiedTable.get(j + 1, i)).toString()) * 10000.0) / 10000.0;

                    //Compare expected and actual values
                    if (roundedExpectedACR$ == roundedActualACR$) {
                        acrResult = "Passed";
                        String status = acrResult;
//                    logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                        test.log((Status.PASS), MarkupHelper.createLabel( modifiedTable.columnNames().get(i) + " : " + "ACR$ " + status + " for " + fieldSegment,(ExtentColor.GREEN)));
                    } else {
                        acrResult = "Failed";
                        String status = acrResult;
//                    logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                    test.log((Status.FAIL), MarkupHelper.createLabel( modifiedTable.columnNames().get(i) + " : " + "ACR$ " + status + " for " + fieldSegment + " | Expected: " + roundedExpectedACR$ + " | Actual: " + roundedActualACR$,(ExtentColor.RED)));
                    }
                    if (roundedExpectedMOM_Per == roundedActualMoM_Per) {
                        momResult = "Passed";
                        String status = momResult;
//                    logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                    test.log((Status.PASS), MarkupHelper.createLabel( modifiedTable.columnNames().get(i) + " : " + "MOM% " + status + " for " + fieldSegment,(ExtentColor.GREEN)));
                    } else {
                        momResult = "Failed";
                        String status = momResult;
//                    logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                    test.log((Status.FAIL), MarkupHelper.createLabel( modifiedTable.columnNames().get(i) + " : " + "MOM% " + status + " for " + fieldSegment + " | Expected: " + roundedExpectedMOM_Per + " | Actual: " + roundedActualMoM_Per,(ExtentColor.RED)));
                    }
                    //Store results in TableSaw Table
                    String month = modifiedTable.columnNames().get(i);
                    String[] columnData = {String.valueOf(new DecimalFormat("#.####").format(roundedExpectedACR$)),
                            String.valueOf(new DecimalFormat("#.####").format(roundedActualACR$)),
                            String.valueOf(new DecimalFormat("#.####").format(roundedExpectedMOM_Per)),
                            String.valueOf(new DecimalFormat("#.####").format(roundedActualMoM_Per)),
                            acrResult,
                            momResult};
                    acrReport.addColumns(StringColumn.create(month, columnData));
                }
                //Store results in LinkedHashMap
                acrReports.put(acrReport.name(), acrReport);
//                test.log(Status.INFO, String.valueOf(acrReports));
                System.out.println(acrReports);
            }
            wb.close();
        }

            //return acrReports;
            return true;

        }


        //Loop through Field Segments and returns an ArrayList with the Cells containing the Field Segment Name
//    public static ArrayList<XSSFCell> getArrayListFieldSegments(String workbookPath, String sheetName, XSSFCell fieldSegmentHeader, int numberOfRowsFieldSegment, int numberOfRowsConstant) throws Exception {
//        //file, workbook, and sheet to connect to
//        FileInputStream inputStream = new FileInputStream(new File(workbookPath));
//        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
//        XSSFSheet sheet = wb.getSheet(sheetName);
//
//        ArrayList<XSSFCell> fieldSegments = new ArrayList<>();
//        for (int i = 0; i >= 0; i++) {
//            try {
//                if(CustomExcelReader.getCellContentByIndex(sheet, fieldSegmentHeader.getRowIndex() + numberOfRowsFieldSegment + 1, fieldSegmentHeader.getColumnIndex()) == null){
//                    return fieldSegments;
//                }
//            } catch (Exception e) {
//                return fieldSegments;
//            }
//            if (CustomExcelReader.getCellContentByIndex(sheet, fieldSegmentHeader.getRowIndex() + numberOfRowsFieldSegment + 1, fieldSegmentHeader.getColumnIndex()).getCellType() == CellType.STRING) {
//                fieldSegments.add(CustomExcelReader.getCellContentByIndex(sheet, fieldSegmentHeader.getRowIndex() + 1 + numberOfRowsFieldSegment, fieldSegmentHeader.getColumnIndex()));
//                numberOfRowsFieldSegment = numberOfRowsFieldSegment + numberOfRowsConstant;
//            } else {
//                i = -1;
//                return fieldSegments;
//            }
//
//        }
//        return null;
//    }

//    public static Table removeTwoRowsFromFieldSegment(Table table) {
//        Table modifiedTable = table;
//
//        //Remove Adj$ and ACR YoY% rows from table
//        for (int i = 2; i < modifiedTable.rowCount(); i += 2) {
//            modifiedTable = modifiedTable.dropRows(i, i + 1);
//        }
//        return modifiedTable;
//    }

//    public static Table removeColumns(Table table, String columnNames) {
//        //Split column names into list using ";" as delimiter
//        String[] columnsArray = columnNames.split(";");
//
//        //For each column name in list, remove column from table
//        for (String column : columnsArray) {
//            table.removeColumns(column);
//        }
//        return table;
//    }

//    }




    //Removes 2 rows from fieldsegment for the ACR_MOM calculations
    private Table removeTwoRowsFromFieldSegment(Table table) {
        Table modifiedTable = table;

        //Remove Adj$ and ACR YoY% rows from table
        for (int i = 2; i < modifiedTable.rowCount(); i += 2) {
            modifiedTable = modifiedTable.dropRows(i, i + 1);
        }
        return modifiedTable;
    }

    //Removes columns with the specified names from a table
    public static Table removeColumns(Table table, String columnNames) {
        //Split column names into list using ";" as delimiter
        String[] columnsArray = columnNames.split(";");

        //For each column name in list, remove column from table
        for (String column : columnsArray) {
            table.removeColumns(column);
        }
        return table;
    }


    //Creates an arraylist of the field segments for the ACR_MOM calculations
    private ArrayList<XSSFCell> getArrayListFieldSegments(String workbookPath, String sheetName, XSSFCell fieldSegmentHeader, int numberOfRowsFieldSegment, int numberOfRowsConstant) throws IOException {
        //file, workbook, and sheet to connect to
        FileInputStream inputStream = new FileInputStream(workbookPath);
        /*Workbook wb = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = (XSSFSheet) wb.getSheet(sheetName);*/
        Workbook wb = WorkbookFactory.create(inputStream);
        Sheet sheet = wb.getSheet(sheetName);

        ArrayList<XSSFCell> fieldSegments = new ArrayList<>();
        for (int i = 0; i >= 0; i++) {
            try {
                if(CustomExcelReader.getCellContentByIndex((XSSFSheet) sheet, fieldSegmentHeader.getRowIndex() + numberOfRowsFieldSegment + 1, fieldSegmentHeader.getColumnIndex()) == null){
                    return fieldSegments;
                }
                if (CustomExcelReader.getCellContentByIndex((XSSFSheet) sheet, fieldSegmentHeader.getRowIndex() + numberOfRowsFieldSegment + 1, fieldSegmentHeader.getColumnIndex()).getCellType() == CellType.STRING) {
                    fieldSegments.add(CustomExcelReader.getCellContentByIndex((XSSFSheet) sheet, fieldSegmentHeader.getRowIndex() + 1 + numberOfRowsFieldSegment, fieldSegmentHeader.getColumnIndex()));
                    numberOfRowsFieldSegment = numberOfRowsFieldSegment + numberOfRowsConstant;
                } else {
                    i = -1;
                    return fieldSegments;
                }
                inputStream.close();
            } catch (Exception e) {
                return fieldSegments;
            }
        }
        return null;
    }

    //Write to excel cell using worksheet name, cell address, and input as parameters in the test case fow input workbook
    public Boolean writeToCell(Row tableRow) throws IOException {
        String workSheetName = tableRow.getString("Parameter 1");
        String cellAddress = tableRow.getString("Parameter 2");
        String input = tableRow.getString("Parameter 3");

        JacobBase write = new JacobBase();
        write.initializeExcel();
        write.setUp(1);
        write.getWorkbook(1);
        write.getWorksheet(workSheetName);
        write.assignSheet(1, workSheetName);
        write.writeCell(cellAddress, input);

        return true;
    }

    }

