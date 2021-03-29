package RolesTesting.Roles_Based.Excel2Json;

import RolesTesting.Roles_Based.PoJos.RoleBasedRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Excel2JsonConverter {
    private static Logger logger = LoggerFactory.getLogger(Excel2JsonConverter.class);
//    List<RoleBasedRow> roleBasedRows = readRoleBasedSheet("", "");
//    String toJsonString = convertToJsonString(roleBasedRows);


    public Excel2JsonConverter() {
    }

    public static List<RoleBasedRow> getRoleBasedRows(File workBook, String sheetName) throws IOException {
        FileInputStream excelFile = new FileInputStream(workBook);
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheet(sheetName);
        Iterator<Row> rows = sheet.iterator();
        List<RoleBasedRow> roleRows = new ArrayList<>();

        int rowNumber = 0;
        while (rows.hasNext()) {
            Row currentRow = rows.next();
            // skip header
            if (rowNumber == 0) {
                rowNumber++;
                continue;
            }
            Iterator<Cell> cellsInRow = currentRow.iterator();
            RoleBasedRow roleBasedRow = new RoleBasedRow();
            int cellIndex = 0;
            while (cellsInRow.hasNext()) {
                Cell currentCell = cellsInRow.next();
                if (cellIndex == 0) {
                    roleBasedRow.setTest_Case_Name(currentCell.getStringCellValue());
                } else if (cellIndex == 1) {
                    roleBasedRow.setTest_Case_Id(currentCell.getStringCellValue());
                } else if (cellIndex == 2) {
                    roleBasedRow.setTest_Case_Description(currentCell.getStringCellValue());
                } else if (cellIndex == 3) {
                    roleBasedRow.setTest_Run_Flag(currentCell.getStringCellValue());
                } else if (cellIndex == 4) {
                    roleBasedRow.setConnection(currentCell.getStringCellValue());
                } else if (cellIndex == 5) {
                    roleBasedRow.setUsername(currentCell.getStringCellValue());
                } else if (cellIndex == 6) {
                    roleBasedRow.setPassword(currentCell.getStringCellValue());
                } else if (cellIndex == 7) {
                    roleBasedRow.setRole_Folder(currentCell.getStringCellValue());
                } else if (cellIndex == 8) {
                    roleBasedRow.setRole_Workbook(currentCell.getStringCellValue());
                } else if (cellIndex == 9) {
                    roleBasedRow.setRefresh(currentCell.getStringCellValue());
                } else if (cellIndex == 10) {
                    roleBasedRow.setCalculate(currentCell.getStringCellValue());
                } else if (cellIndex == 11) {
                    roleBasedRow.setSave(currentCell.getStringCellValue());
                } else if (cellIndex == 12) {
                    roleBasedRow.setSubmit(currentCell.getStringCellValue());
                }else if (cellIndex == 13){
                    roleBasedRow.setListOfRegions(currentCell.getStringCellValue());
                }
                cellIndex++;
            }
            roleRows.add(roleBasedRow);
        }
        workbook.close();
        return roleRows;
    }
}
