package dbm.login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class DBConnection {
	private String user;
	private String pw;
	private String driver;
	private String url;
	public Connection conn;
	private boolean isConnect;
	public DBConnection(int type, String url, String user, String pw) {
		this.url = url;
		this.user = user;
		this.pw = pw;
		
		if(type == DBLogIn.CHOOSE_ORACLE) {
			driver = "oracle.jdbc.driver.OracleDriver";
		}
		else if(type == DBLogIn.CHOOSE_MYSQL) {
			driver = "com.mysql.jdbc.Driver";
		}
	}
		public boolean connection() {
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pw);
			isConnect = true;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isConnect = false;
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "올바르지 않은 Database 사용자 입니다");
			isConnect = false;
		}
		
		return isConnect;
	}
		
	public void disconnect(Connection conn) {
		if(conn!=null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void disconnect(PreparedStatement pstmt) {
		if(pstmt!=null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void disconnect(ResultSet rs) {
		if(rs!=null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void disconnect(PreparedStatement pstmt, ResultSet rs) {
		if(rs!=null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(pstmt!=null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
