package com.termux.styling;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.AtomicFile;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class TermuxStyleActivity extends Activity {

	private static final String DEFAULT_FILENAME = "Default";

	static class Selectable {
		final String displayName;
		final String fileName;

		public Selectable(final String fileName) {
			String name = fileName.replace('-', ' ');
			int dotIndex = name.lastIndexOf('.');
			if (dotIndex != -1) name = name.substring(0, dotIndex);

			this.displayName = capitalize(name);
			this.fileName = fileName;
		}

		@Override
		public String toString() {
			return displayName;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Avoid dim behind:
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		setContentView(R.layout.layout);

		final Button colorSpinner = (Button) findViewById(R.id.color_spinner);
		final Button fontSpinner = (Button) findViewById(R.id.font_spinner);

		final ArrayAdapter<Selectable> colorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);

		colorSpinner.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(TermuxStyleActivity.this).setAdapter(colorAdapter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						copyFile(colorAdapter.getItem(which), true);
					}
				}).create().show();
			}
		});

		final ArrayAdapter<Selectable> fontAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
		fontSpinner.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(TermuxStyleActivity.this).setAdapter(fontAdapter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						copyFile(fontAdapter.getItem(which), false);
					}
				}).create().show();
			}
		});

		List<Selectable> colorList = new ArrayList<>();
		List<Selectable> fontList = new ArrayList<>();
		for (String assetType : new String[] { "colors", "fonts" }) {
			boolean isColors = assetType.equals("colors");
			String assetsFileExtension = isColors ? ".properties" : ".ttf";
			List<Selectable> currentList = isColors ? colorList : fontList;

			currentList.add(new Selectable(isColors ? DEFAULT_FILENAME : DEFAULT_FILENAME));

			try {
				for (String f : getAssets().list(assetType)) {
					if (f.endsWith(assetsFileExtension)) currentList.add(new Selectable(f));
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		Pair<List<Selectable>, List<Selectable>> result = Pair.create(colorList, fontList);

		colorAdapter.addAll(result.first);
		fontAdapter.addAll(result.second);
	}

	void copyFile(Selectable mCurrentSelectable, boolean colors) {
		try {
			final String outputFile = colors ? "colors.properties" : "font.ttf";
			final String assetsFolder = colors ? "colors" : "fonts";

			Context context = createPackageContext("com.termux", Context.CONTEXT_IGNORE_SECURITY);
			File homeDir = new File(context.getFilesDir(), "home");
			File termuxDir = new File(homeDir, ".termux");
			File destinationFile = new File(termuxDir, outputFile);
			if (!(termuxDir.isDirectory() || termuxDir.mkdirs())) throw new RuntimeException("Cannot create termux dir=" + termuxDir.getAbsolutePath());

			// Fix for if the user has messed up with chmod:
			destinationFile.setWritable(true);

			if (mCurrentSelectable.fileName.equals(DEFAULT_FILENAME)) {
				destinationFile.delete();
			} else {
				AtomicFile file = new AtomicFile(destinationFile);
				FileOutputStream out = file.startWrite();
				try {
					try (InputStream in = getAssets().open(assetsFolder + "/" + mCurrentSelectable.fileName)) {
						byte[] buffer = new byte[4096];
						int len;
						while ((len = in.read(buffer)) > 0)
							out.write(buffer, 0, len);
					}
					file.finishWrite(out);
				} catch (RuntimeException e) {
					file.failWrite(out);
					throw e;
				}
			}

			// Note: Must match constant in Term#onCreate():
			final String ACTION_RELOAD = "com.termux.app.reload_style";
			Intent executeIntent = new Intent(ACTION_RELOAD);
			executeIntent.putExtra(ACTION_RELOAD, colors ? "colors" : "font");
			sendBroadcast(executeIntent);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private static String capitalize(String str) {
		boolean lastWhitespace = true;
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (Character.isLetter(chars[i])) {
				if (lastWhitespace) chars[i] = Character.toUpperCase(chars[i]);
				lastWhitespace = false;
			} else {
				lastWhitespace = Character.isWhitespace(chars[i]);
			}
		}
		return new String(chars);
	}
}
