package vakulenko.pi.nure.notesapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class NoteEdit extends Activity {

    private EditText mTitleText;
    private EditText mBodyText;
    private Long mRowId;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private Date dateNote;
    private Spinner mPrioritySpinner;
    private ImageView imageView;

    private NotesDbAdapter mDbHelper;
    private String filePath;
    private static String picPath;

    private static int RESULT_LOAD_IMAGE = 1;

    private static final String TAG = NoteEdit.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.setTheme(this);

        mDbHelper = new NotesDbAdapter(this);

        mDbHelper.open();
        setContentView(R.layout.note_edit);
        setTitle(R.string.edit_note);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mDatePicker = (DatePicker) findViewById(R.id.datePicker);
        mTimePicker = (TimePicker) findViewById(R.id.timePicker);
        mPrioritySpinner = (Spinner) findViewById(R.id.spinnerPriority);
        imageView = (ImageView) findViewById(R.id.edit_note_img);

        Button confirmButton = (Button) findViewById(R.id.confirm);
        Button selectImageButton = (Button) findViewById(R.id.buttonSelectImage);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
                    : null;
        }
        populateFields();
        selectImageButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent i = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                    }
                });
        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
            imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
            picPath = filePath;
            Log.d(TAG, picPath);

        }

    }

    private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(note.getLong(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_DATE)));
            mDatePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            mTimePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
            mTimePicker.setCurrentMinute(c.get(Calendar.MINUTE));
            ArrayAdapter myAdap = (ArrayAdapter) mPrioritySpinner.getAdapter();
            String priority = note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_PRIORITY));
            mPrioritySpinner.setSelection(myAdap.getPosition(priority));

            if (note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_FILEPATH)) != null) {
                String picPath = note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_FILEPATH));
                Log.d(TAG, "populate fields" + picPath);
                imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
            } else {
                Log.d(TAG, "filepath in populate fields null");
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }


    private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();
        String priority = mPrioritySpinner.getSelectedItem().toString().toUpperCase();

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(Calendar.YEAR, mDatePicker.getYear());
        calendar.set(Calendar.MONTH, mDatePicker.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());

        Note note = new Note();
        note.setBody(body);
        note.setDateNote(new Date(calendar.getTimeInMillis()));
        note.setTitle(title);
        note.setPriority(Priority.valueOf(priority));
        note.setNotificationScheduled(false);
        note.setFilePath(filePath);
        if (mRowId == null) {
            Log.d(TAG, "creating with pic" + filePath);
            long id = mDbHelper.createNote(note);

            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateNote(mRowId, title, body, calendar.getTimeInMillis(), priority, filePath);
        }
    }

}


