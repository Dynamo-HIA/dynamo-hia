(* :Title: CZMSensitivityAnalyses *)

(* :Context: CZMPostProcessing` *)

(* :Author: Rudolf T. Hoogenveen, Roel G.M. Breuls, Maiwenn Al *)

(* :Summary:
   CZM postprocessing routine presents results from sensitivity analyses *)

(* :Copyright: © 2004 by Rudolf Hoogenveen *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	2.0 first release CZM 2005, version March
		3.0 version november 2005 
		3.1 version March 2007 *)

(* :Keywords: postprocessing, plots *)


BeginPackage["CZMPostProcessing`CZMSensitivityAnalyses`",
	{"CZMInitialization`CZMLogFile`",
	"CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`", 
	"CZMImportData`CZMImportUserSelections`",
	"CZMImportData`CZMImportCosts`",
	"CZMImportData`CZMImportDALYs`",
	"CZMAdjustData`CZMMakeSelections`",
	"CZMSimulation`CZMSimulationFunctions`",
	"CZMDefineScenarios`CZMDefineScenarios`",
	"CZMSimulation`CZMSimulationMarginalModelDetermPop`",
	"CZMSimulation`CZMSimulationJointModelDetermPop`",
	"CZMSimulation`CZMSimulationJointModelDetermAge`",
	"CZMSimulation`CZMSimulationJointModelStochInd`",
	"CZMPostProcessing`CZMExportUserSelections`",
	"CZMPostProcessing`CZMCalcResults`"}]


Begin["`Private`"]


Print["CZMSensitivityAnalyses package is evaluated"];
printtijd;

printbug[c_] := If[(bugind == 1), Print[{"CZMSensitivityAnalyses", c}]];


(* --------------------------------------------------
	GENERAL ROUTINES
----------------------------------------------------*)

printbug["1."];

(* PLOTTING SETUP VALUES *)

printbug["1.1"];

imagesize	= 175;
stdaxislabel 	= {"time", ""};
stdlabel[r_, ri_] := risknames[[riskindd]][[r]] <> "class " <> ToString[ri];

(* NAMES OF SENSITIVITY INPUT PARAMETERS *)

printbug["1.2"];

riskclasstrans 	= Table[Plus@@Flatten[

				Table[If[(transriskindsel[[r, ri, rj]] == trs),
						{ri, rj},
						{0, 0}],
					{ri, ncrsel[[r]]}, {rj, ncrsel[[r]]}],
				1],
			{r, nrd}, {trs, 2, Max[transriskindsel[[r]]]}];

printbug["1.3"];

scennames = {};

If[(sensparameters[[1]] == 1),

	scennames = Flatten[{scennames,
			Table[risknames[[riskindd[[r]]]] <> " prev " <>	ToString[ri], {r, nrd}, {ri, ncrsel[[r]]}]}]];

If[(sensparameters[[2]] == 1),

	scennames = Flatten[{scennames, Table[risknames[[riskindd[[r]]]] <> ToString[riskclasstrans[[r, ri, 1]]] <> "->" <>
							ToString[riskclasstrans[[r, ri, 2]]],
			 			{r, nrd}, {ri, Length[riskclasstrans[[r]]]}]}]]; 

If[(sensparameters[[3]] == 1),

	scennames = Flatten[{scennames, Table[risknames[[riskindc[[r]]]] <> " mu", {r, nrc}],
						Table[risknames[[riskindc[[r]]]] <> " sigma", {r, nrc}]}]];

If[(sensparameters[[4]] == 1),

	scennames = Flatten[{scennames, Table[risknames[[riskindc[[r]]]] <> " deterministic change (intercept)", {r, nrc}],
						Table[risknames[[riskindc[[r]]]] <> " deterministic change (regression)", {r, nrc}]}]];

If[(sensparameters[[5]] == 1), 

	scennames = Flatten[{scennames, Table[disnames[[disind[[d]]]] <> " incidence", {d, nd}]}]];

If[(sensparameters[[6]] == 1), 

	scennames = Flatten[{scennames, Table[disnames[[disind[[d]]]] <> " excess mort", {d, nd}]}]];

If[(sensparameters[[7]] == 1), 

	scennames = Flatten[{scennames, Table[disnames[[disind[[d]]]] <> " case fatality", {d, nd}]}]];

If[(sensparameters[[8]] == 1),

	scennames = Flatten[{scennames, Table["(discrete) RR" <> risknames[[riskindd[[r]]]], {r, nrd}]}]];

If[(sensparameters[[9]] == 1), 

	scennames = Flatten[{scennames, Table["(continuous) RR" <> risknames[[riskindc[[r]]]], {r, nrc}]}]];

If[(sensparameters[[10]] == 1),	

	scennames = Flatten[{scennames, "tracking of risk factors"}]];

If[(sensparameters[[11]] == 1),	

	scennames = Flatten[{scennames, "RR's one dis on another dis incidence"}]];

If[(sensparameters[[12]] == 1), 

	scennames = Flatten[{scennames,	"RR's one dis on another dis case fatality"}]];	
		
If[(sensparameters[[13]] == 1), 

	scennames = Flatten[{scennames,	"relapsecoef 1", "relapsecoef 2"}]];

If[(sensparameters[[14]] == 1), 

	scennames = Flatten[{scennames,	"event regression coef 1", "event regression coef 2"}]];


(* --------------------------------------------------
	PRINTING ROUTINES FOR EVENT NUMBERS (EXCL LE) FOR EACH MODEL TYPE: SUB-ROUTINES
----------------------------------------------------*)

printbug["2."];

(* PRINTS NAME OF OUTPUT VARIABLE AND SCENARIO SPECIFICATIONS *)

