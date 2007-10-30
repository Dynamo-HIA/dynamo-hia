(* :Title: CZMPresentResults *)

(* :Context: CZMPostProcessing` *)

(* :Author: Rudolf T. Hoogenveen, Roel G.M. Breuls *)

(* :Summary:
   CZM postprocessing routine presents results *)

(* :Copyright: © 2004 by Rudolf Hoogenveen/Roel Breuls *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March
		3.1 version March 2007, storage of indexvalues, new packages CZMStoreResults *)

(* :Keywords: postprocessing, plots *)


BeginPackage["CZMPostProcessing`CZMPresentResults`",
	{"CZMMain`CZMMain`",
	"CZMInitialization`CZMLogFile`",
	"CZMInitialization`CZMDefaultFileNames`",
	"CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`", 
	"CZMImportData`CZMImportUserSelections`",
	"CZMImportData`CZMImportDiseaseData`",
	"CZMImportData`CZMImportDALYs`",
	"CZMImportData`CZMImportCosts`",
	"CZMAdjustData`CZMMakeSelections`",
	"CZMAdjustData`CZMAdjustAfterSelection`",
	"CZMSimulation`CZMSimulationFunctions`",
	"CZMSimulation`CZMSimulationMarginalModelDetermPop`",
	"CZMSimulation`CZMSimulationJointModelDetermPop`",
	"CZMSimulation`CZMSimulationJointModelDetermAge`",
	"CZMSimulation`CZMSimulationJointModelStochInd`",
	"CZMSimulation`CZMStoreResults`",
	"CZMPostProcessing`CZMExportUserSelections`",
	"CZMPostProcessing`CZMCalcResults`",
	"Graphics`Legend`",
	"Graphics`MultipleListPlot`",
	"Graphics`FilledPlot`"}]

Begin["`Private`"]

Print["CZMPresentResults package is evaluated"];
printtijd;

printbug[c_] := If[(bugind == 1), Print[{"CZMPresentResults", c}]];

printbug["1."];

imagesize	= 240;
stdaxislabel 	= {"time", ""};
stdagecolor1	= Table[{RGBColor[(ai - 1) / (nac[[7]] - 1), 0, 1 - (ai - 1) / (nac[[7]] - 1)]}, {ai, nac[[7]]}];
stdagecolor2	= Join[	{{{Axis, 1}, RGBColor[0, 0, 1]}},
			Table[{{ai, ai + 1}, RGBColor[ai / (nac[[7]] - 1), 0, 1 - ai / (nac[[7]] - 1)]}, {ai, nac[[7]] - 1}]
			];
stdmodelcolor	= Table[{RGBColor[m / Plus@@modelsel, 0, 1 - m / Plus@@modelsel]}, {m, 0, Plus@@modelsel - 1}];

stdriskcolor[r_] := Join[{{{Axis, 1}, RGBColor[0, 1, 0]}},
			Table[{{ri, ri + 1}, RGBColor[ri / (ncrsel[[r]] - 1), 1 - ri / (ncrsel[[r]] - 1), 0]},
				{ri, ncrsel[[r]] - 1}]
			];
stdagelegend	= Table["age" <> ToString[initageclass[[7, ai]]] <> "+", {ai, nac[[7]]}];
stdmodellegend	= Table["model" <> ToString[m], {m, Plus@@modelsel}];

stdlabel[r_, ri_] := risknames[[riskindd]][[r]] <> "class " <> ToString[ri];
stdlabelc[r_, ri_] := risknames[[riskindc]][[r]] <> "parameter " <> ToString[ri];
basescenstr 	= " for baseline scenario";
compscenstr 	= " differences with baseline scenario";
ratesstr	= {" absolute numbers "," rates "}[[rates + 1]];

plotset1	= {	DisplayFunction -> Identity, 
			PlotJoined 	-> True,  
			TextStyle 	-> stdtextstyle,
			AxesLabel 	-> stdaxislabel,
			PlotRange	-> All};

plotset2	= {	DisplayFunction -> Identity,
			SymbolShape 	-> None,
			LegendPosition 	-> {-.8, .0},
			LegendSize 	-> {.65, .5},
			PlotJoined 	-> True,
			TextStyle 	-> stdtextstyle,
			AxesLabel 	-> stdaxislabel,
			PlotRange	-> All};

plotset3	= {	PlotRange	-> All,
			DisplayFunction -> Identity,
			TextStyle 	-> stdtextstyle,
			AxesLabel 	-> stdaxislabel,
			PlotRange 	-> All};
										

(*-------------------------------------------------
	PLOTS TOTAL POPULATION NUMBERS & TIME SINCE SMOKING CESSATION
---------------------------------------------------*)

printbug["2."];

(* WRITE TO SCREEN *)

plotpop[respop_, naam_, diffscen_] := Block[{},
	headingprint1[naam];
	Table[Show[
		headingprint3["gender: " <> gennames[[g]]];
	    	GraphicsArray[Table[
			If[(agespecres <= 1),

				(* NUMBERS AGGREGATED OVER AGE *)

				ListPlot[respop[[scen, g]],
					plotset1,
					PlotLabel 	-> scenname[scen, diffscen]],

			If[(agespecres == 2),

				(* NUMBERS SPECIFIED BY AGE CLASS, NOT STACKED *)

				makemultiplelistplot[Table[respop[[scen, g, Range[nstap], ai]], {ai, nac[[7]]}],
					{plotset2,
					PlotStyle 	-> stdagecolor1,
					PlotLegend 	-> stdagelegend,
					PlotLabel 	-> scenname[scen, diffscen]}],

			If[(agespecres == 3),

				(* NUMBERS SPECIFIED BY AGE CLASS, STACKED *)

				respop1 = Table[Plus@@respop[[scen, g, n, Range[ai]]], {ai, nac[[7]]}, {n, nstap}];
				frespop[x_] := Table[respop1[[ai, Round[x]]], {ai, nac[[7]]}];
				FilledPlot[frespop[x], {x, nstap},
					plotset3,
					PlotLabel 	-> scenname[scen, diffscen],
					Fills 		-> stdagecolor2]
				]]],
		     	{scen, Length[respop]}]],
		ImageSize 	-> Min[{nscen imagesize, 900}],
		DisplayFunction :> $DisplayFunction,
		Background 	-> RGBColor[1, 1, 1]
		],
	{g, ng}]
	];

(* WRITE TO NOTEBOOK *)

plotpopnb[respop_, naam_, diffscen_] := 
	{
	headingprint2nb[naam <> If[(diffscen == 0), basescenstr, compscenstr]],
	Table[	{
		headingprint3nb["gender: " <> gennames[[g]]],
	    	Cell[	GraphicsData[
				"PostScript",
				DisplayString[
					GraphicsArray[
						Table[	If[(agespecres <= 1),

								(* NUMBERS AGGREGATED OVER AGE *)

								ListPlot[respop[[scen, g]],
									plotset1,
									PlotLabel 	-> scenname[scen, diffscen], 
									PlotStyle 	-> {Thickness[.02]}],

							If[(agespecres == 2),

								(* NUMBERS SPECIFIED BY AGE CLASS, NOT STACKED *)

								makemultiplelistplot[Table[respop[[scen, g, Range[nstap], ai]],
														{ai, nac[[7]]}],
									{plotset2,
									PlotStyle 	-> stdagecolor1,
									PlotLegend 	-> stdagelegend,
									PlotLabel 	-> scenname[scen, diffscen]}],

							If[(agespecres == 3),

								(* NUMBERS SPECIFIED BY AGE CLASS, STACKED *)

								respop1 = Table[Plus@@respop[[scen, g, n, Range[ai]]],
										{ai, nac[[7]]}, {n , nstap}];
								frespop[x_] := Table[respop1[[ai, Round[x]]], {ai, nac[[7]]}];
								FilledPlot[frespop[x], {x, nstap},
									plotset3,
									PlotLabel 	-> scenname[scen, diffscen],
									Fills 		-> stdagecolor2]
								]]],
						     	{scen, Length[respop]}]
						]
					]
				],
			"Subsection",
			ImageSize -> Min[{nscen imagesize, 900}]
			
			]
		},
	{g, ng}]
	};

(* WRITE TO NOTEBOOK, RESULTS FOR SEVERAL CZM MODEL VERSIONS SIMULTANEOUSLY *)

plotpopnb1[respop_, naam_, diffscen_] := 
	{
	headingprint2nb[naam <> If[(diffscen == 0), basescenstr, compscenstr]],
	Table[	{
		headingprint3nb["gender: " <> gennames[[g]]],
	    	Cell[	GraphicsData[
				"PostScript",
				DisplayString[
					GraphicsArray[
						Table[	If[(agespecres <= 1),

								(* NUMBERS AGGREGATED OVER AGE *)

								makemultiplelistplot[Table[respop[[m, scen, g]], {m, Length[respop]}],
									{plotset2,
									PlotLegend 	-> stdmodellegend,
									PlotLabel 	-> scenname[scen, diffscen], 
									PlotJoined 	-> True, 
									PlotStyle 	-> stdmodelcolor}]

							],
						     	{scen, Length[respop[[1]]]}]
						]
					]
				],
			"Subsection",
			ImageSize -> Min[{nscen imagesize, 900}]
			
			]
		},
	{g, ng}]
	};


(*-------------------------------------------------
	PLOTS (DISCRETE) RISK FACTOR CLASS PREVALENCE NUMBERS AND RATES
---------------------------------------------------*)

printbug["2.1"];

(* WRITE TO SCREEN *)

plotrisk[resrisk_, naam_, diffscen_]:= Block[{},
	headingprint1[naam];
	Table[	headingprint2[scenname[scen, diffscen]],
		Table[	
			headingprint3["gender " <> gennames[[g]]];
			If[(agespecres == 1),

				(*TOTAL NUMBERS, SPECIFICATION BY RISK FACTOR CLASSES STACKED *)

				Show[GraphicsArray[Table[
					resrisk1 = Table[Plus@@resrisk[[scen, r, g, Range[ri]]], {ri, ncrsel[[r]]}];
					fresrisk[x_] := Table[resrisk1[[ri, Round[x]]], {ri, ncrsel[[r]]}];
					FilledPlot[fresrisk[x], {x, nstap},
						plotset3,
						PlotLabel 	-> risknames[[riskindd]][[r]],
						Fills 		-> stdriskcolor[r]],						
						{r, nrd}]],
					ImageSize 	-> Min[{nrd imagesize, 900}],
					DisplayFunction :> $DisplayFunction, 
					Background 	-> RGBColor[1, 1, 1]
					],

				Table[Show[GraphicsArray[Table[
					Switch[agespecres,
						0,

						(* TOTAL NUMBERS FOR EACH RISK FACTOR CLASS *)

						ListPlot[resrisk[[scen, r, g, ri]],
							plotset1,
							PlotLabel 	-> stdlabel[r, ri]],
						2,

						(* NUMBERS BY AGE CLASS FOR EACH RISK FACTOR CLASS, NOT STACKED *)

			     			makemultiplelistplot[Table[resrisk[[scen, r, g, ri, Range[nstap], ai]], {ai, nac[[7]]}],
							{plotset2,
              						PlotStyle 	-> stdagecolor1,
							PlotLegend 	-> stdagelegend,
							PlotLabel 	-> stdlabel[r, ri]}],
						3,

						(* NUMBERS BY AGE CLASS FOR EACH RISK FACTOR CLASS, STACKED *)

						resrisk1 = Table[Plus@@resrisk[[scen, r, g, ri, n, Range[ai]]],
								{ai, nac[[7]]}, {n, nstap}];
						fresrisk[x_] := Table[resrisk1[[ai, Round[x]]], {ai, nac[[7]]}];
						FilledPlot[fresrisk[x], {x, nstap},
							plotset3,
							PlotLabel 	-> stdlabel[r, ri],
							Fills 		-> stdagecolor2]
						],
						{ri, ncrsel[[r]]}]],
					ImageSize 	-> Min[{ncrsel[[r]] imagesize, 900}],
					DisplayFunction :> $DisplayFunction, 
					Background 	-> RGBColor[1, 1, 1]
					],
					{r, nrd}]
				],
		{g, ng}], 
	{scen, Length[resrisk]}]
	];

(* WRITE TO NOTEBOOK *)

