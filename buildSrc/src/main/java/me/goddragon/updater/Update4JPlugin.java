package me.goddragon.updater;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class Update4JPlugin implements Plugin<Project> {
	public void apply(Project project) {	
        project.getTasks().create("generateUpdateConfig", Update4JTask.class).dependsOn("jar");
    }
}
