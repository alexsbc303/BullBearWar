package com.example.bullbearwar.Navigation;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.bullbearwar.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    private Switch darkswitch;
    SharedPref sharedPref;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container != null) {
            container.removeAllViewsInLayout();
        }
        View mView = inflater.inflate(R.layout.fragment_setting, container, false);

        sharedPref = new SharedPref(this.getActivity());
        if (sharedPref.loadNightModeState() == true) {
            getActivity().setTheme(R.style.DarkTheme);
        }
        else getActivity().setTheme(R.style.AppTheme);

        darkswitch = mView.findViewById(R.id.darkswitch);
        if (sharedPref.loadNightModeState() == true) {
            darkswitch.setChecked(true);
        }

        darkswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPref.setNightModeState(true);
                    restartApp();
                }
                else {
                    sharedPref.setNightModeState(false);
                    restartApp();
                }
            }
        });
        return mView;
    }

    public void restartApp() {
        Intent intent = new Intent(getActivity().getApplicationContext(), NavigationActivity.class);
        startActivity(intent);
    }

}
