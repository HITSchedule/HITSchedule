package com.example.aclass.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.example.aclass.R;

import java.util.ArrayList;
import java.util.List;

public class WeekChooseAdapter extends BaseAdapter{

    private String TAG = getClass().getName();

    private Context mContext;
    private int resourceId;
    private List<Integer> weekList;

    private View myView;

    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;
    private Button button8;
    private Button button9;
    private Button button10;
    private Button button11;
    private Button button12;
    private Button button13;
    private Button button14;
    private Button button15;
    private Button button16;
    private Button button17;
    private Button button18;
    private Button button19;
    private Button button20;

    private List<Button> buttons;


    public WeekChooseAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Integer> weekList) {
        this.resourceId = resource;
        this.weekList = weekList;
        this.mContext = context;
        buttons = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return 1;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        myView = LayoutInflater.from(mContext).inflate(resourceId,null);
//        initView(myView);

        button1 = myView.findViewById(R.id.button1);
        button2 = myView.findViewById(R.id.button2);
        button3 = myView.findViewById(R.id.button3);
        button4 = myView.findViewById(R.id.button4);
        button5 = myView.findViewById(R.id.button5);
        button6 = myView.findViewById(R.id.button6);
        button7 = myView.findViewById(R.id.button7);
        button8 = myView.findViewById(R.id.button8);
        button9 = myView.findViewById(R.id.button9);
        button10  = myView.findViewById(R.id.button10);
        button11 = myView.findViewById(R.id.button11);
        button12 = myView.findViewById(R.id.button12);
        button13 = myView.findViewById(R.id.button13);
        button14 = myView.findViewById(R.id.button14);
        button15 = myView.findViewById(R.id.button15);
        button16 = myView.findViewById(R.id.button16);
        button17 = myView.findViewById(R.id.button17);
        button18 = myView.findViewById(R.id.button18);
        button19 = myView.findViewById(R.id.button19);
        button20 = myView.findViewById(R.id.button20);

        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
        buttons.add(button4);
        buttons.add(button5);
        buttons.add(button6);
        buttons.add(button7);
        buttons.add(button8);
        buttons.add(button9);
        buttons.add(button10);
        buttons.add(button11);
        buttons.add(button12);
        buttons.add(button13);
        buttons.add(button14);
        buttons.add(button15);
        buttons.add(button16);
        buttons.add(button17);
        buttons.add(button18);
        buttons.add(button19);
        buttons.add(button20);

        if(weekList.contains(1)){
            button1.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button1.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button1.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button1.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(1)){
                    weekList.remove(new Integer(1));
                    button1.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button1.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(1));
                    button1.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button1.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(2)){
            button2.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button2.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button2.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button2.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(2)){
                    weekList.remove(new Integer(2));
                    button2.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button2.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(2));
                    button2.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button2.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(3)){
            button3.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button3.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button3.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button3.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(3)){
                    weekList.remove(new Integer(3));
                    button3.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button3.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(3));
                    button3.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button3.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(4)){
            button4.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button4.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button4.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button4.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(4)){
                    weekList.remove(new Integer(4));
                    button4.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button4.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(4));
                    button4.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button4.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(5)){
            button5.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button5.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button5.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button5.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(5)){
                    weekList.remove(new Integer(5));
                    button5.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button5.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(5));
                    button5.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button5.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(6)){
            button6.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button6.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button6.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button6.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(6)){
                    weekList.remove(new Integer(6));
                    button6.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button6.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(6));
                    button6.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button6.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(7)){
            button7.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button7.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button7.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button7.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(7)){
                    weekList.remove(new Integer(7));
                    button7.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button7.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(7));
                    button7.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button7.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(8)){
            button8.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button8.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button8.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button8.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(8)){
                    weekList.remove(new Integer(8));
                    button8.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button8.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(8));
                    button8.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button8.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(9)){
            button9.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button9.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button9.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button9.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(9)){
                    weekList.remove(new Integer(9));
                    button9.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button9.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(9));
                    button9.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button9.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(10)){
            button10.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button10.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button10.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button10.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(10)){
                    weekList.remove(new Integer(10));
                    button10.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button10.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(10));
                    button10.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button10.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(11)){
            button11.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button11.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button11.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button11.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(11)){
                    weekList.remove(new Integer(11));
                    button11.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button11.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(11));
                    button11.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button11.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(12)){
            button12.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button12.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button12.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button12.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(12)){
                    weekList.remove(new Integer(12));
                    button12.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button12.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(12));
                    button12.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button12.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(13)){
            button13.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button13.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button13.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button13.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(13)){
                    weekList.remove(new Integer(13));
                    button13.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button13.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(13));
                    button13.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button13.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(14)){
            button14.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button14.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button14.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button14.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(14)){
                    weekList.remove(new Integer(14));
                    button14.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button14.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(14));
                    button14.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button14.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(15)){
            button15.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button15.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button15.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button15.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(15)){
                    weekList.remove(new Integer(15));
                    button15.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button15.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(15));
                    button15.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button15.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(16)){
            button16.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button16.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button16.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button16.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(16)){
                    weekList.remove(new Integer(16));
                    button16.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button16.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(16));
                    button16.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button16.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(17)){
            button17.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button17.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button17.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button17.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(17)){
                    weekList.remove(new Integer(17));
                    button17.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button17.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(17));
                    button17.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button17.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(18)){
            button18.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button18.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button18.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button18.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(18)){
                    weekList.remove(new Integer(18));
                    button18.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button18.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(18));
                    button18.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button18.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(19)){
            button19.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button19.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button19.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button19.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(19)){
                    weekList.remove(new Integer(19));
                    button19.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button19.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(19));
                    button19.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button19.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });
        if(weekList.contains(20)){
            button20.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            button20.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            button20.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            button20.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        button20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weekList.contains(20)){
                    weekList.remove(new Integer(20));
                    button20.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                    button20.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    weekList.add(new Integer(20));
                    button20.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    button20.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
        });



