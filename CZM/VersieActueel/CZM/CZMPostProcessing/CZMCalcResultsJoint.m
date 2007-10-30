(* :Title: CZMCalcResultsJoint *)

(* :Context: CZMPostProcessing` *)

(* :Author: Rudolf T. Hoogenveen *)

(* :Summary:
   CZM postprocessing routine calculates QALYS based on joint models *)

(* :Copyright: © 2006 by Rudolf Hoogenveen *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	3.0 first version, January 2006
		3.1 version March 2007 *)

(* :Keywords: postprocessing, plots, qualilty-adjusted life years *)


BeginPackage["CZMPostProcessing`CZMCalcResultsJoint`",
	{"CZMInitialization`CZMLogFile`",
	"CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`", 
	"CZMImportData`CZMImportUserSelections`",
	"CZMImportData`CZMImportDemography`",
	"CZMImportData`CZMImportRiskFactors`",
	"CZMImportData`CZMImportRelativeRisks`",
	"CZMImportData`CZMImportDiseaseData`",
	"CZMImportData`CZMImportDALYs`",
	"CZMImportData`CZMImportCosts`",
	"CZMAdjustData`CZMMakeSelections`",
	"CZMAdjustData`CZMAdjustAfterSelection`",
	"CZMSimulation`CZMSimulationFunctions`",
	"CZMSimulation`CZMStoreResults`",
	"CZMPostProcessing`CZMExportUserSelections`",
	"CZMPostProcessing`CZMCalcResults`",
	"Graphics`MultipleListPlot`",
	"Graphics`Legend`"}]


popQALY	::usage	= "popQALY"


Begin["`Private`"]


Print["CZMCalcResultsJoint package is evaluated"];
printtijd;

printbug[c_] := If[(bugind == 1), Print[{"CZMCalcResultsJoint", c}]];

imagesize	= 240;
stdaxislabel 	= {"time", ""};
disselnames	= {"selected", "+ non-selected", "+ non-modeled"};
weightnames	= {"total", "disfree", "proportion", "additive", "worst case"};
stdwgtcolor1	= Table[{RGBColor[w / 4, 0, 1 - w / 4]}, {w, 0, 4}];
nm		= Plus@@modelsel;

plotset1	= {	DisplayFunction -> Identity,
			SymbolShape 	-> None,
			LegendPosition 	-> {-1.4, -.5},
			LegendSize 	-> {1.2, .5},
			PlotStyle	-> stdwgtcolor1,
			PlotJoined 	-> True,
			TextStyle 	-> stdtextstyle,
			AxesLabel 	-> stdaxislabel,
			PlotRange	-> All};

(* --------------------------------------------------
	PROCEDURES
----------------------------------------------------*)

(* SORTING *)

hsort[x_]	:= Reverse[Transpose[Sort[Transpose[{x, Range[Length[x]]}]]][[2]]];

(* SMOOTHED RATES *)

pdis0nonind	= Table[0, {nd0}, {ng}];
Do[pdis0nonind[[nondisind[[d]], g]] = meanaggreg[fsmooth[pdis0[[nondisind[[d]], g]]]], {d, Length[nondisind]}, {g, ng}];

pdis0nonmod	= Table[meanaggreg[fsmooth[nonmodelpdis0[[d, g]]]], {d, ndoth}, {g, ng}];

	

(* --------------------------------------------------
	MARGINAL MODEL: QALY WEIGHTING
----------------------------------------------------*)

printbug["3."];

