package com.alley.alley.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alley.alley.R;
import com.alley.alley.adapter.AnimationAdapter;
import com.alley.alley.widget.RenewDownView;
import com.alley.alley.widget.RenewUpView;
import com.alley.rv.base.BaseRVAdapter;
import com.alley.rv.decoration.GridDividerItemDecoration;
import com.alley.rv.widget.AlleyRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AlleyRecyclerView.OnDataRenewListener {
    private AlleyRecyclerView recyclerView;
    private AnimationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (AlleyRecyclerView) findViewById(R.id.AlleyRecyclerView_main);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            list.add("animation " + i);
        }
        initRecyclerView(list);

        initEvent();
    }

    private void initEvent() {
        adapter.setOnItemClickListener(new BaseRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object item, int position) {
                Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_LONG).show();
                if (position == 0) {
                    Intent intent = new Intent(MainActivity.this, SwipeMenuActivity.class);
                    startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(MainActivity.this, DragActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void initRecyclerView(List<String> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridDividerItemDecoration(this));
        recyclerView.setRenewDownEnable(true);
        recyclerView.setRenewUpEnable(true);
        recyclerView.setRenewDownView(new RenewDownView(this));
        recyclerView.setRenewUpView(new RenewUpView(this));
        recyclerView.addOnDataRenewListener(this);

        adapter = new AnimationAdapter(this, list);
//        adapter.setItemAnimator(new XAnimation());//设置显示的动画
//        adapter.setItemAnimationRepeat(true);
        recyclerView.setAdapter(adapter);

        View headerView = getLayoutInflater().inflate(R.layout.view_header_layout, (ViewGroup) recyclerView.getParent(), false);
        View headerView2 = getLayoutInflater().inflate(R.layout.view_header_layout, (ViewGroup) recyclerView.getParent(), false);
        View footerView = getLayoutInflater().inflate(R.layout.view_footer_layout, (ViewGroup) recyclerView.getParent(), false);
//        adapter.addHeaderView(headerView);
//        adapter.addHeaderView(headerView2);
//        adapter.addFooterView(footerView);
    }

    @Override
    public void onRenewDown() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                recyclerView.endRenewDown();
            }
        }, 2 * 1000);
    }

    @Override
    public void onRenewUp() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                recyclerView.endRenewUp();
            }
        }, 1 * 1000);
    }
}
