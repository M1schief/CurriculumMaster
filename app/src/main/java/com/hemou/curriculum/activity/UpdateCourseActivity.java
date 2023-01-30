package com.hemou.curriculum.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hemou.curriculum.R;
import com.hemou.curriculum.adapter.LessonAdapter;
import com.hemou.curriculum.dao.CourseDao;
import com.hemou.curriculum.pojo.Course;
import com.hemou.curriculum.view.SelectTableView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class UpdateCourseActivity extends AppCompatActivity {
    private CourseDao courseDao = new CourseDao(this);
    private ListView mListView;
    private Course course;
    private SharedPreferences sp;
    private SelectTableView select;
    private long date;
    private List<Course> allCourseList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_course);
        mListView = findViewById(R.id.lvLesson);

        course = (Course) getIntent().getSerializableExtra("course");
        showLessonInfo();
        showCourseInfo();
    }

    /**
     * 显示课程信息
     */
    private void showCourseInfo() {
        ((TextView) findViewById(R.id.tvCourseName)).setText(course.getCourseName());
        ((EditText) findViewById(R.id.etCourseName)).setText(course.getCourseName());
    }

    /**
     * 显示课次信息
     */
    private void showLessonInfo() {
        List<Course> courseList = course.toDetail();
        if (courseList == null) return;
        LessonAdapter adapter = new LessonAdapter(this, courseList, new DeleteListener(courseList));
        mListView.setAdapter(adapter);
        setListViewHeight(mListView);
    }

    private class DeleteListener implements View.OnClickListener {

        private List<Course> courseList;
        public DeleteListener(List<Course> courseList){
            this.courseList = courseList;
        }

        @Override
        public void onClick(final View v) {
            new AlertDialog.Builder(UpdateCourseActivity.this)
                    .setTitle("删除课次")
                    .setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteLesson(v, courseList);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();

        }
    }
    private Course getTargetCourse(String target) {
        allCourseList = courseDao.listAll();
        int listSize = allCourseList.size();
        if (listSize > 0) {
            for (int i = 0; i < listSize; i++){
                if(allCourseList.get(i).getCourseName().equals(target)){
                    return allCourseList.get(i);
                }
            }
        }
        return new Course();
    }

    private void coverCourse(Course course, int day, int section){

        List<Course> courseList = course.toDetail();
        Iterator<Course> iterator = courseList.iterator();
        while (iterator.hasNext()) {
            Course c = iterator.next();
            if (c.getDay() == day &&
                    c.getSection() == section) {
                iterator.remove();
                break;
            }
        }
        Course toCourse = Course.toCourse(courseList, course.getId());
        if (null != toCourse) {
            int update = courseDao.update(toCourse);
            if (update > 0) {
                String time = toCourse.getCourseTime();
                course.setCourseTime(time);
                return;
            }
        }
    }

    private void addLesson(int day, int section, String courseName){
        Course course = new Course();
        course.setDay(day);
        course.setSection(section);
        String courseTime = getTargetCourse(courseName).getCourseTime();
        int id = UpdateCourseActivity.this.course.getId();
        if (TextUtils.isEmpty(courseTime)) {
            course.setCourseTime(course.toTime());
        } else {
            course.setCourseTime(courseTime + ";" + course.toTime());
        }
        course.setId(id);
        //修改
        int update = courseDao.update(course);
        if (update > 0) {
            UpdateCourseActivity.this.course.setCourseTime(course.getCourseTime());
        }
    }

    private void deleteLesson(View v, List<Course> courseList){
        Course course = (Course) v.getTag();
        Iterator<Course> iterator = courseList.iterator();
        while (iterator.hasNext()) {
            Course c = iterator.next();
            if (c.getDay() == course.getDay() &&
                    c.getSection() == course.getSection()) {
                iterator.remove();
                break;
            }
        }
        Course toCourse = Course.toCourse(courseList, UpdateCourseActivity.this.course.getId());
        if (null != toCourse) {
            int update = courseDao.update(toCourse);
            if (update > 0) {
                String time = toCourse.getCourseTime();
                UpdateCourseActivity.this.course.setCourseTime(time);
                Toast.makeText(UpdateCourseActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                showLessonInfo();
                return;
            }
        }
        Toast.makeText(UpdateCourseActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
    }

    /**
     * 设置ListView高度
     *
     * @param listView
     */
    public void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        //总高度
        int totalHeight = 0;
        //测量并累加高度
        int count = listAdapter.getCount();
        if (count > 0) {
            View listItem = listAdapter.getView(0, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight() * count;
        }
        //设置高度
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;
        listView.setLayoutParams(params);
    }

    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        this.finish();
    }

    private List<Course> acquireData() {
        List<Course> courses = new ArrayList<>();
        sp = getSharedPreferences("config", MODE_PRIVATE);
        date = sp.getLong("date", new Date().getTime());
        if (sp.getBoolean("isFirstUse", true)) {//首次使用
            sp.edit().putBoolean("isFirstUse", false).apply();
        }else {
            courses = courseDao.listAll();
        }
        return courses;
    }

    /**
     * 添加课次
     *
     * @param view
     */
    public void addLesson(View view) {
        final View inflate = getLayoutInflater().inflate(R.layout.add_lesson_item, null);
        select  = inflate.findViewById(R.id.selectTable);
        select.loadData(acquireData(),new Date(date),UpdateCourseActivity.this.course.getCourseName());
        new AlertDialog.Builder(this)
                .setTitle("添加课次")
                .setView(inflate)
                .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[][] res = select.response;
                        for(int i = 0; i < 9; i++){
                            for(int j = 0; j < 5; j++){
                                //Log.e("0",res[i][j] +"i:"+ i+"j:" + j);
                                if(res[i][j] != "@") {
                                    if(res[i][j] == "$"){
                                        addLesson(j + 1, i + 1, UpdateCourseActivity.this.course.getCourseName());
                                    }else if(res[i][j] == "del") {
                                        //Log.e("0","detect");
                                        coverCourse(getTargetCourse(UpdateCourseActivity.this.course.getCourseName()),j + 1 ,i + 1);
                                    }else{
                                        //Log.e("0",res[i][j] +"i:"+ i+"j:" + j);
                                        coverCourse(getTargetCourse(res[i][j]),j + 1 ,i + 1);
                                        addLesson(j + 1, i + 1,UpdateCourseActivity.this.course.getCourseName());
                                    }
                                }
                            }
                        }

//                        //封装信息
//                        Course course = new Course();
//                        //星期
//                        String day = ((EditText) inflate.findViewById(R.id.tvDay)).getText().toString();
//                        if (!TextUtils.isEmpty(day)) {
//                            course.setDay(Integer.parseInt(day));
//                        } else {
//                            Toast.makeText(UpdateCourseActivity.this, "星期不可为空！", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        //节次
//                        String section = ((EditText) inflate.findViewById(R.id.tvSection)).getText().toString();
//                        if (!TextUtils.isEmpty(section)) {
//                            course.setSection(Integer.parseInt(section));
//                        } else {
//                            Toast.makeText(UpdateCourseActivity.this, "节次不可为空！", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        //单双周
//                        /*int rbId = ((RadioGroup) inflate.findViewById(R.id.rgWeekType)).getCheckedRadioButtonId();
//                        if (R.id.rbSingleWeek == rbId) {
//                            course.setWeekType("s");
//                        } else if (R.id.rbNormalWeek == rbId) {
//                            course.setWeekType("n");
//                        } else if (R.id.rbDoubleWeek == rbId) {
//                            course.setWeekType("d");
//                        } else {
//                            Toast.makeText(UpdateCourseActivity.this, "周类型尚未选择！", Toast.LENGTH_SHORT).show();
//                            return;
//                        }*/
//                        //教室
//                        // String classroom = ((EditText) inflate.findViewById(R.id.tvClassroom)).getText().toString();
//                        // if (!TextUtils.isEmpty(classroom)) {
//                        //     course.setClassroom(classroom);
//                        // } else {
//                        //     Toast.makeText(UpdateCourseActivity.this, "教室尚未填写！", Toast.LENGTH_SHORT).show();
//                        //     return;
//                        // }
//                        //组装上课时间
//                        String courseTime = UpdateCourseActivity.this.course.getCourseTime();
//                        int id = UpdateCourseActivity.this.course.getId();
//                        if (TextUtils.isEmpty(courseTime)) {
//                            course.setCourseTime(course.toTime());
//                        } else {
//                            course.setCourseTime(courseTime + ";" + course.toTime());
//                        }
//                        course.setId(id);
//                        //修改
//                        int update = courseDao.update(course);
//                        if (update > 0) {
//                            UpdateCourseActivity.this.course.setCourseTime(course.getCourseTime());
//                            Toast.makeText(UpdateCourseActivity.this, "添加成功！", Toast.LENGTH_SHORT).show();
//                            showLessonInfo();
//                        } else {
//                            Toast.makeText(UpdateCourseActivity.this, "添加失败！", Toast.LENGTH_SHORT).show();
//                        }
                   }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    /**
     * @param view
     */
    public void delCourse(View view) {
        new AlertDialog.Builder(this)
                .setTitle("删除课程")
                .setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int delete = courseDao.delete(course.getId());
                        if (delete > 0) {
                            Toast.makeText(UpdateCourseActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                            UpdateCourseActivity.this.finish();
                        } else {
                            Toast.makeText(UpdateCourseActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    /**
     * 保存修改
     *
     * @param view
     */
    public void save(View view) {
        //封装信息
        Course course = new Course();
        //id
        course.setId(this.course.getId());
        //课程名
        String courseName = ((EditText) findViewById(R.id.etCourseName)).getText().toString();
        if (!TextUtils.isEmpty(courseName)) {
            course.setCourseName(courseName);
        }
        //老师名
        // String teacherName = ((EditText) findViewById(R.id.etTeacherName)).getText().toString();
        // if (!TextUtils.isEmpty(teacherName)) {
        //     course.setTeacherName(teacherName);
        // }
        // course.setStartWeek(Integer.parseInt(((EditText) findViewById(R.id.etStartWeek)).getText().toString()));
        // course.setEndWeek(Integer.parseInt(((EditText) findViewById(R.id.etEndWeek)).getText().toString()));
        if (this.course.equals(course)) {
            Toast.makeText(this, "您尚未修改课程信息\n无需保存！", Toast.LENGTH_SHORT).show();
        } else {
            int update = courseDao.update(course);
            if (update > 0) {
                Toast.makeText(this, "修改成功！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "修改失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
