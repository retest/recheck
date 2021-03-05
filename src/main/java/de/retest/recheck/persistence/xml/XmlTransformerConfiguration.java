package de.retest.recheck.persistence.xml;

import de.retest.recheck.persistence.xml.util.XmlUtil;
import jakarta.xml.bind.Marshaller;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class XmlTransformerConfiguration {

	/**
	 * @see XmlUtil#addLightWeightAdapter(Marshaller)
	 */
	@Builder.Default
	private final boolean lightweightXml = false;
	/**
	 * @see Marshaller#JAXB_FRAGMENT
	 */
	@Builder.Default
	private final boolean onlyFragment = false;

}
