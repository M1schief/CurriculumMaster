package com.hemou.curriculum.view;

import android.bluetooth.BluetoothA2dp;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hemou.curriculum.pojo.Course;
import com.hemou.curriculum.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectTableView extends LinearLayout {

    //星期
    protected     String[] weekTitle = {"一", "二", "三", "四", "五"};
    //最大星期数
    protected     int weeksNum = weekTitle.length;
    //最大节数
    protected     int maxSection = 9;

    //圆角半径
    protected int radius = 8;
    //线宽
    protected     int tableLineWidth = 1;
    //数字字体大小
    protected     int numberSize = 14;
    //标题字体大小
    protected     int titleSize = 18;
    //课表信息字体大小
    protected     int courseSize = 12;
    //底部按钮大小
    protected     int buttonSize = 12;

    //单元格高度
    protected int cellHeight = 75;
    //星期标题高度
    protected     int titleHeight = 30;
    //最左边数字宽度
    protected     int numberWidth = 20;

    protected Context mContext;
    protected     List<Course> courseList;
    protected     Map<String, Integer> colorMap = new HashMap<>();
    protected     Map<Integer, List<Course>> courseMap = new HashMap<>();

    //开学日期
    protected     Date startDate;
    protected     long weekNum;

    //菜单栏
    protected     ImageView mCategory;
    //周次信息
    protected     TextView mWeekTitle;
    protected     LinearLayout mMainLayout;
    protected     RelativeLayout mTitleLayout;

    protected     int currentX;

    public static String[][] response = new String[9][5];
    static{
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 5; j++){
                response[i][j] = "@";
            }
        }
    }
    public SelectTableView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public SelectTableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    /**
     * 初始化/修改默认参数
     *
     * @param params 参数
     */
    public void initParams(Map<String, Integer> params) {
        if (!params.isEmpty()) {
            try {
                for (String key : params.keySet()) {
                    Class<TimeTableView> timeTableClass = TimeTableView.class;
                    String methodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
                    Method method = timeTableClass.getDeclaredMethod(methodName, int.class);
                    method.invoke(this, params.get(key));
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        preprocessorParam();
    }

    /**
     * 数据预处理
     */
    protected     void preprocessorParam() {
        tableLineWidth = dip2px(tableLineWidth);
        cellHeight = dip2px(cellHeight);
        titleHeight = dip2px(titleHeight);
        numberWidth = dip2px(numberWidth);
    }

    /**
     * 设置菜单按钮的监听事件
     * @param listener
     */
    public void addListener(OnClickListener listener){
        mCategory.setOnClickListener(listener);
    }

    /**
     * 加载数据
     *
     * @param courses
     */
    public void loadData(List<Course> courses, Date date, String target) {
        this.courseList = courses;
        this.startDate = date;
        weekNum = calcWeek(startDate);
        handleData(courseMap, courses, weekNum);
        flushView(courseMap, weekNum, target);
    }

    /**
     * 处理数据
     * @param courseMap 处理结果
     * @param courseList 数据
     * @param weekNum 周次
     */
    protected     void handleData(Map<Integer, List<Course>> courseMap, List<Course> courseList, long weekNum) {
        courseMap.clear();
        for (Course c : courseList) {
            String courseTime = c.getCourseTime();
            if(TextUtils.isEmpty(courseTime))continue;
            String[] courseArray = courseTime.split(";");
            for (int i = 0; i < courseArray.length; i++) {
                Course clone = c.clone();
                String[] info = courseArray[i].split(":");
                //if ("n".o(info[2]) || weekNum % 2 == 0 && "d".equals(info[2]) || weekNum % 2 == 1 && "s".equals(info[2])) {//非单双周
                    clone.setDay(Integer.parseInt(info[0]));
                    clone.setSection(Integer.parseInt(info[1]));

                    List<Course> courses = courseMap.get(clone.getDay());
                    if (null == courses) {
                        courses = new ArrayList<>();
                        courseMap.put(clone.getDay(), courses);
                    }
                    courses.add(clone);
                //}
            }
        }
    }

    /**
     * 初始化视图
     */
    protected     void initView(){
        preprocessorParam();
        //周次标题
        //addWeekTitle(this);
        //星期标签
        addWeekLabel(this);
        //课程信息
        //flushView(null, weekNum);
    }

    /**
     * 刷新课程视图
     * @param courseMap 课程数据
     * @param weekNum 周次
     */
    protected     void flushView(Map<Integer, List<Course>> courseMap, long weekNum, String target) {
        //初始化主布局
        if (null != mMainLayout) removeView(mMainLayout);
        mMainLayout = new LinearLayout(mContext);
        mMainLayout.setOrientation(HORIZONTAL);
        mMainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mMainLayout);
        //周次标题
        //mWeekTitle.setText("第 " + weekNum + " 周");
        //左侧节次标签
        addLeftNumber(mMainLayout);
        //课程信息
        if (null == courseMap || courseMap.isEmpty()) {//数据为空
            //addVerticalTableLine(mMainLayout);
            //TextView emptyLayoutTextView = createTextView("已结课，或未添加课程信息！", titleSize, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0, getResources().getColor(R.color.textColor), Color.WHITE);
            //mMainLayout.addView(emptyLayoutTextView);
            for (int i = 1; i <= weeksNum; i++) {

                addVerticalTableLine(mMainLayout);
                LinearLayout linearLayout = new LinearLayout(mContext);
                linearLayout.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                linearLayout.setOrientation(VERTICAL);
                addBlankCell(linearLayout, 9, i,1);

                mMainLayout.addView(linearLayout);
            }
        } else {//不为空
            for (int i = 1; i <= weeksNum; i++) {
                addVerticalTableLine(mMainLayout);
                addDayCourse(mMainLayout, courseMap, i, target);
            }
        }
        invalidate();
    }

    /**
     * 周次标题
     *
     * @param pViewGroup
     */
    protected     void addWeekTitle(ViewGroup pViewGroup) {
        mTitleLayout = new RelativeLayout(mContext);
        mTitleLayout.setPadding(8, 15, 8, 15);
        mTitleLayout.setBackgroundColor(getResources().getColor(R.color.titleColor));
        //周次信息
        mWeekTitle = new TextView(mContext);
        mWeekTitle.setTextSize(titleSize);
        mWeekTitle.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mWeekTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        mTitleLayout.addView(mWeekTitle);
        //左侧菜单栏

        mCategory = new ImageView(mContext);
        mCategory.setImageResource(R.drawable.category);
        mCategory.setLayoutParams(new LayoutParams(dip2px(30), dip2px(30)));
        mTitleLayout.addView(mCategory);

        pViewGroup.addView(mTitleLayout);
        addHorizontalTableLine(pViewGroup);
    }

    /**
     * 添加星期标签
     */
    protected     void addWeekLabel(ViewGroup pViewGroup) {
        LinearLayout mTitleLayout = new LinearLayout(mContext);
        mTitleLayout.setOrientation(HORIZONTAL);
        mTitleLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, titleHeight));
        addView(mTitleLayout);
        //空白符
        TextView space = new TextView(mContext);
        space.setLayoutParams(new ViewGroup.LayoutParams(numberWidth, ViewGroup.LayoutParams.MATCH_PARENT));
        space.setBackgroundColor(getResources().getColor(R.color.titleColor));
        mTitleLayout.addView(space);
        //星期
        for (int i = 0; i < weeksNum; i++) {
            addVerticalTableLine(mTitleLayout);
            TextView title = createTextView(weekTitle[i], titleSize, 0, ViewGroup.LayoutParams.MATCH_PARENT, 1, getResources().getColor(R.color.textColor), getResources().getColor(R.color.titleColor));
            mTitleLayout.addView(title);
        }
    }

    /**
     * 添加左侧节次数字
     */
    protected     void addLeftNumber(ViewGroup pViewGroup) {
        LinearLayout leftLayout = new LinearLayout(mContext);
        leftLayout.setOrientation(VERTICAL);
        leftLayout.setLayoutParams(new LayoutParams(numberWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
        for (int i = 1; i <= maxSection; i++) {
            addHorizontalTableLine(leftLayout);
            TextView number = createTextView(String.valueOf(i), numberSize, ViewGroup.LayoutParams.MATCH_PARENT, 0, 1, getResources().getColor(R.color.textColor), Color.WHITE);
            leftLayout.addView(number);
        }
        pViewGroup.addView(leftLayout);
    }

    /**
     * 添加单天课程
     *
     * @param pViewGroup pViewGroup 父组件
     * @param day        星期
     */
    protected void addDayCourse(ViewGroup pViewGroup, Map<Integer, List<Course>> courseMap, int day, String target) {
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        linearLayout.setOrientation(VERTICAL);
        List<Course> courses = getCourses(courseMap, day);
        if (null != courses) {
            for (int i = 0, size = courses.size(); i < size; i++) {
                Course course = courses.get(i);
                int section = course.getSection();
                if (i == 0) addBlankCell(linearLayout, section - 1, day, 1);
                else
                    addBlankCell(linearLayout, section - courses.get(i - 1).getSection() - 1, day, courses.get(i - 1).getSection() + 1);
                addCourseCell(linearLayout, course, target);
                if (i == size - 1) addBlankCell(linearLayout, maxSection - section, day, section + 1);
            }
        } else {
            addBlankCell(linearLayout, maxSection, day, 1);
        }
        pViewGroup.addView(linearLayout);
    }

    /**
     * 获取单天课程信息
     *
     * @param day 星期
     * @return 课程信息List
     */
    public List<Course> getCourses(Map<Integer, List<Course>> courseMap, int day) {
        final List<Course> courses = courseMap.get(day);
        if (null != courses) {
            Collections.sort(courses, new Comparator<Course>() {
                @Override
                public int compare(Course o1, Course o2) {
                    return o1.getSection() - o2.getSection();
                }
            });
        }
        return courses;
    }

    /**
     * 添加课程单元格
     * @param pViewGroup 父组件
     * @param course 课程信息
     */
    protected void addCourseCell(ViewGroup pViewGroup, Course course, String target) {
        addHorizontalTableLine(pViewGroup);
        final String name =  course.getCourseName();
        final RoundTextView textView = new RoundTextView(mContext, radius, getColor(target, course.getCourseName()));
        textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //变色
                if(textView.getText().toString().equals(name)){
                    //Log.e("0","start");
                    if(textView.isColored){
                        textView.setTextColor(Color.BLACK);
                        v.setBackgroundColor(Color.rgb(255, 255, 255));
                        textView.isColored = false;
                        response[textView.row - 1][textView.col - 1] = "del";
                        //Log.e("0","change");
                    }
                    else{
                        textView.setTextColor(Color.WHITE);
                        v.setBackgroundColor(Color.rgb(0, 0, 250));
                        textView.isColored = true;
                        response[textView.row - 1][textView.col - 1] = "@";
                    }
                }
                else{
                    if(textView.isColored){
                        textView.setTextColor(Color.BLACK);
                        v.setBackgroundColor(Color.rgb(255, 255, 255));
                        textView.isColored = false;
                        response[textView.row - 1][textView.col - 1] = "@";
                    }
                    else{
                        textView.setTextColor(Color.WHITE);
                        v.setBackgroundColor(Color.rgb(0, 0, 250));
                        textView.isColored = true;
                        response[textView.row - 1][textView.col - 1] = textView.getText().toString();
                        Log.e("content",textView.getText().toString());
                    }
                }

                // if(textView.isColored){
                //     v.setBackgroundColor(Color.rgb(255, 255, 255));
                //     textView.isColored = false;
                //     response[textView.row - 1][textView.col - 1] = "@";
                // }
                // else{
                //     v.setBackgroundColor(Color.rgb(0, 0, 250));
                //     textView.isColored = true;
                //     response[textView.row - 1][textView.col - 1] = textView.getText().toString();
                // }
                //增加节次数组
            }});
        textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,1));
        textView.setTextSize(courseSize);
        if(textView.isColored){
            textView.setTextColor(Color.WHITE);
        }
        textView.setGravity(Gravity.CENTER);
        textView.setText(String.format("%s", course.getCourseName()));
        pViewGroup.addView(textView);
        textView.row = course.getSection();
        textView.col = course.getDay();
        response[textView.row - 1][textView.col - 1] = "@";
    }


    /**
     * 添加空白块
     *
     * @param pViewGroup 父组件
     * @param num        空白块数量
     */
    protected void addBlankCell(ViewGroup pViewGroup, int num, int day, int start) {
        for (int i = 0; i < num; i++) {
            addHorizontalTableLine(pViewGroup);
            final RoundTextView blank = new RoundTextView(mContext);
            blank.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //变色
                    if(blank.isColored){
                        v.setBackgroundColor(Color.rgb(255, 255, 255));//
                        blank.isColored = false;
                        response[blank.row - 1][blank.col - 1] = "@";
                    }else{
                        v.setBackgroundColor(Color.rgb(0, 0, 250));
                        blank.isColored = true;
                        response[blank.row - 1][blank.col - 1] = "$";
                    }
                    // 增加节次数组
                }});
            blank.col = day;
            blank.row = start + i;
            blank.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,1));
            pViewGroup.addView(blank);
            response[blank.row - 1][blank.col - 1] = "@";
        }
    }

    /**
     * 添加垂直线
     *
     * @param pViewGroup 父组件
     */
    protected void addVerticalTableLine(ViewGroup pViewGroup) {
        View view = new View(mContext);
        view.setLayoutParams(new ViewGroup.LayoutParams(tableLineWidth, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setBackgroundColor(getResources().getColor(R.color.viewLine));
        pViewGroup.addView(view);
    }

    /**
     * 添加水平线
     *
     * @param pViewGroup 父组件
     */
    protected void addHorizontalTableLine(ViewGroup pViewGroup) {
        View view = new View(mContext);
        view.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, tableLineWidth));
        view.setBackgroundColor(getResources().getColor(R.color.viewLine));
        pViewGroup.addView(view);
    }

    /**
     * 创建TextView
     *
     * @param content    文本内容
     * @param color  字体颜色
     * @param size   字体大小
     * @param width  宽度
     * @param height 高度
     * @param weight 权重
     * @return
     */
    protected     TextView createTextView(String content, int size, int width, int height, int weight, int color, int bkColor) {
        TextView textView = new TextView(mContext);
        textView.setLayoutParams(new LayoutParams(width, height, weight));
        if(bkColor != -1)textView.setBackgroundColor(bkColor);
        textView.setTextColor(color);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(size);
        textView.setText(content);
        return textView;
    }

    protected     void toggleWeek(int flag){
        if(flag < 0){
            weekNum = weekNum - 1 <= 0 ? weekNum : weekNum - 1;
        }else{
            weekNum = weekNum + 1 > 19  ? weekNum : weekNum + 1;
        }
        handleData(courseMap, courseList, weekNum);
        //flushView(courseMap, weekNum);
    }

    /**
     * 计算当前周次
     * @param date
     * @return
     */
    protected     long calcWeek(Date date) {
        return (new Date().getTime() - date.getTime()) / (1000 * 3600 * 24 * 7) + 1;
    }

    protected     int getColor(Map<String, Integer> map, String name) {
        Integer tip = map.get(name);
        if (null != tip) {
            return tip;
        } else {
            int i = getResources().getColor(color[map.size() % color.length]);
            map.put(name, i);
            return i;
        }
    }

    protected     int getColor(String target, String name) {
        if(name.equals(target)){
            int i = getResources().getColor(R.color.blue);
            return i;
        }
        else{
            int i = getResources().getColor(R.color.white);
            return i;
        }
    }

    protected     int dip2px(float dpValue) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale);
    }

    protected     int color[] = {
            R.color.one, R.color.two, R.color.three,
            R.color.four, R.color.five, R.color.six,
            R.color.seven, R.color.eight, R.color.nine,
            R.color.ten, R.color.eleven, R.color.twelve,
            R.color.thirteen, R.color.fourteen, R.color.fifteen
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentX = (int) event.getX();
                break;
            case MotionEvent.ACTION_UP:
                int i = (int) event.getX() - currentX;
                if(i > 30){
                    toggleWeek(-1);
                }else if(i < -30){
                    toggleWeek(1);
                }
                break;
        }
        return true;
    }

    public void setMaxSection(int maxSection) {
        this.maxSection = maxSection;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setTableLineWidth(int tableLineWidth) {
        this.tableLineWidth = tableLineWidth;
    }

    public void setNumberSize(int numberSize) {
        this.numberSize = numberSize;
    }

    public void setTitleSize(int titleSize) {
        this.titleSize = titleSize;
    }

    public void setCourseSize(int courseSize) {
        this.courseSize = courseSize;
    }

    public void setButtonSize(int buttonSize) {
        this.buttonSize = buttonSize;
    }

    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }

    public void setTitleHeight(int titleHeight) {
        this.titleHeight = titleHeight;
    }

    public void setNumberWidth(int numberWidth) {
        this.numberWidth = numberWidth;
    }
}
