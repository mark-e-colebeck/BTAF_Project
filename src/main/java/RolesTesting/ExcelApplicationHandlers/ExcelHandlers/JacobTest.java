package RolesTesting.ExcelApplicationHandlers.ExcelHandlers;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import mmarquee.automation.AutomationException;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.pattern.PatternNotFoundException;

import java.awt.*;

public class JacobTest {

    public static ActiveXComponent xl;
    public static Object workbooks = null;
    public static Object workbook = null;
    public static Object sheet = null;
    public static String filename = null;
    public static Object app = null;
    public static String filePath = null;
    public static Object worksheet = null;
    public static boolean readonly = false;
    public static Object property = null;
    public static Object lastRow = null;
    public static int workbookIndex;

    public static Object getWorkbook() {
        return workbook;
    }

    public static void setWorkbook(Object workbook) {
        JacobTest.workbook = workbook;
    }

    public static Object getWorkbooks() {
        return workbooks;
    }

    public static void setWorkbooks(Object workbooks) {
        JacobTest.workbooks = workbooks;
    }

    public static Object lastColumn = null;

    public static ActiveXComponent getXl() {
        return xl;
    }

    public static void setXl(ActiveXComponent xl) {
        JacobTest.xl = xl;
    }

    public static void main(String[] args) throws AWTException, PatternNotFoundException, AutomationException {

        JacobBase test = new JacobBase();
        test.initializeExcel();
        test.setUp(1);
        test.getWorkbook(1);
        test.getWorksheet("test");
        test.assignSheet(1, "test");
        test.writeCell("a8" ,"test");
        test.jacobSave(UIAutomation.getInstance());






      //  Date now = new Date();
      //  Long nowtime = now.getTime();
//excel directory needs to be created in advance
//        String path = "C:\\Users\\v-mcoleb\\Documents\\jacobTest.xlsx";
//        writeTest(path);
        // createAndSave(path);
       // printExcel(path);
    }

    // read the value
    private static String GetValue(String position) {
        Object cell = Dispatch.invoke((Dispatch) sheet, "Range", Dispatch.Get,
                new Object[] { position }, new int[1]).toDispatch();
        String value = Dispatch.get((Dispatch) cell, "Value").toString();

        return value;
    }

    private static void createAndSave(String path){
        ActiveXComponent xl = new ActiveXComponent("Excel.Application");
        Object xlo = xl.getObject();
        try {
            System.out.println("version=" + xl.getProperty("Version"));
            System.out.println("version="
                    + Dispatch.get((Dispatch) xlo, "Version"));
            xl.setProperty("Visible", new Variant(true));
            Object workbooks = xl.getProperty("Workbooks").toDispatch();
            Object workbook = Dispatch.get((Dispatch) workbooks, "Add").toDispatch();

            Object sheet = Dispatch.get((Dispatch) workbook, "ActiveSheet")
                    .toDispatch();
            Object a1 = Dispatch.invoke((Dispatch) sheet, "Range",
                    Dispatch.Get, new Object[] { "A1" }, new int[1])
                    .toDispatch();
            Object a2 = Dispatch.invoke((Dispatch) sheet, "Range",
                    Dispatch.Get, new Object[] { "A2" }, new int[1])
                    .toDispatch();
            Dispatch.put((Dispatch) a1, "Value", "123.456");
            Dispatch.put((Dispatch) a2, "Formula", "=A1*2");
            Dispatch.call((Dispatch) workbook, "SaveAs", path);

            System.out.println("a1 from excel:"
                    + Dispatch.get((Dispatch) a1, "Value"));
            System.out.println("a2 from excel:"
                    + Dispatch.get((Dispatch) a2, "Value"));
            // Variant f = new Variant(false);
            // Dispatch.call((Dispatch) workbooks, "Close", f);
            // Dispatch.call((Dispatch) workbook, "Close");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            xl.invoke("Quit", new Variant[] {});
        }
    }

