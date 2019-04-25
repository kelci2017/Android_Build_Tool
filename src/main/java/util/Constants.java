package util;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Constants
 *
 */
public class Constants {
	public static final String DATE_FORMAT_YYYY_MM_DD_HHMISS = "yyyy-MM-dd HH:mm:ss";
	public static final SimpleDateFormat DATE_FORMAT_1 = new SimpleDateFormat("yyMMddHHmmss");
	public static final SimpleDateFormat DATE_FORMAT_2 = new SimpleDateFormat("MM/dd/yyyy HH:mm");
	public static final SimpleDateFormat DATE_FORMAT_3 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	public static final String BROADCASTER_ALL = "/build";
	
	public static final String BACK_SLASH = "/";
	public static final String TEXT_ENCODING = "UTF-8";
	public static final String EMPTY_STRING = "";
	public static final String ONE_SPACE = " ";
	
	public static final String FILE_SEPERATOR = System.getProperty("file.separator");
	

	public static final String GRADLE_FILE_NAME = "build.gradle";
    public static final String BUILD_LOG_FILE = "build_log.txt";
	
	public static final String BUILD_HISTORY_FOLDER = "build_history";
	public static final String BUILD_FOLDER = "build";
	public static final String REPORT_FOLDER = "reports";
	public static final String TEST_FOLDER = "tests";
	public static final String ANDROID_TEST_FOLDER = "androidTests";

    /**
     * File filter for directory.
     */
	public static final IOFileFilter DIR_FILTER = new IOFileFilter() {
		@Override
        public boolean accept(File directory, String fileName) {
			//Ignore directory
        	return true;
        }
		@Override
        public boolean accept(File file) {
    	    return true;
        }
    };
    /**
     * File filter for APK
     */
	public static final IOFileFilter APK_FILE_FILTER = new IOFileFilter() {
		@Override
        public boolean accept(File directory, String fileName) {
			//Ignore directory
        	return false;
        }
		@Override
        public boolean accept(File file) {
    	    return StringUtils.endsWithIgnoreCase(file.getName(), ".apk");
        }
    };
    /**
     * Filter for reports files end with html or htm.
     */
    public static final IOFileFilter TEST_REPORT_FILE_FILTER = new IOFileFilter() {
		@Override
        public boolean accept(File directory, String fileName) {
			//Ignore directory
        	return false;
        }
		@Override
        public boolean accept(File file) {
    	    return StringUtils.equalsIgnoreCase(file.getName(), "index.html")
    	    		|| StringUtils.equalsIgnoreCase(file.getName(), "index.htm");
        }
    };
}
