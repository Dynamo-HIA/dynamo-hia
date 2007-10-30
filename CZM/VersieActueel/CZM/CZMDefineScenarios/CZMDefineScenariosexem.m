(* :Title: CZMDefineScenariosexem *)

(* :Context: CZMDefineScenarios` *)

(* :Author: Rudolf Hoogenveen *)

(* :Summary:
   CZM simulation routine defines exemple scenarios *)

(* :Copyright: © 2005 by Rudolf Hoogenveen *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March
		3.0 version November 2005 
		3.1 version March 2007; initial distribution BMI. HbA1c *)

(* :Keywords: model equations, simulation, scenarios *)


BeginPackage["CZMDefineScenarios`CZMDefineScenariosexem`",
	{"CZMInitialization`CZMLogFile`"}] 

makescenexem::usage 	= "makescenexem: routine calculates parameter values for each scenario"	


Begin["`Private`"]	


Print["CZMDefineScenariosexem package is evaluated"]

makescenexem := {"

(* PROPORTIONAL TRANSITIONS FROM HIGHER TO LOWER CLASSES *)

makereduct[r_, reduct_] := Block[{}, 

	htransrisk = DiagonalMatrix[Table[1, {ncrsel[[r]]}]];

	If[(riskindd[[r]] == 1),

		htransrisk[[3, 2]] += reduct;
        	htransrisk[[2, 2]] -= reduct

		];

	If[(riskindd[[r]] > 1),

		Do[	htransrisk[[ri, ri + 1]] += reduct;
        		htransrisk[[ri + 1, ri + 1]] -= reduct,

			{ri, ncrsel[[r]] - 1}]

		];

      	htransrisk];


(* DEFAULT PARAMETER VALUES *)

If[(n == 0),

	priskscen	= prisksel;			(* INITIAL (DISCRETE) RISK FACTOR CLASS PREVALENCE RATES FOR SCENARIO *)
	transriskscen 	= transrisksel;			(* (DISCRETE) RISK FACTOR CLASS TRANSITION RATES FOR SCENARIO *)
	distscen	= distsel;			(* INITIAL (CONTINUOUS) RISK FACTOR DISTRIBUTIONAL PARAMETERS *)
	a0contscen	= a0contsel;			(* (CONTINUOUS) RISK FACTOR TRANSITION RATES FOR SCENARIO (INTERCEPT) *)
	a1contscen	= a1contsel;			(* (CONTINUOUS) RISK FACTOR TRANSITION RATES FOR SCENARIO (REGRESSION) *)
	incscen		= incsel;			(* DISEASE INCIDENCE RATES FOR SCENARIO *)
	excessmortscen	= excessmortsel;		(* DISEASE EXCESS MORTALITY RATES FOR SCENARIO *)
	casefatscen	= casefat1;			(* DISEASE CASE FATALITY RATES FOR SCENARIO *)
	RRriskscen	= RRriskseladj;			(* (DISCRETE) RR VALUES FOR SCENARIO *)
	RRcontscen	= RRcontsel;			(* (CONTINUOUS) RR VALUES FOR SCENARIO *)
	trackingscen	= 1;				(* TRACKING OF RISK FACTORS *)
	RRdisscen	= RRdisadj;			(* RELATIVE RISKS FOR ONE DISEASE ON ANOTHER DISEASE INCIDENCE *)
	RRcasefatscen	= RRcasefat1;			(* RELATIVE RISKS FOR ONE DISEASE ON ANOTHER DISEASE CASE FATALITY *)

	If[(RRsmokduurind == 1),			
		relapsecoeffscen = relapsecoeff;	(* SMOKING RELAPSE REGRESSION COEFFICIENTS *)
		logRRsmokduurscen = logRRsmokduur];	(* FORMER SMOKER DISEASE INCIDENCE REGRESION COEFFICIENTS *)

	];

(* --------------------------------------------------
	EXAMPLE SCENARIOS

	SOME NOTES:	SCENARIO x CORREPONDS WITH Mod[scen - 1, nscen0] == x-1
			ALL RISK FACTORS AND DISEASES ARE ORDERED WITHIN SELECTIONS
			ALL PARAMETERS ARE SPECIFIED BY (AT LEAST) GENDER AND AGE-YEAR
			DEFAULT VALUES ARE BASED ON INPUT VALUES 
			IN CASE OF EXTREME SCENARIOS (PROPORTIONS OF REDUCTION) INPUT VALUES MAY BECOME ERRONEOUS, E.G.
				RATES<0 OR >1
----------------------------------------------------*)


If[(n < 0),

	(* EXAMPLE SCENARIO 2 FOR RISK FACTOR CLASS TRANSITION RATES *)

	If[(Mod[scen - 1, nscen0] == 1),

		(* REDUCTION OF RATE VALUES FOR RISK FACTOR 1 CLASS TRANSITION trs (SEE INPUT FILE) WITH PROPORTION reduct
			UNIFORM OVER GENDER AND AGE 
		   trs=1 CORRESPONDS WITH VECTOR OF ZERO VALUED RATES APPLIED TO EVERY NON-DEFINED TRANSITION
		   trs>1 CORRESPONDS WITH RATE VALUES FOR TRANSITION trs-1 (SEE INPUT FILE) *)

		Table[If[(riskindd[[r]] == 4),

			transriskscen[[r, Range[ng], 2]] += .024;
			transriskscen[[r, Range[ng], 4]] += .030],

			{r, nrd}]

		]

	];

If[(n == 0),

	(* EXAMPLE SCENARIO 3 FOR INITIAL RISK FACTOR CLASS PREVALENCE RATES *)

	If[(Mod[scen - 1, nscen0] == 1),

		(* PROPORTION reduct OF PREVALENCE NUMBERS MOVE TO NEXT LOWER CLASS UNIFORM OVER GENDER AND AGE *)
(*
		reduct	= .5;	

		Do[priskscen[[r, g]] = makereduct[r, reduct].priskscen[[r, g]], {r, nrd}, {g, ng}];

		Do[priskscen[[1, g, 1, a]] = 1, {g, ng}, {a, na1}];

		Do[priskscen[[1, g, ri, a]] = 0, {g, ng}, {ri, 2, ncrsel[[1]]}, {a, na1}];
*)

		Do[	If[(riskindd[[r]] == 4)||(riskindd[[r]] == 12), (* INDEX VALUE WAS 4 INSTEAD OF 5 *)
				priskscen[[r]] *= 0;
				priskscen[[r, Range[ng], 1]] += .4;
				priskscen[[r, Range[ng], 2]] += .45;
				priskscen[[r, Range[ng], 3]] += .15];

			If[(riskindd[[r]] == 1),
				priskscen[[r, Range[ng], 3]] += priskscen[[r, Range[ng], 2]];
				priskscen[[r, Range[ng], 2]] *= 0];

			If[(riskindd[[r]] == 2),
				htransrisk	= DiagonalMatrix[Table[1.,{ncrsel[[r]]}]];
					Do[htransrisk[[4 + ri,{ri, 4 + ri}]] = {1.,0.},{ri,4}];
					priskscen[[r]] = Table[Plus@@Table[htransrisk[[rj,ri]] priskscen[[r,g,rj]],
								{rj,ncrsel[[r]]}],
									{g,ng},{ri,ncrsel[[r]]}];
				];


			If[(riskindd[[r]] == 3) && (ncrsel[[r]] == 8),

				Do[	priskscen[[r, g, ri]] += priskscen[[r, g, ri + 4]];
					priskscen[[r, g, 4 + ri]] *= 0,
					{g, ng}, {ri, 4}]],

			{r, nrd}];

		];

	];


If[(n < 0),	
	(* EXAMPLE SCENARIO 4 FOR DISEASE INCIDENCE RATES *)

	If[(Mod[scen - 1, nscen0] == 3),

		(* REDUCTION OF DISEASES dissel INCIDENCE RATES WITH PROPORTION reduct UNIFORM OVER GENDER AND AGE *)

		reduct 	= .1;

		dissel	= {1};

		Do[incscen[[dissel[[d]], g]] = (1 - reduct) incscen[[dissel[[d]], g]], {d, Length[dissel]}, {g, ng}];

		];

	(* EXAMPLE SCENARIO 5 FOR DISEASE EXCESS MORTALITY RATES *)

	If[(Mod[scen - 1, nscen0] == 4),

		(* REDUCTION OF DISEASES dissel EXCESS MORTALITY RATES WITH PROPORTION reduct UNIFORM OVER GENDER AND AGE *)

		reduct 	= .1;

		dissel	= {1};

		Do[excessmortscen[[dissel[[d]], g]] = (1 - reduct) excessmortscen[[dissel[[d]], g]], {d, Length[dissel]}, {g, ng}];

		];
		
	(* EXAMPLE SCENARIO 6 FOR RELATIVE RISKS *)

	If[(Mod[scen - 1, nscen0] == 5),

		(* REDUCTION OF RR'S FOR ALL RISK FACTOR CLASSES (EXCEPT 1ST REFERENCE CLASS) FOR DISEASES dissel WITH PROPORTION reduct
			 UNIFORM OVER GENDER AND AGE *)

		reduct	= .1;

		dissel	= {1};

		Do[RRriskscen[[1, RRriskindsel[[1, dissel[[d]] + 1]], g, ri]] =
			(1 - reduct) RRriskscen[[1, RRriskindsel[[1, dissel[[d]] + 1]], g, ri]],
			{d, Length[dissel]}, {g, ng}, {ri, 2, ncrsel[[1]]}]

		];


];

"};

priskscenind		= 1;
transriskscenind	= 0;
distscenind		= 0;
a0scenind		= 0;
a1scenind		= 0;
incscenind		= 0;
excessmortscenind	= 0;
casefatscenind		= 0;
RRriskscenind		= 0;
RRcontscenind		= 0;
trackingmultind		= 0;
RRdisscenind		= 0;
RRcasefatscenind	= 0;

If[(RRsmokduurind == 1),

	relapsecoeffscenind	= 0;
	logRRsmokduurscenind	= 0];

WriteString[logfile,

	"Scenario Definitions\n\n",

		"\t" <> "nscen: " 		<> ToString[nscen] 		<> "\n\n",
		"\t" <> "priskscenind: " 	<> ToString[priskscenind] 	<> "\n",
		"\t" <> "transriskscenind: " 	<> ToString[transriskscenind] 	<> "\n",
		"\t" <> "distscenind: " 	<> ToString[distscenind] 	<> "\n",
		"\t" <> "a0scenind: " 		<> ToString[a0scenind] 		<> "\n",
		"\t" <> "a1scenind: " 		<> ToString[a1scenind] 		<> "\n",
		"\t" <> "incscenind: " 		<> ToString[incscenind] 	<> "\n",
		"\t" <> "excessmortscenind: " 	<> ToString[excessmortscenind] 	<> "\n",
		"\t" <> "casefatscenind: " 	<> ToString[casefatscenind] 	<> "\n",
		"\t" <> "RRriskscenind: " 	<> ToString[RRriskscenind] 	<> "\n",
		"\t" <> "RRcontscenind: " 	<> ToString[RRriskscenind] 	<> "\n",
		"\t" <> "trackingmultind: " 	<> ToString[trackingmultind] 	<> "\n",
		"\t" <> "RRdisscenind: " 	<> ToString[RRdisscenind] 	<> "\n",
		"\t" <> "RRcasefatscenind: " 	<> ToString[RRcasefatscenind] 	<> "\n",
		"\t" <> "relapsecoeffscenind: "	<> ToString[relapsecoeffscenind] <> "\n",
		"\t" <> "logRRsmokduurscenind: " <> ToString[logRRsmokduurscenind] <> "\n\n"
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
