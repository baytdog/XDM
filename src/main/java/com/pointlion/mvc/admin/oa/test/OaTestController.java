package com.pointlion.mvc.admin.oa.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.qchoice.OaQchoiceService;
import com.pointlion.mvc.admin.oa.questions.OaQuestionsService;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.OaAnswers;
import com.pointlion.mvc.common.model.OaQchoice;
import com.pointlion.mvc.common.model.OaQuestions;
import com.pointlion.mvc.common.model.OaTest;
import com.pointlion.mvc.common.model.OaTestresult;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class OaTestController extends BaseController {
	public static final OaTestService service = OaTestService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	public static OaQuestionsService qservice = OaQuestionsService.me;
	public static OaQchoiceService cservice = OaQchoiceService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		renderIframe("list.html");
	}
	public void getListPage1(){
		renderIframe("list1.html");
    }
	/***
     * list page data
     **/
    public void listData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
		String endTime = getPara("endTime","");
		String startTime = getPara("startTime","");
		String applyUser = getPara("applyUser","");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * list page qdata
     **/
    public void listQData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	String tid = getPara("tid","");
    	Page<Record> page = qservice.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),tid);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * list page qdata
     **/
    public void listCData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	String qid = getPara("qid","");
    	Page<Record> page = cservice.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),qid);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    
    
    
    public void anw() {
    	String id = getPara("id");
    	Map<String,List<OaQchoice>> map=new HashMap<>();
    	List<OaQuestions> find = OaQuestions.dao.find("select  * from  oa_questions  where  testid='"+id+"'");
    	for (OaQuestions q : find) {
			List<OaQchoice> find2 = OaQchoice.dao.find("select  * from oa_qchoice where qid='"+q.getId()+"'  order by qchoice");
			map.put(q.getId(), find2);
		}
    keepPara("id");
    	setAttr("qs", find);
    	set("cs", map);
    	
    	renderIframe("answers.html");
    }
    public void check() {
    	String id = getPara("id");
    	Map<String,List<OaQchoice>> map=new HashMap<>();
    	List<OaQuestions> find = OaQuestions.dao.find("select  * from  oa_questions  where  testid='"+id+"'");
    	for (OaQuestions q : find) {
    		List<OaQchoice> find2 = OaQchoice.dao.find("select  * from oa_qchoice where qid='"+q.getId()+"'  order by qchoice");
    		map.put(q.getId(), find2);
    	}
    	
    	
    	String  choiceids="";
    	List<OaAnswers> find2 = OaAnswers.dao.find("select  * from  oa_answers where tid='"+id+"' and cuser='"+ShiroKit.getUsername()+"'");
    	
    	for (OaAnswers oaAnswers : find2) {
			
    		String qid = oaAnswers.getQid();
    		String[] cvalue = oaAnswers.getCvalue().split(",");
    		for (String  c : cvalue) {
				
    		 OaQchoice findFirst = OaQchoice.dao.findFirst("select  * from  oa_qchoice where qid='"+qid+"' and  qchoice='"+c+"'");
    			
    		 choiceids=choiceids+","+findFirst.getId();
			}
		}
    	
    	
    	
    	keepPara("id");
    	setAttr("qs", find);
    	set("cs", map);
    	
    	set("choicedids",choiceids);
    	
    	renderIframe("answers.html");
    }
    
    
	public void doSub() {

		String[] paras = getParas("ids");

		String tid = getPara("tid");

		Map<String, String> choiceMap = new HashMap<>();

		if (paras != null) {

			List<OaQuestions> qs = OaQuestions.dao.find("select  * from oa_questions  where  testid='" + tid + "'");

			for (String para : paras) {

				// System.out.println(string);
				OaQchoice choice = OaQchoice.dao.findById(para);
				if (choiceMap.get(choice.getQid()) == null || choiceMap.get(choice.getQid()).equals("")) {
					choiceMap.put(choice.getQid(), choice.getQchoice());
				} else {
					choiceMap.put(choice.getQid(), choiceMap.get(choice.getQid()) + "," + choice.getQchoice());
				}

			}


			int score = 0;

			for (Map.Entry<String, String> entry : choiceMap.entrySet()) {
				String mapKey = entry.getKey();
				String mapValue = entry.getValue();
				List<OaAnswers> find = OaAnswers.dao.find("select * from oa_answers where  qid='"+mapKey+"' and cuser='"+ShiroKit.getUsername()+"'");
				if(find.size()==0) {
				
				OaAnswers os = new OaAnswers();
				os.setId(UuidUtil.getUUID());
				os.setTid(tid);
				os.setQid(mapKey);
				os.setCvalue(mapValue);
				OaQuestions question = OaQuestions.dao.getById(mapKey);
				if (question.getQanswer().equals(mapValue)) {
					os.setScore(question.getQscore());
					score += Integer.valueOf(question.getQscore());
				} else {
					os.setScore("0");
				}

				os.setCuser(ShiroKit.getUsername());
				os.setCuserid(ShiroKit.getUserId());
				os.setCtime(DateUtil.getCurrentTime());
				os.setCanswer(question.getQanswer());
				os.save();
				System.out.println(mapKey + ":" + mapValue);
				}
			}

			OaTestresult oresult = new OaTestresult();

			oresult.setId(UuidUtil.getUUID());
			oresult.setTid(tid);
			oresult.setAnswers(ShiroKit.getUsername());
			oresult.setAnswersid(ShiroKit.getUserId());
			oresult.setScore(String.valueOf(score));

			if (choiceMap.size() == qs.size()) {
				oresult.setStatus("1");
			} else {
				oresult.setStatus("0");
			}
			oresult.save();

		}
		
		renderSuccess();
	}
    
    
    
    /***
     * save data
     */
    public void save(){
    	OaTest o = getModel(OaTest.class);
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
    		//o.setCreateTime(DateUtil.getCurrentTime());
    		o.setCuser(ShiroKit.getUserName());
    		o.setCtime(DateUtil.getCurrentTime());
    		o.setIfpublish("0");
    	
    		o.save();
    	}
    	renderSuccess();
    }
    /***
     * edit page
     */
    public void getEditPage(){
    	
	List<SysOrg> orgLists = SysOrg.dao.find("select *  from  sys_org where parent_id='root'  and name !='中心领导' order by sort ");
		
		setAttr("orgLists",orgLists);
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		OaTest o = new OaTest();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		if("detail".equals(view)){
    			/*if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}*/
    		}
    	}else{
    	/*	SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());*/
			/*o.setOrgId(org.getId());
			o.setOrgName(org.getName());
			o.setUserid(user.getId());
			o.setApplyerName(user.getName());*/
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaTest.class.getSimpleName()));
		renderIframe("edit.html");
    }
    
    
    public void getAddQ() {
    	
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", "detail");
		
		OaTest o = OaTest.dao.getById(id);
		setAttr("o", o);
		
		renderIframe("editForm.html");
    	
    }
    
    
    public void  saveQ() {
    	OaQuestions o = getModel(OaQuestions.class);
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
    		o.setCuser(ShiroKit.getUserName());
    		o.setCdate(DateUtil.getCurrentTime());
    		o.save();
    	}
    	renderSuccess();
    
    }
    public void  saveQC() {
    	OaQchoice o = getModel(OaQchoice.class);
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
    		o.setCuser(ShiroKit.getUserName());
    		o.setCtime(DateUtil.getCurrentTime());
    		o.save();
    	}
    	renderSuccess();
    	
    }
    
    
    
    
    /***
     * del
     * @throws Exception
     */
    public void delete() throws Exception{
		String ids = getPara("ids");
		service.deleteByIds(ids);
    	renderSuccess("删除成功!");
    }
    /***
     * del
     * @throws Exception
     */
    public void deleteQ() throws Exception{
    	String ids = getPara("ids");
    	qservice.deleteByIds(ids);
    	renderSuccess("删除成功!");
    }
    /***
     * submit
     */
    public void startProcess(){
    	String id = getPara("id");
    	OaTest o = OaTest.dao.getById(id);
    //	o.setIfSubmit(Constants.IF_SUBMIT_YES);
		//String insId = wfservice.startProcess(id, o,null,null);
    	//o.setProcInsId(insId);
    	o.update();
    	renderSuccess("submit success");
    }
    /***
     * callBack
     */
    public void callBack(){
    	String id = getPara("id");
    	try{
    		OaTest o = OaTest.dao.getById(id);
        /*	wfservice.callBack(o.getProcInsId());
        	o.setIfSubmit(Constants.IF_SUBMIT_NO);
        	o.setProcInsId("");*/
        	o.update();
    		renderSuccess("callback success");
    	}catch(Exception e){
    		e.printStackTrace();
    		renderError("callback fail");
    	}
    }

	
}