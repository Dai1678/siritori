package com.example.dai.siritori;

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

    private TextView receivedText;
    private Handler handler;

    public static OpponentTurnFragment newInstance() {
        return new OpponentTurnFragment();
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

        changeOwnTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null){
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    //TODO receivedTextをBundleで渡す
                    fragmentTransaction.replace(R.id.turn_container, MyTurnFragment.newInstance());
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
        udp.boot(50000);    //TODO 決め打ちはどうなの
    }

    @Override
    public void receive(String host, int port, String data) {
        Message message = Message.obtain();
        message.obj = data;
        handler.sendMessage(message);
    }

    class Handler extends android.os.Handler {
        public void handleMessage(Message message){
            String word = message.obj.toString();

            receivedText.setText(word);
        }
    }
}
