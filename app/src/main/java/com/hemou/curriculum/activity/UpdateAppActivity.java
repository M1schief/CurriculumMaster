package com.hemou.curriculum.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hemou.curriculum.R;
import com.hemou.curriculum.adapter.APPAdapter;
import com.hemou.curriculum.adapter.LessonAdapter;
import com.hemou.curriculum.pojo.Course;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateAppActivity extends AppCompatActivity {

    private List<String> allAppList = new ArrayList<>();
    private SharedPreferences preferences;
    private String time;
    private ListView lvAPP;
    private String originTime;
    private CheckBox restButton,workButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_app);
        lvAPP = findViewById(R.id.lvApp);
        time = getIntent().getStringExtra("time");
        originTime = new String(time);
        workButton = (CheckBox) findViewById(R.id.checkBoxWork2);
        restButton = (CheckBox) findViewById(R.id.checkBoxRest2);
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
//                }else{
//                    workButton.setChecked(true);
//                }
//            }
//        });
        getAppList();
        showTime();
        showAPP();
    }

    private void getAppList(){
        PackageManager pm = getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages){
            Log.e("1", packageInfo.packageName);
            allAppList.add(packageInfo.packageName);
        }
    }

    private void showTime(){
        Log.e("0", time);
        String timeZone = time.substring(8);
        String startTime = timeZone.substring(0,5);
        String endTime = timeZone.substring(5);

        char restOrWork = time.charAt(6);
        ((EditText) findViewById(R.id.editTextTimeStart2)).setText(startTime);
        ((EditText) findViewById(R.id.editTextTimeEnd2)).setText(endTime);
        workButton.setChecked(restOrWork == '0' || restOrWork == '2' );
        restButton.setChecked(restOrWork == '1' || restOrWork == '2' );
    }

    private void showAPP(){
        int num = allAppList.size();
        APPAdapter APPAdapter = new APPAdapter(this,allAppList);
        lvAPP.setAdapter(APPAdapter);
        for(int i = 0; i < 6; i++){
            char valid = time.charAt(i);
            //getItemId ERROR 没有新建
            ((Switch) findViewById((int)APPAdapter.getItemId(i))).setChecked(valid == '1');
            ((Switch) findViewById((int)APPAdapter.getItemId(i))).setOnCheckedChangeListener(new appListener(i));
        }
    }

    private class appListener implements CompoundButton.OnCheckedChangeListener{

        int index;
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            char[] charTime = time.toCharArray();
            if(isChecked){
                charTime[index] = '0';
            }else{
                charTime[index] = '1';
            }
            time = charTime.toString();
        }

        public appListener(int index){
            this.index = index;
        }
    }

    public void save(View view) {
        preferences = getSharedPreferences("time_set", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        preferences = getSharedPreferences("time_set", Context.MODE_PRIVATE);
        Set<String> timeSet = preferences.getStringSet("time_set",new HashSet<String>());
        for(String t: timeSet){
            if(t.equals(originTime)){
                timeSet.remove(originTime);
                break;
            }
        }
        timeSet.add(time);
        editor.putStringSet("time_set",timeSet);
        editor.commit();
        originTime = time;
    }

    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        this.finish();
    }

    private boolean del(){
        preferences = getSharedPreferences("time_set", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        preferences = getSharedPreferences("time_set", Context.MODE_PRIVATE);
        Set<String> timeSet = preferences.getStringSet("time_set",new HashSet<String>());
        for(String t: timeSet){
            if(t.equals(originTime)){
                timeSet.remove(originTime);
                editor.putStringSet("time_set",timeSet);
                editor.commit();
                return true;
            }
        }
        return false;
    }

    private void delTime(View view){
        new AlertDialog.Builder(this)
                .setTitle("删除时间段")
                .setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (del()) {
                            Toast.makeText(UpdateAppActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                            UpdateAppActivity.this.finish();
                        } else {
                            Toast.makeText(UpdateAppActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

}
