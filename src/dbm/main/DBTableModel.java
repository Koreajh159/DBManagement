package dbm.main;

import javax.swing.table.AbstractTableModel;

public class DBTableModel extends AbstractTableModel{
	int table_depth = 0;
	String[] columnName;
	Object[][] data;
	boolean isEditable = false;
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return data.length;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnName.length;
	}
	@Override
	public Object getValueAt(int row, int col) {
		// TODO Auto-generated method stub
		return data[row][col];
	}
	@Override
	public void setValueAt(Object value, int row, int col) {
		// TODO Auto-generated method stub
		data[row][col]= value;

        fireTableCellUpdated(row, col); 
	}
	@Override
	public String getColumnName(int col) {
		// TODO Auto-generated method stub
		return columnName[col];
	}
	public void setColumnName(String[] columnName) {
		this.columnName = columnName;
		fireTableStructureChanged();
	}
	public void setData(Object[][] data) {
		this.data = data;
		fireTableStructureChanged();
	}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return isEditable;
	}
	public void addRow() {
		Object[][] temp = data;
		data = new Object[data.length+1][columnName.length];
		for(int i = 0; i <temp.length;i++) {
			data[i] = temp[i];
		}
	}
}
