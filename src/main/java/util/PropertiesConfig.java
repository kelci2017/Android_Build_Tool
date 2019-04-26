package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfig {
	private static final Logger logger = LoggerFactory.getLogger(PropertiesConfig.class);

	public static String ANDROID_SDK_DIR;

	public static String DATABASE_JDBC_CLASSNAME;
	public static String DATABASE_JDBC_URL;
	public static String DATABASE_USER_NAME;
	public static String DATABASE_PASSWORD;
	public static int DATABASE_MIN_CONNECTION = 5;
	public static int DATABASE_MAX_CONNECTION = 10;

	public static synchronized void initConfiguration(){
		Properties properties = new Properties();
        try {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("local.properties");
            properties.load(stream);
            
            ANDROID_SDK_DIR = properties.getProperty("ANDROID_SDK_DIR");
            
            //database jdbc class name
			DATABASE_JDBC_CLASSNAME = properties.getProperty("DATABASE_JDBC_CLASSNAME");
			
			//database url
			DATABASE_JDBC_URL = properties.getProperty("DATABASE_JDBC_URL");
			
			//database user name
			DATABASE_USER_NAME = properties.getProperty("DATABASE_USER_NAME");
			//database password
			DATABASE_PASSWORD = properties.getProperty("DATABASE_PASSWORD");
			//database minimum connection
			DATABASE_MIN_CONNECTION = CommonUtil.parseToInt(properties.getProperty("DATABASE_MIN_CONNECTION"),DATABASE_MIN_CONNECTION);
			//database maximum connection
			DATABASE_MAX_CONNECTION = CommonUtil.parseToInt(properties.getProperty("DATABASE_MAX_CONNECTION"),DATABASE_MAX_CONNECTION);
			
			if(logger.isInfoEnabled()){
				logger.info("ANDROID_SDK_DIR {}" ,ANDROID_SDK_DIR);
				logger.info("DATABASE_JDBC_CLASSNAME {}" ,DATABASE_JDBC_CLASSNAME);
				logger.info("DATABASE_JDBC_URL {}" ,DATABASE_JDBC_URL);
				logger.info("DATABASE_USER_NAME {}" ,DATABASE_USER_NAME);
				logger.info("DATABASE_MIN_CONNECTION {}" ,DATABASE_MIN_CONNECTION);
				logger.info("DATABASE_MAX_CONNECTION {}" ,DATABASE_MAX_CONNECTION);
			}
			
			stream.close();
        }catch(Exception e){
        	logger.error("Failed to load configuration from server.properties {}" ,e.toString());
            e.printStackTrace();
        }
	}
}