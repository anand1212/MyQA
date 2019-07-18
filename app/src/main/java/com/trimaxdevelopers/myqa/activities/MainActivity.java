package com.trimaxdevelopers.myqa.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.trimaxdevelopers.myqa.R;
import com.trimaxdevelopers.myqa.adapters.QAAdapter;
import com.trimaxdevelopers.myqa.customviews.RecyclerViewNonScrollable;
import com.trimaxdevelopers.myqa.database.DatabaseHelper;
import com.trimaxdevelopers.myqa.models.QARow;
import com.trimaxdevelopers.myqa.utils.CustomTimer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private SharedPreferences preferences;

    // dialog to show progress
    private AlertDialog progressDialog;

    private ArrayList<QARow> allData = new ArrayList<>();

    private TextView textTimer, textCountQuestions;
    private RecyclerViewNonScrollable rvQa;
    private LinearLayoutManager layoutManager;
    private boolean isIdle = true;

    private CustomTimer customTimer;

    private final int TIME_LIMIT = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textTimer = findViewById(R.id.textTimer);
        textCountQuestions = findViewById(R.id.textCountQuestions);
        rvQa = findViewById(R.id.rvQa);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvQa.setLayoutManager(layoutManager);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(rvQa);

        rvQa.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isIdle = newState == RecyclerView.SCROLL_STATE_IDLE;
                if (isIdle) {
                    int currentQuestionPos = layoutManager.findFirstCompletelyVisibleItemPosition() + 1;
                    textCountQuestions.setText(String.format(Locale.ENGLISH,
                            "%d / %d", currentQuestionPos, allData.size()));
                }
            }
        });

        progressDialog = new AlertDialog.Builder(this)
                .setTitle("Loading")
                .setMessage("")
                .setCancelable(false)
                .create();

        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        customTimer = new CustomTimer(TIME_LIMIT * 1000) {
            @Override
            public void onTimerStart() {
                textTimer.post(new Runnable() {
                    @Override
                    public void run() {
                        textTimer.setText("0");
                    }
                });
            }

            @Override
            public void onTimerStop() {
                textTimer.post(new Runnable() {
                    @Override
                    public void run() {
                        textTimer.setText("0");
                    }
                });
            }

            @Override
            public void onTimerTikMillis(final int tik) {
                textTimer.post(new Runnable() {
                    @Override
                    public void run() {
                        textTimer.setText(String.valueOf(TIME_LIMIT - (tik / 1000)).concat(" Seconds Left"));
                    }
                });
            }

            @Override
            public void onTimeComplete() {
                textTimer.post(new Runnable() {
                    @Override
                    public void run() {
                        textTimer.setText("0");
                        moveToNext();
                    }
                });
            }

            @Override
            public void onTimeCancel() {
                textTimer.post(new Runnable() {
                    @Override
                    public void run() {
                        textTimer.setText("0");
                    }
                });
            }
        };

        // skip the question
        findViewById(R.id.buttonSkip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToNext();
            }
        });

        // execute sync to generate data from database
        if (preferences.getBoolean("isDatabaseCreated", false)) {
            new GenerateData().execute();
        } else {
            new CreateDatabase().execute();
        }
    }

    // show progress dialog with message
    public void showLoader(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage(text);
                progressDialog.show();
            }
        });
    }

    // hide progress dialog
    public void hideLoader() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class CreateDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            showLoader("Making Questions...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //copying database from assets to
            try {
                File databaseDir = new File(getDatabasePath(DatabaseHelper.DATABASE_NAME).getParent());

                if (!databaseDir.exists()) {
                    boolean isDirCreated = databaseDir.mkdirs();
                    Log.i(TAG, "New Dir Created : " + isDirCreated);
                }

                File databaseFile = new File(databaseDir.getAbsolutePath()
                        .concat(File.separator)
                        .concat(DatabaseHelper.DATABASE_NAME));

                if (!databaseFile.exists()) {
                    boolean isFileCreated = databaseFile.createNewFile();
                    Log.i(TAG, "New File Created : " + isFileCreated);
                }

                // copy database from assets to database folder of app if database is not created
                InputStream inputStream = getAssets().open(DatabaseHelper.DATABASE_NAME);

                OutputStream outputStream = new FileOutputStream(databaseFile);

                int read;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();

                Log.i(TAG, "File Copied to Database Path");
                preferences.edit().putBoolean("isDatabaseCreated", true).apply();

            } catch (Exception e) {
                e.printStackTrace();
                hideLoader();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new GenerateData().execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GenerateData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
            Cursor cursor = databaseHelper.selectAllFromTable("quiz");
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    QARow qaRow = new QARow();

                    qaRow.set_id(cursor.getString(cursor.getColumnIndex("q_id")));
                    qaRow.setQuestion(cursor.getString(cursor.getColumnIndex("question")));

                    // set original answer
                    qaRow.setAnswer(cursor.getString(cursor.getColumnIndex("opt1")));

                    String otp1 = cursor.getString(cursor.getColumnIndex("opt1"));
                    String otp2 = cursor.getString(cursor.getColumnIndex("opt2"));
                    String otp3 = cursor.getString(cursor.getColumnIndex("opt3"));
                    String otp4 = cursor.getString(cursor.getColumnIndex("opt4"));

                    // setup options with random positions
                    ArrayList<String> options = new ArrayList<>();

                    if (otp1 != null && !otp1.isEmpty()) options.add(otp1);
                    if (otp2 != null && !otp2.isEmpty()) options.add(otp2);
                    if (otp3 != null && !otp3.isEmpty()) options.add(otp3);
                    if (otp4 != null && !otp4.isEmpty()) options.add(otp4);

                    Collections.shuffle(options);
                    qaRow.setOptions(options);

                    Log.i(TAG, "GET DATA : " + qaRow.get_id());

                    // add to data to main list
                    allData.add(qaRow);
                    cursor.moveToNext();
                }
                cursor.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideLoader();
            textCountQuestions.setText("1 / ".concat(String.valueOf(allData.size())));
            setupQuestionList();
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Welcome to QA")
                    .setMessage("Do you want to START the Quiz?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            customTimer.startTimer();
                        }
                    })
                    .setCancelable(false)
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    private void setupQuestionList() {
        rvQa.setAdapter(new QAAdapter(this, allData) {
            @Override
            public void onOptionSelected(int position, QARow qARow) {
                allData.set(position, qARow);

                //move to next question
                moveToNext();
            }
        });
    }

    private void moveToNext() {
        //check is not scrolling
        if (isIdle) {
            // get current position of view and move to next by +1
            int nextPos = layoutManager.findFirstCompletelyVisibleItemPosition() + 1;
            if (nextPos < allData.size()) {
                customTimer.cancelTimer();
                customTimer.startTimer();
                rvQa.smoothScrollToPosition(nextPos);
            } else {
                // reached end of question list
                customTimer.cancelTimer();
                Intent intent = new Intent(this , ScoreActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putParcelableArrayListExtra("allData" , allData);
                startActivityForResult(intent , 5);
            }
        }
    }

}
