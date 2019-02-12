package com.example.hitschedule.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.hitschedule.util.DensityUtil;
import com.example.hitschedule.util.Util;

public class CustomDialog extends Dialog {

    private Context context;
    private int height, width;
    private boolean cancelTouchout;
    private View view;
    private int gravity;
    private int animation;

    public CustomDialog(Builder builder) {
        super(builder.context);
        context = builder.context;
        height = builder.height;
        width = builder.width;
        cancelTouchout = builder.cancelTouchout;
        view = builder.view;
        gravity = builder.gravity;
        animation = builder.animation;
    }

    public CustomDialog(Builder builder, int resStyle) {
        super(builder.context, resStyle);
        context = builder.context;
        height = builder.height;
        width = builder.width;
        cancelTouchout = builder.cancelTouchout;
        view = builder.view;
        gravity = builder.gravity;
        animation = builder.animation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
        setCanceledOnTouchOutside(cancelTouchout);
        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();

        lp.height = height;
        lp.width = width;

        if (gravity != -1){
            lp.gravity = gravity;
        }else {
            lp.gravity = Gravity.CENTER;
        }

        if (animation != -1){
            lp.windowAnimations = animation;
        }
        win.setAttributes(lp);
    }

    public View getView(){
        return view;
    }


    public static final class Builder {

        private Context context;
        private int height, width;
        private boolean cancelTouchout;
        private View view;
        private int resStyle = -1;
        private int gravity = -1;
        private int animation = -1;

        public Builder(Context context) {
            this.context = context;
        }


        public Builder view(int resView) {
            view = LayoutInflater.from(context).inflate(resView, null);
            return this;
        }


        public Builder heightpx(int val) {
            height = val;
            return this;
        }

        public Builder widthpx(int val) {
            width = val;
            return this;
        }

        public Builder heightdp(int val) {
            height = DensityUtil.dp2px(context, val);
            return this;
        }

        public Builder widthdp(int val) {
            width = DensityUtil.dp2px(context, val);
            return this;
        }

        public Builder heightDimenRes(int dimenRes) {
            height = context.getResources().getDimensionPixelOffset(dimenRes);
            return this;
        }

        public Builder widthDimenRes(int dimenRes) {
            width = context.getResources().getDimensionPixelOffset(dimenRes);
            return this;
        }

        public Builder style(int resStyle) {
            this.resStyle = resStyle;
            return this;
        }

        public Builder gravity(int gravity){
            this.gravity = gravity;
            return this;
        }

        public Builder animation(int animation){
            this.animation = animation;
            return this;
        }

        public Builder cancelTouchout(boolean val) {
            cancelTouchout = val;
            return this;
        }

        public Builder text(int viewRes, String text){
            TextView textView = view.findViewById(viewRes);
            textView.setText(text);
            return this;
        }

        public Builder addViewOnclick(int viewRes,View.OnClickListener listener){
            view.findViewById(viewRes).setOnClickListener(listener);
            return this;
        }



        public CustomDialog build() {
            if (resStyle != -1) {
                return new CustomDialog(this, resStyle);
            } else {
                return new CustomDialog(this);
            }
        }
    }
}