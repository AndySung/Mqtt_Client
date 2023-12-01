package com.soft.nice.mqtt_client.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.soft.nice.mqtt_client.MqttClass;
import com.soft.nice.mqtt_client.R;
import com.soft.nice.mqtt_client.adapter.Message;
import com.soft.nice.mqtt_client.adapter.MessageListViewAdapter;
import com.soft.nice.mqtt_client.utils.TimeUtils;
import com.soft.nice.mqtt_client.utils.Utils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
public class DashboardFragment extends Fragment {
    View view;
    private ImageButton connect_btn, more_btn;
    private FirebaseAuth mAuth;
    FirebaseFirestore fireDB = FirebaseFirestore.getInstance();
    String userID = "", serverName = "", serverHost = "", userName = "", userPassword;
    TextView host_text, connect_state;
    ListView MessageListInfo;
    MessageListViewAdapter adapter;
    List<Message> arrayList;
    RelativeLayout host_layout;
    MqttClass mqttClass = new MqttClass(getContext());
    Boolean isConnected = true;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_dashboard, container, false);
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
        connect_btn = view.findViewById(R.id.connect_btn);
        more_btn = view.findViewById(R.id.more_btn);
        host_text = view.findViewById(R.id.host_text);
        connect_state = view.findViewById(R.id.connect_state);
        MessageListInfo = view.findViewById(R.id.dashboard_list);
        host_layout = view.findViewById(R.id.host_layout);
        arrayList = new ArrayList<>();
        getInfo(getContext());
        notifyDataSetChanged();
        getMessage(getContext());
    }

    private void initData() {
        more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(host_layout);
            }
        });

        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isConnected) {
                    //重新连接
                    if(userName == null) {  //没有用户认证
                        mqttClass.ConNotUser(getContext(), serverHost, serverName, false, false,false);
                    }else { //有用户认证
                        mqttClass.ConUser(getContext(), serverHost, userName, userPassword, serverName, false, true,false);
                    }
                    connect_btn.setImageResource(R.mipmap.stop_connect_icon);
                    connect_state.setText("Connected");
                    isConnected = true;
                    getMessage(getContext());
                }else {
                    //断开连接
                    try {
                        mAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        mqttClass.ManageUsers();
                        mAuth.signOut();
                        mqttClass.Disconnect(getContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    connect_btn.setImageResource(R.mipmap.reconnect_icon);
                    connect_state.setText("Disconnected");
                    isConnected = false;
                }
            }
        });
    }

    void getInfo(final Context context) {
        mAuth = FirebaseAuth.getInstance();
        fireDB.collection("Users")
                .whereEqualTo("UserID", mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userID = (String) document.get("UserID");
                                serverName = (String) document.get("ServerName");
                                serverHost = (String) document.get("ServerAddress");
                                userName = (String) document.get("ServerUser");
                                userPassword = (String) document.get("UserPassword");
                            }
                            if(!serverHost.equals("")) {
                                host_text.setText(Utils.SplitTwoPartString(serverHost, "tcp://", 1));
                            }
                        }
                    }
                });
        if (MqttClass.client.isConnected()) {
            connect_state.setText("Connected");
            connect_btn.setImageResource(R.mipmap.stop_connect_icon);
            isConnected = true;
        } else {
            connect_state.setText("Disconnected");
            connect_btn.setImageResource(R.mipmap.reconnect_icon);
            isConnected = false;
        }
    }

    public void getMessage(final Context context) {
        MqttClass.client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Message msg = new Message(new String(message.getPayload()), topic, "QoS " + message.getQos(), TimeUtils.getNowTime());
                arrayList.add(msg);
                /**
                 * @param lhs  @param rhs
                 * @return an integer < 0 if lhs is less than rhs, 0 if they are
                 *         equal, and > 0 if lhs is greater than rhs,比较数据大小时,这里比的是时间
                 */
                Collections.sort(arrayList, new Comparator<Message>() {
                    @Override
                    public int compare(Message lhs, Message rhs) {
                        Date date1 = TimeUtils.stringToDate(lhs.getMessage_date());
                        Date date2 = TimeUtils.stringToDate(rhs.getMessage_date());
                        // 对日期字段进行升序，如果欲降序可采用after方法
                        if (date1.before(date2)) {
                            return 1;
                        }
                        return -1;
                    }
                });
                adapter = new MessageListViewAdapter(getContext(), arrayList);
                MessageListInfo.setAdapter(adapter);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    private void notifyDataSetChanged() {//
        if (adapter == null) {
            adapter = new MessageListViewAdapter(getContext(), arrayList);
        }
        MessageListInfo.setAdapter(adapter);
    }

    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.setGravity(Gravity.RIGHT);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.cont_menu, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.clear_msg){
                    arrayList.clear();
                    adapter.notifyDataSetChanged();
                }else {
                    try {
                        mAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        mqttClass.ManageUsers();
                        currentUser.delete();
                        mAuth.signOut();
                        getActivity().finish();
                        mqttClass.Disconnect(getContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        popupMenu.show();
    }
}
