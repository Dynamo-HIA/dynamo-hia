(* :Title: CZMExportUserSelections *)

(* :Context: CZMPostProcessing` *)

(* :Author: Rudolf  T. Hoogenveen, Roel G.M. Breuls *)

(* :Summary:
   CZM package with functions to import the user selections of input*)

(* :Copyright: © 2004 by Rudolf Hoogenveen/Roel Breuls *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March, update menus
		3.0 version November 2005 
		3.1 version March 2007 *)

(* :Keywords: user selections, import *)

BeginPackage["CZMPostProcessing`CZMExportUserSelections`",
	{"CZMInitialization`CZMLogFile`",
	"CZMInitialization`CZMDefaultFileNames`",
	"CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`",
	"CZMImportData`CZMImportUserSelections`"}]


outputnames::usage		= "names of output variables"
outputsel::usage		= "output variables selected"
noutput::usage			= "number of output variables available"

rates::usage			= "absolute numbers (0) or rates (1)"
standardized::usage		= "standardized no (0) or yes (1)"
agespecres::usage		= "presentation of model results specified by age class (>1) or not (<=1)"
withindisease::usage		= "presentation of model results within disease groups"

cumulative::usage		= "yearly (0) or cumulative (1) figures"
discountc::usage		= "cost discounting factor applied to cumulative figures"
discounte::usage		= "effect discounting factor applied to cumulative figures"
disweighting::usage		= "weighting of disease states applied to cumulative figures"
otherdis::usage			= "inclusion of other diseases applied to cumulative figures"
heterogeneity::usage		= "assumption  of homogeneous (0) or heterogeneous (1) population in DALY calculations"

outputscreen::usage		= "presentation of model output on screen (1) or not (0)"
outputnotebook::usage		= "presentation of model output in notebook (1) or not (0)"
outputfile::usage		= "presentation of tabular output in ASCII file (1) or not (0)"
tabeloutput::usage		= "presentation of tabular output (1) or not (0)"
graphicoutput::usage		= "presentation of graphical output (1) or not (0)"


Begin["`Private`"]	


Print["CZMExportUserSelections package is evaluated"]


(* -----------------------------------------------
           OUTPUT VARIABLES
   -----------------------------------------------*)

input = ReadList[Global`inputpath <> useroutput, Word, WordSeparators -> {"/t", "="}];

(* s0 to s3 denote the position of the KEYWORDS in the userinput file *)

s0	= Flatten[Position[input, "OUTPUT VARIABLES SELECTED"]][[1]];
s1	= Flatten[Position[input, "OUTPUT SPECIFICATION"]][[1]];
s2	= Flatten[Position[input, "COMBINING LIFE YEARS"]][[1]];
s3	= Flatten[Position[input, "OUTPUT DEVICE SPECIFICATION"]][[1]];
s4 	= Flatten[Position[input, "END"]][[1]];

outputnames 	= Take[input, {s0 + 1, s1 - 1, 2}]; 
outputsel	= ToExpression[Take[input, {s0 + 2, s1 - 1, 2}]];

constants 	= ToExpression[Take[input, {s1 + 2, s2 - 1, 2}]];

rates		= constants[[1]];
standardized 	= constants[[2]];
agespecres	= constants[[3]];
withindisease	= constants[[4]];
If[(patientsel > 0) && (Mod[patientsel, 10] == 0),
	Print["results presented within disease in case of population restricted to disease (thus: withindisease = 1)"];
	withindisease = 1];								(* DEFAULT SELECTION *)

outputnames	= Join[outputnames, {"\t"<> "joint class prevalence numbers"}];
outputsel	= Join[outputsel, {1}];
noutput		= Length[outputnames];

If[(Length[agesel] > 1),
	Print["only standardization in case of selecting one cohort (thus: standardized = 0)"];
	standardized = 0];								(* DEFAULT SELECTION *)
If[(standardized == 1),
	Print["presentation of absolute total numbers in case of standardization (thus: agespecres = rates = 0)"];
	agespecres = 0; rates = 0];							(* DEFAULT SELECTION *)

constants 	= ToExpression[Take[input, {s2 + 2, s3 - 1, 2}]];

cumulative 	= 0;
discountc 	= 0;
discounte 	= 0;
disweighting 	= constants[[1]];
otherdis	= 0;
heterogeneity	= 0;

If[(discountc > eps) || (discounte > eps), rates = 0;
	Print["no presentation of rates in case of discounting (thus: rates = 0)"];
	cumulative = 1];								(* DEFAULT SELECTION *)

constants 	= ToExpression[Take[input, {s3 + 2, s4 - 1, 2}]];

outputnotebook	= constants[[1]];
outputfile	= constants[[2]];
tabeloutput	= constants[[3]];
graphicoutput	= constants[[4]];
outputscreen	= 0;

If[(graphicoutput == 1),
	Print["presentation in notebook in case of graphical output (thus: outputnotebook = 1)"];
	outputnotebook 	= 1];								(* DEFAULT SELECTION *)
If[(outputfile == 1),
	Print["presentation in table format in case of ASCII file output (thus: tabeloutput = 1)"];
	tabeloutput 	= 1];								(* DEFAULT SELECTION *)
If[(nstap < 2),
	Print["no presentation for separate runs if nstap < 2 (thus: outputfile = 0, outputnotebook = 0)"];
	outputfile	= 0;
	outputnotebook	= 0];								(* DEFAULT SELECTION *)
	

(* --------------------------------------------------
		Write info to Logfile
----------------------------------------------------*)

(* PACKAGE VERSION *)

version = 3.1;

WriteString[logfile, 
		"\t" <> "Package: " <> StringReplace[Evaluate[Context[]], "`Private`"-> ", " ] 
		<> "version " <> ToString[version] <> "\n\n"];

WriteString[logfile,

		"Output Variables Selected\n\n",
		
		Table[outputnames[[Select[Range[noutput] outputsel, Positive]]][[t]] <> "\n", {t, Plus@@outputsel}],
		"\n",

	"Output Specification\n\n",
		"\t" <> "rates no (0) or yes (1): " <> ToString[rates] <> "\n",
		"\t" <> "standardized no (0) or yes (1): " <> ToString[standardized] <> "\n",
		"\t" <> "specification by age: " <> ToString[agespecres] <> "\n\n",
		
	"Life Expectancy Weighting\n\n",
		"\t" <> "cumulative no (0) or yes (1): " <> ToString[cumulative] <> "\n",
		"\t" <> "effect discounting factor: " <> ToString[discounte] <> "\n",
		"\t" <> "cost discounting factor: " <> ToString[discountc] <> "\n",
		"\t" <> "disease status weighting: " <> ToString[disweighting] <> "\n",
		"\t" <> "other diseases included: " <> ToString[otherdis] <> "\n",
		"\t" <> "heterogeneous population: " <> ToString[heterogeneity] <> "\n\n",

	"Output Device Specification\n\n",
		"\t" <> "output to screen: "	<> ToString[outputscreen] <> "\n",
		"\t" <> "output to notebook: " 	<> ToString[outputnotebook] <> "\n",
		"\t" <> "output to file: " 	<> ToString[outputfile] <> "\n",
		"\t" <> "output in table form: " <> ToString[tabeloutput] <> "\n",
		"\t" <> "output in graphical form: " <> ToString[graphicoutput] <> "\n\n"
		
]; 

End[]

Protect[Evaluate[Context[] <> "*"]]


EndPackage[]
