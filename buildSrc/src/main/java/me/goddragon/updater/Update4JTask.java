package me.goddragon.updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import org.update4j.OS;

public class Update4JTask extends DefaultTask {
	@TaskAction
	void generateManifest() throws IOException {
		Builder builder = Configuration.builder();
		builder.basePath("${user.dir}/TeaseAI");
		builder.dynamicProperty("default.launcher.main.class", "me.goddragon.teaseai.Main");
		
		addMainJar(builder);
		appendDeps(builder);

		File configFile = getProject().file("build/pages/releases/update.xml");
		
		if(configFile.exists()) {
			configFile.delete();
		}
		
		try(FileWriter writer = new FileWriter(configFile)) {
			builder.build().write(writer);
		}
	}

	private void addMainJar(Builder builder) throws IOException {
		File mainJar = getMainJarFile();
		
		String uri = GitHubRepository.fromActionsString().getPagesUrl("releases/" + mainJar.getName());
		
		File target = getProject().file("build/pages/releases/" + mainJar.getName());
	
		target.getParentFile().mkdirs();
		
		Files.copy(mainJar.toPath(), target.toPath());
		
		builder.file(FileMetadata
				.readFrom(mainJar.getAbsolutePath())
				.uri(uri)
				.classpath()
		);
	}

	private File getMainJarFile() {
		final Jar jarTask = (Jar) getProject().getTasks().getByName("jar");
		final Provider<String> archiveName = jarTask.getArchiveFileName();
		final RegularFile file = jarTask.getDestinationDirectory().file(archiveName).get();
		return file.getAsFile();
	}

	private void appendDeps(Builder builder) throws IOException {
		Set<ResolvedArtifact> artifacts = getProject().getConfigurations().getByName("runtimeClasspath")
				.getResolvedConfiguration().getResolvedArtifacts();
		
		String[] repos = getRepositoryUrls();

		for (ResolvedArtifact artifact : artifacts) {
			
			addOtherOSFiles(repos, artifact, builder);
			
			String url = getArtifactUrl(repos, artifact);
			
			builder.file(
					FileMetadata.readFrom(artifact.getFile().getAbsolutePath())
						.uri(url)
						.osFromFilename()
						.classpath()
			);
		}
	}
	
	private URI toURI(String url) {
		try {
			return new URI(url);
		} catch (URISyntaxException e) {
			// Should never happen, these urls are generated by this class.
			throw new RuntimeException(e);
		}
	}
	
	private void addOtherOSFiles(String[] repos, ResolvedArtifact artifact, Builder builder) throws IOException {
		OS[] osClassifiers = { OS.LINUX, OS.WINDOWS, OS.MAC };
		String classifier = artifact.getClassifier();
		
		if(classifier != null && OS.fromShortName(classifier) == OS.CURRENT) {
			for(OS os : osClassifiers) {
				if(os == OS.CURRENT) {
					continue;
				}
				
				String url = getArtifactUrl(repos, artifact, os.getShortName());
				String filename = Paths.get(toURI(url).getPath()).getFileName().toString();
				
				File outputFile = getProject()
						.file("build/otherOS/" + os.getShortName() + "/" + filename + ".jar");
				
				outputFile.getParentFile().mkdirs();
				
				ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());
				try(FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
					fileOutputStream.getChannel()
					  .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
				}
				
				builder.file(FileMetadata.readFrom(outputFile.getPath())
						.uri(url)
					    .os(os)
						.classpath());
			}
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
		return getArtifactUrl(repositories, artifact, artifact.getClassifier());
	}
	
	private String getArtifactUrl(String[] repositories, ResolvedArtifact artifact, String classifierOverwrite) {
		for (String repoUrl : repositories) {
			ModuleVersionIdentifier dependency = artifact.getModuleVersion().getId();
			
			String group = dependency.getGroup().replace('.', '/');
			String name = dependency.getName();
			String version = dependency.getVersion();

			String classifier = "";

			if (classifierOverwrite != null) {
				classifier = "-" + classifierOverwrite;
			}
			
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
