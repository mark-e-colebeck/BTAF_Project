package RolesTesting.Constants;

import java.util.LinkedHashMap;

public class Constant {
    private static final LinkedHashMap<String, Integer> months = new LinkedHashMap<>();
    public static LinkedHashMap<String, Integer> getMonths() {
        months.put("JAN", 1);
        months.put("FEB", 2);
        months.put("MAR", 3);
        months.put("APR", 4);
        months.put("MAY", 5);
        months.put("JUN", 6);
        months.put("JUL", 7);
        months.put("AUG", 8);
        months.put("SEP", 9);
        months.put("OCT", 10);
        months.put("NOV", 11);
        months.put("DEC", 12);
        return months;
    }
}
