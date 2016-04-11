package softwarek.kimjungchul.smartmirror;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SendSpeechCommandFragment extends SherlockFragment{

    private Intent i;
    private TextView tv_speech;
    private ImageButton btn_userSpeech;
    private Button btn_stopRecog;

    private SpeechRecognizer mRecognizer;

    private String input_sst; // 명령 결과
    private String enCommand;

    private String smartmirror_ip; // 스마트 미러의 할당받은 ip 주소

    /* 환경 변수 */
    private SharedPreferences prefs;

    /* 스마트 미러로 네트워크 전송 부분*/
    SendComandTask sendComandTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sendspeechcommend, container, false);

        /* 레이아웃의 컴포넌트를 가져옵니다.*/
        tv_speech = (TextView) rootView.findViewById(R.id.tv_speech);
        btn_userSpeech = (ImageButton) rootView.findViewById(R.id.userSpeech);
        btn_stopRecog = (Button) rootView.findViewById(R.id.btn_stopRecog);

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // Intent 생성
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getActivity().getPackageName()); // 호출한 패키지
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR"); // 인식한 언어 설정
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "말해주세요"); // 유저에게 보여줄 문자

        /* 환경 변수 */
        prefs = getActivity().getSharedPreferences("login", 0);
        smartmirror_ip = prefs.getString("SMARTMIRROR_IP", "0.0.0.0");



        btn_userSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.userSpeech) {
                    mRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
                    mRecognizer.setRecognitionListener(listener);
                    i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // Intent 생성
                    i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getActivity().getPackageName()); // 호출한 패키지
                    i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR"); // 인식한 언어 설정
                    i.putExtra(RecognizerIntent.EXTRA_PROMPT, "말해주세요"); // 유저에게 보여줄 문자
                    mRecognizer.startListening(i);
                }
            }
        });

        btn_stopRecog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecognizer.destroy();
            }
        });

        return rootView;
    }

    private RecognitionListener listener = new RecognitionListener() {
        /** 음성인식 준비 완료 */
        @Override
        public void onReadyForSpeech(Bundle params) {
            //Toast.makeText(getActivity(),"SST Ready",Toast.LENGTH_SHORT).show();
        }

        /** 음성인식 시작 */
        @Override
        public void onBeginningOfSpeech() {
            Toast.makeText(getActivity(),"음성 인식 시작",Toast.LENGTH_SHORT).show();
        }

        /** 입력 소리 변경 시 */
        @Override
        public void onRmsChanged(float rmsdB) {
        }

        /** 더 많은 소리를 받을 경우 */
        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        /** 음성인식 끝남 */
        @Override
        public void onEndOfSpeech() {
            //Toast.makeText(getActivity(),"SST Finish",Toast.LENGTH_SHORT).show();
        }

        /** 에러 발생 */
        @Override
        public void onError(int error) {
            //Toast.makeText(getActivity(),"SST Retry :D",Toast.LENGTH_SHORT).show();
            mRecognizer.destroy();
            mRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
            mRecognizer.setRecognitionListener(listener);
            mRecognizer.startListening(i);
        }

        /** 음성인식 결과 받음 */
        @Override
        public void onResults(Bundle results) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            input_sst = results.getStringArrayList(key).get(0);
            if(input_sst.equals("정지")) {
                Toast.makeText(getActivity(),"음성 명령이 정지 되었습니다",Toast.LENGTH_LONG).show();
                mRecognizer.destroy();
                input_sst = "";
                return;
            }
            tv_speech.setText("" + input_sst);
            Toast.makeText(getActivity(),"말씀하신 명령 : "+input_sst,Toast.LENGTH_SHORT).show();
            Log.d("SST : ", input_sst);

            try {
                enCommand = URLEncoder.encode(input_sst, "UTF-8");
                /* AsyncTask*/
                sendComandTask = new SendComandTask();
                sendComandTask.execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            mRecognizer.destroy();

            mRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
            mRecognizer.setRecognitionListener(listener);
            mRecognizer.startListening(i);
        }

        /** 음성인식 결과 일부 유효 */
        @Override
        public void onPartialResults(Bundle partialResults) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            String input_sst = partialResults.getStringArrayList(key).get(0);
            tv_speech.setText("" + input_sst);
            Toast.makeText(getActivity(),"You said : "+input_sst,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };

    class SendComandTask extends AsyncTask<Void, Void, Void> {
        /**
         * Parameter
         *  - 1 parmas는 입력
         *  - 2 progress
         *  - 3 result는 리턴 값 */

        @Override
        protected void onPreExecute() {
            /* Thread의 Run과 같은 기능, Thread로 동작 */
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            /** 라즈베리 파이2 전송하는 부분 */
            try {

                /* 라즈베리 파이 IP : 9090 (port) */
                String urlRasp = "http://"+smartmirror_ip+":9090/android.do?command="+enCommand;
                Log.d("smartmirror url : ",urlRasp);
                URL url = new URL(urlRasp);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String response = null;

                while (true) {
                    response = br.readLine();
                    if (response == null) break;
                    Log.d("[android command]","Response : "+response);
                }

                urlConnection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
