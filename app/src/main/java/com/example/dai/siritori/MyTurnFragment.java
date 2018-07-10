package com.example.dai.siritori;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MyTurnFragment extends Fragment {

    private SharedPreferences preferences;

    private String receiveStr;

    private TextView limitTimeText;
    private SimpleDateFormat dataFormat = new SimpleDateFormat("ss.S", Locale.US);

    private CountDown countDown;
    private TextInputEditText answerEditText;
    private Button sendMessageButton;
    private ProgressBar progressBar;

    public static MyTurnFragment newInstance(String text) {
        MyTurnFragment fragment = new MyTurnFragment();

        Bundle args = new Bundle();
        args.putString("ReceiveWord", text);
        fragment.setArguments(args);
        return fragment;
    }

    public void onAttach(Context context){
        super.onAttach(context);
        preferences = context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_turn, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progressbar);

        TextView lastWordText = view.findViewById(R.id.last_word);

        //相手から受け取った文字列 (必ず平仮名)
        Bundle args = getArguments();
        if (args != null) {
            receiveStr = args.getString("ReceiveWord");
            lastWordText.setText("直前の言葉 : " + receiveStr);
        }

        //動的に残り時間をカウントダウン
        limitTimeText = view.findViewById(R.id.limit_time_text);
        limitTimeText.setText("残り時間 : " + dataFormat.format(0));

        long countNumber = 30000;   //とりあえず30秒
        long interval = 100;    //インターバル 100msごとに更新
        countDown = new CountDown(countNumber, interval);
        countDown.start();

        answerEditText = view.findViewById(R.id.answer_text_input_edit);

        sendMessageButton = view.findViewById(R.id.send_message_button);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendStr = answerEditText.getText().toString();

                progressBar.setVisibility(android.widget.ProgressBar.VISIBLE);
                countDown.cancel();
                sendMessageButton.setEnabled(false);
                answerEditText.setFocusable(false);
                if (!sendStr.equals("")){
                    analysisText(sendStr);  //形態素解析
                }else{
                    Toast.makeText(getContext(), "送信できません", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(android.widget.ProgressBar.INVISIBLE);
                    countDown.start();
                    sendMessageButton.setEnabled(true);
                    answerEditText.setFocusable(true);
                }
            }
        });
    }

    private void analysisText(String text) {
        String requestUrl = "https://jlp.yahooapis.jp/MAService/V1/parse?";
        String appId = "appid=" + getContext().getString(R.string.api_key) + "&";
        String param = "results=ma,uniq&sentence=" + text;

        String url = requestUrl + appId + param;

        AnalysisTask analysisTask = new AnalysisTask();
        analysisTask.setListener(createListener());
        analysisTask.execute(url);
    }

    private AnalysisTask.AnalysisTaskListener createListener(){
        return new AnalysisTask.AnalysisTaskListener() {
            @Override
            public void getAnalysisResult(String result) {

                if (result.contains("Error")) {
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                    countDown.start();
                    progressBar.setVisibility(android.widget.ProgressBar.INVISIBLE);
                    sendMessageButton.setEnabled(true);
                    answerEditText.setFocusable(true);

                }else{
                    String host = preferences.getString("ip", null);
                    String port = preferences.getString("port", "50000");

                    if (lastCharCheck(receiveStr, result) && host != null) {
                        countDown.cancel();
                        progressBar.setVisibility(android.widget.ProgressBar.INVISIBLE);

                        Log.d("server", "host: " + host + " port: " + port + " sendMessage: " + result);
                        UDP udp = new UDP();
                        udp.send(host, Integer.parseInt(port), result);
                        udp.shutdown();

                        if (getFragmentManager() != null) {
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.turn_container, OpponentTurnFragment.newInstance());
                            fragmentTransaction.commit();
                        }
                    }else{
                        Toast.makeText(getContext(), "送信できません", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }

    //receiveStrの末文字とsendStrの初めの文字が等しいか平仮名でチェック
    private boolean lastCharCheck(String receiveText, String sendText){
        Character lastReceiveChar = receiveText.charAt(receiveText.length()-1);
        Character firstSendChar = sendText.charAt(0);

        Log.d("compareText", "receive: " + lastReceiveChar + " send: " + firstSendChar);

        return lastReceiveChar.equals(firstSendChar);
    }

    class CountDown extends CountDownTimer {

        CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // 完了
            String host = preferences.getString("ip", null);
            String port = preferences.getString("port", "50000");
            String winText = "勝ちです!";
            String loseText = "負けです...";

            if (host != null){
                new UDP().send(host, Integer.parseInt(port), winText);

                //負け表示の画面(Fragment)に移動
                if (getFragmentManager() != null) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.turn_container, ResultFragment.newInstance(loseText));
                    fragmentTransaction.commit();
                }
            }else{
                Toast.makeText(getContext(), "送信できません", Toast.LENGTH_SHORT).show();
            }
        }

        // インターバルで呼ばれる
        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {
            limitTimeText.setText("残り時間 : " + dataFormat.format(millisUntilFinished));
        }
    }
}
