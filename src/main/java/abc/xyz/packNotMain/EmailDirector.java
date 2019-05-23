package abc.xyz.packNotMain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//создание задания
public class EmailDirector{

    //создание нового задания
    //в БД делается запись "электронного письма" в виде текста, с возвратом ID, который присваивается этому заданию

    public int newTask(Connection connect, Table table) throws SQLException {

        String nameColumnTaskText = "task_text";
        int taskId = -1;

        //проверка что в таблице есть колонка task_text
        if(table.columExists(nameColumnTaskText) == false){
            return taskId;
        }

        String sql = "INSERT INTO " + table.getName() + " (" + nameColumnTaskText + ") VALUES (?);";

        PreparedStatement stmt = connect.prepareStatement(sql, new String[] {table.getPrimaryKey()});

        stmt.setString(1, getTextNewTask());

        stmt.executeUpdate();

        ResultSet gk = stmt.getGeneratedKeys();
        if(gk.next()){
            taskId = gk.getInt(table.getPrimaryKey());
        }

        stmt.close();

        return taskId;

    }

    private String getTextNewTask(){

        String str = "a1";

        return str;

    }

}
