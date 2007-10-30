(* :Title: CZMAdjustBeforeSelection *)

(* :Context: CZMAdjustData` *)

(* :Author: Rudolf T. Hoogenveen, Roel G.M. Breuls *)

(* :Summary:
   CZM adjust data routine adjusts the all cause mortality relative risks such that they are consistent with
   disease-specific RR's, and imputes missing disease prevalence rates *)

(* :Copyright: © 2004 by Rudolf Hoogenveen/Roel Breuls *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March, other causes mortality RR's, initial distribution time since smoking cessation
		3.0 version november 2005
		3.1 version march 2007; globalized duursmok, ncsmok *)

(* :Keywords: data adjustment before selection, relative risks, disease prevalence *)


BeginPackage["CZMAdjustData`CZMAdjustBeforeSelection`",
	{"CZMInitialization`CZMLogFile`",
	"CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`",
	"CZMImportData`CZMImportUserSelections`",
	"CZMImportData`CZMImportDemography`",
	"CZMImportData`CZMImportRiskFactors`",
	"CZMImportData`CZMImportRelativeRisks`",
	"CZMImportData`CZMImportDiseaseData`",
	"CZMAdjustData`CZMDataSmoothing`"}]

prisk1::usage		= "prisk1[[r,g,ri,a]]: initial disease prevalence rates after imputing equilibrium values"
pdis1::usage 		= "pdis1[[d,g,a]]: initial disease prevalence rates after imputation of missing data"

transrisk1::usage	= "transrisk1[[r,g,rj,ri,a]]: smoothed risk factor class transition rates specified by age year (a) after adjustment"
transriskind1::usage	= "transriskind1[[r,ri,rj]]: risk factor class transition indicator values after adjustment"

riskdisprev1::usage	= "user provided discrete risk factor class after adjustment for lower ages for BMI within diabetics" 

RRriskpresel::usage	= "RRriskpresel[[r,d,g,ri,a]]: adjusted relative risk values (discrete) before selection"
RRcontpresel::usage	= "RRcontpresel[[r,d,g,ri,a]]: adjusted relative risk values (continuous) before selection"
RRdispresel::usage	= "RRdispresel[[d,g,a]]: incidence risks for one disease on another unadjusted for joint risk factors"

a0cont1::usage		= "a0cont1[[r,g,a]]: intercept of deterministic risk factor level change"
a1cont1::usage		= "a1cont1[[r,g,a]]: regression parameter of deterministic risk factor level change"

nstopduur::usage	= "# years since smoking cessation"
stopduur::usage		= "stopduur[[g,ri]]: distribution of former smokers over time since smoking cessation classes"
duurval::usage		= "duurval[[ri]]: class mean values of cessation-time"		
ncsmok::usage		= "# smoking classes in case of cessation-time dependent"	


Begin["`Private`"]	


Print["CZMAdjustBeforeSelection package is evaluated"];
printtijd;

printbug[c_] := If[(bugind == 1), Print[{"CZMAdjustBeforeSelection", c}]];


(* --------------------------------------------------
  OVERWRITING SMOKING PREVALENCE AND HBA1C PREVALENCE DATA (WITH EQUILIBRIUM VALUES CALCULATED FROM TRANSITION RATES)
----------------------------------------------------*)

printbug["1."];

transriskind1	= transriskind0;
transrisk1	= transrisk1pre;

prisk1		= prisk1pre;

printbug["1.1"];

(* OVERWRITING SMOKING PREVALENCE DAT FOR LOWEST AGES *)

prisk1[[1, Range[ng], 1, Range[10]]] 		= 1; 
prisk1[[1, Range[ng], 1 + Range[2], Range[10]]] = 0; 

printbug["1.2"];

(* OVERWRITING HBA1C PREVALENCE DATA WITH EQUILIBRIUM VALUES CALCULATED FROM TRANSITION RATES *)

htrans		= Table[0, {ng}, {ncr0[[12]]}, {ncr0[[12]]}];

Do[	htrans[[g, ri, rj]] = -Log[1 - transrisk1[[12, g, transriskind0[[12, ri, rj]]]]], {g, ng}, {ri, ncr0[[12]]}, {rj, ncr0[[12]]}];

