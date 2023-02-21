package com.pointlion.mvc.common.utils.office.excel;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
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
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                SimpleDateFormat sdf = null;
                if (cell.getCellStyle().getDataFormat() == HSSFDataFormat
                        .getBuiltinFormat("h:mm")) {
                    sdf = new SimpleDateFormat("HH:mm");
                } else {// 日期
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                }
                Date date = cell.getDateCellValue();
                System.out.println(sdf.format(date));
                return sdf.format(date);
            }

        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }

    public static String getCellValue(Cell cell) {
        System.out.println(cell.getCellTypeEnum());
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                SimpleDateFormat sdf = null;
                if (cell.getCellStyle().getDataFormat() == HSSFDataFormat
                        .getBuiltinFormat("h:mm")) {
                    sdf = new SimpleDateFormat("HH:mm");
                } else {// 日期
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                }
                Date date = cell.getDateCellValue();
                System.out.println(sdf.format(date));
                return sdf.format(date);
            }

        }
        //  FormulaEvaluator formulaEvaluator= new XSSFFormulaEvaluator((XSSFWorkbook) wb);
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }


    public static String getStringValue(Cell cell) {
        // cell.setCellType(CellType.STRING);
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            short format = cell.getCellStyle().getDataFormat();
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                SimpleDateFormat sdf = null;
                if (cell.getCellStyle().getDataFormat() == HSSFDataFormat
                        .getBuiltinFormat("h:mm")) {
                    sdf = new SimpleDateFormat("HH:mm");
                } else {// 日期
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                }
           /*     Date date = cell.getDateCellValue();
                return sdf.format(date);*/
                DataFormatter sdt = new DataFormatter();
                return sdt.formatCellValue(cell);

            }
          /*  System.out.println(String.valueOf(cell.getNumericCellValue()));
            return  String.valueOf(cell.getNumericCellValue());*/
            DecimalFormat df = new DecimalFormat("0");
            return df.format(cell.getNumericCellValue());
        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }


    public static String getCellText(Cell cell) {
        String cellValue = "";
       /* if(cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            switch(cell.getCachedFormulaResultType()) {
                case Cell.CELL_TYPE_NUMERIC:
                   // cell.setCellType(CellType.STRING);
                   // System.out.println("Last evaluated as: " + cell.getStringCellValue());
                    System.out.println("Last evaluated as1111: " + cell.getNumericCellValue());
                    cellValue=String.valueOf(cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    System.out.println("Last evaluated as \"" + cell.getRichStringCellValue() + "\"");
                    cellValue=String.valueOf(cell.getRichStringCellValue());
                    break;
            }
        }else if(  cell.getCellType() == Cell.CELL_TYPE_STRING){// 字符串
            cellValue = cell.getStringCellValue();
        }*/


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
//                  cellValue = cell.getCellFormula() + "";
                switch(cell.getCachedFormulaResultType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        // cell.setCellType(CellType.STRING);
                        // System.out.println("Last evaluated as: " + cell.getStringCellValue());
                        cellValue=String.valueOf(cell.getNumericCellValue());
                        break;
                    case Cell.CELL_TYPE_STRING:
                        cellValue=String.valueOf(cell.getRichStringCellValue());
                        break;
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
        System.out.println(cellValue);
        return cellValue;
    }
}
