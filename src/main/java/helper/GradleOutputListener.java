package helper;

public interface GradleOutputListener {
	void gradleOutput(String output, Object... arguments);
}