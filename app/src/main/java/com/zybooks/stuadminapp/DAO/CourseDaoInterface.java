package com.zybooks.stuadminapp.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.zybooks.stuadminapp.Entities.CourseTable;

import java.util.List;
@Dao
public interface CourseDaoInterface {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert (CourseTable course);

    //TODO may have to change to replace instead of ignore
    //TODO may need to add an insert all

    @Delete
    void delete (CourseTable course );


    @Query("DELETE FROM course_table")
    void deleteAllFromCourseTable();

    @Query("SELECT * FROM course_table ORDER BY courseId ASC") // change to order by todo
    List<CourseTable> getAllCoursesFromTable();


}
