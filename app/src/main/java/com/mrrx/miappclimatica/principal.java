package com.mrrx.miappclimatica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class principal extends AppCompatActivity {
    EditText etCity, etCountry;
    TextView txtResult;
    ImageView imgClima;
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "3ec02b4dfff82189640f3fb8b321ab17";
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);
        etCity = findViewById(R.id.etCity);
        etCountry = findViewById(R.id.etCountry);
        txtResult = findViewById(R.id.txtResult);
        imgClima = findViewById(R.id.clima);

    }

    public void getWeatherDetails(View view) {
        String tempUrl = "";
        String city = etCity.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etCity.getWindowToken(),0);
        if (city.equals("")){
            txtResult.setText("El campo CIUDAD no puede estar vacio!");
            Picasso.get().load("http://openweathermap.org/img/wn/13d@2x.png").into(imgClima);
        }else{
            if (!country.equals("")){
                tempUrl = url + "?q=" + city + "," + country + "&units=metric&lang=es&appid=" + appid;
            }else{
                tempUrl = url + "?q=" + city + "&units=metric&lang=es&appid=" + appid;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>(){
                @Override
                public void onResponse(String response){
                    //Log.d("response", response);
                    String output = "";
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String description = jsonObjectWeather.getString("description");
                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        double temp = jsonObjectMain.getDouble("temp");
                        double feelsLike = jsonObjectMain.getDouble("feels_like");
                        float pressure = jsonObjectMain.getInt("pressure");
                        int humidity = jsonObjectMain.getInt("humidity");
                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                        String wind = jsonObjectWind.getString("speed");
                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                        String clouds = jsonObjectClouds.getString("all");
                        JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                        String countryName = jsonObjectSys.getString("country");
                        String cityName = jsonResponse.getString("name");
                        String icnImagen = jsonObjectWeather.getString("icon");
                        txtResult.setTextColor(Color.rgb(255,255,255));
                        output += "El Clima Actual de: " + cityName + " (" + countryName + ")"
                                + "\n Temperatura: " + df.format(temp) + " °C"
                                + "\n Se siente como: " + df.format(feelsLike) + " °C"
                                + "\n Humedad: " + humidity + "%"
                                + "\n Descripción: " + description
                                + "\n Velocidad del viento: " + wind + "m/s (metros por segundo)"
                                + "\n Nuvosidad: " + clouds + "%"
                                + "\n Presión: " + pressure + " hPa";
                        txtResult.setText(output);
                        Picasso.get().load("http://openweathermap.org/img/wn/" + icnImagen + "@2x.png").into(imgClima);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}