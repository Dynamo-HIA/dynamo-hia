(* :Title: CZMImportRelativeRisks *)

(* :Context: CZMImportData` *)

(* :Author: Rudolf T. Hoogenveen, Roel G.M. Breuls *)

(* :Summary:
   CZM import data routine imports relative risks of risk factors on diseases*)

(* :Copyright: © 2004 by Rudolf Hoogenveen/Roel Breuls *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March, update data
		3.0 version november 2005 
		3.1 version March 2007; import relative risks routine *)

(* :Keywords: relative risks, import *)

BeginPackage["CZMImportData`CZMImportRelativeRisks`",
	{"CZMInitialization`CZMLogFile`",
	"CZMInitialization`CZMDefaultFileNames`",
	"CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`",
	"CZMImportData`CZMImportUserSelections`",
	"CZMImportData`CZMImportRiskFactors`",
	"CZMImportData`CZMImportDiseaseData`"}]

RRdis0::usage 		= "RRdis0[[d,g,a]]: relative risks for one disease on another disease incidence"
RRdisind::usage 	= "RRdisind[[d,d1]]: pointer to elements of vector RRdis"
RRdisinddata::usage 	= "RRdisinddata[[d]]: lists of independent co-morbid diseases"
RRdisinddatasel::usage 	= "RRdisinddatasel[[d]]: lists of independent co-morbid diseases adjusted for risk factors"

RRcasefat0::usage	= "RRcasefat0[[d,g,a]]: relatve risks for one disease on another disease case fatality"
RRcasefatind		= "RRcasefatind[[d,d1]]: pointer to elements of vector RRcasefat"

RRrisk0::usage 		= "RRrisk0[[r,d,g,ri,a]]: relative risks for each risk factor r for each related disease,
				specified by gender (g), risk factor class (ri) and age (a). 
				Only values for related diseases are stored in RRrisk (See also RRiskind0)
				d=1 refers to NO RELATION between the risk factor and the disease (i.e. RR=1) 
				d=2 is associated with all cause mortality. 
				Positions d>=3 contain the RRs for the diseases associated with the risk factor."
RRriskind0::usage 	= "RRiskind0[[r,d]]: the pointer to the index in RRrisk0 for each risk factor r and disease d" 
disriskrelated::usage	= "disriskrelated[[r,d]]: diseases related to each risk factor"
logRRsmokduur0::usage	= "logRRsmokduur[[d,g]]: parameters of log-linear decrease of former smoker RR's with stopping time"
relapsecoeff::usage	= "relapsecoeff[[g]]: parameters of decrease of relapse rates with stopping time"

riskdisprevind::usage	= "pairs of empirical risk factor distributions within disease patients"
riskdisprev0::usage	= "empirical risk factor distributions within disease patients data"

pdisDM::usage		= "empirical disease prevalence rates within diabetics"
pdisDMpair::usage	= "pairs of diseases with empirical disease prevalence rates within diabetics"


Begin["`Private`"]	


Print["CZMImportRelativeRisks package is evaluated"]


(* ----------------------------------------------- 
   INPUT RELATIVE RISKS ROUTINE 
   -----------------------------------------------*)
	
readriskdata[r_] := Block[{},

	Skip[dat, Record, RecordSeparators -> {{"(*"}, {"*)"}}];
	Read[dat, Word];
	Read[dat, Word];
	nd1		= Read[dat, Number];
	RRrisk0[[r]]	= Table[0, {nd1 + 1}, {ng}];
	RRrisk0[[r, 1]] = Table[1, {ng}, {ncr0[[r]]}, {nac[[1]]}];

	Do[	Skip[dat, Word];
		RRriskind0[[r, Read[dat, Number] + 1]] = d;
		Do[	Skip[dat, Word];
			RRrisk0[[r, d + 1, g]] = Partition[Read[dat, Table[Number, {ncr0[[r]] nac[[1]]}]], nac[[1]]], {g, ng}],
		{d, nd1}];

	Close[dat]

	]; (* READRISKDATA *)

(* ----------------------------------------------- 
   EPIDEMIOLOGICAL RISK FACTORS 
   -----------------------------------------------*)

(* SMOKING *)

RRrisk0		= Table[0, {nrd0}];
RRriskind0	= Table[0, {nrd0}, {nd0 + 1}];

dat		= OpenRead[Global`inputpath <> RRsmokinput];
readriskdata[1];

(* CHECK AND ADJUSTMENT: 1 = RR NEVER SMOKERS <= RR FORMER SMOKERS <= RR CURRENT SMOKERS *)

RRrisk0[[1]] 	= Max1[RRrisk0[[1]]]; 
Do[RRrisk0[[1, d, g, 3]] = Minc[RRrisk0[[1, d, g, 2]], RRrisk0[[1, d, g, 3]]],
		{d, Length[RRrisk0[[1]]]}, {g, ng}]; 


(* SYSTOLIC BLOOD PRESSURE *)

dat 		= OpenRead[Global`inputpath <> RRSBPinput]; 
readriskdata[2];


