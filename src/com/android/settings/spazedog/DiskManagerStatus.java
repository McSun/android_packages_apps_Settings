package com.android.settings.spazedog;

import android.util.Log;
import android.os.Bundle;
import android.os.StatFs;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class DiskManagerStatus extends SettingsPreferenceFragment {
        private static final String TAG = "DiskManager";

        private Element ELEMENTS[] = {
                new Element("move_apps", "load"),
                new Element("move_system_apps", "load"),
                new Element("move_dalvik", "load"),
                new Element("move_system_dalvik", "load"),
                new Element("move_data", "load"),
                new Element("enable_cache", "load"),
                new Element("enable_reversed_mount", "load"),
                new Element("enable_swap", "load"),
                new Element("run_sdext_fschk", "execute"),
                new Element("set_sdext_fstype", "set"),
                new Element("set_sdcard_readahead", "set"),
                new Element("enable_sdext_journal", "enable")
        };

        private Preference DISK_MANAGER;
        private Preference INTERNAL_STORAGE;
        private Preference EXTERNAL_STORAGE;
        private Preference CACHE_STORAGE;

        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                if (getPreferenceManager() != null) {
                        addPreferencesFromResource(R.xml.diskmanager_status);

                        PreferenceScreen prefscreen = getPreferenceScreen();

                        String lStatusValue;
                        String lAttentionValue; 
                        String lConfigValue;
                        String lPath;
                        String lStatusMsg;
                        String lStatusFormat;
                        String runCount = Utils.fileExists( (lPath = String.format(DiskManagerUtils.PROPS_STATUS_LOCATION, "run_count")) ) ? Utils.fileReadOneLine(lPath) : "0";
                        boolean isActive = !("0".equals(runCount)) ? true : false;
                        boolean lAttention;

                        DISK_MANAGER = (Preference) prefscreen.findPreference(String.format(DiskManagerUtils.RESOURCE_STATUS_KEY, "enable_disk_manager"));
                        if (DISK_MANAGER != null) {
                                lStatusValue = Utils.fileExists( (lPath = String.format(DiskManagerUtils.PROPS_STATUS_LOCATION, "enable_disk_manager")) ) ? Utils.fileReadOneLine(lPath) : "0";
                                lConfigValue = Utils.fileExists( (lPath = String.format(DiskManagerUtils.PROPS_CONFIG_LOCATION, "enable_disk_manager")) ) ? Utils.fileReadOneLine(lPath) : "1";
                                String lLogStatus = Utils.fileExists( (lPath = String.format(DiskManagerUtils.PROPS_STATUS_LOCATION, "log")) ) ? Utils.fileReadOneLine(lPath) : "0";
                                
                                lAttention = !lLogStatus.equals("0") ? true :
                                                !runCount.equals("3") ? true :
                                                !lStatusValue.equals(lConfigValue) ? true : false;

                                lStatusMsg = getString(!lAttention ? R.string.dm_status_msg_ok : (lLogStatus.equals("1") ? R.string.dm_status_msg_warning : R.string.dm_status_msg_error));
                                DISK_MANAGER.setSummary( String.format(getString(isActive ? R.string.dm_status_loaded : R.string.dm_status_unloaded), lStatusMsg) );

                        } else {
                                Log.e(TAG, "The Preference for enable_disk_manager is NULL and not an object!");
                        }

                        for (int i=0; i < ELEMENTS.length; i++) {
                                ELEMENTS[i].preference = (Preference) prefscreen.findPreference(String.format(DiskManagerUtils.RESOURCE_STATUS_KEY, ELEMENTS[i].name));

                                if (ELEMENTS[i].preference != null) {
                                        if (isActive) {
                                                lStatusValue = Utils.fileExists( (lPath = String.format(DiskManagerUtils.PROPS_STATUS_LOCATION, ELEMENTS[i].name)) ) ? Utils.fileReadOneLine(lPath) : null;
                                                lAttentionValue = Utils.fileExists( (lPath = String.format(DiskManagerUtils.PROPS_ATTENTION_LOCATION, ELEMENTS[i].name)) ) ? Utils.fileReadOneLine(lPath) : null;
                                                lConfigValue = Utils.fileExists( (lPath = String.format(DiskManagerUtils.PROPS_CONFIG_LOCATION, ELEMENTS[i].name)) ) ? Utils.fileReadOneLine(lPath) : null;

                                                lAttention = lAttentionValue != null && lAttentionValue.equals("1") ? true : 
                                                                lStatusValue == null ? false : 
                                                                ELEMENTS[i].action != "load" && lStatusValue == null ? false : 
                                                                lConfigValue != null && (lStatusValue.equals(lConfigValue) || lConfigValue.equals("2")) ? false : true;

                                                lStatusMsg = getString( lAttention ? R.string.dm_status_msg_attention : R.string.dm_status_msg_ok );

                                                if (ELEMENTS[i].action == "enable") {
                                                        lStatusFormat = String.format(getString(lStatusValue == null ? R.string.dm_status_unknown : (lStatusValue.equals("1") ? R.string.dm_status_enabled : R.string.dm_status_disabled)), lStatusMsg);

                                                } else if (ELEMENTS[i].action == "set") {
                                                        lStatusFormat = String.format(getString(R.string.dm_status_value), lStatusMsg, lStatusValue != null ? lStatusValue : getString(R.string.dm_status_unknown_fragment));

                                                } else if (ELEMENTS[i].action == "execute") {
                                                        lStatusFormat = lStatusMsg;

                                                } else {
                                                        lStatusFormat = String.format(getString(lStatusValue != null && lStatusValue.equals("1") ? R.string.dm_status_loaded : R.string.dm_status_unloaded), lStatusMsg);
                                                }

                                                ELEMENTS[i].preference.setSummary(lStatusFormat);

                                        } else {
                                                prefscreen.removePreference(ELEMENTS[i].preference);
                                        }

                                } else {
                                        Log.e(TAG, "The Preference for " + ELEMENTS[i].name + " is NULL and not an object!");
                                }
                        }

                        INTERNAL_STORAGE = (Preference) prefscreen.findPreference("dm_storage_internal_s");
                        EXTERNAL_STORAGE = (Preference) prefscreen.findPreference("dm_storage_external_s");
                        CACHE_STORAGE = (Preference) prefscreen.findPreference("dm_storage_cache_s");

                        if (INTERNAL_STORAGE != null && EXTERNAL_STORAGE != null) {
                                if (isActive) {
                                        boolean isReversed = "1".equals(Utils.fileExists( (lPath = String.format(DiskManagerUtils.PROPS_STATUS_LOCATION, "enable_reversed_mount"))) ? Utils.fileReadOneLine(lPath) : "0") ? true : false;
 
                                        INTERNAL_STORAGE.setTitle( String.format(getString(R.string.dm_storage_internal_st), isReversed ? "/sd-ext" : "/data") );
                                        INTERNAL_STORAGE.setSummary( String.format(getString(R.string.dm_storage_usage_ss), DiskManagerUtils.getMB(DiskManagerUtils.diskUsage(isReversed ? "/sd-ext" : "/data")), DiskManagerUtils.getMB(DiskManagerUtils.diskTotal(isReversed ? "/sd-ext" : "/data"))) );

                                        EXTERNAL_STORAGE.setTitle( String.format(getString(R.string.dm_storage_external_st), isReversed ? "/data" : "/sd-ext") );
                                        EXTERNAL_STORAGE.setSummary( String.format(getString(R.string.dm_storage_usage_ss), DiskManagerUtils.getMB(DiskManagerUtils.diskUsage(isReversed ? "/data" : "/sd-ext")), DiskManagerUtils.getMB(DiskManagerUtils.diskTotal(isReversed ? "/data" : "/sd-ext"))) );

                                        String cacheLocation = Utils.fileExists( (lPath = String.format(DiskManagerUtils.PROPS_LOCATION, "cache.destination"))) ? Utils.fileReadOneLine(lPath) : "/cache";

                                        CACHE_STORAGE.setTitle( String.format(getString(R.string.dm_storage_cache_st), cacheLocation) );
                                        CACHE_STORAGE.setSummary( String.format(getString(R.string.dm_storage_usage_ss), DiskManagerUtils.getMB(DiskManagerUtils.diskUsage("/cache")), DiskManagerUtils.getMB(DiskManagerUtils.diskTotal("/cache"))) );

                                } else {
                                        prefscreen.removePreference(INTERNAL_STORAGE);
                                        prefscreen.removePreference(EXTERNAL_STORAGE);
                                        prefscreen.removePreference(CACHE_STORAGE);
                                }

                        } else {
                                Log.e(TAG, "The Preferences for the storage overview is NULL and not objects!");
                        }
                }
        }

        public class Element {
                public String name;
                public String action;

                public Preference preference;

                Element(String pName, String pAction) {
                        name = pName;
                        action = pAction;
                }
        }
}
