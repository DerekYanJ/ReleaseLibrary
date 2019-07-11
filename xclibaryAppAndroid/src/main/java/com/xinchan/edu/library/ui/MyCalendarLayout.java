package com.xinchan.edu.library.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.haibin.calendarview.CalendarLayout;

/**
 * @author derekyan
 * @desc
 * @date 2018/12/6
 */
public class MyCalendarLayout extends CalendarLayout {
    private CalendarExpandStatusChangeListener mCalendarExpandStatusChangeListener;

    public void setCalendarExpandStatusChangeListener(CalendarExpandStatusChangeListener mListener) {
        this.mCalendarExpandStatusChangeListener = mListener;
    }

    public MyCalendarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mCalendarExpandStatusChangeListener != null && event.getAction() == MotionEvent.ACTION_UP) //
            mCalendarExpandStatusChangeListener.OnExpandStatusChange(isExpand());
        return super.onTouchEvent(event);
    }

    public interface CalendarExpandStatusChangeListener{
        void OnExpandStatusChange(boolean isExpand);
    }

    @Override
    public boolean expand() {
        if(mCalendarExpandStatusChangeListener != null)
            mCalendarExpandStatusChangeListener.OnExpandStatusChange(true);
        return super.expand();
    }

    @Override
    public boolean shrink() {
        if(mCalendarExpandStatusChangeListener != null)
            mCalendarExpandStatusChangeListener.OnExpandStatusChange(false);
        return super.shrink();
    }
}