plotrisknb[resrisk_, naam_, diffscen_]:= 
	{
	headingprint2nb[naam <> If[(diffscen == 0), basescenstr, compscenstr]],
	Table[	{
		headingprint3nb[scenname[scen, diffscen] <> " gender " <> gennames[[g]]],
		Switch[agespecres,
			0,

			(* TOTAL NUMBERS FOR EACH RISK FACTOR CLASS *)

			Table[	Cell[	GraphicsData[
						"PostScript",
						DisplayString[
							GraphicsArray[
								Table[	ListPlot[resrisk[[scen, r, g, rj]],
										plotset1,
										PlotLabel 	-> stdlabel[r, rj], 
										AxesLabel 	-> stdaxislabel],
									{rj, 4 ri + 1, Min[{4 ri + 4, ncrsel[[r]]}]}]
								]
							]
						],
					"Subsection",
					ImageSize -> Min[{(Min[{4 ri + 4, ncrsel[[r]]}] - 4 ri) imagesize, 900}]
					],
				{r, nrd}, {ri, 0, Floor[(ncrsel[[r]] - 1) / 4]}],

			1,

			(*TOTAL NUMBERS, SPECIFICATION BY RISK FACTOR CLASSES STACKED *)

			Cell[	GraphicsData[
						"PostScript",
						DisplayString[
							GraphicsArray[
								Table[	resrisk1 = Table[Plus@@resrisk[[scen, r, g, Range[ri], n]],
											{ri, ncrsel[[r]]}, {n, nstap}];
									fresrisk[x_] := Table[resrisk1[[ri, Round[x]]],
											{ri, ncrsel[[r]]}];
									FilledPlot[fresrisk[x], {x, nstap},
										plotset3,
										PlotLabel 	-> risknames[[riskindd]][[r]],
										Fills 		-> stdriskcolor[r]],
									{r, nrd}]
								]
							]
						],
					"Subsection",
					ImageSize -> Min[{nrd imagesize, 900}]
				],

			2,

			(* NUMBERS BY AGE CLASS FOR EACH RISK FACTOR CLASS, NOT STACKED *)

			Table[	Cell[	GraphicsData[
						"PostScript",
						DisplayString[
							GraphicsArray[
								Table[	makemultiplelistplot[Table[resrisk[[scen, r, g, ri, Range[nstap], ai]],
												{ai, nac[[7]]}], 
										{plotset2,
			              						PlotStyle 	-> stdagecolor1,
										PlotLegend 	-> stdagelegend,
										PlotLabel 	-> stdlabel[r, ri]}],
									{ri, ncrsel[[r]]}]
								]
							]
						],
					"Subsection",
					ImageSize -> Min[{ncrsel[[r]] imagesize, 900}]
					],
				{r, nrd}],

			3,

			(* NUMBERS BY AGE CLASS FOR EACH RISK FACTOR CLASS, STACKED *)

			Table[	Cell[	GraphicsData[
						"PostScript",
						DisplayString[
							GraphicsArray[
								Table[	resrisk1 = Table[Plus@@resrisk[[scen, r, g, rj, n, Range[ai]]],
												{ai, nac[[7]]}, {n, nstap}];
									fresrisk[x_] := Table[resrisk1[[ai, Round[x]]],
												{ai, nac[[7]]}];
									FilledPlot[fresrisk[x], {x, nstap},
											plotset3,
											PlotLabel 	-> stdlabel[r, rj],
											Fills 		-> stdagecolor2],												{rj, 4 ri + 1, Min[{4 ri + 4, ncrsel[[r]]}]}]
								]
							]
						],
					"Subsection",
					ImageSize -> Min[{(Min[{4 ri + 4, ncrsel[[r]]}] - 4 ri) imagesize, 900}]
					],
				{r, nrd}, {ri, 0, Floor[(ncrsel[[r]] - 1) / 4]}]
			]
		},
	{scen, Length[resrisk]}, {g, ng}]
	};

(* WRITE TO NOTEBOOK, RESULTS FOR SEVERAL CZM MODEL VERSIONS SIMULTANEOUSLY *)

plotrisknb1[resrisk_, naam_, diffscen_]:= 
	{
	headingprint2nb[naam <> If[(diffscen == 0), basescenstr, compscenstr]],
	Table[	{
		headingprint3nb[scenname[scen, diffscen] <> " gender " <> gennames[[g]]],
		Switch[agespecres,
			0,

			(* TOTAL NUMBERS FOR EACH RISK FACTOR CLASS *)

			Table[	Cell[	GraphicsData[
						"PostScript",
						DisplayString[
							GraphicsArray[
								Table[	makemultiplelistplot[Table[resrisk[[m, scen, r, g, rj]],
												{m, Length[resrisk]}],
										{plotset2,
										PlotLegend 	-> stdmodellegend,
										PlotLabel 	-> stdlabel[r, rj], 
										PlotStyle 	-> stdmodelcolor}],
									{rj, 4 ri + 1, Min[{4 ri + 4, ncrsel[[r]]}]}]
								]
							]
						],
					"Subsection",
					ImageSize -> Min[{(Min[{4 ri + 4, ncrsel[[r]]}] - 4 ri) imagesize, 900}]
					],
				{r, nrd}, {ri, 0, Floor[(ncrsel[[r]] - 1) / 4]}],

			1,

			_,

			2,

			_,

			3,

			_]
		},
	{scen, Length[resrisk[[1]]]}, {g, ng}]
	};


(*-------------------------------------------------
	PLOTS (CONTINUOUS) RISK FACTOR DISTRIBUTION CHARACTERISTICS
---------------------------------------------------*)

printbug["2.2"];

(* WRITE TO NOTEBOOK *)

plotdistnb[resdist_, naam_, diffscen_]:= 
	{
	headingprint2nb[naam <> If[(diffscen == 0), basescenstr, compscenstr]],
	Table[	{
		headingprint3nb[scenname[scen, diffscen] <> " gender " <> gennames[[g]]],
		If[(agespecres <= 1),
			
			(* TOTAL NUMBERS FOR EACH CHARACTERISTIC *)

			Table[	Cell[	GraphicsData[
						"PostScript",
						DisplayString[
							GraphicsArray[
								Table[	ListPlot[resdist[[scen, r, g, ri]],
										plotset1,
										PlotLabel 	-> stdlabelc[r, ri]],
									{ri, 2}]
								]
							]
						],
					"Subsection",
					ImageSize -> Min[{2 imagesize, 900}]
					],
				{r, nrc}],

			(* NUMBERS BY AGE CLASS FOR EACH RISK FACTOR CLASS, NOT STACKED *)

			Table[	Cell[	GraphicsData[
						"PostScript",
						DisplayString[
							GraphicsArray[
								Table[makemultiplelistplot[Table[resdist[[scen, r, g, ri, Range[nstap], ai]],
												{ai, nac[[7]]}], 
										{plotset2,
			              						PlotStyle 	-> stdagecolor1,
										PlotLegend 	-> stdagelegend,
										PlotLabel 	-> stdlabelc[r, ri]}],
									{ri, 2}]
								]
							]
						],
					"Subsection",
					ImageSize -> Min[{2 imagesize, 900}]
					],
				{r, nrc}]

			]
		},
	{scen, Length[resdist]}, {g, ng}]
	};

(*-------------------------------------------------
	PLOTS DISEASE PREVALENCE NUMBERS AND RATES
---------------------------------------------------*)

printbug["2.3"];

(* WRITE TO SCREEN *)

plotdis[resdis_, naam_, diffscen_]:= Block[{},
	nd1 = Length[resdis[[1]]];
	headingprint1[naam];
	Table[
		headingprint2[scenname[scen, diffscen]];
		Table[
			headingprint3["gender " <> gennames[[g]]];
			Table[Show[
    				GraphicsArray[Table[
					If[(agespecres <= 1),

						(* NUMBERS AGGREGATED OVER AGE *)

						ListPlot[resdis[[scen, dj, g]],
							plotset1,
							PlotLabel 	-> mortnames[[dj]]],

					If[(agespecres == 2),

						(* NUMBERS BY AGE CLASS, NOT STACKED *)

						makemultiplelistplot[Table[resdis[[scen, dj, g, Range[nstap], ai]], {ai, nac[[7]]}],
							{plotset2,
							PlotStyle 	-> stdagecolor1,
				 			PlotLegend 	-> stdagelegend,
							PlotLabel 	-> mortnames[[dj]]}],

					If[(agespecres == 3),

						(* NUMBERS BY AGE CLASS, STACKED *)

						resdis1 = Table[Plus@@resdis[[scen, dj, g, n, Range[ai]]], {ai, nac[[7]]}, {n, nstap}];
						fresdis[x_] := Table[resdis1[[ai, Round[x]]], {ai, nac[[7]]}];
						FilledPlot[fresdis[x], {x, nstap},
							plotset3,
							PlotLabel 	-> mortnames[[dj]],
							Fills 		-> stdagecolor2]
					]]],
		     			{dj, 4 di + 1, Min[{4 di + 4, nd1}]}]],
				ImageSize 	-> (Min[{4 di + 4, nd1}] - 4 di) imagesize, 
				DisplayFunction :> $DisplayFunction,
				Background 	-> RGBColor[1, 1, 1]
				], 	
			{di, 0, Floor[(nd1 - 1) / 4]}],
		{g, ng}], 
	{scen, Length[resdis]}]
	];

(* WRITE TO NOTEBOOK *)

plotdisnb[resdis_, naam_, diffscen_]:= Block[{},
	nd1 = Length[resdis[[1]]];
	{
	headingprint2nb[naam <> If[(diffscen == 0), basescenstr, compscenstr]],
	Table[	{
		headingprint3nb[scenname[scen, diffscen] <> " gender " <> gennames[[g]]],
		Table[	Cell[	GraphicsData[
					"PostScript",
					DisplayString[
   						GraphicsArray[
							Table[
								If[(agespecres <= 1),

									(* NUMBERS AGGREGATED OVER AGE *)

									ListPlot[resdis[[scen, dj, g]],
										plotset1,
										PlotLabel 	-> mortnames[[dj]]],

								If[(agespecres == 2),

									(* NUMBERS BY AGE CLASS, NOT STACKED *)

									makemultiplelistplot[Table[resdis[[scen, dj, g, Range[nstap], ai]],
												{ai, nac[[7]]}],
										{plotset2,
										PlotStyle 	-> stdagecolor1,
		 								PlotLegend 	-> stdagelegend,
										PlotLabel 	-> mortnames[[dj]]}],

								If[(agespecres == 3),

									(* NUMBERS BY AGE CLASS, STACKED *)

									resdis1 = Table[Plus@@resdis[[scen, dj, g, n, Range[ai]]], 
												{ai, nac[[7]]}, {n, nstap}];
									fresdis[x_] := Table[resdis1[[ai, Round[x]]], {ai, nac[[7]]}];
									FilledPlot[fresdis[x], {x, nstap},
										plotset3,
										PlotLabel 	-> mortnames[[dj]],
										Fills 		-> stdagecolor2]
								]]],
     								{dj, 4 di + 1, Min[{4 di + 4, nd1}]}]
								]
							]
						],
				"Subsection",
				ImageSize -> Min[{(Min[{4 di + 4, nd1}] - 4 di) imagesize, 900}]
				],
			{di, 0, Floor[(nd1 - 1) / 4]}]
			},
		{scen, Length[resdis]}, {g, ng}] 
	}
	];

(* WRITE TO NOTEBOOK, RESULTS FOR SEVERAL CZM MODEL VERSIONS SIMULTANEOUSLY *)

plotdisnb1[resdis_, naam_, diffscen_]:= Block[{},
	nd1 = Length[resdis[[1, 1]]];
	{
	headingprint2nb[naam <> If[(diffscen == 0), basescenstr, compscenstr]],
	Table[	{
		headingprint3nb[scenname[scen, diffscen] <> " gender " <> gennames[[g]]],
		Table[	Cell[	GraphicsData[
					"PostScript",
					DisplayString[
   						GraphicsArray[
							Table[
								If[(agespecres <= 1),

									(* NUMBERS AGGREGATED OVER AGE *)

									makemultiplelistplot[Table[resdis[[m, scen, dj, g]],
												{m, Length[resdis]}],
										{plotset2,
										PlotStyle 	-> stdmodelcolor,
		 								PlotLegend 	-> stdmodellegend,
										PlotLabel 	-> mortnames[[dj]]}]

								],
     								{dj, 4 di + 1, Min[{4 di + 4, nd1}]}]
								]
							]
						],
				"Subsection",
				ImageSize -> Min[{(Min[{4 di + 4, nd1}] - 4 di) imagesize, 900}]
				],
			{di, 0, Floor[(nd1 - 1) / 4]}]
			},
		{scen, Length[resdis[[1]]]}, {g, ng}] 
	}
	];


(* --------------------------------------------------
	PRINTING ROUTINES FOR EVENT NUMBERS FOR EACH MODEL TYPE: SUB-ROUTINES
----------------------------------------------------*)

printbug["3."];

(* PRINTS NAME OF OUTPUT VARIABLE AND SCENARIO SPECIFICATIONS *)

resname[t_, diffscen_] 	:= Block[{},

printbug["3.1"];

	str = outputnames[[t]] <> If[(diffscen == 0), basescenstr, compscenstr];

	If[(outputscreen == 1),		headingprint2[str]];
	If[(outputnotebook == 1),	addcellnb[headingprint2nb[str]]];
	If[(outputfile == 1),		WriteString[resmodelfile, str <> "\n\n"]]

	];


(* PRINTS TOTAL POPULATION NUMBERS *)

