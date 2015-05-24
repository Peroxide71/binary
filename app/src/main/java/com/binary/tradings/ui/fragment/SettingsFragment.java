package com.binary.tradings.ui.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.binary.tradings.R;
import com.binary.tradings.ui.activity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private Spinner spinner;


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        final String[] values = new String[]{"30", "60", "300"};
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.title_section1));
        spinner = (Spinner)rootView.findViewById(R.id.spinnerTimer);
        spinner.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                values));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int period = new Integer(values[position]);
                Log.i("Period", period + " s");
                SharedPreferences sPrefs = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_KEY,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sPrefs.edit();
                editor.putInt(MainActivity.UPDATE_PERIOD_KEY, period);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }


}
