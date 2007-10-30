(* :Title: CZMConstants *)

(* :Context: CZMInitialization` *)

(* :Author: Rudolf T. Hoogenveen, Roel G.M. Breuls *)

(* :Summary:
   CZM initialization routine defines age class distributions and
   several constants *)

(* :Copyright: © 2004 by Roel G.M. Breuls *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March
		3.0 version november 2005 
		3.1 version March 2007 *)

(* :Keywords: initialization of age class distributions *)


BeginPackage["CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMLogFile`"]

ng::usage 		= "number of genders (=2)"
gennames::usage		= "gennames[[g]]: gender names"
eps::usage 		= "small number used in the denominater of divisions to prevent dividing by zero" 
na::usage 		= "number of age years (= 85)"
na1::usage		= "number of age years ( = na+1 = 86) "
na2::usage 		= "age at which there are no survivers (120), which is used for life table calculations"
nac::usage 		= "nac[[c]]: number of ageclasses for each ageclass distribution defined"
initageclass::usage 	= "initageclass[[c,ai]]: 1st years per ageclass per ageclass distribution"
lengthageclass::usage 	= "lengthageclass[[c,ai]]: number of years per ageclass per ageclass distribution"
ageyearclass::usage 	= "ageyearclass[[c,a]]: for each age-year the ageclass to which it belongs"


Begin["`Private`"]


Print["CZMConstants package is evaluated"];

(* MISCELLANEOUS *)

ng	= 2;
gennames = {"males ", "females "}; 

eps	= 0.000000000001;

(* AGECLASS DISTRIBUTION DATA *)

na	= 85;
na1	= na + 1;
na2	= 120;
nac	= {18, 20, 9, 5, 8, 6, 4};

initageclass = Partition[Flatten[{
		{5 Range[nac[[1]]] - 5, Table[na1, {21 - nac[[1]]}]}, 
        	{0, 1, 2, 5 Range[17], na1},
		{10 Range[9] - 10, Table[na1, {21 - nac[[3]]}]}, 
		{0, 5, 15, 45, 60, Table[na1, {21 - nac[[4]]}]}, 
		{0, 1, 15, 25, 45, 65, 75, 85, Table[na1, {21 - nac[[5]]}]},
		{0, 30 + 10 Range[5], Table[na1, {21 - nac[[6]]}]},
		{0, 40, 60, 80, Table[na1, {21 - nac[[7]]}]}}],
		21]; 

lengthageclass = Table[Drop[initageclass[[c]], 1] - Drop[initageclass[[c]], -1], {c, Length[nac]}]; 

ageyearclass = Partition[	Flatten[Table[Range[nac[[c]]][[ai]], {c, Length[nac]}, {ai, nac[[c]]}, {lengthageclass[[c, ai]]}]],
				na1];


(* --------------------------------------------------
		Write info to Logfile
----------------------------------------------------*)

(* PACKAGE VERSION *)

version = 3.1;

WriteString[logfile, 
		"\t" <> "Package: " <> StringReplace[Evaluate[Context[]], "`Private`"-> ", " ] 
		<> "version: " <> ToString[version] <> "\n\n"];

End[]

Protect[Evaluate[Context[] <> "*"]]


EndPackage[]
