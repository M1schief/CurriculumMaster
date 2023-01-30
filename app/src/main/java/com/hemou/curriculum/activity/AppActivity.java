package com.hemou.curriculum.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.hemou.curriculum.R;
import com.hemou.curriculum.adapter.TimeAdapter;
import com.hemou.curriculum.pojo.Course;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AppActivity extends AppCompatActivity {
    //preference 域
    //private CourseDao courseDao = new CourseDao(this);
    private SharedPreferences preferences;


    private Set<String> timeSet;

    private ListView lvContent;
    //换成time
    private List<Course> courseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        lvContent = findViewById(R.id.timelvContent);
        lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //换成time
                List<String> timeList = new ArrayList<>(timeSet);
                String time = timeList.get(position);
                Intent intent = new Intent(AppActivity.this, UpdateAppActivity.class);
                intent.putExtra("time", time);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        show();
    }

    /**
     * 所有课程
     */
    private void show() {
        //courseList = courseDao.listAll();
        //int listSize = courseList.size();
//        if (listSize > 0) {
//            String[] courseNames = new String[listSize];
//            for (int i = 0; i < listSize; i++) courseNames[i] = courseList.get(i).getCourseName();
//            ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, courseNames);
//            lvContent.setAdapter(arrayAdapter);
//        } else {
//            Toast.makeText(this, "暂无数据，请添加课程！", Toast.LENGTH_SHORT).show();
//        }

        getPreference();
        int setSize = timeSet.size();
        List<String> timeList = new ArrayList<>(timeSet);
        if(setSize > 0){
            String[] timeZone = new String[setSize];
            for(int i = 0; i < setSize; i++) timeZone[i] = timeList.get(i).substring(8);
            TimeAdapter timeAdapter = new TimeAdapter(this, timeSet);
            lvContent.setAdapter(timeAdapter);
        }else{
            Toast.makeText(this, "暂无时间段！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        this.finish();
    }

    /**
     * 添加课程
     *
     * @param view
     */
    public void addTime(View view) {
        final View inflate = getLayoutInflater().inflate(R.layout.add_time_item, null);
        final CheckBox workButton = ((CheckBox) inflate.findViewById(R.id.checkBoxWork));
        final CheckBox restButton = ((CheckBox) inflate.findViewById(R.id.checkBoxRest));
//        workButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                if(workButton.isChecked()){
//                    workButton.setChecked(false);
//                }else{
//                    workButton.setChecked(true);
//                }
//            }
//        });
//        restButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                if(restButton.isChecked()){
//                    restButton.setChecked(false);
//                }else {
//                    workButton.setChecked(true);
//                }
//            }
//        });
        new AlertDialog.Builder(this)
                .setView(inflate)
                .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String start = ((EditText) inflate.findViewById(R.id.editTextTimeStart)).getText().toString();
                        String end = ((EditText) inflate.findViewById(R.id.editTextTimeEnd)).getText().toString();

                        boolean work = workButton.isChecked();
                        boolean rest = restButton.isChecked();
                        if ("".equals(start) || "".equals(end)) {
                            Toast.makeText(AppActivity.this, "时间不可为空！", Toast.LENGTH_SHORT).show();
                        } else {
//                            long insert = 1;
                            if (timeSet.add("000000" + "0" + "0" +start + end)) {
                                Toast.makeText(AppActivity.this, "添加时间段成功！", Toast.LENGTH_SHORT).show();
                                savePreference();
                                show();
                            }else{
                                Toast.makeText(AppActivity.this, "添加时间段失败！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    private void getPreference(){
        preferences = getSharedPreferences("time_set", Context.MODE_PRIVATE);
        timeSet = preferences.getStringSet("time_set",new HashSet<String>());
    }

    private void savePreference(){
        preferences = getSharedPreferences("time_set",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("time_set",timeSet);
        //editor.putStringSet("initValue2", String.valueOf(initial2));
        editor.commit();
    }



//    /**
//     * 修改开学时间
//     * @param view
//     */
//    public void alterDate(View view) {
//        final SharedPreferences config = getSharedPreferences("config", MODE_PRIVATE);
//        final DatePicker datePicker = new DatePicker(this);
//        long date = config.getLong("date", 0);
//        if(date != 0){
//            Calendar c = Calendar.getInstance();
//            c.setTime(new Date(date));
//            datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), null);
//        }
//        new DatePickerDialog.Builder(this)
//                .setTitle("选择开学日期")
//                .setView(datePicker)
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        int year = datePicker.getYear();
//                        int month = datePicker.getMonth();
//                        int dayOfMonth = datePicker.getDayOfMonth();
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.set(year, month, dayOfMonth, 0, 0, 0);
//                        Date time = calendar.getTime();
//                        config.edit().putLong("date", time.getTime()).apply();
//                    }
//                })
//                .setNegativeButton("取消", null)
//                .create()
//                .show();
//    }

}
