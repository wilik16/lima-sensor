package com.pervasif2014.kelompok5.sensory;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
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
import android.widget.Toast;

import com.pervasif2014.kelompok6.sensory.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;

public class Accelero_Activity extends Activity implements SensorEventListener {

    private SensorManager sensorM;
    private String sensor_data="";
    private boolean record_data;
    private List<Double> xdata = new ArrayList<Double>();
    private List<Double> ydata  = new ArrayList<Double>();
    private List<Double> zdata = new ArrayList<Double>();

    private  KNN knn = new KNN();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelero_);
        AssetManager asm = getAssets();
        record_data=false;
        sensorM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        TextView sensor_name = (TextView) findViewById(R.id.acc_name);
        sensor_name.setText(sensor.getName() + " by " + sensor.getVendor());
        sensor_data+="X,Y,Z\n";
        InputStream is = null;
        try {
            is = asm.open("input.arff");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            knn.init(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            final float alpha = 1.0f;
            //gravity is calculated here
            float[] gravityV = new float[3];
            double x,y,z;
            gravityV[0] = alpha * gravityV[0] + (1 - alpha) * event.values[0];
            gravityV[1] = alpha * gravityV[1] + (1 - alpha) * event.values[1];
            gravityV[2] = alpha * gravityV[2] + (1 - alpha) * event.values[2];
            //acceleration retrieved from the event and the gravity is removed
            x = event.values[0] - gravityV[0];
            y = event.values[1] - gravityV[1];
            z = event.values[2] - gravityV[2];
            if(record_data)
            {
                xdata.add(x);
                ydata.add(y);
                zdata.add(z);
            }

            TextView xlabel = (TextView) findViewById(R.id.X_text);
            xlabel.setText("X Axis: " + String.format("%.02f",x) + " m/s");
            TextView ylabel = (TextView) findViewById(R.id.Y_Text);
            ylabel.setText("Y Axis: " + String.format("%.02f",y) + " m/s");
            TextView zlabel = (TextView) findViewById(R.id.Z_Text);
            zlabel.setText("Z Axis: " + String.format("%.02f",z) + " m/s");
            double status = 0.0;
            try {
                status = knn.Classify(x,y,z);
            } catch (Exception e) {
                e.printStackTrace();
            }

            TextView statuslabel = (TextView)findViewById(R.id.status_text);

            if(status == 0.0)
            {
                statuslabel.setText("Status : Berjalan");

            }
            else if (status==1.0)
            {
                statuslabel.setText("Status : Berdiri");

            }
            else if (status==2.0)
            {
                statuslabel.setText("Status : Berlari");

            }
            else if (status==3.0)
            {
                statuslabel.setText("Status : Duduk");

            }
        }
    }

    public void accel_csv_btn_clicked(View view) {
        if(!record_data) {
            record_data=true;
            Button csv_btn = (Button)findViewById(R.id.save_csv_btn);
            csv_btn.setText("Sedang merekam, klik lagi untuk berhenti");
        }
        else {
            record_data=false;
            Button csv_btn = (Button)findViewById(R.id.save_csv_btn);
            csv_btn.setText("Simpan ke CSV");
            for(int i =0;i<xdata.size();i+=10)
            {
                double tempx=0;
                double tempy=0;
                double tempz=0;
                if(xdata.size()-i >=20)
                    for(int j =i;j<i+20;j++)
                    {
                        tempx= tempx + xdata.get(j);
                        tempy= tempy + ydata.get(j);
                        tempz= tempz + zdata.get(j);

                    }
                else
                    for(int j =i;j<xdata.size();j++)
                    {
                        tempx= tempx + xdata.get(j);
                        tempy= tempy + ydata.get(j);
                        tempz= tempz + zdata.get(j);

                    }
                tempx /=10;
                tempy/=10;
                tempz/=10;
                sensor_data+=tempx+","+tempy+","+tempz+"\n";

            }
            MainMenu.write_csv("accelerometer.csv", sensor_data);
            sensor_data = "X,Y,Z\n";
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
