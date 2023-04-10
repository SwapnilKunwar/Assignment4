package com.example.assignment4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final float TODO = 0;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor proxSensor;
    private Sensor geoSensor;

    private DatabaseHelper databaseHelper;
    private DatabaseHelper2 databaseHelper2;
    private DatabaseHelper3 databaseHelper3;
    private SensorEventListener lightSensorListener;
    private SensorEventListener proxSensorListener;
    private SensorEventListener geoSensorListener;

    private float[] mRotationMatrix = new float[9];
    private float[] mOrientationAngles = new float[3];

    private float mDeclination;
    private Switch s1, s2, s3;
    private TextView t1, t2, t3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper=DatabaseHelper.getDB(this);
        databaseHelper2=DatabaseHelper2.getDB2(this);
        databaseHelper3=DatabaseHelper3.getDB3(this);

        s1 = findViewById(R.id.switch1);
        s2 = findViewById(R.id.switch4);
        s3 = findViewById(R.id.switch5);
        t1 = findViewById(R.id.textView);
        t2 = findViewById(R.id.textView4);
        t3 = findViewById(R.id.textView5);
        s1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                    lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
                    lightSensorListener = new SensorEventListener() {
                        public void onSensorChanged(SensorEvent event) {
                            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                                float lightLevel = event.values[0];

                                if(lightLevel<=30) {
                                    databaseHelper2.lightDao().addTx(
                                            new Light(lightLevel));
                                    Log.d("Light sensor value when phone is placed down", "Light level: " + lightLevel);
                                }

                                t1.setText("current value : " + String.valueOf(lightLevel));
                            }
                        }

                        @Override
                        public void onAccuracyChanged(Sensor sensor, int accuracy) {
                            // Do nothing
                        }
                    };
                    sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_FASTEST);
                } else {
                    t1.setText("");
                    sensorManager.unregisterListener(lightSensorListener);
                }
            }
        });
        s2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                    proxSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                    proxSensorListener = new SensorEventListener() {
                        public void onSensorChanged(SensorEvent event) {
                            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                                float proxlevel = event.values[0];
                                if (proxlevel == 0) {
                                    Log.d("Proximity value when phone is placed down", "Prox level: " + proxlevel);
                                }
                                if(proxlevel==0) {
                                    databaseHelper.proximityDao().addTx(
                                            new Proximity(proxlevel)
                                    );
                                }
                                t2.setText("current value : " + String.valueOf(proxlevel));
                            }
                        }

                        @Override
                        public void onAccuracyChanged(Sensor sensor, int accuracy) {
                            // Do nothing
                        }
                    };
                    sensorManager.registerListener(proxSensorListener, proxSensor, SensorManager.SENSOR_DELAY_FASTEST);
                } else {
                    t2.setText("");
                    sensorManager.unregisterListener(proxSensorListener);
                }
            }
        });

        s3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                    geoSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
                    mDeclination = getMagneticDeclination();


                    geoSensorListener = new SensorEventListener() {
                        public void onSensorChanged(SensorEvent event) {
                            if (event.sensor.getType() == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) {
                                // Get the rotation matrix from the sensor data
                                SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);

                                // Adjust the azimuth angle based on the magnetic declination
                                float azimuth = getAdjustedAzimuth(SensorManager.getOrientation(mRotationMatrix, mOrientationAngles)[0]);

                                // Update the UI or do something with the orientation data
                                Log.d("MainActivity", "Azimuth: " + azimuth + " degrees");
                                databaseHelper3.rotationDao().addTx(
                                        new Rotation(azimuth)
                                );
                                t3.setText("Rotate by   :  " + String.valueOf(azimuth));

                            }
                        }

                        @Override
                        public void onAccuracyChanged(Sensor sensor, int accuracy) {
                            // Do nothing
                        }
                    };
                    sensorManager.registerListener(geoSensorListener, geoSensor, SensorManager.SENSOR_DELAY_NORMAL);

                } else {


                }
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightSensorListener);
        sensorManager.unregisterListener(proxSensorListener);
    }

    private float getMagneticDeclination() {
        // Get the user's current location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return TODO;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Get the magnetic declination for the location
        GeomagneticField field = new GeomagneticField(
                (float) location.getLatitude(),
                (float) location.getLongitude(),
                (float) location.getAltitude(),
                System.currentTimeMillis());
        return field.getDeclination();
    }

    private float getAdjustedAzimuth(float azimuth) {
        // Adjust the azimuth angle based on the magnetic declination
        azimuth += mDeclination;
        if (azimuth < 0) {
            azimuth += 360;
        } else if (azimuth >= 360) {
            azimuth -= 360;
        }
        return azimuth;
    }

}
