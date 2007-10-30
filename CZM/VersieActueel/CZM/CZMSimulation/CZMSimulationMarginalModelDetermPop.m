(* :Title: CZMSimulationMarginalModelDetermPop *)

(* :Context: CZMsimulation` *)

(* :Author: Rudolf T. Hoogenveen *)

(* :Summary:
   CZM simulation routine describes deterministic CZM marginal model, i.e. equations of change of marginal
   risk factor class and disease prevalence numbers *)

(* :Copyright: © 2004 by Rudolf Hoogenveen *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March, effect of time since smoking cessation
		3.0 version November 2005 
		3.1 version March 2007; storage of results *)

(* :Keywords: model equations, simulation *)


BeginPackage["CZMSimulation`CZMSimulationMarginalModelDetermPop`",
	{"CZMInitialization`CZMLogFile`", 
	 "CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`",
	"CZMImportData`CZMImportUserSelections`",
	"CZMImportData`CZMImportDemography`",
	"CZMImportData`CZMImportRiskFactors`",
	"CZMImportData`CZMImportRelativeRisks`",
	"CZMAdjustData`CZMDataSmoothing`",
	"CZMAdjustData`CZMMakeSelections`",
	"CZMAdjustData`CZMAdjustBeforeSelection`",
	"CZMAdjustData`CZMMakeSelections`",
	"CZMAdjustData`CZMAdjustAfterSelection`",
	"CZMDefineScenarios`CZMDefineScenarios`",
	"CZMSimulation`CZMSimulationFunctions`"}] 


resmarginalmodeldetermpop::usage 	= "output numbers of deterministic marginal model, see marginalmodelresults"


Begin["`Private`"]	


Print["CZMSimulationMarginalModelDetermPop package is evaluated"];
printtijd;

printbug[c_] := If[(bugind == 1), Print[{"CZMSimulationMarginalModelDetermPop", c}]];

