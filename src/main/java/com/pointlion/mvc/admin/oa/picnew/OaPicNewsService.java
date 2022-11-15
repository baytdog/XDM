package com.pointlion.mvc.admin.oa.picnew;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.admin.oa.notice.NoticeService;
import com.pointlion.mvc.admin.sys.attachment.AttachmentService;
import com.pointlion.mvc.common.model.OaNotice;
import com.pointlion.mvc.common.model.OaNoticeUser;
import com.pointlion.mvc.common.model.OaPicNews;
import com.pointlion.mvc.common.model.SysAttachment;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;

public class OaPicNewsService{

	public static final OaPicNewsService me = new OaPicNewsService();
	
	public static final AttachmentService attach = new AttachmentService(); 
	private final OaPicNews  dao =  new OaPicNews().dao();
			
			//new OaNotice().dao();
	//private final OaNoticeUser nudao = new OaNoticeUser().dao();
	
	
	
	/** 
	* @Title: save 
	* @Description: TODO 
	* @param model void
	* @author bkkco
	* @date 2020年8月26日下午11:20:34
	*/ 
	
	@Before(Tx.class)
	public void save(OaPicNews picnew) {
System.out.println("1111111111");
		//picnew.setTextContent(StringUtil.delHTMLTag(picnew.getContent()));//设置纯文本
		if(StrKit.notBlank(picnew.getId()==null?"":picnew.getId().toString())){//更新
			picnew.update();//更新公共
		}else{//保存
			//picnew.setId(UuidUtil.getUUID());
			picnew.setCdate(DateUtil.getCurrentTime());
			
			SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
			picnew.setRq(sdf.format(new  Date()));
			picnew.save();//保存公告
		}
		//根据推送单位，保存通知到的人
	/*	deleteNoticeUserByNoticeId(notice.getId());//删除该通知所有通知到人
		String orgidarr[] = notice.getToOrgId().split(",");
		for(String orgid : orgidarr){
			List<SysUser> userlist = SysUser.dao.getUserListByOrgId(orgid);//查询机构下所有人员
			for(SysUser user : userlist){
				OaNoticeUser noticeuser = new OaNoticeUser();
				noticeuser.setId(UuidUtil.getUUID());
				noticeuser.setUserId(user.getId());
				noticeuser.setUserName(user.getName());
				noticeuser.setNoticeId(notice.getId());
				noticeuser.save();
			}
		}*/
	
		
	}
	
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void setShowPic(String busid,String ids){
    	String idarr[] = ids.split(",");
    	OaPicNews picn = me.dao.findById(busid);
    	SysAttachment o  =new SysAttachment();
    	for(String id : idarr){
    		o= attach.getById(id);
    		//o.delete();
    	}
    	picn.setImgurl(o.getId());
    	//picn.setDesc("1");
    	picn.update();
    	
    	Db.update("update oa_pic_news set ifshow='0'");
    	
    	List<OaPicNews> find = me.dao.find("select * from oa_pic_news  where imgurl !='' order by cdate desc  limit 3");
    	for (OaPicNews oaPicNews : find) {
    		oaPicNews.setIfshow("1");
    		oaPicNews.update();
    		
		}
	}
	
	
	
	/*
	public static final OaPicNewsService me = new OaPicNewsService();
	public static final String TABLE_NAME = OaPicNews.tableName;
	
	*//***
	 * query by id
	 *//*
	public OaPicNews getById(String id){
		return OaPicNews.dao.findById(id);
	}
	
	*//***
	 * get page
	 *//*
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_  where 1=1";
		sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(applyUser)){
			sql = sql + " and o.applyer_name like '%"+applyUser+"%'";
		}
		sql = sql + " order by o.create_time desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	
	*//***
	 * del
	 * @param ids
	 *//*
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaPicNews o = me.getById(id);
    		o.delete();
    	}
	}
	
*/}