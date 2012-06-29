package com.android.settings.cyanogenmod;

import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class m2sdManagement extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
	
	private static final String M2SD_PATH_PROPS = SystemProperties.get("ro.m2sd.path.props", "/data/m2sd/property");

	private static final String M2SD_SWITCH = "m2sd_switch";
	private static final String M2SD_APPS_SWITCH = "m2sd_apps_switch";
	private static final String M2SD_DATA_SWITCH = "m2sd_data_switch";
	private static final String M2SD_DALVIK_SWITCH = "m2sd_dalvik_switch";
	private static final String M2SD_CACHE_SWITCH = "m2sd_cache_switch";
	private static final String M2SD_CACHE_DOWNLOAD_SWITCH = "m2sd_cache_download_switch";
	private static final String M2SD_SWAP_SWITCH = "m2sd_swap_switch";
	private static final String M2SD_FSCHK_SWITCH = "m2sd_fschk_switch";
	private static final String M2SD_NOATIME_SWITCH = "m2sd_noatime_switch";
	private static final String M2SD_JOURNAL_SWITCH = "m2sd_journal_switch";
	private static final String M2SD_READAHEAD_VALUE = "m2sd_readahead_value";
	private static final String M2SD_EXTFS_VALUE = "m2sd_extfs_value";
	private static final String M2SD_RMOUNT_SWITCH = "m2sd_rmount_switch";

	private static final String M2SD_SWITCH_PROP = "m2sd.switch";
	private static final String M2SD_APPS_SWITCH_PROP = "m2sd.apps.switch";
	private static final String M2SD_DATA_SWITCH_PROP = "m2sd.data.switch";
	private static final String M2SD_DALVIK_SWITCH_PROP = "m2sd.dalvik.switch";
	private static final String M2SD_CACHE_SWITCH_PROP = "m2sd.cache.switch";
	private static final String M2SD_CACHE_DOWNLOAD_SWITCH_PROP = "m2sd.cache.download.switch";
	private static final String M2SD_SWAP_SWITCH_PROP = "m2sd.swap.switch";
	private static final String M2SD_FSCHK_SWITCH_PROP = "m2sd.fschk.switch";
	private static final String M2SD_NOATIME_SWITCH_PROP = "m2sd.noatime.switch";
	private static final String M2SD_JOURNAL_SWITCH_PROP = "m2sd.journal.switch";
	private static final String M2SD_READAHEAD_VALUE_PROP = "m2sd.readahead.value";
	private static final String M2SD_EXTFS_VALUE_PROP = "m2sd.extfs.value";
	private static final String M2SD_RMOUNT_SWITCH_PROP = "m2sd.rmount.switch";

	private static final String M2SD_SWITCH_DEFAULT = "1";
	private static final String M2SD_APPS_SWITCH_DEFAULT = "1";
	private static final String M2SD_DATA_SWITCH_DEFAULT = "0";
	private static final String M2SD_DALVIK_SWITCH_DEFAULT = "0";
	private static final String M2SD_CACHE_SWITCH_DEFAULT = "-1";
	private static final String M2SD_CACHE_DOWNLOAD_SWITCH_DEFAULT = "0";
	private static final String M2SD_SWAP_SWITCH_DEFAULT = "0";
	private static final String M2SD_FSCHK_SWITCH_DEFAULT = "1";
	private static final String M2SD_NOATIME_SWITCH_DEFAULT = "0";
	private static final String M2SD_JOURNAL_SWITCH_DEFAULT = "0";
	private static final String M2SD_READAHEAD_VALUE_DEFAULT = "512";
	private static final String M2SD_EXTFS_VALUE_DEFAULT = "auto";
	private static final String M2SD_RMOUNT_SWITCH_DEFAULT = "0";

	private CheckBoxPreference mM2sdSwitch;
	private CheckBoxPreference mM2sdAppsSwitch;
	private CheckBoxPreference mM2sdDataSwitch;
	private CheckBoxPreference mM2sdDalvikSwitch;
	private ListPreference mM2sdCacheSwitch;
	private CheckBoxPreference mM2sdCacheDownloadSwitch;
	private CheckBoxPreference mM2sdSwapSwitch;
	private CheckBoxPreference mM2sdFschkSwitch;
	private CheckBoxPreference mM2sdNoatimeSwitch;
	private ListPreference mM2sdJournalSwitch;
	private ListPreference mM2sdReadaheadValue;
	private ListPreference mM2sdExtfsValue;
	private CheckBoxPreference mM2sdRmountSwitch;
	private String mPropsPath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getPreferenceManager() != null) {
			if(!M2SD_PATH_PROPS.endsWith("/")) {
				mPropsPath = M2SD_PATH_PROPS + "/";
			} else {
				mPropsPath = M2SD_PATH_PROPS;
			}
			
			addPreferencesFromResource(R.xml.m2sd_management_settings);

			PreferenceScreen prefSet = getPreferenceScreen();

			mM2sdSwitch = (CheckBoxPreference) prefSet.findPreference(M2SD_SWITCH);
			mM2sdAppsSwitch = (CheckBoxPreference) prefSet.findPreference(M2SD_APPS_SWITCH);
			mM2sdDataSwitch = (CheckBoxPreference) prefSet.findPreference(M2SD_DATA_SWITCH);
			mM2sdDalvikSwitch = (CheckBoxPreference) prefSet.findPreference(M2SD_DALVIK_SWITCH);
			mM2sdCacheSwitch = (ListPreference) prefSet.findPreference(M2SD_CACHE_SWITCH);
			mM2sdCacheDownloadSwitch = (CheckBoxPreference) prefSet.findPreference(M2SD_CACHE_DOWNLOAD_SWITCH);
			mM2sdSwapSwitch = (CheckBoxPreference) prefSet.findPreference(M2SD_SWAP_SWITCH);
			mM2sdFschkSwitch = (CheckBoxPreference) prefSet.findPreference(M2SD_FSCHK_SWITCH);
			mM2sdNoatimeSwitch = (CheckBoxPreference) prefSet.findPreference(M2SD_NOATIME_SWITCH);
			mM2sdJournalSwitch = (ListPreference) prefSet.findPreference(M2SD_JOURNAL_SWITCH);
			mM2sdReadaheadValue = (ListPreference) prefSet.findPreference(M2SD_READAHEAD_VALUE);
			mM2sdExtfsValue = (ListPreference) prefSet.findPreference(M2SD_EXTFS_VALUE);
			mM2sdRmountSwitch = (CheckBoxPreference) prefSet.findPreference(M2SD_RMOUNT_SWITCH);

			String lM2sdSwitch = Utils.fileExists(mPropsPath + M2SD_SWITCH_PROP) ? Utils.fileReadOneLine(mPropsPath + M2SD_SWITCH_PROP) : M2SD_SWITCH_DEFAULT;
			String lM2sdAppsSwitch = Utils.fileExists(mPropsPath + M2SD_APPS_SWITCH_PROP) ? Utils.fileReadOneLine(mPropsPath + M2SD_APPS_SWITCH_PROP) : M2SD_APPS_SWITCH_DEFAULT;
			String lM2sdDataSwitch = Utils.fileExists(mPropsPath + M2SD_DATA_SWITCH_PROP) ? Utils.fileReadOneLine(mPropsPath + M2SD_DATA_SWITCH_PROP) : M2SD_DATA_SWITCH_DEFAULT;
			String lM2sdDalvikSwitch = Utils.fileExists(mPropsPath + M2SD_DALVIK_SWITCH_PROP) ? Utils.fileReadOneLine(mPropsPath + M2SD_DALVIK_SWITCH_PROP) : M2SD_DALVIK_SWITCH_DEFAULT;
			String lM2sdCacheSwitch = Utils.fileExists(mPropsPath + M2SD_CACHE_SWITCH_PROP) ? Utils.fileReadOneLine(mPropsPath + M2SD_CACHE_SWITCH_PROP) : M2SD_CACHE_SWITCH_DEFAULT;
			String lM2sdCacheDownloadSwitch = Utils.fileExists(mPropsPath + M2SD_CACHE_DOWNLOAD_SWITCH_PROP) ? Utils.fileReadOneLine(mPropsPath + M2SD_CACHE_DOWNLOAD_SWITCH_PROP) : M2SD_CACHE_DOWNLOAD_SWITCH_DEFAULT;
			String lM2sdSwapSwitch = Utils.fileExists(mPropsPath + M2SD_SWAP_SWITCH_PROP) ? Utils.fileReadOneLine(mPropsPath + M2SD_SWAP_SWITCH_PROP) : M2SD_SWAP_SWITCH_DEFAULT;
			String lM2sdFschkSwitch = Utils.fileExists(mPropsPath + M2SD_FSCHK_SWITCH_PROP) ? Utils.fileReadOneLine(mPropsPath + M2SD_FSCHK_SWITCH_PROP) : M2SD_FSCHK_SWITCH_DEFAULT;
			String lM2sdNoatimeSwitch = Utils.fileExists(mPropsPath + M2SD_NOATIME_SWITCH_PROP) ? Utils.fileReadOneLine(mPropsPath + M2SD_NOATIME_SWITCH_PROP) : M2SD_NOATIME_SWITCH_DEFAULT;
			String lM2sdJournalSwitch = Utils.fileExists(mPropsPath + M2SD_JOURNAL_SWITCH_PROP) ? Utils.fileReadOneLine(mPropsPath + M2SD_JOURNAL_SWITCH_PROP) : M2SD_JOURNAL_SWITCH_DEFAULT;
			String lM2sdReadaheadValue = Utils.fileExists(mPropsPath + M2SD_READAHEAD_VALUE_PROP) ? Utils.fileReadOneLine(mPropsPath + M2SD_READAHEAD_VALUE_PROP) : M2SD_READAHEAD_VALUE_DEFAULT;
			String lM2sdExtfsValue = Utils.fileExists(mPropsPath + M2SD_EXTFS_VALUE_PROP) ? Utils.fileReadOneLine(mPropsPath + M2SD_EXTFS_VALUE_PROP) : M2SD_EXTFS_VALUE_DEFAULT;
			String lM2sdRmountSwitch = Utils.fileExists(mPropsPath + M2SD_RMOUNT_SWITCH_PROP) ? Utils.fileReadOneLine(mPropsPath + M2SD_RMOUNT_SWITCH_PROP) : M2SD_RMOUNT_SWITCH_DEFAULT;

			mM2sdSwitch.setChecked("1".equals(lM2sdSwitch));
			mM2sdAppsSwitch.setChecked("1".equals(lM2sdAppsSwitch));
			mM2sdDataSwitch.setChecked("1".equals(lM2sdDataSwitch));
			mM2sdDalvikSwitch.setChecked("1".equals(lM2sdDalvikSwitch));
			mM2sdCacheSwitch.setValue(lM2sdCacheSwitch);
			mM2sdCacheDownloadSwitch.setChecked("1".equals(lM2sdCacheDownloadSwitch));
			mM2sdSwapSwitch.setChecked("1".equals(lM2sdSwapSwitch));
			mM2sdFschkSwitch.setChecked("1".equals(lM2sdFschkSwitch));
			mM2sdNoatimeSwitch.setChecked("1".equals(lM2sdNoatimeSwitch));
			mM2sdJournalSwitch.setValue(lM2sdJournalSwitch);
			mM2sdReadaheadValue.setValue(lM2sdReadaheadValue);
			mM2sdExtfsValue.setValue(lM2sdExtfsValue);
			mM2sdRmountSwitch.setChecked("1".equals(lM2sdRmountSwitch));

			mM2sdCacheSwitch.setOnPreferenceChangeListener(this);
			mM2sdJournalSwitch.setOnPreferenceChangeListener(this);
			mM2sdReadaheadValue.setOnPreferenceChangeListener(this);
			mM2sdExtfsValue.setOnPreferenceChangeListener(this);

			// Remove SWAP pref is there is no SWAP support with current kernel
			if (Utils.fileExists("/proc/swaps") == false) {
				mM2sdSwapSwitch.setEnabled(false);
				Utils.fileWriteOneLine(mPropsPath + M2SD_SWAP_SWITCH_PROP, "0");
			}

			if ("0".equals(lM2sdCacheSwitch)) {
				mM2sdCacheDownloadSwitch.setEnabled(false);
			}
		}
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		if (preference == mM2sdSwitch) {
			return Utils.fileWriteOneLine(mPropsPath + M2SD_SWITCH_PROP, mM2sdSwitch.isChecked() ? "1" : "0") ? true : false;

		} else if (preference == mM2sdAppsSwitch) {
			return Utils.fileWriteOneLine(mPropsPath + M2SD_APPS_SWITCH_PROP, mM2sdAppsSwitch.isChecked() ? "1" : "0") ? true : false;

		} else if (preference == mM2sdDataSwitch) {
			return Utils.fileWriteOneLine(mPropsPath + M2SD_DATA_SWITCH_PROP, mM2sdDataSwitch.isChecked() ? "1" : "0") ? true : false;

		} else if (preference == mM2sdDalvikSwitch) {
			return Utils.fileWriteOneLine(mPropsPath + M2SD_DALVIK_SWITCH_PROP, mM2sdDalvikSwitch.isChecked() ? "1" : "0") ? true : false;

		} else if (preference == mM2sdCacheDownloadSwitch) {
			return Utils.fileWriteOneLine(mPropsPath + M2SD_CACHE_DOWNLOAD_SWITCH_PROP, mM2sdCacheDownloadSwitch.isChecked() ? "1" : "0") ? true : false;

		} else if (preference == mM2sdSwapSwitch) {
			return Utils.fileWriteOneLine(mPropsPath + M2SD_SWAP_SWITCH_PROP, mM2sdSwapSwitch.isChecked() ? "1" : "0") ? true : false;

		} else if (preference == mM2sdFschkSwitch) {
			return Utils.fileWriteOneLine(mPropsPath + M2SD_FSCHK_SWITCH_PROP, mM2sdFschkSwitch.isChecked() ? "1" : "0") ? true : false;

		} else if (preference == mM2sdNoatimeSwitch) {
			return Utils.fileWriteOneLine(mPropsPath + M2SD_NOATIME_SWITCH_PROP, mM2sdNoatimeSwitch.isChecked() ? "1" : "0") ? true : false;

		} else if (preference == mM2sdRmountSwitch) {
			return Utils.fileWriteOneLine(mPropsPath + M2SD_RMOUNT_SWITCH_PROP, mM2sdRmountSwitch.isChecked() ? "1" : "0") ? true : false;
		} 

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (newValue != null) {
			if (preference == mM2sdCacheSwitch) {
				String sValue = (String) newValue;

				if ("0".equals(sValue)) {
					mM2sdCacheDownloadSwitch.setEnabled(false);

				} else {
					mM2sdCacheDownloadSwitch.setEnabled(true);
				}

				return Utils.fileWriteOneLine(mPropsPath + M2SD_CACHE_SWITCH_PROP, sValue) ? true : false;

			} else if (preference == mM2sdJournalSwitch) {
				return Utils.fileWriteOneLine(mPropsPath + M2SD_JOURNAL_SWITCH_PROP, (String) newValue) ? true : false;

			} else if (preference == mM2sdReadaheadValue) {
				return Utils.fileWriteOneLine(mPropsPath + M2SD_READAHEAD_VALUE_PROP, (String) newValue) ? true : false;

			} else if (preference == mM2sdExtfsValue) {
				return Utils.fileWriteOneLine(mPropsPath + M2SD_EXTFS_VALUE_PROP, (String) newValue) ? true : false;
			}
		}

		return false;
	}
}
