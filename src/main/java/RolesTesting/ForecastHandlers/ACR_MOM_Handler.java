package RolesTesting.ForecastHandlers;

import RolesTesting.Constants.Constant;
import RolesTesting.ExcelApplicationHandlers.ExcelHandlers.SheetHandler;
import Model.InputForm.AzureForecast.ACR_MOM;
import RolesTesting.Util.ConfigProperties;
import mmarquee.automation.AutomationException;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.util.CellAddress;
//import org.junit.Assert;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ACR_MOM_Handler {
    private static Logger logger = Logger.getLogger(ACR_MOM_Handler.class);

    public ACR_MOM_Handler() throws Exception {
    }

    public static List<ACR_MOM> getFieldSegmentsFromTable(String workbookPath, String sheetName, String range) throws Exception {
        Table table = SheetHandler.getTableInCellRangeFromSheet(workbookPath, sheetName, range);
        LinkedHashMap<Integer, String> fieldSegmentAndIndex = new LinkedHashMap<>();
        List<ACR_MOM> acr_momList = new ArrayList<>();
       table.stream().forEach(row -> {
           if (!row.getText("Field Segment").equals("")){
               fieldSegmentAndIndex.put(row.getRowNumber(), row.getText("Field Segment"));
           }
       });

        Table tempTable = (Table) table.removeColumns(new int[]{0});
        List<Integer> indexes = new ArrayList<>(fieldSegmentAndIndex.keySet());
        indexes.forEach(a -> {
            ACR_MOM acr_mom = new ACR_MOM();
            acr_mom.setFieldSegment(fieldSegmentAndIndex.get(a));
            acr_mom.setACR_Dol(tempTable.row(a));
            acr_mom.setMOM_Per(tempTable.row(a+1));
            acr_mom.setAdj_Dol(tempTable.row(a+2));
            acr_mom.setACR_YOY_Per(tempTable.row(a+3));
            acr_momList.add(acr_mom);
        });
        return acr_momList;
    }

    public static boolean validateMoMBasedForecast(String workbookPath, String sheetName, String range) throws Exception {
        List<ACR_MOM> acr_momList = getFieldSegmentsFromTable(workbookPath, sheetName, range);
        AtomicBoolean answer = new AtomicBoolean(true);
        acr_momList.forEach(acr_mom -> {
            Row row = acr_mom.getACR_Dol();
            row.columnNames().remove("Fiscal year / period");
            for (int i=0; i<=row.columnNames().size()-1; i++){
                if (!row.columnNames().get(i).equals("Fiscal year / period")){
                    double mom_per = ((row.getDouble(row.columnNames().get(i+1))/31)  - (row.getDouble(row.columnNames().get(i))/31))
                            / (row.getDouble(row.columnNames().get(i))/31);
                    if (mom_per == acr_mom.getMOM_Per().getDouble(row.columnNames().get(i+1))){
                        answer.set(true);
                    }
                    else answer.set(false);
                }
            }
        });
        return answer.get();
    }

