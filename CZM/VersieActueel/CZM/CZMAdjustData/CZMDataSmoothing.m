(* :Title: CZMDataSmoothing *)

(* :Context: CZMAdjustData` *)

(* :Author: Rudolf T. Hoogenveen, Roel G.M. Breuls *)

(* :Summary:
   CZM adjust data routine converts numbers specified by 5-year ageclass to values specified by age year *)

(* :Copyright: © 2004 by Rudolf Hoogenveen/Roel Breuls *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March
		3.0 version november 2005
		3.1 version march 2007 *)

(* :Keywords: data smoothing *)


BeginPackage["CZMAdjustData`CZMDataSmoothing`",
	{"CZMInitialization`CZMLogFile`",
	"CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`",
	"CZMImportData`CZMImportUserSelections`",
	"CZMImportData`CZMImportDemography`",
	"CZMImportData`CZMImportRiskFactors`",
	"CZMImportData`CZMImportRelativeRisks`",
	"CZMImportData`CZMImportDiseaseData`",
	"CZMImportData`CZMImportDALYs`",
	"CZMImportData`CZMImportCosts`"}]


morttot1::usage		= "morttot1[[g,a]]: smoothed all cause mortality rates specified by age year (a)" 
mortoth1::usage 	= "mortoth1[[g,a]]: smoothed mortality rates other causes specified by age year (a)"

prisk1pre::usage	= "prisk1pre[[r,g,ri,a]]: smoothed risk factor prevalence rates specified by age year (a)"
priskinc1	::usage	= "priskinc1[[r,g,ri,a]]: smoothed risk factor prevalence rates (new disease cases)"

transrisk1pre::usage	= "transrisk1pre[[r,g,rj,ri,a]]: smoothed risk factor class transition rates specified by age year (a)"

trstracking1::usage	= "trstracking1[[r,g,ri,rj,a]]: smoothed risk factor class transition rates related to tracking"

priskDM1::usage		= "user provided discrete risk factor class prevalence rates for diseased populations"

dist1::usage		= "dist1[r,g,2,a]]: continuous risk factor distribution parameters"

rem1::usage 		= "rem1[[d,g,a]]: smoothed disease remission rates specified by age year (a)"
pdis1pre::usage 	= "pdis1[[d,g,a]]: smoothed initial disease prevalence rates before imputation of missing data"
excessmort1::usage 	= "excessmort1[d,g,a]]: smoothed disease-related excess mortality rates specified by age year (a)"
causemort1::usage 	= "causemort1[[d,g,a]]: smoothed cause-specific mortality rates specified by age year (a)"
inc1::usage 		= "inc1[[d,g,a]]: smoothed disease incidence rates specified by age year (a)"
casefat1::usage		= "casefat1[[d,g,a]]: case fatality rates"

RRrisk1::usage		= "RRrisk1[[r,d,g,ri,a]]: smoothed relative risks for discretely distributed risk factors"
RRcont1::usage		= "RRcont1[[r,d,g,a]]: smoothed relative risks for continuously distributed risk factors"
RRdis1::usage		= "RRdis1[[d,g,a]]: smoothed relative risks of one disease on another disease incidence specified by age year (a)"
RRcasefat1::usage	= "RRcasefat1[[d,g,a]]: smoothed relative risks of one disease on another disease case fatality specified by age"

DALYwgt1::usage		= "DALYwgt1[[d,g,a]]: smoothed DALY weights specified by age year (a)"

costsperson1::usage	= "costsperson1[[d,g,a]]: smoothed average health care use costs per person year"
costspatient1::usage	= "costspatient1[[d,g,a]]: smoothed health care costs per patient year"


Begin["`Private`"]


Print["CZMDataSmoothing package is evaluated"];
printtijd;

printbug[c_] := If[(bugind == 1), Print[{"CZMDataSmoothing", c}]];
	
(* DISCRETELY DISTRIBUTED RISK FACTOR CLASS PREVALENCE RATES *)

printbug["1."];

prisk1pre	= Table[If[(Length[prisk0[[r, g, ri]]] == nac[[1]]),
				Min1[fsmooth[prisk0[[r, g, ri]]]],
				prisk0[[r, g, ri]]
				],
			{r, nrd0}, {g, ng}, {ri, ncr0[[r]]}]; 

(* ADJUSTMENT STEPS:
	1	normalization of prevalence rates
	2	smoking: until age 10 only never smokers *)

printbug["2."];

prisk1pre 	= N[Table[prisk1pre[[r, g, ri]] / Plus@@prisk1pre[[r, g]], {r, nrd0}, {g, ng}, {ri, ncr0[[r]]}]]; 

