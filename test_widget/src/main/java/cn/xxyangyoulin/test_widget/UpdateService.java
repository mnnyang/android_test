package cn.xxyangyoulin.test_widget;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import cn.xxyangyoulin.test_widget.custom.Course;

public class UpdateService extends RemoteViewsService {

    private List<Course> mCourses;
    private int MaxCourse = 8;

    @Override
    public void onCreate() {
        super.onCreate();
        initDemoData();
    }

    private void initDemoData() {
        mCourses = new ArrayList<>();
        mCourses.add(new Course(1, 1, 1, Color.GRAY));
        mCourses.add(new Course(1, 1, 6, Color.GRAY));
        mCourses.add(new Course(1, 2, 3, Color.RED));
        mCourses.add(new Course(2, 2, 5, Color.GREEN));
        mCourses.add(new Course(2, 1, 1, Color.BLUE));
        mCourses.add(new Course(1, 2, 5, Color.BLACK));
        mCourses.add(new Course(3, 3, 5, Color.BLACK));
        mCourses.add(new Course(3, 3, 5, Color.BLACK));
        mCourses.add(new Course(3, 2, 5, Color.BLACK).setActiveStatus(false));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private final Context mContext;
        private final List<String> mList;

        public ListRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mList = MyWidget.getList();

            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
        }

        @Override
        public void onDestroy() {
            mList.clear();
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            System.out.println("----------" + position);
            final RemoteViews bigRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.list_demo_item);
            bigRemoteViews.removeAllViews(R.id.item_weekday_day_1);
            bigRemoteViews.removeAllViews(R.id.item_weekday_day_2);
            bigRemoteViews.removeAllViews(R.id.item_weekday_day_3);


            Intent intent = new Intent();
            //TODO
            //intent.setComponent(new ComponentName("包名", "类名"));
            //与CustomWidget中remoteViews.setPendingIntentTemplate配对使用
            bigRemoteViews.setOnClickFillInIntent(R.id.widget_list_item_layout, intent);

            int j = 0;
            for (int i = 1; i <= 3; i++) {
                for (Course cours : mCourses) {
                    if (cours.getRow() == i) {
                        RemoteViews dayRemoteViews = null;
                        switch (cours.getRowNum()) {
                            case 1:
                                dayRemoteViews = new RemoteViews(getPackageName(), R.layout.widget_box_1);
                                dayRemoteViews.setTextViewText(R.id.widget_box_1, "第一行" + j++);
                                break;
                            case 2:
                                dayRemoteViews = new RemoteViews(getPackageName(), R.layout.widget_box_2);
                                dayRemoteViews.setTextViewText(R.id.widget_box_2, "第一行" + j++);
                                break;
                            case 3:
                                dayRemoteViews = new RemoteViews(getPackageName(), R.layout.widget_box_3);
                                dayRemoteViews.setTextViewText(R.id.widget_box_3, "第一行" + j++);
                                break;
                        }

                        switch (i) {
                            case 1:
                                bigRemoteViews.addView(R.id.item_weekday_day_1, dayRemoteViews);
                                break;
                            case 2:
                                bigRemoteViews.addView(R.id.item_weekday_day_2, dayRemoteViews);
                                break;
                            case 3:
                                bigRemoteViews.addView(R.id.item_weekday_day_3, dayRemoteViews);
                                break;

                        }
                    }
                }
            }


            return bigRemoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
