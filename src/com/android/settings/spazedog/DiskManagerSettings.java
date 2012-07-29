package com.android.settings.spazedog;

import android.util.Log;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.android.settings.spazedog.DiskManagerUtils;

public class DiskManagerSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
        private static final String TAG = "DiskManager";

        private Element ELEMENTS[] = {
                new Element("move_apps", "CheckBoxPreference", new Integer(1), true, true),
                new Element("move_system_apps", "CheckBoxPreference", new Integer(0), true, true),
                new Element("move_dalvik", "CheckBoxPreference", new Integer(1), true, true),
                new Element("move_system_dalvik", "CheckBoxPreference", new Integer(0), true, true),
                new Element("move_data", "CheckBoxPreference", new Integer(1), true, true),
                new Element("enable_disk_manager", "CheckBoxPreference", new Integer(1), false, false),
                new Element("enable_reversed_mount", "CheckBoxPreference", new Integer(0), false, true),
                new Element("enable_swap", "CheckBoxPreference", new Integer(1), false, false),
                new Element("run_sdext_fschk", "CheckBoxPreference", new Integer(1), false, true),
                new Element("enable_cache", "ListPreference", new Integer(1), false, false),
                new Element("enable_sdext_journal", "ListPreference", new Integer(0), false, true),
                new Element("set_sdext_fstype", "ListPreference", new String("ext4"), false, true),
                new Element("set_sdcard_readahead", "ListPreference", new Integer(512), false, false)
        };

        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                if (getPreferenceManager() != null) {
                        addPreferencesFromResource(R.xml.diskmanager_settings);

                        for (int i=0; i < ELEMENTS.length; i++) {
                                ELEMENTS[i].attachPreference();

                                if (!ELEMENTS[i].isNull()) {
                                        if (!Utils.fileExists(DiskManagerUtils.SDCARD_LOCATION + "p2") && ELEMENTS[i].isDepended) {
                                                ELEMENTS[i].disable();

                                        } else {
                                                if (ELEMENTS[i].type == "CheckBoxPreference") {
                                                        ELEMENTS[i].checkBoxElement.setChecked("1".equals( Utils.fileExists(ELEMENTS[i].resource.getFilePath()) ? Utils.fileReadOneLine(ELEMENTS[i].resource.getFilePath()) : ELEMENTS[i].defaultValue.toString() )); 

                                                        if (ELEMENTS[i].name == "enable_swap" && (!Utils.fileExists("/proc/swaps") || !Utils.fileExists(DiskManagerUtils.SDCARD_LOCATION + "p3"))) {
                                                                // Do not alter whatever settings a user has set if proc is supported but the sdcard is just not available for some reason
                                                                if (ELEMENTS[i].checkBoxElement.isChecked() && !Utils.fileExists("/proc/swaps")) {
                                                                        Utils.fileWriteOneLine(ELEMENTS[i].resource.getFilePath(), "0");
                                                                }

                                                                ELEMENTS[i].disable();

                                                        } else if (ELEMENTS[i].name == "enable_reversed_mount") {
                                                                reverseCheckBoxes(ELEMENTS[i].checkBoxElement.isChecked(), false);
                                                        }

                                                } else {
                                                        ELEMENTS[i].listElement.setValue( Utils.fileExists(ELEMENTS[i].resource.getFilePath()) ? Utils.fileReadOneLine(ELEMENTS[i].resource.getFilePath()) : ELEMENTS[i].defaultValue.toString() );
                                                        ELEMENTS[i].listElement.setOnPreferenceChangeListener(this);
                                                }
                                        }

                                }else {
                                        Log.e(TAG, "The Preference Element for " + ELEMENTS[i].name + " is NULL and not an object!");
                                }
                        }
                }
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
                for (int i=0; i < ELEMENTS.length; i++) {
                        if ( ELEMENTS[i].checkBoxElement != null && ELEMENTS[i].checkBoxElement == preference ) {
                                if (ELEMENTS[i].name == "enable_reversed_mount") {
                                        boolean tStatus = Utils.fileWriteOneLine(ELEMENTS[i].resource.getFilePath(), ELEMENTS[i].checkBoxElement.isChecked() ? "1" : "0");

                                        if (tStatus) {
                                                reverseCheckBoxes(ELEMENTS[i].checkBoxElement.isChecked(), true);
                                        }

                                        return tStatus;

                                } else {
                                        return Utils.fileWriteOneLine(ELEMENTS[i].resource.getFilePath(), ELEMENTS[i].checkBoxElement.isChecked() ? "1" : "0");
                                }
                        }
                }

                return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue != null) {
                        for (int i=0; i < ELEMENTS.length; i++) {
                                if ( ELEMENTS[i].listElement != null && ELEMENTS[i].listElement == preference ) {
                                        return Utils.fileWriteOneLine(ELEMENTS[i].resource.getFilePath(), (String) newValue) ? true : false;
                                }
                        }
                }

                return false;
        }

        private void reverseCheckBoxes(boolean pStatus, boolean pReverseCheck) {
                String lSummary = getString( pStatus ? R.string.dm_move_internal_cs : R.string.dm_move_external_cs );

                for (int i=0; i < ELEMENTS.length; i++) {
                        if (ELEMENTS[i].doReverse) {
                                ELEMENTS[i].summary(String.format(lSummary, ELEMENTS[i].resource.getSummary()));

                                if (pReverseCheck) {
                                        if (Utils.fileWriteOneLine(ELEMENTS[i].resource.getFilePath(), ELEMENTS[i].checkBoxElement.isChecked() ? "0" : "1")) {
                                                ELEMENTS[i].checkBoxElement.setChecked( ELEMENTS[i].checkBoxElement.isChecked() ? false : true );
                                        }
                                }
                        }
                }
        }

        private class Element {
                public String name;
                public String type;
                public Object defaultValue;
                public boolean isDepended;
                public boolean doReverse;
                public CheckBoxPreference checkBoxElement;
                public ListPreference listElement;
                public Resource resource;

                private class Resource {
                        private String filePath;
                        private String resourceKey;
                        private String resourceSummary;

                        public String getFilePath() {
                               return filePath == null ? (filePath = String.format(DiskManagerUtils.CONFIG_LOCATION, name)) : filePath;
                        }

                        public String getKey() {
                                return resourceKey == null ? (resourceKey = String.format(DiskManagerUtils.RESOURCE_CONFIG_KEY, name)) : resourceKey;
                        }

                        public String getSummary() {
                                return resourceSummary == null ? (resourceSummary = (String) getString(getResources().getIdentifier(String.format(DiskManagerUtils.RESOURCE_CONFIG_SUMMARY, name), "string", "com.android.settings"))) : resourceSummary;
                        }
                }

                Element(String pName, String pType, Object pDefaultValue, boolean pDoReversed, boolean pIsDepended) {
                        name = pName;
                        type = pType; 
                        defaultValue = pDefaultValue;
                        isDepended = pIsDepended;
                        doReverse = pDoReversed;

                        resource = new Resource();
                }

                public void attachPreference() {
                        if (type == "CheckBoxPreference") {
                                checkBoxElement = (CheckBoxPreference) getPreferenceScreen().findPreference(resource.getKey());

                        } else { 
                                listElement = (ListPreference) getPreferenceScreen().findPreference(resource.getKey());
                        }
                }

                public boolean isNull() {
                        if (type == "CheckBoxPreference") { 
                                return checkBoxElement == null ? true : false;

                        } else {
                                return listElement == null ? true : false;
                        }
                }

                public void disable() {
                        if (type == "CheckBoxPreference") {
                                checkBoxElement.setEnabled(false);

                        } else {
                                listElement.setEnabled(false);
                        }
                }

                public void summary(String pSummary) {
                        if (type == "CheckBoxPreference") {
                                checkBoxElement.setSummary(pSummary);

                        } else {
                                listElement.setSummary(pSummary);
                        }
                }
        }
}
