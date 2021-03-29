package FunctionalTesting.ExtractData;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class WorkbookHandler {

    public static Workbook getWorkbookObject(String pathToExcelFile) throws IOException, InvalidFormatException {
        return WorkbookFactory.create(new File(pathToExcelFile));
    }

    //Returns the number of sheets in an Excel workbook
    public static int getNumberOfSheetsInWorkbook(Workbook workbook){
        return workbook.getNumberOfSheets();
    }

    //Creates an Excel workbook with the file path as a string input
    public static void createWorkBook(String workBookNameWithPath) throws IOException {
        Workbook wb = new XSSFWorkbook();
        File temp = new File(workBookNameWithPath);
        try{
            if (!temp.exists()){
                OutputStream fileOut = new FileOutputStream(workBookNameWithPath);
                wb.write(fileOut);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


}
