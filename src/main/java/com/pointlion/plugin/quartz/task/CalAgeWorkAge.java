package com.pointlion.plugin.quartz.task;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @Author: king
 * @date: 2022/12/30 10:11
 * @description: 定时计算年龄和工龄
 */
public class CalAgeWorkAge implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//       **************************计算开始******************
        System.out.println("---------------------开始");
        List<Record> records = Db.find("select * from  xd_employee");

        System.out.println("条数="+records.size());

        DateTimeFormatter dtf= DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DecimalFormat df2  = new DecimalFormat("0.00");//这样为保持2位
        int i=1;
        for (Record emp : records) {
            System.out.println(i);
            String id = emp.get("id");
            String birthday = emp.get("birthday");
            System.out.println("出生日期="+birthday);
            if(birthday!=null){
                LocalDate parseBirth = LocalDate.parse(birthday, dtf);
                long minusDays = LocalDate.now().toEpochDay() - parseBirth.toEpochDay();
                int age = (int) (minusDays / 365);
                int oldAge = emp.get("age");

                if (age != oldAge) {
                    Db.update("update xd_employee set  age='"+age+"' where id='"+id+"'");
                }
            }

            String entrytime = emp.get("entrytime");
            System.out.println("入职时间="+entrytime);
            if(entrytime!=null){
                LocalDate parseEntrytime = LocalDate.parse(entrytime, dtf);
                long entryMinusDays = LocalDate.now().toEpochDay() - parseEntrytime.toEpochDay();
                String seniority = df2.format((double) entryMinusDays / 365);

                System.out.println("相差天数="+entryMinusDays);
                System.out.println(seniority);
                Db.update("update xd_employee set  seniority='"+seniority+"' where id='"+id+"'");
            }
            i++;
        }
//       **************************计算开始******************
    }


    public static void main(String[] args) {

//        System.out.println(df2.format((double) 23 / 2));

//        2016-01-25
        LocalDate parse = LocalDate.parse("1973-09-29", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        long l = LocalDate.now().toEpochDay() - parse.toEpochDay();
        System.out.println(l);
        DecimalFormat df2  = new DecimalFormat("0.00");//这样为保持2位
        System.out.println(df2.format((double) l / 365));

    }
}
