package RolesTesting.ExcelApplicationHandlers.ExcelHandlers;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import tech.tablesaw.io.xlsx.XlsxReader;

import java.io.IOException;

public class CustomExcelReader extends XlsxReader {
    public static XSSFCell getCellContent(XSSFSheet sheet, String cellAddress) throws IOException {
        CellReference ref = new CellReference(cellAddress);
        int rownr = ref.getRow();
        int colnr =ref.getCol();

        XSSFRow row = sheet.getRow(rownr);
        XSSFCell cell = row.getCell(colnr);
        return cell;

    }

    public static XSSFCell getCellContentByIndex(XSSFSheet sheet, int rownr, int colnr) throws IOException {
        XSSFRow row = sheet.getRow(rownr);
        XSSFCell cell = row.getCell(colnr);
        return cell;
    }

    public static int findRow(XSSFSheet sheet, String cellContent) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell.getCellType() == CellType.STRING) {
                    if (cell.getRichStringCellValue().getString().trim().equals(cellContent)) {
                        return row.getRowNum();
                    }
                }
            }
        }
        return 0;
    }

    public static int getRowNum(XSSFSheet sheet, String colLetter, String cellContent) throws IOException {
        CellReference ref = new CellReference(colLetter);
        int colnr =ref.getCol();
        int rownr;
        rownr = findRow(sheet, cellContent);
        XSSFCell cell = getCellContentByIndex(sheet, rownr, colnr);
        String cellCheck = cell.getStringCellValue();
        if (cellCheck.equals(cellContent)) {
            System.out.println("The row number is " + rownr);
            return rownr;
        } else {
            System.out.println("Issue with get row number");
            return 0;
        }
    }

    public static int getColNum(XSSFSheet sheet, int rowIndex, String cellContent) {
        Row row = sheet.getRow(rowIndex);
        for (Cell cell : row) {
            if (cell.getCellType() == CellType.STRING) {
                if (cell.getRichStringCellValue().getString().trim().equals(cellContent)) {
                    return cell.getColumnIndex();
                }


            }

        }
        return 0;
    }

    public static XSSFCell getCellByString(XSSFSheet sheet, String cellContent) throws IOException {
        return getCellContentByIndex(sheet, findRow(sheet, cellContent), getColNum(sheet, findRow(sheet, cellContent), cellContent));
    }

    public static void deleteColumn( Sheet sheet, int columnToDelete ){
        int maxColumn = 0;
        for ( int r=0; r < sheet.getLastRowNum()+1; r++ ){
            Row row = sheet.getRow( r );

            // if no row exists here; then nothing to do; next!
            if ( row == null )
                continue;

            // if the row doesn't have this many columns then we are good; next!
            int lastColumn = row.getLastCellNum();
            if ( lastColumn > maxColumn )
                maxColumn = lastColumn;

            if ( lastColumn < columnToDelete )
                continue;

            for ( int x=columnToDelete+1; x < lastColumn + 1; x++ ){
                Cell oldCell    = row.getCell(x-1);
                if ( oldCell != null )
                    row.removeCell( oldCell );

                Cell nextCell   = row.getCell( x );
                if ( nextCell != null ){
                    Cell newCell    = row.createCell( x-1, nextCell.getCellType() );
                    cloneCell(newCell, nextCell);
                }
            }
        }


        // Adjust the column widths
        for ( int c=0; c < maxColumn; c++ ){
            sheet.setColumnWidth( c, sheet.getColumnWidth(c+1) );
        }
    }

    private static void cloneCell( Cell cNew, Cell cOld ){
        cNew.setCellComment( cOld.getCellComment() );
        cNew.setCellStyle( cOld.getCellStyle() );

        switch (cNew.getCellType() ){
            case BOOLEAN:{
                cNew.setCellValue( cOld.getBooleanCellValue() );
                break;
            }
            case NUMERIC:{
                cNew.setCellValue( cOld.getNumericCellValue() );
                break;
            }
            case STRING:{
                cNew.setCellValue( cOld.getStringCellValue() );
                break;
            }
            case ERROR:{
                cNew.setCellValue( cOld.getErrorCellValue() );
                break;
            }
            case FORMULA:{
                cNew.setCellFormula( cOld.getCellFormula() );
                break;
            }
        }

    }
}

