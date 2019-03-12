package dbm.csv;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import dbm.main.MainFrame;

public class InputThread extends Thread{
	Connection conn;
	MainFrame main;
	ReadCSV readCSV;
	File file;
	boolean isInStr = false;
	boolean isEnd = false;
	String[] str;
	String table_name;
	String[] colName;
	int[] colTypes;
	public InputThread(MainFrame main, String table_name, String[] colName, int[] colTypes) {
		this.main = main;
		file = main.csvFile;
		this.table_name = table_name;
		this.colName = colName;
		this.colTypes = colTypes;
		conn = this.main.conn;
		readCSV = new ReadCSV(file);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!isEnd) {
			inputStart(table_name, colName, colTypes);
		}
		main.updateTable();
		file = null;
		main.csvFile = null;
	}
	public void inputStart(String table_name, String[] colName, int[] colTypes) {
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();
		if((str=readCSV.readLine())!=null) {
			sb.append("insert into "+table_name+"(");
			for(int i = 0; i <colName.length ; i++) {
				sb.append(colName[i]);
				if(i<colName.length-1) {
					sb.append(", ");
				}else {
					sb.append(")");
				}
			}
			sb.append(" values(");
			for(int i = 0; i <colName.length ; i++) {
				sb.append("?");
				if(i<colName.length-1) {
					sb.append(", ");
				} else {
					sb.append(")");
				}
			}
			try {
				pstmt = conn.prepareStatement(sb.toString());
				setData(pstmt, colTypes);
				int result = pstmt.executeUpdate();
				if(result == 0) {
					System.out.println("등록 실패");
				}else {
					System.out.println("등록 성공");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				main.db.disconnect(pstmt);
			}
		}
		else {
			isEnd=true;
		}
	}
	public void setData(PreparedStatement pstmt, int[] colTypes) {
		for(int i = 0 ; i<colTypes.length;i++) {
			try {
				switch(colTypes[i]) {
				case Types.NUMERIC : pstmt.setInt(i+1, Integer.parseInt(str[i].trim()));
				case Types.VARCHAR : pstmt.setString(i+1, str[i]);
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
