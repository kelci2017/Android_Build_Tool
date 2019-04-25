package helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import util.CommonUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommandLineExecuter extends BaseExecuter {
	
	private final String FAILURE_STRING_MATCH = "BUILD FAILED in ";
	
	private String commandPath;
	private String fail_string_match = FAILURE_STRING_MATCH;
	
	/**
	 * Constructor of a command
	 * @param commandPath
	 * @param arguments
	 * @param outputListener
	 */
	public CommandLineExecuter(String commandPath
			, String[] arguments
			, GradleOutputListener outputListener){
		super(arguments,outputListener);
		
		this.commandPath = commandPath;
	}
	/**
	 * Constructor of a command with specific fail message
	 * @param commandPath
	 * @param arguments
	 * @param outputListener
	 */
	public CommandLineExecuter(String commandPath
			, String fail_string_match
			, String[] arguments
			, GradleOutputListener outputListener){
		super(arguments,outputListener);
		
		this.fail_string_match = fail_string_match;
		if(StringUtils.isBlank(fail_string_match)){
			this.fail_string_match = FAILURE_STRING_MATCH;
		}
		this.commandPath = commandPath;
	}

	@Override
	protected void run() throws Exception {
		outputCallback("{}Start running command {} with arguments {}" , System.lineSeparator(),commandPath ,printArray(arguments));
		InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
        	List<String> commandList = new ArrayList<String>();
        	if(SystemUtils.IS_OS_WINDOWS){
        		/**
        		 * Note that if I added /bin/bash on Linux/Mac, it's not accepting arguments, don't know why
        		 */
        		//commandList.add(SystemUtils.IS_OS_WINDOWS?"cmd.exe":"/bin/bash");
        	    commandList.add("cmd.exe");
        	    //commandList.add(SystemUtils.IS_OS_WINDOWS?"/C":"-c");
        	    commandList.add("/C");
        	}
        	commandList.add(commandPath);
        	
        	for(String oneTask:arguments){
        		commandList.add(CommonUtil.unwrapSensitiveData(oneTask));
        	}
			
            ProcessBuilder processBuilder = new ProcessBuilder(commandList);
			
			processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput();
			
            Process process = processBuilder.start();
            inputStream = process.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            boolean anyFailure = false;
            while ((line = bufferedReader.readLine()) != null) {
            	outputCallback(line);
            	if(line.contains(fail_string_match))anyFailure = true;
            }
            if(anyFailure){
            	throw new Exception(line);
            }
        } catch(Exception e) {
        	throw e;
        } finally {
        	try{
        		if(bufferedReader != null)bufferedReader.close();
        	}catch(Exception e){}
        	try{
        		if(inputStreamReader != null)inputStreamReader.close();
        	}catch(Exception e){}
        	try{
        		if(inputStream != null)inputStream.close();
        	}catch(Exception e){}
        }
	}
}
