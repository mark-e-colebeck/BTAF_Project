package FunctionalTesting.ExtractData;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.xlsx.XlsxReadOptions;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class TablesawReader {
    File file;
    String sheetName;

    public TablesawReader(){
    }

    public TablesawReader(File file, String sheetName){
        this.file = file;
        this.sheetName = sheetName;
    }

    private static final TablesawReader INSTANCE = new TablesawReader();


    //Creates a Table using the specified range
    public Table read(XlsxReadOptions options, int startRow, int endRow, int startColumn, int endColumn, boolean isKeyWordSheet) throws IOException, InvalidFormatException {
        List<Table> tables = readMultiple(options, false, startRow, endRow, startColumn, endColumn, isKeyWordSheet);
        // since no specific sheetIndex asked, return first table
        return tables.stream()
                .filter(t -> t != null)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No tables found."));
    }


    //Returns the file input stream
    private InputStream getInputStream(XlsxReadOptions options, byte[] bytes)
            throws FileNotFoundException {
        if (bytes != null) {
            return new ByteArrayInputStream(bytes);
        }
        if (options.source().inputStream() != null) {
            return options.source().inputStream();
        }
        return new FileInputStream(options.source().file());
    }

    //Creates multiple Tables using the specified range
    protected List<Table> readMultiple(XlsxReadOptions options, boolean includeNulls, int startRow, int endRow, int startColumn, int endColumn, boolean isKeyWordSheet)
            throws IOException, InvalidFormatException {
        byte[] bytes = null;
        InputStream input = getInputStream(options, bytes);
        List<Table> tables = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(input)) {
            for (Sheet sheet : workbook) {
                if (getNameAndIndexMap(file).get(sheet.getSheetName())!=null){
                    if (getNameAndIndexMap(file).get(sheet.getSheetName()).equals(options.sheetIndex())) {
                        TablesawReader.TableRange tableArea = new TableRange(startRow, endRow, startColumn, endColumn);
                        if (tableArea != null) {
                            Table table = createTable(sheet, tableArea, options, isKeyWordSheet);
                            tables.add(table);
                        } else if (includeNulls) {
                            tables.add(null);
                        }
                    }
                }
            }
            return tables;
        } finally {
            if (options.source().reader() == null) {
                // if we get a reader back from options it means the client opened it, so let
                // the client close it
                // if it's null, we close it here.
                input.close();
            }
        }
    }


    private void getRowRange(int startColumn, int endColumn, Sheet sheet) {
        for (int i = 0; i < sheet.getLastRowNum(); i++){
            Row row = sheet.getRow(i);
            if (row != null) {
                for (int j = startColumn; j < endColumn; j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        ExtendedColor colour = (ExtendedColor) cell.getCellStyle().getFillForegroundColorColor();
                        if(colour != null && colour.getARGBHex().equals("FF4472C4") && cell.getStringCellValue().equals("RptgReprt Subsegment")) {
                        } else {
                            continue;
                        }
                    }
                }
            }
        }
    }

    //Returns a table created from an Excel sheet
    private Table createTable(Sheet sheet, TablesawReader.TableRange tableArea, XlsxReadOptions options, boolean isKeyWordSheet) {
        // assume header row if all cells are of type String
        Row row = sheet.getRow(tableArea.startRow);

        List<String> headerNames = new ArrayList<>();
        for(int i= tableArea.startColumn;i<=tableArea.endColumn;i++)
        {
            if(row.getCell(i) != null)
            headerNames.add(row.getCell(i).getStringCellValue());
        }
        if (headerNames.size() == tableArea.endColumn - tableArea.startColumn + 1) {
            tableArea.startRow++;
        } else {
            headerNames.clear();
            for (int col = tableArea.startColumn; col <= tableArea.endColumn; col++) {
                headerNames.add("col" + col);
            }
        }
        Table table = Table.create(options.tableName() + "#" + sheet.getSheetName());
        List<Column<?>> columns = new ArrayList<>(Collections.nCopies(headerNames.size(), null));
        for (int rowNum = tableArea.startRow; rowNum <= tableArea.endRow; rowNum++) {
            row = sheet.getRow(rowNum);
            if(row != null) {
                for (int colNum = 0; colNum < headerNames.size(); colNum++) {
                    Cell cell = row.getCell(colNum + tableArea.startColumn);

                    Column<?> column = columns.get(colNum);

                    if (cell != null) {
                        ExtendedColor colour = (ExtendedColor) cell.getCellStyle().getFillForegroundColorColor();
                        if(colour != null && (colour.getARGBHex().equals("FF4472C4") || colour.getARGBHex().equals("FF305496")) && cell.getStringCellValue().equals("")) {
                            Cell previousCell = sheet.getRow(cell.getRowIndex() - 1).getCell(cell.getColumnIndex());
                            if(previousCell != null) {
                                cell.setCellValue(previousCell.getStringCellValue());
                            }
                        }
                        else if(isKeyWordSheet) {
                            if(cell.getStringCellValue().equals("") ) {
                                Cell previousCell = sheet.getRow(cell.getRowIndex() - 1).getCell(cell.getColumnIndex());
                                if(previousCell != null && previousCell.getCellType().equals(CellType.STRING) && (previousCell.getStringCellValue().equals("N") ||
                                        previousCell.getStringCellValue().equals("Y"))) {
                                    cell.setCellValue(previousCell.getStringCellValue());
                                }
                            }
                        }
                    }
                    if (cell != null) {
                        if (column == null) {
                            column = createColumn(headerNames.get(colNum), cell);
                            columns.set(colNum, column);
                            while (column.size() < rowNum - tableArea.startRow) {
                                column.appendMissing();
                            }
                        }
                        Column<?> altColumn = appendValue(column, cell);
                        if (altColumn != null && altColumn != column) {
                            column = altColumn;
                            columns.set(colNum, column);
                        }
                    }
                    if (column != null) {
                        while (column.size() <= rowNum - tableArea.startRow) {
                            column.appendMissing();
                        }
                    }
                }
            }
        }
        columns.removeAll(Collections.singleton(null));
        table.addColumns(columns.toArray(new Column<?>[columns.size()]));
        return table;
    }


    //Returns the sheet name and index from an Excel file
    public static LinkedHashMap<String, Integer> getNameAndIndexMap(File file) throws IOException, InvalidFormatException {
        LinkedHashMap<String, Integer> sheetNameAndIndexMap = new LinkedHashMap<>();
        Workbook workbook = new XSSFWorkbook(file);

        for(int i=0; i<workbook.getNumberOfSheets(); i++){
            sheetNameAndIndexMap.putIfAbsent(workbook.getSheetName(i), i+1);
        }
        return sheetNameAndIndexMap;
    }

    private static class TableRange {
        private int startRow, endRow, startColumn, endColumn;

        TableRange(int startRow, int endRow, int startColumn, int endColumn) {
            this.startRow = startRow;
            this.endRow = endRow;
            this.startColumn = startColumn;
            this.endColumn = endColumn;
        }
    }

    //Creates a column
    private Column<?> createColumn(String name, Cell cell) {
        Column<?> column;
        ColumnType columnType = getColumnType(cell);
        if (columnType == null) {
            columnType = ColumnType.STRING;
        }
        column = columnType.create(name);
        return column;
    }

    //Returns the column type
    private ColumnType getColumnType(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return ColumnType.STRING;
            case NUMERIC:
                return DateUtil.isCellDateFormatted(cell) ? ColumnType.LOCAL_DATE_TIME : ColumnType.INTEGER;
            case BOOLEAN:
                return ColumnType.BOOLEAN;
            default:
                break;
        }
        return null;
    }

    //Appends a cell to a column
    private Column<?> appendValue(Column<?> column, Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                column.appendCell(cell.getRichStringCellValue().getString());
                return null;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    // This will return inconsistent results across time zones, but that matches Excel's
                    // behavior
                    LocalDateTime localDate =
                            date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    column.appendCell(localDate.toString());
                    return null;
                } else {
                    double num = cell.getNumericCellValue();
                    if (column.type() == ColumnType.INTEGER) {
                        Column<Integer> intColumn = (Column<Integer>) column;
                        if ((int) num == num) {
                            intColumn.append((int) num);
                            return null;
                        } else if ((long) num == num) {
                            Column<Long> altColumn = LongColumn.create(column.name(), column.size());
                            altColumn = intColumn.mapInto(s -> (long) s, altColumn);
                            altColumn.append((long) num);
                            return altColumn;
                        } else {
                            Column<Double> altColumn = DoubleColumn.create(column.name(), column.size());
                            altColumn = intColumn.mapInto(s -> (double) s, altColumn);
                            altColumn.append(num);
                            return altColumn;
                        }
                    } else if (column.type() == ColumnType.LONG) {
                        Column<Long> longColumn = (Column<Long>) column;
                        if ((long) num == num) {
                            longColumn.append((long) num);
                            return null;
                        } else {
                            Column<Double> altColumn = DoubleColumn.create(column.name(), column.size());
                            altColumn = longColumn.mapInto(s -> (double) s, altColumn);
                            altColumn.append(num);
                            return altColumn;
                        }
                    } else if (column.type() == ColumnType.DOUBLE) {
                        Column<Double> doubleColumn = (Column<Double>) column;
                        doubleColumn.append(num);
                        return doubleColumn;
                    }
                    else if (column.type() == ColumnType.STRING){
                        Column<String> stringColumn = (Column<String>) column;
                        stringColumn.append(String.valueOf(cell.getNumericCellValue()));
                        return stringColumn;
                    }
                }
                break;
            case BOOLEAN:
                if (column.type() == ColumnType.BOOLEAN) {
                    Column<Boolean> booleanColumn = (Column<Boolean>) column;
                    booleanColumn.append(cell.getBooleanCellValue());
                    return null;
                }
            default:
                break;
        }
        return null;
    }

}
