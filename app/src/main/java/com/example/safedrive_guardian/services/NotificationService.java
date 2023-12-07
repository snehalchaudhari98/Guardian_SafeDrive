package com.example.safedrive_guardian.services;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.safedrive_guardian.MainActivity;
import com.example.safedrive_guardian.R;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationManagerCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Debug;
import android.util.Log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationService {

    private static final String CHANNEL_ID = "safe_drive_guardian";
    private Context context;


    // Snehal's FCM Token
    private String accessToken = "ya29.c.c0AY_VpZg1FG4LNbUiOQr3zAM8dMuxWmoQQGxglL-naNDx9jdk5Ho-wezbTt3N-J2yBeFNHRUwvQIl9iUpp9iscbWn46UJgrdVME6Jl2HmfDWtO5boBjmWsG3GD4C26XwuzEpPu6HEbVNsQqa7AWYZ--Yn5BSkL4P7XLRNxzMk1bO_3tfHD1bzLnEwLruqyU4vhfwutkhsitTj2cYjiZbxqx0X_Spj1XOLSSnVDrQzOd-dPk6T5G9TXdd53iuq6BpsBrePjC-y9FKF8LF_fEfc7nU9BeVI-IFC3v6njhcMVGNyHf5ERRWMo3JdFPjOG4GOGLI-Gu38YvkE05BhibZZ8nMIKikzJKQ25n-Mb6uCkuCwju_CRaGfLsfqL385KYnxMyOIIpyB8ciSqo9d4zshtzswO0Xo5xwckQVBMSap9fcl9J2UZgQ5dq_Bhpx05kU9_O4392RuoxJSdortWQtZoqOJdrinb2ahMr7S8ojOUbucBfeZmo2qoQqm9S04xiMqJlrwzwO1U0ZhkXYdum5yXxIa5suxXcuRFfih1UqYwtiRhnr-oIlvi-7any6jqzIujXxrMo7gtZn6hY6sF163gsJp6JuaOddBkaBV8sQ3SuJ8uSaWW08QcFlJ5adx3mO-gbrs6qMFnyFjIv4B77sOcidiarIYxjl11UVZO6fdVpalp39n7bftey4ZM00RhV4fqS7nQW2snZi7yrnytrIry56VBr3l2ntuirRJOM58l9pl6J28wdwyBmZcppw2IcepaYgfasZ67ikQ3ktBWbBmmh-1V5nv8Venmr8xdmRnehy41QziehI1j1Jcieqc_XsoU-jr0bR3fVR-qQ10p81l0BeObV15z7dbOwYx63JBIloXbFrv4ffejyIy04UI7rmY4xi9YFvwqOl_hIa2iJqrWUgcZMgc6wp1F4RqyOeZ0_sk1JZRSn6Iu4ZvX-R2v5RXm_RdoQ6WJixo_WQ48c9Fz6ggtrujz5QvWQ1mFo9hgxJo6RIXI8uI7ga";


    public NotificationService(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Primary Channel";
            String description = "Main Channel for All Notifications of High Importance";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(String title, String content, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED && activity != null) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1);
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.nav_notifications)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(getUniqueNotificationId(), builder.build());
    }

    public void sendFCMMessage(String NotifTitle, String NotifBody, String receiverFcmToken) {
        Log.d("FCM Token NS:", receiverFcmToken);
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            try {

                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                String jsonPayload = "{"
                        + "\"message\": {"
                        + "\"token\": \"" + receiverFcmToken + "\","
                        + "\"notification\": {"
                        + "\"title\": \"" + NotifTitle + "\","
                        + "\"body\": \"" + NotifBody + "\""
                        + "}"
                        + "}"
                        + "}";

                RequestBody body = RequestBody.create(jsonPayload, mediaType);
                Request request = new Request.Builder()
                        .header("Authorization", "Bearer " + accessToken)
                        .header("Content-Type","application/json")
                        .url("https://fcm.googleapis.com/v1/projects/safe-drive-guardian/messages:send")
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String finalResponse = response.body().string();
                Log.d("FCMResponse", finalResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private int getUniqueNotificationId() {
        return (int) System.currentTimeMillis();
    }
}