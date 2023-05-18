package com.zybooks.stuadminapp.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zybooks.stuadminapp.Adapters.CourseAdapter;
import com.zybooks.stuadminapp.Database.DbRepo;
import com.zybooks.stuadminapp.Entities.CourseTable;
import com.zybooks.stuadminapp.Entities.TermTable;
import com.zybooks.stuadminapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditTerm extends AppCompatActivity
{
    private RecyclerView mRecycView;
    private RecyclerView.LayoutManager mLayMan;
    private CourseAdapter mCAdapter;
    DbRepo mRepo;

    EditText mEdName, mEdSDate, mEDate;

    Calendar mCalStart = Calendar.getInstance();
    Calendar mCalEnd = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener mSDatePicker;
    DatePickerDialog.OnDateSetListener mEDatePicker;

    int mTId, getExTermID;
    String mName, mStartD, mEndD;

    TermTable mSelTerm;
    List<CourseTable> mACourses;
    CourseTable mCourses;

    List<CourseTable> courseInTermList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRepo = new DbRepo(getApplication());

        getAndSetViewsById();
        setRecyclerViewAndAdapter();
        getTerm();
        getAllCourses();


        mCAdapter.courseSetter(courseInTermList);

        setDatePicker();


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }


            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mRepo.delete(mCAdapter.getCourseAt(viewHolder.getAdapterPosition()));
                mCAdapter.mCourseList.remove(viewHolder.getAdapterPosition());
                mCAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                Toast.makeText(EditTerm.this, "Course Deleted", Toast.LENGTH_SHORT).show();


            }
        }).attachToRecyclerView(mRecycView);

    }



    public void setRecyclerViewAndAdapter() {
        mRecycView = findViewById(R.id.edit_term_recyler);
        mLayMan = new LinearLayoutManager(this);
        mCAdapter = new CourseAdapter(this);
        mRecycView.setLayoutManager(mLayMan);
        mRecycView.setAdapter(mCAdapter);


    }

    public void getTerm() {
        mTId = getIntent().getIntExtra("termId", -1);
        for (TermTable t : mRepo.getAllTermsFromRepo()) {
            if (t.getTermId() == mTId) {
                mSelTerm = t;
                getExTermID = t.getTermId();
            }
        }
        if (mSelTerm != null) {
            mName = mSelTerm.getTermTitle();
            mStartD = mSelTerm.getStartOfTerm();
            mEndD = mSelTerm.getEndOfTerm();
        }
        mEdName.setText(mName);
        mEdSDate.setText(mStartD);
        mEDate.setText(mEndD);
    }

    public void getAllCourses() {
        mACourses = mRepo.getAllCoursesFromRep();
        courseInTermList = new ArrayList<>();
        for (CourseTable c : mACourses) {
            if (c.getCourseTermId() == getExTermID) {
                courseInTermList.add(c);
            }
        }
    }

    public void addCourseOnClick(View view) {
        Intent intent = new Intent(this, AddEditCourse.class);
        intent.putExtra("selectedTermId", mTId);
        startActivity(intent);

    }

    public void saveTermOnClickEdit(View view) {
        String name = mEdName.getText().toString();
        String start = mEdSDate.getText().toString();
        String end = mEDate.getText().toString();

        TermTable updatedTerm = new TermTable(mTId, name, start, end);
        mRepo.insert(updatedTerm);

        Intent intent = new Intent(this, ActivityTermsList.class);
        startActivity(intent);

    }

    private void updateLabelStartDate() {
        String format = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        mEdSDate.setText(sdf.format(mCalStart.getTime()));
    }

    private void updateLabelEndDate() {
        String format = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        mEDate.setText(sdf.format(mCalEnd.getTime()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getAndSetViewsById() {
        mEdName = findViewById(R.id.edit_text_title);
        mEdSDate = findViewById(R.id.edit_text_startDate);
        mEDate = findViewById(R.id.edit_text_endDate);
    }


    public void setDatePicker() {
        mSDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mCalStart.set(Calendar.YEAR, year);
                mCalStart.set(Calendar.MONTH, month);
                mCalStart.set(Calendar.DAY_OF_MONTH, day);
                String myFormat = "MM/dd/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                updateLabelStartDate();
            }
        };
        mEdSDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(EditTerm.this, mSDatePicker, mCalStart.get(Calendar.YEAR), mCalStart.get(Calendar.MONTH)
                        , mCalStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        mEDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mCalEnd.set(Calendar.YEAR, year);
                mCalEnd.set(Calendar.MONTH, month);
                mCalEnd.set(Calendar.DAY_OF_MONTH, day);
                String myFormat = "MM/dd/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                updateLabelEndDate();
            }
        };
        mEDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(EditTerm.this, mEDatePicker, mCalEnd.get(Calendar.YEAR), mCalEnd.get(Calendar.MONTH)
                        , mCalEnd.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

}
