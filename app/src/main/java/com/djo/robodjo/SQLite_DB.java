package com.djo.robodjo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.djo.robodjo.TableFixHeaders.MatrixTableAdapter;
import com.djo.robodjo.TableFixHeaders.TableFixHeaders;


public class SQLite_DB extends ActionBarActivity {

    EditText sqlEditor;
    ListView sqlView;
    TextView errTextView;
    DjoDB sqLiteOpenHelper;
    SQLiteDatabase db;
    LinearLayout queryResultLayout;
    TableFixHeaders tableFixHeaders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite__db);
        sqlEditor = (EditText) findViewById(R.id.SQLEditor);
        errTextView = (TextView) findViewById(R.id.errTextView);
        sqLiteOpenHelper = new DjoDB(getApplicationContext());
        db = sqLiteOpenHelper.getWritableDatabase();
        tableFixHeaders = (TableFixHeaders) findViewById(R.id.dbGrid);
        MatrixTableAdapter<String> matrixTableAdapter = new MatrixTableAdapter<String>(this, new String[][]{{""}});
        tableFixHeaders.setAdapter(matrixTableAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sqlite__db, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSelectBtnClick(View view) {
        try {
            MatrixTableAdapter<String> matrixTableAdapter = new MatrixTableAdapter<String>(this, new String[][]{{""}});
            tableFixHeaders.setAdapter(matrixTableAdapter);
            errTextView.setText("");
            String sqlStr = sqlEditor.getText().toString();
            if (sqlStr.toUpperCase().replace(" ", "").indexOf("SELECTDISTINCT") == 0) {
                sqlStr = sqlStr.toUpperCase().replaceFirst("DISTINCT", "DISTINCT 1,");
            } else {
                sqlStr = sqlStr.toUpperCase().replaceFirst("SELECT", "SELECT 1,");
            }

            Cursor c = db.rawQuery(sqlStr, null);
            c.moveToFirst();
            int cols = c.getColumnCount();
            int rows = c.getCount();
            if (rows > 0) {
                String[][] matrix = new String[rows + 1][cols];
                for (int i = 0; i < cols; i++) {
                    matrix[0][i] = c.getColumnName(i);
                }
                int i = 1;
                do {
                    for (int j = 0; j < cols; j++) {
                        matrix[i][j] = c.getString(j);
                    }
                    i++;
                } while (c.moveToNext());
                matrixTableAdapter = new MatrixTableAdapter<String>(this, matrix);
                tableFixHeaders.setAdapter(matrixTableAdapter);
            } else {
                errTextView.setText("Table is empty");
            }
        } catch (Exception exception) {
            errTextView.setText(exception.getMessage());
            Log.e(getClass().getSimpleName(), "SELECT", exception);
        }

    }

    public void onExecSqlBtnClick(View view) {
        try {
            MatrixTableAdapter<String> matrixTableAdapter = new MatrixTableAdapter<String>(this, new String[][]{{""}});
            tableFixHeaders.setAdapter(matrixTableAdapter);
            errTextView.setText("");
            String sqlStr = sqlEditor.getText().toString();
            db.execSQL(sqlStr);
        } catch (Exception exception) {
            errTextView.setText(exception.getMessage());
            Log.e(getClass().getSimpleName(), "EXEC SQL", exception);
        }

    }
}
