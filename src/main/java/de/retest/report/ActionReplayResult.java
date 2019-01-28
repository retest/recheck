package de.retest.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.retest.report.action.ActionReplayData;
import de.retest.report.action.DifferenceRetriever;
import de.retest.report.action.ErrorHolder;
import de.retest.report.action.WindowRetriever;
import de.retest.ui.actions.Action;
import de.retest.ui.actions.ActionExecutionResult;
import de.retest.ui.actions.ActionState;
import de.retest.ui.actions.ExceptionWrapper;
import de.retest.ui.actions.TargetNotFoundException;
import de.retest.ui.actions.TargetNotFoundWrapper;
import de.retest.ui.descriptors.Element;
import de.retest.ui.descriptors.RootElement;
import de.retest.ui.descriptors.SutState;
import de.retest.ui.diff.AttributesDifference;
import de.retest.ui.diff.ElementDifference;
import de.retest.ui.diff.IdentifyingAttributesDifference;
import de.retest.ui.diff.LeafDifference;
import de.retest.ui.diff.RootElementDifference;
import de.retest.ui.diff.StateDifference;
import de.retest.ui.image.Screenshot;

/**
 * This class is more heavy than the {@link ActionExecutionResult} - it contains the result of the execution of an
 * {@link Action} which is one of:
 *
 * <ul>
 * <li>an error if one occurred</li>
 * <li>the {@link StateDifference} if there was a difference between expected and actual state</li>
 * <li>or just the state if everything went as expected.</li>
 * </ul>
 *
 * In addition, this class can be persisted as XML.
 */
@XmlRootElement( name = "action" )
@XmlAccessorType( XmlAccessType.FIELD )
public class ActionReplayResult implements Serializable {

	public static enum ExecutionType {
		NORMAL,
		INSERTION,
		DELETION
	}

	private static final long serialVersionUID = 1L;
	private static final long NO_DURATION = 0L;

	@XmlAttribute
	private final String description;

	private final String stateFilePath;

	@XmlElement
	private final Element targetcomponent;

	@XmlElement
	private final ExceptionWrapper error;
	@XmlElement
	private final TargetNotFoundWrapper targetNotFound;

	@XmlElement
	private final StateDifference stateDifference;

	@XmlAttribute
	private long duration;

	@XmlAttribute
	private final ExecutionType executionType;

	@XmlElement( name = "window" )
	@XmlElementWrapper( name = "state" )
	private final List<RootElement> windows;

	protected ActionReplayResult() {
		description = null;
		stateFilePath = null;
		targetcomponent = null;
		error = null;
		targetNotFound = null;
		stateDifference = null;
		windows = null;
		executionType = null;
	}

	public static ActionReplayResult createActionReplayResult( final ActionReplayData data,
			final ExceptionWrapper error, final TargetNotFoundException targetNotFound,
			final StateDifference difference, final long actualDuration, final SutState actualState ) {
		if ( error != null || targetNotFound != null ) {
			return withError( data, ErrorHolder.of( error, targetNotFound ) );
		}
		if ( !difference.getRootElementDifferences().isEmpty() ) {
			return withDifference( data, WindowRetriever.empty(), DifferenceRetriever.of( difference ),
					actualDuration );
		}
		return withoutDifference( data, WindowRetriever.of( actualState ), actualDuration );
	}

	public static ActionReplayResult withError( final ActionReplayData data, final ErrorHolder error ) {
		return new ActionReplayResult( data, WindowRetriever.empty(), error, DifferenceRetriever.empty(), NO_DURATION,
				null );
	}

	public static ActionReplayResult withDifference( final ActionReplayData data, final WindowRetriever windows,
			final DifferenceRetriever difference, final long duration ) {
		return new ActionReplayResult( data, windows, ErrorHolder.empty(), difference, duration, null );
	}

	public static ActionReplayResult withoutDifference( final ActionReplayData data, final WindowRetriever windows,
			final long duration ) {
		return withDifference( data, windows, DifferenceRetriever.empty(), duration );
	}

