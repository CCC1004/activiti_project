<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<script type="text/javascript" href="${pageContext.request.contextPath }/js/jquery-2.1.3.min.js"></script>
<%@ include file="/js/commons.jspf" %>

<%@taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>流程部署列表</title>
<script type="text/javascript">
	function deleteAllDeploy(key){
		if(confirm("确认删除该流程吗？")){
			jQuery.post("processAction_deleteAllPDByKey.action",{"key":key},function(result){
				if(result!="sucess" && result != null){
					alert('删除失败！');
				}
			})
		}
	}
</script>
</head>
<body>
		<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
		  <tr>
		    <td height="30"><table width="100%" border="0" cellspacing="0" cellpadding="0">
		      <tr>
		        <td height="24" bgcolor="#353c44"><table width="100%" border="0" cellspacing="0" cellpadding="0">
		          <tr>
		            <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
		              <tr>
		                <td width="6%" height="19" valign="bottom"><div align="center"><img src="${pageContext.request.contextPath }/images/tb.gif" width="14" height="14" /></div></td>
		                <td width="94%" valign="bottom"><span class="STYLE1">部署流程列表</span></td>
		              </tr>
		            </table></td>
		            <td><div align="right"><span class="STYLE1">
		              </span></div></td>
		          </tr>
		        </table></td>
		      </tr>
		    </table></td>
		  </tr>
		  <tr>
		    <td><table width="100%" border="0" cellpadding="0" cellspacing="1" bgcolor="#a8c7ce" onmouseover="changeto()"  onmouseout="changeback()">
		      <tr>
		        <td width="12%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">ID</span></div></td>
		        <td width="17%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">名称</span></div></td>
		        <td width="10%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">流程定义的KEY</span></div></td>
		        <td width="10%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">流程定义的版本</span></div></td>
		        <td width="15%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">流程定义的规则文件名称</span></div></td>
		        <td width="15%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">流程定义的规则图片名称</span></div></td>
		        <td width="8%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">部署ID</span></div></td>
		        <td width="13%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">操作</span></div></td>
		      </tr>
		      <s:if test="#pdList!=null && #pdList.size()>0">
		      	<s:iterator value="#pdList">
		      		<tr>
				        <td height="20" bgcolor="#FFFFFF" class="STYLE6"><div align="center"><s:property value="id"/></div></td>
				        <td height="20" bgcolor="#FFFFFF" class="STYLE19"><div align="center"><s:property value="name"/></div></td>
				        <td height="20" bgcolor="#FFFFFF" class="STYLE19"><div align="center"><s:property value="key"/></div></td>
				        <td height="20" bgcolor="#FFFFFF" class="STYLE6"><div align="center"><s:property value="version"/></div></td>
				        <td height="20" bgcolor="#FFFFFF" class="STYLE6"><div align="center"><s:property value="resourceName"/></div></td>
				        <td height="20" bgcolor="#FFFFFF" class="STYLE6"><div align="center"><s:property value="diagramResourceName"/></div></td>
				        <td height="20" bgcolor="#FFFFFF" class="STYLE6"><div align="center"><s:property value="deploymentId"/></div></td>
				        <td height="20" bgcolor="#FFFFFF">
				        	<div align="center" class="STYLE21">
					        	<a href="processAction_deployAllVersionList.action?key=<s:property value="key"/>">流程历史版本</a> 
					        	&nbsp;
					        	<a href="javascript:;" onclick="deleteAllDeploy('<s:property value="key"/>')">删除</a>
					 		</div>
					 	</td>
				    </tr> 
		      	</s:iterator>
		      </s:if>
		      
		    </table></td>
		  </tr>
	</table>
	<hr>
	<br/>
	<!-- 发布流程 -->
	<form action="processAction_deployToZip.action" enctype="multipart/form-data" method="POST">
		<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
		  <tr>
		    <td height="30"><table width="100%" border="0" cellspacing="0" cellpadding="0">
		      <tr>
		        <td height="24" bgcolor="#353c44"><table width="100%" border="0" cellspacing="0" cellpadding="0">
		          <tr>
		            <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
		              <tr>
		                <td width="6%" height="19" valign="bottom"><div align="center"><img src="${pageContext.request.contextPath }/images/tb.gif" width="14" height="14" /></div></td>
		                <td width="94%" valign="bottom"><span class="STYLE1">部署流程定义</span></td>
		              </tr>
		            </table></td>
		            <td><div align="right"><span class="STYLE1">
		              </span></div></td>
		          </tr>
		        </table></td>
		      </tr>
		    </table></td>
		  </tr>
		  <tr>
		    <td>
		    	<div align="left" class="STYLE21">
					流程名称：<s:textfield name="filename" cssStyle="width: 200px;"/><br/>
					流程文件：<s:file name="file" cssStyle="width: 200px;"/><br/>
					<input type="submit" value="上传流程" class="button_ok"/>
				</div>
		    </td>
		  </tr>
	</table>
		
	</form>
</body>
</html>