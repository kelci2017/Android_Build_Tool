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
	
    /**
     * Constructor of a task.
     * <p>projectInfo and taskInfo are mandatory
     * @param projectInfo
     * @param taskInfo
     */
	public BaseTask(String projectInfo, GradleOutputListener outputListener) {
		this.projectInfo = projectInfo;
		this.outputListener = outputListener;
	}
	
	/**
	 * Start running a task. It is a wrapper of {@link #run()} by providing
	 * initialization and argument checking.
	 * @throws Exception
	 */
	public final void start(String taskTitle) throws Exception {
		/**
		 * Note that task name is highlighted
		 * by make it inline(HTML5) h5 tag to occupy a single line.
		 */
		progressOutput("<h5 style='display:inline'>{}</h5>" ,taskTitle);
		
		//If this task need validation of a project before proceeding.
		if(needProjectValidation()){
			projectValidation();
		}
		
		run();
		
		//Print a new line when task is finished.
		progressOutput();
	}
	
	/**
	 * Check if the project is a gradle one by:
	 * <ui>
	 * <li>The folder is not empty
	 * <li>The folder should contain a build.gralde file.
	 * </ui>
	 * @throws Exception
	 */
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
	
	/**
	 * Print out logs to connected socket clients.
	 * @param output
	 * @param arguments
	 */
	protected void progressOutput(String output ,Object... arguments) {
		if(output == null)return;
		
		/**
		 * There should be a better way to replace {} with arguments,
		 * don't have enough time so just leave it for now.
		 */
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
	/**
	 * Print out a new line
	 */
	protected void progressOutput() {
		progressOutput(Constants.EMPTY_STRING);
	}

	
	/**
	 * <p>Whether or not the task needs project validation</p>
	 * <p>Most tasks should return true, the exception is the
	 * code puller like Git or SVN tasks as before the task is executed, the
	 * project path is empty.</p>
	 * <p>If true is return, the validation will do the following:</p>
	 * <pre>
	 *   The project directory exists or not in the file system.
	 *   If there is a build.gradle file in it.
	 *   If sdk.dir is set in server.properties which should point to the sdk home directory in the computer.
	 *   Update sdk.dir in local.properties file.
	 * </pre>
	 * @return
	 */
	protected boolean needProjectValidation(){
		return true;
	}
    
    /**
     * Run the task which implemented by concrete tasks.
     * @throws Exception
     */
	protected abstract void run() throws Exception;

}
