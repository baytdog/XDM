package com.pointlion.mvc.common.model;

import java.util.List;

/**
 * @Author: king
 * @date: 2022/12/16 14:16
 * @description:
 */
public class PrintInfoVo {
    private XdEmployee emp;
    private List<XdEdutrain> edutrainList;
    private List<XdWorkExper> workExperList;


    public XdEmployee getEmp() {
        return emp;
    }

    public void setEmp(XdEmployee emp) {
        this.emp = emp;
    }

    public List<XdEdutrain> getEdutrainList() {
        return edutrainList;
    }

    public void setEdutrainList(List<XdEdutrain> edutrainList) {
        this.edutrainList = edutrainList;
    }

    public List<XdWorkExper> getWorkExperList() {
        return workExperList;
    }

    public void setWorkExperList(List<XdWorkExper> workExperList) {
        this.workExperList = workExperList;
    }




}
