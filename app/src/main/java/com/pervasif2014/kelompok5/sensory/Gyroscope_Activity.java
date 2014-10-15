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

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static java.lang.Math.sqrt;

public class Gyroscope_Activity extends Activity implements SensorEventListener{

    private SensorManager sensorM;
    private String sensor_data="";
    private boolean record_data;

    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private static final double EPSILON = 0.0000001;
    private float timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope_);

        record_data=false;
        sensorM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorM.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        TextView sensor_name = (TextView) findViewById(R.id.gyroscope_name);
        sensor_name.setText(sensor.getName() + " by " + sensor.getVendor());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // This timestep's delta rotation to be multiplied by the current rotation
            // after computing it from the gyro sample data.
            if (timestamp != 0) {
                final float dT = (event.timestamp - timestamp) * NS2S;
                // Axis of the rotation sample, not normalized yet.
                float axisX = event.values[0];
                float axisY = event.values[1];
                float axisZ = event.values[2];

                // Calculate the angular speed of the sample
                float omegaMagnitude = (float)sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

                // Normalize the rotation vector if it's big enough to get the axis
                if (omegaMagnitude > EPSILON) {
                    axisX /= omegaMagnitude;
                    axisY /= omegaMagnitude;
                    axisZ /= omegaMagnitude;
                }

                if(record_data)
                {
                    sensor_data += axisX + "," + axisY + "," + axisZ + "\n";
                }

                TextView xlabel = (TextView) findViewById(R.id.X_text);
                xlabel.setText("X Axis: " + String.format("%.02f",axisX) + " m/s");
                TextView ylabel = (TextView) findViewById(R.id.Y_Text);
                ylabel.setText("Y Axis: " + String.format("%.02f",axisY) + " m/s");
                TextView zlabel = (TextView) findViewById(R.id.Z_Text);
                zlabel.setText("Z Axis: " + String.format("%.02f",axisZ) + " m/s");

                // Integrate around this axis with the angular speed by the timestep
                // in order to get a delta rotation from this sample over the timestep
                // We will convert this axis-angle representation of the delta rotation
                // into a quaternion before turning it into the rotation matrix.
                float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                float sinThetaOverTwo = sin(thetaOverTwo);
                float cosThetaOverTwo = cos(thetaOverTwo);
                deltaRotationVector[0] = sinThetaOverTwo * axisX;
                deltaRotationVector[1] = sinThetaOverTwo * axisY;
                deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                deltaRotationVector[3] = cosThetaOverTwo;
            }
            timestamp = event.timestamp;
            float[] deltaRotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
            // User code should concatenate the delta rotation we computed with the current rotation
            // in order to get the updated rotation.
            // rotationCurrent = rotationCurrent * deltaRotationMatrix;
        }
    }

    public void gyro_csv_btn_clicked(View view) {
        if(!record_data) {
            record_data=true;
            Button csv_btn = (Button)findViewById(R.id.save_csv_gyroscope_btn);
            csv_btn.setText("Sedang merekam, klik lagi untuk berhenti");
        }
        else {
            record_data=false;
            Button csv_btn = (Button)findViewById(R.id.save_csv_gyroscope_btn);
            csv_btn.setText("Simpan ke CSV");
            MainMenu.write_csv("gyroscope.csv", sensor_data);
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
                sensorM.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
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
