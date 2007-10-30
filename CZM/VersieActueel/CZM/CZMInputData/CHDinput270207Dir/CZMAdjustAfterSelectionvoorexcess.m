(* :Title: CZMAdjustAfterSelection *)

(* :Context: CZMAdjustData` *)

(* :Author: Rudolf T. Hoogenveen *)

(* :Summary:
   CZM adjust data routine adjusts the relative risks for intermediate diseases,
   and calculates other causes mortality risks *)

(* :Copyright: \[Copyright] 2004 by Rudolf Hoogenveen *)

(* :Package Version: 2.0 *)

(* :Mathematica Version: 5.0 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March, extra adjustments on excess mortality & RR's
		3.0 version november 2005 *)

(* :Keywords: data adjustment, Relative Risks, intermediate diseases, other causes mortality *)


BeginPackage["CZMAdjustData`CZMAdjustAfterSelection`",
	{"CZMInitialization`CZMLogFile`",
	"CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`",
	"CZMImportData`CZMImportUserSelections`",
	"CZMImportData`CZMImportDemography`",
	"CZMImportData`CZMImportRiskFactors`",
	"CZMImportData`CZMImportRelativeRisks`",
	"CZMImportData`CZMImportDiseaseData`",
	"CZMAdjustData`CZMDataSmoothing`",
	"CZMAdjustData`CZMAdjustBeforeSelection`",
	"CZMAdjustData`CZMMakeSelections`",
	"CZMPostProcessing`CZMExportUserSelections`"}]


makenbirth::usage	= "makenbirth[n]: calculates birth numbers for simulation year n"
makemigpop::usage	= "makemigpop[n]: calculates net migrantion numbers for simulation year n"

RRriskseladj::usage	= "RRriskseladj[[r,d,g,ri,a]]: adjusted (discrete) RR values after selection of risk factors and diseases"
RRcontseladj::usage	= "RRcontseladj[[r,d,g,ri,a]]: adjusted (continuous) RR values after selection of risk factors and diseases"
RMothrisksel::usage 	= "RMothrisksel[[r,g,ri,a]]: adjusted other causes (discrete) RR values after selection of risk factors and diseases"
RMothcontsel::usage 	= "RMothcontsel[[r,g,ri,a]]: adjusted other causes (continuous) RR values after selection of risk factors and diseases"

excessmortseladj::usage	= "excessmortseladj[[d,g,a]]: disease-related excess mortality rates adjusted for co-morbidity after selections made"
mortothsel::usage	= "mortothsel[[g,a]]: other causes mortality rates given adjusted disease excess mortality rates"

le::usage		= "le[[g,a]]: (rest) life expectancy"
de::usage		= "de[[d,g,a]]: disease duration of new case"
costse::usage		= "costse[[d,g,a]]: costs during disease duration of new case"
dewgtdis::usage		= "dewgtdis[[d,g,a]]: DALY weights lost during disease duration of new case"
dewgtmort::usage	= "dewgtmort[[d,g,a]]: DALY weigthts ( = life years) lost after disease duration of new case"
RRdisadj::usage		= "RRdisadj[[d,g,a]]: disease incidence risks for one disease on another adjusted for intermediate diseases"
ERRdisadj::usage	= "ERRdisadj[[d,g,a]]: mean disease incidence risk for one disease on another adjusted for intermediate	diseases"
RRdisprev::usage	= "RRdisprev[[d1,d2,g,a]]: disease prevalence risks for one disease on another adjusted for intermediate diseases"
dispair::usage		= "dispair[[d]]: given pairs of causally related co-morbid diseases, see also dispath"
dispath::usage		= "dispath[[d]]: calculated ordering of causally related diseases, see dispair"

disduur::usage		= "disduur[[d,g,a]]: past disease duration of prevalent disease cases"

logRRsmokduur::usage	= "logRRsmokduur[[d,g]]: parameters of log-linear decrease of former smoker RR's with stopping time"
logRRsmokduuroth::usage	= "logRRsmokduuroth[[g]]: parameters of decrease of other causes mortality RR's with smoking cessation time"
RMothsmok::usage	= "RMothsmok[[g,ri,a]]: cessation-time dependent other causes mortality rate multipliers"

RMriskDMinc	::usage = "RMriskDMinc[[d,r,g,ri]]: relative incidence risks corresponding to emprical prevalence rates for patients"
makedrisk	::usage = "routine calculates state transition matrix that corresponds with change of risk factor prevalence rates"
transriskDM	::usage	= "transriskDM[[d,r,g,a,ri,rj]]: transition rates between classes corresponding to differences between \n
				prevalence rates among non-diabetics and diabetics"

Begin["`Private`"]	


Print["CZMAdjustAfterSelection package is evaluated"];
printtijd;

printbug[c_] := If[(bugind == 1), Print[{"CZMAdjustAfterSelection", c}]];


(* --------------------------------------------------
		DEMOGRAPHIC PROCEDURES
----------------------------------------------------*)

(* CALCULATION OF BIRTH NUMBERS FOR SIMULATION YEAR n GIVEN VALUES TO YEAR ndat, AFTERWARDS ASSUMED CONSTANT *)

printbug["1."];

makenbirth[n_]	:= birthind nbirth0[[Min[{n, Length[nbirth0]}]]];

(* CALCULATION OF NET MIGRATION NUMBERS FOR SIMULATION YEAR n *)
(* GIVEN VALUES TO YEAR Length[migpopyear]-1, THEN LINEAR INTERPOLATION TO YEAR Length[migpopyear], AFTERWARDS ASSUMED CONSTANT *)

makemigpop[n_]	:= migpopind If[( n >= migpopyear[[Length[migpopyear]]]),

			migpop0[[Length[migpopyear]]],

  			If[( n > migpopyear[[Length[migpopyear] - 1]]),

				((n - migpopyear[[Length[migpopyear] - 1]]) migpop0[[Length[migpopyear]]] +
					(migpopyear[[Length[migpopyear]]] - n) migpop0[[Length[migpopyear] - 1]]) /
					(migpopyear[[Length[migpopyear]]] - migpopyear[[Length[migpopyear] - 1]]),

				migpop0[[n]]

				]

			];


(* --------------------------------------------------
		INITIALIZATION
----------------------------------------------------*)

(* DISCRETELY DISTRIBUTED RISK FCTORS *)

printbug["2."];

RRriskseladj 	= RRrisksel;

ERRriskseladj 	= Table[Plus@@(RRriskseladj[[r, d, g]] prisksel[[r, g]]),
			{r, nrd}, {d, Length[RRriskseladj[[r]]]}, {g, ng}]; 

RMriskseladj 	= Table[RRriskseladj[[r, d, g, ri]] / (ERRriskseladj[[r, d, g]] + eps),
			{r, nrd}, {d, Length[RRriskseladj[[r]]]}, {g, ng}, {ri, ncrsel[[r]]}];

(* CONTINUOUSLY DISTRIBUTED RISK FACTORS *)


(* --------------------------------------------------
	CONSTRUCTION OF CAUSALITY PATH BETWEEN DISEASES
----------------------------------------------------*)

printbug["3."];

