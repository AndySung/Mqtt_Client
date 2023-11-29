package com.soft.nice.mqtt_client;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.soft.nice.mqtt_client.utils.Utils;

public class MainActivity extends AppCompatActivity {
    TextInputEditText txtUser,txtPasswd, txtAddress, txtPort, txtName;
    private LinearLayout userLayout;
    Button button;
    private CheckBox userSw;
    String username;
    String passwd;
    String serverAdr;
    String serverName;
    MqttClass mqttClass = new MqttClass(MainActivity.this);
    FirebaseUser user;
    FirebaseFirestore fireDB = FirebaseFirestore.getInstance();

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getColor(R.color.bg_color));
        //设置字体黑色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main);
        mqttClass.mAuth = FirebaseAuth.getInstance();
        user = mqttClass.mAuth.getCurrentUser();
        CheckInternet();
        initView();
        initData();
    }

    public void CheckInternet(){
        if (user != null) {
            ControlAct();
        }
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
//            if (user != null) {
//                ControlAct();
//            }
//        } else {
//            Toast.makeText(MainActivity.this, "Please check internet connection.", Toast.LENGTH_SHORT).show();
//        }
    }

    public void initView() {
        txtUser = findViewById(R.id.txtUser);
        txtPasswd = findViewById(R.id.txtPasswd);
        txtAddress = findViewById(R.id.txtAddress);
        txtPort = findViewById(R.id.txtPort);
        txtName = findViewById(R.id.txtName);
        userSw = findViewById(R.id.userSw);
        userLayout = findViewById(R.id.layout_user);
        button = findViewById(R.id.btnConnect);
    }

    public void initData() {
        userSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showAnimation(isChecked);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            username = txtUser.getText().toString();
            passwd = txtPasswd.getText().toString();
            serverName = txtName.getText().toString();
            if (user != null) {
                mqttClass.ManageUsers();
                user.delete();
                mqttClass.mAuth.signOut();
            }
            if(txtAddress.getText().toString().equals("")) {
                Utils.showAlertDialog(MainActivity.this, "Tips", "Please enter Broker address.");
            }else if(txtPort.getText().toString().equals("")){
                Utils.showAlertDialog(MainActivity.this, "Tips", "Please enter Broker port.");
            }else {
                if(userSw.isChecked()) {
                    if(username.equals("")) {
                        Utils.showAlertDialog(MainActivity.this, "Tips", "please enter user name.");
                    }else if(passwd.equals("")){
                        Utils.showAlertDialog(MainActivity.this, "Tips", "please enter user password.");
                    }else{
                        IsEnableUserConnect(userSw.isChecked());
                    }
                }else{
                    IsEnableUserConnect(userSw.isChecked());
                }
            }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showAnimation(userSw.isChecked());
    }

    //显示动画
    public void showAnimation(boolean isShowAnim){
        if(isShowAnim) {
            TranslateAnimation showAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            showAnim.setDuration(500);
            userLayout.startAnimation(showAnim);
            userLayout.setVisibility(View.VISIBLE);
        }else{
            TranslateAnimation hideAnim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            hideAnim.setDuration(500);
            userLayout.startAnimation(hideAnim);
            userLayout.setVisibility(View.GONE);
        }
    }

    void IsEnableUserConnect(boolean isUserLogin) {
        serverAdr = "tcp://" + txtAddress.getText().toString() + ":" + txtPort.getText().toString();
        if(isUserLogin) {
            mqttClass.ConUser(getApplicationContext(), serverAdr, txtUser.getText().toString(), txtPasswd.getText().toString(), txtName.getText().toString(), false, userSw.isChecked(), true);
        }else {
            mqttClass.ConNotUser(getApplicationContext(), serverAdr, txtName.getText().toString(), false, userSw.isChecked(),true);
        }
    }

    void ControlAct() {
        fireDB.collection("Users")
            .whereEqualTo("UserID", mqttClass.mAuth.getUid())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String serverAddress = (String) document.get("ServerAddress");
                            String user = (String) document.get("ServerUser");
                            String password = (String) document.get("UserPassword");
                            Boolean mosquitto = (boolean) document.get("Mosquitto");
                            Boolean passwdAuth = (boolean) document.get("PasswordAuth");
                            if (mosquitto || passwdAuth) {
                                mqttClass.ReConUser(getApplicationContext(), serverAddress, user, password);

                            } else {
                                mqttClass.ReConNotUser(getApplicationContext(), serverAddress);
                            }
                        }
                    }
                }
            });
    }
}





