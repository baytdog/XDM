package com.pointlion.mvc.admin.grap.url.specialgetdata;

import java.util.Map;

import com.gargoylesoftware.htmlunit.WebClient;
import com.pointlion.mvc.common.model.GrapUrl;

public interface SpecialGetDataInterface {

	public Map<String,Integer> getData(GrapUrl o,WebClient webclient,String startDate,String endDate) throws Exception;//获取数据
	
}
