package com.pointlion.mvc.common.utils.office.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.poi.excel.ExcelWriter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {
	@SuppressWarnings("resource")
	public static List<List<String>> excelToList(String filePath) throws FileNotFoundException, IOException{
		List<List<String>> result = new ArrayList<List<String>>();
        // 读取，全部sheet表及数据
		Workbook workbook = null;
        if (filePath.toLowerCase().endsWith("xls")) {
            workbook = new HSSFWorkbook(new FileInputStream(new File(filePath)));
        } else {
            workbook = new XSSFWorkbook(new FileInputStream(new File(filePath)));
        }
        Sheet sheet = workbook.getSheetAt(0);
        int maxRowNum = sheet.getLastRowNum()+1;
        for (int j = 0; j < maxRowNum; j++) {// 循环所有行
        	List<String> rowList = new ArrayList<String>();
            Row row = sheet.getRow(j);//读取行
            if (row != null) {
                for (int k = 0; k < row.getLastCellNum(); k++) {// getLastCellNum，是获取最后一个不为空的列是第几个
                	String cellStr = row.getCell(k) != null?DbImportExcelUtils.getCellStringValue(row.getCell(k)):"";
                	rowList.add(cellStr);
                }
            }
            result.add(rowList);
        }
        return result;
	}


    /***
     * 数组转excel文件
     * @param path
     * @param rows
     * @return
     */
	public static File listToFile(String path,List<List<String>> rows){
        //通过工具类创建writer
        ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(path);
        //通过构造方法创建writer
        //ExcelWriter writer = new ExcelWriter("d:/writeTest.xls");
        //跳过当前行，既第一行，非必须，在此演示用
        writer.write(rows, true);
        //关闭writer，释放内存
        writer.close();
        return new File(path);
    }


    public static File workExperFile(String path,List<List<String>> rows){
        //通过工具类创建writer
        ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(path);

        writer.write(rows, true);

        writer.merge(0,1,0,0,rows.get(0).get(0),false);
        List<String> secondRow = rows.get(1);
        int secondColumnSize = secondRow.size() / 5;
        for (int i = 0; i < secondColumnSize; i++) {
            writer.merge(0,0,i*5+1,(i+1)*5,rows.get(0).get(i+1),false);
        }

        //关闭writer，释放内存
        writer.close();
        return new File(path);
    }
}