If[(nd > 0),

	(* ADJACENCY MATRIX BETWEEN DISEASES *)

	adjmatrix 	= Sign[RRdisind[[disind, disind]] - 1];

	(* PAIRS OF CAUSALLY RELATED DISEASES *)

	dispair0 = {};
	
	Do[If[(adjmatrix[[d, d1]] == 1), dispair0 = Join[dispair0, {{d, d1}}]], {d, nd}, {d1, nd}];

	(* ORDERING OF DISEASE PAIRS BY METHOD OF LABELING *)

	dislabel = Table[0, {nd}];
	
	Do[dislabel[[dispair0[[d, 2]]]] = Max[{dislabel[[dispair0[[d, 2]]]], dislabel[[dispair0[[d, 1]]]] + 1}],
		{Length[dispair0]}, {d, Length[dispair0]}];
	dispair = {};
	
	Do[If[(dislabel[[dispair0[[d, 1]]]] == lab1) && (dislabel[[dispair0[[d, 2]]]] == lab2), 
			dispair = Join[dispair, {dispair0[[d]]}]],
		{lab2, Max[dislabel]}, {lab1, lab2 - 1, 0, -1}, {d, Length[dispair0]}];

	(* DISEASES ORDERED BY LABEL *)

	dispath	= Transpose[Sort[Transpose[{dislabel, Range[nd]}]]][[2]],

	dispair = {};
	dispath = {}

	];


(* --------------------------------------------------
 ADJUSTMENT OF INCIDENCE RR'S BETWEEN CO-MORBID DISEASES FOR JOINT RISK FACTORS
i.e. using the risk multipliers specified by disease and selected risk factors
----------------------------------------------------*)

printbug["4."];

RRdisadj	= RRdispresel;

Do[If[(RRdisindsel[[d, d1]] > 1),

	(* CAUSAL (INDEPENDENT) DISEASE d CAUSES (DEPENDENT) DISEASE d1 *)

	Do[RRdisadj[[RRdisindsel[[d, d1]], g]] /=

		Times@@Table[
			Plus@@(	RMriskseladj[[r, RRriskindsel[[r, d + 1]], g]] *
				RMriskseladj[[r, RRriskindsel[[r, d1 + 1]], g]] *
				prisksel[[r, g]]),
			{r, nrdpop}],

		{g, ng}]],

	{d, nd}, {d1, nd}];


(* --------------------------------------------------
 ADJUSTMENT OF INCIDENCE RR's BETWEEN CO-MORBID DISEASES FOR INTERMEDIATE DISEASES
i.e. adjustment based on the causal relations between the diseases described by dispair
----------------------------------------------------*)

printbug["5."];

Do[If[MemberQ[Drop[dispair, d1], {dispair[[d1, 1]], dispair[[d2, 2]]}] &&
		(dispair[[d1, 2]] == dispair[[d2, 1]]),

	dA	= dispair[[d1,1]];				(* CAUSAL (INDEPENDENT) DISEASE *)
	dB	= dispair[[d1,2]];				(* INTERMEDIATE DISEASE *)
	dC	= dispair[[d2,2]];				(* DEPENDENT DISEASE *)

	RRdisadj[[RRdisindsel[[dA, dC]]]] *=

		(1 + (RRdisadj[[RRdisindsel[[dA, dB]]]] - 1) pdissel[[dA]] + 
			(RRdisadj[[RRdisindsel[[dB, dC]]]] - 1) pdissel[[dB]]) /
		(1 + (RRdisadj[[RRdisindsel[[dA, dB]]]] - 1) pdissel[[dA]] + 
			(RRdisadj[[RRdisindsel[[dB, dC]]]] - 1) RRdisadj[[RRdisindsel[[dA, dB]]]] pdissel[[dB]])
	],

	{d1, Length[dispair] - 1}, {d2, d1 + 1, Length[dispair]}];

(* INCIDENCE RATE MULTIPLIERS BETWEEN CO-MORBID DISEAES CALCULATED FROM RELATIVE RISKS*)

ERRdisadj	= 0 RRdisadj;

Do[ERRdisadj[[RRdisindsel[[dispair[[d, 1]], dispair[[d, 2]]]]]] =
	1 + (RRdisadj[[RRdisindsel[[dispair[[d, 1]], dispair[[d, 2]]]]]] - 1) pdissel[[dispair[[d, 1]]]],
	{d, Length[dispair]}];


(* --------------------------------------------------
 ADJUSTMENT OF RELATIVE RISKS OF RISK FACTORS FOR INTERMEDIATE DISEASES
----------------------------------------------------*)

printbug["6."];

Do[	d1 	= dispair[[d, 1]];				(* INTERMEDIATE DISEASE *)
	d2 	= dispair[[d, 2]];				(* DEPENDENT DISEASE *)

	(* DISCRETELY DISTRIBUTED RISK FACTORS *)

	Do[	r1 	= RRriskindsel[[r, d1 + 1]];		(* POINTER TO RR VALUES WITH RESPECT TO DISEASE D1 *)
		r2 	= RRriskindsel[[r, d2 + 1]];		(* POINTER TO RR VALUES WITH RESPECT TO DISEASE D2 *)

		If[(r1 > 1) && (r2 > 1),

			(* BOTH DISEASES d1 AND d2 HAVE JOINT RISK FACTOR r *)
			
			RRriskseladj[[r, r2]] /=
				Table[(1 + (RRdisadj[[RRdisindsel[[d1, d2]], g]] - 1) *
					pdissel[[d1, g]] (RRriskseladj[[r, r1, g, ri]] - 1) /
					(ERRriskseladj[[r, r1, g]] + (RRdisadj[[RRdisindsel[[d1, d2]], g]] - 1) pdissel[[d1, g]])),
					{g, ng}, {ri, ncrsel[[r]]}]; 

	    		Do[RRriskseladj[[r, r2, g, ri]] /= RRriskseladj[[r, r2, g, 1]], {g, ng}, {ri, ncrsel[[r]]}];

			Do[ERRriskseladj[[r, r2, g]] = Plus@@(RRriskseladj[[r, r2, g]] prisksel[[r, g]]), {g, ng}];

			Do[RMriskseladj[[r, r2, g, ri]] = RRriskseladj[[r, r2, g, ri]] / ERRriskseladj[[r, r2, g]],
					{g, ng}, {ri, ncrsel[[r]]}]

			],

		{r, nrdpop}],

	(* CONTINUOUSLY DISTRIBUTED RISK FACTORS *)

	{d, Length[dispair]}];


(* --------------------------------------------------
 CALCULATION OF CO-MORBIDITY RATES, DEFAULT METHOD 1, MULTIPLYING DISEASE INCIDENCE RATE RATIOS
----------------------------------------------------*)

printbug["7."];

(* CO-MORBIDITY PREVALENCE RATES ADJUSTED FOR JOINT RISK FACTORS *)

RRdisprev	= Table[1, {nd}, {nd}, {ng}, {na1}];

(* INITIALIZATION: EQUAL TO INCIDENCE RISKS, OR TRANSFORMED ONE FOR REVERSED RELATION *)
 
Do[If[(RRdisindsel[[d, d1]] > 1), RRdisprev[[d, d1]] = RRdisadj[[RRdisindsel[[d, d1]]]]], {d, nd}, {d1, nd}];

printbug["7.1"];

