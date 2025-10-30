package com.bipinexports.productionqr.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bipinexports.productionqr.R;

import java.util.ArrayList;
import java.util.Set;

public class PairedDeviceActivity extends AppCompatActivity {
  private ListView listView;
  private ArrayList<String> mDeviceList = new ArrayList<>();

private void getBluetoothPairedDevices(final ArrayList<String> deviceList, final ListView listView){
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (bluetoothAdapter == null) {
        Toast.makeText(getApplicationContext(), "This device not support bluetooth", Toast.LENGTH_LONG).show();
    } else {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
        }
        Set<BluetoothDevice> all_devices = bluetoothAdapter.getBondedDevices();
        if (all_devices.size() > 0) {
            for (BluetoothDevice currentDevice : all_devices) {
                deviceList.add("Device Name: "+currentDevice.getName() + "\nDevice Address: " + currentDevice.getAddress());
                listView.setAdapter(new ArrayAdapter<>(getApplication(),
                        android.R.layout.simple_list_item_1, deviceList));
            }
        }
    }
}

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_paired_device);
    listView = findViewById(R.id.listView);
    getBluetoothPairedDevices(mDeviceList,listView);
 }
}