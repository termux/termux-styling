package com.termux.styling;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.termux.styling.TermuxStyleActivity.DEFAULT_BACKGROUND;
import static com.termux.styling.TermuxStyleActivity.DEFAULT_FOREGROUND;
import static com.termux.styling.TermuxStyleActivity.KEY_BACKGROUND_COLOR;
import static com.termux.styling.TermuxStyleActivity.KEY_FOREGROUND_COLOR;

/**
 * Helper for extracting foreground and background colors from .properties files
 *
 * @author elFua
 */

public class ColorThemeParser {

    private static final Pattern REGEX_FOREGROUND_COLOR = Pattern.compile("foreground\\s*(?:=|:)\\s*(\\S+)\n", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final Pattern REGEX_BACKGROUND_COLOR = Pattern.compile("background\\s*(?:=|:)\\s*(\\S+)\n", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    protected static Map<String, Integer> parseColorProproperties(Context applicationContext, String fileName) {

        AssetManager assetManager = applicationContext.getAssets();
        String colorThemeData = null;

        try {
            InputStream assetInputStream = assetManager.open("colors/" + fileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(assetInputStream, "UTF-8"));

            StringBuilder buf = new StringBuilder();
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str).append("\n");
            }
            in.close();
            colorThemeData = buf.toString();
        } catch (IOException e) {
            Log.e("termux", "Error while reading file: " + fileName);
        }

        Map<String, Integer> result = new HashMap<>();

        int foreground = extractRegexToColor(colorThemeData, REGEX_FOREGROUND_COLOR, DEFAULT_FOREGROUND);
        int background = extractRegexToColor(colorThemeData, REGEX_BACKGROUND_COLOR, DEFAULT_BACKGROUND);

        result.put(KEY_FOREGROUND_COLOR, foreground);
        result.put(KEY_BACKGROUND_COLOR, background);

        return result;
    }

    private static int extractRegexToColor(String data, Pattern pattern, int defaultIfNotFound) {
        int color = defaultIfNotFound;

        if (data != null) {
            Matcher matcher = pattern.matcher(data);

            if (matcher.find()) {
                String colorHexCode = matcher.group(1);

                try {
                    color = Color.parseColor(colorHexCode);
                } catch (IllegalArgumentException e) {
                    Log.e("termux", "Error parsing color code: " + colorHexCode);
                }
            }
        }

        return color;
    }
}
