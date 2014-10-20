package com.example.openweathert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EnterCity extends Activity implements OnClickListener{
	
	EditText etCity;
	Button btnSubmit;
	  
	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.city);
	        
	        etCity = (EditText) findViewById(R.id.etCity);
	        btnSubmit = (Button) findViewById(R.id.btnSubmit);
	        btnSubmit.setOnClickListener(this);
	        
	    }

	  @Override
	  public void onClick(View v) {
		  Intent intent = new Intent();
		    intent.putExtra("name", etCity.getText().toString());
		    setResult(RESULT_OK, intent);
		    finish();
	  }	  
}
