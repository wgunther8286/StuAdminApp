package com.zybooks.stuadminapp.Database;

import android.app.Application;

import com.zybooks.stuadminapp.DAO.AssessmentDaoInterface;
import com.zybooks.stuadminapp.DAO.CourseDaoInterface;
import com.zybooks.stuadminapp.DAO.InstructorDaoInterface;
import com.zybooks.stuadminapp.DAO.TermsDaoInterface;
import com.zybooks.stuadminapp.Entities.AssessmentTable;
import com.zybooks.stuadminapp.Entities.CourseTable;
import com.zybooks.stuadminapp.Entities.InstructorTable;
import com.zybooks.stuadminapp.Entities.TermTable;

import java.util.List;

public class DbRepo {

    private final TermsDaoInterface mtermDao;
    private final CourseDaoInterface mcourseDao;
    private final AssessmentDaoInterface mAssessmentDao;
    private final InstructorDaoInterface mInstructorDao;

    private List<TermTable> mallTerms;
    private List<CourseTable> mallCourses;
    private List<AssessmentTable> mallAssessments;
    private List<InstructorTable> mallInstructors;


    public DbRepo(Application application){
        SchedulerDatabase db = SchedulerDatabase.getDatabaseInstance(application);
        mtermDao = db.termDao();
        mcourseDao = db.courseDao();
        mAssessmentDao = db.assessmentDao();
        mInstructorDao = db.instructorDao();

        try {
            Thread.sleep(500);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    // TERMS
    // Terms getter
    public List<TermTable> getAllTermsFromRepo() {
        SchedulerDatabase.dbWriteExecutor.execute(()->{
            mallTerms = mtermDao.getAllTermsFromTable();
        });
        try {
            Thread.sleep(500);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return mallTerms;
    }

    // Terms insert
    public void insert(TermTable termTable){
        SchedulerDatabase.dbWriteExecutor.execute(()->{
            mtermDao.insert(termTable);
        });
        try {
            Thread.sleep(500);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void delete (TermTable termTable){
        SchedulerDatabase.dbWriteExecutor.execute(()->{
            mtermDao.delete(termTable);
        });
    }

    public void deleteAllTerms () {
        SchedulerDatabase.dbWriteExecutor.execute(()->{
            mtermDao.deleteAllFromTermsTable();
        });
    }

    // COURSES
    public List<CourseTable> getAllCoursesFromRep() {
        SchedulerDatabase.dbWriteExecutor.execute(() -> {
            mallCourses = mcourseDao.getAllCoursesFromTable();
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mallCourses;
    }

    public void insert(CourseTable course) {
        SchedulerDatabase.dbWriteExecutor.execute(() -> {
            mcourseDao.insert(course);
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void delete(CourseTable course) {
        SchedulerDatabase.dbWriteExecutor.execute(() -> {
            mcourseDao.delete(course);
        });
    }

    public void deleteAllCourses() {
        SchedulerDatabase.dbWriteExecutor.execute(() -> {
            mcourseDao.deleteAllFromCourseTable();
        });
    }


    public List<AssessmentTable> getAllAssessmentsFromRepo() {
        SchedulerDatabase.dbWriteExecutor.execute(() -> {
            mallAssessments = mAssessmentDao.getAssessmentsFromTable();
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mallAssessments;
    }

    public void insert(AssessmentTable assessment) {
        SchedulerDatabase.dbWriteExecutor.execute(() -> {
            mAssessmentDao.insert(assessment);
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void delete(AssessmentTable assessment) {
        SchedulerDatabase.dbWriteExecutor.execute(() -> {
            mAssessmentDao.delete(assessment);
        });
    }

    public void deleteAllAssessments() {
        SchedulerDatabase.dbWriteExecutor.execute(() -> {
            mcourseDao.deleteAllFromCourseTable();
        });
    }



    public List<InstructorTable> getAllInstructorsFromRepo(){
        SchedulerDatabase.dbWriteExecutor.execute(()->{
            mallInstructors = mInstructorDao.getInstructorsFromTable();
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mallInstructors;
    }


    public void insert(InstructorTable instructor) {
        SchedulerDatabase.dbWriteExecutor.execute(() -> {
            mInstructorDao.insert(instructor);
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void delete(InstructorTable instructor) {
        SchedulerDatabase.dbWriteExecutor.execute(() -> {
            mInstructorDao.delete(instructor);
        });
    }

    public void deleteAllInstructors() {
        SchedulerDatabase.dbWriteExecutor.execute(() -> {
            mInstructorDao.deleteAllFromInstructorTable();
        });
    }

}
