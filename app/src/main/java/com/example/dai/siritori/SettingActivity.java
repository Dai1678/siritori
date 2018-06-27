package com.example.dai.siritori;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        final SharedPreferences preferences = getSharedPreferences("Setting", Context.MODE_PRIVATE);

        ListView settingListView = findViewById(R.id.setting_list);

        final ArrayList<String> settingTitleList = new ArrayList<>(Arrays.asList("IPアドレス", "ポート番号"));
        final ArrayList<SettingParamModel> settingItemList = new ArrayList<>();

        SettingParamModel model = null;
        for (int i=0; i<settingTitleList.size(); i++){
            if (i==0) model = new SettingParamModel(settingTitleList.get(i), preferences.getString("ip", "IPアドレスが設定されていません"));
            if (i==1) model = new SettingParamModel(settingTitleList.get(i), preferences.getString("port", "ポート番号が設定されていません"));
            settingItemList.add(model);
        }

        final SettingListAdapter adapter = new SettingListAdapter(this, R.layout.setting_list_row, settingItemList);

        settingListView.setAdapter(adapter);

        settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                @SuppressLint("InflateParams") final View dialogView = getLayoutInflater().inflate(R.layout.dialog_setting, null);

                TextView titleText = dialogView.findViewById(R.id.dialog_title);
                titleText.setText(settingTitleList.get(position));

                builder.setView(dialogView)
                        .setPositiveButton("決定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editParam = dialogView.findViewById(R.id.edit_setting_param);
                                String param = editParam.getText().toString();
                                Log.d("settingParam", param);

                                SharedPreferences.Editor editor = preferences.edit();

                                switch (position){
                                    case 0:
                                        SettingParamModel ipDataModel = new SettingParamModel(settingTitleList.get(position), param);
                                        settingItemList.set(position, ipDataModel);
                                        editor.putString("ip", param);
                                        break;

                                    case 1:
                                        SettingParamModel portDataModel = new SettingParamModel(settingTitleList.get(position), param);
                                        settingItemList.set(position, portDataModel);
                                        editor.putInt("port", Integer.parseInt(param));
                                        break;
                                }

                                adapter.notifyDataSetChanged();

                                editor.apply();

                            }
                        })

                        .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })

                        .show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
