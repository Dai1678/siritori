package com.example.dai.siritori;

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

public class MyTurnFragment extends Fragment {

    public static MyTurnFragment newInstance() {
        return new MyTurnFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_turn, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO 動的に残り時間をタイマー (本当にTextViewか検証)
        TextView remainTime = view.findViewById(R.id.remain_time_text);

        final TextInputLayout textInputLayout = view.findViewById(R.id.answer_text_input_layout);
        final TextInputEditText answerEditText = view.findViewById(R.id.answer_text_input_edit);

        Button sendMessageButton = view.findViewById(R.id.send_message_button);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = answerEditText.getText().toString();

                if (!message.equals("")){
                    //TODO UDP送信


                    //TODO 相手のターン時画面へ遷移
                    if (getFragmentManager() != null) {
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.turn_container, OpponentTurnFragment.newInstance());
                        fragmentTransaction.commit();
                    }
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