	public static ActionReplayResult insertion( final ActionState actionState ) {
		return new ActionReplayResult( ActionReplayData.of( actionState.getAction() ),
				WindowRetriever.of( actionState ), ErrorHolder.empty(), DifferenceRetriever.empty(),
				actionState.getDuration(), ExecutionType.INSERTION );
	}

	public static ActionReplayResult deletion( final ActionState actionState ) {
		return new ActionReplayResult( ActionReplayData.of( actionState.getAction() ),
				WindowRetriever.of( actionState ), ErrorHolder.empty(), DifferenceRetriever.empty(), NO_DURATION,
				ExecutionType.DELETION );
	}

	protected ActionReplayResult( final ActionReplayData data, final WindowRetriever windows, final ErrorHolder error,
			final DifferenceRetriever difference, final long duration, final ExecutionType executionType ) {
		if ( windows.isNull() && difference.isNull() && !error.hasError() ) {
			throw new NullPointerException(
					"ActionReplayResult must not be empty! Affected action: " + data.getDescription() + "." );
		}
		description = data.getDescription();
		stateFilePath = data.getStateFilePath();
		targetcomponent = data.getElement();
		this.windows = windows.get();
		this.error = error.getThrowableWrapper();
		targetNotFound = error.getTargetNotFoundWrapper();
		stateDifference = difference.get();
		this.duration = duration;
		this.executionType = executionType;
	}

	public Throwable getThrowable() {
		return error == null ? null : error.getThrowable();
	}

	public ExceptionWrapper getThrowableWrapper() {
		return error;
	}

	public Throwable getTargetNotFoundException() {
		return targetNotFound == null ? null : targetNotFound.getTargetNotFoundException();
	}

	public TargetNotFoundWrapper getTargetNotFoundWrapper() {
		return targetNotFound;
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

	public String getStateFilePath() {
		return stateFilePath;
	}

	public List<ElementDifference> getElementDifferences() {
		final List<ElementDifference> differences = new ArrayList<>();
		if ( stateDifference != null ) {
			differences.addAll( stateDifference.getNonEmptyDifferences() );
		}
		if ( error != null ) {
			// TODO what to do about this?
		}
		return differences;
	}

	public Set<Object> getUniqueDifferences() {
		final Set<Object> uniqueDifferences = new HashSet<>();
		for ( final ElementDifference elementDifference : getElementDifferences() ) {
			final AttributesDifference attributesDifference = elementDifference.getAttributesDifference();
			if ( attributesDifference != null ) {
				uniqueDifferences.addAll( attributesDifference.getDifferences() );
			}
			final LeafDifference identAttrDiff = elementDifference.getIdentifyingAttributesDifference();
			if ( identAttrDiff != null ) {
				if ( identAttrDiff instanceof IdentifyingAttributesDifference ) {
					uniqueDifferences
							.addAll( ((IdentifyingAttributesDifference) identAttrDiff).getAttributeDifferences() );
				} else {
					// DurationDifference or InsertedDeletedElementDifference
					uniqueDifferences.add( identAttrDiff );
				}
			}
		}
		return uniqueDifferences;
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
		if ( !windows.isEmpty() ) {
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
		// Means error != null or targetNotFound != null.
		return 0;
	}

	public boolean hasError() {
		return error != null || targetNotFound != null;
	}

	public boolean hasDifferences() {
		return stateDifference != null && stateDifference.size() > 0;
	}

	public boolean hasWindows() {
		return windows != null && windows.size() > 0;
	}

	public ExecutionType getExecutionType() {
		return executionType != null ? executionType : ExecutionType.NORMAL;
	}

	@Override
	public String toString() {
		if ( hasError() ) {
			return description + " resulted in " + (error != null ? error : targetNotFound);
		}
		return description + " resulted in " + getElementDifferences().size() + " differences.";
	}

	public String toStringDetailed() {
		if ( hasError() ) {
			return "Check \'" + description + "\' resulted in " + (error != null ? error : targetNotFound);
		}
		final String diffs = getElementDifferences().stream() //
				.map( elementDifference -> "\t" + elementDifference.toString().replace( "\n", "\n\t" ) ) //
				.collect( Collectors.joining( "\n" ) );
		return "Check \'" + description + "\' resulted in:\n" + diffs;
	}
}
