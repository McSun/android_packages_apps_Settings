/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.cyanogenmod;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

//
// CPU Related Settings
//
public class Processor extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String FREQ_CUR_PREF = "pref_cpu_freq_cur";
    public static final String SCALE_CUR_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
    public static final String FREQINFO_CUR_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq";
    private static String FREQ_CUR_FILE = SCALE_CUR_FILE;
    public static final String GOV_PREF = "pref_cpu_gov";
    public static final String GOV_LIST_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";
    public static final String GOV_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
    public static final String FREQ_MIN_PREF = "pref_cpu_freq_min";
    public static final String FREQ_MAX_PREF = "pref_cpu_freq_max";
    public static final String FREQ_LIST_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
    public static final String FREQ_MAX_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
    public static final String FREQ_MIN_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";
    public static final String SOB_PREF = "pref_cpu_set_on_boot";

    /* Controlls for the screen state scalling scripts sleep state */
    public static final String GOV_PREF_SLEEP = "pref_cpu_gov_sleep";
    public static final String FREQ_MIN_PREF_SLEEP = "pref_cpu_freq_min_sleep";
    public static final String FREQ_MAX_PREF_SLEEP = "pref_cpu_freq_max_sleep";
    public static final String GOV_FILE_SLEEP = "/data/property/cpu.sleep.governor";
    public static final String FREQ_MAX_FILE_SLEEP = "/data/property/cpu.sleep.scaling.max";
    public static final String FREQ_MIN_FILE_SLEEP = "/data/property/cpu.sleep.scaling.min";
    public static final String ENABLE_FILE_SLEEP = "/data/property/cpu.sleep.switch";
    public static final String ENABLE_PREF_SLEEP = "pref_cpu_enable_sleep";

    private static final String TAG = "CPUSettings";

    private String mGovernorFormat;
    private String mMinFrequencyFormat;
    private String mMaxFrequencyFormat;

    private Preference mCurFrequencyPref;
    private ListPreference mGovernorPref;
    private ListPreference mMinFrequencyPref;
    private ListPreference mMaxFrequencyPref;

    /* Controlls for the screen state scalling scripts sleep state */
    private ListPreference mGovernorSleepPref;
    private ListPreference mMinFrequencySleepPref;
    private ListPreference mMaxFrequencySleepPref;
    private CheckBoxPreference mEnableSleepPref;

    private class CurCPUThread extends Thread {
        private boolean mInterrupt = false;

        public void interrupt() {
            mInterrupt = true;
        }

        @Override
        public void run() {
            try {
                while (!mInterrupt) {
                    sleep(500);
                    final String curFreq = Utils.fileReadOneLine(FREQ_CUR_FILE);
                    if (curFreq != null)
                        mCurCPUHandler.sendMessage(mCurCPUHandler.obtainMessage(0, curFreq));
                }
            } catch (InterruptedException e) {
            }
        }
    };

    private CurCPUThread mCurCPUThread = new CurCPUThread();

    private Handler mCurCPUHandler = new Handler() {
        public void handleMessage(Message msg) {
            mCurFrequencyPref.setSummary(toMHz((String) msg.obj));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGovernorFormat = getString(R.string.cpu_governors_summary);
        mMinFrequencyFormat = getString(R.string.cpu_min_freq_summary);
        mMaxFrequencyFormat = getString(R.string.cpu_max_freq_summary);

        String[] availableFrequencies = new String[0];
        String[] availableGovernors = new String[0];
        String[] frequencies;
        String availableGovernorsLine;
        String availableFrequenciesLine;
        String temp;

        addPreferencesFromResource(R.xml.processor_settings);

        PreferenceScreen prefScreen = getPreferenceScreen();

        mGovernorPref = (ListPreference) prefScreen.findPreference(GOV_PREF);
	mGovernorSleepPref = (ListPreference) prefScreen.findPreference(GOV_PREF_SLEEP);
        mCurFrequencyPref = (Preference) prefScreen.findPreference(FREQ_CUR_PREF);
        mMinFrequencyPref = (ListPreference) prefScreen.findPreference(FREQ_MIN_PREF);
        mMaxFrequencyPref = (ListPreference) prefScreen.findPreference(FREQ_MAX_PREF);
	mMinFrequencySleepPref = (ListPreference) prefScreen.findPreference(FREQ_MIN_PREF_SLEEP);
	mMaxFrequencySleepPref = (ListPreference) prefScreen.findPreference(FREQ_MAX_PREF_SLEEP);
	mEnableSleepPref = (CheckBoxPreference) prefScreen.findPreference(ENABLE_PREF_SLEEP);

	if (Utils.fileExists(ENABLE_FILE_SLEEP) == false || (temp = Utils.fileReadOneLine(ENABLE_FILE_SLEEP)) == null) {
		mEnableSleepPref.setEnabled(false);

	} else {
		mEnableSleepPref.setChecked("1".equals(temp));
	}

        /* Governor
        Some systems might not use governors */
        if (!Utils.fileExists(GOV_LIST_FILE) || !Utils.fileExists(GOV_FILE) || (temp = Utils.fileReadOneLine(GOV_FILE)) == null || (availableGovernorsLine = Utils.fileReadOneLine(GOV_LIST_FILE)) == null) {
            mGovernorPref.setEnabled(false);
	    mGovernorSleepPref.setEnabled(false);

        } else {
            availableGovernors = availableGovernorsLine.split(" ");

            mGovernorPref.setEntryValues(availableGovernors);
            mGovernorPref.setEntries(availableGovernors);
            mGovernorPref.setValue(temp);
            mGovernorPref.setSummary(String.format(mGovernorFormat, temp));
            mGovernorPref.setOnPreferenceChangeListener(this);

	    if (Utils.fileExists(GOV_FILE_SLEEP) == false || (temp = Utils.fileReadOneLine(GOV_FILE_SLEEP)) == null) {
		    mGovernorSleepPref.setEnabled(false);

	    } else {
		    mGovernorSleepPref.setEntryValues(availableGovernors);
		    mGovernorSleepPref.setEntries(availableGovernors);
		    mGovernorSleepPref.setValue(temp);
		    mGovernorSleepPref.setSummary(String.format(mGovernorFormat, temp));
		    mGovernorSleepPref.setOnPreferenceChangeListener(this);
	    }
        }

        // Disable the min/max list if we dont have a list file
        if (!Utils.fileExists(FREQ_LIST_FILE) || (availableFrequenciesLine = Utils.fileReadOneLine(FREQ_LIST_FILE)) == null) {
            mMinFrequencyPref.setEnabled(false);
            mMaxFrequencyPref.setEnabled(false);
            mMinFrequencySleepPref.setEnabled(false);
            mMaxFrequencySleepPref.setEnabled(false);

        } else {
            availableFrequencies = availableFrequenciesLine.split(" ");

            frequencies = new String[availableFrequencies.length];
            for (int i = 0; i < frequencies.length; i++) {
                frequencies[i] = toMHz(availableFrequencies[i]);
            }

            // Min frequency
            if (!Utils.fileExists(FREQ_MIN_FILE) || (temp = Utils.fileReadOneLine(FREQ_MIN_FILE)) == null) {
                mMinFrequencyPref.setEnabled(false);
		mMinFrequencySleepPref.setEnabled(false);

            } else {
                mMinFrequencyPref.setEntryValues(availableFrequencies);
                mMinFrequencyPref.setEntries(frequencies);
                mMinFrequencyPref.setValue(temp);
                mMinFrequencyPref.setSummary(String.format(mMinFrequencyFormat, toMHz(temp)));
                mMinFrequencyPref.setOnPreferenceChangeListener(this);

		if (Utils.fileExists(FREQ_MIN_FILE_SLEEP) == false || (temp = Utils.fileReadOneLine(FREQ_MIN_FILE_SLEEP)) == null) {
			mMinFrequencySleepPref.setEnabled(false);

		} else {
			mMinFrequencySleepPref.setEntryValues(availableFrequencies);
			mMinFrequencySleepPref.setEntries(frequencies);
			mMinFrequencySleepPref.setValue(temp);
			mMinFrequencySleepPref.setSummary(String.format(mMinFrequencyFormat, toMHz(temp)));
			mMinFrequencySleepPref.setOnPreferenceChangeListener(this);
		}
            }

            // Max frequency
            if (!Utils.fileExists(FREQ_MAX_FILE) || (temp = Utils.fileReadOneLine(FREQ_MAX_FILE)) == null) {
                mMaxFrequencyPref.setEnabled(false);
		mMaxFrequencySleepPref.setEnabled(false);

            } else {
                mMaxFrequencyPref.setEntryValues(availableFrequencies);
                mMaxFrequencyPref.setEntries(frequencies);
                mMaxFrequencyPref.setValue(temp);
                mMaxFrequencyPref.setSummary(String.format(mMaxFrequencyFormat, toMHz(temp)));
                mMaxFrequencyPref.setOnPreferenceChangeListener(this);

		if (Utils.fileExists(FREQ_MAX_FILE_SLEEP) == false || (temp = Utils.fileReadOneLine(FREQ_MAX_FILE_SLEEP)) == null) {
			mMaxFrequencySleepPref.setEnabled(false);

		} else {
			mMaxFrequencySleepPref.setEntryValues(availableFrequencies);
			mMaxFrequencySleepPref.setEntries(frequencies);
			mMaxFrequencySleepPref.setValue(temp);
			mMaxFrequencySleepPref.setSummary(String.format(mMaxFrequencyFormat, toMHz(temp)));
			mMaxFrequencySleepPref.setOnPreferenceChangeListener(this);
		}
            }
        }

        // Cur frequency
        if (!Utils.fileExists(FREQ_CUR_FILE)) {
            FREQ_CUR_FILE = FREQINFO_CUR_FILE;
        }

        if (!Utils.fileExists(FREQ_CUR_FILE) || (temp = Utils.fileReadOneLine(FREQ_CUR_FILE)) == null) {
            mCurFrequencyPref.setEnabled(false);

        } else {
            mCurFrequencyPref.setSummary(toMHz(temp));

            mCurCPUThread.start();
        }
    }

    @Override
    public void onResume() {
        String temp;

        super.onResume();

        if (Utils.fileExists(FREQ_MIN_FILE) && (temp = Utils.fileReadOneLine(FREQ_MIN_FILE)) != null) {
            mMinFrequencyPref.setValue(temp);
            mMinFrequencyPref.setSummary(String.format(mMinFrequencyFormat, toMHz(temp)));
        }

        if (Utils.fileExists(FREQ_MAX_FILE) && (temp = Utils.fileReadOneLine(FREQ_MAX_FILE)) != null) {
            mMaxFrequencyPref.setValue(temp);
            mMaxFrequencyPref.setSummary(String.format(mMaxFrequencyFormat, toMHz(temp)));
        }

	if (Utils.fileExists(FREQ_MIN_FILE_SLEEP) && (temp = Utils.fileReadOneLine(FREQ_MIN_FILE_SLEEP)) != null) {
		mMinFrequencySleepPref.setValue(temp);
		mMinFrequencySleepPref.setSummary(String.format(mMinFrequencyFormat, toMHz(temp)));
	}

	if (Utils.fileExists(FREQ_MAX_FILE_SLEEP) && (temp = Utils.fileReadOneLine(FREQ_MAX_FILE_SLEEP)) != null) {
        	mMaxFrequencySleepPref.setValue(temp);
        	mMaxFrequencySleepPref.setSummary(String.format(mMaxFrequencyFormat, toMHz(temp)));
	}

        if (Utils.fileExists(GOV_FILE) && (temp = Utils.fileReadOneLine(GOV_FILE)) != null) {
            mGovernorPref.setSummary(String.format(mGovernorFormat, temp));
        }

	if (Utils.fileExists(GOV_FILE_SLEEP) && (temp = Utils.fileReadOneLine(GOV_FILE_SLEEP)) != null) {
        	mGovernorSleepPref.setSummary(String.format(mGovernorFormat, temp));
	}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCurCPUThread.interrupt();
        try {
            mCurCPUThread.join();
        } catch (InterruptedException e) {
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
	if (preference == mEnableSleepPref) {
		return Utils.fileWriteOneLine(ENABLE_FILE_SLEEP, mEnableSleepPref.isChecked() ? "1" : "0") ? true : false;
	}

	return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String fname = "";

        if (newValue != null) {
            if (preference == mGovernorPref) {
                fname = GOV_FILE;
            } else if (preference == mGovernorSleepPref) {
                fname = GOV_FILE_SLEEP;
            } else if (preference == mMinFrequencyPref) {
                fname = FREQ_MIN_FILE;
            } else if (preference == mMaxFrequencyPref) {
                fname = FREQ_MAX_FILE;
            } else if (preference == mMinFrequencySleepPref) {
                fname = FREQ_MIN_FILE_SLEEP;
            } else if (preference == mMaxFrequencySleepPref) {
                fname = FREQ_MAX_FILE_SLEEP;
            }

            if (Utils.fileWriteOneLine(fname, (String) newValue)) {
                if (preference == mGovernorPref) {
                    mGovernorPref.setSummary(String.format(mGovernorFormat, (String) newValue));
                } else if (preference == mGovernorSleepPref) {
                    mGovernorSleepPref.setSummary(String.format(mGovernorFormat, (String) newValue));
                } else if (preference == mMinFrequencyPref) {
                    mMinFrequencyPref.setSummary(String.format(mMinFrequencyFormat,
                            toMHz((String) newValue)));
                } else if (preference == mMaxFrequencyPref) {
                    mMaxFrequencyPref.setSummary(String.format(mMaxFrequencyFormat,
                            toMHz((String) newValue)));
                } else if (preference == mMinFrequencySleepPref) {
                    mMinFrequencySleepPref.setSummary(String.format(mMinFrequencyFormat,
                            toMHz((String) newValue)));
                } else if (preference == mMaxFrequencySleepPref) {
                    mMaxFrequencySleepPref.setSummary(String.format(mMaxFrequencyFormat,
                            toMHz((String) newValue)));
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private String toMHz(String mhzString) {
        return new StringBuilder().append(Integer.valueOf(mhzString) / 1000).append(" MHz")
                .toString();
    }
}
