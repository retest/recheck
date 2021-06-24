package de.retest.recheck.ui.descriptors;

import static de.retest.recheck.util.ObjectUtil.nextHashCode;

import java.util.Locale;
import java.util.Objects;

import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.image.Screenshot;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class ScreenshotAttributeDifference extends AttributeDifference {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private final String strategyName;

	@XmlAttribute
	private final double match;

	@SuppressWarnings( "unused" )
	private ScreenshotAttributeDifference() {
		super();
		strategyName = null;
		match = 0.0;
	}

	public ScreenshotAttributeDifference( final Screenshot expected, final Screenshot actual, final String strategyName,
			final double match ) {
		super( Attributes.SCREENSHOT, expected, actual );
		this.strategyName = strategyName;
		this.match = match;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public double getMatch() {
		return match;
	}

	@Override
	public int hashCode() {
		return nextHashCode( nextHashCode( super.hashCode(), strategyName ), match );
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( getClass() != obj.getClass() ) {
			return false;
		}

		final ScreenshotAttributeDifference other = (ScreenshotAttributeDifference) obj;
		return Objects.equals( getKey(), other.getKey() ) && Objects.equals( getExpected(), other.getExpected() )
				&& Objects.equals( getActual(), other.getActual() )
				&& Objects.equals( getStrategyName(), other.getStrategyName() );
	}

	@Override
	public String toString() {
		return getKey() + " = [expected=" + getExpected() + ", actual=" + getActual() + ", " + strategyName + " found "
				+ String.format( Locale.ENGLISH, "%.2f%%", 100 * match ) + " match]";
	}
}
