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


public class Proximity_Activity extends Activity implements SensorEventListener{

    private SensorManager sensorM;
    private String sensor_data="";
    private boolean record_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity_);

        record_data=false;
        sensorM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorM.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        TextView sensor_name = (TextView) findViewById(R.id.proximity_name);
        sensor_name.setText(sensor.getName() + " by " + sensor.getVendor());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            //final float alpha = 1.0f;
            if(record_data)
            {
                sensor_data += event.values[0] + "\n";
            }

            TextView rangelabel = (TextView) findViewById(R.id.range_text);
            rangelabel.setText("Object Range : "+event.values[0]+ " cm");
        }

    }

    public void prox_csv_btn_clicked(View view) {
        if(!record_data) {
            record_data=true;
            Button csv_btn = (Button)findViewById(R.id.save_csv_proximity_btn);
            csv_btn.setText("Sedang merekam, klik lagi untuk berhenti");
        }
        else {
            record_data=false;
            Button csv_btn = (Button)findViewById(R.id.save_csv_proximity_btn);
            csv_btn.setText("Simpan ke CSV");
            MainMenu.write_csv("proximity.csv", sensor_data);
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
                sensorM.getDefaultSensor(Sensor.TYPE_PROXIMITY),
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
