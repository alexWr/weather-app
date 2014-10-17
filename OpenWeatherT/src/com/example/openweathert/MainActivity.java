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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {	
	String responseGeo=null;
	double lon,lat;
	EditText etCity;	
	TextView tvWeather,tvTemp,tvDay1,tvDay2,tvDay3,tvDay4;
	String city,responseString=null,responseString5=null,citySave;
	int humid,temp;
	SharedPreferences sPref;	
	ArrayList<Integer> humid5=new ArrayList<Integer>();
	ArrayList<Integer> temp5=new ArrayList<Integer>();
	GPSTracker gps=new GPSTracker(this);
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
    }
    @Override
   
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_action, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
   public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
        case R.id.action_settings:         	
            return true;
        case R.id.action_enter:
        	return true;
        case R.id.action_show_weather:
        	saveCity();
			new RequestTask().execute("http://api.openweathermap.org/data/2.5/weather?q="+etCity.getText().toString());
        	return true;
        case R.id.action_show_weather5:
        	new RequestTask5().execute("http://api.openweathermap.org/data/2.5/forecast/daily?q="+
        	etCity.getText().toString()+"&mode=json&units=metric&cnt=5");
        	return true;
        case R.id.action_geo:
        	new RequestTaskGeo().execute("http://api.openweathermap.org/data/2.5/forecast/daily?lat="+
        	lat+"&lon="+lon+"&cnt=5&mode=json");
        	return true;
        }
        
        return super.onOptionsItemSelected(item);
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
            tvDay1.setText(temp5.get(1)+"^C\n"+humid5.get(1)+"%");
            tvDay2.setText(temp5.get(2)+"^C\n"+humid5.get(2)+"%");
            tvDay3.setText(temp5.get(3)+"^C\n"+humid5.get(3)+"%");
            tvDay4.setText(temp5.get(4)+"^C\n"+humid5.get(4)+"%");
            
        }
    }
    class RequestTaskGeo extends AsyncTask<String, String, String>{

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
                    responseGeo = out.toString();                   
                    parseDataGeo(responseGeo);                   
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } 
            catch (Exception e) {               
            }
            return responseGeo;
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
            tvDay1.setText(temp5.get(1)+"^C\n"+humid5.get(1)+"%");
            tvDay2.setText(temp5.get(2)+"^C\n"+humid5.get(2)+"%");
            tvDay3.setText(temp5.get(3)+"^C\n"+humid5.get(3)+"%");
            tvDay4.setText(temp5.get(4)+"^C\n"+humid5.get(4)+"%");
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
    		JSONArray jArray=json_array.getJSONArray("list");    		
    		for(int j=0;j<jArray.length();j++){    			
    			JSONObject newJson=jArray.getJSONObject(j);
    			JSONObject newJson1=newJson.getJSONObject("temp");
        		temp5.add(newJson1.getInt("day"));        		
        		humid5.add(newJson.getInt("humidity"));
    		}    		    			    			    			            		                                       		    	
    	}	    		    
    }
    void parseDataGeo(String data) throws JSONException {    	
    	JSONObject json_array=new JSONObject(data);    	
    	for(int i=0; i<json_array.length();i++){    		
    		JSONObject jsonCity=json_array.getJSONObject("city");    		
    		city=jsonCity.getString("name");    		
    		JSONArray jArray=json_array.getJSONArray("list");    		
    		for(int j=0;j<jArray.length();j++){    			
    			JSONObject newJson=jArray.getJSONObject(j);
    			JSONObject newJson1=newJson.getJSONObject("temp");
        		temp5.add(newJson1.getInt("day")-272);        		
        		humid5.add(newJson.getInt("humidity"));
    		}    		   			    			    			            		                                       		    		
    	}	    		    
    }    
}

