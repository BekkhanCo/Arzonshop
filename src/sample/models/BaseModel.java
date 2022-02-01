package sample.models;

import javafx.beans.property.SimpleObjectProperty;
import sample.dbAccess.DBAccessObject;

import java.util.HashMap;

public class BaseModel extends Select {

    public String get(String coloumn) {
        return row.get(coloumn) == null ? null : row.get(coloumn).getValue() == null ? null : String.valueOf(row.get(coloumn).getValue());
    }

    public void setRow(HashMap<String, SimpleObjectProperty<Object>> row) {
        this.row = row;
    }

    public void Set(String key, Object value) {
        row.put(key, new SimpleObjectProperty<>(value));
    }

    public Integer getId() {
        return get("id") == null ? null : Integer.parseInt(get("id"));
    }

    public int save() {
        int ID=0;
        if (getId() == null) {
            ID=insert();
        } else {
            update("id");
        }
        return ID;
    }

    public void save(String key) {
         update(key);
    }

    private int insert() {
        sql = "insert into " + tablename + " (";
        String keys = "";
        String keyValues = " values(";
        for (String key : row.keySet()) {
            keys += "`" + key + "`, ";
            keyValues += "?, ";
            values.add(row.get(key));
        }
        System.out.println(values);
        sql = sql + keys.substring(0, keys.length() - 2) + ")" + keyValues.substring(0, keyValues.length() - 2) + ")";
        System.out.println(sql);

        accessObject = new DBAccessObject();

        row.put("id", new SimpleObjectProperty(accessObject.insertData(sql, values)));
        System.out.println(getId());
        return getId();
    }


    private void update(String key){
        sql="update "+tablename+" set ";
        String keys1="";
        String keyValue="";
        for (String key1: row.keySet()){
            if(!key1.equals(key)){
                keys1+=""+key1+"=?, ";
                values.add(row.get(key1));
            }else keyValue=get(key);
        }
        sql=sql+keys1.substring(0,keys1.length()-2)+" where "+key+"="+keyValue;
        System.out.println(sql);

        accessObject=new DBAccessObject();
        row.put("upId",new SimpleObjectProperty(accessObject.insertData(sql,values)));
    }


    public SimpleObjectProperty getProperty(String column){
        return row.get(column);
    }
}
