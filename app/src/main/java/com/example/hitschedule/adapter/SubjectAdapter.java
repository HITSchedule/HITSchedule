package com.example.hitschedule.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hitschedule.R;
import com.zhuangfei.timetable.model.Schedule;

import java.util.List;

import static com.example.hitschedule.util.Constant.INFO;


public class SubjectAdapter extends BaseAdapter {

    private Context mContext;
    private int resourceId;
    private List<Schedule> scheduleList;
    private View.OnClickListener listener;

    public SubjectAdapter(@NonNull Context context, View.OnClickListener listener, @LayoutRes int resource, @NonNull List<Schedule> scheduleList) {
        this.resourceId = resource;
        this.scheduleList = scheduleList;
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return scheduleList.size();
    }

    @Override
    public Object getItem(int i) {
        return scheduleList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View myView = LayoutInflater.from(mContext).inflate(resourceId,null);
        Schedule schedule = scheduleList.get(i);
        TextView title = myView.findViewById(R.id.name);
        TextView info = myView.findViewById(R.id.info);
        TextView room = myView.findViewById(R.id.room);
        TextView other = myView.findViewById(R.id.other);
        ImageView delete = myView.findViewById(R.id.delete);
        ImageView edit = myView.findViewById(R.id.edit);
        LinearLayout other_layout = myView.findViewById(R.id.other_layout);

        delete.setOnClickListener(listener);
        edit.setOnClickListener(listener);


        title.setText(schedule.getName());
        String s = schedule.getExtras().get(INFO) == null
                ? "null"
                : (String) schedule.getExtras().get(INFO);

        StringBuilder content = new StringBuilder();

        String o = "";
        if (s.startsWith("#")){
            //TODO 处理自己添加的课程
        } else {
            String[] ss = s.split("周，");
            if (ss.length > 1){
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) info.getLayoutParams();
                lp.setMargins(dp2px(mContext, 10), 0, 0, 0);
                info.setLayoutParams(lp);
            }
            for(String each : ss){
                String inf = each.replace("周", "");
                if (each.startsWith("周")){
                    inf = "周" + inf;
                }
                content.append(inf).append("周\n");
            }
        }


        if(o.isEmpty()){
            other_layout.setVisibility(View.GONE);
        } else {
            other_layout.setVisibility(View.VISIBLE);
            other.setText(o);
        }

        info.setText(content.toString().trim());
        room.setText(schedule.getRoom());
        return myView;
    }

    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
