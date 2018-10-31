package com.sgp.mardave;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.akaita.android.circularseekbar.CircularSeekBar;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // UI Variables
    SeekBar sbPower;
    Button btnR;
    Button btnN;
    Button btnD;

    int progress = 0;

    // Bluetooth Variable
    private BluetoothAdapter mBluetoothAdapter = null;
    private Set<BluetoothDevice> pairedDevices;

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;

    private final String DEVICE_ADDRESS = ""; //MAC Address of Bluetooth Module
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    boolean connected = true;

    private int currentApiVersion;


    // Circular Seek Bar variables
    float lastCircularSeekBarValue = 5;
    float progressCircularSeekBar = 0;
    byte buffer[];
    int bufferPosition;
    boolean stopThread;

    @Override
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To hide title bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // Initializing Immersive Mode
        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }

        setContentView(R.layout.activity_main);

        if(BTinit())
        {
            BTconnect();
        }

        // Drive Mode
        btnR = (Button) findViewById(R.id.btnR);
        btnN = (Button) findViewById(R.id.btnN);
        btnD = (Button) findViewById(R.id.btnD);


        final CircularSeekBar seekBar = (CircularSeekBar) findViewById(R.id.seekbar);
        seekBar.setProgressTextFormat(new DecimalFormat("###,###,##0.00"));
        seekBar.setRingColor(Color.BLACK);
        //seekBar.setVisibility(View.GONE);

        btnR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToR();

                btnR.setBackgroundColor(Color.rgb(219, 50, 54));

                btnN.setBackgroundColor(Color.rgb(210, 210, 210));
                btnD.setBackgroundColor(Color.rgb(210, 210, 210));
            }
        });

        btnN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToN();

                btnN.setBackgroundColor(Color.rgb(72, 133, 237));

                btnR.setBackgroundColor(Color.rgb(210, 210, 210));
                btnD.setBackgroundColor(Color.rgb(210, 210, 210));
            }
        });

        btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToD();

                btnD.setBackgroundColor(Color.rgb(60, 186, 84));

                btnR.setBackgroundColor(Color.rgb(210, 210, 210));
                btnN.setBackgroundColor(Color.rgb(210, 210, 210));
            }
        });


        // Steering Wheel
        seekBar.setOnCircularSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar seekBar, float i, boolean b) {
                progressCircularSeekBar = i;
                if(progressCircularSeekBar<5){
                    turnLeft();
                }else if(progressCircularSeekBar>5){
                    turnRight();
                }else if(progressCircularSeekBar==5){
                    setStraight();
                }
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                seekBar.setProgress(5);

            }
        });


        // Power
        sbPower = (SeekBar) findViewById(R.id.sbPower);
        sbPower.setMax(9);
        sbPower.setProgress(progress);


        sbPower.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                String power = "" + progress;
                if (socket != null) {
                    try {
                        socket.getOutputStream().write(power.toString().getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBluetoothAdapter.disable();
    }

    //Initializes Bluetooth module
    public boolean BTinit()
    {
        boolean found = false;

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null) //Checks if the device supports bluetooth
        {
            Toast.makeText(getApplicationContext(), "Device doesn't support bluetooth", Toast.LENGTH_SHORT).show();
        }

        if(!bluetoothAdapter.isEnabled()) //Checks if bluetooth is enabled. If not, the program will ask permission from the user to enable it
        {
            bluetoothAdapter.enable();
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        if(bondedDevices.isEmpty()) //Checks for paired bluetooth devices
        {
            Toast.makeText(getApplicationContext(), "Please pair the device first", Toast.LENGTH_SHORT).show();
        }
        else
        {
            for(BluetoothDevice iterator : bondedDevices)
            {
                if(iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                    device = iterator;
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    public boolean BTconnect()
    {
        boolean connected = true;

        try
        {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Creates a socket to handle the outgoing connection
            socket.connect();

            Toast.makeText(getApplicationContext(),
                    "Connection to Mardave successful", Toast.LENGTH_LONG).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            connected = false;
        }

        if(connected)
        {
            try
            {
                outputStream = socket.getOutputStream(); // Gets the output stream of the socket
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        return connected;
    }


    // Command Functions
    public void setToR() {
        if (socket != null) {
            try {
                socket.getOutputStream().write("R".toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setToN() {
        if (socket != null) {
            try {
                socket.getOutputStream().write("N".toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setToD() {
        if (socket != null) {
            try {
                socket.getOutputStream().write("D".toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setStraight() {
        if (socket != null) {
            try {
                socket.getOutputStream().write("S".toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void turnLeft() {
        if (socket != null) {
            try {
                socket.getOutputStream().write("Q".toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void turnRight() {
        if (socket != null) {
            try {
                socket.getOutputStream().write("E".toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
