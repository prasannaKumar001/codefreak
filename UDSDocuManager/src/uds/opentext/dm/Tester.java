package uds.opentext.dm;

import java.sql.Connection;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.util.GregorianCalendar;
import java.util.List;

import com.opentext.livelink.service.docman.Node;

public class Tester {

	public static void main(String[] args) {
		
		//Connection con=DBUtility.getConnection();
		OTUtility.deleteDocument(84696);
		//String contextID=OTUtility.getContext(85230, authToken);
		//OTUtility.downloadDoc(authToken,"D:\\SEC\\output\\85230.pdf", contextID);
		//System.out.println(authToken);
		//OTUtility.uploaddocument("C:\\Users\\pr878892\\Downloads\\Archive_Center_-_Active-active_Cluster_Overview.pptx");
		
		/*List<Node> nodes=OTUtility.getChildren(85457);
		
		for(Node n:nodes)
		{
			System.out.println(n.getName());
			
			
			
			
		}
*/
		
		//OTUtility.copyDocument(89191, 84358, null);
	}

}
