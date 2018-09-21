package cn.xxyangyoulin.test_widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.orhanobut.logger.Logger;

public class MyWidget extends AppWidgetProvider {
    /** AppWidgetProvider 继承自 BroadcastReceiver */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Logger.d("onReceive");
    }

    /**
     * 根据 updatePeriodMillis 定义的定期刷新操作会调用该函数，此外当用户添加 Widget 时
     * 也会调用该函数，可以在这里进行必要的初始化操作。但如果在<appwidget-provider>
     * 中声明了 android:configure 的 Activity，在用户添加 Widget 时，不会调用 onUpdate()，
     * 需要由 configure Activity 去负责去调用
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Logger.d("onUpdate");

    }

    /** onDeleted()：当 Widget 被删除时调用该方法。 */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Logger.d("onDeleted");

    }

    /**
     * 当 Widget 第一次被添加时调用，例如用户添加了两个你的 Widget，
     * 那么只有在添加第一个 Widget 时该方法会被调用。
     * 所以该方法比较适合执行你所有 Widgets 只需进行一次的操作。
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Logger.d("onEnabled");

    }

    /**
     * 与 onEnabled 恰好相反，当你的最后一个 Widget 被删除时调用该方法，
     * 所以这里用来清理之前在 onEnabled() 中进行的操作。
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Logger.d("onDisabled");

    }

    /**
     * 当 Widget 第一次被添加或者大小发生变化时调用该方法，
     * 可以在此控制 Widget 元素的显示和隐藏。
     */
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Logger.d("onAppWidgetOptionsChanged");

    }
}

/**
 * 测试结果：
 * 第一次添加：
 * onEnabled    onUpdate
 * 第二次以后添加：
 * onUpdate
 * <p>
 * <p>
 * 当前有多个删除第一个：
 * onDeleted
 * 删除最后一个：
 * onDeleted   onDisabled
 */
