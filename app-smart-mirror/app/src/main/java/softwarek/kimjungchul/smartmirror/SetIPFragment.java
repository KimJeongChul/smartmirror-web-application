package softwarek.kimjungchul.smartmirror;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SetIPFragment extends Fragment {


    private EditText et_ip;
    private TextView tv_currentIP;
    private Button btn_setIP;

    private String current_ip; // 환경변수에 저장된 ip주소
    private String smartmirror_ip; // 스마트 미러의 할당받은 ip 주소

    /* 환경 변수 */
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_setip, container, false);

        tv_currentIP = (TextView)rootView.findViewById(R.id.tv_currentIP);
        et_ip = (EditText)rootView.findViewById(R.id.et_ip);
        btn_setIP = (Button) rootView.findViewById(R.id.btn_setIP);

        prefs = getActivity().getSharedPreferences("login", 0);
        editor = prefs.edit();

        current_ip = prefs.getString("SMARTMIRROR_IP","0.0.0.0");
        tv_currentIP.setText(current_ip);

        btn_setIP.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                smartmirror_ip = et_ip.getText().toString();
                editor.putString("SMARTMIRROR_IP",smartmirror_ip);
                editor.commit();
                Toast.makeText(getActivity(),"IP가"+ smartmirror_ip +"로 설정되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });


        return rootView;
    }
}