makeQALY0[result_] := Block[{},

	(* TOTAL POPULATION NUMBERS *)

printbug["3.1"];

	hnpop 	= Table[Plus@@result[[1, scenlist[[scen]]]], {scen, hnscen}] / ndrawinput;

	npopres	= Table[hnpop, {5}, {3}];

printbug["3.2"];

	(* DISCRETE RISK FACTOR CLASS PREVALENCE RATES *)

printbug["3.3"];

	prisk	= Table[Plus@@result[[2, scenlist[[scen]], n, r, g, ri]] / (Plus@@Flatten[result[[2, scenlist[[scen]], n, r, g]]] + eps),
			{scen, hnscen}, {n, hnstap}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];

	(* CONTINUOUS RISK FACTOR DISTRIBUTION CHARACTERISTICS *)

printbug["3.4"];

	pcont	= Table[Plus@@result[[3, scenlist[[scen]], n, r, g, ri]] / (hnpop[[scen, n, g]] + eps),
			{scen, hnscen}, {n, hnstap}, {r, nrc}, {g, ng}, {ri, 2}] / ndrawinput;

	pcont	= Table[{vect1, pcont[[scen, n, r, g, 1]], pcont[[scen, n, r, g, 1]]^2 + pcont[[scen, n, r, g, 2]]},
			{scen, hnscen}, {n, hnstap}, {r, nrc}, {g, ng}];

	(* DISEASE PREVALENCE RATES *)

printbug["3.5"];

	pdis	= Table[Plus@@result[[4, scenlist[[scen]], n, d, g]] / (Plus@@Flatten[result[[1, scenlist[[scen]], n, g]]] + eps),
			{scen, hnscen}, {n, hnstap}, {d, nd}, {g, ng}];
printbug["3.6"];

	(* DISEASE PREVALENCE RATES IN JOINT RISK FACTOR CLASSES *)

	priskjoint = Table[Times@@Table[prisk[[scen, n, r, g, riskclass[[ri, r]]]], {r, nrd}],
				{scen, hnscen}, {n, hnstap}, {g, ng}, {ri, Length[riskclass]}];

	RMrisk	= Table[RRrisk0[[riskindd[[r]], d, g, ri]] /
				(Plus@@(RRrisk0[[riskindd[[r]], d, g]] prisk[[scen, n, r, g]]) + eps),
			{scen, hnscen}, {n, hnstap}, {r, nrd}, {d, Length[RRrisksel[[r]]]}, {g, ng}, {ri, ncrsel[[r]]}];

	RMjoint = Table[Times@@Table[RMrisk[[scen, n, r, RRriskindsel[[r, d + 1]], g, riskclass[[ri, r]]]], {r, nrd}],
				 {scen, hnscen}, {n, hnstap}, {ri, Length[riskclass]}, {d, nd}, {g, ng}];

(* NOG INVOER-FORMAT CHECKEN *)

	RMcont	= Table[meanaggreg[RRcontsel[[r, d, g, ri]]], {r, nrc}, {d, Length[RRcontsel[[r]]]}, {g, ng}, {ri, 3}];

	RMcont	= Table[RMcont[[r, d, g, ri]] / (Plus@@(RMcont[[r, d, g]] pcont[[1, 1, r, g]]) + eps),
				{scen, hnscen}, {n, hnstap}, {r, nrc}, {d, Length[RMcont[[r]]]}, {g, ng}, {ri, 3}];
		
printbug["3.7"];

	(* DISEASE-FREE YEARS *)

	Do[npopres[[2, d, scen, n, g]] *=
			Plus@@Table[priskjoint[[scen, n, g, ri]] Times@@(1 - pdis[[scen, n, All, g]] RMjoint[[scen, n, ri, All, g]]),
					{ri, Length[riskclass]}],
			{d, 3}, {scen, hnscen}, {n, hnstap}, {g, ng}];
printbug["3.8"];

	(* OTHER DISEASES INCLUDED *)

	Do[npopres[[2, d, scen, n]] *= Times@@(1 - pdis0nonind[[nondisind]]), {d, 2, 3}, {scen, hnscen}, {n, hnstap}];
	Do[npopres[[2, 3, scen, n]] *= Times@@(1 - pdis0nonmod), {scen, hnscen}, {n, hnstap}];

printbug["3.9"];

	(* PROPORTIONAL DALY WEIGHTING *)
		
	Do[npopres[[3, d, scen, n, g]] *=
				Plus@@Table[priskjoint[[scen, n, g, ri]] *
					Times@@(1 - DALYwgt0[[disind, g]] pdis[[scen, n, All, g]] RMjoint[[scen, n, ri, All, g]]),
					{ri, Length[riskclass]}],
			{d, 3}, {scen, hnscen}, {n, hnstap}, {g, ng}];
printbug["3.10"];

	(* OTHER DISEASES INCLUDED *)

	Do[npopres[[3, d, scen, n]] *= Times@@(1 - DALYwgt0[[nondisind]] pdis0nonind[[nondisind]]), {d, 2, 3}, {scen, hnscen}, {n, hnstap}];
	Do[npopres[[3, 3, scen, n]] *= Times@@(1 - nonmodelDALYwgt0 pdis0nonmod), {scen, hnscen}, {n, hnstap}];

printbug["3.11"];

	(* ADDITIVE DALY WEIGHTING *)

	Do[npopres[[4, d, scen, n]] -= Plus@@(DALYwgt0[[disind]] pdis[[scen, n]]) hnpop[[scen, n]], {d, 3}, {scen, hnscen}, {n, hnstap}];

printbug["3.12"];

	(* OTHER DISEASES INCLUDED *)

	Do[npopres[[4, d, scen, n]] -= Plus@@(DALYwgt0[[nondisind]] pdis0nonind[[nondisind]]) hnpop[[scen, n]],
			{d, 2, 3}, {scen, hnscen}, {n, hnstap}];

	Do[npopres[[4, 3, scen, n]] -= Plus@@(nonmodelDALYwgt0 pdis0nonmod) hnpop[[scen, n]], {scen, hnscen}, {n, hnstap}];

printbug["3.13"];

	(* WORST CASE DALY WEIGHTING, ONLY MODELED DISEASES INCLUDED, OTHERDIS == 0 *)

	seq	= hsort[Table[Plus@@Flatten[DALYwgtsel[[d]]], {d, nd}]];

	Do[	disfree	= Table[1, {ng}, {nac[[1]]}];

		Do[	npopres[[5, 1, scen, n]] -=
				priskjoint[[scen, n, All, ri]] disfree RMjoint[[scen, n, ri, seq[[d]]]] pdis[[scen, n, seq[[d]]]] *
				DALYwgt0[[disind[[seq[[d]]]]]] hnpop[[scen, n]];

			disfree	*= 1 - RMjoint[[scen, n, ri, seq[[d]]]] pdis[[scen, n, seq[[d]]]],

			{d, nd}],

		{scen, hnscen}, {n, hnstap}, {ri, Length[riskclass]}];
			
printbug["3.14"];

	(* WORST CASE DALY WEIGHTING, ALSO NON-SELECTED DISEASES INCLUDED, OTHERDIS = 1 *)

	seq	= hsort[Table[Plus@@Flatten[DALYwgt0[[d]]], {d, nd0}]];

	Do[	disfree = Table[1, {ng}, {nac[[1]]}];

		Do[	If[(disindinv[[seq[[d]]]] > 0),

				(* SELECTED DISEASE *)

				hpdis = RMjoint[[scen, n, ri, disindinv[[seq[[d]]]]]] pdis[[scen, n, disindinv[[seq[[d]]]]]],

				(* NON-SELECTED DISEASE *)

				hpdis = pdis0nonind[[seq[[d]]]]];

			npopres[[5, 2, scen, n]] -= priskjoint[[scen, n, All, ri]] disfree hpdis DALYwgt0[[seq[[d]]]] hnpop[[scen, n]];

			disfree *= 1 - hpdis,

			{d, nd0}],

		{scen, hnscen}, {n, hnstap}, {ri, Length[riskclass]}];

printbug["3.15"];

	(* WORST CASE DALY WEIGHTING, ALSO NON-MODELD DISEASES INCLUDED, OTHERDIS = 2 *)

	seq	= hsort[Join[Table[Plus@@Flatten[DALYwgt0[[d]]], {d, nd0}], Table[Plus@@Flatten[nonmodelDALYwgt0[[d]]], {d, ndoth}]]];

	Do[	disfree = Table[1, {ng}, {nac[[1]]}];

		Do[	If[(seq[[d]] <= nd0),

				If[(disindinv[[seq[[d]]]] > 0),
				
					(* SELECTED DISEASE *)

					hpdis = RMjoint[[scen, n, ri, disindinv[[seq[[d]]]]]] pdis[[scen, n, disindinv[[seq[[d]]]]]],

					(* NON-SELECTED DISEASE *)

					hpdis = pdis0nonind[[seq[[d]]]]];

				npopres[[5, 3, scen, n]] -=
						priskjoint[[scen, n, All, ri]] disfree hpdis DALYwgt0[[seq[[d]]]] hnpop[[scen, n]],

				(* NON-MODELED DISEASE *)

				hpdis = pdis0nonmod[[seq[[d]] - nd0]];

				npopres[[5, 3, scen, n]] -= priskjoint[[scen, n, All, ri]] disfree hpdis nonmodelDALYwgt0[[seq[[d]] - nd0]] *
								hnpop[[scen, n]]];
		
			disfree *= 1 - hpdis,

			{d, nd0 + ndoth}],

		{scen, hnscen}, {n, hnstap}, {ri, Length[riskclass]}];				

printbug["3.16"];

	Transpose[npopres, {1, 2, 3, 5, 4, 6}]
			
	]; (* END MAKEQALY0 *)


