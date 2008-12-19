package nl.rivm.emi.dynamo.data.structure;

public class TemplateTreeFactoryFactory {
public TemplateTree get(String baseDirectoryAbsolutePath){
	return new TemplateTree(baseDirectoryAbsolutePath);
}
}
