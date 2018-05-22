package seng302.Commands.View;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import seng302.Client;
import seng302.Database.DBManager;
import seng302.State.ClientManager;
import seng302.State.ClientManagerMemory;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import picocli.CommandLine;

public class SQLTest {

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
    public void init() {
        sessionFactory = mock(SessionFactory.class);
        session = mock(Session.class);
        connection = mock(Connection.class);
        statement = mock(Statement.class);
        resultSet = mock(ResultSet.class);
        resultSetMetaData = mock(ResultSetMetaData.class);

        spyDBManager = spy(new DBManager(sessionFactory));
        when(spyDBManager.getDBSession()).thenReturn(session);

        spySQL = spy(new SQL(spyDBManager));
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void CheckValidCommandIsExecutedTest() throws SQLException {
        when(spyDBManager.getStandardSqlConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        when(resultSetMetaData.getColumnCount()).thenReturn(0);
        when(resultSet.next()).thenReturn(false);

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
}
