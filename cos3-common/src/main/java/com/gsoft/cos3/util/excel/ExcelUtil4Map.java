package com.gsoft.cos3.util.excel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.gsoft.cos3.exception.BusinessException;
import com.gsoft.cos3.util.Assert;

/* 
 * ExcelUtil4Map工具类实现功能: 
 * 1、导出导入均以Map<String,Object> 为数据对象
 * 2、如需要导入JavaBean对象，请使用原ExcelUtil.java类
 * 
 */
@Component
public class ExcelUtil4Map
{
    public static List<Map<String, Object>> importExcel(String fileName,
            InputStream input) throws Exception
    {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        /** 验证文件是否合法 */
        validateExcel(fileName);
        // 兼容2003和2007
        Workbook workbook = null;
        try
        {
            if (isExcel2007(fileName))
            {
                workbook = new XSSFWorkbook(input);
            }
            else
            {
                workbook = new HSSFWorkbook(input);
            }
        }
        catch (IOException e)
        {
            throw new BusinessException("解析excel失败", e);
        }
        // 可以处理多个sheet
        Sheet sheet = null;
        for (int s = 0; s < workbook.getNumberOfSheets(); s++)
        {
            sheet = workbook.getSheetAt(s);
            int rows = sheet.getPhysicalNumberOfRows();
            if (rows > 0)
            {// 有数据时才处理
                List<String> fieldList = new ArrayList<String>();
                Row fieldHeadRow = sheet.getRow(0);
                int filedLentgh = fieldHeadRow.getLastCellNum();
                for (int i = 0; i < filedLentgh; i++)
                {// 循环出表头
                    fieldList.add(fieldHeadRow.getCell(i).toString());
                }

                for (int i = 1; i < rows; i++)
                {// 从第2行开始取数据,默认第一行是表头.
                    Row row = sheet.getRow(i);
                    Map<String, Object> fieldMaps = new LinkedHashMap<String, Object>();
                    for (int j = 0; j < filedLentgh; j++)
                    {
                        fieldMaps.put(fieldList.get(j),
                                getStringValueCell(row.getCell(j)));
                    }
                    list.add(fieldMaps);
                }
            }
        }
        input.close(); // 关闭流
        return list;
    }

