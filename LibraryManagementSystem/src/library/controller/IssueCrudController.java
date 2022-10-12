package library.controller;

import library.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class IssueCrudController {
    public static ArrayList<String> getIssuedIds() throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT issueID FROM IssueBook WHERE status=?", "Issued");
        ArrayList<String> idList = new ArrayList<>();
        while (result.next()) {
            idList.add(result.getString(1));
        }
        return idList;
    }
}
