package com.pervasif2014.kelompok5.sensory;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.pervasif2014.kelompok6.sensory.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.datatype.Duration;


public class Detect_Activity extends Activity implements SensorEventListener{

    private SensorManager sensorM;
    private  KNN knn = new KNN();
    private double xA=0,yA=0,zA=0,xG=0,yG=0,zG=0,xL=0,yL=0,zL=0;
    int counter =0;

    Timer timer ;
    postDataTask task;
    public String activitas="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_);
        AssetManager asm = getAssets();
        sensorM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorM.getDefaultSensor(Sensor.TYPE_ALL);
        InputStream is = null;
        try {
            is = asm.open("input_baru2.arff");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            knn.init(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        timer = new Timer();
        task= new postDataTask();
        timer.schedule(task,0,5000);


    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            final float alpha = 1.0f;
            //gravity is calculated here
            float[] gravityV = new float[3];
            gravityV[0] = alpha * gravityV[0] + (1 - alpha) * event.values[0];
            gravityV[1] = alpha * gravityV[1] + (1 - alpha) * event.values[1];
            gravityV[2] = alpha * gravityV[2] + (1 - alpha) * event.values[2];
            //acceleration retrieved from the event and the gravity is removed
            xA+= event.values[0] - gravityV[0];
            yA+= event.values[1] - gravityV[1];
            zA+= event.values[2] - gravityV[2];

            TextView xlabel = (TextView) findViewById(R.id.AcX_text);
            xlabel.setText("X Axis: " + String.format("%.02f",event.values[0]) + " m/s");
            TextView ylabel = (TextView) findViewById(R.id.AcY_Text);
            ylabel.setText("Y Axis: " + String.format("%.02f",event.values[1]) + " m/s");
            TextView zlabel = (TextView) findViewById(R.id.AcZ_Text);
            zlabel.setText("Z Axis: " + String.format("%.02f",event.values[2]) + " m/s");
        }

        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
        {
            xG+=event.values[0];
            yG+=event.values[1];
            zG+=event.values[2];
            TextView xlabel = (TextView) findViewById(R.id.GyX_Text);
            xlabel.setText("X Axis: " + String.format("%.02f",event.values[0]) );
            TextView ylabel = (TextView) findViewById(R.id.GyY_Text);
            ylabel.setText("Y Axis: " + String.format("%.02f",event.values[1]) );
            TextView zlabel = (TextView) findViewById(R.id.GyZ_Text);
            zlabel.setText("Z Axis: " + String.format("%.02f",event.values[2]) );
        }

        if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            xL+=event.values[0];
            yL+=event.values[1];
            zL+=event.values[2];
        }
        counter++;
        if (counter == 20)
        {
            double status = 0.0;
            try {
                status = knn.Classify(xA/20, yA/20, zA/20, xL/20, yL/20, zL/20, xG/20, yG/20, zG/20);
            } catch (Exception e) {
                e.printStackTrace();
            }

            TextView statuslabel = (TextView) findViewById(R.id.status_activity);

            if (status == 0.0) {
                activitas = "Berjalan";
                statuslabel.setText("Status : Berjalan");
            } else if (status == 1.0) {
                activitas = "Berdiri";
                statuslabel.setText("Status : Berdiri");

            } else if (status == 2.0) {
                activitas = "Berlari";
                statuslabel.setText("Status : Berlari");

            } else if (status == 3.0) {
                activitas = "Duduk";
                statuslabel.setText("Status : Duduk");
            }
            xA = 0;
            yA = 0;
            zA = 0;
            xG = 0;
            yG = 0;
            zG = 0;
            xL = 0;
            yL = 0;
            zL = 0;
            counter=0;
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
                sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorM.registerListener(this,
                sensorM.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorM.registerListener(this,
                sensorM.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
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
        getMenuInflater().inflate(R.menu.detect_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    class postDataTask extends TimerTask {

        @Override
        public void run() {
           System.out.println(activitas);
            //implement da post function here

        }

    }
}
