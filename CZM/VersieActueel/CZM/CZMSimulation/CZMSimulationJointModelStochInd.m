(* :Title: CZMSimulationJointModelStochInd *)

(* :Context: CZMSimulation` *)

(* :Author: Rudolf T. Hoogenveen *)

(* :Summary:
   CZM simulation routine describes stochastic joint CZM model sratified by individuals, i.e. random change of risk
   factor class and disease state for successive individuals *)

(* :Copyright: © 2004 by Rudolf Hoogenveen *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM November 2004
		2.0 first release CZM 2005, version March, effect of time since smoking cessation 
		3.1 version March 2007; storage of results *)

(* :Keywords: joint model, model parameters *)


BeginPackage["CZMSimulation`CZMSimulationJointModelStochInd`",
	{"CZMInitialization`CZMLogFile`", 
	"CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`",
	"CZMImportData`CZMImportUserSelections`",
	"CZMImportData`CZMImportDemography`",
	"CZMImportData`CZMImportRiskFactors`",
	"CZMImportData`CZMImportRelativeRisks`",
	"CZMAdjustData`CZMDataSmoothing`",
	"CZMAdjustData`CZMAdjustBeforeSelection`",
	"CZMAdjustData`CZMMakeSelections`",
	"CZMAdjustData`CZMAdjustAfterSelection`",
	"CZMDefineScenarios`CZMDefineScenarios`",
	"CZMSimulation`CZMSimulationFunctions`",
	"CZMPostProcessing`CZMExportUserSelections`",
	"Statistics`DiscreteDistributions`"}] 


resjointmodelstochind::usage	= "output numbers of joint model stratified by individuals, see marginalmodelresults"
ndraw::usage			= "sample size of stochastic joint model stratified by individuals"
jointmodeldetermindprev::usage	= "MMA data file, field 1: dimension of:, field 2: data, see jointmodelresults"


Begin["`Private`"]	


Print["CZMSimulationJointModelStochInd package is evaluated"];
printtijd;

printbug[c_] := If[(bugind == 1), Print[{"CZMSimulationJointModelStochInd", c}]];

