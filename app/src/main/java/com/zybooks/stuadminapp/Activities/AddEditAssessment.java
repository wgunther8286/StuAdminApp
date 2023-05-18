package com.zybooks.stuadminapp.Activities;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zybooks.stuadminapp.Database.DbRepo;
import com.zybooks.stuadminapp.Entities.AssessmentTable;
import com.zybooks.stuadminapp.Entities.CourseTable;
import com.zybooks.stuadminapp.Entities.MyReceiver;
import com.zybooks.stuadminapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
Activity for Assessment class and the Assessment layout
 */

public class AddEditAssessment extends AppCompatActivity
{

    EditText editTxtName, edTxtType, edTxtStart, edTxtEnd;
    Spinner typeSpinner;
    String assessmentType;

    DbRepo mRepo;
    List<AssessmentTable> mAssessmentList;
    List<CourseTable> mCourseList;
    CourseTable mCourse;
    List<AssessmentTable> mAssessByCourseId;

    AssessmentTable assessmentToEdit;

    Calendar mCalStart = Calendar.getInstance();
    Calendar mCalEnd = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener mSDatePicker;
    DatePickerDialog.OnDateSetListener mEDatePicker;

    SimpleDateFormat dateFormatter;
    SimpleDateFormat sdf;

    int mCId, mAId, mACourseId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_assessment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mRepo = new DbRepo(getApplication());

        String myDateFormat = "MM/dd/yyyy";
        dateFormatter = new SimpleDateFormat(myDateFormat, Locale.US);

        mCId = getIntent().getIntExtra("courseId", -1);
        mAId = getIntent().getIntExtra("assessmentId", -1);
        mACourseId = getIntent().getIntExtra("assessCourseId", -1);

        mAssessmentList = mRepo.getAllAssessmentsFromRepo();
        for (AssessmentTable i : mAssessmentList) {
            if (i.getAssessmentId() == mAId) {
                assessmentToEdit = i;
            }
        }
        getAInCourse();

        editTxtName = findViewById(R.id.assessment_name);
        assessmentType=getIntent().getStringExtra("assessmentType");
        //edTxtType = findViewById(R.id.assessment_type);
        edTxtStart = findViewById(R.id.assessment_date);
        edTxtEnd = findViewById(R.id.assessment_end);

        if (mAId != -1) {
            editTxtName.setText(assessmentToEdit.getAssessmentTitle());
            edTxtType.setText(assessmentToEdit.getAssessmentType());
            edTxtStart.setText(assessmentToEdit.getAssessmentStartDate());
            edTxtEnd.setText(assessmentToEdit.getAssessmentEndDate());
        }

