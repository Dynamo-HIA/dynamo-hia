(* :Title: CZMImportDALYs *)

(* :Context: CZMImportData` *)

(* :Author: Rudolf T. Hoogenveen, Roel G.M. Breuls *)

(* :Summary:
   CZM import data routine imports DALY data*)

(* :Copyright: © 2004 by Rudolf Hoogenveen/Roel Breuls *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)
	
(* :History: 	1.0 first update new implementation CZM July 2004
		1.1 September 2004, Pieter added new variable: allcauseDALY0
		2.0 first release CZM 2005, version March
		3.0 version november 2005 
		3.1 version March 2007 *)

(* :Keywords: DALYs, import *)


BeginPackage["CZMImportData`CZMImportDALYs`",
	{"CZMInitialization`CZMLogFile`",
	"CZMInitialization`CZMDefaultFileNames`",
	"CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`",
	"CZMImportData`CZMImportDiseaseData`",
	"CZMImportData`CZMImportUserSelections`"}]


DALYwgt0::usage			= "DALYwgt0[[d,g,ai]]: DALY wgt coefficients of modeled diseases"
nonmodelDALYwgt0::usage 	= "nonmodelDALYwgt0[[d,g,ai]]: DALY wgt coefficients of diseases not modelled in CZM" 
DALYwgtall::usage		= "DALYwgtall[[d,g,ai]]: DALY wgt coefficients of all diseases"


Begin["`Private`"]	


Print["CZMImportDALYs package is evaluated"]

(* INPUT DATA ROUTINE *)

readfile[dat_, n_] := Block[{},

	OpenRead[dat]; 
	Skip[dat, Record, RecordSeparators -> {{"(*"}, {"*)"}}];
	Skip[dat, Word, 2]; 
      	div = Read[dat, Number];
	Table[Read[dat, {Word, Table[Number, {ng}, {nac[[1]]}]}][[2]] / div, {n}]

	];

(* DALY WEIGHTS OF NON-MODELED DISEASES *)

dat			= Global`inputpath <> nonmodelDALYwgtinput;
nonmodelDALYwgt0	= readfile[dat, ndoth];
Close[dat];


(* DALY WEIGHTS OF MODELED DISEASES *)

dat			= Global`inputpath <> DALYwgtinput;
DALYwgt0 		= readfile[dat, 28];
Close[dat];

DALYwgtall		= Join[DALYwgt0, nonmodelDALYwgt0];
	

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
