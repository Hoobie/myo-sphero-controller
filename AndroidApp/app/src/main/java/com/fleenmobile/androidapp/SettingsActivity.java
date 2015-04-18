package com.fleenmobile.androidapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.thalmic.myo.scanner.ScanActivity;

import orbotix.robot.base.RobotProvider;


public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Button pairMyoButton = (Button) findViewById(R.id.pair_myo_button);
        final Button pairSpheroButton = (Button) findViewById(R.id.pair_sphero_button);

        pairMyoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ScanActivity.class);
                SettingsActivity.this.startActivity(intent);
            }
        });

        pairSpheroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RobotProvider.getDefaultProvider().startDiscovery(SettingsActivity.this);
            }
        });

        setContentView(R.layout.activity_settings);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
