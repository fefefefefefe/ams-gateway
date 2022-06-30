package utils.impAndExpData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 导入dmp文件
 */
public class ImpDmp {

    public static void main(String[] args) {
        String user = "system";
        String psw = "pass1009";
        String database = "10.10.23.21:1521/orcl"; // IP要指向数据库服务器的地址
        String tableName = "USERS_TEST"; // 数据表名称
        String file = "F://rb_project//ctl//users.dmp";
        impFile(user, tableName, psw, database, file);
    }

    public static void impFile(String user, String table, String psw, String database, String file) {

        String[] cmds = new String[3];
        // tables不是必填
        String commandBuf = "imp " + user + "/" + psw + "@" + database + "tables=" + table + " fromuser="
                + user + "touser=" + user + " file=" + file + "ignore=y";

        cmds[0] = "cmd";

        cmds[1] = "/C";

        cmds[2] = commandBuf.toString();

        Process process = null;

        try {

            process = Runtime.getRuntime().exec(cmds);

        } catch (IOException e) {

            e.printStackTrace();

        }

        boolean shouldClose = false;

        try {

            InputStreamReader isr = new InputStreamReader(process.getErrorStream());

            BufferedReader br = new BufferedReader(isr);

            String line = null;

            while ((line = br.readLine()) != null) {

                System.out.println(line);

                if (line.indexOf("????") != -1) {

                    shouldClose = true;

                    break;

                }

            }

        } catch (IOException ioe) {

            shouldClose = true;

        }

        if (shouldClose)

            process.destroy();

        int exitVal;

        try {

            exitVal = process.waitFor();

            System.out.print(exitVal);

        } catch (InterruptedException e) {

            e.printStackTrace();

        }

    }

}
