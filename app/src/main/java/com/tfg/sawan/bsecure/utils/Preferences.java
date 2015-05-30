package com.tfg.sawan.bsecure.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * File name:	com.tfg.sawan.bsecure.utils
 * Version:		1.0
 * Date:		28/05/2015 19:12
 * Author:		Sawan J. Kapai Harpalani
 * Copyright:	Copyright 200X Sawan J. Kapai Harpalani
 * <p/>
 * This file is part of Bsecure.
 * <p/>
 * Bsecure is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software
 * Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p/>
 * Bsecure is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 * <p/>
 * You should have received a copy of the GNU General
 * Public License along with Bsecure. If not, see
 * http://www.gnu.org/licenses/.
 */
public class Preferences {
    /**
     *  The Constant PREFERENCES
     */
    protected final static String PREFERENCES = "BsecurePreferences";

    /**
     *  The Constant Message Not Found
     */
    public final static String NOT_FOUND_MESSAGE = "Nothing found";

    /**
     * @param activity
     * @param key
     * @param value
     */
    public static void savePreferences(Activity activity, String key, String value){
        SharedPreferences sp = activity.getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * @param activity
     * @param key
     * @param defaultValue
     * @return String
     */
    public static String readPreferences(Activity activity, String key, String defaultValue){
        SharedPreferences sp = activity.getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    /**
     * @param activity
     */
    public static void removeAllPreferences(Activity activity) {
        SharedPreferences sp = activity.getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
}
