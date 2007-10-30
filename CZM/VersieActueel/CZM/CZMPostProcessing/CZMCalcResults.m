(* :Title: CZMCalcResults *)

(* :Context: CZMPostProcessing` *)

(* :Author: Rudolf T. Hoogenveen, Roel G.M. Breuls, Maiwenn Al *)

(* :Summary:
   CZM postprocessing routine calculates model results *)

(* :Copyright: © 2004 by Rudolf Hoogenveen/Roel Breuls *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March
		3.0 version November 2005 
		3.1 version March 2007; storage of index values, new package CZMStoreResults *)

(* :Keywords: postprocessing, plots *)


BeginPackage["CZMPostProcessing`CZMCalcResults`",
	{"CZMInitialization`CZMLogFile`",
	"CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`", 
	"CZMImportData`CZMImportUserSelections`",
	"CZMImportData`CZMImportRiskFactors`",
	"CZMImportData`CZMImportDiseaseData`",
	"CZMImportData`CZMImportDALYs`",
	"CZMImportData`CZMImportCosts`",
	"CZMAdjustData`CZMMakeSelections`",
	"CZMAdjustData`CZMAdjustAfterSelection`",
	"CZMSimulation`CZMSimulationFunctions`",
	"CZMSimulation`CZMStoreResults`",
	"CZMPostProcessing`CZMExportUserSelections`",
	"Graphics`MultipleListPlot`"}]

mortnames::usage	= "list of disease names extended with other causes and all cause"
scenlist::usage		= "list of scenarios (from 1:nscen) for each user-defined scenario (1:nscen0)"

makeresdiff::usage	= "makeresdiff calculates result of scenarios (diff=0) or differences with baseline scenario (diff=1)"
makenpopagg::usage	= "makenpopagg aggregates model output population numbers unweighted (aggregating over list of scenarios)"
makenpopnonagg::usage	= "makenpopnonagg aggregates model output population numbers unweighted"
makenpop::usage		= "makenpop calculates model output population numbers weighted"
npopdiscount::usage	= "npopdiscount discounts calculated future population numbers"
makenrisk::usage	= "makenrisk aggregates model discrete risk factor class prevalence numbers"
makedist::usage		= "makedist aggregates model continuous risk factor distribution characteristics"
makendis::usage		= "makendis aggregates model disease prevalence numbers output"
makencosts::usage	= "makencosts calculates costs from calculated population and disease prevalence numbers"
ncostsdiscount::usage	= "ncostsdiscount discounts calculated future costs"

headingprint::usage	= "headingprint: prints text at level 0"
headingprint1::usage	= "headingprint1: prints text at level 1"
headingprint2::usage	= "headingprint2: prints text at level 2"
headingprint3::usage	= "headingprint3: prints text at level 3"
headingprintnb::usage	= "headingprintnb: prints text at level 0 in notebook"
headingprint1nb::usage	= "headingprint1nb: prints text at level 1 in notebook"
headingprint2nb::usage	= "headingprint2nb: prints text at level 2 in notebook"
headingprint3nb::usage	= "headingprint3nb: prints text at level 3 in notebook"

scenlist::usage		= "scenlist[scen]: list of random samples for each scenario"
scenname::usage		= "scenname: prints specification of scenario"
plotheading::usage	= "plotheading: prints 'simulation results'"
stdtextstyle::usage	= "standard text style"

nbout::usage		= "Notebook containing model results"

resjointmodelpopprev::usage = "resjointmodelpopprev[[scen,nstap,ng,nz1,na]]: results of joint deterministic population model"
resjointmodelageprev::usage = "resjointmodelageprev[[scen,nstap,ng,nz1,na]]: results of joint deterministic model stratified by age"
resjointmodelindprev::usage = "resjointmodelindprev[[scen,nstap,ng,nz1,na]]: results of joint deterministic model stratified by individuals"

makemultiplelistplot::usage = "makemultiplelistplot constructs multiplelistplots for variable number of lists"


Begin["`Private`"]


Print["CZMCalcResults package is evaluated"];
printtijd;

