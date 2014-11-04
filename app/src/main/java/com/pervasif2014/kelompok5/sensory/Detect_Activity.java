package com.pervasif2014.kelompok5.sensory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pervasif2014.kelompok6.sensory.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Detect_Activity extends Activity implements SensorEventListener{

    private SensorManager sensorM;
    private  KNN knn = new KNN();
    private double xA=0,yA=0,zA=0,xG=0,yG=0,zG=0,xL=0,yL=0,zL=0;
    int counter =0;
    boolean ad = true;

    Timer timer;
    public static int WAKTU_KIRIM = 5000; //milisecond
    postDataTask task;
    TextView statuslabel;
    ImageView gambar;

    private static String URL = "http://192.168.43.237/sensor/add_marker.php";
    private String text_response = "";
    private String timestamp = "";
    private String aktivitas = "Start";
    private String nama = "";
    private Location loc;
    public double latitude = -7.27983, longitude = 112.79749, altitude = -20;

    LocationManager locationManager, mlocManager;
    LocationListener mlocListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detect_);

        AssetManager asm = getAssets();
        sensorM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //Sensor sensor = sensorM.getDefaultSensor(Sensor.TYPE_ALL);
        InputStream is = null;
        try {
            is = asm.open("input3.arff");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            knn.init(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        statuslabel = (TextView) findViewById(R.id.status_activity);
        gambar = (ImageView) findViewById (R.id.gambar);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), false);
        loc = locationManager.getLastKnownLocation(provider);

        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

        inputNama();
    }

    public void inputNama() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        nama = sharedPreferences.getString("nama", "");

        String message;
        if(nama.equals(""))
            message = "Selamat datang!\nMasukkan nama Anda";
        else
            message = "Nama sebelumnya : " + nama + "\nNama nya bisa diganti kok :)";

        final EditText input = new EditText(this);
        input.setHint("Masukkan nama Anda");
        input.setInputType(96); // TYPE_TEXT_VARIATION_PERSON_NAME
        input.setText(nama);

        new AlertDialog.Builder(Detect_Activity.this)
                .setTitle("Hello")
                .setMessage(message)
                .setView(input)
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(input.getText().toString().trim().isEmpty()) {
                            inputNama();
                        }
                        else {
                            nama = input.getText().toString().trim();
                            sharedPreferences.edit().putString("nama",nama).apply();
                            timer = new Timer();
                            task = new postDataTask();
                            timer.schedule(task,0,WAKTU_KIRIM);
                        }
                        TextView name = (TextView) findViewById(R.id.name);
                        name.setText(nama);

                    }
                }).setNegativeButton("Keluar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                locationManager.removeUpdates(mlocListener);
                finish();
            }
        }).setCancelable(false).show();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
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

            // TextView xlabel = (TextView) findViewById(R.id.AcX_text);
            //  xlabel.setText("X Axis: " + String.format("%.02f",event.values[0]) + " m/s");
            //  TextView ylabel = (TextView) findViewById(R.id.AcY_Text);
            // ylabel.setText("Y Axis: " + String.format("%.02f",event.values[1]) + " m/s");
            //  TextView zlabel = (TextView) findViewById(R.id.AcZ_Text);
            //  zlabel.setText("Z Axis: " + String.format("%.02f",event.values[2]) + " m/s");
        }
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            xG+=event.values[0];
            yG+=event.values[1];
            zG+=event.values[2];
            //  TextView xlabel = (TextView) findViewById(R.id.GyX_Text);
            //  xlabel.setText("X Axis: " + String.format("%.02f",event.values[0]) );
            //  TextView ylabel = (TextView) findViewById(R.id.GyY_Text);
            //  ylabel.setText("Y Axis: " + String.format("%.02f",event.values[1]) );
            //  TextView zlabel = (TextView) findViewById(R.id.GyZ_Text);
            //  zlabel.setText("Z Axis: " + String.format("%.02f",event.values[2]) );
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


             if (status == 1.0) {
                aktivitas = "Berdiri";
                statuslabel.setText("Status : Berdiri");
                gambar.setImageResource(R.drawable.stand);

            } else if (status == 3.0) {
                aktivitas = "Duduk";
                statuslabel.setText("Status : Duduk");
                gambar.setImageResource(R.drawable.sit);

            }
            else {
                aktivitas = "Berjalan";
                statuslabel.setText("Status : Berjalan");
                gambar.setImageResource(R.drawable.walk);}

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

        final Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String ButtonText = start.getText().toString();
                // TODO Auto-generated method stub
                if(ButtonText.equals("Start"))
                {
                    statuslabel.setVisibility(View.VISIBLE);
                    gambar.setVisibility(View.VISIBLE);
                    start.setText("Stop");
                    ad = false;
                }
                else if (ButtonText.equals("Stop"))
                {
                    timer.cancel();
                    timer.purge();
                    start.setText("Start");
                    statuslabel.setVisibility(View.INVISIBLE);
                    gambar.setVisibility(View.INVISIBLE);

                }
            }
        });
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
        //sensorM.unregisterListener(this);
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Keluar?")
                .setMessage("Keluar dari aplikasi?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        timer.cancel();
                        timer.purge();
                        locationManager.removeUpdates(mlocListener);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // kosong
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    class postDataTask extends TimerTask {

        @Override
        public void run() {
            //System.out.println(aktivitas);
            //implement da post function here

            timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            latitude = loc.getLatitude();
            longitude = loc.getLongitude();
            altitude = loc.getAltitude();
            try {
                postData(URL);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void postData(String url) throws JSONException {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        JSONObject json = new JSONObject();

        try {
            // JSON data:
            json.put("timestamp", timestamp);
            json.put("nama", nama);
            json.put("aktivitas", aktivitas);
            json.put("latitude", latitude);
            json.put("longitude", longitude);
            json.put("altitude", altitude);

            JSONArray postjson=new JSONArray();
            postjson.put(json);

            // Post the data:
            httppost.setHeader("json",json.toString());
            httppost.getParams().setParameter("jsonpost",postjson);

            // Execute HTTP Post Request
            //System.out.print(json);
            HttpResponse response = httpclient.execute(httppost);

            // for JSON:
            if(response != null)
            {
                InputStream is = response.getEntity().getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();

                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        sb.append("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                text_response = sb.toString();
            }

        }catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            loc = location;
            //Log.e("loc",loc.getLatitude()+"");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS Disabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText( getApplicationContext(),"GPS Enabled",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    /*  REFERENCES
     *  http://stackoverflow.com/questions/4597690/android-timer-how
     */
}
