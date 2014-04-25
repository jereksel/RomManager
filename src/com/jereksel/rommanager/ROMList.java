package com.jereksel.rommanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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

	private boolean valid = true;
	private String xdathread = "";
	private String author = "";

	public ROMList() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			final ViewGroup container, Bundle savedInstanceState) {

		String rooomname = getArguments().getString(ROM_NAME);
		Log.w("myApp", rooomname);
		ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();

		int id = 0;

		if (rooomname.equals("CM")) {
			id = 1;
		} else if (rooomname.equals("PAC")) {
			id = 2;
		} else if (rooomname.equals("AOSP")) {
			id = 3;
		}

		XMLParser parser = new XMLParser();
		// String xml = parser.getXmlFromUrl(URL); // getting XML

		BufferedReader xml = null;
		StringBuilder total = null;
		try {
			File file = new File(container.getContext().getFilesDir(),
					Data.xml[id]);
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

		try {
			xml.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// String xml = parser.getXmlFromUrl(URL);
		Document doc = parser.getDomElement(total.toString()); // getting
																// DOM
																// element

		NodeList nl = doc.getElementsByTagName(KEY_PARENT);

		if (nl.getLength() == 0)
			valid = false;

		if (!valid) {
			Toast.makeText(container.getContext(), "ERROR", Toast.LENGTH_LONG)
					.show();
		}

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

		if (valid) {
			xdathread = parser.getValue((Element) nl.item(0), "xda-thread");
			author = parser.getValue((Element) nl.item(0), "author");
		}

		View view = inflater.inflate(R.layout.romlist_layout, container, false);

		ListView lv = (ListView) view.findViewById(R.id.listview);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				String version = ((TextView) view.findViewById(R.id.label))
						.getText().toString();

				// TOAST
				// Toast.makeText(container.getContext(), name,
				// Toast.LENGTH_LONG).show();

				String download = ((TextView) view.findViewById(R.id.download))
						.getText().toString();

				// Starting new intent
				Intent in = new Intent(container.getContext(),
						RomDetailed.class);
				in.putExtra("VERSION", version);
				in.putExtra("AUTHOR", author);
				in.putExtra("DOWNLOAD", download);
				in.putExtra("XDA", xdathread);
				startActivity(in);

			}
		});

		lv.setAdapter(new SimpleAdapter(container.getContext(), menuItems,
				R.layout.list_item, new String[] { KEY_NAME, KEY_DOWNLOAD },
				new int[] { R.id.label, R.id.download }));

		return view;

	}

}
