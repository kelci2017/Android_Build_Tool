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

	public static final String TEXT_ENCODING = "UTF-8";
	public static final String EMPTY_STRING = "";
	public static final String ONE_SPACE = " ";
	
	public static final String FILE_SEPERATOR = System.getProperty("file.separator");

	public static final String BUILD_FOLDER = "build";
	public static final String REPORT_FOLDER = "reports";
	public static final String TEST_FOLDER = "tests";
	public static final String ANDROID_TEST_FOLDER = "androidTests";
}
