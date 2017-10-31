package uds.opentext.dm;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.sun.xml.internal.ws.developer.StreamingDataHandler;

/**
 * Servlet implementation class DocumentList
 */
public class DocumentList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static final Logger LOGGER = Logger.getLogger(DocumentList.class); 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DocumentList() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest reqest, HttpServletResponse response)
	 */
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String param=request.getParameter("dataID");
		String action=request.getParameter("action");
		if(action.equals("download") || action.equals("Display"))
		{
			int dataID=Integer.valueOf(param);
			String authToken=OTUtility.getAuthToken();
			String contextID=OTUtility.getContext(dataID, authToken);
			StreamingDataHandler downloadStream=OTUtility.downloadDoc(authToken,contextID);
			
			byte[] bytes = null;
		    ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    downloadStream.writeTo(bos);
		    bos.flush();
		    bos.close();
		    bytes = bos.toByteArray();
		    response.setContentType("application/pdf");  
		  
		    if(action.equals("Display"))
		    {
		    	response.setHeader("Content-Disposition","inline; filename=\"" + dataID+".pdf" + "\"");   
		    }
		    else
		    {
		    	response.setHeader("Content-Disposition","attachment; filename=\"" + dataID+".pdf" + "\""); 
		    }
		  
		    OutputStream outputstream=response.getOutputStream();
		    outputstream.write(bytes);
		    outputstream.close();
		   
		}
		
		if(action.equals("Copy"))
		{
			//System.out.println("in post");
			String par=request.getParameter("parentID");
			String name=request.getParameter("name");
			String parent=request.getParameter("dataID");
			if((par!=null&&name!=null&&parent!=null))
			{
				System.out.println(par);
				System.out.println(name);
				int parentID=Integer.valueOf(par);
				int dataID=Integer.valueOf(parent);			 
				OTUtility.copyDocument(parentID, dataID, name);
				PrintWriter os=response.getWriter();
				os.write("<script type=text/javascript"+"> alert('completed')</script>");
			}
		}
			
		if(action.equals("Move"))
		{
			//System.out.println("in post");
			String par=request.getParameter("parentID");
			//String name=request.getParameter("name");
			String data=request.getParameter("dataID");
			if((par!=null&&data!=null))
			{
				//System.out.println(par);
				
				int parentID=Integer.valueOf(par);
				int dataID=Integer.valueOf(data);			 
				String res=OTUtility.moveDocument(dataID,parentID);
				if(res!=null&&res.equals("Moved")){
					PrintWriter os=response.getWriter();
					os.write("<script type=text/javascript"+"> alert('completed')</script>");
				}
			}
		}
		
		if(action.equals("Delete"))
		{
			//System.out.println("in post");
			//String par=request.getParameter("parentID");
			//String name=request.getParameter("name");
			String data=request.getParameter("dataID");
			if((data!=null))
			{
				//System.out.println(par);
				
				//int parentID=Integer.valueOf(par);
				int dataID=Integer.valueOf(data);			 
				String res=OTUtility.deleteDocument(dataID);
				if(res!=null&&res.equals("deleted")){
					PrintWriter os=response.getWriter();
					os.write("<script type=text/javascript"+">window.close()</script>");
				}
			}
		}
		
		
		
		 
			
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		 
	}

}
