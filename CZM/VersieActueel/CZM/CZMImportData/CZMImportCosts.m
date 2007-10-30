(* :Title: CZMImportCosts *)

(* :Context: CZMImportData`CZMImportCosts` *)

(* :Author: Rudolf T. Hoogenveen, Roel G.M. Breuls, Pieter van Baal *)

(* :Summary:
   CZM import data routine imports disease costs data*)

(* :Copyright: © 2004 by Rudolf Hoogenveen/Roel Breuls *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004 
		1.1 update by Pieter for 010305 CZM version
		2.0 first release CZM 2005, version March, data update by Pieter
		3.0 version november 2005 
		3.1 version March 2007 *)

(* :Keywords: costs data, import *)


BeginPackage["CZMImportData`CZMImportCosts`",
	{"CZMInitialization`CZMLogFile`",
	"CZMInitialization`CZMDefaultFileNames`",
	"CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`",
	"CZMImportData`CZMImportUserSelections`",
	"CZMImportData`CZMImportDemography`",
	"CZMImportData`CZMImportDiseaseData`"}]

costsperson0::usage	= "costsperson0[[d,g,ai]]: health care use costs per person year per disease, d=29: total costs"
costspatient0::usage	= "costspatient0[[d,g,ai]]: health care use costs per patient year per disease"


Begin["`Private`"]	


Print["CZMImportCosts package is evaluated"]


(* ----------------------------------------------- 
   		INPUT COST DATA
   -----------------------------------------------*)

readfile[dat_, n_] := Block[{},

	OpenRead[dat]; 
	Skip[dat, Record, RecordSeparators -> {{"(*"}, {"*)"}}];
	Skip[dat, Word, 2]; 
      	div = Read[dat, Number];
	Table[Read[dat, {Word, Table[Number, {ng}, {nac[[1]]}]}][[2]] / div, {n}]
	
	];

(* COSTS PER PERSON YEAR *)

dat 		= Global`inputpath <> costspersoninput;
costsperson0 	= readfile[dat, 29];
Close[dat];

(* COSTS PER PATIENT YEAR *)

dat 		= Global`inputpath <> costspatientinput;
costspatient0 	= readfile[dat, 28];
Close[dat];


(* --------------------------------------------------
		Write info to Logfile
----------------------------------------------------*)

(* PACKAGE VERSION *)

version = 3.1;

WriteString[logfile, 
		"          Package: " <> StringReplace[Evaluate[Context[]], "`Private`"-> ", " ] 
		<> "version " <> ToString[version] <> "\n\n"];

End[]


Protect[Evaluate[Context[]<>"*"]]


EndPackage[]