printbug[c_] := If[(bugind == 1), Print[{"CZMCalcResults", c}]];


(*-------------------------------------------------
	GENERAL PLOT ROUTINED
---------------------------------------------------*)

printbug["1."];

headingprint[text_] := Block[{},
				Print[];
				StylePrint[	text,
						FontFamily 	-> "Helvetica",
						FontSize 	-> 32,
						Background 	-> RGBColor[1, 1, 1],
						TextAlignment 	-> "Center",
			 			FontWeight 	-> "Bold"
					]
				];

headingprintnb[text_] := Cell[	text,
				"Title",
				FontFamily 	-> "Helvetica",
				FontSize 	-> 32,
				Background 	-> RGBColor[.6, .8, .8],
				TextAlignment 	-> "Center",
				FontWeight 	-> "Bold"
				];

headingprint1[text_] := Block[{},
				Print[];
				StylePrint[	text,
						FontFamily 	-> "Helvetica",
						FontSize 	-> 16, 
						Background 	-> RGBColor[0.6, 0.8, 0.8],
						FontWeight 	-> "Bold"
					]
				];

headingprint1nb[text_] := Cell[	text,
				"Subtitle",
				FontFamily 	-> "Helvetica",
				FontSize 	-> 16, 
				Background 	-> RGBColor[.6, .8, .8],
				FontWeight 	-> "Bold"
				];

headingprint2[text_] := Block[{},
				StylePrint[	"    "<> text,
						FontFamily 	-> "Helvetica", 
						FontSize 	-> 14,
						Background	-> None,
						FontWeight 	-> "Bold"
					]
				];

headingprint2nb[text_] := Cell[	text,
				"Section",
				FontFamily 	-> "Helvetica", 
				FontSize 	-> 14,
				Background	-> RGBColor[.6, .8, .8],
				FontWeight 	-> "Bold"
				];

headingprint3[text_] := Block[{},
				Print[];
				StylePrint[	"         "<> text,
						FontFamily 	-> "Helvetica", 
						FontSize 	-> 12,
						Background 	-> None,
						FontSlant 	-> "Italic"
					]
				];

headingprint3nb[text_] := Cell[	text,
				"Subsection",
				FontFamily 	-> "Helvetica", 
				FontSize 	-> 12,
				Background 	-> None,
				FontSlant 	-> "Italic"
				];

makemultiplelistplot[dat_, form_] :=

	Switch[Length[dat],

		1,	MultipleListPlot[dat[[1]], form],
		2,	MultipleListPlot[dat[[1]], dat[[2]], form],
		3,	MultipleListPlot[dat[[1]], dat[[2]], dat[[3]], form],
		4,	MultipleListPlot[dat[[1]], dat[[2]], dat[[3]], dat[[4]], form],
		5,	MultipleListPlot[dat[[1]], dat[[2]], dat[[3]], dat[[4]], dat[[5]], form],
		6,	MultipleListPlot[dat[[1]], dat[[2]], dat[[3]], dat[[4]], dat[[5]], dat[[6]], form],
		7,	MultipleListPlot[dat[[1]], dat[[2]], dat[[3]], dat[[4]], dat[[5]], dat[[6]], dat[[7]], form],
		8,	MultipleListPlot[dat[[1]], dat[[2]], dat[[3]], dat[[4]], dat[[5]], dat[[6]], dat[[7]], dat[[8]], form],
		9,	MultipleListPlot[dat[[1]], dat[[2]], dat[[3]], dat[[4]], dat[[5]], dat[[6]], dat[[7]], dat[[8]], dat[[9]], form]

		];
				

plotheading 	:= headingprint["simulation results"];

scenname[scen_, diffscen_] := "scenario " <> ToString[scen + diffscen] <> If[(diffscen == 1), " compared to baseline scenario"," "];
stdtextstyle 	= {FontFamily -> "Helvetica", FontSize -> 11};

mortnames	= Flatten[{disnames[[disind]], "other causes", "all cause"}];
scenlist	= Table[nscen0 (Range[ndrawinput] - 1) + scen, {scen, nscen0}];


