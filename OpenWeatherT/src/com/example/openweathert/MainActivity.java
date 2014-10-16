package com.example.openweathert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements OnClickListener {
	EditText etCity;
	Button btnSend,btnSend2,btnEnter;
	TextView tvWeather,tvTemp,tvDay1,tvDay2,tvDay3,tvDay4;
	String city,responseString=null,responseString5=null,citySave;
	int humid,temp;
	SharedPreferences sPref;	
	ArrayList<Integer> humid5=new ArrayList<Integer>();
	ArrayList<Integer> temp5=new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        etCity=(EditText)findViewById(R.id.etCity);
        tvWeather=(TextView)findViewById(R.id.tvWeather);
        tvDay1=(TextView)findViewById(R.id.tvDay1);
        tvDay2=(TextView)findViewById(R.id.tvDay2);
        tvDay3=(TextView)findViewById(R.id.tvDay3);
        tvDay4=(TextView)findViewById(R.id.tvDay4);
        tvTemp=(TextView)findViewById(R.id.tvTemp);
        btnSend=(Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        btnEnter=(Button)findViewById(R.id.btnEnter);
        btnEnter.setOnClickListener(this);
        btnSend2=(Button)findViewById(R.id.btnSend2);
        btnSend2.setOnClickListener(this);
        loadCity();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onClick(View v) {
    	if(etCity.getText().toString()==""){
    		 Toast.makeText(MainActivity.this, "Plese enter the city", Toast.LENGTH_SHORT).show();
    	}
    	switch(v.getId()){
    		/*case R.id.btnEnter:
    			Intent intent=new Intent(this,Weather5.class);
    			startActivity(intent);
    			break;*/
    		case R.id.btnSend:
    			saveCity();
    			new RequestTask().execute("http://api.openweathermap.org/data/2.5/weather?q="+etCity.getText().toString());
    			break;
    		case R.id.btnSend2:
    			new RequestTask5().execute("http://api.openweathermap.org/data/2.5/forecast/daily?q="+
    			etCity.getText().toString()+"&mode=json&units=metric&cnt=5");
    			break;
    	} 
    }
    void saveCity() {
        sPref = getPreferences(MODE_PRIVATE);
        Editor ed = sPref.edit();
        ed.putString(citySave, etCity.getText().toString());
        ed.commit();        
      }
    void loadCity() {
        sPref = getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString(citySave, "");
        etCity.setText(savedText);        
      }
    class RequestTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;            
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();                    
                    parseData(responseString);                    
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } 
            catch (Exception e) {               
            }
            return responseString;
        }
        
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            tvTemp.setTextSize(35);
            tvTemp.setText(temp+"^C\n");
            tvWeather.setTextSize(10);
            tvWeather.setText(humid+"%\n"+city);
        }
    }
    
    class RequestTask5 extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;            
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString5 = out.toString();                    
                    parseData5(responseString5);                    
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } 
            catch (Exception e) {               
            }
            return responseString5;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);            
            tvTemp.setTextSize(35);
            tvTemp.setText(temp5.get(0)+"^C\n");
            tvWeather.setTextSize(10);
            tvWeather.setText(humid5.get(0)+"%\n"+city);
            tvDay1.setTextSize(10);
            tvDay2.setTextSize(10);
            tvDay3.setTextSize(10);
            tvDay4.setTextSize(10);
            tvDay1.setText(temp5.get(1)+"\n"+humid5.get(1));
            tvDay2.setText(temp5.get(2)+"\n"+humid5.get(2));
            tvDay3.setText(temp5.get(3)+"\n"+humid5.get(3));
            tvDay4.setText(temp5.get(4)+"\n"+humid5.get(4));
            
        }
    }
    void parseData(String data) throws JSONException {    	        
    	JSONObject json_array=new JSONObject(data);    
    	for(int i=0; i<json_array.length();i++){    		
    		city=json_array.getString("name");
    		JSONObject newJson=json_array.getJSONObject("main");
    		temp=newJson.getInt("temp")-272;
    		humid=newJson.getInt("humidity");	    			    			            		                                       		    		
    	}	    		    
    }
    void parseData5(String data) throws JSONException {    	
    	JSONObject json_array=new JSONObject(data);    	
    	for(int i=0; i<json_array.length();i++){    		
    		JSONObject jsonCity=json_array.getJSONObject("city");    		
    		city=jsonCity.getString("name");
    		Log.d("myLogs","T1");
    		JSONArray jArray=json_array.getJSONArray("list");
    		Log.d("myLogs","T2");
    		for(int j=0;j<jArray.length();j++){
    			Log.d("myLogs","T3");
    			JSONObject newJson=jArray.getJSONObject(j);
    			JSONObject newJson1=newJson.getJSONObject("temp");
        		temp5.add(newJson1.getInt("day"));
        		Log.d("myLogs","T5");
        		humid5.add(newJson.getInt("humidity"));
    		}    		
    			    			    			            		                                       		    		
    	}	    		    
    }    
}