resname[t_, diffscen_] := Block[{},

	str = outputnames[[t]] <> " differences with baseline input value " <>
		If[(diffscen == 0), "for baseline scenario", "difference with baseline scenario"];

	addcellnb[headingprint2nb[str]]];


(* PRINTS RESULTS: TOTAL POPULATION NUMBERS *)

printrespop[respop_, diffscen_] :=
	
	Do[	str = "input " <> scennames[[draw]] <>
			If[(diffscen == 0), " for baseline scenario", " difference with baseline scenario"];

		addcellnb[headingprint3nb[str]];			

		addcellnb[Cell[BoxData[ToBoxes[respop[[draw]] // TableForm]], "Subsection"]],

		{draw, Length[respop]}];


(* PRINTS RESULTS: RISK FACTOR CLASS PREVALENCE NUMBERS *)

printresrisk[resrisk_, diffscen_] :=

	Do[	str = "input "<> scennames[[draw]] <> " on " <> risknames[[riskindd[[r]]]] <> " "<> gennames[[g]] <>
			If[(diffscen == 0), "for baseline scenario", "difference with baseline scenario"];

		addcellnb[headingprint3nb[str]];					
			
		addcellnb[Cell[BoxData[ToBoxes[resrisk[[draw, r, g]] // TableForm]], "Subsection"]],

		{draw, Length[resrisk]}, {r, nrd}, {g, ng}];


(* PRINTS RESULTS: DISEASE (PREVALENCE, INCIDENCE, MORTALITY) NUMBERS *)

printresdis[resdis_, diffscen_] :=

	Do[	str = "input "<> scennames[[draw]] <> " on " <> mortnames[[d]] <>
			If[(diffscen == 0), "for baseline scenario", "difference with baseline scenario"];

		addcellnb[headingprint3nb[str]];					

		addcellnb[Cell[BoxData[ToBoxes[resdis[[draw, d]] // TableForm]], "Subsection"]],

		{draw, Length[resdis]}, {d, Length[resdis[[1]]]}];


(* --------------------------------------------------
	PLOTTING RESULTS IN POPULATION NUMBER FORMAT
----------------------------------------------------*)

printbug["3."];
		
plotpopnb[respop_, names_] := Block[{},

	hndraw 	= Length[respop];

	{
	Table[	{
		headingprint3nb[" gender " <> gennames[[g]]],
		Table[	Cell[	GraphicsData[
					"PostScript",
					DisplayString[
						GraphicsArray[
							Table[	ListPlot[respop[[draw2, g]],
									DisplayFunction -> Identity, 
									PlotLabel 	-> names[[draw2]], 
									PlotJoined 	-> True,  
									TextStyle 	-> stdtextstyle,
									AxesLabel 	-> stdaxislabel],
							     	{draw2, 4 draw1 + 1, Min[{4 draw1 + 4, hndraw}]}]
							]
						]
					],
				"Subsection",
				ImageSize -> ( Min[{4 draw1 + 4, hndraw}] - (4 draw1) ) imagesize
				],
			{draw1, 0, Floor[(hndraw - 1) / 4]}]
		},
	{g, ng}]
	}

	];


(* --------------------------------------------------
	PLOTTING RESULTS IN RISK FACTOR CLASS PREVALENCE NUMBER FORMAT
----------------------------------------------------*)

plotrisknb[resrisk_, names_]:= Block[{},

	hndraw 	= Length[resrisk];

	{
	Table[	{
		headingprint3nb[" gender " <> gennames[[g]] <> " risk factor " <> risknames[[riskindd[[r]]]] <>
			" class  " <> ToString[ri]],

		Table[	Cell[	GraphicsData[
						"PostScript",
						DisplayString[
							GraphicsArray[
								Table[	ListPlot[resrisk[[draw2, r, g, ri]],
										DisplayFunction -> Identity, 
										PlotLabel 	-> names[[draw2]],
										PlotJoined 	-> True,
										TextStyle 	-> stdtextstyle,
										AxesLabel 	-> stdaxislabel],
									{draw2, 4 draw1 + 1, Min[{4 draw1 + 4, hndraw}]}]
								]
							]
						],
				"Subsection",
				ImageSize -> ( Min[{4 draw1 + 4, hndraw}] - (4 draw1) ) imagesize
				],
			{draw1, 0, Floor[(hndraw - 1) / 4]}]
		},
	{g, ng}, {r, nrd}, {ri, ncrsel[[r]]}]
	}

	];


(* --------------------------------------------------
	PLOTTING RESULTS IN DISEASE PREVALENFCE NUMBER FORMAT
----------------------------------------------------*)

plotdisnb[resdis_, names_]:= Block[{},

	hndraw 	= Length[resdis];
	nd1 	= Length[resdis[[1]]];

	{
	Table[	{
		headingprint3nb[" gender " <> gennames[[g]] <> " disease " <> mortnames[[d]]],
		Table[	Cell[	GraphicsData[
					"PostScript",
					DisplayString[
   						GraphicsArray[
							Table[	ListPlot[resdis[[draw2, d, g]],
									DisplayFunction -> Identity, 
									PlotLabel 	-> names[[draw2]], 
									PlotJoined 	-> True, 
									TextStyle 	-> stdtextstyle,
									AxesLabel 	-> stdaxislabel],
								{draw2, 4 draw1 + 1, Min[{4 draw1 + 4, hndraw}]}]
								]
							]
						],
				"Subsection",
				ImageSize -> ( Min[{4 draw1 + 4, hndraw}] - (4 draw1) ) imagesize
				],
			{draw1, 0, Floor[(hndraw - 1) / 4]}]
		},

	{g, ng}, {d, Length[resdis[[1]]]}] 
	}

	];


(* --------------------------------------------------
	PRINTING ROUTINES FOR EVENT NUMBERS: GENERIC ROUTINES
----------------------------------------------------*)

printbug["4."];

makedis[resmodel_, outputind_, nd_] := Block[{},

printbug["4.1"];

	ndis	= If[(rates == 0),

				If[(cumulative == 0),

					Table[Plus@@Flatten[resmodel[[outputind, draw, n, d, g]]],
						{draw, ndrawinput}, {d, nd}, {g, ng}, {n, nstap}],

					Table[Plus@@Flatten[resmodel[[outputind, draw, Range[n], d, g]]],
						{draw, ndrawinput}, {d, nd}, {g, ng}, {n, nstap}]],

				Table[Plus@@Flatten[resmodel[[outputind, draw, n, d, g]]] / Plus@@Flatten[resmodel[[1, draw, n, g]]],
					{draw, ndrawinput}, {d, nd}, {g, ng}, {n, nstap}]

			];

printbug["4.1.1"];

	ndis	= Table[(ndis[[draw]] - ndis[[1]]) / (ndis[[1]] + eps) , {draw, 2, ndrawinput}] / deltasens;
	
printbug["4.1.2"];

	resname[outputind, 0];	

	If[(graphicoutput == 1),	

		addcellnb[plotdisnb[ndis, scennames]]];

	If[(tabeloutput == 1),		

		ndis 	= roundoff[ndis, 1000];
		printresdis[ndis, 0]];

printbug["4.1.3"]
		
	];

makedisdiff[resmodel_, scen_, outputind_, nd_] := Block[{},

printbug["4.2"];

	ndis	= If[(rates == 0),

				If[(cumulative == 0),

					Table[Plus@@Flatten[resmodel[[outputind, draw + ndrawinput scen, n, d, g]]] -
							Plus@@Flatten[resmodel[[outputind, draw, n, d, g]]],
						{draw, ndrawinput}, {d, nd}, {g, ng}, {n, nstap}],

					Table[Plus@@Flatten[resmodel[[outputind, draw + ndrawinput scen, Range[n], d, g]]] -
							Plus@@Flatten[resmodel[[outputind, draw, Range[n], d, g]]],
						{draw, ndrawinput}, {d, nd}, {g, ng}, {n, nstap}]],

				Table[Plus@@Flatten[resmodel[[outputind, draw + ndrawinput scen, n, d, g]]] /
						Plus@@Flatten[resmodel[[1, draw + ndrawinput scen, n, g]]] -
						Plus@@Flatten[resmodel[[outputind, draw, n, d, g]]] /
						Plus@@Flatten[resmodel[[1, draw, n, g]]],
					{draw, ndrawinput}, {d, nd}, {g, ng}, {n, nstap}]

			];

printbug["4.2.1"];

	ndis	= Table[(ndis[[draw]] - ndis[[1]]) / (ndis[[1]] + eps) , {draw, 2, ndrawinput}] / deltasens;

printbug["4.2.2"];

	resname[outputind, 1];	

	If[(graphicoutput == 1),
	
		addcellnb[plotdisnb[ndis, scennames]]];

	If[(tabeloutput == 1),		

		ndis 	= roundoff[ndis, 1000];
		printresdis[ndis, 1]];

printbug["4.2.3"]
	
	];
(* --------------------------------------------------
	PRINTING ROUTINES FOR EVENT NUMBERS FOR EACH MODEL TYPE FOR BASELINE SCENARIO: MAIN ROUTINE
----------------------------------------------------*)

printnumbers[resmodel_] := Block[{},

	(* TOTAL POPULATION NUMBERS *)

printbug["4.3"];

	If[(outputsel[[1]] == 1),
					
		npop	= If[(cumulative == 0),

				Table[Plus@@Flatten[resmodel[[1, draw, n, g]]],	{draw, ndrawinput}, {g, ng}, {n, nstap}],
				Table[Plus@@Flatten[resmodel[[1, draw, Range[n], g]]],
					{draw, ndrawinput}, {g, ng}, {n, nstap}]];

printbug["4.3.1"];

		npop	= Table[(npop[[draw]] - npop[[1]]) / (npop[[1]] + eps) , {draw, 2, ndrawinput}] / deltasens;

printbug["4.3.2"];		

		resname[1, 0];	

		If[(graphicoutput == 1),	

			addcellnb[plotpopnb[npop, scennames]]];

		If[(tabeloutput == 1),		

			npop 	= roundoff[npop, 1000];
			printrespop[npop, 0]];

printbug["4.3.3"]

		];

	(* DISCRETELY DISTRIBUTED RISK FACTOR CLASS PREVALENCE NUMBERS OR RATES *)

	If[(nrd > 0) && (outputsel[[2]] == 1),	

printbug["4.4"];

		nrisk = If[(rates == 0),
				
				Table[Plus@@Flatten[resmodel[[2, draw, n, r, g, ri]]],
					{draw, ndrawinput}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}],

				Table[Plus@@Flatten[resmodel[[2, draw, n, r, g, ri]]] /
						Plus@@Flatten[resmodel[[1, draw, n, g]]],
					{draw, ndrawinput}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}]
				];

printbug["4.4.1"];

		nrisk	= Table[(nrisk[[draw]] - nrisk[[1]]) / (nrisk[[1]] + eps) , {draw, 2, ndrawinput}] / deltasens;

printbug["4.4.2"];		

		resname[2, 0];	

		If[(graphicoutput == 1),	

			addcellnb[plotrisknb[nrisk, scennames]]];

		If[(tabeloutput == 1),		

			nrisk 	= roundoff[nrisk, 1000];
			printresrisk[nrisk, 0]];

printbug["4.4.3"]

		];

	(* DISEASE PREVALENCE NUMBERS OR RATES*)

printbug["4.5"];

	If[(nd > 0) && (outputsel[[4]] == 1), 	makedis[resmodel, 4, nd]];					
		
	(* DISEASE INCIDENCE NUMBERS *)

printbug["4.6"];

	If[(nd > 0) && (outputsel[[5]] == 1), 	makedis[resmodel, 5, nd]];

	(* DISEASE MORTALITY NUMBERS *)

printbug["4.7"];

	If[(outputsel[[6]] == 1), 		makedis[resmodel, 6, nd]];

	(* MEAN AGE AT DISEASE ONSET *)

	If[(nd > 0) && (outputsel[[7]] == 1),

printbug["4.8"];

		onsetage = Table[Plus@@Flatten[resmodel[[7, draw, n, d, g]]] / Plus@@Flatten[resmodel[[4, draw, n, d, g]]],
					{draw, ndrawinput}, {d, nd}, {g, ng}, {n, nstap}];

printbug["4.8.1"];

		onsetage = Table[(onsetage[[draw]] - onsetage[[1]]) / (onsetage[[1]] + eps) , {draw, 2, ndrawinput}] /
				deltasens;

printbug["4.8.2"];

		resname[7, 0];	
		
		If[(graphicoutput == 1),	

			addcellnb[plotdisnb[onsetage, scennames]]];

		If[(tabeloutput == 1),		

			onsetage = roundoff[onsetage, 1000];
			printresdis[onsetage, 0]];

printbug["4.8.3"]

		];

	(* MEAN TIME SINCE SMOKING CESSATION *)

	If[(RRsmokduurind == 1) && (outputsel[[8]] == 1),

printbug["4.9"];

		duurstop = Table[Plus@@Flatten[resmodel[[8, draw, n, g]]] / Plus@@Flatten[resmodel[[2, draw, n, 1, g, 3]]],
				{draw, ndrawinput}, {g, ng}, {n, nstap}];

printbug["4.9.1"];

		duurstop = Table[(duurstop[[draw]] - duurstop[[1]]) / (duurstop[[1]] + eps) , {draw, 2, ndrawinput}] /
				deltasens;

printbug["4.9.2"];

		resname[8, 0];
		
		If[(graphicoutput == 1),	

			addcellnb[plotpopnb[duurstop, scennames]]];

		If[(tabeloutput == 1),		

			duurstop = roundoff[duurstop, 1000];
			printrespop[duurstop, 0]];

printbug["4.9.3"]
		
		];	

]; (* END PRINTNUMBERS *)

(* --------------------------------------------------
	PRINTING ROUTINES FOR EVENT NUMBERS FOR EACH MODEL TYPE COMPARED TO BASELINE SCENARIO: MAIN ROUTINE
----------------------------------------------------*)

printdiffnumbers[resmodel_, scen_] := Block[{},

	(* TOTAL POPULATION NUMBERS *)

	If[(outputsel[[1]] == 1),

printbug["4.10"];

		npop	= If[(cumulative == 0),

				Table[Plus@@Flatten[resmodel[[1, draw + ndrawinput scen, n, g]]] -
						Plus@@Flatten[resmodel[[1, draw, n, g]]],
					{draw, ndrawinput}, {g, ng}, {n, nstap}],

				Table[Plus@@Flatten[resmodel[[1, draw + ndrawinput scen, Range[n], g]]] -
						Plus@@Flatten[resmodel[[1, draw, Range[n], g]]],
					{draw, ndrawinput}, {g, ng}, {n, nstap}]];

printbug["4.10.1"];

		npop	= Table[(npop[[draw]] - npop[[1]]) / (npop[[1]] + eps) , {draw, 2, ndrawinput}] / deltasens;


printbug["4.10.2"];

		resname[1, 1];
		
		If[(graphicoutput == 1),

			addcellnb[plotpopnb[npop, scennames]]];

		If[(tabeloutput == 1),		

			npop 	= roundoff[npop, 1000];
			printrespop[npop, 1]];

printbug["4.10.3"]
		
		];

	(* DISCRETELY DISTRIBUTED RISK FACTOR CLASS PREVALENCE NUMBERS AND RATES *)

	If[(nrd > 0) && (outputsel[[2]] == 1),	

printbug["4.11"];
				
		nrisk	= If[(rates == 0),

				Table[Plus@@Flatten[resmodel[[2, draw + ndrawinput scen, n, r, g, ri]]] -
						Plus@@Flatten[resmodel[[2, draw, n, r, g, ri]]],
					{draw, ndrawinput}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}],

				Table[Plus@@Flatten[resmodel[[2, draw + ndrawinput scen, n, r, g, ri]]] /
						Plus@@Flatten[resmodel[[1, draw + ndrawinput scen, n, g]]] -
						Plus@@Flatten[resmodel[[2, draw, n, r, g, ri]]] /
						Plus@@Flatten[resmodel[[1, draw, n, g]]],
					{draw, ndrawinput}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}]];

printbug["4.11.1"];

		nrisk	= Table[(nrisk[[draw]] - nrisk[[1]]) / (nrisk[[1]] + eps) , {draw, 2, ndrawinput}] / deltasens;

printbug["4.11.2"];

		resname[2, 1];	

		If[(graphicoutput == 1),	

			addcellnb[plotrisknb[nrisk, scennames]]];

		If[(tabeloutput == 1),		

			nrisk 	= roundoff[nrisk, 1000];
			printresrisk[nrisk, 1]];

printbug["4.11.3"]

		];

	(* DISEASE PREVALENCE NUMBERS AND RATES *)

printbug["4.12"];

	If[(nd > 0) && (outputsel[[4]] == 1), 	makedisdiff[resmodel, scen, 4, nd]];
				
	(* DISEASE INCIDENCE NUMBERS *)

printbug["4.13"];

	If[(nd > 0) && (outputsel[[5]] == 1), 	makedisdiff[resmodel, scen, 5, nd]];

	(* DISEASE MORTALITY NUMBERS *)

printbug["4.14"];

	If[(outputsel[[6]] == 1), 		makedisdiff[resmodel, scen, 6, nd + 2]];

	(* MEAN AGE AT DISEASE ONSET *)

	If[(nd > 0) && (outputsel[[7]] == 1),

printbug["4.15"];

		onsetage	= Table[Plus@@Flatten[resmodel[[7, draw + ndrawinput scen, n, d, g]]] /
					Plus@@Flatten[resmodel[[4, draw + ndrawinput scen, n, d, g]]] -
					Plus@@Flatten[resmodel[[7, draw, n, d, g]]] /
					Plus@@Flatten[resmodel[[4, draw, n, d, g]]],
					{draw, ndrawinput}, {d, nd}, {g, ng}, {n, nstap}];

printbug["4.15.1"];

		onsetage	= Table[(onsetage[[draw]] - onsetage[[1]]) / (onsetage[[1]] + eps) , {draw, 2, ndrawinput}] / deltasens;

printbug["4.15.2"];
		
		resname[7, 1];	

		If[(graphicoutput == 1),	

			addcellnb[plotdisnb[nmort, scennames]]];

		If[(tabeloutput == 1),		

			onsetage = roundoff[onsetage, 1000];
			printresdis[onsetage, 1]];

printbug["4.15.3"]
		
		];

	(* MEAN TIME SINCE SMOKING CESSATION *)

	If[(RRsmokduurind == 1) && (outputsel[[8]] == 1),

printbug["4.16"];

		duurstop	= Table[Plus@@Flatten[resmodel[[8, draw + ndrawinput scen, n, g]]] /
					Plus@@Flatten[resmodel[[2, draw + ndrawinput scen, n, 1, g, 3]]] -
					Plus@@Flatten[resmodel[[8, draw, n, g]]] /
					Plus@@Flatten[resmodel[[2, draw, n, 1, g, 3]]],
				{draw, ndrawinput}, {g, ng}, {n, nstap}];

printbug["4.16.1"];

		duurstop = Table[(duurstop[[draw]] - duurstop[[1]]) / (duurstop[[1]] + eps) , {draw, 2, ndrawinput}] / deltasens;

printbug["4.16.2"];
		
		resname[8, 1];	

		If[(graphicoutput == 1),	

			addcellnb[plotpopnb[duurstop, scennames]]];

		If[(tabeloutput == 1),		

			duurstop = roundoff[duurstop, 1000];
			printrespop[duurstop, 1]];

printbug["4.16.3"]
		
		]

	]; (* END PRINTDIFFNUMBERS *)

(* --------------------------------------------------
	SENSITIVITY OF LIFE EXPECTANCY BASED OUTPUT NUMBERS FOR BASELINE SCENARIO
----------------------------------------------------*)

printbug["5."];

(* INDICATOR FUNCTION *)

EqInd[d_, d1_]	:= 1 + If[(d == d1), deltasens, 0];

(* DISCOUNTING FUTURE NUMBERS *)

makediscount[npop_, disc_, delta_] := Table[npop[[g, n]] / (1 + (1 + delta) disc)^(n - 1), {g, ng}, {n, nstap}];

(* DISCOUNTING FUTURE NUMBERS AND PRESENTATION *)

makepopcosts[npop_, names_, poptype_, cumul_] := Block[{},

printbug["5.1"];

	npop1	= npop;

	If[(cumul == 1), npop1 = Table[Plus@@npop1[[scen, g, Range[n]]], {scen, Length[npop1]}, {g, ng}, {n, nstap}]];

printbug["5.1.1"];

	npop1	= Table[(npop1[[scen]] - npop1[[1]]) / (npop1[[1]] + eps), {scen, 2, Length[npop1]}] / deltasens;

printbug["5.1.2"];
	
	If[(graphicoutput == 1),

		addcellnb[headingprint3nb[poptype]];
		addcellnb[plotpopnb[npop1, names]]];
	
	If[(tabeloutput == 1),

		npop1 	= roundoff[npop1, 1000];
 		printrespop[npop1, 0]];

printbug["5.1.3"]

	]; (* MAKEPOPCOSTS *)
		
printpopcosts[resmodel_] := Block[{},

	(* TOTAL POPULATION NUMBERS & COSTS *)

	If[(outputsel[[1]] == 1) && (outputsel[[9]] == 1),

		(* MEAN AGECLASS DISABILITY WEIGHTS AND COSTS *)

		meanDALYwgt	= Table[meanaggreg[DALYwgtsel[[d, g]]], {d, nd}, {g, ng}];

		meanDALYwgt	= Table[Plus@@(meanDALYwgt[[d, g]] resmodel[[4, 1, n, d, g]]) /	(Plus@@resmodel[[4, 1, n, d, g]] + eps),
					{n, nstap}, {d, nd}, {g, ng}];

		meancostspatient = Table[meanaggreg[costspatientsel[[d, g]]], {d, nd}, {g, ng}];
		
		meancostspatient = Table[Plus@@(meancostspatient[[d, g]] resmodel[[4, 1, n, d, g]]) / (Plus@@resmodel[[4, 1, n, d, g]] + eps),
					{n, nstap}, {d, nd}, {g, ng}];

		meancostspersonoth = Table[meanaggreg[costspersonothsel[[g]]], {g, ng}];
		
		meancostspersonoth = Table[Plus@@(meancostspersonoth[[g]] resmodel[[1, 1, n, g]]) / (Plus@@resmodel[[1, 1, n, g]] + eps),
					{n, nstap}, {g, ng}];

printbug["5.2"];

		resname[1, 0];
		
		(* TOTAL POPULATION NUMBERS *)

printbug["5.2.1"];
		
		npop	= Table[Plus@@resmodel[[1, 1, n, g]], {g, ng}, {n, nstap}];

		npop1	= makediscount[npop, discounte, 0];

		npop2	= makediscount[npop, discounte, deltasens];

printbug["5.2.2"];

		makepopcosts[{npop1, npop2}, {"discounting"}, "total population", cumulative];

		(* DISEASE-FREE POPULATION NUMBERS *)

printbug["5.3"];

		pdis	= Table[Plus@@resmodel[[4, 1, n, d, g]] / (Plus@@Flatten[resmodel[[1, 1, n, g]]] + eps),
					{n, nstap}, {d, nd}, {g, ng}];

		nfree	= Table[npop[[g, n]] Times@@(1 - pdis[[n, Range[nd], g]]), {g, ng}, {n, nstap}];

		nfree1	= makediscount[nfree, discounte, 0];

		nfree2	= makediscount[nfree, discounte, deltasens];

		nfree3	= Table[npop[[g, n]] *
					Times@@Table[1 - EqInd[d, d1] pdis[[n, d1, g]], {d1, nd}] /
					(1 + discounte)^(n - 1),
				{d, nd}, {g, ng}, {n, nstap}];

printbug["5.3.1"];

		makepopcosts[Join[{nfree1, nfree2}, nfree3], Join[{"discounting"}, disnames[[disind]]],
				"disease-free population", cumulative];

		(* DALY-WEIGHTED POPULATION NUMBERS *)

printbug["5.4"];

		nDALY	= Table[npop[[g, n]] Times@@Table[1 - meanDALYwgt[[n, d, g]] pdis[[n, d, g]], {d, nd}],
				{g, ng}, {n, nstap}];

		nDALY1	= makediscount[nDALY, discounte, 0];

		nDALY2	= makediscount[nDALY, discounte, deltasens];

		nDALY3	= Table[npop[[g, n]] *
				Times@@Table[1 - EqInd[d, d1] meanDALYwgt[[n, d1, g]] pdis[[n, d1, g]], {d1, nd}] /
				(1 + discounte)^(n - 1),
				{d, nd}, {g, ng}, {n, nstap}];

printbug["5.4.1"];

		makepopcosts[Join[{nDALY1, nDALY2}, nDALY3], Join[{"discounting"}, disnames[[disind]]],
				"DALY weighted population", cumulative];

		(* COSTS EXCL. OTHER COSTS *)

printbug["5.5"];

		ncosts 	= Table[Plus@@Table[Plus@@(meancostspatient[[n, d, g]] resmodel[[4, 1, n, d, g]]), {d, nd}],
				{g, ng}, {n, nstap}];

		ncosts1	= makediscount[ncosts, discountc, 0];

		ncosts2	= makediscount[ncosts, discountc, deltasens];

		ncosts3	= Table[Plus@@Table[EqInd[d, d1] Plus@@(meancostspatient[[n, d1, g]] resmodel[[4, 1, n, d1, g]]),
						{d1, nd}] /
				(1 + discountc)^(n - 1),
				{d, nd}, {g, ng}, {n, nstap}];	

printbug["5.5.1"];

		makepopcosts[Join[{ncosts1, ncosts2}, ncosts3], Join[{"discounting"}, disnames[[disind]]],
					"total costs excl. other", cumulative];

		(* COSTS INCL. OTHER COSTS *)

printbug["5.5.2"];

		othcosts = Table[Plus@@(meancostspersonoth[[n, g]] resmodel[[1, 1, n, g]]), {g, ng}, {n, nstap}];

		ncosts4	= makediscount[ncosts + othcosts, discountc, 0];

		ncosts5	= makediscount[ncosts + othcosts, discountc, deltasens];

		ncosts6	= ncosts3 + Table[makediscount[othcosts, discountc, 0], {nd}];	

printbug["5.5.3"];

		makepopcosts[Join[{ncosts4, ncosts5}, ncosts6], Join[{"discounting"}, disnames[[disind]]],
					"total costs incl. other", cumulative];

		(* COSTS (EXCL. OTHER) PER DALY *)

printbug["5.6"];

		If[(cumulative == 1),

			costsDALY1 = Table[Plus@@ncosts1[[g, Range[n]]] / (Plus@@nDALY1[[g, Range[n]]] + eps),
						{g, ng}, {n, nstap}];

			costsDALY2 = Table[Plus@@ncosts2[[g, Range[n]]] / (Plus@@nDALY2[[g, Range[n]]] + eps),
						{g, ng}, {n, nstap}];

			costsDALY3 = Table[Plus@@ncosts3[[d, g, Range[n]]] / (Plus@@nDALY3[[d, g, Range[n]]] + eps),
						{d, nd}, {g, ng}, {n, nstap}];

printbug["5.6.1"];

			makepopcosts[Join[{costsDALY1, costsDALY2}, costsDALY3],
				Join[{"discounting"}, disnames[[disind]]], "costs(excl.other)/DALY", 0];

		(* COSTS (INCL. OTHER) PER DALY *)

printbug["5.6.2"];

			costsDALY1 = Table[Plus@@ncosts4[[g, Range[n]]] / (Plus@@nDALY1[[g, Range[n]]] + eps),
						{g, ng}, {n, nstap}];

			costsDALY2 = Table[Plus@@ncosts5[[g, Range[n]]] / (Plus@@nDALY2[[g, Range[n]]] + eps),
						{g, ng}, {n, nstap}];

			costsDALY3 = Table[Plus@@ncosts6[[d, g, Range[n]]] / (Plus@@nDALY3[[d, g, Range[n]]] + eps),
						{d, nd}, {g, ng}, {n, nstap}];

printbug["5.6.3"];

			makepopcosts[Join[{costsDALY1, costsDALY2}, costsDALY3],
				Join[{"discounting"}, disnames[[disind]]], "costs(incl.other)/DALY", 0];


			];
			
		];

]; (* END PRINTPOPCOSTS *)


(* --------------------------------------------------
	SENSITIVITY OF LIFE EXPECTANCY BASED OUTPUT NUMBERS COMPARED TO BASELINE SCENARIO
----------------------------------------------------*)

printbug["6."];
		
printdiffpopcosts[resmodel_, hscen_] := Block[{},

	scenr = 1 + ndrawinput {0, hscen};

	(* TOTAL POPULATION NUMBERS & COSTS *)

	If[(outputsel[[1]] == 1) && (outputsel[[9]] == 1),

		(* MEAN AGECLASS DISABILITY WEIGHTS AND COSTS *)

		meanDALYwgt	= Table[meanaggreg[DALYwgtsel[[d, g]]], {d, nd}, {g, ng}];

		meanDALYwgt	= Table[Plus@@(meanDALYwgt[[d, g]] resmodel[[4, 1, n, d, g]]) /	(Plus@@resmodel[[4, 1, n, d, g]] + eps),
					{n, nstap}, {d, nd}, {g, ng}];

		meancostspatient = Table[meanaggreg[costspatientsel[[d, g]]], {d, nd}, {g, ng}];
		
		meancostspatient = Table[Plus@@(meancostspatient[[d, g]] resmodel[[4, 1, n, d, g]]) / (Plus@@resmodel[[4, 1, n, d, g]] + eps),
					{n, nstap}, {d, nd}, {g, ng}];

		meancostspersonoth = Table[meanaggreg[costspersonothsel[[g]]], {g, ng}];
		
		meancostspersonoth = Table[Plus@@(meancostspersonoth[[g]] resmodel[[1, 1, n, g]]) / (Plus@@resmodel[[1, 1, n, g]] + eps),
					{n, nstap}, {g, ng}];

printbug["6.2"];

		resname[1, 1];
		
		(* TOTAL POPULATION NUMBERS *)

printbug["6.2.1"];

		npop	= Table[Plus@@resmodel[[1, scenr[[scen]], n, g]], {scen, 2}, {g, ng}, {n, nstap}];

		dnpop	= npop[[2]] - npop[[1]];

		npop1	= makediscount[dnpop, discounte, 0];

		npop2	= makediscount[dnpop, discounte, deltasens];

printbug["6.2.2"];

		makepopcosts[{npop1, npop2}, {"discounting"}, "total population", cumulative];

		(* DISEASE-FREE POPULATION NUMBERS *)

printbug["6.3"];

		pdis	= Table[Plus@@resmodel[[4, scenr[[scen]], n, d, g]] /
					(Plus@@Flatten[resmodel[[1, scenr[[scen]], n, g]]] + eps),
				{scen, 2}, {n, nstap}, {d, nd}, {g, ng}];

printbug["6.3.1"];

		nfree	= Table[npop[[scen, g, n]] Times@@(1 - pdis[[scen, n, Range[nd], g]]),
				{scen, 2}, {g, ng}, {n, nstap}];

printbug["6.3.2"];

		nfree	= nfree[[2]] - nfree[[1]];

		nfree1	= makediscount[nfree, discounte, 0];

		nfree2	= makediscount[nfree, discounte, deltasens];

		nfree3	= Table[npop[[scen, g, n]] *
					Times@@Table[1 - EqInd[d, d1] pdis[[scen, n, d1, g]], {d1, nd}] /
					(1 + discounte)^(n - 1),
				{scen, 2}, {d, nd}, {g, ng}, {n, nstap}];

		nfree3	= nfree3[[2]] - nfree3[[1]];

printbug["6.3.3"];

		makepopcosts[Join[{nfree1, nfree2}, nfree3], Join[{"discounting"}, disnames[[disind]]],
					"disease-free population", cumulative];

		(* DALY-WEIGHTED POPULATION NUMBERS *)

printbug["6.4"];

		nDALY	= Table[npop[[scen, g, n]] Times@@Table[1 - meanDALYwgt[[n, d, g]] pdis[[scen, n, d, g]], {d, nd}],
				{scen, 2}, {g, ng}, {n, nstap}];

		nDALY	= nDALY[[2]] - nDALY[[1]];

		nDALY1	= makediscount[nDALY, discounte, 0];

		nDALY2	= makediscount[nDALY, discounte, deltasens];

printbug["6.4.1"];

		nDALY3	= Table[npop[[scen, g, n]] *
				Times@@Table[1 - EqInd[d, d1] meanDALYwgt[[n, d1, g]] pdis[[scen, n, d1, g]], {d1, nd}] /
				(1 + discounte)^(n - 1),
				{scen, 2}, {d, nd}, {g, ng}, {n, nstap}];

		nDALY3	= nDALY3[[2]] - nDALY3[[1]];

printbug["6.4.2"];

		makepopcosts[Join[{nDALY1, nDALY2}, nDALY3], Join[{"discounting"}, disnames[[disind]]],
					"DALY weighted population", cumulative];

		(* COSTS (EXCL. OTHER) *)

printbug["6.5"];

		ncosts 	= Table[Plus@@Table[Plus@@(meancostspatient[[n, d, g]] resmodel[[4, scenr[[scen]], n, d, g]]),
						{d, nd}],
				{scen, 2}, {g, ng}, {n, nstap}];

		dncosts	= ncosts[[2]] - ncosts[[1]];

		ncosts1	= makediscount[dncosts, discountc, 0];
	
		ncosts2	= makediscount[dncosts, discountc, deltasens];
	
printbug["6.5.1"];

		ncosts3	= Table[Plus@@Table[EqInd[d, d1] *
						Plus@@(meancostspatient[[n, d1, g]] resmodel[[4, scenr[[scen]], n, d1, g]]),
						{d1, nd}] /
				(1 + discountc)^(n - 1),
				{scen, 2}, {d, nd}, {g, ng}, {n, nstap}];

		ncosts3	= ncosts3[[2]] - ncosts3[[1]];

printbug["6.5.2"];

		makepopcosts[Join[{ncosts1, ncosts2}, ncosts3], Join[{"discounting"}, disnames[[disind]]],
					"total costs excl. other", cumulative];

		(* COSTS (INCL. OTHER) *)

printbug["6.5.3"];

		othcosts = Table[Plus@@(meancostspersonoth[[n, g]] resmodel[[1, scenr[[scen]], n, g]]),
				{scen, 2}, {g, ng}, {n, nstap}];

		othcosts = othcosts[[2]] - othcosts[[1]];

		ncosts4	= makediscount[dncosts + othcosts, discountc, 0];
	
		ncosts5	= makediscount[dncosts + othcosts, discountc, deltasens];
	
printbug["6.5.4"];

		ncosts6	= ncosts3 + Table[makediscount[othcosts, discountc, 0], {nd}];

printbug["6.5.5"];

		makepopcosts[Join[{ncosts4, ncosts5}, ncosts6], Join[{"discounting"}, disnames[[disind]]],
					"total costs incl. other", cumulative];

		(* COSTS PER DALY *)

printbug["6.6"];

		If[(cumulative == 1),

			costsDALY1 = Table[Plus@@ncosts1[[g, Range[n]]] / (Plus@@nDALY1[[g, Range[n]]] + eps),
						{g, ng}, {n, nstap}];

			costsDALY2 = Table[Plus@@ncosts2[[g, Range[n]]] / (Plus@@nDALY2[[g, Range[n]]] + eps),
						{g, ng}, {n, nstap}];

			costsDALY3 = Table[Plus@@ncosts3[[d, g, Range[n]]] / (Plus@@nDALY3[[d, g, Range[n]]] + eps),
					{d, nd}, {g, ng}, {n, nstap}];

printbug["6.6.1"];

			makepopcosts[Join[{costsDALY1, costsDALY2}, costsDALY3],
					Join[{"discounting"}, disnames[[disind]]], "costs excl. other/DALY", 0];

printbug["6.6.2"];

			costsDALY1 = Table[Plus@@ncosts4[[g, Range[n]]] / (Plus@@nDALY1[[g, Range[n]]] + eps),
						{g, ng}, {n, nstap}];

			costsDALY2 = Table[Plus@@ncosts5[[g, Range[n]]] / (Plus@@nDALY2[[g, Range[n]]] + eps),
						{g, ng}, {n, nstap}];

			costsDALY3 = Table[Plus@@ncosts6[[d, g, Range[n]]] / (Plus@@nDALY3[[d, g, Range[n]]] + eps),
					{d, nd}, {g, ng}, {n, nstap}];

printbug["6.6.3"];

			makepopcosts[Join[{costsDALY1, costsDALY2}, costsDALY3],
					Join[{"discounting"}, disnames[[disind]]], "costs incl. other/DALY", 0]

			];
			
		];

]; (* END PRINTDIFFPOPCOSTS *)


(* --------------------------------------------------
	PRINTING ROUTINE FOR RESULTS FROM ONE CZM MODEL VERSION
----------------------------------------------------*)

printbug["7."];

printresmodel[resmodel_, naam1_] := Block[{},

	(* DIFFERENCES WITH BASELINE INPUT VALUE FOR BASELINE SCENARIO *)

printbug["7.1"];

	str = naam1 <> "differences with baseline input value, baseline scenario";
	
	addcellnb[headingprint1nb[str]];						

	printnumbers[resmodel];

	printpopcosts[resmodel];

	(* DIFFERENCES WITH BASELINE INPUT VALUE FOR DIFFERENCE WITH BASELINE SCENARIO *)

printbug["7.2"];

	Do[	str = naam1 <> "differences with baseline input value, scenario " <> ToString[scen] <>
			" compared to baseline scenario";
	
		addcellnb[headingprint1nb[str]];						

		printdiffnumbers[resmodel, scen - 1];

		printdiffpopcosts[resmodel, scen - 1],

		{scen, 2, nscen0}];
	
	]; (* END PRINTRESMODEL *)


(* --------------------------------------------------
	CONCATENATION OF NOTEBOOK CELLS
----------------------------------------------------*)

addcellnb[cell_] := Block[{}, cellnb = Flatten[{cellnb, cell}]];


(* --------------------------------------------------
	CONCATENATION OF RESULTS OVER CZM MODEL VERSIONS SELECTED
----------------------------------------------------*)

printbug["8."];

If[(analyse == 1),

printbug["8.1"];

	resmodel	= {resmarginalmodeldetermpop, resjointmodeldetermpop, resjointmodeldetermage, resjointmodelstochind,
	 			resjointmodelstochindcourse, resjointmodelstochpop, resjointmodelstochage};

	cellnb		= {headingprintnb["sensitivity analyses results"]};

	(* PRINTING RESULTS FOR EACH CZM MODEL VERSION SELECTED *)

	Do[If[(modelsel[[m]] == 1), printresmodel[resmodel[[m]], modelnames[[m]]]], {m, nmodel}];

	NotebookWrite[nbout, Flatten[cellnb]]

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
