(* :Title: CZMImportDemography *)

(* :Context: CZMImportData` *)

(* :Author: Rudolf T. Hoogenveen, Roel G.M. Breuls *)

(* :Summary:
   CZM input data routine imports demographic data *)

(* :Copyright: © 2004 by Roel G.M. Breuls/Rudolf Hoogenveen *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March
		3.0 version november 2005 
		3.1 version March 2007 *)


(* :Keywords: demographic data, import *)


BeginPackage["CZMImportData`CZMImportDemography`",
	{"CZMInitialization`CZMLogFile`",
	"CZMInitialization`CZMDefaultFileNames`",
	"CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`"}]


npop0::usage 		= "npop0[[g,a]]: initial population numbers, specified by gender (g=1..ng) and age (a=1..na1)"
npop1::usage 		= "npop1[[g,ai]: initial population numbers, aggregated to 5 year ageclass values."
morttot0::usage		= "mortot0[[g,ai]]: empirical total mortality rates, specified by gender (g) and ageclass (ai=1..nac[2])"

nbirth0::usage 		= "nbirth0[[n,g]]: birth numbers specified by year and gender, specification see input file"

migpop0::usage 		= "migpop0[[n,g,a]]: net migrantion numbers specified by year, gender and age, specification see input file"
migpopyear::usage	= "migpopyear[[n]]: years for which migration data are given"


Begin["`Private`"]


Print["CZMImportDemography package is evaluated"]

(* INITIALIZATION DATA FILE *)

dat		= OpenRead[Global`inputpath <> deminput];
Skip[dat, Record, RecordSeparators->{{"(*"}, {"*)"}}];
Read[dat, Word];


(* POPULATION NUMBERS *)

npop0 		= Table[0, {ng}];
Do[	Read[dat, Word];
	npop0[[g]] = Read[dat, Table[Number, {na1}]],
	{g, ng}];	
npop1 		= Table[aggreg[npop0[[g]], 1], {g, ng}];
npop2 		= Table[aggreg[npop0[[g]], 2], {g, ng}];


(* MORTALITY NUMBERS *)

mort2		= Table[0, {ng}];
Read[dat, Table[Word, {2}]];
div		= Read[dat, Number];
mort2 		= Partition[Read[dat, Table[Number, {ng nac[[2]]}]], nac[[2]]] / div;
morttot0 	= N[Table[aggregc[(mort2 npop2)[[g]], 2, 1] / npop1[[g]], {g, ng}]];


(* SEXRATIO *)

Read[dat, Word];
sexratio 	= Read[dat, Number];
sexratio 	= {sexratio, 1} / (1 + sexratio);


(* NUMBERS OF NEWBORNS *)

Read[dat, Table[Word, {2}]];
ndat		= Read[dat, Number];
nbirthtot 	= Read[dat, Table[Number, {2 ndat}]];
nbirthtot 	= Transpose[Partition[nbirthtot, 2]][[2]];
nbirth0 	= Transpose[Table[nbirthtot sexratio[[g]],{g, ng}]];


(* NET NUMBERS OF MIGRANTS *)

Read[dat, Table[Word, {2}]];
ndat		= Read[dat, Number];
migpop0 	= Read[dat, Table[Number, {ndat (ng na1 + 1)}]];
Close[dat];
migpopyear	= (Transpose[Partition[migpop0, 2na1 + 1]])[[1]];
migpopyear	= migpopyear - migpopyear[[1]] + 1;
migpop0 	= N[Partition[Partition[Flatten[Transpose[Drop[Transpose[Partition[migpop0, ng na1 + 1]], 1]]], na1], ng]];


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
