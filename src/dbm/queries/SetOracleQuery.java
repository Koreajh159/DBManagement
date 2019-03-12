package dbm.queries;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import javax.swing.JOptionPane;

public class SetOracleQuery {
	private static ResultSet rs;
	private static PreparedStatement pstmt;
	
	public static ResultSet showTableNames(Connection conn) { 
		String sql = "select table_name from user_tables";
		try {
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	
	public static ResultSet showTableAllDetail(Connection conn, String table_name) {
		String sql = "select * from "+table_name;
		try {
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	
	///////////////////////////////////////////////////// DDL /////////////////////////////////////////////////////
	
	public static Object dataInput(ResultSet rs,int col, int type) {
		Object obj = null;
		int cnt = col+1;
		try {
			switch(type) {
				case Types.ARRAY:obj=rs.getArray(cnt);break;
				case Types.BIGINT:obj=rs.getLong(cnt);break;
				case Types.INTEGER:obj=rs.getInt(cnt);break;
				case Types.DECIMAL:obj=rs.getBigDecimal(cnt);break;
				case Types.LONGVARCHAR:obj=rs.getString(cnt);break;
				case Types.VARCHAR:obj=rs.getString(cnt);break;
				case Types.DATE:obj=rs.getDate(cnt);break;
				case Types.FLOAT:obj=rs.getDouble(cnt);break;
				case Types.DOUBLE:obj=rs.getDouble(cnt);break;
				case Types.TIME:obj=rs.getTime(cnt);break;
				case Types.BOOLEAN:obj=rs.getBoolean(cnt);
				case Types.DATALINK:obj=rs.getURL(cnt);break;
				case Types.SMALLINT:obj=rs.getShort(cnt);break;
				case Types.TINYINT:obj=rs.getByte(cnt);break;
				case Types.REAL:obj=rs.getFloat(cnt);break;
				case Types.NUMERIC:obj=rs.getInt(cnt); if(rs.wasNull()) {
					obj = null;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			if(e.getMessage().equals("Overflow Exception")) {
				JOptionPane.showMessageDialog(null, "데이터베이스에 용량을 초과하는 데이터가 삽입되어 있습니다.");
			}else {
				e.printStackTrace();
			}
		}
		if(obj==null) {
			System.out.println("null포인트 익셉션");
		}
		return obj;
	}
	
	public static void createTable(Connection conn, String title, String[][] str) {
		StringBuffer sb = new StringBuffer();
		sb.append("create table "+title+"(");
		for(int i = 0 ; i <str.length;i++) {
			sb.append(str[i][0]+" ");
			sb.append(str[i][1]);
			if(!str[i][2].equals("")) {
				sb.append("("+str[i][2]+")");
			}
			if(!str[i][3].equals("")) {
				sb.append(" default "+str[i][3]);
			}
			if(!str[i][4].equals("없음")&&!str[i][4].equals("")) {
				sb.append(" "+str[i][4]);
			}
			if(i<str.length-1) {
				sb.append(" , ");
			}else {
				sb.append(")");
			}
		}
		System.out.println(sb.toString());
		try {
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "등록 실패");
		}
	}
	
	public static void alterTable(Connection conn, String table_name) {
		StringBuffer sb = new StringBuffer();
		sb.append("alter table "+table_name);
	}
	
	public static String[][] descTable(Connection conn, String table_name) {
		String sql = "select * from "+table_name;
		String[][] desc = null;
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			ResultSetMetaData rsmd = rs.getMetaData();
			desc = new String[rsmd.getColumnCount()][4];
			for(int i=0; i<desc.length;i++) {
				desc[i][0] = rsmd.getColumnName(i+1); //Column 명
				desc[i][1] = rsmd.getColumnTypeName(i+1); //Column type
				desc[i][2] = Integer.toString(rsmd.getPrecision(i+1)); //Column 크기
				if(rsmd.isNullable(i+1)==rsmd.columnNoNulls)desc[i][3] = "not null";//Column nullable
				else if(rsmd.isNullable(i+1)==rsmd.columnNullable)desc[i][3] = "없음";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return desc;
	}
	
	
	public static void dropTable(Connection conn, String table_name) {
		String sql = "drop table "+table_name;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "삭제 실패");
			e.printStackTrace();
		}
	}
	
	public static boolean isExistTable(Connection conn, String table_name) {
		String sql = "select table_name from user_tables where table_name='"+table_name.toUpperCase()+"'";
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	///////////////////////////////////////////////// DML /////////////////////////////////////////////////////
	
	public static void deleteRecord(Connection conn, String table_name, String[] columnName, Object[] record) {
		 StringBuffer sb = new StringBuffer();
		 sb.append("delete "+table_name+" where ");
		 for(int i = 0; i<columnName.length ; i++) {
			 if(i!=0 && record[i]!=null) {
				 sb.append(" and ");
			 }
			 try {
				 if(record[i]!=null) {
					 sb.append(columnName[i]+"='"+(String)record[i]+"'");
				 }
			 }catch(ClassCastException e) {
				 String IntegerCast = "java.lang.Integer cannot be cast to java.lang.String";
				 if(e.getMessage().equals(IntegerCast)) {
					 sb.append(columnName[i]+"="+Integer.toString((int)record[i]));
				 }
			 }
		 }
		 try {
			 System.out.println(sb.toString());
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static ResultSet getBeforeData(Connection conn, String table_name) {
		String sql = "select * from "+table_name;
		try {
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rs;
	}
	public static void updateTable(Connection conn, String table_name, Object data, ResultSet rs, int row, ResultSetMetaData rsmd, String colName) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("update "+table_name+" set "+colName+"="+data+" where ");
		try {
			rs.first();
			for(int i = 0; i <row ; i++) {
				rs.next();
			}
			for(int i=0;i<rsmd.getColumnCount();i++) {
				if(rsmd.getColumnName(i+1)!=colName) {
					sb.append(rsmd.getColumnName(i+1)+"="+rs.getObject(i+1));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}