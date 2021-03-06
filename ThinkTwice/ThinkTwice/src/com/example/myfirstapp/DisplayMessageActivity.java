package com.example.myfirstapp;
//this is like each view? clicking send gets directed to this

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.example.FatSecret.FatSecretException;
import com.example.FatSecret.FatSecretAPI.OAuthBase;
import com.example.FatSecret.FatSecretAPI.Result;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class DisplayMessageActivity extends ActionBarActivity {
	private static final String TAG = "MyActivity";
    private static final String URL_BASE = "http://platform.fatsecret.com/rest/server.api?";
    static final String KEY_NAME = "name";
    static final String KEY_COST = "type";
    static final String KEY_DESC = "description";
	ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();//for storing swipes
	ArrayList<HashMap<String, String>> compareItems = new ArrayList<HashMap<String, String>>();//for storing swipes
	ArrayList<XMLDataCollected> infoList = new ArrayList<XMLDataCollected>();//data is set into this
	ArrayList<XMLDataCollected> compareList = new ArrayList<XMLDataCollected>();//data is set into this
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_display_message);	    
		// Get the message from the intent
	    Intent intent = getIntent();
	    String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
	    
	  //  String returnMessage = getdata();
	    // Create the text view

	    //OAUTH TIME
	    Calendar cal = Calendar.getInstance();
	    long today_ms = cal.getTimeInMillis();
	    long today = today_ms / 1000;
	    //OAUTH KEYS
	    String consumerKey = "";
	    String consumerSecret = "";
	        try {
	        	//OAUTH URL
	            String urlBase = URL_BASE + "search_expression="+message+"&method=foods.search";//use string builder https://www.youtube.com/watch?v=meXgGO4LTO8
	    	    OAuthBase oAuth = new OAuthBase();

	    		URL url = new URL(urlBase);
	    		//OAUTH
	    		Result result = new Result();
	    		oAuth.generateSignature(url, consumerKey, consumerSecret, null, null, result);    	    
	    	    HandlingXML doingWork = new HandlingXML();
	    	    HandlingXML response = doHttpMethodReq(result.getNormalizedUrl(), "POST", result.getNormalizedRequestParameters() + "&" + OAuthBase.OAUTH_SIGNATURE + "=" + URLEncoder.encode(result.getSignature(), "utf-8"), null,doingWork);
				
				
			//Do work on the parsed XML data hashmap
				 ArrayList<HashMap<String, String>> menuItems = response.getHashMap();
				 
			//Set adapter to hashmap and create the list in view
				 ListView mainListView = (ListView) findViewById(R.id.compareItem);
				 ListAdapter listAdapter = new SimpleAdapter(this, menuItems, R.layout.item,
			                new String[] { KEY_NAME, KEY_DESC, KEY_COST }, new int[] {
						 R.id.name, R.id.description, R.id.type });
				 mainListView.setAdapter( listAdapter );  
				 
				 
			//Onclick listener to remove selected item from view. 
				 mainListView.setOnItemClickListener(new OnItemClickListener() {
			          public void onItemClick(AdapterView<?> parent, View view,
			              int position, long id) {
			        	  Intent intent = getIntent();
			        	  
			        	  //get values from view
			        	  String name = ((TextView) view.findViewById(R.id.name)).getText().toString();
			              String description = ((TextView) view.findViewById(R.id.description)).getText().toString();
			              String cost = ((TextView) view.findViewById(R.id.type)).getText().toString();
			              
			              //Setting up Intent to launch new Activity on selecting single List Item
			              Intent in = new Intent(getApplicationContext(), SingleListItem.class);//2nd parameter is the activity you pass it to
			              
			              //Sending data to new activity
			              in.putExtra(KEY_NAME, name);
			              in.putExtra(KEY_DESC, description);
			              in.putExtra(KEY_COST, cost);
			              
			              //Passing the "plan to eat list" through the intents
			              if(intent.getSerializableExtra("list") != null){
			            	  ArrayList<XMLDataCollected> infoList = (ArrayList<XMLDataCollected>) intent.getSerializableExtra("list");//passing saved list
			            	  in.putExtra("list", infoList);
			              }
			            //Passing the "compare list" through the intents
			              if(intent.getSerializableExtra("compare") != null){
			            	  ArrayList<XMLDataCollected> compareList = (ArrayList<XMLDataCollected>) intent.getSerializableExtra("compare");//passing saved list
			            	  in.putExtra("compare", compareList);//passing saved list
			              }
			              startActivity(in);     
			          }
			        });
			 
	        } catch (Exception e) {
	        	Log.v(TAG, "ERROR: "+e );
	        }	       
	}       

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }


//Handles and parses the data
    public HandlingXML doHttpMethodReq(String urlStr, String requestMethod, String paramStr, Map<String, String> header, HandlingXML doingWork) throws FatSecretException{
    	StringBuffer sb = new StringBuffer();
    	try {
    		// Construct data
    		
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();//new saxparser
			XMLReader xr = sp.getXMLReader();//xml reader
			doingWork = new HandlingXML();
			xr.setContentHandler(doingWork);//look for things we want to look for
			
    		// Send data
    		URL url = new URL(urlStr);
    		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    		conn.setDoOutput(true);
    		conn.setDoInput(true);
    		if (requestMethod != null)
    			conn.setRequestMethod(requestMethod);
    		
    		if (header != null) {
    			for (String key : header.keySet()) {
    				conn.setRequestProperty(key, header.get(key));
    			}
    		} 

    		// If use POST, must use this
    		OutputStreamWriter wr = null;
    		if (requestMethod != null && !requestMethod.equals("GET") && !requestMethod.equals("DELETE")) {
    			wr = new OutputStreamWriter(conn.getOutputStream());
    			wr.write(paramStr);
    			wr.flush();
    		}
    		
    		// Get the response

    		InputSource a = new InputSource(new InputStreamReader(conn.getInputStream()));
    		xr.parse(a);//parse it from the document
    	} catch (Exception e) {
    		throw new FatSecretException(1, "An unknown error occurred: 'please try again later'");
    	}
    	
    	return doingWork; 	
    }
}
