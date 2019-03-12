package dbm.login;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import dbm.main.MainFrame;

public class DBLogIn extends JDialog{
	JPanel p_north, p_nor1, p_nor2, p_south;
	Choice ch_db;
	JTextField id;
	JPasswordField pw;
	JButton bt_ok, bt_cancel;
	public static int CHOOSE_ORACLE = 1;
	public static int CHOOSE_MYSQL = 2;
	
	DBConnection db;
	MainFrame main;
	public DBLogIn(MainFrame main) {
		setLayout(new BorderLayout());
		
		p_north = new JPanel(new BorderLayout());
		p_nor1 = new JPanel();
		p_nor2 = new JPanel();
		p_south = new JPanel();
		
		ch_db = new Choice();
		ch_db.setPreferredSize(new Dimension(80, 20));
		ch_db.add("Oracle");
		ch_db.add("MySQL");
		p_nor1.add(ch_db);
		
		id = new JTextField();
		id.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER) {
					checkUser();
				}
			}
		});
		id.setPreferredSize(new Dimension(100, 20));
		pw = new JPasswordField();
		pw.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER) {
					checkUser();
				}
			}
		});
		pw.setPreferredSize(new Dimension(100, 20));
		p_nor2.add(id);
		p_nor2.add(pw);
		
		bt_ok = new JButton("확인");
		bt_cancel = new JButton("취소");
		p_south.add(bt_ok);
		p_south.add(bt_cancel);
		this.main = main;
		bt_ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkUser();
			}
		});
		bt_cancel.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
				dispose();
			}
		});
		p_north.add(p_nor1, BorderLayout.WEST);
		p_north.add(p_nor2);
		add(p_north);
		add(p_south, BorderLayout.SOUTH);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				id.requestFocus();
			}
		});
		setSize(250, 130);
		setLocationRelativeTo(main);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		setVisible(true);
	}
	
	public void checkUser() {
		char[] pw_arr = pw.getPassword();
		String pass = "";
		for(int i = 0 ; i<pw_arr.length;i++) {
			pass+=pw_arr[i];
		}
		System.out.println(pass);
		String url = null;
		if(ch_db.getSelectedIndex()==0) {
			url = "jdbc:oracle:thin:@localhost:1521:XE";
			if((db = new DBConnection(CHOOSE_ORACLE, url, id.getText(), pass)).connection()) {
				main.setConnection(db.conn, db);
				dispose();
			}
		}
		else if(ch_db.getSelectedIndex()==1) {
			url = "jdbc:mysql://localhost:3306/";
			if(new DBConnection(CHOOSE_MYSQL, url, id.getText(), pass).connection()) {
				main.setConnection(db.conn, db);
				dispose();
			}
		}
		
	}
}
