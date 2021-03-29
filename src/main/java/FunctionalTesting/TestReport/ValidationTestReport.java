package FunctionalTesting.TestReport;

import FunctionalTesting.DataModel.BackendReportValidationData;
import FunctionalTesting.DataModel.InputFormValidationData;
import FunctionalTesting.DataModel.TestCaseDetails;
import FunctionalTesting.DataModel.ValidationDataResults;
import nonapi.io.github.classgraph.json.JSONSerializer;

import java.io.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Scanner;

public class ValidationTestReport {

    //Creates an HTML report with test results
    public void generateTestReport(TestCaseDetails testCaseDetails, List<InputFormValidationData> inputFormValDataList, List<BackendReportValidationData> backendValDataList, List<ValidationDataResults> valDataResultList) throws IOException, URISyntaxException {
        String testData = JSONSerializer.serializeObject(testCaseDetails);
        String data= JSONSerializer.serializeObject(inputFormValDataList);
        String backendData= JSONSerializer.serializeObject(backendValDataList);
        String valResult= JSONSerializer.serializeObject(valDataResultList);
        InputStream in = getClass().getResourceAsStream("/template.html");
        BufferedReader myObj = new BufferedReader(new InputStreamReader(in));
        Scanner myReader = new Scanner(myObj);
        String template="";
        String currentLine;
        while ((currentLine = myObj.readLine()) != null) {
            template+= currentLine+"\n";
        }
        myReader.close();
        template=template.replace("@data",data);
        template=template.replace("@backendData",backendData);
        template=template.replace("@valResult",valResult);
        template=template.replace("@testData",testData);
        FileWriter myWriter = new FileWriter(testCaseDetails.getTestCaseName()+".html");
        myWriter.write(template);
        myWriter.close();
    }
}
