package com.humanharvest.organz.commands.view;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.database.DBManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Ignore
public class SQLTest extends BaseTest {

    private DBManager spyDBManager;
    private SQL spySQL;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private ResultSetMetaData resultSetMetaData;

    private SessionFactory sessionFactory;
    private Session session;

    @Before
    public void init() throws SQLException {
        sessionFactory = mock(SessionFactory.class);
        session = mock(Session.class);
        connection = mock(Connection.class);
        statement = mock(Statement.class);
        resultSet = mock(ResultSet.class);
        resultSetMetaData = mock(ResultSetMetaData.class);
        spyDBManager = spy(new DBManager(sessionFactory));

        when(spyDBManager.getDBSession()).thenReturn(session);

        when(spyDBManager.getStandardSqlConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        when(resultSetMetaData.getColumnCount()).thenReturn(0);
        when(resultSet.next()).thenReturn(false);


        spySQL = spy(new SQL(spyDBManager));
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void CheckValidCommandIsExecutedTest() throws SQLException {
        String[] inputs = {"SELECT * FROM TEST"};

        CommandLine.run(spySQL, System.out, inputs);

        verify(statement, times(1)).executeQuery("SELECT * FROM TEST");
    }

    @Test
    public void CheckEmptyCommandIsNotExecutedTest() throws SQLException {
        String[] inputs = {};

        CommandLine.run(spySQL, System.out, inputs);

        verify(spyDBManager, times(0)).getStandardSqlConnection();
    }

    @Test
    public void CheckValidCommandWithSpacesIsExecutedTest() throws SQLException {
        String[] inputs = {"SELECT", "*", "FROM", "TEST"};

        CommandLine.run(spySQL, System.out, inputs);

        verify(statement, times(1)).executeQuery("SELECT * FROM TEST");
    }


    @Test
    public void CheckValidCommandWithEscapedQuotesIsExecutedTest() throws SQLException {
        String[] inputs = {"SELECT", "\"\"*\"\"", "FROM", "TEST"};

        CommandLine.run(spySQL, System.out, inputs);

        verify(statement, times(1)).executeQuery("SELECT \"*\" FROM TEST");
    }

    @Test
    public void CheckCommandMultipleRowsOutputTest() throws SQLException {
        String[] inputs = {"SELECT * FROM TEST"};
        //Setup fake ResultSet with three columns and 2 rows
        when(resultSet.isBeforeFirst()).thenReturn(true);
        when(resultSetMetaData.getColumnCount()).thenReturn(3);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet.getString(1)).thenReturn("First").thenReturn("Row 2 First");
        when(resultSet.getString(2)).thenReturn("Second").thenReturn("Row 2 Second");
        when(resultSet.getString(3)).thenReturn("Third").thenReturn("Row 2 Third");

        CommandLine.run(spySQL, System.out, inputs);

        verify(statement, times(1)).executeQuery("SELECT * FROM TEST");
        assertTrue(outContent.toString().contains("First; Second; Third"));
        assertTrue(outContent.toString().contains("Row 2 First; Row 2 Second; Row 2 Third"));
    }


    @Test
    public void CheckCommandZeroRowsOutputTest() throws SQLException {
        String[] inputs = {"SELECT * FROM TEST"};
        //Setup fake empty ResultSet
        when(resultSet.isBeforeFirst()).thenReturn(false);

        CommandLine.run(spySQL, System.out, inputs);

        verify(statement, times(1)).executeQuery("SELECT * FROM TEST");
        assertTrue(outContent.toString().contains("No rows"));
        System.out.println(outContent.toString());
    }

    @Test
    public void SQLExceptionInExecutionTest() throws SQLException {
        String[] inputs = {"Invalid SQL"};
        //Setup fake ResultSet with three columns and 2 rows
        when(statement.executeQuery(anyString())).thenThrow(new SQLException("Fake Exception Message"));

        CommandLine.run(spySQL, System.out, inputs);

        verify(statement, times(1)).executeQuery("Invalid SQL");
        assertTrue(outContent.toString().contains("An error occurred with your query."));
        assertTrue(outContent.toString().contains("\nInvalid SQL\nFake Exception Message"));
    }
}
