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

public class GeoMagnetic_Activity extends Activity implements SensorEventListener {

    private SensorManager sensorM;
    private String sensor_data="";
    private boolean record_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        record_data=false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_magnetic_);
        sensorM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        TextView sensor_name = (TextView) findViewById(R.id.magnet_name);
        sensor_name.setText(sensor.getName() + " by " + sensor.getVendor());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            //final float alpha = 1.0f;
            //gravity is calculated here
            if(record_data)
            {
                sensor_data += event.values[0] + "," + event.values[1] + "," + event.values[2] + "\n";
            }

            TextView xlabel = (TextView) findViewById(R.id.X_text);
            xlabel.setText("X Axis: " + String.format("%.02f",event.values[0]) + " μT");
            TextView ylabel = (TextView) findViewById(R.id.Y_Text);
            ylabel.setText("Y Axis: " + String.format("%.02f",event.values[1]) + " μT");
            TextView zlabel = (TextView) findViewById(R.id.Z_Text);
            zlabel.setText("Z Axis: " + String.format("%.02f",event.values[2]) + " μT");
        }

    }

    public void magnet_csv_btn_clicked(View view)
    {
        if(!record_data) {
            record_data=true;
            Button csv_btn = (Button)findViewById(R.id.save_csv_magnet_btn);
            csv_btn.setText("Sedang merekam, klik lagi untuk berhenti");
        }
        else {
            record_data=false;
            Button csv_btn = (Button)findViewById(R.id.save_csv_magnet_btn);
            csv_btn.setText("Simpan ke CSV");
            MainMenu.write_csv("magnetic.csv", sensor_data);
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
                sensorM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
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
