package com.example.dai.siritori;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class OpponentTurnFragment extends Fragment {

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

        TextView receivedText = view.findViewById(R.id.received_text);

        //TODO UDP通信でもらった文字列をsetText
        //receivedText.setText("");

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
}
