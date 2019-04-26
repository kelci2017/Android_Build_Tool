package task;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.util.FS;

import java.io.File;


public class GitPullerTask extends BaseTask {
	private static final String CONTROL_DIRECTORY_TAG = ".git";
	private static final String BRANCH_MASTER = "master";
	
	private GitConfig gitConfig;
	private String[] strings_directory;
	
	public GitPullerTask(String projectInfo, GradleOutputListener outputListener) {
		super(projectInfo, outputListener);
	}

	public GitConfig setGitConfig(String directory, String git_url, String commitNo) {
		gitConfig = new GitConfig();

		gitConfig.setCommit_number(commitNo);
		gitConfig.setGit_url(git_url);
		gitConfig.setWorking_directory(directory);

		return gitConfig;
	}


	@Override
	protected void run() throws Exception {
		File workingDirectory = new File(gitConfig.getWorking_directory());
		if(!workingDirectory.exists()){
			workingDirectory.mkdir();
		}
		if(!workingDirectory.exists() || !workingDirectory.isDirectory()){
			throw new Exception("Project working directory doesn't exist.");
		}
		//Directory not empty
		if(workingDirectory.list().length>0){
			if(!validRepository()){

			}else{
				//Pull the specific commit then check it out
				repoPull(gitConfig.getCommit_number());
			}
		} else {
			//Clone the repository
			gitClone();
			//Pull the specific commit then check it out
			repoPull(gitConfig.getCommit_number());
		}
	}

	@Override
	protected boolean needProjectValidation(){
		return false;
	}

	private boolean validRepository() {
		if(controlDirectory() == null) return false;
		File workingDirectory = new File(gitConfig.getWorking_directory());
		if(!workingDirectory.exists() || !workingDirectory.isDirectory()){
			return false;
		}
		File[] files = workingDirectory.listFiles();
		if(files == null || files.length ==0){
			return false;
		}
		
		for(File file:files){
			if(file.getName().equalsIgnoreCase(controlDirectory()))return true;
		}
		return false;
    }
	
	private void gitClone() throws Exception {
		progressOutput("Start cloning {}" ,gitConfig.getGit_url());
		CloneCommand cloneCommand = Git.cloneRepository().setURI(gitConfig.getGit_url());
		
		grantCredential(cloneCommand);
		
		cloneCommand.setDirectory(new File(gitConfig.getWorking_directory())).setCloneAllBranches(true).call();
		progressOutput("Finished cloning");
	}

	protected void repoPull(String commitHash) throws Exception{
		progressOutput("Start pulling code from server {}" ,gitConfig.getGit_url());
		
		Git git = Git.open(new File(gitConfig.getWorking_directory()));
	
		progressOutput("Discarding changes made by previous build");
		//Discard any change
		git.reset().setMode(ResetType.HARD).setRef("HEAD").call();
		
		progressOutput("Checking out {}" ,BRANCH_MASTER);
		CheckoutCommand checkoutCommand = git.checkout().setName(BRANCH_MASTER);
		Ref refCheckout = checkoutCommand.setForce(true).call();
		strings_directory = gitConfig.getWorking_directory().split("/");

		git.branchDelete()
		.setBranchNames("branch_" + strings_directory[strings_directory.length - 1])
		.setForce(true)
		.call();
		
		progressOutput("Pulling updates from server...");
		//Pull from remote repository
		PullCommand pullCommand = git.pull();
		grantCredential(pullCommand);
		pullCommand.call();
	
		progressOutput("Checking out {} with specific commit {}" ,"branch_" + strings_directory[strings_directory.length - 1] ,commitHash);
		//Checkout to specific commit
		CheckoutCommand checkCommand = git.checkout();
		checkCommand.setCreateBranch(true)
		.setName("branch_" + strings_directory[strings_directory.length - 1])
		.setStartPoint(commitHash);
		Ref ref = checkCommand.call();
		
		RevWalk walk = new RevWalk(git.getRepository());
		RevCommit commit = walk.parseCommit(ref.getObjectId());
        walk.dispose();
        progressOutput("Finished pulling: {}" ,commit.getFullMessage());
	}

	protected String controlDirectory(){
		return CONTROL_DIRECTORY_TAG;
	}
	
	/**
	 * Grant the command permissions for specific protocol.
	 * @param transportCommand
	 */
	private void grantCredential(TransportCommand transportCommand){
		if(StringUtils.startsWithIgnoreCase(gitConfig.getGit_url(), "ssh://")){
			transportCommand.setTransportConfigCallback( new TransportConfigCallback() {
				@Override
				public void configure( Transport transport ) {
				    SshTransport sshTransport = ( SshTransport )transport;
				    sshTransport.setSshSessionFactory(createSshSessionFactory());
				}
			});
		} else if(StringUtils.startsWithIgnoreCase(gitConfig.getGit_url(), "https://")){
			if(!StringUtils.isAnyBlank(gitConfig.getCredential_user() ,gitConfig.getCredential_password())){
				transportCommand.setCredentialsProvider(
		        		new UsernamePasswordCredentialsProvider(
		        				gitConfig.getCredential_user() ,gitConfig.getCredential_password()));
			}
		}
	}

	private SshSessionFactory createSshSessionFactory(){
		SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
	        @Override
			protected void configure(Host host, Session session) {
			    session.setConfig("StrictHostKeyChecking", "no");
			    if(!StringUtils.isBlank(gitConfig.getCredential_password())){
			        session.setPassword(gitConfig.getCredential_password());
			    }
			}
	        @Override
	        protected JSch createDefaultJSch(FS fs) throws JSchException {
	          JSch defaultJSch = super.createDefaultJSch(fs);
	          if(!StringUtils.isBlank(gitConfig.getPrivate_key_path())){
	        	  if(!StringUtils.isBlank(gitConfig.getCredential_password())){
	        		  //Private key with password
	    	          defaultJSch.addIdentity(gitConfig.getPrivate_key_path() ,gitConfig.getCredential_password());
	        	  }else{
	        		  //Private key without password
	        		  defaultJSch.addIdentity(gitConfig.getPrivate_key_path());
	        	  }
	          }
	          return defaultJSch;
	        }
		};
		return sshSessionFactory;
	}

	private static class GitConfig{
		private String working_directory;
		private String git_url;
		private String private_key_path;
		private String credential_user;
		private String credential_password;
		private String commit_number;
		public String getWorking_directory() {
			return working_directory;
		}
		public void setWorking_directory(String working_directory) {
			this.working_directory = working_directory;
		}
		public String getGit_url() {
			return git_url;
		}
		public void setGit_url(String git_url) {
			this.git_url = git_url;
		}
		public String getPrivate_key_path() {
			return private_key_path;
		}
		public void setPrivate_key_path(String private_key_path) {
			this.private_key_path = private_key_path;
		}
		public String getCredential_user() {
			return credential_user;
		}
		public void setCredential_user(String credential_user) {
			this.credential_user = credential_user;
		}
		public String getCredential_password() {
			return credential_password;
		}
		public void setCredential_password(String credential_password) {
			this.credential_password = credential_password;
		}
		public String getCommit_number() {
			return commit_number;
		}
		public void setCommit_number(String commit_number) {
			this.commit_number = commit_number;
		}
	}
}
