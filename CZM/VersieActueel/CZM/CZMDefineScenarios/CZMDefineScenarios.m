(* :Title: CZMDefineScenarios *)

(* :Context: CZMDefineScenarios` *)

(* :Author: Rudolf Hoogenveen *)

(* :Summary:
   CZM simulation routine defines scenarios used for sensitivity analyses *)

(* :Copyright: © 2004 by Rudolf Hoogenveen *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March
		3.0 version november 2005
		3.1 version March 2007 *)

(* :Keywords: model equations, simulation, sensitivity analyses *)


BeginPackage["CZMDefineScenarios`CZMDefineScenarios`",
	{"CZMInitialization`CZMLogFile`", 
	"CZMImportData`CZMImportUserSelections`",
	"CZMDefineScenarios`CZMDefineScenariosexem`",
	"CZMDefineScenarios`CZMDefineScenariossens`"}] 

makescen::usage 	= "makescen: routine calculates parameter values for each scenario"
deltasens::usage	= "deltasens: relative change of input parameter values used for sensitivity analyses"


Begin["`Private`"]	


Print["CZMDefineScenarios package is evaluated"];
printtijd;

printbug[c_] := If[(bugind == 1), Print[{"CZMDefineScenarios", c}]];

deltasens = .001;

makescen := Switch[analyse,

			0,	makescenexem,
			1,	makescensens,
			3,	makescenexem
			
			];


(* --------------------------------------------------
		Write info to Logfile
----------------------------------------------------*)

(* PACKAGE VERSION *)

version = 3.1;

WriteString[logfile, 
		"\t" <> "Package: " <> StringReplace[Evaluate[Context[]], "`Private`"-> ", " ] 
		<> "version " <> ToString[version] <> "\n\n"];

End[]


Protect[Evaluate[Context[] <> "*"]]


EndPackage[]
