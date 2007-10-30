(* :Title: CZMDefineScenariossens *)

(* :Context: CZMDefineScenarios` *)

(* :Author: Rudolf Hoogenveen *)

(* :Summary:
   CZM simulation routine defines scenarios used for sensitivity analyses *)

(* :Copyright: © 2005 by Rudolf Hoogenveen *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March
		3.0 version November 2005 
		3.2 version March 2007 *)

(* :Keywords: model equations, simulation, sensitivity analyses *)


BeginPackage["CZMDefineScenarios`CZMDefineScenariossens`",
	{"CZMInitialization`CZMLogFile`",
	"CZMImportData`CZMImportUserSelections`"}] 

makescensens::usage 	= "makescensens: routine calculates parameter values for each scenario"	


Begin["`Private`"]	


Print["CZMDefineScenariossens package is evaluated"]

makescensens := {"

(* DEFAULT PARAMETER VALUES *)

If[(n == 0),

	priskscen	= prisksel;			(* INITIAL (DISCRETE) RISK FACTOR CLASS PREVALENCE RATES FOR SCENARIO *)
	transriskscen 	= transrisksel;			(* (DISCRETE) RISK FACTOR CLASS TRANSITION RATES FOR SCENARIO *)
	distscen	= distsel;			(* INITIAL (CONTINUOUS) RISK FACTOR DISTRIBUTIONAL PARAMETERS *)
(*	a0contscen	= a0contsel;			(* (CONTINUOUS) RISK FACTOR TRANSITION RATES FOR SCENARIO (INTERCEPT) *)
	a1contscen	= a1contsel;			(* (CONTINUOUS) RISK FACTOR TRANSITION RATES FOR SCENARIO (REGRESSION) *)
*)	incscen		= incsel;			(* DISEASE INCIDENCE RATES FOR SCENARIO *)
	excessmortscen	= excessmortsel;		(* DISEASE EXCESS MORTALITY RATES FOR SCENARIO *)
	casefatscen	= casefat1;			(* DISEASE CASE FATALITY RATES FOR SCENARIO *)
	RRriskscen	= RRriskseladj;			(* (DISCRETE) RR VALUES FOR SCENARIO *)
(*	RRcontscen	= RRcontsel;			(* (CONTINUOUS) RR VALUES FOR SCENARIO *)
	trackingscen	= 1;				(* TRACKING OF RISK FACTORS *)
*)	RRdisscen	= RRdisadj;			(* RELATIVE RISKS FOR ONE DISEASE ON ANOTHER DISEASE INCIDENCE *)
	RRcasefatscen	= RRcasefat1;			(* RELATIVE RISKS FOR ONE DISEASE ON ANOTHER DISEASE CASE FATALITY *)

	If[(RRsmokduurind == 1),			
		relapsecoeffscen = relapsecoeff;	(* SMOKING RELAPSE REGRESSION COEFFICIENTS *)
		logRRsmokduurscen = logRRsmokduur];	(* FORMER SMOKER DISEASE INCIDENCE REGRESION COEFFICIENTS *)

nparameters = {	sensparameters[[1]] Plus@@ncrsel,
		sensparameters[[2]] Plus@@Table[Length[transriskscen[[r1, 1]]] - 1, {r1, nrd}],
		sensparameters[[3]] 2 nrc,
		sensparameters[[4]] 2 nrc,
		sensparameters[[5]] nd,
		sensparameters[[6]] nd,
		sensparameters[[7]] nd,
		sensparameters[[8]] nrd,
		sensparameters[[9]] nrc,
		sensparameters[[10]] 1,
		sensparameters[[11]] 1,
		sensparameters[[12]] 1,
		sensparameters[[13]] 2,
		sensparameters[[14]] 2};

nparameters = FoldList[Plus, 0, nparameters];

(* NEW INITIAL (DISCRETE) RISK FACTOR CLASS PREVALENCE RATE VALUES *)

If[(sensparameters[[1]] == 1),

	Do[If[(Mod[scen - 1, ndrawinput] == nparameters[[1]] + Plus@@ncrsel[[Range[r - 1]]] + ri),

		dp1	= Max0[deltasens / (1 - (1 + deltasens) priskscen[[r, Range[ng], ri]])];

		dp2	= dp1 priskscen[[r, Range[ng], ri]] / (1 - priskscen[[r, Range[ng], ri]] + eps);

		priskscen[[r, Range[ng], ri]] *= (1 + dp1);

		Do[If[(rj != ri), priskscen[[r, Range[ng], rj]] *= (1 - dp2)], {rj, ncrsel[[r]]}]],

		{r, nrd}, {ri, ncrsel[[r]]}]];

(* DELTA (DISCRETE) RISK FACTOR CLASS TRANSITION RATE VALUES *)

If[(sensparameters[[2]] == 1),

	Do[If[(Mod[scen - 1, ndrawinput] == nparameters[[2]] + Plus@@Table[Length[transrisksel[[r1, 1]]] - 1, {r1, r - 1}] + ri),

		transriskscen[[r, Range[ng], 1 + ri]] *= (1 + deltasens)],

		{r, nrd}, {ri, Length[transriskscen[[r, 1]]] - 1}]];

(* DELTA INITIAL (CONTINUOUS) RISK FACTOR CLASS PREVALENCE RATES (MU & SIGMA) VALUES *)

If[(sensparameters[[3]] == 1),

	Do[If[(Mod[scen - 1, ndrawinput] == nparameters[[3]] + r),

		distscen[[r, Range[ng], 1]] *= (1 + deltasens)],

		{r, nrc}];

	Do[If[(Mod[scen - 1, ndrawinput] == nparameters[[3]] + nrc + r),

		distscen[[r, Range[ng], 2]] *= (1 + deltasens)],

		{r, nrc}]];

(* DELTA INITIAL (CONTINUOUS) RISK FACTOR CLASS TRANSITION RATES (INTERCEPT A0 & REGRESSION A1) VALUES *)

If[(sensparameters[[4]] == 1),

	Do[If[(Mod[scen - 1, ndrawinput] == nparameters[[4]] + r),

		a0contscen[[r]] *= (1 + deltasens)],

		{r, nrc}];

	Do[If[(Mod[scen - 1, ndrawinput] == nparameters[[4]] + nrc + r),

		a1contscen[[r]] *= (1 + deltasens)],

		{r, nrc}]];

(* DELTA DISEASE INCIDENCE RATE VALUES *)

If[(sensparameters[[5]] == 1),

	Do[If[(Mod[scen - 1, ndrawinput] == nparameters[[5]] + d),

		incscen[[d]] *= (1 + deltasens)],

		{d, nd}]];

(* DELTA DISEASE-RELATED EXCESS MORTALITY RATE VALUES *)

If[(sensparameters[[6]] == 1),

	Do[If[(Mod[scen - 1, ndrawinput] == nparameters[[6]] + d),

		excessmortscen[[d]] *= (1+ deltasens)],

		{d, nd}]];

(* DELTA DISEASE-RELATED CASE FATALITY RATES *)

If[(sensparameters[[7]] == 1),

	Do[If[(Mod[scen - 1, ndrawinput] == nparameters[[7]] + d),

		If[(casefatindsel[[d]] > 1), casefatscen[[casefatindsel[[d]]]] *= (1+ deltasens)]],

		{d, nd}]];

(* DELTA RELATIVE RISK VALUES (DISCRETE) VALUES *)

If[(sensparameters[[8]] == 1),

	Do[If[(Mod[scen - 1, ndrawinput] == nparameters[[8]] + r),

		Do[	RRriskscen[[r, d, g, ri, a]] = RRriskscen[[r, d, g, ri, a]]^(1 + deltasens),
					
			{d, 2, Length[RRriskscen[[r]]]}, {g, ng}, {ri, ncrsel[[r]]}, {a, na1}]],

		{r, nrd}]];

(* DELTA RELATIVE RISK VALUES (CONTINUOUS) *)

If[(sensparameters[[9]] == 1),

	Do[If[(Mod[scen - 1, ndrawinput] == nparameters[[9]] + r),

		RRcontscen[[r]] = RRcontscen[[r]]^(1 + deltasens)],

		{r, nrc}]];

(* DELTA RISK FACTOR TRACKING VALUES *)

If[(sensparameters[[10]] == 1) && (Mod[scen - 1, ndrawinput] == nparameters[[10]] + 1),

		trackingscen *= (1 + deltasens)];

(* DELTA RELATIVE RISK VALUES VALUES ONE DISEASE ON ANOTHER DISEASE INCIDENCE *)

If[(sensparameters[[11]] == 1) && (Mod[scen - 1, ndrawinput] == nparameters[[11]] + 1),

		Do[	RRdisscen[[d, g]] = RRdisadj[[d, g]]^(1 + deltasens), {d, 2, Length[RRdisscen]}, {g, ng}]];

(* DELTA RELATIVE RISK VALUES VALUES ONE DISEASE ON ANOTHER DISEASE CASE FATALITY *)

If[(sensparameters[[12]] == 1) && (Mod[scen - 1, ndrawinput] == nparameters[[12]] + 1),

		Do[	RRcasefatscen[[d, g]] = RRcasefat1[[d, g]]^(1 + deltasens), {d, 2, Length[RRcasefat1]}, {g, ng}]];

(* DELTA RELAPSE REGRESSION COEFFICIENTS FOR FORMER SMOKERS *)

If[(sensparameters[[13]] == 1),

	If[(Mod[scen - 1, ndrawinput] == nparameters[[13]] + 1),

			relapsecoeffscen[[Range[ng], 1]] *= (1 + deltasens)];

	If[(Mod[scen - 1, ndrawinput] == nparameters[[13]] + 2),

			relapsecoeffscen[[Range[ng], 2]] *= (1 + deltasens)]];

(* DELTA DISEASE INCIDENCE REGRESSION COEFFICIENTS FOR FORMER SMOKERS *)

If[(sensparameters[[14]] == 1),

	If[(Mod[scen - 1, ndrawinput] == nparameters[[14]]+ 1),

			Do[logRRsmokduurscen[[d, g, 1]] -= deltasens, {d, 2, Length[logRRsmokduurscen]}, {g, ng}]];

	If[(Mod[scen - 1, ndrawinput] == nparameters[[14]] + 2),

			Do[logRRsmokduurscen[[d, g, 2]] *= (1 + deltasens), {d, 2, Length[logRRsmokduurscen]}, {g, ng}]]];


(* NEW SCENARIO VALUES *)

hscen	= Floor[(scen - 1) / ndrawinput];

If[(hscen == 1),
		Do[	priskscen[[1, g, ncrsel[[1]] - 1]] += priskscen[[1, g, ncrsel[[1]]]];
			priskscen[[1, g, ncrsel[[1]]]] *= 0,
			{g, ng}]];

];

"};

priskscenind		= sensparameters[[1]];
transriskscenind	= sensparameters[[2]];
distscenind		= sensparameters[[3]];
a0scenind		= sensparameters[[4]];
a1scenind		= sensparameters[[4]];
incscenind		= sensparameters[[5]];
excessmortscenind	= sensparameters[[6]];
casefatscenind		= sensparameters[[7]];
RRriskscenind		= sensparameters[[8]];
RRcontscenind		= sensparameters[[9]];
trackingmultind		= sensparameters[[10]];
RRdisscenind		= sensparameters[[11]];
RRcasefatscenind	= sensparameters[[12]];

If[(RRsmokduurind == 1),

	relapsecoeffscenind	= sensparameters[[13]];
	logRRsmokduurscenind	= sensparameters[[14]]];

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
