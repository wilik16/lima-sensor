package com.pervasif2014.kelompok5.sensory;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pervasif2014.kelompok6.sensory.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

public class MainMenu extends Activity {
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        fm = getFragmentManager();


    }

    public static void write_csv(String filename,String data) {
        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root+"/sensor_data");
        if(!dir.exists()){
            dir.mkdirs();
        }

        CharSequence date = DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date());
        File file = new File(dir,date+" "+filename);

        try {
            FileOutputStream fs = new FileOutputStream(file);
            fs.write(data.getBytes());
            fs.close();
            Toast.makeText(sensory.getAppContext(), "File CSV tersimpan\n"+file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void accelerometer_activity(View view) {
        Intent acc_intent = new Intent(MainMenu.this,Accelero_Activity.class);
        MainMenu.this.startActivity(acc_intent);
    }

    public void magnet_activity(View view) {
        Intent acc_intent = new Intent(MainMenu.this,GeoMagnetic_Activity.class);
        MainMenu.this.startActivity(acc_intent);
    }

    public void proximity_activity(View view) {
        Intent acc_intent = new Intent(MainMenu.this,Proximity_Activity.class);
        MainMenu.this.startActivity(acc_intent);
    }

    public void light_activity(View view) {
        Intent acc_intent = new Intent(MainMenu.this,Light_Activity.class);
        MainMenu.this.startActivity(acc_intent);
    }

    public void orientation_activity(View view) {
        Intent acc_intent = new Intent(MainMenu.this,Orientation_Activity.class);
        MainMenu.this.startActivity(acc_intent);
    }

    public void gyroscope_activity(View view) {
        Intent acc_intent = new Intent(MainMenu.this,Gyroscope_Activity.class);
        MainMenu.this.startActivity(acc_intent);
    }

    public void gps_activity(View view) {
        Intent acc_intent = new Intent(MainMenu.this,GPS_Activity.class);
        MainMenu.this.startActivity(acc_intent);
    }

    public void view_sensor_list() {
        SensorManager sensorM = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sensorM.getSensorList(Sensor.TYPE_ALL);
        String temp;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Daftar Sensor:");
        for (Sensor sensor : sensors) {
            temp = "\n- " + sensor.getName() + " by " + sensor.getVendor();
            stringBuilder.append(temp);
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getAllProviders();

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            stringBuilder.append("\n- GPS Sensor");
        } else {
            Log.d("TAG", "Tidak ada GPS");
        }

        sensorListDialog dialog = new sensorListDialog();
        Bundle args = new Bundle();
        args.putString("data",stringBuilder.toString());
        dialog.setArguments(args);
        dialog.show(fm,"dialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_sensor_list) {
            view_sensor_list();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
