package com.zybooks.stuadminapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zybooks.stuadminapp.Adapters.TermAdapter;
import com.zybooks.stuadminapp.Database.DbRepo;
import com.zybooks.stuadminapp.Entities.CourseTable;
import com.zybooks.stuadminapp.Entities.TermTable;
import com.zybooks.stuadminapp.R;

import java.util.List;
/*
  Activity for the Terms List layout and class
 */
public class ActivityTermsList extends AppCompatActivity
{

    private RecyclerView mRecView;
    private TermAdapter mAdapt;
    private RecyclerView.LayoutManager mLayMang;
    DbRepo dpRepo;

    public List<TermTable> termTableList;

    //onCreate for Terms List class and layout
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        dpRepo = new DbRepo(getApplication());


        getTermsList();
        buildRecyclerView();

        // Deleting with swipe
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
            {
                return false;
            }
        // Course Deletion Validation
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
            {

                for (CourseTable c: dpRepo.getAllCoursesFromRep())
                {
                    if (c.getCourseTermId() == mAdapt.getTermAt(viewHolder.getAdapterPosition()).getTermId())
                        if (mAdapt.getTermAt(viewHolder.getAdapterPosition()).getTermId() == c.getCourseTermId())
                        {
                            mAdapt.notifyDataSetChanged();
                            Toast.makeText(ActivityTermsList.this, "Remove all Courses in Term before deleting", Toast.LENGTH_LONG).show();
                            return;
                        }
                }

                dpRepo.delete(mAdapt.getTermAt(viewHolder.getAdapterPosition()));
                mAdapt.mTermsList.remove(viewHolder.getAdapterPosition());
                mAdapt.notifyItemRemoved(viewHolder.getAdapterPosition());

                Toast.makeText(ActivityTermsList.this, "Term Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(mRecView);

    }


    public void getTermsList()
    {
        termTableList = dpRepo.getAllTermsFromRepo();

    }


    public void buildRecyclerView() {
        mRecView = findViewById(R.id.recyler_view_terms);
        mRecView.setHasFixedSize(true);
        mLayMang = new LinearLayoutManager(this);
        mAdapt = new TermAdapter(this);
        mRecView.setLayoutManager(mLayMang);
        mRecView.setAdapter(mAdapt);

        mAdapt.termsSetter(dpRepo.getAllTermsFromRepo());

    }


    public void addTermOnClick(View view) {
        Intent intent = new Intent(this, AddTerm.class);
        startActivity(intent);
    }


}
