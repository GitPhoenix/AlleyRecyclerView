package com.alley.alley.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.alley.alley.R;
import com.alley.alley.adapter.SwipeMenuAdapter;
import com.alley.alley.widget.RenewDownView;
import com.alley.alley.widget.RenewUpView;
import com.alley.rv.base.BaseRVAdapter;
import com.alley.rv.decoration.LinearDividerItemDecoration;
import com.alley.rv.widget.AlleyRecyclerView;
import com.alley.rv.widget.AlleySwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;


public class SwipeMenuActivity extends AppCompatActivity implements AlleyRecyclerView.OnDataRenewListener {
    private AlleySwipeRecyclerView recyclerView;
    private SwipeMenuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_menu);
        recyclerView = (AlleySwipeRecyclerView) findViewById(R.id.AlleySwipeRecyclerView);

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
                Toast.makeText(SwipeMenuActivity.this, "" + position, Toast.LENGTH_LONG).show();
            }
        });

        adapter.setOnItemChildClickListener(new BaseRVAdapter.OnRecyclerViewItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseRVAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.btOpen:
                        Toast.makeText(SwipeMenuActivity.this,"show open",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btDelete:
                        Toast.makeText(SwipeMenuActivity.this,"show delete",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btFavorite:
                        Toast.makeText(SwipeMenuActivity.this,"show Favorite",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btGood:
                        Toast.makeText(SwipeMenuActivity.this,"show good",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.image_iv:
                        Toast.makeText(SwipeMenuActivity.this,"show image",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void initRecyclerView(List<String> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new LinearDividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setRenewDownEnable(true);
        recyclerView.setRenewUpEnable(true);
        recyclerView.setRenewDownView(new RenewDownView(this));
        recyclerView.setRenewUpView(new RenewUpView(this));
        recyclerView.setSwipeDirection(AlleySwipeRecyclerView.DIRECTION_LEFT);
        recyclerView.addOnDataRenewListener(this);

        adapter = new SwipeMenuAdapter(this, list);
        recyclerView.setAdapter(adapter);
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
