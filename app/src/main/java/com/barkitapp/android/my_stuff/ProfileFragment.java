package com.barkitapp.android.my_stuff;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.barkitapp.android.R;
import com.barkitapp.android._core.services.DeviceId;
import com.barkitapp.android._core.services.UserId;
import com.barkitapp.android.parse_backend.functions.CreateUser;
import com.barkitapp.android.parse_backend.functions.GetInviteCode;
import com.barkitapp.android.parse_backend.functions.GetUser;
import com.barkitapp.android.parse_backend.objects.BarkItUser;
import com.barkitapp.android.startup.Setup;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.ParseObject;

public class ProfileFragment extends Fragment implements GetUser.OnCreateUserCompleted {

    private Tracker mTracker;
    private BarkItUser user;

    private TextView level;
    private TextView creativity;
    private TextView social;
    private TextView friendliness;
    private TextView communicative;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Setup application = (Setup) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mTracker.setScreenName(ProfileFragment.class.getSimpleName());
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
        else {  }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.fragment_mystuff_profile, container, false);


        level = (TextView) layout.findViewById(R.id.level);
        creativity = (TextView) layout.findViewById(R.id.creativity);
        social = (TextView) layout.findViewById(R.id.social);
        friendliness = (TextView) layout.findViewById(R.id.friendliness);
        communicative = (TextView) layout.findViewById(R.id.communicative);

        GetUser.run(getActivity(), this, DeviceId.get(getActivity()));

        return layout;
    }

    @Override
    public void onCreateUserCompleted(ParseObject result) {
        user = new BarkItUser(result);

        creativity.setText(user.getPost_counter() / 20 + "");
        social.setText(user.getReferred_friend_counter() + "");
        friendliness.setText(user.getUpvote_counter() / 120  + "");
        communicative.setText(user.getReply_counter() / 60 + "");

        level.setText(user.getLevel() + "");
    }

    @Override
    public void onCreateUserFailed(String error) {
        level.setText("...");
        creativity.setText("...");
        social.setText("...");
        friendliness.setText("...");
        communicative.setText("...");
    }
}
