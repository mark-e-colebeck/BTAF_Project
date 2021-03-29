package RolesTesting.Roles_Based.PoJos;

//import com.fasterxml.jackson.databind.ObjectMapper;

public class RBTReport {
    boolean status;
    Object expected;
    Object actual;

    public RBTReport(boolean status, Object expected, Object actual) {
        this.status = status;
        this.expected = expected;
        this.actual = actual;
    }

    public boolean isStatus() {
        return status;
    }

    public Object getExpected() {
        return expected;
    }

    public Object getActual() {
        return actual;
    }
}
