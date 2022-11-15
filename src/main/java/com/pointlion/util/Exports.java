/**
 * 
 */
package com.pointlion.util;

import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import cn.hutool.poi.excel.ExcelWriter;





/** 
 * @ClassName: Exports 
 * @Description: TODO
 * @date: 2021年5月27日 上午10:26:58  
 */
public class Exports {
	
	public static File exporttest(HttpServletRequest request,HttpServletResponse response,String path) throws Exception {
		
		

		response.setContentType("application/vnd.ms-excel");
		// 文件名
		String fileName = "报表名称.xls";
		// 特殊编码转译
		//fileName = StringUtils.charConversion(fileName);
		// 处理文件名包含特殊字符出现的乱码问题
		String userAgent = request.getHeader("User-Agent");
		if (StringUtils.isNotBlank(userAgent)) {
		    userAgent = userAgent.toLowerCase();
		    if (userAgent.contains("msie") || userAgent.contains("trident") || userAgent.contains("edge")) {
		        if (fileName.length() > 150) {// 解决IE 6.0问题
		            fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
		        } else {
		        	
		            fileName = URLEncoder.encode(fileName, "UTF-8");
		        }
		    } else {
		        fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
		    }
		}
		response.setHeader("Content-disposition", "attachment;filename=\"" + fileName + "\"");
		OutputStream stream = response.getOutputStream();
		// 创建excel文件对象
		HSSFWorkbook wb = new HSSFWorkbook();
		// 创建sheet
		Sheet sheet = wb.createSheet("sheet1");
		//表头字体
		Font headerFont = wb.createFont();
		headerFont.setFontName("微软雅黑");
		headerFont.setFontHeightInPoints((short) 18);
	//	headerFont.setBoldweight(Font.COLOR_NORMAL);
		headerFont.setColor(HSSFColor.BLACK.index);
		//正文字体
		Font contextFont = wb.createFont();
		contextFont.setFontName("微软雅黑");
		contextFont.setFontHeightInPoints((short) 12);
	//	contextFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
		contextFont.setColor(HSSFColor.BLACK.index);
		 
		//表头样式，左右上下居中
		CellStyle headerStyle = wb.createCellStyle();
		headerStyle.setFont(headerFont);
		//headerStyle.setAlignment(CellStyle.);// 左右居中
	//	headerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
		headerStyle.setLocked(true);
		headerStyle.setWrapText(false);// 自动换行
		//单元格样式，左右上下居中 边框
		CellStyle commonStyle = wb.createCellStyle();
		commonStyle.setFont(contextFont);
		//commonStyle.setAlignment(CellStyle.ALIGN_CENTER);// 左右居中
		//commonStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
		commonStyle.setLocked(false);
		commonStyle.setWrapText(false);// 自动换行
		//commonStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		//commonStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
		//commonStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
		//commonStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
		//单元格样式，左右上下居中 边框
		CellStyle commonWrapStyle = wb.createCellStyle();
		commonWrapStyle.setFont(contextFont);
		//commonWrapStyle.setAlignment(CellStyle.ALIGN_CENTER);// 左右居中
		//commonWrapStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
		commonWrapStyle.setLocked(true);
		commonWrapStyle.setWrapText(true);// 自动换行
	/*	commonWrapStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		commonWrapStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
		commonWrapStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
		commonWrapStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
*/		//单元格样式，竖向 边框
		CellStyle verticalStyle = wb.createCellStyle();
		verticalStyle.setFont(contextFont);
	/*	verticalStyle.setAlignment(CellStyle.ALIGN_CENTER);// 左右居中
		verticalStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
*/		verticalStyle.setRotation((short) 255);//竖向
		verticalStyle.setLocked(true);
		verticalStyle.setWrapText(false);// 自动换行
	/*	verticalStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		verticalStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
		verticalStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
		verticalStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
*/		 
		//单元格样式，左右上下居中 无边框
		CellStyle commonStyleNoBorder = wb.createCellStyle();
		commonStyleNoBorder.setFont(contextFont);
/*		commonStyleNoBorder.setAlignment(CellStyle.ALIGN_CENTER);// 左右居中
		commonStyleNoBorder.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
*/		commonStyleNoBorder.setLocked(true);
		commonStyleNoBorder.setWrapText(false);// 自动换行
		//单元格样式，左对齐 边框
		CellStyle alignLeftStyle = wb.createCellStyle();
		alignLeftStyle.setFont(contextFont);
/*		alignLeftStyle.setAlignment(CellStyle.ALIGN_LEFT);// 左对齐
		alignLeftStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
*/		alignLeftStyle.setLocked(true);
		alignLeftStyle.setWrapText(false);// 自动换行
/*		alignLeftStyle.setAlignment(CellStyle.ALIGN_LEFT);// 左对齐
		alignLeftStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		alignLeftStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
		alignLeftStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
		alignLeftStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
*/		//单元格样式，左对齐 无边框
		CellStyle alignLeftNoBorderStyle = wb.createCellStyle();
		alignLeftNoBorderStyle.setFont(contextFont);
/*		alignLeftNoBorderStyle.setAlignment(CellStyle.ALIGN_LEFT);// 左对齐
		alignLeftNoBorderStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
*/		alignLeftNoBorderStyle.setLocked(true);
		alignLeftNoBorderStyle.setWrapText(false);// 自动换行
	//	alignLeftNoBorderStyle.setAlignment(CellStyle.ALIGN_LEFT);// 左对齐
		//单元格样式，右对齐
		CellStyle alignRightStyle = wb.createCellStyle();
		alignRightStyle.setFont(contextFont);
	/*	alignRightStyle.setAlignment(CellStyle.ALIGN_LEFT);// 左对齐
		alignRightStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
*/		alignRightStyle.setLocked(true);
		alignRightStyle.setWrapText(false);// 自动换行
		//alignRightStyle.setAlignment(CellStyle.ALIGN_RIGHT);// 左对齐
		 
		// 填报时间
		String tbsj = "11111111111111";
		// 填报单位
		String tbdwTxt = "222222222222";
		// 填报人
		String cjrxm = "cesgu";
		// 审核人
		String shrxm = "333333333333333333";
		// 年度 季度
		String nd = "44444444444444444444";
		String jd = "55555555555555555";
		 
		// 行号
		int rowNum = 0;
		//设置列宽
		for (int i = 0; i < 7; i++) {
		    sheet.setColumnWidth(i, 6000);
		}
		 
		//第一行
		Row r0 = sheet.createRow(rowNum++);
		r0.setHeight((short) 800);
		Cell c00 = r0.createCell(0);
		c00.setCellValue("66666666666666");
		c00.setCellStyle(headerStyle);
		//合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
		 
		// 第二行
		Row r1 = sheet.createRow(rowNum++);
		r1.setHeight((short) 500);
		String[] row_first = {"填表单位：", "", "", "", "", " 年第 季度：", ""};
		for (int i = 0; i < row_first.length; i++) {
		    Cell tempCell = r1.createCell(i);
		    tempCell.setCellStyle(alignLeftNoBorderStyle);
		    if (i == 0) {
		        tempCell.setCellValue(row_first[i] + tbdwTxt);
		    } else if (i == 5) {
		        tempCell.setCellStyle(alignRightStyle);
		        if (StringUtils.isNotBlank(nd) && StringUtils.isNotBlank(jd))
		            tempCell.setCellValue(nd + "年第" + jd + "季度");
		        else
		            tempCell.setCellValue(row_first[i]);
		    } else {
		        tempCell.setCellValue(row_first[i]);
		    }
		}
		// 合并
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 5, 6));
		 
		//第三行
		Row r2 = sheet.createRow(rowNum++);
		r2.setHeight((short) 700);
		String[] row_second = {"名称", "采集情况", "", "", "登记情况", "", "备注"};
		for (int i = 0; i < row_second.length; i++) {
		    Cell tempCell = r2.createCell(i);
		    tempCell.setCellValue(row_second[i]);
		    tempCell.setCellStyle(commonStyle);
		}
		// 合并
		sheet.addMergedRegion(new CellRangeAddress(2, 3, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 3));
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 5));
		sheet.addMergedRegion(new CellRangeAddress(2, 3, 6, 6));
		 
		//第三行
		Row r3 = sheet.createRow(rowNum++);
		r3.setHeight((short) 700);
		String[] row_third = {"", "登记数(人)", "办证总数(人)", "办证率(%)", "登记户数(户)", "签订数(份)", ""};
		for (int i = 0; i < row_third.length; i++) {
		    Cell tempCell = r3.createCell(i);
		    tempCell.setCellValue(row_third[i]);
		    tempCell.setCellStyle(commonWrapStyle);
		}
		 
		//循环每一行
