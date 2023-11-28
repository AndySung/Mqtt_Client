package com.soft.nice.mqtt_client.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
    private RadioGroup subscribe_type_group;
    private RadioButton qos_0, qos_1, qos_2;
    private Button subscribe_btn;
    private ListView listSubscribe;
    private ArrayAdapter<String> SubscribeAdapter;
    private ArrayList<String> arrayList;
    private int qos_type;
    LocalBroadcastManager broadcastManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_subscribe,container,false);
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
        qos_0 = view.findViewById(R.id.qos_0);
        qos_1 = view.findViewById(R.id.qos_1);
        qos_2 = view.findViewById(R.id.qos_2);
        subscribe_type_group = view.findViewById(R.id.subscribe_type_group);
        subscribe_btn = view.findViewById(R.id.subscribe_btn);
        listSubscribe = view.findViewById(R.id.subscribe_list);
        qos_type = 0;
        arrayList = new ArrayList<>();
        isEmptyEditText();
        notifyDataSetChanged();
        /** 注册广播接收器 **/
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("subscribe_receive_data");
        broadcastManager.registerReceiver(subscribeBR, intentFilter);
    }

    /** 广播接收，重新连接后需要订阅所有的 topic **/
    BroadcastReceiver subscribeBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("subscribe_reconnected");
            if(msg.equals("reconnected")) {
                SubScribeAll();
            }
        }
    };

    private void initData() {
        subscribe_type_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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

        /** Topic edittext点击判断 **/
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

        /** 订阅按钮点击事件，通过topic进行订阅**/
        subscribe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MqttClass.client.isConnected()) {
                    Sub(getContext(), editText_client_topic.getText().toString());
                    if(arrayList.size() == 0 || !arrayList.contains("Topic: " + editText_client_topic.getText().toString() +"\nQoS: "+ qos_type + "\nEnabled")) {
                        arrayList.add(0, "Topic: " + editText_client_topic.getText().toString() + "\nQoS: "+ qos_type  + "\nEnabled");
                    }else{
                        Utils.showToast(getContext(), "This topic has been subscribed");
                    }
                    SubscribeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, arrayList);
                    listSubscribe.setAdapter(SubscribeAdapter);
                }else{
                    Utils.showToast(getContext(), "The connection has been disconnected, please reconnect");
                }
            }
        });

        /** Listview点击事件，目前没有功能, 可以用于订阅或者取消订阅**/
        listSubscribe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String TopicStr = Utils.SplitTwoPartString(adapterView.getAdapter().getItem(i).toString(), "\n",0);
                String QosStr = Utils.SplitTwoPartString(adapterView.getAdapter().getItem(i).toString(), "\n",1);
                String EnableStr = Utils.SplitTwoPartString(adapterView.getAdapter().getItem(i).toString(), "\n",2);
                String topic =  Utils.SplitTwoPartString(TopicStr, "pic: ",1);
                String qos = Utils.SplitTwoPartString(QosStr, "oS: ",1);
                Log.i("andysong--->>sub:", TopicStr+"\n"+QosStr+"\n"+EnableStr+"\n"+topic+"\n"+qos);
            }
        });

        /** Listview长按取消订阅，并删除 **/
        listSubscribe.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Tips").setMessage("Are you sure you want to cancel this subscription?").setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (arrayList.size() != 0) {
                            String TopicStr = Utils.SplitTwoPartString(adapterView.getAdapter().getItem(position).toString(), "\n",0);
                            String topic =  Utils.SplitTwoPartString(TopicStr, "pic: ",1);
                            UnSub(topic);
                            Utils.removeItem(arrayList, topic);
//                            arrayList.remove(position);
                            SubscribeAdapter.notifyDataSetChanged();
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

    /** topic为空，订阅按钮设置不可点击 **/
    public void isEmptyEditText() {
        if(editText_client_topic.getText().toString().trim().equals("")) {
            subscribe_btn.setEnabled(false);
        }else {
            subscribe_btn.setEnabled(true);
        }
    }

    /** topic订阅 **/
    private void Sub(final Context context, String topic) {
        try {
            MqttClass.client.subscribe(topic, qos_type);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /** 根据topic取消订阅 **/
    private void UnSub(String subscribeMsg) {
        try {
            IMqttToken unsubToken = MqttClass.client.unsubscribe(subscribeMsg);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Utils.showToast(getContext(), "UnSubscription successful");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /** 定义一个注销广播，在绑定的activity中进行销毁，因为我如果直接在onDestory中进行销毁，fragment切换时候就会被执行，这样就收不到广播了 **/
    public void UnRegisterBroadCast() {
        super.onDestroy();
        if(subscribeBR != null) {
            broadcastManager.unregisterReceiver(subscribeBR);
        }
    }

    private void notifyDataSetChanged() {
        if (SubscribeAdapter == null) {
            SubscribeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, arrayList);
        }
        listSubscribe.setAdapter(SubscribeAdapter);
    }

    private void SubScribeAll() {/**重新连接后，订阅所有订阅过的topic**/
        if(arrayList.size() != 0){
            for(int i = 0; i < arrayList.size(); i++){
                String TopicStr = Utils.SplitTwoPartString(arrayList.get(i).toString(), "\n",0);
                String topic =  Utils.SplitTwoPartString(TopicStr, "pic: ",1);
                Sub(getContext(), topic);
            }
        }
    }

}
