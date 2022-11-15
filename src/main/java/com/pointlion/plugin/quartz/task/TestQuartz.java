/**
 * 
 */
package com.pointlion.plugin.quartz.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/** 
 * @ClassName: TestQuartz 
 * @Description: TODO
 * @date: 2020年12月2日 下午1:32:22  
 */
public class TestQuartz  implements Job{

	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("test================================");
		SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");
		 Record dob = Db.findById("oa_days" , "id", sdf.format(new Date()));
		 System.out.println(dob.get("status").toString());
		 
		 
		 
		 //热线剩余处理天数积算
		 List<Record> records = Db.find("select * from  oa_hotline where status not in ('5','6')");
		 
		 for (Record record : records) {
			 
			 if(record.get("clqx")!=null) {
				 Object object = record.get("syts");
				 int syts;
				 if(object==null) {
					 syts=Integer.valueOf(record.get("clqx"));
				 }else {
					 syts= Integer.valueOf(object.toString());
				 }
				 if(dob.get("status").toString().equals("1")) {
					 syts= syts-1;
					 Db.update("update oa_hotline set syts='"+syts+"' where id='"+record.get("id")+"'");
				 }
			 }

			
		}
		 
		//信访处理天数计算
		 
		List<Record> letters = Db.find("select * from oa_letter where status not in ('5','6')");
		
		for (Record letter : letters) {
			if(letter.get("clqx")!=null) {
				
				 Object object = letter.get("syts");
				 int syts;
				 if(object==null) {
					 syts=Integer.valueOf(letter.get("clqx"));
				 }else {
					 syts= Integer.valueOf(object.toString());
				 }
					 syts= syts-1;
					 Db.update("update oa_hotline set syts='"+syts+"' where id='"+letter.get("id")+"'");
				
			}
			
			
		}
		
		 
	}

}
