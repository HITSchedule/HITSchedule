package com.example.aclass.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aclass.R;
import com.example.aclass.database.MySubject;
import com.zhuangfei.timetable.model.Schedule;

import java.util.List;


public class SimpleAdapter extends BaseAdapter {

    private Context mContext;
    private int resourceId;
    private List<Schedule> scheduleList;

    public SimpleAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Schedule> scheduleList) {
        this.resourceId = resource;
        this.scheduleList = scheduleList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return scheduleList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View myView = LayoutInflater.from(mContext).inflate(resourceId,null);
        Schedule schedule = scheduleList.get(i);
        TextView title = myView.findViewById(R.id.name);
        TextView info = myView.findViewById(R.id.info);
        TextView room = myView.findViewById(R.id.room);
        TextView other = myView.findViewById(R.id.other);

        LinearLayout other_layout = myView.findViewById(R.id.other_layout);



        title.setText(schedule.getName());
        String s = schedule.getExtras().get("info") == null ? "null" : (String) schedule.getExtras().get("info");
        String content = "";
        String o = "";
        if(s.startsWith("#")){
            String con = s.replace("#","");
            Log.d("TESTTESTTEST", "getView: " + content);
            content = con.split("`")[0];
            o = con.split("`").length > 1 ? con.split("`")[1] : "";
        } else {
            String[] ss = s.split("周，");
            if (ss.length > 1){
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) info.getLayoutParams();
                lp.setMargins(dp2px(mContext, 10), 0, 0, 0);
                info.setLayoutParams(lp);
            }
            for(String each : ss){
                String inf = each.replace("周", "");
                content = content + inf + "周\n";
            }
        }
        if(o.isEmpty()){
            other_layout.setVisibility(View.GONE);
        } else {
            other_layout.setVisibility(View.VISIBLE);
            other.setText(o);
        }

        info.setText(content.trim());
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
