package sample.models;
import sample.dbAccess.DBAccessObject;

public class Delete extends BaseModel {

    public static void delete(String sql) {
        new DBAccessObject().executeUpdate(sql);
    }

    public static void delete(BaseModel model, String id) {
        String sql = "delete from `" + model.tablename + "` where id = " + id;
        System.out.println(sql);
        new DBAccessObject().executeUpdate(sql);
    }

}
