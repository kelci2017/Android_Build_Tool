package task;

import util.CommonUtil;
import util.Constants;

public abstract class BaseExecuter {
	protected String[] arguments;
	protected GradleOutputListener outputListener;
	

	public BaseExecuter(String[] arguments
			,GradleOutputListener outputListener){
		this.arguments = arguments;
		this.outputListener = outputListener;
	}

	protected void outputCallback(String output ,Object... arguments){
		if(outputListener == null || output == null)return;
		outputListener.gradleOutput(output,arguments);
	}

	public void start() throws Exception {
		if(arguments == null || arguments.length ==0){
			throw new Exception("Tasks is empty.");
		}
		
		run();
	}

	protected abstract void run() throws Exception;

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
