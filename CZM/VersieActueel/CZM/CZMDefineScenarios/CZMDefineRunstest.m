(* :Title: CZMDefineRunstest *)

(* :Context: CZMDefineScenarios` *)

(* :Author: Rudolf Hoogenveen *)

(* :Summary:
   CZM simulation routine defines selections made for different CZM test runs *)

(* :Copyright: © 2005 by Rudolf Hoogenveen *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	2.1 version August 2005
		3.0 version November 2005 
		3.1 version March 2007 *)

(* :Keywords: simulation, selections, runs *)


BeginPackage["CZMDefineScenarios`CZMDefineRunstest`",
	{"CZMInitialization`CZMLogFile`"}] 


makeruntest::usage 	= "makeruntest: routine makes selections for new CZM runs"	


Begin["`Private`"]	


Print["CZMDefineRunstest package is evaluated"]

makeruntest := {"

	test = ToExpression[StringDrop[ToString[Global`application], 11]];

	analyse	= 3;
	nscen0	= nscen = 1;
	birthind = migpopind = 0;
	riskval	= Global`riskfactor;
	disval	= Global`disease;	
	ageclssel = 16;

	(* TOTAL POPULATION, BASELINE VALUES *)

	If[MemberQ[{1, 4, 7, 14, 29}, test],

		agemin		= 1;
		agemax		= na1;
		nstap		= 1];

	(* COHORT VALUES OVER TIME (AGE) *)

	If[MemberQ[{2, 5, 8, 11, 12, 13, 15, 30}, test],

		agemin		= 61;
		agemax		= 61;
		nstap		= 2]; (* was 20 *)

	(* FIXED AGE VALUES OVER TIME *)

	If[MemberQ[{3, 6, 9, 10, 31}, test],

		agemin		= 1;
		agemax		= na1;
		nstap		= 40];

	(* COMPARING SELECTIONS FOR GIVEN RISK FACTOR ( = GLOBAL VARIABLE riskfactor ) *)

	If[MemberQ[{1, 2, 3, 7, 8, 9, 11, 13, 14, 15}, test],

		excessmortcond 	= 1;
		mortothind 	= 0;
		riskindd	= riskind = {riskval}];

	(* COMPARING SELECTIONS FOR GIVEN DISEASE ( = GLOBAL VARIABLE disease ) *)
						
	If[MemberQ[{4, 5, 6, 12}, test],

		disindrisk	= 0;
		excessmortcond 	= 1;
		mortothind 	= 0];
		
(* TESTING EPIDEMIOLOGICAL SELECTIONS
   - ADJUSTMENT OF EXCESS MORTALITY RATES (EXCESSMORTCOND)
   - EFFECT OF RISK FACTORS THROUGH NON-MODELD DISEASES (MORTOTHIND)
   - SELECTION OF NO DISEASES OR ALL RISK FACTOR RELATED DISEASES (DISINDRISK)
*)

	If[MemberQ[{1, 2, 3}, test],

		disindrisk = 1;
		If[MemberQ[{2, 4, 6, 8}, run], excessmortcond = 2];
		If[MemberQ[{3, 4, 7, 8}, run], disindrisk = 0];
		If[MemberQ[{5, 6, 7, 8}, run], mortothind = 1]];

(* TESTING DISEASE SELECTIONS
   - ADJUSTMENT OF EXCESS MORTALITY RATES USING CALCULATED CO-MORBIDITY RATES (EXCESSMORTCOND = 1)
   - NO EFFECT OF RISK FACTORS THROUGH NON-MODELD DISEASES (MORTOTHIND = 0)
   - SELECTION OF GIVEN DISEASE ONLY OR ALSO CO-MORBID DISEASES
   - SELECTION OF NO/SELECTED RISK FACTORS
*)

	If[MemberQ[{4, 5, 6}, test],

		If[MemberQ[{1, 3}, run], disind0 = {disval}];
		If[MemberQ[{2, 4}, run], disind0 = Union[disind0, {disval}]];
		If[MemberQ[{1, 2}, run], riskind = riskindd = {}]];

(* TESTING RISK FACTOR SELECTIONS
   - ADJUSTMENT OF EXCESS MORTALITY RATES USING CALCULATED CO-MORBIDITY RATES (EXCESSMORTCOND = 1)
   - NO EFFECT OF RISK FACTORS THROUGH NON-MODELD DISEASES (MORTOTHIND = 0)
   - SELECTION OF GIVEN RISK FCTOR ONLY OR ALSO SELECTED RISK FACTORS
   - SELECTION OF NO/SELECTED DISEASES
*)

	If[MemberQ[{7, 8, 9}, test],

		If[MemberQ[{1, 3}, run], disind0 = {}];
		If[MemberQ[{1, 2}, run], riskind = riskindd = {riskval}];
		If[MemberQ[{3, 4}, run], riskind = riskindd = Union[riskind, riskindd, {riskval}]]];

