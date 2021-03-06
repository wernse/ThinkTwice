package com.example.myfirstapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HandlingXML extends DefaultHandler implements Serializable{
	private static final String TAG = "MyActivity";
	boolean id =false;
	boolean name=false;
	boolean type=false;
	boolean description = false;
	boolean first = true;
    static final String KEY_ID = "id";
    static final String KEY_NAME = "name";
    static final String KEY_COST = "type";
    static final String KEY_DESC = "description";
    ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();
	ArrayList<XMLDataCollected> infoList = new ArrayList<XMLDataCollected>();//data is set into this
    XMLDataCollected info = new XMLDataCollected();//data is set into this
    HashMap<String, String> map = new HashMap<String, String>();

    public ArrayList<XMLDataCollected> getInformation(){//returns info in objects.
    	return infoList;
    }
    
    public ArrayList<HashMap<String, String>> getHashMap(){//returns info in hashmap.
    	return menuItems;
    }
    
	@Override
	public void startElement(String uri, String localName, String qName,  //localName = name of node
			Attributes attributes) throws SAXException {//attributes is the attribute value e.g. city data="1"
		
		
		// TODO Auto-generated method stub
		//skip adding an empty value into the list
		if(localName.equals("food") && first == false){
			infoList.add(info);
			info = new XMLDataCollected();
			menuItems.add(map);
			map = new HashMap<String, String>();
		}
		
		if(localName.equals("food_id")){
			id = true;
			first =false;
		}			
		
		if(localName.equals("food_name")){
			name = true;
		}
		
		if(localName.equals("food_type")){
			type = true;
		}
		
		if(localName.equals("food_description")){
			description = true;
		}			
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		
		if (id) {
			String value = new String(ch, start, length);
			int idValue = Integer.parseInt(value);
			info.setId(idValue);
			map.put(KEY_ID, value);
			id = false;
		}
 
		if (name) {
			String value = new String(ch, start, length);
			info.setName(value);
			map.put(KEY_NAME, value);
			name = false;
		}
 
		if (type) {
			String value = new String(ch, start, length);
			info.setType(value);
			map.put(KEY_COST, value);
			type = false;
		}
 
		if (description) {
			String value = new String(ch, start, length);
			info.setDescription(value);
			map.put(KEY_DESC, value);
			description = false;
		} 
	}	
}
