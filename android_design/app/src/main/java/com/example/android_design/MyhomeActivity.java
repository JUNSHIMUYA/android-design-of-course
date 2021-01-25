package com.example.android_design;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android_design.db.Addcity;
import com.example.android_design.db.County;
import com.example.android_design.util.DBAdapter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;


public class MyhomeActivity extends ListActivity {

    private Button bexit;
    private Button addcity;
    private ListView listView;
    private ArrayAdapter<String>adapter;

    private static ArrayList<String>citylist=new ArrayList<>();
    private DBAdapter dbAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myhome);

        bexit=findViewById(R.id.exit);
        addcity=findViewById(R.id.add_city);
        listView=findViewById(android.R.id.list);

        dbAdapter=new DBAdapter(this);
        dbAdapter.open();
        initlist(citylist);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_expandable_list_item_1,citylist);
        listView.setAdapter(adapter);


        bexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyhomeActivity.this,WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        });

        addcity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder  builder=new AlertDialog.Builder(MyhomeActivity.this);
                builder.setTitle("请输入要添加的城市");
                final EditText et=new EditText(MyhomeActivity.this);
                builder.setView(et);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String city_name=et.getText().toString();
                        Addcity addcity=new Addcity();
                        addcity.setName(city_name);
                        if(!citylist.contains(city_name)&&city_name!=null)
                        {
                            citylist.add(city_name);
                            dbAdapter.insert(addcity);
                        }
                        else
                        {
                            Toast.makeText(MyhomeActivity.this,"城市已经存在或者输入为空",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.show();
            }
        });

       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

           }
       });
      listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
          @Override
          public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
              AlertDialog.Builder builder = new AlertDialog.Builder(MyhomeActivity.this);
              builder.setMessage("确认删除吗");
              builder.setTitle("提示");
              builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      dialog.dismiss();
                  }
              });
              builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      String c_name=citylist.get(position);
                      citylist.remove(position);
                      dbAdapter.deleteData(c_name);
                      adapter.notifyDataSetChanged();
                      dialog.dismiss();
                  }
              });
              builder.show();
              return true;
          }
      });

      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              String name=citylist.get(position);
              List<County>counties=DataSupport.select("countyName","weatherId").find(County.class);
              Log.v("dblength",counties.size()+"a");
              String code=null;
              for(int i=0;i<counties.size();i++)
              {
                  Log.v("dblength",counties.get(i).getCountyName());
                  if(counties.get(i).getCountyName().equals(name))
                  {
                      code=counties.get(i).getWeatherId();
                      Log.v("dblength",code);
                      break;
                  }
              }
              Intent intent=new Intent(MyhomeActivity.this,WeatherActivity.class);
              Bundle data=new Bundle();
              data.putString("c_name",name);
              data.putString("code",code);
              intent.putExtras(data);
              startActivity(intent);
              finish();
          }
      });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbAdapter.close();//关闭与数据库的连接
    }

    private void initlist(ArrayList<String>st)
    {

        Log.v("stlength",st.size()+"");
        Addcity []addcities=dbAdapter.quertAllAddcity();
        if(addcities!=null)
        {
            int length=addcities.length;
            String s=Integer.toString(length);
            Log.v("dblength",s);
            for(int i=0;i<length;i++)
            {
                if(!st.contains(addcities[i].name))
                {
                    st.add(addcities[i].name);
                }
            }
        }
    }
}

