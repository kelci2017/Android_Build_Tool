package task;

import util.CommonUtil;
import util.Constants;

public abstract class BaseExecuter {
	protected String[] arguments;
	protected GradleOutputListener outputListener;
	
	/**
	 * Constructor of gradle executer.
	 * @param projectInfo The ProjectInfo object passed from BaseTask
	 * @param arguments Tasks or options for this gradle run
	 * @param outputListener The callback of this run, mostly for output purpose.
	 */
	public BaseExecuter(String[] arguments
			,GradleOutputListener outputListener){
		this.arguments = arguments;
		this.outputListener = outputListener;
	}
	
	/**
	 * The callback will be triggered here.
	 * @param outputListener
	 * @param output
	 * @param arguments
	 */
	protected void outputCallback(String output ,Object... arguments){
		if(outputListener == null || output == null)return;
		outputListener.gradleOutput(output,arguments);
	}
	
	/**
	 * <p>The wrapper method of run which does some initialization checking.</p>
	 * @throws Exception
	 */
	public void start() throws Exception {
		if(arguments == null || arguments.length ==0){
			throw new Exception("Tasks is empty.");
		}
		
		run();
	}
	
	/**
	 * Concrete implements should override this method
	 * @throws Exception
	 */
	protected abstract void run() throws Exception;

	/**
	 * Print the output for an string array separated by a space. 
	 * @param arguments
	 * @return
	 */
	protected static String printArray(String[] arguments) {
		StringBuilder outputBuilder = new StringBuilder();
		if(arguments != null && arguments.length >0){
			for(String argument:arguments){
				if(outputBuilder.length()>0)outputBuilder.append(Constants.ONE_SPACE);
			    outputBuilder.append(CommonUtil.starsSensitiveData(argument));
			}
		}
		return outputBuilder.toString();
	}
}