(* --------------------------------------------------
	GENERAL ROUTINES OF CALCULATING OUTPUT NUMBERS
----------------------------------------------------*)

printbug["2."];

makeresdiff[plusnres_, diffscen_] := (*Block[{},*)

	If[(diffscen == 0),

		(* FOR BASELINE SCENARIO *)

		plusnres,

		(* DIFFERENCES WITH BASELINE SCENARIO *)

		Drop[plusnres, 1] - Table[plusnres[[1]], {nscen0 - 1}]]

	(*];*)


(* --------------------------------------------------
	GENERAL ROUTINES OF CALCULATING OUTPUT NUMBERS: TOTAL POPULATION NUMBERS (FORMAT)
----------------------------------------------------*)

(* POPULATION NUMBERS & RATES: AGGREGATED OVER AGE OR SPECIFIED BY AGE CLASS DIRECTLY ON MODEL RESULTS *)

printbug["2.1"];
			
makenpopnonagg[npop_, diffscen_] := Block[{},

	plusnpop = If[(agespecres <= 1),

			(* AGGREGATED OVER AGE *)

			Table[Plus@@Flatten[npop[[scen, n, g]]], {scen, nscen0}, {g, ng}, {n, nstap}],

			(* SPECIFIED BY AGE CLASS *)

			Table[aggregc[npop[[scen, n, g]], 1, 7], {scen, nscen0}, {g, ng}, {n, nstap}]];

	makeresdiff[plusnpop, diffscen]
	
	]; (* END MAKENPOPNONAGG *)

(* POPULATION NUMBERS & RATES: AGGREGATED OVER AGE OR SPECIFIED BY AGE CLASS INCLUDING AGGREGATING OVER LIST OF SCENARIOS *)

printbug["2.2"];

makenpopagg[npop_, diffscen_] := Block[{},

	plusnpop = If[(agespecres <= 1),

			(* AGGREGATED OVER AGE *)

			Table[Plus@@Flatten[npop[[scenlist[[scen]], n, g]]], {scen, nscen0}, {g, ng}, {n, nstap}],

			(* SPECIFIED BY AGE CLASS *)

			Table[aggregc[Plus@@npop[[scenlist[[scen]], n, g]], 1, 7], {scen, nscen0}, {g, ng}, {n, nstap}]

			] / ndrawinput;

	makeresdiff[plusnpop, diffscen]
		
	]; (* END MAKENPOPAGG *)

