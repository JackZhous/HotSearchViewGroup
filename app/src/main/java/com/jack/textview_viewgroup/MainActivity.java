
package com.jack.textview_viewgroup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jack.textview_viewgroup.view.TextViewGroup;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {

    private TextViewGroup viewGroup;

    public ArrayList<String> list = new ArrayList<>();

    private TextView btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn = (TextView)findViewById(R.id.more);
        list.add("极乐净土");
        list.add("ppap");
        list.add("法医秦明");
        list.add("一年生");
        list.add("舞动采访间");
        list.add("吃货木下");
        list.add("不可抗力");
        list.add("暴走大事件");
        list.add("守望先锋");
        list.add("夏目友人帐");
        list.add("你的名字");
        list.add("张继科");
        list.add("蓝瘦香菇");
        list.add("主播炸了");
        list.add("主播真会玩");
        list.add("西部世界");
        list.add("蜡笔小新");
        list.add("刺客列传");

        list.add("麻雀");
        list.add("阴阳师");
        list.add("大胃王密子君");
        list.add("敖厂长");
        list.add("谷阿莫");
        list.add("起小点");
        list.add("釜山行");
        list.add("火隐忍者");
        list.add("唔冻");
        list.add("逗鱼时刻");
        list.add("镇魂街");
        list.add("日剧");
        list.add("snh48");
        list.add("杨洋");
        list.add("错生");
        list.add("老e");
        list.add("fate");
        list.add("徐老师来巡山");


        init_layout_resource();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGroup.expandOrClose();
            }
        });
    }

    private void init_layout_resource(){
        viewGroup = (TextViewGroup)findViewById(R.id.viewgroup);
        int index = 0;
        for (String str : list){
            Log.i("jackzhous", "--- " + str);
            TextView textView = new TextView(this);
            textView.setText(str);
            textView.setTextSize(15);

            textView.setBackground(getResources().getDrawable(R.drawable.text_view_bg));
            textView.setTag(str);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "" + v.getTag(), Toast.LENGTH_SHORT).show();
                }
            });

            viewGroup.addView(textView, index);
            index++;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}
