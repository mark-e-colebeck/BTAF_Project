package Model.InputForm.AzureForecast;

import tech.tablesaw.api.Row;

public class ACR_MOM {

    Row ACR_Dol;
    Row MOM_Per;
    Row Adj_Dol;
    Row ACR_YOY_Per;
    String fieldSegment;

    public String getFieldSegment() {
        return fieldSegment;
    }

    public void setFieldSegment(String fieldSegment) {
        this.fieldSegment = fieldSegment;
    }

    public Row getACR_Dol() {
        return ACR_Dol;
    }

    public void setACR_Dol(Row ACR_Dol) {
        this.ACR_Dol = ACR_Dol;
    }

    public Row getMOM_Per() {
        return MOM_Per;
    }

    public void setMOM_Per(Row MOM_Per) {
        this.MOM_Per = MOM_Per;
    }

    public Row getAdj_Dol() {
        return Adj_Dol;
    }

    public void setAdj_Dol(Row adj_Dol) {
        Adj_Dol = adj_Dol;
    }

    public Row getACR_YOY_Per() {
        return ACR_YOY_Per;
    }

    public void setACR_YOY_Per(Row ACR_YOY_Per) {
        this.ACR_YOY_Per = ACR_YOY_Per;
    }
}
