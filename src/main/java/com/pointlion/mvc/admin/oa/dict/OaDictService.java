package com.pointlion.mvc.admin.oa.dict;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.dto.ZtreeNode;
import com.pointlion.mvc.common.model.OaDict;
import com.pointlion.mvc.common.model.OaDictGroup;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ShiroKit;

public class OaDictService{
	public static final OaDictService me = new OaDictService();
	public static final String TABLE_NAME = OaDict.tableName;
	public static final String GROUP_TABLE_NAME = OaDictGroup.tableName;
	
	/***
	 * query by id
	 */
	public OaDict getById(String id){
		return OaDict.dao.findById(id);
	}
	
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize,String groupId){
		String sql  = " from "+TABLE_NAME+" d where 1=1";
		if(StrKit.notBlank(groupId)){
			sql = sql + " and d.group_id='"+groupId+"'";
		}
		sql = sql + " order by d.sortnum";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	/***
	 * get page
	 */
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
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaDict o = me.getById(id);
    		o.delete();
    	}
	}
	
	
	
	/***
	 * 根据id 查询孩子
	 * @param id
	 * @return
	 */
	public List<OaDictGroup> getChildrenByPid(String id){
		return OaDictGroup.dao.find("select * from "+GROUP_TABLE_NAME+" m where m.parent_id='"+id+"' order by m.sort");
	}
	
	/***
	 * 递归
	 * 查询孩子
	 * @param id
	 * @return
	 */
	public List<OaDictGroup> getChildrenAllTree(String id){
		List<OaDictGroup> list =  getChildrenByPid(id);//根据id查询孩子
		for(OaDictGroup o : list){
			System.out.println(o.getName());
			o.setChildren(getChildrenAllTree(o.getId()));
		}
		return list;
	}
	
	
 
	
	
 
	 
	/***
	 * 转成ZTreeNode
	 * @param 
	 * olist 数据
	 * open  是否展开所有
	 * ifOnlyLeaf 是否只选叶子
	 * @return
	 */
	public List<ZtreeNode> toZTreeNode(List<OaDictGroup> olist,Boolean open,Boolean ifOnlyLeaf){
		List<ZtreeNode> list = new ArrayList<ZtreeNode>();
		for(OaDictGroup o : olist){
			ZtreeNode node = toZTreeNode(o);
			if(o.getChildren()!=null&&o.getChildren().size()>0){//如果有孩子
				node.setChildren(toZTreeNode(o.getChildren(),open,ifOnlyLeaf));
				if(ifOnlyLeaf){//如果只选叶子
					node.setNocheck(true);
				}
			}
			if("1".equals(node.getType())){
				node.setOpen(true);
			}else{
				node.setOpen(open);
			}
			list.add(node);
		}
		return list;
	}
	
	/***
	 * 菜单转成ZtreeNode
	 * @param 
	 * @return
	 */
	public ZtreeNode toZTreeNode(OaDictGroup group){
		ZtreeNode node = new ZtreeNode();
		node.setId(group.getId());
		node.setName(group.getName());
		return node;
	}
}