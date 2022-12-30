package com.pointlion.util;

import com.pointlion.annotation.ChangeFields;
import com.pointlion.annotation.NeedMapping;
import com.pointlion.mvc.common.model.*;
import org.apache.poi.ss.formula.functions.T;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @Author: king
 * @date: 2022/12/22 13:31
 * @description: 字典映射
 */
public class DictMapping {


    /**
     * @Method dictMapping
     * @param :
     * @Date 2022/12/22 11:10
     * @Description  字典表映射汉字转字典值
     * @Author king
     * @Version  1.0
     * @Return java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Integer>>
     */
    public static Map<String, Map<String,String>> dictMapping(){
        Map<String,Map<String,String>> dictMap=new HashMap<>();
        List<XdDict> dictsList = XdDict.dao.find("select * from xd_dict");
        dictsList.stream().forEach(xdDict -> {
            if (dictMap.get(xdDict.getType()) == null) {
                Map<String,String> map =new HashMap<>();
                map.put(xdDict.getName(),xdDict.getValue());
                dictMap.put(xdDict.getType(),map);
            }else{
                Map<String, String> map = dictMap.get(xdDict.getType());
                map.put(xdDict.getName(),xdDict.getValue());
                dictMap.put(xdDict.getType(),map);
            }
            /*switch (xdDict.getType()){
                case "unit":
                    if(dictMap.get("unit")==null){
                        Map<String,String> unitMap=new HashMap<>();
                        unitMap.put(xdDict.getName(),xdDict.getValue());
                        dictMap.put("unit",unitMap);
                    }else{
                        Map<String, String> unit = dictMap.get("unit");
                        unit.put(xdDict.getName(),xdDict.getValue());
                        dictMap.put("unit",unit);
                    }
                    break;
                case  "officestatus":
                    if(dictMap.get("officestatus")==null){
                        Map<String,String> officestatusMap=new HashMap<>();
                        officestatusMap.put(xdDict.getName(),xdDict.getValue());
                        dictMap.put("officestatus",officestatusMap);
                    }else{
                        Map<String, String> officestatus = dictMap.get("officestatus");
                        officestatus.put(xdDict.getName(),xdDict.getValue());
                        dictMap.put("officestatus",officestatus);
                    }
                    break;
                case  "position":
                    if(dictMap.get("position")==null){
                        Map<String,String> positionMap=new HashMap<>();
                        positionMap.put(xdDict.getName(),xdDict.getValue());
                        dictMap.put("position",positionMap);
                    }else{
                        Map<String, String> position = dictMap.get("position");
                        position.put(xdDict.getName(),xdDict.getValue());
                        dictMap.put("position",position);
                    }
                    break;
                case  "ismarry":
                    if(dictMap.get("ismarry")==null){
                        Map<String,String> ismarryMap=new HashMap<>();
                        ismarryMap.put(xdDict.getName(),xdDict.getValue());
                        dictMap.put("ismarry",ismarryMap);
                    }else{
                        Map<String, String> ismarry = dictMap.get("ismarry");
                        ismarry.put(xdDict.getName(),xdDict.getValue());
                        dictMap.put("ismarry",ismarry);
                    }
                    break;
                case  "edu":
                    if(dictMap.get("edu")==null){
                        Map<String,String> eduMap=new HashMap<>();
                        eduMap.put(xdDict.getName(),xdDict.getValue());
                        dictMap.put("edu",eduMap);
                    }else{
                        Map<String, String> edu = dictMap.get("edu");
                        edu.put(xdDict.getName(),xdDict.getValue());
                        dictMap.put("edu",edu);
                    }
                    break;
                default:

            }*/
        });
        return dictMap;
    }

    /**
     * @Method dictMappingValueToName
     * @Date 2022/12/22 13:45
     * @Description  字典映射字典值转汉字
     * @Author king
     * @Version  1.0
     * @Return java.util.Map<java.lang.String,java.util.Map<java.lang.Integer,java.lang.String>>
     */
    public static Map<String,Map<String,String>> dictMappingValueToName(){
        Map<String,Map<String,String>> dictMap=new HashMap<>();
        List<XdDict> dictsList = XdDict.dao.find("select * from xd_dict");
        dictsList.stream().forEach(xdDict -> {
            if (dictMap.get(xdDict.getType()) == null) {
                Map<String,String> map =new HashMap<>();
                map.put(xdDict.getValue(),xdDict.getName());
                dictMap.put(xdDict.getType(),map);
            }else{
                Map<String, String> map = dictMap.get(xdDict.getType());
                map.put(xdDict.getValue(),xdDict.getName());
                dictMap.put(xdDict.getType(),map);
            }

        });
        return dictMap;
    }


