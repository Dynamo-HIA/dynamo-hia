(* :Title: CZMSimulationJointModelDetermPop *)

(* :Context: CZMsimulation` *)

(* :Author: Rudolf T. Hoogenveen *)

(* :Summary:
   CZM simulation routine describes deterministic joint CZM model, i.e. change of joint risk factor
   class and disease prevalence numbers *)

(* :Copyright: © 2004 by Rudolf Hoogenveen *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM November 2004
		2.0 first release CZM 2005, version March
		3.0 version November 2005 
		3.1 version March 2007; storage of results *)

(* :Keywords: joint model, model parameters *)


BeginPackage["CZMSimulation`CZMSimulationJointModelDetermPop`",
	{"CZMInitialization`CZMLogFile`", 
	 "CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`",
	"CZMImportData`CZMImportUserSelections`",
	"CZMImportData`CZMImportDemography`",
	"CZMImportData`CZMImportRiskFactors`",
	"CZMImportData`CZMImportRelativeRisks`",
	"CZMAdjustData`CZMDataSmoothing`",
	"CZMAdjustData`CZMMakeSelections`",
	"CZMAdjustData`CZMAdjustAfterSelection`",
	"CZMDefineScenarios`CZMDefineScenarios`",
	"CZMSimulation`CZMSimulationFunctions`",
	"CZMPostProcessing`CZMExportUserSelections`"}] 


resjointmodeldetermpop::usage	= "output numbers of deterministic joint model total population, see marginalmodelresults"
jointmodeldetermpopprev::usage	= "MMA data file, field 1: dimension of:, field 2: data, see jointmodelresults"


Begin["`Private`"]


Print["CZMSimulationJointModelDetermPop package is evaluated"];
printtijd;

printbug[c_] := If[(bugind == 1), Print[{"CZMSimulationJointModelDetermPop", c}]];