    private static void writeTest(String path){
        ActiveXComponent xl = new ActiveXComponent("Excel.Application");
        Object xlo = xl.getObject();
        try {
            System.out.println("version=" + xl.getProperty("Version"));
            System.out.println("version="
                    + Dispatch.get((Dispatch) xlo, "Version"));
            xl.setProperty("Visible", new Variant(true));
            Object workbooks = xl.getProperty("Workbooks").toDispatch();
            Object workbook = Dispatch.get((Dispatch) workbooks, "Add").toDispatch();

            Object sheet = Dispatch.get((Dispatch) workbook, "ActiveSheet")
                    .toDispatch();
            Object a1 = Dispatch.invoke((Dispatch) sheet, "Range",
                    Dispatch.Get, new Object[] { "A1" }, new int[1])
                    .toDispatch();
            Object a2 = Dispatch.invoke((Dispatch) sheet, "Range",
                    Dispatch.Get, new Object[] { "A2" }, new int[1])
                    .toDispatch();
            Dispatch.put((Dispatch) a1, "Value", "123.456");
            Dispatch.put((Dispatch) a2, "Formula", "=A1*2");
            Dispatch.call((Dispatch) workbook, "SaveAs", path);

            System.out.println("a1 from excel:"
                    + Dispatch.get((Dispatch) a1, "Value"));
            System.out.println("a2 from excel:"
                    + Dispatch.get((Dispatch) a2, "Value"));
            // Variant f = new Variant(false);
            // Dispatch.call((Dispatch) workbooks, "Close", f);
            // Dispatch.call((Dispatch) workbook, "Close");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            xl.invoke("Quit", new Variant[] {});
        }
    }

    private static boolean printExcel(String path){
        ComThread.InitSTA();
        ActiveXComponent xl = new ActiveXComponent("Excel.Application");
        try {
            // System.out.println("version=" + xl.getProperty("Version"));
            Dispatch.put(xl, "Visible", new Variant(false));
            Dispatch workbooks = xl.getProperty("Workbooks").toDispatch();
            Dispatch excel = Dispatch.call(workbooks, "Open", path).toDispatch();
            Dispatch.get(excel, "PrintOut");
            System.out.println("printOver");
            Dispatch.call(excel, "Close");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            xl.invoke("Quit", new Variant[] {});
            ComThread.Release();
        }
        return false;
    }

//    void initializeExcel() {
//        ComThread.InitSTA();
//        this.app = ActiveXComponent.connectToActiveInstance("Excel.Application");
//        if (this.app == null) {
//            this.app = new ActiveXComponent("Excel.Application");
//        }
//
//        app.setProperty("visible", true);
//    }
//
//    public void setUp(String filePath) {
//        if (!filePath.equalsIgnoreCase(this.filePath)) {
//            this.initializeExcel();
//            this.workbooks = this.app.invokeGetComponent("workbooks");
//            this.workbook = this.workbooks.invokeGetComponent("open", new Variant[]{new Variant(filePath)});
//            this.worksheet = this.workbook.invokeGetComponent("Activesheet");
//            this.lastRow = this.getLastRow();
//            this.lastColumn = this.getLastColumn();
//            this.workbookIndex = this.getWorkbookIndex(filePath);
//            this.filePath = filePath;
//        }
//
//    }
//
//
//    public void setActiveSheet(String sheetName) {
//        this.getWorksheet(sheetName).invoke("Activate");
//        this.assignSheet(sheetName);
//    }
//
//    private ActiveXComponent getWorksheet(String sheetName) {
//        return this.workbook.invokeGetComponent("sheets", new Variant[]{new Variant(sheetName)});
//    }
//
//    public void assignSheet(String worksheetName) {
//        if (!worksheetName.equalsIgnoreCase(this.sheetName)) {
//            this.worksheet = this.getWorksheet(worksheetName);
//            this.lastRow = this.getLastRow();
//            this.lastColumn = this.getLastColumn();
//            this.sheetName = worksheetName;
//        }
//
//    }
//
//

}
