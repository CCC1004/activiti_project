package cn.itcast.ssh.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.repository.ProcessDefinition;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.ssh.service.ILeaveBillService;
import cn.itcast.ssh.service.IWorkflowService;
import cn.itcast.ssh.utils.ValueContext;
import cn.itcast.ssh.web.form.WorkflowBean;

/**
 * <p>Title:ProcessAction </p>
* <p>Description: 流程</p>
* <p>Company: 北京亿沃特科技有限公司</p> 
* @author caochaochao
* @date 2018年10月17日 上午11:38:11
 */
@SuppressWarnings("serial")
public class ProcessAction extends ActionSupport implements ModelDriven<WorkflowBean>{

	private WorkflowBean workflowBean = new WorkflowBean();
	
	@Override
	public WorkflowBean getModel() {
		return workflowBean;
	}
	
	private String result;

	private IWorkflowService workflowService;
	
	private ILeaveBillService leaveBillService;

	public void setLeaveBillService(ILeaveBillService leaveBillService) {
		this.leaveBillService = leaveBillService;
	}

	public void setWorkflowService(IWorkflowService workflowService) {
		this.workflowService = workflowService;
	}
	
	/**
	 * 发布流程
	* @return String
	 */
	public String deployToZip(){
		File file = workflowBean.getFile();
		String deploymentName = workflowBean.getFilename();
		workflowService.deploymentProcessByZip(deploymentName , file);
		return "deployList";
	}
	
	/**
	 * @Title: deployList 
	* @Description: 部署流程列表
	* @return String
	* @throws
	 */
	public String deployList(){
		List<ProcessDefinition> pdList = workflowService.findProcessDefinitionListNewVersion();
		//放置到上下文对象中
		ValueContext.putValueContext("pdList", pdList);
		return "deployList";
	}
	
	/**
	 * 流程定义所有版本列表
	* @return String
	 */
	public String deployAllVersionList(){
		String key = workflowBean.getKey();
		List<ProcessDefinition> allList = workflowService.findProcessDefinitionListByKey(key);
		ValueContext.putValueContext("allList", allList);
		return "deployAllList";
	}
	
	/**
	 * 根据KEY，删除该KEY所有版本的流程定义
	*  void
	 */
	public void deleteAllPDByKey(){
		HttpServletResponse response = ServletActionContext.getResponse();
		String key = workflowBean.getKey();
		try {
			workflowService.deleteAllPDByKey(key);
			result = "sucess";
		} catch (Exception e) {
			result = "error";
			e.printStackTrace();
		}
		writeJson(response,result);
	}
	
	
	
	/**
	 * 根据deploymentId，删除该流程定义
	 *  void
	 */
	public void deletePDById(){
		HttpServletResponse response = ServletActionContext.getResponse();
		String deploymentId = workflowBean.getDeploymentId();
		try {
			workflowService.deletePDById(deploymentId);
			result = "sucess";
		} catch (Exception e) {
			result = "error";
			e.printStackTrace();
		}
		writeJson(response,result);
	}
	
	
	/**
	 * 向前台写json格式的数据
	 * @param response 由调用者指定response对象
	 * @param object 被转换的对象
	 */
	public void writeJson(HttpServletResponse response, Object object) {
		try {
			//String json = JSON.toJSONStringWithDateFormat(object, "yyyy-MM-dd HH:mm:ss");
			String json = JSONObject.toJSONString(object,
					SerializerFeature.WriteMapNullValue,
					SerializerFeature.WriteDateUseDateFormat,
					SerializerFeature.WriteNullNumberAsZero,
					SerializerFeature.WriteNullStringAsEmpty);
			response.setContentType("application/json;charset=utf-8");
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(json);
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
