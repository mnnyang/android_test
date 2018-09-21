package cn.xxyangyoulin.courseview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CourseView mCourseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCourseView = findViewById(R.id.CourseView);

        mCourseView.addCourseBeforeInit(new Course(1, 2, 4, Color.GRAY).setText("y"));
        mCourseView.addCourseBeforeInit(new Course(4, 2, 6, Color.GRAY).setText("y"));
        mCourseView.addCourseBeforeInit(new Course(2, 2, 2, Color.GRAY).setText("y"));
        mCourseView.addCourseBeforeInit(new Course(5, 2, 8, Color.GRAY).setText("y"));

        System.out.println("----------获取的值：" + mCourseView.getRowItemWidth());


        mCourseView.setOnItemClickListener(new CourseView.OnItemClickListener() {
            @Override
            void onClick(List<Course> courses, View itemLayout) {
                System.out.println("click" + courses);
                super.onClick(courses, itemLayout);
            }
            int i=0;

            @Override
            void onAdd(Course course, View addView) {
                super.onAdd(course, addView);
                mCourseView.addCourseAfterInit(new Course(2, 2, 4, Color.DKGRAY).setText(""+i++));
                mCourseView.addCourseAfterInit(new Course(course.getRow(), 2, course.getCol(), Color.DKGRAY).setText(""+i++));
            }
        });


    }

    public void clean(View view) {
        mCourseView.resetView();
    }
}
