package com.example.dai.siritori;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TurnActivity extends AppCompatActivity implements ResultFragment.ResultFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn);

        String screenTypeStr = "";

        Intent intent = getIntent();
        if (intent != null) {
            screenTypeStr = intent.getStringExtra("screenType");
        }

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            switch (screenTypeStr) {
                case "own":
                    fragmentTransaction.replace(R.id.turn_container, MyTurnFragment.newInstance("しりとり"));
                    break;

                case "opponent":
                    fragmentTransaction.replace(R.id.turn_container, OpponentTurnFragment.newInstance());
                    break;

                default:
                    return;
            }

            fragmentTransaction.commit();
        }
    }

    @Override
    public void finishGame() {
        finish();
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

                //TODO menu追加
        }
        return true;
    }

}
