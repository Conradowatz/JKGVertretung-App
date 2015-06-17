package de.conradowatz.jkgvertretung.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.conradowatz.jkgvertretung.R;
import de.conradowatz.jkgvertretung.activities.MainActivity;
import de.conradowatz.jkgvertretung.tools.PreferenceReader;
import de.conradowatz.jkgvertretung.variables.Tag;
import de.conradowatz.jkgvertretung.variables.Vertretung;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllgVertretungsplanFragment extends Fragment {

    private View contentView;

    private ViewPager viewPager;
    private TabLayout tabs;
    private int dayCount = -1;

    public AllgVertretungsplanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contentView = inflater.inflate(R.layout.fragment_stundenplan, container, false);
        viewPager = (ViewPager) contentView.findViewById(R.id.viewPager);
        tabs = (TabLayout) contentView.findViewById(R.id.materialTabs);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity.vertretungsAPI==null) return contentView;

        StundenplanPagerAdapter stundenplanPagerAdapter = new StundenplanPagerAdapter(mainActivity.vertretungsAPI.getTagList(), getActivity().getLayoutInflater());
        viewPager.setAdapter(stundenplanPagerAdapter);
        tabs.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.white));
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);

        return contentView;
    }

    public void onDayAdded() {

        if (viewPager!=null) {
            viewPager.getAdapter().notifyDataSetChanged();
            tabs.setupWithViewPager(viewPager);
        }
    }

    private class StundenplanPagerAdapter extends PagerAdapter {

        private ArrayList<Tag> tagList;
        private LayoutInflater layoutInflater;

        @Override
        public CharSequence getPageTitle(int position) {

            return tagList.get(position).getDatumString().split(",")[0];
        }

        public StundenplanPagerAdapter(ArrayList<Tag> tagList, LayoutInflater layoutInflater) {

            this.tagList = tagList;
            this.layoutInflater = layoutInflater;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View page = layoutInflater.inflate(R.layout.stundenplan_page, container, false);
            LinearLayout linearLayout = (LinearLayout) page.findViewById(R.id.linearLayout);
            TextView datumText = (TextView) page.findViewById(R.id.datumText);

            datumText.setText(tagList.get(position).getDatumString());

            ArrayList<Vertretung> vertretungsList = tagList.get(position).getVertretungsList();

            for (int i=0; i<vertretungsList.size(); i++) {

                Vertretung vertretung = vertretungsList.get(i);

                View stundenItem = layoutInflater.inflate(R.layout.vertretungsplan_stunde_item, linearLayout, false);
                TextView stundeText = (TextView) stundenItem.findViewById(R.id.stundeText);
                TextView fachText = (TextView) stundenItem.findViewById(R.id.fachText);
                TextView raumText = (TextView) stundenItem.findViewById(R.id.raumText);
                TextView infoText = (TextView) stundenItem.findViewById(R.id.infoText);
                TextView kursText = (TextView) stundenItem.findViewById(R.id.kursText);

                stundeText.setText(vertretung.getStunde());
                fachText.setText(vertretung.getFach());
                raumText.setText(vertretung.getRaum());
                if (vertretung.getInfo().isEmpty())
                    infoText.setHeight(0);
                infoText.setText(vertretung.getInfo());
                kursText.setText(vertretung.getKlasse()+":");

                linearLayout.addView(stundenItem);
            }

            if (linearLayout.getChildCount()==0) {

                TextView textView = (TextView) layoutInflater.inflate(R.layout.spacedtext_item, linearLayout, false);
                textView.setText("Für diesen Tag steht kein Vertretungsplan bereit");
                linearLayout.addView(textView);
            }


            container.addView(page);
            return page;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return tagList.size();
        }
    }


}