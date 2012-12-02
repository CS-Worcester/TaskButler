package edu.worcester.cs499summer2012.database;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class BackUpToSD {
	public static final String EXPORT_FILE_NAME = Environment
			.getExternalStorageDirectory().getPath();

	private Context context;
	private SQLiteDatabase db;
	private Exporter exporter;

	public BackUpToSD(Context context, SQLiteDatabase db) {
		this.context = context;
		this.db =  db;

		try {
			// create a file on the SD-Card to export the DB
			File myFile = new File(EXPORT_FILE_NAME);
			myFile.createNewFile();

			FileOutputStream fileOUt = new FileOutputStream(myFile);
			BufferedOutputStream outPut = new BufferedOutputStream(fileOUt);

			exporter = new Exporter(outPut);
		} catch (Exception e) {
		}// should probably deal with this
	}

	/**
	 * export the data to the SD card (if it exists)
	 * 
	 * @param data_source
	 * @param context2
	 */
	public void exportData(Context context, TasksDataSource data_source) {
		try {
			exporter.startDbExport(db.getPath()); //going to b

			// get the tables out of the given sqlite database
			String sql = "SELECT * FROM sqlite_master"; // TODO: This shouldn't
														// be a raw request

			Cursor cur = db.rawQuery(sql, new String[0]); // TODO: Dhimitri this
															// shouldn't
															// be a raw request?
			Log.d("db", "show tables, cur size " + cur.getCount());
			cur.moveToFirst();

			String tableName = "TaskButler.db";
			while (cur.getPosition() < cur.getCount()) {
				// these aren't required for export (save space)
				if (!tableName.equals("android_metadata")
						&& !tableName.equals("sqlite_sequence")) {
					exportTable(tableName);
				}

				cur.moveToNext();
			}
			exporter.endDbExport();
			exporter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void exportTable(String tableName) throws IOException {
		exporter.startTable(tableName);

		// get everything from the table
		String sql = "select * from " + tableName;
		Cursor cur = db.rawQuery(sql, new String[0]);
		int numcols = cur.getColumnCount();
		cur.moveToFirst();

		// move through the table, creating rows
		// and adding each column with name and value
		// to the row
		while (cur.getPosition() < cur.getCount()) {
			exporter.rowStart();
			String name; // name of the row
			String val; // value in the given row
			for (int i = 0; i < numcols; i++) {
				name = cur.getColumnName(i);
				val = cur.getString(i);
				exporter.addColumn(name, val);
			}

			exporter.rowEnd();
			cur.moveToNext();
		}

		cur.close();

		exporter.endTable();
	}
	
/********************************************/
	class Exporter {
		private static final String CLOSING_WITH_TICK = "'>";
		private static final String DBSTART = "<export-database name='";
		private static final String DBEND = "</export-database>";
		private static final String TABLESTART = "<table name='";
		private static final String TABLEEND = "</table>";
		private static final String ROWSTART = "<row>";
		private static final String ROWEND = "</row>";
		private static final String COLUMNSTART = "<col name='";
		private static final String COLUMNEND= "</col>";

		private BufferedOutputStream bufferOut;

		public Exporter() throws FileNotFoundException {
			this(new BufferedOutputStream(context.openFileOutput(
					EXPORT_FILE_NAME, Context.MODE_WORLD_READABLE)));
		}

		public Exporter(BufferedOutputStream buffOut) {
			bufferOut = buffOut;
		}

		public void close() {
			if (bufferOut != null) {
				try {
					bufferOut.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public void startDbExport(String dbName)  {
			String stg = DBSTART + dbName + CLOSING_WITH_TICK;
			try {
				bufferOut.write(stg.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void endDbExport() throws IOException {
			bufferOut.write(DBEND.getBytes());
		}

		public void startTable(String tableName) throws IOException {
			String stg = TABLESTART + tableName + CLOSING_WITH_TICK;
			bufferOut.write(stg.getBytes());
		}

		public void endTable()  {
			try {
				bufferOut.write(TABLEEND.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void rowStart() {
			try {
				bufferOut.write(ROWSTART.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void rowEnd() {
			try {
				bufferOut.write(ROWEND.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void addColumn(String name, String insertValue) throws IOException {
			String stg = COLUMNSTART + name + CLOSING_WITH_TICK + insertValue + COLUMNEND;
			bufferOut.write(stg.getBytes());
		}
	}

}