printrespop[respop_, diffscen_] := Block[{},

printbug["3.2"];
	
	Do[	str = scenname[scen, diffscen];

		If[(outputscreen == 1),		headingprint3[str]];				
		If[(outputnotebook == 1),	addcellnb[headingprint3nb[str]]];	
		If[(outputfile == 1),		WriteString[resmodelfile, str <> "\n\n"]];		

printbug["3.2.1"];

		If[(agespecres <= 1),

printbug["3.2.2"];

			(* AGGREGATED OVER AGE *)

			If[(outputscreen == 1),		Print[TableForm[respop[[scen]]]]];
			If[(outputnotebook == 1),	addcellnb[Cell[BoxData[ToBoxes[respop[[scen]] // MatrixForm]],
								"Subsection"]]];
			If[(outputfile == 1),		Export[resmodelfile, respop[[scen]], "Table"];			
							WriteString[resmodelfile, "\n\n"]],

printbug["3.2.3"];

			(* SPECIFIED BY AGE *)

			If[(outputscreen == 1),		Do[	Print[gennames[[g]]];
								Print[TableForm[Transpose[respop[[scen, g]]]]],
								{g, ng}]];

			If[(outputnotebook == 1),	addcellnb[Table[
								{headingprint3nb[gennames[[g]]],			
								Cell[BoxData[ToBoxes[Transpose[respop[[scen, g]]] // MatrixForm]],
									"Subsection"]},
								{g, ng}]]];
				
			If[(outputfile == 1),		Do[	WriteString[resmodelfile, gennames[[g]] <> "\n\n"];	
								Export[resmodelfile, Transpose[respop[[scen, g]]], "Table"];
								WriteString[resmodelfile, "\n\n"],
								{g, ng}]];
			],

		{scen, Length[respop]}]

	];


(* PRINTS (DISCRETE) RISK FACTOR CLASS PREVALENCE NUMBERS *)

printresrisk[resrisk_, diffscen_] := Block[{},

printbug["3.3"];

	Do[	str = scenname[scen, diffscen] <> risknames[[riskindd[[r]]]] <> " "<> gennames[[g]];

		If[(outputscreen == 1),		headingprint3[str]];
		If[(outputnotebook == 1),	addcellnb[headingprint3nb[str]]];					
		If[(outputfile == 1),		WriteString[resmodelfile, str <> "\n\n"]];				

		If[(agespecres <= 1),

			(* AGGREGATED OVER AGE *)

			If[(outputscreen == 1),		Print[TableForm[resrisk[[scen, r, g]]]]];
			If[(outputnotebook == 1),	addcellnb[Cell[BoxData[ToBoxes[resrisk[[scen, r, g]] // MatrixForm]],
								"Subsection"]]];
			If[(outputfile == 1),		Export[resmodelfile, resrisk[[scen, r, g]], "Table"];		
							WriteString[resmodelfile, "\n\n"]],
			
			(* SPECIFIED BY AGE *)

			If[(outputscreen == 1),		Do[	Print[ToString[ri]];				
								Print[TableForm[Transpose[resrisk[[scen, r, g, ri]]]]],
								{ri, ncrsel[[r]]}]];
	
			If[(outputnotebook == 1),	addcellnb[Table[
								{headingprint3nb[ToString[ri]],				
								Cell[BoxData[ToBoxes[Transpose[resrisk[[scen, r, g, ri]]] // MatrixForm]],
									"Subsection"]},
								{ri, ncrsel[[r]]}]]];

			If[(outputfile == 1),		Do[	WriteString[resmodelfile, ToString[ri] <> "\n\n"];	
								Export[resmodelfile, Transpose[resrisk[[scen, r, g, ri]]], "Table"];
								WriteString[resmodelfile, "\n\n"],
								{ri, ncrsel[[r]]}]];

			],

		{scen, Length[resrisk]}, {r, nrd}, {g, ng}]

	];

(* PRINTS (CONTINUOUS) RISK FACTOR CLASS DISTRIBUTION CHARACTERISTICS *)

printresdist[resdist_, diffscen_] := Block[{},

printbug["3.4"];

	Do[	str = scenname[scen, diffscen] <> risknames[[riskindc[[r]]]] <> " "<> gennames[[g]];

		If[(outputscreen == 1),		headingprint3[str]];
		If[(outputnotebook == 1),	addcellnb[headingprint3nb[str]]];					
		If[(outputfile == 1),		WriteString[resmodelfile, str <> "\n\n"]];				

		If[(agespecres <= 1),

			(* AGGREGATED OVER AGE *)

			If[(outputscreen == 1),		Print[TableForm[resdist[[scen, r, g]]]]];
			If[(outputnotebook == 1),	addcellnb[Cell[BoxData[ToBoxes[resdist[[scen, r, g]] // MatrixForm]],
								"Subsection"]]];
			If[(outputfile == 1),		Export[resmodelfile, resdist[[scen, r, g]], "Table"];		
							WriteString[resmodelfile, "\n\n"]],
			
			(* SPECIFIED BY AGE *)

			If[(outputscreen == 1),		Do[	Print[ToString[ri]];				
								Print[TableForm[Transpose[resdist[[scen, r, g, ri]]]]],
								{ri, 2}]];
	
			If[(outputnotebook == 1),	addcellnb[Table[
								{headingprint3nb[ToString[ri]],				
								Cell[BoxData[ToBoxes[Transpose[resdist[[scen, r, g, ri]]] // MatrixForm]],
									"Subsection"]},
								{ri, 2}]]];

			If[(outputfile == 1),		Do[	WriteString[resmodelfile, ToString[ri] <> "\n\n"];	
								Export[resmodelfile, Transpose[resdist[[scen, r, g, ri]]], "Table"];
								WriteString[resmodelfile, "\n\n"],
								{ri, 2}]];

			],

		{scen, Length[resdist]}, {r, nrc}, {g, ng}]

	];


(* PRINTS DISEASE (PREVALENCE, INCIDENCE, MORTALITY) NUMBERS *)

printresdis[resdis_, diffscen_] := Block[{},

printbug["3.5"];

	Do[	str = scenname[scen, diffscen] <> mortnames[[d]];

		If[(outputscreen == 1),		headingprint3[str]];
		If[(outputnotebook == 1),	addcellnb[headingprint3nb[str]]];					
		If[(outputfile == 1),		WriteString[resmodelfile, str <> "\n\n"]];								
		If[(agespecres <= 1),

			(* AGGREGATED OVER AGE *)

			If[(outputscreen == 1),		Print[TableForm[resdis[[scen, d]]]]];
			If[(outputnotebook == 1),	addcellnb[Cell[BoxData[ToBoxes[resdis[[scen, d]] // MatrixForm]],
								"Subsection"]]];
			If[(outputfile == 1),		Export[resmodelfile, resdis[[scen, d]], "Table"];		
							WriteString[resmodelfile, "\n\n"]],

			(* SPECIFIED BY AGE *)

			If[(outputscreen == 1),		Do[	Print[gennames[[g]]];				
								Print[TableForm[Transpose[resdis[[scen, d, g]]]]],
								{g, ng}]];

			If[(outputnotebook == 1),	addcellnb[Table[
								{headingprint3nb[gennames[[g]]],			
								Cell[BoxData[ToBoxes[Transpose[resdis[[scen, d, g]]] // MatrixForm]],
									"Subsection"]},
								{g, ng}]]];
			
			If[(outputfile == 1),		Do[	WriteString[resmodelfile, gennames[[g]] <> "\n\n"];	
								Export[resmodelfile, Transpose[resdis[[scen, d, g]]], "Table"];
								WriteString[resmodelfile, "\n\n"],
								{g, ng}]];

			],

		{scen, Length[resdis]}, {d, Length[resdis[[1]]]}]

	];
		
(* --------------------------------------------------
	PRINTING ROUTINES FOR EVENT NUMBERS FOR COMPARING MODEL TYPES: SUB-ROUTINES
----------------------------------------------------*)

(* PRINTS NAME OF OUTPUT VARIABLE AND SCENARIO SPECIFICATIONS *)

resnameabs[t_, diffscen_] := Block[{},

printbug["3.6"];

	str = outputnames[[t]] <> If[(diffscen == 0), basescenstr, compscenstr] <> " (absolute)";

	If[(outputscreen == 1),		headingprint2[str]];
	If[(outputnotebook == 1),	addcellnb[headingprint2nb[str]]];						
	If[(outputfile == 1),		WriteString[resmodelfile, str <> "\n\n"]]					

	];


resnamerel[t_, diffscen_] := Block[{},

printbug["3.7"];

	str = outputnames[[t]] <> If[(diffscen == 0), basescenstr, compscenstr] <> " (relative)";

	If[(outputscreen == 1),		headingprint2[str]];								
	If[(outputnotebook == 1),	addcellnb[headingprint2nb[str]]];						
	If[(outputfile == 1),		WriteString[resmodelfile, str <> "\n\n"]]					

	];

(* PRINTS RESULTS: DIFFERENCES BETWEEN TOTAL POPULATION NUMBERS *)

printdrespop[drespop_, respop_, diffscen_, respopind_] := Block[{},

printbug["3.8"];

	hprint[hres_, hdiffscen_] := Block[{},

		Do[	If[(outputscreen == 1),		headingprint3[scenname[scen, hdiffscen]]];				
			If[(outputnotebook == 1),	addcellnb[headingprint3nb[scenname[scen, hdiffscen]]]];			
			If[(outputfile == 1),		WriteString[resmodelfile, scenname[scen, hdiffscen] <> "\n\n"]];		

			If[(agespecres <= 1), 

				(* NO AGE SPECIFICATION *)

				If[(outputscreen == 1),		Print[TableForm[hres[[scen]]]]];				
				If[(outputnotebook == 1),	addcellnb[Cell[BoxData[ToBoxes[hres[[scen]] // MatrixForm]],
									"Subsection"]]];

				If[(outputfile == 1),		Export[resmodelfile, hres[[scen]], "Table"];			
								WriteString[resmodelfile, "\n\n"]],

				(* SPECIFIED BY AGE CLASS *)
			
				If[(outputscreen == 1),		Do[	Print[gennames[[g]]];				
									Print[TableForm[Transpose[hres[[scen, g]]]]],
									{g, ng}]];

				If[(outputnotebook == 1),	addcellnb[Table[
									{headingprint3nb[gennames[[g]]],				
									Cell[BoxData[ToBoxes[Transpose[hres[[scen, g]]] // MatrixForm]],
										"Subsection"]},
									{g, ng}]]];

				If[(outputfile == 1),		Do[	WriteString[resmodelfile, gennames[[g]] <> "\n\n"];	
									Export[resmodelfile, Transpose[hres[[scen, g]]], "Table"];
									WriteString[resmodelfile, "\n\n"],
									{g, ng}]]

				],

			{scen, Length[hres]}]

		];

	(* ABSOLUTE DIFFERENCES *)

	resnameabs[respopind, diffscen];		

	hprint[drespop, diffscen];

	(* RELATIVE DIFFERENCES *)

	resnamerel[respopind, diffscen];

	hprint[drespop respop /	(respop^2 + eps), diffscen]

	];

(* PRINTS RESULTS: DIFFERENCES BETWEEN (DISCRETE) RISK FACTOR CLASS PREVALENCE NUMBERS *)

printdresrisk[dresrisk_, resrisk_, diffscen_, resriskind_, absind_] := Block[{},

printbug["3.9"];

	hprint[hres_, hdiffscen_] := Block[{},

		Do[	str = scenname[scen, hdiffscen] <> risknames[[riskindd[[r]]]] <> " " <> gennames[[g]];

			If[(outputscreen == 1),		headingprint3[str]];							
			If[(outputnotebook == 1),	addcellnb[headingprint3nb[str]]];					
			If[(outputfile == 1),		WriteString[resmodelfile, str <> "\n\n"]];				

			If[(agespecres <= 1),

				(* NO AGE SPECIFICATION *)

				If[(outputscreen == 1),		Print[TableForm[hres[[scen, r, g]]]]];			
				If[(outputnotebook == 1),	addcellnb[Cell[BoxData[ToBoxes[hres[[scen, r, g]] // MatrixForm]],
									"Subsection"]]];	
				If[(outputfile == 1),		Export[resmodelfile, hres[[scen, r, g]], "Table"];			
								WriteString[resmodelfile, "\n\n"]],

				(* SPECIFIED BY AGE CLASS *)

				If[(outputscreen == 1),		Do[	Print[ToString[ri]];				
									Print[TableForm[Transpose[hres[[scen, r, g, ri]]]]],
									{ri, ncrsel[[r]]}]];

				If[(outputnotebook == 1),	addcellnb[Table[
									{headingprint3nb[ToString[ri]],				
									Cell[BoxData[ToBoxes[Transpose[hres[[scen, r, g, ri]]] //
										MatrixForm]], "Subsection"]},
									{ri, ncrsel[[r]]}]]];

				If[(outputfile == 1),		Do[	WriteString[resmodelfile, ToString[ri] <> "\n\n"];	
									Export[resmodelfile, Transpose[hres[[scen, r, g, ri]]], "Table"];
									WriteString[resmodelfile, "\n\n"],
									{ri, ncrsel[[r]]}]]

				],

			{scen, Length[hres]}, {r, nrd}, {g, ng}]

		];


	(* ABSOLUTE DIFFERENCES *)

	resnameabs[resriskind];

	hprint[dresrisk, diffscen];
		
	(* RELATIVE DIFFERENCES IF ALLOWED (absind == 1) *)

	hprint[dresrisk resrisk / (resrisk^2 + eps)]
	
	];

(* PRINTS RESULTS: DIFFERENCES BETWEEN (CONTINUOUS) RISK FACTOR CLASS DISTRIBUTION CHARACTERISTICS *)

printdresdist[dresdist_, resdist_, diffscen_, resdistind_, absind_] := Block[{},

printbug["3.11"];

	hprint[hres_, hdiffscen_] := Block[{},

		Do[	str = scenname[scen, hdiffscen] <> risknames[[riskindc[[r]]]] <> " " <> gennames[[g]];

			If[(outputscreen == 1),		headingprint3[str]];							
			If[(outputnotebook == 1),	addcellnb[headingprint3nb[str]]];					
			If[(outputfile == 1),		WriteString[resmodelfile, str <> "\n\n"]];				

			If[(agespecres <= 1),

				(* NO AGE SPECIFICATION *)

				If[(outputscreen == 1),		Print[TableForm[hres[[scen, r, g]]]]];			
				If[(outputnotebook == 1),	addcellnb[Cell[BoxData[ToBoxes[hres[[scen, r, g]] // MatrixForm]],
									"Subsection"]]];	
				If[(outputfile == 1),		Export[resmodelfile, hres[[scen, r, g]], "Table"];			
								WriteString[resmodelfile, "\n\n"]],

				(* SPECIFIED BY AGE CLASS *)

				If[(outputscreen == 1),		Do[	Print[ToString[ri]];				
									Print[TableForm[Transpose[hres[[scen, r, g, ri]]]]],
									{ri, 2}]];

				If[(outputnotebook == 1),	addcellnb[Table[
									{headingprint3nb[ToString[ri]],				
									Cell[BoxData[ToBoxes[Transpose[hres[[scen, r, g, ri]]] //
										MatrixForm]], "Subsection"]},
									{ri, 2}]]];

				If[(outputfile == 1),		Do[	WriteString[resmodelfile, ToString[ri] <> "\n\n"];	
									Export[resmodelfile, Transpose[hres[[scen, r, g, ri]]], "Table"];
									WriteString[resmodelfile, "\n\n"],
									{ri, 2}]]

				],

			{scen, Length[hres]}, {r, nrc}, {g, ng}]

		];

	(* ABSOLUTE DIFFERENCES *)

	resnameabs[resdistind];

	hprint[dresdist, diffscen];

	(* RELATIVE DIFFERENCES IF ALLOWED (absind == 1) *)

	hprint[dresdist resdist / (resdist^2 + eps), diffscen];

	];

(* PRINTS RESULTS: DIFFERENCES BETWEEN DISEASE (PREVALENCE, INCIDENCE, MORTALITY) NUMBERS *)

printdresdis[dresdis_, resdis_, diffscen_, resdisind_, absind_]:= Block[{},

printbug["3.12"];

	hprint[hres_, hdiffscen_] := Block[{},

		Do[	str = scenname[scen, hdiffscen] <> mortnames[[d]];

			If[(outputscreen == 1),		headingprint3[str]];							
			If[(outputnotebook == 1),	addcellnb[headingprint3nb[str]]];					
			If[(outputfile == 1),		WriteString[resmodelfile, str <> "\n\n"]];				

			If[(agespecres <= 1),

				(* NO AGE SPECIFICATION *)

				If[(outputscreen == 1),		Print[TableForm[hres[[scen, d]]]]];			
				If[(outputnotebook == 1),	addcellnb[Cell[BoxesData[ToBoxes[hres[[scen, d]] // MatrixForm]],
									"Subsection"]]];

				If[(outputfile == 1),		Export[resmodelfile, hres[[scen, d]], "Table"];		
								WriteString[resmodelfile, "\n\n"]],

				(* SPECIFIED BY AGE CLASS *)

				If[(outputscreen == 1),		Do[	Print[gennames[[g]]];				
									Print[TableForm[Transpose[hres[[scen, d, g]]]]],
									{g, ng}]];

				If[(outputnotebook == 1),	addcellnb[Table[
									{headingprint3nb[gennames[[g]]],														Cell[BoxesData[ToBoxes[Transpose[hres[[scen, d, g]]] //
										MatrixForm]], "Subsection"]},
									{g, ng}]]];

				If[(outputfile == 1),		Do[	WriteString[resmodelfile, gennames[[g]] <> "\n\n"];	
									Export[resmodelfile, Transpose[hres[[scen, d, g]]], "Table"];
									WriteString[resmodelfile, "\n\n"],
									{g, ng}]];

				],

			{scen, Length[hres]}, {d, Length[hres[[1]]]}]

		];

	(* ABSOLUTE DIFFERENCES *)

	resnameabs[resdisind];

	hprint[dresdis, diffscen];

	(* RELATIVE DIFFERENCES IF ALLOWED (absind == 1) *)

	hprint[dresdis resdis / (resdis^2 + eps), diffscen]
	
	];


(*-------------------------------------------------
	PRESENTING ROUTINE FOR EVENT NUMBERS FOR EACH MODEL TYPE 
---------------------------------------------------*)

printbug["4."];

presentnumbers[resmodel_, diffscen_] := Block[{},

	(* TOTAL POPULATION NUMBERS *)

printbug["4.1"];

	hnpop0		= makenpopagg[resmodel[[1]], 0];
	standardnpop	= Table[Plus@@hnpop0[[scen, g, 1]], {scen, nscen0}, {g, ng}];

	(* DISCRETELY DISTRIBUTED RISK FACTOR CLASS PREVALENCE NUMBERS *)

printbug["4.2"];

	hnrisk0		= makenrisk[resmodel[[2]], 0];
	hprisk0		= Table[hnrisk0[[scen, r, g, ri]] / (Plus@@hnrisk0[[scen, r, g]] + eps),
				{scen, nscen0}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];
	standardnrisk	= Table[Plus@@hnrisk0[[scen, r, g, ri, 1]], {scen, nscen0}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];

	(* DISEASE PREVALENCE NUMBERS *)

printbug["4.3"];

	hndis0		= makendis[resmodel[[4]], 0];
	
	(* TOTAL POPULATION NUMBERS *)

	If[(outputsel[[1]] == 1),

printbug["4.4"];

		npop	= makenpop[resmodel, diffscen];
		npop	= npopdiscount[npop, diffscen, discounte, standardnpop];

		(* GRAPHICAL OUTPUT *)

		If[(graphicoutput == 1),

printbug["4.4.1"];

			If[(outputscreen == 1), plotpop[npop, outputnames[[1]], diffscen]];

			If[(outputnotebook == 1),
				addcellnb[plotpopnb[npop, outputnames[[1]], diffscen]];
				If[(cumulative == 1),
					hnpop = If[(agespecres <= 1),
							Table[npop[[scen, g, nstap]], {scen, Length[npop]}, {g, ng}],
							Table[Plus@@npop[[scen, g, nstap]], {scen, Length[npop]}, {g, ng}]
							];
					addcellnb[Cell[BoxData[ToBoxes[hnpop]], "Subsection"]]
					]
				]
			];

		(* TABEL OUTPUT *)

		If[(tabeloutput == 1), 

printbug["4.4.2"];

			If[(outputfile == 1), WriteString[resmodelfile, outputnames[[1]] <> "\n\n"]];
			printrespop[npop, diffscen]]

		];

	(* DISCRETELY DISTRIBUTED RISK FACTOR CLASS PREVALENCE NUMBERS *)

	If[(nrd > 0) && (outputsel[[2]] == 1),

printbug["4.5"];

		nrisk 	= If[(diffscen == 0),
				hnrisk0,
				makenrisk[resmodel[[2]], diffscen]
				];

		If[(rates == 0) && (diffscen == 0), nrisk = nrisk[[{1}]]];
		If[(rates == 1) && (diffscen == 0), nrisk = hprisk0[[{1}]]];
		If[(rates == 1) && (diffscen == 1), nrisk = Drop[hprisk0, 1] - Table[hprisk0[[1]], {nscen0 - 1}]];

		If[(standardized == 1),
			nrisk = Table[nrisk[[scen, r, g, ri, n]] / standardnrisk[[scen, r, g, ri]],
					{scen, Length[nrisk]}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}]];

		If[(discounte > eps),
			nrisk = Table[nrisk[[scen, r, g, ri, n]] / (1 + discounte)^(n - 1),
					{scen, Length[nrisk]}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}]];

		If[(cumulative == 1),
			nrisk = Table[Plus@@nrisk[[scen, r, g, ri, Range[n]]],
					{scen, Length[nrisk]}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}]];

		(* GRAPHICAL OUTPUT *)

		If[(graphicoutput == 1),
	
			If[(outputscreen == 1), plotrisk[nrisk, outputnames[[2]], diffscen]];
			If[(outputnotebook == 1), addcellnb[plotrisknb[nrisk, outputnames[[2]], diffscen]]]];

		(* TABEL OUTPUT *)

		If[(tabeloutput == 1),

			If[(outputfile == 1), WriteString[resmodelfile, outputnames[[2]] <> ratesstr <> "\n\n"]];
			printresrisk[nrisk, diffscen]]		

		];

	(* CONTINUOUSLY DISTRIBUTED RISK FACTOR DISTRIBUTION CHARACTERISTICS *)

	If[(riskcontind == 1) && (nrc > 0) && (outputsel[[3]] == 1),

printbug["4.6"];

		dist	= makedist[resmodel[[3]], 0];

		dist	= Table[dist[[scen, r, g, ri]] / (hnpop0[[scen, g]] + eps), {scen, nscen0}, {r, nrc}, {g, ng}, {ri, 2}];

		If[(diffscen == 0), dist = dist[[{1}]]];

		If[(diffscen == 1), dist = Drop[dist, 1] - Table[dist[[1]], {nscen0 - 1}]];

		(* GRAPHICAL OUTPUT *)

		If[(graphicoutput == 1), If[(outputnotebook == 1), addcellnb[plotdistnb[dist, outputnames[[3]], diffscen]]]];

		(* TABEL OUTPUT *)

		If[(tabeloutput == 1),

			If[(outputfile == 1), WriteString[resmodelfile, outputnames[[3]] <> "\n\n"]];
			printresdist[dist, diffscen]]

		];

	(* DISEASE DATA *)

	hmakendis[outputnr_, ndis0_, pdis0_] := Block[{},

printbug["4.7"];

		ndis = If[(diffscen == 0),
				ndis0,
				makendis[resmodel[[outputnr]], diffscen]
				];

		If[(rates == 0) && (diffscen == 0), ndis = ndis[[{1}]]];
		If[(rates == 1) && (diffscen == 0), ndis = pdis0[[{1}]]];
		If[(rates == 1) && (diffscen == 1), ndis = Drop[pdis0, 1] - Table[pdis0[[1]], {nscen0 - 1}]];

		If[(standardized == 1),
			ndis = Table[ndis[[scen, d, g, n]] / standardnpop[[scen, g]],
					{scen, Length[ndis]}, {d, Length[ndis[[1]]]}, {g, ng}, {n, nstap}]];

		If[(discounte > eps),
			ndis = Table[ndis[[scen, d, g, n]] / (1 + discounte)^(n - 1),
					{scen, Length[ndis]}, {d, Length[ndis[[1]]]}, {g, ng}, {n, nstap}]];

		If[(cumulative == 1),
			ndis = Table[Plus@@ndis[[scen, d, g, Range[n]]],
					{scen, Length[ndis]}, {d, Length[ndis[[1]]]}, {g, ng}, {n, nstap}]];

		(* GRAPHICAL OUTPUT *)

		If[(graphicoutput == 1),

			If[(outputscreen == 1), plotdis[ndis, outputnames[[outputnr]], diffscen]];
			If[(outputnotebook == 1), addcellnb[plotdisnb[ndis, outputnames[[outputnr]], diffscen]]]];

		(* TABEL OUTPUT *)

		If[(tabeloutput == 1),

			If[(outputfile == 1), WriteString[resmodelfile, outputnames[[outputnr]] <> ratesstr <> "\n\n"]];
			printresdis[ndis, diffscen]]		

		];

	(* DISEASE PREVALENCE NUMBERS *)

	If[(nd > 0) && (outputsel[[4]] == 1),

printbug["4.8"];

		hpdis0 = Table[hndis0[[scen, d]] / (hnpop0[[scen]] + eps), {scen, nscen0}, {d, nd}];

		hmakendis[4, hndis0, hpdis0]];
		
	(* DISEASE INCIDENCE NUMBERS *)

	If[(nd > 0) && (outputsel[[5]] == 1),

printbug["4.9"];

		hninc0 = makendis[resmodel[[5]], 0];

		hpinc0 = Table[hninc0[[scen, d]] / (hnpop0[[scen]] + eps), {scen, nscen0}, {d, nd}];

		hmakendis[5, hninc0, hpinc0]];

	(* DISEASE MORTALITY NUMBERS *)

	If[(outputsel[[6]] == 1),

printbug["4.10"];

		hnmort0 = makendis[resmodel[[6]], 0];

		hpmort0 = Table[hnmort0[[scen, d]] / (hnpop0[[scen]] + eps), {scen, nscen0}, {d, nd + 2}];

		hmakendis[6, hnmort0, hpmort0]];
	
	(* MEAN AGE AT DISEASE ONSET *)

	If[(nd > 0) && (outputsel[[7]] == 1),

printbug["4.11"];

		onsetage = makendis[resmodel[[7]], 0] / hndis0;

		onsetage = makeresdiff[onsetage];

		If[(diffscen == 0), onsetage = onsetage[[{1}]]];

		If[(graphicoutput == 1),

			If[(outputscreen == 1), plotdis[onsetage, outputnames[[7]], diffscen]];
			If[(outputnotebook == 1), addcellnb[plotdisnb[onsetage, outputnames[[7]], diffscen]]]];

		If[(tabeloutput == 1),

			If[(outputfile == 1), WriteString[resmodelfile, outputnames[[7]] <> "\n\n"]];
			printresdis[onsetage, diffscen]]	

		];
	
	(* MEAN TIME SINCE SMOKING CESSATION *)

	If[(RRsmokduurind == 1) && (outputsel[[8]] == 1),

printbug["4.12"];

		duurstop = makenpopagg[resmodel[[8]], 0] / (hnrisk0[[Range[nscen0], 1, Range[ng], 3]] + eps);

		duurstop = makeresdiff[duurstop, diffscen];

		If[(diffscen == 0), duurstop = duurstop[[{1}]]];

		(* GRAPHICAL OUTPUT *)
		
		If[(graphicoutput == 1),

			If[(outputscreen == 1), plotpop[duurstop, outputnames[[8]], diffscen]];
			If[(outputnotebook == 1), addcellnb[plotpopnb[duurstop, outputnames[[8]], diffscen]]]];

		(* TABEL OUTPUT *)

		If[(tabeloutput == 1),

			If[(outputfile == 1), WriteString[resmodelfile, outputnames[[8]] <> "\n\n"]];
			printrespop[duurstop, diffscen]];

		];

	(* CURRENT COSTS *)

	If[(outputsel[[9]] == 1),

printbug["4.13"];
		
		ncosts 	= makencosts[resmodel];

		ncosts	= ncostsdiscount[ncosts, discounte, standardnpop];		

		plusncosts = If[(agespecres <= 1),

			(* AGGREGATED OVER AGE *)

			Table[Plus@@ncosts[[scen, d, g, n]], {scen, nscen0}, {d, nd + 2}, {g, ng}, {n, nstap}],

			(* SPECIFIED BY AGE CLASS *)

			Table[aggregc[ncosts[[scen, d, g, n]], 1, 7], {scen, nscen0}, {d, nd + 2}, {g, ng}, {n, nstap}]];
	
		plusncosts = makeresdiff[plusncosts, diffscen];

		If[(diffscen == 0), plusncosts = plusncosts[[{1}]]];

		(* GRAPHICAL OUTPUT *)

		If[(graphicoutput == 1),

			If[(outputscreen == 1), plotdis[plusncosts, outputnames[[9]], diffscen]];
			If[(outputnotebook == 1), addcellnb[plotdisnb[plusncosts, outputnames[[9]], diffscen]]]];

		(* TABEL OUTPUT *)

		If[(tabeloutput == 1),

			If[(outputfile == 1), WriteString[resmodelfile, outputnames[[9]] <> "\n\n"]];
			printresdis[plusncosts, diffscen]];

		];
	
]; (* END PRESENTNUMBERS *)


(*-------------------------------------------------
	PLOTTING ROUTINE FOR EVENT NUMBERS (EXCL LE) FOR COMPARING MODEL TYPES 
---------------------------------------------------*)

printbug["5."];


absname[t_] := "absolute differences for " <> outputnames[[t]];
relname[t_] := "relative differences for " <> outputnames[[t]];


presentdiffnumbers[resmodel_, diffscen_] := Block[{},

	(* TOTAL POPULATION NUMBERS *)

printbug["5.1"];

	npop10	= Table[makenpopagg[resmodel[[m, 1]], 0], {m, Plus@@modelsel}];
	standardnpop1 = Table[Plus@@npop10[[m, scen, g, 1]], {m, Plus@@modelsel}, {scen, nscen0}, {g, ng}];

	(* DISCRETELY DISTRIBUTED RISK FACTOR CLASS PREVALENCE NUMBERS *)

printbug["5.2"];

	nrisk10	= Table[makenrisk[resmodel[[m, 2]], 0], {m, Plus@@modelsel}];
	prisk10	= Table[nrisk10[[m, scen, r, g, ri]] / (Plus@@nrisk10[[m, scen, r, g]] + eps),
				{m, Plus@@modelsel}, {scen, nscen0}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];
	standardnrisk1 = Table[Plus@@nrisk10[[m, scen, r, g, ri, 1]],
				{m, Plus@@modelsel}, {scen, nscen0}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];

	(* DISEASE PREVALENCE NUMBERS *)

printbug["5.3"];

	ndis10	= Table[makendis[resmodel[[m, 4]], 0], {m, Plus@@modelsel}];
	
	(* TOTAL POPULATION NUMBERS *)

	If[(outputsel[[1]] == 1),

printbug["5.4"];

		npop1	= Table[makenpop[resmodel[[m]], diffscen], {m, Plus@@modelsel}];

printbug["5.4.1"];
		
		npop1	= Table[npopdiscount[npop1[[m]], diffscen, discounte, standardnpop1[[m]]], {m, Plus@@modelsel}];

printbug["5.4.2"];
		
		(* GRAPHICAL OUTPUT *)

		If[(graphicoutput == 1),

			If[(outputnotebook == 1), addcellnb[plotpopnb1[npop1, outputnames[[1]], diffscen]]]]

		];

	(* DISCRETELY DISTRIBUTED RISK FACTOR CLASS PREVALENCE NUMBERS *)

	If[(nrd > 0) && (outputsel[[2]] == 1),

printbug["5.5"];

		nrisk1 	= If[(diffscen == 0), nrisk10, Table[makenrisk[resmodel[[m, 2]], diffscen], {m, Plus@@modelsel}]];

printbug["5.5.1"];
		
		If[(rates == 0) && (diffscen == 0), nrisk1 = Table[nrisk1[[m, {1}]], {m, Plus@@modelsel}]];
printbug["5.5.1.1"];
		If[(rates == 1) && (diffscen == 0), nrisk1 = Table[prisk10[[m, {1}]], {m, Plus@@modelsel}]];
printbug["5.5.1.2"];
		If[(rates == 1) && (diffscen == 1),
			nrisk1 = Table[prisk10[[m, scen]] - prisk10[[m, 1]], {m, Plus@@modelsel}, {scen, 2, nscen0}]];

printbug["5.5.2"];
			
		If[(standardized == 1),
			nrisk1 = Table[nrisk1[[m, scen, r, g, ri, n]] / standardnrisk1[[m, scen, r, g, ri]],
					{m, Plus@@modelsel}, {scen, Length[nrisk1[[m]]]}, {r, nrd}, {g, ng},
					{ri, ncrsel[[r]]}, {n, nstap}]];

printbug["5.5.3"];			

		If[(discounte > eps),
			nrisk1 = Table[nrisk1[[m, scen, r, g, ri, n]] / (1 + discounte)^(n - 1),
					{m, Plus@@modelsel}, {scen, Length[nrisk1[[m]]]}, {r, nrd}, {g, ng},
					{ri, ncrsel[[r]]}, {n, nstap}]];

printbug["5.5.4"];			

		If[(cumulative == 1),
			nrisk1 = Table[Plus@@nrisk1[[m, scen, r, g, ri, Range[n]]],
					{m, Plus@@modelsel}, {scen, Length[nrisk1[[m]]]}, {r, nrd}, {g, ng},
					{ri, ncrsel[[r]]}, {n, nstap}]];

printbug["5.5.5"];			

		(* GRAPHICAL OUTPUT *)

		If[(graphicoutput == 1),

			If[(outputnotebook == 1), addcellnb[plotrisknb1[nrisk1, outputnames[[2]], diffscen]]]]

		];

	(* CONTINUOUSLY DISTRIBUTED RISK FACTOR DISTRIBUTION CHARACTERISTICS *)

printbug["5.6"];

	If[(riskcontind == 1) && (nrc > 0) && (outputsel[[3]] == 1), _];

	(* DISEASE DATA *)

	hmakendis[outputnr_, ndis10_, pdis10_] := Block[{},

printbug["5.7"];

		ndis1 = If[(diffscen == 0), ndis10, Table[makendis[resmodel[[m, outputnr]], diffscen], {m, Plus@@modelsel}]];

printbug["5.7.1"];	

		If[(rates == 0) && (diffscen == 0), ndis1 = Table[ndis1[[m, {1}]], {m, Plus@@modelsel}]];
		If[(rates == 1) && (diffscen == 0), ndis1 = Table[pdis10[[m, {1}]], {m, Plus@@modelsel}]];
		If[(rates == 1) && (diffscen == 1),
			ndis1 = Table[pdis10[[m, scen]] - pdis10[[m, 1]], {m, Plus@@modelsel}, {scen, 2, nscen0}]];

printbug["5.7.2"];
			
		If[(standardized == 1),
			ndis1 = Table[ndis1[[m, scen, d, g, n]] / standardnpop1[[m, scen, g]],
					{m, Plus@@modelsel}, {scen, Length[ndis1[[1]]]}, {d, Length[ndis1[[1, 1]]]}, {g, ng}, {n, nstap}]];

printbug["5.7.3"];
			
		If[(discounte > eps),
			ndis1 = Table[ndis1[[m, scen, d, g, n]] / (1 + discounte)^(n - 1),
					{m, Plus@@modelsel}, {scen, Length[ndis1[[1]]]}, {d, Length[ndis1[[1, 1]]]}, {g, ng}, {n, nstap}]];

printbug["5.7.4"];

		If[(cumulative == 1),
			ndis1 = Table[Plus@@ndis1[[m, scen, d, g, Range[n]]],
					{m, Plus@@modelsel}, {scen, Length[ndis1[[1]]]}, {d, Length[ndis1[[1, 1]]]}, {g, ng}, {n, nstap}]];

printbug["5.7.5"];			

		(* GRAPHICAL OUTPUT *)

		If[(graphicoutput == 1),

			If[(outputnotebook == 1), addcellnb[plotdisnb1[ndis1, outputnames[[outputnr]], diffscen]]]]
				
		];

	(* DISEASE PREVALENCE NUMBERS *)

	If[(nd > 0) && (outputsel[[4]] == 1),

printbug["5.8"];
		pdis10 = Table[ndis10[[m, scen, d]] / (npop10[[m, scen]] + eps), {m, Plus@@modelsel}, {scen, nscen0}, {d, nd}];
			
		hmakendis[4, ndis10, pdis10]];
		
	(* DISEASE INCIDENCE NUMBERS *)

	If[(nd > 0) && (outputsel[[5]] == 1),

printbug["5.9"];

		ninc10 = Table[makendis[resmodel[[m, 5]], 0], {m, Plus@@modelsel}];
		pinc10 = Table[ninc10[[m, scen, d]] / (npop10[[m, scen]] + eps), {m, Plus@@modelsel}, {scen, nscen0}, {d, nd}];

		hmakendis[5, ninc10, pinc10]];

	(* DISEASE MORTALITY NUMBERS *)

	If[(outputsel[[6]] == 1),

printbug["5.10"];

		nmort10 = Table[makendis[resmodel[[m, 6]], 0], {m, Plus@@modelsel}];
		pmort10 = Table[nmort10[[m, scen, d]] / (npop10[[m, scen]] + eps), {m, Plus@@modelsel}, {scen, nscen0}, {d, nd + 2}];

		hmakendis[6, nmort10, pmort10]];
	
	(* MEAN AGE AT DISEASE ONSET *)

	If[(nd > 0) && (outputsel[[7]] == 1),

printbug["5.11"];

		onsetage1 = Table[makendis[resmodel[[m, 7]], 0], {m, Plus@@modelsel}] / ndis10;
		
		onsetage1 = Table[makeresdiff[onsetage1[[m]]], {m, Plus@@modelsel}];
		
		If[(diffscen == 0), onsetage1 = Table[onsetage1[[m, {1}]], {m, Plus@@modelsel}]];

		(* GRAPHICAL OUTPUT *)

		If[(graphicoutput == 1),

			If[(outputnotebook == 1), addcellnb[plotdisnb1[onsetage, outputnames[[7]], diffscen]]]]

		];
	
	(* MEAN TIME SINCE SMOKING CESSATION *)

	If[(RRsmokduurind == 1) && (outputsel[[8]] == 1),

printbug["5.12"];

		duurstop1 = Table[makenpop0[resmodel[[m, 8]], 0] / (nrisk10[[m, Range[nscen0], 1, Range[ng], 3]] + eps), {m, Plus@@modelsel}];
		
		duurstop1 = Table[makeresdiff[duurstop1[[m]], diffscen], {m, Plus@@modelsel}];
		
		If[(diffscen == 0), duurstop1 = Table[duurstop1[[m, {1}]], {m, Plus@@modelsel}]];
				
		(* GRAPHICAL OUTPUT *)

		If[(graphicoutput == 1),

			If[(outputnotebook == 1), addcellnb[plotpopnb1[duurstop, outputnames[[8]], diffscen]]]];

		];

]; (* END PRESENTDIFFNUMBERS *)


(* --------------------------------------------------
	PLOTTING RESULTS FOR EACH CZM MODEL VERSION SELECTED
----------------------------------------------------*)

printbug["6."];

eachname1[m_] 	:= Block[{},

			str = modelnames[[m]] <> basescenstr;
			If[(outputscreen == 1), 			headingprint1[str]];
			If[(outputnotebook == 1), 			addcellnb[headingprint1nb[str]]];
			If[(outputfile ==1) && (tabeloutput == 1),	WriteString[resmodelfile, str <> "\n\n"]]];

diffname1[m_] 	:= Block[{},

			str = modelnames[[m]] <> compscenstr;
			If[(outputscreen == 1), 			headingprint1[str]];
			If[(outputnotebook == 1), 			addcellnb[headingprint1nb[str]]];
			If[(outputfile ==1) && (tabeloutput == 1),	WriteString[resmodelfile, str <> "\n\n"]]];


(* --------------------------------------------------
	PLOTTING RESULTS FOR EACH PAIR OF CZM MODEL VERSIONS SELECTED
----------------------------------------------------*)

eachname2[m1_, m2_] := Block[{},

			str = "differences between " <> modelnames[[m1]] <> " and " <> modelnames[[m2]] <>
				" for baseline scenario";
			If[(outputscreen == 1),				headingprint1[str]];
			If[(outputnotebook == 1),			addcellnb[headingprint1nb[str]]];
			If[(outputfile ==1) && (tabeloutput == 1),	WriteString[resmodelfile, str <> "\n\n"]]];
	
diffname2[m1_, m2_] := Block[{},

			str = "differences between " <> modelnames[[m1]] <> " and " <> modelnames[[m2]] <>
				"differences with baseline scenario";
			If[(outputscreen == 1),				headingprint1[str]];
			If[(outputnotebook == 1),			addcellnb[headingprint1nb[str]]];
			If[(outputfile ==1) && (tabeloutput == 1),	WriteString[resmodelfile, str <> "\n\n"]]];


(* --------------------------------------------------
	CONCATENATION OF NOTEBOOK CELLS
----------------------------------------------------*)

addcellnb[cell_] := Block[{}, cellnb = Flatten[{cellnb, cell}]];


(* --------------------------------------------------
	RISK FACTOR CLASS AND DISEASE PREVALENCE NUMBERS CONDITIONAL ON DISEASE
----------------------------------------------------*)

printbug["7."];
(* GENERATES RISK FACTOR CLASS PREVALENCE RATES WITHIN DISEASED PATIENTS FROM MARGINAL MODEL ON TOTAL POPULATION *)

makeresriskdis0[dat_] := Block[{},

printbug["7.1"];

	(* POPULATION NUMBERS TOTAL AND SPECIFIED BY AGE CLASS *)

printbug["7.1.1"];

	hnpop		= Table[Plus@@dat[[1, scenlist[[scen]], n, g]], {scen, nscen0}, {g, ng}, {n, nstap}];

	meanhnpop	= Table[aggregc[hnpop[[scen, g, n]], 1, 7], {scen, nscen0}, {g, ng}, {n, nstap}];


	(* CALCULATED RISK FACTOR CLASS PREVALENCE NUMBERS AND DATA FOR HBA1C *)

printbug["7.1.2"];

	hprisk 		= Table[Plus@@dat[[2, scenlist[[scen]], n, r, g, ri]] /
					(Plus@@(Plus@@dat[[2, scenlist[[scen]], n, r, g]]) + eps),
				{scen, nscen0}, {n, nstap}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];

	hprisksel 	= Table[meanaggreg[prisksel[[r, g, ri]]],
				{r, nrdpop + 1, nrdpop + nrddis}, {g, ng}, {ri, ncrsel[[r]]}];

	(* CALCULATED DISEASE PREVALENE RATES *)

printbug["7.1.3"];

	hpdis		= Table[Plus@@dat[[4, scenlist[[scen]], n, d, g]] / (Plus@@dat[[1, scenlist[[scen]], n, g]] + eps),
				{scen, nscen0}, {n, nstap}, {d, nd}, {g, ng}];

	(* RELATIVE RISK VALUES *)

printbug["7.1.4"];

	meanRRrisk	= Table[meanaggreg[RRriskseladj[[r, RRriskindsel[[r, d + 1]], g, ri]]],
				{r, nrd}, {d, nd}, {g, ng}, {ri, ncrsel[[r]]}];

	meanRRdis	= Table[meanaggreg[RRdisadj[[RRdisindsel[[d, d1]], g]]], {d, nd}, {d1, nd}, {g, ng}];

	meanERRrisk	= Table[Plus@@(meanRRrisk[[r, d, g]] hprisk[[scen, n, r, g]]),
				{scen, nscen0}, {n, nstap}, {r, nrd}, {d, nd}, {g, ng}];

	(* PAIRS OF RISK FACTORS AND DISEASES RELATED, SUCH AS HBA1C AND DIABETES *)

printbug["7.1.5"];

	hriskdispair	= Transpose[{riskindddis, disindinv[[disriskindddis]]}];

	resrisk		= Table[0, {nd}, {nscen0}, {r, nrd}, {ng}, {ncrsel[[r]]}, {nstap}];

	(* ADJUSTMENT OF RISK FACTOR VALUES *)

printbug["7.1.6"];
	
	Do[	(* RISK FACTORS WORKING IN TOTAL POPULATION *)
			
		If[Not[MemberQ[riskindddis, r]],

			(* ADJUSTMENT FOR CAUSAL RELATION BETWEEN RISK FACTOR AND DISEASE *)

			hpriskadj	= hprisk[[Range[nscen0], Range[nstap], r]];

			Do[	hadj	= meanRRrisk[[r, d, g, ri]];

				Do[hpriskadj[[scen, n, g, ri]] *= hadj, {scen, nscen0}, {n, nstap}],

				{g, ng}, {ri, 1,ncrsel[[r]]}];

			(* ADJUSTMENT FOR INTERMEDIATE DISEASES *)

			hadj = Times@@Table[1 + (meanRRdis[[d1, d, g]] - 1) (meanRRrisk[[r, d, g, ri]] - 1) hpdis[[scen, n, d1, g]] /
						(meanERRrisk[[scen, n, r, d1, g]] + (meanRRdis[[d1, d, g]] - 1) hpdis[[scen, n, d1, g]] +
							eps),
						{d1, nd}, {scen, nscen0}, {n, nstap}, {g, ng}, {ri, ncrsel[[r]]}];

			Do[hpriskadj[[scen, n, g, ri]] *= hadj[[scen, n, g, ri]], {scen, nscen0}, {n, nstap}, {g, ng}, {ri, ncrsel[[r]]}];
			(* SCALING *)

	        	hpriskadj = Table[hpriskadj[[scen, n, g, ri]] / (Plus@@hpriskadj[[scen, n, g]] + eps),
					{scen, nscen0}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}];

			(* VALUE AVERAGED OVER TOTAL POPULATION OR SPECIFIED BY AGE CLASS *)

			If[(agespecres <= 1),

				(* VALUES AVERAGED OVER TOTAL POPULATION *)

				Do[resrisk[[d, scen, r, g, ri, n]] = Plus@@(hpriskadj[[scen, g, ri, n]] hnpop[[scen, g, n]]) /
										Plus@@hnpop[[scen, g, n]],
					{scen, nscen0}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}],

				(* VALUES SPECIFIED BY AGE CLASS *)

				Do[resrisk[[d, scen, r, g, ri, n]] = aggregc[hpriskadj[[scen, g, ri, n]] hnpop[[scen, g, n]], 1, 7] /
										(meanhnpop[[scen, g, n]] + eps),
					{scen, nscen0}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}]

				]

			];

		(* RISK FACTOR DISTRIBUTION WITHIN RELATED DISEASE, E.G. HBA1C WITHIN DIABETICS *)			

		If[MemberQ[hriskdispair, {r, d}],

			If[(agespecres <= 1),

				(* VALUES AVERAGED OVER TOTAL POPULATION *)

				Do[resrisk[[d, scen, r, g, ri, n]] =
					Plus@@Flatten[dat[[2, scenlist[[scen]], n, r, g, ri]]] /
						(Plus@@Flatten[dat[[2, scenlist[[scen]], n, r, g]]] + eps),
					{scen, nscen0}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}],

				(* VALUES SPECIFIED BY AGE CLASS *)

				Do[resrisk[[d, scen, r, g, ri, n]] =
					aggregc[Plus@@dat[[2, scenlist[[scen]], n, r, g, ri]], 1, 7] /
						(aggregc[Plus@@(Plus@@dat[[2, scenlist[[scen]], n, r, g]]), 1, 7] + eps),
					{scen, nscen0}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}]

				]];

		(* NO RESULTS PRESENTED FOR DISEASE-SPECIFIC RISK FACTORS WITHIN DISEASES OTHER THAN RELATED ONE *)

		If[MemberQ[riskindddis, r] && Not[MemberQ[hriskdispair, {r, d}]] && (agespecres > 1),

			Do[resrisk[[d, scen, r, g, ri, n]] = Table[0, {nac[[7]]}],
				{scen, nscen0}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}]],

		{r, nrd}, {d, nd}];

	(* RELATIVE RISK VALUES OF ONE DISEASE ON ANOTHER DISEASE INCIDENCE *)

printbug["7.1.7"];

	hRRdis 	= Table[meanRRdis, {nscen0}, {nstap}];

	(* ADJUSTMENT FOR JOINT RISK FACTORS WORKING ON TOTAL POPULATION *)

printbug["7.1.8"];

	meanRMrisk 	= Table[meanERRrisk[[scen, n, r, d, g]] meanRRrisk[[r, d, g, ri]] / (meanERRrisk[[scen, n, r, d, g]]^2 + eps),
				{scen, nscen0}, {n, nstap}, {r, nrdpop}, {d, nd}, {g, ng}, {ri, ncrsel[[r]]}];

	Do[hRRdis[[scen, n, d, d1, g]] *= Times@@Table[Plus@@(meanRMrisk[[scen, n, r, d, g]] meanRMrisk[[scen, n, r, d1, g]] *
								hprisk[[scen, n, r, g]]),
							{r, nrdpop}],
		{scen, nscen0}, {n, nstap}, {d, nd}, {d1, nd}, {g, ng}];

	(* ADJUSTMENT FOR JOINT RISK FACTORS WORKING WITHIN DISEASE, E.G. HBA1C WITHIN DIABETICS *)

printbug["7.1.8"];

	meanRMrisk = Table[Plus@@(meanRRrisk[[nrdpop + r, d, g]] hprisk[[scen, n, nrdpop + r, g]]) /
					Plus@@(meanRRrisk[[nrdpop + r, d, g]] hprisksel[[r, g]]),
				 {scen, nscen0}, {n, nstap}, {r, nrddis}, {d, nd}, {g, ng}];

printbug["7.1.9"];

	Do[If[(d1 != disindinv[[disriskindddis[[r]]]]),
 
       			Do[hRRdis[[scen, n, disindinv[[disriskindddis[[r]]]], d1, g]] *=
					(1 + hpdis[[scen, n, disindinv[[disriskindddis, r]], g]] (meanRMrisk[[scen, n, r, d1, g]] - 1)),
				{scen, nscen0}, {n, nstap}, {g, ng}]],

		{r, nrddis}];

	If[(agespecres <= 1),

		(* VALUES AVERAGED OVER TOTAL POPULATION *)

printbug["7.1.10"];

		resdis = Table[Plus@@(hpdis[[scen, n, d1, g]] hnpop[[scen, g, n]] *
				If[(d1 == d),
					1,
					hRRdis[[scen, n, d, d1, g]] / (1 + hpdis[[scen, n, d, g]] (hRRdis[[scen, n, d, d1, g]] - 1))
					]) /
					Plus@@hnpop[[scen, g, n]],
				{d, nd}, {scen, nscen0}, {d1, nd}, {g, ng}, {n, nstap}],

		(* VALUES SPECIFIED BY AGE CLASS *)

printbug["7.1.11"];

		resdis = Table[aggregc[hpdis[[scen, n, d1, g]] hnpop[[scen, g, n]] *
					If[(d1 == d),
						1,
						hRRdis[[scen, n, d, d1, g]] / (1 + hpdis[[scen, n, d, g]] (hRRdis[[scen, n, d, d1, g]] - 1))
						] , 1, 7] /
					aggregc[hnpop[[scen, g, n]], 1, 7],
				{d, nd}, {scen, nscen0}, {d1, nd}, {g, ng}, {n, nstap}]

		];

	(* DISEASE PREVALENCE RATES WITHIN SAME DISEASE EQUALS VALUE 1 *)

printbug["7.1.12"];

	Do[resdis[[d, Range[nscen0], d]] = 1 + 0 resdis[[d, Range[nscen0], d]], {d, nd}];

	{resrisk, resdis}

	]; (* END MAKERESRISKDIS0 *)

(* GENERATES RISK FACTOR CLASS PREVALENCE RATES WITHIN DISEASED PATIENTS FROM JOINT MODELS ON TOTAL POPULATION *)

makeresriskdis1[dat_] := Block[{},

printbug["7.2"];

	resrisk	= Table[0, {nd}, {nscen0}, {r, nrd}, {ng}, {ncrsel[[r]]}, {nstap}];

printbug["7.2.1"];

	Do[	(* STATES OF PERSONS WITH DISEASE d *)

		disset 	= zinddis[[d, 2]];

printbug["7.2.2"];

		Do[	(* STATES OF PERSONS WITH DISEASE d AND IN CLASS ri OF RISK FACTOR r *)

			currdis	= Intersection[zindrisk[[r, ri]], disset];

			Do[	resdis	= If[(agespecres <= 1),

					Plus@@Flatten[dat[[scenlist[[scen]], n, g, disset]]],

					aggregc[Plus@@(Plus@@dat[[scenlist[[scen]], n, g, disset]]), 1, 7]];

				(* PROPORTION OF DISEASED PERSONS IN RISK FACTOR CLASS *)

				resrisk[[d, scen, r, g, ri, n]] = If[(agespecres <= 1),

					Plus@@Flatten[dat[[scenlist[[scen]], n, g, currdis]]],

					aggregc[Plus@@(Plus@@dat[[scenlist[[scen]], n, g, currdis]]), 1, 7]] / (resdis + eps),

				{scen, nscen0}, {g, ng}, {n, nstap}],

			{r, nrd}, {ri, ncrsel[[r]]}],
				
		{d, nd}];

	resrisk

	];

(* GENERATES DISEASE PREVALENCE RATES WITHIN DISEASED PATIENTS FROM JOINT MODELS ON TOTAL POPULATION *)

makeresdisdis1[dat_] := Block[{},

printbug["7.3"];

	resdis	= Table[0, {nd}, {nscen0}, {nd}, {ng}, {nstap}];

printbug["7.3.1"];

	Do[	(* STATES OF PERSONS WITH DISEASE d *)

		disset 	= zinddis[[d, 2]];

printbug["7.3.2"];

		Do[	(* STATES OF PERSONS WITH DISEASE d AND IN DISEASE d1 *)

			currdis	= Intersection[zinddis[[d1, 2]], disset];

			Do[	resdis1	= If[(agespecres <= 1),

					Plus@@Flatten[dat[[scenlist[[scen]], n, g, disset]]],

					aggregc[Plus@@(Plus@@dat[[scenlist[[scen]], n, g, disset]]), 1, 7]];

				(* PROPORTION OF DISEASED PERSONS IN RISK FACTOR CLASS *)

				resdis[[d, scen, d1, g, n]] = If[(agespecres <= 1),

					Plus@@Flatten[dat[[scenlist[[scen]], n, g, currdis]]],

					aggregc[Plus@@(Plus@@dat[[scenlist[[scen]], n, g, currdis]]), 1, 7]] / (resdis1 + eps),

				{scen, nscen0}, {g, ng}, {n, nstap}],

			{d1, nd}],
				
		{d, nd}];

	resdis

	];

(* GENERATES RISK FACTOR CLASS PREVALENCE RATES WITHIN DISEASED PATIENTS FROM JOINT MODELS STRATIFIED BY AGE *)

makeresriskdis2[dat_] := Block[{},

printbug["7.4"];

	(* MARGINAL DISEASE PREVALENCE NUMBERS *)

	resdis	= Table[0, {nd}, {nscen}, {ng}, {nstap}, {na2}];

printbug["7.4.1"];

	Do[	disset	= zinddis[[d, 2]];

		Do[resdis[[d, scen, g, n, agesel[[a1]] + n - 1]] += Plus@@Flatten[dat[[scen, g, a1, n, disset]]],
			{scen, nscen}, {g, ng}, {n, nstap}, {a1, Length[agesel]}],

		{d, nd}];

	resdis	= Table[Plus@@resdis[[d, scenlist[[scen]]]], {d, nd}, {scen, nscen0}];

	resdis	= Table[Flatten[{resdis[[d, scen, g, n, Range[na]]], Plus@@Drop[resdis[[d, scen, g, n]], na]}],
			{d, nd}, {scen, nscen0}, {g, ng}, {n, nstap}];

	(* RISK FACTOR CLASS PREVALENCE NUMBERS WITHIN DISEASED POPULATION *)

printbug["7.4.2"];

	resrisk	= Table[0, {nd}, {nscen}, {r, nrd}, {ng}, {ncrsel[[r]]}, {nstap}, {na2}];

	Do[	disset = Intersection[zinddis[[d, 2]], zindrisk[[r, ri]]];

		Do[resrisk[[d, scen, r, g, ri, n, agesel[[a1]] + n - 1]] += Plus@@Flatten[dat[[scen, g, a1, n, disset]]],
			{scen, nscen0}, {g, ng}, {n, nstap}, {a1, Length[agesel]}],

		{d, nd}, {r, nrd}, {ri, ncrsel[[r]]}];

	resrisk	= Table[Plus@@resrisk[[d, scenlist[[scen]]]], {d, nd}, {scen, nscen0}];

	resrisk	= Table[Flatten[{resrisk[[d, scen, r, g, ri, n, Range[na]]], Plus@@Drop[resrisk[[d, scen, r, g, ri, n]], na]}],
			{d, nd}, {scen, nscen0}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}];

	(* TRANSFORMED TO PROPORTIONS *)

printbug["7.4.3"];

	resrisk = If[(agespecres <= 1),

			Table[Plus@@resrisk[[d, scen, r, g, ri, n]] / (Plus@@resdis[[d, scen, g, n]] + eps),
				{d, nd}, {scen, nscen0}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}],


			Table[aggregc[resrisk[[d, scen, r, g, ri, n]], 1, 7] / (aggregc[resdis[[d, scen, g, n]], 1, 7] + eps),
				{d, nd}, {scen, nscen0}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}]]
		
	];

(* GENERATES DISEASE PREVALENCE RATES WITHIN DISEASED PATIENTS FROM JOINT MODELS STRATIFIED BY AGE *)

makeresdisdis2[dat_] := Block[{},

printbug["7.5"];

	(* MARGINAL DISEASE PREVALENCE NUMBERS *)

printbug["7.5.1"];

	resdis	= Table[0, {nd}, {nscen}, {ng}, {nstap}, {na2}];

	Do[	disset	= zinddis[[d, 2]];

		Do[resdis[[d, scen, g, n, agesel[[a1]] + n - 1]] += Plus@@Flatten[dat[[scen, g, a1, n, disset]]],
			{scen, nscen}, {g, ng}, {n, nstap}, {a1, Length[agesel]}],

		{d, nd}];

	resdis	= Table[Plus@@resdis[[d, scenlist[[scen]]]], {d, nd}, {scen, nscen0}];

	resdis	= Table[Flatten[{resdis[[d, scen, g, n, Range[na]]], Plus@@Drop[resdis[[d, scen, g, n]], na]}],
			{d, nd}, {scen, nscen0}, {g, ng}, {n, nstap}];

	(* DISEASE PREVALENCE NUMBERS WITHIN DISEASED POPULATION *)

printbug["7.5.2"];

	resdis1	= Table[0, {nd}, {nscen}, {nd}, {ng}, {nstap}, {na2}];

	Do[	disset = Intersection[zinddis[[d, 2]], zinddis[[d1, 2]]];

		Do[resdis1[[d, scen, d1, g, n, agesel[[a1]] + n - 1]] += Plus@@Flatten[dat[[scen, g, a1, n, disset]]],
			{scen, nscen0}, {g, ng}, {n, nstap}, {a1, Length[agesel]}],

		{d, nd}, {d1, nd}];

	resdis1	= Table[Plus@@resdis1[[d, scenlist[[scen]]]], {d, nd}, {scen, nscen0}];

	resdis1	= Table[Flatten[{resdis1[[d, scen, d1, g, n, Range[na]]], Plus@@Drop[resdis1[[d, scen, d1, g, n]], na]}],
			{d, nd}, {scen, nscen0}, {d1, nd}, {g, ng}, {n, nstap}];

	(* TRANSFORMED TO PROPORTIONS *)

printbug["7.5.3"];

	resdis1 = If[(agespecres <= 1),

			Table[Plus@@resdis1[[d, scen, d1, g, n]] / (Plus@@resdis[[d, scen, g, n]] + eps),
				{d, nd}, {scen, nscen0}, {d1, nd}, {g, ng}, {n, nstap}],


			Table[aggregc[resdis1[[d, scen, d1, g, n]], 1, 7] / (aggregc[resdis[[d, scen, g, n]], 1, 7] + eps),
				{d, nd}, {scen, nscen0}, {d1, nd}, {g, ng}, {n, nstap}]]
		
	];

(* GENERATES RISK FACTOR CLASS PREVALENCE RATES WITHIN DISEASED PATIENTS FROM JOINT MODELS STRATIFIED BY INDIVIDUALS *)

makeresriskdis3[dat_] := Block[{},

printbug["7.6"];

	(* MARGINAL DISEASE PREVALENCE NUMBERS *)

printbug["7.6.1"];

	nstap1 	= Table[Plus@@Flatten[Sign[dat[[scen, g, a1, b, 4]]]],
			{scen, nscen}, {g, ng}, {a1, Length[agesel]}, {b, ndraw}] / nz;

	resdis	= Table[0, {nd}, {nscen}, {ng}, {nstap}, {na2}];

	Do[If[(dat[[scen, g, a1, b, 4, n, nrd + d]] == 2), ++resdis[[d, scen, g, n, agesel[[a1]] + n - 1]]],
			{scen, nscen0}, {g, ng}, {a1, Length[agesel]}, {b, ndraw}, {n, nstap1[[scen, g, a1, b]]}, {d, nd}];

	resdis	= Table[Plus@@resdis[[d, scenlist[[scen]]]], {d, nd}, {scen, nscen0}];

	resdis	= Table[Flatten[{resdis[[d, scen, g, n, Range[na]]], Plus@@Drop[resdis[[d, scen, g, n]], na]}],
			{d, nd}, {scen, nscen0}, {g, ng}, {n, nstap}];

	(* RISK FACTOR CLASS PREVALENCE NUMBERS WITHIN DISEASED POPULATION *)

printbug["7.6.2"];

	resrisk	= Table[0, {nd}, {nscen}, {r, nrd}, {ng}, {ncrsel[[r]]}, {nstap}, {na2}];

	Do[If[(dat[[scen, g, a1, b, 4, n, r]] == ri) && (dat[[scen, g, a1, b, 4, n, nrd + d]] == 2),
				++resrisk[[d, scen, r, g, ri, n, agesel[[a1]] + n - 1]]],
			{scen, nscen}, {g, ng}, {a1, Length[agesel]}, {b, ndraw}, {n, nstap1[[scen, g, a1, b]]},
			{r, nrd}, {ri, ncrsel[[r]]}, {d, nd}];

	resrisk	= Table[Plus@@resrisk[[d, scenlist[[scen]]]], {d, nd}, {scen, nscen0}];
 
	resrisk	= Table[Flatten[{resrisk[[d, scen, r, g, ri, n, Range[na]]], Plus@@Drop[resrisk[[d, scen, r, g, ri, n]], na]}],
			{d, nd}, {scen, nscen0}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}];

	(* TRANSFORMED TO PROPORTIONS *)

printbug["7.6.3"];

	resrisk = If[(agespecres <= 1),

			Table[Plus@@resrisk[[d, scen, r, g, ri, n]] / (Plus@@resdis[[d, scen, g, n]] + eps),
				{d, nd}, {scen, nscen0}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}],

			Table[aggregc[resrisk[[d, scen, r, g, ri, n]], 1, 7] / (aggregc[resdis[[d, scen, g, n]], 1, 7] + eps),
				{d, nd}, {scen, nscen0}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}, {n, nstap}]]
		
	];

(* GENERATES DISEASE PREVALENCE RATES WITHIN DISEASED PATIENTS FROM JOINT MODELS STRATIFIED BY INDIVIDUALS *)

makeresdisdis3[dat_] := Block[{},

printbug["7.7"];

	(* MARGINAL DISEASE PREVALENCE NUMBERS *)

printbug["7.7.1"];

	nstap1 	= Table[Plus@@Flatten[Sign[dat[[scen, g, a1, b, 4]]]],
			{scen, nscen}, {g, ng}, {a1, Length[agesel]}, {b, ndraw}] / nz;

	resdis	= Table[0, {nd}, {nscen}, {ng}, {nstap}, {na2}];

	Do[If[(dat[[scen, g, a1, b, 4, n, nrd + d]] == 2), ++resdis[[d, scen, g, n, agesel[[a1]] + n - 1]]],
			{scen, nscen0}, {g, ng}, {a1, Length[agesel]}, {b, ndraw}, {n, nstap1[[scen, g, a1, b]]}, {d, nd}];

	resdis	= Table[Plus@@resdis[[d, scenlist[[scen]]]], {d, nd}, {scen, nscen0}];

	resdis	= Table[Flatten[{resdis[[d, scen, g, n, Range[na]]], Plus@@Drop[resdis[[d, scen, g, n]], na]}],
			{d, nd}, {scen, nscen0}, {g, ng}, {n, nstap}];

	(* RISK FACTOR CLASS PREVALENCE NUMBERS WITHIN DISEASED POPULATION *)

printbug["7.7.2"];

	resdis1	= Table[0, {nd}, {nscen}, {nd}, {ng}, {nstap}, {na2}];

	Do[If[(dat[[scen, g, a1, b, 4, n, nrd + d1]] == 2) && (dat[[scen, g, a1, b, 4, n, nrd + d]] == 2),
				++resdis1[[d, scen, d1, g, n, agesel[[a1]] + n - 1]]],
			{scen, nscen}, {g, ng}, {a1, Length[agesel]}, {b, ndraw}, {n, nstap1[[scen, g, a1, b]]}, {d1, nd}, {d, nd}];

	resdis1	= Table[Plus@@resdis1[[d, scenlist[[scen]]]], {d, nd}, {scen, nscen0}];
 
	resdis1	= Table[Flatten[{resdis1[[d, scen, d1, g, n, Range[na]]], Plus@@Drop[resdis1[[d, scen, d1, g, n]], na]}],
			{d, nd}, {scen, nscen0}, {d1, nd}, {g, ng}, {n, nstap}];
 
	(* TRANSFORMED TO PROPORTIONS *)

printbug["7.7.3"];

	resdis1 = If[(agespecres <= 1),

			Table[Plus@@resdis1[[d, scen, d1, g, n]] / (Plus@@resdis[[d, scen, g, n]] + eps),
				{d, nd}, {scen, nscen0}, {d1, nd}, {g, ng}, {n, nstap}],

			Table[aggregc[resdis1[[d, scen, d1, g, n]], 1, 7] / (aggregc[resdis[[d, scen, g, n]], 1, 7] + eps),
				{d, nd}, {scen, nscen0}, {d1, nd}, {g, ng}, {n, nstap}]]
		
	];

(* RISK FACTOR AND DISEASE RESULTS FOR ANY MODEL *)

makeresriskdis[m_] := Block[{},

printbug["7.8"];

	(* MODEL RESULTS *)

	dat = Switch[m,
			1, resmarginalmodeldetermpop,
			2, Drop[ReadList[Global`outputpath <> "jointmodeldetermpopprev.m"], 1],
			3, Drop[ReadList[Global`outputpath <> "jointmodeldetermageprev.m"], 1],
			4, Drop[ReadList[Global`outputpath <> "jointmodelstochindprev.m"], 1],
			_, {}
			];

	(* RESULTS FROM MODELS ON TOTAL POPULATION *)

	If[(MemberQ[{1}, m]),

printbug["7.8.1"];

		hres	= makeresriskdis0[dat];
		resrisk	= hres[[1]];
		resdis	= hres[[2]]];
		
	If[(MemberQ[{2, 6}, m]),

printbug["7.8.2"];

		dat 	= Partition[dat, nstap];
		resrisk = makeresriskdis1[dat];
		resdis	= makeresdisdis1[dat]];
		
	If[(MemberQ[{3, 7}, m]),

printbug["7.8.3"];

		dat 	= Partition[Partition[Partition[dat, nstap], Length[agesel]], ng];
		resrisk = makeresriskdis2[dat];
		resdis	= makeresdisdis2[dat]];

	If[(MemberQ[{4}, m]),

printbug["7.8.4"];

		dat[[All, 4]] = makezvalinv[dat[[All, 4]]];
		dat 	= Partition[Partition[Partition[dat, ndraw], Length[agesel]], ng];
		resrisk	= makeresriskdis3[dat];
		resdis	= makeresdisdis3[dat]];

	{resrisk, resdis}
			
	];


(* --------------------------------------------------
	CONSTRUCTION OF GRAPHICAL OUTPUT
----------------------------------------------------*)

If[((graphicoutput == 1) || (tabeloutput == 1)) && MemberQ[{0, 3, 4}, analyse],

printbug["9."];

	If[(outputfile == 1),

printbug["9.1"];

		date 		= ToString[Date[][[3]]] <> "-" <> ToString[Date[][[2]]] <> "-" <> ToString[Date[][[1]]];

		time 		= ToString[Date[][[4]]] <> "h" <> ToString[Date[][[5]]];

		resmodeltxt	= Global`outputpath <> "results" <> date <> "_" <> time <> ".txt";

		resmodelfile	= OpenWrite[resmodeltxt]];

	

	If[(outputnotebook == 1), cellnb = {headingprintnb["graphical & tabular results of one model run"]}];

	(* PRESENTING RESULTS FOR EACH CZM MODEL VERSION SELECTED *)

	Do[	If[(modelsel[[m]] == 1),

printbug["9.2"];

			(* RESULTS FOR BASELINE SCENARIO *)

			eachname1[m];
			presentnumbers[resmodel[[m]], 0];

			(* DIFFERENCES WITH BASELINE SCENARIO *)

			If[(nscen0 > 1),
				diffname1[m];
				presentnumbers[resmodel[[m]], 1]]
			],

		{m, nmodel}];
	
	(* PRESENTING RESULTS FOR ALL CZM MODEL VERSIONS SELECTED *)

	hmodelsel = Select[modelsel Range[Length[modelsel]], Positive];

	If[(Length[hmodelsel] > 1),

printbug["9.3"];

			(* RESULTS FOR BASELINE SCENARIO *)
	
			presentdiffnumbers[resmodel[[hmodelsel]], 0];

			(* DIFFERENCES WITH BASELINE SCENARIO *)

			If[(nscen0 > 1), presentdiffnumbers[resmodel[[hmodelsel]], 1]]

		];

	If[(withindisease == 1),

printbug["9.4"];

		Do[	If[(modelsel[[m]] == 1) && (MemberQ[{1, 2, 3, 4}, m]),

printbug["9.4.1"];

				str 	= modelnames[[m]];
				str1[d_] := " within " <> disnames[[disind[[d]]]];
				hres	= makeresriskdis[m];
				resrisk	= hres[[1]];
				resdis 	= hres[[2]];

				(* RESULTS FOR BASELINE SCENARIO *)

				(* GRAPHICAL OUTPUT IN NOTEBOOK *)
				
				If[(outputnotebook == 1),
printbug["9.4.2"];
					addcellnb[headingprint1nb[str]];			
					Do[addcellnb[plotrisknb[resrisk[[d, {1}]], outputnames[[2]] <> str1[d], 0]],	{d, nd}];
					Do[addcellnb[plotdisnb[resdis[[d, {1}]], outputnames[[4]] <> str1[d], 0]], {d, nd}]];

				(* TABEL OUTPUT IN OUTPUTFILE *)

				If[(outputfile == 1) && (tabeloutput == 1),
printbug["9.4.3"];
					WriteString[resmodelfile, str <> "\n\n"];
					Do[	WriteString[resmodelfile, outputnames[[2]] <> str1[d] <> basescenstr <> "\n\n"];
						Do[	WriteString[resmodelfile, risknames[[riskind[[r]]]] <> gennames[[g]] <> "\n\n"];
							Export[resmodelfile, resrisk[[d, 1, r, g]], "Table"];
							WriteString[resmodelfile, "\n\n"],
							{r, nrd}, {g, ng}],
						{d, nd}];

					WriteString[resmodelfile, "\n\n"];
printbug["9.4.4"];
					Do[	WriteString[resmodelfile, outputnames[[4]] <> str1[d] <> basescenstr <> "\n\n"];
						Do[	WriteString[resmodelfile, disnames[[disind[[d1]]]] <> "\n\n"];				
							Export[resmodelfile, resdis[[d, 1, d1]], "Table"];
							WriteString[resmodelfile, "\n\n"],
							{d1, nd}],
						{d, nd}];

					WriteString[resmodelfile, "\n\n"]];
					
				If[(nscen0 > 1),
printbug["9.4.5"];
					(* DIFFERENCES WITH BASELINE SCENARIO *)

					resrisk	= Table[resrisk[[d, scen]] - resrisk[[d, 1]], {d, nd}, {scen, 2, nscen0}];			
					resdis	= Table[resdis[[d, scen]] - resdis[[d, 1]], {d, nd}, {scen, 2, nscen0}];			

					(* GRAPHICAL OUTPUT IN NOTEBOOK *)

					If[(outputnotebook == 1),
printbug["9.4.6"];	
						Do[addcellnb[plotrisknb[resrisk[[d]], outputnames[[2]] <> str1[d], 1]], {d, nd}];
						Do[addcellnb[plotdisnb[resdis[[d]], outputnames[[4]] <> str1[d], 1]], {d, nd}]];

					(* TABEL OUTPUT IN OUTPUTFILE *)

					If[(outputfile == 1) && (tabeloutput == 1),
printbug["9.4.7"];
						Do[	WriteString[resmodelfile, outputnames[[2]] <> str1[d] <> compscenstr <> "\n\n"];
							Do[	WriteString[resmodelfile, risknames[[riskind[[r]]]] <> gennames[[g]] <>
										"\n\n"];
								Export[resmodelfile, resrisk[[d, scen, r, g]], "Table"];
								WriteString[resmodelfile, "\n\n"],
								{r, nrd}, {scen, nscen0 - 1}, {g, ng}],
							{d, nd}];

						WriteString[resmodelfile, "\n\n"];
printbug["9.4.8"];
						Do[	WriteString[resmodelfile, outputnames[[4]] <> str1[d] <> compscenstr <> "\n\n"];
							Do[	WriteString[resmodelfile, disnames[[disind[[d1]]]] <> "\n\n"];
								Export[resmodelfile, resdis[[d, scen, d1]], "Table"];
								WriteString[resmodelfile, "\n\n"],
								{d1, nd}, {scen, nscen0 - 1}],
							{d, nd}];

						WriteString[resmodelfile, "\n\n"]]
						
					] (* NSCEN0>1*)

				],

			{m, nmodel}];

		Do[	If[(modelsel[[m1]] == 1) && (modelsel[[m2]] == 1) && (MemberQ[{1, 2, 3, 4}, m1]) && (MemberQ[{2, 3, 4}, m2]),
printbug["9.4.9"];
				hres 	= makeresriskdis[m2] - makeresriskdis[m1];
				resrisk	= hres[[1]];
				resdis	= hres[[2]];

				(* RESULTS FOR BASELINE SCENARIO *)

				(* GRAPHICAL OUTPUT IN NOTEBOOK *)

				If[(outputnotebook == 1),
printbug["9.4.10"];
					str = modelnames[[m1]] <> " compared to " <> modelnames[[m2]];
					addcellnb[headingprint1nb[str]];			
	
					Do[addcellnb[plotrisknb[resrisk[[d, {1}]],
								outputnames[[2]] <> " within " <> disnames[[disind[[d]]]], 0]],
						{d, nd}]];

				If[(nscen0 > 1),
printbug["9.4.11"];
					(* DIFFERENCES WITH BASELINE SCENARIO *)

					resrisk = Table[resrisk[[d, scen]] - resrisk[[d, 1]], {d, nd}, {scen, 2, nscen0}];

					(* GRAPHICAL OUTPUT IN NOTEBOOK *)
					
					If[(outputnotebook == 1),
	
						Do[addcellnb[plotrisknb[resrisk[[d]],
									outputnames[[2]] <> " within " <> disnames[[disind[[d]]]], 1]],
							{d, nd}]]					

					] (* NSCEN0>1*)

				],

			{m1, nmodel - 1}, {m2, m1 + 1, nmodel}]
		];
	
	If[(outputnotebook == 1), NotebookWrite[nbout, Flatten[cellnb]]]

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
