(* :Title: CZMSimulationMarginalModelDetermDis *)

(* :Context: CZMsimulation` *)

(* :Author: Rudolf T. Hoogenveen *)

(* :Summary:
   CZM simulation routine describes deterministic CZM marginal model, i.e. equations of change of marginal
   risk factor class and disease prevalence numbers *)

(* :Copyright: © 2004 by Rudolf Hoogenveen *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.0 *)

(* :History: 	3.1 new *)

(* :Keywords: model equations, simulation, patients *)


BeginPackage["CZMSimulation`CZMSimulationMarginalModelDetermDis`",
	{"CZMMain`CZMMain`",
	"CZMInitialization`CZMLogFile`", 
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
	"CZMSimulation`CZMSimulationFunctions`",
	"Statistics`NormalDistribution`",
	"Statistics`DiscreteDistributions`"}] 


resmarginalmodeldetermdis	::usage = "output numbers of deterministic marginal model on disease population, see marginalmodelresults"



Begin["`Private`"]	


Print["CZMSimulationMarginalModelDetermDis package is evaluated"];
printtijd;

printbug[c_] := If[(bugind == 1), Print[{"CZMSimulationMarginalModelDetermDis", c}]];

appltype	= StringTake[ToString[Global`application], {8, 11}];

printbug["1."];

(* INITIALIZE OUTPUT VARIABLES *)

respop = resrisk = resdist = resdis = resinc = resmort = resonsetage = resduurstop = resmortrisk = Table[0., {nscen}, {nstap}];

