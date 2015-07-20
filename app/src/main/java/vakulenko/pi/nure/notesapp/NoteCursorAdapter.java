package vakulenko.pi.nure.notesapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class NoteCursorAdapter extends CursorAdapter {

    private static final String TAG = NoteCursorAdapter.class.getSimpleName();

    private final LayoutInflater inflater;

    private final Map<Priority, Integer> priorityIcons;

    public NoteCursorAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, c, flags);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        priorityIcons = new HashMap<Priority, Integer>();
        priorityIcons.put(Priority.HIGH, R.drawable.ic_action_important);
        priorityIcons.put(Priority.NORMAL, R.drawable.ic_action_half_important);
        priorityIcons.put(Priority.LOW, R.drawable.ic_action_not_important);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.notes_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView titleTextView = (TextView) view.findViewById(R.id.text1);
        String title = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));
        String picturePath = cursor.getString((cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_FILEPATH)));
        Log.d("FILEPATH", " " + picturePath);
        titleTextView.setText(title);
        TextView dateTextView = (TextView) view.findViewById(R.id.text2);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        ImageView picture = (ImageView) view.findViewById(R.id.imgNote);
        picture.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        Note note = Note.fromCursor(cursor);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String noteDate = df.format(cursor.getLong(cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_DATE)));
        dateTextView.setText(noteDate);
        Log.d(TAG, "date " + noteDate);
        Log.d(TAG, "priority" + note.getPriority().toString());
        imageView.setImageResource(priorityIcons.get(note.getPriority()));
    }
}
