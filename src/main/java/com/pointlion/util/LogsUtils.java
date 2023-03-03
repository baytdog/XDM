package com.pointlion.util;

import com.pointlion.mvc.common.model.XdLogsLeave;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.JSONUtil;
import com.pointlion.plugin.shiro.ShiroKit;

/**
 * @Author: king
 * @date: 2023/3/2 22:56
 * @description:
 */
public class LogsUtils {
    /**
     * @Method insertLeaveLogs
     * @param oldObj:老数据
     * @param newObj:新数据
     * @Date 2023/3/2 23:01
     * @Description 年假日志记录
     * @Author king
     * @Version  1.0
     * @Return void
     */
    public static void insertLeaveLogs(String summaryId, String type ,String oldObj,String newObj){
       /* String oldStr = JSONUtil.beanToJsonString(oldObj);
        String newStr = JSONUtil.beanToJsonString(newObj);*/
        XdLogsLeave log=new XdLogsLeave();
        log.setSummaryId(summaryId);
        log.setType(type);
        log.setNewValue(newObj);
        log.setOldValue(oldObj);
        log.setCreateTime(DateUtil.getCurrentTime());
        log.setCreateUser(ShiroKit.getUserId());
        log.save();



    }
}
