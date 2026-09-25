package com.example.aotweatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<WeatherAttributes> {
    private Context context;
    private List<WeatherAttributes> weatherAttri;

    public CustomAdapter(@NonNull Context context, List<WeatherAttributes> weatherAttri) {
        super(context, 0, weatherAttri);
        this.context = context;
        this.weatherAttri = weatherAttri;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_layout, parent, false);
        }

        WeatherAttributes everyWeather = weatherAttri.get(position);
        TextView forecast = convertView.findViewById(R.id.forecast);
        TextView date = convertView.findViewById(R.id.date);
        TextView min = convertView.findViewById(R.id.min);
        TextView max = convertView.findViewById(R.id.max);
        ImageView pic = convertView.findViewById(R.id.currentpic);

        if (everyWeather != null) {
            forecast.setText(everyWeather.getWeather());
            date.setText(everyWeather.getDate());
            min.setText("Min: " + everyWeather.getMin() + "°F");
            max.setText("Max: " + everyWeather.getMax() + "°F");
            pic.setImageResource(everyWeather.getPicture());
        }

        return convertView;
    }
}