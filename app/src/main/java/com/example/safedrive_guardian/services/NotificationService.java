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


    // TODO: Change this access token by getting the token Snehal's FCM application
    private String accessToken = "ya29.c.c0AY_VpZhx-mCR4H0gWwENZ9URisQRkY6Pd8MqgwvVIeY-_yDIA6FAlxRAqK3cyjtIcWZk1dqrIkpeOu0EqtZcIOardhE3o1pITXPYbvr3w7-nG-EkB6rj7q3pY05Z8UuGt59zeQByqwrqRP2h5nWp0wVcFEbQyYG3bRrrDQA4nwfdq8kM3YU4q7SYQYB0NNde01vb6dEMAIdRL1lDZ3FoQB9a1eu6x0vaDE6UFtTFQYvek0T_--dAAqpf6jVuBGuc82xwB9LmWSWOe8Y8OIgfxUFTnFYihgeTsNqvgceO8kqWjEqDXS-PI3OxbltHczOWzKeuYaRWVjoAMpc3x7uKTgoohkYZdycHm9teqyVcbndmEU9W33VVgzANH385Ctw5bBJ9jsi6B9_0e1uiMu_6Razr9ed_aU45VMhFtlvxwqz1UzIYWJvI3mMSaZXvWM-f61gUoe2V4I4Obv42l9p5BU7vZBUnpZd3gXj7r1uIcFFgQpB2pmm4s4d64By3XQUqYBtM0-t8XXk49Y_94z6yRlqiccemm9JFI5B73Qq13ysvmy5R6_lR95mQZ0XcJ6l6X-v8_0X2Vf4SySeWSo8wXWF343qrWeukpV8WlY7rsrb9vVh8donmO5jFrW1vurxpgasI021MskQWnhu4ryo2fm1yoc5pmZh47MeVoxOkX4pY4_Iqhjj_ziiIF2l9QyI93e658MlvBJ2OBudwzgmd3BiSinSQgFBjk5neQwocgwyS3kndw_Wts4eQBipkku4MedQQBo7J0V3I1dBsSbVRr-zVWi21v1rR5VW2XMxziBQF7s5Fygz8vFpVshUUsO1nyeRuR6x9XgFfvaV3fhO8lV0S9YW00sW9rIeujp14bsZ-ao9tMZ4Jxzc4O-vwqXB_Xb4sQu5X5-_x95qr6ck4vcYo_2wdsgVUh9zFtxU7ixn6xeB5_0Stimx9BZxJr7t3m2QOJwZ22VFvvR9Isfr8dQRpixgpxmjuVWqq6_enOQoy2Mzvf8v7zjW";

    // Not same as accessToken, this is the unique FCM token for the device
    private String fcmToken = "";


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
                .setSmallIcon(R.drawable.nav_notifications) // replace with your notification icon
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(getUniqueNotificationId(), builder.build());
    }

    private void sendFCMMessage(String NotifTitle, String NotifBody, String receiverFcmToken) {
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