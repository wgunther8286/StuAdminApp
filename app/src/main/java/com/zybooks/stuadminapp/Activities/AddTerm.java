package com.zybooks.stuadminapp.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.zybooks.stuadminapp.Database.DbRepo;
import com.zybooks.stuadminapp.Entities.TermTable;
import com.zybooks.stuadminapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
/*
Class and Activity for Adding Terms
 */
public class AddTerm extends AppCompatActivity
{
    List<TermTable> termTableList;
    DbRepo repo;

    DatePickerDialog.OnDateSetListener mSDatePicker;
    DatePickerDialog.OnDateSetListener mEDatePicker;

    EditText tName, sDate, eDate;

    Calendar mCalStart = Calendar.getInstance();
    Calendar mCalEnd = Calendar.getInstance();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tName = findViewById(R.id.edit_text_title);
        sDate = findViewById(R.id.edit_text_startDate);
        eDate = findViewById(R.id.edit_text_endDate);
        repo = new DbRepo(getApplication());

        mSDatePicker = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                mCalStart.set(Calendar.YEAR, year);
                mCalStart.set(Calendar.MONTH, month);
                mCalStart.set(Calendar.DAY_OF_MONTH, day);

                updateLabelStart();
            }
        };
        sDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new DatePickerDialog(AddTerm.this, mSDatePicker, mCalStart.get(Calendar.YEAR), mCalStart.get(Calendar.MONTH)
                        , mCalStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        mEDatePicker = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                mCalEnd.set(Calendar.YEAR, year);
                mCalEnd.set(Calendar.MONTH, month);
                mCalEnd.set(Calendar.DAY_OF_MONTH, day);
                String myFormat = "MM/dd/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                updateLabelEnd();
            }
        };
        eDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddTerm.this, mEDatePicker, mCalEnd.get(Calendar.YEAR), mCalEnd.get(Calendar.MONTH)
                        , mCalEnd.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    public void saveOnClick(View view)
    {
        String name = tName.getText().toString();
        String start = sDate.getText().toString();
        String end = eDate.getText().toString();
        TermTable term;

        if (name.trim().isEmpty() || start.trim().isEmpty() || end.trim().isEmpty())
        {

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Field(s) left blank");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.dismiss();
                        }
                    });
            alertDialog.show();

        } else {

            List<TermTable> allTerms = repo.getAllTermsFromRepo();
            int termsSize = allTerms.size();
            if (!allTerms.isEmpty()) {
                int lastId = allTerms.get(termsSize - 1).getTermId();

                term = new TermTable(lastId + 1, name, start, end);

            } else {
                term = new TermTable(1, name, start, end);
            }

            repo.insert(term);

            Intent intent = new Intent(this, ActivityTermsList.class);
            startActivity(intent);

        }
    }

//
    private void updateLabelStart()
    {
        String format = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        sDate.setText(sdf.format(mCalStart.getTime()));

    }
//
    private void updateLabelEnd()
    {
        String format = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        eDate.setText(sdf.format(mCalEnd.getTime()));

    }

//
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