(* DISCRETELY DISTRIBUTED RISK FACTOR CLASS PREVALENCE RATES (NEW DISEASE CASES) *)

printbug["2."];

priskinc1	= Table[0, {nrd0}];

Do[If[MemberQ[Transpose[riskdispair][[1]], r],

	priskinc1[[r]]	= Table[Min1[fsmooth[priskinc0[[r, g, ri]]]], {g, ng}, {ri, ncr0[[r]]}];
	priskinc1[[r]]	= N[Table[priskinc1[[r, g, ri]] / Plus@@priskinc1[[r, g]], {g, ng}, {ri, ncr0[[r]]}]]],

	{r, nrd0}]; 

(* DISCRETELY DISTRIBUTED RISK FACTOR CLASS TRANSITION RATES *)

printbug["3."];

transrisk1pre 	= Table[If[(Length[transrisk0[[r, g, ri]]] == nac[[1]]),
				fsmooth[transrisk0[[r, g, ri]]],
				transrisk0[[r, g, ri]]
				],
			{r, nrd0}, {g, ng}, {ri, Length[transrisk0[[r, g]]]}];
			
(* RISK FACTOR CLASS TRANSITION RATES RELATED TO TRACKING *)

printbug["4."];

trstracking1	= Table[0, {r, nrd0}, {ng}, {ncr0[[r]]}, {2}, {na1}];

If[(trackingind == 1),

	Do[If[(trstrackingind[[r]] == 1), 

		Do[trstracking1[[r, g, ri, rj]] = fsmooth[trstracking0[[r, g, ri, rj]]], {g, ng}, {ri, ncr0[[r]]}, {rj, 2}]],

		{r, nrd0}]

	];

(* USER PROVIDED DISCRETE RISK FACTOR CLASS PREVALENCE RATES, NOT-USED *)

printbug["5."];

If[(userriskdata >= 1),	priskDM1 = Table[fsmooth[priskDM0[[r, g, ri]]], {r, Length[riskDMpair]}, {g, ng}, {ri, Length[priskDM0[[r, g]]]}]];

(* DISEASE PREVALENCE, INCIDENCE, REMISSION, CASE FATALITY AND EXCESS MORTALITY RATES *)

printbug["6."];

inc1 		= Table[fsmooth[inc0[[d, g]]], {d, nd0}, {g, ng}]; 

rem1 		= Table[fsmooth[rem0[[d, g]]], {d, Length[rem0]}, {g, ng}]; 

pdis1pre	= Table[fsmooth[pdis0[[d, g]]], {d, nd0}, {g, ng}]; 

casefat1 	= Table[fsmooth[casefat0[[d, g]]], {d, Length[casefat0]}, {g, ng}];

excessmort1 	= Table[fsmooth[excessmort0[[d, g]]], {d, nd0}, {g, ng}];

causemort1 	= Table[fsmooth[causemort0[[d, g]]], {d, nd0}, {g, ng}]; 

morttot1 	= Table[fsmooth[morttot0[[g]]], {g, ng}]; 

(* COSTS AND DALY WEIGHTS *)

printbug["9."];

DALYwgt1 	= Table[fsmooth[DALYwgt0[[d, g]]], {d, nd0}, {g, ng}];

costsperson1 	= Table[fsmooth[costsperson0[[d,g]] ], {d, nd0 + 1}, {g, ng}];

costspatient1 	= Table[fsmooth[costspatient0[[d, g]] ], {d, nd0}, {g, ng}];


(* RELATIVE RISKS *)

printbug["10."];

RRrisk1 	= Table[fsmooth[RRrisk0[[r, d, g, ri]]],
			{r, nrd0}, {d, Length[RRrisk0[[r]]]}, {g, ng}, {ri, ncr0[[r]]}]; 

Do[If[(r != 6), RRrisk1[[r]] = Max1[RRrisk1[[r]]]], {r, nrd0}];

RRdis1		= Table[fsmooth[RRdis0[[d, g]]], {d, Length[RRdis0]}, {g, ng}];

RRcasefat1	= Table[fsmooth[RRcasefat0[[d, g]]], {d, Length[RRcasefat0]}, {g, ng}];


(* CONTINUOUS RISK FACTOR DATA *)

printbug["11."];
(*
dist1		= Table[Sign[Plus@@dist0[[r, g, ri]]] fsmooth[Sign[Plus@@dist0[[r, g, ri]]] dist0[[r, g, ri]]],
			{r, Length[dist0]}, {g, ng}, {ri, 2}];

RRcont1		= Table[fsmooth[RRcont0[[r, d, g]]], {r, Length[RRcont0]}, {d, Length[RRcont0[[r]]]}, {g, ng}];
*)
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
