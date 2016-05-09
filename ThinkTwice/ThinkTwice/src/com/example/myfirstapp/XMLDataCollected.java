package com.example.myfirstapp;

import java.io.Serializable;

import android.util.Log;

public class XMLDataCollected implements Serializable{
	int id =0;
    String name=null;
    String type=null;
    String description = null;//Per 342g - Calories: 835kcal | Fat: 32.28g | Carbs: 105.43g | Protein: 29.41g</food_description>
	private static final String TAG = "MyActivity";
    public void setId(int id){
    	this.id = id;
    }
    public void setName(String name){
    	this.name = name;
    }
    public void setType(String type){
    	this.type = type;
    }    
    public void setDescription(String description){
    	this.description = description;
    }
    
    
    public int getId(){
    	return this.id;
    }
    public String getName(){
    	return this.name;
    }
    public String getType(){
    	return this.type;
    }    
    public String getDescription(){
    	return this.description;
    }
    
    public int calculateCalories(){
		String[] split= this.description.split("\\s+");
		String calories = split[split.length-10];
		String a = calories.substring(0, (calories.length()-4)); 
		int result = Integer.parseInt(a);
		return result;
    }
    
    public String[] getMetric(){
		String[] split= this.description.split("\\s+");
		String valueString = split[1];
		String gramCheck = valueString.substring( (valueString.length()-1), (valueString.length())); 
		String metric = "";
		int i = 2;
		while(i<split.length){
			if(split[i].equals("-")){
				break;
			}
			metric = metric + " " +split[i];
		i++;
		}
		if(gramCheck.equals("g") ){
			valueString = valueString.substring( 0, (valueString.length()-1));
			metric = "g";
		}
		String[] result = new String[]{valueString, metric};
		return result;
    }
    
    public float getProtein(){
		String[] split= this.description.split("\\s+");
		String proteinString = split[split.length-1];
		String proteinValue = proteinString.substring( 0, (proteinString.length()-1));
		float protein = Float.parseFloat(proteinValue);
		return protein;

	}
    public float getCarbs(){
		String[] split= this.description.split("\\s+");
		String carbsString = split[split.length-4];
		String carbsValue = carbsString.substring( 0, (carbsString.length()-1));
		float carbs = Float.parseFloat(carbsValue);
		return carbs;
	}
    public float getFat(){
		String[] split= this.description.split("\\s+");
		String fatString = split[split.length-7];
		String fatValue = fatString.substring( 0, (fatString.length()-1));
		float fat = Float.parseFloat(fatValue);
		return fat;
	}
    
    
    
}
