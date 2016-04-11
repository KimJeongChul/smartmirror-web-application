package softwarek.kimjungchul.smartmirror.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import softwarek.kimjungchul.smartmirror.R;
import java.util.ArrayList;


public class NavDrawerListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private Profile profile;

    private ProfilePictureView profilePictureView;
    private ImageView imgIcon;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
        this.context = context;
        this.navDrawerItems = navDrawerItems;

    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }

        if(position == 0) { // 개인 프로필 메뉴
            /** API 사용 또는 자체 회원 가입 기능을 구현 할 경우 추가해야 함 */
            SharedPreferences prefs = context.getSharedPreferences("login",0);
            String facebook_login = prefs.getString("FACEBOOK_LOGIN", "LOGOUT");
            Log.d("FACEBOOK_LOGIN::::::", facebook_login);

            /* 페이스북 로그인 경우*/
            if(facebook_login.equals("LOGIN")) {
                FacebookSdk.sdkInitialize(convertView.getContext());
                profile = Profile.getCurrentProfile(); // 개인 프로필을 가져온다.

                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.drawer_facebook_item, null);

                profilePictureView = (ProfilePictureView) convertView.findViewById(R.id.profile_image_facebook);
                TextView txtTitle = (TextView) convertView.findViewById(R.id.title);

                String id = profile.getId(); // 유저 ID
                String name = profile.getName(); // 유저 이름
                profilePictureView.setProfileId(id); // 페이스 북 프로필 사진
                txtTitle.setText(name);
            }

        } else {
            // 이후 메뉴부터는 정해진 사진을 불러온다.
            imgIcon = (ImageView) convertView.findViewById(R.id.icon);
            TextView txtTitle = (TextView) convertView.findViewById(R.id.title);

            imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
            txtTitle.setText(navDrawerItems.get(position).getTitle());
        }

        return convertView;
    }
}
