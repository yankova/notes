package vakulenko.pi.nure.notesapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class NotesContentProvider extends ContentProvider {

    private static final String TAG = NotesContentProvider.class.getSimpleName();

    public static final String NOTES_TABLE = "notes";

    public static final String AUTHORITY = "vakulenko.pi.nure.notesapp.NotesContentProvider";
    public static final String NOTES_PATH = "notes";

    public static final Uri NOTES_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + NOTES_PATH);

    public static final String NOTES_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + NOTES_PATH;

    public static final String NOTES_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + NOTES_PATH;

    public static final int URI_NOTES = 1;
    public static final int URI_NOTES_ID = 2;
    public static final int URI_NOTES_TODAY = 3;

    private static final UriMatcher uriMatcher;

    static {
        Log.d(TAG, "static init");
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, NOTES_PATH, URI_NOTES);
        uriMatcher.addURI(AUTHORITY, NOTES_PATH + "/#", URI_NOTES_ID);
        uriMatcher.addURI(AUTHORITY, NOTES_PATH + "/today", URI_NOTES_TODAY);
    }

    // TODO refactor names
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "notes";
    private static final int DATABASE_VERSION = 2;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
//            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
//                    + newVersion + ", which will destroy all old data");
//            db.execSQL(TABLE_DROP);
//            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate");
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query " + uri.toString());

        switch (uriMatcher.match(uri)) {
            case URI_NOTES:                //

                Log.d(TAG, "URI_NOTES");
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = Note.KEY_TITLE + " ASC";
                }
                break;
            case URI_NOTES_ID:
                String id = uri.getLastPathSegment();
                Log.d(TAG, "URI_NOTES_ID - " + id);

                if (TextUtils.isEmpty(selection)) {
                    selection = Note.KEY_ROWID + " = " + id;
                } else {
                    selection = selection + " AND " + Note.KEY_ROWID + " = " + id;
                }
                break;

            case URI_NOTES_TODAY:
                Log.d(TAG, "URI_NOTES_TODAY");
                selection = " date(date_note/1000, 'unixepoch') = '" + DateUtils.getTodayDate() + "' ";
                Log.d(TAG, "selection - " + selection);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }


        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(NOTES_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), NOTES_CONTENT_URI);


        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        Log.d(TAG, "getType - " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_NOTES:
            case URI_NOTES_TODAY:
                return NOTES_CONTENT_TYPE;
            case URI_NOTES_ID:
                return NOTES_CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.d(TAG, "insert");
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        Log.d(TAG, "delete");
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        Log.d(TAG, "update");
        return 0;
    }

}