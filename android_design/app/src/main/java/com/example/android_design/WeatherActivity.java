package com.example.android_design;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android_design.db.MyWeather;
import com.example.android_design.gson.Weather;
import com.example.android_design.util.HttpUtil;
import com.example.android_design.util.MyXFormatter;
import com.example.android_design.util.Utility;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    //每天最高最低温度。
    ArrayList<Entry> maxList = new ArrayList<Entry>();
    ArrayList<Entry> minList = new ArrayList<Entry>();
    ArrayList<String> data_later = new ArrayList<String>();

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    // private LinearLayout forecastLayout;  //原来
    private LineChart chart;
    private TextView apiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;


    //刷新
    public SwipeRefreshLayout swipeRefresh;

    //滑动更换城市
    public DrawerLayout drawerLayout;
    private Button navButton;
    private Button myHome;

    //语音播报
    private TextToSpeech textToSpeech;
    private Button stop;

    public static final String PREFERENCE_NAME = "save";  //文件名
    public static int MODE = MODE_PRIVATE;  //操作模式
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isnetconnect=isNetworkConnected(WeatherActivity.this);
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        String name = null;
        String code = null;
        if (data != null) {
            name = data.getString("c_name");
            code = data.getString("code");
        }

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);
        prefs = getSharedPreferences(PREFERENCE_NAME, MODE);


        bingPicImg = findViewById(R.id.bing_pic_img);
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        // forecastLayout=findViewById(R.id.forecast_layout);   //原来
        apiText = findViewById(R.id.api_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);

        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);

        chart = findViewById(R.id.chart);

        myHome = findViewById(R.id.my_home);

        stop=findViewById(R.id.stop);

        String weatherString = prefs.getString("weather", null);

        final String weatherId;
        final String myname;

        if (name != null)
        {
            weatherId = code;
            myname = name;
            if(isnetconnect)
            {
                requestWeather(weatherId);
            }
            else
            {
                requestWeather(weatherId);
//                List<MyWeather> myWeathers = DataSupport.findAll(MyWeather.class);
//                Log.v("dblength", "myweather");
//                for (int i = 0; i < myWeathers.size(); i++)
//                {
//                    if (myWeathers.get(i).getCityname().equals(myname)) {
//                        Log.v("dblength", "myweather2");
//                        Log.v("dblength", myWeathers.get(i).getCityname() + "q");
//
//                        MyWeather weather = new MyWeather();
//                        weather.setStatus(myWeathers.get(i).getStatus());
//                        weather.setDregree(myWeathers.get(i).getDregree());
//                        weather.setInfo(myWeathers.get(i).getInfo());
//                        weather.setCityname(myWeathers.get(i).getCityname());
//                        weather.setUpdatetime(myWeathers.get(i).getUpdatetime());
//                        weather.setSportinfo(myWeathers.get(i).getSportinfo());
//                        weather.setCarwashinfo(myWeathers.get(i).getCarwashinfo());
//                        weather.setComfortinfo(myWeathers.get(i).getCarwashinfo());
//                        weather.setPm(myWeathers.get(i).getPm());
//                        weather.setApi(myWeathers.get(i).getApi());
//
//
//                        weather.setFc_info1(myWeathers.get(i).getFc_info1());
//                        weather.setFc_data1(myWeathers.get(i).getFc_data1());
//                        weather.setFc_max1(myWeathers.get(i).getFc_max1());
//                        weather.setFc_min1(myWeathers.get(i).getFc_min1());
//
//                        weather.setFc_info2(myWeathers.get(i).getFc_info2());
//                        weather.setFc_data2(myWeathers.get(i).getFc_data2());
//                        weather.setFc_max2(myWeathers.get(i).getFc_max2());
//                        weather.setFc_min2(myWeathers.get(i).getFc_min2());
//
//                        weather.setFc_info3(myWeathers.get(i).getFc_info3());
//                        weather.setFc_data3(myWeathers.get(i).getFc_data3());
//                        weather.setFc_max3(myWeathers.get(i).getFc_max3());
//                        weather.setFc_min3(myWeathers.get(i).getFc_min3());
//
//                        weather.setFc_info4(myWeathers.get(i).getFc_info4());
//                        weather.setFc_data4(myWeathers.get(i).getFc_data4());
//                        weather.setFc_max4(myWeathers.get(i).getFc_max4());
//                        weather.setFc_min4(myWeathers.get(i).getFc_min4());
//
//                        weather.setFc_info5(myWeathers.get(i).getFc_info5());
//                        weather.setFc_data5(myWeathers.get(i).getFc_data5());
//                        weather.setFc_max5(myWeathers.get(i).getFc_max5());
//                        weather.setFc_min5(myWeathers.get(i).getFc_min5());
//
//                        weather.setFc_info6(myWeathers.get(i).getFc_info6());
//                        weather.setFc_data6(myWeathers.get(i).getFc_data6());
//                        weather.setFc_max6(myWeathers.get(i).getFc_max6());
//                        weather.setFc_min6(myWeathers.get(i).getFc_min6());
//
//                        Log.v("dblength", myWeathers.get(i).getStatus() + "sq");
//                        Log.v("dblength", weather.getDregree()+ "q");
//                        Log.v("dblength", weather.getInfo() + "q");
//                        Log.v("dblength", weather.getUpdatetime()+ "q");
//
//                        Log.v("dblength", myWeathers.get(i).getFc_max1()+"tq");
//
//                        showWeatherInfo2(weather);
//                        break;
//                    }
//                }
            }

        }
        else {
            if (weatherString != null) {
                Weather weather = Utility.handleWeatherResponse(weatherString);
                weatherId = weather.basic.weatherId;
                showWeatherInfo(weather);
            } else {
                weatherId = getIntent().getStringExtra("weather_id");
                weatherLayout.setVisibility(View.INVISIBLE);
                requestWeather(weatherId);
            }
        }

