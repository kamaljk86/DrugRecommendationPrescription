package co.in.drugprescription.sync;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

public class DrugContentProvider extends ContentProvider{

    private static final String TAG = "DrugContentProvider";

    private CashbackSQLiteOpenHelper sqLiteOpenHelper;

    private static final String DRUG_DBNAME = "drug";

    public static final String DRUG_TABLE = "drug_prescription";

    //Column Names
    private static final String FIELD_ID = "id";
    private static final String FIELD_DRUG_ID = "drugid";
    private static final String FIELD_DRUG_NAME = "drugname";
    private static final String FIELD_MANUFACTUREDBY = "manufacturedby";
    private static final String FIELD_USEDFOR = "usedfor";
    private static final String FIELD_RATE = "rate";
    private static final String FIELD_DESCRIPTION = "description";

    private SQLiteDatabase cbDB;

    private static final String CREATE_TABLE_DRUG = "CREATE TABLE "
            + DRUG_TABLE + "(" + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+ FIELD_DRUG_ID + " TEXT,"
            + FIELD_DRUG_NAME + " TEXT,"+ FIELD_MANUFACTUREDBY + " TEXT,"+ FIELD_USEDFOR + " TEXT,"
            + FIELD_RATE + " TEXT,"+ FIELD_DESCRIPTION + " TEXT" + ")";

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(SyncConstants.CONTENT_URI, DRUG_TABLE, 1);
    }
    @Override
    public boolean onCreate() {
        sqLiteOpenHelper = new CashbackSQLiteOpenHelper( getContext(), DRUG_DBNAME, CREATE_TABLE_DRUG);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        String tableNme = "";
        switch(uriMatcher.match(uri)){
            case 1 :
                tableNme = DRUG_TABLE;
                break;
            default:
                return null;
        }

        cbDB = sqLiteOpenHelper.getWritableDatabase();

        Cursor cursor = (SQLiteCursor) cbDB.query(tableNme, projection, selection, selectionArgs,
                null, null, sortOrder);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        try {
            String tableNme = "";
            switch (uriMatcher.match(uri)) {
                case 1:
                    tableNme = DRUG_TABLE;
                    break;
                default:
                    return null;
            }

            cbDB = sqLiteOpenHelper.getWritableDatabase();
            long rowid = cbDB.insert(tableNme, null, contentValues);
            return getContentUriRow(rowid);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String where, String[] selectionArgs) {
        String tableNme = DRUG_TABLE;

        cbDB = sqLiteOpenHelper.getWritableDatabase();

        return cbDB.delete(tableNme, where, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String where, String[] selectionArgs) {
        String tableNme = DRUG_TABLE;

        cbDB = sqLiteOpenHelper.getWritableDatabase();
        return cbDB.update(tableNme,contentValues,where,selectionArgs );
    }
    private Uri getContentUriRow(long rowid){
        return Uri.fromParts(SyncConstants.CONTENT_URI, DRUG_TABLE, Long.toString(rowid));
    }
    public class CashbackSQLiteOpenHelper extends SQLiteOpenHelper {

        private String sql;

        CashbackSQLiteOpenHelper(Context context, String dbName, String msql) {
            super(context, dbName, null, 1);
            sql = msql;
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {  }
    }
}