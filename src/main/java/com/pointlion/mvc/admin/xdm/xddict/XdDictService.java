package com.pointlion.mvc.admin.xdm.xddict;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.admin.oa.dict.OaDictService;
import com.pointlion.mvc.common.dto.ZtreeNode;
import com.pointlion.mvc.common.model.*;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class XdDictService{
	public static final XdDictService me = new XdDictService();
	public static final String TABLE_NAME = XdDict.tableName;
	public static final String GROUP_TABLE_NAME = XdDictGroup.tableName;

	/***
	 * query by id
	 */
	public XdDict getById(String id){
		return XdDict.dao.findById(id);
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
			XdDict o = me.getById(id);
			o.delete();
		}
	}



	/***
	 * 根据id 查询孩子
	 * @param id
	 * @return
	 */
	public List<XdDictGroup> getChildrenByPid(String id){
		return XdDictGroup.dao.find("select * from "+GROUP_TABLE_NAME+" m where m.parent_id='"+id+"' order by m.sort");
	}

	/***
	 * 递归
	 * 查询孩子
	 * @param id
	 * @return
	 */
	public List<XdDictGroup> getChildrenAllTree(String id){
		List<XdDictGroup> list =  getChildrenByPid(id);//根据id查询孩子
		for(XdDictGroup o : list){
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
	public List<ZtreeNode> toZTreeNode(List<XdDictGroup> olist,Boolean open,Boolean ifOnlyLeaf){
		List<ZtreeNode> list = new ArrayList<ZtreeNode>();
		for(XdDictGroup o : olist){
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
	public ZtreeNode toZTreeNode(XdDictGroup group){
		ZtreeNode node = new ZtreeNode();
		node.setId(group.getId());
		node.setName(group.getName());
		return node;
	}
}