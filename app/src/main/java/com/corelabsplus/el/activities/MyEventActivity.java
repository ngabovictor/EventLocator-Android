package com.corelabsplus.el.activities;

import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.corelabsplus.el.R;
import com.corelabsplus.el.adapters.MyEventTabsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyEventActivity extends AppCompatActivity {
    @BindView(R.id.tabs_pager) ViewPager tabsPager;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tab_layout) TabLayout tabLayout;
//    @BindView(R.id.info_tab) TabItem eventInfoTab;
//    @BindView(R.id.reservations_tab) TabItem eventReservationsTab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String title = getIntent().getStringExtra("eventTitle");

        getSupportActionBar().setTitle(title);

        final MyEventTabsAdapter adapter = new MyEventTabsAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        tabsPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(tabsPager);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
