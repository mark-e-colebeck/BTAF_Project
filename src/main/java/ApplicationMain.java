import RolesTesting.ExecutionHandlers.RoleBasedHandlers.RoleBasedDriver;
import FunctionalTesting.DataModel.InputWbData;
import FunctionalTesting.DataModel.TestCaseDetails;
import FunctionalTesting.DataValidator.PerformValidation;
import FunctionalTesting.DataValidator.ValidateMappingDocument;
import FunctionalTesting.ExtractData.ExtractTable;
import FunctionalTesting.LoadInputFiles.GenerateInputFiles;
import FunctionalTesting.TestReport.GenerateTestReport;
import com.aventstack.extentreports.ExtentReports;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import RolesTesting.ExecutionHandlers.RoleBasedHandlers.*;

public class ApplicationMain extends Application{
    PerformValidation performValidation;
    ValidateMappingDocument validateMappingDocument;
    InputWbData inputWbData;
    ExtractTable extractTable;
    Map<String,InputWbData> finalMap = new HashMap<>();
    int uploadedDependentFileCount = 0;
    GenerateInputFiles generateInputFiles = new GenerateInputFiles();
    public ExtentReports extentReports;
    RoleBasedDriver roleBasedDriver;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            validateMappingDocument = new ValidateMappingDocument();
            performValidation = new PerformValidation();
            extractTable = new ExtractTable();

            FileChooser fileChooser = new FileChooser();
            AtomicReference<AtomicReferenceArray<File>> keywordSheet = new AtomicReference<>(new AtomicReferenceArray<>(new File[0]));

            Text testNameLabel = new Text("Test Case Name");
            TextField testNameText = new TextField();

            Text testIdLabel = new Text("Test Case ID");
            TextField testIdText = new TextField();
            testIdText.setDisable(true);

            Text testTypeText = new Text("Test Type");
            ChoiceBox testType = new ChoiceBox();
            testType.setPrefWidth(200);
            testType.getItems().add("Roles Testing");
            testType.getItems().add("Functional Testing");

            Text executionEnvironmentText = new Text("Execution Environment");
            ChoiceBox executionEnvironment = new ChoiceBox();
            executionEnvironment.setPrefWidth(200);
            executionEnvironment.getItems().add("BPC QR /Project SIT System");
            executionEnvironment.getItems().add("BPC QR/ Project Development System");
            executionEnvironment.getItems().add("BPC QR/ Project Test System");

            Text loginTypeText = new Text("Login Type");
            ChoiceBox loginType = new ChoiceBox();
            loginType.setPrefWidth(200);
            loginType.getItems().add("SSO");
            loginType.getItems().add("Credential");

            Text userNameLabel = new Text("UserName");
            TextField userNameText = new TextField();

            Text passwordLabel = new Text("Password");
            PasswordField passwordText = new PasswordField();

            Text roleTypeText = new Text("Role Type");
            ChoiceBox roleType = new ChoiceBox();
            roleType.setPrefWidth(200);
            roleType.getItems().add("Admin");
            roleType.getItems().add("BizOps");
            roleType.getItems().add("Report");
            roleType.getItems().add("Planner");

            Text inputWbText = new Text("Load Workbook to Execute");
            TextField inputText = new TextField();
            Button buttonInputWb = new Button("Browse");

            Button buttonAddToTestRun = new Button("Add to Test Run");
            buttonAddToTestRun.setDisable(true);
            buttonAddToTestRun.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white;");

            Text inputWbText_RB = new Text("Load Workbook to Execute");
            TextField inputText_RB = new TextField();
            Button buttonInputWb_RB = new Button("Browse");

            Button buttonAddToTestRun_RB = new Button("Add to Test Run");
            buttonAddToTestRun_RB.setDisable(true);
            buttonAddToTestRun_RB.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white;");

            Button executeTest = new Button("Execute Tests");
            executeTest.setDisable(true);

            TreeItem<String> base = new TreeItem<>("Test Run Selections");
            base.setExpanded(true);
            TreeView<String> view = new TreeView<>();
            view.setRoot(base);
            view.setCellFactory(e -> new CustomCell(executeTest));
            view.setPrefHeight(400);
            view.setPrefWidth(450);

            userNameLabel.setVisible(false);
            passwordLabel.setVisible(false);
            userNameText.setVisible(false);
            passwordText.setVisible(false);
            loginTypeText.setVisible(false);
            loginType.setVisible(false);
            roleType.setVisible(false);
            roleTypeText.setVisible(false);
            executionEnvironmentText.setVisible(false);
            executionEnvironment.setVisible(false);
            inputWbText.setVisible(false);
            inputText.setVisible(false);
            buttonInputWb.setVisible(false);
            buttonAddToTestRun.setVisible(false);

