package RolesTesting.ExecutionHandlers.AutomationExecutionHandlers;

import RolesTesting.Util.ConfigProperties;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.ptr.PointerByReference;
import mmarquee.automation.*;
import mmarquee.automation.uiautomation.TreeScope;

public class WindowsSyncCheck extends UIAutomation{

    private AutomationElement rootElement = getRootElement();
    public AutomationElement waitUntilLoad(ControlType controlType, String title, int numberOfRetries) throws AutomationException, InterruptedException {
        AutomationElement element = null;
        Variant.VARIANT.ByValue variant1 = new Variant.VARIANT.ByValue();
        variant1.setValue(22, controlType.getValue());
        Variant.VARIANT.ByValue variant2 = new Variant.VARIANT.ByValue();
        WTypes.BSTR sysAllocated = OleAuto.INSTANCE.SysAllocString(title);
        variant2.setValue(8, sysAllocated);

        try {
            PointerByReference pCondition1 = createPropertyCondition(PropertyID.Name.getValue(), variant2);
            PointerByReference pCondition2 = createPropertyCondition(PropertyID.ControlType.getValue(), variant1);
            PointerByReference pAndCondition = createAndCondition(pCondition1, pCondition2);

            for(int loop = 0; loop < numberOfRetries; ++loop) {
                try {
                    element = rootElement.findFirst(new TreeScope(4), pAndCondition);
                } catch (AutomationException var16) {
                    logger.info("Not found, retrying " + title);
                    try {
                        Thread.sleep(Long.parseLong(ConfigProperties.getProperty("global.wait.sync.milliseconds")));
                    }catch (Exception e){
                        Thread.currentThread().interrupt();
                    }
                }

                if (element != null) {
                    break;
                }
            }
        } finally {
            OleAuto.INSTANCE.SysFreeString(sysAllocated);
        }

        if (element == null) {
            logger.warning("Failed to find desktop window `" + title + "`");
            throw new ItemNotFoundException(title);
        } else {
            return element;
        }
    }



}
