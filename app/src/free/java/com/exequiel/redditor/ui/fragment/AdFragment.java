package com.exequiel.redditor.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exequiel.redditor.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
/**
 * Created by m4ch1n3 on 2/11/2017.
 */

public class AdFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.ad_fragment, container, false);

        MobileAds.initialize(getActivity(), com.exequiel.redditor.BuildConfig.YOUR_ADMOB_APP_ID);
        AdView addView =  (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        addView.loadAd(adRequest);

        return view;
    }
}