(* --------------------------------------------------
	MARGINAL MODEL: COSTS
----------------------------------------------------*)

makecosts0[result_] := Block[{},

printbug["3.17"];

	hnpop 	= Table[Plus@@result[[1, scenlist[[scen]]]], {scen, hnscen}] / ndrawinput;

	pdis	= Table[Plus@@result[[4, scenlist[[scen]], n, d, g]] / (Plus@@Flatten[result[[1, scenlist[[scen]], n, g]]] + eps),
			{scen, hnscen}, {n, hnstap}, {d, nd}, {g, ng}];

	ncosts	= Table[0, {3}];

(* COSTS OF SELECTED DISEASES *)

	ncosts[[1]] = Table[hnpop[[scen, n]] Plus@@(pdis[[scen, n]] costspatient0[[disind]]), {scen, hnscen}, {n, nstap}];

(* + COSTS OF NON-SELECTED DISEASES *)

	ncosts[[2]] = ncosts[[1]] +
			Table[hnpop[[scen, n]] Plus@@(pdis0[[nondisind]] costspatient0[[nondisind]]), {scen, hnscen}, {n, nstap}];

(* + COSTS OF NON-MODELED DISEASES *)

	costspersonoth = costsperson0[[29]] - Plus@@Drop[costsperson0, -1];

	ncosts[[3]] = ncosts[[2]] + Table[hnpop[[scen, n]] costspersonoth, {scen, hnscen}, {n, nstap}];

	ncosts];