(* ADJUSTMENT STEP 1: FOR INTERMEDIATE DISEASE d1 BETWEEN d AND d2, E.G. CHD BETWEEN DM AND CHF *)

Do[If[(RRdisindsel[[d, d1]] > 1) && (RRdisindsel[[d1, d2]] > 1),

		RRdisprev[[d, d2]] *= 

			(1 + (RRdisadj[[RRdisindsel[[d, d1]]]] - 1) pdissel[[d]] +
				(RRdisadj[[RRdisindsel[[d1, d2]]]] - 1) RRdisadj[[RRdisindsel[[d, d1]]]] pdissel[[d1]]) /
			(1 + (RRdisadj[[RRdisindsel[[d, d1]]]] - 1) pdissel[[d]] +
				(RRdisadj[[RRdisindsel[[d1, d2]]]] - 1) pdissel[[d1]])
		],

	{d, nd}, {d2, nd}, {d1, nd}];

printbug["7.2"];

(* ADJUSTMENT STEP 2: FOR DISEASES d1 AND d2 WITH JOINT CAUSAL DISEASE d, E.G. CHD AND CVA WITH CAUSAL DISEASE DM *)

Do[If[(RRdisindsel[[d, d1]] > 1) && (RRdisindsel[[d, d2]] > 1) && (d1 != d2),

		RRdisprev[[d1, d2]] *= 

			(1 - pdissel[[d1]]) *
			(1 + (RRdisadj[[RRdisindsel[[d, d1]]]] RRdisadj[[RRdisindsel[[d, d2]]]] - 1) pdissel[[d]]) /
			( (1 + (RRdisadj[[RRdisindsel[[d, d1]]]] - 1) pdissel[[d]]) *
				(1 + (RRdisadj[[RRdisindsel[[d, d2]]]] - 1) pdissel[[d]]) -
			(1 - (RRdisadj[[RRdisindsel[[d, d1]]]] RRdisadj[[RRdisindsel[[d, d2]]]] - 1) pdissel[[d]]) *
				pdissel[[d1]])
		],

	{d1, nd}, {d2, nd}, {d, nd}];

printbug["7.3"];

(* --------------------------------------------------
 CALCULATION OF CO-MORBIDITY RATES, METHOD 2, 2x2 DISEASE STATE-TRANSITION MODEL, OPTIONAL
----------------------------------------------------*)

If[(0 == 1),

(* CO-MORBIDTY PREVALENCE RATES UNADJUSTED FOR JOINT RISK FACTORS *)

(* ADJUSTMENT STEP 3: CO-MORBIDITY THROUGH JOINT RISK FATORS *)

RRdisprevmarg	= RRdisprev;

Do[RRdisprevmarg[[d, d1, g]] *=

		Times@@Table[
			Plus@@(	RMriskseladj[[r, RRriskindsel[[r, d + 1]], g]] *
				RMriskseladj[[r, RRriskindsel[[r, d1 + 1]], g]] *
				prisksel[[r, g]]),
			{r, nrdpop}],

		{d, nd}, {d1, nd}, {g, ng}];

(* ADJUSTMENT STEP 4: SYMMETRIC COMORBIDITY RATIOS *)

comorbratio 	= Table[RRdisprevmarg[[d1, d2]] / ( 1 + (RRdisprevmarg[[d1, d2]] - 1) pdissel[[d1]] ), {d1, nd}, {d2, nd}];

Do[comorbratio[[d, d]] = 1 + 0 comorbratio[[d, d]], {d, nd}];

comorbratio	= Maxc[comorbratio, Transpose[comorbratio]];

(* DISEASE INCIDENCE RATE RATIOS *)

RRdisinc	= RRdisprevmarg;

comorbratio	= Table[1, {nd}, {nd}, {ng}, {na1}];

Do[	(* CURRENT DISEASE PAIR *)

	dis2		= {d1, d2};

	RRdis2		= {RRdisinc[[d1, d2]], RRdisinc[[d2, d1]]};

	ERRdis2		= Table[1 + (RRdis2[[d]] - 1) pdissel[[dis2[[d]]]], {d, 2}];

	(* REMISSION, ALL AND 1-YEAR POPULATION DISEASE INCIDENCE RATE *)

	hrem		= Table[rem1[[remindsel[[dis2[[d]]]]]], {d, 2}];

	hinc		= incsel[[dis2]] / (1 - pdissel[[dis2]]);

	hincsurv	= hinc (1 - casefat1[[casefatindsel[[dis2]]]]) (1 - .5 excessmortsel[[dis2]]);

	hexcessmort	= excessmortsel[[dis2]];

	(* STATE-TRANSITION PROBABILITY MATRIX *)

	trans		= Table[{	{- hinc[[2, g]] / ERRdis2[[1, g]] - hinc[[1, g]] / ERRdis2[[2, g]],
						hincsurv[[2, g]] / ERRdis2[[1, g]],
						hincsurv[[1, g]] / ERRdis2[[2, g]],
						vect00},
					{hrem[[2, g]],
						- hexcessmort[[2, g]] - hinc[[1, g]] RRdis2[[2, g]] / ERRdis2[[2, g]] - hrem[[2, g]],
						vect00,
						hincsurv[[1, g]] RRdis2[[2, g]] / ERRdis2[[2, g]]},
					{hrem[[1, g]],
						vect00,
						- hexcessmort[[1, g]] - hinc[[2, g]] RRdis2[[1, g]] / ERRdis2[[1, g]] - hrem[[1, g]],
						hincsurv[[2,g]] RRdis2[[1, g]] / ERRdis2[[1, g]]},
					{vect00,
						hrem[[1, g]],
						hrem[[2, g]],
						- hexcessmort[[1, g]] - hexcessmort[[2, g]] - hrem[[1, g]] - hrem[[2, g]]}},
				{g, ng}];

	trans		= Transpose[trans, {1, 3, 4, 2}];

	mortoth		= morttot1 - Plus@@(pdissel[[dis2]] hexcessmort + incsel[[dis2]] casefat1[[casefatindsel[[dis2]]]]);

	trans		= Table[DiagonalMatrix[Table[1 - mortoth[[g, a]], {4}]] + trans[[g, a]], {g, ng}, {a, na1}];

	(* DISEASE STATE PREVALENCE RATES CALCULATED BY LIFE-TABLE METHOD *)

	prevcomorb	= Table[0, {ng}, {na1}, {4}];

	prevcomorb[[Range[ng], 1, 1]] = 1;

	Do[prevcomorb[[g, a + 1, d3]] = Plus@@Table[prevcomorb[[g, a, d4]] trans[[g, a, d4, d3]], {d4, 4}],
				{g, ng}, {a, na1 - 1}, {d3, 4}];

	prevcomorb	= Table[prevcomorb[[g, a]] / Plus@@prevcomorb[[g, a]], {g, ng}, {a, na1}];

	(* COMORBIDITY RATIO *)

	comorbratio[[d1, d2]] = Table[prevcomorb[[g, Range[na1], 4]] /
					((prevcomorb[[g, Range[na1], 2]] + prevcomorb[[g, Range[na1], 4]]) *
						(prevcomorb[[g, Range[na1], 3]] + prevcomorb[[g, Range[na1], 4]]) + eps),
				{g, ng}];

	comorbratio[[d2, d1]] = comorbratio[[d1, d2]],

	{d1, nd - 1}, {d2, d1 + 1, nd}];

(*Print[MatrixForm[.001 Round[1000 Table[meanaggreg[comorbratio[[1, 2, g]]], {g, ng}]]]];*)

RRdisprev	= Table[comorbratio[[d1, d2]] (1 - pdissel[[d1]]) / ( 1 - comorbratio[[d1, d2]] pdissel[[d1]]), {d1, nd}, {d2, nd}];

]; (* END CALCULATION OF CO-MORBIDITY RATES, OPTIONAL METHOD 2 *)


