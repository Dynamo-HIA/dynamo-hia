package nl.rivm.emi.dynamo.data.types.atomic;

import java.util.regex.Pattern;

import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

import org.eclipse.core.databinding.UpdateValueStrategy;

/*
 * Nonnegative Integer without fixed upper limit.
 */
public class UniqueName extends AbstractString implements ContainerType{

	static final protected String XMLElementName = "uniquename";
	public UniqueName() {
		super(XMLElementName);
	}
}
