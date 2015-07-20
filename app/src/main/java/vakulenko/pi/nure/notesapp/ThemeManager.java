package vakulenko.pi.nure.notesapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class ThemeManager {

    private static final String TAG = ThemeManager.class.getSimpleName();

    private static final String PREF_THEME = "theme_preference";
    private static final String PREF_FONT = "font_preference";

    private static final Map<String, Integer> availableThemes;
    private static final int DEFAULT_THEME = R.style.Theme_Medium;

    static {
        availableThemes = new HashMap<String, Integer>();
        availableThemes.put("Default", R.style.AppTheme);
        availableThemes.put("Theme_Small", R.style.Theme_Small);
        availableThemes.put("Theme_Medium", R.style.Theme_Medium);
        availableThemes.put("Theme_Large", R.style.Theme_Large);
        availableThemes.put("Theme_Light_Small", R.style.Theme_Light_Small);
        availableThemes.put("Theme_Light_Medium", R.style.Theme_Light_Medium);
        availableThemes.put("Theme_Light_Large", R.style.Theme_Light_Large);
    }

    public static void setTheme(Context context) {
        context.setTheme(getThemeFromPreferences(context));
    }

    protected static int getThemeFromPreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String themePref = preferences.getString(PREF_THEME, "Theme");
        String fontPref = preferences.getString(PREF_FONT, "Medium");
        String theme = themePref + "_" + fontPref;

        Log.d(TAG, "selected theme - " + theme);

        if (availableThemes.containsKey(theme)) {
            return availableThemes.get(theme);
        }

        return DEFAULT_THEME;
    }

    public static String getCurrentThemeName(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_THEME, "Theme");
    }

}