(* --------------------------------------------------
	JOINT MODEL: COSTS
----------------------------------------------------*)

makecosts1 := Block[{},

printbug["3.21"];

	ncosts	= Table[0, {3}];

(* COSTS OF SELECTED DISEASES *)

printbug["3.22"];

	ncosts[[1]] = Table[Plus@@Table[Plus@@hnprev[[scen, n, g, zinddis[[d, 2]]]] costspatient0[[disind[[d]], g]], {d, nd}],
				{scen, hnscen}, {n, nstap}, {g, ng}];

(* + COSTS OF NON-SELECTED DISEASES *)

printbug["3.23"];

	ncosts[[2]] = ncosts[[1]] +
			Table[hnpop[[scen, g, n]] Plus@@(pdis0[[nondisind, g]] costspatient0[[nondisind, g]]),
				{scen, hnscen}, {n, nstap}, {g, ng}];

(* + COSTS OF NON-MODELED DISEASES *)

printbug["3.24"];

	costspersonoth = costsperson0[[29]] - Plus@@Drop[costsperson0, -1];

	ncosts[[3]] = ncosts[[2]] + Table[hnpop[[scen, g, n]] costspersonoth[[g]], {scen, hnscen}, {n, nstap}, {g, ng}];

	ncosts];
		

(* --------------------------------------------------
	JOINT MODEL: QALY WEIGHTING IN CASE OF PROPORTIONAL AND ADDITIVE MODEL
----------------------------------------------------*)

printbug["1"];

