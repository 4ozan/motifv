package com.example.motifv;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

        newQuoteButton.setOnClickListener(v -> fetchQuote());
    }

    private void fetchQuote() {
        Request request = new Request.Builder().url(API_URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> quoteText.setText("Failed to fetch quote 😢"));
            }

            @SuppressLint("SetTextI18n")
            @Override public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        String quote = json.getString("quote");

                        runOnUiThread(() -> quoteText.setText("“" + quote + "”"));

                    } catch (Exception e) {
                        runOnUiThread(() -> quoteText.setText("Error parsing quote."));
                    }
                }
            }
        });
    }
}
