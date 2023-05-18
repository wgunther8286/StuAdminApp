package com.zybooks.stuadminapp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.zybooks.stuadminapp.Database.DbRepo;
import com.zybooks.stuadminapp.Entities.CourseTable;
import com.zybooks.stuadminapp.Entities.InstructorTable;
import com.zybooks.stuadminapp.R;

import java.util.List;

/*
Class and Activity for Instructor
 */
public class AddEditInstructor extends AppCompatActivity
{

    EditText edTxtName, edTxtPhone, edTxtEmail ;

    List<CourseTable> mCourseList;
    CourseTable mCourse;
    InstructorTable iToEdit;

    int mCId, mIId, mACourseId ;

    DbRepo mRepository;
    List<InstructorTable> mInstructorList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_instructor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mRepository = new DbRepo(getApplication());

        mCId = getIntent().getIntExtra("courseId", -1);
        mIId = getIntent().getIntExtra("InstructorId", -1);
        mACourseId = getIntent().getIntExtra("assessCourseId", -1);


        mInstructorList = mRepository.getAllInstructorsFromRepo();
        for (InstructorTable instructor: mInstructorList)
        {
            if (instructor.getInstructorID() == mIId)
            {
                iToEdit = instructor;
            }
        }

        edTxtName = findViewById(R.id.instructor_name);
        edTxtPhone = findViewById(R.id.instructor_phone);
        edTxtEmail = findViewById(R.id.instructor_email);

        if (mIId != -1 )
        {
            edTxtName.setText(iToEdit.getInstructorName());
            edTxtPhone.setText(iToEdit.getInstructorPhone());
            edTxtEmail.setText(iToEdit.getInstructorEmail());
        }

    }




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



    public void SaveInstructorButton(View view)
    {

        String name = edTxtName.getText().toString();
        String phone = edTxtPhone.getText().toString();
        String email = edTxtEmail.getText().toString();

        mInstructorList = mRepository.getAllInstructorsFromRepo();
        int instId = 1;
        for (InstructorTable i : mInstructorList )
        {
            if (i.getInstructorID() >= instId)
            {
                instId = i.getInstructorID();
            }
        }

        if (mIId != -1)
        {
            InstructorTable updateTable = new InstructorTable(mIId, name, phone, email, mACourseId);
            mRepository.insert(updateTable);
        }else{
            InstructorTable insertInstructor = new InstructorTable(++instId, name, phone, email, mCId);
            mRepository.insert(insertInstructor);
        }


        Intent intent = new Intent(this, AddEditCourse.class);
        if (mCId != -1)
        {
            intent.putExtra("courseId", mCId);
        }
        else {
            intent.putExtra("courseId", mACourseId);
        }
        startActivity(intent);
    }
}
