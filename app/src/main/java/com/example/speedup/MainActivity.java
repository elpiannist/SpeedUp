package com.example.speedup;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements LocationListener{
    int rssis;
    String heart_rate;
    BluetoothGattCharacteristic bluetoothGattCharacteristic;
    BluetoothGattDescriptor bluetoothGattDescriptor;
    BluetoothDevice bluetoothDevice;
    BluetoothGatt bluetoothGatt;
    BluetoothAdapter bluetoothAdapter;
    BluetoothGattService bluetoothGattService;
    List<BluetoothGattService> bluetoothGattServices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
         /*
         UI Section
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = findViewById(R.id.settings);
        /*

        Bluetooth Section
         */
        final BluetoothManager bluetoothManager =(BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter=bluetoothManager.getAdapter();
        bluetoothDevice = bluetoothAdapter.getRemoteDevice("C5:72:5B:33:12:3B");
        bluetoothGatt = bluetoothDevice.connectGatt(this,true, bluetoothGattCallback);
        bluetoothGatt.discoverServices();

        /*
        GPS Section
         */
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, this);
        }
        catch (SecurityException e ){
            TextView text_speed = findViewById(R.id.current_speed);
            text_speed.setText("No location permission");
        }



        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                bluetoothGatt.discoverServices();
                if (bluetoothGattServices != null) {
                    if (bluetoothGattService == null) {
                        for (BluetoothGattService bluetoothGattServ : bluetoothGattServices) {
                            if (bluetoothGattServ.getUuid().toString().equals("0000180d-0000-1000-8000-00805f9b34fb")) {
                                bluetoothGattService = bluetoothGattServ;
                                bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"));
                                bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic,true);
                                bluetoothGattDescriptor = bluetoothGattCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                                bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
                            }
                        }
                    }


                    bluetoothGatt.readCharacteristic(bluetoothGattCharacteristic);
                    bluetoothGatt.readRemoteRssi();

                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        TextView text_speed = findViewById(R.id.current_speed);
        double thespeed=location.getSpeed()*3.6;
        if(thespeed<1.0)thespeed=0.0;
        String formatted = String.format("%.1f",thespeed);
        text_speed.setText(formatted+" km/h");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback(){
        @Override
        public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic){
            heart_rate = String.valueOf(bluetoothGattCharacteristic.getValue()[1])+" bpm";
          TextView textViews = findViewById(R.id.heart_rate);
           textViews.setText(heart_rate);
        }
        @Override
        public void onServicesDiscovered(BluetoothGatt bluetoothGatt,int status){
            bluetoothGattServices=bluetoothGatt.getServices();
        }
    };
}
