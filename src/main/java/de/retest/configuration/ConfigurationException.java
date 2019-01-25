package de.retest.configuration;

public class ConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final Property property;
	private final String details;

	public ConfigurationException( final Property property, final String details ) {
		this( property, details, null );
	}

	public ConfigurationException( final Property property, final Throwable throwable ) {
		this( property, null, throwable );
	}

	public ConfigurationException( final Property property, final String details, final Throwable throwable ) {
		super( "Incorrect retest configuration! \nProperty '" + property.getKey() + "' has an incorrect value '"
				+ property.getValue() + "': " + "\n" + details, throwable );
		assert details != null || throwable != null;
		this.property = property;
		this.details = createDetails( details, throwable );
	}

	private static String createDetails( final String details, final Throwable throwable ) {
		if ( details != null ) {
			if ( throwable != null ) {
				return details + " (" + getThrowableMessage( throwable ) + ")";
			}
			return details;
		}
		return getThrowableMessage( throwable );
	}

	private static String getThrowableMessage( final Throwable throwable ) {
		return throwable.getClass().getName() + ": " + throwable.getMessage();
	}

	public String getPropertyKey() {
		return property.getKey();
	}

	public String getPropertyValue() {
		return property.getValue();
	}

	public String getDetails() {
		return details;
	}

}
