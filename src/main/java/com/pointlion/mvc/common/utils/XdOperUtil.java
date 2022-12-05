package com.pointlion.mvc.common.utils;

import com.pointlion.annotation.ChangeFields;
import com.pointlion.mvc.common.model.XdEmployee;
import com.pointlion.mvc.common.model.XdOplogSummary;
import com.pointlion.plugin.shiro.ShiroKit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @Author: king
 * @date: 2022/12/2 11:27
 * @description:
 */
public class XdOperUtil {

    /* *
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
                    Object newValue = method.invoke(newBean);
                    Object oldValue = method.invoke(oldBean);
//                    Object newValue = field.get(newBean);
//                    Object oldValue = field.get(oldBean);
                    if(!Objects.equals(newValue, oldValue)) {
                        builder.append("tablename:"+newBean.getClass().getSimpleName()+",")
                        .append("fieldName:"+method.getAnnotation(ChangeFields.class).value()+",") //获取字段名称
                                .append("fieldDesc:"+method.getAnnotation(ChangeFields.class).desc()+",")
                                    .append("oldValue:"+oldValue+",")
                                        .append("newValue:"+newValue)
                                            .append("-");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

    public static <T> void logSummary(String lid,String oid,T newObj,T oldObj,String otype,String status){
        String id = null;
        try {
             id = oldObj.getClass().getSuperclass().getDeclaredMethod("getId").invoke(oldObj).toString();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        System.out.println(id);
        String newObjStr = newObj==null?"":JSONUtil.beanToJsonString(newObj);
        String oldObjStr=oldObj==null?"":JSONUtil.beanToJsonString(oldObj);
        XdOplogSummary logSum=new XdOplogSummary();
        logSum.setId(lid);
        logSum.setOid(oid);
        logSum.setTid(id);
        logSum.setTname(oldObj.getClass().getSimpleName());
        logSum.setChangeb(oldObjStr);
        logSum.setChangea(newObjStr);
        logSum.setStatus(status);
        logSum.setOtype(otype);
        logSum.setCtime(DateUtil.getCurrentTime());
        logSum.setCuser(ShiroKit.getUserId());
        logSum.save();
    }

    public static <T> void logSummary(String oid,T bean,String otype,String status){
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
        logSum.setStatus(status);
        logSum.setOtype(otype);
        logSum.setCtime(DateUtil.getCurrentTime());
        logSum.setCuser(ShiroKit.getUserId());
        logSum.save();
    }




    public static void main(String[] args) throws Exception {

        XdEmployee e1=new XdEmployee();
        e1.setId("12345");
        XdEmployee e2=new XdEmployee();
        e2.setId("7890");


//        logSummary(e2);
//        logSummary(e1);
//        e1.setId("11");
//        e2.setId("22");
//        e1.setName(LogsEnum.C.name());
//        String changedMetheds = getChangedMetheds(e1, e2);
//        System.out.println(changedMetheds.split("^-")[0]);
//       // changedMetheds
//        System.out.println(LogsEnum.C.name());

    }

}
