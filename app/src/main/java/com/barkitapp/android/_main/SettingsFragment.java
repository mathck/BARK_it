package com.barkitapp.android._main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.barkitapp.android.R;
import com.barkitapp.android._core.services.InternalAppData;
import com.barkitapp.android._core.services.MasterList;
import com.barkitapp.android._core.utility.SharedPrefKeys;
import com.barkitapp.android.parse_backend.objects.FeaturedLocation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
