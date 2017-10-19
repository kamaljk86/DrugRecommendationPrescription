package co.in.drugprescription.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DataBaseHandler";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "drug";
    //Table Name
    private static final String TABLE_DRUG = "drug_prescription";
    //Column Names
    private static final String FIELD_ID = "id";
    private static final String FIELD_DRUG_ID = "drugid";
    private static final String FIELD_DRUG_NAME = "drugname";
    private static final String FIELD_MANUFACTUREDBY = "manufacturedby";
    private static final String FIELD_USEDFOR = "usedfor";
    private static final String FIELD_RATE = "rate";
    private static final String FIELD_DESCRIPTION = "description";

    private static final String CREATE_TABLE_DRUG = "CREATE TABLE "
            + TABLE_DRUG + "(" + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+ FIELD_DRUG_ID + " TEXT,"
            + FIELD_DRUG_NAME + " TEXT,"+ FIELD_MANUFACTUREDBY + " TEXT,"+ FIELD_USEDFOR + " TEXT,"
            + FIELD_RATE + " TEXT,"+ FIELD_DESCRIPTION + " TEXT" + ")";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DRUG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRUG);
        onCreate(db);
    }

    // Method to get drugs suggestions using autocomplete
    public LinkedHashMap<String,String> getDrugDetailsQuickSearch(){
        LinkedHashMap<String,String> selectedDrugDetailsList = new LinkedHashMap<String,String>();
        try {

            String selectQuery = "SELECT  * FROM " + TABLE_DRUG;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    selectedDrugDetailsList.put(cursor.getString(cursor.getColumnIndex(FIELD_ID)),cursor.getString(cursor.getColumnIndex(FIELD_DRUG_NAME)));

                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
        catch (Exception e) {
            Log.e(TAG,"-- Exception "+ e.toString());
        }
        return selectedDrugDetailsList;
    }

    // Method to get drug by drugname
    public LinkedHashMap<String,String> getDrugByName(String drugName){
        LinkedHashMap<String,String> selectedDrugDetailsList = new LinkedHashMap<String,String>();
        try {

            String selectQuery = "SELECT * FROM " + TABLE_DRUG+" where "+FIELD_DRUG_NAME+"='"+drugName+"' COLLATE NOCASE ";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    selectedDrugDetailsList.put(cursor.getString(cursor.getColumnIndex(FIELD_ID)),cursor.getString(cursor.getColumnIndex(FIELD_DRUG_NAME)));

                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
        catch (Exception e) {
            Log.e(TAG,"-- Exception "+ e.toString());
        }
        return selectedDrugDetailsList;
    }

    // Method to get drug by drugid
    public ArrayList<String> getDrugByID(String drugID){
        ArrayList<String> drugList = new ArrayList<String>();
        String selectQuery="";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            selectQuery = "SELECT * FROM " +TABLE_DRUG+" where "+FIELD_ID+"='"+drugID+"'";
            Cursor cursor = db.rawQuery(selectQuery, null);
            if(cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        drugList.add(cursor.getString(cursor.getColumnIndex(FIELD_DRUG_NAME)));
                        drugList.add(cursor.getString(cursor.getColumnIndex(FIELD_MANUFACTUREDBY)));
                        drugList.add(cursor.getString(cursor.getColumnIndex(FIELD_USEDFOR)));
                        drugList.add(cursor.getString(cursor.getColumnIndex(FIELD_RATE)));
                        drugList.add(cursor.getString(cursor.getColumnIndex(FIELD_DESCRIPTION)));
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            db.close();
        }
        catch (Exception e) {
            Log.e(TAG,"-- Exception "+ e.toString());
        }
        return drugList;
    }
}