    /**
     * @Method orgMapping
     * @param type:	 1 : 汉字转字典值，0：字典值转汉子
     * @Date 2022/12/22 14:42
     * @Description
     * @Author king
     * @Version  1.0
     * @Return java.util.Map<java.lang.String,java.lang.String>
     */
    public static Map<String,String> orgMapping(String type){
        Map<String,String> orgMap=new HashMap<>();
        List<SysOrg> orgList = SysOrg.dao.find("select * from sys_org");
        if("1".equals(type)){

            orgList.stream().forEach( sysOrg -> orgMap.put(sysOrg.getName(),sysOrg.getId()) );
        }else{
            orgList.stream().forEach( sysOrg -> orgMap.put(sysOrg.getId(),sysOrg.getName()) );
        }
        return  orgMap;
    }
    /**
     * @Method projectsMapping
     * @Date 2022/12/22 13:39
     * @Description 项目映射
     * @Author king
     * @Version  1.0
     * @Return java.util.Map<java.lang.String,java.lang.Integer>
     */
    public static  Map<String,String> projectsMapping(){
        Map<String,String> projectsMap=new HashMap<>();
        List<XdProjects> xdProjectsList = XdProjects.dao.find("select * from xd_projects");
        xdProjectsList.stream().forEach( xdProjects -> projectsMap.put(xdProjects.getProjectName(),String.valueOf(xdProjects.getId())) );
        return  projectsMap;
    }
    /**
     * @Method projectsMappingValueToName
     * @Date 2022/12/22 14:53
     * @Description 字典值转汉字
     * @Author king
     * @Version  1.0
     * @Return java.util.Map<java.lang.Integer,java.lang.String>
     */
    public static  Map<String,String> projectsMappingValueToName(){
        Map<String,String> projectsMap=new HashMap<>();
        List<XdProjects> xdProjectsList = XdProjects.dao.find("select * from xd_projects");
        xdProjectsList.stream().forEach( xdProjects -> projectsMap.put(String.valueOf(xdProjects.getId()),xdProjects.getProjectName()) );
        return  projectsMap;
    }

    /**
     * @Method opLogsMapping
     * @param list:	 异动详情列表
     * @Date 2022/12/30 15:29
     * @Description 异动详情显示汉化
     * @Author king
     * @Version  1.0
     * @Return void
     */
    public static void opLogsMapping(List<XdOplogDetail> list){
        Map<String, String> orgMap = orgMapping("0");
        Map<String, String> projectMap = projectsMappingValueToName();
        Map<String, Map<String, String>> dictMap = dictMappingValueToName();
        for (XdOplogDetail detail : list) {
            String fieldName = detail.getFieldName();
            Map<String, String> dict=new HashMap<>();
            if("married".equals(fieldName)){
                dict = dictMap.get("ismarry");
                /*    xdOplogDetail.setOldValue(dict.get(xdOplogDetail.getOldValue()));
                    xdOplogDetail.setNewValue(dict.get(xdOplogDetail.getNewValue()));*/
            }else if("national".equals(fieldName)){
                dict = dictMap.get("nation");
            }else if("politicsstatus".equals(fieldName)){
                dict = dictMap.get("polity");
            }else if("topedu".equals(fieldName)||"edubg1".equals(fieldName)||"edubg2".equals(fieldName)){
                dict = dictMap.get("edu");
            }else if("issoldier".equals(fieldName)){
                dict = dictMap.get("solider");
            }else if("inductionstatus".equals(fieldName)){
                dict = dictMap.get("officestatus");
            }else if("department".equals(fieldName)){
                dict = orgMap;
            }else if("unitname".equals(fieldName)){
                dict = dictMap.get("unit");
            }else if("costitem".equals(fieldName)){
                dict = projectMap;
            }else if("position".equals(fieldName)){
                dict = dictMap.get("position");
            }else if("hardstaff".equals(fieldName)){
                dict = dictMap.get("hardstaff");
            }else{
                continue;
            }
            detail.setOldValue(dict.get(detail.getOldValue())==null?"":dict.get(detail.getOldValue()));
            detail.setNewValue(dict.get(detail.getNewValue())==null?"":dict.get(detail.getNewValue()));
        }

    }

    public static  <T> void fieldValueToName(T bean, Map<String, String> orgMap, Map<String, String> projectsMap,Map<String, Map<String, String>> dictMap ){

        Method[] declaredMethods = bean.getClass().getSuperclass().getDeclaredMethods();

        for(Method method : declaredMethods) {
            method.setAccessible(true);
            if (method.isAnnotationPresent(NeedMapping.class)) {
                try {
                    String setName = method.getName().replaceFirst("^s", "g");
                    Method getName = bean.getClass().getSuperclass().getDeclaredMethod(setName);
                    String mappingCode = method.getAnnotation(NeedMapping.class).dictName();
                    Object filedValue = getName.invoke(bean);
                    String chinaValue="";
                    if("org".equals(mappingCode)){
                         chinaValue = orgMap.get(filedValue);
                    }else if("projects".equals(mappingCode)){
                         chinaValue = projectsMap.get(filedValue);

                     /*   if(filedValue==null){
                            method.invoke(bean,"");
                        }else{
                            if(chinaValue==null){
                                method.invoke(bean,filedValue.toString());
                            }else{
                                method.invoke(bean,chinaValue);
                            }
                        }*/
                    }else{
                        Map<String, String> StringMap = dictMap.get(mappingCode);
                         chinaValue = StringMap.get(filedValue);
                       /* if(filedValue==null){
                            method.invoke(bean,"");
                        }else{
                            if(chinaValue==null){
                                method.invoke(bean,filedValue.toString());
                            }else{
                                method.invoke(bean,chinaValue);
                            }
                        }*/

                    }
                    if(filedValue==null){
                        method.invoke(bean,"");
                    }else{
                        if(chinaValue==null){
                            method.invoke(bean,filedValue.toString());
                        }else{
                            method.invoke(bean,chinaValue);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                try {

                    if(method.getName().startsWith("get")&& method.getReturnType().getSimpleName().endsWith("String")){
                        Object invoke = method.invoke(bean);
                        if(invoke==null){
                            String setName = method.getName().replaceFirst("^g", "s");
                            Method setMethod = bean.getClass().getSuperclass().getDeclaredMethod(setName,method.getReturnType());
                            setMethod.invoke(bean,"");
                        }
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
