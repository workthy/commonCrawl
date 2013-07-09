package common.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;



public class Dbop {
	
	Connection con = null;
	PreparedStatement ps=null;
	
	public  Connection getConnetion() throws SQLException{	
		String driver = "com.mysql.jdbc.Driver";
		// String url = "jdbc:mysql://123.58.173.237:3306/crawl";
		String url = "jdbc:mysql://localhost:3306/crawl";
		String user = "crawl";
		String password = "crawler";
		// 加载驱动程序
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// 连续数据库
		con = DriverManager.getConnection(url, user, password);
		return con;
		
	}
	
	public  void closeConnection(Connection con) throws SQLException{	
		
		if(con != null){
			con.close();
		}
	}

	
	public void execute(String sql) throws SQLException{	
		con = this.getConnetion();		
		ps = con.prepareStatement(sql);
		ps.execute(sql);
		ps.close();
		this.closeConnection(con);
	}
	
	
		
}
