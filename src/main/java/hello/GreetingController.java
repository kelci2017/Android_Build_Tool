package hello;

import helper.CommandLineExecuter;
import helper.GradleCommandLineExecuter;
import helper.GradleOutputListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import util.CommonUtil;
import util.Constants;
import util.PropertiesConfig;

import java.io.File;

@Controller
public class GreetingController implements GradleOutputListener {

    private static String FETCH_BUILD_INFO_BODY;
    private static String TOOLS_VERSION;
    private static final String BUILD_TOOLS_FOLDER = "build-tools";
    private static final String ZIPALIGN = "zipalign";
    private static final String TOOLS_VERSION_MATCH_STR = "The buildToolsVersion is:";

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
//    public Greeting greeting(HelloMessage message) throws Exception {
//        Thread.sleep(1000); // simulated delay
//        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
//    }
    public Greeting greeting(InputInfo message) throws Exception {
        //Thread.sleep(1000); // simulated delay
        runGradleTask(message.getVersionCode());
//        return runGradleTask();
        return new Greeting("Nihao, " + HtmlUtils.htmlEscape(message.getVersionCode()) + " " + message.getVersionName() + " " + message.getCommitNo() +  "!");
    }

    private void runGradleTask(String local_directory) {
        String gradlePath = CommonUtil.appendFilePath(local_directory
                ,"app"
                ,"build.gradle");
        //Log.info("the gradle path is: " + gradlePath);
        File fileGradle = new File(gradlePath);
        try{
            FileUtils.write(
                    new File(local_directory+"/local.properties")
                    ,"sdk.dir=" + PropertiesConfig.ANDROID_SDK_DIR
                    ,Constants.TEXT_ENCODING);
        }catch(Exception e) {

        }

       try{
           String gradleFileString = FileUtils.readFileToString(fileGradle, Constants.TEXT_ENCODING);
               //Add a task
               StringBuilder taskBuilder = new StringBuilder();

               taskBuilder.append(System.lineSeparator())
                       .append("task fetchBuildInfo {").append(System.lineSeparator())
                       .append("    doFirst {").append(System.lineSeparator())
                       .append("        println \"The sdk.dir is:\"+\"${android.getSdkDirectory().getAbsolutePath()}\"").append(System.lineSeparator())
                       .append("        println(\"The buildToolsVersion is:\"+android.buildToolsVersion)").append(System.lineSeparator())

                       .append("        android.applicationVariants.all { variant ->").append(System.lineSeparator())
                       .append("            variant.outputs.each { output ->").append(System.lineSeparator())
                       .append("                println \"APK output file:\" + output.outputFile").append(System.lineSeparator())
                       .append("            }").append(System.lineSeparator())
                       .append("        }").append(System.lineSeparator())

                       .append("    }").append(System.lineSeparator())
                       .append("}");

               FETCH_BUILD_INFO_BODY = taskBuilder.toString();
           if(!StringUtils.contains(gradleFileString, "fetchBuildInfo")){
               //Add a task
               FileUtils.write(fileGradle, FETCH_BUILD_INFO_BODY,Constants.TEXT_ENCODING, true);
               String[] tasksBuildInfo = {"fetchBuildInfo"};
               try{
                   new GradleCommandLineExecuter(local_directory,tasksBuildInfo,this).start();
               }catch (Exception e){

               }
           }

       }catch(Exception e) {

       }

        //Delete reports tests folder
        String reportsTestPath = CommonUtil.appendFilePath(local_directory
                ,"app"
                ,Constants.BUILD_FOLDER
                ,Constants.REPORT_FOLDER
                ,Constants.TEST_FOLDER);
        File fileTestReports = new File(reportsTestPath);
        FileUtils.deleteQuietly(fileTestReports);

        //Delete reports tests folder
        String reportsAndroidTestPath = CommonUtil.appendFilePath(local_directory
                ,"app"
                ,Constants.BUILD_FOLDER
                ,Constants.REPORT_FOLDER
                ,Constants.ANDROID_TEST_FOLDER);
        File fileAndroidTestReports = new File(reportsAndroidTestPath);
        FileUtils.deleteQuietly(fileAndroidTestReports);

        //Now it is time to build the app.
        try{
            String[] arguments = {"assembleDebug"};
            new GradleCommandLineExecuter(local_directory,arguments,this).start();
        }catch(Exception e){
        }
        String zipalignPath = CommonUtil.appendFilePath(PropertiesConfig.ANDROID_SDK_DIR
                ,TOOLS_VERSION
                ,BUILD_TOOLS_FOLDER
                ,ZIPALIGN);
                String apkPath = local_directory + "/app/build/outputs/apk/debug";
                String apkPathIn = apkPath;
                File fileApk = new File(apkPath);
                String fileNameOut = replaceUnsignApkName(fileApk.getName());
                String apkPathOut = fileApk.getParent() + Constants.FILE_SEPERATOR + fileNameOut;


                    String[] zipalignArgument = {
                            "-f"         //Overwrite existing apk
                            //,"-v"        //verbose output
                            ,"4"         //4-byte boundaries
                            ,apkPathIn   //APK to be compressed
                            ,apkPathOut  //APK output
                    };
                    try{
                        new CommandLineExecuter(zipalignPath, zipalignArgument, this).start();
                    }catch(Exception e) {

                    }

                    progressOutput("APK has been zipaligned successfully {}", apkPath);


        }

    protected void progressOutput(String output ,Object... arguments) {
        if(output == null)return;
        /**
         * There should be a better way to replace {} with arguments,
         * don't have enough time so just leave it for now.
         */
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
        this.template.convertAndSend("/topic/greetings", new Greeting(output));
    }

    @Override
    public void gradleOutput(String output, Object... arguments) {

        progressOutput(output,arguments);
        if(StringUtils.startsWith(output,TOOLS_VERSION_MATCH_STR)){
            TOOLS_VERSION = StringUtils.substringAfter(output, TOOLS_VERSION_MATCH_STR);
        }
    }

    private String replaceUnsignApkName(String unsignedApkFileName){
        if(StringUtils.endsWithIgnoreCase(unsignedApkFileName, "-unsigned.apk")){
            unsignedApkFileName = StringUtils.replace(unsignedApkFileName, "-unsigned.apk", ".apk");
        }else{
            unsignedApkFileName = StringUtils.replaceIgnoreCase(unsignedApkFileName, ".apk" ,"-signed.apk");
        }
        return unsignedApkFileName;
    }
    }
