package com.zybooks.stuadminapp.Activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zybooks.stuadminapp.Adapters.AssessmentAdapter;
import com.zybooks.stuadminapp.Adapters.InstructorAdapter;
import com.zybooks.stuadminapp.Database.DbRepo;
import com.zybooks.stuadminapp.Entities.AssessmentTable;
import com.zybooks.stuadminapp.Entities.CourseTable;
import com.zybooks.stuadminapp.Entities.InstructorTable;
import com.zybooks.stuadminapp.Entities.MyReceiver;
import com.zybooks.stuadminapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEditCourse extends AppCompatActivity
{
    EditText mNameTxt, mSDate, mEDate, mStatus, mNotes;

    Calendar mCalStart = Calendar.getInstance();
    Calendar mCalEnd = Calendar.getInstance();
    SimpleDateFormat dateFormatter;
    SimpleDateFormat sdf;

    DatePickerDialog.OnDateSetListener mSDatePicker;
    DatePickerDialog.OnDateSetListener mEDatePicker;

    DbRepo mRepo;
    List<CourseTable> mDBcoursesList;
    CourseTable mSelectedCourse;
    List<AssessmentTable> aTableList;
    List<InstructorTable> iTableList;

    RecyclerView mRecyclerViewB;
    RecyclerView mRecyclerViewA;
    AssessmentAdapter mAAdapter;
    InstructorAdapter mIAdapter;
    RecyclerView.LayoutManager mLayoutManger;
    RecyclerView.LayoutManager mLayoutMangerB;

    int mTermId;
    int mCId;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        mSDate = findViewById(R.id.add_course_start_date);
        /*
        Button instrBtn = findViewById(R.id.add_instructor_btn);
        instrBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(AddEditCourse.this, AddEditInstructor.class);
                startActivity(intent);
            }


        });

         */



        mTermId = getIntent().getIntExtra("selectedTermId", -1);
        mCId = getIntent().getIntExtra("courseId", -1);

        mRepo = new DbRepo(getApplication());

        getAndSetViewsById();
        getAllCourses();
        getAssessmentsInCourse();
        getInstructorsInCourse();
        setRecyViews();
        deleteInstructorHelper();
        deleteAssessmentHelper();
        setDatePicker();



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.edit_course_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item)
    {

        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.share_notes:
                Intent notesIntent = new Intent();
                notesIntent.setAction(Intent.ACTION_SEND);
                notesIntent.putExtra(Intent.EXTRA_TEXT, mNotes.getText().toString());
                notesIntent.putExtra(Intent.EXTRA_TITLE, "Sharing Note");
                notesIntent.setType("text/plain");
                Intent noteIntentChooser = Intent.createChooser(notesIntent, null);
                startActivity(noteIntentChooser);
                return true;

            case R.id.set_course_start_alert:
                String cSDate = mSDate.getText().toString();
                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                Date start = null;

                try{
                    start = sdf.parse(cSDate);
                    //start = dateFormatter.parse(courseStartDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long sTrigger = start.getTime();
                Intent sIntent = new Intent(AddEditCourse.this, MyReceiver.class);
                sIntent.putExtra("key", mSelectedCourse.getCourseName() + " Course Starts Today!");
                Toast.makeText(AddEditCourse.this, "Course Start Date Notification Set", Toast.LENGTH_SHORT).show();
                PendingIntent sSend = PendingIntent.getBroadcast(AddEditCourse.this, ++MainActivity.alertNum, sIntent, PendingIntent.FLAG_IMMUTABLE);
                AlarmManager sAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                sAlarmManager.set(AlarmManager.RTC_WAKEUP,sTrigger,sSend);
                return true;

            case R.id.set_course_end_alert:
                String cEndDate = mEDate.getText().toString();
                String myEndFormat = "MM/dd/yy";
                SimpleDateFormat sdfEnd = new SimpleDateFormat(myEndFormat, Locale.US);
                Date end = null;

                try {
                    end = sdfEnd.parse(cEndDate);
                    //end = dateFormatter.parse(courseEndDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Long eTrigger = end.getTime();
                Intent eIntent = new Intent(AddEditCourse.this, MyReceiver.class);
                eIntent.putExtra("key", mSelectedCourse.getCourseName() + " Course ends today!");
                Toast.makeText(AddEditCourse.this, "Course End notification set", Toast.LENGTH_SHORT);
                PendingIntent eSend = PendingIntent.getBroadcast(AddEditCourse.this, MainActivity.alertNum++, eIntent, PendingIntent.FLAG_IMMUTABLE);
                AlarmManager endAlarmManager = (AlarmManager)  getSystemService(Context.ALARM_SERVICE);
                endAlarmManager.set(AlarmManager.RTC_WAKEUP,eTrigger,eSend);
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    public void addInstructorButton(View view)
    {

        Intent intent = new Intent(this, AddEditInstructor.class);
        intent.putExtra("courseId", mCId);
        startActivity(intent);

    }

    public void addAssessmentButton(View view)
    {

        Intent intent = new Intent(this, AddEditAssessment.class);
        intent.putExtra("courseId", mCId);
        startActivity(intent);
    }

    public void getAssessmentsInCourse()
    {
        aTableList = new ArrayList<>();
        List<AssessmentTable> list = mRepo.getAllAssessmentsFromRepo();
        for (AssessmentTable l : list){
            if (l.getAssessmentCourseId() == mCId){
                aTableList.add(l);
            }
            /*
            if (aTableList.size() >= 5)
            {
                Toast.makeText(AddEditCourse.this, "Can Not Add more than 5 assessments per course.", Toast.LENGTH_SHORT);
            }

             */
        }


    }

    public void getInstructorsInCourse()
    {
        iTableList = new ArrayList<>();
        List<InstructorTable> list = mRepo.getAllInstructorsFromRepo();
        for (InstructorTable instructor : list){
            if (instructor.getInstructorCourseId() == mCId){
                iTableList.add(instructor);
            }
        }

    }

    public void getSelectedCourse()
    {

        for (CourseTable course : mDBcoursesList)
        {
            if (mCId == course.getCourseId())
            {
                mSelectedCourse = course;
            }
        }

        mNameTxt.setText(mSelectedCourse.getCourseName());
        mSDate.setText(mSelectedCourse.getCourseStart());
        mEDate.setText(mSelectedCourse.getCourseEnd());
        mStatus.setText(mSelectedCourse.getCourseStatus());
        mNotes.setText(mSelectedCourse.getCourseNotes());
        mTermId = mSelectedCourse.getCourseTermId();
    }

    public void saveOnClick(View view)
    {
        String name = mNameTxt.getText().toString();
        String start = mSDate.getText().toString();
        String end = mEDate.getText().toString();
        String status = mStatus.getText().toString();
        String note = mNotes.getText().toString();
        if (note.trim().isEmpty())
        {
            note = " ";
        }
        if (name.trim().isEmpty() || start.trim().isEmpty() || end.trim().isEmpty() || status.trim().isEmpty())
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
            return;
        }
        if (mCId != -1)
        {
            CourseTable updateCourse = new CourseTable(mCId, name, start, end, status, note, mTermId);
            mRepo.insert(updateCourse);

        } else {

            int newId = mDBcoursesList.size();
            for (CourseTable course : mDBcoursesList)
            {
                if (course.getCourseId() >= newId)
                {
                    newId = course.getCourseId();
                }
            }
            CourseTable addCourse = new CourseTable(newId + 1, name, start, end, status, note, mTermId);
            mRepo.insert(addCourse);
        }

        Intent intent = new Intent(this, ActivityTermsList.class);
        startActivity(intent);

    }

    public void getAllCourses() {
        mDBcoursesList = mRepo.getAllCoursesFromRep();
    }


    public void setDatePicker()
    {
        mSDatePicker = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                mCalStart.set(Calendar.YEAR, year);
                mCalStart.set(Calendar.MONTH, month);
                mCalStart.set(Calendar.DAY_OF_MONTH, day);
                //String myFormat = "MM/dd/yyyy";
                //SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                updateLabelStart();
            }
        };
        mSDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Date date;
                String info=mSDate.getText().toString();
                /*
                if(info.equals("")) info = "12/27/2022";
                try{
                    mCalStart.setTime(sdf.parse(info));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                 */
                new DatePickerDialog(AddEditCourse.this, mSDatePicker, mCalStart.get(Calendar.YEAR), mCalStart.get(Calendar.MONTH)
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
                //String myFormat = "MM/dd/yyyy";
                //SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                updateLabelEnd();
            }
        };
        mEDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new DatePickerDialog(AddEditCourse.this, mEDatePicker, mCalEnd.get(Calendar.YEAR), mCalEnd.get(Calendar.MONTH)
                        , mCalEnd.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void getAndSetViewsById()
    {
        mNameTxt = findViewById(R.id.add_course_name);
        mSDate = findViewById(R.id.add_course_start_date);
        mEDate = findViewById(R.id.add_course_end_date);
        mStatus = findViewById(R.id.add_course_status);
        mNotes = findViewById(R.id.add_course_notes);
    }


    private void updateLabelStart()
    {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        mSDate.setText(sdf.format(mCalStart.getTime()));
    }

    private void updateLabelEnd()
    {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        mEDate.setText(sdf.format(mCalEnd.getTime()));
    }

    public void deleteInstructorHelper()
    {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mRepo.delete(mIAdapter.getInstructorAt(viewHolder.getAdapterPosition()));
                mIAdapter.mInstructorList.remove(viewHolder.getAdapterPosition());
                mIAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                Toast.makeText(AddEditCourse.this, "Instructor Deleted", Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(mRecyclerViewA);
    }

    public void deleteAssessmentHelper()
    {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
            {
                mRepo.delete(mAAdapter.getAssessmentAt(viewHolder.getAdapterPosition()));
                mAAdapter.mAssessmentList.remove(viewHolder.getAdapterPosition());
                mAAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                Toast.makeText(AddEditCourse.this, "Assessment Deleted", Toast.LENGTH_LONG).show();

            }
        }).attachToRecyclerView(mRecyclerViewB);
    }

    public void setRecyViews()
    {
        if (mCId != -1)
        {
            getSelectedCourse();
            setTitle("Edit Course");


            mRecyclerViewB = findViewById(R.id.assessment_recyclerView);
            mRecyclerViewA = findViewById(R.id.instructor_recyclerView);

            mLayoutManger = new LinearLayoutManager(this);
            mLayoutMangerB = new LinearLayoutManager(this);

            mAAdapter = new AssessmentAdapter(this);
            mIAdapter = new InstructorAdapter(this);

            mRecyclerViewB.setLayoutManager(mLayoutManger);
            mRecyclerViewA.setLayoutManager(mLayoutMangerB);

            mRecyclerViewB.setAdapter(mAAdapter);
            mRecyclerViewA.setAdapter(mIAdapter);

            mAAdapter.assessmentSetter(aTableList);
            mIAdapter.instructorSetter(iTableList);
        }else{
            findViewById(R.id.add_instructor_btn).setVisibility(View.INVISIBLE);
            findViewById(R.id.add_assessment_btn).setVisibility(View.GONE);
            findViewById(R.id.instructorText).setVisibility(View.INVISIBLE);
            findViewById(R.id.assessmentText).setVisibility(View.INVISIBLE);
        }
    }


    public void termsBtn(View view) {


    }
}
