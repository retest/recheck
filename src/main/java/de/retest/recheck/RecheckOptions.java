package de.retest.recheck;

public class RecheckOptions {

	private FileNamerStrategy fileNamerStrategy = new MavenConformFileNamerStrategy();
	private String suiteName = fileNamerStrategy.getTestClassName();

	public FileNamerStrategy getFileNamerStrategy() {
		return fileNamerStrategy;
	}

	public void setFileNamerStrategy( final FileNamerStrategy fileNamerStrategy ) {
		this.fileNamerStrategy = fileNamerStrategy;
	}

	public String getSuiteName() {
		return suiteName;
	}

	public void setSuiteName( final String suiteName ) {
		this.suiteName = suiteName;
	}

}