makeQALY1[disweight_, otherdis_] := Block[{},

printbug["1.3"];

	(* QALY VALUES FOR EACH JOINT MODEL STATE *)

	QALYjoint = Table[1, {ng}, {nz1}, {nac[[1]]}];

	(* CALCULATION IN CASE OF DISEASE-FREE *)

	If[(disweight == 2),

		Do[	QALYjoint[[All, zinddis[[d, 2]]]] *= 0, {d, nd}]];

	(* CALCULATION IN CASE OF PROPORTIONAL WEIGHTING *)

	If[(disweight == 3),

		Do[	QALYjoint[[g, zinddis[[d, 2, di]]]] *= 1 - DALYwgt0[[disind[[d]], g]],
			{g, ng}, {d, nd}, {di, Length[zinddis[[d, 2]]]}]];

	(* CALCULATION IN CASE OF ADDITIVE WEIGHTING *)

	If[(disweight == 4),

		Do[	QALYjoint[[g, zinddis[[d, 2, di]]]] -= DALYwgt0[[disind[[d]], g]],
			{g, ng}, {d, nd}, {di, Length[zinddis[[d, 2]]]}]];

	(* CALCULATION OF CURRENT POPULATION QALY VALUES *)

	resQALY	= Table[Plus@@(hnprev[[scen, n, g]] QALYjoint[[g]]), {scen, hnscen}, {g, ng}, {n, hnstap}];

	(* INCLUSION OF NON-SELECTED DISEASES *)

	If[(otherdis >= 2),

		(* CALCULATION IN CASE OF DISEASE-FREE *)

		If[(disweight == 2),

			hresQALY = Times@@(1 - pdis0[[nondisind]]);

			Do[resQALY[[scen, g, n]] *= hresQALY[[g]], {scen, hnscen}, {g, ng}, {n, hnstap}]];

		(* CALCULATION IN CASE OF PROPORTIONAL WEIGHTING *)

		If[(disweight == 3), 

			hresQALY = Times@@(1 - pdis0[[nondisind]] DALYwgt0[[nondisind]]);

			Do[resQALY[[scen, g, n]] *= hresQALY[[g]], {scen, hnscen}, {g, ng}, {n, hnstap}]];

		(* CALCULATION IN CASE OF ADDITIVE WEIGHTING *)

		If[(disweight == 4),

			hresQALY = Plus@@(pdis0[[nondisind]] DALYwgt0[[nondisind]]); 

			Do[resQALY[[scen, g, n]] -= hresQALY[[g]] hnpop[[scen, g, n]], {scen, hnscen}, {g, ng}, {n, hnstap}]]];

	(* INCLUSION OF NON-MODELED DISEASES *)

	If[(otherdis >= 3),

printbug["1.4"];
		(* CALCULATION IN CASE OF DISEASE-FREE *)

		If[(disweight == 2),

			hresQALY = Times@@(1 - nonmodelpdis0);
			Do[resQALY[[scen, g, n]] *= hresQALY[[g]], {scen, hnscen}, {g, ng}, {n, hnstap}]];

		(* CALCULATION IN CASE OF PROPORTIONAL WEIGHTING *)

		If[(disweight == 3),

			hresQALY = Times@@(1 - nonmodelpdis0 nonmodelDALYwgt0);
			Do[resQALY[[scen, g, n]] *= hresQALY[[g]], {scen, hnscen}, {g, ng}, {n, hnstap}]];

		(* CALCULATION IN CASE OF ADDITIVE WEIGHTING *)

		If[(disweight == 4),

			hresQALY = Plus@@(nonmodelpdis0 nonmodelDALYwgt0);
			Do[resQALY[[scen, g, n]] -= hresQALY[[g]] hnpop[[scen, g, n]], {scen, hnscen}, {g, ng}, {n, hnstap}]]];

	resQALY

	]; (* END MAKEQALY1 *)


(* --------------------------------------------------
	QALY WEIGHTING IN CASE OF WORST CASE MODEL
----------------------------------------------------*)

printbug["2"];

