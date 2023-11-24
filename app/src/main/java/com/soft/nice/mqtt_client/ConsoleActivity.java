package com.soft.nice.mqtt_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.soft.nice.mqtt_client.fragment.DashboardFragment;
import com.soft.nice.mqtt_client.fragment.FragmentClass;
import com.soft.nice.mqtt_client.fragment.PublishFragment;
import com.soft.nice.mqtt_client.fragment.SubscribeFragment;

public class ConsoleActivity extends AppCompatActivity {

    private ImageView dashboard_img, subscribe_img, publish_img;
    private TextView dashboard_text, subscribe_text, publish_text;
    MqttClass mqttClass = new MqttClass(ConsoleActivity.this);
    private FirebaseAuth mAuth;
    FragmentClass fragmentClass = new FragmentClass(ConsoleActivity.this);
    LinearLayout btnSub, btn_DashBoard;
    LinearLayout btnPub;
    FirebaseFirestore fireDB = FirebaseFirestore.getInstance();
//    ProgressBar progressBar;
    DashboardFragment myDashboardFragment;
    PublishFragment myPublishFragment;
    SubscribeFragment mySubscribeFragment;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getColor(R.color.bg_color));
        //设置字体黑色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_console);
        mAuth = FirebaseAuth.getInstance();
        if (savedInstanceState == null) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fragmentClass.ChangeFragment(new DashboardFragment());
//                    LayoutName();
//                    progressBar=findViewById(R.id.progressInfo);
//                    progressBar.setVisibility(View.INVISIBLE);
                }
            }, 1000);
        }
        btn_DashBoard = findViewById(R.id.btnDashboard);
        btnPub = findViewById(R.id.btnPublish);
        btnSub = findViewById(R.id.btnSubscribe);
        dashboard_img = findViewById(R.id.dashboard_img);
        subscribe_img = findViewById(R.id.subscribe_img);
        publish_img = findViewById(R.id.publish_img);
        dashboard_text = findViewById(R.id.dashboard_text);
        subscribe_text = findViewById(R.id.subscribe_text);
        publish_text = findViewById(R.id.publish_text);
        myDashboardFragment = new DashboardFragment();
        myPublishFragment =   new PublishFragment();
        mySubscribeFragment =   new SubscribeFragment();

        switchButton(true,false,false);
        btnPub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchButton(false,false,true);
                fragmentClass.ChangeFragment(myPublishFragment);
                setTitle("Publish");
            }
        });
        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchButton(false,true,false);
                fragmentClass.ChangeFragment(mySubscribeFragment);
                setTitle("Subscribe");
            }
        });
        btn_DashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    switchButton(true,false,false);
                    fragmentClass.ChangeFragment(myDashboardFragment);
                  //  LayoutName();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.cont_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//menu
//        if (item.getItemId() == R.id.log_out) {
//            try {
//                mAuth = FirebaseAuth.getInstance();
//                FirebaseUser currentUser = mAuth.getCurrentUser();
//                mqttClass.ManageUsers();
//                currentUser.delete();
//                mAuth.signOut();
//                finish();
//                mqttClass.Disconnect(getApplicationContext());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mqttClass.Disconnect(getApplicationContext());
//        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mqttClass.ManageUsers();
        currentUser.delete();
        mAuth.signOut();
        finish();

    }

   /* void LayoutName() {
        fireDB.collection("Users")
                .whereEqualTo("UserID", mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override

                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String servername = (String) document.get("ServerName");
                                setTitle(servername);
                            }
                        }
                    }
                });
    }*/

    @SuppressLint("NewApi")
    public void switchButton(boolean dashboardChecked, boolean subscribeChecked, boolean publishChecked) {
        if(dashboardChecked ) {
            dashboard_img.setImageResource(R.mipmap.dashboard_select);
            dashboard_text.setTextColor(getColor(R.color.blue));
            subscribe_img.setImageResource(R.mipmap.subscribe_unselect);
            subscribe_text.setTextColor(getColor(R.color.gray));
            publish_img.setImageResource(R.mipmap.publish_unselect);
            publish_text.setTextColor(getColor(R.color.gray));
        }else if(subscribeChecked){
            subscribe_img.setImageResource(R.mipmap.subscribe_select);
            subscribe_text.setTextColor(getColor(R.color.blue));
            dashboard_img.setImageResource(R.mipmap.dashboard_unselect);
            dashboard_text.setTextColor(getColor(R.color.gray));
            publish_img.setImageResource(R.mipmap.publish_unselect);
            publish_text.setTextColor(getColor(R.color.gray));
        }else if(publishChecked) {
            publish_img.setImageResource(R.mipmap.publish_select);
            publish_text.setTextColor(getColor(R.color.blue));
            dashboard_img.setImageResource(R.mipmap.dashboard_unselect);
            dashboard_text.setTextColor(getColor(R.color.gray));
            subscribe_img.setImageResource(R.mipmap.subscribe_unselect);
            subscribe_text.setTextColor(getColor(R.color.gray));
        }
    }
}




