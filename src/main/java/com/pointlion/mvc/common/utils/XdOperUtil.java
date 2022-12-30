package com.pointlion.mvc.common.utils;

import com.jfinal.plugin.activerecord.Db;
import com.pointlion.annotation.ChangeFields;
import com.pointlion.mvc.common.model.XdEmployee;
import com.pointlion.mvc.common.model.XdOplogSummary;
import com.pointlion.mvc.common.model.XdSteps;
import com.pointlion.plugin.shiro.ShiroKit;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Objects;

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


    }

    public static void setChangeValue(Object obj,String fieldName,String newValue)  {
        try {
            Method[] declaredMethods = obj.getClass().getSuperclass().getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                declaredMethod.setAccessible(true);
                String name = declaredMethod.getName();
                if(name.toLowerCase().startsWith("set")&& name.toLowerCase().replaceAll("set","").equals(fieldName)){
                    Object invoke = declaredMethod.invoke(obj, newValue);

                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }



}
