package com.example.android_design;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

import com.example.android_design.gson.Weather;
import com.example.android_design.util.HttpUtil;
import com.example.android_design.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    public static final String PREFERENCE_NAME = "save";  //文件名
    public static int MODE = MODE_PRIVATE;  //操作模式
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        updateWeather();
        updateBingPic();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour=8*60*60*1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this,AutoUpdateService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);

    }

    private void updateWeather()
    {
        final SharedPreferences preferences=getSharedPreferences(PREFERENCE_NAME,MODE);
        String weatherString=preferences.getString("weather",null);
        if(weatherString!=null)
        {
            Weather weather= Utility.handleWeatherResponse(weatherString);
            String weatherId=weather.basic.weatherId;

            String weatheUrl="http://guolin.tech/api/weather?cityid="+
                    weatherId+"&key=bc0418b57b2d4918819d3974ac1285d9";
            HttpUtil.sendOkHttpRequest(weatheUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                        String reponseText=response.body().toString();
                        Weather weather=Utility.handleWeatherResponse(reponseText);
                        if(weather!=null&&"ok".equals(weather.status))
                        {
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putString("weather",reponseText);
                            editor.apply();
                        }
                }
            });
        }
    }

    private void updateBingPic()
    {
        final SharedPreferences preferences=getSharedPreferences(PREFERENCE_NAME,MODE);
        String weatherString=preferences.getString("weather",null);
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic=response.body().toString();
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
            }
        });
    }
}