(* TOTAL CHOLESTEROL LEVEL *)

dat 		= OpenRead[Global`inputpath <> RRcholinput]; 
readriskdata[3];


(* BMI *)

dat 		= OpenRead[Global`inputpath <> RRBMIinput]; 
readriskdata[4];


(* PHYSICAL ACTIVITY *)

dat 		= OpenRead[Global`inputpath <> RRlichactinput]; 
readriskdata[5];


(* CONSUMPTION OF ALCOHOL *)

dat 		= OpenRead[Global`inputpath <> RRalcoinput]; 
readriskdata[6];


(* CONSUMPTION OF FATTY ACIDS *)

dat 		= OpenRead[Global`inputpath <> RRverzvetinput]; 
readriskdata[7];


(* CONSUMPTION OF TRANS FATTY ACIDS *)

dat 		= OpenRead[Global`inputpath <> RRtransvetinput]; 
readriskdata[8];


(* CONSUMPTION OF FRUIT *)

dat 		= OpenRead[Global`inputpath <> RRfruitinput]; 
readriskdata[9];


(* CONSUMPTION OF VEGETABLES *)

dat 		= OpenRead[Global`inputpath <> RRgroenteinput]; 
readriskdata[10];


(* CONSUMPTION OF FISH (ACIDS) *)

dat 		= OpenRead[Global`inputpath <> RRvisinput]; 
readriskdata[11];


(* HbA1c *)

dat 		= OpenRead[Global`inputpath <> RRHbA1cinput]; 
readriskdata[12];

++RRriskind0;

