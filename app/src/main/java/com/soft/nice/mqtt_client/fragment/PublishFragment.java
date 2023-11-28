package com.soft.nice.mqtt_client.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private RadioGroup publish_type_group;
    private RadioButton qos_0, qos_1, qos_2;
    private Button publish_save_btn, publish_btn;
    private MyListView listPublish;
    private ArrayAdapter<String> PublishAdapter;
    private ArrayList<String> arrayList;
    private int qos_type;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_publish,container,false);
            initView();
            initData();
        }else {
            //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误
            ViewGroup parent = (ViewGroup) view.getParent();
            if(parent != null) {
                parent.removeView(view);
            }
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        notifyDataSetChanged();
    }

    private void initView() {
        editText_client_topic = view.findViewById(R.id.editText_client_topic);
        editText_client_message = view.findViewById(R.id.editText_client_message);
        retain_check = view.findViewById(R.id.retain_check);
        publish_type_group = view.findViewById(R.id.publish_type_group);
        qos_0 = view.findViewById(R.id.qos_0);
        qos_1 = view.findViewById(R.id.qos_1);
        qos_2 = view.findViewById(R.id.qos_2);
        qos_type = 0;
        publish_btn = view.findViewById(R.id.publish_btn);
        listPublish = view.findViewById(R.id.publish_list);
        publish_save_btn = view.findViewById(R.id.publish_save_btn);
        arrayList = new ArrayList<>();
        isEmptyEditText();
        notifyDataSetChanged();
    }

    public void isEmptyEditText() {
        if(editText_client_topic.getText().toString().trim().equals("") || editText_client_message.getText().toString().trim().equals("")) {
            publish_btn.setEnabled(false);
            publish_save_btn.setEnabled(false);
        }else {
            publish_btn.setEnabled(true);
            publish_save_btn.setEnabled(true);
        }
    }

    private void initData() {
        publish_type_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton radioButton = view.findViewById(checkedId);
                if(radioButton.isChecked()) {
                    if(radioButton.getText().toString().equals("QoS 0")) {
                        qos_type = 0;
                    }else if(radioButton.getText().toString().equals("QoS 1")){
                        qos_type = 1;
                    }else if(radioButton.getText().toString().equals("QoS 2")){
                        qos_type = 2;
                    }
                }
            }
        });
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
                publishMsg(editText_client_topic.getText().toString(),editText_client_message.getText().toString(),qos_type, retain_check.isChecked());
            }
        });

        //保存消息
        publish_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(arrayList.size() == 0 || !arrayList.contains("Topic: " + editText_client_topic.getText().toString() + "\n" + "Message: " + editText_client_message.getText().toString() + "\nQoS: " + qos_type + "\nRetain: "+retain_check.isChecked())) {
                        arrayList.add(0, "Topic: " + editText_client_topic.getText().toString() + "\n" + "Message: " + editText_client_message.getText().toString() + "\nQoS: " + qos_type + "\nRetain: "+retain_check.isChecked());
                    }else{
                        Utils.showToast(getContext(), "This topic and message has been subscribed");
                    }
                    PublishAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, arrayList);
                    listPublish.setAdapter(PublishAdapter);
            }
        });

        listPublish.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String TopicStr = Utils.SplitTwoPartString(adapterView.getAdapter().getItem(i).toString(), "\n",0);
                String MessageStr = Utils.SplitTwoPartString(adapterView.getAdapter().getItem(i).toString(), "\n",1);
                String QosStr = Utils.SplitTwoPartString(adapterView.getAdapter().getItem(i).toString(), "\n",2);
                String RetainStr = Utils.SplitTwoPartString(adapterView.getAdapter().getItem(i).toString(), "\n",3);

                String topic =  Utils.SplitTwoPartString(TopicStr, "pic: ",1);
                String message = Utils.SplitTwoPartString(MessageStr, "age: ",1);
                String qos =  Utils.SplitTwoPartString(QosStr, "oS: ",1);
                String isRetainChecked = Utils.SplitTwoPartString(RetainStr, " ",1);
                if(isRetainChecked.equals("true")) {
                    publishMsg(topic, message, Integer.parseInt(qos), true);
                }else{
                    publishMsg(topic, message, Integer.parseInt(qos), false);
                }
            }
        });

        listPublish.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Tips").setMessage("Whether to delete this message?").setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (arrayList.size() != 0) {
                            arrayList.remove(position);
                            PublishAdapter.notifyDataSetChanged();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).create().show();
                return true;
            }
        });
    }

    public void publishMsg(String topic, String message,int qos_type, boolean isRetain) {
        if(MqttClass.client.isConnected()) {
            try {
                MqttClass.client.publish(topic, message.getBytes(), qos_type, isRetain);
                Utils.showToast(getContext(), "Message sent");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }else{
            Utils.showToast(getContext(), "The connection has been disconnected, please reconnect");
        }
    }

    private void notifyDataSetChanged() {//
        if (PublishAdapter == null) {
            PublishAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, arrayList);
        }
        listPublish.setAdapter(PublishAdapter);
    }
}
