package de.retest.recheck.ui.diff;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Structurally stores the information about a test breakage caused by the
 * <a href="https://retest.github.io/docs/recheck-web/element-identification-problem/">GUI element identification
 * problem</a>. This information can be re-used by review or recheck.cli when the user applies a change.
 */
@Data
@RequiredArgsConstructor
public class ElementIdentificationWarning implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The name of the file the test resides in (e.g. "MySeleniumTest.java"). Note that the file name can be different
	 * from the qualified class name of the test class given in {@link #qualifiedTestName}. Also, the file name (which
	 * comes without path) can exist multiple times in different folders (aka packages). Therefore we need both.
	 */
	@XmlAttribute
	private final String testFileName;

	/**
	 * The line number of the invocation of the method that needs to be adapted (e.g. 534).
	 */
	@XmlAttribute
	private final Integer testLineNumber;

	/**
	 * The method name of the calling method that needs to be adapted (e.g. "findById"). Note that e.g. an ID change can
	 * both affect a call to `findByTag("a")` as well as a call to `findByCSS("a")`. Therefore, the affected attribute
	 * (e.g id) alone does not suffice.
	 */
	@XmlAttribute
	private final String findByMethodName;

	/**
	 * The qualified name of the test class (e.g. "de.retest.MySeleniumTest"). Note that the class can reside in a file
	 * with a different name (e.g. "Tests.java"). But the file name alone as given by {@link #testFileName} does not
	 * suffice as well, because a file with the same name could exist multiple times in different packages of the
	 * project.
	 */
	@XmlAttribute
	private final String qualifiedTestName;

	@SuppressWarnings( "unused" )
	private ElementIdentificationWarning() {
		testFileName = null;
		testLineNumber = null;
		findByMethodName = null;
		qualifiedTestName = null;
	}
}