//    public static boolean validateACR_MOM(Row row, String workbookName){
//
//    }

    public static void inputDataInCell(Row row, String workBookName) throws IOException, InvalidFormatException, AutomationException, InterruptedException {
        String workSheetName = row.getString(ConfigProperties.getProperty("testCaseFlow.parameter1"));
        SheetHandler.writeToCell(workBookName,
                workSheetName,
                Arrays.asList(row.getString(ConfigProperties.getProperty("testCaseFlow.parameter2")).split(";")));
    }

    private static List<Integer> getCellAddressWRTBase(String baseRange, String newAddress){
        int newR0 = new CellAddress(baseRange.split(":")[0]).getRow() + 1;
        int newC0 = new CellAddress(baseRange.split(":")[0]).getColumn();

        int currentR = new CellAddress(newAddress).getRow();
        int currentC = new CellAddress(newAddress).getColumn();
        return Arrays.asList(currentR-newR0, currentC-newC0);
    }

    private static List<Integer> getColumnRangeFromBaseForSub(String baseRange, String subTableRange){
        int bigTableColReference = new CellAddress(baseRange.split(":")[0]).getColumn();
        int smallTableColReference = new CellAddress(subTableRange.split(":")[0]).getColumn();
        int startColumn = smallTableColReference - bigTableColReference;
        int endColumn = new CellAddress(subTableRange.split(":")[1]).getColumn() -
                new CellAddress(baseRange.split(":")[0]).getColumn();
        return IntStream.rangeClosed(startColumn, endColumn)
                .boxed()
                .collect(Collectors.toList());
    }

    private static List<Integer> getStartAndEndRowFromBaseForSub(String baseRange, String subTableRange){
        int bigTableRowReference = new CellAddress(baseRange.split(":")[0]).getRow() + 1;
        int smallTableRowStart = new CellAddress(subTableRange.split(":")[0]).getRow();
        int startRow = smallTableRowStart - bigTableRowReference;
        int endRow = new CellAddress(subTableRange.split(":")[1]).getRow() -
                bigTableRowReference;
        return IntStream.rangeClosed(startRow, endRow)
                .boxed()
                .collect(Collectors.toList());
    }

    private static Column getFirstColumnForACR_MoM(String baseRange, String subTableRange, Table table){
        int[]  rowsRange = getStartAndEndRowFromBaseForSub(baseRange, subTableRange)
                .stream().mapToInt(i->i).toArray();
        int bigTableColReference = new CellAddress(baseRange.split(":")[0]).getColumn();
        int smallTableColReference = new CellAddress(subTableRange.split(":")[0]).getColumn();
        return table.rows(rowsRange).column(smallTableColReference - bigTableColReference - 1);
    }

    public static Table getSubTableFromBigTableUsingRange(String baseRange, String subTableRange, Table baseTable){
        int[]  rowsRange = getStartAndEndRowFromBaseForSub(baseRange, subTableRange)
                .stream().mapToInt(i->i).toArray();
        int[] columnsRange = getColumnRangeFromBaseForSub(baseRange, subTableRange)
                .stream().mapToInt(i->i).toArray();
        LinkedHashMap<Integer, String> hashMapWithIndexes = new LinkedHashMap<>();
        AtomicInteger count = new AtomicInteger();
        baseTable.columnNames().forEach(s -> {
            hashMapWithIndexes.put(count.getAndIncrement(), s);
        });
        List<String> columnNames = new ArrayList<>();
        for (int j : columnsRange) {
            if (hashMapWithIndexes.get(j) != null) {
                columnNames.add(hashMapWithIndexes.get(j));
            }
        }
        return baseTable.rows(rowsRange).retainColumns(columnNames.toArray(new String[0]));
    }

    public static boolean ACR_Dollar_Calc(Row row, String workBookName) throws Exception {
        String baseRange = row.getString(ConfigProperties.getProperty("testCaseFlow.ACR_MOM_BaseRange"));
        String subTableRange = row.getString(ConfigProperties.getProperty("testCaseFlow.ACR_MOM_SubTableRange"));
        String sheetName = row.getString(ConfigProperties.getProperty("testCaseFlow.ACR_MOM_SheetName"));
        Table subTable = getSubTableFromBigTableUsingRange(baseRange,
                                subTableRange,
                                SheetHandler.getTableInCellRangeFromSheet(workBookName, sheetName, baseRange));
        calculateUsingFormula(baseRange,
                subTableRange,
                SheetHandler.getTableInCellRangeFromSheet(workBookName, sheetName, baseRange),
                subTable);
        return true;
    }

    public static boolean calculateUsingFormula(String baseRange, String subTableRange, Table baseTable, Table subTable){
        double acr_dol = (double) getFirstColumnForACR_MoM(baseRange, subTableRange, baseTable).get(0);
        double mom_per = (double) getFirstColumnForACR_MoM(baseRange, subTableRange, baseTable).get(1);
//        double adj_dol = (double) getFirstColumnForACR_MoM(baseRange, subTableRange, baseTable).get(2);
        double acr_yoy = (double) getFirstColumnForACR_MoM(baseRange, subTableRange, baseTable).get(3);

        final double[] acr_dol_temp = {0};
        final double[] mom_per_temp = {0};
        final double[] adj_dol_temp = {0};
        final double[] acr_yoy_temp = {0};

        AtomicInteger count = new AtomicInteger();
        subTable.columns().forEach(objects -> {
            double acr_dol_v = Double.parseDouble(objects.getString(0));
            double mom_per_v = Double.parseDouble(objects.getString(1));
            double adj_dol_v = 0;
            if (!objects.getString(2).equals("")){
                adj_dol_v = Double.parseDouble(objects.getString(2));
            }
            double acr_yoy_v = Double.parseDouble(objects.getString(3));
            if(count.get() == 0){
                count.getAndIncrement();
                //Assert.assertSame(acr_dol * (1 + mom_per_v) + adj_dol_v, acr_dol_v);
            }
            else {
                //Assert.assertSame(acr_dol_temp[0] * (1 + mom_per_v) + adj_dol_v, acr_dol_v);
            }
            acr_dol_temp[0] = acr_dol_v;
            mom_per_temp[0] = mom_per_v;
            adj_dol_temp[0] = adj_dol_v;
            acr_yoy_temp[0] = acr_yoy_v;
        });
        return true;
    }

    public static List<Map.Entry<String, Double>> getLastSixValuesFromPresentColumnForACR_MOM(String baseRange, String subTableRange, Table table, List<Integer> range){
        int bigTableRowReference = new CellAddress(baseRange.split(":")[0]).getRow() + 1;
        int smallTableRowStart = new CellAddress(subTableRange.split(":")[0]).getRow();
        int mom_row = smallTableRowStart - bigTableRowReference;

        List<Double> mom_values = new ArrayList<>();
        LinkedHashMap<String, Double> monthNameAndValueMap = new LinkedHashMap<>();
        table.columnNames().forEach(s -> {
            if(!s.contains("FY") && !s.contains("Field") && !s.contains("Fiscal") && !s.contains("Rept")){
                mom_values.add(Double.valueOf(table.column(s).get(mom_row).toString()));
                monthNameAndValueMap.put(s, Double.valueOf(table.column(s).get(mom_row).toString()));
            }
            else {
                mom_values.add(0.0);
            }
        });

        return new ArrayList<>(monthNameAndValueMap.entrySet()).subList(range.get(0)-3, range.get(1)-3);
    }

    public static boolean validateMoMForAllMonths(String baseRange, String subTableRange, Table table) throws IOException {
        int bigTableRowReference = new CellAddress(baseRange.split(":")[0]).getRow() + 1;
        int smallTableRowStart = new CellAddress(subTableRange.split(":")[0]).getRow();
        int mom_row = smallTableRowStart - bigTableRowReference + 1;

        String[] rowNames = {"ACR $: Expected", "ACR $: Actual", "MOM%: Expected", "MOM% Actual", "ACR $: Result", "MOM%: Result"};
        Table acrReport = Table.create("acrReport");
        acrReport.addColumns(StringColumn.create("Field Segment: ", rowNames));

        getColumnRangeFromBaseForSub(baseRange, subTableRange).forEach(integer -> {
            int[] rangeDriven = new int[]{integer-7, integer};
            List<Map.Entry<String, Double>> lastSixValues = getLastSixValuesFromPresentColumnForACR_MOM(baseRange,
                    subTableRange,
                    table,
                    Arrays.stream(rangeDriven)
                        .boxed()
                        .collect(Collectors.toList()
                    )
            );

            double firstValue = lastSixValues.get(0).getValue();
            Integer firstMonth = Constant.getMonths().get(lastSixValues.get(0).getKey().substring(0, 3));
            int firstYear = Integer.parseInt(lastSixValues.get(0).getKey().split(",")[1].trim());
            double lastValue = lastSixValues.get(lastSixValues.size()-1).getValue();
            Integer lastMonth = Constant.getMonths().get(lastSixValues.get(lastSixValues.size()-1).getKey().substring(0, 3));
            int lastYear = Integer.parseInt(lastSixValues.get(lastSixValues.size() - 1).getKey().split(",")[1].trim());
            Integer currentMonth = Constant.getMonths().get(table.columnNames().get(integer).substring(0,3));
            int currentYear = Integer.parseInt(table.columnNames().get(integer).split(",")[1].trim());

            YearMonth yearMonthObject1 = YearMonth.of(firstYear, firstMonth);
            int daysInMonth1 = yearMonthObject1.lengthOfMonth();
            YearMonth yearMonthObject2 = YearMonth.of(lastYear, lastMonth);
            int daysInMonth2 = yearMonthObject2.lengthOfMonth();
            YearMonth yearMonthObject3 = YearMonth.of(currentYear, currentMonth);
            int daysInMonth3 = yearMonthObject3.lengthOfMonth();
            double tempValue = (lastValue/daysInMonth2)/(firstValue/daysInMonth1);

            double expectedMOM_Per = Math.pow(tempValue, 0.16666666666) - 1;
            double tempRoundUntoSixDecimals = 1 + Math.round(expectedMOM_Per * 1000000.0 )/ 1000000.0;
            double tempRoundSixMulLastValueRatio = (lastValue/daysInMonth2) * tempRoundUntoSixDecimals;
            double expectedAcr_Dol = tempRoundSixMulLastValueRatio * daysInMonth3;

//            System.out.println("Field Segment: "+ table.column(0).get(mom_row - 1));
//            System.out.println("Month: " + table.columnNames().get(integer));
//            System.out.println("ACR $: " +
//                    new DecimalFormat("#.####").format(expectedAcr_Dol) +
////                    Math.round(expectedACR_Dol * 10000000.0)/10000000.0 +
//                    " " +
//                    new DecimalFormat("#.####").format(Double.parseDouble(table.column(integer).get(mom_row - 1).toString()))
////                    Math.round(Double.parseDouble(table.column(integer).get(mom_row - 1).toString()) * 10000000.0)/10000000.0
//                    );
//            System.out.println("MOM%: "+ expectedMOM_Per + " " + table.column(integer).get(mom_row));
//            System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");
//            Assert.assertSame(expectedACR_Dol, table.column(integer).get(mom_row - 1));
//            Assert.assertSame(expectedMOM_Per, table.column(integer).get(mom_row));
            String fieldSegment = "Field Segment: "+ table.column(0).get(mom_row - 1);
            acrReport.column(0).setName(fieldSegment);
            String acrResult;
            String momResult;
            if(Math.round(expectedAcr_Dol * 10000.0 )/ 10000.0 == Double.parseDouble(table.column(integer).get(mom_row - 1).toString())){
                acrResult = "Pass";
            }
            else{
                acrResult = "Fail";
            }
            if(Math.round(expectedMOM_Per * 1000000.0 )/ 1000000.0 == Double.parseDouble(table.column(integer).get(mom_row).toString())){
                momResult = "Pass";
            }
            else{
                momResult = "Fail";
            }
            String month = table.columnNames().get(integer);
            String[] columnData = {String.valueOf(new DecimalFormat("#.####").format(expectedAcr_Dol)),
                    String.valueOf(new DecimalFormat("#.####").format(Double.parseDouble(table.column(integer).get(mom_row - 1).toString()))),
                    String.valueOf(Math.round(expectedMOM_Per * 1000000.0 )/ 1000000.0),
                    String.valueOf(table.column(integer).get(mom_row)),
                    acrResult,
                    momResult};
            acrReport.addColumns(StringColumn.create(month, columnData));
        });
//        System.out.println(acrReport);
        try {
            Files.write(Paths.get("log/logFile.txt"), acrReport.toString().getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
        return true;
    }

    public static void verifyACR_MOM(Row row,String workBookName) throws Exception {
        Table table1 = SheetHandler.getTableInCellRangeFromSheet(workBookName,
                row.getString(ConfigProperties.getProperty("testCaseFlow.ACR_MOM_SheetName")),
                row.getString(ConfigProperties.getProperty("testCaseFlow.ACR_MOM_BaseRange")));
        ACR_MOM_Handler.validateMoMForAllMonths(
                row.getString(ConfigProperties.getProperty("testCaseFlow.ACR_MOM_BaseRange")),
                row.getString(ConfigProperties.getProperty("testCaseFlow.ACR_MOM_SubTableRange")),
                table1
        );
    }

}