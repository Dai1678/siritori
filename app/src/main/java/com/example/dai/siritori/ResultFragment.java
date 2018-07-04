package com.example.dai.siritori;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ResultFragment extends Fragment {

    private ResultFragmentListener listener = null;

    public ResultFragment() {
        // Required empty public constructor
    }

    public static ResultFragment newInstance(String result) {
        ResultFragment fragment = new ResultFragment();

        Bundle args = new Bundle();
        args.putString("Result", result);
        fragment.setArguments(args);
        return fragment;
    }

    public void onAttach(Context context){
        super.onAttach(context);

        if (!(context instanceof ResultFragmentListener)) {
            throw new UnsupportedOperationException(
                    "Listener is not Implementation.");
        } else {
            listener = (ResultFragmentListener) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO 結果によって値変更
        Bundle args = new Bundle();
        String resultBattle = args.getString("Result");

        TextView resultText = view.findViewById(R.id.result_text);
        resultText.setText("あなたの" + resultBattle);

        Button gameEndButton = view.findViewById(R.id.game_end_button);
        gameEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.finishGame();
                }
            }
        });
    }

    public interface ResultFragmentListener{
        void finishGame();
    }
}
