package com.soft.nice.mqtt_client.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.soft.nice.mqtt_client.MqttClass;
import com.soft.nice.mqtt_client.R;
import com.soft.nice.mqtt_client.utils.Utils;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;

public class SubscribeFragment extends Fragment {
    View view;
    private TextInputEditText editText_client_topic;
    private RadioButton qos_0, qos_1, qos_2;
    private Button subscribe_btn;
    private ListView listSubscribe;
    private ArrayAdapter<String> SubscribeAdapter;
    private ArrayList<String> arrayList;
    private int qos_type;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(R.layout.fragment_subscribe,container,false);
        initView();
        initData();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ( listSubscribe.getAdapter() == null) {
            notifyDataSetChanged();
        }
    }

    private void initView() {
        editText_client_topic = view.findViewById(R.id.editText_client_topic);
        qos_0 = view.findViewById(R.id.qos_0);
        qos_1 = view.findViewById(R.id.qos_1);
        qos_2 = view.findViewById(R.id.qos_2);
        subscribe_btn = view.findViewById(R.id.subscribe_btn);
        listSubscribe = view.findViewById(R.id.subscribe_list);
        arrayList = new ArrayList<>();
        isEmptyEditText();
        notifyDataSetChanged();
    }

    private void initData() {
        editText_client_topic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isEmptyEditText();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEmptyEditText();
            }
        });
        subscribe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MqttClass.client.isConnected()) {
                    Sub(getContext());
                    arrayList.add(0, "Topic: " + editText_client_topic.getText().toString() + "\n" + "Enabled");
                    SubscribeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, arrayList);   //////qos eklenecek. mesaj sırası tamam.
                    listSubscribe.setAdapter(SubscribeAdapter);
                }else{
                    Utils.showToast(getContext(), "The connection has been disconnected, please reconnect");
                }
            }
        });
    }

    public void isEmptyEditText() {
        if(editText_client_topic.getText().toString().trim().equals("")) {
            subscribe_btn.setEnabled(false);
        }else {
            subscribe_btn.setEnabled(true);
        }
    }

    //订阅
    private void Sub(final Context context) {
        if(qos_0.isChecked()) {
            qos_type = 0;
        }else if(qos_1.isChecked()) {
            qos_type = 1;
        }else if(qos_2.isChecked()) {
            qos_type = 2;
        }
        try {
            MqttClass.client.subscribe(editText_client_topic.getText().toString(), qos_type);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // 取消订阅
    private void UnSub() {
        try {
            IMqttToken unsubToken = MqttClass.client.unsubscribe(editText_client_topic.getText().toString());
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void notifyDataSetChanged() {//
        if (SubscribeAdapter == null) {
            SubscribeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, arrayList);   //////qos eklenecek. mesaj sırası tamam.
        }
        listSubscribe.setAdapter(SubscribeAdapter);
    }
}
