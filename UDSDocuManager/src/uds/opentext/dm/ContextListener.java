package uds.opentext.dm;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

public class ContextListener implements ServletContextListener {
	
	 /**
     * Initialize log4j when the application is being started
     */
	 
	
    @Override
    public void contextInitialized(ServletContextEvent event) {
        // initialize log4j here
        ServletContext context = event.getServletContext();
        String log4jConfigFile = context.getInitParameter("log4j-config-location");
        String fullPath = context.getRealPath("") + File.separator + log4jConfigFile;
        System.setProperty("log_dir", "UDSDocumanager"); 
        PropertyConfigurator.configure(fullPath);
         
    }
    
    public  String getDate()
    {
    	Timestamp timesatmp=new Timestamp(System.currentTimeMillis());
		String time=timesatmp.toString();
		  
        Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
        Matcher match= pt.matcher(time);
        while(match.find())
        {
           String s= match.group();
           time=time.replaceAll("\\"+s, "");
        }
		return time;
    }
    
    public void contextDestroyed()
    {
    	
    }

}
