package me.goddragon.updater;

import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.gradle.api.Project;

public class JitPackUtil {
	private JitPackUtil() {
		// Utility class
	}

	private static final int HASH_LENGTH = 10;
	
	private static final String URL_BASE = "https://jitpack.io/com/github/%s/%s/%s/TeaseAIJava-%s.jar";

	public static String getArtifactUrl(Project project) {
		String version = getVersion(project);
		GitHubRepository repo = GitHubRepository.fromActionsString();
		
		return String.format(URL_BASE, repo.getOwner(), repo.getRepository(), version, version);
	}

	private static String getVersion(Project project) {
		try (Git git = Git.open(project.getProjectDir())) {
			String fullHash = git.getRepository()
					.findRef(Constants.HEAD)
					.getObjectId().name();
			return fullHash.substring(0, HASH_LENGTH);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} 
	}
}
