package com.hemou.curriculum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;

import com.hemou.curriculum.R;

import java.util.List;

import androidx.annotation.NonNull;

public class APPAdapter extends BaseAdapter {

    //private CourseDao dao;
    private Context context;
    private LayoutInflater layoutInflater;
    private List<String> APPList;

    //private View.OnClickListener listener;

    public APPAdapter(Context context, @NonNull List<String> AppList){
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.APPList = AppList;

        //this.listener = listener;
        //dao = new CourseDao(context);
    }

    @Override
    public int getCount() {
        return APPList.size();
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
            convertView = layoutInflater.inflate(R.layout.app_item, null);
            holder = new ViewHolder();
            holder.APPSwitch = convertView.findViewById(R.id.AppSwitch);
            holder.APPSwitch.setTag(APPList.get(position));
            //holder.ivDel.setOnClickListener(listener);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        setContent(position, holder);
        return convertView;
    }

    private void setContent(int i, ViewHolder view){
        view.APPSwitch.setText(String.format("应用名%s", APPList.get(i)));
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
        Switch APPSwitch;
    }
}
