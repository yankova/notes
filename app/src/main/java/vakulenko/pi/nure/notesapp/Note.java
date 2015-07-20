package vakulenko.pi.nure.notesapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Note {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_PRIORITY = "priority";
    public static final String KEY_DATE = "date_note";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_FILE_PATH = "file_path";
    public static final String KEY_NOTIFICATION_SCHEDULED = "notification_scheduled";


    private long id;
    private String title;
    private String body;
    private Priority priority;
    private Date dateNote;
    private Date createdAt;
    private String filePath;
    private boolean notificationScheduled;


    public Note() {
        createdAt = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Date getDateNote() {
        return dateNote;
    }

    public void setDateNote(Date dateNote) {
        this.dateNote = dateNote;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isNotificationScheduled() {
        return notificationScheduled;
    }

    public void setNotificationScheduled(boolean value) {
        notificationScheduled = value;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, title);
        values.put(KEY_BODY, body);
        values.put(KEY_PRIORITY, priority.toString().toUpperCase());
        values.put(KEY_DATE, dateNote.getTime());
        values.put(KEY_CREATED_AT, createdAt.getTime());
        values.put(KEY_FILE_PATH, filePath);
        values.put(KEY_NOTIFICATION_SCHEDULED, notificationScheduled);
        return values;
    }


    @Override
    public String toString() {
        return "NoteItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", priority=" + priority +
                ", dateNote=" + dateNote +
                // ", createdAt=" + createdAt +
                ", filePath='" + filePath + '\'' +
                '}';
    }

    public static Note fromCursor(Cursor cursor) {
        Note note = new Note();

        int idIndex = cursor.getColumnIndexOrThrow(KEY_ROWID);
        int titleIndex = cursor.getColumnIndexOrThrow(KEY_TITLE);
        int descriptionIndex = cursor.getColumnIndexOrThrow(KEY_BODY);
        int priorityIndex = cursor.getColumnIndexOrThrow(KEY_PRIORITY);
        int dueDateIndex = cursor.getColumnIndexOrThrow(KEY_DATE);
        //int createdAtIndex = cursor.getColumnIndexOrThrow(KEY_CREATED_AT);
        int filePathIndex = cursor.getColumnIndexOrThrow(KEY_FILE_PATH);

        note.id = cursor.getLong(idIndex);
        note.title = cursor.getString(titleIndex);
        note.body = cursor.getString(descriptionIndex);
        note.priority = Priority.valueOf(cursor.getString(priorityIndex));
        note.dateNote = new Date(cursor.getLong(dueDateIndex));
        //note.createdAt = new Date(cursor.getLong(createdAtIndex));
        note.filePath = cursor.getString(filePathIndex);
        Log.d("Note", note.toString());

        return note;
    }

    public static Note fromExtras(Bundle extras) {
        Note note = new Note();

        note.id = extras.getLong(KEY_ROWID, 0);
        note.title = extras.getString(KEY_TITLE);
        note.body = extras.getString(KEY_BODY);
        note.priority = Priority.valueOf(extras.getString(KEY_PRIORITY));
        note.dateNote = new Date(extras.getLong(KEY_DATE));
        note.createdAt = new Date(extras.getLong(KEY_CREATED_AT));
        note.filePath = extras.getString(KEY_FILE_PATH);

        return note;
    }
}
