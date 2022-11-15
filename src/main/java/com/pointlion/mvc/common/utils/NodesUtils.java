/**
 * 
 */
package com.pointlion.mvc.common.utils;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.pointlion.mvc.common.model.OaNodes;
import com.pointlion.mvc.common.model.OaSteps;
import com.pointlion.plugin.shiro.ShiroKit;

/** 
 * @ClassName: NodesUtils 
 * @Description: TODO
 * @date: 2021年1月11日 上午9:12:23  
 */
public class NodesUtils {
	
	/**
	 * 
	* @Title: insetNodes 
	* @Description: 送拟办插入节点 
	* @param startNodes
	* @param endNnodes void
	* @date 2021年1月11日上午9:16:51
	 */
	public static void insetNodes( OaSteps startNodes,List<OaSteps> endNnodes,String oid) {
		
		
		//List<OaNodes> nodesList =new ArrayList<OaNodes>();
		List<OaNodes> find = OaNodes.dao.find("select  * from  oa_nodes where oid='"+oid+"'");
			int  size= find.size();
		
			size=size+1;
			String endNodes="";
			OaNodes pNode= new OaNodes();
			pNode.setId(startNodes.getId());
			pNode.setNodename(startNodes.getUsername());
			pNode.setOrgid(startNodes.getOrgid());
			pNode.setStartnode(startNodes.getId());
			for (OaSteps oaSteps2 : endNnodes) {
				endNodes=endNodes+","+oaSteps2.getId();
			}
			pNode.setEndnode(endNodes);
			pNode.setBackup2(endNodes);
			pNode.setNodecomplete(startNodes.getIfcomplete());
			pNode.setCompletetime(startNodes.getCompletetime());
			pNode.setSortnum(size);
			pNode.setOid(oid);
			pNode.setStatus("1");
			pNode.setBackup1("1");
			pNode.setCuserid(ShiroKit.getUserId());
			pNode.setCusername(ShiroKit.getUserName());
			pNode.setCtime(DateUtil.getCurrentTime());
			pNode.save();
			
			for (OaSteps eNode : endNnodes) {
				size=size+1;
				OaNodes sNode= new OaNodes();
				
				sNode.setId(eNode.getId());
				sNode.setNodename(eNode.getUsername());
				sNode.setStartnode(eNode.getId());
				sNode.setEndnode("");
				sNode.setBackup2("");
				sNode.setNodecomplete(eNode.getIfcomplete());
				sNode.setCompletetime(eNode.getCompletetime());
				sNode.setSortnum(size);
				sNode.setOid(oid);
				sNode.setStatus("1");
				sNode.setBackup1("1");
				sNode.setOrgid(eNode.getOrgid());
				sNode.setParentid(pNode.getId());
				sNode.setCuserid(ShiroKit.getUserId());
				sNode.setCusername(ShiroKit.getUserName());
				sNode.setCtime(DateUtil.getCurrentTime());
				sNode.save();
				
				
			}
			
		
		
	}
	
	 
	
	
	public  static void   inserNode(List<OaSteps> stepsList,String oid,String level) {
		List<OaNodes> find = OaNodes.dao.find("select  * from  oa_nodes where oid='"+oid+"'");
		int size =find.size();
		for (OaSteps steps : stepsList) {
			size=size+1;
			OaNodes sNode= new OaNodes();
			sNode.setId(steps.getId());
			sNode.setNodename(steps.getUsername());
			sNode.setStartnode(steps.getId());
			sNode.setOrgid(steps.getOrgid());
			sNode.setEndnode("");
			sNode.setBackup2("");
			sNode.setNodecomplete(steps.getIfcomplete());
			sNode.setCompletetime(steps.getCompletetime());
			sNode.setSortnum(size);
			sNode.setOid(oid);
			sNode.setStatus("1");
			sNode.setBackup1(level);
			sNode.setCuserid(ShiroKit.getUserId());
			sNode.setCusername(ShiroKit.getUserName());
			sNode.setCtime(DateUtil.getCurrentTime());
			sNode.save();
		}
		
		
		
		
	}
	
	public  static void   inserNode(List<OaSteps> stepsList,String oid,String parentid,String level) {
		List<OaNodes> find = OaNodes.dao.find("select  * from  oa_nodes where oid='"+oid+"'");
		int size =find.size();
		for (OaSteps steps : stepsList) {
			size=size+1;
			OaNodes sNode= new OaNodes();
			sNode.setId(steps.getId());
			sNode.setNodename(steps.getUsername());
			sNode.setStartnode(steps.getId());
			sNode.setEndnode("");
			sNode.setBackup2("");
			sNode.setNodecomplete(steps.getIfcomplete());
			sNode.setCompletetime(steps.getCompletetime());
			sNode.setSortnum(size);
			sNode.setOid(oid);
			sNode.setOrgid(steps.getOrgid());
			
			sNode.setStatus("1");
			
		 
			if(!steps.getId().equals(parentid)) {
				sNode.setParentid(parentid);
			}
			 
				
			sNode.setBackup1(level);
			sNode.setCuserid(ShiroKit.getUserId());
			sNode.setCusername(ShiroKit.getUserName());
			sNode.setCtime(DateUtil.getCurrentTime());
			sNode.save();
		}
		
		
		
		
	}
	
