package com.pointlion.mvc.admin.grap.url.specialgetdata.impl;

import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.grap.url.specialgetdata.SpecialGetDataInterface;
import com.pointlion.mvc.common.model.GrapUrl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class QilushihuaFangchanshuiImpl implements SpecialGetDataInterface{

	@Override
	public Map<String, Integer> getData(GrapUrl o,WebClient webclient,String startDate,String endDate) throws Exception {
		//首页
		HtmlPage page = webclient.getPage("http://banshui.sd-n-tax.gov.cn/EnterpriseDzswjMainAction.do?activity=dzswj_ts_ywlx&ywlx=&scdm=SY_TCYW_FYXXCJSB&qxdm=&ssval=");
		HtmlElement e = page.getElementsByTagName("ul").get(1).getElementsByTagName("a").get(0);
		HtmlPage p = e.click();
		String pStr = p.asXml();
		String temp = pStr.substring(pStr.indexOf("tmp_code"));
		String tmp_code = temp.substring(temp.indexOf("=")+1, temp.indexOf("\""));
		//所有数据的json接口
		String url = "http://wb.taxcloud.sdds.gov.cn/sbzs-cjpt-web/biz/sbzs/fcssbcj/xInitData?zsxmDm=10110&ywbm=FCSSBCJ&gdslxDm=2&channelId=C03&tmp_code="+tmp_code+"&lx=1&gsdq=237&_random=0.8785460857854426&_bizReq_path_=sbzs%2Ffcssbcj&_guideParam=zsxmDm-10110%3Bywbm-FCSSBCJ%3BgdslxDm-2%3BchannelId-C03%3Btmp_code-"+tmp_code+"%3Blx-1%3Bgsdq-237";
		Document d = Jsoup.connect(url).get();
		String sss = d.text();
//		System.out.println(sss);
		JSONObject obj = JSONObject.fromObject(sss);
		String objBody = obj.getString("body");
//		System.out.println(objBody);
		JSONObject body = JSONObject.fromObject(objBody);
		JSONArray arr = body.getJSONObject("fwxxGrid").getJSONArray("fwxxGridlb");
		for(int i=0;i<arr.size();i++){
			JSONObject fc = arr.getJSONObject(i);
			String fybh = fc.getString("fybh")==null?"":fc.getString("fybh");//房源编号
			Record r = Db.use("Oracle_QLSH_SWGL_KF_153").findFirst("select * from GRAP_HOUSE_TAX_DATA where id = '"+fybh+"'");
			if(r==null){
				String fwcqzsh = fc.getString("fwcqzsh")==null?"":fc.getString("fwcqzsh");//产权证书号
				String fcmc = fc.getString("fcmc")==null?"":fc.getString("fcmc");//房产名称
				String xzqhszMc = fc.getString("xzqhszMc")==null?"":fc.getString("xzqhszMc");//坐落地址，行政区域
				String jdxzMc = fc.getString("jdxzMc")==null?"":fc.getString("jdxzMc");//坐落街道
				String fwzldz = fc.getString("fwzldz")==null?"":fc.getString("fwzldz");//详细地址
				String zgswskfjMc = fc.getString("zgswskfjMc")==null?"":fc.getString("zgswskfjMc");//所属主管税务所
				String csqdsj = fc.getString("csqdsj")==null?"":fc.getString("csqdsj");//房产取得时间
				String jzmj = fc.getString("jzmj")==null?"":fc.getString("jzmj");//建筑面积
				//先打开页面--准备找iframe--找房产原值
				String sql = "INSERT INTO GRAP_HOUSE_TAX_DATA"
						+ "(ID,FC_CQ_DOC_NUM,FC_NAME,FC_ADDRESS,FC_ADDRESS2,FC_ADDRESS_DETAIL,FC_TAX_ORG,FC_GET_TIME,FC_ACREAGE,FC_WORTH,FC_TYPE) "
						  + "VALUES ('"+fybh+"','"+fwcqzsh+"','"+fcmc+"','"+xzqhszMc+"','"+jdxzMc+"','"+fwzldz+"','"+zgswskfjMc+"','"+csqdsj+"','"+jzmj+"','','1')";
				Db.use("Oracle_QLSH_SWGL_KF_153").update(sql);
			}
		}
		//房产原值，单独抓取
		//js等待时间
		webclient.getPage("http://wb.taxcloud.sdds.gov.cn/sbzs-cjpt-web/biz/sbzs/fcssbcj?lx=1&zsxmDm=10110&gdslxDm=2&channelId=C03&tmp_code="+tmp_code);
		getYz(webclient,tmp_code);//第一页
		return null;
	}
	
	public void getYz(WebClient webclient,String tmp_code) throws Exception{
		//找iframe找房产原值
		HtmlPage p22 = null;
		boolean ifgo = true;
		webclient.waitForBackgroundJavaScript(10000);
		while(ifgo){
			try{
			   p22 = (HtmlPage)webclient.getWebWindowByName("frmSheet").getEnclosedPage();
			   if(p22!=null&&(p22.getElementById("cjPageFyxx")!=null)){
				   ifgo = false;
			   }else{
				   ifgo = true;
			   }
			}catch(Exception e22){
				ifgo = true;
			}
		}
		getyzData(p22);//第一页
		HtmlAnchor a = p22.getAnchorByText("下一页");
		String sclass = a.getAttribute("class");
		boolean whileFor = true;
		while(whileFor){
			a.click();//点击下一页
			p22 = (HtmlPage)webclient.getWebWindowByName("frmSheet").getEnclosedPage();
			getyzData(p22);
			a = p22.getAnchorByText("下一页");
			sclass = a.getAttribute("class");
			if("active ng-hide".equals(sclass)){
				whileFor = false;
			}
		}
	}
	public void getyzData(HtmlPage p22)throws Exception{
		List<HtmlElement> trarr = p22.getElementById("cjPageFyxx").getElementsByTagName("tr");
		for(HtmlElement tr:trarr){
			if(tr.getElementsByTagName("td").get(1).getElementsByTagName("input")!=null&&tr.getElementsByTagName("td").get(1).getElementsByTagName("input").size()>0){
				DomElement fjbhNode = tr.getElementsByTagName("td").get(1).getElementsByTagName("input").get(0);
				String bh =  fjbhNode.getAttribute("value");
				System.out.println(bh);
				Record r = Db.use("Oracle_QLSH_SWGL_KF_153").findFirst("select * from GRAP_HOUSE_TAX_DATA where ID='"+bh+"'");
				if(r!=null){
					HtmlPage p33 = tr.getElementsByTagName("a").get(1).click();
					List<HtmlElement> inputlist = p33.getElementById("cjjzsymxb_table").getElementsByTagName("table").get(0).getElementsByTagName("input");
					for(HtmlElement fcyz:inputlist){
						if("p.fcyz".equals(fcyz.getAttribute("ng-model"))){
							String yz = fcyz.getAttribute("value");
							Db.use("Oracle_QLSH_SWGL_KF_153").update("update GRAP_HOUSE_TAX_DATA set FC_WORTH='"+yz+"' where ID='"+bh+"'");
							break;
						}
					}
				}
			}
		}
	}
}
