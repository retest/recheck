package de.retest.recheck.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.retest.recheck.report.action.ActionReplayData;
import de.retest.recheck.report.action.DifferenceRetriever;
import de.retest.recheck.report.action.WindowRetriever;
import de.retest.recheck.ui.actions.Action;
import de.retest.recheck.ui.actions.ActionExecutionResult;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.descriptors.SutState;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.InsertedDeletedElementDifference;
import de.retest.recheck.ui.diff.LeafDifference;
import de.retest.recheck.ui.diff.RootElementDifference;
import de.retest.recheck.ui.diff.StateDifference;
import de.retest.recheck.ui.diff.meta.MetadataDifference;
import de.retest.recheck.ui.image.Screenshot;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;

/**
 * This class is more heavy than the {@link ActionExecutionResult} - it contains the result of the execution of an
 * {@link Action} which is one of:
 *
 * <ul>
 * <li>the {@link StateDifference} if there was a difference between expected and actual state</li>
 * <li>or just the state if everything went as expected.</li>
 * </ul>
 *
 * In addition, this class can be persisted as XML.
 */
@XmlRootElement( name = "action" )
@XmlAccessorType( XmlAccessType.FIELD )
public class ActionReplayResult implements Serializable {

	private static final long serialVersionUID = 2L;

	@XmlAttribute
	private final String description;

	private final String goldenMasterPath;

	@XmlElement
	private final Element targetcomponent;

	@XmlElement
	private final StateDifference stateDifference;

	@XmlElement
	@Getter
	private final MetadataDifference metadataDifference;

	@XmlAttribute
	private long duration;

	@XmlElement( name = "window" )
	@XmlElementWrapper( name = "state" )
	private final List<RootElement> windows;

	protected ActionReplayResult() {
		description = null;
		goldenMasterPath = null;
		targetcomponent = null;
		stateDifference = null;
		metadataDifference = MetadataDifference.empty();
		windows = null;
	}

	protected ActionReplayResult( final ActionReplayData data, final WindowRetriever windows,
			final DifferenceRetriever difference, final long duration ) {
		if ( windows.isNull() && difference.isNull() ) {
			throw new NullPointerException(
					"ActionReplayResult must not be empty! Affected action: " + data.getDescription() + "." );
		}
		description = data.getDescription();
		goldenMasterPath = data.getGoldenMasterPath();
		targetcomponent = data.getElement();
		this.windows = windows.get();
		stateDifference = difference.getStateDifference();
		metadataDifference = difference.getMetadataDifference();
		this.duration = duration;
	}

	public static ActionReplayResult createActionReplayResult( final ActionReplayData data,
			final StateDifference difference, final long actualDuration, final SutState actualState ) {

		if ( difference != null && !difference.getRootElementDifferences().isEmpty() ) {
			return withDifference( data, WindowRetriever.empty(), DifferenceRetriever.of( difference ),
					actualDuration );
		}
		return withDifference( data, WindowRetriever.of( actualState ), DifferenceRetriever.empty(), actualDuration );
	}

	public static ActionReplayResult withDifference( final ActionReplayData data, final WindowRetriever windows,
			final DifferenceRetriever difference, final long duration ) {
		return new ActionReplayResult( data, windows, difference, duration );
	}

	public StateDifference getStateDifference() {
		return stateDifference;
	}

	public long getDuration() {
		return duration;
	}

	public String getDescription() {
		return description;
	}

	public String getGoldenMasterPath() {
		return goldenMasterPath;
	}

	/**
	 * Recursively gets <em>all</em> element differences (including child-child-... differences).
	 *
	 * @return all element differences
	 */
	public List<ElementDifference> getAllElementDifferences() {
		final List<ElementDifference> differences = new ArrayList<>();
		if ( stateDifference != null ) {
			differences.addAll( stateDifference.getNonEmptyDifferences() );
		}
		return differences;
	}

	public Set<LeafDifference> getDifferences() {
		final Set<LeafDifference> result = new HashSet<>();
		for ( final ElementDifference elementDifference : getAllElementDifferences() ) {
			result.addAll( elementDifference.getAttributeDifferences() );
			final LeafDifference identifyingAttributesDifference =
					elementDifference.getIdentifyingAttributesDifference();
			if ( identifyingAttributesDifference instanceof InsertedDeletedElementDifference ) {
				result.add( identifyingAttributesDifference );
			}
		}
		return result;
	}

	public List<RootElement> getWindows() {
		return windows;
	}

	public Element getTargetComponent() {
		return targetcomponent;
	}

	public Screenshot getTargetScreenshot() {
		if ( getTargetComponent() != null ) {
			return getTargetComponent().getScreenshot();
		}
		return null;
	}

	public int getCheckedUiElementsCount() {
		if ( hasWindows() ) {
			int result = 0;
			for ( final RootElement rootElement : windows ) {
				result += rootElement.countAllContainedElements();
			}
			return result;
		}
		if ( stateDifference != null ) {
			int result = 0;
			for ( final RootElementDifference rootElementDifference : stateDifference.getRootElementDifferences() ) {
				result += rootElementDifference.getCheckedUiComponentCount();
			}
			return result;
		}
		return 0;
	}

	public boolean hasDifferences() {
		return stateDifference != null && stateDifference.size() > 0;
	}

	public boolean hasWindows() {
		return windows != null && !windows.isEmpty();
	}

	@Override
	public String toString() {
		return "ActionReplayResult('" + description + "' resulted in " + getAllElementDifferences().size()
				+ " differences.)";
	}
}
