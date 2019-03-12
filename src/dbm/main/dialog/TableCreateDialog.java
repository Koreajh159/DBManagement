package dbm.main.dialog;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import dbm.main.MainFrame;
import dbm.queries.SetOracleQuery;

public class TableCreateDialog extends JDialog{
	JPanel p_main, p_bt, p_bt1, p_bt2, p_mainLabel, p_mainForm;
	JScrollPane scroll;
	JScrollBar bar;
	JButton bt_create, bt_cancel, bt_add, bt_del;
	
	JLabel l_colName, l_size, l_type, l_limit, l_default, l_sel;
	
	Dimension d_inputForm, d_inputL, d_inputS, d_mainForm;
	ArrayList<JPanel> arr_p = new ArrayList<JPanel>();

	int mainFormHeight=35;
	
	public TableCreateDialog(MainFrame main) {
		Font font = new Font("HY엽서L", Font.BOLD, 13);
		Font font_label = new Font("굴림", Font.BOLD, 13);
		Dimension d_bt = new Dimension(60, 30);
		d_inputL = new Dimension(100, 25);
		d_inputS = new Dimension(60, 25);
		d_inputForm = new Dimension(470, 30);
		
		setLayout(new BorderLayout());
		p_main = new JPanel(new BorderLayout());
		p_bt = new JPanel(new BorderLayout());
		p_bt1 = new JPanel();
		bt_add = new JButton("추가");
		bt_add.setMargin(new Insets(0,0,0,0));
		bt_add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createInputForm();
			}
		});
		bt_del = new JButton("제거");
		bt_del.setMargin(new Insets(0,0,0,0));
		bt_del.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteColumns();
			}
		});
		p_bt1.add(bt_add);
		p_bt1.add(bt_del);
		p_bt1.setPreferredSize(new Dimension(60, 150));
		
		p_bt2 = new JPanel();
		bt_create = new JButton("생성");
		bt_create.setMargin(new Insets(0,0,0,0));
		bt_create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String title = inputTitle();
				if(SetOracleQuery.isExistTable(main.conn, title)) {
					JOptionPane.showMessageDialog(null, "이미 존재하는 테이블이름 입니다.");
				}else {
					SetOracleQuery.createTable(main.conn, title, getTableInformation());
					main.initTableSetting();
					dispose();
				}
			}
		});
		bt_cancel = new JButton("취소");
		bt_cancel.setMargin(new Insets(0,0,0,0));
		bt_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		p_bt2.add(bt_create);
		p_bt2.add(bt_cancel);
		
		p_bt.setPreferredSize(new Dimension(80, 300));
		p_bt.add(p_bt1, BorderLayout.NORTH);
		p_bt.add(p_bt2);
		
		changeChildBtSetting(p_bt1, d_bt, font);
		changeChildBtSetting(p_bt2, d_bt, font);
		
		p_mainLabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		p_mainLabel.setBorder(BorderFactory.createLineBorder(Color.red));
		l_colName = new JLabel("컬럼 명", SwingConstants.CENTER);
		l_colName.setPreferredSize(d_inputL);
		l_type = new JLabel("타입", SwingConstants.CENTER);
		l_type.setPreferredSize(d_inputS);
		l_size = new JLabel("크기", SwingConstants.CENTER);
		l_size.setPreferredSize(d_inputS);
		l_default = new JLabel("디폴트", SwingConstants.CENTER);
		l_default.setPreferredSize(d_inputL);
		l_limit = new JLabel("제약조건", SwingConstants.CENTER);
		l_limit.setPreferredSize(d_inputS);
		l_sel = new JLabel("선택", SwingConstants.CENTER);
		l_sel.setPreferredSize(d_inputS);
		
		
		p_mainLabel.add(l_colName);
		p_mainLabel.add(l_type);
		p_mainLabel.add(l_size);
		p_mainLabel.add(l_default);
		p_mainLabel.add(l_limit);
		p_mainLabel.add(l_sel);
		changeChildSetting(p_mainLabel, font_label);
		p_mainLabel.setPreferredSize(new Dimension(470, 25));
		p_mainForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
		d_mainForm = new Dimension(470, mainFormHeight*p_mainForm.getComponentCount());
		p_mainForm.setPreferredSize(d_mainForm);
		
		p_main.add(p_mainLabel, BorderLayout.NORTH);
		p_main.add(p_mainForm);
		p_main.setBorder(BorderFactory.createLineBorder(Color.blue));
		scroll = new JScrollPane(p_main, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll);
		add(p_bt, BorderLayout.EAST);
		setSize(570, 300);
		setLocationRelativeTo(main);
		setModal(true);
		setVisible(true);
	}
	
	public void createInputForm() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		JTextField t_colName, t_size, t_default;
		Choice ch_dataType, ch_limit;
		JCheckBox chk_p;
		p.setPreferredSize(d_inputForm);
		t_colName = new JTextField();
		t_colName.setPreferredSize(d_inputL);
		ch_dataType = new Choice();
		ch_dataType.setPreferredSize(d_inputS);
		ch_dataType.add("varchar2");
		ch_dataType.add("number");
		ch_dataType.add("date");
		t_size = new JTextField();
		t_size.setPreferredSize(d_inputS);
		t_default = new JTextField();
		t_default.setPreferredSize(d_inputL);
		ch_limit = new Choice();
		ch_limit.setPreferredSize(d_inputS);
		ch_limit.add("없음");
		ch_limit.add("primary key");
		ch_limit.add("foreign key");
		ch_limit.add("unique");
		ch_limit.add("not null");
		ch_limit.add("check");
		chk_p = new JCheckBox();
		chk_p.setPreferredSize(new Dimension(60, 20));
		chk_p.setHorizontalAlignment(SwingConstants.CENTER);
		
		p.add(t_colName);
		p.add(ch_dataType);
		p.add(t_size);
		p.add(t_default);
		p.add(ch_limit);
		p.add(chk_p);
		arr_p.add(p);
		p.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		d_mainForm.setSize(470, mainFormHeight*p_mainForm.getComponentCount());
		p_mainForm.setPreferredSize(d_mainForm);
		p_mainForm.add(p);
		p_mainForm.updateUI();
		bar = scroll.getVerticalScrollBar();
		bar.setValue(bar.getMaximum());
	}
	
	public void deleteColumns() {
		for(int i=0;i<arr_p.size();i++) {
			JCheckBox box = (JCheckBox)arr_p.get(i).getComponent(5);
			if(box.isSelected()) {
				p_mainForm.remove(arr_p.get(i));
				arr_p.remove(i);
			}
		}
		p_mainForm.updateUI();
		bar = scroll.getVerticalScrollBar();
		bar.setValue(bar.getMaximum());
	}
	
	public String[][] getTableInformation() {
		String[][] info = new String[arr_p.size()][5];
		JTextField txt = null;
		Choice ch = null;
		for(int i = 0; i<arr_p.size();i++) {
			for(int j = 0; j<5 ; j++) {
				try{
					txt = (JTextField)arr_p.get(i).getComponent(j);
					info[i][j] = txt.getText();
				}catch(ClassCastException e) {
					try {
						ch = (Choice)arr_p.get(i).getComponent(j);
						info[i][j] = ch.getSelectedItem();
					}catch(Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		return info;
	}
	
	public void changeChildBtSetting(Component component, Dimension d, Font font) {
		if(component instanceof Container) {
			for(Component child : ((Container)component).getComponents()) {
				try {
					JButton bt = (JButton)child;
					if(d!=null) {
						child.setPreferredSize(d);
					}
					if(font!=null) {
						child.setFont(font);
					}
					bt.setContentAreaFilled(false);
					changeChildBtSetting(child, d, font);
				} catch (ClassCastException e) {
					// TODO Auto-generated catch block
					continue;
				}
			}
		}
	}
	public String inputTitle() {
		String title = JOptionPane.showInputDialog("테이블 이름을 입력하세요");
		if(title.equals("") || title==null) {
			title = inputTitle();
		}
		return title;
	}
	public void changeChildSetting(Component component, Font font) {
		if(component instanceof Container) {
			for(Component child : ((Container)component).getComponents()) {
				if(font!=null) {
					child.setFont(font);	
				} 
			}
		}
	}
}
