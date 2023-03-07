package com.pointlion.plugin.quartz.task;

import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.XdCertLog;
import com.pointlion.mvc.common.model.XdEmpCert;
import com.pointlion.mvc.common.model.XdEmployee;
import com.pointlion.mvc.common.utils.DateUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @Author: king
 * @date: 2023/3/7 13:49
 * @description: 更新员工离职 status 改为1 。修改证件信息。插入证件持证变动表
 */
public class UpdateLeaveJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("=======================UpdateLeaveJob 测试");
        List<XdEmployee> employees = XdEmployee.dao.find("select * from  xd_employee where inductionstatus='2' and status='0' and departime is not null and departime!=''");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMM");
        System.out.println(employees.size()+"-------------------------------");
        for (XdEmployee employee : employees) {
            LocalDate nextMonth = LocalDate.parse(employee.getDepartime()).plusMonths(1);
            LocalDate now = LocalDate.now();
            String nextMonthStr = dtf.format(nextMonth);
            String nowStr = dtf.format(now);
            if(Integer.valueOf(nextMonthStr)<= Integer.valueOf(nowStr)){
                employee.setStatus("1");
                employee.update();
                List<XdEmpCert> xdEmpCerts = XdEmpCert.dao.find("select * from  xd_emp_cert where eid='"+employee.getId()+"'");
                for (XdEmpCert empCert : xdEmpCerts) {
                    empCert.setStatus("0");
                    empCert.update();
                    XdCertLog log=new XdCertLog();
                    log.setYear(nextMonth.getYear());
                    log.setMonth(nextMonth.getMonthValue());
                    SysOrg org = SysOrg.dao.findById(empCert.getDepartment());
                    log.setDeptId(empCert.getDepartment());
                    log.setDeptName(org==null?"":org.getName());
                    log.setEname(employee.getName()+"(离职)");
                    log.setEid(employee.getId());
                    log.setCertTitle(empCert.getCertTile());
                    log.setLogType("");
                    log.setNum(-1);
                    log.setCreateUser("离职系统定时");
                    log.setCreateDate(DateUtil.getCurrentTime());
                    log.save();
                }
            }
        }


    }

    public static void main(String[] args) {
        System.out.println(LocalDate.parse("2023-03-07").getYear());
        System.out.println(LocalDate.parse("2023-03-07").getMonth());
        System.out.println(LocalDate.parse("2023-03-07").getMonthValue());
        System.out.println(LocalDate.parse("2023-01-31").plusMonths(1).getYear());
        System.out.println(LocalDate.parse("2023-01-31").plusMonths(1).getDayOfMonth());
        System.out.println(LocalDate.parse("2023-01-31").plusMonths(1).getMonthValue());
        String yyyyMM1 = DateTimeFormatter.ofPattern("yyyyMM").format(LocalDate.parse("2022-12-07"));
        String yyyyMM2 = DateTimeFormatter.ofPattern("yyyyMM").format(LocalDate.parse("2022-12-07"));

    }
}


