package uds.opentext.dm;

import java.sql.Connection;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.util.GregorianCalendar;
import java.util.List;

import com.opentext.livelink.service.docman.Node;

public class Tester {

	public static void main(String[] args) {
		
		List<Node> children=OTUtility.excludedNodes(85457);
		System.out.println("Excluded Node: "+children.size());
	}

}
