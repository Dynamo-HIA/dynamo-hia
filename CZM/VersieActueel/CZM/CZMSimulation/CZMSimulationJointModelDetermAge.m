(* :Title: CZMSimulationJointModelDetermAge *)

(* :Context: CZMSimulation` *)

(* :Author: Rudolf T. Hoogenveen *)

(* :Summary:
   CZM simulation routine describes deterministic joint CZM model stratified by age, i.e. change of joint risk factor
   class and disease prevalence numbers for successive cohorts *)

(* :Copyright: © 2004 by Rudolf Hoogenveen *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM November 2004
		2.0 first release CZM 2005, version March
		3.0 version November 2005 
		3.1 version March 2007; storage of results *)

(* :Keywords: joint model, model parameters *)


BeginPackage["CZMSimulation`CZMSimulationJointModelDetermAge`",
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


resjointmodeldetermage::usage	= "output numbers of deterministic joint model stratified by cohort, see marginalmodelresults"
jointmodeldetermageprev::usage	= "MMA data file, field 1: dimension of:, field 2: data, see jointmodelresults"


Begin["`Private`"]


Print["CZMSimulationJointModelDetermAge package is evaluated"];
printtijd;

printbug[c_] := If[(bugind == 1), Print[{"CZMSimulationJointModelDetermAge", c}]];

appltype	= StringTake[ToString[Global`application], {8, 11}];

respop = resrisk = resdist = resdis = resinc = resmort = resonsetage = resduurstop = resmortrisk = Table[0., {nscen}, {nstap}];

