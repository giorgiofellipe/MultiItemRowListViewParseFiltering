package com.giorgio.ParseContactsFilter;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import com.parse.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ContactsAdapter mContactsAdapter;
    private ListView mListView;
    private EditText mEditText;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        //inicializa a API Parse
        Parse.initialize(this, "JjQGefpXj5EbqDsap5E0Et4c8pWFAAbREIPWbTWM", "jsKTiX5sFRs3S24ZRJYtiRU1L0hdyqzlJbCLkkMs");
        //Registra a classe Contacts no PARSE
        ParseObject.registerSubclass(ParseContact.class);
        ParseAnalytics.trackAppOpened(getIntent());

        mListView = (ListView) findViewById(R.id.contact_list);
        mContactsAdapter = new ContactsAdapter(this, new ArrayList<ParseContact>());

        RemoteDataTask task = new RemoteDataTask();
        task.execute();
    }

    public void updateData(String sText) {
        ParseQuery<ParseContact> query = ParseQuery.getQuery("Contacts");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        if (sText != null) {
            /** a unica forma de fazer uma query case insensitive é utilizando o whereMatches, que busca
             * em qualquer local da String
             */
            query.whereMatches("name", "("+ sText +")", "i");
//            a SOLUÇAO é como te passei por email, criar uma outra coluna com lowerCased e fazer esse where abaixo ;)
//            query.whereStartsWith("name", sText.toLowerCase());
        }
        query.orderByAscending("name");
        query.findInBackground(new FindCallback<ParseContact>() {
            @Override
            public void done(List<ParseContact> objects, ParseException e) {
                if (objects != null) {
                    mContactsAdapter.clear();
                    for (int i = 0; i < objects.size(); i++) {
                        ParseObject parseObject = objects.get(i);
                        ParseContact contact = new ParseContact();
                        contact.setName(parseObject.get("name").toString());
                        ParseFile parseFile = parseObject.getParseFile("photo");
                        if (parseFile != null && parseFile.getUrl() != null && parseFile.getUrl().length() > 0) {
                            contact.setPhotoFile((ParseFile) parseFile);
                        }
                        mContactsAdapter.add(contact);
                    }
                }
            }
        });
    }

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            updateData(null);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            mListView = (ListView) findViewById(R.id.contact_list);

            mContactsAdapter = new ContactsAdapter(MainActivity.this, new ArrayList<ParseContact>());

            final int spacing = (int) getResources().getDimension(R.dimen.spacing);
            final int itemsPerRow = getResources().getInteger(R.integer.items_per_row);
            final MultiItemRowListAdapter wrapperAdapter = new MultiItemRowListAdapter(getApplicationContext(), mContactsAdapter, itemsPerRow, spacing);

            mListView.setAdapter(wrapperAdapter);

            mListView.setVisibility(View.VISIBLE);
            mListView.setTextFilterEnabled(true);

            mEditText = (EditText) findViewById(R.id.search_contact);
            mEditText.addTextChangedListener(new DelayedTextWatcher(300) {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    mListView.setVisibility(View.INVISIBLE);
                }
                @Override
                public void afterTextChangedDelayed(Editable s) {
                    mListView.setVisibility(View.VISIBLE);
                    System.out.println("Searched for [" + s + "]");
                    updateData(s.toString());
                }
            });
        }


    }

}
