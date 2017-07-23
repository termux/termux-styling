package com.termux.styling;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
                final AlertDialog dialog = new AlertDialog.Builder(TermuxStyleActivity.this).setAdapter(colorAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        copyFile(colorAdapter.getItem(which), true);
                    }
                }).create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        ListView lv = dialog.getListView();
                        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                                showLicense(colorAdapter.getItem(position), true);
                                return true;
                            }
                        });
                    }
                });
                dialog.show();
            }
        });

        final ArrayAdapter<Selectable> fontAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        fontSpinner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog = new AlertDialog.Builder(TermuxStyleActivity.this).setAdapter(fontAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        copyFile(fontAdapter.getItem(which), false);
                    }
                }).create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        ListView lv = dialog.getListView();
                        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                                showLicense(fontAdapter.getItem(position), false);
                                return true;
                            }
                        });
                    }
                });
                dialog.show();
            }
        });

        List<Selectable> colorList = new ArrayList<>();
        List<Selectable> fontList = new ArrayList<>();
        for (String assetType : new String[]{"colors", "fonts"}) {
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

    void showLicense(Selectable mCurrentSelectable, boolean colors) {
        try {
            final String assetsFolder = colors ? "colors" : "fonts";
            String fileName = mCurrentSelectable.fileName;
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex != -1) fileName = fileName.substring(0, dotIndex);
            fileName += ".txt";

            try (InputStream in = getAssets().open(assetsFolder + "/" + fileName)) {
                byte[] buffer = new byte[in.available()];
                in.read(buffer);
                final SpannableString license = new SpannableString(new String(buffer));
                Linkify.addLinks(license, Linkify.ALL);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(mCurrentSelectable.displayName)
                        .setMessage(license)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
            }
        } catch (IOException e) {
            // Ignore.
        }
    }

    void copyFile(Selectable mCurrentSelectable, boolean colors) {
        final String outputFile = colors ? "colors.properties" : "font.ttf";
        try {
            final String assetsFolder = colors ? "colors" : "fonts";

            Context context = createPackageContext("com.termux", Context.CONTEXT_IGNORE_SECURITY);
            File homeDir = new File(context.getFilesDir(), "home");
            File termuxDir = new File(homeDir, ".termux");
            File destinationFile = new File(termuxDir, outputFile);
            if (!(termuxDir.isDirectory() || termuxDir.mkdirs()))
                throw new RuntimeException("Cannot create termux dir=" + termuxDir.getAbsolutePath());

            // Fix for if the user has messed up with chmod:
            destinationFile.setWritable(true);
            destinationFile.getParentFile().setWritable(true);
            destinationFile.getParentFile().setExecutable(true);

            boolean defaultChoice = mCurrentSelectable.fileName.equals(DEFAULT_FILENAME);
            // Write to existing file to keep symlink if this is used.
            try (FileOutputStream out = new FileOutputStream(destinationFile)) {
                if (defaultChoice) {
                    if (colors) {
                        byte[] comment = "# Using default color theme.".getBytes(StandardCharsets.UTF_8);
                        out.write(comment);
                    } else {
                        // Just leave an empty font file as a marker.
                    }
                } else {
                    try (InputStream in = getAssets().open(assetsFolder + "/" + mCurrentSelectable.fileName)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = in.read(buffer)) > 0)
                            out.write(buffer, 0, len);
                    }
                }
            }

            // Note: Must match constant in Term#onCreate():
            final String ACTION_RELOAD = "com.termux.app.reload_style";
            Intent executeIntent = new Intent(ACTION_RELOAD);
            executeIntent.putExtra(ACTION_RELOAD, colors ? "colors" : "font");
            sendBroadcast(executeIntent);
        } catch (Exception e) {
            Log.w("termux", "Failed to write " + outputFile, e);
            String message = getResources().getString(R.string.writing_failed) + e.getMessage();
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
