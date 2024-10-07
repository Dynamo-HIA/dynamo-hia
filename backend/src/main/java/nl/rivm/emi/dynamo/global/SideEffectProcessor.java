package nl.rivm.emi.dynamo.global;

/**
 * Class that does some processing. The result is reached through side-effects
 * only, it accepts no parameters and returns no result.
 * 
 * @author mondeelr
 * 
 */
public interface SideEffectProcessor {
	public boolean doIt();
}