//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(weekList.contains(1)){
//                    weekList.remove(new Integer(1));
//                    button1.setBackgroundColor(mContext.getResources().getColor(R.color.green));
//                    button1.setTextColor(mContext.getResources().getColor(R.color.white));
//                } else {
//                    weekList.add(new Integer(1));
//                    button1.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
//                    button1.setTextColor(mContext.getResources().getColor(R.color.black));
//                }
//            }
//        });
//
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(weekList.contains(2)){
//                    weekList.remove(new Integer(2));
//                    button2.setBackgroundColor(mContext.getResources().getColor(R.color.green));
//                    button2.setTextColor(mContext.getResources().getColor(R.color.white));
//                } else {
//                    weekList.add(new Integer(2));
//                    button2.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
//                    button2.setTextColor(mContext.getResources().getColor(R.color.black));
//                }
//            }
//        });
//
//        button3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(weekList.contains(3)){
//                    weekList.remove(new Integer(3));
//                    button3.setBackgroundColor(mContext.getResources().getColor(R.color.green));
//                    button3.setTextColor(mContext.getResources().getColor(R.color.white));
//                } else {
//                    weekList.add(new Integer(3));
//                    button3.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
//                    button3.setTextColor(mContext.getResources().getColor(R.color.black));
//                }
//            }
//        });
//
//
//        button4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(weekList.contains(4)){
//                    weekList.remove(new Integer(4));
//                    button4.setBackgroundColor(mContext.getResources().getColor(R.color.green));
//                    button4.setTextColor(mContext.getResources().getColor(R.color.white));
//                } else {
//                    weekList.add(new Integer(4));
//                    button4.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
//                    button4.setTextColor(mContext.getResources().getColor(R.color.black));
//                }
//            }
//        });
//
//
//        button5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(weekList.contains(5)){
//                    weekList.remove(new Integer(5));
//                    button5.setBackgroundColor(mContext.getResources().getColor(R.color.green));
//                    button5.setTextColor(mContext.getResources().getColor(R.color.white));
//                } else {
//                    weekList.add(new Integer(5));
//                    button5.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
//                    button5.setTextColor(mContext.getResources().getColor(R.color.black));
//                }
//            }
//        });

//        for(int j = 0; j < 20; j++){
//            button = buttons.get(j);
//            final int finalJ = j;
//            Log.d(TAG, "getView: " + finalJ);
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(weekList.contains(finalJ +1)){
//                        button.setBackgroundColor(mContext.getResources().getColor(R.color.green));
//                        button.setTextColor(mContext.getResources().getColor(R.color.white));
//                    } else {
//                        button.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
//                        button.setTextColor(mContext.getResources().getColor(R.color.black));
//                    }
//                }
//            });
//
//        }
        return myView;
    }

    @SuppressLint("ResourceAsColor")
    private void initView(View myView) {

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


    public void onClick(View view) {
        for(int i = 0; i < 20; i++){
            Button button = buttons.get(i);
            Log.d(TAG, "onClick: button" + button.getId() + "view" + view.getId());
            if(button.getId() == view.getId()){
                if(weekList.contains(i+1)){
                    weekList.remove(new Integer(i+1));
                    button.setBackgroundColor(Color.parseColor("#CFCDCC"));
                    button.setTextColor(view.getResources().getColor(R.color.black));
                } else {
                    Log.d(TAG, "onClick: 点击了某按钮" + button.getText());
                    weekList.add(new Integer(i+1));
                    button.setTextColor(Color.WHITE);
                    button.setBackgroundResource(R.color.green);

                }
            }
        }
    }
}
