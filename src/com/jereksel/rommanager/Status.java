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

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Status extends Fragment {

	TextView androidversion;
	TextView lcdtype;

	public Status() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.status_layout, container, false);
		
		((TextView) view.findViewById(R.id.android_version)).setTypeface(null, Typeface.BOLD);
		((TextView) view.findViewById(R.id.lcd_type)).setTypeface(null, Typeface.BOLD);

		
		androidversion = (TextView) view
				.findViewById(R.id.android_version_text);
		androidversion.setText(Build.VERSION.RELEASE);

		File dir = new File("/sys/devices/");
		if (dir.exists() && dir.isDirectory()) {
			Pattern p = Pattern.compile("pri_lcd_.*");
			File[] SysList = dir.listFiles();
			for (int i = 0; i < SysList.length; i++) {
				Matcher matcher = p.matcher(SysList[i].getName());
				if (matcher.find()) {
					lcdtype = (TextView) view.findViewById(R.id.lcd_type_text);
					lcdtype.setText(matcher.group(0).replace("pri_lcd_", ""));
					break;
				}
			}

		}

		return view;
	}

}
