package task;

import com.google.gson.JsonElement;

public class TaskInfo {
    private String task_name;
    private String task_class;
    /**
     * <p>This property indicates whether or not block
     * whole process if it fails. For example,
     * unit or Ui testing, user want to proceed even testing fails.
     * default is true.</p>
     */
    private boolean blockProcess = true;
    private JsonElement arguments;

    public String getTask_name () {
        return task_name;
    }
    public void setTask_name (String task_name) {
        this.task_name = task_name;
    }
    public String getTask_class () {
        return task_class;
    }
    public void setTask_class (String task_class) {
        this.task_class = task_class;
    }
    public JsonElement getArguments() {
        return arguments;
    }
    public void setArguments(JsonElement arguments) {
        this.arguments = arguments;
    }
    public boolean isBlockProcess() {
        return blockProcess;
    }
    public void setBlockProcess(boolean blockProcess) {
        this.blockProcess = blockProcess;
    }
}
