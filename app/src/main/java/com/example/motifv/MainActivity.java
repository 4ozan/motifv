package com.example.motifv;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView quoteText;
    private Button newQuoteButton;
    private final OkHttpClient client = new OkHttpClient();
    private final String API_URL = "https://api.kanye.rest/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quoteText = findViewById(R.id.quoteText);
        newQuoteButton = findViewById(R.id.newQuoteButton);

        fetchQuote();
        scheduleQuoteUpdates();

        newQuoteButton.setOnClickListener(v -> fetchQuote());
    }

    private void fetchQuote() {
        Request request = new Request.Builder().url(API_URL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> quoteText.setText("Failed to fetch quote 😢"));
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    try {
                        JSONObject json = new JSONObject(body);
                        String quote = json.getString("quote");
                        runOnUiThread(() -> quoteText.setText("“" + quote + "”"));
                    } catch (Exception e) {
                        runOnUiThread(() -> quoteText.setText("Error parsing quote."));
                    }
                }
            }
        });
    }

    private void scheduleQuoteUpdates() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, QuoteReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        long interval = 30 * 1000;
        long triggerTime = System.currentTimeMillis() + interval;
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime, interval, pendingIntent);
    }
}
