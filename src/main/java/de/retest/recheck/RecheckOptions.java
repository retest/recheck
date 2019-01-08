package de.retest.recheck;

import java.util.ArrayList;
import java.util.List;

import de.retest.ui.ignore.ShouldIgnore;

public class RecheckOptions {

	private FileNamerStrategy fileNamerStrategy;
	private String suiteName;
	private final List<ShouldIgnore> shouldIgnores = new ArrayList<>();

	public RecheckOptions( final FileNamerStrategy fileNamerStrategy, final String suiteName ) {
		this.fileNamerStrategy = fileNamerStrategy;
		this.suiteName = suiteName;
	}

	public RecheckOptions() {
		this( new MavenConformFileNamerStrategy(), new MavenConformFileNamerStrategy().getTestClassName() );
	}

	public FileNamerStrategy getFileNamerStrategy() {
		return fileNamerStrategy;
	}

	public void setFileNamerStrategy( final FileNamerStrategy fileNamerStrategy ) {
		this.fileNamerStrategy = fileNamerStrategy;
	}

	public List<ShouldIgnore> getShouldIgnores() {
		return shouldIgnores;
	}

	public void addShouldIgnore( final ShouldIgnore shouldIgnores ) {
		this.shouldIgnores.add( shouldIgnores );
	}

	public String getSuiteName() {
		return suiteName;
	}

	public void setSuiteName( final String suiteName ) {
		this.suiteName = suiteName;
	}
}