//        if(weatherString!=null&&name==null) {
//            Weather weather = Utility.handleWeatherResponse(weatherString);
//            weatherId=weather.basic.weatherId;
//            showWeatherInfo(weather);
//        }
//        else
//        {
//            if(name!=null&&code!=null)
//            {
//                weatherId=code;
//            }
//            else
//            {
//                weatherId=getIntent().getStringExtra("weather_id");
//                weatherLayout.setVisibility(View.INVISIBLE);
//            }
//            requestWeather(weatherId);
//        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        myHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, MyhomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.stop();
            }
        });

    }

    public void requestWeather(final String weatherId) {
        final String weatherUrl = "http://guolin.tech/api/weather?cityid=" +
                weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.v("m", responseText);
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("weather", responseText);
                            editor.apply();

                            MyWeather myWeather = new MyWeather();
                            myWeather.setCityname( weather.basic.cityName);
                            myWeather.setDregree( weather.now.temperature);
                            myWeather.setInfo( weather.now.more.info);
                            myWeather.setUpdatetime( weather.basic.update.updateTime);
                            myWeather.setStatus( weather.status);
                            myWeather.setCarwashinfo(weather.suggestion.carWash.info);
                            myWeather.setComfortinfo(weather.suggestion.comfort.info);
                            myWeather.setSportinfo(weather.suggestion.sport.info);
                            myWeather.setApi(weather.aqi.city.aqi);
                            myWeather.setPm(weather.aqi.city.pm25);

                            myWeather.setFc_info1(weather.forecastList.get(0).more.info);
                            myWeather.setFc_data1(weather.forecastList.get(0).date);
                            myWeather.setFc_max1(weather.forecastList.get(0).temperature.max);
                            myWeather.setFc_min1(weather.forecastList.get(0).temperature.min);

                            myWeather.setFc_info2(weather.forecastList.get(1).more.info);
                            myWeather.setFc_data2(weather.forecastList.get(1).date);
                            myWeather.setFc_max2(weather.forecastList.get(1).temperature.max);
                            myWeather.setFc_min2(weather.forecastList.get(1).temperature.min);

                            myWeather.setFc_info3(weather.forecastList.get(2).more.info);
                            myWeather.setFc_data3(weather.forecastList.get(2).date);
                            myWeather.setFc_max3(weather.forecastList.get(2).temperature.max);
                            myWeather.setFc_min3(weather.forecastList.get(2).temperature.min);

                            myWeather.setFc_info4(weather.forecastList.get(3).more.info);
                            myWeather.setFc_data4(weather.forecastList.get(3).date);
                            myWeather.setFc_max4(weather.forecastList.get(3).temperature.max);
                            myWeather.setFc_min4(weather.forecastList.get(3).temperature.min);

                            myWeather.setFc_info5(weather.forecastList.get(4).more.info);
                            myWeather.setFc_data5(weather.forecastList.get(4).date);
                            myWeather.setFc_max5(weather.forecastList.get(4).temperature.max);
                            myWeather.setFc_min5(weather.forecastList.get(4).temperature.min);

                            myWeather.setFc_info6(weather.forecastList.get(5).more.info);
                            myWeather.setFc_data6(weather.forecastList.get(5).date);
                            myWeather.setFc_max6(weather.forecastList.get(5).temperature.max);
                            myWeather.setFc_min6(weather.forecastList.get(5).temperature.min);


                            Log.v("dblength",myWeather.getStatus()+"r");
                            Log.v("dblength",weather.now.temperature+"r");
                            Log.v("dblength",myWeather.getDregree()+"r1");

                            myWeather.save();
                            showWeatherInfo(weather);
                        } else {
                            Log.v("m", "yes");
                            Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }


    //原来
