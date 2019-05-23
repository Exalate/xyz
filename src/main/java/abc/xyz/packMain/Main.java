package abc.xyz.packMain;

import abc.xyz.packNotMain.EmailDirector;
import abc.xyz.packNotMain.EmailPerformer;
import abc.xyz.packNotMain.Table;

import java.util.HashMap;

import java.sql.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Main {

    private static final Logger logger = Logger.getGlobal();

    public static void main(String[] args) {

        //настройки таблиц бд
        Table tableTask = toFormTableTask();
        Table tableRegistrySentMessageName = toFormTableRegistrySentMes();

        //механизмы создания, исполнения заданий на отправку
        EmailDirector emailDirector = new EmailDirector();
        EmailPerformer emailPerformer = new EmailPerformer();

        try {
            //настройки подключения к бд
            Class.forName("org.postgresql.Driver");//
            String url = "jdbc:postgresql://localhost:5432/testTask";
            String login = "postgres";
            String password = "123123";

            //соедниение
            Connection connect = DriverManager.getConnection(url, login, password);

            try {
                //проверка таблицыСуществуют в бд, иначе создание
                tableCheck(connect, tableTask);
                tableCheck(connect, tableRegistrySentMessageName);

                int result = -1;
                boolean first = true;

                //цикл симуляции создания и исполнения заданий, одно задание всегда появляется при новой итерации
                //последующие в этой же итерации создаются с некоторым шансом
                //при создании нового задания возвращается ID этого задания в БД

                //затем в той же итерации происходит чтение заданий, симуляция отправки,запись этого события отправки
                //в отдельную таблицу бд, с указанием даты(с точностю до дня) и ИД задания

                while (true) {

                    while (first || needDirectNewTask()) {
                        first = false;

                        //создание
                        result = emailDirector.newTask(connect, tableTask);
                        if (result != (-1)) {
                            //System.out.println("Сохранено. ID задания: " + result);
                            logger.info("Сохранено. ID задания: " + result);
                        }
                    }

                    //исполнение
                    emailPerformer.executeAllTasks(connect, tableTask, tableRegistrySentMessageName);

                    //задержка итерации
                    TimeUnit.SECONDS.sleep(3);

                    //УДАЛИТЬ
                    break;

                }

            } finally {
                connect.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //требование создания нового задания
    private static boolean needDirectNewTask() {

        int a = (int) (Math.random() * 3);

        if (a == 0) {
            return false;
        } else {
            return true;
        }

    }

    //проверка что БД содержить таблицу, иначе создает таблицу в БД
    private static void tableCheck(Connection connect, Table table) throws SQLException {

        DatabaseMetaData md = connect.getMetaData();
        ResultSet rs = md.getTables(null, null, table.getName().toLowerCase(), null);

        if(rs.next() == false){  //таблицы нет

            Statement stmt = connect.createStatement();
            stmt.execute(table.getTextCreateNewTable());

            stmt.close();

        }

        rs.close();

    }

    //возвращает параметры конткретной таблицы
    private static Table toFormTableTask(){

        String name = "task_test";

        String primaryKey = "task_id";

        HashMap<String,String> param_columns = new HashMap<>();
        param_columns.put("task_id","serial");
        param_columns.put("task_text","varchar(1000)");

        Table table = new Table(name, param_columns, primaryKey);

        return table;
    }
    private static Table toFormTableRegistrySentMes(){

        String name = "reg_test";

        String primaryKey = "reg_id";

        HashMap<String,String> param_columns = new HashMap<>();
        param_columns.put("reg_id","serial");
        param_columns.put("reg_date","date");

        Table table = new Table(name, param_columns, primaryKey);

        return table;
    }

}



