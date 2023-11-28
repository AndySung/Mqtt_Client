package com.soft.nice.mqtt_client.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/***
 * 此为自定义Listview，作用是为了解决 listview 和 scrollview 同用冲突
 * */
public class MyListView extends ListView {
    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public MyListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,//右移运算符，相当于除于4
                MeasureSpec.AT_MOST);//测量模式取最大值
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);//重新测量高度
    }

    /*float preY = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preY = ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (slideToTheTop(ev) || slideToTheBottom(ev))
                    getParent().requestDisallowInterceptTouchEvent(false);
                else
                    getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }*/

    /**
     * 当第一个可见item为0且手势为向下滑动且全部露出
     * @param ev
     * @return
     */
    /*private boolean slideToTheTop(MotionEvent ev) {
        return getFirstVisiblePosition() == 0 && getChildAt(0).getTop() == 0 && ev.getY() - preY > 0;
    }*/

    /**
     * 最后一个可见item为全部item最后一个且手势向上滑动且全部露出
     * @param ev
     * @return
     */
    /*private boolean slideToTheBottom(MotionEvent ev) {
        return getLastVisiblePosition() == getCount() - 1 && getChildAt(getChildCount() - 1).getBottom() == getHeight() && ev.getY() - preY < 0;
    }*/
}