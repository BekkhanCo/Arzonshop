package sample.dbase;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DBTransaction {

    Connection connection;
    boolean save=false;

    public DBTransaction(Connection dbConnection) {
        connection=dbConnection;
        save=false;
        try {
            doDBTransaction();
            commit();
            save=true;
        } catch (Exception e) {

            e.printStackTrace();
            rollback();

        }
    }

    public abstract void doDBTransaction() throws Exception;


    public void commit() throws SQLException {
        connection.commit();
    }

    public void rollback(){
        try {
            connection.rollback();
            save=false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isSave(){
        return save;
    }

}