(* TESTING DEMOGRAPHIC PROJECTIONS
   - NEWBORNS OR MIGRATION NUMBERS INCLUDED
   - WITHOUR RISK FACTORS AND DISEASES OR WITH RISK FACTOR SMOKING AND SOME DISEASES INCLUDED
   RESULTS ARE AGE-AGGREGATED NUMBERS OVER TIME
   COMPARING RESULTS OVER TIME
*)

	If[(test == 10),

		disind0		= {};
		birthind	= migpopind = 1;
		If[MemberQ[{2, 4}, run], birthind = 0];
		If[MemberQ[{3, 4}, run], migpopind = 0];
		If[MemberQ[{1, 2, 3, 4}, run], riskind = riskindd = {}; disind0 = {}]];

(* TESTING CZM MODEL VERSIONS ON RISK FACTORS
   - SELECTION OF NO/SELECTED DISEASES
   - SELECTION OF DIFFERENT MODEL VERSIONS (THROUGH USERINPUT)
   RESULTS ARE AGE-AGGREGATED NUMBERS OVER TIME
   COMPARING RESULTS OVER TIME
*)

	If[(test == 11),

		If[(run == 2),	disindrisk = 0; disind0 = {}]]; 

(* TESTING CZM MODEL VERSIONS ON DISEASES
   - SELECTION OF NO/SELECTED RISK FACTORS
   - SELECTION OF DIFFERENT MODEL VERSIONS (THROUGH USERINPUT)
   RESULTS ARE AGE-AGGREGATED NUMBERS OVER TIME
   COMPARING RESULTS OVER TIME
*)

	If[(test == 12),

		If[(run == 2),	riskindd = riskind0 = {}]]; 

(* TESTING ONE CZM MODEL VERSION ON RISK FACTOR SMOKING AGE AND DURATION DEPENDENT
   MARGINAL DETERMINISTIC MODEL
   COMPARING RESULTS OVER TIME
*)

	If[(test == 13),

		riskindd	= riskind = {1};
		RRsmokduurind	= 0;
		disindrisk	= 0;
		disind0		= {};
		nstap		= 1;
		modelsel	= {0, 0, 0, 1, 0, 0, 0};
		If[MemberQ[{2, 4}, run], RRsmokduurind = 1];
		If[MemberQ[{3, 4}, run], (*disindrisk = 1*) disind0 = {6}]];

(* TESTING ONE CZM MODEL VERSION ON RISK FACTOR
   - SELECTION OF DISEASES (THROUGH USERINPUT)
   - SELECTION OF DIFFERENT MODEL VERSIONS (THROUGH USERINPUT)
   COMPARING RESULTS AT BASELINE
*)

	If[MemberQ[{14, 15}, test], _]; 

(* TESTING CZM ON USE OF EMPIRICAL DATA FOR DIABETICS
*)

	If[MemberQ[{29, 30, 31}, test],

		disind0	= {7};
		patientsel = 70;
		riskindd = riskind = {2};
		nscen0 = nscen = 2;
		userriskdata = 0;
		If[MemberQ[{1}, rn], userriskdata = 1];
		excessmortcond	= 3;
		mortothind	= 1];





(* COMMENT ON USER SELECTIONS OVERRULED *)

	Print[FromCharacterCode[{84, 101, 115, 116, 105, 110, 103, 58, 32, 97, 103, 101, 
				109, 105, 110, 44, 32, 97, 103, 101, 109, 97, 120, 44, 32, 101, 120, 99, 
				101, 115, 115, 109, 111, 114, 116, 99, 111, 110, 100, 44, 32, 109,
				111, 114, 116, 111, 116, 104, 105, 110, 100, 44, 32, 110, 115, 99, 101, 
				110, 40, 48, 41, 44, 32, 97, 110, 97, 108, 121, 115, 101, 44, 32, 
				110, 115, 116, 97, 112, 44, 32, 114, 105, 115, 107, 105, 110, 100, 
				40, 100, 41, 44, 32, 100, 105, 115, 105, 110, 100, 40, 48, 41, 44, 32,
				100, 105, 115, 105, 110, 100, 114, 105, 115, 107, 32, 115, 101, 108, 101,
				99, 116, 105, 111, 110, 115, 32, 111, 118, 101, 114, 114, 117, 108, 101, 100}]];

"};


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