/*		for (FluidPopulationInfoGatherReportDto excelData : list) {
		    Row tempRow = sheet.createRow(rowNum++);
		    tempRow.setHeight((short) 500);
		    // 循环单元格填入数据
		    for (int j = 0; j < 7; j++) {
		        Cell tempCell = tempRow.createCell(j);
		        tempCell.setCellStyle(commonStyle);
		        String tempValue;
		        if (j == 0) {
		            // 乡镇、街道名称
		            tempValue = excelData.getTbdwTxt();
		        } else if (j == 1) {
		            // 登记数（人）
		            tempValue = excelData.getLdrkdjs();
		        } else if (j == 2) {
		            // 办证总数（人）
		            tempValue = excelData.getJzzbzzs();
		        } else if (j == 3) {
		            // 办证率（%）
		            tempValue = StringUtils.isNotBlank(excelData.getJzzbzl()) ? excelData.getJzzbzl() + "%" : "";
		        } else if (j == 4) {
		            // 登记户数（户）
		            tempValue = excelData.getCzfwdjhs();
		        } else if (j == 5) {
		            // 签订数（份）
		            tempValue = excelData.getZazrsqds();
		        } else {
		            // 备注
		            tempValue = excelData.getRemark();
		        }
		        tempCell.setCellValue(tempValue);
		    }
		}
		 */
		// 注释行
		Row remark = sheet.createRow(rowNum++);
		remark.setHeight((short) 500);
		String[] row_remark = {"注：表中的“办证率=办证总数÷登记数×100%”", "", "", "", "", "", ""};
		for (int i = 0; i < row_remark.length; i++) {
		    Cell tempCell = remark.createCell(i);
		    if (i == 0) {
		        tempCell.setCellStyle(alignLeftStyle);
		    } else {
		        tempCell.setCellStyle(commonStyle);
		    }
		    tempCell.setCellValue(row_remark[i]);
		}
		//int remarkRowNum = list.size() + 4;
		int remarkRowNum =  4;
		// 注
		sheet.addMergedRegion(new CellRangeAddress(remarkRowNum, remarkRowNum, 0, 6));
		 
		// 尾行
		Row foot = sheet.createRow(rowNum++);
		foot.setHeight((short) 500);
		String[] row_foot = {"审核人：", "", "填表人：", "", "填表时间：", "", ""};
		for (int i = 0; i < row_foot.length; i++) {
		    Cell tempCell = foot.createCell(i);
		    tempCell.setCellStyle(alignLeftNoBorderStyle);
		    if (i == 0) {
		        tempCell.setCellValue(row_foot[i] + shrxm);
		    } else if (i == 2) {
		        tempCell.setCellValue(row_foot[i] + cjrxm);
		    } else if (i == 4) {
		        tempCell.setCellValue(row_foot[i] + tbsj);
		    } else {
		        tempCell.setCellValue(row_foot[i]);
		    }
		}
		//int footRowNum = list.size() + 5;
		int footRowNum = 5;
		// 注
		sheet.addMergedRegion(new CellRangeAddress(footRowNum, footRowNum, 0, 1));
		sheet.addMergedRegion(new CellRangeAddress(footRowNum, footRowNum, 2, 3));
		sheet.addMergedRegion(new CellRangeAddress(footRowNum, footRowNum, 4, 6));
		 
		//导出
		try {
		   if (null != wb && null != stream) {
			   
			   ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(path);
		        //通过构造方法创建writer
		        //ExcelWriter writer = new ExcelWriter("d:/writeTest.xls");
		        //跳过当前行，既第一行，非必须，在此演示用
		       // writer.write(rows, true);
		      
		        //关闭writer，释放内存
		        writer.close();
		        wb.write(stream);
			      stream.close();
		      
		     
		   }
		} catch (Exception e) {
		//   logger.error("excel文档导出错误-异常信息：", e);
		}
		  return new File(path);
	}
	
	
	public static File exportCount(HttpServletRequest request,HttpServletResponse response,String path,String []arrys,String eportUser,String times) throws Exception {
		
		

		response.setContentType("application/vnd.ms-excel");
		// 文件名
		String fileName = "来信统计"+times.replace("来信时间", "(")+")"+".xls";
		// 特殊编码转译
		//fileName = StringUtils.charConversion(fileName);
		// 处理文件名包含特殊字符出现的乱码问题
		String userAgent = request.getHeader("User-Agent");
		if (StringUtils.isNotBlank(userAgent)) {
		    userAgent = userAgent.toLowerCase();
		    if (userAgent.contains("msie") || userAgent.contains("trident") || userAgent.contains("edge")) {
		        if (fileName.length() > 150) {// 解决IE 6.0问题
		            fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
		        } else {
		        	
		            fileName = URLEncoder.encode(fileName, "UTF-8");
		        }
		    } else {
		        fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
		    }
		}
		response.setHeader("Content-disposition", "attachment;filename=\"" + fileName + "\"");
		OutputStream stream = response.getOutputStream();
		// 创建excel文件对象
		HSSFWorkbook wb = new HSSFWorkbook();
		// 创建sheet
		Sheet sheet = wb.createSheet("sheet1");
		//表头字体
		Font headerFont = wb.createFont();
		headerFont.setFontName("微软雅黑");
		headerFont.setFontHeightInPoints((short) 18);
	//	headerFont.setBoldweight(Font.COLOR_NORMAL);
		//headerFont.setColor(HSSFColor.BLUE.index);
		headerFont.setColor((short) 8);
		//正文字体
		Font contextFont = wb.createFont();
		contextFont.setFontName("微软雅黑");
		contextFont.setFontHeightInPoints((short) 12);
	//	contextFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
		contextFont.setColor((short)8);
		 
		//表头样式，左右上下居中
		CellStyle headerStyle = wb.createCellStyle();
		headerStyle.setFont(headerFont);
		//headerStyle.setAlignment(CellStyle.);// 左右居中
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
	//	headerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerStyle.setLocked(true);
		headerStyle.setWrapText(false);// 自动换行
		//单元格样式，左右上下居中 边框
		CellStyle commonStyle = wb.createCellStyle();
		commonStyle.setFont(contextFont);
		//commonStyle.setAlignment(CellStyle.ALIGN_CENTER);// 左右居中
		commonStyle.setAlignment(HorizontalAlignment.CENTER);// 左右居中
		//commonStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
		commonStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中
		commonStyle.setLocked(true);
		commonStyle.setWrapText(false);// 自动换行
		//commonStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		//commonStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
		//commonStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
		//commonStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
		//单元格样式，左右上下居中 边框
		CellStyle commonWrapStyle = wb.createCellStyle();
		commonWrapStyle.setFont(contextFont);
		//commonWrapStyle.setAlignment(CellStyle.ALIGN_CENTER);// 左右居中
		commonWrapStyle.setAlignment(HorizontalAlignment.CENTER);// 左右居中
		//commonWrapStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
		commonWrapStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中
		commonWrapStyle.setLocked(true);
		commonWrapStyle.setWrapText(true);// 自动换行
	/*	commonWrapStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		commonWrapStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
		commonWrapStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
		commonWrapStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
*/		//单元格样式，竖向 边框
		CellStyle verticalStyle = wb.createCellStyle();
		verticalStyle.setFont(contextFont);
	/*	verticalStyle.setAlignment(CellStyle.ALIGN_CENTER);// 左右居中
		verticalStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
*/		verticalStyle.setRotation((short) 255);//竖向
		verticalStyle.setLocked(true);
		verticalStyle.setWrapText(false);// 自动换行
	/*	verticalStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		verticalStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
		verticalStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
		verticalStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
*/		 
		//单元格样式，左右上下居中 无边框
		CellStyle commonStyleNoBorder = wb.createCellStyle();
		commonStyleNoBorder.setFont(contextFont);
/*		commonStyleNoBorder.setAlignment(CellStyle.ALIGN_CENTER);// 左右居中
		commonStyleNoBorder.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
*/		commonStyleNoBorder.setLocked(true);
		commonStyleNoBorder.setWrapText(false);// 自动换行
		//单元格样式，左对齐 边框
		CellStyle alignLeftStyle = wb.createCellStyle();
		alignLeftStyle.setFont(contextFont);
/*		alignLeftStyle.setAlignment(CellStyle.ALIGN_LEFT);// 左对齐
		alignLeftStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
*/		alignLeftStyle.setLocked(true);
		alignLeftStyle.setWrapText(false);// 自动换行
/*		alignLeftStyle.setAlignment(CellStyle.ALIGN_LEFT);// 左对齐
		alignLeftStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		alignLeftStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
		alignLeftStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
		alignLeftStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
*/		//单元格样式，左对齐 无边框
		CellStyle alignLeftNoBorderStyle = wb.createCellStyle();
		alignLeftNoBorderStyle.setFont(contextFont);
/*		alignLeftNoBorderStyle.setAlignment(CellStyle.ALIGN_LEFT);// 左对齐
		alignLeftNoBorderStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
*/		alignLeftNoBorderStyle.setLocked(true);
		alignLeftNoBorderStyle.setWrapText(false);// 自动换行
	//	alignLeftNoBorderStyle.setAlignment(CellStyle.ALIGN_LEFT);// 左对齐
		//单元格样式，右对齐
		CellStyle alignRightStyle = wb.createCellStyle();
		alignRightStyle.setFont(contextFont);
	/*	alignRightStyle.setAlignment(CellStyle.ALIGN_LEFT);// 左对齐
		alignRightStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
*/		alignRightStyle.setLocked(true);
		alignRightStyle.setWrapText(false);// 自动换行
		//alignRightStyle.setAlignment(CellStyle.ALIGN_RIGHT);// 左对齐
		 
		// 填报时间
		 
		// 行号
		int rowNum = 0;
		
		 
		//第一行
		Row r0 = sheet.createRow(rowNum++);
		r0.setHeight((short) 800);
		Cell c00 = r0.createCell(0);
		c00.setCellValue(times);
		c00.setCellStyle(headerStyle);
		//合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 18));
		 

		//第三行
		Row r2 = sheet.createRow(rowNum++);
		r2.setHeight((short) 700);
		String[] row_second = {"分类", "参拍", "流转", "额度审核", "app", "居住证", "社保个税", "驾驶证", "名下有额度", "军人","额度有效期", "新能源备案", "投诉", "车管所", "国拍", "经信委","专用车","营运撤辆","总计"};
		
		//设置列宽
		for (int i = 0; i < row_second.length; i++) {
		    sheet.setColumnWidth(i,4000);
		}
		for (int i = 0; i < row_second.length; i++) {
		    Cell tempCell = r2.createCell(i);
		    tempCell.setCellValue(row_second[i]);
		    tempCell.setCellStyle(commonStyle);
		}
		 Row tempRow = sheet.createRow(rowNum++);
		    tempRow.setHeight((short) 500);
		   
		    
		    for (int i = 0; i < arrys.length; i++) {
		    	// 循环单元格填入数据
		        Cell tempCell = tempRow.createCell(i);
		        tempCell.setCellStyle(commonStyle);
		        String tempValue="0";
		      
		        if(arrys[i] !=null) {
		        	tempValue=arrys[i] ;
		        }
		        
		        tempCell.setCellValue(tempValue);
			}
		    

		 
		   SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd hh:MM:ss");
		// 尾行
		Row foot = sheet.createRow(rowNum++);
		foot.setHeight((short) 500);
		String[] row_foot = {"导出人：", "", "", "导出时间：",  "", "","" };
		for (int i = 0; i < row_foot.length; i++) {
		    Cell tempCell = foot.createCell(i);
		    tempCell.setCellStyle(alignLeftNoBorderStyle);
		    if (i == 0) {
		        tempCell.setCellValue(row_foot[i] + eportUser);
		    } else if (i == 3) {
		        tempCell.setCellValue(row_foot[i] + sdf.format(new Date()));
		    }  else {
		        //tempCell.setCellValue(row_foot[i]);
		    }
		}
		//int footRowNum = list.size() + 5;
		int footRowNum = 3;
		// 注
		sheet.addMergedRegion(new CellRangeAddress(footRowNum, footRowNum, 0, 2));
		sheet.addMergedRegion(new CellRangeAddress(footRowNum, footRowNum, 3, 6));
		 
		//导出
		try {
		   if (null != wb && null != stream) {
			   
			   ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(path);
		        //通过构造方法创建writer
		        //ExcelWriter writer = new ExcelWriter("d:/writeTest.xls");
		        //跳过当前行，既第一行，非必须，在此演示用
		       // writer.write(rows, true);
		      
		        //关闭writer，释放内存
		        writer.close();
		        wb.write(stream);
			      stream.close();
		      
		     
		   }
		} catch (Exception e) {
		//   logger.error("excel文档导出错误-异常信息：", e);
		}
		  return new File(path);
	}
	
	
}
