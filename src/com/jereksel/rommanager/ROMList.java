/*
 * Copyright (C) 2014 Andrzej Ressel (jereksel@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jereksel.rommanager;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class ROMList extends Fragment {

    // XML node keys
    public static final String ROM_NAME = "romname";
    private static final String KEY_PARENT = "rom";
    private static final String KEY_NAME = "name";
    private static final String KEY_DOWNLOAD = "download";
    private static final String KEY_CHANGELOG = "changelog";
    private static final String KEY_OTHER_DATA = "other-info";
    private boolean valid = true;
    private String xdathread;
    private String author;

    public ROMList() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();
        String xmlfile;

        xmlfile = getArguments().getString(ROM_NAME).toLowerCase() + ".xml";

        Log.w("NAZWA:", xmlfile);

        XMLParser parser = new XMLParser();

        BufferedReader xml;
        StringBuilder total = null;
        try {
            File file = new File(container.getContext().getFilesDir(),
                    xmlfile);
            InputStream inputStream = new FileInputStream(file);
            xml = new BufferedReader(new InputStreamReader(inputStream));
            total = new StringBuilder();
            String line;

            while ((line = xml.readLine()) != null) {
                total.append(line);
            }
        } catch (Exception e) {
            Toast.makeText(container.getContext(), "ERROR" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.w("ERROR:", e.getMessage());
        }

        Document doc = parser.getDomElement(total.toString());

        NodeList nl = doc.getElementsByTagName(KEY_PARENT);

        if (nl.getLength() == 0)
            // XML file is not valid
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
            map.put(KEY_CHANGELOG, parser.getValue(e, KEY_CHANGELOG));

            Log.w("CHANGELOG:", parser.getValue(e, KEY_CHANGELOG));

            // adding HashList to ArrayList
            menuItems.add(map);
        }

        nl = doc.getElementsByTagName(KEY_OTHER_DATA);

        if (valid) {
            xdathread = parser.getValue((Element) nl.item(0), "xda-thread");
            author = parser.getValue((Element) nl.item(0), "author");
        }

        View view = inflater.inflate(R.layout.romlist_layout, container, false);
        if (!(valid)) return view;
        ListView lv = (ListView) view.findViewById(R.id.listview);

        lv.setAdapter(new SimpleAdapter(container.getContext(), menuItems,
                R.layout.romlist_item, new String[]{KEY_NAME, KEY_DOWNLOAD, KEY_CHANGELOG},
                new int[]{R.id.label, R.id.download, R.id.changelog}));

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String version = ((TextView) view.findViewById(R.id.label))
                        .getText().toString();

                String download = ((TextView) view.findViewById(R.id.download))
                        .getText().toString();

                String changelog = ((TextView) view.findViewById(R.id.changelog))
                        .getText().toString();

                // Starting new intent
                Intent in = new Intent(container.getContext(),
                        RomDetailed.class);
                in.putExtra("VERSION", version);
                in.putExtra("AUTHOR", author);
                in.putExtra("DOWNLOAD", download);
                in.putExtra("XDA", xdathread);
                in.putExtra("CHANGELOG", changelog);
                Log.w("CHANGELOG:", changelog);
                startActivity(in);

            }
        });

        return view;

    }

}
