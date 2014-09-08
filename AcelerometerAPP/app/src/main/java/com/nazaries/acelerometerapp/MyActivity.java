package com.nazaries.acelerometerapp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import java.util.List;


public class MyActivity extends Activity implements SensorEventListener {

    private long last_update;
    private TextView x;
    private TextView y;
    private TextView z;
    private TextView theta;
    private TextView beta;
    private TextView gamma;
    private TextView az;
    private TextView delay;
    private SensorManager sm;
    private Sensor acelerometer;
    private Sensor gyroscope;
    private Sensor magnetic;
    float[] acelerometerValues;
    float[] magneticValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        acelerometerValues=new float[3];
        magneticValues=new float[3];

        x=(TextView)findViewById(R.id.acX);
        y=(TextView)findViewById(R.id.acY);
        z=(TextView)findViewById(R.id.acZ);
        theta=(TextView)findViewById(R.id.theta);
        beta=(TextView)findViewById(R.id.beta);
        gamma=(TextView)findViewById(R.id.gamma);
        az=(TextView)findViewById(R.id.az);
        delay=(TextView)findViewById(R.id.delay);

        x.setText("0.0");
        y.setText("0.0");
        z.setText("0.0");
        theta.setText("0.0");
        beta.setText("0.0");
        gamma.setText("0.0");
        az.setText("0.0");
        delay.setText("0 ns");

        last_update=0;

        sensorInit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorInit();
    }

    @Override
    protected void onStop() {
        sm.unregisterListener(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        long current_time,difference_time;

        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            synchronized (this) {
                acelerometerValues = sensorEvent.values;
                x.setText("X:" + sensorEvent.values[0]);
                y.setText("Y:" + sensorEvent.values[1]);
                z.setText("Z:" + sensorEvent.values[2]);
                current_time=sensorEvent.timestamp;
                difference_time=(current_time-last_update)/1000000;
                last_update=current_time;
                delay.setText("Delay:"+difference_time+" ms");
            }
        }else if(sensorEvent.sensor.getType()==Sensor.TYPE_GYROSCOPE){
            synchronized (this) {
                theta.setText("Theta:" + sensorEvent.values[0]);
                beta.setText("Beta:" + sensorEvent.values[1]);
                gamma.setText("Gamma:" + sensorEvent.values[2]);
                current_time=sensorEvent.timestamp;
            }
        }else if(sensorEvent.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            synchronized (this){
                magneticValues=sensorEvent.values;
                current_time=sensorEvent.timestamp;
            }
        }else{

        }

        az.setText("Azimut:"+getAzimut());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void sensorInit(){
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        acelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetic = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (acelerometer!=null) {
            sm.registerListener(this, acelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (gyroscope!=null) {
            sm.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (magnetic!=null) {
            sm.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private float getAzimut(){
        float[] orientation= new float[3];
        float[] rotation= new float[9];

        SensorManager.getRotationMatrix(rotation, null, acelerometerValues,magneticValues);

        orientation=SensorManager.getOrientation(rotation,orientation);

        return orientation[0];
    }
}
