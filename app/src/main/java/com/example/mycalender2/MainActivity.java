package com.example.mycalender2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvCurrentDate;
    private TextView tvPreMonth;
    private TextView tvNextMonth;
    private GridView gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化布局
        initView();
    }

    private void initView() {
        tvCurrentDate = (TextView) findViewById(R.id.tvCurrentDate);
        tvPreMonth = (TextView) findViewById(R.id.tvPreMonth);
        tvNextMonth = (TextView) findViewById(R.id.tvNextMonth);
        gv = (GridView) findViewById(R.id.gv);
        // 初始化适配器
        initAdapter();
    }

    private void initAdapter() {
        final List<DayBean> dataList = new ArrayList<>();
        final DayAdapter adapter = new DayAdapter(dataList, this);
        gv.setAdapter(adapter);

        // 拿到日历对象，动态设置时间
        // 使用日历对象可以帮我们避免一些问题，如 月数 的临界点问题，到的 12 月是再加 1 的话会自动
        // 帮我们加到下一年去，同理从 1 月到 12 月也一样。
        final Calendar calendar = Calendar.getInstance();
        setCurrentData(calendar);

        updateAdapter(calendar, dataList, adapter);

        tvPreMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                updateAdapter(calendar, dataList, adapter);
            }
        });

        tvNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
                updateAdapter(calendar, dataList, adapter);
            }
        });
    }

    private void updateAdapter(Calendar calendar, List<DayBean> dataList, DayAdapter adapter) {
        dataList.clear();
        setCurrentData(calendar);
        // 得到本月一号的星期索引
        // 索引从 1 开始，第一个为星期日,减1是为了与星期对齐，如星期一对应索引1，星期二对应索引二
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int weekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;


        // 将日期设为上个月
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        int preMonthDays = getMonth(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        // 拿到上一个月的最后几天的天数
        for (int i = 0; i < weekIndex; i++) {
            DayBean bean = new DayBean();
            bean.setYear(calendar.get(Calendar.YEAR));
            bean.setMonth(calendar.get(Calendar.MONTH) + 1);
            bean.setDay(preMonthDays - weekIndex + i + 1);
            bean.setCurrentDay(false);
            bean.setCurrentMonth(false);
            dataList.add(bean);
        }

        // 将日期设为当月
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        int currentDays = getMonth(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        // 拿到当月的天数
        for (int i = 0; i < currentDays; i++) {
            DayBean bean = new DayBean();
            bean.setYear(calendar.get(Calendar.YEAR));
            bean.setMonth(calendar.get(Calendar.MONTH) + 1);
            bean.setDay(i + 1);
            // 当前日期
            String nowDate = getFormatTime("yyyy-M-d", Calendar.getInstance().getTime());
            // 选择的日期
            String selectDate = getFormatTime("yyyy-M-", calendar.getTime()) + (i + 1);
            // 假如相等的话，那么就是今天的日期了
            if (nowDate.contentEquals(selectDate)) {
                bean.setCurrentDay(true);
            } else {
                bean.setCurrentDay(false);
            }
            bean.setCurrentMonth(true);
            dataList.add(bean);
        }

        // 拿到下个月第一周的天数
        // 先拿到下个月第一天的星期索引
        // 之前设为了1号，所以将日历对象的月数加 1 就行了
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        weekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        for (int i = 0; i < 7 - weekIndex; i++) {
            DayBean bean = new DayBean();
            bean.setYear(calendar.get(Calendar.YEAR));
            bean.setMonth(calendar.get(Calendar.MONTH) + 1);
            bean.setDay(i + 1);
            bean.setCurrentDay(false);
            bean.setCurrentMonth(false);
            dataList.add(bean);
        }

        adapter.notifyDataSetChanged();
        // 最后将日期设为当月
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
    }
    // 设置当前的时间
    private void setCurrentData(Calendar calendar) {
        tvCurrentDate.setText(calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月");
    }
    // 判断是否为闰年
    public boolean isRunYear(int y) {
        return y % 4 == 0 && y % 100 != 0 || y % 400 == 0;
    }
    // 格式化时间，设置时间很方便，也比较简单，学的很快
    public static String getFormatTime(String p, Date t) {
        return new SimpleDateFormat(p, Locale.CHINESE).format(t);
    }
    // 传入年和月得出当月的天数
    public int getMonth(int m, int y) {
        switch (m) {
            case 2:
                return isRunYear(y) ? 29 : 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }
}

