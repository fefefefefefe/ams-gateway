package utils;

import java.io.File;
import java.util.List;

/**
 * @ClassName FileUtils
 * @Description TODO
 * @Author niqj
 * @Date 2022/3/25 10:15
 * @Version 1.0
 **/
public class FileUtils {


    /*
     * 删除文件
     * @param path 文件路径
     */
    public static boolean removeFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("文件不存在！");
            return false;
        }
        dfsdelete(path);
        return true;
    }

    public static void dfsdelete(String path) {
        File file = new File(path);
        if (file.isFile()) {//如果此file对象是文件的话，直接删除
            file.delete();
            return;
        }
        //当 file是文件夹的话，先得到文件夹下对应文件的string数组 ，递归调用本身，实现深度优先删除
        String[] list = file.list();
        for (int i = 0; i < list.length; i++) {
            dfsdelete(path + File.separator + list[i]);

        }//当把文件夹内所有文件删完后，此文件夹已然是一个空文件夹，可以使用delete()直接删除
        file.delete();
        return;
    }
}
