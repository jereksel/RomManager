package com.jereksel.rommanager;

/*
 * http://www.tutecentral.com/android-custom-navigation-drawer/
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
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

	// XML node keys
	static final String KEY_ITEM = "rom"; // parent node

	private DrawerLayout mDrawerLayout;
	public static ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	CustomDrawerAdapter adapter;

	private ProgressDialog pDialog;

	List<DrawerItem> dataList;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		final MainActivity context = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Initializing
		dataList = new ArrayList<DrawerItem>();
		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		pDialog = ProgressDialog.show(context, "Downloading/Preparing Data..",
				"Please wait", true, false);

		final DownloadXMLs[] array = new DownloadXMLs[(Data.xml).length];

		for (int i = 0; i <= (Data.xml).length - 1; i++) {
			Log.w("TESCIK", String.valueOf(i));
			array[i] = new DownloadXMLs(i, context);
			(array[i]).start();
		}

		Log.w("myApp", "TEST1");

		new Thread() {
			public void run() {

				dataList = new ArrayList<DrawerItem>();

				dataList.add(new DrawerItem("Status",
						R.drawable.ic_action_settings));

				Log.w("myApp", "TEST2");


				for (int i = 0; i <= (Data.xml).length - 1; i++) {
					try {
						array[i].join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				BufferedReader xml = null;
				StringBuilder total = null;
				try {
					File file = new File(context.getFilesDir(), Data.xml[0]);
					InputStream inputStream = new FileInputStream(file);
					xml = new BufferedReader(new InputStreamReader(inputStream));
					total = new StringBuilder();
					String line;

					while ((line = xml.readLine()) != null) {
						total.append(line);
					}
				} catch (Exception e) {
					Log.e("Error: ", e.getMessage());
				}

				XMLParser parser = new XMLParser();
				Document doc = parser.getDomElement(total.toString()); // getting
																		// DOM
																		// element

				Log.w("INFO: ", total.toString());

				NodeList nl = doc.getElementsByTagName(KEY_ITEM);

				for (int i = 0; i < nl.getLength(); i++) {
					Element e = (Element) nl.item(i);
					dataList.add(new DrawerItem(parser.getValue(e, "name"),
							R.drawable.ic_action_settings));
				}
				pDialog.dismiss();

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.w("myApp", "TEST3");
						CustomDrawerAdapter adapter = new CustomDrawerAdapter(
								context, R.layout.custom_drawer_item, dataList);
						mDrawerList.setAdapter(adapter);
						mDrawerList
								.setOnItemClickListener(new DrawerItemClickListener());
						Log.w("myApp", "KONIEC");

						if (savedInstanceState == null) {
							SelectItem(0);
						}
					}
				});
			}
		}.start();

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

		//Maybe this Thread is not necessary
		new Thread() {
			public void run() {

				Fragment fragment = null;

				final Bundle args = new Bundle();

				if (possition == 0) {
					fragment = new Status();
				} else {

					fragment = new ROMList();
					args.putString(ROMList.ROM_NAME, dataList.get(possition)
							.getItemName());

				}

				fragment.setArguments(args);
				FragmentManager frgManager = getFragmentManager();

				final FragmentManager frgManagerFinal = frgManager;
				final Fragment fragmentfinal = fragment;

				frgManagerFinal.beginTransaction()
						.replace(R.id.content_frame, fragmentfinal).commit();

			}
		}.start();

		mDrawerList.setItemChecked(possition, true);
		setTitle(dataList.get(possition).getItemName());
		mDrawerLayout.closeDrawer(mDrawerList);

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

			SelectItem(position);

		}
	}

	private class DownloadXMLs extends Thread {

		int i;
		MainActivity context;

		public DownloadXMLs(int i, MainActivity context) {

			this.i = i;
			this.context = context;

		}

		public void run() {

			int count;
			try {
				URL url = new URL(Data.downloadxml[i]);
				File file2 = new File(context.getFilesDir(), Data.xml[i]);
				if (!(file2.exists())) {
					URLConnection conection = url.openConnection();
					String location = file2.getAbsolutePath();
					conection.connect();
					InputStream input = new BufferedInputStream(
							url.openStream(), 8192);
					OutputStream output = new FileOutputStream(location);

					byte data[] = new byte[1024];

					while ((count = input.read(data)) != -1) {
						// publishing the progress....
						// After this onProgressUpdate will be called
						// writing data to file
						output.write(data, 0, count);
					}

					// flushing output
					output.flush();

					// closing streams
					output.close();
					input.close();

				}
			} catch (Exception e) {
				Log.e("Error: ", e.getMessage());
			}

			Log.w("Zakoñczono: ", String.valueOf(i));
		}
	}

}
