package RolesTesting.ExecutionHandlers.AutomationExecutionHandlers;

import RolesTesting.Roles_Based.InputHandlers.RoleAndEnvironmentSetup;
import Model.Execution.InitialExecutionKeys;
import mmarquee.automation.AutomationException;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.pattern.PatternNotFoundException;
import RolesTesting.Util.ConfigProperties;

import java.io.IOException;

public class InitialExecutors {
    public static String getConnectionString(InitialExecutionKeys keys){
        return keys.getConnection();
    }

    public static void clickOnConnection(UIAutomation automation, InitialExecutionKeys initialExecutionKeys) throws Exception {
        ActionExecutors.clickOnConnection(automation, getConnectionString(initialExecutionKeys));
    }

    public static void ClickOnRoleFoldersInPlugin(UIAutomation automation, InitialExecutionKeys initialExecutionKeys) throws AutomationException, InterruptedException, PatternNotFoundException, IOException {
         ActionExecutors.clickOnRoleFolders(automation, initialExecutionKeys.getRoleFolders(), initialExecutionKeys.getWorkSheetToSelect());
    }

    public static void connectionClickAndRoleFolderSelect(UIAutomation automation, InitialExecutionKeys initialExecutionKeys) throws Exception {
        clickOnConnection(automation, initialExecutionKeys);

        try {
            String username = ConfigProperties.getProperty("role.Username");
            String password = ConfigProperties.getProperty("role.Password");
            String connection = ConfigProperties.getProperty("role.Connection");

            RoleAndEnvironmentSetup.credentialsLogin(automation, username, password, connection);
        }
        finally {
            ClickOnRoleFoldersInPlugin(automation, initialExecutionKeys);
        }


    }



}
