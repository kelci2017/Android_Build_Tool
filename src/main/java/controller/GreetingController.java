package controller;

import task.GradleCommandTask;
import task.GradleOutputListener;
import task.GitPullerTask;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController implements GradleOutputListener {

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/build")
    @SendTo("/topic/buildlog")
    public void buildAndroidLog(InputInfo message) throws Exception {
        buildLogFile(message);
    }

    protected void progressOutput(String output ,Object... arguments) {
        if(output == null)return;

        int argumentIndex = 0;
        while(output.indexOf("{}") != -1){
            Object argument = null;
            if(arguments != null && argumentIndex<arguments.length){
                argument = arguments[argumentIndex];
            }
            output = StringUtils.replaceOnce(output, "{}", argument==null?"":argument.toString());


            argumentIndex ++;
        }
        System.out.println(output);
        this.template.convertAndSend("/topic/buildlog", new BuildLog(output));
    }

    @Override
    public void gradleOutput(String output, Object... arguments) {

        progressOutput(output,arguments);
    }

    private void buildLogFile(InputInfo message) {
        GitPullerTask taskGitPuller = new GitPullerTask(message.getGitUrl(), this);

        taskGitPuller.setGitConfig( message.getLocalDirectory(),message.getGitUrl(), message.getCommitNo());
        if(message.getGitUrl().equals("") || message.getGitUrl() == null || message.getCommitNo().equals("") || message.getCommitNo() == null) {
            runGradleTask(message);
        } else {
            try{
                taskGitPuller.start("Update Code(" + message.getGitUrl() + ")");
            }catch (Exception e) {

            }
            runGradleTask(message);
        }


    }

    private void runGradleTask(InputInfo message){
        GradleCommandTask gradleCommandTask = new GradleCommandTask(message.getLocalDirectory(), this);
        try{
            gradleCommandTask.start("Gradle command(" + message.getLocalDirectory() + ")");
        }catch (Exception e) {

        }
    }
    }
