package edu.ewubd.EventManagement;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.ewubd.calculatorassignment.R;

public class CreateEventActivity extends AppCompatActivity {

    Button cancel, share, save;
    EditText name, place, type, budget, capacity, dateTime, email,phone, description;
    RadioButton indoor, outdoor,online;
    TextView errorTv;
    private String existingKey="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        cancel = findViewById(R.id.cancel);
        share = findViewById(R.id.share);
        save= findViewById(R.id.save);


        name= findViewById(R.id.name);
        place= findViewById(R.id.place);
        budget = findViewById(R.id.budget);
        capacity = findViewById(R.id.capacity);
        dateTime = findViewById(R.id.dateTime);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        indoor= findViewById(R.id.indoor);
        outdoor = findViewById(R.id.outdoor);
        online = findViewById(R.id.online);
        description = findViewById(R.id.description);
        errorTv = findViewById(R.id.errorTv);

        Intent i = getIntent();
        if(i.hasExtra("VALUE")){

            String value = i.getStringExtra("VALUE");
            //KeyValueDB db = new KeyValueDB(CreateEventActivity.this);
            String values[] = value.split("---");
            name.setText(values[0]);
            place.setText(values[1]);
            if(values[2].equalsIgnoreCase("out")){
                outdoor.setChecked(true);
            }
            else if(values[2].equalsIgnoreCase("in")){
                indoor.setChecked(true);
            }
            else if(values[2].equalsIgnoreCase("on")){
                online.setChecked(true);
            }
            dateTime.setText(values[3]);
            capacity.setText(values[4]);
            budget.setText(values[5]);
            email.setText(values[6]);
            phone.setText(values[7]);
            description.setText(values[8]);
            System.out.println(values[0]);


            //db.close();

        }

        if(i.hasExtra("EVENT_KEY")){
            existingKey = i.getStringExtra("EVENT_KEY");
        }



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String errmsg = "";
                boolean out = outdoor.isChecked();
                boolean in = indoor.isChecked();
                boolean on = online.isChecked();

                String tfname = name.getText().toString();
                String tfplace = place.getText().toString();
                String tfdateTime =dateTime.getText().toString();
                String tfcapacity = capacity.getText().toString();
                String tfbudget = budget.getText().toString();
                String tfemail = email.getText().toString();
                String tfphone = phone.getText().toString();
                String tfdescription = description.getText().toString();



                String type ="";
                if(out){
                    type = "out";
                }
                else if (in)
                    type = "in";
                else
                    type = "on";

                if(out == false && in == false && on == false){
                    errmsg += "Event type was not selected\n";
                }

                if(tfname.length() < 3){
                    errmsg += "Invalid Name\n";
                }

                if(tfplace.length() < 5){
                    errmsg += "Invalid Place Name\n";
                }

                if(tfdescription.length() < 5){
                    errmsg += "Invalid Place Name\n";
                }
                if(!(tfemail.contains("@")) || !(tfemail.contains("."))){
                    errmsg += "Invalid Email\n";
                }
                if(errmsg.length()>0){
                    errorTv.setText(errmsg);
//                    Toast.makeText(CreateEventActivity.this,"hello",Toast.LENGTH_LONG).show();
                    return;
                }



                else {
//                System.out.println(key);
                    String value = tfname + "---" + tfplace + "---" + type + "---" + tfdateTime + "---" + tfcapacity + "---" +
                            tfbudget + "---" + tfemail + "---" + tfphone + "---" + tfdescription + "---";
                    KeyValueDB db = new KeyValueDB(CreateEventActivity.this);

                    if (existingKey.length() == 0) {
                        String key = tfname + System.currentTimeMillis();
                        existingKey = tfname;
                        db.insertKeyValue(key, value);

                    } else {
                        db.updateValueByKey(existingKey, value);
                    }
                    db.close();

                    //Remote Database
                String[] keys = {"action","id","semester","key","event"};
                String[] values = {"backup","2018260039","2023-1",existingKey,value};
                httpRequest(keys,values);
                errorTv.setText("saved Successful");
                Toast.makeText(CreateEventActivity.this,"Saved Successful",Toast.LENGTH_LONG).show();

                Intent  i = new Intent(CreateEventActivity.this,MainActivity.class);
                startActivity(i);
                    finish();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }
    private void httpRequest(final String keys[],final String values[]){
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
                for(int i=0; i<ja.length(); i++){
                    JSONObject event = ja.getJSONObject(i);
                    String eventKey = event.getString("e_key");
                    String eventValue = event.getString("e_value");
                }
            }
        }catch(Exception e){}
    }




}