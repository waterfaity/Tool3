package com.waterfairy.tool3.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.waterfairy.tool3.R;


public class RecyclerViewActivity extends AppCompatActivity {

    private static final String TAG = "recycler";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view2);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//        recyclerView.setItemAnimator(new SlideInDownAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        HaAdapter myAdapter = new HaAdapter(this, Test.getData());
        myAdapter.setResIds(new int[]{R.layout.item_1, R.layout.item2, R.layout.item3, R.layout.item4, R.layout.item5, R.layout.item6});
        recyclerView.setAdapter(myAdapter);
    }


}
