package utils;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Section;
import com.spire.doc.documents.HorizontalAlignment;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.documents.ParagraphStyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 填写并生成word文档
 *
 * @author zhaomiao
 */
public class WordFill {

    /**
     * 写入并下载word文档
     * @param path 文件路径(包含文件名及后缀)
     * @param title 文档标题
     * @param text 文档数据    自动换行：\r\n
     */
    public static void wordFill(String path, String title, List<String> text){

        //创建doc文档
        Document doc = new Document();

        // 添加一个section
        Section section = doc.addSection();
        // 添加一个标题
        Paragraph para1 = section.addParagraph();
        // 设置文档的标题为底稿名称
        para1.appendText(title);
        // 设置标题的对齐方式
        para1.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
        // 设置标题格式
        ParagraphStyle style1 = new ParagraphStyle(doc);
        style1.setName("titleStyle");
        style1.getCharacterFormat().setBold(true);
        style1.getCharacterFormat().setFontSize(15f);
        doc.getStyles().add(style1);
        para1.applyStyle("titleStyle");
        //设置段落的段后间距
        para1.getFormat().setAfterSpacing(15f);
        // 遍历存text信息
        AtomicInteger i = new AtomicInteger(1);
        text.forEach(t -> {
            // 创建一个段落
            Paragraph para2 = section.addParagraph();
            // 添加段落内容
            para2.appendText(i+"、"+t);
            //设置段落的段首缩进
            para2.getFormat().setFirstLineIndent(25f);
            //设置段落的段后间距
            para2.getFormat().setAfterSpacing(10f);
            i.getAndIncrement();
        });
        //保存文档
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(path);
            doc.saveToFile(fileOutputStream, FileFormat.Docx);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
