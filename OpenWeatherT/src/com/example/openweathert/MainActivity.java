package com.example.openweathert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
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
	Button btnSend,btnSend2;
	TextView tvWeather;
	String city,responseString=null;
	int humid,temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        etCity=(EditText)findViewById(R.id.etCity);
        tvWeather=(TextView)findViewById(R.id.tvWeather);
        btnSend=(Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        btnSend2=(Button)findViewById(R.id.btnSend2);
        btnSend2.setOnClickListener(this);
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
    		case R.id.btnSend:
    			new RequestTask().execute("http://api.openweathermap.org/data/2.5/weather?q="+etCity.getText().toString());
    			break;
    		case R.id.btnSend2:
    			Intent intent=new Intent(this,Weather5.class);
    			startActivity(intent);
    	} 
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
            tvWeather.setTextSize(10);
            tvWeather.setText("City: "+city+"\n"+"humidity: "+humid+"%\n"+ "temperature: "+temp+"^C");
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
}

