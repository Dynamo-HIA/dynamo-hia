package nl.rivm.emi.dynamo.data.objects;

import java.util.ArrayList;

public abstract class CategoricalObjectBase {
String[] categoryNames ;

protected CategoricalObjectBase(String[] categoryNames){
	this.categoryNames = categoryNames;
}

public int getNumberOfCategories(){
	return categoryNames.length;
}
}
