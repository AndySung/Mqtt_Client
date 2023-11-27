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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;
import com.soft.nice.mqtt_client.MqttClass;
import com.soft.nice.mqtt_client.R;
import com.soft.nice.mqtt_client.utils.MyListView;
import com.soft.nice.mqtt_client.utils.Utils;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;

public class PublishFragment extends Fragment {
    View view;
    private TextInputEditText editText_client_topic;
    private TextInputEditText editText_client_message;
    private CheckBox retain_check;
    private RadioButton qos_0, qos_1, qos_2;
    private Button publish_btn;
    private MyListView listPublish;
    private ArrayAdapter<String> PublishAdapter;
    private ArrayList<String> arrayList;
    private int qos_type;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(R.layout.fragment_publish,container,false);
        initView();
        initData();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ( listPublish.getAdapter() == null) {
            notifyDataSetChanged();
        }
    }

    private void initView() {
        editText_client_topic = view.findViewById(R.id.editText_client_topic);
        editText_client_message = view.findViewById(R.id.editText_client_message);
        retain_check = view.findViewById(R.id.retain_check);
        qos_0 = view.findViewById(R.id.qos_0);
        qos_1 = view.findViewById(R.id.qos_1);
        qos_2 = view.findViewById(R.id.qos_2);
        publish_btn = view.findViewById(R.id.publish_btn);
        listPublish = view.findViewById(R.id.publish_list);
        arrayList = new ArrayList<>();
        isEmptyEditText();
        notifyDataSetChanged();
    }

    public void isEmptyEditText() {
        if(editText_client_topic.getText().toString().trim().equals("") || editText_client_message.getText().toString().trim().equals("")) {
            publish_btn.setEnabled(false);
        }else {
            publish_btn.setEnabled(true);
        }
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

        editText_client_message.addTextChangedListener(new TextWatcher() {
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

        publish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MqttClass.client.isConnected()) {
                    Pub(getContext(), retain_check.isChecked());
                    arrayList.add(0, "Topic: " + editText_client_topic.getText().toString() + "\n" + "Message: " + editText_client_message.getText().toString());
                    PublishAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, arrayList);   //////qos eklenecek. mesaj sırası tamam.
                    listPublish.setAdapter(PublishAdapter);
                }else{
                    Utils.showToast(getContext(), "The connection has been disconnected, please reconnect");
                }
            }
        });
    }

    private void Pub(Context context, boolean isRetainChecked) {
        if(qos_0.isChecked()) {
            qos_type = 0;
        }else if(qos_1.isChecked()) {
            qos_type = 1;
        }else if(qos_2.isChecked()) {
            qos_type = 2;
        }
        try {
            MqttClass.client.publish(editText_client_topic.getText().toString(), editText_client_message.getText().toString().getBytes(), qos_type, isRetainChecked);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void notifyDataSetChanged() {//
        if (PublishAdapter == null) {
            PublishAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, arrayList);   //////qos eklenecek. mesaj sırası tamam.
        }
        listPublish.setAdapter(PublishAdapter);
    }
}