    private static Object getStringValueCell(Cell cell)
    {
        Object cellValue = "";
        DecimalFormat df = new DecimalFormat("0"); // 处理科学计数法，因为double太长，会自动变成科学计数法，所以必须用这种转成String
        if (null != cell)
        {
            // 以下是判断数据的类型
            switch (cell.getCellType())
            {
                case Cell.CELL_TYPE_NUMERIC: // 数字
                    try
                    {
                        /*
                         * 此处判断使用公式生成的字符串有问题，因为HSSFDateUtil.isCellDateFormatted
                         * (cell)判断过程中cell .getNumericCellValue();方法会抛出java.lang
                         * .NumberFormatException异常
                         */
                        if (HSSFDateUtil.isCellDateFormatted(cell))
                        {
                            cellValue = cell.getDateCellValue();
                            break;
                        }
                    }
                    catch (IllegalStateException e)
                    {
                    }
                    cellValue = df.format(cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING: // 字符串

                    try
                    {
                        /*
                         * 此处判断使用公式生成的字符串有问题，因为HSSFDateUtil.isCellDateFormatted
                         * (cell)判断过程中cell .getNumericCellValue();方法会抛出java.lang
                         * .NumberFormatException异常
                         */
                        if (HSSFDateUtil.isCellDateFormatted(cell))
                        {
                            cellValue = cell.getDateCellValue();
                            break;
                        }
                    }
                    catch (IllegalStateException e)
                    {
                    }
                    cellValue = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_BOOLEAN: // Boolean
                    cellValue = cell.getBooleanCellValue() + "";
                    break;
                case Cell.CELL_TYPE_FORMULA: // 公式
                    try
                    {
                        /*
                         * 此处判断使用公式生成的字符串有问题，因为HSSFDateUtil.isCellDateFormatted
                         * (cell)判断过程中cell .getNumericCellValue();方法会抛出java.lang
                         * .NumberFormatException异常
                         */
                        if (HSSFDateUtil.isCellDateFormatted(cell))
                        {
                            cellValue = cell.getDateCellValue();
                            break;
                        }
                        else
                        {
                            // 处理科学计数法
                            cellValue = df.format(cell.getNumericCellValue());
                        }
                    }
                    catch (IllegalStateException e)
                    {
                        cellValue = String
                                .valueOf(cell.getRichStringCellValue());
                    }
                    break;
                case Cell.CELL_TYPE_BLANK: // 空值
                    cellValue = "";
                    break;
                case Cell.CELL_TYPE_ERROR: // 故障
                    cellValue = "非法字符";
                    break;
                default:
                    cellValue = "未知类型";
                    break;
            }
        }
        return cellValue;
    }

    /**
     * 对list数据源将其里面的数据导入到excel表单
     * @param sheetName 工作表的名称
     * @param sheetSize 每个sheet中数据的行数,此数值必须小于65536
     * @param output java输出流
     */
    @SuppressWarnings("deprecation")
    public static boolean exportExcel(List<Map<String, Object>> list,
            String sheetName, int sheetSize, OutputStream output)
    {
        List<String> fieldHeadRow = new ArrayList<String>();
        Map<String, Object> map = list.get(0);
        Iterator<Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext())
        {
            Entry<String, Object> entry = it.next();
            fieldHeadRow.add(entry.getKey());
        }
        HSSFWorkbook workbook = new HSSFWorkbook();// 产生工作薄对象
        // excel2003中每个sheet中最多有65536行,为避免产生错误所以加这个逻辑.
        if (sheetSize > 65536 || sheetSize < 1)
        {
            sheetSize = 65536;
        }
        double sheetNo = Math.ceil(list.size() / sheetSize);// 取出一共有多少个sheet.

        for (int index = 0; index <= sheetNo; index++)
        {
            HSSFSheet sheet = workbook.createSheet();// 产生工作表对象
            if (sheetNo == 0)
            {
                workbook.setSheetName(index, sheetName);
            }
            else
            {
                workbook.setSheetName(index, sheetName + index);// 设置工作表的名称.
            }
            HSSFRow row;
            HSSFCell cell;// 产生单元格
            row = sheet.createRow(0);// 产生一行
            // 写入各个字段的列头名称
            HSSFCellStyle style = workbook.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            for (int i = 0; i < fieldHeadRow.size(); i++)
            {// 文件头
                cell = row.createCell((short) i);
                cell.setCellValue(fieldHeadRow.get(i));
            }
            for (int i = 0; i < list.size(); i++)
            {// 文件内容
                row = sheet.createRow((int) i + 1);
                map = list.get(i);
                for (int j = 0; j < fieldHeadRow.size(); j++)
                {
                    cell = row.createCell((short) j);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    if (Assert.isNotEmpty(map.get(fieldHeadRow.get(j))))
                    {
                        cell.setCellValue(
                                map.get(fieldHeadRow.get(j)).toString());
                    }
                    else
                    {
                        cell.setCellValue("");
                    }
                }
            }
        }
        try
        {
            output.flush();
            workbook.write(output);
            output.close();
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Output is closed ");
            return false;
        }
    }

