package com.pointlion.mvc.common.utils.office.excel;

import cn.hutool.poi.excel.ExcelWriter;
import org.apache.http.impl.conn.Wire;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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


    public static List<List<String>> excelToStringList(String filePath) throws FileNotFoundException, IOException{
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
                    String cellStr = row.getCell(k) != null?DbImportExcelUtils.formatgetCellStringValue(row.getCell(k)):"";
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
        int secondColumnSize = secondRow.size() / 6;
        for (int i = 0; i < secondColumnSize; i++) {
            writer.merge(0,0,i*6+1,(i+1)*6,rows.get(0).get(i+1),false);
        }

        //关闭writer，释放内存
        writer.close();
        return new File(path);
    }

    public static File empCertFile(String path,List<List<String>> rows){
        //通过工具类创建writer
        ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(path);

        writer.write(rows, true);

        Sheet sheet = writer.getSheet();


        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, 2, 0, 3, 1);
        XSSFSimpleShape shape = drawing.createSimpleShape(anchor);
        // 设置图形的类型为线
        shape.setShapeType(ShapeTypes.LINE);
        // 设置填充颜色
        shape.setFillColor(0, 0, 0);
        // 设置边框线型：solid=0、dot=1、dash=2、lgDash=3、dashDot=4、lgDashDot=5、lgDashDotDot=6、sysDash=7、sysDot=8、sysDashDot=9、sysDashDotDot=10
        shape.setLineStyle(0);
        // 设置边框线颜色
        shape.setLineStyleColor(0, 0, 0);
        // 设置边框线宽,单位Point
        shape.setLineWidth(1);
        // 对角线单元格内容
        //writer.writeCellValue(0, 0, "地下      土壤");
        int sumtotal=0,sumperson = 0,sumnc = 0,sumwc = 0,sumyb = 0,sumeb = 0,sumzn = 0;
        for (int i = 2; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            sumperson+=Integer.valueOf(row.get(2));
                sumnc+=Integer.valueOf(row.get(3));
                sumwc+=Integer.valueOf(row.get(4));
                sumyb+=Integer.valueOf(row.get(5));
                sumeb+=Integer.valueOf(row.get(6));
                sumzn+=Integer.valueOf(row.get(7));
        }

        sumtotal=sumperson+sumnc+sumwc+sumyb+sumeb+sumzn;

        writer.writeCellValue(1, 1, sumtotal);
        writer.writeCellValue(2, 1, sumperson);
        writer.writeCellValue(3, 1, sumnc);
        writer.writeCellValue(4, 1, sumwc);
        writer.writeCellValue(5, 1, sumyb);
        writer.writeCellValue(6, 1, sumeb);
        writer.writeCellValue(7, 1, sumzn);


        //关闭writer，释放内存
        writer.close();
        return new File(path);
    }

    public static File empCertByTitleFile(String path,List<List<String>> rows){
        //通过工具类创建writer
        ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(path);

        writer.write(rows, true);

        writer.merge(0,0,0,12,rows.get(0).get(0),false);

        //关闭writer，释放内存
        writer.close();
        return new File(path);
    }


    /**
     * @Method conTractFile
     * @param path:
     * @param rows:
     * @Date 2022/12/20 15:47
     * @Exception
     * @Description 合同导出
     * @Author king
     * @Version  1.0
     * @Return java.io.File
     */
    public static File conTractFile(String path,List<List<String>> rows){
        //通过工具类创建writer
        ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(path);

        writer.write(rows, true);
        for (int i = 0; i < 7; i++) {
            writer.merge(0,1,i,i,rows.get(0).get(i),false);
        }


        List<String> secondRow = rows.get(1);
        int secondColumnSize = (secondRow.size()-7) / 4;
        for (int i = 0; i < secondColumnSize; i++) {
            writer.merge(0,0,i*4+7,(i+1)*4+6,rows.get(0).get(i+7),false);
        }

        //关闭writer，释放内存
        writer.close();
        return new File(path);
    }


    public static File scheduletFile(String path,List<List<String>> rows){
        //通过工具类创建writer
        ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(path);

        writer.write(rows, true);
        /*for (int i = 0; i < 7; i++) {
            writer.merge(0,1,i,i,rows.get(0).get(i),false);
        }*/
        int size = rows.get(1).size();
       // writer.merge(0,1,i,i,rows.get(0).get(i),false);
        writer.merge(0,0,0,size-1,rows.get(0).get(0),false);
        for (int i = 0; i <7 ; i++) {
            writer.merge(1,3,i,i,rows.get(1).get(i),false);
        }
        writer.merge(1,3,size-1,size-1,rows.get(1).get(size-1),false);
        List<String> secondRow = rows.get(1);
      /*  int secondColumnSize = (secondRow.size()-7) / 4;
        for (int i = 0; i < secondColumnSize; i++) {
            writer.merge(0,0,i*4+7,(i+1)*4+6,rows.get(0).get(i+7),false);
        }*/

        //关闭writer，释放内存
        writer.close();
        return new File(path);
    }

    public static File attendanceFile(String path,List<List<String>> rows,int dayNum){
        //通过工具类创建writer
        ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(path);

        writer.write(rows, true);
        /*for (int i = 0; i < 7; i++) {
            writer.merge(0,1,i,i,rows.get(0).get(i),false);
        }*/
        int size = rows.get(1).size();
        // writer.merge(0,1,i,i,rows.get(0).get(i),false);
        writer.merge(0,0,0,size-1,rows.get(0).get(0),false);
        for (int i = 0; i <7 ; i++) {
            writer.merge(1,3,i,i,rows.get(1).get(i),false);
        }
//        writer.merge(1,3,size-1,size-1,rows.get(1).get(size-1),false);
        List<String> secondRow = rows.get(1);

      int merColStart=7+dayNum*3;
        for (int i = merColStart; i <merColStart+10 ; i++) {
            writer.merge(1,3,i,i,rows.get(1).get(i),false);
        }
        for (int i = merColStart+10; i <merColStart+13 ; i++) {
            writer.merge(1,2,i,i,rows.get(1).get(i),false);
        }
        for (int i = merColStart+13; i <merColStart+56 ; i++) {
            System.out.println(rows.get(1).get(i));
            writer.merge(1,3,i,i,rows.get(1).get(i),false);
        }

        //writer.merge(1,3,size-1,size-1,rows.get(1).get(size-1),false);

        //关闭writer，释放内存
        writer.close();
        return new File(path);
    }
}