makeQALY2[otherdis_] := Block[{},

	disrest	= Range[nz1];

	(* ORDERING OF DISEASES BY QALY VALUE *)

	seq = Switch[otherdis,

		1, hsort[Table[Plus@@Flatten[DALYwgtsel[[d]]], {d, nd}]],
		2, hsort[Table[Plus @@ Flatten[DALYwgt0[[d]]], {d, nd0}]],
		3, hsort[Join[Table[Plus@@Flatten[DALYwgt0[[d]]], {d, nd0}], Table[Plus@@Flatten[nonmodelDALYwgt0[[d]]], {d, ndoth}]]]];

	(* INITIAL VALUES *)

	resQALY	= hnpop;

	pdisfree = Table[1, {ng}, {nac[[1]]}];

	(* CHANGE OF QALY VALUES DUE TO (ORDERED) DISEASE *)

	Do[	If[MemberQ[disind, seq[[d]]],

printbug["2.2"];
			(* SELECTED DISEASE *)

			disspec = Intersection[disrest, zinddis[[disindinv[[seq[[d]]]], 2]]];
			disrest	= Complement[disrest, zinddis[[disindinv[[seq[[d]]]], 2]]];

			Do[resQALY[[scen, g, n]] -= DALYwgt0[[seq[[d]], g]] pdisfree[[g]] Plus@@hnprev[[scen, n, g, disspec]],
				{scen, hnscen}, {g, ng}, {n, hnstap}],

			If[(seq[[d]] <= nd0),

				(* NON-SELECTED DISEASE *)

				Do[resQALY[[scen, g, n]] -=
					DALYwgt0[[seq[[d]], g]] pdisfree[[g]] pdis0[[seq[[d]], g]] Plus@@hnprev[[scen, n, g, disrest]],
					{scen, hnscen}, {g, ng}, {n, hnstap}];

				pdisfree *= 1 - pdis0[[seq[[d]]]],

				(* NON-MODELED DISEASE *)

				Do[resQALY[[scen, g, n]] -=
					nonmodelDALYwgt0[[seq[[d]] - nd0, g]] nonmodelpdis0[[seq[[d]] - nd0, g]] pdisfree[[g]] *
						Plus@@hnprev[[scen, n, g, disrest]],
					{scen, hnscen}, {g, ng}, {n, hnstap}];

				pdisfree *= 1 - nonmodelpdis0[[seq[[d]] - nd0]]]
		],

		{d, Length[seq]}];

	resQALY

	]; (* END MAKEQALY2 *)


(* --------------------------------------------------
	DISCOUNTING AND CUMULATIVE
----------------------------------------------------*)

makepopQALY := Block[{},

	hpopQALY	= Transpose[popQALY, {3, 4, 5, 6, 7, 1, 2}];

	(* DISCOUNTING *)

	If[(discounte > eps),	Do[hpopQALY[[n]] /= (1 + discounte)^(n - 1), {n, hnstap}]];

	(* CUMULATIVE *)

	If[(cumulative == 1),	hpopQALY = Table[Plus@@hpopQALY[[Range[n]]], {n, hnstap}]];

	(* AGGREGATION OVER AGE *)

	hpopQALY	= Table[Plus@@hpopQALY[[n]], {n, nstap}];
	hpopQALY	= Transpose[hpopQALY, {6, 1, 5, 4, 3, 2}];

	(* FOR OTHER SCENARIOS, DIFFERENCE WITH BASELINE SCENARIO *)

	Do[hpopQALY[[m, g, scen]] -= hpopQALY[[m, g, 1]], {m, nm}, {g, ng}, {scen, 2, hnscen}];

	hpopQALY];

makepopcosts := Block[{},

	hpopcosts	= Transpose[popcosts, {3, 4, 5, 1, 6, 2}];

	(* DISCOUNTING *)

	If[(discountc > eps), Do[hpopcosts[[n]] /= (1 + discountc)^(n - 1), {n, hnstap}]];

	(* CUMULATIVE *)

	If[(cumulative == 1), hpopcosts = Table[Plus@@hpopcosts[[Range[n]]], {n, hnstap}]];

	(* AGGREGATION OVER AGE *)

	hpopcosts	= Table[Plus@@hpopcosts[[n]], {n, nstap}];

	hpopcosts 	= Transpose[hpopcosts, {5, 4, 3, 1, 2}];

	(* FOR OTHER SCENARIOS, DIFFERENCE WITH BASELINE SCENARIO *)

	Do[hpopcosts[[scen]] -= hpopcosts[[1]], {scen, 2, hnscen}];

	hpopcosts];

makepopratio := Table[popcosts[[scen, g, od, m]] / (popQALY[[m, g, scen, od, w]] + eps),
			{scen, hnscen}, {g, ng}, {od, 3}, {m, nm}, {w, 3, 5}];


(* --------------------------------------------------
	MAIN ROUTINE
----------------------------------------------------*)

hmodelsel	= Flatten[Position[modelsel, 1]];
hmodelnames	= {"marginal", "joint pop", "joint age", "joint ind"}[[hmodelsel]];

popQALY		= Table[0, {nm}, {5}, {3}];
popcosts	= Table[0, {nm}];


