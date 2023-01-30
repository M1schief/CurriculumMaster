package com.hemou.curriculum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;

import com.hemou.curriculum.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;

public class TimeAdapter extends BaseAdapter {

    //private CourseDao dao;
    private Context context;
    private LayoutInflater layoutInflater;
    private Set<String> timeSet;
    private List<String> timeList;

    //private View.OnClickListener listener;

    public TimeAdapter(Context context, @NonNull Set<String> timeSet){
        this.context = context;
        this.timeSet = timeSet;
        this.layoutInflater = LayoutInflater.from(context);
        this.timeList = new ArrayList<>(timeSet);

        //this.listener = listener;
        //dao = new CourseDao(context);
    }

    @Override
    public int getCount() {
        return timeSet.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(null == convertView){
            convertView = layoutInflater.inflate(R.layout.time_item, null);
            holder = new ViewHolder();
            holder.TimeSwitch = convertView.findViewById(R.id.timeSwitch);
            holder.TimeSwitch.setTag(timeList.get(position));
            //holder.ivDel.setOnClickListener(listener);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        setContent(position, holder);
        return convertView;
    }

    private void setContent(int i, ViewHolder view){
        view.TimeSwitch.setText(String.format("时间段%s", timeList.get(i).substring(8)));
//        String weekType = courseList.get(i).getWeekType();
//        if("s".equals(weekType)){
//            view.tvWeekType.setText("单周");
//        }else if("d".equals(weekType)){
//            view.tvWeekType.setText("双周");
//        }else{
//            view.tvWeekType.setText("正常周次");
//        }
    }

    static class ViewHolder{
        Switch TimeSwitch;
    }
}
