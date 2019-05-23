package abc.xyz.packNotMain;

import java.util.HashMap;
import java.util.Map;

//шаблон таблицы БД
public class Table{

    private String name;
    private HashMap<String,String> columns;
    private String primaryKey;

    public Table(String name, HashMap<String,String> columns){

        this.name = name;
        this.columns = columns;

    }
    public Table(String name, HashMap<String,String> columns, String primaryKey){

        this.name = name;
        this.columns = columns;
        this.primaryKey = primaryKey;

    }

    //текст для запроса создания новой таблицы в БД
    public String getTextCreateNewTable(){

        String str = "CREATE TABLE " + name;

        str = str + " (";
        boolean first = true;
        for(Map.Entry<String,String> colum : columns.entrySet()){

            if(first == false){
                str = str + ", ";
            }
            else{
                first = false;
            }

            str = str + colum.getKey() + " " + colum.getValue();

        }
        if(primaryKey.isEmpty() == false) {
            str = str + ", primary key (" + primaryKey + "))";
        }
        else{
            str = str + ")";
        }

        return str;
    }

    public String getName(){
        return name;
    }

    public String getPrimaryKey(){
        return primaryKey;
    }

    public boolean columExists(String columName){

        return columns.containsKey(columName);

    }

}