appltype	= StringTake[ToString[Global`application], {8, 11}];

respop = resrisk = resdist = resdis = resinc = resmort = resonsetage = resduurstop = resmortrisk = Table[0., {nscen}, {nstap}];

If[(modelsel[[1]] == 1) && (appltype != "outp"),

(* INITIALIZE OUTPUT VARIABLES *)

printbug["1."];

respop 		= Table[0., {nscen}, {nstap}, {ng}, {nac[[1]]}];
resrisk 	= Table[0., {nscen}, {nstap}, {r, nrd}, {ng}, {ncrsel[[r]]}, {nac[[1]]}];
resdis 		= Table[0., {nscen}, {nstap}, {nd}, {ng}, {nac[[1]]}]; 
resinc		= Table[0., {nscen}, {nstap}, {nd}, {ng}, {nac[[1]]}]; 
resmort 	= Table[0., {nscen}, {nstap}, {nd + 2}, {ng}, {nac[[1]]}]; 
resonsetage	= Table[0., {nscen}, {nstap}, {nd}, {ng}, {nac[[1]]}];
If[(riskcontind == 1) && (nrc > 0),
	resdist		= Table[0., {nscen}, {nstap}, {nrc}, {ng}, {2}, {nac[[1]]}]];

(* INITIALIZE MODEL VARIABLES *)

printbug["2."];

If[(RRsmokduurind == 1),

	n		= 0;
	scen		= 1;
	ToExpression[makescen];
	
	(* CHARACTERISTICS OF DISTRIBUTIN OF TIME SINCE SMOKING CESSATION *)

printbug["2.1"];	

	resduurstop	= Table[0, {nscen}, {nstap}, {ng}, {nac[[1]]}];		(* OUTPUT VARIABLE *)

(* Delete testrun17 151106 Jack
printbug["2.2"];

	ncsmok		= ncrsel[[1]] + nstopduur - 1;				(* # SMOKING CLASSES *)

printbug["2.3"];

	duurval		= Range[nstopduur] - .5;				(* MEAN TIME SINCE SMOKING CESSATION *)
End of delete testrun17 151106 Jack *)

	(* OTHER CAUSES SMOKING RISKS DEPEND ON TIME SINCE SMOKING CESSATION *)

printbug["2.4"];

	hRMothsmok	= Table[RMothrisksel[[1, g, ri]] / RMothrisksel[[1, g, 1]], {g, ng}, {ri, 3}];

printbug["2.5"];

	RMsmokformoth	= Table[1 + (hRMothsmok[[g, 2]] - 1) Exp[ -logRRsmokduuroth[[g, 1]] *
					Exp[ - logRRsmokduuroth[[g, 2]] Max0[Range[na1] - 51] ] duurval[[ri]] ],
				{g, ng}, {ri, nstopduur}];

printbug["2.6"];

	ERRothsmok	= Table[Plus@@(hRMothsmok[[g, Range[2]]] prisksel[[1, g, Range[2]]]) +
				Plus@@(RMsmokformoth[[g]] stopduur[[g]]) prisksel[[1, g, 3]],
				{g, ng}];

printbug["2.7"];

	Do[hRMothsmok[[g]] = Join[	Table[hRMothsmok[[g, ri]] / ERRothsmok[[g]], {ri, 2}],
					Table[RMsmokformoth[[g, ri]] / ERRothsmok[[g]], {ri, nstopduur}]],
				{g, ng}],

	(* THEY DO NOT *)

printbug["2.9"];

	resduurstop	= Table[0., {nscen}, {nstap}]				(* OUTPUT VARIABLE *)
	];

(* CALCULATION OF SMOKING CLASS TRANSITION RATES ACCORDING TO GIVEN SCENARIO scen AND FOR GIVEN TIME n *)

printbug["3."];

hmaketransrisksmok[] := Block[{},
	
	htransrisksmok = Table[0, {ng}, {ncsmok}, {ncsmok}, {na1}];
	
	Do[	(* SMOKING CLASS TRANSITION RATES *)

		Do[htransrisksmok[[g, ri, ri]] 	= vect11, {ri, 2}];
		htransrisksmok[[g, ncsmok, ncsmok]] 	= vect11;
		Do[htransrisksmok[[g, ri, ri + 1]] 	= vect11, {ri, 3, ncsmok - 1}];

		(* TRANSITIONS BETWEEN CLASSES *)			
			
		htransrisksmok[[g, 1, {1, 2, 3}]] +=
					{-Plus@@transrisk[[1, g, 1, {2, 3}]], transrisk[[1, g, 1, 2]], transrisk[[1, g, 1, 3]]};

		htransrisksmok[[g, 2, {2, 3}]] += {-transrisk[[1, g, 2, 3]], transrisk[[1, g, 2, 3]]};

		htransrisksmok[[g, ncsmok, {2, ncsmok}]] += {relapserate[[g, nstopduur]], -relapserate[[g, nstopduur]]};
			
		Do[htransrisksmok[[g, 2 + ri, {2, 3 + ri}]] += {relapserate[[g, ri]], -relapserate[[g, ri]]}, {ri, nstopduur - 1}],

		{g, ng}];

	htransrisksmok

	];

(* CALCULATION OF SMOKING CLASS MORTALITY RATES FOR GIVEN TIME n *)

printbug["4."];

hmakemortsmok[] := Block[{},

	mortsmok = Table[0, {ng}, {ncsmok}, {na1}];	

	(* DISEASE-RELATED EXCESS MORTALITY RATES *)

	ERRsmok = Table[Plus@@Table[nrisksmok[[g, ri]] RRriskscen[[1, RRriskindsel[[1, d + 1]], g, ri]], {ri, 2}] +
				Plus@@Table[nrisksmok[[g, 2 + ri]] RRsmokform[[RRriskindsel[[1, d + 1]], g, ri]], {ri, nstopduur}],
				{d, nd}, {g, ng}];

	Do[mortsmok[[g, ri]] = Plus@@Table[mortspec[[d, g]] nrisksmok[[g, ri]] RRriskscen[[1, RRriskindsel[[1, d + 1]], g, ri]] /
						(ERRsmok[[d, g]] + eps), {d, nd}],
				{g, ng}, {ri, 2}];

	Do[mortsmok[[g, 2 + ri]] = Plus@@Table[mortspec[[d, g]] nrisksmok[[g, 2 + ri]] RRsmokform[[RRriskindsel[[1, d + 1]], g, ri]] /
						(ERRsmok[[d, g]] + eps), {d, nd}],
				{g, ng}, {ri, nstopduur}];

	(* OTHER CAUSES MORTALITY RATES *)

	Do[mortsmok[[g, ri]] += hRMothsmok[[g, ri]] mortothsel[[g]], {g, ng}, {ri, ncsmok}];

	mortsmok

	];


(*-------------------------------------------------
		LOOP OVER SCENARIOS
---------------------------------------------------*)

Do[
	(* DEFINES SCENARIO-SPECIFIC INPUT PARAMETER VALUES *)

printbug["5."];

	n		= 0;
	ToExpression[makescen];
	RMothrisk	= RMothrisksel;

	(* FORMER SMOKER EVENT RATES DEPEND ON TIME SINCE SMOKING CESSATION *)

printbug["5.2"];

	If[(RRsmokduurind == 1),

		(* FORMER SMOKER RELAPSE RATES DEPEND ON TIME SINCE SMOKING CESSATION *)

		relapserate 	= Table[1 - Exp[ -relapsecoeffscen[[g, 1]] Exp[ -relapsecoeffscen[[g, 2]] 12 duurval ] *
						( 1 - Exp[ -relapsecoeffscen[[g, 2]] 12 ] ) ],
					{g, ng}];

		nrisksmok	= Table[0, {ng}, {ncsmok}, {na1}];

		Do[	Do[nrisksmok[[g, ri]] 		= priskscen[[1, g, ri]] npop0[[g]], {ri, 2}];
			Do[nrisksmok[[g, 2 + ri]] 	= prisksel[[1, g, 3]] stopduur[[g, ri]] npop0[[g]], {ri, nstopduur}];
			nrisksmok[[g, 2 + 1]] 		+= (priskscen[[1, g, 3]] - prisksel[[1, g, 3]]) npop0[[g]],

			{g, ng}];
	
		RRsmokform	= Table[1 + (RRriskscen[[1, d, g, 2]] - 1) Exp[ -logRRsmokduurscen[[d, g, 1]] *
					Exp[ - logRRsmokduurscen[[d, g, 2]] Max0[Range[na1] - 51] ] duurval[[ri]] ],
				{d, Length[RRriskscen[[1]]]}, {g, ng}, {ri, nstopduur}];

		Do[RRriskscen[[1, d, g, 3]] = Plus@@(RRsmokform[[d, g]] Drop[nrisksmok[[g]], 2]) / (Plus@@Drop[nrisksmok[[g]], 2] + eps),
				{d, Length[RRriskscen[[1]]]}, {g, ng}]

		];

	(* CURRENT MEAN RELATIVE RISK AND RISK MULTIPLIER VALUES *)

printbug["5.3"];
		
	ERRrisk 	= Table[Plus@@(RRriskscen[[r, d, g]] prisksel[[r, g]]),
				{r, nrd}, {d, Length[RRriskscen[[r]]]}, {g, ng}];

	(* BASELINE DISEASE INCIDENCE RATES *)

printbug["5.4"];

	incbase 	= Table[incscen[[d]] / (1 - pdissel[[d]]) /
				Times@@Table[ERRrisk[[r, RRriskindsel[[r, d + 1]]]], {r, nrdpop}] / 
				Times@@Table[1 + (RRdisscen[[RRdisindsel[[d1, d]]]] - 1) pdissel[[d1]], {d1, nd}],
				{d, nd}];

	(* ADJUSTED EXCESS MORTALITY RATES *)

printbug["5.5"];

	excessmortadj 	= Min1[Max0[excessmortseladj + (excessmortscen - excessmortsel)]];

	(* DISCRETELY DISTRIBUTED RISK FACTOR CLASS TRANSITION RATES *)

printbug["5.6"];

	transrisk	= Table[maketransmarginal[transriskscen[[r]], transriskindsel[[r]], r, ncrsel[[r]], trackingscen], {r, nrd}];
	
	If[(RRsmokduurind == 1), transrisksmok0 = hmaketransrisksmok[]];

	(* INITIAL POPULATION NUMBERS *)

printbug["5.7"];

	npop 		= Table[npop0[[g]] agesel1, {g, ng}];

	(* INITIAL DISCRETE RISK FACTOR CLASS PREVALENCE NUMBERS *)

printbug["5.8"];

	nrisk		= Join[	Table[priskscen[[r, g, ri]] npop[[g]], {r, nrdpop}, {g, ng}, {ri, ncrsel[[r]]}],

				Table[priskscen[[nrdpop + r, g, ri]] pdissel[[disindinv[[disriskindddis[[r]]]], g]] npop[[g]],
					{r, nrddis}, {g, ng}, {ri, ncrsel[[nrdpop + r]]}]];

	If[(RRsmokduurind == 1),

printbug["5.9"];

		nrisksmok = Table[nrisksmok[[g, ri]] agesel1, {g, ng}, {ri, 2 + nstopduur}]];

	(* INITIAL CLASS PREVALENCE NUMBERS FOR DISCRETELY DISTRIUTED RISK FACTORS RESTRICTED TO DISEASED PERSONS *)

printbug["5.10"];

	hprisk 		= Table[priskscen[[nrdpop + r, g, ri]] /
					RRriskscen[[nrdpop + r, RRriskindsel[[nrdpop + r, disindinv[[disriskindddis[[r]]]] + 1]], g, ri]],
				{r, nrddis}, {g, ng}, {ri, ncrsel[[nrdpop + r]]}];

        hprisk 		= Table[hprisk[[r, g, ri]] / Plus@@hprisk[[r, g]], {r, nrddis}, {g, ng}, {ri, ncrsel[[nrdpop + r]]}];

	prisknondis 	= Table[(hprisk[[r, g, ri]] - pdissel[[disindinv[[disriskindddis[[r]]]], g]] priskscen[[nrdpop + r, g, ri]]) /
					(1 - pdissel[[disindinv[[disriskindddis[[r]]]], g]]),
				{r, nrddis}, {g, ng}, {ri, ncrsel[[nrdpop + r]]}];

	(* INITIAL CONTINUOUSLY DISTRIBUTED RISK FACTOR DISTRIBUTION CHARACTERISTICS *)

	(* INITIAL  DISEASE PREVALENCE RATES *)

printbug["5.12"];

	pdis 		= pdissel;

	(* INITIAL SUM OF AGE AT DISEASE ONSET *)

printbug["5.13"];

	onsetage 	= Table[pdis[[d, g]] npop[[g]] (Range[na1] - .5 - disduur[[d, g]]), {d, nd}, {g, ng}];


	(*-------------------------------------------------
			LOOP OVER 1 YEAR TIME STEPS
	---------------------------------------------------*)

	Do[
		nbirth 		= makenbirth[n] (1 - .5 morttot1[[Range[ng], 1]]);
		
		migpop 		= makemigpop[n] (1 - .5 morttot1);
	
		(* WGTSUBSET = PROPORTIONAL DISTRIBUTION OF 84+ POPULATION NUMBERS OVER AGE YEAR 84 AND AGE CLASS 85+ *)
		
		wgtsubset 	= Table[Take[npop[[g]], -2] / (Plus@@Take[npop[[g]], -2] + eps), {g, ng}];

		(* CURRENT DISCRETE & CONTINUOUS RISK FACTOR CLASS PREVALENCE RATES *)

		priskcurr	= Table[nrisk[[r, g, ri]] / (Plus@@nrisk[[r, g]] + eps), {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];

		
		(* CURRENT MEAN RELATIVE RISK AND RISK MULTIPLIER VALUES *)
		(* FORMER SMOKER EVENT RATES DEPEND ON TIME SINCE SMOKING CESSATION *)

		If[(RRsmokduurind == 1),

			Do[RRriskscen[[1, d, g, 3]] =
				Plus@@(RRsmokform[[d, g]] Drop[nrisksmok[[g]], 2]) / (Plus@@Drop[nrisksmok[[g]], 2] + eps),
				{d, Length[RRriskscen[[1]]]}, {g, ng}]];

		(* DISCRETELY DISTRIBUTED RISK FACTORS *)

		ERRrisk		= Table[Plus@@(RRriskscen[[r, d, g]] priskcurr[[r, g]]), {r, nrd}, {d, Length[RRriskscen[[r]]]}, {g, ng}];

		RMrisk		= Table[RRriskscen[[r, d, g, ri]] / (ERRrisk[[r, d, g]] + eps),
					{r, nrd}, {d, Length[RRriskscen[[r]]]}, {g, ng}, {ri, ncrsel[[r]]}];

		(* CONTINUOUSLY DISTRIBUTED RISK FACTORS *)

		(* DISCRETELY DISTRIBUTED RISK FACTORS RESTRICTED TO DISEASES, E.G. HBA1C *)

		ERMriskdis	= Table[Plus@@(RRriskscen[[r, RRriskindsel[[r, d + 1]], g]] priskcurr[[r, g]]) /
						(Plus@@(RRriskscen[[r, RRriskindsel[[r, d + 1]], g]] prisksel[[r, g]]) + eps),
					{r, nrdpop + 1, nrdpop + nrddis}, {d, nd}, {g, ng}];

		(* CURRENT DISEASE INCIDENCE RATES *)

		inc 		= Table[incbase[[d]] (1 - pdis[[d]]) *

					(* MEAN RELATIVE RISK FROM DISCRETELY DISTRIBUTED RISK FACTORS *)

					Times@@Table[ERRrisk[[r, RRriskindsel[[r, d + 1]]]], {r, nrdpop}] *

					(* MEAN RELATIVE RISK FROM CO-MORBIDITY *)

					Times@@Table[1 + (RRdisscen[[RRdisindsel[[d1, d]]]] *

						(* ADJUSTMENT RESULTING FROM DISEASE-RESTRICTED RISK FACTORS *)

						Times@@Table[If[(disriskindddis[[r]] == disind[[d1]]),
									ERMriskdis[[r, d]],
									1],
								{r, nrddis}] - 1) pdis[[d1]],

						{d1, nd}],

					{d, nd}];

		(* CASE FATALITY RATES ADJUSTED FOR CO-MORBIDITY *)

		casefatadj	= Table[casefatscen[[casefatindsel[[d]]]] *

					Times@@Table[1 + (RRcasefatscen[[RRcasefatindsel[[d1, d]]]] - 1) pdis[[d1]], {d1, nd}] /

					Times@@Table[1 + (RRcasefatscen[[RRcasefatindsel[[d1, d]]]] - 1) pdissel[[d1]], {d1, nd}],

					{d, nd}];

		(* CURRENT DISEASE MORTALITY RATES *)

		mortspec 	= excessmortadj pdis + inc (casefatadj + (1 - casefatadj) .5 excessmortadj);

		(* CURRENT OTHER CAUSES MORTALITY RATE MULTIPLIERS *)

		mortoth	= Table[Times@@Table[Plus@@(RMothrisksel[[r, g]] priskcurr[[r, g]]), {r, 2, nrd}] *
					mortothsel[[g]],
					{g, ng}];

		(* OTHER CAUSES MORTALITY RISKS FOR 1ST RISK FACTOR, I.E. SMOKERS IN CASE OF DURATION DEPENDENCY *)

		If[(RRsmokduurind == 1),

			mortoth	*= Table[Plus@@(hRMothsmok[[g]] nrisksmok[[g]]) / (Plus@@nrisksmok[[g]] + eps), {g, ng}]];

		(* OTHER CAUSES MORTALITY RISKS FOR 1ST RISK FACTOR IN ALL OTHER CASES *)

		If[(RRsmokduurind == 0) && (nrd > 0),

			mortoth	*= Table[Plus@@(RMothrisksel[[1, g]] priskcurr[[1, g]]), {g, ng}]];

		(* CURRENT ALL CAUSE MORTALITY RATES *)

		morttot 	= Table[mortoth[[g]] + Plus@@Table[mortspec[[d, g]], {d, nd}], {g, ng}];

		(* NEW DISCRETELY DISTRIBUTED RISK FACTOR CLASS PREVALENCE NUMBERS *)

		mortrisk = Join[Table[	Plus@@Table[mortspec[[d, g]] RMrisk[[r, RRriskindsel[[r, d + 1]], g, ri]], {d, nd}] +
					RMothrisksel[[r, g, ri]] mortoth[[g]] / (Plus@@(RMothrisksel[[r, g]] priskcurr[[r, g]]) + eps),
					{r, nrdpop}, {g, ng}, {ri, ncrsel[[r]]}],

				Table[	RMrisk[[nrdpop + r, RRriskindsel[[nrdpop + r, disindinv[[disriskindddis[[r]]]] + 1]], g, ri]] *
						excessmortadj[[disindinv[[disriskindddis[[r]]]], g]] *
						(1 - pdis[[disindinv[[disriskindddis[[r]]]], g]]),
					{r, nrddis}, {g, ng}, {ri, ncrsel[[nrdpop + r]]}]];

		
		(*--------------------------------------------------------------------------
		FILL OUTPUT VARIABLES WITH MODEL RESULTS FOR SCENARIO scen AND TIME STEP n 
		---------------------------------------------------------------------------*)

		respop[[scen, n]] = Table[aggreg[npop[[g]], 1], {g, ng}] +
					Table[Ceiling[.2 lengthageclass[[1, Range[nac[[1]]]]]] eps, {ng}];

		resrisk[[scen, n]] = Table[aggreg[nrisk[[r, g, ri]], 1], {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];

		resinc[[scen, n]] = Table[aggreg[inc[[d, g]] npop[[g]], 1], {d, nd}, {g, ng}];

		resdis[[scen, n]] = Table[aggreg[pdis[[d, g]] npop[[g]], 1], {d, nd}, {g, ng}];

		resmort[[scen, n, Range[nd]]] = Table[aggreg[mortspec[[d, g]] npop[[g]], 1], {d, nd}, {g, ng}];

		resmort[[scen, n, nd + 1]] = Table[aggreg[mortoth[[g]] npop[[g]], 1], {g, ng}];

		resmort[[scen, n, nd + 2]]= Plus@@resmort[[scen, n, Range[nd + 1]]];

		resonsetage[[scen, n]] = Table[aggreg[onsetage[[d, g]], 1], {d, nd}, {g, ng}];

		If[(RRsmokduurind == 1),
			resduurstop[[scen, n]] = Table[aggreg[Plus@@(nrisksmok[[g, 2 + Range[nstopduur]]] duurval), 1],	{g, ng}]];

		If[(riskcontind == 1) && (nrc > 0),
			resdist[[scen, n]] = Table[{aggreg[mucurr[[r, g]] npop[[g]], 1], aggreg[varcurr[[r, g]] npop[[g]], 1]},
					{r, nrc}, {g, ng}]];

		resmortrisk[[scen, n]] = Table[aggreg[mortrisk[[r, g, ri]] nrisk[[r, g, ri]], 1], {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];

			
		(*---------------------------------------------------------
	 	CALCULATED NEW VALUES OF MODEL STATE VARIABLES, I.E. AT END OF 1-YEAR TIME STEP 
		----------------------------------------------------------*)

		(* NEW DISEASE PREVALENCE RATES *)

		pdis 	= Table[subsetp[
				pdis[[d, g, 1]] +
				.5 inc[[d, g, 1]] (1 - (1 - pdis[[d, g, 1]]) casefatadj[[d, g, 1]]) *
					(1 - .25 (excessmortscen[[d, g, 1]] + rem1[[remindsel[[d]], g, 1]])),
				inc[[d, g]] (1 - (1 - pdis[[d, g]]) casefatadj[[d, g]]) *
					(1 - .5 (excessmortscen[[d, g]] + rem1[[remindsel[[d]], g]])) +
				pdis[[d, g]] (1 - rem1[[remindsel[[d]], g]] - excessmortscen[[d, g]] (1 - pdis[[d, g]])),
				wgtsubset[[g]]
				],
				{d, nd}, {g, ng}];

		(* NEW SUM OF AGE AT DISEASE ONSET OF PREVALENT CASES *)

		onsetage = Table[subsetn[
				.5 inc[[d, g, 1]] (1 - casefatadj[[d, g, 1]]) *
					(1 - .25 (excessmortscen[[d, g, 1]] + rem1[[remindsel[[d]], g, 1]])) *
					nbirth[[g]] .5,
				inc[[d, g]] (1 -  casefatadj[[d, g]]) *
					(1 - .5 (excessmortscen[[d, g]] + rem1[[remindsel[[d]], g]])) npop[[g]] Range[na1] +
				onsetage[[d, g]] *
					(1 - rem1[[remindsel[[d]], g]] - excessmortscen[[d, g]] (1 - pdissel[[d, g]]) - morttot1[[g]])
				],
				{d, nd}, {g, ng}];

		(* NEW TOTAL POPULATION NUMBERS *)

		npop 	= Table[subsetn[nbirth[[g]], npop[[g]] (1 - morttot[[g]]) ], {g, ng}];
		
		(* RE-SCALING NUMBERS BECAUSE OF MIGRATION *)

		onsetage = Table[(npop[[g]] + migpop[[g]]) / (npop[[g]] + eps) onsetage[[d, g]], {d, nd}, {g, ng}];

		npop	+= migpop;

		(* SCENARIO-SPECIFIC TRANSITION RATES *)

		ToExpression[makescen];
	
		transrisk = Table[maketransmarginal[transriskscen[[r]], transriskindsel[[r]], r, ncrsel[[r]], trackingscen], {r, nrd}];
	
		If[(RRsmokduurind == 1), transrisksmok0 = hmaketransrisksmok[]];

		(* NEW RISK FACTOR CLASS PREVALENCE NUMBERS *)

		nrisk 	= Join[	Table[subsetn[
					nbirth[[g]] priskscen[[r, g, ri, 1]],
					nrisk[[r, g, ri]] (1 - mortrisk[[r, g, ri]] - Plus@@transrisk[[r, g, ri]]) +
						Plus@@(transrisk[[r, g, Range[ncrsel[[r]]], ri]] nrisk[[r, g]])
					],
					{r, nrdpop}, {g, ng}, {ri, ncrsel[[r]]}],

				Table[subsetn[
					nbirth[[g]] priskscen[[r, g, ri, 1]] *
					(pdis[[disindinv[[disriskindddis[[r - nrdpop]]]], g, 1]] + 
					.5 inc[[disindinv[[disriskindddis[[r - nrdpop]]]], g, 1]]),
					nrisk[[r, g, ri]] (1 - mortrisk[[r, g, ri]] - Plus@@transrisk[[r, g, ri]]) +
					Plus@@(transrisk[[r, g, All, ri]] nrisk[[r, g]]) +
					inc[[disindinv[[disriskindddis[[r - nrdpop]]]], g]] npop[[g]] priskincsel[[r - nrdpop, g, ri]]
					],
					{r, nrdpop + 1, nrdpop + nrddis}, {g, ng}, {ri, ncrsel[[r]]}]];
				
		(* NEW CONTINUOUSLY DISTRIBUTED RISK FACTOR DISTRIBUTION CHARACTERISTICS *)

		(* NEW SMOKING CLASS PREVALENCE NUMBERS IN CASE OF DURATION DEPENDENT *)

		If[(RRsmokduurind == 1),

			(* ADJUSTMENT OF SMOKING CLASS TRANSITION RATES FOR MORTALITY *)

			mortsmok = hmakemortsmok[];

			transrisksmok = transrisksmok0;

			Do[transrisksmok[[g, ri, ri]] -= mortsmok[[g, ri]], {g, ng}, {ri, ncsmok}];

			(* SMOKING CLASS PREVALENCE NUMBERS, NEW VALUES *)

			nrisksmok = Table[subsetn[
						nbirth[[g]]*
							If[(ri <= 2),
								priskscen[[1, g, ri, 1]],
								priskscen[[1, g, 3, 1]] stopduur[[g, ri - 2, 1]]],
						Plus@@(transrisksmok[[g, Range[ncsmok], ri]] nrisksmok[[g]])
						],
						{g, ng}, {ri, ncsmok}];

			(* OVERWRITING PREVIOUSLY CALCULATED PREVALENCE NUMBERS *)

			nrisk[[1]] = Table[Join[nrisksmok[[g, Range[2]]], {Plus@@nrisksmok[[g, 2 + Range[nstopduur]]]}], {g, ng}]

			];

		(* RE-SCALING DISCRETE RISK FACTOR CLASS PREVALENCE NUMBERS BECAUSE OF MIGRATION *)

		sumnrisk = Table[Plus@@nrisk[[r, g]], {r, nrd}, {g, ng}];

		nrisk = Join[	Table[nrisk[[r, g, ri]] npop[[g]] / (sumnrisk[[r, g]] + eps), {r, nrdpop}, {g, ng}, {ri, ncrsel[[r]]}],

				Table[nrisk[[nrdpop + r, g, ri]] pdis[[disindinv[[disriskindddis[[r]]]], g]] npop[[g]] /
					(sumnrisk[[nrdpop + r, g]] + eps),
					{r, nrddis}, {g, ng}, {ri, ncrsel[[nrdpop + r]]}]
				];

		If[(RRsmokduurind == 1),

			nrisksmok = Table[nrisksmok[[g, ri]] npop[[g]] / (sumnrisk[[1, g]] + eps), {g, ng}, {ri, ncsmok}]],


		(*-------------------------------------------------
			END OF LOOP OVER 1 YEAR TIME STEPS
		---------------------------------------------------*)

	{n, nstap}],


(*-------------------------------------------------
		END OF SCENARIO LOOP
---------------------------------------------------*)

{scen, nscen}]];

resmarginalmodeldetermpop = {respop, resrisk, resdist, resdis, resinc, resmort, resonsetage, resduurstop, resmortrisk};

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
