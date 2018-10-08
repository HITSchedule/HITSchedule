package com.example.aclass.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.aclass.R;

import java.util.List;

public class MyListAdapter extends BaseAdapter {

    private Context mContext;
    private int resourceId;
    private List<String> itemList;

    public MyListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> itemList) {
        this.resourceId = resource;
        this.itemList = itemList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return itemList.size();
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
        TextView item = myView.findViewById(R.id.item_name);
        String item_name = itemList.get(i);

        item.setText(item_name);
        return myView;
    }
}
