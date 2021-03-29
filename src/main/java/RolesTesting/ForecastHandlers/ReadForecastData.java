package RolesTesting.ForecastHandlers;

import RolesTesting.ExcelApplicationHandlers.ExcelHandlers.CellDataToDataObject;
import RolesTesting.Model.Forecast.Product;
import RolesTesting.Model.Forecast.SubProduct;
import RolesTesting.Model.Forecast.Variables;
import org.apache.poi.ss.usermodel.Cell;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ReadForecastData {
    public static List<Product> mapDataToModels(Cell[][] data){
        List<Product> productsInSection = new ArrayList<>();
        List<Cell> dataList = CellDataToDataObject.convertCellArrayToCellList(data);
        dataList.forEach(cell -> {
            int columnIndexOfCell = cell.getColumnIndex();
            if (columnIndexOfCell == 0 & cell.toString()!=null){
                int numberOfRowsUntilNextProduct = getNumberOfRowsUntilNextItemMap(dataList, 0).get(cell);
                Product product = new Product();
                product.setSubProducts(getSubProductsForProduct(dataList, cell.getRowIndex(), cell.getRowIndex()+numberOfRowsUntilNextProduct));
                productsInSection.add(product);
            }
        });
        return productsInSection;
    }

    public static List<SubProduct> getSubProductsForProduct(List<Cell> dataList, int rowIndexOfProduct, int numberOfRowsForThisProduct){
        List<Cell> subProducts = getNonEmptyEntriesInColumn(dataList, 1, rowIndexOfProduct, rowIndexOfProduct + numberOfRowsForThisProduct);
        List<SubProduct> subProductsList = new ArrayList<>();
        subProducts.forEach(cell -> {
            int columnIndexOfCell = cell.getColumnIndex();
            if (columnIndexOfCell == 1 & cell.toString()!=null){
                int numberOfRowsUntilNextSubProduct = getNumberOfRowsUntilNextItemMap(dataList, 1).get(cell);
                SubProduct subProduct = new SubProduct();
                subProduct.setVariables(getVariablesForSubProduct(dataList, cell.getRowIndex(), cell.getRowIndex()+numberOfRowsUntilNextSubProduct));
                subProductsList.add(subProduct);
            }
        });
        return subProductsList;
    }

    public static List<Variables> getVariablesForSubProduct(List<Cell> dataList, int rowIndexOfSubProduct, int numberOfRowsForThisSubProduct){
        List<Cell> variables = getNonEmptyEntriesInColumn(dataList, 2, rowIndexOfSubProduct, rowIndexOfSubProduct + numberOfRowsForThisSubProduct);
        List<String> variableItems = new ArrayList<>();
        variables.forEach(cell -> {
            variableItems.add(cell.toString());
        });
        List<Variables> variablesList = new ArrayList<>();
        Variables variables1 = new Variables();
        variables1.setItems(variableItems);
        variablesList.add(variables1);
        return variablesList;
    }

    public static LinkedHashMap<Cell, Integer> getNumberOfRowsUntilNextItemMap(List<Cell> dataList, int columnIndex){
        LinkedHashMap<Cell, Integer> rowProductMap = new LinkedHashMap<>();
//        final int counter = {0};
//        dataList.forEach(cell -> {
//            int columnIndexOfCell = cell.getColumnIndex();
//            if (columnIndexOfCell == columnIndex){
//                rowProductMap.put(cell, counter[0]);
//                counter[0] = 0;
//            }
//            counter[0] += 1;
//        });
        int count = 0;
        for (int i=0; i<=dataList.size()-1; i++){
            int columnIndexOfCell = dataList.get(i).getColumnIndex();
            if (columnIndexOfCell == columnIndex){
                count += 1;
                int count2 = 0;
                for (int j = i+1; j<=dataList.size()-1; j++){
                    columnIndexOfCell = dataList.get(j).getColumnIndex();
                    if (columnIndexOfCell == columnIndex){
                        count2 += 1;
                        rowProductMap.put(dataList.get(i), dataList.get(j).getRowIndex()+1);
                        break;
                    }
                }
                if (count2 < 1){
                    rowProductMap.put(dataList.get(i), dataList.get(dataList.size()-1).getRowIndex());
                    break;
                }
            }
        }
        if (count<1){
            rowProductMap.put(dataList.get(0), dataList.get(dataList.size()-1).getRowIndex());
        }

        return rowProductMap;
    }

    public static List<Cell> getNonEmptyEntriesInColumn(List<Cell> dataList, int columnIndex, int startRow, int endRow){
        List<Cell> entries = new ArrayList<>();
        if(endRow<dataList.size()-1){
            for (int i = startRow; i <= endRow; i++){
                if (dataList.get(i).getColumnIndex() == columnIndex & !dataList.get(i).toString().equals("")){
                    entries.add(dataList.get(i));
                }
            }
        }
        else {
            for (int i = startRow; i <= dataList.size()-1; i++){
                if (dataList.get(i).getColumnIndex() == columnIndex & !dataList.get(i).toString().equals("")){
                    entries.add(dataList.get(i));
                }
            }
        }
        return entries;
    }
}
