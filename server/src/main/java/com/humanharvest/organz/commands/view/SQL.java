package com.humanharvest.organz.commands.view;

import com.humanharvest.organz.commands.modify.Load;
import com.humanharvest.organz.database.DBManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.state.State.DataStorageType;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//import com.humanharvest.organz.database.DBManager;

/**
 * Command line to print the result of any SQL SELECT statement the user executes
 */

@Command(name = "sql", description = "Execute a SQL SELECT statement and get the results", sortOptions = false)
public class SQL implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(SQL.class.getName());

    private DBManager dbManager;
    private final PrintStream outputStream;

    public SQL(DBManager dbManager) {
        this.dbManager = dbManager;
        outputStream = System.out;
    }

    public SQL(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public SQL() {
        outputStream = System.out;
    }

    @Parameters
    private List<String> allParams;

    @Override
    public void run() {
        if (allParams == null) {
            outputStream.print("No SQL input, please enter a valid SQL command");
            return;
        } else if (State.getCurrentStorageType() == DataStorageType.MEMORY) {
            outputStream.print("Currently not connected to the database, cannot execute SQL");
            return;
        } else if (dbManager == null) {
            dbManager = DBManager.getInstance();
        }

        String sql = String.join(" ", allParams);

        //Standard implementation with normal connection
        try (Connection connection = dbManager.getStandardSqlConnection()) {
            connection.setReadOnly(true);

            executeQuery(connection, sql, outputStream);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            outputStream.print("Couldn't connect to the database");
        }
    }

    private static void executeQuery(Connection connection, String sql, PrintStream outputStream) {
        try (Statement stmt = connection.createStatement()) {
            //This is allowed since the administrator is the one executing the statement
            //noinspection JDBCExecuteWithNonConstantString
            try (ResultSet resultSet = stmt.executeQuery(sql)) {
                if (!resultSet.isBeforeFirst()) {
                    outputStream.print("No rows were returned for that query");
                    return;
                }

                int columns = resultSet.getMetaData().getColumnCount();
                StringBuilder buffer = new StringBuilder();
                while (resultSet.next()) {
                    for (int i = 1; i <= columns; i++) {
                        if (i > 1) {
                            buffer.append("; ");
                        }
                        buffer.append(resultSet.getString(i));
                    }
                    buffer.append('\n');
                }
                outputStream.print(buffer.toString());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            outputStream.print("An error occurred with your query."
                    + "If you were using double quotes, please ensure they were escaped with a backslash and "
                    + "enclosed in a quoted string. The command as it was sent "
                    + "to the database was: " + sql);
        }
    }
}
