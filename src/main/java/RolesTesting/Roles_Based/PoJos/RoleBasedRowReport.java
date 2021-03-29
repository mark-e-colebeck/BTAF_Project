package RolesTesting.Roles_Based.PoJos;

import javax.management.relation.Role;
import RolesTesting.Roles_Based.PoJos.RoleBasedRow;

public class RoleBasedRowReport{
    private String rowReport;
    private RoleBasedRow roleBasedRow;

    public RoleBasedRowReport(String rowReport, RoleBasedRow roleBasedRow) {
        this.rowReport = rowReport;
        this.roleBasedRow = roleBasedRow;
    }

    public RoleBasedRow getRoleBasedRow() {
        return roleBasedRow;
    }

    public void setRoleBasedRow(RoleBasedRow roleBasedRow) {
        this.roleBasedRow = roleBasedRow;
    }

    public String getRowReport() {
        return rowReport;
    }

    public void setRowReport(String name){
        this.rowReport = name;
    }
}