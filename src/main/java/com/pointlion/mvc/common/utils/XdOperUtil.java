package com.pointlion.mvc.common.utils;

import com.jfinal.plugin.activerecord.Db;
import com.pointlion.annotation.ChangeFields;
import com.pointlion.mvc.common.model.*;
import com.pointlion.plugin.shiro.ShiroKit;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @Author: king
 * @date: 2022/12/2 11:27
 * @description:
 */
public class XdOperUtil {

    /**
     * @Method getChangedMetheds
     * @param newBean:	 新数据
     * @param oldBean:	 旧数据
     * @Date 2022/12/2 14:15
     * @Exception
     * @Description 比较两个对象修改字段、内容
     * @Author king
     * @Version  1.0
     * @Return java.lang.String
     */
    public static <T> String getChangedMetheds(T newBean, T oldBean){

        Method[] declaredMethods = newBean.getClass().getSuperclass().getDeclaredMethods();
        //Method[] declaredMethods = newBean.getClass().getDeclaredMethods();
        StringBuilder builder = new StringBuilder();
        for(Method method : declaredMethods) {
            method.setAccessible(true);
            if (method.isAnnotationPresent(ChangeFields.class)) {
                try {
                    Object newValue = (method.invoke(newBean)==null?"":method.invoke(newBean));
                    Object oldValue = (method.invoke(oldBean)==null?"":method.invoke(oldBean));
//                    Object newValue = field.get(newBean);
//                    Object oldValue = field.get(oldBean);
                    if(!Objects.equals(newValue, oldValue)) {
                        builder.append("\"tablename\":\""+newBean.getClass().getSimpleName()+"\",")
                        .append("\"fieldName\":\""+method.getAnnotation(ChangeFields.class).value()+"\",") //获取字段名称
                                .append("\"fieldDesc\":\""+method.getAnnotation(ChangeFields.class).desc()+"\",")
                                    .append("\"oldValue\":\""+oldValue+"\",")
                                        .append("\"newValue\":\""+newValue)
                                            .append("\"--");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }



    public static <T> XdOplogSummary logSummary(String lid,String oid,T newObj,T oldObj,String otype,String status){
        String id = null;
        T obj=(oldObj==null?newObj:oldObj);
        try {
             id = obj.getClass().getSuperclass().getDeclaredMethod("getId").invoke(obj).toString();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        String newObjStr = newObj==null?"":JSONUtil.beanToJsonString(newObj);
        String oldObjStr=oldObj==null?"":JSONUtil.beanToJsonString(oldObj);
        XdOplogSummary logSum=new XdOplogSummary();
        logSum.setId(lid);
        logSum.setOid(oid);
        logSum.setTid(id);
        logSum.setTname(obj.getClass().getSimpleName());
        logSum.setChangeb(oldObjStr);
        logSum.setChangea(newObjStr);
        logSum.setStatus(status);
        logSum.setOtype(otype);
        logSum.setCtime(DateUtil.getCurrentTime());
        logSum.setCuser(ShiroKit.getUserId());
        logSum.setLastversion(0);
        //logSum.save();
        return logSum;
    }

    public static <T> void logSummary(String oid,T bean,String otype,String status,int lastVersion){
        String id = "";
        try {
            id = bean.getClass().getSuperclass().getDeclaredMethod("getId").invoke(bean).toString();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        String beanStr = (bean==null?"":JSONUtil.beanToJsonString(bean));
        XdOplogSummary logSum=new XdOplogSummary();
        logSum.setId(UuidUtil.getUUID());
        logSum.setOid(oid);
        logSum.setTid(id);
        logSum.setTname(bean.getClass().getSimpleName());
        if("C".equals(otype)){
            logSum.setChangeb("");
            logSum.setChangea(beanStr);
        }else{
            logSum.setChangeb(beanStr);
            logSum.setChangea("");
        }
        logSum.setLastversion(lastVersion);
        logSum.setStatus(status);
        logSum.setOtype(otype);
        logSum.setCtime(DateUtil.getCurrentTime());
        logSum.setCuser(ShiroKit.getUserId());
        logSum.save();
    }

    /**
     * @Method insertSteps
     * @param oid:	主体id
     * @param stype:	类型1.员工
     * @param parentId:	 步骤父类id
     * @param orgid:	 所属部门id
     * @param userId:	 步骤操作人id
     * @param userName:	 步骤操作人姓名
     * @Date 2022/12/7 14:29
     * @Exception
     * @Description 步骤插入
     * @Author king
     * @Version  1.0
     * @Return void
     */
    public static void insertSteps(String oid,String stype,String parentId,String orgid,String userId,String userName){
        XdSteps steps=new XdSteps();
        steps.setId(UuidUtil.getUUID());
        steps.setOid(oid);
        steps.setStype(stype);
        steps.setParentid(parentId);
        steps.setOrgid(orgid);
        steps.setUserid(userId);
        steps.setUsername(userName);
        steps.setCuserid(ShiroKit.getUserId());
        steps.setCusername(ShiroKit.getUserName());
        steps.setCtime(DateUtil.getCurrentTime());
        steps.save();
    }
    public static void insertEmpoloyeeSteps(XdEmployee employee,String parentId,String orgid,String userId,String userName,String otype,String auditResult){
        XdSteps steps=new XdSteps();
        steps.setId(UuidUtil.getUUID());
        steps.setOid(employee.getId());
        steps.setStype("1");
        steps.setParentid(parentId);
        steps.setStep(employee.getName());
        steps.setStepdesc(employee.getEmpnum());
        steps.setOrgid(orgid);
        steps.setUserid(userId);
        steps.setUsername(userName);
        //steps.setBackup1(otype);
        steps.setSoptye(otype);
        steps.setAuditresult(auditResult);//待审批
        steps.setCuserid(ShiroKit.getUserId());
        steps.setCusername(ShiroKit.getUserName());
        steps.setCtime(DateUtil.getCurrentTime());
        steps.save();
    }
    public static void queryLastVersion(String id){

        Integer count = Db.queryInt("select count(*) from xd_oplog_summary where oid='" + id + "'");
        if(count>0){
            Integer integer = Db.queryInt("select max(lastversion) from xd_oplog_summary where oid='" + id + "'");
            Db.update("update xd_oplog_summary set lastversion='"+(integer+1)+"' where oid='"+id+"' and lastversion='0'" );
        }
    }

    public static void main(String[] args) throws Exception {

        /*XdEmployee e1=new XdEmployee();
        e1.setId("12345");
        XdEmployee e2=new XdEmployee();
        e2.setId("7890");*/
        XdEmployee e2=new XdEmployee();
        e2.setId("7890");
        setChangeValue(e2,"name","小渣渣");

        System.out.println(e2);

        LocalDate localDate = LocalDate.parse("2022-12-31").plusDays(1);
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String format = dtf.format(localDate);
        System.out.println(format);
        String abc="12345";
        System.out.println(abc.substring(abc.length() - 1));


    }

    public static void setChangeValue(Object obj,String fieldName,String newValue)  {
        try {
            Method[] declaredMethods = obj.getClass().getSuperclass().getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                declaredMethod.setAccessible(true);
                String name = declaredMethod.getName();
                if(name.toLowerCase().startsWith("set")&& name.toLowerCase().replaceAll("set","").equalsIgnoreCase(fieldName)){
                    Object invoke = declaredMethod.invoke(obj, newValue);

                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }



    /**
     * @Method updateEdu
     * @param oid:	 员工id
     * @Date 2023/1/2 14:55
     * @Exception
     * @Description  更新员工学历信息
     * @Author king
     * @Version  1.0
     * @Return void
     */
    public static void updateEdu(String oid) {
        XdEmployee employee = XdEmployee.dao.findById(oid);
        boolean arz=true;
        boolean uarz=true;
        List<XdEdutrain> xdEdutrainList = XdEdutrain.dao.find("select * from xd_edutrain where eid='" + oid + "' order by grade, edubg desc");
        for (XdEdutrain edutrain : xdEdutrainList) {
            if(arz && edutrain.getGrade().equals("0")){//全日制
                employee.setEdubg2(edutrain.getEdubg());
                employee.setSchool2(edutrain.getTrainOrgname());
                employee.setMajor2(edutrain.getMajor());
                arz=false;
            }

            if(uarz && edutrain.getGrade().equals("1")){//非全日制
                employee.setEdubg1(edutrain.getEdubg());
                employee.setSchool1(edutrain.getTrainOrgname());
                employee.setMajor1(edutrain.getMajor());
                uarz=false;
            }
        }



        xdEdutrainList.stream().forEach( xdEdutrain-> {
            String edubg = xdEdutrain.getEdubg();
            String topedu = employee.getTopedu();
            if(edubg!=null){
                if(topedu==null || (Integer.valueOf(edubg)>Integer.valueOf(topedu))){
                    employee.setTopedu(edubg);
                }
            }
        });
        if(arz){

            employee.setEdubg2("");
            employee.setSchool2("");
            employee.setMajor2("");
        }

        if(uarz){
            employee.setEdubg1("");
            employee.setSchool1("");
            employee.setMajor1("");
        }


        employee.update();
    }




    public static void updateEmpCert(XdEmployee emp){
        String inductionstatus = emp.getInductionstatus();
        List<XdEmpCert> xdEmpCerts = XdEmpCert.dao.find("select * from  xd_emp_cert where ename='" + emp.getName() + "'");
        if(inductionstatus.equals("2")){
            xdEmpCerts.forEach(xdEmpCert ->{
                    xdEmpCert.setStatus("0");
                    xdEmpCert.setBackup1("在职->离职");
                    xdEmpCert.update();
            });
        }
        if(inductionstatus.equals("1")){
            xdEmpCerts.forEach(xdEmpCert ->{
                xdEmpCert.setStatus("1");
                xdEmpCert.setBackup1("离职->在职");
                xdEmpCert.update();
            });
        }
    }



    public static String apprListToValue(String filedName,String filedValue){
        if("edubg".equals(filedName)){
            if("1".equals(filedValue)){
                return "技工";
            }else if("2".equals(filedValue)){
                return "职高";
            }else if("3".equals(filedValue)){
                return "中专";
            }else if("4".equals(filedValue)){
                return "高中";
            }else if("5".equals(filedValue)){
                return "大专";
            }else if("6".equals(filedValue)){
                return "本科";
            }else{
                return "";
            }

        }else if("grade".equals(filedName)){
            if("0".equals(filedValue)){
                return "全日制";
            }else if("1".equals(filedValue)){
                return "非全日制";
            }else{
                return "";
            }
        }
        return "";

    }



    public static void updateEmpRelationInfos(XdEmployee emp){
        String name =emp.getName();
        String id = emp.getId();
        List<XdEdutrain> xdEdutrainList = XdEdutrain.dao.find("select * from  xd_edutrain where eid ='" + id + "'");
        xdEdutrainList.forEach(xdEdutrain-> {
            if(!xdEdutrain.getEname().equals(name)){
                xdEdutrain.setEname(name);
                xdEdutrain.update();
            }
        });

        List<XdWorkExper> workExperList = XdWorkExper.dao.find("select * from xd_work_exper where eid ='" + id + "'");
        workExperList.forEach(xdWorkExper-> {
                if(!xdWorkExper.getEname().equals(name)){
                    xdWorkExper.setEname(name);
                    xdWorkExper.update();
                }
        });

        List<XdEmpCert> xdEmpCerts = XdEmpCert.dao.find("select * from  xd_emp_cert where eid='" + id + "'");
        xdEmpCerts.forEach(xdEmpCert-> {
                if(!xdEmpCert.getEname().equals(name)){
                    xdEmpCert.setEname(name);
                    xdEmpCert.update();
                }
        });
    }

    /**
     * @Method updateEmpCert
     * @param cert:
     * @Date 2023/1/6 10:35
     * @Exception
     * @Description  证书调整更新员工证书
     * @Author king
     * @Version  1.0
     * @Return void
     */
    public  static void updateEmpCert(XdEmpCert cert){
        String eid = cert.getEid();
        XdEmployee emp = XdEmployee.dao.findById(eid);

        List<XdEmpCert> xdEmpCerts = XdEmpCert.dao.find("select * from  xd_emp_cert where status='1' and ename='" + cert.getEname() + "'");
        String certStr="";
        for (XdEmpCert xdEmpCert : xdEmpCerts) {
            certStr=certStr+xdEmpCert.getCertTile()+"、";
        }
        String certs = certStr.replaceAll("、$", "");
        emp.setCertificates(certs);
        emp.update();
    }

}
