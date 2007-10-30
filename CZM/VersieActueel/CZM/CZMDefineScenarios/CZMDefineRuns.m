(* :Title: CZMDefineRuns *)

(* :Context: CZMDefineScenarios` *)

(* :Author: Rudolf Hoogenveen *)

(* :Summary:
   CZM simulation routine defines selections made for different CZM runs *)

(* :Copyright: © 2005 by Rudolf Hoogenveen *)

(* :Package Version:	2.0
			3.0 version November 2005
			3.1 version March 2007  *)

(* :Mathematica Version: 5.1 *)

(* :History: 	version August 2005 *)

(* :Keywords: simulation, selections, runs *)


BeginPackage["CZMDefineScenarios`CZMDefineRuns`",
	{"CZMInitialization`CZMLogFile`", 
	"CZMDefineScenarios`CZMDefineRunstest`"}] 


makerun::usage 		= "makerun: routine makes selections for new CZM runs"	


Begin["`Private`"]	


Print["CZMDefineRuns package is evaluated"];

appltype = StringTake[ToString[Global`application], {8, 11}];

makerun := Switch[appltype,

			"test",	makeruntest,
			_,	""
			
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
