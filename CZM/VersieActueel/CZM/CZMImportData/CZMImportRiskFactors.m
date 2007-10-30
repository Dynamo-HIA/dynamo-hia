(* :Title: CZMImportRiskFactors *)

(* :Context: CZMImportData` *)

(* :Author: Rudolf T. Hoogenveen, Roel G.M. Breuls *)

(* :Summary:
   CZM import data routine imports initial riskfactor class prevalence 
   and transition rates *)

(* :Copyright: © 2004 by Rudolf Hoogenveen/Roel Breuls *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March, update data
		3.0 version november 2005 
		3.1 version March 2007 *)

(* :Keywords: risk factor data, import *)


BeginPackage["CZMImportData`CZMImportRiskFactors`",
	{"CZMInitialization`CZMLogFile`",
	"CZMInitialization`CZMDefaultFileNames`",
	"CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`",
	"CZMImportData`CZMImportUserSelections`"}]


prisk0::usage 		= "prisk0[[r,g,ri,ai]]: all initial risk factor class prevalence rates" 
priskinc0	::usage = "priskinc0[[r,g,ri,ai]]: all initial risk factor class prevalence rates (new disease cases)"
priskDM0::usage 	= "priskDM0[[r,g,ri,ai]]: all initial risk factor class prevalence rates (within current disease (DM) cases)"

transrisk0::usage 	= "transrisk0[[r,g,ri,ai]]: all initial risk factor class transition rates"
transriskind0::usage	= "transriskind0[[r,ri,rj]]: risk factor class transition indicator values"
ncr0::usage 		= "ncr0[[r]]: number of classes for all risk factors"

riskdispair::usage	= "riskdispair[[r]]: risk factors restricted to diseases"
riskdispairinv::usage	= "riskdispairinv[[r]]: diseases to which risk factors are restricted, see riskdispair"

riskDMpair::usage	= "riskDMpair[[r]]: empirical risk factor distributions within patients"
riskDMpairinv::usage	= "riskDMpairinv[[r]]: diseases for which empirical risk factor distributions are provided, see riskDMpair"


Begin["`Private`"]


Print["CZMImportRiskFactors package is evaluated"]

prisk0 = priskinc0	= Table[0, {nrd0}, {ng}];

ncr0			= Table[0, {nrd0}];
transrisk0		= Table[0, {nrd0}, {ng}];
transriskind0		= Table[1, {nrd0}];


(* ----------------------------------------------- 
   RISK FACTOR DATA INPUT ROUTINE
   -----------------------------------------------*)
	
