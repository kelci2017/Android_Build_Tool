**Android build tool**

**Ideas**

* This build tool is created with IntelliJ and it's a maven project
* This build tool can build local android project
* When the page is loaded, there will a websocket connected with server, browser and server communicated by web socket
* When user input all the information and click Build button, the input info will be sent to server
* Based on the sent info, server use git pull the remote repository and use gradle to build the project
* The build log is sent by web socket to client and shown on the screen dynamically
* After built completed, apk file is stored in the local directory/app/build/output

Build android project from local directory:
* Select the left "Build project from local directory"
* Input the local directory of android project
* Click the Build button

Build android project from git remote repository:
* Select the left "Build project from git remote repository"
* Input the local directory where the pulled remote repository will be saved
* Input the remote git url
* Input the commit number

**Demo**
![LoginScreenshot](https://github.com/kelci2017/Android_Build_Tool/blob/master/BuildAndroidGoogleChrome4_2.gif)
