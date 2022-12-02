package com.pointlion.mvc.common.utils;

import com.pointlion.annotation.ChangeFields;
import com.pointlion.enums.LogsEnum;
import com.pointlion.mvc.common.model.XdEmployee;
import com.sun.xml.internal.bind.v2.TODO;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @Author: king
 * @date: 2022/12/2 11:27
 * @description:
 */
public class ChangesUtil {

    /* *
     * @Method getChangedMetheds
     * @param newBean:	 新数据
     * @param oldBean:	 旧数据
     * @Date 2022/12/2 14:15
     * @Exception
     * @Description
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

    public static void main(String[] args) {

        XdEmployee e1=new XdEmployee();
        XdEmployee e2=new XdEmployee();
        e1.setId("11");
        e2.setId("22");
        e1.setName(LogsEnum.C.name());
        String changedMetheds = getChangedMetheds(e1, e2);
        System.out.println(changedMetheds.split("^-")[0]);
        System.out.println(LogsEnum.C.name());

    }

}