            loginType.setOnAction((event) -> {
                String login_Type = (String)loginType.getValue();
                if(login_Type != null && login_Type.equalsIgnoreCase("SSO")){
                    userNameText.setDisable(true);
                    passwordText.setDisable(true);
                }
                else{
                    userNameText.setDisable(false);
                    passwordText.setDisable(false);
                }
            });

            buttonInputWb.setOnAction(e -> {
                keywordSheet.set(new AtomicReferenceArray<>(new File[1]));
                keywordSheet.get().set(0, fileChooser.showOpenDialog(primaryStage));
                inputText.setText(keywordSheet.get().get(0).getName() != null ? keywordSheet.get().get(0).getName() : "");
                buttonAddToTestRun.setDisable(false);
            });

            buttonInputWb_RB.setOnAction(e -> {
                keywordSheet.set(new AtomicReferenceArray<>(new File[1]));
                keywordSheet.get().set(0, fileChooser.showOpenDialog(primaryStage));
                inputText_RB.setText(keywordSheet.get().get(0).getName() != null ? keywordSheet.get().get(0).getName() : "");
                buttonAddToTestRun_RB.setDisable(false);
            });

            buttonAddToTestRun.setOnAction(e -> {
                if(testNameText.getText().isEmpty() || testNameText.getText().equals("")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Enter a test case name to proceed.");
                    alert.showAndWait();
                } else {
                    uploadedDependentFileCount = 0;
                    buttonAddToTestRun.setDisable(true);
                    TestCaseDetails testCaseDetails = new TestCaseDetails();
                    inputWbData = new InputWbData();
                    inputWbData.setTestCaseDetails(testCaseDetails);
                    testCaseDetails.setTestCaseName(testNameText.getText());
                    testCaseDetails.setTestCaseID(testIdText.getText());
                    testCaseDetails.setTestType((String) testType.getValue());
                    testCaseDetails.setExecutionEnvironment(executionEnvironment.getValue().toString());
                    testCaseDetails.setLoginType(loginType.getValue().toString());
                    if(testCaseDetails.getLoginType().equalsIgnoreCase("Credential")){
                        testCaseDetails.setUserName(userNameText.getText());
                        testCaseDetails.setPassword(passwordText.getText());
                    }
                    else{
                        testCaseDetails.setUserName("");
                        testCaseDetails.setPassword("");
                    }
                    testCaseDetails.setRoleType((String) roleType.getValue());
                    inputWbData.setKeyWordSheet(keywordSheet.get().get(0));
                    finalMap.put(testCaseDetails.getTestCaseName(), inputWbData);

                    executeTest.setDisable(false);
                    TreeItem root1 = new TreeItem("Test Case Name: "+ testCaseDetails.getTestCaseName());
                    TreeItem item1 = new TreeItem("Test Cases ID: " + testCaseDetails.getTestCaseID());
                    TreeItem item2 = new TreeItem("Test Type: " + testCaseDetails.getTestType());
                    TreeItem item3 = new TreeItem("Execution Environment: " + testCaseDetails.getExecutionEnvironment());
                    TreeItem item4 = new TreeItem("Role Type: " + testCaseDetails.getRoleType());
                    root1.getChildren().addAll(item1,item2,item3,item4);
                    base.getChildren().add(root1);

                    testNameText.clear();
                    executionEnvironment.getSelectionModel().clearSelection();
                    loginType.getSelectionModel().clearSelection();
                    userNameText.clear();
                    passwordText.clear();
                    roleType.getSelectionModel().clearSelection();
                    inputText.clear();
                    testType.getSelectionModel().clearSelection();
                }
            });

