package com.pointlion.mvc.admin.oa.workflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.admin.sys.mobilemessage.SysMobileMessageService;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.flowable.FlowablePlugin;
import com.pointlion.plugin.shiro.ShiroKit;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.editor.constants.ModelDataJsonConstants;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.engine.*;
//import org.flowable.engine.history.HistoricTaskInstance;
//import org.flowable.engine.history.;
//import org.flowable.engine.impl.pvm.PvmActivity;
//import org.flowable.engine.impl.pvm.PvmTransition;
//import org.flowable.engine.impl.pvm.process.ActivityImpl;
//import org.flowable.engine.impl.pvm.process.ProcessDefinitionImpl;
//import org.flowable.engine.impl.pvm.process.TransitionImpl;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
//import org.flowable.engine.task.Task;
import org.apache.commons.lang3.StringUtils;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkFlowService {
	public static final WorkFlowService me = new WorkFlowService();
	public static final SysMobileMessageService mobileMessageService = SysMobileMessageService.me;
	/**
	 * ???????????????
	 * @throws UnsupportedEncodingException 
	 * */
	public void createModel(ProcessEngine pe,String name,String key) throws UnsupportedEncodingException{
		RepositoryService repositoryService = pe.getRepositoryService();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        Model modelData = repositoryService.newModel();

        ObjectNode modelObjectNode = objectMapper.createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        String description = StringUtils.defaultString("??????????????????");
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        modelData.setMetaInfo(modelObjectNode.toString());
        modelData.setName(name);
        modelData.setKey(StringUtils.defaultString(key));

        repositoryService.saveModel(modelData);
        repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
	}
	
	/***
	 * ????????????
	 * @param id
	 * @return
	 */
	@Before(Tx.class)
	public String deploy(String id) {
		String message = "";
		try {
			ProcessEngine pe = FlowablePlugin.buildProcessEngine();
			RepositoryService repositoryService = pe.getRepositoryService();
			org.flowable.engine.repository.Model modelData = repositoryService.getModel(id);
			BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
			JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
			BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
			BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
			byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel,"ISO-8859-1");
			
			String processName = modelData.getName();
			if (!StringUtils.endsWith(processName, ".bpmn20.xml")){
				processName += ".bpmn20.xml";
			}
//			System.out.println("========="+processName+"============"+modelData.getName());
			ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
			Deployment deployment = repositoryService.createDeployment().name(modelData.getName())
					.addInputStream(processName, in).deploy();
//					.addString(processName, new String(bpmnBytes)).deploy();
			
			// ??????????????????
			List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();
			for (ProcessDefinition processDefinition : list) {
				repositoryService.setProcessDefinitionCategory(processDefinition.getId(), modelData.getCategory());
				message = "????????????";
			}
			if (list.size() == 0){
				message = "??????????????????????????????";
			}
		} catch (Exception e) {
			throw new FlowableException("????????????????????????????????????????????????",e);
		}
		return message;
	}
	/***
	 * ????????????
	 */
	@Before(Tx.class)
	public void deleteModel(String id){
		FlowablePlugin.buildProcessEngine().getRepositoryService().deleteModel(id);
	}
	/***
	 * ????????????????????????????????????
	 * @throws XMLStreamException 
	 * @throws UnsupportedEncodingException 
	 */
	@Before(Tx.class)
	public void deleteModelRemainMaxVersion() throws UnsupportedEncodingException, XMLStreamException{
		List<ActReModel> list = ActReModel.dao.find("select * from act_re_model");//???????????????
		//????????????????????????
		for(ActReModel o:list){
			ActReModel max = ActReModel.dao.findFirst("select MAX(VERSION_) VERSION_ from act_re_model where KEY_='"+o.getKey()+"'");
			if(!o.getVersion().equals(max.getVersion())){
				me.deleteModel(o.getId());
			}
		}
		Page<ActReModel> list2 = ActReModel.dao.getCustomModelPage(1, 999999);//????????????????????????
		for(ActReModel o:list2.getList()){
				me.deleteModel(o.getId());
		}
//		//??????????????????
//		for(ActReModel o:list){
//			me.deploy(o.getId());
//		}
//		for(ActReModel o:list){
//			me.deleteModel(o.getId());
//		}
//		//????????????????????????????????????
//		List<Record> list2 = Db.find("select * from act_re_procdef");
//		for(Record r:list2){
//			me.convertToModel(r.getStr("ID_"));
//		}
	}
	/***
	 * ??????/??????
	 */
	@Before(Tx.class)
	public String updateState(String state,String procDefId){
		if (state.equals("active")) {
			FlowablePlugin.buildProcessEngine().getRepositoryService().activateProcessDefinitionById(procDefId, true, null);
			return "????????????";
		} else if (state.equals("suspend")) {
			FlowablePlugin.buildProcessEngine().getRepositoryService().suspendProcessDefinitionById(procDefId, true, null);
			return "????????????";
		}
		return "?????????";
	}
	
	/***
	 * ???????????????
	 */
	@Before(Tx.class)
	public Model convertToModel(String procDefId) throws UnsupportedEncodingException, XMLStreamException {
		RepositoryService repositoryService = FlowablePlugin.buildProcessEngine().getRepositoryService();
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();
		InputStream bpmnStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
		processDefinition.getResourceName());
		XMLInputFactory xif = XMLInputFactory.newInstance();
		InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
		XMLStreamReader xtr = xif.createXMLStreamReader(in);
		BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
	
		BpmnJsonConverter converter = new BpmnJsonConverter();
		ObjectNode modelNode = converter.convertToJson(bpmnModel);
		org.flowable.engine.repository.Model modelData = repositoryService.newModel();
		modelData.setKey(processDefinition.getKey());
		modelData.setName(processDefinition.getName());
		modelData.setCategory(processDefinition.getCategory());//.getDeploymentId());
		modelData.setDeploymentId(processDefinition.getDeploymentId());
		modelData.setVersion(Integer.parseInt(String.valueOf(repositoryService.createModelQuery().modelKey(modelData.getKey()).count()+1)));
	
		ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
		modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, processDefinition.getName());
		modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, modelData.getVersion());
		modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, processDefinition.getDescription());
		modelData.setMetaInfo(modelObjectNode.toString());
	
		repositoryService.saveModel(modelData);
	
		repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes("utf-8"));
	
		return modelData;
	}
	
	/***
	 * ???????????????????????????ID
	 * @param procDefId
	 * @param proInsId
	 * @param resType
	 * @return
	 * @throws Exception
	 */
	public InputStream resourceRead(String procDefId, String proInsId, String resType) throws Exception {
		RuntimeService runtimeService = FlowablePlugin.buildProcessEngine().getRuntimeService();
		RepositoryService repositoryService = FlowablePlugin.buildProcessEngine().getRepositoryService();
		if (StringUtils.isBlank(procDefId)){
			ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(proInsId).singleResult();
			procDefId = processInstance.getProcessDefinitionId();
		}
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();
		
		String resourceName = "";
		if (resType.equals("image")) {
			resourceName = processDefinition.getDiagramResourceName();
		} else if (resType.equals("xml")) {
			resourceName = processDefinition.getResourceName();
		}
		
		InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
		return resourceAsStream;
	}
	/***
	 * ???????????????????????????
	 */
	@Before(Tx.class)
	public void deleteDeployment(String deploymentId) {
		FlowablePlugin.buildProcessEngine().getRepositoryService().deleteDeployment(deploymentId, true);
	}
	/***
	 * ???????????????????????????
	 */
	@Before(Tx.class)
	public void deleteAllDeployment() {
		List<Record> list = Db.find("select * from act_re_deployment");
		for(Record r:list){
			try{
				FlowablePlugin.buildProcessEngine().getRepositoryService().deleteDeployment(r.getStr("ID_"), true);
			}catch(Exception e){
				
			}
		}
	}
	
	/***
	 * ????????????
	 * @param id
	 * @param m
	 * @param title
	 * @param var
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String startProcess(String id,com.jfinal.plugin.activerecord.Model m,String title,Map<String, Object> var){
		String modelName = m.getClass().getSimpleName();
		String defkey = WorkFlowUtil.getDefkeyByModelName(modelName);
		if(StrKit.notBlank(defkey)){
			return startProcess(id,m,defkey,title,var,ShiroKit.getUsername());
		}else{
			throw new RuntimeException("??????modelName?????????????????????????????????Key?????????????????????????????????model???????????????????????????Key!");
		}
	}
	/***
	 * ????????????
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	@Before(Tx.class)
	@SuppressWarnings("rawtypes")
	public String startProcess(String id,com.jfinal.plugin.activerecord.Model m,String defKey,String title,Map<String, Object> var){
		return startProcess(id,m,defKey,title,var,ShiroKit.getUsername());
	}
	/***
	 * ????????????,????????????????????????shiro---???????????????
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	@Before(Tx.class)
	@SuppressWarnings("rawtypes")
	public String startProcess(String id,com.jfinal.plugin.activerecord.Model m,String defKey,String title,Map<String, Object> var,String username){
		if(var==null){
			var = new HashMap<String, Object>();
		}
		if(StrKit.notBlank(title)){
			var.put(OAConstants.WORKFLOW_VAR_APPLY_TITLE, title);
		}
		var = getVar(m, var, id, username, defKey);
		ProcessInstance procIns = FlowablePlugin.buildProcessEngine().getRuntimeService().startProcessInstanceByKey(defKey,id,var);
		return procIns.getId();
	}
	
	
	/***
	 * ????????????
	 */
	@Before(Tx.class)
	public void completeTask(String taskid,String username,Map<String,Object> var){
		completeTask(taskid,username,"",var);
	}
	@Before(Tx.class)
	public void completeTask(String taskid,String username,String comment,Map<String,Object> var){
		TaskService service = FlowablePlugin.buildProcessEngine().getTaskService();
		VTasklist task = VTasklist.dao.getTaskRecord(taskid);
		String insid = task.getStr("INSID");
		if(var==null){
			var = new HashMap<String,Object>();
		}
		String pass = String.valueOf(var.get("pass"));
		if(comment==null){comment="";}
		if(Constants.FLOW_IF_AGREE_YES.equals(pass)){//????????????
			comment = "[??????]"+comment;
		}else if(Constants.FLOW_IF_AGREE_NO.equals(pass)){//???????????????
			comment = "[?????????]"+comment;
		}
		if(StrKit.notBlank(insid)&&StrKit.notBlank(comment)){
			service.addComment(taskid, insid, comment);
		}
		service.claim(taskid, username);
		service.complete(taskid, var);
		//????????????
//		mobileMessageService.sendMessage(insid);
	}
	/***
	 * ?????????????????????????????????
	 * --????????????
	 */
	public List<Record> getToDoListByKey(String tableName ,String key,String username){
		String sql = "select * from v_tasklist t ,"+tableName+" b where t.INSID=b.proc_ins_id and  t.DEFKEY='"+key+"'";
		if(StrKit.notBlank(username)){
			sql = sql + " and (t.ASSIGNEE='"+username+"' or t.CANDIDATE='"+username+"')";
		}
		return Db.find(sql);
	}

	/***
	 * ??????????????????
	 */
	public List<Record> getToDoListByUsername(String username){
		String sql = "select u.UserName,tt.* from (SELECT e.BUSINESS_KEY_ businessId,v.TEXT_ username,t.* FROM v_tasklist t,act_ru_execution e, act_ru_variable v WHERE	e.PROC_INST_ID_ = t.INSID AND t.INSID = v.PROC_INST_ID_ AND v.NAME_ = '"+OAConstants.WORKFLOW_VAR_APPLY_USERNAME+"'";
		if(StrKit.notBlank(username)){
			sql = sql + " and (t.ASSIGNEE='"+username+"' or t.CANDIDATE='"+username+"')";
		}
		sql = sql + " ORDER BY t.DEFNAME) tt LEFT JOIN sys_user u on u.username=tt.username ";
		return Db.find(sql);
	}

