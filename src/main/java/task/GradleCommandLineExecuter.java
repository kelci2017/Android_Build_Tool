package task;

import org.apache.commons.lang3.SystemUtils;
import util.CommonUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GradleCommandLineExecuter extends BaseExecuter {
	private final String FAILURE_STRING = "BUILD FAILED";
	private final String FAILURE_STRING_MATCH = "BUILD FAILED in ";
	
	protected String projectInfo;

	public GradleCommandLineExecuter(String projectInfo
			,String[] arguments
			,GradleOutputListener outputListener){
		super(arguments,outputListener);
		
		this.projectInfo = projectInfo;
	}

	@Override
	protected void run() throws Exception {
        String commandPath = projectInfo + "/gradlew";
        
		outputCallback("{}Start running command {} with arguments {}" , System.lineSeparator(),commandPath ,printArray(arguments));
		
		InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
        	List<String> commandList = new ArrayList<String>();
        	if(SystemUtils.IS_OS_WINDOWS){
        	    commandList.add("cmd.exe");
        	    commandList.add("/C");
        	}
        	commandList.add(commandPath);
        	
        	for(String oneTask:arguments){
        		commandList.add(CommonUtil.unwrapSensitiveData(oneTask));
        	}
			
            ProcessBuilder processBuilder = new ProcessBuilder(commandList);
			
			Map<String, String> env = processBuilder.environment();
	        env.put("JAVA_OPTS", "");
			
			processBuilder.directory(new File(projectInfo));
			
			processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput();
			
            Process process = processBuilder.start();
            inputStream = process.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            boolean anyFailure = false;
            while ((line = bufferedReader.readLine()) != null) {
//				System.out.println(line);
            	outputCallback(line);
            	if(line.contains(FAILURE_STRING_MATCH))anyFailure = true;
            }
            if(anyFailure){
            	//throw new Exception(FAILURE_STRING);
            }
        } catch(Exception e) {
        	e.printStackTrace();
        	//throw e;
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