Do[	htrans[[g, ri, ri]] = - Plus@@htrans[[g, ri]], {g, ng}, {ri, ncr0[[12]]}];

htrans 		= Transpose[htrans, {1, 4, 3, 2}];

htrans		= Max0[Min1[Table[MatrixExp[htrans[[g, a]]], {g, ng}, {a, na1}]]];

printbug["1.3"];

(* EQUILIBRIUM VALUES = EIGENVECTORS OF TRANSITION MATRIX *)

hprisk 		= Table[prisk1[[12, g, ri]] / RRrisk1[[12, RRriskind0[[12, 7 + 1]], g, ri]], {g, ng}, {ri, ncr0[[12]]}];

hprisk 		= Table[hprisk[[g, ri]] / Plus@@hprisk[[g]], {g, ng}, {ri, ncr0[[12]]}];

printbug["1.4"];

prisknondis 	= Table[(hprisk[[g, ri]] - pdis1pre[[7, g]] prisk1[[12, g, ri]]) / (1 - pdis1pre[[7, g]]), {g, ng}, {ri, ncr0[[12]]}];

RMrisk 		= RRrisk1[[12, RRriskind0[[12, 7 + 1]]]];

RMrisk 		= Table[RMrisk[[g, ri]] / Plus@@(RMrisk[[g]] prisknondis[[g]]), {g, ng}, {ri, ncr0[[12]]}];

printbug["1.5"];

hprisk 		= Abs[Table[Eigenvectors[
			pdis1pre[[7, g, a]] htrans[[g, a]] (1 - excessmort1[[7, g, a]]) +
			inc1[[7, g, a]] DiagonalMatrix[RMrisk[[g, Range[ncr0[[12]]], a]] prisknondis[[g, Range[ncr0[[12]]], a]]]][[1]],
			{g, ng}, {a, na1}]];

(* SMOOTHING *)

hprisk		= Table[fsmooth[meanaggreg[hprisk[[g, Range[na1], ri]]]], {g, ng}, {ri, ncr0[[12]]}];

prisk1[[12]] 	= Table[hprisk[[g, ri]] / Plus@@hprisk[[g]], {g, ng}, {ri, ncr0[[12]]}];


(* --------------------------------------------------
	IMPUTATION OF MISSING DISEASE PREVALENCE RATES
----------------------------------------------------*)

printbug["3."];

hpdis 	= Table[0, {na2 + 1}]; 
a1 	= Flatten[{Range[na1], Table[na1, {na2 - na1}]}]; 
wgt 	= Table[(1 - morttot1[[g, na1]])^Range[na2 + 1 - na], {g, ng}]; 

pdis1 	= pdis1pre;

Do[If[(Max[pdis1[[d, g]]] < eps) && (Max[inc1[[d, g]]] > eps), 

	Do[hpdis[[a + 1]] = hpdis[[a]] + inc1[[d, g, a1[[a]]]] (1 - casefat1[[casefatind0[[d]], g, a1[[a]]]]) *
		( 1 - .5 (excessmort1[[d, g, a1[[a]]]] + rem1[[remind0[[d]], g, a1[[a]]]]) ) 
		- rem1[[remind0[[d]], g, a1[[a]]]] hpdis[[a]] 
		- excessmort1[[d, g, a1[[a]]]] hpdis[[a]] (1 - hpdis[[a]]),
		{a, na2}]; 

	pdis1[[d, g]] = Flatten[{hpdis[[Range[na]]], Plus@@(Drop[hpdis, na] wgt[[g]]) / Plus@@wgt[[g]]}]],

	{d, nd0}, {g, ng}];


(* --------------------------------------------------
	DISEASE-SPECIFIC MORTALITY RATES
----------------------------------------------------*)

printbug["4."];

excessmortadj = Min1[Max0[Table[Minc[
			excessmort1[[d, g]],
			(causemort1[[d, g]] -
				inc1[[d, g]] casefat1[[casefatind0[[d]], g]]) / (pdis1[[d, g]] + eps)],
			{d, nd0}, {g, ng}]]];

