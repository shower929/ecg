package com.swm.core;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.swm.heart.R;

public class EngineeringActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView mEngineeringList;
    private EngineeringAdapter mEngineeringAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineering_main);
        mEngineeringList = (ListView) findViewById(R.id.swm_engineering_list);
        mEngineeringAdapter = new EngineeringAdapter();
        mEngineeringList.setAdapter(mEngineeringAdapter);
        mEngineeringList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ((EngineeringModel)mEngineeringAdapter.getItem(position)).target);
        startActivity(intent);
    }
}