(* # DELETE 120406 JACK
(* --------------------------------------------------
	GENERAL ROUTINES OF CALCULATING OUTPUT NUMBERS: WEIGHTED TOTAL POPULATION NUMBERS
----------------------------------------------------*)

printbug["3."];

makenpop[result_, diffscen_] := Block[{},

	(* TOTAL POPULATION NUMBERS *)

printbug["3.1"];

	npop1 	= Table[Plus@@result[[1, scenlist[[scen]]]], {scen, nscen0}] / ndrawinput;

printbug["3.2"];

	(* DISCRETE RISK FACTOR CLASS PREVALENCE RATES *)

printbug["3.3"];

	prisk	= Table[Plus@@result[[2, scenlist[[scen]], n, r, g, ri]] / (Plus@@Flatten[result[[2, scenlist[[scen]], n, r, g]]] + eps),
			{scen, nscen0}, {n, nstap}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}] / ndrawinput;

	(* CONTINUOUS RISK FACTOR DISTRIBUTION CHARACTERISTICS *)

printbug["3.4"];

	pcont	= Table[Plus@@result[[3, scenlist[[scen]], n, r, g, ri]] / (npop1[[scen, n, g]] + eps),
			{scen, nscen0}, {n, nstap}, {r, nrc}, {g, ng}, {ri, 2}] / ndrawinput;

	pcont	= Table[{vect1, pcont[[scen, n, r, g, 1]], pcont[[scen, n, r, g, 1]]^2 + pcont[[scen, n, r, g, 2]]},
			{scen, nscen0}, {n, nstap}, {r, nrc}, {g, ng}];

	(* DISEASE PREVALENCE RATES *)

printbug["3.5"];

	pdis	= Table[Plus@@result[[4, scenlist[[scen]], n, d, g]] / (Plus@@Flatten[result[[1, scenlist[[scen]], n, g]]] + eps),
			{scen, nscen0}, {n, nstap}, {d, nd}, {g, ng}];

printbug["3.6"];

	If[(disweighting > 0) && (heterogeneity == 1), 

		(* DISEASE PREVALENCE RATES IN JOINT RISK FACTOR CLASSES *)

		priskjoint = Table[Times@@Table[prisk[[scen, n, r, g, riskclass[[ri, r]]]], {r, nrd}],
					{scen, nscen0}, {n, nstap}, {g, ng}, {ri, Length[riskclass]}];

		RMrisk	= Table[meanaggreg[RRrisksel[[r, d, g, ri]]],
					{r, nrd}, {d, Length[RRrisksel[[r]]]}, {g, ng}, {ri, ncrsel[[r]]}];

		RMrisk	= Table[RMrisk[[r, d, g, ri]] / (Plus@@(RMrisk[[r, d, g]] prisk[[scen, n, r, g]]) + eps),
					{scen, nscen0}, {n, nstap}, {r, nrd}, {d, Length[RMrisk[[r]]]}, {g, ng}, {ri, ncrsel[[r]]}];

		RMcont	= Table[meanaggreg[RRcontsel[[r, d, g, ri]]],
					{r, nrc}, {d, Length[RRcontsel[[r]]]}, {g, ng}, {ri, 3}];

		RMcont	= Table[RMcont[[r, d, g, ri]] / (Plus@@(RMcont[[r, d, g]] pcont[[scen, n, r, g]]) + eps),
					{scen, nscen0}, {n, nstap}, {r, nrc}, {d, Length[RMcont[[r]]]}, {g, ng}, {ri, 3}];
		
		pdisjoint = Table[Times@@Table[RMrisk[[scen, n, r, RRriskindsel[[r, d + 1]], g, riskclass[[ri, r]]]], {r, nrd}] *
						pdis[[scen, n, d, g]],
					 {scen, nscen0}, {n, nstap}, {ri, Length[riskclass]}, {d, nd}, {g, ng}];

		];

printbug["3.7"];

	Switch[disweighting,

		(* DISEASE-FREE YEARS *)

		1,

		If[(heterogeneity == 0),

			Do[npop1[[scen, n]] *=
					Times@@(1 - pdis[[scen, n, Range[nd]]]), {scen, nscen0}, {n, nstap}],

			Do[npop1[[scen, n, g]] *=
					Plus@@Table[priskjoint[[scen, n, g, ri]] Times@@(1 - pdisjoint[[scen, n, ri, Range[nd], g]]),
						{ri, Length[riskclass]}],
					{scen, nscen0}, {n, nstap}, {g, ng}]

			];

		(* OTHER DISEASES INCLUDED *)

		Do[npop1[[scen, n]] *=
				(1 + otherdis (Times@@Table[1 - pdis0[[nondisind[[d]]]], {d, nd0 - nd}] Times@@(1 - nonmodelpdis0) - 1)),
				{scen, nscen0}, {n, nstap}],
		
		(* PROPORTIONAL DALY WEIGHTING *)

		2,

		If[(heterogeneity == 0),

			Do[npop1[[scen, n]] *=
					Times@@Table[1 - DALYwgt0[[disind[[d]]]] pdis[[scen, n, d]], {d, nd}],
					{scen, nscen0}, {n, nstap}],

			Do[npop1[[scen, n, g]] *=
					Plus@@Table[
						priskjoint[[scen, n, g, ri]] *
						Times@@Table[1 - DALYwgt0[[disind[[d]], g]] pdisjoint[[scen, n, ri, d, g]], {d, nd}],
						{ri, Length[riskclass]}],
					{scen, nscen0}, {n, nstap}, {g, ng}]

			];

		(* OTHER DISEASES INCLUDED *)

		Do[npop1[[scen, n]] *=
				(1 + otherdis *
					(Times@@Table[1 - DALYwgt0[[nondisind[[d]]]] pdis0[[nondisind[[d]]]], {d, nd0 - nd}] *
					Times@@(1 - nonmodelDALYwgt0 nonmodelpdis0) - 1)),
				{scen, nscen0}, {n, nstap}],
					
		(* ADDITIVE DALY WEIGHTING *)

		3,

		Do[npop1[[scen, n]] *=
				(1 - 	Plus@@Table[DALYwgt0[[disind[[d]]]] pdis[[scen, n, d]], {d, nd}] -
					otherdis Plus@@Table[DALYwgt0[[nondisind[[d]]]] pdis0[[nondisind[[d]]]], {d, nd0 - nd}] -
					otherdis Plus@@(nonmodelDALYwgt0 nonmodelpdis0)),
				{scen, nscen0}, {n, nstap}],

		(* WORST DISEASE DALY WEIGHTING *)

		4,

		If[(otherdis == 0),

			(* ONLY MODELED DISEASES INCLUDED *)

			seq	= Ordering[Table[Plus@@Flatten[DALYwgtsel[[d]]], {d, nd}]];

			If[(heterogeneity == 0),

				Do[npop1[[scen, n]] *=
						(1 - 
						Plus@@Table[DALYwgt0[[disind[[seq[[d]]]]]] pdis[[scen, n, seq[[d]]]] *
									Times@@(1 - pdis[[scen, n, Range[seq[[d]] - 1]]]),
								{d, nd}]),
						{scen, nscen0}, {n, nstap}],

				
				Do[npop1[[scen, n, g]] *=
						Plus@@Table[
							priskjoint[[scen, n, g, ri]] *
							(1 -
							Plus@@Table[DALYwgt0[[disind[[seq[[d]]]], g]] *
										pdisjoint[[scen, n, ri, seq[[d]], g]] *
										Times@@(1 - pdisjoint[[scen, n, ri, Range[seq[[d]] - 1], g]]),
									{d, nd}]),
							{ri, Length[riskclass]}],
						{scen, nscen0}, {n, nstap}, {g, ng}]

				],

			(* ALSO NON-MODELED DISEASES INCLUDED *)

			seq = Ordering[Join[Table[Plus@@Flatten[DALYwgt0[[d]]], {d, nd0}],
						Table[Plus@@Flatten[nonmodelDALYwgt0[[d]]], {d, ndoth}]]];

			If[(heterogeneity == 0),

				(* HOMOGENEOUS POPULATION *)

				hpdis = Table[If[(seq[[d]] <= nd0),
							If[(disindinv[[seq[[d]]]] > 0),
								pdis[[Range[nscen], Range[nstap], disindinv[[seq[[d]]]]]],
								Table[pdis0[[seq[[d]]]], {nscen0}, {nstap}]
								],
							Table[nonmodelpdis0[[seq[[d]] - nd0]], {nscen0}, {nstap}]
							],
						{d, nd0 + ndoth}];

				Do[npop1[[scen, n]] *=
						(1 -
						Plus@@Table[DALYwgtall[[seq[[d]]]] hpdis[[d, scen, n]] *
									Times@@(1 - hpdis[[Range[d - 1], scen, n]]),
								{d, nd0 + ndoth}]),
						{scen, nscen0}, {n, nstap}],

				(* HETEROGENEOUS POPULATION *)

				hpdis = Table[If[(seq[[d]] <= nd0),
						If[(disindinv[[seq[[d]]]] > 0),
							Table[pdisjoint[[scen, n, ri, disindinv[[seq[[d]]]]]],
								{scen, nscen0}, {n, nstap}, {ri, Length[riskclass]}],
							Table[pdis0[[seq[[d]]]], {nscen0}, {nstap}, {Length[riskclass]}]
							],
						Table[nonmodelpdis0[[seq[[d]] - nd0]], {nscen0}, {nstap}, {Length[riskclass]}]
						],
						{d, nd0 + ndoth}];

				Do[npop1[[scen, n, g]] *=
						Plus@@Table[
							priskjoint[[scen, n, g, ri]] *
							(1 -
							Plus@@Table[DALYwgtall[[seq[[d]], g]] hpdis[[d, scen, n, ri, g]] *
										Times@@(1 - hpdis[[Range[d - 1], scen, n, ri, g]]),
									{d, nd0 + ndoth}]),
							{ri, Length[riskclass]}],
						{scen, nscen0}, {n, nstap}, {g, ng}]

				]

			]

		];

printbug["3.8"];

	makenpopnonagg[npop1, diffscen]
			
	]; (* END MAKENPOP *)
