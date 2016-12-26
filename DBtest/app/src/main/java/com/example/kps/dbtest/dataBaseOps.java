package com.example.kps.dbtest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

public class dataBaseOps extends SQLiteOpenHelper {
    public static final String dataBaseName="GatorsTallyDB.db";
    public static final String TABLE_NAME="gatorsTally";
    public static final String ID="id";
    public static final String TASK="task";
    public static final String DUEDATE="date";
    public static final String DUETIME="time";
    public static final String COMPLETE ="complete";
    public dataBaseOps(Context context){
        super(context,dataBaseName,null,1);
    }
   // String[] createquery=""
    public  void onCreate(SQLiteDatabase db){
        db.execSQL(
                "CREATE TABLE gatorsTally "+
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT,task TEXT,date TEXT,time TEXT,complete TEXT )"
        );
    }
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }
    public long insertTask(String task, String dueDate,
                          String dueTime, String completed){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();

        contentValues.put(TASK,task);
        contentValues.put(DUEDATE,dueDate);
        contentValues.put(DUETIME,dueTime);
        contentValues.put(COMPLETE,completed);
        long test=db.insert("gatorsTally",null,contentValues);
        return test;
    }
    public int markComplete(String Sno){

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COMPLETE,"COMPLETED");
        //int update=db.update("gatorsTally",contentValues, ID+"= ?" , new String[]{ String.valueOf(Sno) });
        int update=db.update("gatorsTally",contentValues, ID +" = "+Sno ,null);
        return update;
    }
    public ArrayList<String> getNotCompletedTasks(){
        ArrayList<String> notCompletedTasks=new ArrayList<String>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from gatorsTally where completed is 'False' ",null);
        res.moveToFirst();
        while(!res.isAfterLast()){
            String temp="ID:"+res.getString(res.getColumnIndex(ID))+"\n";
            temp += "TASK:"+res.getString(res.getColumnIndex(TASK))+"\n";
            temp += "DATE:"+res.getString(res.getColumnIndex(DUEDATE))+"\n";
            temp += "TIME:"+res.getString(res.getColumnIndex(DUETIME))+"\n";
            temp += "STATUS:"+res.getString(res.getColumnIndex(COMPLETE));
            notCompletedTasks.add(temp);
            res.moveToNext();
        }
        return notCompletedTasks;
    }
    public ArrayList<String> getAllTasks(){
        ArrayList<String> allTasks=new ArrayList<String>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from gatorsTally ",null);
        res.moveToFirst();
        while(!res.isAfterLast()){
            String temp="ID:"+res.getString(res.getColumnIndex(ID))+"\n";
            temp += "TASK:"+res.getString(res.getColumnIndex(TASK))+"\n";
            temp += "DATE:"+res.getString(res.getColumnIndex(DUEDATE))+"\n";
            temp += "TIME:"+res.getString(res.getColumnIndex(DUETIME))+"\n";
            temp += "STATUS:"+res.getString(res.getColumnIndex(COMPLETE));
            allTasks.add(temp);
            res.moveToNext();
        }
        return allTasks;
    }
    public int deleteTask( String Sno){
        SQLiteDatabase db=getWritableDatabase();
        int delete=db.delete("gatorsTally",ID+" = "+ Sno,null);
        return delete;
    }
    public int getRowsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

}
