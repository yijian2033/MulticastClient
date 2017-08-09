package com.jian.multicastclient;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView mTvShow;

    private static final int PORT = 8080;
    private static final String IP = "224.224.224.224";
    private MulticastSocket socket;
    private InetAddress address;
    private DatagramPacket datagramPacket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvShow = (TextView) findViewById(R.id.tv);
        try {
            socket = new MulticastSocket(PORT);
            address = InetAddress.getByName(IP);
            socket.setTimeToLive(4);
            byte[] bys = new byte[1024];
            datagramPacket = new DatagramPacket(bys, 0, bys.length, address, PORT);
            socket.joinGroup(address);
            Executors.newScheduledThreadPool(2).scheduleAtFixedRate(new WorkThread(), 0, 5, TimeUnit.SECONDS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class WorkThread implements Runnable {


        @Override
        public void run() {
            Log.e("john", "开始循环...");
            try {
                socket.receive(datagramPacket);
                final String result = new String(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mTvShow.setText(result);
                        Toast.makeText(getApplicationContext(), "接收结果：" + result, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
