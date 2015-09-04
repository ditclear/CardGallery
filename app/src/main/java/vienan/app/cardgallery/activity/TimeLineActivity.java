package vienan.app.cardgallery.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import vienan.app.cardgallery.R;
import vienan.app.cardgallery.adapter.StatusExpandAdapter;
import vienan.app.cardgallery.model.CardModel;
import vienan.app.cardgallery.model.ChildStatusEntity;
import vienan.app.cardgallery.model.GroupStatusEntity;

/**
 * Created by vienan on 2015/9/2.
 */
public class TimeLineActivity extends Activity {

    private ExpandableListView expandlistView;
    private StatusExpandAdapter statusAdapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        context = this;
        expandlistView = (ExpandableListView) findViewById(R.id.expandlist);
        initExpandListView();
    }

    /**
     * 初始化可拓展列表
     */
    private void initExpandListView() {
        statusAdapter = new StatusExpandAdapter(context, getListData());
        expandlistView.setAdapter(statusAdapter);
        expandlistView.setGroupIndicator(null); // 去掉默认带的箭头
        expandlistView.setSelection(0);// 设置默认选中项

        // 遍历所有group,将所有项设置成默认展开
        int groupCount = expandlistView.getCount();
        for (int i = 0; i < groupCount; i++) {
            expandlistView.expandGroup(i);
        }

        expandlistView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // TODO Auto-generated method stub
                String date=statusAdapter.getGroupList().get(groupPosition).getGroupName();
                Intent intent=new Intent(TimeLineActivity.this,MainActivity.class);
                intent.putExtra("date",date);
                startActivity(intent);
                return true;
            }
        });
    }

    private List<GroupStatusEntity> getListData() {
        List<GroupStatusEntity> groupList;
        String[] strArray =getCreateDate();
        List<CardModel> titles=null;

        groupList = new ArrayList<GroupStatusEntity>();
        for (int i = strArray.length-1; i>=0 ; i--) {
            GroupStatusEntity groupStatusEntity = new GroupStatusEntity();
            groupStatusEntity.setGroupName(strArray[i]);
            titles = new Select().from(CardModel.class).where("date=?",new Object[]{strArray[i]}).execute();
            List<ChildStatusEntity> childList = new ArrayList<ChildStatusEntity>();
            for (int j = 0; j < titles.size(); j++) {
                ChildStatusEntity childStatusEntity = new ChildStatusEntity();
                childStatusEntity.setCompleteTime(titles.get(j).title);
                childStatusEntity.setIsfinished(true);
                childList.add(childStatusEntity);
            }

            groupStatusEntity.setChildList(childList);
            groupList.add(groupStatusEntity);
        }
        return groupList;
    }
    private List<CardModel> getAll() {
        return new Select()
                .from(CardModel.class)
                .execute();
    }

    public String[] getCreateDate() {

        List<CardModel> lists=new Select()
                .from(CardModel.class)
                .groupBy("date").execute();
        String[] dates=new String[lists.size()];
        for (int i = 0; i < lists.size(); i++) {
            dates[i]=lists.get(i).date;
        }
        return dates;
    }
    /**
     * 点击返回键退出程序
     */
    private static Boolean isExit = false;
    private Handler mHandler = new Handler();
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isExit == false) {
                isExit = true;
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
            } else {
                finish();
                System.exit(0);
            }
        }
        return false;
    }

}