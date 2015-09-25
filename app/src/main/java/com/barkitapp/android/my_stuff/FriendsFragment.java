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
import com.barkitapp.android.parse_backend.objects.BarkItUser;
import com.barkitapp.android.startup.Setup;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.ParseObject;

public class FriendsFragment extends Fragment implements CreateUser.OnCreateUserCompleted, GetInviteCode.OnInviteCodeCompleted {

    private Tracker mTracker;
    private TextView friend_counter;
    private TextView invitedFriendstext;
    private BarkItUser user;
    private EditText invite_code;

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
            mTracker.setScreenName(FriendsFragment.class.getSimpleName());
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
        else {  }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.fragment_mystuff_friend_invite, container, false);

        invite_code = (EditText) layout.findViewById(R.id.code);


        friend_counter = (TextView) layout.findViewById(R.id.friend_count);
        invitedFriendstext = (TextView) layout.findViewById(R.id.invitedFriendstext);

        CreateUser.run(getActivity(), this, DeviceId.get(getActivity()));
        GetInviteCode.run(getActivity(), this, UserId.get(getActivity()));

//        FloatingActionButton fab = (FloatingActionButton) layout.findViewById(R.id.share);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//                sharingIntent.setType("text/plain");
//                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.using_barkit_check_it_out)); // todo replace link
//                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.download_and_enter_my_invite_code) + friend_invite_code + "\n\n" + "http://barkitapp.com/");
//                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
//            }
//        });
//        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.accent)));

        return layout;
    }

    @Override
    public void onCreateUserCompleted(ParseObject result) {
        user = new BarkItUser(result);

        invitedFriendstext.setText(R.string.invited_friends);
        friend_counter.setText(user.getReferred_friend_counter() + "");
    }

    @Override
    public void onCreateUserFailed(String error) {
        invitedFriendstext.setText(R.string.try_again_later);
        friend_counter.setText(":(");
    }

    @Override
    public void onInviteCodeCompleted(String result) {
        invite_code.setText(result);
    }

    @Override
    public void onInviteCodeFailed(String error) {
        invite_code.setText("");
    }
}
