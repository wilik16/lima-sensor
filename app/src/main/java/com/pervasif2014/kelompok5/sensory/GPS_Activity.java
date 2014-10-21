package com.pervasif2014.kelompok5.sensory;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pervasif2014.kelompok6.sensory.R;

public class GPS_Activity extends Activity {

    private String sensor_data="";
    private boolean record_data;
    private LocationManager locationManager;
    private Location loc;
    TextView lat_text, lng_text, alt_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_);

        lat_text = (TextView) findViewById(R.id.lat_text);
        lng_text = (TextView) findViewById(R.id.lng_text);
        alt_text = (TextView) findViewById(R.id.alt_text);

        record_data=false;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), false);
        loc = locationManager.getLastKnownLocation(provider);

        if(record_data)
        {
            sensor_data += loc.getLatitude() + "," + loc.getLongitude() + "," + loc.getAltitude() + "\n";
        }

        lat_text.setText("Latitude  : " + String.valueOf(loc.getLatitude()));
        lng_text.setText("Longitude : " + String.valueOf(loc.getLongitude()));
        alt_text.setText("Altitude  : " + String.valueOf(loc.getAltitude()));

        LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener mlocListener = new MyLocationListener();
        mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
    }

    public void gps_csv_btn_clicked(View view) {
        if(!record_data) {
            record_data=true;
            Button csv_btn = (Button)findViewById(R.id.save_csv_btn);
            csv_btn.setText("Sedang merekam, klik lagi untuk berhenti");
        }
        else {
            record_data=false;
            Button csv_btn = (Button)findViewById(R.id.save_csv_btn);
            csv_btn.setText("Simpan ke CSV");
            MainMenu.write_csv("gps.csv", sensor_data);
            sensor_data = "";
        }
    }

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            loc = location;

            if(record_data)
            {
                sensor_data += loc.getLatitude() + "," + loc.getLongitude() + "," + loc.getAltitude() + "\n";
            }

            lat_text.setText("Latitude  : " + String.valueOf(loc.getLatitude()));
            lng_text.setText("Longitude : " + String.valueOf(loc.getLongitude()));
            alt_text.setText("Altitude  : " + String.valueOf(loc.getAltitude()));
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
}