appltype	= StringTake[ToString[Global`application], {8, 11}];

respop = resrisk = resdist = resdis = resinc = resmort = resonsetage = resduurstop = resmortrisk = Table[0., {nscen}, {nstap}];

If[(modelsel[[2]] == 1) && (appltype != "outp"),

(* INITIALIZE OUTPUT VARIABLES *)

printbug["1."];

respop 		= Table[0., {nscen}, {nstap}, {ng}, {nac[[1]]}];
resrisk 	= Table[0., {nscen}, {nstap}, {r, nrd}, {ng}, {ncrsel[[r]]}, {nac[[1]]}];
resdis 		= Table[0., {nscen}, {nstap}, {nd}, {ng}, {nac[[1]]}]; 
resinc 		= Table[0., {nscen}, {nstap}, {nd}, {ng}, {nac[[1]]}]; 
resmort 	= Table[0., {nscen}, {nstap}, {nd + 2}, {ng}, {nac[[1]]}]; 
resonsetage 	= Table[0., {nscen}, {nstap}, {nd}, {ng}, {nac[[1]]}]; 
If[(RRsmokduurind == 1),
	resduurstop	= Table[0., {nscen}, {nstap}, {ng}, {nac[[1]]}]];

mortrisk	= Table[0., {r, nrd}, {ng}, {ncrsel[[r]]}, {na1}];

(* JOINT STATE PREVALENCE NUMBERS MADE AVAILABLE *)

printbug["2."];

outfile		= OpenWrite[Global`outputpath <> "jointmodeldetermpopprev.m"];
Put[{nscen, Length[agesel], nstap, nz1, agemin, 0, riskind, disind, nscen0}, outfile];

(* EMPIRICAL INITIAL CLASS PREVALENCE RATES *)

printbug["3."];

ERRrisk 	= Table[Plus@@(RRriskseladj[[r, d, g]] prisksel[[r, g]]), {r, nrd}, {d, Length[RRriskseladj[[r]]]}, {g, ng}];

RMrisk		= Table[RRriskseladj[[r, d, g, ri]] / (ERRrisk[[r, d, g]] + eps),
			{r, nrd}, {d, Length[RRriskseladj[[r]]]}, {g, ng}, {ri, ncrsel[[r]]}];

printbug["4."];

n		= 0;
scen		= 1;
ToExpression[makescen];

printbug["5."];	

prevcurr0	= makeprevcurr[prisksel, pdissel, RMrisk, RRdisscen, RRcasefatscen];

		
(*-------------------------------------------------
		LOOP OVER SCENARIOS
---------------------------------------------------*)

Do[
	(* DEFINES SCENARIO-SPECIFIC INPUT PARAMETER VALUES *)

printbug["6."];

	n		= 0;
	ToExpression[makescen];

	(* ADJUSTED EXCESS MORTALITY RATES *)

printbug["6.1"];

	excessmortadj 	= Min1[Max0[excessmortseladj + (excessmortscen - excessmortsel)]];
	
	(* DISEASE INCIDENCE FIGURES *)

printbug["6.1"];

	inc		= makeincjoint[incscen, RRriskscen, RRdisscen];

printbug["6.2"];

	incbase		= Table[incscen[[d, g]]^2 / (Plus@@(inc[[g, d]] prevcurr0[[g]]) + eps), {d, nd}, {g, ng}];


printbug["6.3"];

	rate		= maketransjoint[transriskscen, incbase, excessmortscen, casefatscen,
						RRriskscen, trackingscen, RRdisscen, RRcasefatscen];

printbug["6.4"];

	transjoint	= rate[[1]];
	inc		= rate[[2]];
	mortoth		= rate[[3]];

	(* CHANGE OF INITIAL CLASS PREVALENCE RATES ACCORDING TO SCENARIO *)

printbug["6.5"];

	prevcurr = If[(scen == 1), prevcurr0, changeprevcurr[prevcurr0, priskscen]];


	(* RESTRICTION TO DISEASE PATIENT POPULATION *)

	If[(Quotient[patientsel, 10] > 0) && (Mod[patientsel, 10] == 0),

		prevcurr[[Range[ng], zinddis[[disindinv[[Quotient[patientsel, 10]]], 1]]]] *= 0];

printbug["6.6"];
	nprev		= Table[prevcurr[[g, zi]] npop0[[g]] agesel1, {g, ng}, {zi, nz1}];

	npop		= Table[Plus@@nprev[[g]], {g, ng}];
	
	prevnewborn 	= prevcurr[[Range[ng], Range[nz1], 1]];
	(* MEAN AGE AT ONSET OF INCIDENT AND PREVALENT DISEASE CASES *)

	If[(outputsel[[7]] == 1),

printbug["6.7"];

		onsetage = Table[Range[na1], {ng}, {nz1}, {nd}] - 1;

		Do[onsetage[[g, zinddis[[d, 2, zi]], d]] = Range[na1] - .5 - disduur[[d, g]],
				{g, ng}, {d, nd}, {zi, Length[zinddis[[d, 2]]]}]];


	(*-------------------------------------------------
			LOOP OVER 1 YEAR TIME STEPS
	---------------------------------------------------*)

	Do[
		(* UPDATE OF MEAN AGE AT ONSET OF NEW DISEASE CASES *)

printbug["7"];

		If[(outputsel[[7]] == 1), Do[++onsetage[[g, zinddis[[d, 1]], d]], {g, ng}, {d, nd}]];	

		(* #NEWBORNS *)
		
		nbirth 	= makenbirth[n] (1 - .5 morttot1[[Range[ng], 1]]);

		(* 1-YEAR MIGRATION NUMBERS APPLIED TO SELECTED COHORT TIMES PROBABILITY OF HALF YEAR SURVIVAL *)

		migpop 	= makemigpop[n] ( 1 - .5 morttot1);

		(* WGTSUBSET = PROPORTIONAL DISTRIBUTION OF 84+ POPULATION NUMBERS OVER AGE YEAR 84 AND AGE CLASS 85+ *)
	
		wgtsubset = Table[Take[npop[[g]], -2] / (Plus@@Take[npop[[g]], -2] + eps), {g, ng}];

		(* MORTALITY NUMBERS FOR EACH RISK FACTOR CLASS *)

		Do[mortrisk[[r, g, ri]] = Plus@@(mortoth[[g, zindrisk[[r, ri]]]] nprev[[g, zindrisk[[r, ri]]]]),
				{r, nrdpop}, {g, ng}, {ri, ncrsel[[r]]}];
					
		Do[	disset	= Intersection[zindrisk[[r, ri]], zinddis[[d, 1]]];

			Do[	mortrisk[[r, g, ri]] += Plus@@(nprev[[g, disset]] inc[[g, d, disset]]) *
	 						(casefatscen[[casefatindsel[[d]], g]] +
							.5 (1 - casefatscen[[casefatindsel[[d]], g]]) excessmortadj[[d, g]]),
					{g, ng}];

			disset	= Intersection[zindrisk[[r, ri]], zinddis[[d, 2]]];

			Do[mortrisk[[r, g, ri]] += Plus@@nprev[[g, disset]] excessmortadj[[d, g]], {g, ng}],

			{r, nrdpop}, {ri, ncrsel[[r]]}, {d, nd}];

		Do[	d1	= disindinv[[disriskindddis[[r]]]];
			disset	= Intersection[zindrisk[[nrdpop + r, ri]], zinddis[[d1, 2]]];
			Do[	mortrisk[[nrdpop + r, g, ri]] = excessmortadj[[d1, g]] Plus@@nprev[[g, disset]], {g, ng}],

			{r, nrddis}, {ri, ncrsel[[nrdpop + r]]}];


		(*--------------------------------------------------------------------------
		CALCULATED NEW VALUES OF MODEL STATE VARIABLES, I.E. AT END OF 1-YEAR TIME STEP 
		----------------------------------------------------------*)

printbug["8."];		
		nprevnew 	= Table[Plus@@(nprev[[g]] transjoint[[g, Range[nz1], zj]]), {g, ng}, {zj, nz1}];

		If[(outputsel[[7]] == 1),

			onsetage	= Table[subsetp[
							.5 .5 Plus@@(prevnewborn[[g]] transjoint[[g, Range[nz1], zj, 1]]),
							Plus@@(onsetage[[g, Range[nz1], d]] nprev[[g]] transjoint[[g, Range[nz1], zj]]) /
								(Plus@@(nprev[[g]] transjoint[[g, Range[nz1], zj]]) + eps),
							wgtsubset[[g]]
							],
						{g, ng}, {zj, nz1}, {d, nd}]];


		(*--------------------------------------------------------------------------
		FILL OUTPUT VARIABLES WITH MODEL RESULTS FOR SCENARIO scen AND TIME STEP n 
		---------------------------------------------------------------------------*)

printbug["9."];

		respop[[scen, n]] = Table[aggreg[Plus@@nprev[[g]], 1], {g, ng}] +
					Table[Ceiling[.2 lengthageclass[[1, Range[nac[[1]]]]]] eps, {ng}];

		Do[resrisk[[scen, n, r, g, ri]] = aggreg[Plus@@nprev[[g, zindrisk[[r, ri]]]], 1],
					{r, nrdpop}, {g, ng}, {ri, ncrsel[[r]]}];

		Do[resrisk[[scen, n, nrdpop + r, g, ri]] =
					aggreg[Plus@@nprev[[g, Intersection[zindrisk[[nrdpop + r, ri]],
									zinddis[[disindinv[[disriskindddis[[r]]]], 2]]]]], 1],
					{r, nrddis}, {g, ng}, {ri, ncrsel[[nrdpop + r]]}];

		resdis[[scen, n]] = Table[aggreg[Plus@@nprev[[g, zinddis[[d, 2]]]], 1], {d, nd}, {g, ng}];

		resinc[[scen, n]] = Table[aggreg[Plus@@(nprev[[g, zinddis[[d, 1]]]] inc[[g, d, zinddis[[d, 1]]]]), 1],
					{d, nd}, {g, ng}];

		resmort[[scen, n, Range[nd]]] = Table[aggreg[
					Plus@@(nprev[[g, zinddis[[d, 1]]]] inc[[g, d, zinddis[[d, 1]]]]) *
	 					(casefatscen[[casefatindsel[[d]], g]] +
						.5 (1 - casefatscen[[casefatindsel[[d]], g]]) excessmortadj[[d, g]]) +
					Plus@@nprev[[g, zinddis[[d, 2]]]] excessmortadj[[d, g]] , 1],
					{d, nd}, {g, ng}];

		resmort[[scen, n, nd + 1]] = Table[aggreg[Plus@@(mortoth[[g]] nprev[[g]]), 1], {g, ng}];

		resmort[[scen, n, nd + 2]] = Table[aggreg[Plus@@(nprev[[g]] - nprevnew[[g]]), 1], {g, ng}];

		If[(outputsel[[7]] == 1),
			resonsetage[[scen, n]] = Table[aggreg[
						Plus@@(onsetage[[g, zinddis[[d, 2]], d]] nprev[[g, zinddis[[d, 2]]]]), 1],
						{d, nd}, {g, ng}]];	
		
		resmortrisk[[scen, n]] = Table[aggreg[mortrisk[[r, g, ri]], 1],	{r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];

		PutAppend[Table[aggreg[nprev[[g, zi]], 1], {g, ng}, {zi, nz1}],	outfile]; 

		(*---------------------------------------------------------
		CALCULATED NEW VALUES OF MODEL STATE VARIABLES, I.E. AT END OF 1-YEAR TIME STEP 
		----------------------------------------------------------*)

printbug["10."];		
		nprev		= Table[subsetn[nbirth[[g]] prevnewborn[[g, zi]], nprevnew[[g, zi]]], {g, ng}, {zi, nz1}];

		sumnprev 	= Table[Plus@@nprev[[g]], {g, ng}];

		(* RE-SCALING BECAUSE OF MIGRATION *)

		npop		= sumnprev + migpop;
		nprev		= Table[nprev[[g, zi]] npop[[g]] / (sumnprev[[g]] + eps), {g, ng}, {zi, nz1}],


		(*-------------------------------------------------
			END OF LOOP OVER 1 YEAR TIME STEPS
		---------------------------------------------------*)


	{n, nstap}],


(*-------------------------------------------------
	END OF SCENARIO LOOP
---------------------------------------------------*)

{scen, nscen}];


Close[outfile]];

printbug["10."];

resjointmodeldetermpop = {respop, resrisk, resdist, resdis, resinc, resmort, resonsetage, resduurstop, resmortrisk};



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
