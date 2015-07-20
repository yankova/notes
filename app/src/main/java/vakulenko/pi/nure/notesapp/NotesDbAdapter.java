package vakulenko.pi.nure.notesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

public class NotesDbAdapter {

    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_DATE = "date_note";
    public static final String KEY_PRIORITY = "priority";
    public static final String KEY_FILEPATH = "file_path";

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_CREATE =
            "create table notes " +
                    "(_id integer primary key autoincrement, "
                    + "title text not null, " +
                    "body text not null, " +
                    "date_note integer not null," +
                    "priority text not null, " +
                    "file_path text, " +
                    "created_at integer, " +
                    "notification_scheduled integer, " +

                    "CHECK (priority IN (\"LOW\",\"NORMAL\",\"HIGH\" )));";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "notes";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    public long createNote(Note note) {
        return mDb.insert(DATABASE_TABLE, null, note.toContentValues());
    }

    public boolean updateNote(Note note) {
        ContentValues args = note.toContentValues();
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + note.getId(), null) > 0;
    }

    public long createNote(String title, String body, long dateNote, String priority, String filePath) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);
        initialValues.put(KEY_DATE, dateNote);
        initialValues.put(KEY_PRIORITY, priority);
        initialValues.put(KEY_FILEPATH, filePath);
        Log.d(TAG, "create note " + filePath);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllNotes() {

        return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE,
                KEY_BODY, KEY_DATE, KEY_PRIORITY, KEY_FILEPATH}, null, null, null, null, null);
    }

    public Cursor fetchNote(long rowId) throws SQLException {
        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID,
                                KEY_TITLE, KEY_BODY, KEY_DATE, KEY_PRIORITY, KEY_FILEPATH}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updateNote(long rowId, String title, String body, long dateNote, String priority, String filePath) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);
        args.put(KEY_DATE, dateNote);
        args.put(KEY_PRIORITY, priority);
        args.put(KEY_FILEPATH, filePath);
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllNotesContainingText(String text) {
        String[] fieldsToSelect = new String[]{Note.KEY_ROWID, Note.KEY_TITLE,
                Note.KEY_BODY, Note.KEY_PRIORITY, Note.
                KEY_DATE, Note.KEY_CREATED_AT, Note.KEY_FILE_PATH};
        String criteria = Note.KEY_TITLE + " LIKE '%" + text + "%' OR " + Note.KEY_BODY + " LIKE '%" + text + "%'";
        return mDb.query(DATABASE_TABLE, fieldsToSelect, criteria, null, null, null, null);
    }

    public Cursor fetchAllNotesWithPriority(List<Priority> priorities) {
        String[] fieldsToSelect = new String[]{Note.KEY_ROWID, Note.KEY_TITLE,
                Note.KEY_BODY, Note.KEY_PRIORITY, Note.KEY_DATE, Note.KEY_CREATED_AT, Note.KEY_FILE_PATH};

        StringBuilder sb = new StringBuilder();
        for (Priority prio : priorities) {
            sb.append("\"").append(prio.name()).append("\",");
        }
        String prioList = sb.toString().substring(0, sb.toString().length() - 1);
        String criteria = Note.KEY_PRIORITY + " IN (" + prioList + ")";
        return mDb.query(DATABASE_TABLE, fieldsToSelect, criteria, null, null, null, null);
    }

}