Do[	(* READING PREVALENCE DATA *)

	modind = 0;

printbug["5"];

	If[(hmodelsel[[m]] == 1),

		(* MARGINAL MODEL *)

		result 		= leesprev1;
		hnscen		= result[[1, 1]];
		hnstap		= result[[1, 2]];
		result		= result[[2, 1]];
		popQALY[[m]] 	= makeQALY0[result];
		popcosts[[m]]	= makecosts0[result],

		(* JOINT MODELS *)

		hnprev	= Switch[hmodelsel[[m]],	2,	leesprevpop,
							3,	leesprevage,
							4,	leesprevind];

		hnscen	= hnprev[[1, 1]];
		hnstap	= hnprev[[1, 3]];
		hnprev	= hnprev[[2]];
		
		hnpop	= Table[Plus@@hnprev[[scen, n, g]], {scen, hnscen}, {g, ng}, {n, hnstap}];

		Do[	If[(w < 5),	popQALY[[m, w, od]] = makeQALY1[w, od]];
			If[(w == 5), 	popQALY[[m, w, od]] = makeQALY2[od]],
			{w, 5}, {od, 3}];

		popcosts[[m]] = makecosts1		

		],
	
	{m, nm}];

popQALY		= makepopQALY;
popcosts	= makepopcosts;
ratio		= makepopratio;


(* --------------------------------------------------
		MMA OUTPUT FILE
----------------------------------------------------*)

hnbout = NotebookCreate[];
	
cellnb		= {headingprintnb["generic output numbers"]};

addcellnb[cell_] := cellnb = Flatten[{cellnb, cell}];

printbug["6.1"];

(* weighted population numbers *)

addcellnb[headingprint2nb["weighted population numbers"]];

Table[	addcellnb[headingprint3nb["diseases included: " <> disselnames[[od]] <> ", gender: "<> gennames[[g]] <>
					", scenario: " <> ToString[scen]]];

	addcellnb[Cell[	GraphicsData["PostScript", DisplayString[GraphicsArray[
				Table[makemultiplelistplot[popQALY[[m, g, scen, od]],
								{plotset1,
								PlotLegend	-> weightnames,
								PlotLabel	-> hmodelnames[[m]]}],
					{m, nm}]]]],
			"Subsection", 
               		ImageSize -> nm imagesize]],
	{od, 3}, {g, ng}, {scen, hnscen}];

Table[	addcellnb[headingprint3nb[weightnames[[w]] <> ", gender: " <> gennames[[g]] <> ", scenario: " <> ToString[scen]]];
	addcellnb[Cell[	GraphicsData["PostScript", DisplayString[GraphicsArray[
				Table[makemultiplelistplot[popQALY[[All, g, scen, od, w]],
								{plotset1,
								PlotLegend	-> hmodelnames,
								PlotLabel	-> disselnames[[od]]}],
					{od, 3}]]]],
			"Subsection",
			ImageSize -> 3 imagesize]],
	{w, 5}, {g, ng}, {scen, hnscen}];

printbug["6.2"];

(* costs *)

addcellnb[headingprint2nb["costs"]];

Table[	addcellnb[headingprint3nb["gender: "<> gennames[[g]] <> ", scenario: " <> ToString[scen]]];
	addcellnb[Cell[	GraphicsData["PostScript", DisplayString[GraphicsArray[
				Table[makemultiplelistplot[popcosts[[scen, g, od]],
								{plotset1,
								PlotLegend 	-> hmodelnames,
								PlotLabel 	-> disselnames[[od]]}],
					{od, 3}]]]],
			"Subsection", 
               		ImageSize -> 3 imagesize]],
	{g, ng}, {scen, hnscen}];

printbug["6.3"];

(* cost effectiveness ratios *)

addcellnb[headingprint2nb["cost effectiveness ratios"]];

Table[	addcellnb[headingprint3nb["diseases included: " <> disselnames[[od]] <> ", gender: "<> gennames[[g]] <>
						 ", scenario: " <> ToString[scen]]];
	addcellnb[Cell[	GraphicsData["PostScript", DisplayString[GraphicsArray[
				Table[makemultiplelistplot[ratio[[scen, g, od, m]],
								{plotset1,
								PlotLegend 	-> weightnames[[Range[3, 5]]],
								PlotLabel 	-> hmodelnames[[m]]}],
					{m, nm}]]]],
			"Subsection", 
               		ImageSize -> nm imagesize]],
	{od, 3}, {g, ng}, {scen, hnscen}];

NotebookWrite[hnbout, Flatten[cellnb]];

printtijd;


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
