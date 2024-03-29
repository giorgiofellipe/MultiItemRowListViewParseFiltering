package com.giorgio.ParseContactsFilter;

import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by giorgiofellipe on 12/08/14.
 */
public abstract class DelayedTextWatcher implements TextWatcher {

    private long delayTime;
    private WaitTask lastWaitTask;

    public DelayedTextWatcher(long delayTime) {
        super();
        this.delayTime = delayTime;
    }

    @Override
    public void afterTextChanged(Editable s) {
        synchronized (this) {
            if (lastWaitTask != null){
                lastWaitTask.cancel(true);
            }
            lastWaitTask = new WaitTask();
            lastWaitTask.execute(s);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    public abstract void afterTextChangedDelayed(Editable s);

    private class WaitTask extends AsyncTask<Editable, Void, Editable> {
        @Override
        protected Editable doInBackground(Editable... params) {
            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException e) {
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(Editable result) {
            super.onPostExecute(result);
            afterTextChangedDelayed(result);
        }
    }

}