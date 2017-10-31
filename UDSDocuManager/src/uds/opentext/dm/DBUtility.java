package uds.opentext.dm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtility {
	
	public static String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static String DB_URL = "jdbc:sqlserver://C-RHQ-ECMDEV\\DOL:1433;databaseName=UDS";
	public static String USER = "UDSUSER";
	public static String PASS = "Password@321";
	
	public static Connection getConnection()
	{
		Connection con=null;
		try 
		{
			Class.forName(JDBC_DRIVER);
			System.out.println("Connecting to database...");
			con=DriverManager.getConnection(DB_URL,USER,PASS);
		}
		catch (ClassNotFoundException | SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;	
	}
	
	public String closeConnection(Connection con)
	{
		String status = "failed";
		if(con!=null)
		{
			try
			{	
				con.close();
				status="Success";
			} 
			catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				status="Failed";
				e.printStackTrace();
			}
		}
		return status;
	}
	
	

}