(* --------------------------------------------------
 ADJUSTMENT OF EXCESS MORTALITY RATES FOR DOUBLE COUNTING MORTALITY CASES
----------------------------------------------------*)

printbug["8."];

excessmortseladj = excessmortsel;

(* CO-MORBIDITY EFFECTS THROUGH JOINT RISK FACTORS, THUS EXCLUDING INDEPENDENT EFFECTS THROUGH CAUSALLY RELATED DISEASES *)

RMcomorb = Table[1., {nd}, {nd}, {ng}, {na1}];

Do[	RMcomorb[[d, d1, g]] *=

		Times@@Table[Plus@@(	RMriskseladj[[r, RRriskindsel[[r, d + 1]], g]] *
					RMriskseladj[[r, RRriskindsel[[r, d1 + 1]], g]] *
					prisksel[[r, g]]),
				{r, nrdpop}],

		{d, nd}, {d1, nd}, {g, ng}];

printbug["8.02"];

(* CO-MORBIDITY RATE MULTIPLIERS, NB, ARE SYMMETRIC *)

RMdisprev = Table[RRdisprev[[d, d1]] RMcomorb[[d, d1]] / (1 + (RRdisprev[[d, d1]] - 1) RMcomorb[[d, d1]] pdissel[[d]]), {d, nd}, {d1, nd}];

If[(nd > 0), RMdisprev = Maxc[RMdisprev, Transpose[RMdisprev]]];


(* ADJUSTMENT THROUGH CO-MORBID DISEASES AND BASED ON CALCULATED CO-MORBIDITY RATES *)

If[MemberQ[{1}, excessmortcond] && (nd > 0),

printbug["8.1"];

	Do[	a	= agerange[[ha]];

		(* WDE: CO-VARIANCE MATRIX OF DISEASE PREVALENCE RATES, WDD: DIAGONAL OF CO-VARIANCE MATRIX *)

		wdd 	= DiagonalMatrix[Table[pdissel[[d, g, a]] (1 - pdissel[[d, g, a]]), {d, nd}] + .000001];
		wde 	= wdd;

		(* ADJUSTMENT FOR JOINT RISK FACTORS AND JOINT DISEASES *)

		Do[If[(d != d1), wde[[d, d1]] = pdissel[[d, g, a]] pdissel[[d1, g, a]]] (RMdisprev[[d, d1, g, a]] - 1), {d, nd}, {d1, nd}];

		wde	= Maxc[wde, Transpose[wde]];

		excessmortseladj[[All, g, a]] =
			Inverse[wde] . wdd .
				(excessmortsel[[All, g, a]] -
					casefat1[[casefatindsel, g, a]] incsel[[All, g, a]] / (1 - pdissel[[All, g, a]])) +
				casefat1[[casefatindsel, g, a]] incsel[[All, g, a]] / (1 - pdissel[[All, g, a]]),

		{g, ng}, {ha, Length[agerange]}];

	excessmortseladj = Min1[Max0[excessmortseladj]]

	]; (* EXCESSMORTCOND==1 *)

(* ADJUSTMENT BASED ON CBS-REGISTERED CAUSE-SPECIFIC MORTALITY RATES *)

If[MemberQ[{2}, excessmortcond] && (nd > 0),

printbug["8.2"];	

	excessmortseladj = Min1[Max0[Table[Minc[
				excessmortsel[[d]],
				(causemortsel[[d]] - incsel[[d]] casefat1[[casefatindsel[[d]]]] / ( 1 - pdissel[[d]])) /
					(pdissel[[d]] + eps)],
				{d, nd}]]]

	]; (* EXCESSMORTCOND==2 *)

(* ADJUSTMENT BASED ON CO-MORBIDITY CALCULATED USING ALL CAUSE MORTALITY RATES, SEE WORD-DOC ATTRIBUTING EXCESS MORTALITY RATES *)

