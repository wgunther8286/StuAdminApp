package com.zybooks.stuadminapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zybooks.stuadminapp.Database.DbRepo;
import com.zybooks.stuadminapp.R;

public class MainActivity extends AppCompatActivity {

    public static int alertNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DbRepo databaseRepository = new DbRepo(getApplication());
        databaseRepository.getAllTermsFromRepo();

        Button button = findViewById(R.id.termBtn1);
        //Button button2 = findViewById(R.id.assessBtn);
        //Button button3 = findViewById(R.id.courseBtn);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActivityTermsList.class);
                startActivity(intent);
            }


        });
        /*
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditAssessment.class);
                startActivity(intent);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditCourse.class);
                startActivity(intent);
            }
        });

         */




    }
}



