package com.jereksel.rommanager;

/*
 * http://www.tutecentral.com/android-custom-navigation-drawer/
 */

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.StrictMode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends Activity {

    // All static variables
    public static final String URL = "http://192.168.1.2/rom.xml";
    // XML node keys
    static final String KEY_ITEM = "rom"; // parent node
    static final String KEY_PARENT = "rom"; // parent node

    public static final int PLEASE_WAIT_DIALOG = 1;
	
	private DrawerLayout mDrawerLayout;
	public static ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	CustomDrawerAdapter adapter;

	List<DrawerItem> dataList;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		final MainActivity context = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);

		// Initializing
		dataList = new ArrayList<DrawerItem>();
		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
				
		
		new Thread() {
			public void run() {

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
	        	
	        	
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.w("myApp", "TEST3");
						CustomDrawerAdapter adapter = new CustomDrawerAdapter(context, R.layout.custom_drawer_item,
								dataList);
								mDrawerList.setAdapter(adapter);
								mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
								Log.w("myApp", "KONIEC");
	            		        
								if (savedInstanceState == null) {
									SelectItem(0);
		}}});}}.start();

		
//		new FirstDrawerCreater(this).execute();
		

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		


	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void SelectItem(final int possition) {

		new Thread() {
			public void run() {
		
		Fragment fragment = null;
		
		final Bundle args = new Bundle();

		
		if (possition==0) {
			fragment = new Status();
		}
		else {

			fragment = new ROMList();
			args.putString(ROMList.ROM_NAME, dataList.get(possition).getItemName());

		}
		

		
		fragment.setArguments(args);
		FragmentManager frgManager = getFragmentManager();
		
		
		final FragmentManager frgManagerFinal = frgManager;
		final Fragment fragmentfinal = fragment;

				frgManagerFinal.beginTransaction().replace(R.id.content_frame, fragmentfinal)
				.commit();
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
				
		mDrawerList.setItemChecked(possition, true);
		setTitle(dataList.get(possition).getItemName());
		mDrawerLayout.closeDrawer(mDrawerList);

			}});}}.start();
		
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return false;
	}

	public class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
		//	Log.d("myApp",view.toString());
			SelectItem(position);

		}
	}


//Czytaj wiêcej na: http://javastart.pl/narzedzia/asynctask/#ixzz2zqbHTIQJ

}
