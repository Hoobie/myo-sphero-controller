package com.fleenmobile.androidapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import orbotix.robot.base.CollisionDetectedAsyncData;
import orbotix.robot.base.Robot;
import orbotix.robot.base.RobotProvider;
import orbotix.robot.sensor.DeviceSensorsData;
import orbotix.sphero.CollisionListener;
import orbotix.sphero.ConnectionListener;
import orbotix.sphero.DiscoveryListener;
import orbotix.sphero.PersistentOptionFlags;
import orbotix.sphero.SensorControl;
import orbotix.sphero.SensorFlag;
import orbotix.sphero.SensorListener;
import orbotix.sphero.Sphero;


public class SettingsActivity extends Activity {
    /** The Sphero Robot */
    private Sphero mRobot;
    private String TAG = "suck my balls";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void pairSphero(View v) {
        RobotProvider.getDefaultProvider().addConnectionListener(new ConnectionListener() {
            @Override
            public void onConnected(Robot robot) {
                mRobot = (Sphero) robot;
                SettingsActivity.this.connected();
            }

            @Override
            public void onConnectionFailed(Robot sphero) {
                Log.d(TAG, "Connection Failed: " + sphero);
                Toast.makeText(SettingsActivity.this, "Sphero Connection Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDisconnected(Robot robot) {
                Log.d(TAG, "Disconnected: " + robot);
                Toast.makeText(SettingsActivity.this, "Sphero Disconnected", Toast.LENGTH_SHORT).show();
                SettingsActivity.this.stopBlink();
                mRobot = null;
            }
        });

        RobotProvider.getDefaultProvider().addDiscoveryListener(new DiscoveryListener() {
            @Override
            public void onBluetoothDisabled() {
                Log.d(TAG, "Bluetooth Disabled");
                Toast.makeText(SettingsActivity.this, "Bluetooth Disabled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void discoveryComplete(List<Sphero> spheros) {
                Log.d(TAG, "Found " + spheros.size() + " robots");
            }

            @Override
            public void onFound(List<Sphero> sphero) {
                Log.d(TAG, "Found: " + sphero);
                RobotProvider.getDefaultProvider().connect(sphero.iterator().next());
            }
        });

        boolean success = RobotProvider.getDefaultProvider().startDiscovery(this);
        if(!success){
            Toast.makeText(SettingsActivity.this, "Unable To start Discovery!", Toast.LENGTH_LONG).show();
        }
    }

    private void connected() {
        Log.d(TAG, "Connected On Thread: " + Thread.currentThread().getName());
        Log.d(TAG, "Connected: " + mRobot);
        Toast.makeText(SettingsActivity.this, mRobot.getName() + " Connected", Toast.LENGTH_LONG).show();

        final SensorControl control = mRobot.getSensorControl();
        control.addSensorListener(new SensorListener() {
            @Override
            public void sensorUpdated(DeviceSensorsData sensorDataArray) {
                Log.d(TAG, sensorDataArray.toString());
            }
        }, SensorFlag.ACCELEROMETER_NORMALIZED, SensorFlag.GYRO_NORMALIZED);

        control.setRate(1);
        mRobot.enableStabilization(false);
        mRobot.drive(90, 0);
        mRobot.setBackLEDBrightness(.5f);

        mRobot.getCollisionControl().startDetection(255, 255, 255, 255, 255);
        mRobot.getCollisionControl().addCollisionListener(new CollisionListener() {
            public void collisionDetected(CollisionDetectedAsyncData collisionData) {
                Log.d(TAG, collisionData.toString());
            }
        });

        SettingsActivity.this.blink(false); // Blink the robot's LED

        boolean preventSleepInCharger = mRobot.getConfiguration().isPersistentFlagEnabled(PersistentOptionFlags.PreventSleepInCharger);
        Log.d(TAG, "Prevent Sleep in charger = " + preventSleepInCharger);
        Log.d(TAG, "VectorDrive = " + mRobot.getConfiguration().isPersistentFlagEnabled(PersistentOptionFlags.EnableVectorDrive));

        mRobot.getConfiguration().setPersistentFlag(PersistentOptionFlags.PreventSleepInCharger, false);
        mRobot.getConfiguration().setPersistentFlag(PersistentOptionFlags.EnableVectorDrive, true);

        Log.d(TAG, "VectorDrive = " + mRobot.getConfiguration().isPersistentFlagEnabled(PersistentOptionFlags.EnableVectorDrive));
        Log.v(TAG, mRobot.getConfiguration().toString());

        // Start MainActivity with mRobot
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("SPHERO", mRobot);
        startActivity(i);

    }
    boolean blinking = true;

    private void stopBlink() {
        blinking = false;
    }
    /**
     * Causes the robot to blink once every second.
     *
     * @param lit
     */
    private void blink(final boolean lit) {
        if (mRobot == null) {
            blinking = false;
            return;
        }

        //If not lit, send command to show blue light, or else, send command to show no light
        if (lit) {
            mRobot.setColor(0, 0, 0);

        } else {
            mRobot.setColor(0, 255, 0);
        }

        if (blinking) {
            //Send delayed message on a handler to run blink again
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    blink(!lit);
                }
            }, 2000);
        }
    }

}
