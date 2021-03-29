package RolesTesting.ExcelApplicationHandlers.ExcelHandlers;

import org.apache.poi.ss.usermodel.Cell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CellDataToDataObject {
    public static List<Cell> convertCellArrayToCellList(Cell[][] cellArray){
        List<Cell> dataList = new ArrayList<>();
        for(Cell[] eachRow : cellArray){
            dataList.addAll(Arrays.asList(eachRow));
        }
        while (dataList.remove(null)){}
        return dataList;
    }

//    public static List<List<Cell>> convertCellArrayToCellList(Cell[][] cellArray){
//        List<List<Cell>> dataList = new ArrayList<>();
//        for(Cell[] eachRow : cellArray){
//            dataList.add(Arrays.asList(eachRow));
//        }
//        return dataList;
//    }
}
