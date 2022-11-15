package com.pointlion.mvc.admin.grap.url.specialgetdata.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.jfinal.kit.StrKit;
import com.pointlion.mvc.admin.grap.url.specialgetdata.SpecialGetDataInterface;
import com.pointlion.mvc.common.model.GrapUrl;
import com.pointlion.mvc.common.utils.DateUtil;

public class QilushihuaHuanbaoshuiGetData2 implements SpecialGetDataInterface{

	@Override
	public Map<String, Integer> getData(GrapUrl o,WebClient webclient,String startDate,String endDate) throws Exception {
		doLogin(webclient);
		String year = "2018";
		for(int i=1;i<=12;i++){
			String start = "";
			String end = "";
			String day = DateUtil.getLastDayOfMonth(Integer.parseInt(year),i);
			String month = "";
			if(i<10){
				month = "0"+i;
			}else{
				month = i+"";
			}
			start = year+"-"+month+"-01";
			end = day;
			//废气
			getGas(webclient, start,end,year,month);
			getWater1(webclient, start, end, year, month, "http://10.110.1.210/hjjc/lab_ws/WebFrm_tjyb1.aspx", "E:/环保税导出数据/齐鲁石化检测机构废水检测数据1/");
		}
			getWater2(webclient, year, "http://10.110.1.210/hjjc/report_2016/report_lab_ws_qfx_tjyb.aspx", "E:/环保税导出数据/齐鲁石化检测机构废水检测数据2/");
		return null;
	}

	/***
	 * 获取废气
	 * @param webclient
	 */
	@SuppressWarnings("resource")
	public void getGas(WebClient webclient,String startDate,String endDate,String year,String month)throws Exception{
		if(webclient!=null){
			HtmlPage p = webclient.getPage("http://10.110.1.210/hjjc/report_2016/report_yq_jcjg.aspx");
			DomElement start = p.getElementById("datestr1");
			start.setAttribute("value", startDate);
			DomElement end = p.getElementById("datestr2");
			end.setAttribute("value", endDate);
			DomElement Button1 = p.getElementById("Button1");
			HtmlPage ppp = Button1.click();

			String html = ppp.asXml();
			Document document = Jsoup.parse(html);//转成DOM格式方便解析
			Element date = document.getElementById("lb_rq");
			String queryDate = date.text();//查询日期
			Element main = document.getElementById("DataGrid1");
			if(main!=null){
				List<Element> trList =  main.getElementsByTag("tr");
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
				CellRangeAddress cras=new CellRangeAddress(0, 0, 0,trList.get(0).getElementsByTag("td").size()-1);
			    sheet.addMergedRegion(cras);
			    sheet.setColumnWidth(0, 300*16);
			    sheet.setColumnWidth(1, 300*16);
			    sheet.setColumnWidth(2, 300*16);
			    sheet.setColumnWidth(3, 300*16);
			    sheet.setColumnWidth(4, 300*16);
			    sheet.setColumnWidth(5, 300*16);
			    sheet.setColumnWidth(6, 300*16);
			    sheet.setColumnWidth(7, 300*16);
			    sheet.setColumnWidth(8, 300*16);
			    sheet.setColumnWidth(9, 300*16);
				HSSFCell cell = row1.createCell(0);
				cell.setCellValue(queryDate);
				cell.setCellStyle(style);
				//动态创建表头和数据
				int i = 1;//行号
				for(Element e:trList){
					HSSFRow row = sheet.createRow(i);
					List<Element> tdList = e.getElementsByTag("td");
					int j = 0;
					String paifangkou = "";
					for(Element td:tdList){
						HSSFCell c = row.createCell(j);
						c.setCellStyle(style);
						c.setCellValue(td.text());
						if(j==2){
							paifangkou = td.text();
						}
						j++;
					}
					try{
						String path = "E:/环保税导出数据/齐鲁石化检测机构废气检测数据/";
						File pathFile = new File(path);
						if(!pathFile.exists()){
							pathFile.mkdirs();
						}
						String filePath = path+paifangkou+"_"+year+month+".xls";
						FileOutputStream fout = new FileOutputStream(filePath);
						wb.write(fout);
						fout.close();
					}catch (Exception e1){
						e1.printStackTrace();
					}
					i++;
				}
			}
		}
	}

