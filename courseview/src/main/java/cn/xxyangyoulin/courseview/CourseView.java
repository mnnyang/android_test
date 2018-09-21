package cn.xxyangyoulin.courseview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CourseView extends FrameLayout {

    private int mWidth;
    private int mHeight;

    private int mRowCount = 7;
    private int mColCount = 12;

    private int mRowItemWidth = dip2px(50);
    private int mColItemHeight = dip2px(60);

    private int currentIndex = 1;

    /** 行item的宽度根据view的总宽度自动平均分配 */
    private boolean mRowItemWidthAuto = true;

    List<Course> mCourseList = new ArrayList<>();

    private Course mAddTagCourse;
    private View mAddTagCourseView;

    /** item view radius */
    private float mCourseItemRadius = 0;

    private Paint mLinePaint;
    private Path mLinePath = new Path();

    /** 显示垂直分割线 */
    private boolean mShowVerticalLine = false;

    /** 显示水平分割线 */
    private boolean mShowHorizontalLine = true;

    /** 第一次绘制 */
    private boolean mFirstDraw;

    /** text padding value */
    private int mTextLRPadding = dip2px(2);
    private int mTextTBPadding = dip2px(2);
    private int mTextColor = Color.WHITE;

    /** 不活跃的背景 */
    private int mInactiveColor = 0xFF909090;
    private int textTBMargin = dip2px(3);
    private int textLRMargin = dip2px(3);

    public CourseView(@NonNull Context context) {
        super(context);
    }

    public CourseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        l("调用init");
        initPaint();
    }

    private void initPaint() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStrokeWidth(1);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        l("调用onMeasure");

        mHeight = mColItemHeight * mColCount;

        if (mRowItemWidthAuto) {
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
            mRowItemWidth = mWidth / mRowCount;
        } else {
            mWidth = mRowItemWidth * mRowCount;
        }

        /*初始化网格记录数组*/

        int widthResult = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY);
        int heightResult = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);

        setMeasuredDimension(widthResult, heightResult);
    }

    /** 把数组中的数据全部添加到界面 */
    private void initCourseItemView() {
        l("调用initCourseItemView");

        for (Course course : mCourseList) {
            realAddCourseItemView(course);
        }
    }

    /** 在界面初始化之前添加数据 */
    public void addCourseBeforeInit(Course course) {
        if (course == null) {
            return;
        }

        if (!mCourseList.contains(course)) {
            mCourseList.add(course);
        }
    }

    /** 在界面初始化之后添加数据 */
    public void addCourseAfterInit(Course course) {
        if (course == null) {
            return;
        }

        if (!mCourseList.contains(course)) {
            mCourseList.add(course);
            realAddCourseItemView(course);
        }
    }

    private void realAddCourseItemView(Course course) {

        View itemView = createItemView(course);

        LayoutParams params = new LayoutParams(mRowItemWidth,
                mColItemHeight * course.getRowNum());

        params.leftMargin = (course.getRow() - 1) * mRowItemWidth;
        params.topMargin = (course.getCol() - 1) * mColItemHeight;

        itemView.setLayoutParams(params);

        if (!course.isShowVisiable()) {
            return;
        }

        if (course.getActiveStatus()) {
            addView(itemView);
        } else {
            addView(itemView, 0);
        }
    }

    private void setItemViewBackground(Course course, View itemView) {
        StateListDrawable drawable;

        if (course.getActiveStatus()) {
            drawable = getShowBgDrawable(course.getColor(), course.getColor() & 0x80FFFFFF);
        } else {
            drawable = getShowBgDrawable(mInactiveColor, mInactiveColor & 0x80FFFFFF);
        }
        itemView.setBackground(drawable);
    }

    private StateListDrawable getShowBgDrawable(int color, int color2) {
        StateListDrawable drawable;
        drawable = Utils.getPressedSelector(getContext(),
                color, color2, mCourseItemRadius);
        return drawable;
    }

    @NonNull
    private TextView getCourseTextView(int h, int w) {
        TextView tv = new TextView(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(w, h);
        tv.setLayoutParams(params);

        tv.setTextColor(mTextColor);
        //bold
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);
        return tv;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        drawLine(canvas);

        if (!mFirstDraw) {
            initCourseItemView();
            mFirstDraw = true;
        }
    }

    private void drawLine(Canvas canvas) {
        //横线
        if (mShowHorizontalLine) {
            for (int i = 1; i < mColCount; i++) {
                mLinePath.reset();
                mLinePath.moveTo(0, i * mColItemHeight);
                mLinePath.lineTo(mWidth, i * mColItemHeight);
                canvas.drawPath(mLinePath, mLinePaint);
            }
        }

        //竖线
        if (mShowVerticalLine) {
            for (int i = 1; i < mRowCount; i++) {
                mLinePath.reset();
                mLinePath.moveTo(i * mRowItemWidth, 0);
                mLinePath.lineTo(i * mRowItemWidth, mHeight);
                canvas.drawPath(mLinePath, mLinePaint);
            }
        }
    }

    /*事件*/
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("COurseView", "onTouchEvent");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                System.out.println(event.getX() + "-d--" + event.getY());
                return true; //TODO why?
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("up");
                addTagCourseView((int) event.getX(), (int) event.getY());
                break;
        }

        return super.onTouchEvent(event);
    }

    private void addTagCourseView(int x, int y) {

        /*找到点击的方框坐标*/
        int x1 = x / mRowItemWidth + 1;
        int y1 = y / mColItemHeight + 1;

        if (x1 > mRowCount) x1 = mRowCount;

        if (y1 > mColCount) y1 = mColCount;

        if (mAddTagCourse == null)
            mAddTagCourse = new Course();

        if (mAddTagCourseView == null)
            mAddTagCourseView = createAddTagView();
        else removeView(mAddTagCourseView);

        mAddTagCourse.setRow(x1);
        mAddTagCourse.setCol(y1);

        realAddTagCourseView();
    }

    /**
     * 移除添加按钮
     */
    public void removeAddTagView() {
        if (mAddTagCourseView != null) {
            removeView(mAddTagCourseView);
        }
    }

    public void resetView() {
        removeAllViews();
        initCourseItemView();
    }

    /**
     * 建立添加按钮
     */
    private View createAddTagView() {
        final StopEventView bgLayout = new StopEventView(getContext());

        ImageView iv = new ImageView(getContext());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(textLRMargin, textTBMargin, textLRMargin, textTBMargin);
        iv.setLayoutParams(params);

        iv.setImageResource(R.drawable.ic_add_black_24dp);
        iv.setScaleType(ImageView.ScaleType.CENTER);
        iv.setBackgroundColor(Color.GRAY);
        iv.setClickable(true);
        iv.setFocusable(true);

        iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onAdd(mAddTagCourse, mAddTagCourseView);
                    removeAddTagView();
                }
            }
        });

        bgLayout.addView(iv);
        return bgLayout;
    }

    /**
     * 建立itemview
     */
    @SuppressLint("ClickableViewAccessibility")
    private View createItemView(final Course course) {
        final StopEventView bgLayout = new StopEventView(getContext());
        //TextView
        final TextView tv = getCourseTextView(mColItemHeight * course.getRowNum(), mRowItemWidth);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        int tag1 = course.getActiveStatus() ? 1 : -1;
        int tag2 = tag1 == -1 ? 2 : 1;
        params.setMargins(textLRMargin, textTBMargin, textLRMargin, textTBMargin);
        //params.setMargins(textLRMargin * tag2, textTBMargin * tag2,
        //        tag1 * textLRMargin, tag1 * textTBMargin);

        tv.setLayoutParams(params);

        tv.setText(course.getText());
        tv.setLineSpacing(-2, 1);
        tv.setPadding(mTextLRPadding, mTextTBPadding, mTextLRPadding, mTextTBPadding);
        tv.setTextColor(mTextColor);

        bgLayout.addView(tv);

        setItemViewBackground(course, tv);
        tv.setClickable(true);
        tv.setFocusable(true);

        itemEvent(course, bgLayout, tv);

        return bgLayout;
    }

    private void itemEvent(final Course course, final ViewGroup viewGroup, TextView textView) {
        final List<Course> courses = new ArrayList<>();
        courses.add(course);

        /*查找在点击的item范围内重叠的item*/
        for (Course findCourse : mCourseList) {
            if (findCourse.getRow() == course.getRow()
                    && course != findCourse) {

                if (findCourse.getCol() < course.getCol() + course.getRowNum()
                        && findCourse.getCol() >= course.getCol()) {
                    courses.add(findCourse);
                }
            }
        }

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onClick(courses, viewGroup);
                }
            }
        });

        textView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onLongClick(courses, viewGroup);
                    return true;
                }
                return false;
            }
        });
    }

    private void realAddTagCourseView() {
        LayoutParams params = new LayoutParams(mRowItemWidth,
                mColItemHeight * mAddTagCourse.getRowNum());

        params.leftMargin = (mAddTagCourse.getRow() - 1) * mRowItemWidth;
        params.topMargin = (mAddTagCourse.getCol() - 1) * mColItemHeight;

        mAddTagCourseView.setLayoutParams(params);
        addView(mAddTagCourseView);
    }

    private OnItemClickListener mItemClickListener;

    public static class OnItemClickListener {
        void onClick(List<Course> course, View itemView) {
        }

        void onLongClick(List<Course> course, View itemView) {
        }

        void onAdd(Course course, View addView) {
        }

        /** 活跃状态的item占用了相同的网格，将会重叠显示 */
        void onGridOccupationError(Course... courses) {

        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public int dip2px(float dpValue) {
        return (int) (0.5f + dpValue * getContext().getResources().getDisplayMetrics().density);
    }

    public static void l(String s) {
        System.out.println("------------------" + s);
    }

    /***************************************************/

    public int getRowCount() {
        return mRowCount;
    }

    public CourseView setRowCount(int rowCount) {
        mRowCount = rowCount;
        return this;
    }

    public int getColCount() {
        return mColCount;
    }

    public CourseView setColCount(int colCount) {
        mColCount = colCount;
        return this;
    }

    public int getRowItemWidth() {
        return mRowItemWidth;
    }

    public CourseView setRowItemWidth(int rowItemWidth) {

        mRowItemWidth = rowItemWidth;
        return this;
    }

    public int getColItemHeight() {
        return mColItemHeight;
    }

    public CourseView setColItemHeight(int colItemHeight) {
        mColItemHeight = colItemHeight;
        return this;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public CourseView setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        return this;
    }

    public boolean isRowItemWidthAuto() {
        return mRowItemWidthAuto;
    }

    public CourseView setRowItemWidthAuto(boolean rowItemWidthAuto) {
        mRowItemWidthAuto = rowItemWidthAuto;
        return this;
    }

    public float getCourseItemRadius() {
        return mCourseItemRadius;
    }

    public CourseView setCourseItemRadius(float courseItemRadius) {
        mCourseItemRadius = courseItemRadius;
        return this;
    }

    public boolean isShowVerticalLine() {
        return mShowVerticalLine;
    }

    public CourseView setShowVerticalLine(boolean showVerticalLine) {
        mShowVerticalLine = showVerticalLine;
        return this;
    }

    public boolean isShowHorizontalLine() {
        return mShowHorizontalLine;
    }

    public CourseView setShowHorizontalLine(boolean showHorizontalLine) {
        mShowHorizontalLine = showHorizontalLine;
        return this;
    }

    public int getTextLRPadding() {
        return mTextLRPadding;
    }

    public CourseView setTextLRPadding(int textLRPadding, int textTBPadding) {
        mTextLRPadding = textLRPadding;
        mTextTBPadding = textTBPadding;

        return this;
    }

    public int getTextTBPadding() {
        return mTextTBPadding;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public CourseView setTextColor(int textColor) {
        mTextColor = textColor;
        return this;
    }

    public int getInactiveColor() {
        return mInactiveColor;
    }

    public CourseView setInactiveColor(int inactiveColor) {
        mInactiveColor = inactiveColor;
        return this;
    }

    public CourseView setTextTBMargin(int textTBMargin, int textLRMargin) {
        this.textTBMargin = textTBMargin;
        this.textLRMargin = textLRMargin;
        return this;
    }

    public int getTextTBMargin() {
        return textTBMargin;
    }

    public int getTextLRMargin() {
        return textLRMargin;
    }
}
