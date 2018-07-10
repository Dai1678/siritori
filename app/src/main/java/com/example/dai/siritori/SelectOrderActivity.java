package com.example.dai.siritori;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class SelectOrderActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_order);

        Button battleFirstButton = findViewById(R.id.battle_first_button);
        Button battleAfterButton = findViewById(R.id.battle_after_button);

        battleFirstButton.setOnClickListener(this);
        battleAfterButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(this, TurnActivity.class);

        switch (v.getId()){
            case R.id.battle_first_button:
                intent.putExtra("screenType", "own");
                break;

            case R.id.battle_after_button:
                intent.putExtra("screenType", "opponent");
                break;
        }

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