If[MemberQ[{3}, excessmortcond] && (nd > 0),

printbug["8.3"];

	If[(nrd == 0),

		RMdis2 = RMdistot = Table[1., {nd}, {ng}, {na1}],

		(* RISK MULTIPLIERS INCLUDING EFFECT THROUGH CAUSALLY RELATED DISEASES *)

		RMdisagg = Table[RMriskseladj[[r, RRriskindsel[[r, d + 1]]]], {r, nrdpop}, {d, nd}];

		Do[If[(Max[RRdisprev[[d, d1, g]]] > 1),

			Do[RMdisagg[[r, d1, g, ri]] *=

				1 + (RRdisprev[[d, d1, g]] - 1) RMriskseladj[[r, RRriskindsel[[r, d + 1]], g, ri]] pdissel[[d, g]],

				{r, nrdpop}, {ri, ncrsel[[r]]}]],

			{d, nd}, {d1, nd}, {g, ng}];

		Do[	ERMdisagg = Plus@@(RMdisagg[[r, d, g]] prisksel[[r, g]]);

			Do[RMdisagg[[r, d, g, ri]] /= ERMdisagg, {ri, ncrsel[[r]]}],

			{r, nrdpop}, {d, nd}, {g, ng}];

		(* MEAN QUADRATIC DISEASE RATE MULTIPLIER VALUES *)

		RMdis2 = Table[Times@@Table[Plus@@(RMdisagg[[r, d, g]]^2 prisksel[[r, g]]), {r, nrdpop}], {d, nd}, {g, ng}];

		(* MEAN PRODUCT OF DISEASE RATE AND ALL CAUSE MORTALITY RATE MULTIPLER VALUES *)

		RMdistot = Table[Times@@Table[Plus@@(RMdisagg[[r, d, g]] RMriskseladj[[r, 2, g]] prisksel[[r, g]]), {r, nrdpop}],
				{d, nd}, {g, ng}]];

                Do[	a	= agerange[[ha]];

		(* WDE: CO-VARIANCE MATRIX OF DISEASE PREVALENCE RATES, WDD: DIAGONAL OF CO-VARIANCE MATRIX *)

		wde 	= DiagonalMatrix[Table[1 - RMdis2[[d, g, a]] pdissel[[d, g, a]], {d, nd}]];
		wdd 	= DiagonalMatrix[Table[1 - pdissel[[d, g, a]], {d, nd}]];
		
		Do[If[(d != d1),

			(* ADJUSTMENT FOR JOINT RISK FACTORS AND JOINT DISEASES *)

			RMcomorbrisk = Times@@Table[Plus@@(RMdisagg[[r, d, g, All, a]] RMdisagg[[r, d1, g, All, a]] prisksel[[r, g, All, a]]),
							{r, nrdpop}];
						
			wde[[d, d1]] = pdissel[[d1, g, a]]] (RMdisprev[[d, d1, g, a]] - RMcomorbrisk),

			{d, nd}, {d1, nd}];

		excessmortseladj[[All, g, a]] =	Inverse[wde] .
			(wdd .
				(excessmortsel[[All, g, a]] -
					casefat1[[casefatindsel, g, a]] incsel[[All, g, a]] / (1 - pdissel[[All, g, a]])) -
				Table[(RMdistot[[d, g, a]] - 1) morttot1[[g, a]], {d, nd}]) +
				casefat1[[casefatindsel, g, a]] incsel[[All, g, a]] / (1 - pdissel[[All, g, a]])               


          ,

		{g, ng}, {ha, Length[agerange]}];
               
          

	excessmortseladj = Min1[Max0[excessmortseladj]];

                

                

	]; (* EXCESSMORTCOND==3 *) 

       excesscor = 0*excessmortseladj;
       excessmortselcor = excessmortsel; (* initialiseer hulp arrays *)



     Do[a = agerange[[ha]];
    (*WDE : CO - VARIANCE MATRIX OF DISEASE 
        PREVALENCE           RATES, WDD : DIAGONAL OF CO - VARIANCE MATRIX*)
    wde = DiagonalMatrix[Table[1 - RMdis2[[d, g, a]] pdissel[[d, g, a]], {d, nd}]];
    wdd = DiagonalMatrix[Table[1 - pdissel[[d, g, a]], {d, nd}]];
    Do[If[(d ? d1),(*ADJUSTMENT 
          FOR JOINT RISK FACTORS AND JOINT DISEASES*)
    RMcomorbrisk = Times @@ Table[Plus @@ (RMdisagg[[r, d, g, All, a]] RMdisagg[[r, d1, g, All, a]] prisksel[[r, g, All, a]]), {r, nrdpop}];
          wde[[d, d1]] = pdissel[[d1, g, a]]] (RMdisprev[[d, d1, g, a]] - RMcomorbrisk), {d, nd}, {d1, nd}];
    excesscor[[All, g, a]] = excessmortseladj[[All, g, a]];          (*attributable mortality where we set the am of disease 2 to zero*)
    excesscor[[2, g, a]] = 0*excesscor[[2, g, a]];
 
 (*  calculate  the excess mortality from the attributable mortality*)

   excessmortselcor[[All, g, a]] = Inverse[wdd].(wde.(excesscor[[All, g, a]] 
              - casefat1[[casefatindsel,g, a]] incsel[[All, g, a]]/(1 - pdissel[[All, g, a]])) 
              + Table[(RMdistot[[d, g, a]] - 1) morttot1[[g, a]], {d, nd}])
              + casefat1[[casefatindsel, g, a]] incsel[[All, g, a]]/(1 - pdissel[[All, g, a]])  

       , {g, ng}, {ha, Length[agerange]}];
 (* export results *)

Excessout1 = Partition[excessmortselcor[[2, 1, All]], 5];
Excessout2 = Partition[excessmortselcor[[2, 2, All]], 5];
Export["Excess_Other_CHD.dat", TableForm[0.01*Round[100000*{Append[Apply[Plus, Excessout1, 
      1]/5, excessmortselcor[[2, 1, 86]]],
            Append[Apply[Plus, Excessout2, 1]/5, excessmortselcor[[2, 2, 86]]]}]]]




(* --------------------------------------------------
	OTHER CAUSES MORTALITY RATES
----------------------------------------------------*)

printbug["8.5"];

mortcasefat 	= incsel (casefat1[[casefatindsel]] + (1 - casefat1[[casefatindsel]]) .5 excessmortseladj);
dismort		= pdissel excessmortseladj + mortcasefat;
mortothsel 	= Max0[morttot1 - Plus@@dismort];


(* --------------------------------------------------
	OTHER CAUSES MORTALITY RATE MULTIPLIERS
----------------------------------------------------*)

printbug["10."];

