package me.goddragon.updater;

import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.repositories.UrlArtifactRepository;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskAction;
import org.gradle.jvm.tasks.Jar;
import org.update4j.Configuration;
import org.update4j.Configuration.Builder;
import org.update4j.FileMetadata;

public class Update4JTask extends DefaultTask {
	@TaskAction
	void generateManifest() throws IOException {
		Builder builder = Configuration.builder();
		builder.basePath("\\${user.dir}/gradletest");

		addMainJar(builder);
		appendDeps(builder);

		try(FileWriter writer = new FileWriter(getProject().file("build/update.xml"))) {
			builder.build().write(writer);
		}
	}

	private void addMainJar(Builder builder) {
		String uri = JitPackUtil.getArtifactUrl(getProject());
		
		builder.file(FileMetadata
				.readFrom(getMainJarPath())
				.uri(uri)
				.classpath()
		);
	}

	private String getMainJarPath() {
		final Jar jarTask = (Jar) getProject().getTasks().getByName("jar");
		final Provider<String> archiveName = jarTask.getArchiveFileName();
		final RegularFile file = jarTask.getDestinationDirectory().file(archiveName).get();
		return file.getAsFile().getAbsolutePath();
	}

	private void appendDeps(Builder builder) {
		Set<ResolvedArtifact> artifacts = getProject().getConfigurations().getByName("runtimeClasspath")
				.getResolvedConfiguration().getResolvedArtifacts();
		
		String[] repos = getRepositoryUrls();

		for (ResolvedArtifact artifact : artifacts) {
			String url = getArtifactUrl(repos, artifact);
			
			builder.file(
					FileMetadata.readFrom(artifact.getFile().getAbsolutePath())
						.uri(url)
						.classpath()
			);
		}
	}

	private String[] getRepositoryUrls() {
		return getProject().getRepositories().stream()
				.filter(repository -> repository instanceof UrlArtifactRepository)
				.map(repository -> (UrlArtifactRepository) repository)
				.map(repository -> repository.getUrl().toString())
				.toArray(String[]::new);

	}

	private String getArtifactUrl(String[] repositories, ResolvedArtifact artifact) {
		for (String repoUrl : repositories) {
			ModuleVersionIdentifier dependency = artifact.getModuleVersion().getId();
			String classifier = "";

			if (artifact.getClassifier() != null) {
				classifier = "-" + artifact.getClassifier();
			}

			String group = dependency.getGroup().replace('.', '/');
			String name = dependency.getName();
			String version = dependency.getVersion();

			String jarUrl = String.format("%s/%s/%s/%s/%s-%s%s.jar", repoUrl, group, name, version, name, version,
					classifier);

			if (exists(jarUrl)) {
				return jarUrl;
			}
		}

		throw new RuntimeException("Artifact not found in any repository");
	}

	private boolean exists(String url) {
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("HEAD");
			
			return con.getResponseCode() == HttpURLConnection.HTTP_OK;
		} catch (Exception e) {
			return false;
		}
	}
}