If[(modelsel[[5]] == 1) && (appltype != "outp"),

resmort 	= Table[0, {nscen}, {nstap}, {nd + 2}];
resonsetage	= Table[0, {nscen}, {nstap}, {nd}, {ng}, {nac[[1]]}];

dDM		= disindinv[[Quotient[patientsel, 10]]];		(* dDM is the order number of disease patientsel *)
RMothriskcurr	= RMothrisksel;


printbug["2."];

(* INITIAL DISTRIBUTION OVER DISEASE STATES *)

(* CALCULATION OF INITIAL PREVALENCE RATES OF OTHER DISEASES CONDITIONAL ON HAVING DISEASE PATIENTSEL *)

(* RISK FACTOR MULTIPLIERS ON POPULATION LEVEL *)

printbug["2.2"];
		
ERRrisk0 	= Table[Plus@@(RRriskseladj[[r, d, g]] prisksel[[r, g]]), {r, nrd}, {d, Length[RRrisksel[[r]]]}, {g, ng}];

RMrisk0		= Table[RRriskseladj[[r, d, g, ri]] / (ERRrisk0[[r, d, g]] + eps),
			{r, nrd}, {d, Length[RRrisksel[[r]]]}, {g, ng}, {ri, ncrsel[[r]]}];

(* CO-MORBIDITY PREVALENCE RATE MULTIPLIERS *)

printbug["2.3"];

RMcomorb	= If[(nrd == 0),

			Table[1., {nd}, {nd}, {ng}, {na1}],

			Table[Times@@Table[Plus@@(	RMrisk0[[r, RRriskindsel[[r, d + 1]], g]] *
							RMrisk0[[r, RRriskindsel[[r, d1 + 1]], g]] *
							prisksel[[r, g]]),
						{r, nrdpop}],
				{d, nd}, {d1, nd}, {g, ng}]];

(* MEAN INDEPENDENT DISEASE RELATIVE RISK VALUES *)

printbug["5.3.2"];

RMdis	= Table[1, {nd}];

Do[If[(dispair[[d, 1]] == dDM),	RMdis[[dispair[[d, 2]]]] *=

	RRdisadj[[RRdisindsel[[dDM, dispair[[d, 2]]]]]] RMcomorb[[dDM, dispair[[d, 2]]]] /
		(1 + (RRdisadj[[RRdisindsel[[dDM, dispair[[d, 2]]]]]] - 1) RMcomorb[[dDM, dispair[[d, 2]]]] pdissel[[dDM]])],

	{d, Length[dispair]}];

Do[If[(dispair[[d, 1]] != dDM),	RMdis[[dispair[[d, 2]]]] *=

	(1 + (RRdisadj[[RRdisindsel[[dispair[[d, 1]], dispair[[d, 2]]]]]] - 1) RMdis[[dispair[[d, 1]]]] pdissel[[dispair[[d, 1]]]]) /
	(1 + (RRdisadj[[RRdisindsel[[dispair[[d, 1]], dispair[[d, 2]]]]]] - 1) *
			RMcomorb[[dispair[[d, 1]], dispair[[d, 2]]]] pdissel[[dispair[[d, 1]]]])],

	{d, Length[dispair]}];


(*-------------------------------------------------
		LOOP OVER SCENARIOS
---------------------------------------------------*)

Do[	(* DEFINES SCENARIO-SPECIFIC INPUT PARAMETER VALUES *)

printbug["5."];

	n		= 0;
	ToExpression[makescen];
	incscen[[dDM]]	*= 0;

(* FORMER SMOKER EVENT RATES DEPEND ON TIME SINCE SMOKING CESSATION *)

printbug["5.2"];

	If[(RRsmokduurind == 1),

		(* FORMER SMOKER RELAPSE RATES DEPEND ON TIME SINCE SMOKING CESSATION *)

		relapserate 	= Table[1 - Exp[ -relapsecoeffscen[[g, 1]] Exp[ -relapsecoeffscen[[g, 2]] 12 duurval ] *
						( 1 - Exp[ -relapsecoeffscen[[g, 2]] 12 ] ) ],
					{g, ng}];

		prisksmok	= Table[0, {ng}, {ncsmok}, {na1}];

		Do[	Do[prisksmok[[g, ri]] 		= priskscen[[1, g, ri]], {ri, 2}];
			Do[prisksmok[[g, 2 + ri]] 	= prisksel[[1, g, 3]] stopduur[[g, ri]], {ri, nstopduur}];
			prisksmok[[g, 2 + 1]] 		+= Max0[priskscen[[1, g, 3]] - prisksel[[1, g, 3]]];
			Do[prisksmok[[g, 2 + Range[nstopduur], a]] -=
				Max0[prisksel[[1, g, 3, a]] - priskscen[[1, g, 3, a]]] stopduur[[g, All, a]], {a, na1}],

			{g, ng}];
	
		RRsmokform	= Table[1 + (RRriskscen[[1, d, g, 2]] - 1) Exp[ -logRRsmokduurscen[[d, g, 1]] *
					Exp[ - logRRsmokduurscen[[d, g, 2]] Max0[Range[na1] - 51] ] duurval[[ri]] ],
					{d, Length[RRrisksel[[1]]]}, {g, ng}, {ri, nstopduur}];
		(* SCALING *)

		If[(RRsmokduurscale == 1), RRsmokform =

			Table[RRsmokform[[d, g, ri]] RRriskscen[[1, d, g, 3]] / Plus@@(RRsmokform[[d, g]] stopduur[[g]]),
					{d, Length[RRrisksel[[1]]]}, {g, ng}, {ri, nstopduur}]];

		Do[	RRriskscen[[1, d, g, 3]] = Plus@@(RRsmokform[[d, g]] stopduur[[g]]), {d, Length[RRrisksel[[1]]]}, {g, ng}];

		];


(*-------------------------------------------------
	CALCULATION OF INITIAL PREVALENCE RATES OF OTHER DISEASES CONDITIONAL ON HAVING DISEASE PATIENTSEL
---------------------------------------------------*)

	(* ADJUSTED EXCESS MORTALITY RATES *)

printbug["5.3.2"];

	excessmortadj 	= Min1[Max0[excessmortseladj + (excessmortscen - excessmortsel)]];

	(* DISEASE PREVALENCE RATES CONDITIONAL ON HAVING DISEASE PATIENTSEL *)

printbug["5.3.3"];

	hpdisDM		= pdissel RMdis;
	hpdisDM[[dDM]]	*= 0;
	incscen[[dDM]] 	*= 0;

	pdisnonDM	= Table[(pdissel[[d]] - hpdisDM[[d]] pdissel[[dDM]]) / (1 - pdissel[[dDM]]), {d, nd}];
	pdisnonDM[[dDM]] *= 0;

	mortnonDM	= Plus@@(excessmortadj pdisnonDM) + mortothsel;


(*-------------------------------------------------
	CALCULATION OF BASELINE DISEASE INCIDENCE RATES CONDITIONAL ON WITH AND WITHOUT DISEASE PATIENTSEL
---------------------------------------------------*)

printbug["5.4"];

	incbase		= Table[RRdisscen[[RRdisindsel[[dDM, d]], g]] incscen[[d, g]] / (1 - pdissel[[d, g]]) /

				(* MEAN RELATIVE RISK FROM DISCRETELY DISTRIBUTED RISK FACTORS *)

				Times@@Table[ERRrisk0[[r, RRriskindsel[[r, d + 1]], g]], {r, nrd}] /

				(* MEAN RELATIVE RISK FROM CO-MORBIDITY *)

				(1 + (RRdisscen[[RRdisindsel[[dDM, d]], g]] - 1) RMcomorb[[dDM, d, g]] pdissel[[dDM, g]]) /

				Times@@Table[1 + (RRdisscen[[RRdisindsel[[d1, d]], g]] - 1) hpdisDM[[d1, g]], {d1, nd}],

				{d, nd}, {g, ng}];


(*-------------------------------------------------
	CALCULATION OF INITIAL RISK FACTOR CLASS PREVALENCE RATES CONDITIONAL ON HAVING DISEASE PATIENTSEL
---------------------------------------------------*)

printbug["5.3.4"];

	priskcurr	= Join[	Table[RMrisk0[[r, RRriskindsel[[r, dDM + 1]], g, ri]] prisksel[[r, g, ri]],
					{r, nrdpop}, {g, ng}, {ri, ncrsel[[r]]}],
				Drop[prisksel, nrdpop]];

	If[(userriskdata >= 1), Do[priskcurr[[DMriskpairsel[[dDM, r1]]]] = priskcurr[[DMriskpairsel[[dDM, r1]]]] RMriskDMinc[[dDM, r1]],
					{r1, Length[DMriskpairsel[[dDM]]]}]];	


(*-------------------------------------------------
	OTHER INITIALISATION STEPS
---------------------------------------------------*)

	(* DISCRETELY DISTRIBUTED RISK FACTOR CLASS TRANSITION RATES *)

printbug["5.6"];

	transrisk	= Table[maketransmarginal[transriskscen[[r]], transriskindsel[[r]], r, ncrsel[[r]], trackingscen], {r, nrd}];

	If[(RRsmokduurind == 1), transrisksmok0 = maketransrisksmok[transrisk, relapserate]];

	casefatadj	= Table[casefatscen[[casefatindsel[[d]]]], {d, nd}];

	(* INITIAL POPULATION NUMBERS, DISEASE PATIENTSEL PREVALENCE NUMBERS AND RISK FACTOR CLASS PREVALENCE NUMBERS *)

printbug["5.8"];

	npop 		= Table[npop0[[g]] agesel1, {g, ng}];

	ndis 		= pdissel[[dDM]] npop;

	If[(scen > 1), Do[priskcurr[[r]] = priskscen[[r]], {r, nrd}]];		

	nriskDM		= Table[priskcurr[[r, g, ri]] pdissel[[dDM, g]] npop[[g]], {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];

	If[(RRsmokduurind == 1),
		nrisksmok = Table[If[(ri < 3),
					nriskDM[[1, g, ri]],
					prisksmok[[g, ri]] / (Plus@@Drop[prisksmok[[g]], 2] + eps) nriskDM[[1, g, 3]]],
					{g, ng}, {ri, 2 + nstopduur}]];

	(* INITIAL SUM OF AGE AT DISEASE ONSET *)

printbug["5.13"];

	onsetage 	= Table[hpdisDM[[d, g]] ndis[[g]] (Range[na1] - .5 - disduur[[d, g]]), {d, nd}, {g, ng}];
	onsetage[[dDM]]	= Table[ndis[[g]] (Range[na1] - .5 - disduur[[dDM, g]]), {g, ng}];
	
		
	(*-------------------------------------------------
			LOOP OVER 1 YEAR TIME STEPS
	---------------------------------------------------*)

	Do[	nbirth 		= makenbirth[n] (1 - .5 morttot1[[All, 1]]);
		migpop 		= makemigpop[n] (1 - .5 morttot1);
	
		(* WGTSUBSET = PROPORTIONAL DISTRIBUTION OF 84+ POPULATION NUMBERS OVER AGE YEAR 84 AND AGE CLASS 85+ *)

printbug["6.1"];

		wgtsubset 	= Table[Take[npop[[g]], -2] / (Plus@@Take[npop[[g]], -2] + eps), {g, ng}];

		(* CURRENT RISK FACTOR CLASS PREVALENCE RATES CONDITIONAL ON HAVING DISEASE PATIENTSEL *)

printbug["6.2"];

		priskcurr	= Table[nriskDM[[r, g, ri]] / (Plus@@nriskDM[[r, g]] + eps), {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];

		(* CURRENT MEAN RELATIVE RISK AND RISK MULTIPLIER VALUES *)
		(* FORMER SMOKER EVENT RATES DEPEND ON TIME SINCE SMOKING CESSATION *)

		If[(RRsmokduurind == 1), Do[RRriskscen[[1, d, g, 3]] =

			Plus@@(RRsmokform[[d, g]] Drop[nrisksmok[[g]], 2]) / (Plus@@Drop[nrisksmok[[g]], 2] + eps),
			{d, Length[RRrisksel[[1]]]}, {g, ng}]];

		(* CURRENT RISK MULTIPLIER RATE VALUES CONDITIONAL ON HAVING DISEASE PATIENTSEL *)

printbug["6.3"];

		ERRrisk		= Table[Plus@@(RRriskscen[[r, d, g]] priskcurr[[r, g]]), {r, nrd}, {d, Length[RRrisksel[[r]]]}, {g, ng}];

		RMrisk		= Table[RRriskscen[[r, d, g, ri]] / (ERRrisk[[r, d, g]] + eps),
					{r, nrd}, {d, Length[RRrisksel[[r]]]}, {g, ng}, {ri, ncrsel[[r]]}];

		(* CO-MORBIDITY PREVALENCE RATE MULTIPLIERS *)

printbug["6.4"];

		RMcomorb	Table[1., {nd}, {nd}, {ng}, {na1}];

		If[(nrd > 0), Do[If[(d != d1) && (d != dDM) && (d1 != dDM), RMcomorb[[d, d1, g]] =

			Times@@Table[Plus@@(	RMrisk[[r, RRriskindsel[[r, d + 1]], g]] *
						RMrisk[[r, RRriskindsel[[r, d1 + 1]], g]] *
						prisksel[[r, g]]),
					{r, nrdpop}]],
				{d, nd}, {d1, nd}, {g, ng}]];

		(* ADJUSTMENT COEFFICIENTS FOR UPDATING OTHER DISEASES PREVALENCE RATES CONDITIONAL ON HAVING DISEASE PATIENTSEL *)

printbug["6.5"];

		covdisdisDM	= RMcomorb;

		Do[	d1	= dispair[[d, 1]];
			d2	= dispair[[d, 2]];

			If[(d1 != dDM) & (d2 != dDM), covdisdisDM[[d1, d2]] *=

				RRdisadj[[RRdisindsel[[d1, d2]]]] /
					(1 + (RRdisadj[[RRdisindsel[[d1, d2]]]] - 1) RMcomorb[[d1, d2]] pdisDM[[d1]])],

			{d, Length[dispair]}];

		covdisdisDM	-= 1;

printbug["6.6.1"];

		If[(RRsmokduurind == 1), Do[RMothriskcurr[[1, g, 3]] =
				Plus@@Drop[RMothsmok[[g]] nrisksmok[[g]], 2] / (Plus@@Drop[nrisksmok[[g]], 2] + eps), {g, ng}]];

printbug["6.6.2"];

		ERMothrisk	= Table[Plus@@(RMothriskcurr[[r, g]] priskcurr[[r, g]]), {r, nrd}, {g, ng}];
		ERMothriskall	= Times@@ERMothrisk;

printbug["6.7"];

		covothdisDM	= Table[If[(nrd == 0),
						1,
						Times@@Table[Plus@@(	RMothriskcurr[[r, g]] *
									RMrisk[[r, RRriskindsel[[r, d + 1]], g]] *
									priskcurr[[r, g]]),
							{r, nrd}] / (ERMothriskall[[g]] + eps)],
					{d, nd}, {g, ng}] - 1;

		(* CURRENT DISEASE INCIDENCE RATE VALUES *)

printbug["6.8"];

		inc	= Table[incbase[[d, g]] (1 - hpdisDM[[d, g]]) *

				(* MEAN RELATIVE RISK FROM DISCRETELY DISTRIBUTED RISK FACTORS *)

				Times@@Table[ERRrisk[[r, RRriskindsel[[r, d + 1]], g]], {r, nrd}] *

				(* ADJUSTMENT RESULTING FROM CO-MORBIDITY THROUGH JOINT RISK FACTORS *)

				Times@@Table[1 + (RRdisscen[[RRdisindsel[[d1, d]], g]] - 1) hpdisDM[[d1, g]], {d1, nd}],

				{d, nd}, {g, ng}];


		(* CURRENT OTHER CAUSES WITHIN PATIENTSEL AND POPULATION ALL CAUSE MORTALITY RATES *)

printbug["6.11"];

		mortothcurr	= ERMothriskall mortothsel;
		pdiscurr	= ndis / (npop + eps);
		mortDM		= (excessmortadj[[dDM]] + Plus@@(excessmortadj hpdisDM)) + mortothcurr;

		(* DISEASE-RELATED MORTALITY RATES *)

printbug["6.12"];

		mortspec	= excessmortadj hpdisDM + casefatadj inc;
		mortspec[[dDM]]	= excessmortadj[[dDM]];
		
		(* NEW DISCRETELY DISTRIBUTED RISK FACTOR CLASS PREVALENCE NUMBERS *)

printbug["6.13"];

		mortrisk = Table[excessmortadj[[dDM, g]] +
					Plus@@Table[If[(d == dDM), 0, mortspec[[d, g]] RMrisk[[r, RRriskindsel[[r, d + 1]], g, ri]]],
							{d, nd}] +
					RMothriskcurr[[r, g, ri]] / (ERMothrisk[[r, g]] + eps) mortothcurr[[g]],
				{r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];

		If[(RRsmokduurind == 1),

			mortsmok	= makemortsmok[nrisksmok, RRsmokform, mortspec, RMothsmok, mortothcurr];

			mortrisk[[1]]	= Table[Join[Take[mortsmok[[g]], 2],
						{Plus@@Drop[mortsmok[[g]] nrisksmok[[g]], 2] / (Plus@@Drop[nrisksmok[[g]], 2] + eps)}],
						{g, ng}]];
						
	
		(*--------------------------------------------------------------------------
		FILL OUTPUT VARIABLES WITH MODEL RESULTS FOR SCENARIO scen AND TIME STEP n 
		---------------------------------------------------------------------------*)

printbug["6.14"];

		respop[[scen, n]] = Table[aggreg[ndis[[g]], 1], {g, ng}];

		resrisk[[scen, n]] = Table[aggreg[nriskDM[[r, g, ri]], 1], {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];

		resinc[[scen, n]] = Table[aggreg[inc[[d, g]] ndis[[g]], 1], {d, nd}, {g, ng}];
		resinc[[scen, n, dDM]] = Table[aggreg[incscen[[dDM, g]] (npop[[g]] - ndis[[g]]), 1], {g, ng}];

		resdis[[scen, n]] = Table[aggreg[hpdisDM[[d, g]] ndis[[g]], 1], {d, nd}, {g, ng}];
		resdis[[scen, n, dDM]] = Table[aggreg[ndis[[g]], 1], {g, ng}];

		Do[	resmort[[scen, n, d]] = Table[aggreg[mortspec[[d, g]] ndis[[g]], 1], {g, ng}], {d, nd}]; 
		resmort[[scen, n, nd + 1]] = Table[aggreg[mortothcurr[[g]] ndis[[g]], 1], {g, ng}];
		resmort[[scen, n, nd + 2]] = Plus@@resmort[[scen, n, Range[nd + 1]]];

		resonsetage[[scen, n]] = Table[aggreg[onsetage[[d, g]], 1], {d, nd}, {g, ng}];

		If[(RRsmokduurind == 1), resduurstop[[scen, n]] = Table[aggreg[Plus@@(Drop[nrisksmok[[g]], 2] duurval), 1], {g, ng}]];
 
		resmortrisk[[scen, n]] = Table[aggreg[mortrisk[[r, g, ri]] nriskDM[[r, g, ri]], 1], {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];

	
		(*---------------------------------------------------------
	 	CALCULATED NEW VALUES OF MODEL STATE VARIABLES, I.E. AT END OF 1-YEAR TIME STEP 
		----------------------------------------------------------*)

		(* NEW SUM OF AGE AT DISEASE ONSET OF PREVALENT CASES *)

printbug["7.0"]; 

		onsetage = Table[subsetn[
				.5 inc[[d, g, 1]] (1 - casefatadj[[d, g, 1]]) *
					(1 - .25 (excessmortscen[[d, g, 1]] + rem1[[remindsel[[d]], g, 1]])) *
					nbirth[[g]] .5,
				(incscen[[dDM, g]] (pdissel[[d, g]] - hpdisDM[[d, g]]) / (pdissel[[dDM, g]] + eps) (npop[[g]] - ndis[[g]]) +
				inc[[d, g]] (1 -  casefatadj[[d, g]]) *
					(1 - .5 (excessmortscen[[d, g]] + rem1[[remindsel[[d]], g]])) npop[[g]]) Range[na1] +
				onsetage[[d, g]] *
					(1 - rem1[[remindsel[[d]], g]] - excessmortscen[[d, g]] (1 - pdissel[[d, g]]) - morttot1[[g]])
				],
				{d, nd}, {g, ng}];

		(* NEW DISEASE PATIENTSEL PREVALENCE NUMBERS *)

printbug["7.1"];

		ndis = Table[subsetn[
				nbirth[[g]] pdissel[[dDM, g, 1]],
				incscen[[dDM, g]] (npop[[g]] - ndis[[g]]) (1 - casefatadj[[dDM, g]]) *
					(1 - .5 (excessmortscen[[dDM, g]] + rem1[[remindsel[[dDM]], g]])) +
				(1 - excessmortadj[[dDM, g]] - rem1[[remindsel[[dDM]], g]] -
					Plus@@Table[If[(d == dDM), 0, mortspec[[d, g]]], {d, nd}] - mortothcurr[[g]]) ndis[[g]]
				],
				{g, ng}];


		(* NEW DISEASE PREVALENCE RATES FOR ALL OTHER DISEASES *)

printbug["7.2"];

		hpdisDM 	= Table[subsetp[
				hpdisDM[[d, g, 1]] +
				.5 inc[[d, g, 1]] (1 - (1 - hpdisDM[[d, g, 1]]) casefatadj[[d, g, 1]]) *
					(1 - .25 (excessmortscen[[d, g, 1]] + rem1[[remindsel[[d]], g, 1]])),
				incscen[[dDM, g]] (pdissel[[d, g]] - hpdisDM[[d, g]]) / (pdissel[[dDM, g]] + eps) +
				inc[[d, g]] (1 - (1 - hpdisDM[[d, g]]) casefatadj[[d, g]]) *
					(1 - .5 (excessmortscen[[d, g]] + rem1[[remindsel[[d]], g]])) +
				hpdisDM[[d, g]] (1 - rem1[[remindsel[[d]], g]] - excessmortadj[[d, g]] (1 - hpdisDM[[d, g]]) -
					Plus@@Table[excessmortadj[[d1, g]] covdisdisDM[[d, d1, g]] hpdisDM[[d1, g]], {d1, nd}] -
					covothdisDM[[d, g]] mortothcurr[[g]]),
				wgtsubset[[g]]
				],
				{d, nd}, {g, ng}];

		hpdisDM[[dDM]]	*= 0;

		(* NEW RISK FACTOR CLASS PREVALENCE NUMBERS *)

printbug["7.3"];

		nriskDM 	= Table[subsetn[
				nbirth[[g]] priskscen[[r, g, ri, 1]] pdissel[[dDM, g, 1]] RMrisk[[r, RRriskindsel[[r, dDM + 1]], g, ri, 1]],
				nriskDM[[r, g, ri]] (1 - mortrisk[[r, g, ri]] - Plus@@transrisk[[r, g, ri]]) +
				Plus@@(transrisk[[r, g, All, ri]] nriskDM[[r, g]])
				],
				{r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];
				
		(* NEW SMOKING CLASS PREVALENCE NUMBERS IN CASE OF DURATION DEPENDENT *)
printbug["7.4"]; 
		If[(RRsmokduurind == 1),

			(* ADJUSTMENT OF SMOKING CLASS TRANSITION RATES FOR MORTALITY *)

			transrisksmok	= transrisksmok0;

			Do[transrisksmok[[g, ri, ri]] 		-= mortsmok[[g, ri]], {g, ng}, {ri, 2}];
			Do[transrisksmok[[g, ri, ri + 1]] 	-= mortsmok[[g, ri]], {g, ng}, {ri, 3, ncsmok - 1}];
			Do[transrisksmok[[g, ri, ri]] 		-= mortsmok[[g, ri]], {g, ng}, {ri, ncsmok, ncsmok}];

			(* SMOKING CLASS PREVALENCE NUMBERS, NEW VALUES *)

			nrisksmok = Table[subsetn[
						nbirth[[g]] If[(ri <= 3), priskcurr[[1, g, ri, 1]] pdissel[[dDM, g, 1]], 0],
						Plus@@(transrisksmok[[g, All, ri]] nrisksmok[[g]])
						],
						{g, ng}, {ri, ncsmok}];

			(* OVERWRITING PREVIOUSLY CALCULATED PREVALENCE NUMBERS *)
printbug["7.5"]; 
			nriskDM[[1]] = Table[Join[Take[nrisksmok[[g]], 2], {Plus@@Drop[nrisksmok[[g]], 2]}], {g, ng}]];

		(* NEW TOTAL POPULATION NUMBERS *)

printbug["7.6"];

		npop 	= Table[subsetn[nbirth[[g]], npop[[g]] (1 - mortDM[[g]] pdiscurr[[g]] - mortnonDM[[g]] (1 - pdiscurr[[g]]))],
				{g, ng}];
		
		(* RE-SCALING NUMBERS BECAUSE OF MIGRATION *)

printbug["7.8"];

		dpop	= 1 + Sign[npop] migpop / (npop + eps);

		ndis	*= dpop;
		npop	*= dpop;

printbug["7.9"];

		Do[nriskDM[[r, g, ri]] *= dpop[[g]], {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}],

		
		(*-------------------------------------------------
			END OF LOOP OVER 1 YEAR TIME STEPS
		---------------------------------------------------*)

	{n, nstap}],


(*-------------------------------------------------
		END OF SCENARIO LOOP
---------------------------------------------------*)

{scen, nscen}]

]; (* IF[(MODELSEL[[5]] == 1) && (APPLTYPE != "outp") *)

resmarginalmodeldetermdis = {respop, resrisk, resdist, resdis, resinc, resmort, resonsetage, resduurstop, resmortrisk};

printinfo;


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
