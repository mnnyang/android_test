package cn.xxyangyoulin.courseview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

public class CourseView extends FrameLayout {

    private int mWidth;
    private int mHeight;

    private int mRowCount = 7;
    private int mColCount = 12;

    private int mRowItemWidth = dip2px(50);
    private int mColItemHeight = dip2px(60);
    private boolean mRowItemWidthAuto = true;

    int mTableTags[][];

    List<Course> mCourseList = new ArrayList<>();
    private Paint mLinePaint;
    private boolean childDrawTag;
    private float mCourseItemRadius = 0;

    public CourseView(@NonNull Context context) {
        super(context);
    }

    public CourseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
        initPaint();
    }

    private void initPaint() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStrokeWidth(5);
        mLinePaint.setStyle(Paint.Style.STROKE);
    }

    private void init() {
        mCourseList.add(new Course(1, 1, 1,Color.RED));
        mCourseList.add(new Course(3, 2, 2,Color.GREEN));
        mCourseList.add(new Course(4, 3, 3,Color.GRAY));
        mCourseList.add(new Course(5, 2, 4,Color.YELLOW));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = mColItemHeight * mColCount;

        if (mRowItemWidthAuto) {
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
            mRowItemWidth = mWidth / mRowCount;
        } else {
            mWidth = mRowItemWidth * mRowCount;
        }

        int widthResult = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY);
        int heightResult = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);

        setMeasuredDimension(widthResult, heightResult);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private void addCourseItemView() {
        for (Course course : mCourseList) {
            View itemView = createItemView(course);

            System.out.println(course.getCol());

            LayoutParams params = new LayoutParams(mRowItemWidth,
                    mColItemHeight * course.getRowNum());

            params.leftMargin = (course.getRow() - 1) * mRowItemWidth;
            params.topMargin = (course.getCol() - 1) * mColItemHeight;

            itemView.setLayoutParams(params);
            addView(itemView);
        }
    }

    private View createItemView(Course course) {
        final LinearLayout bgLayout = new LinearLayout(getContext());
        bgLayout.setOrientation(VERTICAL);

        //TextView
        TextView tv = getCourseTextView(mColItemHeight * course.getRowNum(), mRowItemWidth);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(10, 10, 10, 10);
        tv.setLayoutParams(params);
        tv.setText("Text");

        tv.setLineSpacing(-2, 1);
        tv.setTextColor(Color.BLUE);

        bgLayout.addView(tv);


        setCurBg(course, tv);
        tv.setClickable(true);
        tv.setFocusable(true);

        return bgLayout;
    }

    private void setCurBg(Course course, View itemView) {
        StateListDrawable drawable;
        drawable = getShowBgDrawable(course.getColor(), course.getColor() & 0x80FFFFFF);
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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, h);
        tv.setLayoutParams(params);
        //bold
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);
        return tv;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        drawLine(canvas);
        if (!childDrawTag) {
            addCourseItemView();
            childDrawTag = true;
        }
    }

    private void drawLine(Canvas canvas) {
        for (int i = 1; i < mRowCount; i++) {
            canvas.drawLine(i * mRowItemWidth, 0, i * mRowItemWidth, mHeight, mLinePaint);
        }

        for (int i = 1; i < mColCount; i++) {
            canvas.drawLine(0, i * mColItemHeight, mWidth, i * mColItemHeight, mLinePaint);
        }
    }

    public int dip2px(float dpValue) {
        return (int) (0.5f + dpValue * getContext().getResources().getDisplayMetrics().density);
    }
}
