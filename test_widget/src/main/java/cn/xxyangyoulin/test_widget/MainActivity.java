package cn.xxyangyoulin.test_widget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Logger初始化的时候，将上面新建的类实例化，并设置到Logger中
        PrettyFormatStrategy strategy = PrettyFormatStrategy.newBuilder()
                .logStrategy(new LogCatStrategy())
                .tag("TAG")//（可选）这里也可以设置全局TAG
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(strategy));



        Logger.d("调用Create");
    }

    public class LogCatStrategy implements LogStrategy {

        @Override
        public void log(int priority, String tag, String message) {
            Log.println(priority, randomKey() + tag, message);
        }

        private int last;

        private String randomKey() {
            int random = (int) (10 * Math.random());
            if (random == last) {
                random = (random + 1) % 10;
            }
            last = random;
            return String.valueOf(random);
        }
    }

}