If[(modelsel[[3]] == 1) && (appltype != "outp"),

(* INITIALIZE OUTPUT VARIABLES *)

printbug["1."];

respop 		= Table[0., {nscen}, {nstap}, {ng}, {Length[agesel]}];
resrisk 	= Table[0., {nscen}, {nstap}, {r, nrd}, {ng}, {ncrsel[[r]]}, {Length[agesel]}];
resdis 		= Table[0., {nscen}, {nstap}, {nd}, {ng}, {Length[agesel]}]; 
resinc 		= Table[0., {nscen}, {nstap}, {nd}, {ng}, {Length[agesel]}]; 
resmort 	= Table[0., {nscen}, {nstap}, {nd + 2}, {ng}, {Length[agesel]}]; 
resonsetage	= Table[0., {nscen}, {nstap}, {nd}, {ng}, {Length[agesel]}]; 
If[(RRsmokduurind == 1),
	resduurstop	= Table[0., {nscen}, {nstap}, {ng}, {nac[[1]]}]];

resmortrisk	= Table[0., {nscen}, {nstap}, {r, nrd}, {ng}, {ncrsel[[r]]}, {Length[agesel]}];;
mortrisk	= Table[0., {r, nrd}, {ncrsel[[r]]}]; 

(* JOINT STATE PREVALENCE NUMBERS *)

outfile		= OpenWrite[Global`outputpath <> "jointmodeldetermageprev.m"];

Put[{nscen, Length[agesel], nstap, nz1, agemin, 0, riskind, disind, nscen0}, outfile];

(* EMPIRICAL INITIAL CLASS PREVALENCE RATES *)

printbug["1.1"];

ERRrisk		= Table[Plus@@(RRriskseladj[[r, d, g]] prisksel[[r, g]]), {r, nrd}, {d, Length[RRriskseladj[[r]]]}, {g, ng}];

RMrisk		= Table[RRriskseladj[[r, d, g, ri]] / (ERRrisk[[r, d, g]] + eps),
			{r, nrd}, {d, Length[RRriskseladj[[r]]]}, {g, ng}, {ri, ncrsel[[r]]}];

printbug["1.2"];

n		= 0;
scen		= 1;
ToExpression[makescen];

printbug["1.3"];
	
prevcurr0	= makeprevcurr[prisksel, pdissel, RMrisk, RRdisscen, RRcasefatscen];

printbug["1.4"];

				
(*-------------------------------------------------
		LOOP OVER SCENARIOS
---------------------------------------------------*)

Do[
	(* DEFINES SCENARIO-SPECIFIC INPUT PARAMETER VALUES *)

printbug["2."];

	n		= 0;
	ToExpression[makescen];
	
	(* ADJUSTED EXCESS MORTALITY RATES *)

printbug["2.1"];

	excessmortadj 	= Min1[Max0[excessmortseladj + (excessmortscen - excessmortsel)]];
	
	(* DISEASE INCIDENCE FIGURES *)

printbug["2.2"];

	inc		= makeincjoint[incscen, RRriskscen, RRdisscen];

	incbase		= Table[incscen[[d, g]]^2 / (Plus@@(inc[[g, d]] prevcurr0[[g]]) + eps), {d, nd}, {g, ng}];

	(* CHANGE OF INITIAL CLASS PREVALENCE RATES ACCORDING TO SCENARIO *)

printbug["2.3"];

	prevcurr = If[(scen == 1), prevcurr0, changeprevcurr[prevcurr0, priskscen]];

	(* UNCHANGED TRANSITION RATES FOR DISEASE-FREE PERSONS IN CASE OF SCENARIOS RESTRICTED TO DISEASE PATIENT POPULATION *)

printbug["2.4"]; 

	If[(Quotient[patientsel, 10] > 0) && (scen == 1),

		transjointref = maketransjoint[transriskscen, incbase, excessmortscen, casefatscen, 
							RRriskscen, trackingscen, RRdisscen, RRcasefatscen][[1]]];

	(* RESTRICTION TO DISEASE PATIENT POPULATION *)

	If[(Quotient[patientsel, 10] > 0) && (Mod[patientsel, 10] == 0),

printbug["2.5"];

		prevcurr[[Range[ng], zinddis[[disindinv[[Quotient[patientsel, 10]]], 1]]]] *= 0];

	prevnewborn 	= prevcurr[[Range[ng], Range[nz1], 1]];

printbug["2.6"];


(*-------------------------------------------------
		LOOP OVER GENDER AND AGE
---------------------------------------------------*)
	
	Do[
		a	= agesel[[a1]];				(* INITIAL AGE OF COHORT*)
		ha	= a;					(* CURRENT AGE OF COHORT*)

printbug["3."];

		(* INITIAL POPULATIONAND CLASS PREVALENCE NUMBERS *)
			
		nprev	= prevcurr[[g, Range[nz1], a]] npop0[[g, a]];
		npop	= Plus@@nprev;

		(* MEAN AGE AT ONSET OF INCIDENT AND PREVALENT DISEASE CASES *)

		onsetage = Table[a, {nz1}, {nd}] - 1;

		Do[onsetage[[zinddis[[d, 2, zi]], d]] = a - .5 - disduur[[d, g, a]], {d, nd}, {zi, Length[zinddis[[d, 2]]]}];
				

		(*-------------------------------------------------
				LOOP OVER 1 YEAR TIME STEPS
		---------------------------------------------------*)
				
		Do[
			(* UPDATE OF AGE AT ONSET OF NEW DISEASE CASES *)
printbug["4."];	
			Do[++onsetage[[zinddis[[d, 1]], d]], {d, nd}];	

			(* CURRENT TRANSITION RATE VALUES *)
printbug["4.1"];
			rate		= maketransjointga[g, ha, transriskscen, incbase, excessmortscen, casefatscen, 
								RRriskscen, trackingscen, RRdisscen, RRcasefatscen];

			transjoint 	= rate[[1]];

			If[(Quotient[patientsel, 10] > 0),
				transjoint[[zinddis[[disindinv[[Quotient[patientsel, 10]]], 1]], Range[nz1]]] = 
						transjointref[[g, zinddis[[disindinv[[Quotient[patientsel, 10]]], 1]], Range[nz1], ha]]];
			
			inc		= rate[[2]];
			mortoth		= rate[[3]];

			(* MORTALITY NUMBERS FOR EACH RISK FACTOR CLASS *)
printbug["4.2"];

			Do[	mortrisk[[r, ri]] = Plus@@(mortoth[[zindrisk[[r, ri]]]] nprev[[zindrisk[[r, ri]]]]),
				{r, nrdpop}, {ri, ncrsel[[r]]}];
					
			Do[	disset	= Intersection[zindrisk[[r, ri]], zinddis[[d, 1]]];

				mortrisk[[r, ri]] += Plus@@(nprev[[disset]] inc[[d, disset]]) *
	 						(casefatscen[[casefatindsel[[d]], g, ha]] +
							.5 (1 - casefatscen[[casefatindsel[[d]], g, ha]]) excessmortadj[[d, g, ha]]);

				disset	= Intersection[zindrisk[[r, ri]], zinddis[[d, 2]]];

				mortrisk[[r, ri]] += Plus@@nprev[[disset]] excessmortadj[[d, g, ha]],

				{r, nrdpop}, {ri, ncrsel[[r]]}, {d, nd}];

			Do[	d1	= disindinv[[disriskindddis[[r]]]];
				disset	= Intersection[zindrisk[[nrdpop + r, ri]], zinddis[[d1, 2]]];
				mortrisk[[nrdpop + r, ri]] = excessmortadj[[d1, g, ha]] Plus@@nprev[[disset]],

				{r, nrddis}, {ri, ncrsel[[nrdpop + r]]}];
printbug["4.3"];

			(*---------------------------------------------------------
			CALCULATED NEW VALUES OF MODEL STATE VARIABLES, I.E. AT END OF 1-YEAR TIME STEP 
			----------------------------------------------------------*)

			onsetage	= Table[Plus@@(onsetage[[Range[nz1], d]] nprev transjoint[[Range[nz1], zj]]) /
						(Plus@@(nprev transjoint[[Range[nz1], zj]]) + eps),
						{zj, nz1}, {d, nd}];

			nprevnew	= Table[Plus@@(nprev transjoint[[Range[nz1], zj]]), {zj, nz1}];
			npopnew		= Plus@@nprevnew;

			
			(*--------------------------------------------------------------------------
			FILL OUTPUT VARIABLES WITH MODEL RESULTS FOR SCENARIO scen AND TIME STEP n 
			---------------------------------------------------------------------------*)
	
			respop[[scen, n, g, a1]] = npop;
	
			Do[	resrisk[[scen, n, r, g, ri, a1]] = Plus@@nprev[[zindrisk[[r, ri]]]], {r, nrdpop}, {ri, ncrsel[[r]]}];

			Do[	resrisk[[scen, n, nrdpop + r, g, ri, a1]] =
				Plus@@nprev[[Intersection[zindrisk[[nrdpop + r, ri]],
								zinddis[[disindinv[[disriskindddis[[r]]]], 2]]]]],
				{r, nrddis}, {ri, ncrsel[[nrdpop + r]]}];

 			Do[resdis[[scen, n, d, g, a1]] = Plus@@nprev[[zinddis[[d, 2]]]], {d, nd}];

			Do[resinc[[scen, n, d, g, a1]] = Plus@@(nprev[[zinddis[[d, 1]]]] inc[[d, zinddis[[d, 1]]]]), {d, nd}];

			Do[resmort[[scen, n, d, g, a1]] = 
					Plus@@(nprev[[zinddis[[d, 1]]]] inc[[d, zinddis[[d, 1]]]]) *
	 					(casefatscen[[casefatindsel[[d]], g, ha]] +
						.5 (1 - casefatscen[[casefatindsel[[d]], g, ha]]) *
						excessmortadj[[d, g, ha]]) +
					Plus@@nprev[[zinddis[[d, 2]]]] excessmortadj[[d, g, ha]],
					{d, nd}];

			resmort[[scen, n, nd + 1, g, a1]] = Plus@@(mortoth nprev);

			resmort[[scen, n, nd + 2, g, a1]] = Plus@@(npop - npopnew);

			Do[resonsetage[[scen, n, d, g, a1]] = Plus@@(onsetage[[zinddis[[d, 2]], d]] nprev[[zinddis[[d, 2]]]]), {d, nd}];

			Do[resmortrisk[[scen, n, r, g, ri, a1]] = mortrisk[[r, ri]], {r, nrd}, {ri, ncrsel[[r]]}];

			PutAppend[nprev, outfile];

			(*---------------------------------------------------------
			NEW POPULATION NUMBERS
			----------------------------------------------------------*)

			nprev		= nprevnew;			
			npop		= npopnew;
			ha		= Min[{ha + 1, na1}],


			(*-------------------------------------------------
				END OF LOOP OVER 1 YEAR TIME STEPS
			---------------------------------------------------*)

		{n, nstap}],


	(*-------------------------------------------------
		END OF LOOP OVER GENDER AND AGE
	---------------------------------------------------*)

	{g, ng}, {a1, Length[agesel]}],


(*-------------------------------------------------
		END OF LOOP OVER SCENARIOS
---------------------------------------------------*)

{scen, nscen}];

printbug["6."];


Close[outfile];

resjointmodeldetermage = transformres[respop, resrisk, resdist, resdis, resinc, resmort, resonsetage, resduurstop, resmortrisk],

resjointmodeldetermage = {respop, resrisk, resdist, resdis, resinc, resmort, resonsetage, resduurstop, resmortrisk}];


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
