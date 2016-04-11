package softwarek.kimjungchul.smartmirror;

import android.app.Notification;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class NotiListenService extends NotificationListenerService {

    private String smartmirror_ip; // 스마트 미러의 할당받은 ip 주소

    /* 환경 변수 */
    private SharedPreferences prefs;

    public NotiListenService() {
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //super.onNotificationPosted(sbn);
        /**
         * 보안 / 알림 액세스 / 알림 설정해야함!
         */
        Notification noti = sbn.getNotification();
        Bundle bundle = noti.extras;

        prefs = getSharedPreferences("login", 0);
        smartmirror_ip = prefs.getString("SMARTMIRROR_IP", "0.0.0.0");
        Log.d("smartmirror IP : ",smartmirror_ip);

        /*
        int icon = bundle.getInt(Notification.EXTRA_SMALL_ICON);
        Bitmap notificationLargeIcon = ((Bitmap) bundle.getParcelable(Notification.EXTRA_LARGE_ICON));
        */

        /**
         * strPacakge : 패캐지 이름
         * title : 이름, 사용자, 전화번호 등...
         * text : 내용 */
        String strPackage = sbn.getPackageName();
        String title = bundle.getString(Notification.EXTRA_TITLE);
        CharSequence text = bundle.getCharSequence(Notification.EXTRA_TEXT);

        if(title == null) title = "";
        if(text == null) text = "";

        Log.d("[noti] get :","package : "+strPackage+" // title : "+title+" // text : "+text);

        /* 페이스북, 구글 메일, 카카오톡, 전화, 문자 패키지만 라즈베리파이 알림으로 전송 */
        if(strPackage.equals("com.facebook.orca") || strPackage.equals("com.facebook.katana") || strPackage.equals("com.android.mms")
                || strPackage.equals("com.kakao.talk") || strPackage.equals("com.google.android.gm") || strPackage.equals("com.lge.ltecall")) {

            /** 라즈베리 파이2 전송하는 부분 */
            try {
                String enPackage = URLEncoder.encode(strPackage, "UTF-8");
                String enTitle = URLEncoder.encode(title, "UTF-8");
                String enText = URLEncoder.encode(text.toString(), "UTF-8");
            /* 라즈베리 파이 IP : 9090 (port) */
                String urlRasp = "http://"+smartmirror_ip+":9090/noti.do?package=" + enPackage + "&title=" + enTitle + "&text=" + enText;
                Log.d("smartmirror url : ",urlRasp);
                URL url = new URL(urlRasp);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String response = null;

                while (true) {
                    response = br.readLine();
                    if (response == null) break;
                    Log.d("[noti] response :", response);
                }

                urlConnection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //super.onNotificationRemoved(sbn);
    }
}

