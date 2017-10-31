
<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.opentext.livelink.service.docman.Node"%>
<%@page import="uds.opentext.dm.OTUtility"%>
<%@page import="java.util.List"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% int x=1;  %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style type="text/css">
		
 			.browseRow1 { background-color: #FFFFFF;  } 
 			.browseRow2 { background-color: #FFFFFF;  }
	</style>
	<script src="script/jquery-3.2.1.min.js"></script>
  	<script src="script/jquery-3.2.1.js"></script>
  	<script src="script/jquery-ui.js"></script>
	 <LINK REL="stylesheet" TYPE="text/css" HREF="style/screen.css?v=1344.1360">
	 <link rel="stylesheet" TYPE="text/css"  href="style/jquery-ui.css">
	 <link rel="stylesheet" type="text/css" href="style/datatables.min.css"/>
	 <script type="text/javascript" src="script/datatables.min.js"></script>
	 <script type="text/javascript" src="script/ot.js"></script>
	<style type="text/css">
		.tabs {
			float:right!important;
			}
		.updatedBrowse .browseListHeader TD {
   			height: 40px;
			}
			
		table.dataTable thead > tr > th {
    		 padding-left: 30px !important;
   			 padding-right: initial !important;
		}

	    table.dataTable thead .sorting,
	   	table.dataTable thead .sorting_asc, 
	    table.dataTable thead .sorting_desc, 
	    table.dataTable thead .sorting_asc_disabled, 
	    table.dataTable thead .sorting_desc_disabled{
	    	 background-position: center;    
	     }
	     
		table.dataTable tbody th, table.dataTable tbody td{
	     	padding-right: 0px;
	     }
		table.dataTable thead .sorting_asc{
			background-image: none;
		}
			
		.options {
				background: transparent;
				padding: 5px 35px 5px 5px;
				font-size: 12px;
				-webkit-appearance: none;
				-moz-appearance: none;
				appearance: none;
			}
				
		.dataTables_filter{
			margin-right: 1.6em;	
			display:inline-table;
					
			}
				
		 .dataTables_length{
			display:inline-block;
			}		
	</style>
<title>OpenText Document List</title>
</head>
<body>
	<div id="header-inner">
		<h1 id="logo">
			<img alt="" src="headerbar_content_server.png">
		</h1>
	</div>
	
	
	<div style="min-height: 117px; ">
		<table  width="100%" border="0" class="level1table"  align="Right">
			<tbody >
				<tr>
					<td align="right" class="lavel1td browseRow2"></td>
					<td align="left" class="lavel1td browseRow2">Request Type</td>
					<td align="right" class="lavel1td browseRow2"></td>
					<td align="left" class="lavel1td browseRow2">ID</td>
					
				</tr>
				<tr>
					<td align="right" class="lavel1td browseRow2"></td>
					<td align="left" class="lavel1td browseRow2">Office</td>
					<td align="right" class="lavel1td browseRow2"></td>
					<td align="left" class="lavel1td browseRow2">Date</td>
					
				</tr>
				<tr>
					<td align="right" class="lavel1td browseRow2"></td>
					<td align="left" class="lavel1td browseRow2">Req Status</td>
					<td align="right" class="lavel1td browseRow2"></td>
					<td align="left" class="lavel1td browseRow2">Customer Name</td>	
				</tr>
				<tr>
					<td></td>
					<td></td>
					<td align="right" class="lavel1td browseRow2"></td>	
					<td align="left" class="lavel1td browseRow2">Customer ID</td>
				</tr>
			</tbody>
		</table>
	</div>
		<div style="margin-top:-10 px;">
	
			<%
				String parentID=request.getParameter("parentID");
			
				//need to write logic to get parent id from DB
				
				List<Node> nodes=OTUtility.getChildren(85457);//parent ID
				List<Node> nodes2=OTUtility.excludedNodes(85457);
				
				
			%>	
			<c:set var="n" value="<%=nodes%>"/>
			<c:set var="k" value="<%=nodes2%>"/>
			<div id="tabs"  >
  				
				<ul>
					<li class="tabs"><a href="#tabs-1">Documents</a></li>
					<li class="tabs"><a href="#tabs-2">Excluded</a></li>
					<li class="tabs"><a href="#tabs-3">Comments</a></li>
					<div class="options">
  						<select class="" onchange="downloadAllFiles();" onfocus="this.selectedIndex = -1;">
						  	<option value="Scan">Scan</option>
						  	<option id="Display">Display</option>
						  	<option id="Upload">Upload</option>
						  	<option id="Export">Export </option>
						  	<option id="Copy">Copy</option>
						  	<option id="Move">Move</option>
						  	<option value="Rename">Rename</option>
						  	<option id="Exclude">Exclude</option>
						  	<option value="Retrieve">Retrieve</option>
						 	 <option id="Delete">Delete</option>
						  	<option value="Send">Send</option>
						</select>
  					</div>
				</ul>
				<div id="tabs-1">
				 <form name=form1 method=post>
	   				<table id="CScontent" width="100%" border="0" cellspacing="1" cellpadding="0" class="browseTable updatedBrowse">
						<thead>
							<tr class="browseListHeader">
							 	<td class="browseListHeaderCheck"></td>
							 	<td class="browseListHeaderName">Data ID</td>
							 	<td class="browseListHeaderName">Comments</td>
							 	<td class="browseListHeaderName">Created Date</td>
						       	<td class="browseListHeaderName" >Documents</td>
						       	<td class="browseListHeaderCheck" style="background-image: none";>
							 		<input type="checkbox" name="top_checkbox" value="checkbox" onClick="toggle(this);">
							 	</td>
						    </tr>
						</thead>
						<c:forEach items="${n}" var="element">
							<tr class="browseRow2">
								<td align="right" class="browseRow2">
					    			<a href="#" id="${element.getID()}" onclick="downlaodFile(this.id,'download');" title="Download">Download</a>
					    			<div id="z85229" class="functionMenuDiv"></div>
						    	</td>
								<td align="right"  class="browseRow2"><c:out value="${element.getID()}"/></td>
							 	<td align="right"  class="browseRow2"><c:out value="${element.getComment()}"/></td>
							 	<td align="right"  class="browseRow2"><c:out value="${element.getCreateDate()}"/></td>
								<td align="right"  class="browseRow2">
					    			<a class="browseItemNameContainer"><c:out value="${element.getName()}"/></a>
					    			<div id="z85229" class="functionMenuDiv"></div>
						    	</td>
						    		
						    	<td  class="browseListHeaderCheck ">
									<input   type="checkbox" name="foo" id="ckb" value="${element.getID()}">
								</td>		
						    </tr>
						</c:forEach>
	 				</table>
	 			</form>
	  		</div>
  			<div id="tabs-2">
    			<form name=form1 method=post>
	   				<table id="Exclude" width="100%" border="0" cellspacing="1" cellpadding="0" class="browseTable updatedBrowse">
						<thead>
							<tr class="browseListHeader">
							 	<td class="browseListHeaderCheck"></td>
							 	<td class="browseListHeaderName">Data ID</td>
							 	<td class="browseListHeaderName">Comments</td>
							 	<td class="browseListHeaderName">Created Date</td>
						       	<td class="browseListHeaderName" >Documents</td>
						       	<td class="browseListHeaderCheck" style="background-image: none";>
							 		<input type="checkbox" name="top_checkbox" value="checkbox" onClick="toggle(this);">
							 	</td>
						    </tr>
						</thead>
						<c:forEach items="${k}" var="element">
							<tr class="browseRow2" style="color: red">
								<td align="right" class="browseRow2">
					    			<a href="#" id="${element.getID()}" onclick="downlaodFile(this.id,'download');" title="Download">Download</a>
					    			<div id="z85229" class="functionMenuDiv"></div>
						    	</td>
								<td align="right"  class="browseRow2"><c:out value="${element.getID()}"/></td>
							 	<td align="right"  class="browseRow2"><c:out value="${element.getComment()}"/></td>
							 	<td align="right"  class="browseRow2"><c:out value="${element.getCreateDate()}"/></td>
								<td align="right"  class="browseRow2">
					    			<a class="browseItemNameContainer"><c:out value="${element.getName()}"/></a>
					    			<div id="z85229" class="functionMenuDiv"></div>
						    	</td>
						    		
						    	<td  class="browseListHeaderCheck ">
									<input   type="checkbox" name="foo" id="ckb" value="${element.getID()}">
								</td>		
						    </tr>
						</c:forEach>
	 				</table>
	 			</form>
  			</div>
  			<div id="tabs-3">
    			<p></p>
    		</div>
		</div>	
		</div>
 			<p class="copyright">
				OpenText Content Server version 16. Copyright © 1995 - 2016 Open Text. All Rights Reserved.
			</p>
</body>
</html>