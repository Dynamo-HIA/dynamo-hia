(* :Title: CZMCompareRuns *)

(* :Context: CZMPostProcessing` *)

(* :Author: Rudolf T. Hoogenveen, Maiwenn Al *)

(* :Summary:
   CZM postprocessing routine compares results from different CZM model runs *)

(* :Copyright: © 2004 by Rudolf Hoogenveen/Roel Breuls *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March
		3.0 version November 2005 
		3.1 version March 2007 *)

(* :Keywords: postprocessing, model versions *)


BeginPackage["CZMPostProcessing`CZMCompareRuns`",
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
	"CZMAdjustData`CZMAdjustAfterSelection`",
	"CZMSimulation`CZMSimulationFunctions`",
	"CZMSimulation`CZMSimulationMarginalModelDetermPop`",
	"CZMSimulation`CZMSimulationJointModelDetermPop`",
	"CZMSimulation`CZMSimulationJointModelDetermAge`",
	"CZMSimulation`CZMSimulationJointModelStochInd`",
	"CZMPostProcessing`CZMExportUserSelections`",
	"CZMPostProcessing`CZMCalcResults`",
	"CZMPostProcessing`CZMPresentResults`",
	"Graphics`Legend`",
	"Graphics`MultipleListPlot`"}]


Begin["`Private`"]


Print["CZMCompareRuns package is evaluated"];
printtijd;

printbug[c_] := If[(bugind == 1), Print[{"CZMCompareRuns", c}]];

himagesize	= 240;
graphind	= 1;
tabind		= 0;
figsize		= 900;


If[(analyse == 3),

(* --------------------------------------------------
	INITIALIZATIONS
----------------------------------------------------*)

printbug["1."];

	(* READS INPUT FILE CONTAINING RESULTS FROM ALL MODEL RUNS *)

	dat 		= Global`outputpath <> "outfileresmodelrun.m";
	hnrun		= (Read[dat])[[1]];
	resmodel 	= Table[Read[dat], {2 hnrun}];
	Close[dat];

printbug["1.1"];

	(* SIMULATION TIME PERIOD, ASSSUMED EQUAL FOR ALL RUNS; #SCENARIOS *)

	hnstap 	= resmodel[[1, 8]];
	hnscen	= resmodel[[1, 9]];

printbug["1.2"];

	(* # DISEASES, # RISK FACTORS AND CLASSES SELECTED IN BASELINE RUN *)

	hnd 	= Length[resmodel[[1, 2]]];
	hnrd	= Length[resmodel[[1, 1]]];
	hncr 	= ncr0[[resmodel[[1, 1]]]];

printbug["1.3"];

	(* FOR EACH RISK FACTOR IN BASELINE RUN POINTER TO RISK FACTOR IN OTHER RUNS *)

	pointerrisk = Table[0, {hnrun - 1}, {hnrd}];

	Do[If[(resmodel[[2 s - 1, 1, r1]] == resmodel[[1, 1, r]]), pointerrisk[[s - 1, r]] = r1],
		{s, 2, hnrun}, {r, hnrd}, {r1, Length[resmodel[[2 s - 1, 1]]]}];

printbug["1.4"];

	(* FOR EACH RISK FACTOR IN BASELINE RUN POINTER TO RISK FACTOR IN OTHER RUNS *)

	pointerdis = Table[0, {hnrun - 1}, {hnd}];

	Do[If[(resmodel[[2 s - 1, 2, d1]] == resmodel[[1, 2, d]]), pointerdis[[s - 1, d]] = d1],
		{s, 2, hnrun}, {d, hnd}, {d1, Length[resmodel[[2 s - 1, 2]]]}];

printbug["1.5"];

	(* MODEL SELECTIONS *)

	hmodelsel = Select[Range[7] resmodel[[1, 7]], Positive];

printbug["1.6"];

	(* MID AGE CLASSES *)

	midage = initageclass[[1, Range[nac[[1]]]]] + .5 lengthageclass[[1, Range[nac[[1]]]]];

printbug["1.7"];

	(* GRAPHICAL CONSTANTS *)

	stdruncolor1	= Table[{RGBColor[1 - s / hnrun, 0, s / hnrun]}, {s, hnrun}];
	stdrunlegend1	= Table["run" <> ToString[s], {s, hnrun}];
	stdruncolor2	= Table[{RGBColor[1 - s / hnrun, 0, s / hnrun]}, {s, 0, hnrun}];
	stdrunlegend2	= Flatten[{"data", Table["run" <> ToString[s], {s, hnrun}]}];

printbug["1.8"];

	plotset1	= {	PlotRange	-> All,
				PlotJoined 	-> True,
				DisplayFunction	-> Identity,
				SymbolShape 	-> None,
				LegendPosition 	-> {.5, .0},
				LegendSize 	-> {.65, .5}};	

printbug["1.9"];

	(* AGE PER YEAR OF COHORT, POPULATION NUMBERS, AND RISK-FACTOR RELATED DISEASES *)

	agen	= Minc[resmodel[[1, 5]] + Range[hnstap] - 1, na1];
	hnpop	= Table[Plus@@resmodel[[2 s, hmodelsel[[m]], 1, scen, n, g]],
			{m, Length[hmodelsel]}, {scen, hnscen}, {s, hnrun}, {g, ng}, {n, hnstap}];

	hdisrisk = Table[0, {hnrd}];

printbug["1.10"];

	Do[If[(riskdispair[[d1, 1]] == resmodel[[1, 1, r]]) && (riskdispair[[d1, 2]] == resmodel[[1, 2, d]]), hdisrisk[[r]] = d],
		{r, hnrd}, {d, Length[resmodel[[1, 2]]]}, {d1, Length[riskdispair]}];
	
	];


(* --------------------------------------------------
	NOTEBOOK OPERATIONS
---------------------------------------------------*)

If[(analyse == 3),

printbug["2."];

	addcellnb[cell_] := Block[{}, cellnb = Flatten[{cellnb, cell}]];

	hnbout 	= NotebookCreate[];

	tekst	= "";

	applnumber = ToExpression[StringDrop[ToString[Global`application], 11]];

	If[(Global`riskfactor > 0), tekst = " for " <> risknames[[Global`riskfactor]]];

	If[(Global`disease > 0), tekst = " for " <> disnames[[Global`disease]]];

	tekst = tekst <> ", test " <> ToString[applnumber];

	cellnb = {headingprintnb["comparison of model runs" <> tekst]}

	];


(* --------------------------------------------------
	COMPARING RESULTS AT BASELINE
----------------------------------------------------*)

printbug["3."];

(* GENERIC PROCEDURES FOR DISEASE OUTPUT VARIABLES *)

printdis1[res_] := Block[{},

printbug["3.1"];

	Do[	Print[{"run", s}];

		Do[	Print[{"disease", disnames[[resmodel[[1, 2, d]]]]}];
			Print[MatrixForm[res[[s, d]]]],

			{d, Length[res[[s]]]}],

		{s, Length[res]}]

	];

printdis1nb[res_] := Block[{},

printbug["3.2"];

	Do[	addcellnb[headingprint2nb["run" <> ToString[s + 1]]];

		Do[	addcellnb[headingprint3nb["disease" <> disnames[[resmodel[[1, 2, d]]]]]];
			addcellnb[Cell[BoxData[ToBoxes[res[[s, d]] // MatrixForm]], "Subsection"]],

			{d, Length[res[[s]]]}],

		{s, Length[res]}]

	];

plotdis1[prev_] := Block[{},

printbug["3.3"];

	plot1 = Table[MultipleListPlot[
				Table[prev[[s, d, g]], {s, Length[prev]}],
				plotset1,
				PlotStyle 	-> stdruncolor1,
				PlotLabel 	-> disnames[[resmodel[[1, 2, d]]]],
				AxesLabel	-> {"ageclass", ""},
				PlotLegend 	-> stdrunlegend1],
			{g, ng}, {d, hnd}];

	Do[	addcellnb[headingprint3nb[gennames[[g]]]];

		Do[addcellnb[Cell[GraphicsData["PostScript",
			DisplayString[GraphicsArray[Table[Show[plot1[[g, d]]], {d, 4 di + 1, Min[{4 di + 4, hnd}]}]]]],
			"Subsection", ImageSize -> Min[{(Min[{4 di + 4, hnd}] - 4 di) himagesize, figsize}]]],
			{di, 0, Floor[(hnd - 1) / 4]}],

		{g, ng}]

	];

makeresdis1[o_, scen_] := Block[{},

printbug["3.4"];

	prev = Table[0, {hnrun}, {hnd}, {ng}, {nac[[1]]}];

	Do[prev[[1, d]] = resmodel[[2, hmodelsel[[m]], o, scen, 1, d]], {d, hnd}];

	Do[prev[[s, d]] = If[(pointerdis[[s - 1, d]] > 0),
					resmodel[[2 s, hmodelsel[[m]], o, scen, 1, pointerdis[[s - 1, d]]]],
					prev[[1, d]]],
		{s, 2, hnrun}, {d, hnd}];

	If[(tabind == 1), 	printdis1nb[prev]];
	If[(graphind == 1),	plotdis1[prev]];

	];

If[(hnstap == 1) && (hnrun > 1) && (analyse == 3),

printbug["3.5"];

	Do[	tekst	= "Results at baseline, specified by ageclass, " <> modelnames[[hmodelsel[[m]]]];
		addcellnb[headingprint1nb[tekst]];

		(* TOTAL POPULATION NUMBERS *)
printbug["3.6"];
		pop	= Table[resmodel[[2 s, hmodelsel[[m]], 1, 1, scen]], {s, hnrun}];

		tekst	= "Total population numbers" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];
	
		If[(tabind == 1),

 			Do[	addcellnb[headingprint3nb["run" <> ToString[s]]];
				addcellnb[Cell[BoxData[ToBoxes[pop[[s]] // MatrixForm]], "Subsection"]],

				{s, hnrun}]];

		If[(graphind == 1),	

			plot1 	= Table[MultipleListPlot[
						Table[pop[[s, g]], {s, hnrun}],
						plotset1,
						PlotStyle	-> stdruncolor1,
						PlotLabel	-> gennames[[g]],
						AxesLabel	-> {"ageclass", ""},
						PlotLegend 	-> stdrunlegend1],
					{g, ng}];

			addcellnb[Cell[GraphicsData["PostScript",
					DisplayString[GraphicsArray[Table[Show[plot1[[g]]], {g, ng}]]]],
					"Subsection", ImageSize -> Min[{ng himagesize, figsize}]]]];
	
		(* RISK FACTOR CLASS PREVALENCE NUMBERS *)
printbug["3.7"];

		risk	= Table[0, {hnrun}, {r, hnrd}, {ng}, {hncr[[r]]}, {nac[[1]]}];	

		Do[risk[[1, r]] = resmodel[[2, hmodelsel[[m]], 2, scen, 1, r]], {r, hnrd}];

		Do[risk[[s, r]] = If[(pointerrisk[[s - 1, r]] > 0),
						resmodel[[2 s, hmodelsel[[m]], 2, scen, 1, pointerrisk[[s - 1, r]]]],
						risk[[1, r]]],
			{s, 2, hnrun}, {r, hnrd}];

		tekst	= "Risk factor class prevalence numbers" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];

		If[(tabind == 1),

			Do[	addcellnb[headingprint3nb["run" <> ToString[s]]];

				Do[	addcellnb[headingprint3nb["risk factor" <> risknames[[resmodel[[1, 1, r]]]] <> "gender" <>
						gennames[[g]]]];
					addcellnb[Cell[BoxData[ToBoxes[risk[[s, r, g]] // MatrixForm]], "Subsection"]],

					{r, Length[risk[[s]]]}, {g, ng}],

				{s, hnrun}]];

		If[(graphind == 1),	

			plot1 = Table[MultipleListPlot[
						Table[risk[[s, r, g, ri]], {s, hnrun}],
						plotset1,
						PlotStyle 	-> stdruncolor1,
						PlotLabel 	-> gennames[[g]] <> ToString[ri],
						AxesLabel	-> {"ageclass", ""},
						PlotLegend 	-> stdrunlegend1],
					{r, hnrd}, {g, ng}, {ri, hncr[[r]]}];

			Do[	addcellnb[headingprint3nb[risknames[[resmodel[[1, 1, r]]]] <> gennames[[g]]]];
			    	addcellnb[Cell[GraphicsData["PostScript",
					DisplayString[GraphicsArray[
						Table[Show[plot1[[r, g, rj]]], {rj, 4 ri + 1, Min[{4 ri + 4, hncr[[r]]}]}]]]],
					"Subsection", ImageSize -> Min[{(Min[{4 ri + 4, hncr[[r]]}] - 4 ri) himagesize, figsize}]]],
				{r, hnrd}, {g, ng}, {ri, 0, Floor[(hncr[[r]] - 1) / 4]}]];

		(* DISEASE PREVALENCE NUMBERS *)
printbug["3.8"];
		tekst	= "Disease prevalence numbers" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];

		makeresdis1[4, scen];

		(* DISEASE INCIDENCE NUMBERS *)
printbug["3.9"];
		tekst	= "Disease incidence numbers" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];

		makeresdis1[5, scen];

		(* DISEASE-RELATED MORTALITY NUMBERS *)
printbug["3.10"];
		tekst	= "Mortality numbers" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];

		makeresdis1[6, scen];

		(* ALL CAUSE MORTALITY NUMBERS *)
printbug["3.11"];
		prev	= Table[resmodel[[2 s, hmodelsel[[m]], 6, scen, 1, Length[resmodel[[2 s, hmodelsel[[m]], 6, 1, 1]]]]], {s, hnrun}];

		If[(tabind == 1),	

			Do[	addcellnb[headingprint3nb["run" <> ToString[s]]];
				addcellnb[Cell[BoxData[ToBoxes[prev[[s]] // MatrixForm]], "Subsection"]],

				{s, hnrun}]];

		If[(graphind == 1),	

			plot1 	= Table[MultipleListPlot[
						Table[prev[[s, g]], {s, hnrun}],
						plotset1,
						PlotStyle 	-> stdruncolor1,
						PlotLabel 	-> gennames[[g]],
						AxesLabel	-> {"ageclass", ""},
						PlotLegend 	-> stdrunlegend1],
					{g, ng}];

			addcellnb[headingprint3nb["all cause"]];

			addcellnb[Cell[GraphicsData["PostScript",
						DisplayString[GraphicsArray[Table[Show[plot1[[g]]], {g, ng}]]]],
						"Subsection", ImageSize -> Min[{ng himagesize, figsize}]]]],

		{m, Length[hmodelsel]}, {scen, hnscen}];

	Do[
		(* ADJUSTED DISEASE-RELATED EXCESS MORTALITY RATES *)
printbug["3.12"];
		excessmort = Table[0, {hnrun}, {hnd}, {ng}, {nac[[1]]}];

		Do[excessmort[[1, d, g]] = meanaggreg[resmodel[[2, 8, d, g]]], {d, hnd}, {g, ng}];

		Do[excessmort[[s, d]] = If[(pointerdis[[s - 1, d]] > 0),

							Table[meanaggreg[resmodel[[2 s, 8, pointerdis[[s - 1, d]], g]]], {g, ng}],
							excessmort[[1, d]]],
			{s, 2, hnrun}, {d, hnd}];

		tekst	= "Excess mortality rates";
		addcellnb[headingprint2nb[tekst]];

		If[(tabind == 1),	printdis1nb[excessmort]];
		If[(graphind == 1),	plotdis1[excessmort]],

		{m, Length[hmodelsel]}];

	];(* COMPARING RESULTS AT BASELINE *)


(* --------------------------------------------------
	COMPARING RESULTS OVER SIMULATION TIME, AGGREGATED OVER AGE
----------------------------------------------------*)

printbug["4."];

makeresdis2[o_, scen_] := Block[{},

printbug["4.1"];

	prev 	= Table[0, {hnrun}, {hnd}, {ng}, {hnstap}];

	Do[prev[[1, d, g, n]] = Plus@@resmodel[[2, hmodelsel[[m]], o, scen, n, d, g]], {d, hnd}, {g, ng}, {n, hnstap}];

	Do[prev[[s, d]] = If[(pointerdis[[s - 1, d]] > 0),
						Table[Plus@@resmodel[[2 s, hmodelsel[[m]], o, 1, n, pointerdis[[s - 1, d]], g]],
							{g, ng}, {n, hnstap}],
						prev[[1, d]]],
		{s, 2, hnrun}, {d, hnd}];

	If[(tabind == 1),	

		Do[	addcellnb[headingprint3nb["run" <> ToString[s]]];

			Do[	headingprint3nb["disease" <> disnames[[resmodel[[1, 2, d]]]]];
				addcellnb[Cell[BoxData[ToBoxes[prev[[s, d]] // MatrixForm]], "Subsection"]],

				{d, Length[prev[[s]]]}],

			{s, hnrun}]];

	If[(graphind == 1),	

		plot1 = Table[MultipleListPlot[
					Table[prev[[s, d, g]], {s, hnrun}],
					plotset1,
					PlotStyle 	-> stdruncolor1,
					PlotLabel 	-> disnames[[resmodel[[1, 2, d]]]],
					AxesLabel	-> {"time", ""},
					PlotLegend 	-> stdrunlegend1],
				{g, ng}, {d, hnd}];

		Do[	addcellnb[headingprint3nb[gennames[[g]]]];

			Do[addcellnb[Cell[GraphicsData["PostScript",
					DisplayString[GraphicsArray[Table[Show[plot1[[g, d]]], {d, 4 di + 1, Min[{4 di + 4, hnd}]}]]]],
					"Subsection", ImageSize -> Min[{(Min[{4 di + 4, hnd}] - 4 di) himagesize, figsize}]]],
				{di, 0, Floor[(Length[prev[[1]]] - 1) / 4]}],

			{g, ng}]];

	];

If[(hnstap > 1) && (hnrun > 1) && (analyse == 3),

printbug["4.2"];

	Do[	tekst	= "Results over time, aggregated over age, " <> modelnames[[hmodelsel[[m]]]];
		addcellnb[headingprint1nb[tekst]];

		(* TOTAL POPULATION NUMBERS *)
printbug["4.3"];
		pop	= Table[Plus@@resmodel[[2 s, hmodelsel[[m]], 1, scen, n, g]], {s, hnrun}, {g, ng}, {n, hnstap}];

		tekst	= "Total population numbers" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];

		If[(tabind == 1),	

			Do[	addcellnb[headingprint3nb["run" <> ToString[s]]];
				addcellnb[Cell[BoxData[ToBoxes[pop[[s]] // MatrixForm]], "Subsection"]],

				{s, hnrun}]];

		If[(graphind == 1),	

			plot1 	= Table[MultipleListPlot[
						Table[pop[[s, g]], {s, hnrun}],
						plotset1,
						PlotStyle 	-> stdruncolor1,
						PlotLabel 	-> gennames[[g]],
						AxesLabel	-> {"time", ""},
						PlotLegend 	-> stdrunlegend1],
					{g, ng}];

			addcellnb[Cell[GraphicsData["PostScript",
						DisplayString[GraphicsArray[Table[Show[plot1[[g]]], {g, ng}]]]],
						"Subsection", ImageSize -> Min[{ng himagesize, figsize}]]]];

		(* RISK FACTOR CLASS PREVALENCE NUMBERS *)
printbug["4.4"];
		risk	= Table[0, {hnrun}, {r, hnrd}, {ng}, {hncr[[r]]}, {hnstap}];

		Do[risk[[1, r, g, ri, n]] = Plus@@resmodel[[2, hmodelsel[[m]], 2, scen, n, r, g, ri]],
			{r, hnrd}, {g, ng}, {ri, hncr[[r]]}, {n, hnstap}];
	
		Do[risk[[s, r]] = If[(pointerrisk[[s - 1, r]] > 0),
							Table[Plus@@resmodel[[2 s, hmodelsel[[m]], 2, 1, n, pointerrisk[[s - 1, r]], g, ri]],
								{g, ng}, {ri, hncr[[r]]}, {n, hnstap}],
							risk[[1, r]]],
			{s, 2, hnrun}, {r, hnrd}];

		tekst	= "Risk factor class prevalence numbers" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];

		If[(tabind == 1),	

			Do[	addcellnb[headingprint3nb["run" <> ToString[s]]];
		
				Do[	headingprint3nb["risk factor" <> risknames[[resmodel[[1, 1, r]]]] <> "gender" <> gennames[[g]]];
					addcellnb[Cell[BoxData[ToBoxes[risk[[s, r, g]] // MatrixForm]], "Subsection"]],

					{r, Length[risk[[s]]]}, {g, ng}],

				{s, hnrun}]];

		If[(graphind == 1),	

			plot1 	= Table[MultipleListPlot[
						Table[risk[[s, r, g, ri]], {s, hnrun}],
						plotset1,
						PlotStyle 	-> stdruncolor1,
						PlotLabel 	-> risknames[[resmodel[[1, 1, r]]]] <> ToString[ri],
						AxesLabel	-> {"time", ""},
						PlotLegend 	-> stdrunlegend1],
					{r, hnrd}, {g, ng}, {ri, hncr[[r]]}];

			Do[	addcellnb[headingprint3nb[risknames[[resmodel[[1, 1, r]]]] <> gennames[[g]]]];
			    	addcellnb[Cell[GraphicsData["PostScript",
					DisplayString[GraphicsArray[
						Table[Show[plot1[[r, g, rj]]], {rj, 4 ri + 1, Min[{4 ri + 4, hncr[[r]]}]}]]]],
					"Subsection", ImageSize -> Min[{(Min[{4 ri + 4, hncr[[r]]}] - 4 ri) himagesize, figsize}]]],
				{r, hnrd}, {g, ng}, {ri, 0, Floor[(hncr[[r]] - 1) / 4]}]];

		(* DISEASE PREVALENCE NUMBERS *)
printbug["4.5"];
		tekst	= "Disease prevalence numbers" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];

		makeresdis2[4, scen];

		(* DISEASE INCIDENCE NUMBERS *)
printbug["4.6"];
		tekst	= "Disease incidence numbers" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];

		makeresdis2[5, scen];

		(* DISEASE-RELATED MORTALITY NUMBERS *)
printbug["4.7"];
		tekst	= "Mortality numbers" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];

		makeresdis2[6, scen];

		(* OTHER CAUSES AND ALL CAUSE MORTALITY NUMBERS *)
printbug["4.8"];
		prev	= Table[Plus@@resmodel[[2 s, hmodelsel[[m]], 6, scen, n, Length[resmodel[[2 s, hmodelsel[[m]], 6, 1, 1]]] - 2 + d, g]],
				{s, hnrun}, {d, 2}, {g, ng}, {n, hnstap}];

		hmortnames = {"other causes", "all cause"};

		If[(tabind == 1),

			Do[	addcellnb[headingprint3nb["run" <> ToString[s] <> hmortnames[[d]]]];
				addcellnb[Cell[BoxData[ToBoxes[prev[[s, d]] // MatrixForm]], "Subsection"]],

				{s, hnrun}, {d, 2}]];

		If[(graphind == 1),

			plot1 = Table[MultipleListPlot[
						Table[prev[[s, d, g]], {s, hnrun}],
						plotset1,
						PlotStyle 	-> stdruncolor1,
						PlotLabel 	-> hmortnames[[d]],
						AxesLabel	-> {"time", ""},
						PlotLegend 	-> stdrunlegend1],
					{g, ng}, {d, 2}];

			Do[	addcellnb[headingprint3nb[gennames[[g]]]];
	
				addcellnb[Cell[GraphicsData["PostScript",
						DisplayString[GraphicsArray[Table[Show[plot1[[g, d]]], {d, 2}]]]],
						"Subsection", ImageSize -> Min[{2 himagesize, figsize}]]],
			
				{g, ng}]];

	(* LIFE EXPECTANCY NUMBERS *)
printbug["4.9"];
		pop = Table[Plus@@Flatten[resmodel[[2 s, hmodelsel[[m]], 1, scen, Range[hnstap], g]]] /
					(Plus@@resmodel[[2 s, hmodelsel[[m]], 1, scen, 1, g]] + eps),
				{s, hnrun}, {g, ng}];

		addcellnb[headingprint3nb["life expectancy"]];

		addcellnb[Cell[BoxData[ToBoxes[pop // MatrixForm]], "Subsection"]],

		{m, Length[hmodelsel]}, {scen, hnscen}];

	]; (* COMPARING RESULTS OVER SIMULATION TIME, AGGREGATED OVER AGE *)


(* --------------------------------------------------
	COMPARING RESULTS WITH DATA OVER SIMULATION TIME, AGGREGATED OVER AGE
----------------------------------------------------*)

printbug["5."];

makeresdis3[o_, disdat_, scen_] := Block[{},

printbug["5.1"];

	prev	= Table[0, {hnrun + 1}, {hnd}, {ng}, {hnstap}];

	Do[prev[[1, d, g, n]] = disdat[[resmodel[[1, 2, d]], g, Min[{resmodel[[1, 5]] + n - 1, na1}]]],
		{d, hnd}, {g, ng}, {n, hnstap}];

	Do[prev[[2, d, g, n]] = Plus@@resmodel[[2, hmodelsel[[m]], o, scen, n, d, g]] / hnpop[[m, scen, 1, g, n]],
		{d, hnd}, {g, ng}, {n, hnstap}];

	Do[prev[[s + 1, d]] = If[(pointerdis[[s - 1, d]] > 0),

					Table[Plus@@resmodel[[2 s, hmodelsel[[m]], o, scen, n, pointerdis[[s - 1, d]], g]],
						{g, ng}, {n, hnstap}] /
						hnpop[[m, scen, s]],
					prev[[1, d]]],
		{s, 2, hnrun}, {d, hnd}];

	If[(graphind == 1),

		plot1 	= Table[MultipleListPlot[
					Table[prev[[s, d, g]], {s, hnrun + 1}],
					plotset1,
					PlotStyle 	-> stdruncolor2,
					PlotLabel 	-> disnames[[resmodel[[1, 2, d]]]],
					AxesLabel	-> {"time", ""},
					PlotLegend 	-> stdrunlegend2],
				{g, ng}, {d, hnd}];

		Do[	addcellnb[headingprint3nb[gennames[[g]]]];

			Do[addcellnb[Cell[GraphicsData["PostScript",
					DisplayString[GraphicsArray[Table[Show[plot1[[g, d]]], {d, 4 di + 1, Min[{4 di + 4, hnd}]}]]]],
					"Subsection", ImageSize -> Min[{(Min[{4 di + 4, hnd}] - 4 di) himagesize, figsize}]]],
				{di, 0, Floor[(hnd - 1) / 4]}],

			{g, ng}]

		];

	]; 

If[(hnstap >= 1) && (hnrun >= 1) && (analyse == 3) && (resmodel[[1, 5]] == resmodel[[1, 6]]),

printbug["5.2"];

	Do[	tekst	= "Comparison with cohort data specified by year, " <> modelnames[[hmodelsel[[m]]]] <> ", age " <>
				ToString[resmodel[[1, 5]] - 1];
		addcellnb[headingprint1nb[tekst]];

		(* RISK FACTOR CLASS PREVALENCE RATES *)
printbug["5.3"];
		risk = Table[0, {hnrun + 1}, {r, hnrd}, {ng}, {hncr[[r]]}, {hnstap}];

printbug["5.3.1"];

		Do[risk[[1, r, g, ri]] = prisk1[[resmodel[[1, 1, r]], g, ri, agen]], {r, hnrd}, {g, ng}, {ri, hncr[[r]]}];

printbug["5.3.2"];

		Do[	If[(hdisrisk[[r]] == 0),		

			Do[risk[[2, r, g, ri, n]] = Plus@@resmodel[[2, hmodelsel[[m]], 2, scen, n, r, g, ri]] / hnpop[[m, scen, 1, g, n]],
				{g, ng}, {ri, hncr[[r]]}, {n, hnstap}];

			Do[If[(pointerrisk[[s - 1, r]] > 0),
				risk[[s + 1, r]] =
					Table[Plus@@resmodel[[2 s, hmodelsel[[m]], 2, scen, n, pointerrisk[[s - 1, r]], g, ri]] /
						hnpop[[m, scen, s, g, n]],
						{g, ng}, {ri, hncr[[r]]}, {n, hnstap}],
					risk[[1, r]]],
				{s, 2, hnrun}],

			Do[risk[[2, r, g, ri, n]] = Plus@@resmodel[[2, hmodelsel[[m]], 2, scen, n, r, g, ri]] /
								Plus@@resmodel[[2, hmodelsel[[m]], 4, scen, n, hdisrisk[[r]], g]],
				{g, ng}, {ri, hncr[[r]]}, {n, hnstap}];

			Do[If[(pointerrisk[[s - 1, r]] > 0),
				risk[[s + 1, r]] =
					Table[Plus@@resmodel[[2 s, hmodelsel[[m]], 2, scen, n, pointerrisk[[s - 1, r]], g, ri]] /
						Plus@@resmodel[[2, hmodelsel[[m]], 4, scen, n, hdisrisk[[pointerrisk[[s - 1, r]]]], g]],
						{g, ng}, {ri, hncr[[r]]}, {n, hnstap}],
					risk[[1, r]]],
				{s, 2, hnrun}]],

			{r, hnrd}];

		tekst	= "Risk factor class prevalence rates" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];

		If[(graphind == 1),

			plot1 	= Table[MultipleListPlot[
						Table[risk[[s, r, g, ri]], {s, hnrun + 1}],
						plotset1,
						PlotStyle 	-> stdruncolor2,
						PlotLabel 	-> risknames[[resmodel[[1, 1, r]]]] <> ToString[ri],
						AxesLabel	-> {"time", ""},
						PlotLegend 	-> stdrunlegend2],
					{r, hnrd}, {g, ng}, {ri, hncr[[r]]}];


			Do[	addcellnb[headingprint3nb[risknames[[resmodel[[1, 1, r]]]] <> gennames[[g]]]];
			    	addcellnb[Cell[GraphicsData["PostScript",
					DisplayString[GraphicsArray[
						Table[Show[plot1[[r, g, rj]]], {rj, 4 ri + 1, Min[{4 ri + 4, hncr[[r]]}]}]]]],
					"Subsection", ImageSize -> Min[{(Min[{4 ri + 4, hncr[[r]]}] - 4 ri) himagesize, figsize}]]],
				{r, hnrd}, {g, ng}, {ri, 0, Floor[(hncr[[r]] - 1) / 4]}]];

		(* DISEASE PREVALENCE RATES *)
printbug["5.4"];
		tekst	= "Disease prevalence rates" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];

		makeresdis3[4, pdis1, scen];

		(* DISEASE INCIDENCE RATES *)
printbug["5.5"];
		tekst	= "Disease incidence rates" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];

		makeresdis3[5, inc1, scen];

		(* DISEASE-RELATED MORTALITY RATES *)
printbug["5.6"];
		tekst	= "Mortality rates" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];

		If[(resmodel[[1, 3]] == 2), mortdat = causemort1];

		If[(resmodel[[1, 3]] == 1),

			hexcessmort = Min1[Max0[Table[Minc[
						excessmort1[[d, g]],
						(causemort1[[d, g]] -
							inc1[[d, g]] casefat1[[casefatind0[[d]], g]]) / (pdis1[[d, g]] + eps)],
						{d, nd0}, {g, ng}]]];

			mortdat = Table[pdis1[[d, g]] hexcessmort[[d, g]] +
					inc1[[d, g]] (casefat1[[casefatind0[[d]], g]] +
					(1 - casefat1[[casefatind0[[d]], g]]) .5 hexcessmort[[d, g]]),
					{d, nd0}, {g, ng}]

			];
 
		makeresdis3[6, mortdat, scen];

		(* ALL CAUSE MORTALITY RATES *)
printbug["5.7"];
		prev = Table[0, {hnrun + 1}, {ng}, {hnstap}];

		Do[prev[[1, g, n]] = morttot1[[g, Min[{resmodel[[1, 5]] + n - 1, na1}]]], {g, ng}, {n, hnstap}];

		Do[prev[[s + 1, g, n]] =
				Plus@@resmodel[[2 s, hmodelsel[[m]], 6, scen, n, Length[resmodel[[2 s, hmodelsel[[m]], 6, scen, 1]]], g]] /
					hnpop[[m, scen, s, g, n]],
				{s, hnrun}, {g, ng}, {n, hnstap}];

		If[(graphind == 1),

			plot1 = Table[MultipleListPlot[
						Table[prev[[s, g]], {s, hnrun + 1}],
						plotset1,
						PlotStyle 	-> stdruncolor2,
						PlotLabel 	-> gennames[[g]],
						AxesLabel	-> {"time", ""},
						PlotLegend 	-> stdrunlegend2],
					{g, ng}];

			addcellnb[headingprint3nb["all cause"]];

			addcellnb[Cell[GraphicsData["PostScript",
						DisplayString[GraphicsArray[Table[Show[plot1[[g]]], {g, ng}]]]],
						"Subsection", ImageSize -> Min[{ng himagesize, figsize}]]]],

		{m, Length[hmodelsel]}, {scen, hnscen}];

	]; (* COMPARING RESULTS OVER SIMULATION TIME, AGGREGATED OVER AGE *)


(* --------------------------------------------------
	COMPARING MODEL INPUT AND SUBSEQUENT PARAMETER VALUES
----------------------------------------------------*)

printbug["6."];

If[(hnstap == 1) && (hnrun == 1) && (analyse == 3),

	tekst	= "From input data to model parameter values";
	addcellnb[headingprint1nb[tekst]];

	(* RELATIVE RISKS FOR EACH RISK FACTOR AND DISEASE SELECTED IN BASELINE RUN *)

printbug["6.1"];

	RRA 	= Table[RRrisk0[[resmodel[[1, 1, r]], RRriskind0[[resmodel[[1, 1, r]], resmodel[[1, 2, d]] + 1]]]], {r, hnrd}, {d, hnd}];

printbug["6.1.1"];

	RRB 	= Table[meanaggreg[RRrisk1[[resmodel[[1, 1, r]], RRriskind0[[resmodel[[1, 1, r]], resmodel[[1, 2, d]] + 1]], g, ri]]],
			{r, hnrd}, {d, hnd}, {g, ng}, {ri, hncr[[r]]}];

printbug["6.1.2"];

	RRC 	= Table[meanaggreg[resmodel[[2, 10, r, RRriskind0[[resmodel[[1, 1, r]], resmodel[[1, 2, d]] + 1]], g, ri]]],
			{r, hnrd}, {d, hnd}, {g, ng}, {ri, hncr[[r]]}];

printbug["6.1.3"];

	RRABC	= Table[{RRA[[r, d, g, ri]], RRB[[r, d, g, ri]], RRC[[r, d, g, ri]]}, {r, hnrd}, {d, hnd}, {g, ng}, {ri, hncr[[r]]}];
				
printbug["6.1.4"];

	plot1 	= Table[MultipleListPlot[RRABC[[r, d, g, ri]],
				plotset1,
				PlotStyle 	-> Table[{RGBColor[s / 2, 0, 1 - s / 2]}, {s, 0, 2}],
				PlotLabel 	-> disnames[[resmodel[[1, 2, d]]]],
				AxesLabel	-> {"ageclass", ""},
				PlotLegend 	-> {"data", "smooth", "adj"}],
			{r, hnrd}, {g, ng}, {d, hnd}, {ri, hncr[[r]]}];
	
	tekst	= "RR's empirical and adjusted";
	addcellnb[headingprint2nb[tekst]];

printbug["6.1.5"];

	Do[	tekst	= risknames[[resmodel[[1, 1, r]]]] <> " class " <> ToString[ri] <> " " <> gennames[[g]];
		addcellnb[headingprint3nb[tekst]];

printbug["6.1.6"];

		If[(tabind == 1),

			Do[	addcellnb[headingprint3nb[disnames[[resmodel[[1, 2, d]]]]]];
				addcellnb[Cell[BoxData[ToBoxes[roundoff[RRABC[[r, d, g, ri]], 100] // MatrixForm]], "Subsection"]],

				{d, hnd}]];
printbug["6.1.7"];

		If[(graphind == 1),

			addcellnb[Table[Cell[GraphicsData["PostScript", DisplayString[GraphicsArray[
					Table[Show[plot1[[r, g, d, ri]]], {d, 4 di + 1, Min[{4 di + 4, hnd}]}]]]],
					"Subsection", ImageSize -> Min[{(Min[{4 di + 4, hnd}] - 4 di) himagesize, figsize}]],
				{di, 0, Floor[(hnd - 1) / 4]}]]],

		{r, hnrd}, {g, ng}, {ri, hncr[[r]]}];

	(* RELATIVE RISKS FOR SMOKING IN CASE OF DURATION-DEPENDENT *)

printbug["6.2"];

	If[(RRsmokduurind == 1),	

		RRC 	= Table[meanaggreg[resmodel[[2, 10, 1, RRriskind0[[resmodel[[1, 1, 1]], resmodel[[1, 2, d]] + 1]], g, 3]]],
				{d, hnd}, {g, ng}];
printbug["6.2.1"];	

		age1	= 5 Range[nac[[1]]] - 2.5;

printbug["6.2.3"];

		duurval	= Range[nstopduur] - .5;

printbug["6.2.4"];

		meanstopduur = Table[meanaggreg[stopduur[[g, ri]]], {g, ng}, {ri, nstopduur}];

printbug["6.2.5"];		

		RRform	= Max1[Table[Plus@@((1 + (RRrisk0[[1, d, g, 2, a]] - 1) Exp[-logRRsmokduur[[d, g, 1]] *
						Exp[-logRRsmokduur[[d, g, 2]] Max0[age1[[a]] - 51]] duurval]) *
						meanstopduur[[g, Range[nstopduur], a]]) /
					(Plus@@meanstopduur[[g, Range[nstopduur], a]] + eps),
				{d, Length[logRRsmokduur]}, {g, ng}, {a, nac[[1]]}]];

printbug["6.2.6"];

		RRD	= Table[RRform[[RRriskind0[[resmodel[[1, 1, 1]], resmodel[[1, 2, d]] + 1]], g]], {d, hnd}, {g, ng}];

printbug["6.2.7"];

		RRCD	= Table[{RRC[[d, g]], RRD[[d, g]]}, {d, hnd}, {g, ng}];		

printbug["6.2.8"];

		plot1 	= Table[MultipleListPlot[
					RRCD[[d, g]],
					plotset1,
					PlotStyle 	-> Table[{RGBColor[s, 0, 1 - s]}, {s, 0, 1}],
					PlotLabel 	-> disnames[[resmodel[[1, 2, d]]]],
					AxesLabel	-> {"ageclass", ""},
					PlotLegend 	-> {"age", "duration"}],
				{g, ng}, {d, hnd}];

printbug["6.2.9"];		

		tekst	= "smoking RR's empirical and adjusted";
		addcellnb[headingprint2nb[tekst]];
		
		Do[	tekst	= gennames[[g]];

			addcellnb[headingprint3nb[tekst]];

			If[(tabind == 1),

				Do[	addcellnb[headingprint3nb[disnames[[resmodel[[1, 2, d]]]]]];
					addcellnb[Cell[BoxData[ToBoxes[roundoff[RRCD[[d, g]], 100] // MatrixForm]], "Subsection"]],

					{d, hnd}]];

			If[(graphind == 1),

				addcellnb[Table[Cell[GraphicsData["PostScript", DisplayString[GraphicsArray[
						Table[Show[plot1[[g, d]]], {d, 4 di + 1, Min[{4 di + 4, hnd}]}]]]],
						"Subsection", ImageSize -> Min[{(Min[{4 di + 4, hnd}] - 4 di) himagesize, figsize}]],
					{di, 0, Floor[(hnd - 1) / 4]}]]],

			{g, ng}]

		];

	(* RELATIVE RISKS FOR ALL CAUSE MORTALITY *)

printbug["6.3"];

	tekst	= "all cause mortality RR's empirical and adjusted";
	addcellnb[headingprint2nb[tekst]];

printbug["6.3.1"];

	RRA	= Table[RRrisk0[[resmodel[[1, 1, r]], 2]], {r, hnrd}];

printbug["6.3.2"];

	RRB	= Table[meanaggreg[RRrisk1[[resmodel[[1, 1, r]], 2, g, ri]]], {r, hnrd}, {g, ng}, {ri, hncr[[r]]}];

printbug["6.3.3"];

	RRC	= Table[meanaggreg[RRriskpresel[[resmodel[[1, 1, r]], 2, g, ri]]], {r, hnrd}, {g, ng}, {ri, hncr[[r]]}];

printbug["6.3.4"];

	RRD	= Table[(resmodel[[2, hmodelsel[[m]], 9, 1, 1, r, g, ri]] /
						(resmodel[[2, hmodelsel[[m]], 2, 1, 1, r, g, ri]] + eps)) /
					((resmodel[[2, hmodelsel[[m]], 9, 1, 1, r, g, 1]] + eps) /
						(resmodel[[2, hmodelsel[[m]], 2, 1, 1, r, g, 1]] + eps)),
				{m, Length[hmodelsel]}, {r, hnrd}, {g, ng}, {ri, hncr[[r]]}];

printbug["6.3.5"];

	RRABCD	= Table[Join[	{RRA[[r, g, ri]], RRB[[r, g, ri]], RRC[[r, g, ri]]},
					Table[RRD[[m, r, g, ri]], {m, Length[hmodelsel]}]],
			{r, hnrd}, {g, ng}, {ri, hncr[[r]]}];

printbug["6.3.6"];

	hn	= 2 + Length[hmodelsel];

printbug["6.3.7"];

	hname	= Join[	{"data", "smooth", "presel"}, Table["model " <> ToString[m], {m, Length[hmodelsel]}]];

printbug["6.3.8"];

	plot1	= Table[MultipleListPlot[
				RRABCD[[r, g, ri]],
				plotset1,
				PlotStyle 	-> Table[{RGBColor[s / hn, 0, 1 - s / hn]}, {s, 0, hn}],
				PlotLabel 	-> gennames[[g]] <> " class " <> ToString[ri],
				AxesLabel	-> {"ageclass", ""},
				PlotLegend 	-> hname],
			{r, hnrd}, {g, ng}, {ri, hncr[[r]]}];

printbug["6.3.9"];

	Do[	tekst	= risknames[[resmodel[[1, 1, r]]]];
		addcellnb[headingprint3nb[tekst]];

printbug["6.3.10"];

		If[(tabind == 1),

			Do[	addcellnb[headingprint3nb[gennames[[g]] <> " class " <> ToString[ri]]];
				addcellnb[Cell[BoxData[ToBoxes[roundoff[RRABCD[[r, g, ri]], 100] // MatrixForm]], "Subsection"]],

				{g, ng}, {ri, hncr[[r]]}]];

printbug["6.3.11"];

		If[(graphind == 1),

			Do[	addcellnb[Cell[GraphicsData["PostScript",
						DisplayString[GraphicsArray[Table[Show[plot1[[r, g, ri]]], {ri, hncr[[r]]}]]]],
						"Subsection", ImageSize -> Min[{hncr[[r]] himagesize, figsize}]]],
				{g, ng}]],

		{r, hnrd}];

	(* DISEASE-RELATED EXCESS MORTALITY RATES IN BASELINE RUN *)

printbug["6.4"];

	emA 	= excessmort0[[resmodel[[1, 2]]]];

	emB 	= Table[meanaggreg[excessmort1[[resmodel[[1, 2, d]], g]]], {d, hnd}, {g, ng}];

	emC 	= Table[meanaggreg[resmodel[[2, 8, d, g]]], {d, hnd}, {g, ng}];

	plot1 	= Table[MultipleListPlot[
				{emA[[d, g]], emB[[d, g]], emC[[d, g]]},
				plotset1,
				PlotStyle 	-> Table[{RGBColor[s / 2, 0, 1 - s / 2]}, {s, 0, 2}],
				PlotLabel 	-> disnames[[resmodel[[1, 2, d]]]],
				AxesLabel	-> {"ageclass", ""},
				PlotRange 	-> All,
				PlotLegend 	-> {"data", "smoothed", "adjusted"}],
			{g, ng}, {d, hnd}];

	
	tekst	= "excess mortality rates";
	addcellnb[headingprint2nb[tekst]];

	Do[	addcellnb[headingprint3nb[gennames[[g]]]];

		If[(graphind == 1),

			addcellnb[Table[Cell[GraphicsData["PostScript",DisplayString[GraphicsArray[
				Table[Show[plot1[[g, d]]], {d, 4 di + 1, Min[{4 di + 4, hnd}]}]]]],
				"Subsection", ImageSize -> Min[{(Min[{4 di + 4, hnd}] - 4 di) himagesize, figsize}]],

				{di, 0, Floor[(hnd - 1) / 4]}]]], 

		{g, ng}];

	Do[	tekst	= "for " <> modelnames[[hmodelsel[[m]]]];
		addcellnb[headingprint2nb[tekst]];

		(* ALL CAUSE MORTALITY RATES *)

printbug["6.5"];

		morttotreal = resmodel[[2, hmodelsel[[m]], 6, 1, 1, Length[resmodel[[2, hmodelsel[[m]], 6, 1, 1]]]]] /
				(resmodel[[2, hmodelsel[[m]], 1, 1, 1]] + eps);

		plot1	= Table[MultipleListPlot[
					{morttot0[[g]], meanaggreg[morttot1[[g]]], morttotreal[[g]]},
					plotset1,
					PlotStyle 	-> Table[{RGBColor[s / 2, 0, 1 - s / 2]}, {s, 0, 2}],
					AxesLabel	-> {"ageclass", ""},
					PlotLabel 	-> gennames[[g]],
					PlotLegend 	-> {"data", "smoothed", "realised"}],
				{g, ng}];

		tekst	= "all cause mortality rates";
		addcellnb[headingprint2nb[tekst]];

		If[(graphind == 1),

			addcellnb[Cell[GraphicsData["PostScript",
					DisplayString[GraphicsArray[Table[Show[plot1[[g]]], {g, ng}]]]],
					"Subsection", ImageSize -> Min[{ng himagesize, figsize}]]]],		

		{m, Length[hmodelsel]}];

	(* RELATIVE RISKS FOR EACH RISK PAIR OF DISEASES SELECTED IN BASELINE RUN *)

printbug["6.6"];

	RRA	= Table[RRdis0[[RRdisind[[disind[[d]], disind[[d1]]]]]], {d, hnd}, {d1, hnd}];

	RRB 	= Table[meanaggreg[RRdispresel[[RRdisind[[ disind[[d]], disind[[d1]]]], g]]], {d, hnd}, {d1, hnd}, {g, ng}];

	RRC 	= Table[meanaggreg[RRdisadj[[RRdisind[[ disind[[d]], disind[[d1]]]], g]]], {d, hnd}, {d1, hnd}, {g, ng}];

	RRD 	= Table[meanaggreg[RRdisprev[[d, d1, g]]], {d, hnd}, {d1, hnd}, {g, ng}];

	plot1	= Table[MultipleListPlot[
				{RRA[[d, d1, g]], RRB[[d, d1, g]], RRC[[d, d1, g]], RRD[[d, d1, g]]},
				plotset1,
				PlotStyle 	-> Table[{RGBColor[s / 3, 0, 1 - s / 3]}, {s, 0, 3}],
				AxesLabel	-> {"ageclass", ""},
				PlotLabel 	-> disnames[[disind[[d1]]]],
				PlotLegend 	-> {"data", "unadjusted", "adjusted", "prevalence"}],
				{g, ng}, {d, hnd}, {d1, hnd}];

	tekst	= "co-morbidity RR's";
	addcellnb[headingprint2nb[tekst]];

	If[(graphind == 1),

		Do[	addcellnb[headingprint3nb[gennames[[g]] <> disnames[[disind[[d]]]] <> " on"]];

			Do[addcellnb[Table[Cell[GraphicsData["PostScript",DisplayString[GraphicsArray[
				Table[Show[plot1[[g, d, d1]]], {d1, 4 di + 1, Min[{4 di + 4, hnd}]}]]]],
				"Subsection", ImageSize -> Min[{(Min[{4 di + 4, hnd}] - 4 di) himagesize, figsize}]],

				{di, 0, Floor[(hnd - 1) / 4]}]]], 

		{g, ng}, {d, hnd}]]

	];


(* --------------------------------------------------
	COMPARING MODEL INPUT AND RESULTS OVER TIME FOR FIXED AGE CLASS
----------------------------------------------------*)

printbug["7."];

makeresdis4[o_, disdat_, scen_] := Block[{},

	prev = Table[0, {hnrun + 1}, {hnd}];

	Do[prev[[1, d]] = Table[(meanaggreg[disdat[[resmodel[[1, 2, d]], g]]])[[ageclssel]], {g, ng}, {hnstap}], {d, hnd}];

printbug["7.1"];

	Do[prev[[2, d]] = Table[resmodel[[2, hmodelsel[[m]], o, scen, Range[hnstap], d, g, ageclssel]] / (hnpop[[1, g]] + eps), {g, ng}],
				{d, hnd}];

printbug["7.2"];

	Do[prev[[s + 1, d]] = If[(pointerdis[[s - 1, d]] > 0),

					Table[resmodel[[2 s, hmodelsel[[m]], o, scen, Range[hnstap], pointerdis[[s - 1, d]], g, ageclssel]] /
						(hnpop[[s, g]] + eps),
						{g, ng}],
					prev[[1, d]]],

				{s, 2, hnrun}, {d, hnd}];

printbug["7.3"];

	If[(graphind == 1),

		plot1 	= Table[MultipleListPlot[
					Table[prev[[s, d, g]], {s, hnrun + 1}],
					plotset1,
					PlotStyle 	-> stdruncolor2,
					PlotLabel 	-> disnames[[resmodel[[1, 2, d]]]],
					AxesLabel	-> {"time", ""},
					PlotLegend 	-> stdrunlegend2],
				{g, ng}, {d, hnd}];

		Do[	addcellnb[headingprint3nb[gennames[[g]]]];

			Do[addcellnb[Cell[GraphicsData["PostScript",
					DisplayString[GraphicsArray[Table[Show[plot1[[g, d]]], {d, 4 di + 1, Min[{4 di + 4, hnd}]}]]]],
					"Subsection", ImageSize -> Min[{(Min[{4 di + 4, hnd}] - 4 di) himagesize, figsize}]]],
				{di, 0, Floor[(hnd - 1) / 4]}],

			{g, ng}]

		]

	];

If[(hnstap > 1) && (hnrun >= 1) && (analyse == 3) && (resmodel[[1, 5]] == 1) && (resmodel[[1, 6]] == na1),

	Do[	hnpop = Table[resmodel[[2 s, hmodelsel[[m]], 1, scen, n, g, ageclssel]], {s, hnrun}, {g, ng}, {n, hnstap}];

printbug["7.4"];
	
		tekst	= "Comparison with cross-sectional data specified by year, " <> modelnames[[hmodelsel[[m]]]] <> ", ageclass "<>
				ToString[initageclass[[1, ageclssel]]] <> " - " <> ToString[initageclass[[1, ageclssel + 1]] - 1];
		addcellnb[headingprint1nb[tekst]];

		(* POPULATION NUMBERS *)

printbug["7.5"];
		
		pop		= Table[0, {hnrun + 1}];
		pop[[1]] 	= Table[npop1[[g, ageclssel]], {g, ng}, {hnstap}];
		pop[[1 + Range[hnrun]]] = hnpop;

		tekst	= "Population numbers" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];

		If[(graphind == 1),

			plot1 	= Table[MultipleListPlot[
						Table[pop[[s, g]], {s, hnrun + 1}],
						plotset1,
						PlotStyle 	-> stdruncolor2,
						PlotLabel 	-> gennames[[g]],
						AxesLabel	-> {"time", ""},
						PlotLegend 	-> stdrunlegend2],
					{g, ng}];

			addcellnb[Cell[GraphicsData["PostScript",
					DisplayString[GraphicsArray[Table[Show[plot1[[g]]], {g, ng}]]]],
					"Subsection", ImageSize -> Min[{ng himagesize, figsize}]]]

			];

		(* RISK FACTOR CLASS PREVALENCE NUMBERS *)

printbug["7.5"]; 

		risk = Table[0, {hnrun + 1}, {r, hnrd}];

		Do[risk[[1, r]] = Table[(meanaggreg[prisk1[[resmodel[[1, 1, r]], g, ri]]])[[ageclssel]],
						{g, ng}, {ri, hncr[[r]]}, {hnstap}],
					{r, hnrd}];

		Do[	If[(hdisrisk[[r]] == 0),

			(* RISK FACTORS FOR TOTAL POPULATION *)

printbug["7.51"];

			risk[[2, r]] = Table[resmodel[[2, hmodelsel[[m]], 2, scen, n, r, g, ri, ageclssel]] / (hnpop[[1, g, n]] + eps),
						{g, ng}, {ri, hncr[[r]]}, {n, hnstap}];
printbug["7.52"];

			Do[risk[[s + 1, r]] = If[(pointerrisk[[s - 1, r]] > 0),

							Table[resmodel[[2 s, hmodelsel[[m]], 2, scen, n, r, g, ri, ageclssel]] /
								(hnpop[[s, g, n]] + eps),
								{g, ng}, {ri, hncr[[r]]}, {n, hnstap}],
							risk[[1, r]]],

						{s, 2, hnrun}],

			(* DISEASE-RELATED RISK FACTORS *)

printbug["7.53"];

			risk[[2, r]] = Table[resmodel[[2, hmodelsel[[m]], 2, scen, n, r, g, ri, ageclssel]] /
						(resmodel[[2, hmodelsel[[m]], 4, scen, n, hdisrisk[[r]], g, ageclssel]] + eps),
						{g, ng}, {ri, hncr[[r]]}, {n, hnstap}];

			Do[risk[[s + 1, r]] = Table[resmodel[[2, hmodelsel[[m]], 2, scen, n, r, g, ri, ageclssel]] /
							(resmodel[[2, hmodelsel[[m]], 4, scen, n, hdisrisk[[pointerrisk[[s - 1, r]]]], g,
								ageclssel]] + eps),
							{g, ng}, {ri, hncr[[r]]}, {n, hnstap}],
						{s, 2, hnrun}]],

			{r, hnrd}];

printbug["7.54"]; 

		tekst	= "Risk factor class prevalence rates" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];

		If[(graphind == 1),

			plot1 	= Table[MultipleListPlot[
						Table[risk[[s, r, g, ri]], {s, hnrun + 1}],
						plotset1,	
						PlotStyle 	-> stdruncolor2,
						PlotLabel 	-> risknames[[resmodel[[1, 1, r]]]] <> ToString[ri],
						AxesLabel	-> {"time", ""},
						PlotLegend 	-> stdrunlegend2],
					{r, hnrd}, {g, ng}, {ri, hncr[[r]]}];

			Do[	addcellnb[headingprint3nb[risknames[[resmodel[[1, 1, r]]]] <> gennames[[g]]]];
			    	addcellnb[Cell[GraphicsData["PostScript",
					DisplayString[GraphicsArray[
						Table[Show[plot1[[r, g, rj]]], {rj, 4 ri + 1, Min[{4 ri + 4, hncr[[r]]}]}]]]],
					"Subsection", ImageSize -> Min[{(Min[{4 ri + 4, hncr[[r]]}] - 4 ri) himagesize, figsize}]]],
				{r, hnrd}, {ri, 0, Floor[(hncr[[r]] - 1) / 4]}, {g, ng}]];

		(* DISEASE PREVALENCE NUMBERS *)

printbug["7.6"];

		tekst	= "Disease prevalence rates" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];

		makeresdis4[4, pdis1, scen];

		(* DISEASE INCIDENCE NUMBERS *)

printbug["7.7"];

		tekst	= "Disease incidence rates" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];

		makeresdis4[5, inc1, scen];

		(* ALL CAUSE MORTALITY NUMBERS *)

printbug["7.7"];

		tekst	= "All cause mortality rates" <> ", scenario " <> ToString[scen];
		addcellnb[headingprint2nb[tekst]];

		mort		= Table[0, {hnrun + 1}];
		mort[[1]]	= Table[morttot0[[g, ageclssel]], {g, ng}, {hnstap}];
		Do[mort[[s + 1]] = Table[resmodel[[2 s, hmodelsel[[m]], 6, scen, Range[hnstap],
						Length[resmodel[[2 s, hmodelsel[[m]], 6, scen, 1]]], g, ageclssel]] / (hnpop[[s, g]] + eps),
						{g, ng}],
					{s, hnrun}];
		
		If[(graphind == 1),

			plot1 	= Table[MultipleListPlot[
						Table[mort[[s, g]], {s, hnrun + 1}],
						plotset1,
						PlotStyle 	-> stdruncolor2,
						PlotLabel 	-> gennames[[g]],
						AxesLabel	-> {"time", ""},
						PlotLegend 	-> stdrunlegend2],
					{g, ng}];

			addcellnb[Cell[GraphicsData["PostScript",
					DisplayString[GraphicsArray[Table[Show[plot1[[g]]], {g, ng}]]]],
					"Subsection", ImageSize -> Min[{ng himagesize, figsize}]]]

			],

		{m, Length[hmodelsel]}, {scen, hnscen}]

	]; (* END COMPARING MODEL INPUT AND RESULTS OVER TIME FOR FIXED AGE CLASS *)


If[(analyse == 3), NotebookWrite[hnbout, Flatten[cellnb]]];
	

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
