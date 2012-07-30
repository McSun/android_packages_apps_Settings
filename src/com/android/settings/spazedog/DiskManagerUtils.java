package com.android.settings.spazedog;

import android.os.StatFs;
import android.os.SystemProperties;

public class DiskManagerUtils {
        private static final String SIZE_PRIFIX[] = {"b","Kb","Mb","Gb"};

        public static final String CONFIG_LOCATION = "/data/property/disk.manager.%s";
        public static final String PROPS_LOCATION = "/props/%s";
        public static final String PROPS_STATUS_LOCATION = "/props/status.%s";
        public static final String PROPS_CONFIG_LOCATION = "/props/config.%s";
        public static final String PROPS_ATTENTION_LOCATION = "/props/attention.%s";
        public static final String LOG_LOCATION = "/%s/diskManager.log";

        public static final String RESOURCE_SHARED_TITLE = "dm_%s_gt";

        public static final String RESOURCE_CONFIG_KEY = "dm_%s_c";
        public static final String RESOURCE_CONFIG_TITLE = "dm_%s_ct";
        public static final String RESOURCE_CONFIG_SUMMARY = "dm_%s_cs";

        public static final String RESOURCE_STATUS_KEY = "dm_%s_s";
        public static final String RESOURCE_STATUS_TITLE = "dm_%s_st";
        public static final String RESOURCE_STATUS_SUMMARY = "dm_%s_ss";

        public static final String SDCARD_LOCATION = SystemProperties.get("diskManager.sdcard.path", "/dev/block/mmcblk0");
        public static final String SCRIPT_LOCATION = SystemProperties.get("diskManager.script.path", "/system/etc/init.d/10diskManager.rc");
    
        public static String getMB(double iNum) {
                String lPrifix = SIZE_PRIFIX[0];
                double iCal = (double) iNum;
                double iDevide = 1024D;

                for (int i=1; i < SIZE_PRIFIX.length; i++) {
                        if (iCal < iDevide) {
                                break;
                        }

                        iCal = iCal/iDevide;
                        lPrifix = SIZE_PRIFIX[i];
                }

                return "" + (Math.round(iCal*100.0)/100.0) + lPrifix;
        }
    
        public static double diskUsage(String dir) {
                StatFs stat = new StatFs(dir);
                double result = ((double) stat.getBlockCount() - (double) stat.getAvailableBlocks()) * (double) stat.getBlockSize();

                return result;
        }

        public static double diskTotal(String dir) {
                StatFs stat = new StatFs(dir);
                double result = (double) stat.getBlockCount() * (double) stat.getBlockSize();

                return result;
        }
}

