package me.goddragon.updater;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class TriggerJitPackTask extends DefaultTask {

	@TaskAction
	public void notifyJitPack() throws IOException {
		URL url = new URL(JitPackUtil.getArtifactUrl(getProject()));
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("HEAD");
		con.setReadTimeout(10*60*1000);
		int responseCode = con.getResponseCode();
		
		if (responseCode != 200) {
            throw new RuntimeException( "Artifact not generated on jitpack" );
        }
	}
}
