package com.example.adbroslan.bottomnavigtaion2;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import helpers.MQTTHelper;

public class tempFragment extends Fragment {
    MQTTHelper mqttHelper;
    TextView dataReceived,updateTime,last1;
    ProgressBar progressBar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_temp,container,false);
        dataReceived = v.findViewById(R.id.dataTemp);
        progressBar = v.findViewById(R.id.progressBar1);
        updateTime = v.findViewById(R.id.updateTime);
        last1 = v.findViewById(R.id.last1);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        startMqtt();
        super.setUserVisibleHint(true);
        return v;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        mqttHelper = new MQTTHelper(getActivity().getApplicationContext());
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){
            Log.w("MyFragment", "Fragment is visible.");
        }
        else{
            mqttHelper.unsubscribe();
            mqttHelper.disconnect();
            Log.w("MyFragment", "Fragment is not visible.");

        }
    }

    private void startMqtt(){
        mqttHelper = new MQTTHelper(getActivity().getApplicationContext());
        mqttHelper.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("Debug","Connected");
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Log.w("Debug","Disconnected");
                dataReceived.setText("n/a");
                dataReceived.setVisibility(TextView.VISIBLE);
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String time = sdf.format(currentTime.getTime());

                Log.w("DebugTemp",mqttMessage.toString());
                dataReceived.setText(mqttMessage.toString()+" °C");
                dataReceived.setVisibility(TextView.VISIBLE);
                progressBar.setVisibility(ProgressBar.INVISIBLE);

                updateTime.setText(time.toString());
                updateTime.setVisibility(TextView.VISIBLE);
                last1.setVisibility(TextView.VISIBLE);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
}
