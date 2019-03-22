package com.gsoft.portal.api.controller;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.util.Assert;
import com.zaxxer.hikari.util.DriverDataSource;

import io.swagger.annotations.ApiOperation;

/**
 * 导出数据库中所有数据表结构
 * 
 * @author chenxx
 *
 */
@RestController
@RequestMapping("/site")
public class ExportTbXlsxController {

	/**
	 * 本地临时目录
	 */
	public static String tmpRootPath = System.getProperty("java.io.tmpdir");

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${spring.datasource.driver-class-name}")
	private String driver;

	@ApiOperation("导出数据库中表结构")
	@RequestMapping(value = "/exportTableInfo", method = RequestMethod.GET)
	public ReturnDto ExportXlsx(String databaseName) throws SQLException, FileNotFoundException, IOException {
		String filename = "cos3数据库设计.xlsx";
		export(databaseName, filename);
		return new ReturnDto("导出成功！");
	}

	/**
	 * 导出Excel
	 * 
	 * @param databaseName
	 * @param filename
	 * @throws SQLException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void export(String databaseName, String filename) throws SQLException, FileNotFoundException, IOException {

		Properties properties = new Properties();
		DataSource dataSource = new DriverDataSource(url, driver, properties, username, password);
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		FileOutputStream fileOutputStream = null;
		Connection con = dataSource.getConnection();// 获得连接
		Statement st = con.createStatement();

		rs = st.executeQuery("SELECT TABLE_COMMENT, TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where table_schema = '"
				+ databaseName + "' ");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> tmpMap = null;
		while (rs.next()) {
			tmpMap = new HashMap<String, Object>();
			tmpMap.put("tableName", rs.getString("TABLE_NAME"));
			tmpMap.put("tableComment", rs.getString("TABLE_COMMENT"));
			list.add(tmpMap);
		}

		Workbook book = new XSSFWorkbook();
		StringBuffer sb = new StringBuffer();
		Sheet sheet = book.createSheet("数据库设计");// 创建一个表格

		int rowNum = 0;
		for (Map<String, Object> map : list) {
			System.out.println("tbName is : " + map.get("tableName"));
			sb = new StringBuffer();
			sb.append("SELECT (@rowNO := @rowNo + 1) AS 编号, a.COLUMN_NAME AS 列名, a.DATA_TYPE AS 字段类型, a.CHARACTER_MAXIMUM_LENGTH AS 长度,");
			sb.append("a.IS_NULLABLE AS 是否必填, a.COLUMN_DEFAULT AS 默认值, a.COLUMN_COMMENT AS 备注 FROM INFORMATION_SCHEMA.COLUMNS a,");
			sb.append("(SELECT @rowNO := 0) b WHERE a.table_schema = '" + databaseName + "' ");
			sb.append("AND a.table_name = '" + map.get("tableName") + "' ");

			rs = st.executeQuery(sb.toString());
			rsmd = rs.getMetaData();
			int coloums = rsmd.getColumnCount();
			sheet.autoSizeColumn(1, true);

			// 表名部分
			sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 6));
			Row tableNameRow = sheet.createRow(rowNum++);
			Cell headCell = tableNameRow.createCell(0);
			
			if (!Assert.isEmpty(map.get("tableComment"))) {
				headCell.setCellValue(map.get("tableName") + "(" + map.get("tableComment") + ")");
			} else {
				headCell.setCellValue(map.get("tableName")+"");
			}
			
			CellStyle tableNameStyle = book.createCellStyle();
			tableNameStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			Font font = book.createFont();
			font.setFontName("微软雅黑");
			font.setFontHeightInPoints((short) 16);
			tableNameStyle.setFont(font);
			tableNameStyle.setBorderBottom(CellStyle.BORDER_THIN); // 下边框
			tableNameStyle.setBorderRight(CellStyle.BORDER_THIN);// 右边框
			headCell.setCellStyle(tableNameStyle);

			// 表头
			Row row = sheet.createRow(rowNum++);
			CellStyle titleStyle = book.createCellStyle();
			for (int i = 0; i < coloums; i++) {
				titleStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
				titleStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
				titleStyle.setBorderBottom(CellStyle.BORDER_THIN); // 下边框
				titleStyle.setBorderLeft(CellStyle.BORDER_THIN);// 左边框
				titleStyle.setBorderTop(CellStyle.BORDER_THIN);// 上边框
				titleStyle.setBorderRight(CellStyle.BORDER_THIN);// 右边框
				Font font1 = book.createFont();
				font1.setFontName("微软雅黑");
				font1.setFontHeightInPoints((short) 11);
				titleStyle.setFont(font1);
				Cell cell = row.createCell(i);
				cell.setCellValue(rsmd.getColumnLabel(i + 1));
				cell.setCellStyle(titleStyle);
			}

			CellStyle contentStyle = book.createCellStyle();
			contentStyle.setBorderBottom(CellStyle.BORDER_THIN); // 下边框
			contentStyle.setBorderLeft(CellStyle.BORDER_THIN);// 左边框
			contentStyle.setBorderTop(CellStyle.BORDER_THIN);// 上边框
			contentStyle.setBorderRight(CellStyle.BORDER_THIN);// 右边框
			Font font2 = book.createFont();
			font2.setFontName("微软雅黑");
			font2.setFontHeightInPoints((short) 11);
			contentStyle.setFont(font2);

			// 内容
			while (rs.next()) {
				Row row2 = sheet.createRow(rowNum++);
				for (int i = 0; i < coloums; i++) {
					Cell cell2 = row2.createCell(i);
					cell2.setCellValue(rs.getString(i + 1));
					cell2.setCellStyle(contentStyle);
				}
			}
			rowNum++;
		}

		if (!tmpRootPath.endsWith("/")) {
			tmpRootPath = tmpRootPath + "/";
		}
		fileOutputStream = new FileOutputStream(tmpRootPath + filename);
		book.write(fileOutputStream);

		if (fileOutputStream != null) {
			fileOutputStream.close();
		}

		if (st != null) {
			st.close();
		}

		if (rs != null) {
			rs.close();
		}

		if (con != null) {
			con.close();
		}
	}
}
