package com.example.myfirstapp;


import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
 
public class SingleListItem extends ActionBarActivity{
    static final String KEY_NAME = "name";
    static final String KEY_COST = "type";
    static final String KEY_DESC = "description";
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
	private static final String TAG = "MyActivity";
	ArrayList<XMLDataCollected> infoList = new ArrayList<XMLDataCollected>();//data is set into this
	ArrayList<XMLDataCollected> compareList = new ArrayList<XMLDataCollected>();//data is set into this
    private int[] COLORS={Color.RED,Color.MAGENTA,Color.YELLOW};
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_list_item_view);
        
        //retrieve all TextViews
        TextView name = (TextView) findViewById(R.id.name);
        TextView servingSize = (TextView) findViewById(R.id.servingSize);
        TextView calories = (TextView) findViewById(R.id.calories);
        TextView fat = (TextView) findViewById(R.id.fat);
        TextView carbs = (TextView) findViewById(R.id.carbs);
        TextView protein = (TextView) findViewById(R.id.protein);
        
        //gets the passed maintained "menu" list
        Intent i = getIntent();
        if(i.getSerializableExtra("list") != null){
      	  infoList = (ArrayList<XMLDataCollected>) i.getSerializableExtra("list");//passing saved list
        }
        
        //gets the intent data and stores it into an object
        String nameValue = i.getStringExtra(KEY_NAME);
        XMLDataCollected info = new XMLDataCollected();//data is set into this
        String descriptionValue = i.getStringExtra(KEY_DESC);
        info.setDescription(descriptionValue);
        
        //calculate different metrics
        String[] servingSizeInfo = info.getMetric();
        String servingSizeFinal = servingSizeInfo[0] +" "+servingSizeInfo[1];
        String caloriesFinal = info.calculateCalories()+"cals";
        String fatFinal = info.getFat()+"g";
        String carbsFinal = info.getCarbs()+"g";
        String proteinFinal = info.getProtein()+"g";
        
        //store metrics and update the view
        name.setText("Name: " + nameValue);
        servingSize.setText("Serving Size: "+servingSizeFinal);
        calories.setText("Calories: "+caloriesFinal);
        fat.setText("Fat: "+fatFinal);
        //setting background based on this color in order to make it more dynamic
        fat.setBackgroundColor(COLORS[2]);
        carbs.setText("Carbs: "+carbsFinal);
        carbs.setBackgroundColor(COLORS[0]);
        protein.setText("Protein: "+proteinFinal);
        protein.setBackgroundColor(COLORS[1]);
        
        //get values to create the pie graph
		float proteinV = info.getProtein()*4;
		float carbsV =info.getCarbs()*4;
		float fatV = info.getFat()*9;
		float values[]={carbsV,proteinV,fatV};
        LinearLayout lv1 = (LinearLayout) findViewById(R.id.linear);  
        values = calculateData(values);  
        MyGraphview graphview = new MyGraphview(this, values);  
        lv1.addView(graphview);      
    }
    
    
    //pie graph creation adapted from http://stackoverflow.com/questions/4397192/draw-pie-chart-in-android
    private float[] calculateData(float[] data) {  
    	float total = 0;  
    	for (int i = 0; i < data.length; i++) {  
    	total += data[i];  
    	}  
    	for (int i = 0; i < data.length; i++) {  
    	data[i] = 360 * (data[i] / total);  
    	}  
    	return data;  
    	}  
    	  
    public class MyGraphview extends View
    {
        private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        private float[] value_degree;

        RectF rectf = new RectF (20, 20, 400, 400);
        int temp=0;
        public MyGraphview(Context context, float[] values) {

            super(context);
            value_degree=new float[values.length];
            for(int i=0;i<values.length;i++)
            {
                value_degree[i]=values[i];
            }
        }
    	  
        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.onDraw(canvas);

            for (int i = 0; i < value_degree.length; i++) {//values2.length; i++) {
                if (i == 0) {
                    paint.setColor(COLORS[i]);
                    canvas.drawArc(rectf, 0, value_degree[i], true, paint);
                } 
                else
                {
                        temp += (int) value_degree[i - 1];
                        paint.setColor(COLORS[i]);
                        canvas.drawArc(rectf, temp, value_degree[i], true, paint);
                }
            }
        } 
    	}  
    	  
    
    
    //swipe motion detection adapted from http://stackoverflow.com/questions/6645537/how-to-detect-the-swipe-left-or-right-in-android
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {     
        switch(event.getAction())
        {
          case MotionEvent.ACTION_DOWN:
              x1 = event.getX();     
          break;         
          case MotionEvent.ACTION_UP:
              x2 = event.getX();
              float deltaX = x2 - x1;
              if (Math.abs(deltaX) > MIN_DISTANCE)
              {
            	  Intent i = getIntent();
            	  Intent in = new Intent(getApplicationContext(), MainActivity.class);
            	  //passes the current "compare item list" if there
            	  if(i.getSerializableExtra("list") != null){
                	  infoList = (ArrayList<XMLDataCollected>) i.getSerializableExtra("list");//passing saved list
                  }                     
            	  
            	  //get the values from intent, setup the object to add to either list
            	  String nameValue = i.getStringExtra(KEY_NAME);  
                  String descriptionValue = i.getStringExtra(KEY_DESC);
                  String costValue = i.getStringExtra(KEY_COST);
                  XMLDataCollected info = new XMLDataCollected();//data is set into this
                  info.setName(nameValue);
                  info.setType(costValue );
                  info.setDescription( descriptionValue);
                  
                  // Left to Right swipe action Add to menu list
                  if (x2 > x1)
                  {
                	//Adding object to the "planned menu" list
                	  infoList.add(info);
                	  in.putExtra("list", infoList);
                	  //passing the compare list if not null
                	  if(i.getSerializableExtra("compare") != null){
                		  compareList = (ArrayList<XMLDataCollected>) i.getSerializableExtra("compare");//passing saved list
                		  in.putExtra("compare", compareList);
                		  }   
                	  Toast.makeText(this, "Left to Right swipe [ADD]next", Toast.LENGTH_SHORT).show ();
                	  startActivity(in);
                  }

                  // Right to left swipe action Add to compare list            
                  else 
                  {
                	  //replace old compare with new compare, only one compare item allowed
                	  compareList = new ArrayList<XMLDataCollected>();
                      Toast.makeText(this, "Right to Left swipe [COMPARE]previous", Toast.LENGTH_SHORT).show ();   
                      compareList.add(info);
                  	  in.putExtra("compare", compareList);
                      in.putExtra("list", infoList);
                      startActivity(in);   
                  }
              }
              else
              {
              }                          
          break;   
        }           
        return super.onTouchEvent(event);       
    }
}


