package com.example.aotweatherapp;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView lat, longitudeString, city, quote, currentTemp;
    private EditText zipCodeEditText;
    private ImageView imageCurrent, aotImageView;
    private Button searchButton;
    private ListView weatherListView;
    private String description, saying;
    int picID;
    double temperature;
    ConstraintLayout mainLayout;

    private static final String geoURL = "https://api.openweathermap.org/geo/1.0/zip?zip=";
    private static final String weatherURL = "https://api.openweathermap.org/data/2.5/forecast?lat=%f&lon=%f&appid=";
    private static final String apiKey = "d41095ef6989e41c2984be761983bffa";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        zipCodeEditText = findViewById(R.id.editZip);
        searchButton = findViewById(R.id.search);
        lat = findViewById(R.id.lat);
        longitudeString = findViewById(R.id.longitude);
        city = findViewById(R.id.cityName);
        quote = findViewById(R.id.quote);
        currentTemp = findViewById(R.id.currentTemp);
        weatherListView = findViewById(R.id.listView);
        imageCurrent = findViewById(R.id.pic);
        aotImageView = findViewById(R.id.aotImageView);
        mainLayout = findViewById(R.id.mainConstraint);

        ArrayList<WeatherAttributes> weatherList = new ArrayList<>();
        CustomAdapter adapter = new CustomAdapter(this, weatherList);
        weatherListView.setAdapter(adapter);

        searchButton.setOnClickListener(v -> {
            String zipCode = zipCodeEditText.getText().toString();
            if (zipCode.length() == 5) {
                //start screen stuff
                lat.setVisibility(View.VISIBLE);
                longitudeString.setVisibility(View.VISIBLE);
                city.setVisibility(View.VISIBLE);
                quote.setVisibility(View.VISIBLE);
                currentTemp.setVisibility(View.VISIBLE);
                weatherListView.setVisibility(View.VISIBLE);
                imageCurrent.setVisibility(View.VISIBLE);
                aotImageView.setVisibility(View.GONE);

                //tells to strat importing the data
                new GeocodingApiTask().execute(zipCode);//This line is what is sent to Params
            } else {
                Toast.makeText(MainActivity.this, "Please enter a valid zip code", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private class GeocodingApiTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String zipCode = params[0];
            try {
                String geocodingUrl = geoURL + zipCode + "&appid=" + apiKey;
                URL url = new URL(geocodingUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String jsonResponse = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResponse += line;
                }
                reader.close();
                inputStream.close();

                return jsonResponse.toString();//This line is the second String in Params
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    double latitude = jsonObject.getDouble("lat");
                    double longitude = jsonObject.getDouble("lon");
                    String cityName = jsonObject.getString("name");

                    city.setText("City: " + cityName);
                    lat.setText("Lat: " + latitude);
                    longitudeString.setText("Long: "+longitude);
                    new WeatherApiTask().execute(latitude, longitude);//Calls on other Async Task to collect Weather data using lat long param #1 (double)

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Invalid Zip Code", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class WeatherApiTask extends AsyncTask<Double, Void, String> {

        @Override
        protected String doInBackground(Double... params) {
            double latitude = params[0];
            double longitude = params[1];
            try {
                String weatherUrl = String.format(weatherURL, latitude, longitude) + apiKey;
                URL url = new URL(weatherUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String jsonResponse = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResponse += line;
                }
                reader.close();
                inputStream.close();

                return jsonResponse.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray listArray = jsonObject.getJSONArray("list");

                    ArrayList<WeatherAttributes> weatherList = new ArrayList<>();
                    weatherList.clear();//clears for when a new zip is entered

                    for (int i = 0; i < 35; i+=7) {
                        JSONObject forecast = listArray.getJSONObject(i);
                        JSONObject main = forecast.getJSONObject("main");
                        //converting k->c->f
                        temperature = main.getDouble("temp") - 273.15;
                        temperature = (temperature * 9/5) + 32;
                        Log.d("CurrentTemperature", "Temperature: " + temperature);


                        JSONArray weatherArray = forecast.getJSONArray("weather");//acessing first temp aka current temp
                        JSONObject weather = weatherArray.getJSONObject(0);
                        description = weather.getString("description");//for quote and pic


                        //when its day the image changes to day wallpaper, at night it changes to night
                        String dateTime = forecast.getString("dt_txt");
                        String time = "21:00:00";//dateTime.substring(11);

                        if (time.equals("00:00:00") || time.equals("03:00:00") || time.equals("06:00:00") || time.equals("09:00:00") || time.equals("12:00:00") || time.equals("15:00:00")) {
                            mainLayout.setBackgroundResource(R.drawable.day);
                            zipCodeEditText.setTextColor(Color.WHITE);
                            searchButton.setBackgroundColor(Color.parseColor("#85c1e9"));
                            longitudeString.setTextColor(Color.WHITE);
                            city.setTextColor(Color.WHITE);
                            quote.setTextColor(Color.WHITE);
                            currentTemp = findViewById(R.id.currentTemp);
                            weatherListView.setBackgroundColor(Color.parseColor("#8085C1E9"));

                        } else {
                            mainLayout.setBackgroundResource(R.drawable.night);
                            zipCodeEditText.setTextColor(Color.WHITE);
                            searchButton.setBackgroundColor(Color.parseColor("#85c1e9"));
                            longitudeString.setTextColor(Color.WHITE);
                            city.setTextColor(Color.WHITE);
                            quote.setTextColor(Color.WHITE);
                            currentTemp = findViewById(R.id.currentTemp);
                            weatherListView.setBackgroundColor(Color.parseColor("#8085C1E9"));
                        }


                        double minTempK = main.getDouble("temp_min");
                        double maxTempK = main.getDouble("temp_max");


                        double minTempF = (minTempK - 273.15) *9.0/5.0 + 32;
                        double maxTempF = (maxTempK -273.15)*9.0/5.0 +32;

                        if (description.contains("clear"))
                            picID = R.drawable.sunny;
                        else if (description.contains("rain"))
                            picID = R.drawable.rainydayarmin;
                        else if (description.contains("cloud"))
                            picID = R.drawable.cloudy;
                        else if (description.contains("snow"))
                            picID = R.drawable.snow;
                        else
                            picID = R.drawable.aotbackround;

                        WeatherAttributes newAttribute = new WeatherAttributes(picID, forecast.getString("dt_txt").substring(0,11), description, String.format("%.2f", minTempF), String.format("%.2f", maxTempF));
                        //actually adds to listView
                        weatherList.add(newAttribute);

                    }
                    CustomAdapter adapter = new CustomAdapter(MainActivity.this, weatherList);
                    weatherListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    if (description.contains("clear")) {
                        saying = "Eren: \"You Know What I Hate Most In This World? People Who Aren't Free.\"";
                    }
                    else if (description.contains("rain")) {
                        saying = "Armin: \"Rain... It's pouring. Just like that day.\"";
                    }
                    else if (description.contains("cloud")) {
                        saying = "Hange: \"It’s cloudy today... Titans don’t move as much in this weather.\"";
                    }
                    else if (description.contains("snow")) {
                        saying = "Levi: \"The snow covers everything, making it seem peaceful… but death is just beneath it.\"";
                    }
                    else if(description.contains("sunny")) {
                        saying = "Zeke: \"The weather is downright lovely\"";
                    }
                    else {
                        saying = "Floch: \"The weather’s nice today. Perfect for a massacre.\" ";
                    }
                    Log.d("QUOTE", saying);
                    quote.setText(saying);

                    JSONObject item = listArray.getJSONObject(0);
                    JSONObject main = item.getJSONObject("main");
                    double currentTemperature = (main.getDouble("temp")-273.15)*9.0/5.0 + 32;
                    currentTemp.setText("Current Temperature: " +String.format("%.2f", currentTemperature) +"°F ");

                    if (description.contains("clear"))
                        imageCurrent.setImageResource(R.drawable.clear);
                    else if (description.contains("rain"))
                        imageCurrent.setImageResource(R.drawable.rainydayarmin);
                    else if (description.contains("cloud"))
                        imageCurrent.setImageResource(R.drawable.cloudy);
                    else if (description.contains("snow"))
                        imageCurrent.setImageResource(R.drawable.snow);
                    else
                        imageCurrent.setImageResource(R.drawable.aotbackround);

                    lat.setTextColor(Color.WHITE);
                    longitudeString.setTextColor(Color.WHITE);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error getting data", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



}

