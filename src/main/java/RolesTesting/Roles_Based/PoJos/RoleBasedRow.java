package RolesTesting.Roles_Based.PoJos;

public class RoleBasedRow {
    String test_Case_Name;
    String test_Case_Id;
    String test_Case_Description;
    String test_Run_Flag;
    String connection;
    String username;
    String password;
    String role_Folder;
    String role_Workbook;
    String refresh;
    String calculate;
    String save;
    String submit;
    String listOfRegions;
    String macro1;
    String macro2;
    String macro3;
    String macro4;
    String macro5;
    String macro6;
    String macro7;
    String macro8;


    public RoleBasedRow() {
    }

    public RoleBasedRow(String test_Case_Name, String test_Case_Id, String test_Case_Description, String test_Case_Status, String connection, String username, String password, String role_Folder, String role_Workbook, String refresh, String calculate, String save, String submit,String listOfRegions) {
        this.test_Case_Name = test_Case_Name;
        this.test_Case_Id = test_Case_Id;
        this.test_Case_Description = test_Case_Description;
        this.test_Run_Flag = test_Case_Status;
        this.connection = connection;
        this.username = username;
        this.password = password;
        this.role_Folder = role_Folder;
        this.role_Workbook = role_Workbook;
        this.refresh = refresh;
        this.calculate = calculate;
        this.save = save;
        this.submit = submit;
        this.listOfRegions = listOfRegions;
    }

    public String getTest_Case_Name() {
        return test_Case_Name;
    }

    public void setTest_Case_Name(String test_Case_Name) {
        this.test_Case_Name = test_Case_Name;
    }

    public String getTest_Case_Id() {
        return test_Case_Id;
    }

    public void setTest_Case_Id(String test_Case_Id) {
        this.test_Case_Id = test_Case_Id;
    }

    public String getTest_Case_Description() {
        return test_Case_Description;
    }

    public void setTest_Case_Description(String test_Case_Description) {
        this.test_Case_Description = test_Case_Description;
    }

    public String getTest_Run_Flag() {
        return test_Run_Flag;
    }

    public void setTest_Run_Flag(String test_Run_Flag) {
        this.test_Run_Flag = test_Run_Flag;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole_Folder() {
        return role_Folder;
    }

    public void setRole_Folder(String role_Folder) {
        this.role_Folder = role_Folder;
    }

    public String getRole_Workbook() {
        return role_Workbook;
    }

    public void setRole_Workbook(String role_Workbook) {
        this.role_Workbook = role_Workbook;
    }

    public String getRefresh() {
        return refresh;
    }

    public void setRefresh(String refresh) {
        this.refresh = refresh;
    }

    public String getCalculate() {
        return calculate;
    }

    public void setCalculate(String calculate) {
        this.calculate = calculate;
    }

    public String getSave() {
        return save;
    }

    public void setSave(String save) {
        this.save = save;
    }

    public String getSubmit() {
        return submit;
    }

    public void setSubmit(String submit) {
        this.submit = submit;
    }
    public String getListOfRegions(){return listOfRegions;}

    public void setListOfRegions(String listOfRegions){
        this.listOfRegions = listOfRegions;
    }

    public String getMacro1() {
        return macro1;
    }

    public void setMacro1(String macro1) {
        this.macro1 = macro1;
    }

    public String getMacro2() {
        return macro2;
    }

    public void setMacro2(String macro2) {
        this.macro2 = macro2;
    }

    public String getMacro3() {
        return macro3;
    }

    public void setMacro3(String macro3) {
        this.macro3 = macro3;
    }

    public String getMacro4() {
        return macro4;
    }

    public void setMacro4(String macro4) {
        this.macro4 = macro4;
    }

    public String getMacro5() {
        return macro5;
    }

    public void setMacro5(String macro5) {
        this.macro5 = macro5;
    }

    public String getMacro6() {
        return macro6;
    }

    public void setMacro6(String macro6) {
        this.macro6 = macro6;
    }

    public String getMacro7() {
        return macro7;
    }

    public void setMacro7(String macro7) {
        this.macro7 = macro7;
    }

    public String getMacro8() {
        return macro8;
    }

    public void setMacro8(String macro8) {
        this.macro8 = macro8;
    }
}