	/***
	 * 获取废水
	 * @param webclient
	 */
	public void getWater(WebClient webclient,String startDate,String endDate,String year,String month)throws Exception{

	}
	public void getWater1(WebClient webclient,String startDate,String endDate,String year,String month,String url,String pathName)throws Exception{
		if(webclient!=null){
			HtmlPage p = webclient.getPage(url);
			DomElement start = p.getElementById("datestr1");
			start.setAttribute("value", startDate);
			DomElement end = p.getElementById("datestr2");
			end.setAttribute("value", endDate);
			DomElement Button1 = p.getElementById("Button1");
			HtmlPage ppp = Button1.click();

			String html = ppp.asXml();
			Document document = Jsoup.parse(html);//转成DOM格式方便解析
			Element date = document.getElementById("lb_rq");
			String queryDate = date.text();//查询日期
			Element main = document.getElementById("DataGrid1");
			if(main!=null){
				List<Element> trList =  main.getElementsByTag("tr");
				makeExcel(trList, queryDate, year, month, pathName);
			}
		}
	}
	public void getWater2(WebClient webclient,String year,String url,String pathName)throws Exception{
		if(webclient!=null){
			List<String> monthList = new ArrayList<String>();
			monthList.add("01");
			monthList.add("04");
			monthList.add("07");
			monthList.add("10");
			for(String month:monthList){
				HtmlPage p = webclient.getPage(url);

				HtmlSelect startYear = (HtmlSelect)p.getElementById("ddl_year1");
				startYear.setDefaultValue(year);
				HtmlSelect startMonth = (HtmlSelect)p.getElementById("ddl_month1");
				startMonth.setDefaultValue(month);
				HtmlSelect endYear = (HtmlSelect)p.getElementById("ddl_year2");
				endYear.setDefaultValue(year);
				HtmlSelect endMonth = (HtmlSelect)p.getElementById("ddl_month2");
				endMonth.setDefaultValue(month);
				DomElement Button1 = p.getElementById("Button1");
				HtmlPage ppp = Button1.click();
				String html = ppp.asXml();
				System.out.println(html);
				Document document = Jsoup.parse(html);//转成DOM格式方便解析
				Element date = document.getElementById("lb_rq");
				String queryDate = date.text();//查询日期
				Element main = document.getElementById("DataGrid1");
				if(main!=null){
					List<Element> trList =  main.getElementsByTag("tr");
					makeExcel(trList, queryDate, year, month, pathName);
				}
			}

		}
	}
	public void makeExcel(List<Element> trList,String queryDate,String year,String month,String pathName) throws Exception{
		//创建excel标题
		HSSFWorkbook wb = createExcelModel(trList, queryDate);
		HSSFSheet sheet = wb.getSheetAt(0);
		//动态创建表头和数据
		int i = 1;//行号   从第一行开始
		Map<String,String> span = new HashMap<String,String>();//   列号：合并行的。开始行：结束行
//		List<String> howManyOrg = new ArrayList<String>();
		for(Element e:trList){
			HSSFRow row = sheet.createRow(i);
			List<Element> tdList = e.getElementsByTag("td");
			int j = 0;//列号
			for(Element td:tdList){
				//当前行有合并单元格的情况的话
				String nowSpan = span.get(j+"");//当前列,合并行的数量
				if(StrKit.notBlank(nowSpan)){
					//在合并的单元格区间内
			    	if(i>=Integer.parseInt(nowSpan.split(":")[0])&&i<=Integer.parseInt(nowSpan.split(":")[1])){
			    		//列号后移一位
			    		j++;
			    	}
			    }
				//插入单元格
				HSSFCell c = row.createCell(j);
				c.setCellValue(td.text());
				//组织合并单元格信息
				//合并的行数量
				String spanNum = td.attr("rowspan");
				if(StrKit.notBlank(spanNum)){
					int endSpan = i+Integer.parseInt(spanNum)-1;
					String value = i+":"+endSpan;
					span.put(j+"", value);//
					//执行合并单元格
					CellRangeAddress spAdd=new CellRangeAddress(i, endSpan, j,j);
				    sheet.addMergedRegion(spAdd);
//				    //第0列的数据存起来
//				    if(j==0){
//				    	howManyOrg.add(value+":"+td.text());
//				    }
				}
				j++;
			}
			i++;
		}
		String path = pathName;
		File pathFile = new File(path);
		if(!pathFile.exists()){
			pathFile.mkdirs();
		}
		String filePath = path+"全部_"+year+month+".xls";
		FileOutputStream fout = new FileOutputStream(filePath);
		wb.write(fout);
		fout.close();
		//拆分大的excel
		splitExcel(wb, year, month, trList, queryDate,pathName);

	}

