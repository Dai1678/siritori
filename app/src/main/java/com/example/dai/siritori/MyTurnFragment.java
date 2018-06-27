package com.example.dai.siritori;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MyTurnFragment extends Fragment {

    private SharedPreferences preferences;

    public static MyTurnFragment newInstance() {
        return new MyTurnFragment();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO 相手からの言葉をsetText
        TextView lastWordText = view.findViewById(R.id.last_word);

        //TODO 動的に残り時間をタイマー (本当にTextViewか検証)
        TextView limitTimeText = view.findViewById(R.id.limit_time_text);

        final TextInputLayout textInputLayout = view.findViewById(R.id.answer_text_input_layout);
        final TextInputEditText answerEditText = view.findViewById(R.id.answer_text_input_edit);

        //TODO preferenceからIPアドレスとポート番号を取得
        final String host = preferences.getString("ip", null);
        final int port = preferences.getInt("port", 0);

        Button sendMessageButton = view.findViewById(R.id.send_message_button);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = answerEditText.getText().toString();

                if (!message.equals("") && host != null && port != 0){
                    //TODO UDP送信
                    new UDP().send(host, port, message);

                    //TODO 相手のターン時画面へ遷移
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
}
