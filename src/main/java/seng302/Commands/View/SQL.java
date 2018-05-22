package seng302.Commands.View;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.management.Query;

import seng302.Database.DBManager;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * Command line to print the result of any SQL SELECT statement the user executes
 */

@Command(name = "sql", description = "Execute a SQL SELECT statement and get the results", sortOptions = false)
public class SQL implements Runnable {

    private DBManager dbManager;

    public SQL() {
        dbManager = DBManager.getInstance();
    }

    public SQL(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    @Parameters
    private List<String> allParams;

    @Override
    public void run() {
        String sql = String.join(" ", allParams);

        //Standard implementation with normal connection
        Connection conn;
        try {
            conn = dbManager.getStandardSqlConnection();
            conn.setReadOnly(true);
        } catch (SQLException e) {
            System.out.println("Couldn't connect to the database");
            e.printStackTrace();
            return;
        }
        try {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columns = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columns; i++) {
                    if (i > 1) System.out.print(" | ");
                    System.out.print(resultSet.getString(i));
                }
                System.out.print("\n");
            }
        } catch (SQLException e) {
            System.out.println("An error occurred with your query. If you were using double quotes, please ensure "
                    + "they were escaped with a backslash. The command as it was sent to the database was:");
            System.out.println(sql);
            System.out.println(e.getMessage());
        }


        //May work with the hibernate setup
//        org.hibernate.query.Query query = dbManager.getDBSession().createNativeQuery( sql );
//        List<Object> rows = query.list();
//        for (Object row : rows) {
//            Object[] columns = (Object[]) row;
//            for (Object cell : columns) {
//                System.out.print(cell + ",");
//            }
//            System.out.print("\n");
//        }
    }
}
