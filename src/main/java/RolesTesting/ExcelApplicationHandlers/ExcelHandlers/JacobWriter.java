package RolesTesting.ExcelApplicationHandlers.ExcelHandlers;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class JacobWriter {

    private static ActiveXComponent xl;
    private static Object workbooks = null;
    private static Object workbook = null;
    private static Object sheet = null;
    private static String filename = null;
    private static boolean readonly = false;


    public static void main(String[] args) {

        String strInputDoc = "C:\\Users\\v-mcoleb\\Documents\\jacobTest.xlsx";  // file to be opened.

        ComThread.InitSTA();

        ActiveXComponent xl = new ActiveXComponent("Excel.Application"); // Instance of application object created.

        try {
// Get Excel application object properties in 2 ways:
            System.out.println("version=" + xl.getProperty("Version"));
            System.out.println("version=" + Dispatch.get(xl, "Version"));

// Make Excel instance visible:
            Dispatch.put(xl, "Visible", new Variant(true));

// Open XLS file, get the workbooks object required for access:
            //get "Workbooks" property from "Application" object
            Dispatch workbooks = xl.getProperty("Workbooks").toDispatch();
//call method "Open" with filepath param on "Workbooks" object and save "Workbook" object
            Dispatch workbook = Dispatch.call(workbooks, "Open", new Variant(strInputDoc)).toDispatch();
//get "Worksheets" property from "Workbook" object
            //Dispatch sheets = Dispatch.get(workbook, "Worksheets").toDispatch();
//Call method "Select" on "Worksheets" object with "Sheet2" param
            //Dispatch.call(sheets, "Select", "Write").toDispatch();

            //Dispatch sheets = Dispatch.get(workbook, "ActiveSheet").toDispatch();

            Dispatch sheets = Dispatch.call(workbook, "sheets", new Variant[]{new Variant("Write")}).toDispatch();



//probably again save "Worksheet" object and continue same way

// put in a value in cell A22 and place a a formula in cell A23:
            //Dispatch a22 = Dispatch.invoke(sheets, "Range", Dispatch.Get, new Object[] { "A22" }, new int[1]).toDispatch();
            Dispatch a22 = Dispatch.invoke(sheets, "Range", Dispatch.Get, new Object[] { "A22" }, new int[1]).toDispatch();
            //Dispatch a23 = Dispatch.invoke(sheets, "Range", Dispatch.Get, new Object[] { "A23" }, new int[1]).toDispatch();
            Dispatch a23 = Dispatch.invoke(sheets, "Range", Dispatch.Get, new Object[] { "A23" }, new int[1]).toDispatch();

            Dispatch.put(a22, "Value", "123.456");
            Dispatch.put(a23, "Formula", "=A22*2");

// Get values from cells A1 and A2
            System.out.println("a22 from excel:" + Dispatch.get(a22, "Value"));
            System.out.println("a23 from excel:" + Dispatch.get(a23, "Value"));

// Save the open workbook as "C:\jacob-1.16-M1\Test1.xls" file:
            Dispatch.call(workbook, "SaveAs", new Variant(strInputDoc),new Variant("1"));

// Close the Excel workbook without saving:
//            Variant saveYesNo = new Variant(false);
//            Dispatch.call(workbook, "Close", saveYesNo);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

// Quit Excel:
 xl.invoke("Quit", new Variant[] {});
            ComThread.Release();
        }

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
            Object workbook = Dispatch.get((Dispatch) workbooks, "Add")
                    .toDispatch();
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
}


