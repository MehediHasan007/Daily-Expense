package com.example.dailyexpense;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "expense.db";
    public static final String TABLE_NAME = "expense";
    public static final String COL_ID = "ID";
    public static final String COL_EXPENSE_TYPE = "ExpenseType";
    public static final String COL_EXPENSE_AMOUNT = "ExpenseAmount";
    public static final String COL_EXPENSE_DATE = "ExpenseDate";
    public static final String COL_EXPENSE_TIME = "ExpenseTime";
    public static final String COL_EXPENSE_IMAGE = "ExpenseImage";
    public static final int DATABASE_VERSION = 6;
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;

    String create_table = "create table "+TABLE_NAME+" (ID integer primary key,ExpenseType text,ExpenseAmount integer,ExpenseDate integer,ExpenseTime text,ExpenseImage text)";

    private Context context;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(create_table); 
        }catch (Exception e){
            Toast.makeText(context, "Exception "+e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);

    }

    public long insertData(String expenseType, int expenseAmount, long expenseDate,String expenseTime,String expenseImage){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EXPENSE_TYPE,expenseType);
        contentValues.put(COL_EXPENSE_AMOUNT,expenseAmount);
        contentValues.put(COL_EXPENSE_DATE,expenseDate);
        contentValues.put(COL_EXPENSE_TIME,expenseTime);
        contentValues.put(COL_EXPENSE_IMAGE,expenseImage);
        long id = getWritableDatabase().insert(TABLE_NAME,null,contentValues);
        return id;
    }
    public Cursor showData(){
        String show_all = "select * from "+TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(show_all,null);
        return cursor;
    }
    //UserRequiredData-----------------------------------------------------------------------------------------------------------------

    public Cursor showData(String expTypeInput){
        String show_all = "Select * from "+TABLE_NAME+" Where ExpenseType = '"+expTypeInput+"'";
        return getReadableDatabase().rawQuery(show_all,null);
    }

    public Cursor showData(long fromDate, long toDate){
        String show_all = "Select * from "+TABLE_NAME+" where ExpenseDate between '"+fromDate+"' AND '"+toDate+"' ";
        return getReadableDatabase().rawQuery(show_all,null);
    }

    public Cursor showData(String expTypeInput,long fromDate, long toDate){
        String show_all = "Select * from "+TABLE_NAME+" where ExpenseType = '"+expTypeInput+"' AND ExpenseDate between '"+fromDate+"' AND '"+toDate+"' ";
        return getReadableDatabase().rawQuery(show_all,null);
    }

    public  Cursor getAmount(String expTypeInput,long fromDate, long toDate) {
        String sql1;
        if (expTypeInput.equals("")) {
            sql1= "Select SUM(ExpenseAmount) AS sumAmount from " + TABLE_NAME + " where  ExpenseDate between '" + fromDate + "' AND '" + toDate + "' ";
        } else {
            sql1 = "Select SUM(ExpenseAmount) AS sumAmount from " + TABLE_NAME + " where ExpenseType = '" + expTypeInput + "' AND ExpDate between '" + fromDate + "' AND '" + toDate + "' ";
        }


        return getReadableDatabase().rawQuery(sql1, null);
    }
//---------------------------------------------------------------------------------------------------------------------------------------------



    public void deleteData(int id){
        getWritableDatabase().delete(TABLE_NAME," ID =?",new String[]{String.valueOf(id)});
    }
    public void updateData(int id,String expenseType, int expenseAmount, long expenseDate,String expenseTime,String expenseImage){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EXPENSE_TYPE,expenseType);
        contentValues.put(COL_EXPENSE_AMOUNT,expenseAmount);
        contentValues.put(COL_EXPENSE_DATE,expenseDate);
        contentValues.put(COL_EXPENSE_TIME,expenseTime);
        contentValues.put(COL_EXPENSE_IMAGE,expenseImage);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_NAME,contentValues," ID =?",new String[]{String.valueOf(id)});
        db.close();

    }
}