//	/***
//	 * ???????????????????????????
//	 * --??????????????????
//	 */
//	public Page<Record> getToDoPageByKey(int pnum,int psize,String tableName,String key ,String username){
//		String sql = " from v_tasklist t ,"+tableName+" b where t.INSID=b.proc_ins_id ";
//		if(StrKit.notBlank(key)){
//			sql = sql + "and  t.DEFKEY='"+key+"'";
//		}
//		if(StrKit.notBlank(username)){
//			sql = sql + " and (t.ASSIGNEE='"+username+"' or t.CANDIDATE='"+username+"')";
//		}
//		return Db.paginate(pnum, psize, "select * ",sql);
//	}
	/***
	 * ???????????????????????????
	 * --??????????????????
	 */
	public Page<Record> getToDoPageByKey(int pnum,int psize,String key ,String username,String sqlEXT){
		String tableName = WorkFlowUtil.getTablenameByDefkey(key);
		String sql = " from v_tasklist t ,"+tableName+" o,act_hi_procinst p where o.proc_ins_id=p.ID_ and t.INSID=o.proc_ins_id ";
		if(StrKit.notBlank(key)){
			sql = sql + "and  t.DEFKEY='"+key+"'";
		}
		if(StrKit.notBlank(username)){
			sql = sql + " and (t.ASSIGNEE='"+username+"' or t.CANDIDATE='"+username+"')";
		}
		if(StrKit.notBlank(sqlEXT)){
			sql = sql + sqlEXT;
		}
		sql = sql +" order by o.create_time desc";
		return Db.paginate(pnum, psize, "select *,p.PROC_DEF_ID_ defid  ",sql);
	}
	/***
	 * ??????????????????
	 * --???????????????????????????
	 */
	public Page<Record> getHavedonePage(int pnum,int psize,String defkey,String username,String sqlEXT){
		String tableName = WorkFlowUtil.getTablenameByDefkey(defkey);
		if(StrKit.isBlank(tableName)){
			tableName = OaApplyCustom.tableName;
		}
		String sql = "FROM "+tableName+" o, ( SELECT DISTINCT p.BUSINESS_KEY_, d.ID_ defid FROM act_hi_taskinst t, act_hi_procinst p, act_re_procdef d WHERE t.ASSIGNEE_='"+username+"' AND p.PROC_DEF_ID_ = d.ID_ AND d.KEY_ = '"+defkey+"' AND t.END_TIME_ is not NULL AND t.PROC_INST_ID_ = p.ID_) tt WHERE tt.BUSINESS_KEY_ = o.id ";
//		String sql  = " from "+tableName+" o , (select BUSINESS_KEY_,d.ID_ defid from act_hi_identitylink i,act_hi_procinst p,act_re_procdef d where i.TYPE_='participant' and p.ID_=i.PROC_INST_ID_ and p.PROC_DEF_ID_=d.ID_ and d.KEY_='"+defkey+"' and i.USER_ID_='"+username+"') tt where tt.BUSINESS_KEY_=o.id";
		if(StrKit.notBlank(sqlEXT)){
			sql = sql + sqlEXT;
		}
		sql = sql +" order by o.create_time desc";
		return Db.paginate(pnum, psize, " select o.*,defid ", sql);
	}
	/***
	 * ?????????????????????
	 * --??????????????????
	 */
	public Page<Record> getFlowCCPage(int pnum,int psize,String key ,String userid,String sqlEXT){
		String tableName = WorkFlowUtil.getTablenameByDefkey(key);
		if(StrKit.isBlank(tableName)){
			tableName = OaApplyCustom.tableName;
		}
		String sql = " from "+tableName+" o, oa_flow_carbon_c  cc, act_hi_procinst p  where o.proc_ins_id=p.ID_ and cc.business_id = o.id and cc.user_id='"+userid+"' and cc.defkey='"+key+"' ";
		if(StrKit.notBlank(sqlEXT)){
			sql = sql + sqlEXT;
		}
		sql = sql +" order by o.create_time desc";
		return Db.paginate(pnum, psize, "select o.*,p.PROC_DEF_ID_ defid  ",sql);
	}
	/***
	 * ??????????????????
	 */
	public List<Record> getHisTaskList(String insid){
		//oracle????????????comment???event??????????????????sql??????
		//select  t.NAME_ taskName,t.ASSIGNEE_ assignee,t.EXECUTION_ID_ exeId,t.ID_ taskId,t.END_TIME_ endTime,c.MESSAGE_ message from act_hi_taskinst t LEFT JOIN (select * from ACT_HI_COMMENT dd where dd.TYPE_!='event')c  ON c.TASK_ID_=t.ID_ where t.proc_inst_id_ = '18fb7c365c99425da16cfdb2c1a643f9' and c.ACTION_='AddComment' order by t.end_time_ asc
		List<Record> taskList = Db.find("select  t.NAME_ taskName,t.ASSIGNEE_ assignee,t.EXECUTION_ID_ exeId,t.ID_ taskId,t.END_TIME_ endTime,c.MESSAGE_ message from act_hi_taskinst t LEFT JOIN act_hi_comment c  ON c.TASK_ID_=t.ID_ where t.proc_inst_id_ = '"+insid+"' and (c.TYPE_='comment' or c.TYPE_ is null) order by t.end_time_ is null,t.end_time_ asc");
		for(Record r : taskList){
			String assignee = r.getStr("assignee");
			if(StrKit.notBlank(assignee)){
				SysUser user  = SysUser.dao.getByUsername(assignee);
				if(user!=null){
					r.set("assignee", user.getName()+"["+assignee+"]");
				}else{
					r.set("assignee", "?????????["+assignee+"]");
				}
			}else{
				List<VTasklist> tl = VTasklist.dao.find("select * from v_tasklist t where t.TASKID='"+r.getStr("taskId")+"'");
				List<String> cl = new ArrayList<String>();
				for(VTasklist t : tl){
					String c = t.getCANDIDATE();
					if(StrKit.notBlank(c)){
						SysUser u = SysUser.dao.getByUsername(c);
						if(u!=null){
							cl.add(u.getName()+"["+c+"]");
						}else{
							cl.add("?????????["+c+"]");
						}
					}else{
						cl.add("?????????["+c+"]");
					}
					
				}
				r.set("assignee", StringUtils.join(cl,","));
			}
			String message = r.getStr("message");
			if(message==null){
				r.set("message","");
			}
			String endtime = r.getStr("endTime");
			r.set("endTime", StrKit.notBlank(endtime)?endtime.substring(0,endtime.indexOf(".")):"");
		}
		return taskList;
//		return Db.find("SELECT t.assignee_,	u.name,	t.name_,	t.end_time_,	c.message_ FROM	sys_user u ,	act_hi_taskinst t LEFT JOIN act_hi_comment c ON t.id_ = c.task_id_ where u.username=t.ASSIGNEE_ AND t.proc_inst_id_ = '"+insid+"' ORDER BY t.end_time_ desc ");
	}
	
	/***
	 * ???????????????????????????
	 */
	public List<Record> getHisTaskParter(String insid){
		return Db.find("select i.*,u.name from act_hi_identitylink i,sys_user u where u.username=i.USER_ID_ AND PROC_INST_ID_='"+insid+"'");
	}
	
	/***
	 * ??????????????????id??????????????????ID
	 * @param insid
	 * @return
	 */
	public String getDefIdByInsId(String insid){
		Record proc = Db.findFirst("select * from act_hi_procinst p where p.PROC_INST_ID_=?",insid);
		return proc.getStr("PROC_DEF_ID_");
	}
	
	/***
	 * ????????????defkey????????????????????????
	 * @param defKey
	 * @return
	 */
	public String getDefNameByDefKey(String defKey){
		ActReProcdef def = ActReProcdef.dao.findFirst("select * from act_re_procdef p where p.KEY_='"+defKey+"' ORDER BY VERSION_ DESC");
		if(def!=null){
			return def.getName();
		}else{
			return "";
		}
	}
	
	/***
	 * ??????????????????
	 */
	public void deleteIns(String procid){
		ProcessEngine pe = FlowablePlugin.buildProcessEngine();
		Record run = Db.findFirst("select * from act_ru_execution t where t.PROC_INST_ID_='"+procid+"'");
		if(run!=null){
//			try{
				pe.getRuntimeService().deleteProcessInstance(procid, "??????????????????");
//			}catch(Exception e){
//				e.printStackTrace();
//			}
		}
		Record his = Db.findFirst("select * from act_hi_procinst t where t.PROC_INST_ID_='"+procid+"'");
		if(his!=null){
//			try{
				pe.getHistoryService().deleteHistoricProcessInstance(procid);
//			}catch(Exception e){
//				e.printStackTrace();
//			}
		}
	}
	
	/***
	 * ????????????
	 */
	public void callBack(String procid){
		deleteIns(procid);
	}
	public void updateIfCompleteAndIfAgree(String tableName,String pass,String id){
		if(StrKit.isBlank(tableName)){//???????????????????????????????????????????????????
			tableName = OaApplyCustom.tableName;
		}
		String ifAgree = Constants.IF_AGREE_NO;//?????????
		if(Constants.SUBMIT_PASS_YES.equals(pass)){//??????
			ifAgree = Constants.IF_AGREE_YES;
		}
		Db.update("UPDATE "+tableName+" SET if_complete = '"+Constants.IF_COMPLETE_YES+"' WHERE id = '"+id+"' ");//?????????????????????
		Db.update("UPDATE "+tableName+" SET if_agree = '"+ifAgree+"' WHERE id = '"+id+"' ");//????????????
	}
	
	
	//---------------- mobile  zhouzhongyan --------------------
	/**
	 * ????????????list
	 * @author 28995
	 * @param username
	 * @return
	 */
	
	/**
	 * ??????????????????
	 * @author 28995
	 * @param username
	 * @return
	 */
	public int getTodoNum(String username){
		String sql = "select count(1) NUM from v_tasklist where 1=1 ";
		if(StrKit.notBlank(username)){
			sql = sql + " and ASSIGNEE='"+username+"' or CANDIDATE='"+username+"'";
		}
		Record result =  Db.findFirst(sql);
		return Integer.parseInt(result.getStr("NUM"));
	}
	public int getHaveDoneNum(String username){
		String sql = "select COUNT(1) NUM FROM oa_contract_apply o, ( SELECT DISTINCT p.BUSINESS_KEY_, d.ID_ defid FROM act_hi_taskinst t,"
				+ " act_hi_procinst p, act_re_procdef d WHERE t.ASSIGNEE_='"+username+"' AND p.PROC_DEF_ID_ = d.ID_ AND t.END_TIME_ is not NULL"
				+ " AND t.DELETE_REASON_='completed' AND t.PROC_INST_ID_ = p.ID_) tt WHERE tt.BUSINESS_KEY_ = o.id  order by o.create_time desc";

		Record re = Db.findFirst(sql);
		return Integer.parseInt(re.getStr("NUM"));
		
	}
	
	/***
	 * ????????????
	 */
