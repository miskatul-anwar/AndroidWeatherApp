package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    TextView cityName;
    Button search;
    TextView show;
    String url;



    class getWeather extends AsyncTask <String, Void, String>{
        @Override
        protected String doInBackground(String... urls){
            StringBuilder result = new StringBuilder();
            try{
                URL url  = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine())!=null){
                    result.append(line).append("\n");
                }
                return result.toString();
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray weatherArray = jsonObject.getJSONArray("weather");
                JSONObject main = jsonObject.getJSONObject("main");
                String temp = String.valueOf(main.getInt("temp")- 273)+"°C";
                int tmp = main.getInt("temp")-273;
                int hmd = main.getInt("humidity");
                ((TextView)findViewById(R.id.temperature)).setText(temp);
                String feels_Like="Feels Like: "+String.valueOf(main.getInt("feels_like")-273)+"°C";
                ((TextView)findViewById(R.id.feels_like)).setText(feels_Like);
                String humidity="💧"+String.valueOf(main.getInt("humidity"))+"%";
                ((TextView)findViewById(R.id.humidity)).setText(humidity);
                String pressurehPa  = String.valueOf(main.getInt("pressure"))+"hPa";
                double atm = (main.getDouble("pressure")) / 1013.25;
                String pressureAtm = String.format("%.2f", atm) + "atm";
                ((TextView)findViewById(R.id.atm)).setText("🍃"+pressurehPa+" | "+pressureAtm);
                String min = String.valueOf(main.getInt("temp_min")-273)+"°C";
                String max = String.valueOf(main.getInt("temp_max")-273)+"°C";
                ((TextView)findViewById(R.id.min_max)).setText("Min: "+min+"\nMax: "+max);
                if (tmp >= 30) {
                    if (hmd >= 60) {
                        ((TextView)findViewById(R.id.condition)).setText("It's hot and humid.");
                        ((TextView)findViewById(R.id.emoji)).setText("🌡️");
                    } else if (hmd >= 40) {
                        ((TextView)findViewById(R.id.condition)).setText("It's hot and moderately humid.");
                        ((TextView)findViewById(R.id.emoji)).setText("☀️");
                    } else {
                        ((TextView)findViewById(R.id.condition)).setText("It's hot and dry.");
                        ((TextView)findViewById(R.id.emoji)).setText("💦");
                    }
                } else if (tmp >= 20) {
                    if (hmd >= 60) {
                        ((TextView)findViewById(R.id.condition)).setText("It's warm and humid.");
                        ((TextView)findViewById(R.id.emoji)).setText("🌤️");
                    } else if (hmd >= 40) {
                        ((TextView)findViewById(R.id.condition)).setText("It's warm and comfortable.");
                        ((TextView)findViewById(R.id.emoji)).setText("🌞");
                    } else {
                        ((TextView)findViewById(R.id.condition)).setText("It's warm and dry.");
                        ((TextView)findViewById(R.id.emoji)).setText("⛅");
                    }
                } else if (tmp >= 10) {
                    if (hmd >= 60) {
                        ((TextView)findViewById(R.id.condition)).setText("It's cool and humid.");
                        ((TextView)findViewById(R.id.emoji)).setText("🍃");
                    } else if (hmd >= 40) {
                        ((TextView)findViewById(R.id.condition)).setText("It's cool and comfortable.");
                        ((TextView)findViewById(R.id.emoji)).setText("🌥️");
                    } else {
                        ((TextView)findViewById(R.id.condition)).setText("It's cool and dry.");
                        ((TextView)findViewById(R.id.emoji)).setText("⛱️");
                    }
                } else {
                    if (hmd >= 60) {
                        ((TextView)findViewById(R.id.condition)).setText("It's cold and humid.");
                        ((TextView)findViewById(R.id.emoji)).setText("⛄");
                    } else if (hmd >= 40) {
                        ((TextView)findViewById(R.id.condition)).setText("It's cold and comfortable.");
                        ((TextView)findViewById(R.id.emoji)).setText("☃️");
                    } else {
                        ((TextView)findViewById(R.id.condition)).setText("It's cold and dry.");
                        ((TextView)findViewById(R.id.emoji)).setText("❄️");
                    }
                }


        }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = findViewById(R.id.cityName);
        search=findViewById(R.id.search);
        final String[] temp={""};

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Searching", Toast.LENGTH_SHORT).show();
                String city = cityName.getText().toString();
                ((TextView)findViewById(R.id.city)).setText("🔎 "+city);
                try{
                    if (city!=null){
                    url="https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=71a97c9382778e093f828f60dc237edf";
                }else {
                        Toast.makeText(MainActivity.this,"Missing City Name",Toast.LENGTH_SHORT).show();
                    }
                    getWeather task = new getWeather();
                    temp[0]=task.execute(url).get();
                    }catch (ExecutionException e){
                    e.printStackTrace();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                if (temp[0]==null){
                    show.setText("Unable to fetch data !");
                }
            }
        });
    }
}