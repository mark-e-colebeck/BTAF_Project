package RolesTesting.DependencyCustomHandlers;

import RolesTesting.ExcelApplicationHandlers.ExcelHandlers.SheetHandler;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.DataReader;
import tech.tablesaw.io.ReaderRegistry;
import tech.tablesaw.io.xlsx.XlsxReadOptions;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class XlsxReaderTablesaw {

    String workBookName;
    String sheetName;

    public XlsxReaderTablesaw(){
    }

    public XlsxReaderTablesaw(String workBookPath, String sheetName){
        this.workBookName = workBookPath;
        this.sheetName = sheetName;
    }

    private static final XlsxReaderTablesaw INSTANCE = new XlsxReaderTablesaw();

    public String getWorkBookName() {
        return workBookName;
    }

    public void setWorkBookName(String workBookName) {
        this.workBookName = workBookName;
    }

//    static {
//        register(Table.defaultReaderRegistry);
//    }

    public static void register(ReaderRegistry registry) {
        registry.registerExtension("xlsx", (DataReader<?>) INSTANCE);
        registry.registerMimeType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", (DataReader<?>) INSTANCE);
        registry.registerOptions(XlsxReadOptions.class, (DataReader<?>) INSTANCE);
    }

    public Table read(XlsxReadOptions options, int startRow, int endRow, int startColumn, int endColumn) throws IOException {
        List<Table> tables = readMultiple(options, false, startRow, endRow, startColumn, endColumn);
        // since no specific sheetIndex asked, return first table
        return tables.stream()
                .filter(t -> t != null)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No tables found."));
    }


    /**
     * Read at most a table from every sheet.
     *
     * @param includeNulls include nulls for sheets without a table
     * @return a list of tables, at most one for every sheet
     */
    protected List<Table> readMultiple(XlsxReadOptions options, boolean includeNulls, int startRow, int endRow, int startColumn, int endColumn)
            throws IOException {
        byte[] bytes = null;
        InputStream input = getInputStream(options, bytes);
        List<Table> tables = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(input)) {
            for (Sheet sheet : workbook) {
                if (SheetHandler.getNameAndIndexMap(workBookName).get(sheet.getSheetName())!=null){
                    if (SheetHandler.getNameAndIndexMap(workBookName).get(sheet.getSheetName()).equals(options.sheetIndex())){
                        XlsxReaderTablesaw.TableRange tableArea = new TableRange(startRow, endRow, startColumn, endColumn);
//                        XlsxReaderTablesaw.TableRange tableArea = findTableArea(sheet);
                        if (tableArea != null) {
                            Table table = createTable(sheet, tableArea, options);
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

    private Boolean isBlank(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                if (cell.getRichStringCellValue().length() > 0) {
                    return false;
                }
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)
                        ? cell.getDateCellValue() != null
                        : cell.getNumericCellValue() != 0) {
                    return false;
                }
                break;
            case BOOLEAN:
                if (cell.getBooleanCellValue()) {
                    return false;
                }
                break;
            case BLANK:
                return true;
            default:
                break;
        }
        return null;
    }

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

    private static class TableRange {
        private int startRow, endRow, startColumn, endColumn;

        TableRange(int startRow, int endRow, int startColumn, int endColumn) {
            this.startRow = startRow;
            this.endRow = endRow;
            this.startColumn = startColumn;
            this.endColumn = endColumn;
        }
    }

    private XlsxReaderTablesaw.TableRange findTableArea(Sheet sheet) {
        // find first row and column with contents
        int row1 = -1;
        int row2 = -1;
        XlsxReaderTablesaw.TableRange lastRowArea = null;
        for (Row row : sheet) {
            XlsxReaderTablesaw.TableRange rowArea = findRowArea(row);
            if (lastRowArea == null && rowArea != null) {
                if (row1 < 0) {
                    lastRowArea = rowArea;
                    row1 = row.getRowNum();
                    row2 = row1;
                }
            } else if (lastRowArea != null && rowArea == null) {
                if (row2 > row1) {
                    break;
                } else {
                    row1 = -1;
                }
            } else if (lastRowArea == null && rowArea == null) {
                row1 = -1;
            } else if (rowArea.startColumn < lastRowArea.startColumn
                    || rowArea.endColumn > lastRowArea.endColumn) {
                lastRowArea = null;
                row2 = -1;
            } else {
                row2 = row.getRowNum();
            }
        }
        return row1 >= 0 && lastRowArea != null
                ? new XlsxReaderTablesaw.TableRange(row1, row2, lastRowArea.startColumn, lastRowArea.endColumn)
                : null;
    }

    private XlsxReaderTablesaw.TableRange findRowArea(Row row) {
        int col1 = -1;
        int col2 = -1;
        for (Cell cell : row) {
            Boolean blank = isBlank(cell);
            if (col1 < 0 && Boolean.FALSE.equals(blank)) {
                col1 = cell.getColumnIndex();
                col2 = col1;
            } else if (col1 >= 0 && col2 >= col1) {
                if (Boolean.FALSE.equals(blank)) {
                    col2 = cell.getColumnIndex();
                } else if (Boolean.TRUE.equals(blank)) {
                    break;
                }
            }
        }
        return col1 >= 0 && col2 >= col1 ? new XlsxReaderTablesaw.TableRange(0, 0, col1, col2) : null;
    }

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

    private Table createTable(Sheet sheet, XlsxReaderTablesaw.TableRange tableArea, XlsxReadOptions options) {
        // assume header row if all cells are of type String
        Row row = sheet.getRow(tableArea.startRow);
        List<String> headerNames = new ArrayList<>();
        for (Cell cell : row) {
            if (cell.getCellType() == CellType.STRING) {
                headerNames.add(cell.getRichStringCellValue().getString());
            } else {
                break;
            }
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
            for (int colNum = 0; colNum < headerNames.size(); colNum++) {
                Cell cell =
                        row.getCell(colNum + tableArea.startColumn);
                Column<?> column = columns.get(colNum);
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
        columns.removeAll(Collections.singleton(null));
        table.addColumns(columns.toArray(new Column<?>[columns.size()]));
        return table;
    }

    @SuppressWarnings("unchecked")
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

    private Column<?> createColumn(String name, Cell cell) {
        Column<?> column;
        ColumnType columnType = getColumnType(cell);
        if (columnType == null) {
            columnType = ColumnType.STRING;
        }
        column = columnType.create(name);
        return column;
    }

}
