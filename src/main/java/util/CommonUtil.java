package util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import org.apache.commons.io.FileUtils;

/**
 * Commonly used methods.
 *
 */
public class CommonUtil {
	private static final Gson GSON = new Gson();

	public static int parseToInt(String ori,int defaultValue){
		try{
			return Integer.parseInt(ori);
		}catch(Exception e){}
		
		return defaultValue;
	}

	public static final String formatDate(Date date,String format){
        if(date==null||format==null)return null;

        try{
            DateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(date);
        }catch(Exception e){}
        return null;
    }

	public static String toJson(Object src) {
        return GSON.toJson(src);
    }

	public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return GSON.fromJson(json,classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
        return GSON.fromJson(json,typeOfT);
    }

    public static <T> T fromJsonNoException(String json, Class<T> classOfT) {
        try {
            return fromJson(json, classOfT);
        } catch (JsonSyntaxException e){
            return null;
        } catch (Exception e){
            return null;
        }
    }

    public static <T> T fromJsonNoException(String json, Type typeOfT) {
        try {
            return fromJson(json, typeOfT);
        } catch (JsonSyntaxException e){
            return null;
        } catch (Exception e){
            return null;
        }
    }
	
    //Following static variables are for (un)wraping sensitive data.
    
	private static String[] SENSITIVE_ARRAY = {"<sensitive>","</sensitive>"};
	private static String[] BLANK_ARRAY = {"",""};
	private static String SENSITIVE_WRAPER = "<sensitive>?</sensitive>";
	private static String SENSITIVE_REGEX = "<sensitive>.*</sensitive>";
	private static String STARS = "xxxxxx";

	public static String wrapSensitiveData(String unSensitive){
		if(StringUtils.isBlank(unSensitive))return unSensitive;
		return StringUtils.replace(SENSITIVE_WRAPER, "?", unSensitive);
	}

	public static String unwrapSensitiveData(String sensitive){
		if(StringUtils.isEmpty(sensitive))return sensitive;
		
		return StringUtils.replaceEach(sensitive, SENSITIVE_ARRAY, BLANK_ARRAY);
	}

	public static String[] unwrapSensitiveData(String[] sensitives){
		if(sensitives == null)return sensitives;
		
		for(int i=0;i<sensitives.length;i++){
		    sensitives[i] = unwrapSensitiveData(sensitives[i]);
		}
		return sensitives;
	}

	public static String starsSensitiveData(String sensitive){
        if(StringUtils.isEmpty(sensitive))return sensitive;
		
		return StringUtils.replaceAll(sensitive, SENSITIVE_REGEX, STARS);
	}

	public static String appendString(String... toBeAppends){
		if(toBeAppends == null)return null;
		StringBuffer stringBuffer = new StringBuffer();
		for(String str:toBeAppends){
			if(str == null)continue;
			stringBuffer.append(str);
		}
		
		return stringBuffer.toString();
	}

	public static String appendFilePath(String... toBeAppends){
		if(toBeAppends == null)return null;
		StringBuffer stringBuffer = new StringBuffer();
		for(String str:toBeAppends){
			if(str == null)continue;
			if(stringBuffer.length() > 0){
				stringBuffer.append(Constants.FILE_SEPERATOR);
			}
			stringBuffer.append(str);
		}
		
		return stringBuffer.toString();
	}

	public static Collection<File> listFiles(String dirPath, IOFileFilter fileFilter, IOFileFilter dirFilter){
		try{
			return FileUtils.listFiles(new File(dirPath), fileFilter, dirFilter);
		}catch(Exception e){
			return null;
		}
	}
}
