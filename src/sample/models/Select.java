package sample.models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import sample.dbAccess.DBAccessObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Select extends RecursiveTreeObject<BaseModel> {
    protected String tablename = "";

    DBAccessObject accessObject = new DBAccessObject();
    protected String sql;
    protected List<String> columns=new ArrayList<>();
    ArrayList<SimpleObjectProperty<Object>> values = new ArrayList<>();
    protected HashMap<String, SimpleObjectProperty<Object>> row = new HashMap<>();

    public Select select() {
        sql = "select * from " + tablename + " ";
        return this;
    }

    public Select where(String column, Object value) {
        sql = sql + "where " + column + "=? ";
        values.add(new SimpleObjectProperty(value));
        return this;
    }

    public Select andWhere(String column, Object value) {
        sql = sql + "and " + column + "=? ";
        values.add(new SimpleObjectProperty(value));
        return this;
    }

    public <T> T getFirst() {
        row = accessObject.getDataListOne(sql, values);
        return (T) this;
    }


    public <T> ObservableList<T> all(){
        select();
//        row=accessObject.getDataListOne(sql,values);
        return accessObject.getDataMapList(this.getClass(), sql, values);
    }

    public <T> T find(int id) {
        select().where("id", id).getFirst();
        return (T) this;
    }
}