disriskrelated	= Table[Flatten[Table[Select[Range[nd0], RRriskind0[[r, # + 1]] == d1 &],
	{d1, 3, Length[RRriskind0[[r]]]}]], {r, 1, nrd0}];



(* ----------------------------------------------- 
   RELATIVE RISKS FOR ONE DISEASE ANOTHER
   -----------------------------------------------*)

dat 		= OpenRead[Global`inputpath <> RRCVDinput]; 
Skip[dat, Record, RecordSeparators -> {{"(*"}, {"*)"}}];
Read[dat, Word];

(* CONSTRUCTION OF CO-MORBIDITY INDICATOR VALUES *)

nd1		= Read[dat, {Word, Number}][[2]];
RRdisinddata	= Table[0, {nd1}];
RRdisinddatasel	= Table[0, {nd1}];
RRdisind 	= Table[1, {nd0}, {nd0}]; 

(* FILLS IN CO-MORBIDITY RATE VALUES *)

RRdis0		= Table[0, {nd1 + 1}];
RRdis0[[1]]	= Table[1, {ng}, {nac[[1]]}];


Do[	RRdisinddata[[d]]	= Read[dat, {Word, Table[Number, {2}]}][[2]];
	RRdisind[[RRdisinddata[[d, 1]], RRdisinddata[[d, 2]]]] = d + 1;
	RRdisinddatasel[[d]]	= Read[dat, {Word, Number}][[2]];
	RRdis0[[d + 1]] 	= Partition[Read[dat, Table[Number, {ng nac[[1]]}]], nac[[1]]],
	{d, nd1}];

Close[dat];

RRdisinddatasel	= Select[Range[nd1] RRdisinddatasel, Positive];


(* ----------------------------------------------- 
   RELATIVE RISKS FOR CASE FATALITY = 1-MONTH MORTALITY
   -----------------------------------------------*)

dat 		= OpenRead[Global`inputpath <> RRcasefatinput]; 
Skip[dat, Record, RecordSeparators -> {{"(*"}, {"*)"}}];
Read[dat, Word];

(* CONSTRUCTION OF CO-MORBIDITY INDICATOR VALUES *)

nd1		= Read[dat, {Word, Number}][[2]];
RRcasefatinddata = Table[0, {nd1}];
RRcasefatind 	= Table[1, {nd0}, {nd0}]; 

(* FILLS IN CO-MORBIDITY RATE VALUES *)

RRcasefat0	= Table[0, {nd1 + 1}];
RRcasefat0[[1]]	= Table[1, {ng}, {nac[[1]]}];


Do[	RRcasefatinddata[[d]]	= Read[dat, {Word, Table[Number, {2}]}][[2]];
	RRcasefatind[[RRcasefatinddata[[d, 1]], RRcasefatinddata[[d, 2]]]] = d + 1;
	RRcasefat0[[d + 1]] 	= Partition[Read[dat, Table[Number, {ng nac[[1]]}]], nac[[1]]],
	{d, nd1}];

Close[dat];


(* ----------------------------------------------- 
   RISK FACTOR AND DISEASE DATA INPUT ROUTINE CONDITIONAL ON DISEASE PREVALENCE, TRANSFORMED TO RELATIVE RISK VALUES, NOT-USED
   -----------------------------------------------*)

If[(userriskdata == 1),
	
	readriskdata1[r_, nac_] := Block[{},
		Skip[dat, Record, RecordSeparators -> {{"(*"}, {"*)"}}];
		Read[dat, Word];

	(* #RISK FACTOR CLASSES *)

		Read[dat, {Word, Number}][[2]];

	(* INITIAL RISK FACTOR CLASS PREVALENCE DATA *)

		prev	= trans = Table[0, {ng}];

		Do[	div 		= N[Read[dat, {Table[Word, {2}], Number}][[2]]];
			prev[[g]] 	= Partition[Read[dat, Table[Number, {ncr0[[r]] nac}]], nac] / div,
			{g, ng}];

		transind	= Max[transriskind0[[r]]] - 1;

	(* RISK FACTOR CLASS TRANSITION RATE DATA, DATA READ, NOT USED *)

		Do[	div = N[Read[dat, {Table[Word, {2}], Number}][[2]]];
			trans[[g]] = Table[Read[dat, Table[Number, {nac}]], {ri, Max[transriskind0[[r]]] - 1}] / div;
			trans[[g]] = Join[{Table[0, {nac}]}, trans[[g]]],
			{g, ng}];

		Close[dat];

		{prev, trans}

		]; (* READRISKDATA1 *)

	readdisdata1 := Block[{},

		Skip[dat, Record, RecordSeparators -> {{"(*"}, {"*)"}}];
		Read[dat, Word];

		hnd	= Read[dat, {Word, Number}][[2]];
		pdisDM	= pdisDMpair = Table[0, {hnd}];
		div	= N[Read[dat, {Word, Number}][[2]]];

		Do[	pdisDMpair[[d]] = Read[dat, Table[Number, {2}]];
			pdisDM[[d]]	= Partition[Read[dat, Table[Number, {ng nac[[1]]}]], nac[[1]]] / div,

			{d, hnd}];

		Close[dat]

		];

	(* EMPIRICAL RISK FACTOR DISTRIBUTIONS WITHIN DISEASE GROUPS, NOT USED *)

	(* BMI & DIABETES *)

	riskdisprevind = {{4, 7}};						
	dat 	= OpenRead[Global`inputpath <> "BmiInputDm280405.txt"];
	WriteString[logfile, "\t" <> "BmiInputDm280405.txt" <> "\n\n"];
	riskdisprev0	= {readriskdata1[riskdisprevind[[1, 1]], nac[[1]]][[1]]};

	(* EMPIRICAL DISEASE PREVALENCE RATES WITHIN DISEASE GROUPS *)

	If[(1 == 0),

		dat = OpenRead[Global`inputpath <> "disinputDM.txt"];
		WriteString[logfile, "\t" <> "disinputDM.txt" <> "\n\n"];
		readdisdata1;

		(* CALCULATED RELATED DISEASE PREVALENCE RATE RATIO *)

		pcomorbB = Table[pdisDM[[d, g]] (1 - pdis0[[pdisDMpair[[d, 2]], g]]) /
					(pdis0[[pdisDMpair[[d, 1]], g]] - pdisDM[[d, g]] pdis0[[pdisDMpair[[d, 2]], g]]),
				{d, Length[pdisDMpair]}, {g, ng}];

		(* ADJUSTMENT OF RELATIVE RISKS *)

		Do[If[(RRdisind[[pdisDMpair[[d, 1]], pdisDMpair[[d, 2]]]] == 1),
	
				(* NEW ASSOCIATION DATA ADDED *)

				RRdis0 = Join[RRdis0, {pcomorbB[[d]]}];
				RRdisind[[pdisDMpair[[d, 1]], pdisDMpair[[d, 2]]]] = Length[RRdis0],

				(* OLD ASSOCIATION DATA OVERWRITEN *)
	
				RRdis0[[RRdisind[[pdisDMpair[[d, 1]], pdisDMpair[[d, 2]]]]]] = pcomorbB[[d]]

				],
			{d, Length[pdisDMpair]}]

		]

	];


(* ----------------------------------------------- 
   PARAMETERS OF LOG-LINEAR MODELS OF DECREASE OF ALL CAUSE MORTALITY AND
   DISEASE INCIDENCE RR's WITH TIME SINCE SMOKING CESSATION
   -----------------------------------------------*)

dat 		= OpenRead[Global`inputpath <> smokduurinput]; 
Skip[dat, Record, RecordSeparators -> {{"(*"}, {"*)"}}];
Read[dat, Word];

logRRsmokduur0	= Table[0, {1 + Plus@@Sign[RRriskind0[[1]] - 1]} ];

logRRsmokduur0[[1]] = Table[0, {ng}, {2}];

Do[	Read[dat, Word];
	logRRsmokduur0[[d]] = Partition[Read[dat, Table[Number, {4}]], 2],
	{d, 2, Length[logRRsmokduur0]}];

Read[dat, Word];
relapsecoeff = Partition[Read[dat, Table[Number, {2 ng}]], 2];

Close[dat];


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