        mSDatePicker = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                mCalStart.set(Calendar.YEAR, year);
                mCalStart.set(Calendar.MONTH, month);
                mCalStart.set(Calendar.DAY_OF_MONTH, day);
                UpdateLabelStart();
            }
        };
        mEDatePicker = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                mCalEnd.set(Calendar.YEAR, year);
                mCalEnd.set(Calendar.MONTH, month);
                mCalEnd.set(Calendar.DAY_OF_MONTH, day);
                UpdateLabelEnd();
            }
        };

        edTxtStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new DatePickerDialog(AddEditAssessment.this, mSDatePicker, mCalStart.get(Calendar.YEAR)
                        , mCalStart.get(Calendar.MONTH), mCalStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        edTxtEnd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new DatePickerDialog(AddEditAssessment.this, mEDatePicker, mCalEnd.get(Calendar.YEAR),
                        mCalEnd.get(Calendar.MONTH), mCalEnd.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        int statusPosition = 0;
        String[] assessmentTypeArray = {"Objective", "Performance"};
        if(assessmentType == "Objective"){
            statusPosition = 1;
        }else if(assessmentType == "Performance"){
            statusPosition = 2;
        }

        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,assessmentTypeArray);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setSelection(statusPosition);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.assessment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.assessment_date_notification:
                String editTxtSDate = edTxtStart.getText().toString();
                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                Date sDate = null;

                try {
                    sDate = sdf.parse(editTxtSDate);
                    //sDate = dateFormatter.parse(editTxtSDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Long timeTrigger = sDate.getTime();
                Intent intent = new Intent(AddEditAssessment.this, MyReceiver.class);
                intent.putExtra("key", assessmentToEdit.getAssessmentTitle() + " Assessment starts today!");
                Toast.makeText(AddEditAssessment.this, "Assessment Start Notification Set", Toast.LENGTH_SHORT).show();

                PendingIntent sendStart = PendingIntent.getBroadcast(AddEditAssessment.this, MainActivity.alertNum++, intent, FLAG_IMMUTABLE);
                AlarmManager alarmMan = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmMan.set(AlarmManager.RTC_WAKEUP, timeTrigger, sendStart);
                return true;

            case R.id.assessment_end_date_notification:
                String edTxtEDate = edTxtEnd.getText().toString();
                String myEndFormat = "MM/dd/yy";
                SimpleDateFormat sdfEnd = new SimpleDateFormat(myEndFormat, Locale.US);
                Date eDate = null;

                try {
                    eDate = sdfEnd.parse(edTxtEDate);
                    //eDate = dateFormatter.parse(edTxtEDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Long eTrigger = eDate.getTime();
                Intent eIntent = new Intent(AddEditAssessment.this, MyReceiver.class);
                eIntent.putExtra("key", assessmentToEdit.getAssessmentTitle() + " Assessment ends today!");
                Toast.makeText(AddEditAssessment.this, "Assessment Date End notification set", Toast.LENGTH_SHORT).show();

                PendingIntent sendE = PendingIntent.getBroadcast(AddEditAssessment.this, MainActivity.alertNum++, eIntent, FLAG_IMMUTABLE);
                AlarmManager eAlarmMan = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                eAlarmMan.set(AlarmManager.RTC_WAKEUP, eTrigger, sendE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void SaveAssessmentBtn(View view)
    {

        String title, date, endDate;

        title = editTxtName.getText().toString();
        //type = edTxtType.getText().toString();
        String type = typeSpinner.getSelectedItem().toString();
        date = edTxtStart.getText().toString();
        endDate = edTxtEnd.getText().toString();

        mAssessmentList = mRepo.getAllAssessmentsFromRepo();


        int assessmentId = 1;
        for (AssessmentTable assessment : mAssessmentList) {
            if (assessment.getAssessmentId() >= assessmentId) {
                assessmentId = assessment.getAssessmentId();
            }
        }


        if (mAId != -1) {
            AssessmentTable updateTable = new AssessmentTable(mAId, title, type, date, endDate, mACourseId);
            mRepo.insert(updateTable);
        } else {
            AssessmentTable insertTable = new AssessmentTable(++assessmentId, title, type, date, endDate, mCId);
            mRepo.insert(insertTable);
        }


        Intent intent = new Intent(this, AddEditCourse.class);
        if (mCId != -1) {
            intent.putExtra("courseId", mCId);
        } else {
            intent.putExtra("courseId", mACourseId);
        }
        //getAInCourse();

        startActivity(intent);
    }


    public void UpdateLabelStart()
    {
        String format = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        edTxtStart.setText(sdf.format(mCalStart.getTime()));
    }

    public void UpdateLabelEnd()
    {
        String format = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        edTxtEnd.setText(sdf.format(mCalEnd.getTime()));
    }

    public void getAInCourse()
    {
        mAssessmentList = new ArrayList<>();
        List<AssessmentTable> list = mRepo.getAllAssessmentsFromRepo();
        for (AssessmentTable l : list) {
            if (l.getAssessmentCourseId() == mACourseId) {
                mAssessmentList.add(l);
            }
            if (mAssessmentList.size() > 5) {
                Toast.makeText(AddEditAssessment.this, "Can Not Add more than 5 assessments per course.", Toast.LENGTH_SHORT);
            }
        }


    }
}