    /**
     *  可自定义合并单元格的表头导出Excel
     *  [功能详细描述]
     * @param [参数1]     [参数1说明]
     * @param [参数2]     [参数2说明]
     * @return [返回类型说明]
     * @exception/throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean exportMergerExcel(List<Map<String, Object>> dataList,
            List<List<ColumnVO>> columnList, String sheetName, int sheetSize,
            OutputStream output)
    {
        HSSFWorkbook workbook = new HSSFWorkbook();// 产生工作薄对象

        // excel2003中每个sheet中最多有65536行,为避免产生错误所以加这个逻辑.
        if (sheetSize > 65536 || sheetSize < 1)
        {
            sheetSize = 65536;
        }

        double sheetNo = Math.ceil(dataList.size() / sheetSize);// 取出一共有多少个sheet.

        for (int sheetIndex = 0; sheetIndex <= sheetNo; sheetIndex++)
        {
            HSSFSheet sheet = workbook.createSheet();// 产生工作表对象

            sheet.autoSizeColumn(1, true);// 表格列宽自适应

            if (sheetNo == 0)
            {
                workbook.setSheetName(sheetIndex, sheetName);
            }
            else
            {
                workbook.setSheetName(sheetIndex, sheetName + sheetIndex);// 设置工作表的名称.
            }

            // 表头
            HSSFRow row = null;

            // 写入各个字段的列头名称
            HSSFCellStyle style = workbook.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中样式

            Map<String, String> keyMap = new HashMap<String, String>();
            int totalColumn = 0;
            List<String> contains = new ArrayList<String>();
            for (int i = 0; i < columnList.size(); i++)
            {
                List<ColumnVO> columns = columnList.get(i);
                int startColumn = 0;
                row = sheet.createRow(i);
                int size = 0;
                if (i == 0)
                {
                    size = columns.size();
                }
                else
                {
                    size = totalColumn;
                }
                int index = 0;
                int p = 0;
                for (int j = 0; j < size; j++)
                {
                    if (i > 0)
                    {
                        if (p == columns.size())
                        {
                            break;
                        }
                    }

                    if (!contains.contains(startColumn + "," + i))
                    {
                        ColumnVO column = null;

                        if (i == 0)
                        {
                            column = columns.get(j);
                        }
                        else
                        {
                            column = columns.get(j - index);
                        }

                        if (Assert.isNotEmpty(column.getField()))
                        {
                            keyMap.put(String.valueOf(startColumn),
                                    column.getField());

                        }

                        HSSFCell cell = row.createCell(startColumn);

                        cell.setCellStyle(style);

                        if (Assert.isNotEmpty(column.getTitle()))
                        {
                            cell.setCellValue(
                                    column.getTitle().replaceAll("<br>", ""));
                        }
                        else
                        {
                            cell.setCellValue(column.getTitle());
                        }

                        int colspan = 1;
                        if ((Assert.isNotEmpty(column.getColspan())
                                && column.getColspan() > 1))
                        {
                            colspan = column.getColspan();
                        }

                        int rowspan = 1;
                        if (Assert.isNotEmpty(column.getRowspan())
                                && column.getRowspan() > 1)
                        {
                            rowspan = column.getRowspan();
                        }
                        if (colspan > 1 || rowspan > 1)
                        {
                            // 合并单元格(startRow，endRow，startColumn，endColumn)
                            sheet.addMergedRegion(new CellRangeAddress(i,
                                    i + rowspan - 1, startColumn,
                                    startColumn + colspan - 1));
                        }

                        if (rowspan > 1)
                        {
                            for (int k = 0; k < rowspan; k++)
                            {
                                contains.add(startColumn + "," + (k + i));
                            }
                        }

                        startColumn += colspan;
                        p += 1;

                    }
                    else
                    {
                        startColumn += 1;
                        index += 1;
                    }
                }
                if (i == 0)
                {
                    totalColumn = startColumn;
                }
            }

            // 遍历集合
            int rownum = columnList.size();
            if (dataList != null)
            {
                for (int i = 0; i < dataList.size(); i++)
                {
                    int number = 0;
                    row = sheet.createRow(i + rownum);
                    Map<String, Object> map = dataList.get(i);

                    for (int j = 0; j < keyMap.size(); j++)
                    {
                        String key = keyMap.get(String.valueOf(j));

                        if (key.contains(","))
                        {
                            key = key.substring(0, key.indexOf(","));
                        }

                        String value = map.get(key) + "";
                        HSSFCell cell = row.createCell(number);

                        style = workbook.createCellStyle();
                        style.setAlignment(HSSFCellStyle.ALIGN_RIGHT); // 创建一个居中格式

                        cell.setCellStyle(style);

                        cell.setCellValue(value);
                        number++;
                    }
                }
            }
        }

        try
        {
            output.flush();
            workbook.write(output);
            output.close();
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Output is closed ");
            return false;
        }

    }

    private static boolean isExcel2003(String filePath)
    {
        boolean flag = filePath.matches("^.+\\.(?i)(xls)$");
        return flag;
    }

    private static boolean isExcel2007(String filePath)
    {
        Boolean flag = filePath.matches("^.+\\.(?i)(xlsx)$");
        return flag;
    }

    private static void validateExcel(String fileName)
    {
        /** 检查文件名是否为空或者是否是Excel格式的文件 */
        if (Assert.isEmpty(fileName))
        {
            throw new BusinessException("文件名不能为空");
        }
        if (!(isExcel2003(fileName) || isExcel2007(fileName)))
        {
            throw new BusinessException("文件名不是excel格式");
        }
    }

}