package com.jereksel.rommanager;
 
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
 
public class FirstDrawerCreater extends AsyncTask<Void, Void, Void> {
 
    Activity wywolujaceActivity;
    ArrayList<DrawerItem> dataList;
    
    public static final String URL = "http://192.168.1.2/rom.xml";

    public FirstDrawerCreater(Activity wywolujaceActivity) {
        this.wywolujaceActivity = wywolujaceActivity;
    }
 
    @Override
    protected void onPreExecute() {
    	Log.w("myApp", "TEST1");
    }
    
    @Override
    protected Void doInBackground(Void... arg0) {
		
    	dataList = new ArrayList<DrawerItem>();
    	
		dataList.add(new DrawerItem("Status", R.drawable.ic_action_settings));

		Log.w("myApp", "TEST2");
		
        XMLParser parser = new XMLParser();
        String xml = parser.getXmlFromUrl(URL); // getting XML
        Document doc = parser.getDomElement(xml); // getting DOM element

        NodeList nl = doc.getElementsByTagName("rom");
        
        for (int i = 0; i < nl.getLength(); i++) {
        	Element e = (Element) nl.item(i);
        	dataList.add(new DrawerItem(parser.getValue(e, "name"), R.drawable.ic_action_settings));
        }
        
        Log.w("myApp", "TEST3");

        new Runnable() {
            public void run() {
        CustomDrawerAdapter adapter = new CustomDrawerAdapter(wywolujaceActivity, R.layout.custom_drawer_item,
    			dataList);

        ListView mDrawerList = MainActivity.mDrawerList;
        	
        mDrawerList.setAdapter(adapter);
            }};
        
        Log.w("myApp", "KONIEC");
		return null;
    }
    
    protected Void onPostExecute() {
    	

    
    return null;
    
    }

 
}
