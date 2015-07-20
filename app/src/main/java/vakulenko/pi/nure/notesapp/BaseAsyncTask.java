package vakulenko.pi.nure.notesapp;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

public abstract class BaseAsyncTask<T, E, V> extends AsyncTask<T, E, V> {

    private ProgressBar progressBar;

    public BaseAsyncTask(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    protected void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
       progressBar.setVisibility(View.GONE);
    }

}
