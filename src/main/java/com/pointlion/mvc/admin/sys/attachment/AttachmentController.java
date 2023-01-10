package com.pointlion.mvc.admin.sys.attachment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletResponse;

import com.pointlion.mvc.common.model.*;
import org.apache.commons.io.FileUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.RzUtils;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.util.MyPicRender;


public class AttachmentController extends BaseController {
	public static final AttachmentService service = AttachmentService.me;
	private static  final String baseUrl ="D:\\apache-tomcat-8.5.83";
	/***
	 * 列表页面
	 */
	public void getListPage(){

    	renderIframe("list.html");
    }
	/***
     * 获取分页数据
     **/
    public void listData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	Page<Record> page = service.getPage("",Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    
    /***
	 * 业务数据上传附件
	 */
	public void getBusinessUploadListPage(){
		String busid = getPara("busid","");
		String view = getPara("view","");
		setAttr("busid", busid);
		setAttr("view", view);
		setAttr("loginId", ShiroKit.getUserId());
		
		
    	renderIframeOpen("businessUploadList.html");
    }
    
	/***
	 * 获取数据列表
	 */
	public void getBusinessUploadList(){
		String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	String busid = getPara("busid","");
    	Page<Record> page = service.getPage(busid,Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    
	/***
	 * 获取业务附件数量
	 */
	public void getBusinessAttachmentCount(){
		String busid = getPara("busid");
		Integer c = service.getBusinessAttachmentCount(busid);
		renderSuccess(c, "查询成功");
	}

    /***
     * 删除
     * @throws Exception
     */
    public void delete() throws Exception{
		String ids = getPara("ids");
    	//执行删除
		service.deleteByIds(ids);
    	renderSuccess("删除成功!");
    }
    
    /***
	 * 文件上传
	 */
	public void attachmentUpload(){
		String busid = getPara("busid","");
		String moduelFrom = getPara("moduelFrom","notknowfrom");//来源
		String key = getPara("key","notknowkey");
		String path = "/attachment/"+moduelFrom+"/"+key+"/"+DateUtil.getCurrentYear()+"/"+DateUtil.getCurrentMonth()+"/"+DateUtil.getCurrentDay();//保存路径
		UploadFile file = getFile("file",path);
		String savePath = file.getUploadPath();//实际保存的路径
		String bathPath = savePath.replace(path, "");//根路径upload目录
		String filename = file.getOriginalFileName();//文件实际名字 
		String stuf = filename.substring(filename.lastIndexOf(".")+1);//扩展名
		String newUrl = path+"/"+UuidUtil.getUUID()+"."+stuf;//新文件相对路径
		String newRealFileUrl = bathPath+newUrl;//文件实际存储路径
		file.getFile().renameTo(new File(newRealFileUrl));//文件重命名
		SysAttachment attachment = new SysAttachment();
		attachment.setId(UuidUtil.getUUID());
		attachment.setUrl(newUrl);
		attachment.setRealUrl(newRealFileUrl);
		SysUser user = SysUser.dao.getById(ShiroKit.getUserId());
		SysOrg org = SysOrg.dao.getById(user.getOrgid());
		attachment.setCreateUserId(user.getId());
		attachment.setCreateUserName(user.getName());
		attachment.setCreateOrgId(org.getId());
		attachment.setCreateOrgName(org.getName());
		attachment.setCreateTime(DateUtil.getCurrentTime());
		attachment.setSuffix(stuf);
		attachment.setFileName(filename);
		attachment.setBusinessId(busid);
		attachment.setDes("1");
		attachment.save();
		renderSuccess("上传成功");
		
		 
	}
	
	/**
	 * 
	* @Title: emailAttachmentUpload 
	* @Description: 邮件附件上传
	* @date 2021年1月26日下午9:41:52
	 */
	public void emailAttachmentUpload(){
		String busid = getPara("busid","");
		String moduelFrom = getPara("moduelFrom","notknowfrom");//来源
		String key = getPara("key","notknowkey");
		String path = "/attachment/"+moduelFrom+"/"+key+"/"+DateUtil.getCurrentYear()+"/"+DateUtil.getCurrentMonth()+"/"+DateUtil.getCurrentDay();//保存路径
		
	
		System.out.println(getRequest().getContentLength());
		UploadFile file = getFile("file",path);
		String savePath = file.getUploadPath();//实际保存的路径
		String bathPath = savePath.replace(path, "");//根路径upload目录
		String filename = file.getOriginalFileName();//文件实际名字 
		String stuf = filename.substring(filename.lastIndexOf(".")+1);//扩展名
		String newUrl = path+"/"+UuidUtil.getUUID()+"."+stuf;//新文件相对路径
		String newRealFileUrl = bathPath+newUrl;//文件实际存储路径
		File toFile = new File(newRealFileUrl);
		
		
		file.getFile().renameTo(toFile);//文件重命名
		
		
		
		int limitSize=1024*1024;
		if(toFile.length()>limitSize) {
			
			toFile.delete();
			renderSuccess("上传附件不能大于1M!");
			
		}else {

			SysAttachment attachment = new SysAttachment();
			attachment.setId(UuidUtil.getUUID());
			attachment.setUrl(newUrl);
			attachment.setRealUrl(newRealFileUrl);
			SysUser user = SysUser.dao.getById(ShiroKit.getUserId());
			SysOrg org = SysOrg.dao.getById(user.getOrgid());
			attachment.setCreateUserId(user.getId());
			attachment.setCreateUserName(user.getName());
			attachment.setCreateOrgId(org.getId());
			attachment.setCreateOrgName(org.getName());
			attachment.setCreateTime(DateUtil.getCurrentTime());
			attachment.setSuffix(stuf);
			attachment.setFileName(filename);
			attachment.setBusinessId(busid);
			attachment.setDes("1");
			attachment.save();
			renderSuccess("上传成功");
			
		}
		
		
		
		 
	}
    
	/***
	 * 文件上传
	 */
	public void attachmentUpload1(){
		String busid = getPara("busid","");
		String moduelFrom = getPara("moduelFrom","notknowfrom");//来源
		String key = getPara("key","notknowkey");
		String path = "/attachment/"+moduelFrom+"/"+key+"/"+DateUtil.getCurrentYear()+"/"+DateUtil.getCurrentMonth()+"/"+DateUtil.getCurrentDay();//保存路径
		UploadFile file = getFile("file",path);
		String savePath = file.getUploadPath();//实际保存的路径
		String bathPath = savePath.replace(path, "");//根路径upload目录
		String filename = file.getOriginalFileName();//文件实际名字 
		String stuf = filename.substring(filename.lastIndexOf(".")+1);//扩展名
		String newUrl = path+"/"+UuidUtil.getUUID()+"."+stuf;//新文件相对路径
		String newRealFileUrl = bathPath+newUrl;//文件实际存储路径
		file.getFile().renameTo(new File(newRealFileUrl));//文件重命名
		
		
		Db.update("update sys_attachment set des='0'  where business_id='"+busid+"'");
		
		SysAttachment attachment = new SysAttachment();
		attachment.setId(UuidUtil.getUUID());
		attachment.setUrl(newUrl);
		attachment.setRealUrl(newRealFileUrl);
		SysUser user = SysUser.dao.getById(ShiroKit.getUserId());
		SysOrg org = SysOrg.dao.getById(user.getOrgid());
		attachment.setCreateUserId(user.getId());
		attachment.setCreateUserName(user.getName());
		attachment.setCreateOrgId(org.getId());
		attachment.setCreateOrgName(org.getName());
		attachment.setCreateTime(DateUtil.getCurrentTime());
		attachment.setSuffix(stuf);
		attachment.setFileName(filename);
		attachment.setBusinessId(busid);
		attachment.setDes("1");
		attachment.save();
		
		renderSuccess(attachment, "上传成功");
	 
		
		
	}
	
	/**
	 * 
	* @Title: attachmentUploadDepartmentsFiles 
	* @Description: TODO  部门首页文件管理
	* @date 2021年1月24日下午7:07:55
	 */
	public void attachmentUploadDepartmentsFiles(){
		

		//String busid = getPara("busid","");
		String moduelFrom = getPara("moduelFrom","notknowfrom");//来源
		String key = getPara("key","notknowkey");
		String path = "/attachment/"+moduelFrom+"/"+key+"/"+DateUtil.getCurrentYear()+"/"+DateUtil.getCurrentMonth()+"/"+DateUtil.getCurrentDay();//保存路径
		
		System.out.println(getRequest().getContentLength());
		
 
	 
		
			UploadFile file = getFile("file", path);
			//UploadFile file1 =getFile("file", path, 2*Const.DEFAULT_MAX_POST_SIZE);
			String savePath = file.getUploadPath();//实际保存的路径
			String bathPath = savePath.replace(path, "");//根路径upload目录
			String filename = file.getOriginalFileName();//文件实际名字 
			String stuf = filename.substring(filename.lastIndexOf(".")+1);//扩展名
			String newUrl = path+"/"+UuidUtil.getUUID()+"."+stuf;//新文件相对路径
			String newRealFileUrl = bathPath+newUrl;//文件实际存储路径
			
			 
			File toFile = new File(newRealFileUrl);
			
			
			file.getFile().renameTo(toFile);//文件重命名
			
			
			 
			
			System.out.println("================"+toFile.length());
			
			int limitSize=1024*1024*50;
			if(toFile.length()>limitSize) {
				
				toFile.delete();
				renderSuccess("上传附件不能大于50M!");
			}else {
				
				OaDepartmentsFiles df=new OaDepartmentsFiles();
				
				df.setId(UuidUtil.getUUID());
				df.setCtime(DateUtil.getCurrentTime());
				df.setOrgid(ShiroKit.getUserOrgId());
				df.setCuserid(ShiroKit.getUserId());
				df.setCusername(ShiroKit.getUserName());
				df.save();
				
				
				
				SysAttachment attachment = new SysAttachment();
				attachment.setId(UuidUtil.getUUID());
				attachment.setUrl(newUrl);
				attachment.setRealUrl(newRealFileUrl);
				SysUser user = SysUser.dao.getById(ShiroKit.getUserId());
				SysOrg org = SysOrg.dao.getById(user.getOrgid());
				attachment.setCreateUserId(user.getId());
				attachment.setCreateUserName(user.getName());
				attachment.setCreateOrgId(org.getId());
				attachment.setCreateOrgName(org.getName());
				attachment.setCreateTime(DateUtil.getCurrentTime());
				attachment.setSuffix(stuf);
				attachment.setFileName(filename);
				attachment.setBusinessId(df.getId());
				//attachment.setDes("1");
				attachment.setModuelFrom(ShiroKit.getUserOrgId());
				attachment.save();
				renderSuccess(attachment, "上传成功");
				
			}
			
			
		
	 
		
	 
		
	
		
	}
	
	/***
	 * 下载文件
	 * @throws IOException 
	 */
	public void downloadFile() throws IOException{
		String id = getPara("id");
		SysAttachment attachment = SysAttachment.dao.getById(id);
		String fileUrl = attachment.getUrl();
		
		System.out.println("fileUrl="+fileUrl);
		//String baseUrl = this.getRequest().getSession().getServletContext().getRealPath("");
		//String baseUrl ="D:\\apache-tomcat-7.0.82";
		String[] arry = fileUrl.split("/");
		
	 	System.out.println(arry[arry.length-1]);
		File file = new File(baseUrl+"/upload"+fileUrl);
		fileUrl=fileUrl.replace(arry[arry.length-1], attachment.getFileName());
		
		File downloadFile =new File(baseUrl+"/upload"+fileUrl);
		
		 //file.renameTo(downloadFile);//文件重命名
		//file.to
		
		
		
	    FileUtils.copyFile(file, downloadFile);
	    
	    RzUtils.insertOaRz(id, "2");
		renderFile(downloadFile);
	}
	
	
	public void  showFile() {
			
		String id = getPara("id");
		SysAttachment attachment = SysAttachment.dao.getById(id);
		String fileUrl = attachment.getUrl();
		
		System.out.println("fileUrl="+fileUrl);
		//String baseUrl = this.getRequest().getSession().getServletContext().getRealPath("");
		//String baseUrl ="D:\\apache-tomcat-7.0.82";
		//System.out.println("baseUrl"+baseUrl);
		
		File file = new File(baseUrl+"/upload"+fileUrl);
		
		MyPicRender img=new MyPicRender(file);
	    RzUtils.insertOaRz(id, "3");
		render(img);
	}
	
	
	public void lookFile() throws Exception {
		HttpServletResponse response = getResponse();
		boolean boo = true;
/*		String filePath = "E:\\file\\2018\\demo\\1PB.pdf";
		File f = new File(filePath);
		if (!f.exists()) {
		response.sendError(404, "File not found!");
		return;
		}*/
		
		String id = getPara("id");
		SysAttachment attachment = SysAttachment.dao.getById(id);
		String fileUrl = attachment.getUrl();
		
		System.out.println("fileUrl="+fileUrl);
		//String baseUrl = this.getRequest().getSession().getServletContext().getRealPath("");
		//String baseUrl ="D:\\apache-tomcat-7.0.82";
//		String baseUrl ="D:\\apache-tomcat-8.5.83";
		//System.out.println("baseUrl"+baseUrl);
		
		File f = new File(baseUrl+"/upload"+fileUrl);
		
		
		
		BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
		byte[] buf = new byte[1024];
		int len = 0;
		response.reset(); // 非常重要
		if (boo) { // 在线打开（预览）
	/*	URL u = new URL("file:///" + filePath);
		response.setContentType(u.openConnection().getContentType());*/
		response.setHeader("Content-Disposition", "inline; filename=" + f.getName());
		} 
		OutputStream out = response.getOutputStream();
		while ((len = br.read(buf)) > 0)
		out.write(buf, 0, len);
		br.close();
		out.close();

		renderNull();

		}

	/**
	 * @Method getCertListPage
	 * @param :
	 * @Date 2023/1/3 16:28
	 * @Description 获取人员证书列表
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void getCertListPage(){
		String curr = getPara("pageNumber");
		String pageSize = getPara("pageSize");
		String busid = getPara("busid","");
		XdEmployee employee = XdEmployee.dao.findById(busid);

		List<XdEmpCert> xdEmpCerts = XdEmpCert.dao.find("select * from  xd_emp_cert where ename='" + employee.getName() + "'");
		List<String> busiList=new ArrayList<>();
		xdEmpCerts.stream().forEach(xdEmpCert->  busiList.add(xdEmpCert.getId()));

		Page<Record> page = service.getPage(busiList,Integer.valueOf(curr),Integer.valueOf(pageSize));
		renderPage(page.getList(),"",page.getTotalRow());
	}
	/**
	 * @Method attachmentCertUpload
	 * @Date 2023/1/5 14:27
	 * @Description 上传头像照片
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void attachmentCertUpload(){
		String busid = getPara("busid","");
		String moduelFrom = getPara("moduelFrom","notknowfrom");//来源
		String key = getPara("key","notknowkey");
		String path = "/attachment/"+moduelFrom+"/"+key+"/"+DateUtil.getCurrentYear()+"/"+DateUtil.getCurrentMonth()+"/"+DateUtil.getCurrentDay();//保存路径
		UploadFile file = getFile("file",path);
		String savePath = file.getUploadPath();//实际保存的路径
		String bathPath = savePath.replace(path, "");//根路径upload目录
		String filename = file.getOriginalFileName();//文件实际名字
		String stuf = filename.substring(filename.lastIndexOf(".")+1);//扩展名
		String newUrl = path+"/"+UuidUtil.getUUID()+"."+stuf;//新文件相对路径
		String newRealFileUrl = bathPath+newUrl;//文件实际存储路径
		file.getFile().renameTo(new File(newRealFileUrl));//文件重命名
		SysAttachment attachment = new SysAttachment();
		String id = UuidUtil.getUUID();
		attachment.setId(id);
		attachment.setUrl(newUrl);
		attachment.setRealUrl(newRealFileUrl);
		SysUser user = SysUser.dao.getById(ShiroKit.getUserId());
		SysOrg org = SysOrg.dao.getById(user.getOrgid());
		attachment.setCreateUserId(user.getId());
		attachment.setCreateUserName(user.getName());
		attachment.setCreateOrgId(org.getId());
		attachment.setCreateOrgName(org.getName());
		attachment.setCreateTime(DateUtil.getCurrentTime());
		attachment.setSuffix(stuf);
		attachment.setFileName(filename);
		attachment.setBusinessId(busid);
		attachment.setDes("1");
		attachment.save();
		XdEmployee employee = XdEmployee.dao.findById(busid);
		employee.setCertPicId(id);
		employee.update();
		renderSuccess(id);


	}


}