//    public void showWeatherInfo(Weather weather)
//    {
//        if(weather!=null&&"ok".equals(weather.status))
//        {
//            String cityName=weather.basic.cityName;
//            String updateTime=weather.basic.update.updateTime.split(" ")[1];
//            String degree=weather.now.temperature+"°C";
//            String weatherInfo=weather.now.more.info;
//            titleCity.setText(cityName);
//            titleUpdateTime.setText(updateTime);
//            degreeText.setText(degree);
//            weatherInfoText.setText(weatherInfo);
//           // forecastLayout.removeAllViews();
//            for(Forecast forecast:weather.forecastList)
//            {
////                View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
////                TextView dateText=view.findViewById(R.id.date_text);
////                TextView infoText=view.findViewById(R.id.info_text);
////                TextView maxText=view.findViewById(R.id.max_text);
////                TextView minText=view.findViewById(R.id.min_text);
////
////                dateText.setText(forecast.date);
////                infoText.setText(forecast.more.info);
////                maxText.setText(forecast.temperature.max);
////                minText.setText(forecast.temperature.min);
//                //forecastLayout.addView(view);
//            }
//        if(weather.aqi!=null)
//        {
//            apiText.setText(weather.aqi.city.aqi);
//            pm25Text.setText(weather.aqi.city.pm25);
//        }
//        String comfort="舒适度："+weather.suggestion.comfort.info;
//        String carWash="洗车指数："+weather.suggestion.carWash.info;
//        String sport="运动建议："+weather.suggestion.sport.info;
//        comfortText.setText(comfort);
//        carWashText.setText(carWash);
//        sportText.setText(sport);
//        weatherLayout.setVisibility(View.VISIBLE);
//            Intent intent=new Intent(this,AutoUpdateService.class);
//            startService(intent);
//        }
//        else
//        {
//            Toast.makeText(WeatherActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();
//        }
//
////        String cityName=weather.basic.cityName;
////        String updateTime=weather.basic.update.updateTime.split(" ")[1];
////        String degree=weather.now.temperature+"°C";
////        String weatherInfo=weather.now.more.info;
////        titleCity.setText(cityName);
////        titleUpdateTime.setText(updateTime);
////        degreeText.setText(degree);
////        weatherInfoText.setText(weatherInfo);
////        forecastLayout.removeAllViews();
////        for(Forecast forecast:weather.forecastList)
////        {
////            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
////            TextView dateText=view.findViewById(R.id.date_text);
////            TextView infoText=view.findViewById(R.id.info_text);
////            TextView maxText=view.findViewById(R.id.max_text);
////            TextView minText=view.findViewById(R.id.min_text);
////
////            dateText.setText(forecast.date);
////            infoText.setText(forecast.more.info);
////            maxText.setText(forecast.temperature.max);
////            minText.setText(forecast.temperature.min);
////            forecastLayout.addView(view);
////        }
////        if(weather.aqi!=null)
////        {
////            apiText.setText(weather.aqi.city.aqi);
////            pm25Text.setText(weather.aqi.city.pm25);
////        }
////        String comfort="舒适度："+weather.suggestion.comfort.info;
////        String carWash="洗车指数："+weather.suggestion.carWash.info;
////        String sport="运动建议："+weather.suggestion.sport.info;
////        comfortText.setText(comfort);
////        carWashText.setText(carWash);
////        sportText.setText(sport);
////        weatherLayout.setVisibility(View.VISIBLE);
//    }
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    public void showWeatherInfo(Weather weather) {

        if (weather != null && "ok".equals(weather.status)) {

            String cityName = weather.basic.cityName;
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            String degree = weather.now.temperature + "°C";
            String weatherInfo = weather.now.more.info;
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);

//            // forecastLayout.removeAllViews();
//            for (Forecast forecast : weather.forecastList) {
////                View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
////                TextView dateText=view.findViewById(R.id.date_text);
////                TextView infoText=view.findViewById(R.id.info_text);
////                TextView maxText=view.findViewById(R.id.max_text);
////                TextView minText=view.findViewById(R.id.min_text);
////                dateText.setText(forecast.date);
////                infoText.setText(forecast.more.info);
////                maxText.setText(forecast.temperature.max);
////                minText.setText(forecast.temperature.min);
//                //forecastLayout.addView(view);
//            }

            Legend legend=chart.getLegend();

            legend.setTextColor(Color.RED);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextColor(Color.RED);

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setTextColor(Color.RED);
            chart.getAxisRight().setEnabled(false);
            leftAxis.setAxisMinimum(-10f);//设置最小值
            leftAxis.setAxisMaximum(50f);//设置最大值
            maxList.clear();
            minList.clear();
            data_later.clear();

            for (int i = 0; i < weather.forecastList.size(); i++) {
                String t_max = weather.forecastList.get(i).temperature.max;
                int max_val = Integer.parseInt(t_max);
                maxList.add(new Entry(i, (float) max_val));

                String t_min = weather.forecastList.get(i).temperature.min;
                int min_val = Integer.parseInt(t_min);
                minList.add(new Entry(i, (float) min_val));

                data_later.add(weather.forecastList.get(i).date);
            }

            LineDataSet dataSet = new LineDataSet(maxList, "每日最高温度");
            LineDataSet dataSet2 = new LineDataSet(minList, "每日最低温度");

            xAxis.setLabelRotationAngle(60);
            xAxis.setValueFormatter(new MyXFormatter(data_later));
            xAxis.setLabelCount(6, true);

            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet.setHighLightColor(Color.RED); // 设置点击某个点时，横竖两条线的颜色
            dataSet.setDrawValues(true);//在点上显示数值 默认true
            dataSet.setValueTextSize(10f);//数值字体大小，同样可以设置字体颜色、自定义字体等
            dataSet.setValueTextColor(Color.RED);

            dataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet2.setHighLightColor(Color.RED); // 设置点击某个点时，横竖两条线的颜色
            dataSet2.setDrawValues(true);//在点上显示数值 默认true
            dataSet2.setValueTextSize(10f);//数值字体大小，同样可以设置字体颜色、自定义字体等
            dataSet2.setValueTextColor(Color.RED);
            chart.getDescription().setEnabled(false);
            LineData data = new LineData(dataSet, dataSet2);

            chart.setData(data);

            if (weather.aqi != null) {
                apiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);
            }
            String comfort = "舒适度：" + weather.suggestion.comfort.info;
            String carWash = "洗车指数：" + weather.suggestion.carWash.info;
            String sport = "运动建议：" + weather.suggestion.sport.info;
            String s="钟老师,您好。现在是"+weather.basic.update.updateTime+","+weather.basic.cityName+",今天天气"+weatherInfo+",温度是"+degree+","+"PM2.5指数为："+weather.aqi.city.pm25+","+comfort+","+sport;
            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
            InitTTS(s);
            weatherLayout.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);

        } else {
            Log.v("dblength", "showinfo失败");
            Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
        }
    }


    public void showWeatherInfo2(MyWeather weather) {
        if (weather != null && "ok".equals(weather.status)) {
            String cityName = weather.getCityname();
            String updateTime = weather.getUpdatetime();
            String degree = weather.getDregree() + "°C";
            String weatherInfo = weather.getInfo();
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);
            // forecastLayout.removeAllViews();
//            for(Forecast forecast:weather.forecastList)
//            {
//                View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
//                TextView dateText=view.findViewById(R.id.date_text);
//                TextView infoText=view.findViewById(R.id.info_text);
//                TextView maxText=view.findViewById(R.id.max_text);
//                TextView minText=view.findViewById(R.id.min_text);
//                dateText.setText(forecast.date);
//                infoText.setText(forecast.more.info);
//                maxText.setText(forecast.temperature.max);
//                minText.setText(forecast.temperature.min);
            //forecastLayout.addView(view);


            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextColor(Color.RED);

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setTextColor(Color.RED);
            chart.getAxisRight().setEnabled(false);
            leftAxis.setAxisMinimum(-10f);//设置最小值
            leftAxis.setAxisMaximum(50f);//设置最大值
            maxList.clear();
            minList.clear();
            data_later.clear();
//
//            for(int i=0;i<weather.forecastList.size();i++)
//            {
//                String t_max = weather.forecastList.get(i).temperature.max;
//                int max_val=Integer.parseInt(t_max);
//                maxList.add(new Entry(i,( float)max_val));
//
//                String t_min = weather.forecastList.get(i).temperature.min;
//                int min_val=Integer.parseInt(t_min);
//                minList.add(new Entry(i,( float)min_val));
//
//                data_later.add(weather.forecastList.get(i).date);
//            }

            String t_max1 = weather.getFc_max1();//最高温度
            int max_val1=Integer.parseInt(t_max1);
            maxList.add(new Entry(0,( float)max_val1));
            String t_min1 = weather.getFc_min1();//最低温度
            int min_val1=Integer.parseInt(t_min1);
            minList.add(new Entry(0,( float)min_val1));
            data_later.add(weather.getFc_data1());

            String t_max2 = weather.getFc_max2();//最高温度
            int max_val2=Integer.parseInt(t_max2);
            maxList.add(new Entry(1,( float)max_val2));
            String t_min2 = weather.getFc_min2();//最低温度
            int min_val2=Integer.parseInt(t_min2);
            minList.add(new Entry(1,( float)min_val2));
            data_later.add(weather.getFc_data2());

            String t_max3 = weather.getFc_max3();//最高温度
            int max_val3=Integer.parseInt(t_max3);
            maxList.add(new Entry(2,( float)max_val3));
            String t_min3 = weather.getFc_min3();//最低温度
            int min_val3=Integer.parseInt(t_min3);
            minList.add(new Entry(2,( float)min_val3));
            data_later.add(weather.getFc_data3());

            String t_max4 = weather.getFc_max4();//最高温度
            int max_val4=Integer.parseInt(t_max4);
            maxList.add(new Entry(3,( float)max_val4));
            String t_min4 = weather.getFc_min4();//最低温度
            int min_val4=Integer.parseInt(t_min4);
            minList.add(new Entry(3,( float)min_val4));
            data_later.add(weather.getFc_data4());

            String t_max5 = weather.getFc_max5();//最高温度
            int max_val5=Integer.parseInt(t_max5);
            maxList.add(new Entry(4,( float)max_val5));
            String t_min5 = weather.getFc_min5();//最低温度
            int min_val5=Integer.parseInt(t_min5);
            minList.add(new Entry(4,( float)min_val5));
            data_later.add(weather.getFc_data5());

            String t_max6 = weather.getFc_max6();//最高温度
            int max_val6=Integer.parseInt(t_max6);
            maxList.add(new Entry(5,( float)max_val6));
            String t_min6= weather.getFc_min6();//最低温度
            int min_val6=Integer.parseInt(t_min6);
            minList.add(new Entry(5,( float)min_val6));
            data_later.add(weather.getFc_data6());

            LineDataSet dataSet = new LineDataSet(maxList, "每日最高温度");
            LineDataSet dataSet2 = new LineDataSet(minList, "每日最低温度");

            xAxis.setLabelRotationAngle(60);
            xAxis.setValueFormatter(new MyXFormatter(data_later));
            xAxis.setLabelCount(6, true);

            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet.setHighLightColor(Color.RED); // 设置点击某个点时，横竖两条线的颜色
            dataSet.setDrawValues(true);//在点上显示数值 默认true
            dataSet.setValueTextSize(10f);//数值字体大小，同样可以设置字体颜色、自定义字体等
            dataSet.setValueTextColor(Color.RED);

            dataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet2.setHighLightColor(Color.RED); // 设置点击某个点时，横竖两条线的颜色
            dataSet2.setDrawValues(true);//在点上显示数值 默认true
            dataSet2.setValueTextSize(10f);//数值字体大小，同样可以设置字体颜色、自定义字体等
            dataSet2.setValueTextColor(Color.RED);
            chart.getDescription().setEnabled(false);
            LineData data = new LineData(dataSet, dataSet2);

            chart.setData(data);

            if(weather.getApi()!=null)
            {
                apiText.setText(weather.getApi());
                pm25Text.setText(weather.getPm());
            }
            String comfort="舒适度："+weather.getComfortinfo();
            String carWash="洗车指数："+weather.getCarwashinfo();
            String sport="运动建议："+weather.getSportinfo();
            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
            weatherLayout.setVisibility(View.VISIBLE);
            String s="钟老师,您好。现在是"+weather.getUpdatetime()+","+weather.getCityname()+",今天天气"+weatherInfo+",温度是"+weather.getDregree()+","+"PM2.5指数为："+weather.getPm()+","+weather.getComfortinfo()+","+weather.getSportinfo();
            InitTTS(s);
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        } else {
            Log.v("dblength", "showinfo失败");
            Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
        }
    }

    //播报
    public void InitTTS(final String s)
    {
        textToSpeech=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==textToSpeech.SUCCESS)
                {
                    textToSpeech.setPitch(1.0f);
                    textToSpeech.setSpeechRate(1.0f);
                    textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null,null);
                }
                else
                {
                    Toast.makeText(WeatherActivity.this,"数据丢失或不支持", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //判断是否连接网络
    public  boolean isNetworkConnected(Context context)
    {
        if (context != null)
        {
            ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkCapabilities networkCapabilities=connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if(networkCapabilities!=null)
            {
                return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            }
        }
        Toast.makeText(context,"网络已断开",Toast.LENGTH_SHORT).show();
        return false;
    }
}



