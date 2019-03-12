package dbm.csv;

import dbm.main.MainFrame;

public class InputDB {
	InputThread thread;
	public InputDB(MainFrame main, String table_name, String[] colName, int[] colTypes) {
		thread = new InputThread(main, table_name, colName, colTypes);
		thread.start();
	}
}
