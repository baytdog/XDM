package com.pointlion.mvc.common.utils.office.excel;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DbImportExcelUtils {
	@SuppressWarnings("deprecation")
	public static String getCellStringValue(Cell cell) {
        String cellValue = "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: // 数字
                DecimalFormat df = new DecimalFormat("0");
                cellValue = df.format(cell.getNumericCellValue());
                break;

            case Cell.CELL_TYPE_STRING: // 字符串
                cellValue = cell.getStringCellValue();
                break;

            case Cell.CELL_TYPE_BOOLEAN: // Boolean
                cellValue = cell.getBooleanCellValue() + "";
                break;

            case Cell.CELL_TYPE_FORMULA: // 公式
                cellValue = cell.getCellFormula() + "";
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
        return cellValue;
    }

    public static String formatgetCellStringValue(Cell cell) {
        if(cell.getCellTypeEnum()==CellType.NUMERIC){
            if(HSSFDateUtil.isCellDateFormatted(cell)){
                SimpleDateFormat sdf = null;
                if (cell.getCellStyle().getDataFormat() == HSSFDataFormat
                        .getBuiltinFormat("h:mm")) {
                    sdf = new SimpleDateFormat("HH:mm");
                } else {// 日期
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                }
                Date date = cell.getDateCellValue();
                return sdf.format(date);
            }
        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }


    public static String getStringValue(Cell cell) {
       // cell.setCellType(CellType.STRING);
        if(cell.getCellTypeEnum()==CellType.NUMERIC){
            short format = cell.getCellStyle().getDataFormat();
            if(HSSFDateUtil.isCellDateFormatted(cell)){
                SimpleDateFormat sdf = null;
                if (cell.getCellStyle().getDataFormat() == HSSFDataFormat
                        .getBuiltinFormat("h:mm")) {
                    sdf = new SimpleDateFormat("HH:mm");
                } else {// 日期
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                }
           /*     Date date = cell.getDateCellValue();
                return sdf.format(date);*/
                DataFormatter sdt =new DataFormatter();
                return  sdt.formatCellValue(cell);

            }
          /*  System.out.println(String.valueOf(cell.getNumericCellValue()));
            return  String.valueOf(cell.getNumericCellValue());*/
            DecimalFormat df = new DecimalFormat("0");
            return df.format(cell.getNumericCellValue());
        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }
}
