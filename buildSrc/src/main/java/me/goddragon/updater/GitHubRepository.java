package me.goddragon.updater;

public class GitHubRepository {
	private final String owner;
	private final String repository;

	public GitHubRepository(String owner, String repository) {
		super();
		this.owner = owner;
		this.repository = repository;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public String getRepository() {
		return repository;
	}
	
	private static String getActionString() {
		String actionString = System.getenv("GITHUB_REPOSITORY");
		
		if(actionString == null) {
			throw new RuntimeException("This task is only available on Github Actions!");
		}
		
		return actionString;
	}
	
	public static GitHubRepository fromActionsString() {
		
		String[] parts = getActionString().split("/", 2);
		String owner = parts[0];
		String repository = parts[1];
		return new GitHubRepository(owner, repository);
	}
	
	public String getPagesUrl(String file) {
		return String.format("https://%s.github.io/%s/%s", 
				this.owner, this.repository, file);
	}
}
