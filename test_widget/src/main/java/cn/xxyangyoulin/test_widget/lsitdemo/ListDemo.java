package cn.xxyangyoulin.test_widget.lsitdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import cn.xxyangyoulin.test_widget.R;

public class ListDemo extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_demo);

        mListView = findViewById(R.id.list_view);

        mListView.setAdapter(new Adapter());
    }


    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 11;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.list_demo_item, null);
                //LinearLayout day1 = convertView.findViewById(R.id.item_weekday_day_0);
                //LinearLayout day2 = convertView.findViewById(R.id.item_weekday_day_0);
                //LinearLayout day3 = convertView.findViewById(R.id.item_weekday_day_0);

                 holder = new Holder();
                //holder.day1 = day1;
                //holder.day2 = day2;
                //holder.day3 = day3;

                convertView.setTag(holder);
            }else{
                holder = (Holder) convertView.getTag();
            }

            if (position == 2){
                holder.day2.setPadding(0,0,0,-300);
            }

            return convertView;
        }
    }

    static class Holder {
        LinearLayout day1;
        LinearLayout day2;
        LinearLayout day3;
    }
}
