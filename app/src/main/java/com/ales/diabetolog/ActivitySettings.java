package com.ales.diabetolog;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import Data.DecimalDigitsFilter;
import Data.Nastavitev;

public class ActivitySettings extends AppCompatActivity {
    private static ApplicationDiabetoLog applicationDiabetoLog;
    public int counter = 0;
    public static List<products> retrievePeople() {
        List<products> list = new ArrayList<products>();
        list.add(new products("Яблуко",  1));
        list.add(new products("Груша солодка",  2));
        list.add(new products("Груша звичайна",  3));
        list.add(new products("Помідор",  4));
        list.add(new products("Гречка",  5));
        list.add(new products("Яловичина",  6));
        list.add(new products("Сир",  7));
        list.add(new products("Молоко",  8));
        list.add(new products("Вареники з сиром",  9));
        list.add(new products("Сухарі",  10));
        list.add(new products("Рис",  11));
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        applicationDiabetoLog=((ApplicationDiabetoLog)getApplication());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onPause() {
        super.onPause();
        applicationDiabetoLog.save();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
        private List<View> allEds;
        private List<products> mList;
        private AddAdapter adapter;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            final View rootView;


            if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                rootView = inflater.inflate(R.layout.fragment_activity_bazalni_odmerki,
                        container,
                        false);

                Button addButton = (Button) rootView.findViewById(R.id.notifi);
                //инициализировали наш массив с edittext.aьи
                allEds = new ArrayList<View>();

                //находим наш linear который у нас под кнопкой add edittext в activity_main.xml
                final LinearLayout linear = (LinearLayout) rootView.findViewById(R.id.linear1);



                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater li = LayoutInflater.from(v.getContext());
                        final  View view = li.inflate(R.layout.custom_edittext_layout1, null);
                        //берем наш кастомный лейаут находим через него все наши кнопки и едит тексты, задаем нужные данные

                        Button deleteField = (Button) view.findViewById(R.id.button2);
                        deleteField.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    ((LinearLayout) view.getParent()).removeView(view);
                                    allEds.remove(view);
                                } catch (IndexOutOfBoundsException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });

                        allEds.add(view);
                        //добавляем елементы в linearlayout
                        linear.addView(view);


                    }

                });
                }
                else {
                rootView = inflater.inflate(R.layout.fragment_activity_bolus_wizard,
                        container,
                        false);
                Button addButton = (Button) rootView.findViewById(R.id.notifi);
                //инициализировали наш массив с edittext.aьи
                allEds = new ArrayList<View>();
                //находим наш linear который у нас под кнопкой add edittext в activity_main.xml
                final LinearLayout linear = (LinearLayout) rootView.findViewById(R.id.linear);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        //берем наш кастомный лейаут находим через него все наши кнопки и едит тексты, задаем нужные данные
                        LayoutInflater li = LayoutInflater.from(v.getContext());
                        final  View view = li.inflate(R.layout.custom_edittext_layout, null);
                        Button deleteField = (Button) view.findViewById(R.id.button2);
                        deleteField.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    ((LinearLayout) view.getParent()).removeView(view);
                                    allEds.remove(view);
                                } catch(IndexOutOfBoundsException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                        AutoCompleteTextView text = (AutoCompleteTextView) view.findViewById(R.id.timeinput);
                        //добавляем все что создаем в массив
                        allEds.add(view);
                        //добавляем елементы в linearlayout
                        linear.addView(view);
                        mList = retrievePeople();

                        text.setThreshold(1);
                        adapter = new AddAdapter(rootView.getContext(), R.layout.custom_edittext_layout, R.id.lbl_name, mList);
                        text.setAdapter(adapter);
                        EditText OH = (EditText) view.findViewById(R.id.aboutins);
                        EditText massa = (EditText) view.findViewById(R.id.massa);


                    }
                });
                Button showDataBtn = (Button) rootView.findViewById(R.id.button3);
                showDataBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //преобразуем наш ArrayList в просто String Array
                        String [] name = new String[allEds.size()];
                        int [] OH=new int[allEds.size()];
                        int [] mass=new int[allEds.size()];
                        String temp;
                        LayoutInflater li = LayoutInflater.from(v.getContext());
                        final  View view = li.inflate(R.layout.custom_edittext_layout, null);
                        //запускаем чтение всех елементов этого списка и запись в массив
                        for(int i=0; i < allEds.size(); i++) {
                            try {
                                name[i] = ((EditText) allEds.get(i).findViewById(R.id.timeinput)).getText().toString();

                                OH[i] = Integer.parseInt(name[i].replaceAll("\\D+", "")) / 12;

                                mass[i] = Integer.parseInt(((EditText) allEds.get(i).findViewById(R.id.massa)).getText().toString());

                                OH[i] = (mass[i] * OH[i] / 100);
                                temp = String.valueOf(OH[i]);
                                ((EditText) allEds.get(i).findViewById(R.id.aboutins)).setText(temp);

                            }
                            catch (Exception e)
                            {
                                Toast.makeText(rootView.getContext(), "Будь ласка, заповніть дані!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            };
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Основне";
                case 1:
                    return "Інсулін";
            }
            return null;
        }
    }
}
