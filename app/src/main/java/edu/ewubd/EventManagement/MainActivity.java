package edu.ewubd.EventManagement;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.ewubd.calculatorassignment.R;

public class MainActivity extends AppCompatActivity {

    private ListView lvEvents;
    private ArrayList<Event> events;
    private CustomEventAdapter adapter;

    Button  createBtn,exitBtn,historyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvEvents = findViewById(R.id.listEvents);
        events = new ArrayList<>();
        loadData();
        createBtn  = findViewById(R.id.createBtn);
        historyBtn  = findViewById(R.id.historyBtn);
        exitBtn  = findViewById(R.id.exitBtn);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,CreateEventActivity.class);
                startActivity(i);
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void showDialog(String msg, String title, String position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(msg);
        builder.setTitle(title);

        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Util.getInstance().deleteByKey(MainActivity.this, key);
                        KeyValueDB db = new KeyValueDB(MainActivity.this);
                        db.deleteDataByKey(position);
                        Toast.makeText(MainActivity.this,"Event is Deleted",Toast.LENGTH_LONG).show();
                        loadData();
                        String[] keys = {"action", "id", "semester"};
                        String[] values = {"restore", "2018-2-60-039", "2023-1"};
                        httpRequest(keys, values);
                        dialog.cancel();
                    }

                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

        private void loadData(){
            events.clear();
            KeyValueDB db = new KeyValueDB(this);
            Cursor rows = db.execute("SELECT * FROM key_value_pairs");
            if (rows.getCount() == 0) {
                return;
            }

                //events = new Event[rows.getCount()];
                while (rows.moveToNext()) {
                    String key = rows.getString(0);
                    String eventData = rows.getString(1);
                    String[] fieldValues = eventData.split("---");

                    String name = fieldValues[0];
                    String place = fieldValues[1];
                    String eventType = fieldValues[2];
                    String dateTime = fieldValues[3];
                    String capacity = fieldValues[4];
                    String budget = fieldValues[5];
                    String email = fieldValues[6];
                    String phone = fieldValues[7];
                    String description = fieldValues[8];

                    Event e = new Event(key, name, place, eventType, dateTime, capacity, budget, email, phone, description);
                    events.add(e);
                }
            db.close();
            adapter = new CustomEventAdapter(this, events);
            lvEvents.setAdapter(adapter);

            // handle the click on an event-list item
            lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                    // String item = (String) parent.getItemAtPosition(position);
                    System.out.println(position);
                    Intent i = new Intent(MainActivity.this, CreateEventActivity.class);
                    i.putExtra("EVENT_KEY", events.get(position).key);
                    String values = events.get(position).name+"---"+events.get(position).place+"---"+events.get(position).eventType+"---"+
                            events.get(position).datetime+"---"+events.get(position).capacity+"---"+
                            events.get(position).budget+"---"+events.get(position).email+"---"+
                            events.get(position).phone+"---"+ events.get(position).description;
                    i.putExtra("VALUE", values);
                    startActivity(i);
                }
            });
            // handle the long-click on an event-list item
            lvEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    //String message = "Do you want to delete event - "+events[position].name +" ?";
                    String message = "Do you want to delete event - "+events.get(position).name +" ?";
                    System.out.println(message);
                    showDialog(message, "Delete Event", events.get(position).key);
//                    showDialog(message, "Delete Event", "LOL");
                    return true;
                }
            });




    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        String[] keys = {"action", "id", "semester"};
        String[] values = {"restore", "2018-2-60-039", "2023-1"};
        httpRequest(keys, values);

    }

    @Override
    protected void onStart() {
        super.onStart();
        String[] keys = {"action", "id", "semester"};
        String[] values = {"restore", "2018-2-60-039", "2023-1"};
        httpRequest(keys, values);
    }

    private void httpRequest(final String keys[], final String values[]){
        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params=new ArrayList<NameValuePair>();
                for (int i=0; i<keys.length; i++){
                    params.add(new BasicNameValuePair(keys[i],values[i]));
                }
                String url= "https://www.muthosoft.com/univ/cse489/index.php";
                String data="";
                try {
                    data=JSONParser.getInstance().makeHttpRequest(url,"POST",params);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(String data){
                if(data!=null){
                    System.out.println(data);
                    updateEventListByServerData(data);
                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    public void updateEventListByServerData(String data){
        try{
            JSONObject jo = new JSONObject(data);
            if(jo.has("events")){
                JSONArray ja = jo.getJSONArray("events");
//                KeyValueDB db =  new KeyValueDB(MainActivity.this); // jihan suggested
                for(int i=0; i<ja.length(); i++){
                    JSONObject event = ja.getJSONObject(i);
                    String eventKey = event.getString("e_key");
                    String eventValue = event.getString("e_value");
                    // split eventValue to show in event list

                    String[] fieldValues = eventValue.split("---");

                    String name = fieldValues[0];
                    String place = fieldValues[1];
                    String eventType = fieldValues[2];
                    String dateTime = fieldValues[3];
                    String capacity = fieldValues[4];
                    String budget = fieldValues[5];
                    String email = fieldValues[6];
                    String phone = fieldValues[7];
                    String description = fieldValues[8];

                    Event e = new Event(eventKey, name, place, eventType, dateTime, capacity, budget, email, phone, description);
                    events.add(e);


                }
//                db.close();
//
            }
        }catch(Exception e){}
        adapter.notifyDataSetChanged();
    }



}