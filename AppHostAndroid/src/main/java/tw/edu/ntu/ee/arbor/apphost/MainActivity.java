package tw.edu.ntu.ee.arbor.apphost;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends ActionBarActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private Context mContext;

    ViewPager mViewPager;

    private TextView mTextView;
    private ImageButton mAddButton;
    private CalendarView mCalenderView;

    private int[] currentActivities = new int[3];

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_calender);

        mContext = this;

        mAddButton = (ImageButton) findViewById(R.id.imageButton_add);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RecordActivity.class);
                startActivity(intent);
            }
        });

        Date today = new Date(System.currentTimeMillis());
        mCalenderView = (CalendarView) findViewById(R.id.calendarView);
        mCalenderView.setDate(today.getTime(), true, true);
        mCalenderView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
                Random r = new Random();
                for (int i = 0; i < 3; i++) {
                    currentActivities[i] = r.nextInt(5);
                }
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mSectionsPagerAdapter.notifyDataSetChanged();
                mViewPager.setAdapter(mSectionsPagerAdapter);
                mViewPager.setCurrentItem(0);
            }
        });


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        Intent intent = new Intent(this, LauncherService.class);
        startService(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, currentActivities[position]);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_ACTIVITY_TYPE = "activity_type";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, int act_type) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putInt(ARG_ACTIVITY_TYPE, act_type);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView labelTextView = (TextView) rootView.findViewById(R.id.section_label);
            labelTextView.setText(String.valueOf(getArguments().getInt(ARG_SECTION_NUMBER)) + "/3");

            int activityType = getArguments().getInt(ARG_ACTIVITY_TYPE);
            TextView typeTextView = (TextView) rootView.findViewById(R.id.textView_act_type);
            typeTextView.setText(getResources().getTextArray(R.array.activity_types)[activityType]);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView_background);
            switch (activityType) {
                case 0: //Resting
                    imageView.setImageResource(R.drawable.bg_resting);
                    break;
                case 1: // Dining
                    imageView.setImageResource(R.drawable.bg_dining);
                    break;
                case 2: // Sports
                    imageView.setImageResource(R.drawable.bg_sports);
                    break;
                case 3: // Recreation
                    imageView.setImageResource(R.drawable.bg_recreation);
                    break;
                case 4: // Working
                    imageView.setImageResource(R.drawable.bg_working);
                    break;
                default:
                    imageView.setImageResource(R.drawable.bg_add_activity);
                    break;
            }
            return rootView;
        }
    }
}
