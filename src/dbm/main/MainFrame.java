package dbm.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import dbm.csv.InputDB;
import dbm.login.DBConnection;
import dbm.login.DBLogIn;
import dbm.main.dialog.TableCreateDialog;
import dbm.queries.SetOracleQuery;

public class MainFrame extends JFrame{
	JPanel p_center, p_east, p_east1, p_east2, p_center2;
	JTable tb_db;
	JScrollPane scroll_db;
	JButton bt_search, bt_insert, bt_mod, bt_create, bt_del, bt_prev;
	JTextField t_path;
	JFileChooser chooser;
	public Connection conn;
	public DBConnection db;
	Dimension d_bt = new Dimension(180, 30);
	Font font = new Font("HY엽서L", Font.BOLD, 16);
	ResultSet rs;
	ResultSetMetaData rsmd;

	DBTableModel model;
	MouseListener listener_tbMouse;
	KeyListener listener_tbKey;
	ImageIcon icon;
	MainFrame me;
	String curr_table;
	public File csvFile;
	public MainFrame() {
		me = this;
		listener_tbMouse = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2) {
					int row = tb_db.getSelectedRow();
					changeTable(row);
				}
			}
		};
		listener_tbKey = new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					model.setValueAt(tb_db.getValueAt(tb_db.getSelectedRow(), tb_db.getSelectedColumn()), tb_db.getSelectedRow(), tb_db.getSelectedColumn());
					tb_db.setModel(model);
					tb_db.updateUI();
				}
			}	
		};
		
		setLayout(new BorderLayout());
		p_center = new JPanel(new BorderLayout());
		p_center.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		tb_db = new JTable();
		scroll_db = new JScrollPane(tb_db);
		
		p_center2 = new JPanel(new BorderLayout());
		t_path = new JTextField();
		t_path.setPreferredSize(new Dimension(480, 30));
		bt_prev = new JButton();
		bt_prev.setIcon(new ImageIcon((icon = new ImageIcon("res/back.png")).getImage().getScaledInstance(40, 30, Image.SCALE_SMOOTH)));
		p_center2.add(bt_prev, BorderLayout.WEST);
		p_center2.add(t_path);
		p_center.add(scroll_db);
		p_center.add(p_center2, BorderLayout.NORTH);
		
		p_east = new JPanel(new BorderLayout());
		p_east1 = new JPanel();
		bt_search = new JButton("파일 찾기");
		bt_insert = new JButton("등록");
		
		p_east1.add(bt_search);
		p_east1.add(bt_insert);
	
		changeChildSetting(p_east1, d_bt, font);
		p_east1.setPreferredSize(new Dimension(80, 450));
		p_east.setPreferredSize(new Dimension(80, 600));
		
		p_east2 = new JPanel();
		bt_create = new JButton("생성");	
		bt_mod = new JButton("수정");
		bt_del = new JButton("삭제");
		p_east2.add(bt_create);
		p_east2.add(bt_mod);
		p_east2.add(bt_del);
		changeChildSetting(p_east2, d_bt, font);
		p_east.add(p_east1, BorderLayout.NORTH);
		p_east.add(p_east2);
		
		add(p_center);
		add(p_east, BorderLayout.EAST);
		
		chooser = new JFileChooser();
		model = new DBTableModel();
		
		setBtEvent();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				db.disconnect(conn);
			}
		});
		setSize(600, 600);
		setLocationRelativeTo(null);
		setVisible(true);
		new DBLogIn(this);
		initTableSetting();
		setTableAction();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	public void setConnection(Connection conn, DBConnection db) {
		this.conn = conn;
		this.db = db;
	}
	public void setBtEvent() {
		bt_prev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(model.table_depth==1) {
					initTableSetting();
					setTableAction();
					tb_db.removeKeyListener(listener_tbKey);
					bt_create.setText("생성");
				}
			}
		});
		bt_search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser.setFileFilter(new FileNameExtensionFilter("CSV파일", "csv"));
				chooser.showOpenDialog(null);
				t_path.setText(chooser.getSelectedFile().getAbsolutePath());
				csvFile = chooser.getSelectedFile();
			}
		});
		bt_create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(model.table_depth==0) {
					new TableCreateDialog(me);
				}else {
					model.addRow();
					
					tb_db.setModel(model);
					tb_db.updateUI();
				}
			}
		});
		/*
		 bt_mod.addActionListener(new ActionListener() {		 
			public void actionPerformed(ActionEvent e) {
				if(model.table_depth==0) {
					new TableAlterDialog(me, (String)model.data[tb_db.getSelectedRow()][0]);
				}
			}
		});
		*/
		bt_mod.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(model.table_depth!=0) {
					rs = SetOracleQuery.getBeforeData(conn, curr_table);
					try {
						rsmd = rs.getMetaData();
						rs.last();
						int rowtotal=rs.getRow();
						int coltotal = rsmd.getColumnCount();
						rs.first();
						for(int i = 0 ; i<rowtotal; i++) {
							for(int a = 0 ; a<coltotal;a++) {
								if(tb_db.getValueAt(i, a)!=rs.getObject(a+1)) {
									SetOracleQuery.updateTable(conn, curr_table, tb_db.getValueAt(i, a), rs, rs.getRow(), rsmd, rsmd.getColumnName(a+1));
								}
							}
							rs.next();
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			}
		});
		bt_del.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(model.table_depth==0) {
					SetOracleQuery.dropTable(conn, (String)model.data[tb_db.getSelectedRow()][0]);
					initTableSetting();
				}else {
					Object[] record = new Object[model.data[tb_db.getSelectedRow()].length];
					for(int i = 0; i < record.length ; i++) {
						record[i] = model.data[tb_db.getSelectedRow()][i];
					}
					SetOracleQuery.deleteRecord(conn, curr_table, model.columnName, record);
					updateTable();
				}
			}
		});
		bt_insert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(csvFile!=null) {
					bt_insert.setEnabled(false);
					try {
						ResultSetMetaData rsmd = SetOracleQuery.showTableAllDetail(conn, curr_table).getMetaData();
						int colCount = rsmd.getColumnCount();
						String[] colName = new String[colCount];
						int[] colTypes = new int[colCount];
						for(int i = 0; i <colCount;i++) {
							colName[i] = rsmd.getColumnName(i+1);
							colTypes[i] = rsmd.getColumnType(i+1);
						}
						new InputDB(me, curr_table, colName, colTypes);
						t_path.setText("");
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					bt_insert.setEnabled(true);
				}		
			}
		});
		
	}
	
	
	public void setTableAction() {
		tb_db.addMouseListener(listener_tbMouse);
	}
	public void removeTableAction() {
		tb_db.removeMouseListener(listener_tbMouse);
	}
	
	
	public void initTableSetting() {
		rs = SetOracleQuery.showTableNames(conn);
		
		try {
			rs.last();
			int total = rs.getRow();
			String[][] names = new String[total][1];
			String[] columnName = {"테이블 목록"};
			System.out.println(total);
			rs.first();
			for(int i = 0 ; i<total; i++) {
				names[i][0] = rs.getString("table_name");
				rs.next();
			}
			model.setData(names);
			model.setColumnName(columnName);
			model.isEditable = false;
			model.table_depth=0;
			tb_db.setModel(model);
			tb_db.updateUI();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.disconnect(rs);
		}
	}
	/*public void insertRow() {
		String[][] data = new String[model.data.length+1][model.columnName.length];
		for(int i = 0; i <model.data.length ; i++) {
			
		}
	}*/
	
	public void updateTable() {
		rs = SetOracleQuery.showTableAllDetail(conn, curr_table);
		String[] columnNames = null;
		Object[][] data = null;
		try {
			rs.last();
			int total = rs.getRow();
			rsmd = rs.getMetaData();
			columnNames = new String[rsmd.getColumnCount()];
			int[] columnTypes = new int[rsmd.getColumnCount()];
			System.out.println(columnTypes.length+", "+columnNames.length);
			for(int i = 0 ; i<rsmd.getColumnCount();i++) {
				columnNames[i] = rsmd.getColumnName(i+1);
				columnTypes[i] = rsmd.getColumnType(i+1);
			}
			
			data = new Object[total][columnNames.length];
			rs.first();
			for(int i = 0; i <total; i++) {
				for(int a = 0; a<columnNames.length;a++) {
					data[i][a] = SetOracleQuery.dataInput(rs, a, columnTypes[a]);
				}
				rs.next();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.disconnect(rs);
		}
		model.setColumnName(columnNames);
		model.data = data;
		tb_db.setModel(model);
		tb_db.updateUI();
	}
	
	public void changeTable(int row) {
		String table_name = (String)model.data[row][0];
		curr_table = table_name;
		rs=SetOracleQuery.showTableAllDetail(conn,  table_name);
		String[] columnNames = null;
		Object[][] data = null;
		try {
			rs.last();
			int total = rs.getRow();
			rsmd = rs.getMetaData();
			columnNames = new String[rsmd.getColumnCount()];
			int[] columnTypes = new int[rsmd.getColumnCount()];
			System.out.println(columnTypes.length+", "+columnNames.length);
			for(int i = 0 ; i<rsmd.getColumnCount();i++) {
				columnNames[i] = rsmd.getColumnName(i+1);
				columnTypes[i] = rsmd.getColumnType(i+1);
			}
			
			data = new Object[total][columnNames.length];
			rs.first();
			for(int i = 0; i <total; i++) {
				for(int a = 0; a<columnNames.length;a++) {
					data[i][a] = SetOracleQuery.dataInput(rs, a, columnTypes[a]);
				}
				rs.next();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.disconnect(rs);
		}
		model.setColumnName(columnNames);
		model.data = data;
		model.table_depth+=1;
		bt_create.setText("입력");
		removeTableAction();
		model.isEditable=true;
		tb_db.addKeyListener(listener_tbKey);
		tb_db.updateUI();
	}
	
	public void changeChildSetting(Component component, Dimension d, Font font) {
		component.setPreferredSize(d);
		component.setFont(font);
		if(component instanceof Container) {
			for(Component child : ((Container)component).getComponents()) {
				try {
					JButton bt = (JButton)child;
					bt.setContentAreaFilled(false);
					changeChildSetting(child, d, font);
				} catch (ClassCastException e) {
					// TODO Auto-generated catch block
					continue;
				}
			}
		}
	}
	
	public void searchTable(String str) {
		
	}
	
	public static void main(String[] args) {
		new MainFrame();
	}
}
