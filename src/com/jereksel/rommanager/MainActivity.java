/*
 * Copyright (C) 2014 Ahamed Ishak
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

/*
 * http://www.tutecentral.com/android-custom-navigation-drawer/
 */

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    // XML node keys
    private static final String KEY_ITEM = "rom"; // parent node
    private static ListView mDrawerList;
    private MainActivity context;
    private ProgressDialog Dialog;
    private List<DrawerItem> dataList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String rom = "Status";
    public Menu menu;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        final MainActivity context = this;
        this.context = context;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing
        dataList = new ArrayList<DrawerItem>();
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);

        final ProgressDialog pDialog;

        pDialog = ProgressDialog.show(context, "Downloading/Preparing Data..",
                "Please wait", true, false);

        final DownloadXML DownloadThread;

        Log.w("TESCIK", "");
        DownloadThread = new DownloadXML(context, false);
        DownloadThread.start();

        new Thread() {
            public void run() {

                dataList = new ArrayList<DrawerItem>();

                dataList.add(new DrawerItem("Status"));

                try {
                    DownloadThread.join();
                } catch (InterruptedException e) {
                    Toast.makeText(getApplicationContext(), "ERROR" + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

                Log.w("START:", "pliki");
                BufferedReader xml;
                StringBuilder total = null;
                try {
                    File file = new File(context.getFilesDir(), "rom.xml");
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
                Document doc = parser.getDomElement(total.toString());
                NodeList nl = doc.getElementsByTagName(KEY_ITEM);

                for (int i = 0; i < nl.getLength(); i++) {
                    Element e = (Element) nl.item(i);
                    NodeList n = e.getElementsByTagName("name");
                    dataList.add(new DrawerItem(parser.getElementValue(n.item(0))));
                }



                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pDialog.dismiss();
                        CustomDrawerAdapter adapter = new CustomDrawerAdapter(
                                context, R.layout.custom_drawer_item, dataList);
                        mDrawerList.setAdapter(adapter);
                        mDrawerList
                                .setOnItemClickListener(new DrawerItemClickListener());

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
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void SelectItem(final int possition) {

        rom = dataList.get(possition)
                .getItemName();

        // Maybe this Thread is not necessary
        new Thread() {
            public void run() {

                Fragment fragment;
                final Bundle args = new Bundle();

                if (possition == 0) {
                    fragment = new Status();
//                    item.setVisible(false);
                } else {
                    fragment = new ROMList();
                    args.putString(ROMList.ROM_NAME, dataList.get(possition)
                            .getItemName());
                }

                fragment.setArguments(args);
                FragmentManager frgManager = getFragmentManager();

                frgManager.beginTransaction()
                        .replace(R.id.content_frame, fragment).commit();

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

        switch (item.getItemId()) {
            case R.id.delete_xml:

                new Thread() {
                    public void run() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Dialog = ProgressDialog.show(context,
                                        "Downloading/Preparing Data..",
                                        "Please wait", true, false);
                            }
                        });

                        DownloadXML array;

                        array = new DownloadXML(context, true);
                        (array).start();

                        try {
                            array.join();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Dialog.dismiss();
                                Intent mStartActivity = new Intent(context,
                                        MainActivity.class);
                                int mPendingIntentId = 123456;
                                PendingIntent mPendingIntent = PendingIntent
                                        .getActivity(context,
                                                mPendingIntentId, mStartActivity,
                                                PendingIntent.FLAG_CANCEL_CURRENT);
                                AlarmManager mgr = (AlarmManager) context
                                        .getSystemService(Context.ALARM_SERVICE);
                                mgr.set(AlarmManager.RTC,
                                        System.currentTimeMillis() + 100,
                                        mPendingIntent);
                                System.exit(0);

                            }
                        });

                    }
                }.start();
                break;

            case R.id.about_screen:
                Intent in = new Intent(context,
                        AboutScreen.class);
                startActivity(in);
                break;

            case R.id.changelog_dialog:
                Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.AppChangelog));
                builder.setCancelable(true);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }

        return mDrawerToggle.onOptionsItemSelected(item);

    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            SelectItem(position);

        }
    }

    private class DownloadXML extends Thread {

        final MainActivity context;
        final boolean deletefile;

        public DownloadXML(MainActivity context, boolean deletefile) {

            this.context = context;
            this.deletefile = deletefile;
        }

        public void run() {

            int count;
            try {
                URL url = new URL(Data.downloadxml);
                File file = new File(context.getFilesDir(), Data.xml);
                if (deletefile)
                    file.delete();
                if (!(file.exists())) {
                    URLConnection connection = url.openConnection();
                    String location = file.getAbsolutePath();
                    connection.connect();
                    InputStream input = new BufferedInputStream(
                            url.openStream());
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
                    Log.w("Pobrano: ", "");


                    Thread extract = new Thread() {
                        public void run() {
                            String command = "tar -zxvf " + context.getFilesDir() + "/rom.tar.gz";

                            Log.w("EXTRACT COMMAND:", command);

                            try {
                                Process process = Runtime.getRuntime().exec("su");
                                DataOutputStream out = new DataOutputStream(process.getOutputStream());
                                out.writeBytes("cd " + context.getFilesDir() + "\n");
                                out.writeBytes("rm -rf *.xml\n");
                                out.writeBytes("tar -zxvf rom.tar.gz\n");
                                out.writeBytes("exit\n");
                                out.flush();
                                process.waitFor();
                                Log.w("KONIEC:", "runtime");
                            } catch (IOException e) {
                                Log.w("EXTRACT ERROR:", e.getMessage());
                            } catch (InterruptedException e) {
                                Log.w("EXTRACT ERROR:", e.getMessage());
                            }
                        }
                    };

                    extract.start();
                    try {
                        extract.join();
                    } catch (InterruptedException e) {
                        Log.w("THREAD ERROR:", e.getMessage());
                    }

                }
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            Log.w("Zakonczono: ", "");
        }
    }

}