appltype	= StringTake[ToString[Global`application], {8, 11}];

respop = resrisk = resdist = resdis = resinc = resmort = resonsetage = resduurstop = resmortrisk = Table[0., {nscen}, {nstap}];

If[(modelsel[[4]] == 1) && (appltype != "outp"),

(* INITIALIZE OUTPUT VARIABLES *)

ndraw		= 10000;

(* INIDIVUAL RISK FACTOR CLASS STATES, INITIAL DISEASE STATES, TIMES AT DISEASE ONSET, AND TIMES OF MORTALITY *)

respop		= Table[.0, {nscen}, {nstap}, {ng}, {Length[agesel]}];
resrisk 	= Table[.0, {nscen}, {nstap}, {r, nrd}, {ng}, {ncrsel[[r]]}, {Length[agesel]}];
resdis		= Table[.0, {nscen}, {nstap}, {nd}, {ng}, {Length[agesel]}];
resinc		= Table[.0, {nscen}, {nstap}, {nd}, {ng}, {Length[agesel]}];
resmort		= Table[.0, {nscen}, {nstap}, {nd + 2}, {ng}, {Length[agesel]}]; 
resonsetage	= Table[.0, {nscen}, {nstap}, {nd}, {ng}, {Length[agesel]}];
resmortrisk 	= Table[.0, {nscen}, {nstap}, {r, nrd}, {ng}, {ncrsel[[r]]}, {Length[agesel]}];

printbug["1."];

If[(RRsmokduurind == 1),

	resduurstop	= Table[.0, {nscen}, {nstap}, {ng}, {Length[agesel]}];

	(* MEAN DURATION SINCE SMOKING CESSATION SPECIFIED BY AGE *)

	meanstopduur	= Table[Plus@@(duurval stopduur[[g, Range[nstopduur], a]]), {g, ng}, {a, na1}];

	RRsmokformoth = Table[1 + (RMothrisksel[[1, g, 2]] / RMothrisksel[[1, g, 1]] - 1) Exp[-logRRsmokduuroth[[g, 1]] *
					Exp[-logRRsmokduuroth[[g, 2]] Max0[Range[na1] - 51] ] meanstopduur[[g]] ],
				{g, ng}],

	(*JACK 240506resduurstop	= .0;*)resduurstop	= Table[0., {nscen}, {nstap}];

	];

(* JOINT STATE PREVALENCE NUMBERS MADE AVAILABLE *)

outfile		= OpenWrite[Global`outputpath <> "jointmodelstochindprev.m"];
Put[{nscen, Length[agesel], nstap, nz1, agemin, ndraw, riskind, disind, nscen0}, outfile]; 

(*{nscen, Length[agesel], nstap, nz1, agemin, ndraw, riskind, disind}*)


(* INITIAL CLASS PREVALENCE RATES *)

printbug["2."];

ERRrisk		= Table[Plus@@(RRriskseladj[[r, d, g]] prisksel[[r, g]]), {r, nrd}, {d, Length[RRriskseladj[[r]]]}, {g, ng}];

RMrisk		= Table[RRriskseladj[[r, d, g, ri]] / (ERRrisk[[r, d, g]] + eps),
			{r, nrd}, {d, Length[RRriskseladj[[r]]]}, {g, ng}, {ri, ncrsel[[r]]}];

printbug["2.1"];

n		= 0;
scen		= 1;
ToExpression[makescen];

printbug["2.2"];

prevcurr0	= makeprevcurr[prisksel, pdissel, RMrisk, RRdisscen, RRcasefatscen];

casefatbase	= Table[casefatscen[[casefatindsel[[d]]]] *
				Times@@Table[1 + (RRcasefatscen[[RRcasefatindsel[[d1, d]]]] - 1) pdissel[[d1]], {d1, nd}],
			{d, nd}];
printbug["2.3"];

	
(*-------------------------------------------------
	LOOP OVER SCENARIOS
---------------------------------------------------*)

nprevnew	= Table[0, {nz}];	(* CLASS PREVALENCE VALUES AFTER 1-YEAR TIME STEP *)
incevent	= Table[0, {nd}];	(* INCIDENCE EVENT INDICATOR VALUES *)
casefatevent	= Table[0, {nd}];	(* CASE FATALITY EVENT INDICATOR VALEUS *)

Do[
	(* DEFINES SCENARIO-SPECIFIC INPUT PARAMETER VALUES *)

printbug["3."];

	n		= 0;
	ToExpression[makescen];

	(* ADJUSTED EXCESS MORTALITY RATES *)

printbug["3.1"];

	excessmortadj 	= Min1[Max0[excessmortseladj + (excessmortscen - excessmortsel)]];
	
	transrisk	= Table[maketransmarginal[transriskscen[[r]], transriskindsel[[r]], r, ncrsel[[r]], trackingscen], {r, nrd}];

printbug["3.2"];

	If[(Quotient[patientsel, 10] > 0) && (scen == 1), transriskref = transrisk];

	(* DISCRETELY DISTRIBUTED RISK FACTORS *)

printbug["3.3"];

	ERRrisk		= Table[Plus@@(RRriskscen[[r, d, g]] priskscen[[r, g]]),
				{r, nrd}, {d, Length[RRriskscen[[r]]]}, {g, ng}];

	RMrisk		= Table[RRriskscen[[r, d, g, ri]] / (ERRrisk[[r, d, g]] + eps),
				{r, nrd}, {d, Length[RRriskscen[[r]]]}, {g, ng}, {ri, ncrsel[[r]]}];

	(* DISEASE INCIDENCE FIGURES *)

printbug["3.4"];

	inc		= makeincjoint[incscen, RRriskscen, RRdisscen];

	incbase		= Table[incscen[[d, g]]^2 / (Plus@@(inc[[g, d]] prevcurr0[[g]]) + eps), {d, nd}, {g, ng}];

	(* CHANGE OF INITIAL CLASS PREVALENCE RATES ACCORDING TO SCENARIO *)

printbug["3.5"];

	prevcurr 	= If[(scen == 1), prevcurr0, changeprevcurr[prevcurr0, priskscen]];

	prevnewborn 	= prevcurr[[Range[ng], Range[nz1], 1]];

	(* INITIAL DISCRETELY DISTRIBUTED DISEASE-SPECIFIC RISK FACTOR CLASSES, E.G. HBA1C *)

printbug["3.6"];

	hprisk	= Table[priskscen[[nrdpop + r, g, ri]] /
				RRrisksel[[nrdpop + r, RRriskindsel[[nrdpop + r, disindinv[[disriskindddis[[r]]]] + 1]], g, ri]],
			{r, nrddis}, {g, ng}, {ri, ncrsel[[nrdpop + r]]}];

	hprisk	= Table[hprisk[[r, g, ri]] / Plus@@hprisk[[r, g]], {r, nrddis}, {g, ng}, {ri, ncrsel[[nrdpop + r]]}];

	hRMriskdis = Table[RRrisksel[[nrdpop + r, RRriskindsel[[nrdpop + r, disindinv[[disriskindddis[[r]]]] + 1]], g, ri]] /
			(Plus@@(RRrisksel[[nrdpop + r, RRriskindsel[[nrdpop + r, disindinv[[disriskindddis[[r]]]] + 1]], g]] *
				hprisk[[r, g]]) + eps),
			{r, nrddis}, {g, ng}, {ri, ncrsel[[nrdpop + r]]}];

printbug["3.7"];


If[(scen == 1),	initprev = Table[0, {ng}, {Length[agesel]}, {ndraw}];
		initcont = Table[0, {ng}, {Length[agesel]}, {ndraw}]];


(*-------------------------------------------------
	LOOP OVER GENDER, AGE AND SAMPLE SIZE
---------------------------------------------------*)

	Do[	a	= agesel[[a1]];			(* INITIAL AGE OF COHORT *)
		b	= 1;				(* FIRST RANDOM DRAWING FROM POPULATION SAMPLE *)
		
	While[(b <= ndraw),

		ha	= agesel[[a1]];			(* CURRENT AGE OF COHORT *)
		breject	= False;
		nprev 	= If[(scen  == 1), Table[0, {nz}], initprev[[g, a1, b]]];
		
		If[(scen == 1), (* REFERENCE SCENARIO *)

			(* INITIAL DISCRETELY DISTRIBUTED RISK FACTOR CLASSES *)

			Do[	nprev[[r]] = Max[Range[ncrsel[[r]] + 1] Sign[Random[] - FoldList[Plus, 0, priskscen[[r, g, All, ha]]]]],
				{r, nrdpop}];

			(* INITIAL DISEASE STATES *)

			Do[	d = dispath[[d1]];

				(* BASELINE DISEASE PREVALENCE RATE *)

				currpdis = pdissel[[d, g, ha]];

				(* EFFECT THROUGH DISCRETELY DISTRIBUTED POPULATION RISK FACTORS *)

				Do[currpdis *= RMrisk[[r, RRriskindsel[[r, d + 1]], g, nprev[[r]], ha]], {r, nrdpop}];
				
				(* EFFECT THROUGH NOT CAUSALLY RELATED RISK FACTORS WITH EMPIRICAL DATA *)

				If[(userriskdata >= 1),	Do[

					currpdis *= RMriskDMinc[[d, r1, g, nprev[[DMriskpairsel[[d, r1]]]], ha]],
					{r1, Length[DMriskpairsel[[d]]]}]];

				(* EFFECT THROUGH INDEPENDENT CO-MORBIDITY DISEASES *)

				Do[If[MemberQ[dispair, {dispath[[d2]], d}],

					currpdis *= (2 - nprev[[nrd + dispath[[d2]]]]) +
							(nprev[[nrd + dispath[[d2]]]] - 1) *
								RRdisadj[[RRdisindsel[[dispath[[d2]], dispath[[d1]]]], g, ha]] /
							ERRdisadj[[RRdisindsel[[dispath[[d2]], dispath[[d1]]]], g, ha]]],
					{d2, d1 - 1}];

				(* EFFECT THROUGH DISCRETELY DISTRIBUTED DISEASE-SPECIFIC RISK FACTORS *)

				Do[	hr 	= nrdpop + riskdisdsel[[dispath[[d2]], r]];
					hd	= nprev[[nrd + disindinv[[disriskindddis[[hr - nrdpop]]]]]];

					currpdis *= RMrisk[[hr, RRriskindsel[[hr, d + 1]], g, nprev[[hr]], ha]] (hd - 1) + (2 - hd),

					{d2, d1 - 1}, {r, Length[riskdisdsel[[dispath[[d2]]]]]}];

				nprev[[nrd + d]] = 1 + Max0[Sign[currpdis - Random[]]];

				(* DISCRETELY DISTRIBUTED DISEASE-SPECIFIC RISK FACTORS HAVING THE RELATED DISEASE *)

				Do[	hr 	= nrdpop + riskdisdsel[[d, r]];
					hprisk	= (nprev[[nrd + d]] - 1) priskscen[[hr, g, All, ha]] +
							(2 - nprev[[nrd + d]]) priskincsel[[hr - nrdpop, g, All, ha]];

					nprev[[hr]] = Max[Range[ncrsel[[hr]] + 1] Sign[Random[] - FoldList[Plus, 0, hprisk]]],

					{r, Length[riskdisdsel[[d]]]}]

				,{d1, nd}]; (* INITIAL DISEASE STATES *)

			If[(patientsel == 0) || (Mod[patientsel, 10] > 0)|| (nprev[[nrd + disindinv[[Quotient[patientsel, 10]]]]] == 2),

				initprev[[g, a1, b]] = nprev;
				b++,
				breject = True]];  (* END REFERENCE SCENARIO *)

		(* ALTERNATIVE SCENARIO *)

		If[(scen > 1), 
			rsel = Select[Range[nrd] Sign[Table[Plus @@Flatten[Abs[prisksel[[r]] - priskscen[[r]]]], {r, nrd}]], Positive];
			If[! ((Quotient[patientsel, 10] > 0)
				&& (Mod[patientsel, 10] > 0)
				&& (nprev[[nrd + disindinv[[Quotient[patientsel, 10]]]]] == 1)), 
				Do[nprev[[rsel[[r]]]] = Max[Range[ncrsel[[rsel[[r]]]] + 1]*Sign[Random[] - FoldList[Plus, 0, priskscen[[rsel[[r]], g, All, ha]]]]],
				{r, Length[rsel]}]];
			b++];

		n	= 0;				(* TIME STEP *)	
		dmort	= 0;				(* MORTALITY INDICATOR *)

		(* RESTRICTION TO DISEASED POPULATION *)

		If[(Quotient[patientsel, 10] > 0) && (Mod[patientsel, 10] == 0) &&
			(nprev[[nrd + disindinv[[Quotient[patientsel, 10]]]]] == 1), dmort = 1];
		
		(* AGE AT DISEASE ONSET *)

		onsetage = ha - 1 - disduur[[Range[nd], g, ha]];

		(* AGE AT SMOKING CESSATION *)

		If[(nrd > 0),

			duurstop = If[(RRsmokduurind == 1) && (nprev[[1]] == 3),
					(prisksel[[1, g, 3, ha]] Plus@@(stopduur[[g, Range[nstopduur], ha]] duurval) +
					(priskscen[[1, g, 3, ha]] - prisksel[[1, g, 3, ha]]) duurval[[1]]) / (priskscen[[1, g, 3, ha]] + eps),
					0]];

		resprev	= Table[0, {nstap}];						

		While[(n < nstap) && (dmort == 0),	

				
		(*-------------------------------------------------
				LOOP OVER 1 YEAR TIME STEPS
		---------------------------------------------------*)

			++n;

			(* RANDOM RISK FACTOR CLASS TRANSITIONS *) 

			Do[	transrate = transrisk[[r, g, nprev[[r]], Range[ncrsel[[r]]], ha]];

				(* ADJUSTMENT FOR TRACKING *)

				If[(trackingind == 1) && (trstrackingind[[riskindd[[r]]]] == 1),

					Do[transrate[[ri + 1]] += trackingscen trstrackingsel[[r, g, ri, 1, ha]],
						{ri, ncrsel[[r]] - 1}];

					Do[transrate[[ri - 1]] += trackingscen trstrackingsel[[r, g, ri, 2, ha]],
						{ri, 2, ncrsel[[r]]}]

					];
		
				(* NO TRANSITIONS IN CASE OF DISEASE-FREE FOR DISEASE-SPECIFIC RISK FACTORS, E.G. HBA1C *)

				If[(r > nrdpop) && (nprev[[nrd + disindinv[[disriskindddis[[r - nrdpop]]]]]] == 1),
					transrate = Table[0, {ncrsel[[r]]}];
					transrate[[nprev[[r]]]] = 1];

				(* DURATION-DEPENDENT SMOKING RELAPSE RATES *)

				If[(RRsmokduurind == 1) && (r == 1) && (nprev[[r]] == 3),

					transrate[[2]] = 1 - Exp[ -relapsecoeffscen[[g, 1]] Exp[ -relapsecoeffscen[[g, 2]] 12 duurstop] *
									( 1 - Exp[ -relapsecoeffscen[[g, 2]] 12] ) ];
					transrate[[3]] = 1 - transrate[[2]]];

				nprevnew[[r]] = Max[Range[ncrsel[[r]] + 1] Sign[Random[] - FoldList[Plus, 0, transrate]]],

				{r, nrd}];

			(* RELATIVE RISKS FOR EX-SMOKERS DEPEND ON TIME SINCE SMOKING CESSATION *)

			If[(RRsmokduurind == 1),

				Do[RRriskscen[[1, d, g, 3, ha]] =
						1 + (RRriskscen[[1, d, g, 2, ha]] - 1) *
						Exp[-logRRsmokduur[[d, g, 1]] Exp[-logRRsmokduur[[d, g, 2]] Max0[ha - 51] ] duurstop],
					{d, Length[logRRsmokduur]}, {g, ng}]];

			(* RANDOM DISEASE STATE TRANSITIONS *)

			incevent = Table[0, {nd}];

			Do[If[(nprev[[nrd + d]] == 1),

					(* INCIDENCE AND CASE FATALITY EVENTS IN CASE OF DISEASE-FREE *)

					inc = incbase[[d, g, ha]] *

						(* DISCRETELY DISTRIBUTED POPULATION RISK FACTORS *)

						Times@@Table[RRriskscen[[r, RRriskindsel[[r, d + 1]], g, nprev[[r]], ha]], {r, nrdpop}] *

						(* DISCRETELY DISTRIBUTED RISK FACTORS RESTRICTED TO DISEASES, E.G. HBA1C *)

						Times@@Table[	1 +
								(nprev[[nrd + disindinv[[disriskindddis[[r]]]]]] - 1) *
								(RRriskscen[[nrdpop + r, RRriskindsel[[nrdpop + r, d + 1]], g,													nprev[[nrdpop + r]], a]] - 1),
								{r, nrddis}] *

						(* CO-MORBIDITY *)

						Times@@Table[1 + (RRdisadj[[RRdisindsel[[d1, d]], g, ha]] - 1) (nprev[[nrd + d1]] - 1),
								{d1, nd}];

					incevent[[d]] 	= Max0[Sign[inc - Random[]]];

					onsetage[[d]] 	= ha;

					(* CASE FATALITY RATES ADJUSTED FOR CO-MORBID DISEASE STATUS *)

					casefatadj	= casefatbase[[d, g, ha]] *
								Times@@Table[
									1 +
									(RRcasefatscen[[RRcasefatindsel[[d1, d]], g, ha]] - 1) *
										nprev[[nrd + d1]],
									{d1, nd}];
								
					probcasefat	= casefatadj +
								.5 (1 - casefatadj ) *
									(excessmortscen[[d, g, ha]] + rem1[[remindsel[[d]], g, ha]]);

					casefatevent[[d]] = incevent[[d]] Max0[Sign[probcasefat - Random[]]];

					nprevnew[[nrd + d]] = 1 + incevent[[d]] (1 - casefatevent[[d]]),

					(* REMISSION EVENTS IN CASE OF DISEASED *)
					
					remrate		= rem1[[remindsel[[d]], g, ha]];

					nprevnew[[nrd + d]] = 2 - Max0[Sign[remrate - Random[]]]],

				{d, nd}]; (* END RANDOM DISEASE STATE TRANSITIONS *)

			(* MORTALITY EVENT *)

			mortoth		= Times@@Table[RMothrisksel[[r, g, nprev[[r]], ha]], {r, 2, nrd}] mortothsel[[g, ha]] *

						If[(nrd == 0),

							1,

							If[(RRsmokduurind == 1) && (nprev[[1]] == 3),

								(1 + (RMothrisksel[[1, g, 2, ha]] / RMothrisksel[[1, g, 1, ha]] - 1) *
									Exp[-logRRsmokduuroth[[g, 1]] Exp[-logRRsmokduuroth[[g, 2]] *
									Max0[ha - 51] ] duurstop ]) /
								RRsmokformoth[[g, ha]],

								RMothrisksel[[1, g, nprev[[1]], ha]]]

							];

			excessmortcurr	= excessmortadj[[Range[nd], g, ha]] (nprev[[nrd + Range[nd]]] - 1);

			morttot 	= Plus@@excessmortcurr + mortoth;

			dmortprev 	= Max0[Sign[morttot - Random[]]];

			dmort		= Max[{casefatevent incevent, dmortprev}];
	
	
			(*--------------------------------------------------------------------------
			FILL OUTPUT VARIABLES WITH MODEL RESULTS FOR SCENARIO scen AND TIME STEP n 
			---------------------------------------------------------------------------*)

			++respop[[scen, n, g, a1]];

			Do[++resrisk[[scen, n, r, g, nprev[[r]], a1]], {r, nrdpop}];

			Do[If[(nprev[[nrd + disindinv[[disriskindddis[[r]]]]]] == 2),
					++resrisk[[scen, n, nrdpop + r, g, nprev[[nrdpop + r]], a1]]], {r, nrddis}];

			resdis[[scen, n, Range[nd], g, a1]] += nprev[[nrd + Range[nd]]] - 1;

			resinc[[scen, n, Range[nd], g, a1]] += incevent;

			If[(dmortprev == 1),

				resmort[[scen, n, Range[nd], g, a1]] += excessmortcurr / morttot;

				resmort[[scen, n, nd + 1, g, a1]] += mortoth / morttot];

			If[(dmortprev == 0) && (dmort == 1),

				resmort[[scen, n, Range[nd], g, a1]] += casefatevent incevent];
		
			resmort[[scen, n, nd + 2, g, a1]] += dmort;

			resonsetage[[scen, n, Range[nd], g, a1]] += (nprev[[nrd + Range[nd]]] - 1) onsetage;

			If[(RRsmokduurind == 1) && (nprev[[1]] == 3), resduurstop[[scen, n, g, a1]] += duurstop];

			If[(dmort == 1),

				Do[	++resmortrisk[[scen, n, r, g, nprev[[r]], a1]], {r, nrdpop}];

				Do[	If[(nprev[[nrd + disindinv[[disriskindddis[[r]]]]]] == 2),
				 		++resmortrisk[[scen, n, nrdpop + r, g, nprev[[nrdpop + r]], a1]]], {r, nrddis}]];

			resprev[[n]] = makezval[nprev];
	 		
			(*---------------------------------------------------------
		 	CALCULATED NEW VALUES OF MODEL STATE VARIABLES, I.E. AT END OF 1-YEAR TIME STEP
			----------------------------------------------------------*)

			If[(RRsmokduurind == 1),

				If[(nprev[[1]] == 3) && (nprevnew[[1]] == 3), ++duurstop];	(* CONTINUOUSLY FORMER SMOKER *)
				If[(nprev[[1]] == 2) && (nprevnew[[1]] == 3), duurstop = .5]];	(* NEW FORMER SMOKER *)
									
			nprev		= nprevnew (1 - dmort);
			ha		= Min[{ha + 1, na1}]


		(*-------------------------------------------------
				END OF LOOP OVER 1 YEAR TIME STEPS
			---------------------------------------------------*)

		];

		If[!breject, PutAppend[{scen, g, agesel[[a1]], resprev}, outfile]]


	(*-------------------------------------------------
		END OF LOOP OVER GENDER, AGE AND DRAWING
	---------------------------------------------------*)

	], {g, ng}, {a1, Length[agesel]}],

(*-------------------------------------------------
	END OF LOOP OVER SCENARIOS
---------------------------------------------------*)

{scen, nscen}]; 

Close[outfile];


(*-------------------------------------------------
	SCALING OF RESULTS FROM SAMPLE SIZE TO POPULATION SIZE
---------------------------------------------------*)

printbug["5."];

scalepop	= npop0;

If[(Quotient[patientsel, 10] > 0) && (Mod[patientsel, 10] == 0),scalepop *= pdissel[[disindinv[[Quotient[patientsel, 10]]]]]];

Do[respop[[scen, n]] *= scalepop[[All, agesel]] / ndraw, {scen, nscen}, {n, nstap}];

Do[resrisk[[scen, n, r, g, ri]] *= scalepop[[g, agesel]] / ndraw, {scen, nscen}, {n, nstap}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];

If[(riskcontind == 1) && (nrc > 0), Do[resdist[[scen, n, r, g, ri]] *= scalepop[[g, agesel]] / ndraw,
	{scen, nscen}, {n, nstap}, {r, nrc}, {g, ng}, {ri, 2}]];

Do[resdis[[scen, n, d]] *= scalepop[[All, agesel]] / ndraw, {scen, nscen}, {n, nstap}, {d, nd}];

Do[resinc[[scen, n, d]] *= scalepop[[All, agesel]] / ndraw, {scen, nscen}, {n, nstap}, {d, nd}];

Do[resmort[[scen, n, d]] *= scalepop[[All, agesel]] / ndraw, {scen, nscen}, {n, nstap}, {d, nd + 2}];

Do[resonsetage[[scen, n, d]] *= scalepop[[All, agesel]] / ndraw, {scen, nscen}, {n, nstap}, {d, nd}];

If[(RRsmokduurind == 1), Do[resduurstop[[scen, n]] *= scalepop[[All, agesel]] / ndraw, {scen, nscen}, {n, nstap}]];

Do[resmortrisk[[scen, n, r, g, ri]] *= scalepop[[g, agesel]] / ndraw, {scen, nscen}, {n, nstap}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];

printbug["5.1"];

resjointmodelstochind = transformres[respop, resrisk, resdist, resdis, resinc, resmort, resonsetage, resduurstop, resmortrisk],

resjointmodelstochind = {respop, resrisk, resdist, resdis, resinc, resmort, resonsetage, resduurstop, resmortrisk}];

 (* IF[(MODELSEL[[4]] == 1) && (APPLTYPE != "outp") *)

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
