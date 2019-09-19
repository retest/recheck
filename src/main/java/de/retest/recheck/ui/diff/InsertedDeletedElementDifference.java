package de.retest.recheck.ui.diff;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.image.Screenshot;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class InsertedDeletedElementDifference implements LeafDifference {

	private static final long serialVersionUID = 1L;

	@XmlElement
	private final Element expectedElement;

	@XmlElement
	private final Element actualElement;

	// TODO See RET-417:
	// Because Element gets erased in lightweight XML, we need the info doubly
	@XmlElement
	private final IdentifyingAttributes expected;

	@XmlElement
	private final IdentifyingAttributes actual;

	// Because Element gets erased in lightweight XML, we need the info doubly
	@XmlElement
	private final Screenshot screenshot;

	private InsertedDeletedElementDifference() {
		// for JAXB
		expected = null;
		actual = null;
		expectedElement = null;
		actualElement = null;
		screenshot = null;
	}

	private InsertedDeletedElementDifference( final Element expected, final Element actual ) {
		expectedElement = expected;
		actualElement = actual;
		screenshot = expectedElement != null ? expectedElement.getScreenshot() : actualElement.getScreenshot();
		this.expected = expected != null ? expected.getIdentifyingAttributes() : null;
		this.actual = actual != null ? actual.getIdentifyingAttributes() : null;
	}

	public static InsertedDeletedElementDifference differenceFor( final Element expected, final Element actual ) {
		if ( expected == null && actual == null ) {
			return null;
		}
		if ( expected != null && actual != null ) {
			throw new IllegalStateException( "We treat only insertions or deletions here!" );
		}
		return new InsertedDeletedElementDifference( expected, actual );
	}

	@Override
	public String toString() {
		return "[expected=" + expected + ", actual=" + actual + "]";
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public List<ElementDifference> getNonEmptyDifferences() {
		return Collections.emptyList();
	}

	@Override
	public Element getActual() {
		return actualElement;
	}

	@Override
	public Element getExpected() {
		return expectedElement;
	}

	@Override
	public List<ElementDifference> getElementDifferences() {
		return Collections.emptyList();
	}

	public Screenshot getScreenshot() {
		return screenshot;
	}

	public boolean isInserted() {
		return expectedElement == null;
	}

	/**
	 * @return The inserted or deleted element. That is, {@link #getActual()} if {@link #isInserted()} is
	 *         <code>true</code>, otherwise {@link #getExpected()}.
	 */
	public Element getInsertedOrDeletedElement() {
		return isInserted() ? actualElement : expectedElement;
	}

}
