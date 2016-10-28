/*
 *         Copyright (C) 2016-2017 宙斯
 *         All rights reserved
 *
 *        filename :Class4
 *        description :
 *
 *         created by jackzhous at  11/07/2016 12:12:12
 *         http://blog.csdn.net/jackzhouyu
 */
package com.jack.textview_viewgroup.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

/***********
 * author: jackzhous
 * file: TextViewGroup.java
 * create date: 2016/10/18 10:17
 * desc:
 ************/
public class TextViewGroup extends ViewGroup {

    private static final String TAG = "jackzhous";
    private static final int default_space = 30;                                                      //默认组件与组件之间的间隔

    private static final int expand_space = 200;                                                     //默认增大布局

    private int view_default_height = 0;

    private boolean isExpand = false;                                                               //组件是否已经扩展

    private int childCount = 0;
    private int parent_bottom, parent_width;                                                                        //当前组件的宽度，子组件都必须在这个宽度之内
    private int second_enter = 0;                                                                   //第一次进入
    private int moveUpDistance, moveDownDistance = 0;                                               //组件向上滑动和向下滑动的最大距离

    private Scroller mScroller;


    public TextViewGroup(Context context) {
        super(context);
        init(context);
    }

    public TextViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context){
        mScroller = new Scroller(context);
    }

    /**
     * 返回当前组件的默认布局参数，该组件下的子组件都会使用这个默认的LayoutParams
     * @param attrs
     * @return
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LinearLayout.LayoutParams(getContext(), attrs);
    }


    //计算组件及其子组件的宽高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        childCount = getChildCount();

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        view_default_height = (view_default_height == 0) ? sizeHeight : view_default_height;        //view_default_height用于保存组件的原始高度，后续改为扩张高度

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(sizeWidth, view_default_height);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        parent_bottom = b;
        parent_width = r;

        int childViewWidth = l;                                                                     //一行child宽度和，用于判断是否超出父的宽度
        int start_index = 0;
        int end_index = 0;
        int startX = 0;
        int startY = 0;
        int space;
        for(int i = 0; i < childCount; i++){
            View child = getChildAt(i);

            int sizeWidth = child.getMeasuredWidth();
            int sizeHeight = child.getMeasuredHeight();
            space = r - childViewWidth;                                                             //一行里面的剩余空间
            childViewWidth = childViewWidth + default_space + sizeWidth;                            //一行view的宽度和 = 组件宽度 + 间隔
            if(childViewWidth > r){
                end_index = i;
                onLayoutChildView(start_index, end_index, startX, startY, space);
                startY = startY + default_space + sizeHeight;
                childViewWidth = l + sizeWidth;
                start_index = i;
            }

        }

        /**
         * 说明还有一部分没有布局的child
         */
        if(end_index != childCount){
            onLayoutChildView(start_index, childCount, 0, startY, 0);
        }
        second_enter++;
    }

    /**
     * 布局一行的组件视图
     * @param start_index  其实child
     * @param end_index    结束child
     * @param start_x      x开始的位置
     * @param start_y      y开始的位置
     * @param space        一行剩余的空间
     */
    private void onLayoutChildView(int start_index, int end_index, int start_x, int start_y, int space){

        int endX = 0;
        int endY = 0;
        int sub_space = 0;                                                                           //需要将每行的剩余空间分摊到每个组件上去,减去30是一个选取的值，防止计算大小的精确问题，超出右边父的最长宽度
        if(space > 0){
            int view_numbers = end_index - start_index;
            sub_space = space/ view_numbers;
        }
        int i;
        for(i = start_index; i < end_index; i++){
            View child = getChildAt(i);
            endX = start_x + child.getMeasuredWidth() + sub_space;
            endY = start_y + child.getMeasuredHeight();
            if(second_enter < 2){                                                                   //分摊只需要在前两次进行分摊，后续不在分摊；因为后续布局都已经完成，再次分摊会照成重新获取padding值，该值会累加的
                int paddingLR = child.getPaddingLeft() + sub_space / 2;
                int paddingTB = child.getPaddingBottom();
                child.setPadding(paddingLR, paddingTB, paddingLR, paddingTB);
            }
                                                                                                    //每排最后一个必须等于右边限制的位置，对齐；除了最后一排单独几个那种
            if(i == end_index - 1 && space != 0){
                endX = parent_width;
            }
            child.layout(start_x, start_y, endX, endY);

            start_x = endX + default_space;
        }
        if(i == childCount){                                                                        //最后一行时，计算父组件最大值和child最下面的Y值，计算差值作为向上滑动的最大距离
            moveUpDistance = endY - parent_bottom + default_space + 40;
        }

    }

    /**
     * 其他组件控制该组件的发大于缩小接口
     * 放大或者缩小组件
     * 放大状态下可以进行滑动操作
     */
    public void expandOrClose(){
        if(!isExpand){
            view_default_height = view_default_height + expand_space;
            isExpand = true;
        }else{
            isExpand = false;
            view_default_height = view_default_height - expand_space;
        }
        requestLayout();
        postInvalidate();
    }

    private int downY;
    private int moveY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //只有在展开的情况下才能进行滑动操作

        if(!isExpand){
            return super.onTouchEvent(event);
        }

        int action = event.getAction();

        switch (action){
            case MotionEvent.ACTION_DOWN:
                downY = (int)event.getY();
                break;

            case MotionEvent.ACTION_MOVE:

                moveY = (int)event.getY();

                int dy = downY - moveY;                                                             //需要移动的距离
                int need_move_y = getScrollY() + dy;                                                //getScrollY()会得到一个距离值，该距离值=原始组件位置和偏移后组件的差值
                if(need_move_y < 0){
                    scrollTo(0, 0);
                }else if (need_move_y > moveUpDistance){
                    scrollTo(0, moveUpDistance);
                }else{
                    scrollBy(0, dy);
                }
                downY = moveY;
                break;

            case MotionEvent.ACTION_UP:
                break;

            default:
                break;
        }
        return true;
    }


}
