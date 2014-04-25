package com.jereksel.rommanager;

import com.jereksel.rommanager.RomDetailed;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ROMList extends Fragment {

    // XML node keys
	public static final String ROM_NAME = "romname";
    public static String KEY_ITEM = "CM";
    static final String KEY_PARENT = "rom";
    static final String KEY_ID = "id";
    static final String KEY_NAME = "name";
    static final String KEY_DOWNLOAD = "download";

    
    static final String KEY_OTHER_DATA = "other-info";
    static final String KEY_INFO = "info";

	ImageView ivIcon;
	TextView tvItemName;

	public static final String IMAGE_RESOURCE_ID = "iconResourceID";
	public static final String ITEM_NAME = "itemName";

	private String xdathread="";
	private String author="";
	
	public ROMList() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container,
			Bundle savedInstanceState) {

		Toast.makeText(container.getContext(), getArguments().getString(ROM_NAME), 
     		   Toast.LENGTH_SHORT).show();
		
		String rooomname = getArguments().getString(ROM_NAME);
		Log.w("myApp", rooomname);
	    ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();
	    
	    String URL="http://192.168.1.2/cm.xml";
	    
	    if(rooomname.equals("CM")){URL = "https://raw.githubusercontent.com/jereksel/YouShouldNotEnterForNow/master/cm.xml";}
	    else if(rooomname.equals("PAC")){URL = "http://192.168.1.2/pac.xml";}
	    else if(rooomname.equals("AOSP")){URL = "http://192.168.1.2/aosp.xml";}

	    
            
	    Log.w("DANE URL", URL);
	    
	    
	    
        XMLParser parser = new XMLParser();
        String xml = parser.getXmlFromUrl(URL); // getting XML
        Document doc = parser.getDomElement(xml); // getting DOM element
 
        NodeList nl = doc.getElementsByTagName(KEY_PARENT);
	    
        for (int i = 0; i < nl.getLength(); i++) {
            // creating new HashMap
            HashMap<String, String> map = new HashMap<String, String>();
            Element e = (Element) nl.item(i);
            // adding each child node to HashMap key => value
            map.put(KEY_NAME, parser.getValue(e, KEY_NAME));
            map.put(KEY_DOWNLOAD, parser.getValue(e, KEY_DOWNLOAD));
            
            // adding HashList to ArrayList
            menuItems.add(map);
        }
        
        nl = doc.getElementsByTagName(KEY_OTHER_DATA);
        
        xdathread = parser.getValue((Element) nl.item(0), "xda-thread");
        author = parser.getValue((Element) nl.item(0), "author");

          
		View view = inflater.inflate(R.layout.romlist_layout, container, false);
		
	    ListView lv = (ListView) view.findViewById(R.id.listview);

        lv.setOnItemClickListener(new OnItemClickListener() {
        	 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String version = ((TextView) view.findViewById(R.id.label)).getText().toString();
                 
                // TOAST
            //    Toast.makeText(container.getContext(), name, 
            //    		   Toast.LENGTH_LONG).show();
 
                String download = ((TextView) view.findViewById(R.id.download)).getText().toString();

                
                // Starting new intent
                Intent in = new Intent(container.getContext(), RomDetailed.class);
                in.putExtra("VERSION", version);
                in.putExtra("AUTHOR", author);
                in.putExtra("DOWNLOAD", download);
                in.putExtra("XDA", xdathread);
                startActivity(in);
                
                
            }
        });
	    
	    
	    
	    lv.setAdapter(new SimpleAdapter(container.getContext(), menuItems,
                R.layout.list_item,
                new String[] {KEY_NAME, KEY_DOWNLOAD }, new int[] {
                        R.id.label, R.id.download }));   

	    
		return view;
		
	}

}