dismort		= Table[pdis1[[d, g]] excessmortadj[[d, g]] +
			inc1[[d, g]] (casefat1[[casefatind0[[d]], g]] + (1 - casefat1[[casefatind0[[d]], g]]) .5 excessmortadj[[d, g]]),
			{d, nd0}, {g, ng}];


(* --------------------------------------------------
	TERMS OF QUADRATIC RISK FUNCTION VALUES USED FOR CONTINUOUSLY DISTRIBUTED RISK FACTORS
----------------------------------------------------*)


(* --------------------------------------------------
	ALL CAUSE MORTALITY RELATIVE RISK ADJUSTMENT(1)
1	class 1 = reference class
2	smoking: >=1
3	all risk factors: <= large value (=35)
4	all risk factors: all cause mortality risks are maximum (if>1) or minimum (if<1)
	of empirical and calculated (through disease-specific RR's) values
----------------------------------------------------*)

printbug["6."];

RRriskpresel 	= RRrisk1;

(* ADJUSTMENT STEPS 1, 2 & 3 *)

printbug["6.1"];

RRriskpresel	= Table[RRriskpresel[[r, d, g, ri]] / RRriskpresel[[r, d, g, 1]],
			{r, nrd0}, {d, Length[RRriskpresel[[r]]]}, {g, ng}, {ri, ncr0[[r]]}];

RRriskpresel[[1]] = Max1[RRriskpresel[[1]]];

RRriskpresel 	= Table[Minc[RRriskpresel[[r, d, g, ri, a]] / RRriskpresel[[r, d, g, 1, a]], 35],
			{r, nrd0}, {d, Length[RRriskpresel[[r]]]}, {g, ng}, {ri, ncr0[[r]]}, {a, na1}]; 

(* CALCULATION OF MEAN RISK VALUES AND RISK MULTIPLIERS FOR DISCRETELY AND CONTINUOUSLY DISTRIBUTED RISK FACTORS *)

printbug["6.2"];

ERRriskpresel 	= Table[Plus@@(RRriskpresel[[r, d, g]] prisk1[[r, g]]),
			{r, nrd0}, {d, Length[RRriskpresel[[r]]]}, {g, ng}]; 

RMriskpresel 	= Table[RRriskpresel[[r, d, g, ri]] / (ERRriskpresel[[r, d, g]] + eps),
			{r, nrd0}, {d, Length[RRriskpresel[[r]]]}, {g, ng}, {ri, ncr0[[r]]}]; 

(* ADJUSTMENT STEP 4 *)

printbug["6.3"];

Do[	(* DISCRETELY DISTRIBUTED RISK FACTORS *)

	riskdisind = MemberQ[Transpose[riskdispair][[1]], r];

	If[riskdisind,

printbug["6.4"];

		(* DISCRETELY DISTRIBUTED RISK FACTORS RESTRICTED TO DISEASES, E.G. HBA1C *)

		d1	= riskdispairinv[[r]];

		(* RISK FACTOR CLASS PREVALENCE RATES *)

		hprisk 	= Table[prisk1[[r, g, ri]] / RRrisk1[[r, RRriskind0[[r, d1 + 1]], g, ri]], {g, ng}, {ri, ncr0[[r]]}];
	 
        	hprisk 	= Table[hprisk[[g, ri]] / Plus@@hprisk[[g]], {g, ng}, {ri, ncr0[[r]]}];

		prisknondis = Table[(hprisk[[g, ri]] - pdis1[[d1, g]] prisk1[[r, g, ri]]) / (1 - pdis1[[d1, g]]),
					{g, ng}, {ri, ncr0[[r]]}];

		(* ALL CAUSE MORTALITY RATE RATIOS *)

		hRRrisk	= Table[morttot1[[g]] +
				pdis1[[d1, g]] *
					(Plus@@Table[(RMriskpresel[[r, RRriskind0[[r, d + 1]], g, ri]] - 1) dismort[[d, g]], {d, nd0}] -
					(RMriskpresel[[r, RRriskind0[[r, d1 + 1]], g, ri]] - 1) dismort[[d1, g]]),
				{g, ng}, {ri, ncr0[[r]]}],

		(* ALL CAUSE MORTALITY RATE RATIOS FOR DISCRETELY DISTRIBUTED POPULATION RISK FACTORS *)

printbug["6.5"];

		hRRrisk	= Table[morttot1[[g]] +
				Plus@@Table[(RMriskpresel[[r, RRriskind0[[r, d + 1]], g, ri]] - 1) dismort[[d, g]], {d, nd0}],
				{g, ng}, {ri, ncr0[[r]]}]

		];

	(* ALL CAUSE MORTALITY RATE RATIOS *)

	hRRrisk	= Table[hRRrisk[[g, ri]] / hRRrisk[[g, 1]], {g, ng}, {ri, ncr0[[r]]}]; 

	(* MEAN ALL CAUSE MORTALITY RATE RATIO VALUES *)

	EhRRrisk = If[riskdisind,

			Table[1 + (Plus@@(hRRrisk[[g]] prisk1[[r, g]]) - 1) pdis1[[d1, g]], {g, ng}],

			Table[Plus@@(hRRrisk[[g]] prisk1[[r, g]]), {g, ng}]]; 

	(* RISK FACTORS WITH DECREASING RR VALUES, I.E. CONSUMPTION OF FRUIT, VEGETABLES, FRUIT, FISH *)

	If[(r > 8) && (r < 12),

printbug["6.6"];

		(* DISCRETELY DISTRIBUTED RISK FACTORS *)

		If[(mortothind == 0),

			Do[RRriskpresel[[r, 2, g, ri]] = hRRrisk[[g, ri]],
				{g, ng}, {ri, ncr0[[r]]}],

			Do[RRriskpresel[[r, 2, g, ri]] = Minc[RRriskpresel[[r, 2, g, ri]], hRRrisk[[g, ri]]],
				{g, ng}, {ri, ncr0[[r]]}]]

		]; 

	(* RISK FACTORS WITH INCREASING RR VALUES, I.E. SMOKING, SBP, CHOLESTEROL, BMI, PHYSICAL ACTIVITY,
	   CONSUMPTION OF (TRANS) FATTY ACIDS, HBA1C *)

	If[MemberQ[{1, 2, 3, 4, 5, 7, 8, 12}, r],

printbug["6.7"];

		If[(mortothind == 0),

			Do[RRriskpresel[[r, 2, g, ri]] = hRRrisk[[g, ri]],
				{g, ng}, {ri, ncr0[[r]]}],

			Do[RRriskpresel[[r, 2, g, ri]] = Maxc[RRriskpresel[[r, 2, g, ri]], hRRrisk[[g, ri]]],
				{g, ng}, {ri, ncr0[[r]]}]]

		]; 

	(* OTHER RISK FACTORS, I.E. CONSUMPTION OF ALCOHOL *)

	If[(r == 6),

printbug["6.8"];

		RRriskpresel[[r, 2]] = 
			Table[	If[(ri == 2),

					If[(mortothind == 0),
						RRriskpresel[[r, 2, g, ri]] = hRRrisk[[g, ri]],
						RRriskpresel[[r, 2, g, ri]] = Minc[RRriskpresel[[r, 2, g, ri]], hRRrisk[[g, ri]]]],

					If[(mortothind == 0),
						RRriskpresel[[r, 2, g, ri]] = hRRrisk[[g, ri]],
						RRriskpresel[[r, 2, g, ri]] = Maxc[RRriskpresel[[r, 2, g, ri]], hRRrisk[[g, ri]]]]

					],

				{g, ng}, {ri, ncr0[[r]]}]]; 

	(* NEW ALL CAUSE MORTALITY RISKS *)

printbug["6.9"];

	RMriskpresel[[r, 2]] = Table[RRriskpresel[[r, 2, g, ri]] / Plus@@(RRriskpresel[[r, 2, g]] prisk1[[r, g]]), {g, ng}, {ri, ncr0[[r]]}],

{r, nrd0}];


(* --------------------------------------------------
CALCULATION OF RISK FACTOR 1-YEAR CLASS TRANSITION RATES FROM CLASS PREVALENCE RATES ADJUSTED FOR MORTALITY AND TIME TREND
----------------------------------------------------*)

printbug["7."];

If[(0 == 1),

	(* MULTIPLE SMOOTHING PROCEDURE *)

	fsmoothn[x_, n_] := Block[{},
				x1 = x;
				Do[x1 = fsmooth[meanaggreg[x1]], {n}];
				x1
				];

	(* NEW TRANSITION RISK POINTER VALUES *)

	transriskind1	= Table[1, {r, nrd0}, {ncr0[[r]]}, {ncr0[[r]]}];

	(* NEW TRANSITION RATE VALUES *)

	transrisk1	= Table[0, {r, nrd0}, {ng}, {1 + 2 (ncr0[[r]] - 1)}, {na1}];

	(* BASELINE ALL CAUSE MORTALITY RISKS *)

	mort0		= Table[morttot1[[g]] / Plus@@(prisk1[[r, g]] RRriskpresel[[r, 2, g]]), {r, nrd0}, {g, ng}];

	(* CALCULATION OF TRANSITION RISKS *)

	Do[	(* 1-YEAR DIFFERENCE OF PREVALENCE RATE VALUES *)

		htrs = 	(* OLD PREVALENC RISK VALUE *)

			(prisk1[[r, g, ri]] +

			(* INFLOW FROM PREVIOUS STATE *)

			If[(ri == 1),
				0,
				prisk1[[r, g, ri - 1]] transrisk1[[r, g, transriskind1[[r, ri - 1, ri]]]]
				] -

			(* OUTFLOW FROM MORTALITY AND TRANSITION TO PREVIOUS CLASS *)

			(RRriskpresel[[r, 2, g, ri]] mort0[[r, g]] +

				If[(ri == 1),
					0,
					transrisk1[[r, g, transriskind1[[r, ri, ri - 1]]]]
					]
				) prisk1[[r, g, ri]])[[Range[na1 - 1]]] -

			(* MINUS NEW PREVALENCE RISK VALUE *)
 
			( 1 - morttot1[[g, Range[na1 - 1]]]) prisk1[[r, g, ri, Range[2, na1]]];

		(* TRANSITION RATE TO NEXT CLASS *)

		transriskind1[[r, ri, ri + 1]] = 2 + 2 (ri-1);

		htrs1	= Max0[htrs] / (prisk1[[r, g, ri, Range[na1 - 1]]] + eps);

		(* SPECIFIC ADJUSTMENTS FOR RISK FACTORS *)

		If[( r == 1),			htrs1[[Range[1, 15]]] = 0];		(* SMOKING *)
		If[( r == 1) && (ri == 1),	htrs1[[Range[1, 15]]] = 0;		(* SMOKING *)
						htrs1[[Range[40, na]]]= 0];
		If[MemberQ[{2, 3, 4}, r],	htrs1[[Range[20]]] *= 0];		(* SBP, CHOL & BMI *)

		htrs1	= Min1[fsmoothn[Flatten[{htrs1, Take[htrs1, -1]}], 6]];

		transrisk1[[r, g, 2 + 2 (ri - 1)]] = htrs1;

		(* TRANSITION RATE FROM NEXT CLASS *)

		transriskind1[[r, ri + 1, ri]] = 3 + 2 (ri - 1);

		htrs1	= Max0[-htrs] / (prisk1[[r, g, ri + 1, Range[na1 - 1]]] + eps);

		(* SPECIFIC ADJUSTMENTS FOR RISK FACTORS *)

		If[( r == 1 ),			htrs1[[Range[1, 15]]] = 0];		(* SMOKING *)
		If[( r == 1 ) && ( ri == 1),	htrs1 *= 0];				(* SMOKING *)
		If[( r == 2 ),			htrs1 *= 0];
		If[MemberQ[{2, 3, 4}, r],	htrs1[[Range[20]]] *=0];		(* SBP, CHOL & BMI *)

		htrs1 = Min1[fsmoothn[Flatten[{htrs1, Take[htrs1, -1]}], 6]];

		htransrisk0[[r, g, 3 + 2 (ri - 1)]] = htrs1,

		{r, nrd0}, {g, ng}, {ri, ncr0[[r]] - 1}];

	];


(* --------------------------------------------------
	ALL CAUSE MORTALITY RELATIVE RISK ADJUSTMENT(2)
1	selection of other causes mortality RR's
	smoking: all cause mortality effect only through modeled diseases until age 30
	         no RR values for current and former smokers smaller than 1
	risk factors other than smoking: other cause mortality RR's until age 30 equal to age=30 value
	alcohol moderate drinkers: other causes RR <=1
	other causes and risk factors: >=1
	all risk factors: other causes mortality risks for women equal to values for men
2	re-calculation of all cause mortality RR's
----------------------------------------------------*)


If[(mortothind == 1),

printbug["8."];

	Do[	

(*----------------------------------------------------
	DISCRETELY DISTRIBUTED RISK FACTORS
----------------------------------------------------*)

		riskdisind = MemberQ[Transpose[riskdispair][[1]], r];

		If[riskdisind,

printbug["8.1"];

			(* OTHER CAUSES MORTALITY RATE RATIOS FOR DISCRETELY DISTRIBUTED RISK FACTORS RESTRICTED TO DISEASES *)

			RMothrisk = Table[1, {ng}, {ncr0[[r]]}, {na1}],

			(* OTHER CAUSES MORTALITY RATE RATIOS FOR DISCRETELY DISTRIBUTED RISK FACTORS APPLIED TO TOTAL POPULATION *)

			RMothrisk = Max0[Table[RMriskpresel[[r, 2, g, ri]] morttot1[[g]] -
							Plus@@Table[RMriskpresel[[r, RRriskind0[[r, d + 1]], g, ri]] dismort[[d, g]],
							{d, nd0}],
					{g, ng}, {ri, ncr0[[r]]}]]

			];

		(* SCALING FROM RISK MULTIPLIERS TO RELATIVE RISKS *)

		RMothrisk = Table[RMothrisk[[g, ri]] / RMothrisk[[g, 1]], {g, ng}, {ri, ncr0[[r]]}];

		(* SMOKING *)
printbug["8.2"];
		If[(r == 1), RMothrisk[[Range[ng], Range[ncr0[[r]]], Range[30]]] = 1]; 

		(* CONSUMPTION OF ALCOHOL *)
printbug["8.3"];
		If[(r == 6),

			Do[RMothrisk[[g, ri]] =
				If[(ri == 2),
					Min1[RMothrisk[[g, ri]]],
					Max1[RMothrisk[[g, ri]]]],
				{g, ng}, {ri, ncr0[[r]]}]

			];

		(* CONSISTENCY CHECKS *)

		(* SMOKING *)
printbug["8.4"];
		If[(r== 1), Do[RMothrisk[[g, 2]] = Maxc[RMothrisk[[g, 2]], RMothrisk[[g, 3]]], {g, ng}]];

		(* RISK FACTORS WITH DECREASING RR VALUES, I.E. CONSUMPTION OF FRUIT, VEGETABLES, FRUIT, FISH *)
printbug["8.5"];
		If[(r > 8) && (r < 12),

			Do[RMothrisk[[g, ri + 1]] = Minc[RMothrisk[[g, ri + 1]], RMothrisk[[g, ri]]], {g, ng}, {ri, ncr0[[r]] - 1}]];

		(* RISK FACTORS WITH INCREASING RR VALUES, I.E. SBP, CHOLESTEROL, BMI, PHYSICAL ACTIVITY,
		  	 CONSUMPTION OF (TRANS) FATTY ACIDS, HBA1C *)
printbug["8.6"];
		If[MemberQ[{2, 3, 4, 5, 7, 8, 12}, r],

			 Do[RMothrisk[[g, ri + 1]] = Maxc[RMothrisk[[g, ri + 1]], RMothrisk[[g, ri]]], {g, ng}, {ri, ncr0[[r]] - 1}]];


		(* OTHER THAN SMOKING *)
printbug["8.7"];
		If[(r > 1), Do[RMothrisk[[g, ri, Range[30]]] = RMothrisk[[g, ri, 30]], {g, ng}, {ri, ncr0[[r]]}]];

		(* MORE STABLE RESULTS *)
printbug["8.8"];
		(*RMothrisk[[2]] 	= RMothrisk[[1]];*)

		Do[RMothrisk[[g, ri]] = fsmooth[meanaggreg[RMothrisk[[g, ri]]]], {g, ng}, {ri, ncr0[[r]]}];
		
		(* SCALING OF OTHER CAUSES MORTALITY RELATIVE RISKS TO RISK MULTIPLIERS *)
printbug["8.9"];
		RMothrisk	= Table[RMothrisk[[g, ri]] / (Plus@@(RMothrisk[[g]] prisk1[[r, g]]) + eps), {g, ng}, {ri, ncr0[[r]]}];

		(* NEW ALL CAUSE MORTALITY RISKS *)

		hRR 	= If[riskdisind,

				(* RISK FACTORS RESTRICTED TO DISEASES *)
printbug["8.10"];
				d1	= riskdispairinv[[r]];

				Table[	RMothrisk[[g, ri]] (morttot1[[g]] -  Plus@@Table[dismort[[d, g]], {d, nd0}]) +
					pdis1[[d1, g]] *
						(Plus@@Table[RMriskpresel[[r, RRriskind0[[r, d + 1]], g, ri]] dismort[[d, g]], {d, nd0}] -
						RMriskpresel[[r, RRriskind0[[r, d1 + 1]], g, ri]] dismort[[d1, g]]),
					{g, ng}, {ri, ncr0[[r]]}],

				(* RISK FACTORS APPLIED TO TOTAL POPULATION *)
printbug["8.11"];
				Table[	RMothrisk[[g, ri]] (morttot1[[g]] -  Plus@@Table[dismort[[d, g]], {d, nd0}]) +
					Plus@@Table[RMriskpresel[[r, RRriskind0[[r, d + 1]], g, ri]] dismort[[d, g]], {d, nd0}],
					{g, ng}, {ri, ncr0[[r]]}]

				]; 

		RRriskpresel[[r, 2]] = Table[hRR[[g, ri]] / hRR[[g, 1]], {g, ng}, {ri, ncr0[[r]]}];

		(* SMOKING *)
printbug["8.12"];
		If[(r == 1), RRriskpresel[[r]] = Max1[RRriskpresel[[r]]]],
	

(*----------------------------------------------------
	CONTINUOUSLY DISTRIBUTED RISK FACTORS
----------------------------------------------------*)

		{r, nrd0}] (* DISCRETELY (INCL. CONTINUOUSLY) DISTRIBUTED RISK FACTORS *)

	]; (* If[(mortothind == 1) *)


(* --------------------------------------------------
	UNADJUSTMENT OF INCIDENCE RISKS FROM ONE DISEASE ON ANOTHER
	FROM EFFECTS THROUGH SMOKING, BLOOD PRESSURE AND CHOLESTEROL
----------------------------------------------------*)

printbug["9."];

RRdispresel	= RRdis1;

(* PAIRS OF CAUSALLY RELATED DISEASES WITH RR VALUES TO BE UNADJUSTED *)

Do[	RRdispresel[[d + 1, g]] *=
		
		Times@@Table[
			Plus@@(	RMriskpresel[[r, RRriskind0[[r, RRdisinddata[[RRdisinddatasel[[d]], 1]] + 1]], g]] *
				RMriskpresel[[r, RRriskind0[[r, RRdisinddata[[RRdisinddatasel[[d]], 2]] + 1]], g]] *
				prisk1[[r, g]]),
			{r, 3}],
 		{d, Length[RRdisinddatasel]}, {g, ng}];


(* --------------------------------------------------
	DETERMINISTIC CHANGES OF CONTINUOUSLY DISTRIBUTED RISK FACTORS 
----------------------------------------------------*)


(* --------------------------------------------------
   CALCULATION OF INITIAL DISTRIBUTION OF FORMER SMOKERS OVER TIME SINCE SMOKING CESSATION CLASSES
----------------------------------------------------*)

printbug["11."];

If[(RRsmokduurind == 1),

	nstopduur	= 20;				(* # 1-YEAR CLASSES OF TIME SINCE SMOKING CESSATION *)

	ncsmok		= ncr0[[1]] + nstopduur - 1;	(* # SMOKING CLASSES *)

	duurval		= Range[nstopduur] - .5;	(* MEAN TIME SINCE SMOKING CESSATION FOR EACH CLASS *)

	(* SMOKING CLASS PREVALENCE NUMBERS *)
printbug["11.1"];
	Nsmok		= Table[0., {na2}, {ncsmok}];		(* SMOKING PREVALENCE NUMBERS OF COHORT *)
	Nsmok[[1, 1]] 	= 1.;					(* INITIALLLY ONLY NEVER SMOKERS *)

	(* DISTRIBUTION OF FORMER SMOKRES OVER TIME SINCE SMOKING CESSATION CLASSES *)
printbug["11.1"];
	stopduur	= Table[0, {ng}, {nstopduur}, {na1}];
	
	Do[	(* RELAPSE RATE OF FORMER SMOKERS *)
printbug["11.2"];
		relapserate 	= 1 - Exp[ -relapsecoeff[[g, 1]] Exp[ -relapsecoeff[[g, 2]] 12 duurval ]
						( 1 - Exp[ -relapsecoeff[[g, 2]] 12 ] ) ];

		(* BASELINE MORTALITY RATES, RR'S OF CURRENT AND FORMER SMOKERS *)

		mortbase 	= morttot1[[g]] / Plus@@(RRriskpresel[[1, 2, g]] prisk1[[1, g]]);
	
		RRcurr	 	= RRriskpresel[[1, 2, g, 2]];	

		RRform		= Table[1 + (RRcurr - 1) Exp[-logRRsmokduur0[[2, g, 1]] *
						Exp[-logRRsmokduur0[[2, g, 2]] Max0[Range[na1] - 51]] duurval[[ri]] ],
					{ri, nstopduur}];

		(* SMOKING CLASS TRANSITION RATES *)

		transsmok = Table[0, {ncsmok}, {ncsmok}, {na1}];

		Do[transsmok[[ri, ri]] 		= 1, {ri, 2}];
		transsmok[[ncsmok, ncsmok]] 	= 1;
		Do[transsmok[[ri, ri + 1]] 	= 1, {ri, 3, ncsmok - 1}];

		(* TRANSITIONS = INFLOW AND OUTFLOW *)

		transsmok[[1, {1, 2}]] 		+= {-transrisk1[[1, g, transriskind0[[1, 1, 2]]]],
							transrisk1[[1, g, transriskind0[[1, 1, 2]]]]};
		transsmok[[2, {2, 3}]] 		+= {-transrisk1[[1, g, transriskind0[[1, 2, 3]]]],
							transrisk1[[1, g, transriskind0[[1, 2, 3]]]]};
		transsmok[[ncsmok, {2, ncsmok}]] += {relapserate[[nstopduur]], -relapserate[[nstopduur]]};

		Do[transsmok[[2 + ri, {2, 3 + ri}]] += {relapserate[[ri]], -relapserate[[ri]]}, {ri, nstopduur - 1}];

		(* MORTALITY = OUTFLOW *)

		transsmok[[1, 1]] 		-= mortbase;
		transsmok[[2, 2]] 		-= RRcurr mortbase;
		transsmok[[ncsmok, ncsmok]] 	-= RRform[[nstopduur]] mortbase;
		Do[transsmok[[2 + ri, 3 + ri]] -= RRform[[ri]] mortbase, {ri, nstopduur - 1}];

		transsmok			= Transpose[transsmok, {2, 3, 1}];

		(* LIFE COURSE OF SMOKING PREVALENCE NUMBERS *)
		
		Do[Nsmok[[a + 1]] = Nsmok[[a]].transsmok[[Min[a, na1]]], {a, na2 - 1}];

		(* DISTRIBUTION OF FORMER SMOKERS OVER TIME SINCE CESSATION CLASSES *)

		Do[stopduur[[g, ri, a]] = Nsmok[[a, 2 + ri]] / (Plus@@Nsmok[[a, 2 + Range[nstopduur]]] + eps),
						{ri, nstopduur}, {a, na}];

		Do[stopduur[[g, ri, na1]] = Plus@@Nsmok[[Range[na1, na2], 2 + ri]] / 
						Plus@@Flatten[Nsmok[[Range[na1, na2], 2 + Range[nstopduur]]]],
						{ri, nstopduur}],
		{g, ng}]		
		
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
