package com.example.motifv;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class QuoteReceiver extends BroadcastReceiver {

    private final OkHttpClient client = new OkHttpClient();
    private final String API_URL = "https://api.kanye.rest/";
    private static final String CHANNEL_ID = "QUOTE_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        fetchQuoteAndNotify(context);
    }

    private void fetchQuoteAndNotify(Context context) {
        Request request = new Request.Builder().url(API_URL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {}

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    try {
                        JSONObject json = new JSONObject(body);
                        String quote = json.getString("quote");
                        showNotification(context, quote);
                    } catch (Exception ignored) {}
                }
            }
        });
    }

    private void showNotification(Context context, String quote) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Motivational Quotes", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Kanye says:")
                .setContentText(quote)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(quote))
                .setSmallIcon(R.drawable.ic_quote)
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
