package utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.RowTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelListener extends AnalysisEventListener {

    // 表头
    private List<String> topRow = new ArrayList<>();
    // 获取数据
    private List<String[]> rows = new ArrayList<>();
    // 错误信息
    private String errorMsg = "";
    // 错误标志
    private boolean errorFlag = false;

    /**
     * 获取每一行的数据
     *
     * @param data    数据对象
     * @param context 分析控制器
     */
    @Override
    public void invoke(Object data, AnalysisContext context) {
        // 空行校验
        context.readWorkbookHolder().setIgnoreEmptyRow(false);
        Map<Integer, Object> da = (Map<Integer, Object>) data;
        List<String> rowData = new ArrayList<>();
        // 按照顺序获取数据
        int size = da.keySet().size();
        for (int i = 0; i < size; i++) {
            rowData.add(da.get(i) != null && !"null".equals(da.get(i)) ? da.get(i).toString() : null);
        }
        // 添加到行数据中
        rows.add(rowData.toArray(new String[]{}));
    }


    /**
     * 判断是否有下一行
     *
     * @param context
     * @return
     */
    @Override
    public boolean hasNext(AnalysisContext context) {
        if (RowTypeEnum.EMPTY.equals(context.readRowHolder().getRowType())) {
            doAfterAllAnalysed(context);
            return false;
        }
        return super.hasNext(context);
    }

    /**
     * 整个Excel全部加载完成后事件
     *
     * @param context 分析控制器
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("共计：" + rows.size());
    }

    /**
     * 获取到表头之后回调
     *
     * @param headMap 表头数据
     * @param context 分析控制器
     */
    @Override
    public void invokeHeadMap(Map headMap, AnalysisContext context) {
        int size = headMap.keySet().size();
        for (int i = 0; i < size; i++) {
            topRow.add(headMap.get(i).toString());
        }
    }

    /**
     * 在转换异常 获取其他异常下会调用本接口。抛出异常则停止读取。如果这里不抛出异常则 继续读取下一行。
     *
     * @param exception
     * @param context
     * @throws Exception
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        System.out.println("解析失败，但是继续解析下一行:{}" + exception.getMessage());
        // 失败标志
        errorFlag = true;
        // 如果是某一个单元格的转换异常 能获取到具体行号
        // 如果要获取头的信息 配合invokeHeadMap使用
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) exception;
            errorMsg += String.format("第%s行，第%s列解析异常", excelDataConvertException.getRowIndex(),
                    excelDataConvertException.getColumnIndex());
        }
    }


    public List<String> getTopRow() {
        return topRow;
    }


    public List<String[]> getRows() {
        return rows;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public boolean isErrorFlag() {
        return errorFlag;
    }

}
