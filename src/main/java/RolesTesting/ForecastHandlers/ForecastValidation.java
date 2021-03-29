package ForecastHandlers;

import RolesTesting.Util.ConfigProperties;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.util.*;

public class ForecastValidation {

    private static LinkedHashMap<String, List<Row>> getSameSegmentMap(Table table){
        LinkedHashSet<String> segments = (LinkedHashSet<String> ) table.column(0).asList();
        LinkedHashMap<String, List<Row>> segmentMap = new LinkedHashMap<>();
        segments.forEach(s -> {
            List<Row> sameSegmentRows = new ArrayList<>();
            table.stream().iterator().forEachRemaining(row -> {
                if (s.equals(row.getString(0))){
                    sameSegmentRows.add(row);
                }
            });
            segmentMap.put(s, sameSegmentRows);
        });
        return segmentMap;
    }

    public static boolean performActualAndForecastValidation(Table table, Row rowWithDetails) throws IOException {
//        int[] columns = {table.columnCount()};
//        for (int i = rowWithDetails.getInt(ConfigProperties.getProperty("testCaseFlow.parameter3"));
//                i<=rowWithDetails.getInt(ConfigProperties.getProperty("testCaseFlow.parameter4"));
//                i++){
//            columns[i] = i;
//        }
//        table.columns(columns)

        getSameSegmentMap(table).forEach((s, rows) -> {
            List<String> actuals = new ArrayList<>();
            List<String> forecasts = new ArrayList<>();
            rows.forEach(row -> {
                try {
                    for (int i = rowWithDetails.getInt(ConfigProperties.getProperty("testCaseFlow.parameter3"));
                    i<=rowWithDetails.getInt(ConfigProperties.getProperty("testCaseFlow.parameter4"));
                    i++){
                        actuals.add(row.getText(i));
                    }

                    for (int i = rowWithDetails.getInt(ConfigProperties.getProperty("testCaseFlow.parameter5"));
                         i<=rowWithDetails.getInt(ConfigProperties.getProperty("testCaseFlow.parameter6"));
                         i++){
                        forecasts.add(row.getText(i));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    return true;
    }
}