readriskdata[r_, nac_] := Block[{},
	Skip[dat, Record, RecordSeparators -> {{"(*"}, {"*)"}}];
	Read[dat, Word];

(* #RISK FACTOR CLASSES *)

	ncr0[[r]] = Read[dat, {Word, Number}][[2]];

(* INITIAL RISK FACTOR CLASS PREVALENCE DATA *)

	Do[	div 		= N[Read[dat, {Table[Word, {2}], Number}][[2]]];
		prisk0[[r, g]] 	= Partition[Read[dat, Table[Number, {ncr0[[r]] nac}]], nac] / div,
		{g, ng}];


(* INITIAL RISK FACTOR CLASS PREVALENCE DATA (NEW DISEASE CASES) *)

	If[MemberQ[Transpose[riskdispair][[1]], r],

		Do[	div 		= N[Read[dat, {Table[Word, {2}], Number}][[2]]];
			priskinc0[[r, g]] = Partition[Read[dat, Table[Number, {ncr0[[r]] nac}]], nac] / div,
			{g, ng}]];

(* RISK FACTOR CLASS TRANSITIONS SELECTED *)

	ntrans		= Read[dat, {Word, Number}][[2]];
	transind	= Partition[Read[dat, Table[Number, {2 ntrans}]], 2];

(* RISK FACTOR CLASS TRANSITION RATE DATA *)

	Do[	div 			= N[Read[dat, {Table[Word, {2}], Number}][[2]]];
		transrisk0[[r, g]] 	= Table[Read[dat, Table[Number, {nac}]], {ri, Length[transind]}] / div;
		transrisk0[[r, g]] 	= Join[{Table[0, {nac}]}, transrisk0[[r, g]]],
		{g, ng}];

	Close[dat];

	transriskind0[[r]] = Table[1, {ncr0[[r]]}, {ncr0[[r]]}];

	Do[	transriskind0[[r, transind[[ri, 1]], transind[[ri, 2]]]] = ri + 1, 
		{ri, Length[transind]}]

	]; (* READRISKDATA *)


(* ----------------------------------------------- 
   RISK FACTORS ONLY RELEVANT TO DISEASED POPULATIONS, E.G. HBA1C AND DIABETES
   -----------------------------------------------*)


riskdispair	= {{12, 7}};
riskdispairinv 	= Table[0, {nrd0}];
Do[riskdispairinv[[riskdispair[[r, 1]]]] = riskdispair[[r, 2]], {r, Length[riskdispair]}];


(* SMOKING *)

dat		= OpenRead[Global`inputpath <> smokinput];
readriskdata[1, nac[[1]]];
If[(RRsmokduurind == 1), transrisk0[[1, Range[ng], transriskind0[[1, 2, 3]]]] *= .76];


(* SYSTOLIC BLOOD PRESSURE *)

dat 		= OpenRead[Global`inputpath <> SBPinput]; 
readriskdata[2, nac[[1]]];


(* TOTAL CHOLESTEROL *)
	
dat 		= OpenRead[Global`inputpath <> cholinput];  
readriskdata[3, nac[[1]]];


(* BMI *)
	 
dat 		= OpenRead[Global`inputpath <> BMIinput];
readriskdata[4, nac[[1]]];


(* PHYSICAL ACTIVITY *)

dat 		= OpenRead[Global`inputpath <> lichactinput];
readriskdata[5, na1];


(* CONSUMPTION OF ALCOHOL *)

dat 		= OpenRead[Global`inputpath <> alcoinput];
readriskdata[6, nac[[1]]];


(* CONSUMPTION OF FATTY ACIDS *)

dat 		= OpenRead[Global`inputpath <> verzvetinput];
readriskdata[7, nac[[1]]];


(* CONSUMPTION OF TRANS FATTY ACIDS *)

dat 		= OpenRead[Global`inputpath <> transvetinput];
readriskdata[8, nac[[1]]];


(* CONSUMPTION OF FRUIT *)

dat 		= OpenRead[Global`inputpath <> fruitinput];
readriskdata[9, nac[[1]]];


(* CONSUMPTION OF VEGETABLES *)

dat 		= OpenRead[Global`inputpath <> groenteinput];
readriskdata[10, nac[[1]]];


(* CONSUMPTION OF FISH *)

dat 		= OpenRead[Global`inputpath <> visinput];
readriskdata[11, nac[[1]]];

(* HBA1C *)

dat 		= OpenRead[Global`inputpath <> HbA1cinput];
readriskdata[12, nac[[1]]];


(* ----------------------------------------------- 
   EMPIRICAL RISK FACTOR DISTRIBUTIONS WITHIN PATIENTS
   -----------------------------------------------*)

If[(userriskdata >= 1),


	dat		= OpenRead[Global`inputpath <> riskdisinput];
	skipcomment[dat]; 
        Skip[dat, Word];
	npair 		= Read[dat, Number];
	riskDMpair 	= Table[0, {npair}];
	priskDM0 	= Table[0, {npair}, {ng}];

	Do[	skipcomment[dat];
		riskDMpair[[r]] = Transpose[Read[dat, Table[{Word, Number}, {2}]]][[2]];

		Do[	Skip[dat, String];
			div	= N[Read[dat, {Table[Word, {2}], Number}][[2]]];
			priskDM0[[r, g]] = Partition[Read[dat, Table[Number, {ncr0[[riskDMpair[[r, 1]]]] nac[[1]]}]], nac[[1]]] / div,

			{g, ng}],

		{r, npair}];

	Close[dat];

	riskDMpairinv = Table[{}, {nrd0}];
	Do[riskDMpairinv[[riskDMpair[[r, 1]]]] = Flatten[{riskDMpair[[r, 2]], riskDMpairinv[[riskDMpair[[r, 1]]]]}], {r, npair}]];
	

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