# *)

(* # NEW #*)

(* --------------------------------------------------
	GENERAL ROUTINES OF CALCULATING OUTPUT NUMBERS: TOTAL POPULATION NUMBERS
----------------------------------------------------*)

printbug["3."];

makenpop[result_, diffscen_] := Block[{},

	(* TOTAL POPULATION NUMBERS *)

	npop1 	= Table[Plus@@result[[1, scenlist[[scen]]]], {scen, nscen0}] / ndrawinput;

	makenpopnonagg[npop1, diffscen]
			
	]; (* END MAKENPOP *)

(* --------------------------------------------------
	GENERAL ROUTINES OF CALCULATING OUTPUT NUMBERS: DISCOUNTING WEIGHTED FUTURE TOTAL POPULATION NUMBERS
----------------------------------------------------*)

printbug["4."];

npopdiscount[npop_, diffscen_, discount_, standardnpop_] := Block[{},

	npop1 = npop;

	(* ONLY REFERENCE SCENARIO *)

printbug["4.1"];

	If[(diffscen == 0), npop1 = npop1[[{1}]]];

	(* STANDARDIZED TO INITIAL POPULATION SIZE 1 *)

printbug["4.2"];

	If[(standardized == 1),
		npop1 = Table[npop1[[scen, g, n]] / standardnpop[[scen, g]], {scen, Length[npop1]}, {g, ng}, {n, nstap}]];

	(* FUTURE NUMBERS DISCOUNTED *)

printbug["4.3"];

	If[(discount > eps),
		npop1 = Table[npop1[[scen, g, n]] / (1 + discount)^(n - 1), {scen, Length[npop1]}, {g, ng}, {n, nstap}]];

	(* CUMULATIVE *)

printbug["4.4"];

	If[(cumulative == 1),
		npop1 = Table[Plus@@npop1[[scen, g, Range[n]]], {scen, Length[npop1]}, {g, ng}, {n, nstap}]];

	npop1

	]; (* END NPOPDISCOUNT *)


(* --------------------------------------------------
	GENERAL ROUTINES OF CALCULATING OUTPUT NUMBERS: RISK FACTOR CLASS PREVALENCE NUMBERS
----------------------------------------------------*)

printbug["5."];

(* (DISCRETE) RISK FACTOR CLASS RATES AND NUMBERS: AGGREGATED OVER AGE OR SPECIFIED BY AGE CLASS,
FOR BASELINE SCENARIO OR DIFFERENCES WITH BASELINE SCENARIO *)

makenrisk[nrisk_, diffscen_] := Block[{},

	plusnrisk = If[(agespecres <= 1),
	
			(* AGGREGATED OVER AGE *)

			Table[Plus@@Flatten[nrisk[[scenlist[[scen]], n, r, g, ri]]],
				{scen, nscen0}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}],

			(* SPECIFIED BY AGE CLASS *)
			
			Table[aggregc[Plus@@nrisk[[scenlist[[scen]], n, r, g, ri]], 1, 7],
				{scen, nscen0}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}]

			] / ndrawinput;

	makeresdiff[plusnrisk, diffscen]

	]; (* END MAKENRISK *)

