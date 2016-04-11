package softwarek.kimjungchul.smartmirror;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;

public class HomeFragment extends Fragment
{
    private Profile profile;
    private TextView tv_home_user_id;
    private TextView tv_home_user_name;
    private ProfilePictureView profilePictureView;

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle instance)
    {
        super.onCreate(instance);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FacebookSdk.sdkInitialize(view.getContext());
        profile = Profile.getCurrentProfile();

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Log out ...");

        String id = profile.getId();
        String name = profile.getName();

        tv_home_user_id = (TextView)view.findViewById(R.id.home_user_id);
        tv_home_user_name = (TextView)view.findViewById(R.id.home_user_name);

        tv_home_user_id.setText(id);
        tv_home_user_name.setText(name);

        profilePictureView = (ProfilePictureView) view.findViewById(R.id.home_profile_image_facebook);
        profilePictureView.setProfileId(id);

        /** Facebook logout*/
        Button logoutButton = (Button) view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();

                mProgressDialog.setCancelable(false);
                mProgressDialog.show();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                         /* -- switch -> Button R.id. */
                        LoginManager.getInstance().logOut();
                        SharedPreferences prefs = getActivity().getSharedPreferences("login", 0);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("FACEBOOK_LOGIN", "LOGOUT");
                        editor.commit();

                        Intent mainIntent = new Intent(getActivity(), SplashActivity.class);
                        startActivity(mainIntent);
                        getActivity().finish();
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                    }
                }, 1000);
            }
        });
        return view;
    }
}