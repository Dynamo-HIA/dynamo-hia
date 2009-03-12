package nl.rivm.emi.dynamo.data.types.interfaces;
/**
 * The atomic types used in Dynamo-HIA are divided into ContainerTypes 
 * (that are marked by this marker-interface) and PayloadType-s that 
 * have their own interface.
 *
 * @author mondeelr
 */
public interface WrapperType {
public WrapperType getNextWrapper();
}