(* (CONTINUOUS) RISK FACTOR CLASS RATES AND NUMBERS: AGGREGATED OVER AGE OR SPECIFIED BY AGE CLASS,
FOR BASELINE SCENARIO OR DIFFERENCES WITH BASELINE SCENARIO *)

makedist[dist_, diffscen_] := Block[{},

	plusdist = If[(agespecres <= 1),

			(* AGGREGATED OVER AGE *)

			Table[Plus@@Flatten[dist[[scenlist[[scen]], n, r, g, ri]]],
				{scen, nscen0}, {r, nrc}, {g, ng}, {ri, 2}, {n, nstap}],

			(* SPECIFIED BY AGE CLASS *)

			Table[aggregc[Plus@@dist[[scenlist[[scen]], n, r, g, ri]], 1, 7],
				{scen, nscen0}, {r, nrc}, {g, ng}, {ri, 2}, {n, nstap}]

			] / ndrawinput;

	makeresdiff[plusdist, diffscen]

	]; (* END MAKEDIST *)

(* --------------------------------------------------
	GENERAL ROUTINES OF CALCULATING OUTPUT NUMBERS: DISEASE (PREVALENCE, INCIDENCE AND MORTALITY) NUMBERS AND RATES
----------------------------------------------------*)

printbug["6."];

