package com.pointlion.mvc.common.model;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.dto.ZtreeNode;
import com.pointlion.mvc.common.model.base.BaseGrapWebsite;
@SuppressWarnings("serial")
public class GrapWebsite extends BaseGrapWebsite<GrapWebsite> {
	public static final GrapWebsite dao = new GrapWebsite();
	public static final String tableName = "grap_website";
	
	/***
	 * 根据主键查询
	 */
	public GrapWebsite getById(String id){
		return GrapWebsite.dao.findById(id);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		GrapWebsite o = GrapWebsite.dao.getById(id);
    		o.delete();
    	}
	}
	
	/****
	 * 获取所有的站点
	 */
	public List<GrapWebsite> getAllWebSite(){
		return GrapWebsite.dao.find("select * from "+tableName+" order by create_time desc");
	}
	
	/***
	 * 菜单转成ZTreeNode
	 * @param 
	 * olist 数据
	 * open  是否展开所有
	 * ifOnlyLeaf 是否只选叶子
	 * @return
	 */
	public List<ZtreeNode> toZTreeNode(List<GrapWebsite> olist,Boolean open,Boolean ifOnlyLeaf){
		List<ZtreeNode> list = new ArrayList<ZtreeNode>();
		for(GrapWebsite o : olist){
			ZtreeNode node = toZTreeNode(o);
//			if(o.getChildren()!=null&&o.getChildren().size()>0){//如果有孩子
//				node.setChildren(toZTreeNode(o.getChildren(),open,ifOnlyLeaf));
//				if(ifOnlyLeaf){//如果只选叶子
//					node.setNocheck(true);
//				}
//			}
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
	public ZtreeNode toZTreeNode(GrapWebsite o){
		ZtreeNode node = new ZtreeNode();
		node.setId(o.getId());
		node.setName(o.getName());
		return node;
	}
}