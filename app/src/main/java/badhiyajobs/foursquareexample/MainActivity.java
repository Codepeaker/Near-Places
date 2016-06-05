package badhiyajobs.foursquareexample;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.nearby.messages.internal.DistanceImpl;

import badhiyajobs.foursquareexample.fragments.FragmentFood;
import badhiyajobs.foursquareexample.fragments.FragmentNightlife;
import badhiyajobs.foursquareexample.fragments.FragmentShop;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class MainActivity extends AppCompatActivity implements MaterialTabListener {
    public String location;
    private MaterialTabHost tabHost;
    private ViewPager viewPager;
    public static final int SHOP_LIST = 0;
    public static final int FOOD_LIST = 1;
    public static final int NIGHTLIFE_LIST = 2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        Bundle extras= getIntent().getExtras();
        location = extras.getString("location");

        Toast.makeText(this,location,Toast.LENGTH_LONG).show();



        tabHost = (MaterialTabHost) findViewById(R.id.materialTabHost);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(adapter.getPageTitle(i)) //.setIcon(adapter.getIcon(i))
                            .setTabListener(this));

        }
        viewPager.setCurrentItem(1);



    }
    public void radiusintent(View v){
        Intent i = new Intent(MainActivity.this,DistanceLimit.class);
        i.putExtra("location",location);
        startActivity(i);
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int num) {
            Fragment fragment = null;
            switch (num) {
                case SHOP_LIST:
                    fragment = FragmentShop.newInstance("", "");
                    break;
                case FOOD_LIST:
                    fragment = FragmentFood.newInstance("", "");
                    break;
                case NIGHTLIFE_LIST:
                    fragment = FragmentNightlife.newInstance("", "");
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getStringArray(R.array.tabs)[position];
        }

    }



}