//	public void callBackTask(String taskId){
//		ProcessEngine pe =  FlowablePlugin.buildProcessEngine();
//		HistoryService historyService = pe.getHistoryService();
//		RuntimeService runTimeService = pe.getRuntimeService();
//		RepositoryService repositoryService = pe.getRepositoryService();
//		TaskService taskService = pe.getTaskService();
//		try {
//            Map<String, Object> variables;
//            // ??????????????????
//            HistoricTaskInstance currTask =historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
//            // ??????????????????
//            ProcessInstance instance = runTimeService.createProcessInstanceQuery().processInstanceId(currTask.getProcessInstanceId()).singleResult();
//            if (instance == null) {
////                log.error("??????????????????");
//            }
//            variables=instance.getProcessVariables();
//            // ??????????????????
//            ProcessDefinitionEntity definition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(currTask.getProcessDefinitionId());
//            if (definition == null) {
////                log.error("?????????????????????");
//            }
//            // ?????????????????????
//            ActivityImpl currActivity = ((ProcessDefinitionImpl) definition).findActivity(currTask.getTaskDefinitionKey());
//            List<PvmTransition> nextTransitionList = currActivity.getOutgoingTransitions();
//            for (PvmTransition nextTransition : nextTransitionList) {
//                PvmActivity nextActivity = nextTransition.getDestination();
//                List<HistoricTaskInstance> completeTasks = historyService.createHistoricTaskInstanceQuery().processInstanceId(instance.getId()).taskDefinitionKey(nextActivity.getId()).finished().list();
//                int finished = completeTasks.size();
//                if (finished > 0) {
////                    log.error("???????????????????????????????????????????????????");
//                }
//                List<Task> nextTasks = taskService.createTaskQuery().processInstanceId(instance.getId()).taskDefinitionKey(nextActivity.getId()).list();
//                for (Task nextTask : nextTasks) {
//                    //??????????????????????????????
//                    List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
//                    List<PvmTransition> pvmTransitionList = nextActivity.getOutgoingTransitions();
//                    for (PvmTransition pvmTransition : pvmTransitionList) {
//                        oriPvmTransitionList.add(pvmTransition);
//                    }
//                    pvmTransitionList.clear();
//                    //???????????????
//                    ActivityImpl nextActivityImpl = ((ProcessDefinitionImpl) definition).findActivity(nextTask.getTaskDefinitionKey());
//                    TransitionImpl newTransition = nextActivityImpl.createOutgoingTransition();
//                    newTransition.setDestination(currActivity);
//                    //????????????
//                    taskService.complete(nextTask.getId(), variables);
//                    historyService.deleteHistoricTaskInstance(nextTask.getId());
//                    //????????????
//                    currActivity.getIncomingTransitions().remove(newTransition);
//                    List<PvmTransition> pvmTList = nextActivity.getOutgoingTransitions();
//                    pvmTList.clear();
//                    for (PvmTransition pvmTransition : oriPvmTransitionList) {
//                        pvmTransitionList.add(pvmTransition);
//                    }
//                }
//            }
//            historyService.deleteHistoricTaskInstance(currTask.getId());
//        } catch (Exception e) {
//        	e.printStackTrace();
//        }
//	}
	
	
	/***
	 * ???????????????????????????
	 * @param var
	 * @param username
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, Object> getVar(com.jfinal.plugin.activerecord.Model m ,Map<String, Object> var,String id,String username,String defkey){
		var.put(OAConstants.WORKFLOW_VAR_APPLY_USERNAME, username);
		var.put(OAConstants.WORKFLOW_VAR_PROCESS_INSTANCE_START_TIME, DateUtil.getCurrentTime());
		var.put(OAConstants.WORKFLOW_VAR_APPLY_BUSINESS_CLASSNAME, m.getClass().getName());
		return var;
	}

	/***
	 * ????????????????????????
	 */
	public Record getTaskRecord(String id){
		return Db.findFirst("SELECT * FROM v_tasklist t WHERE t.TASKID='"+id+"'");
	}
}
