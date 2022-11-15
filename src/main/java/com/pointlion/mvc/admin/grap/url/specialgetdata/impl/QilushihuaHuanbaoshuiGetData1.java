package com.pointlion.mvc.admin.grap.url.specialgetdata.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.grap.url.specialgetdata.SpecialGetDataInterface;
import com.pointlion.mvc.common.model.GrapDbSource;
import com.pointlion.mvc.common.model.GrapUrl;
import com.pointlion.mvc.common.utils.DateUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class QilushihuaHuanbaoshuiGetData1 implements SpecialGetDataInterface{

	@Override
	public Map<String, Integer> getData(GrapUrl o,WebClient webclient,String startMonth,String endMonth) throws Exception {
		if(webclient!=null){
			List<String> list = new ArrayList<String>();
			list.add("宏达热电");
			list.add("蓝帆医疗3-4号");
			list.add("蓝帆医疗5-7号");
			list.add("蓝帆医疗天然气排放口");
			list.add("齐鲁一化肥1");
			list.add("齐鲁一化肥2");
			list.add("齐鲁一化肥3");
			list.add("齐鲁一化肥4");
			list.add("齐鲁一化肥");

			GrapDbSource ds = GrapDbSource.dao.getById(o.getDbSourceId());
			String dbconfigname = ds.getDbConfigName();
			Record r1 = Db.use(dbconfigname).findFirst("select * from HBS_PP_PZ where F_FWLJ='HBJ_A' ");//废气存放路径
			String p1 = r1.getStr("F_CFLJ");
			Record r2 = Db.use(dbconfigname).findFirst("select * from HBS_PP_PZ where F_FWLJ='HBJ_W' ");//废水存放路径
			String p2 = r2.getStr("F_CFLJ");
			List<String> l = DateUtil.getMonthBetween(startMonth, endMonth);
			for(String s:list){//遍历排放口
				String ens = URLEncoder.encode(s,"UTF-8");
				for(String month:l){//遍历月份
					String startDay = "";
					String endDay = "";
					String lastDay = DateUtil.getLastDayOfMonth(month);
					startDay = month+"-01+00%3A00%3A00";
					endDay   =  lastDay+"+23%3A59%3A59";
					//废气
					makeGas(webclient,o ,s, month, ens, startDay, endDay,p1);
				}
			}
			for(String s:list){//遍历排放口
				String ens = URLEncoder.encode(s,"UTF-8");
				for(String month:l){//遍历月份
					String startDay = "";
					String endDay = "";
					String lastDay = DateUtil.getLastDayOfMonth(month);
					startDay = month+"-01";
					endDay   =  lastDay;
					//废水
					makeWater(webclient,o ,s, month, ens, startDay, endDay,p2);
				}
			}
		}

		return null;
	}


	/***
	 * 废气的
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public void makeGas(WebClient webclient,GrapUrl o,String org,String month,String ens,String start,String end,String p) throws Exception{
		//创建excel标题
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFFont font = wb.createFont();
        font.setFontName("仿宋_GB2312");
        font.setBold(true);//粗体显示
        font.setFontHeightInPoints((short) 8);
		HSSFSheet sheet = wb.createSheet("废气检测结果");
		HSSFRow row1 = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setFont(font);
		CellRangeAddress cras1=new CellRangeAddress(0, 1, 0,0);
	    sheet.addMergedRegion(cras1);
	    CellRangeAddress cras2=new CellRangeAddress(0, 0, 1,3);
	    sheet.addMergedRegion(cras2);
	    CellRangeAddress cras3=new CellRangeAddress(0, 0, 4,6);
	    sheet.addMergedRegion(cras3);
	    CellRangeAddress cras4=new CellRangeAddress(0, 0, 7,9);
	    sheet.addMergedRegion(cras4);
	    CellRangeAddress cras5=new CellRangeAddress(0, 1, 10,10);
	    sheet.addMergedRegion(cras5);
	    CellRangeAddress cras6=new CellRangeAddress(0, 1, 11,11);
	    sheet.addMergedRegion(cras6);
	    CellRangeAddress cras7=new CellRangeAddress(0, 1, 12,12);
	    sheet.addMergedRegion(cras7);
	    HSSFCell cell0 = row1.createCell(0);
	    cell0.setCellValue("检测时间");
	    cell0.setCellStyle(style);
	    HSSFCell cell1 = row1.createCell(1);
	    cell1.setCellValue("二氧化硫");
	    cell1.setCellStyle(style);
	    HSSFCell cell2 = row1.createCell(4);
	    cell2.setCellValue("氮氧化物");
	    cell2.setCellStyle(style);
	    HSSFCell cell3 = row1.createCell(7);
	    cell3.setCellValue("颗粒物");
	    cell3.setCellStyle(style);
	    HSSFCell cell10 = row1.createCell(10);
	    cell10.setCellValue("氧含量(%)");
	    cell10.setCellStyle(style);
	    HSSFCell cell11 = row1.createCell(11);
	    cell11.setCellValue("烟气温度(℃)");
	    cell11.setCellStyle(style);
	    HSSFCell cell12 = row1.createCell(12);
	    cell12.setCellValue("废气排放量(m3/h)");
	    cell12.setCellStyle(style);

	    HSSFRow row2 = sheet.createRow(1);
	    HSSFCell cell4 = row2.createCell(1);
	    cell4.setCellValue("实测浓度(mg/m3)");
	    cell4.setCellStyle(style);
	    HSSFCell cell41 = row2.createCell(2);
	    cell41.setCellValue("折算浓度(mg/m3)");
	    cell41.setCellStyle(style);
	    HSSFCell cell42 = row2.createCell(3);
	    cell42.setCellValue("排放量(kg)");
	    cell42.setCellStyle(style);
	    HSSFCell cell43 = row2.createCell(4);
	    cell43.setCellValue("实测浓度(mg/m3)");
	    cell43.setCellStyle(style);
	    HSSFCell cell45 = row2.createCell(5);
	    cell45.setCellValue("折算浓度(mg/m3)");
	    cell45.setCellStyle(style);
	    HSSFCell cell46 = row2.createCell(6);
	    cell46.setCellValue("排放量(kg)");
	    cell46.setCellStyle(style);
	    HSSFCell cell47 = row2.createCell(7);
	    cell47.setCellValue("实测浓度(mg/m3)");
	    cell47.setCellStyle(style);
	    HSSFCell cell48 = row2.createCell(8);
	    cell48.setCellValue("折算浓度(mg/m3)");
	    cell48.setCellStyle(style);
	    HSSFCell cell49 = row2.createCell(9);
	    cell49.setCellValue("排放量(kg)");
	    cell49.setCellStyle(style);
	    //取出数据
//	    			  http://60.210.111.130:8002/ajax/WasteGas/QueryAnalysis/HistoryReportQUIDYN/HistoryReport.ashx?Method=QueryHistoryReport&subid=1608&subname=%E5%AE%8F%E8%BE%BE%E7%83%AD%E7%94%B5&start=2018-12-01+00%3A00%3A00&end=2018-12-27+23%3A59%3A59&index=1&sort=1&YWGS=&showValidate=0&multiCode=201%2C203%2C207%2C221%2C205%2C222%2C225%2C210&codes=201%2C203%2C207%2C209%2C525%2C210&showUpload=0&page=1&rows=999999
	    String url = "http://60.210.111.130:8002/ajax/WasteGas/QueryAnalysis/HistoryReportQUIDYN/HistoryReport.ashx?Method=QueryHistoryReport&subid=1608&subname="+ens+"&start="+start+"&end="+end+"&index=1&sort=1&YWGS=&showValidate=0&multiCode=201%2C203%2C207%2C221%2C205%2C222%2C225%2C210&codes=201%2C203%2C207%2C209%2C525%2C210&showUpload=0&page=1&rows=999999";
	    TextPage page = webclient.getPage(url);
	    String jsondata = page.getContent();
	    JSONObject jsonobj = JSONObject.fromObject(jsondata);
	    JSONArray arr = jsonobj.getJSONArray("rows");
	    for(int i=0;i<arr.size();i++){
	    	HSSFRow row = sheet.createRow(i+2);
	    	HSSFCell c0 = row.createCell(0);
	    	HSSFCell c1 = row.createCell(1);
	    	HSSFCell c2 = row.createCell(2);
	    	HSSFCell c3 = row.createCell(3);
	    	HSSFCell c4 = row.createCell(4);
	    	HSSFCell c5 = row.createCell(5);
	    	HSSFCell c6 = row.createCell(6);
	    	HSSFCell c7 = row.createCell(7);
	    	HSSFCell c8 = row.createCell(8);
	    	HSSFCell c9 = row.createCell(9);
	    	HSSFCell c10 = row.createCell(10);
	    	HSSFCell c11 = row.createCell(11);
	    	HSSFCell c12 = row.createCell(12);
	    	JSONObject datar = arr.getJSONObject(i);
	    	c0.setCellValue(datar.get("DateTime")+"");
	    	c1.setCellValue(datar.get("val2_201")+"");
	    	c2.setCellValue(datar.get("cvt_201")+"");
	    	c3.setCellValue(datar.get("ex_201")+"");
	    	c4.setCellValue(datar.get("val_203")+"");
	    	c5.setCellValue(datar.get("cvt_203")+"");
	    	c6.setCellValue(datar.get("ex_203")+"");
	    	c7.setCellValue(datar.get("val_207")+"");
	    	c8.setCellValue(datar.get("cvt_207")+"");
	    	c9.setCellValue(datar.get("ex_207")+"");
	    	c10.setCellValue(datar.get("val_209")+"");
	    	c11.setCellValue(datar.get("val_525")+"");
	    	c12.setCellValue(datar.get("val_210")+"");
	    }

	    File path = new File(p);
	    if(!path.exists()){
	    	path.mkdirs();
	    }
	    String filePath = path+"/"+org+"_"+month.replace("-", "")+".xls";
		FileOutputStream fout = new FileOutputStream(filePath);
		wb.write(fout);
		fout.close();

	}

	/***
	 * 废水的
	 */
	@SuppressWarnings("resource")
	public void makeWater(WebClient webclient,GrapUrl o,String org,String month,String ens,String start,String end,String p) throws Exception{
		//创建excel标题
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFFont font = wb.createFont();
        font.setFontName("仿宋_GB2312");
        font.setBold(true);//粗体显示
        font.setFontHeightInPoints((short) 8);
		HSSFSheet sheet = wb.createSheet("废水检测结果");
		HSSFRow row1 = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setFont(font);
		CellRangeAddress cras1=new CellRangeAddress(0, 1, 0,0);
	    sheet.addMergedRegion(cras1);
	    CellRangeAddress cras2=new CellRangeAddress(0, 0, 1,3);
	    sheet.addMergedRegion(cras2);
	    CellRangeAddress cras3=new CellRangeAddress(0, 0, 4,6);
	    sheet.addMergedRegion(cras3);
	    CellRangeAddress cras5=new CellRangeAddress(0, 1, 7,7);
	    sheet.addMergedRegion(cras5);
	    CellRangeAddress cras6=new CellRangeAddress(0, 1, 8,8);
	    sheet.addMergedRegion(cras6);
	    CellRangeAddress cras7=new CellRangeAddress(0, 1, 9,9);
	    sheet.addMergedRegion(cras7);
	    HSSFCell cell0 = row1.createCell(0);
	    cell0.setCellValue("检测时间");
	    cell0.setCellStyle(style);
	    HSSFCell cell1 = row1.createCell(1);
	    cell1.setCellValue("化学需氧量");
	    cell1.setCellStyle(style);
	    HSSFCell cell2 = row1.createCell(4);
	    cell2.setCellValue("氨氮");
	    cell2.setCellStyle(style);
	    HSSFCell cell3 = row1.createCell(7);
	    cell3.setCellValue("废水排放量(m³)");
	    cell3.setCellStyle(style);
	    HSSFCell cell10 = row1.createCell(8);
	    cell10.setCellValue("总磷(mg/l)");
	    cell10.setCellStyle(style);
	    HSSFCell cell11 = row1.createCell(9);
	    cell11.setCellValue("总氮(mg/l)");
	    cell11.setCellStyle(style);

	    HSSFRow row2 = sheet.createRow(1);
	    HSSFCell cell4 = row2.createCell(1);
	    cell4.setCellValue("浓度(mg/L)");
	    cell4.setCellStyle(style);
	    HSSFCell cell41 = row2.createCell(2);
	    cell41.setCellValue("排放量(t)");
	    cell41.setCellStyle(style);
	    HSSFCell cell42 = row2.createCell(3);
	    cell42.setCellValue("有效小时个数");
	    cell42.setCellStyle(style);
	    HSSFCell cell43 = row2.createCell(4);
	    cell43.setCellValue("浓度(mg/L)");
	    cell43.setCellStyle(style);
	    HSSFCell cell45 = row2.createCell(5);
	    cell45.setCellValue("排放量(t)");
	    cell45.setCellStyle(style);
	    HSSFCell cell46 = row2.createCell(6);
	    cell46.setCellValue("有效小时个数");
	    cell46.setCellStyle(style);
	    //取出数据
//	    			  http://60.210.111.130:8002/ajax/WasteWater/QueryAnalysis/HistoryReportQUIDYN/HistoryReport.ashx?Method=QueryHistoryReport&subid=5905&subname=%E9%BD%90%E7%BF%94%E8%85%BE%E8%BE%BE&start=2018-12-01&end=2018-12-27&index=2&sort=1&showValidate=0&multiCode=316%2C311%2C494&showUpload=0&YWGS=&codes=316%2C311%2C494%2C313%2C466%2C302&page=1&rows=99999
//	    		      http://60.210.111.130:8002/ajax/WasteWater/QueryAnalysis/HistoryReportQUIDYN/HistoryReport.ashx?Method=QueryHistoryReport&subid=5905&subname=%E5%AE%8F%E8%BE%BE%E7%83%AD%E7%94%B5&start=2018-05-01+00%3A00%3A00&end=2018-05-31+23%3A59%3A59&index=2&sort=1&showValidate=0&multiCode=316%2C311%2C494&showUpload=0&YWGS=&codes=316%2C311%2C494%2C313%2C466%2C302&page=1&rows=99999
		String url = "http://60.210.111.130:8002/ajax/WasteWater/QueryAnalysis/HistoryReportQUIDYN/HistoryReport.ashx?Method=QueryHistoryReport&subid=5905&subname="+ens+"&start="+start+"&end="+end+"&index=2&sort=1&showValidate=0&multiCode=316%2C311%2C494&showUpload=0&YWGS=&codes=316%2C311%2C494%2C313%2C466%2C302&page=1&rows=99999";
	    TextPage page = webclient.getPage(url);
	    String jsondata = page.getContent();
	    JSONObject jsonobj = JSONObject.fromObject(jsondata);
	    JSONArray arr = jsonobj.getJSONArray("rows");
	    for(int i=0;i<arr.size();i++){
	    	HSSFRow row = sheet.createRow(i+2);
	    	HSSFCell c0 = row.createCell(0);
	    	HSSFCell c1 = row.createCell(1);
	    	HSSFCell c2 = row.createCell(2);
	    	HSSFCell c3 = row.createCell(3);
	    	HSSFCell c4 = row.createCell(4);
	    	HSSFCell c5 = row.createCell(5);
	    	HSSFCell c6 = row.createCell(6);
	    	HSSFCell c7 = row.createCell(7);
	    	HSSFCell c8 = row.createCell(8);
	    	HSSFCell c9 = row.createCell(9);
	    	JSONObject datar = arr.getJSONObject(i);
	    	c0.setCellValue(datar.get("DateTime")+"");
	    	c1.setCellValue(datar.get("val_316")+"");
	    	c2.setCellValue(datar.get("flow_316")+"");
	    	c3.setCellValue(datar.get("sample_316")+"");
	    	c4.setCellValue(datar.get("val_311")+"");
	    	c5.setCellValue(datar.get("flow_311")+"");
	    	c6.setCellValue(datar.get("sample_311")+"");
	    	c7.setCellValue(datar.get("flow_494")+"");
	    	c8.setCellValue(datar.get("val_313")+"");
	    	c9.setCellValue(datar.get("val_466")+"");
	    }

	    File path = new File(p);
	    if(!path.exists()){
	    	path.mkdirs();
	    }
	    String filePath = path+"/"+org+"_"+month.replace("-", "")+".xls";
		FileOutputStream fout = new FileOutputStream(filePath);
		wb.write(fout);
		fout.close();
	}

//	public void makeWater(WebClient webclient,GrapUrl o,String s,String year,String month,String ens,String start,String end){
////					  http://60.210.111.130:8002/ajax/WasteWater/QueryAnalysis/HistoryReportQUIDYN/HistoryReport.ashx?Method=ExportExcel&subid=5905&subname=%E5%AE%8F%E8%BE%BE%E7%83%AD%E7%94%B5&index=2&start=2018-01-01&end=2018-11-31&chkcodes=316%2C311%2C494%2C313%2C466%2C302&YWGS=R&multiCode=316%2C311&childCol=1%2C1&isShowDiv=1&showValidate=0&showUpload=0&sort=1&ShowHourCount=1
////					  http://60.210.111.130:8002/ajax/WasteWater/QueryAnalysis/HistoryReportQUIDYN/HistoryReport.ashx?Method=ExportExcel&subid=5905&subname=%E9%BD%90%E7%BF%94%E8%85%BE%E8%BE%BE&index=2&start=2018-07-19&end=2018-12-20&chkcodes=316%2C311%2C494%2C313%2C466%2C302&YWGS=R&multiCode=316%2C311&childCol=1%2C1&isShowDiv=1&showValidate=0&showUpload=0&sort=1&ShowHourCount=1
//		String url = "http://60.210.111.130:8002/ajax/WasteWater/QueryAnalysis/HistoryReportQUIDYN/HistoryReport.ashx?Method=ExportExcel&subid=5905&subname="+ens+"&index=2&start="+start+"&end="+end+"&chkcodes=316%2C311%2C494%2C313%2C466%2C302&YWGS=R&multiCode=316%2C311&childCol=1%2C1&isShowDiv=1&showValidate=0&showUpload=0&sort=1&ShowHourCount=1";
//		try{
//			UnexpectedPage p = webclient.getPage(url);
//			InputStream is = p.getInputStream();
//			String path = "E:/环保税导出数据/淄博环保局废水监测数据/";
//			File fileP = new File(path);
//			if(!fileP.exists()){
//				fileP.mkdirs();
//			}
//			File file = new File(path+s+"_"+year+month+".xls");
//		    OutputStream os = new FileOutputStream(file);
//		    int bytesRead = 0;
//		    byte[] buffer = new byte[8192];
//		    while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
//		       os.write(buffer, 0, bytesRead);
//		    }
//		    os.close();
//		    is.close();
//		}catch(Exception e){
//			System.out.println("废水："+s+" "+month+"月份没有数据");
//		}
//	}
//	public void makeGas2(WebClient webclient,GrapUrl o,String org,String year,String month,String ens,String start,String end) throws Exception{
		//废气的
//		http://60.210.111.130:8002/ajax/WasteGas/QueryAnalysis/HistoryReportQUIDYN/HistoryReport.ashx?Method=ExportExcel&subid=1608&subname=%E5%AE%8F%E8%BE%BE%E7%83%AD%E7%94%B5&index=1&start=2018-01-01+00%3A00%3A00&end=2018-12-18+11%3A59%3A59&chkcodes=201%2C203%2C207%2C209%2C525%2C210&YWGS=R&multiCode=201%2C203%2C207%2C221%2C205%2C222%2C225&childCol=1%2C1%2C1&isShowDiv=1&showValidate=0&showUpload=0&sort=1&ShowHourCount=1
//		http://60.210.111.130:8002/ajax/WasteGas/QueryAnalysis/HistoryReportQUIDYN/HistoryReport.ashx?Method=ExportExcel&subid=1608&subname=%E5%AE%8F%E8%BE%BE%E7%83%AD%E7%94%B5&index=1&start=2018-01-01+00%3A00%3A00&end=2018-09-30+23%3A59%3A59&chkcodes=201%2C203%2C207%2C209%2C525%2C210&YWGS=R&multiCode=201%2C203%2C207%2C221%2C205%2C222%2C225&childCol=1%2C1%2C1&isShowDiv=1&showValidate=0&showUpload=0&sort=1&ShowHourCount=1

//		String url = "http://60.210.111.130:8002/ajax/WasteGas/QueryAnalysis/HistoryReportQUIDYN/HistoryReport.ashx?Method=ExportExcel&subid=1608&subname="+ens+"&index=1&start="+start+"&end="+end+"&chkcodes=201%2C203%2C207%2C209%2C525%2C210&YWGS=R&multiCode=201%2C203%2C207%2C221%2C205%2C222%2C225&childCol=1%2C1%2C1&isShowDiv=1&showValidate=0&showUpload=0&sort=1&ShowHourCount=1";
//		try{
//			UnexpectedPage p = webclient.getPage(url);
//			InputStream is = p.getInputStream();
//			String path = "E:/环保税导出数据/淄博环保局废气监测数据/";
//			File fileP = new File(path);
//			if(!fileP.exists()){
//				fileP.mkdirs();
//			}
//			File file = new File(path+org+"_"+year+month+".xls");
//		    OutputStream os = new FileOutputStream(file);
//		    int bytesRead = 0;
//		    byte[] buffer = new byte[8192];
//		    while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
//		       os.write(buffer, 0, bytesRead);
//		    }
//		    os.close();
//		    is.close();
//		}catch(Exception e){
//			System.out.println("废气："+org+" "+month+"月份没有数据");
//		}
//	}
}
