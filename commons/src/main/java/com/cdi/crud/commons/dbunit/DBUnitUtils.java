package com.cdi.crud.commons.dbunit;

import org.apache.deltaspike.core.api.exclude.Exclude;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.operation.DatabaseOperation;
import org.jboss.arquillian.persistence.dbunit.dataset.yaml.YamlDataSet;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

@Exclude
public class DBUnitUtils {

    private static DataSource ds;

    private static DatabaseConnection databaseConnection;

    public static void createDataset(String dataset) {

        if (!dataset.startsWith("/")) {
            dataset = "/" + dataset;
        }
        try {
            initConn();
            DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, new YamlDataSet(DBUnitUtils.class.getResourceAsStream(dataset)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("could not initialize dataset:" + dataset + " \nmessage: " + e.getMessage());
        } finally {
            closeConn();
        }
    }

    public static void deleteDataset(String dataset) {
        if (!dataset.startsWith("/")) {
            dataset = "/" + dataset;
        }
        try {
            initConn();
            DatabaseOperation.DELETE_ALL.execute(databaseConnection, new YamlDataSet(DBUnitUtils.class.getResourceAsStream(dataset)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("could not delete dataset dataset:" + dataset + " \nmessage: " + e.getMessage());
        } finally {
            closeConn();
        }
    }

    private static void closeConn() {
        try {
            if (databaseConnection != null && !databaseConnection.getConnection().isClosed()) {
                databaseConnection.getConnection().close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("could not close conection \nmessage: " + e.getMessage());
        }

    }

    private static void initConn() throws SQLException, NamingException, DatabaseUnitException {
        if (ds == null) {
            ds = (DataSource) new InitialContext().lookup("java:jboss/datasources/ExampleDS");
        }
        databaseConnection = new DatabaseConnection(ds.getConnection());
    }

    public static void createRemoteDataset(URL context, String dataset) {
        HttpURLConnection con = null;
        try {
            URL obj = new URL(context + "rest/dbunit/create/" + dataset);

            con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            con.setDoOutput(true);

            int responseCode = con.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Could not create remote dataset\nstatus:" + responseCode + "\nerror:" + con.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    public static void deleteRemoteDataset(URL context, String dataset) {
        HttpURLConnection con = null;
        try {
            URL obj = new URL(context + "rest/dbunit/delete/" + dataset);

            con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            con.setDoOutput(true);

            int responseCode = con.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Could not create remote dataset\nstatus:" + responseCode + "\nerror:" + con.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }
}
