package com.example.dai.siritori;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class OpponentTurnFragment extends Fragment implements UDPServer {

    private SharedPreferences preferences;

    private TextView receivedText;
    private Handler handler;

    public static OpponentTurnFragment newInstance() {
        return new OpponentTurnFragment();
    }

    public void onAttach(Context context){
        super.onAttach(context);
        preferences = context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_opponent_turn, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        receivedText = view.findViewById(R.id.received_text);

        Button changeOwnTurnButton = view.findViewById(R.id.change_own_turn_button);

        //TODO 自動遷移ならいらない
        changeOwnTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null){
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                    fragmentTransaction.replace(R.id.turn_container, MyTurnFragment.newInstance(receivedText.toString()));
                    fragmentTransaction.commit();
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        handler = new Handler();
        UDP udp = new UDP(this);

        final String port = preferences.getString("port", "50000");
        udp.boot(Integer.parseInt(port));
    }

    @Override
    public void receive(String host, int port, String data) {
        Message message = Message.obtain();
        message.obj = data;
        handler.sendMessage(message);
    }

    @SuppressLint("HandlerLeak")
    class Handler extends android.os.Handler {
        public void handleMessage(Message message){
            String word = message.obj.toString();

            if (word.equals("勝ちです!")){
                //TODO ResultFragmentに遷移 -> Debug
                //負け側の画面(Fragment)に移動
                if (getFragmentManager() != null) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.turn_container, ResultFragment.newInstance(word));
                    fragmentTransaction.commit();
                }

            }else{
                receivedText.setText(word);

                //TODO 何秒か後に自動でMyTurnFragmentに移動 -> Debug
                handler = new Handler();
                handler.postDelayed(new SplashHandler(word), 2000);
            }

        }
    }

    class SplashHandler implements Runnable {

        private String text;
        
        SplashHandler(String text){
            this.text = text;
        }
        
        public void run() {
            if (getFragmentManager() != null){
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                //TODO receivedTextをBundleで渡す → Debug
                fragmentTransaction.replace(R.id.turn_container, MyTurnFragment.newInstance(text));
                fragmentTransaction.commit();
            }
        }
    }
}
