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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RomDetailed extends Activity {

	private String download;
	private String version;
	private String author;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rom_detailed_layout);

		// getting intent data
		Intent in = getIntent();

		// Get XML values from previous intent
		version = in.getStringExtra("VERSION");
		author = in.getStringExtra("AUTHOR");
		download = in.getStringExtra("DOWNLOAD");

		// Displaying all values on the screen
		TextView name = (TextView) findViewById(R.id.rom_name);
		TextView authorView = (TextView) findViewById(R.id.rom_author);

		name.setText(version);
		authorView.setText(author);

	}

	public void DownloadWhole(View view) {
		goToUrl(download);
	}

	private void goToUrl(String url) {
		Uri uriUrl = Uri.parse(url);
		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
		startActivity(launchBrowser);
	}

}