(* DISEASE RATES & NUMBERS: AGGREGATED OVER AGE OR SPECIFIED BY AGE CLASS, FOR BASELINE SCENARIO OR DIFFERENCES WITH BASELINE SCENARIO *)

makendis[ndis_, diffscen_] := Block[{},

	plusndis = If[(agespecres <= 1),

			(* AGGREGATED OVER AGE *)

			Table[Plus@@Flatten[ndis[[scenlist[[scen]], n, d, g]]],
					{scen, nscen0}, {d, Length[ndis[[1, 1]]]}, {g, ng}, {n, nstap}],

			(* SPECIFIED BY AGE CLASS *)

			Table[aggregc[Plus@@ndis[[scenlist[[scen]], n, d, g]], 1, 7],
					{scen, nscen0}, {d, Length[ndis[[1, 1]]]}, {g, ng}, {n, nstap}]

			] / ndrawinput;

	makeresdiff[plusndis, diffscen]

	]; (* END MAKENDIS *)


(* --------------------------------------------------
	GENERAL ROUTINES OF CALCULATING OUTPUT NUMBERS: COSTS
----------------------------------------------------*)

printbug["7."];

makencosts[resmodel_] := Block[{},

	ncosts 	= Table[Join[	Table[costspatient0[[disind[[d]], g]] Plus@@resmodel[[4, scenlist[[scen]], n, d, g]], {d, nd}],
				{otherdis meanaggreg[costspersonothsel[[g]]] Plus@@resmodel[[1, scenlist[[scen]], n, g]]}],
			{scen, nscen0}, {g, ng}, {n, nstap}] / ndrawinput;

	Transpose[Table[Join[ncosts[[scen, g, n]], {Plus@@ncosts[[scen, g, n]]}], {scen, nscen0}, {g, ng}, {n, nstap}], {1, 3, 4, 2, 5}]

	]; (* END MAKENCOSTS *)


(* --------------------------------------------------
	GENERAL ROUTINES OF CALCULATING OUTPUT NUMBERS: DISCOUNTING FUTURE COSTS
----------------------------------------------------*)

printbug["7.1"];

ncostsdiscount[ncosts_, discount_, standardnpop_] := Block[{},

	ncosts1 = ncosts;

	(* STANDARDIZED TO INITIAL POPULATION SIZE 1 *)

	If[(standardized == 1),
		ncosts1 = Table[ncosts1[[scen, d, g, n]] / standardnpop[[scen, g]],
				{scen, nscen0}, {d, nd + 2}, {g, ng}, {n, nstap}]];

	(* FUTURE NUMBERS DISCOUNTED *)

	If[(discount > eps),
		ncosts1 = Table[ncosts1[[scen, d, g, n]] / (1 + discount)^(n - 1),
				{scen, nscen0}, {d, nd + 2}, {g, ng}, {n, nstap}]];

	(* CUMULATIVE *)

	If[(cumulative == 1),
		ncosts1 = Table[Plus@@ncosts1[[scen, d, g, Range[n]]], {scen, nscen0}, {d, nd + 2}, {g, ng}, {n, nstap}]];

	ncosts1

	];
	

(* --------------------------------------------------
		Notebook containing model results
----------------------------------------------------*)

If[(outputnotebook == 1), nbout = NotebookCreate[]];


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
