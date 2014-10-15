package com.pervasif2014.kelompok5.sensory;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pervasif2014.kelompok6.sensory.R;


public class Orientation_Activity extends Activity implements SensorEventListener{

    private SensorManager sensorM;
    private String sensor_data="";
    private boolean record_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        record_data=false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orientation_);
        sensorM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorM.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        TextView sensor_name = (TextView) findViewById(R.id.orientation_name);
        sensor_name.setText(sensor.getName() + " by " + sensor.getVendor());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            //final float alpha = 1.0f;
            //gravity is calculated here
            if(record_data)
            {
                sensor_data += event.values[0] + "," + event.values[2] + "," + event.values[1] + "\n";
            }

            TextView azimuthlabel = (TextView) findViewById(R.id.Azimuth_Text);
            azimuthlabel.setText("Azimuth: " + String.format("%.02f",event.values[0]));
            TextView rolllabel = (TextView) findViewById(R.id.Roll_Text);
            rolllabel.setText("Roll: " + String.format("%.02f",event.values[2]));
            TextView pitchlabel = (TextView) findViewById(R.id.Pitch_Text);
            pitchlabel.setText("Pitch: " + String.format("%.02f",event.values[1]));
        }

    }

    public void orient_csv_btn_clicked(View view)
    {
        if(!record_data) {
            record_data=true;
            Button csv_btn = (Button)findViewById(R.id.save_csv_orient_btn);
            csv_btn.setText("Sedang merekam, klik lagi untuk berhenti");
        }
        else {
            record_data=false;
            Button csv_btn = (Button)findViewById(R.id.save_csv_orient_btn);
            csv_btn.setText("Simpan ke CSV");
            MainMenu.write_csv("orientation.csv", sensor_data);
            sensor_data = "";
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorM.registerListener(this,
                sensorM.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorM.unregisterListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
