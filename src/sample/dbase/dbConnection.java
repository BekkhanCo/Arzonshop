package sample.dbase;

import javafx.scene.control.Alert;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class dbConnection {
//    private String host="WIN-2BPLBNR309G";
//    private int port=3306;
//    private String database="desertshop";
//    private String user="arzonshop";
//    private String parol="14237856";

    private String host="localhost";
    private int port=3306;
    private String database="dukon";
    private String user="root";
    private String parol="";

    public dbConnection() {

        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("Report/settings.prop"));
            host = properties.getProperty("HOST");
            port = Integer.parseInt(properties.getProperty("PORT"));
            database  = properties.getProperty("DATABASE");
            user= properties.getProperty("USER");
            parol= properties.getProperty("PAROL");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//
//    private String host="VERTU-PC";
//    private int port=3308;
//    private String database="desertshop";
//    private String user="arzonshop";
//    private String parol="14237856";

//    private static String host="WIN-QN1BN4EJNKB";
//    private int port=3306;
//    private String database="desertshop";
//    private String user="desert";
//    private String parol="123";

//    private static String host="141.8.192.93";
//    private int port=3306;
//    private String database="a0551053_desertshop";
//    private String user="a0551053_desertshop";
//    private String parol="bryan6808";
    Connection connection;

    public Connection getConnection(){

        try {
            connection= DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database
                    +"?verifyServerCertificate=false&useSSl=false",user,parol);
        }  catch (SQLException throwables) {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(throwables+"");
            alert.show();
            throwables.printStackTrace();
        }

        return connection;
    }

}