	/***
	 * 拆分excel
	 * @param wb
	 * @param year
	 * @param month
	 * @param trList
	 * @param queryDate
	 * @param pathName
	 * @throws Exception
	 */
	public void splitExcel(HSSFWorkbook wb,String year,String month,List<Element> trList,String queryDate,String pathName)throws Exception{
		List<String> howManyOrg = new ArrayList<String>();
		String path = pathName;
		File pathFile = new File(path);
		if(!pathFile.exists()){
			pathFile.mkdirs();
		}
		HSSFSheet sheet = wb.getSheetAt(0);
		String orgname = "";
		String thisvalue = "";
		int s = 2;
		int e = 2;
		Boolean first = false;
		for(int r=2;r<=sheet.getLastRowNum();r++){//循环行--第二行开始
				HSSFCell c = sheet.getRow(r).getCell(0);
				if(c==null){//同一个单位的
					e = e + 1;
				}else{
					String orgnameTemp = c.getStringCellValue();
					if(first){
						thisvalue = s+":"+e+":"+orgname; //上一个的
						howManyOrg.add(thisvalue);
						s = r;
						e = r;
					}
					orgname = orgnameTemp;//不同单位了
					first = true;
				}
				if(r==sheet.getLastRowNum()){
					if(c==null){//同一个单位的
						e = e + 1;
						thisvalue = s+":"+e+":"+orgname; //上一个的
						howManyOrg.add(thisvalue);
					}else{
						String orgnameTemp = c.getStringCellValue();
						thisvalue = s+":"+e+":"+orgnameTemp; //上一个的
						howManyOrg.add(thisvalue);
					}
				}
		}
		for(String h : howManyOrg){//循环创建文件
			HSSFWorkbook wbNew = createExcelModel(trList, queryDate);
			HSSFSheet sheetNew = wbNew.getSheetAt(0);
			String arr[] = h.split(":");
			int start = Integer.parseInt(arr[0]);
			int end = Integer.parseInt(arr[1]);
			int r = 2;//从第三行开始
			for(int i=start;i<=end;i++){
				HSSFRow rowold = sheet.getRow(i);
				if(rowold!=null){
					HSSFRow row = sheetNew.createRow(r);//创建行
					for(int c=0;c<=rowold.getLastCellNum();c++){//创建列
						HSSFCell cc = row.createCell(c);
						if(rowold.getCell(c)==null){
							cc.setCellValue("");
						}else{
							cc.setCellValue(rowold.getCell(c).getStringCellValue());
						}
					}
				}
				r++;
			}
			String filePath = path+arr[2]+"_"+year+month+".xls";
			FileOutputStream fout = new FileOutputStream(filePath);
			wbNew.write(fout);
			fout.close();
		}
	}

	/***
	 * 登录
	 * @param webclient
	 * @throws Exception
	 */
	public void doLogin(WebClient webclient)throws Exception{
		HtmlPage loginpage = webclient.getPage("http://10.110.1.210/hjjc/login.aspx");//登录页面
		HtmlInput username = loginpage.getHtmlElementById("Txt_userid");//用户名
        HtmlInput pwd = loginpage.getHtmlElementById("Password1");//密码
        webclient.waitForBackgroundJavaScriptStartingBefore(20000);//设置js加载时间
        HtmlInput imgLogin = (HtmlInput)loginpage.getElementById("ImageButton1");
        username.setAttribute("value", "lilei.qlsh");//账号
        pwd.setAttribute("value", "lI7588491");//密码
        imgLogin.click();//登录之后的页面
//      indexPage.save(new File("E:/1.html"));
	}

	/***
	 * 创建空excel
	 * @param trList
	 * @param queryDate
	 */
	public HSSFWorkbook createExcelModel(List<Element> trList,String queryDate){
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
		CellRangeAddress cras=new CellRangeAddress(0, 0, 0,trList.get(0).getElementsByTag("td").size()-1);
	    sheet.addMergedRegion(cras);
	    for(int ii=0;ii<=trList.get(0).getElementsByTag("td").size()-1;ii++){
	    	sheet.setColumnWidth(ii, 300*16);
	    }
		HSSFCell cell = row1.createCell(0);
		cell.setCellValue(queryDate);
		cell.setCellStyle(style);
		Element e = trList.get(0);
		HSSFRow row = sheet.createRow(1);
			List<Element> tdList = e.getElementsByTag("td");
			int j = 0;//列号
			for(Element td:tdList){
				//当前行有合并单元格的情况的话
				HSSFCell c = row.createCell(j);
				c.setCellValue(td.text());
				j++;
			}
		return wb;
	}
}
