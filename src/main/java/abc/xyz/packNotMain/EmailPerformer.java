package abc.xyz.packNotMain;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

//выполнение задания
public class EmailPerformer{

    private static final Logger logger = Logger.getGlobal();

    public void executeAllTasks(Connection connect, Table tableTask, Table tableReg) throws SQLException {

        //отправка, удаление и запись делается в транзакции
        connect.setAutoCommit(false);

        Statement stmt = connect.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableTask.getName());

        ArrayList<Integer> lineID = new ArrayList<>();

        try {

            while (rs.next()) {
                String str = rs.getString("task_text");
                //"Отправка" письма
                //System.out.println("Отправляется: " + str + "....");
                logger.info("Отправляется: " + str + "....");
                lineID.add(rs.getInt(tableTask.getPrimaryKey()));
            }

            //очистка таблицы "отправленных" писем
            for (Integer x : lineID) {
                //System.out.println("ИД задания на удаление: " + x);
                logger.info("ИД задания на удаление: " + x);
                stmt.addBatch("DELETE FROM " + tableTask.getName() + " WHERE " + tableTask.getPrimaryKey() + " = " + x);
            }
            stmt.executeBatch();

            //запись в отдельной таблице о дате "отправки"
            PreparedStatement ps = connect.prepareStatement("INSERT INTO " + tableReg.getName() + " (reg_id, reg_date) VALUES (?,?)");
            for (Integer x : lineID) {
                ps.setInt(1, x);
                ps.setDate(2, new Date(System.currentTimeMillis()));
                ps.addBatch();
            }
            ps.executeBatch();

            stmt.executeBatch();

            //подтверждение транзакции
            connect.commit();

            rs.close();
            stmt.close();
        }
        catch (SQLException e){
            //откат транзакции
            connect.rollback();
        }

    }
}
