package task;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import util.Constants;
import util.PropertiesConfig;

import java.io.File;

public abstract class BaseTask {
	private static String ARGUMENT_BRACKET = "{}";
	protected GradleOutputListener outputListener;
	
    protected String projectInfo;
	private String buildOutput;

	public BaseTask(String projectInfo, GradleOutputListener outputListener) {
		this.projectInfo = projectInfo;
		this.outputListener = outputListener;
	}

	public final void start(String taskTitle) throws Exception {

		progressOutput("<h5 style='display:inline'>{}</h5>" ,taskTitle);
		
		//If this task need validation of a project before proceeding.
		if(needProjectValidation()){
			projectValidation();
		}
		
		run();
		
		//Print a new line when task is finished.
		progressOutput();
	}

	private void projectValidation() throws Exception {
		File fileProject = new File(projectInfo);
		if(!fileProject.exists() || !fileProject.isDirectory()){
			throw new Exception("The project path doesn't exist in file system or is not a directory.");
		};
		
		File gradleFile = new File(projectInfo + "/build.gradle");
		if(!gradleFile.exists() || !gradleFile.isFile()){
			throw new Exception("The project doesn't look like an Android project(build.gradle missing).");
		}
    	
    	//Check if local.properties exists
    	FileUtils.write(
    			new File(projectInfo+"/local.properties")
    			,"sdk.dir=" + PropertiesConfig.ANDROID_SDK_DIR
    			,Constants.TEXT_ENCODING);
	}

	protected void progressOutput(String output ,Object... arguments) {
		if(output == null)return;

		int argumentIndex = 0;
		while(output.indexOf(ARGUMENT_BRACKET) != -1){
			Object argument = null;
			if(arguments != null && argumentIndex<arguments.length){
				argument = arguments[argumentIndex];
			}
			output = StringUtils.replaceOnce(output, ARGUMENT_BRACKET, argument==null?"":argument.toString());
			
			argumentIndex ++;
		}
		outputListener.gradleOutput(output,arguments);
		buildOutput = output;
		//the build output should be sent to client by web socket
	}
	public String getBuildOutput() {
		return buildOutput;
	}

	protected void progressOutput() {
		progressOutput(Constants.EMPTY_STRING);
	}

	protected boolean needProjectValidation(){
		return true;
	}

	protected abstract void run() throws Exception;

}