If[(1 == 1),

	(* RESULTING OTHER CAUSES MORTALITY RATES AND RATE MULTIPLIERS *)
	(* INITIAL ESTIMATION BASED ON ALL CAUSE MORTALITY RATE RATIOS *)

printbug["10.1"];

	RMothrisksel 		= Table[RMriskseladj[[r, 2, g, ri]] morttot1[[g]], {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];

	(* ADJUSTMENT FOR POPULATION RISK FACTORS *)

printbug["10.2"];

	Do[	RMothrisksel[[r, g, ri]] -= Plus@@Table[RMriskseladj[[r, RRriskindsel[[r, d + 1]], g, ri]] dismort[[d, g]], {d, nd}],

		{r, nrdpop}, {g, ng}, {ri, ncrsel[[r]]}];

	(* ADJUSTMENT FOR RISK FCTORS RESTRICTED TO DISEASES, E.G. HBA1C *)

printbug["10.3"];

	Do[	d1 = disindinv[[disriskindddis[[r]]]];

		RMothrisksel[[nrdpop + r, g, ri]] -=

			pdis1[[d1, g]] *
			(Plus@@Table[RMriskseladj[[nrdpop + r, RRriskindsel[[nrdpop + r, d + 1]], g, ri]] dismort[[d, g]], {d, nd}] -
			RMriskseladj[[nrdpop + r, RRriskindsel[[nrdpop + r, d1 + 1]], g, ri]] dismort[[d1, g]]),

		{r, nrddis}, {g, ng}, {ri, ncrsel[[nrdpop + r]]}];			
	
	RMothrisksel	= Max0[RMothrisksel];
	
	(* OTHER CAUSES MORTALITY RISK MULTIPLIERS *)

printbug["10.4"];

	Do[	If[(Min[RMothrisksel[[r, g, Range[ncrsel[[r]]], a]]] < eps),

			RMothrisksel[[r, g, Range[ncrsel[[r]]], a]] = 1,

			RMothrisksel[[r, g, Range[ncrsel[[r]]], a]] = 
				RMothrisksel[[r, g, Range[ncrsel[[r]]], a]] / (RMothrisksel[[r, g, 1, a]] + eps)],

		{r, nrd}, {g, ng}, {a, na1}];

printbug["10.5"];

	(* CONSISTENCY CHECKS ON CALCULATED OTHER CAUSES MORTALITY RISK MULTIPLIERS *)

	Do[	If[(riskindd[[r]] == 1),

	(* NON-MONOTONIC RR'S, I.E. SMOKING, RR NEVER SMOKERS <= RR FORMER SMOKERS <=RR CURRENT SMOKERS *)

printbug["10.6"];
			Do[	RMothrisksel[[r, g, 1]] = Minc[RMothrisksel[[r, g, 1]], RMothrisksel[[r, g, 3]]];
				RMothrisksel[[r, g, 2]] = Maxc[RMothrisksel[[r, g, 2]], RMothrisksel[[r, g, 3]]],
				{g, ng}]];

		If[MemberQ[{2, 3, 4, 5, 7, 8, 12}, riskindd[[r]]],

	(* MONOTONOUS INCREASING RR'S, I.E. SBP, CHOLESTEROL, PHYSICAL ACTIVITY, FAT, TRANS FATTY ACIDS, HBA1C,
   	RR CLASS RI <= RR CLASS RI+1 *)

printbug["10.7"];

			Do[	RMothrisksel[[r, g, ri + 1]] = Maxc[RMothrisksel[[r, g, ri]], RMothrisksel[[r, g, ri + 1]]],
				{g, ng}, {ri, ncrsel[[r]] - 1}]];
	
		If[(riskindd[[r]] == 6),

	(* NON-MONOTONIC RR'S, i.e. ALCOHOL, RR CLASS 3 <= RR CLASS 4 *)

printbug["10.8"];

			Do[	RMothrisksel[[r, g, ri + 1]] = Maxc[RMothrisksel[[r, g, ri]], RMothrisksel[[r, g, ri + 1]]],
				{g, ng}, {ri, 3, 3}]];

		If[(riskindd[[r]] > 8),

	(* MONOTONOUS DECREASING RR'S, I.E. FRUIT, VEGETABLES, RR CLASS RI >= RR CLASS RI+1 *)

printbug["10.9"];

			Do[	RMothrisksel[[r, g, ri + 1]] = Minc[RMothrisksel[[r, g, ri]], RMothrisksel[[r, g, ri + 1]]],
				{g, ng}, {ri, ncrsel[[r]] - 1}]],

	{r, nrd}];

printbug["10.10"];

	sgn		= Sign[Table[Plus@@RMothrisksel[[r, g]], {r, nrd}, {g, ng}]];	

printbug["10.11"];

	RMothrisksel	= Table[(1 - sgn[[r, g]]) Table[1, {na1}] +
					sgn[[r, g]] RMothrisksel[[r, g, ri]] / (Plus@@(RMothrisksel[[r, g]] prisksel[[r, g]]) + eps),
				{r, nrd}, {g, ng}, {ri, ncrsel[[r]]}],

	(* CONTINUOUSLY DISTRIBUTED RISK FACTORS *)

	RMothrisksel	= Table[1, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}, {na1}]

	];


(* --------------------------------------------------
   CALCULATION OF OTHER CAUSES MORTALITY RISKS DEPENDENT ON TIME DURATION SINCE SMOKING CESSATION
----------------------------------------------------*)

printbug["9."];

If[(RRsmokduurind == 1),

	age1		= 5 Range[nac[[1]]] - 2.5;

printbug["9.1"];

	logRRsmokduur	= logRRsmokduur0;

printbug["9.2"];

	meanstopduur	= Table[meanaggreg[stopduur[[g, ri]]], {g, ng}, {ri, nstopduur}];

printbug["9.3"];

	meanstopduur	= Table[Plus@@(Table[Plus@@(duurval meanstopduur[[g, Range[nstopduur], a]]), {a, nac[[1]]}] *
						prisk0[[1, g, 3]] npop1[[g]]) /
					Plus@@(prisk0[[1, g, 3]] npop1[[g]]),
				{g, ng}];

printbug["9.4"];

	indduur		= Table[Sign[Max[logRRsmokduur0[[d]]]], {d, Length[logRRsmokduur0]}];

printbug["9.5"];

	meanRRsmok	= Table[Plus@@(RRrisk0[[1, d, g, ri]] prisk0[[1, g, ri]] npop1[[g]]) / Plus@@(prisk0[[1, g, ri]] npop1[[g]]),
				{d, Length[RRrisk0[[1]]]}, {g, ng}, {ri, 3}];

printbug["9.5"];

	Do[If[(indduur[[d]] == 0),

			Table[logRRsmokduur[[d, g, 1]] =
					(1 - (meanRRsmok[[d, g, 3]] - 1) / (meanRRsmok[[d, g, 2]] - 1 + eps)) / meanstopduur[[g]],
				{g, ng}]],
		{d, 2, Length[indduur]}];

printbug["9.6"];

	RRform		= Max1[Table[Plus@@((1 + (RRrisk0[[1, d, g, 2, a]] - 1) Exp[-logRRsmokduur[[d, g, 1]] *
							Exp[-logRRsmokduur[[d, g, 2]] Max0[age1[[a]] - 51]] duurval]) *
							stopduur[[g, Range[nstopduur], a]]) /
						(Plus@@stopduur[[g, Range[nstopduur], a]] + eps),
				{d, Length[logRRsmokduur]}, {g, ng}, {a, nac[[1]]}]];

printbug["9.7"];

	ERRsmok		= Table[Plus@@(RRrisk0[[1, d, g, Range[2]]] prisk0[[1, g, Range[2]]]) + RRform[[d, g]] prisk0[[1, g, 3]],
				{d, Length[RRrisk0[[1]]]}, {g, ng}];


	RRothsmok	= Table[RMothrisksel[[1, g, ri]] / RMothrisksel[[1, g, 1]], {g, ng}, {ri, ncr0[[1]]}];

	(* SMOKING RELATED DISEASES *)

printbug["9.8"];

	dissmok		= Select[Range[nd0] Sign[Drop[RRriskind0[[1]], 1] - 1], Positive];

	(* UNSELECTED SMOKING RELATED DISEASES *)

printbug["9.9"];

	dissmoknonsel0	= Select[Range[Length[dissmok]], Not[MemberQ[disind, dissmok[[#]]]] &];

printbug["9.10"];

	If[(Length[dissmoknonsel0] > 0),

printbug["9.11"];

		dissmoknonsel	= dissmok[[dissmoknonsel0]];

		ha		= 10;

printbug["9.12"];

		dismortnonsel	= (Transpose[(excessmort0[[dissmoknonsel]] pdis0[[dissmoknonsel]] +
						casefat0[[casefatind0[[dissmoknonsel]]]] inc0[[dissmoknonsel]]), {3, 2, 1}])[[ha]];

printbug["9.13"];

		RRsmoknonsel	= Transpose[RRrisk0[[1, RRriskind0[[1, dissmoknonsel + 1]], Range[ng], Range[3], ha]]];

printbug["9.14"];

		ERRsmoknonsel	= Transpose[ERRsmok[[RRriskind0[[1, dissmoknonsel + 1]], Range[ng], ha]]];

printbug["9.15"];

		logRRsmokduurnonsel = Transpose[logRRsmokduur[[dissmoknonsel0 + 2]], {3, 2, 1}];

printbug["9.16"];

		wgt		= Table[(RRsmoknonsel[[g, Range[Length[dissmoknonsel]], 2]] - 1) dismortnonsel[[g]] / ERRsmoknonsel[[g]],
					{g, ng}];

printbug["9.17"];

		parA		= Table[Plus@@(wgt[[g]] logRRsmokduurnonsel[[1, g]]) / Plus@@wgt[[g]], {g, ng}];

printbug["9.18"];	

		parB		= Table[Plus@@(wgt[[g]] logRRsmokduurnonsel[[1, g]] logRRsmokduurnonsel[[2, g]]) /
						Plus@@(wgt[[g]] logRRsmokduurnonsel[[1, g]]),
					{g, ng}];

printbug["9.19"];

		logRRsmokduuroth = Transpose[{parA, parB}],

		(* ALL SMOKING RELATED DISEASES SELECTED *)

printbug["9.20"];

		logRRsmokduuroth = Table[0, {ng}, {2}];

		];

(* CALCULATION OF OTHER CAUSES RR'S DEPENDENT ON TIME SINCE SMOKING CESSATION *)

	hRRothsmok	= Table[1 + (RRothsmok[[g, 2]] - 1) Exp[-logRRsmokduuroth[[g, 1]] *
					Exp[-logRRsmokduuroth[[g, 2]] Max0[Range[na1] - 51]] duurval[[ri]]],
				{g, ng}, {ri, nstopduur}];
	
	EhRRothsmok	= Table[Plus@@Take[RRothsmok[[g]] prisk1[[1, g]], 2] +
					Plus@@(hRRothsmok[[g]] stopduur[[g]]) prisk1[[1, g, 3]],
				{g, ng}];

	RMothsmok	= Table[Join[Take[RRothsmok[[g]], 2], hRRothsmok[[g]]], {g, ng}];
	RMothsmok	= Table[RMothsmok[[g, ri]] / EhRRothsmok[[g]], {g, ng}, {ri, ncsmok}];
		
	];


(* --------------------------------------------------
	CALCULATION OF LIFE EXPECTANCY FIGURES
----------------------------------------------------*)

printbug["11."];

a1 		= Flatten[{Range[na1], Table[na1, {na2 - na1}]}];
a2 		= Flatten[{Range[na1], Table[na1, {na2 + 1 - na1}]}];

surv0 		= Table[1, {ng}, {na2 + 1}];			(* SURVIVAL NUMBERS OF CLOSED COHORT OF TOTAL POPULATION *)
prev 		= Table[1, {nd}, {ng}, {na2 + 1}];		(* SURVIVAL NUMBERS OF CLOSED COHORT OF DISEASED PERSONS *)
prevduur	= Table[0, {nd}, {ng}, {na2 + 1}];		(* PREVALENCE NUMBERS OF DYNAMIC COHORT *)
nonprevduur 	= Table[0, {nd}, {ng}, {na2 + 1}];		(* SUM OF AGE AT DISEASE ONSET OF PREVALENT CASES *)


(* CALCULATION OF SURVIVAL AND DISEASE PREVALENCE CURVES USING LIFE TABLE METHOD *)

printbug["11.1"];

Do[surv0[[g, a + 1]] = surv0[[g, a]] - morttot1[[g, a1[[a]]]] surv0[[g, a]], {g, ng}, {a, na2}];

Do[prev[[d, g, a + 1]] = 
		prev[[d, g, a]] - rem1[[remindsel[[d]], g, a1[[a]]]] prev[[d, g, a]]
		- (morttot1[[g, a1[[a]]]] + (1 - pdissel[[d, g, a1[[a]]]]) excessmortsel[[d, g, a1[[a]]]]) prev[[d, g, a]],
		{d, nd}, {g, ng}, {a, na2}];

printbug["11.2"];

Do[prevduur[[d, g, a + 1]] = 
		prevduur[[d, g, a]] + (1 - casefat1[[casefatindsel[[d]], g, a1[[a]]]]) inc1[[d, g, a1[[a]]]]
		- rem1[[remindsel[[d]], g, a1[[a]]]] prevduur[[d, g, a]]
		- (morttot1[[g, a1[[a]]]] + (1 - pdissel[[d, g, a1[[a]]]]) excessmortsel[[d, g, a1[[a]]]]) *
			prevduur[[d, g, a]],
		{d, nd}, {g, ng}, {a, na2}];

printbug["11.3"];

Do[nonprevduur[[d, g, a + 1]] = 
		nonprevduur[[d, g, a]] + (1 - casefat1[[casefatindsel[[d]], g, a1[[a]]]]) inc1[[d, g, a1[[a]]]] (a - .5)
		- rem1[[remindsel[[d]], g, a1[[a]]]] nonprevduur[[d, g, a]]
		- (morttot1[[g, a1[[a]]]] + (1 - pdissel[[d, g, a1[[a]]]]) excessmortsel[[d, g, a1[[a]]]]) *
			nonprevduur[[d, g, a]],
		{d, nd}, {g, ng}, {a, na2}];

printbug["11.4"];

(* DISCOUNTING *)

surv0	= Table[surv0[[g]] / (1 + discounte)^(Range[na2 + 1] - 1), {g, ng}];

preve	= Table[prev[[d, g]] / (1 + discounte)^(Range[na2 + 1] - 1), {d, nd}, {g, ng}];

prevc	= Table[prev[[d, g]] / (1 + discountc)^(Range[na2 + 1] - 1), {d, nd}, {g, ng}];
		
(* REST LIFE EXPECTANCY *)

printbug["11.5"];

le1 	= Table[.5 Plus@@(Drop[Drop[surv0[[g]], a], 1] + Drop[Drop[surv0[[g]], a], -1]) / surv0[[g, a + 1]],
		{g, ng}, {a, 0, na2}];

le 	= Table[Flatten[{le1[[g, Range[na]]], Plus@@Drop[le1[[g]] surv0[[g]], na] / Plus@@Drop[surv0[[g]], na]}], {g, ng}];

(* DISEASE DURATION OF NEW CASES *)

printbug["11.6"];

de1 	= Table[.5 Plus@@(Drop[Drop[preve[[d, g]], a], 1] + Drop[Drop[preve[[d, g]], a], -1]) / preve[[d, g, a + 1]],
		{d, nd}, {g, ng}, {a, 0, na2}];

de 	= Table[Flatten[{de1[[d, g, Range[na]]], Plus@@Drop[de1[[d, g]] preve[[d, g]], na] / Plus@@Drop[preve[[d, g]], na]}],
		{d, nd}, {g, ng}];

(* COSTS AND DALYS OVER DISEASE DURATION FOR NEW CASES *)

printbug["11.7"];

costspatient 	= Table[costspatientsel[[d, g, a2]], {d, nd}, {g, ng}];

DALYwgt		= Table[DALYwgt1[[d, g, a2]], {d, nd}, {g, ng}];	

costse1 	= Table[.5 Plus@@(Drop[Drop[prevc[[d, g]] costspatient[[d, g]], a], 1] +
				Drop[Drop[prevc[[d, g]] costspatient[[d, g]], a], -1]) / 
    			prevc[[d, g, a + 1]],
			{d, nd}, {g, ng}, {a, 0, na2}];

costse 		= Table[(1 - casefat1[[casefatindsel[[d]], g]]) *
				Flatten[{costse1[[d, g, Range[na]]], Plus@@Drop[costse1[[d, g]] prevc[[d, g]], na] /
				Plus@@Drop[prevc[[d, g]], na]}] +
			.042 casefat1[[casefatindsel[[d]], g]] costspatient[[d, g, Range[na1]]],
			{d, nd}, {g, ng}];

printbug["11.8"];

dewgt1 		= Table[.5 Plus@@(Drop[Drop[preve[[d, g]] DALYwgt[[d, g]], a], 1] + Drop[Drop[preve[[d, g]] DALYwgt[[d, g]], a], -1]) /
				preve[[d, g, a + 1]],
			{d, nd}, {g, ng}, {a, 0, na2}];

dewgtdis 	= Table[(1 - casefat1[[casefatindsel[[d]],g]]) *
				Flatten[{dewgt1[[d, g, Range[na]]], Plus@@Drop[dewgt1[[d, g]] surv0[[g]], na] /
				Plus@@Drop[surv0[[g]], na]}] +
			.042 casefat1[[casefatindsel[[d]], g]] DALYwgt1[[d, g]],
			{d, nd}, {g, ng}];

(* PROPORTION OF DISEASE-RELATED MORTALITY CODED AS CAUSE-SPECIFIC *)

printbug["11.9"];

primmortsel 	= Min1[Table[causemortsel[[d, g]] /
			(pdissel[[d, g]] excessmortsel[[d, g]] +
				incsel[[d, g]] (casefat1[[casefatindsel[[d]], g]] +
				.5 ( 1 - casefat1[[casefatindsel[[d]], g]]) excessmortsel[[d, g]]) + eps),
			{d, nd}, {g, ng}]];

printbug["11.10"];

dewgtmort 	= Table[Table[(1 - casefat1[[casefatindsel[[d]], g, a]]) (le[[g, a]] - de[[d, g, a]]) *
				primmortsel[[d, g, Round[Minc[a + de[[d, g, a]], na1]]]],
				{a, na1}] +
			primmortsel[[d, g]] casefat1[[casefatindsel[[d]], g]] (le[[g]] - .042),
			{d, nd}, {g, ng}];

dewgtmort = Join[dewgtmort, {le}];


(* --------------------------------------------------
	CALCULATION OF PAST DISEASE DURATION
----------------------------------------------------*)

printbug["11.11"];

disduur	= Table[Range[na1] - .5 - nonprevduur[[d, g, Range[na1]]] / (prevduur[[d, g, Range[na1]]] + eps), {d, nd}, {g, ng}];


(* --------------------------------------------------
	ADJUSTMENT STEPS RELATED TO USE OF EMPIRICAL RISK FACTOR PREVALENCE DATA FOR DISEASE PATIENTS
----------------------------------------------------*)

printbug["13"];

(* CALCULATION OF RISK FACTOR CLASS TRANSITION RATES CORRESPONDING WITH CHANGE OF PREVALENCE RATES *)

makedrisk[r_, priskold_, prisknew_, trt_] := Block[{},

printbug["13.1"];

	nri = hnri = Length[priskold];
	If[(trt == 1), hnri /= 2];

printbug["13.2"];

	translist2 = Flatten[Table[{ri, ri + rj}, {rj, hnri - 1}, {ri, hnri - rj}], 1];
	If[(trt == 1), translist2 += hnri];

printbug["13.3"];

	prisk = priskold;
	transrisk = Table[0, {nri}, {nri}];

	If[(trt == 1),

printbug["13.4"];

		translist1	= Transpose[Partition[Range[nri], hnri]];
		Do[	ri	= translist1[[rk, 1]];
			rj	= translist1[[rk, 2]];
			If[(prisk[[ri]] < prisknew[[ri]]),
				transrisk[[rj, ri]] = Min[{prisk[[rj]], prisknew[[ri]] - prisk[[ri]]}];
				prisk[[ri]]	+= transrisk[[rj, ri]];
				prisk[[rj]]	-= transrisk[[rj, ri]],
				transrisk[[ri, rj]] = prisk[[ri]] - prisknew[[ri]];
				prisk[[ri]]	-= transrisk[[ri, rj]];
				prisk[[rj]]	+= transrisk[[ri, rj]]],
			{rk, Length[translist1]}];

printbug["13.5"];

		Do[	If[MemberQ[translist2, {ri, rj}] || MemberQ[translist2, {rj, ri}],

				If[(prisk[[ri]] < prisknew[[ri]]),
					transrisk[[rj, ri]] = Min[{prisk[[rj]], prisknew[[ri]] - prisk[[ri]]}];
					prisk[[ri]]	+= transrisk[[rj, ri]];
					prisk[[rj]]	-= transrisk[[rj, ri]],
					transrisk[[ri, rj]] = prisk[[ri]] - prisknew[[ri]];
					prisk[[ri]]	-= transrisk[[ri, rj]];
					prisk[[rj]]	+= transrisk[[ri, rj]]]],
			{ri, 2 hnri, hnri + 1, -1}, {rj, hnri + 1, ri - 1}]];

	If[(trt == 0),

printbug["13.6"];
	
		Do[	If[MemberQ[translist2, {ri, rj}] || MemberQ[translist2, {rj, ri}],

				If[(prisk[[ri]] < prisknew[[ri]]),
					transrisk[[rj, ri]] = Min[{prisk[[rj]], prisknew[[ri]] - prisk[[ri]]}];
					prisk[[ri]]	+= transrisk[[rj, ri]];
					prisk[[rj]]	-= transrisk[[rj, ri]],
					transrisk[[ri, rj]] = prisk[[ri]] - prisknew[[ri]];
					prisk[[ri]]	-= transrisk[[ri, rj]];
					prisk[[rj]]	+= transrisk[[ri, rj]]]],
			{ri, hnri - 1}, {rj, ri + 1, hnri}]];

printbug["13.7"];

 	Do[	transrisk[[ri]] /= (priskold[[ri]] + eps), {ri, nri}];
	Do[	transrisk[[ri, ri]] = 1 - Plus@@transrisk[[ri]], {ri, nri}];

      transrisk]; (* MAKEDRISK *)

If[(userriskdata >= 1),	

	(* RISK MULTILIPLIER VALUE FOR INITIAL DISTRIBUTION *)

printbug["13.7"];

	RMriskDMinc	= Table[priskDM1[[DMriskindsel[[d, r1]]]] / (prisksel[[DMriskpairsel[[d, r1]]]] + eps),
				{d, nd}, {r1, Length[DMriskpairsel[[d]]]}];

	(* STATE TRANSITION MATRIX CORRESPONDING TO DIFFERENCE IN STATE PREVALENCE RATES *)

printbug["13.8"];

	hpriskDM	= Table[priskDM1[[DMriskindsel[[d, r1]]]], {d, nd}, {r1, Length[DMriskpairsel[[d]]]}];

	hprisknonDM 	= Table[(prisksel[[DMriskpairsel[[d, r1]], g, ri]] - priskDM1[[DMriskindsel[[d, r1]], g, ri]] pdissel[[d, g]]) /
					(1 - pdissel[[d, g]]),
				{d, nd}, {r1, Length[DMriskpairsel[[d]]]}, {g, ng}, {ri, ncrsel[[DMriskpairsel[[d, r1]]]]}];

printbug["13.9"];

	transriskDM	= Table[makedrisk[DMriskindsel[[d, r1]], hprisknonDM[[d, r1, g, All, agerange[[a]]]],
						hpriskDM[[d, r1, g, All, agerange[[a]]]], 1],
				{d, nd}, {r1, Length[DMriskpairsel[[d]]]}, {g, ng}, {a, Length[agerange]}]];




(* --------------------------------------------------
		Write info to Logfile
----------------------------------------------------*)


(* PACKAGE VERSION *)

version = 2.0;

WriteString[logfile, 
		"\t" <> "Package: " <> StringReplace[Evaluate[Context[]], "`Private`"-> ", " ] 
		<> "version " <> ToString[version] <> "\n\n"];
End[]


Protect[Evaluate[Context[] <> "*"]]


EndPackage[]

