package cn.itcast.ssh.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * 工作流程工具类
 * @author waterC
 *
 */
public class ActivitiUtils {
	
	public static RepositoryService repositoryService;
	public static RuntimeService runtimeService;
	public static TaskService taskService;
	public static HistoryService historyService;
	public static FormService formService;

	static{
		//从web中获取spring容器
		WebApplicationContext wac = WebApplicationContextUtils
				.getWebApplicationContext(ServletActionContext.getServletContext());
		repositoryService = (RepositoryService) wac.getBean("repositoryService");
		runtimeService = (RuntimeService)wac.getBean("runtimeService");
		taskService = (TaskService) wac.getBean("taskService");
		historyService = (HistoryService) wac.getBean("historyService");
		formService = (FormService) wac.getBean("formService");
	}
	
	/**	***************************创建表*****************************	*/
	
	/**
	 * 配置数据库参数，创建工作流所使用的23张表
	 */
	/**
	 * 创建工作流所使用的23张表
	* @param driver	数据库驱动		例：com.mysql.jdbc.Driver
	* @param url 数据库地址	jdbc:mysql://localhost:3306/activiti?useUnicode=true&characterEncoding=utf8
	* @param username	用户名
	* @param password	密码
	* @param databaseSchemaUpdate 设置数据库建表策略：
	* 			DB_SCHEMA_UPDATE_TRUE 若不存在表就创建表，若存在则直接使用
	* 			DB_SCHEMA_UPDATE_CREATE_DROP 每次都先删除表，再创建
	* 			DB_SCHEMA_UPDATE_FALSE	若不存在，则抛出异常 
	 */
	public static void createActTableByParm(String driver, String url, 
			String username, String password, String databaseSchemaUpdate){
		//1.创建Activiti配置对象的实例
		ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
		
		//2.设置数据库连接信息
		processEngineConfiguration.setJdbcDriver(driver);
		processEngineConfiguration.setJdbcUrl(url);
		processEngineConfiguration.setJdbcUsername(username);
		processEngineConfiguration.setJdbcPassword(password);
		//3.设置建表策略
		processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		
		//4.使用配置对象，创建流程引擎实例
		ProcessEngine buildProcessEngine = processEngineConfiguration.buildProcessEngine();
		
		System.out.println("配置数据库参数，创建工作流所使用的23张表：	"+buildProcessEngine);
	}
	
