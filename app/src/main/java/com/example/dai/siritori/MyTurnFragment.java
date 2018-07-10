package com.example.dai.siritori;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyTurnFragment extends Fragment {

    private SharedPreferences preferences;

    TextView limitTimeText;
    private SimpleDateFormat dataFormat = new SimpleDateFormat("ss.SSS", Locale.US);

    OkHttpClient client;

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

        //TODO 相手からの言葉をsetText → Debug
        TextView lastWordText = view.findViewById(R.id.last_word);

        String receiveStr;  //相手から受け取った文字列
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
        final CountDown countDown = new CountDown(countNumber, interval);
        countDown.start();

        final TextInputEditText answerEditText = view.findViewById(R.id.answer_text_input_edit);

        //TODO preferenceからIPアドレスとポート番号を取得 → Debug
        final String host = preferences.getString("ip", null);
        final String port = preferences.getString("port", "50000");

        Button sendMessageButton = view.findViewById(R.id.send_message_button);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = answerEditText.getText().toString();

                //TODO しりとりのルール : lastWordTextの一番うしろの文字から始まっているか確認
                //TODO 入力文字をひらがなに変換する
                //TODO 形態素解析結果を取得してOKならsend

                analysisText(message);

                Log.d("UDPSend", "message:" + message + ", host:" + host + ", port:" + port);
                if (emptyDataCheck(message, host) ){
                    countDown.cancel();
                    //TODO UDP送信 → Debug
                    new UDP().send(host, Integer.parseInt(port), message);

                    //TODO 相手のターン時画面(Fragment)へ遷移
                    if (getFragmentManager() != null) {
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.turn_container, OpponentTurnFragment.newInstance());
                        fragmentTransaction.commit();
                    }
                }else{
                    Toast.makeText(getContext(), "送信できません", Toast.LENGTH_SHORT).show();
                }
            }
        });

        answerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //TODO 入力文字に対して動的に何かしたい時

            }
        });
    }

    private void analysisText(String text) {
        String requestUrl = "https://jlp.yahooapis.jp/MAService/V1/parse?";
        String appId = "appid=" + "dj00aiZpPW5FWEJQWWs0eHZyOCZzPWNvbnN1bWVyc2VjcmV0Jng9ZmQ-" + "&";   //TODO clientIdをstring.xmlに隠す
        String param = "results=ma,uniq&sentence=" + text;

        String url = requestUrl + appId + param;

        new AnalysisTask().execute(url);
    }

    private boolean emptyDataCheck(String message, String host){
        return !message.equals("") && host != null;
    }

    class CountDown extends CountDownTimer {

        CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // 完了
            //TODO 相手が勝ったことをUDPで送信する → Debug
            //TODO preferenceからIPアドレスとポート番号を取得 → Debug
            final String host = preferences.getString("ip", null);
            final int port = preferences.getInt("port", 50000);
            final String winText = "勝ちです!";
            final String loseText = "負けです...";

            if (host != null && port != 0){
                //TODO UDP送信 → Debug
                new UDP().send(host, port, winText);

                //負け側の画面(Fragment)に移動
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
        @Override
        public void onTick(long millisUntilFinished) {
            // 残り時間を分、秒、ミリ秒に分割
            //long mm = millisUntilFinished / 1000 / 60;
            //long ss = millisUntilFinished / 1000 % 60;
            //long ms = millisUntilFinished - ss * 1000 - mm * 1000 * 60;
            //limitTimeText.setText(String.format("%1$02d:%2$02d.%3$03d", mm, ss, ms));

            limitTimeText.setText(dataFormat.format(millisUntilFinished));

        }
    }
}