            buttonAddToTestRun_RB.setOnAction(e -> {
                if(testNameText.getText().isEmpty() || testNameText.getText().equals("")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Enter a test case name to proceed.");
                    alert.showAndWait();
                } else {
                    uploadedDependentFileCount = 0;
                    buttonAddToTestRun_RB.setDisable(true);
                    TestCaseDetails testCaseDetails = new TestCaseDetails();
                    inputWbData = new InputWbData();
                    inputWbData.setTestCaseDetails(testCaseDetails);
                    testCaseDetails.setTestCaseName(testNameText.getText());
                    testCaseDetails.setTestCaseID(testIdText.getText());
                    testCaseDetails.setTestType((String) testType.getValue());
                    inputWbData.setKeyWordSheet(keywordSheet.get().get(0));
                    finalMap.put(testCaseDetails.getTestCaseName(), inputWbData);

                    executeTest.setDisable(false);
                    TreeItem root1 = new TreeItem("Test Case Name: "+ testCaseDetails.getTestCaseName());
                    TreeItem item1 = new TreeItem("Test Cases ID: " + testCaseDetails.getTestCaseID());
                    TreeItem item2 = new TreeItem("Test Type: " + testCaseDetails.getTestType());
                    root1.getChildren().addAll(item1,item2);
                    base.getChildren().add(root1);

                    testNameText.clear();
                    executionEnvironment.getSelectionModel().clearSelection();
                    loginType.getSelectionModel().clearSelection();
                    userNameText.clear();
                    passwordText.clear();
                    roleType.getSelectionModel().clearSelection();
                    inputText_RB.clear();
                    testType.getSelectionModel().clearSelection();

                }
            });
            executeTest.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.NONE);
                alert.setTitle("Test Run Status");
                AtomicInteger successfulTestCount = new AtomicInteger();
                finalMap.entrySet().forEach(val->
                {
                    extentReports = GenerateTestReport.initializeValues(val.getKey());
                    if(val.getValue().getTestCaseDetails().getTestType().equals("Functional Testing")) {
                        try {
                            if(generateInputFiles.runTestAction(val.getValue(),extentReports)) {
                                successfulTestCount.getAndIncrement();
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            Alert alertFailure = new Alert(Alert.AlertType.ERROR);
                            alertFailure.setHeaderText("Failure in executing test cases" + exception);
                            alertFailure.showAndWait();
                        } finally {
                            extentReports.flush();
                        }
                    } else {
                        try {
                            roleBasedDriver.executeEachRowInTestSheet(keywordSheet.get().get(0),"RBT_RoleFolder", extentReports);
                            roleBasedDriver.executeEachRowInTestSheet(keywordSheet.get().get(0), "RBT_RoleWorkbook", extentReports);
                            roleBasedDriver.executeEachRowInTestSheet(keywordSheet.get().get(0), "RBT", extentReports);
                            successfulTestCount.getAndIncrement();
                            extentReports.flush();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            Alert alertFailure = new Alert(Alert.AlertType.ERROR);
                            alertFailure.setHeaderText("Failure in executing role based test: " + exception);
                            alertFailure.showAndWait();
                        } finally {
                            extentReports.flush();
                        }
                    }
                });
                if(successfulTestCount.get() == finalMap.size()) {
                    Alert alertSuccess = new Alert(Alert.AlertType.INFORMATION);
                    alertSuccess.setHeaderText("Test cases executed successfully");
                    alertSuccess.showAndWait();
                } else {
                    System.out.println("Not all test are successful");
                    Alert alertFailure = new Alert(Alert.AlertType.ERROR);
                    alertFailure.setHeaderText("Failure in executing test cases");
                    alertFailure.setContentText("Not all test are successful");
                    alertFailure.showAndWait();
                }
            });

           /* executeRolesBasedTest.setOnAction(e -> {
                extentReports = GenerateTestReport.initializeValues();
                try {
                    roleBasedDriver.executeEachRowInTestSheet("src/main/resources/Azure Forecast Input Workbook - NonUS_RBT.xlsx",
                            "RBT_RoleFolder", extentReports);
                    Alert alertSuccess = new Alert(Alert.AlertType.INFORMATION);
                    alertSuccess.setHeaderText("Role based test executed successfully");
                    alertSuccess.showAndWait();
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Alert alertFailure = new Alert(Alert.AlertType.ERROR);
                    alertFailure.setHeaderText("Failure in executing role based test");
                    alertFailure.showAndWait();
                }
            });*/

            GridPane gridPane = new GridPane();
            gridPane.setMinSize(695 , 650);
            gridPane.setPadding(new Insets(10, 5, 10, 30));
            gridPane.setVgap(5);
            gridPane.setHgap(5);
            gridPane.setAlignment(Pos.CENTER);
            GridPane treeGrid = new GridPane();

            testType.setOnAction((event) -> {
                String login_Type = (String)testType.getValue();
                if(login_Type != null && login_Type.equalsIgnoreCase("Functional Testing")){
                    userNameText.setVisible(true);
                    passwordText.setVisible(true);
                    loginTypeText.setVisible(true);
                    loginType.setVisible(true);
                    roleType.setVisible(true);
                    roleTypeText.setVisible(true);
                    executionEnvironmentText.setVisible(true);
                    executionEnvironment.setVisible(true);
                    userNameLabel.setVisible(true);
                    passwordLabel.setVisible(true);
                    inputWbText.setVisible(true);
                    inputText.setVisible(true);
                    buttonInputWb.setVisible(true);
                    buttonAddToTestRun.setVisible(true);
                    inputText_RB.setVisible(false);
                    inputWbText_RB.setVisible(false);
                    buttonInputWb_RB.setVisible(false);
                    buttonAddToTestRun_RB.setVisible(false);
                }
                else{
                    userNameLabel.setVisible(false);
                    passwordLabel.setVisible(false);
                    userNameText.setVisible(false);
                    passwordText.setVisible(false);
                    loginTypeText.setVisible(false);
                    loginType.setVisible(false);
                    roleType.setVisible(false);
                    roleTypeText.setVisible(false);
                    executionEnvironmentText.setVisible(false);
                    executionEnvironment.setVisible(false);
                    inputWbText.setVisible(false);
                    inputText.setVisible(false);
                    buttonInputWb.setVisible(false);
                    buttonAddToTestRun.setVisible(false);
                    inputText_RB.setVisible(true);
                    inputWbText_RB.setVisible(true);
                    buttonInputWb_RB.setVisible(true);
                    buttonAddToTestRun_RB.setVisible(true);
                }
            });

            treeGrid.setMinSize(600, 600);
            treeGrid.setPadding(new Insets(10, 5, 10, 5));
            treeGrid.setVgap(5);
            treeGrid.setHgap(5);
            treeGrid.setAlignment(Pos.TOP_LEFT);
            gridPane.add(testNameLabel, 0, 0);
            gridPane.add(testNameText, 1, 0);
            gridPane.add(testIdLabel, 0, 2);
            gridPane.add(testIdText, 1, 2);
            gridPane.add(inputWbText, 0, 16);
            gridPane.add(inputText, 1, 16);
            gridPane.add(buttonInputWb, 2, 16);

            gridPane.add(testTypeText, 0, 4);
            gridPane.add(testType, 1, 4);

            gridPane.add(inputWbText_RB, 0, 6);
            gridPane.add(inputText_RB, 1, 6);
            gridPane.add(buttonInputWb_RB, 2, 6);

            gridPane.add(executionEnvironmentText, 0, 6);
            gridPane.add(executionEnvironment, 1, 6);

            gridPane.add(loginTypeText, 0, 8);
            gridPane.add(loginType, 1, 8);

            gridPane.add(userNameLabel, 0, 10);
            gridPane.add(userNameText, 1, 10);
            gridPane.add(passwordLabel, 0, 12);
            gridPane.add(passwordText, 1, 12);

            gridPane.add(roleTypeText, 0, 14);
            gridPane.add(roleType, 1, 14);

            gridPane.add(buttonAddToTestRun, 1, 20);
            gridPane.add(buttonAddToTestRun_RB, 1, 10);
            treeGrid.add(view ,0,19);
            treeGrid.add(executeTest, 0, 21);
            executeTest.setAlignment(Pos.CENTER);
            executeTest.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white;");
            HBox hBox = new HBox(gridPane,treeGrid);
            hBox.setAlignment(Pos.CENTER);
            Scene scene = new Scene(hBox);
            primaryStage.setTitle("Form Validation");
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.show();
        }
        catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
    class CustomCell extends TreeCell<String> {
        Button executeTest;
        public CustomCell(Button executeTest) {
            this.executeTest = executeTest;
        }
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (isEmpty()) {
                setGraphic(null);
                setText(null);
            } else {
                if (! this.getTreeItem().isLeaf() && (this.getTreeItem().getParent() != null)) {
                    GridPane gridPane = new GridPane();
                    gridPane.setAlignment(Pos.TOP_LEFT);
                    gridPane.setHgap(10);
                    gridPane.setVgap(10);
                    Label label = new Label(item);
                    Image img = new Image(getClass().getClassLoader().getResourceAsStream("images/delete_file.png"));
                    ImageView imgView = new ImageView(img);
                    imgView.setFitHeight(30);
                    imgView.setFitWidth(30);
                    imgView.setPreserveRatio(true);
                    Button button = new Button();
                    button.setGraphic(imgView);
                    gridPane.add(label,0,0);
                    gridPane.add(button,7,0);
                    setGraphic(gridPane);
                    setText(null);
                    button.setOnAction(e -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Delete Test Cases");
                        alert.showAndWait();
                        if (alert.getResult() == ButtonType.OK) {
                            TreeItem<String> selected = this.getTreeItem();
                            String key = this.getTreeItem().getValue().split(":")[1].trim();
                            finalMap.entrySet().removeIf(e1 -> e1.getKey().equals(key));
                            selected.getParent().getChildren().remove(selected);
                        }
                        if(finalMap.isEmpty() || finalMap.size() != 0) {
                            executeTest.setDisable(true);
                        }
                    });
                } else {
                    setGraphic(null);
                    setText(item);
                }
            }
        }
    }
    public static void main(String[] args) {
        Application.launch(args);
    }
}
