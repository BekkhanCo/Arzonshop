package sample.dbAccess;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.dbase.dbConnection;
import sample.models.BaseModel;
import sample.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBAccessObject {
    Connection connection;
    PreparedStatement pst;
    ResultSet rst;
    dbConnection dbConnection = new dbConnection();
    public static int p_id;

    protected static DBCallback dbCallback;

    public HashMap<String, SimpleObjectProperty<Object>> getMapOne(String sql, List<SimpleObjectProperty<Object>> sql_values) throws Exception {
        return getMapOne(sql, sql_values, false);
    }

    public HashMap<String, SimpleObjectProperty<Object>> getMapOne(String sql, List<SimpleObjectProperty<Object>> sql_values, boolean transaction) throws Exception {
        if (dbCallback != null) {
            dbCallback.handle(sql, sql_values);
            dbCallback = null;
        }
        ResultSet resultSet = executeQuery(sql, sql_values);
        if (resultSet == null) {
            return null;
        }
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int col_len = resultSetMetaData.getColumnCount();
        if (resultSet.next()) {
            HashMap<String, SimpleObjectProperty<Object>> map = new HashMap<>();
            for (int i = 1; i <= col_len; i++) {
                if (resultSetMetaData.getColumnTypeName(i).toUpperCase().contains("BLOB")) {
                    map.put(resultSetMetaData.getColumnLabel(i), new SimpleObjectProperty<>(resultSet.getBinaryStream(resultSetMetaData.getColumnLabel(i))));
                } else
                    map.put(resultSetMetaData.getColumnLabel(i), new SimpleObjectProperty<>(resultSet.getString(resultSetMetaData.getColumnLabel(i))));

            }
            resultSet.getStatement().close();
            resultSet.close();
            return map;
        }
        return null;
    }


    public void executeUpdate(String sql) {
        try {
            connection = dbConnection.getConnection();
            pst = connection.prepareStatement(sql);
            pst.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            close();
        }
    }

    private void close() {
        try {
            connection.close();
            pst.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void closeAll() {
        try {
            connection.close();
            pst.close();
            rst.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public <T> ObservableList<T> getDataList(Class klass, String sql) {
        ObservableList<T> list = FXCollections.observableArrayList();
        try {
            connection = dbConnection.getConnection();
            pst = connection.prepareStatement(sql);
            rst = pst.executeQuery();
            ResultSetMetaData metaData = pst.getMetaData();
            BaseModel model;
            HashMap<String, SimpleObjectProperty<Object>> map;
            while (rst.next()) {
                model = (BaseModel) klass.newInstance();
                map = new HashMap<>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    map.put(metaData.getColumnLabel(i), new SimpleObjectProperty<>(rst.getString(i)));
                }
                model.setRow(map);
                list.add((T) model);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return list;
    }

    public HashMap<String, SimpleObjectProperty<Object>> getDataListOne(String sql, List<SimpleObjectProperty<Object>> list) {
        try {
            rst = executeQuery(sql, list);
            ResultSetMetaData metaData = rst.getMetaData();
            HashMap<String, SimpleObjectProperty<Object>> map;

            while (rst.next()) {
                map = new HashMap<>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    map.put(metaData.getColumnLabel(i), new SimpleObjectProperty<>(rst.getString(i)));
                }
                return map;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            close();
        }
        return null;
    }

    public <T> ObservableList<T> getDataMapList(Class modelCalss, String sql, List<SimpleObjectProperty<Object>> list) {
        ObservableList<T> mapList = FXCollections.observableArrayList();
        try {
            rst = executeQuery(sql, list);
            ResultSetMetaData metaData = rst.getMetaData();
            HashMap<String, SimpleObjectProperty<Object>> map;
            int nomer = 1;
            BaseModel model;
            while (rst.next()) {
                model = (BaseModel) modelCalss.newInstance();
                map = new HashMap<>();
                map.put("nomer", new SimpleObjectProperty<>(nomer));
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    map.put(metaData.getColumnLabel(i), new SimpleObjectProperty<>(rst.getString(i)));
                }
                model.setRow(map);
                mapList.add((T) model);
                nomer++;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } finally {
            closeAll();
        }
        return mapList;
    }

    private ResultSet executeQuery(String sql, List<SimpleObjectProperty<Object>> list) {
        try {
            connection = dbConnection.getConnection();
            pst = connection.prepareStatement(sql);
            sqlValues(pst, list);
            return pst.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private void sqlValues(PreparedStatement preparedStatement, List<SimpleObjectProperty<Object>> list) {
        try {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getValue() instanceof Integer) {
                    pst.setInt(i + 1, (Integer) (list.get(i).getValue()));
                } else if (list.get(i).getValue() instanceof Double) {
                    pst.setDouble(i + 1, (Double) (list.get(i).getValue()));
                } else if (list.get(i).getValue() instanceof String) {
                    pst.setString(i + 1, (String) (list.get(i).getValue()));
                } else pst.setNull(i + 1, 1);

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    int id;

    public int insertData(String sql, ArrayList<SimpleObjectProperty<Object>> list) {
        id = 0;
        try {
            connection = dbConnection.getConnection();
            pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            sqlValues(pst, list);
            if (pst.executeUpdate() > 0) {
                rst = pst.getGeneratedKeys();
                while (rst.next()) {
                    id = rst.getInt(1);
                    return id;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            close();
        }
        return 0;
    }

    public int updateData(String sql, ArrayList<SimpleObjectProperty<Object>> list) {
        try {
            connection = dbConnection.getConnection();
            pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            sqlValues(pst, list);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            close();
        }
        return 0;
    }

    public static boolean InsertBuyurtma(double mah_id, double sotish, double soni, String date, double soat, double check_id, double delta) {
        boolean b = false;
        dbConnection connectionBase = new dbConnection();
        Connection connection = connectionBase.getConnection();
        String sql = "INSERT INTO `sotuv` (`mah_id`, `sotish`, `soni`, `sana`,`soat`,`check_id`,`delta`) VALUES ('" + mah_id + "','" + sotish + "','" + soni + "','" + date + "'," + soat + ",'" + check_id + "'," + delta + ")";

        Statement statement = null;
        try {
            statement = connection.createStatement();
            b = statement.execute(sql);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return b;
    }

    public static boolean SpisatBuyurtma(int mah_id, int soni, String date, String soat, String smen) {
        boolean b = false;
        dbConnection connectionBase = new dbConnection();
        Connection connection = connectionBase.getConnection();
        String sql = "INSERT INTO `spisat` (`m_id`, `soni`, `sana`,`soat`,`smen`) VALUES ('" + mah_id + "','" + soni + "','" + date + "','" + soat + "','" + smen + "')";
        Statement statement = null;
        try {
            statement = connection.createStatement();
            b = statement.execute(sql);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return b;
    }

    public boolean check(String password) {
        boolean b = false;
        try {
            connection = dbConnection.getConnection();
            pst = connection.prepareStatement("SELECT * FROM `users` WHERE pass='" + password + "'");
            rst = pst.executeQuery();
            while (rst.next()) {
                b = true;
                p_id = rst.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            close();
        }
        return b;
    }

    ObservableList<User> users = FXCollections.observableArrayList();

    public User checkUser(String password, String User) {
        connection = dbConnection.getConnection();
        String Sql = "SELECT * FROM `users` WHERE pass='" + password + "' AND uname='" + User + "'";
        users = getDataList(User.class, Sql);
        if (users.size() != 0) {
            return users.get(0);
        }
        return null;
    }

    public ObservableList<HashMap<String, SimpleObjectProperty<Object>>> executeMapList(String sql, List<SimpleObjectProperty<Object>> sql_values) throws Exception {
        return executeMapList(sql, sql_values, false);
    }

    public ObservableList<HashMap<String, SimpleObjectProperty<Object>>> executeMapList(String sql, List<SimpleObjectProperty<Object>> sql_values, boolean transaction) throws Exception {

        ResultSet resultSet = executeQuery(sql, sql_values);
        if (resultSet == null) {
            return FXCollections.emptyObservableList();
        }
        ObservableList<HashMap<String, SimpleObjectProperty<Object>>> list = FXCollections.observableArrayList();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int col_len = resultSetMetaData.getColumnCount();
        HashMap<String, SimpleObjectProperty<Object>> map;
        int t = 1;
        while (resultSet.next()) {
            map = new HashMap<>();
            map.put("t_r", new SimpleObjectProperty<>(t));
            for (int i = 1; i <= col_len; i++) {
                if (resultSetMetaData.getColumnTypeName(i).toUpperCase().contains("BLOB")) {
                    map.put(resultSetMetaData.getColumnLabel(i), new SimpleObjectProperty<>(resultSet.getBinaryStream(resultSetMetaData.getColumnLabel(i))));
                } else
                    map.put(resultSetMetaData.getColumnLabel(i), new SimpleObjectProperty<>(resultSet.getString(resultSetMetaData.getColumnLabel(i))));
            }

            t++;
            list.add(map);
        }
        resultSet.getStatement().close();
        resultSet.close();
        return list;
    }

}