	/**
	 * xml配置文件，创建工作流所使用的23张表
	* @param fileName 配置文件名 	activiti.cfg.xml
	 */
	public static void createActTableByXml(String fileName){
		ProcessEngine buildProcessEngine = null;
		try {
			buildProcessEngine = ProcessEngineConfiguration
								.createProcessEngineConfigurationFromResource(fileName)
								.buildProcessEngine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("xml配置文件，创建工作流所使用的23张表：	"+buildProcessEngine);
	}
	
	/**	***************************部署对象*****************************	*/
	
	/**
	 * 通过classpath，部署流程定义
	* @param deploymentName	部署名称
	* @param bpmnFilePath	bpmn文件路径 	如：diagrams/helloworld.bpmn
	* @param pngFilePath
	 */
	public static void deploymentProcessByClasspath(String deploymentName, String bpmnFilePath, String pngFilePath){
		repositoryService.createDeployment()//创建一个部署对象
						.name(deploymentName)//添加部署的名称
						.addClasspathResource(bpmnFilePath)//从classpath的资源中加载，一次只能加载一个文件
						.addClasspathResource(pngFilePath)//从classpath的资源中加载，一次只能加载一个文件
						.deploy();//完成部署
	}
	
	/**
	 * 通过上传zip文件，部署流程定义
	* @param deploymentName	部署名称
	* @param zipInputStream	zip文件的输入流
	 */
	public static void deploymentProcessByZip(String deploymentName, ZipInputStream zipInputStream){
		repositoryService.createDeployment()//创建一个部署对象
						.name(deploymentName)//添加部署的名称
						.addZipInputStream(zipInputStream)//指定zip格式的文件完成部署
						.deploy();//完成部署
	}
	
	/**
	 * 通过InputStream，部署流程定义
	* @param deploymentName	部署名称
	* @param bpmnName	bpmn文件名称
	* @param pngName	png文件名称
	* @param bpmnFilePath	bpmn文件路径 ，从根目录开始获取，如：/diagrams/processVariables.bpmn
	* @param pngFilePath	png文件路径
	 */
	public static void deploymentProcessByInstream(String deploymentName, String bpmnName, String pngName,
												String bpmnFilePath, String pngFilePath){
		InputStream inputStreambpmn = ActivitiUtils.class.getResourceAsStream(bpmnFilePath);
		InputStream inputStreampng = ActivitiUtils.class.getResourceAsStream(pngFilePath);
		repositoryService.createDeployment()//创建一个部署对象
						.name(deploymentName)//添加部署的名称
						.addInputStream(bpmnName, inputStreambpmn)//使用资源文件的名称（要求：与资源文件的名称要一致），和输入流完成部署
						.addInputStream(pngName, inputStreampng)//使用资源文件的名称（要求：与资源文件的名称要一致），和输入流完成部署
						.deploy();//完成部署
	}
	
	/**
	 * 查询部署对象列表
	 * @return List<Deployment>	对应表（act_re_deployment）
	 */
	public static List<Deployment> findDeploymentList(){
		List<Deployment> list = repositoryService
						.createDeploymentQuery()//创建部署对象查询
						.orderByDeploymenTime().asc()//
						.list();
		return list;
	}
	
	
	/**
	 * 删除部署对象
	 * @return List<Deployment>	对应表（act_re_deployment）
	 */
	public static void deleteDeploymentById(String deploymentId){
		repositoryService.deleteDeployment(deploymentId, true);
	}
	
	
	/**	***************************流程定义*****************************	*/
	
	/**	*******查询流程定义*******	*/

	/**
	 * 根据流程定义ID，获取流程定义
	* @param processDefinitionId	流程定义ID ，来源于流程定义表act_re_procdef的主键ID；格式为流程的KEY：版本号：随机数
	* @return ProcessDefinition		流程定义	（act_re_procdef 流程定义表）
	 */
	public static ProcessDefinition findProcessDefinitionByDefinitionId(String processDefinitionId){
		ProcessDefinition processDefinition = repositoryService
									.createProcessDefinitionQuery()//创建一个流程定义的查询
									/**指定查询条件,where条件*/
									.processDefinitionId(processDefinitionId)//使用流程定义ID查询
									.singleResult();//返回唯一结果集
		return processDefinition;
	}
	
	/**
	 * 根据部署对象ID，获取流程定义
	* @param deploymentId	部署对象ID（部署ID） ，来源于部署对象表act_re_deployment的主键ID；部署流程时自动生成
	* @return ProcessDefinition	流程定义	（act_re_procdef 流程定义表）
	 */
	public static ProcessDefinition findProcessDefinitionByDeployId(String deploymentId){
		ProcessDefinition processDefinition = repositoryService
								.createProcessDefinitionQuery()//创建一个流程定义的查询
								/**指定查询条件,where条件*/
								.deploymentId(deploymentId)//使用部署对象ID查询
								.singleResult();//返回唯一结果集
		return processDefinition;
	}

	/**
	 * 根据流程定义的key，查看该流程有多少个版本
	* @param processDefinitionKey	流程定义的key ，来源于bpmn流程图的ID
	* @return long	个数（版本数）
	 */
	public static long findProcessDefinitionCountByKey(String processDefinitionKey){
		long count = repositoryService
								.createProcessDefinitionQuery()//创建一个流程定义的查询
								/**指定查询条件,where条件*/
								.processDefinitionKey(processDefinitionKey)//使用流程定义的key查询
								.count();
		return count;
	}
	
	/**
	 * 根据流程定义的key，查询该Key的流程定义列表(该KEY的所有版本流程)
	* @param processDefinitionKey	流程定义的key ，来源于bpmn流程图的ID
	* @param versionSort	排序	：asc desc
	* @return List<ProcessDefinition>	流程定义列表	（act_re_procdef 流程定义表）
	 */
	public static List<ProcessDefinition> findProcessDefinitionListByKey(String processDefinitionKey, String versionSort){
		List<ProcessDefinition> definitionlist = null;
		if("asc".equals(versionSort)){
			definitionlist = repositoryService
								.createProcessDefinitionQuery()//创建一个流程定义的查询
								/**指定查询条件,where条件*/
								.processDefinitionKey(processDefinitionKey)//使用流程定义的key查询
								/**排序*/
								.orderByProcessDefinitionVersion().asc()//按照版本的升序排列
								.list();//返回一个集合列表，封装流程定义
		}else{
			definitionlist = repositoryService
								.createProcessDefinitionQuery()//创建一个流程定义的查询
								/**指定查询条件,where条件*/
								.processDefinitionKey(processDefinitionKey)//使用流程定义的key查询
								/**排序*/
								.orderByProcessDefinitionVersion().desc()//按照版本的降序排列
								.list();//返回一个集合列表，封装流程定义
		}
		return definitionlist;
	}

	/**
	 *  查询全部流程定义列表
	* @return List<ProcessDefinition>	（act_re_procdef 流程定义表）
	 */
	public static List<ProcessDefinition> findProcessDefinitionAllList(){
		List<ProcessDefinition> list = repositoryService
				.createProcessDefinitionQuery()//
				.orderByProcessDefinitionVersion().asc()//使用流程定义的版本升序排列
				.list();
		return list;
	}
	
	/**
	 * 查询最新版本的流程定义
	* @return List<ProcessDefinition>	（act_re_procdef 流程定义表）
	 */
	public static List<ProcessDefinition> findProcessDefinitionListNewVersion(){
		List<ProcessDefinition> list = repositoryService
						.createProcessDefinitionQuery()//
						.orderByProcessDefinitionVersion().asc()//使用流程定义的版本升序排列
						.list();
		/*
		 * Map<String,ProcessDefinition>
			  map集合的key：流程定义的key
			  map集合的value：流程定义的对象
			  map集合的特点：当map集合key值相同的情况下，后一次的值将替换前一次的值
		 */
		Map<String, ProcessDefinition> map = new LinkedHashMap<String, ProcessDefinition>();
		if(list!=null && list.size()>0){
			for(ProcessDefinition pd:list){
				map.put(pd.getKey(), pd);
			}
		}
		List<ProcessDefinition> pdList = new ArrayList<ProcessDefinition>(map.values());
		return pdList;
	}
	
	
	/**	*******删除流程定义*******	*/
	
	/**
	 * 删除流程定义（不带级联的删除）
	*    只能删除没有启动的流程，如果流程启动，就会抛出异常
	* @param deploymentId 	部署对象ID（部署ID）
	 */
	public static void deleteSingleProcessDefinition(String deploymentId){
		repositoryService.deleteDeployment(deploymentId);
	}
	
	
	/**
	 * 删除流程定义（级联删除）
	 * 	  不管流程是否启动，都能可以删除
	 * @param deploymentId 	部署对象ID（部署ID）
	 */
	public static void deleteSingleProcessDefinitionTrue(String deploymentId){
		repositoryService.deleteDeployment(deploymentId, true);
	}
	
	
	/**
	 * 删除流程定义（删除key相同的所有不同版本的流程定义）
	* @param processDefinitionKey 	流程定义的key ，来源于bpmn流程图的ID
	 */
	public static void deleteProcessDefinitionByKey(String processDefinitionKey){
		//流程定义的key
		//先使用流程定义的key查询流程定义，查询出所有的版本
		List<ProcessDefinition> list = repositoryService
						.createProcessDefinitionQuery()//
						.processDefinitionKey(processDefinitionKey)//使用流程定义的key查询
						.list();
		//遍历，获取每个流程定义的部署ID
		if(list!=null && list.size()>0){
			for(ProcessDefinition pd:list){
				//获取部署ID
				String deploymentId = pd.getDeploymentId();
				repositoryService.deleteDeployment(deploymentId, true);
			}
		}
	}
	
	/**	*******流程实例*******	*/

	/**
	 * 启动流程实例
	* @param processDefinitionKey	流程定义的key ，来源于bpmn流程图的ID
	* @return ProcessInstance	流程实例	（act_ru_execution 正在执行的执行对象表）
	 */
	public static ProcessInstance startProcessInstance(String processDefinitionKey){
		ProcessInstance pi = runtimeService
						.startProcessInstanceByKey(processDefinitionKey);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
		return pi;
	}
	
	
	/**
	 * 启动流程实例,并设置流程变量
	 * @param processDefinitionKey	流程定义的key ，来源于bpmn流程图的ID
	 * @param variables	流程变量
	 * @return ProcessInstance	流程实例	（act_ru_execution 正在执行的执行对象表）
	 */
	public static ProcessInstance startProcessInstanceAndSetVar(String processDefinitionKey, Map<String, Object> variables){
		/**启动流程实例的同时，设置流程变量，使用流程变量用来指定任务的办理人，对应task.pbmn文件中#{userID}*/
//		variables.put("userID", "周芷若");//例：指定任务执行人（分配任务）
		ProcessInstance pi = runtimeService
				.startProcessInstanceByKey(processDefinitionKey, variables);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
		return pi;
	}

	/**
	 * 查询流程状态（判断流程正在执行，还是结束）
	* @param processInstanceId	流程实例ID
	* @return boolean	true-流程结束； false-流程没有结束
	 */
	public static boolean isProcessEnd(String processInstanceId){
		boolean flag = false;
		ProcessInstance pi = runtimeService
						.createProcessInstanceQuery()//创建流程实例查询
						.processInstanceId(processInstanceId)//使用流程实例ID查询
						.singleResult();
		if(pi==null){//流程已经结束
			flag = true;
		}else{//流程没有结束
		}
		return flag;
	}
	

	/**
	 * 根据流程实例ID，查询历史流程实例
	* @param processInstanceId	流程实例ID
	* @return HistoricProcessInstance	（act_hi_procinst 已经执行完的历史流程实例信息）
	 */
	public static HistoricProcessInstance findHistoryProcessInstance(String processInstanceId){
		HistoricProcessInstance hpi = historyService
						.createHistoricProcessInstanceQuery()//创建历史流程实例查询
						.processInstanceId(processInstanceId)//使用流程实例ID查询
						.singleResult();
		return hpi;
	}
	
	/**
	 * 根据流程定义的KEY，查询历史流程实例列表
	 * @param processDefinitionKey	流程定义的KEY
	 * @return List<HistoricProcessInstance>	（act_hi_procinst 已经执行完的历史流程实例信息）
	 */
	public static List<HistoricProcessInstance> findHistoryProcessInstanceListByKey(String processDefinitionKey){
		List<HistoricProcessInstance> list = historyService
						.createHistoricProcessInstanceQuery()//创建历史流程实例查询
						.processDefinitionKey(processDefinitionKey)//按照流程定义的KEY查询
						.orderByProcessInstanceStartTime().desc()//按照开始时间降序排列
						.list();
		return list;
	}
	

	/**	***************************个人任务*****************************	*/
	
	/**
	 * 根据任务办理人，查看当前任务办理人的任务个数（查询 act_ru_task）（个人任务）
	* @param assignee	任务办理人
	* @return long	任务个数
	 */
	public static long findTaskCountByAssignee(String assignee){
		
		long count = taskService
								.createTaskQuery()//创建任务查询对象
								/**查询条件（where部分）*/
								.taskAssignee(assignee)//指定个人任务查询，指定办理人
								/**排序*/
								.orderByTaskCreateTime().asc()//使用创建时间的升序排列
								/**返回结果集*/
								.count();//返回列表
		return count;
	}
	
	/**
	 * 根据任务办理人，查看所有任务列表（按创建时间正序）	
	 * @param assignee	任务办理人
	 * @return List<Task>	（ act_ru_task 正在执行的任务表）
	 */
	public static List<Task> findTaskListByAssignee(String assignee){
		
		List<Task> list = taskService
				.createTaskQuery()//创建任务查询对象
				/**查询条件（where部分）*/
				.taskAssignee(assignee)//指定个人任务查询，指定办理人
				/**排序*/
				.orderByTaskCreateTime().asc()//使用创建时间的升序排列
				/**返回结果集*/
				.list();//返回列表
		return list;
	}
	
	
	/**
	 * 根据任务办理人，查看所有任务列表（分页）（按创建时间正序）
	 * @param assignee	任务办理人
	* @param firstResult	第几条
	* @param maxResults		每页显示条数
	 * @return List<Task>	（ act_ru_task 正在执行的任务表）
	 */
	public static List<Task> findTaskListPageByAssignee(String assignee, int firstResult, int maxResults){
		
		List<Task> listPage = taskService
				.createTaskQuery()//创建任务查询对象
				/**查询条件（where部分）*/
				.taskAssignee(assignee)//指定个人任务查询，指定办理人
				/**排序*/
				.orderByTaskCreateTime().asc()//使用创建时间的升序排列
				/**返回结果集*/
				.listPage(firstResult, maxResults);//分页查询
		return listPage;
	}
	

	/**
	 * 根据任务办理人，查询历史任务
	* @param taskAssignee	任务办理人
	* @return List<HistoricTaskInstance>	（act_hi_taskinst 已经执行完的历史任务表）
	 */
	public static List<HistoricTaskInstance> findHistoryTaskList(String taskAssignee){
		List<HistoricTaskInstance> histTasklist = historyService
						.createHistoricTaskInstanceQuery()//创建历史任务实例查询
						.taskAssignee(taskAssignee)//指定历史任务的办理人
						.list();
		return histTasklist;
	}
	
	/**
	 * 根据执行对象ID，查看任务详情
	* @param executionId	执行对象ID
	* @return Task	（ act_ru_task 正在执行的任务表）
	 */
	public static Task findTaskByExecutionId(String executionId){
		Task task = taskService
						.createTaskQuery()//创建任务查询对象
						/**查询条件（where部分）*/
						.executionId(executionId)//使用执行对象ID查询
						/**返回结果集*/
						.singleResult();//返回惟一结果集
		return task;
	}
	
	/**
	 * 根据流程实例ID，查看任务详情
	 * @param executionId	流程实例ID
	 * @return Task	（ act_ru_task 正在执行的任务表）
	 */
	public static Task findTaskByProcessInstanceId(String processInstanceId){
		Task task = taskService
				.createTaskQuery()//创建任务查询对象
				/**查询条件（where部分）*/
				.processInstanceId(processInstanceId)//使用流程实例ID查询
				/**返回结果集*/
				.singleResult();//返回惟一结果集
		return task;
	}
	
	/**
	 * 根据流程定义ID，查看任务详情
	 * @param executionId	流程定义ID
	 * @return Task	（ act_ru_task 正在执行的任务表）
	 */
	public static Task findTaskByProcessDefinitionId(String processDefinitionId){
		Task task = taskService
				.createTaskQuery()//创建任务查询对象
				/**查询条件（where部分）*/
				.processDefinitionId(processDefinitionId)//使用流程定义ID查询
				/**返回结果集*/
				.singleResult();//返回惟一结果集
		return task;
	}
	
	

	/**	*******完成（办理）任务*******	*/
	
	/**
	 * 根据任务ID，完成任务（单例流程）
	* @param taskId 任务ID
	 */
	public static void completeTask(String taskId){
			taskService
						.complete(taskId);//根据任务ID，完成任务
			System.out.println("完成任务！任务ID："+taskId);
	}
	
	/**
	 * 根据任务ID、分支条件，完成任务（分支流程）
	* @param taskId	任务ID
	* @param variables Map存储流程变量(分支条件)。对应.bpmn文件中，连线的参数：${message=='不重要'}
	 */
	public static void completeBranchTask(String taskId, Map<String, Object> variables){
		taskService
					.complete(taskId,variables);
		System.out.println("完成任务：任务ID："+taskId);
	}
	

	/**	***************************组任务*****************************	*/
	
	/**
	 * 查询当前人的组任务列表
	* @param candidateUser
	* @return List<Task>
	 */
	public static List<Task> findMyGroupTask(String candidateUser){
		List<Task> list = taskService
						.createTaskQuery()//创建任务查询对象
						/**查询条件（where部分）*/
						.taskCandidateUser(candidateUser)//组任务的办理人查询
						/**排序*/
						.orderByTaskCreateTime().asc()//使用创建时间的升序排列
						/**返回结果集*/
						.list();//返回列表
		return list;
	}
	
	/**
	 * 根据任务ID，查询当前任务的组成员（办理人）列表
	* @param taskId	任务ID
	* @return List<IdentityLink>	办理人列表 （act_ru_identitylink 存放任务的办理人，包括个人任务和组任务）
	 */
	public static List<IdentityLink> findIdentityLinkByTaskId(String taskId){
		List<IdentityLink> list = taskService.getIdentityLinksForTask(taskId);
		return list;
	}
	
	/**
	 * 拾取任务(认领任务)，将组任务分给个人任务，指定任务的办理人
	* @param taskId	任务ID
	* @param userId 分配的个人ID（可以是组任务中的成员，也可以是非组任务的成员）
	 */
	public static void claim(String taskId, String userId){
		taskService.claim(taskId, userId);
	}
	
	/**
	 * 将个人任务回退到组任务。前提:之前一定是个组任务
	* @param taskId 任务ID
	 */
	public static void setAssigeeToGroup(String taskId){
		taskService.setAssignee(taskId, null);
	}
	
	/**
	 *  向组任务中添加成员
	* @param taskId	任务ID
	* @param userId 成员办理人
	 */
	public static void addGroupUser(String taskId, String userId){
		taskService.addCandidateUser(taskId, userId);
	}
	
	/**
	 * 向组任务中删除成员
	* @param taskId	任务ID
	* @param userId 成员办理人
	 */
	public static void deleteGroupUser(String taskId, String userId){
		taskService.deleteCandidateUser(taskId, userId);
	}
	

	/**	***************************流程变量*****************************	*/

	/**	*******设置流程变量*******	*/
	
	/**
	 * 设置流程变量（单个变量）
	* @param taskId	任务ID
	* @param variableName	变量名称
	* @param value 		变量值
	 */
	public static void setVariable(String taskId, String variableName, Object value){
		/**一：设置流程变量，使用基本数据类型*/
		taskService.setVariable(taskId, variableName, value);
		System.out.println("设置流程变量成功！");
	}
	
	/**
	 * 设置流程变量（单个变量），且该变量与任务进行绑定，只有通过当前taskID，才能获取该变量
	 * @param taskId	任务ID
	 * @param variableName	变量名称
	 * @param value 		变量值
	 */
	public static void setVariableLocal(String taskId, String variableName, Object value){
		/**一：设置流程变量，使用基本数据类型*/
		taskService.setVariableLocal(taskId, variableName, value);
		System.out.println("设置流程变量成功！");
	}
	
	/**
	 * 设置流程变量（javabean类型）
	 * @param taskId	任务ID
	 * @param variableName	变量名称
	 * @param value 		变量值（javaBean对象，注：实现序列化Serializable）
	 * 		当一个javaBean放置到流程变量中，要求javabean的属性不能再发生变化
	 *    	 	如果发生变化，再获取的时候，抛出异常
	 * 		解决方案：在Person对象中添加：
	 * 			private static final long serialVersionUID = 6757393795687480331L;
	 *      	同时实现Serializable 
	 */
	public static void setVariableToBean(String taskId, String variableName, Object value){
		/**二：设置流程变量，使用javabean类型*/
		taskService.setVariable(taskId, variableName, value);
		System.out.println("设置流程变量成功！");
	}

	/**
	 * 将变量存储至Map集合中，设置流程变量
	 * 				map集合的key就是流程变量的名称，map集合的value就是流程变量的值
	 * @param taskId	任务ID
	 * @param variables  Map集合
	 */
	public static void setVariablesToMap(String taskId, Map<String, ? extends Object> variables){
		/**一：设置流程变量，使用Map集合 */
		taskService.setVariables(taskId, variables);
		System.out.println("设置流程变量成功！");
	}
	
	/**	*******获取流程变量*******	*/
	
	/**
	 *  获取流程变量
	* @param taskId		任务ID
	* @param variableName		变量名称
	* @return Object	变量值
	 */
	public static Object getVariable(String taskId, String variableName){
		/**一：获取流程变量，使用基本数据类型*/
		Object object = taskService.getVariable(taskId, variableName);
		return object;
	}

	/**
	 * 获取流程变量,javaBean对象
	* @param taskId		任务ID
	* @param variableName		变量名称
	* @return Object	javaBean对象
	 */
	public static Object getVariableToBean(String taskId, String variableName){
		/**二：获取流程变量，使用javabean类型*/
		Object object = taskService.getVariable(taskId,variableName);
		return object;
	}
	
	/**
	 * 获取流程变量,Map集合
	* @param taskId		任务ID
	* @return Map<String,Object>	变量值Map集合
	 */
	public static Map<String, Object> getVariablesToMap(String taskId){
		 Map<String, Object> map = taskService.getVariables(taskId);
		 return map;
	}
	
	
	/**	***************************历史活动*****************************	*/

	/** ***********查询历史活动********* */
	
	/**
	 * 查询历史活动
	* @param processInstanceId 流程实例ID
	* @return List<HistoricActivityInstance> 	（ act_hi_actinst 历史所有完成的活动）
	 */
	public static List<HistoricActivityInstance> findHistoryActivitiList(String processInstanceId){
		List<HistoricActivityInstance> list = historyService
						.createHistoricActivityInstanceQuery()//创建历史活动实例的查询
						.processInstanceId(processInstanceId)//流程实例查询
						.orderByHistoricActivityInstanceStartTime().asc()//
						.list();
		return list;
	}
	
	

	/**	********************************************************	*/

	
}
