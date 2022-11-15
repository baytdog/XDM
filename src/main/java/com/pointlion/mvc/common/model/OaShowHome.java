/**
 * 
 */
package com.pointlion.mvc.common.model;

 /** 
 * @ClassName: OaShowHome 
 * @Description: 首页最新发布显示数据
 * @date: 2021年4月8日 下午1:36:20  
 */
public class OaShowHome implements Comparable<OaShowHome> {
	
	public  String id;
	
	public  String showTitle;
	
	public  String tipTitle;
	
	public  String type;
	
	public  String cTime;
	
	
 
	public String getId() {
		return id;
	}



	public String getShowTitle() {
		return showTitle;
	}



	public String getTipTitle() {
		return tipTitle;
	}



	public String getType() {
		return type;
	}



	public String getcTime() {
		return cTime;
	}


	public void setId(String id) {
		this.id = id;
	}




	public void setShowTitle(String showTitle) {
		this.showTitle = showTitle;
	}



	public void setTipTitle(String tipTitle) {
		this.tipTitle = tipTitle;
	}



	public void setType(String type) {
		this.type = type;
	}



	public void setcTime(String cTime) {
		this.cTime = cTime;
	}



	@Override
	public int compareTo(OaShowHome o) {
		
		return  o.cTime.compareTo(this.cTime) ;
	}
	
	

}
