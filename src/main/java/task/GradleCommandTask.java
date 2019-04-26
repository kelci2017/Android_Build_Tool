package task;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import util.CommonUtil;
import util.Constants;
import util.PropertiesConfig;

import java.io.File;

public class GradleCommandTask extends BaseTask {

    private static String FETCH_BUILD_INFO_BODY;
    private String gradleFileString;

    public GradleCommandTask(String projectInfo, GradleOutputListener outputListener){
        super(projectInfo, outputListener);
    }

    @Override
    protected void run() throws Exception {
        String gradlePath = CommonUtil.appendFilePath(projectInfo
                ,"app"
                ,"build.gradle");
        File fileGradle = new File(gradlePath);
        try{
            FileUtils.write(
                    new File(projectInfo+"/local.properties")
                    ,"sdk.dir=" + PropertiesConfig.ANDROID_SDK_DIR
                    , Constants.TEXT_ENCODING);
        }catch(Exception e) {

        }

        try{
            gradleFileString = FileUtils.readFileToString(fileGradle, Constants.TEXT_ENCODING);
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
                    new GradleCommandLineExecuter(projectInfo,tasksBuildInfo,outputListener).start();
                }catch (Exception e){

                }
            }

        }catch(Exception e) {

        }

        //Delete reports tests folder
        String reportsTestPath = CommonUtil.appendFilePath(projectInfo
                ,"app"
                ,Constants.BUILD_FOLDER
                ,Constants.REPORT_FOLDER
                ,Constants.TEST_FOLDER);
        File fileTestReports = new File(reportsTestPath);
        FileUtils.deleteQuietly(fileTestReports);

        //Delete reports tests folder
        String reportsAndroidTestPath = CommonUtil.appendFilePath(projectInfo
                ,"app"
                ,Constants.BUILD_FOLDER
                ,Constants.REPORT_FOLDER
                ,Constants.ANDROID_TEST_FOLDER);
        File fileAndroidTestReports = new File(reportsAndroidTestPath);
        FileUtils.deleteQuietly(fileAndroidTestReports);

        try{
            String[] arguments = {"assembleDebug"};
            new GradleCommandLineExecuter(projectInfo,arguments,outputListener).start();
        }catch(Exception e){
        }
    }
}
