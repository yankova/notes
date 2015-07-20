package vakulenko.pi.nure.notesapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class NotesMain extends ActionBarActivity {

    private ListView listView;
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int FAKE_DELAY_IN_SECONDS = 1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    private SearchView searchView;
    private NotesDbAdapter mDbHelper;

    private MenuItem highPrio;
    private MenuItem normalPrio;
    private MenuItem lowPrio;
    private ProgressBar progressBar;



    private String TAG = NotesMain.class.getSimpleName();


    private class PriorityFilteringTask extends BaseAsyncTask<Void, Void, Cursor> {

        public PriorityFilteringTask(ProgressBar progressBar) {
            super(progressBar);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar();
        }

        @Override
        protected Cursor doInBackground(Void... ts) {

            try {
                TimeUnit.SECONDS.sleep(FAKE_DELAY_IN_SECONDS);
            } catch (InterruptedException e) {
                Log.e(TAG, "Background task failed", e);
            }

            List<Priority> priorities = getCurrentPriorities();
            Log.d(TAG, "priorities - " + priorities);

            Cursor result = null;

            if (priorities.size() > 0) {
                result = mDbHelper.fetchAllNotesWithPriority(priorities);
            }

            return result;
        }

        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);

            if (result == null) {
                listView.removeAllViews();
            } else {
                fillData(result);
            }

            hideProgressBar();
        }

    }

    private class NotesLoadingTask extends BaseAsyncTask<Void, Void, Cursor> {

        public NotesLoadingTask(ProgressBar progressBar) {
            super(progressBar);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar();
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            try {
                TimeUnit.SECONDS.sleep(FAKE_DELAY_IN_SECONDS);
            } catch (InterruptedException e) {
                Log.e(TAG, "Background task failed", e);
            }

            Cursor cursor = mDbHelper.fetchAllNotes();

            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            fillData(cursor);
            hideProgressBar();
        }
    }

    private class NotesSearchTask extends BaseAsyncTask<String, Void, Cursor> {

        public NotesSearchTask(ProgressBar progressBar) {
            super(progressBar);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar();
        }

        @Override
        protected Cursor doInBackground(String... texts) {

            try {
                TimeUnit.SECONDS.sleep(FAKE_DELAY_IN_SECONDS);
            } catch (InterruptedException e) {
                Log.e(TAG, "Background task failed", e);
            }

            String textToSearch = texts[0];
            Cursor notesContainingText = mDbHelper.fetchAllNotesContainingText(textToSearch);

            int count = notesContainingText.getCount();
            Log.d(TAG, "notes count - " + count);

            return notesContainingText;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            fillData(cursor);
            hideProgressBar();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.setTheme(this);

        setContentView(R.layout.activity_notes_main);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        initActivityComponents();
        fillData();
        registerForContextMenu(listView);
    }

    private void fillData() {
        Log.d(TAG, "fill data");
        new NotesLoadingTask(progressBar).execute();
    }

    private void fillData(Cursor cursor) {

        startManagingCursor(cursor);

        CursorAdapter notes = new NoteCursorAdapter(this, R.layout.notes_row, cursor,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(notes);
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createNote() {
        Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }



    private void initActivityComponents() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(NotesMain.this, NoteEdit.class);
                intent.putExtra(NotesDbAdapter.KEY_ROWID, id);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notes_main, menu);

        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        highPrio = menu.findItem(R.id.menu_priority_high);
        normalPrio = menu.findItem(R.id.menu_priority_normal);
        lowPrio = menu.findItem(R.id.menu_priority_low);



        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "onQueryTextSubmit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "onQueryTextChange - " + s);

                if (!s.isEmpty()) {
                    new NotesSearchTask(progressBar).execute(s);

                    // Cursor notesContainingText = mDbHelper.fetchAllNotesContainingText(s);
                    //int count = notesContainingText.getCount();
                    //Log.d(TAG, "notes count - " + count);
                    //fillData(notesContainingText);
                }

                return false;
            }
        });

        return result;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == INSERT_ID) {
            createNote();
            return true;
        }
        if (id == R.id.action_settings) {
            openSettings(item);
            return true;
          }
        return super.onOptionsItemSelected(item);
    }

    public void openSettings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void priorityChecked(MenuItem menuItem) {
        Log.d(TAG, "priority checked");
        menuItem.setChecked(!menuItem.isChecked());
        new PriorityFilteringTask(progressBar).execute();
//        fillDataDependingOnSelectedPriorities();
    }

    private void fillDataDependingOnSelectedPriorities() {
        List<Priority> priorities = getCurrentPriorities();
        Log.d(TAG, "priorities - " + priorities);

        // TODO fix prio filtering when using search
        if (priorities.size() > 0) {
            fillData(mDbHelper.fetchAllNotesWithPriority(priorities));
        } else {
            // TODO fix this case
            //listView.removeAllViews();
        }
    }

    private List<Priority> getCurrentPriorities() {
        List<Priority> currentPriorities = new ArrayList<Priority>();

        Log.d(TAG, "before");
        if (highPrio.isChecked()) {
            Log.d(TAG, "after");
            currentPriorities.add(Priority.HIGH);
        }

        if (normalPrio.isChecked()) {
            currentPriorities.add(Priority.NORMAL);
        }

        if (lowPrio.isChecked()) {
            currentPriorities.add(Priority.LOW);
        }

        return currentPriorities;
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mDbHelper.close();
    }

}