	public static  void bundRelation(OaSteps startNode,List<OaSteps> endNnodes) {
		
		String endNode ="";
		
		for (OaSteps oaSteps : endNnodes) {
			endNode=endNode+","+oaSteps.getId();
		}
		
		String udpateSql="update  oa_nodes  set endnode='"+endNode+"',backup2= '"+endNode+"' where  id='"+startNode.getId()+"'";
		
		Db.update(udpateSql);
	}
	
	
	public static void  completeNode(OaSteps step) {
		String id = step.getId();
		
		OaNodes node = OaNodes.dao.findById(id);
		if(node!=null) {
			node.setCompletetime(step.getCompletetime());
			node.setNodecomplete(step.getIfcomplete());
			node.update();
		}
		
	}
	
	
	public  static void updateBundRelation(String oid,String delId,OaSteps newStep) {
		
		
		OaNodes delNode = OaNodes.dao.findById(delId);
		
		if(delNode!=null) {
			OaNodes pNode = OaNodes.dao.findById(delNode.getParentid());
			
			if(pNode!=null) {
			String newEnds = pNode.getEndnode().replaceAll(delId,newStep.getId());
			pNode.setEndnode(newEnds);
			pNode.update();
		
			delNode.setStatus("0");
			delNode.update();
			List<OaNodes> find = OaNodes.dao.find("select  * from  oa_nodes where oid='"+oid+"'");
			int size =find.size();
			
			
			OaNodes newNode=new OaNodes();
			newNode.setId(newStep.getId());
			newNode.setNodename(newStep.getUsername());
			newNode.setStartnode(newStep.getId());
			newNode.setOrgid(newStep.getOrgid());
			newNode.setEndnode("");
			newNode.setBackup2("");
			newNode.setNodecomplete("0");
			newNode.setParentid(delNode.getParentid());
			//newNode.setSortnum(delNode.getSortnum());
			newNode.setSortnum(size+1);
			newNode.setOid(oid);
			newNode.setStatus("1");
			newNode.setBackup1("1");
			newNode.setCuserid(delNode.getCuserid());
			newNode.setCtime(delNode.getCtime());
			newNode.setCusername(delNode.getCusername());
			newNode.save();
			//delNode.delete();
			
			}	
			
			
		}
		
	
		
		
	//	OaNodes.dao.find("select  * from oa_nodes where oid='"+oid+"' and endnode like '%"+delId+"%'");
		
	}
	
	
	
	/**
	 * 
	* @Title: deleteNodes 
	* @Description: TODO  删除节点
	* @date 2021年1月11日下午10:07:39
	 */
	public static void deleteNodes(String delId) {
		
	 OaNodes dNode = OaNodes.dao.findById(delId);
	 
	 OaNodes pNode = OaNodes.dao.findById(dNode.getParentid());
	 
	 pNode.setEndnode(pNode.getEndnode().replaceAll(delId, ""));
	 pNode.setBackup2(pNode.getBackup2().replaceAll(delId, ""));
	 
	 pNode.update();
	 dNode.setStatus("3");
	 dNode.update();
		
	}
	
	/**
	 * 
	* @Title: addNodes 
	* @Description: 添加节点 
	* @param oid
	* @param stepsList void
	* @date 2021年1月11日下午10:35:40
	 */
	
	public  static void addNodes(String oid,List<OaSteps> stepsList) {
		
		
		List<OaNodes> noCompleteNode = OaNodes.dao.find("select * from oa_nodes where oid='"+oid+"' and nodecomplete='0' and status='1'");
		String parentId="";
		 int noCompleteSize = noCompleteNode.size();
		if(noCompleteSize>=1) {
			OaNodes oaNodes = noCompleteNode.get(0);
			if(oaNodes.getParentid()==null) {
				parentId=oaNodes.getId();
			}else {
				parentId=oaNodes.getParentid();
			}
		}else  {
			
		}
			
			
		if(noCompleteSize>=1) {
			
			OaNodes pNodes = OaNodes.dao.findById(parentId);
			
			String newEndnode="";
			
			List<OaNodes> allNodes = OaNodes.dao.find("select  * from  oa_nodes where oid='"+oid+"'");
			int size=allNodes.size();
			for (OaSteps step : stepsList) {
				size=size+1;
				OaNodes newNode=new OaNodes();
				newNode.setId(step.getId());
				newNode.setNodename(step.getUsername());
				newNode.setStartnode(step.getId());
				newNode.setEndnode("");
				newNode.setOrgid(step.getOrgid());
				newNode.setBackup2("");
				newNode.setNodecomplete("0");
				newNode.setParentid(parentId);
				newNode.setSortnum(size);
				newNode.setOid(oid);
				newNode.setStatus("1");
				newNode.setCuserid(ShiroKit.getUserId());
				newNode.setCtime(DateUtil.getCurrentTime());
				newNode.setCusername(ShiroKit.getUserName());
				newNode.save();
				
				newEndnode=newEndnode+","+step.getId();
			}
			pNodes.setEndnode(pNodes.getEndnode()+newEndnode);
			pNodes.setBackup2(pNodes.getEndnode()+newEndnode);
			pNodes.setNodecomplete("1");
			pNodes.update();
		
		}
		
	}
	
	
	
	
	
	
	
}
