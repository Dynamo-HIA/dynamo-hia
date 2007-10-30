(* :Title: CZMMakeSelections *)

(* :Context: CZMAdjustData` *)

(* :Author: Rudolf T. Hoogenveen, Roel G.M. Breuls *)

(* :Summary:
   CZM adjust data routine constructs the variables for the risk factors and diseases selected *)

(* :Copyright: © 2004 by Rudolf Hoogenveen/Roel Breuls *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		1.1 September 2004, Pieter moved 'relcostsothsel'to package: CZMCeaCalculations  
		2.0 first release CZM 2005, version March 
		3.1 version March 2007 ; drawing of patients *)

(* :Keywords: risk factors, diseases, relative risks, selection *)


BeginPackage["CZMAdjustData`CZMMakeSelections`",
	{"CZMInitialization`CZMLogFile`",
	"CZMInitialization`CZMConstants`",
	"CZMImportData`CZMImportUserSelections`",
	"CZMImportData`CZMImportDemography`",
	"CZMImportData`CZMImportRiskFactors`", 
	"CZMImportData`CZMImportRelativeRisks`",
	"CZMImportData`CZMImportDiseaseData`",
	"CZMImportData`CZMImportDALYs`",
	"CZMImportData`CZMImportCosts`",
	"CZMAdjustData`CZMDataSmoothing`",
	"CZMAdjustData`CZMAdjustBeforeSelection`"}] 


riskindd::usage 	= "riksindd[[r]]: selected discretely distributed risk factors"
riskindc::usage 	= "riksindc[[r]]: selected continuously distributed risk factors"
riskinddinv::usage	= "riskinddinv[[r]]: indicator values of selected discrete risk factors"
riskinddpop::usage	= "riskinddpop[[r]]: risk factors applied to total population"
riskindddis::usage	= "riskindddis[[r]]: risk factors applied to diseases populations only"
disriskindddis::usage	= "disriskindddis[[r]]: diseases related to riskindddis"
riskdisdsel	::usage	= "riskdisdsel[[d]]: discretely distributed risk factors restricted for each selected disease"

riskDMpairsel		::usage	= "riskDMpairsel[[r,d]]: disease(-indexe)s with empirical risk factor prevalence data for patients"
DMriskpairsel		::usage	= "DMriskpairsel[[d,r]]: disease(-indexe)s with empirical risk factor prevalence data for patients"
DMriskindsel		::usage	= "DMriskindsel[[d,r]]: pointers to empirical prevalence data for patients, see DMrisk[airsel"

nrd::usage 		= "# selected discrete risk factors"
nrdpop::usage 		= "# selected discrete risk factors on population level"
nrddis::usage 		= "# selected discrete risk factors restricted to diseases, e.g. HbA1c"
nrc::usage 		= "# selected continuous risk factors"

prisksel::usage 	= "prisksel[[r,g,ri,a]]: class prevalence rates for selected risk factors"
priskincsel	::usage = "priskincsel[[r,g,ri,a]]: class prevalence rates for selected risk factors (new disease cases)"

ncrsel::usage		= "ncrsel[[r]]: number of classes for selected risk factors"
transrisksel::usage 	= "transrisksel[[r,g,ri,a]]: class transition rates for selected risk factors"
transriskindsel::usage	= "transriskindsel[[r,ri,rj]]: class transition indicator values for selected risk factors"
trstrackingsel::usage	= "trstracking1[[r,g,ri,rj,a]]: smoothed risk factor class transition rates related to tracking"
distsel::usage		= "distsel[[r,g,2,a]]: parameters of continuously distributed risk factors selected"
varnoisesel::usage	= "sdnoisesel[[r]]: standard error of random change of continuous risk factors"
a0contsel::usage	= "a0contsel[[r,g,a]]: intercept of deterministic (selected) risk factor level change"
a1contsel::usage	= "a1contsel[[r,g,a]]: regression parameter of deterministic (selected) risk factor level change"

disind::usage		= "disind[[d]]: selected diseases, order is given in the user input file"
disindinv::usage	= "disindinv[[d]]: indicator values of selected diseases"
nd::usage 		= "number of diseases (non-cancer and cancer) selected"
nondisind::usage	= "nondisind[[d]]: non-selected diseases"
pdissel::usage 		= "pdissel[[d,g,a]]: initial prevalence rates for selected diseases"
incsel::usage 		= "incsel[[d,g,a]]: incidence rates for selected diseases"
excessmortsel::usage	= "excessmortsel[[d,g,a]]: excess mortality rates for selected diseases"
causemortsel::usage 	= "causemortsel[[d,g,a]]: cause mortality rates for selected diseases"
remindsel::usage 	= "remindsel[[d]]: remission rates indicator values for selected diseases"
casefatindsel::usage 	= "casefatindsel[[d]]: case fatality indicator values for selected diseases"

RRrisksel::usage	= "RRrisksel[[r,d,g,ri,a]]: relative risks for selected discretely distributed risk factors" 
RRcontsel::usage	= "RRcontsel[[r,d,g,a]]: risk  parameters for selected continuously distributed risk factors" 
RRriskindsel::usage 	= "RRriskindsel[[r,d]]: (discrete) RR list indicator values for selected risk factors and diseases"
RRcontindsel::usage 	= "RRcontindsel[[r,d]]: (continuous) RR list indicator values for selected risk factors and diseases"
RRdisindsel::usage	= "RRdisindsel[[d,d]]: relative risk list indicator values for one disease on another disease incidence"
RRcasefatindsel::usage	= "RRcasefatindsel[[d,d]]: relative risk list indicator values for one disease on another disease case fatality"

costspatientsel::usage 	= "costspatientsel[[d,g,a]]: health care costs per patient per year for selected diseases"
costspersonothsel::usage= "costspersonothsel[[g,a]]: costs of non-modeled diseases per person"
DALYwgtsel::usage 	= "DALYwgtsel[[d,g,a]]: DALY weight coefficients for selected diseases"

ndrawinput::usage	= "# (non)random input parameter samples"
nscen::usage 		= "# user-defined scenarios including parameter draws ( = nscen0 * ndawinput )"


Begin["`Private`"]	


Print["CZMMakeSelections package is evaluated"];
printtijd;

printbug[c_] := If[(bugind == 1), Print[{"CZMMakeSelections", c}]];


(* --------------------------------------------------
		RISK FACTOR SELECTIONS
----------------------------------------------------*)

printbug["1."];

riskindc	= If[(riskcontind == 1),
			Intersection[riskindc0, riskind],
			{}];
riskindd	= Complement[riskind, riskindc];

nrd		= Length[riskindd];
nrc		= Length[riskindc];

(* DISTINGUISHING DISCRETELY DISTRIBUTED RISK FACTORS RESTRICTED TO DISEASES, E.G. HBA1C, AND POPULATION RISKS *)

printbug["2."];

riskindddis	= {};
Do[If[MemberQ[Transpose[riskdispair][[1]], riskindd[[r]]], riskindddis = {riskindddis, r}], {r, nrd}];
riskindddis	= Flatten[riskindddis];
disriskindddis	= Flatten[Table[riskdispair[[Range[Length[riskdispair]] *
					(1 - Abs[Sign[Transpose[riskdispair][[1]] - riskindd[[riskindddis]][[r]]]]), 2]],
			{r, Length[riskindddis]}]];
riskinddpop	= Complement[Range[nrd], riskindddis];

riskindd	= riskindd[[Flatten[{riskinddpop, riskindddis}]]];
riskinddinv 	= Table[0, {nrd0}];
Do[riskinddinv[[riskindd[[d]]]] = d, {d, Length[riskindd]}];

nrdpop		= Length[riskinddpop];
nrddis		= Length[riskindddis];




(* CONTINUOUSLY DISTRIBUTED RISK FACTOR DISTRIBUTION CHARACTERISTICS *)

(* CONTINUOUSLY DISTRIBUTED RISK FACTOR TRANSITION RATES *)


(* --------------------------------------------------
		DISEASE SELECTIONS
----------------------------------------------------*)

printbug["3."];

disind = Switch[disindrisk,
			1,	Union[Flatten[Table[Select[Range[nd0] Sign[Drop[RRriskind0[[riskindd[[r]]]], 1] - 1], Positive], {r, nrd}]]],
			_,	disind0];

printbug["3.1"];

disind		= Union[disind, disriskindddis];
nd		= Length[disind];

(* --------------------------------------------------
		RISK FACTOR SELECTIONS, CONTINUED
----------------------------------------------------*)
riskdisdsel	= Table[If[(riskdispairinv[[riskind[[riskindddis[[ri]]]]]] == disind[[d]]), ri, 0], {d, nd}, {ri, nrddis}];
riskdisdsel	= Table[Select[riskdisdsel[[d]], Positive], {d, nd}];

(* DISCRETELY DISTRIBUTED RISK FACTOR CLASS PREVALENCE RATES *)

printbug["2.1"];

prisksel 	= prisk1[[riskindd]];
ncrsel 		= ncr0[[riskindd]];
priskincsel	= priskinc1[[riskindd[[riskindddis]]]];


(* DISCRETELY DISTRIBUTED RISK FACTOR CLASS TRANSITION RATES *)

printbug["2.2"];

transrisksel 	= transrisk1[[riskindd]];
transriskindsel	= transriskind1[[riskindd]];

If[(trackingind == 1), trstrackingsel = trstracking1[[riskindd]]];

printbug["3.2"];

(* --------------------------------------------------
		DISEASE SELECTIONS, CONTINUED
----------------------------------------------------*)

disindinv 	= Table[0, {nd0}];
Do[disindinv[[disind[[d]]]] = d, {d, nd}];
nondisind	= Complement[Range[nd0], disind];

printbug["3.3"];

pdissel 	= pdis1[[disind]]; 
incsel 		= inc1[[disind]];
remindsel 	= remind0[[disind]];
excessmortsel 	= excessmort1[[disind]]; 
casefatindsel 	= casefatind0[[disind]]; 
causemortsel 	= causemort1[[disind]]; 


(* --------------------------------------------------
		RISK FACTOR SELECTIONS, CONTINUED
----------------------------------------------------*)

(* EMPIRICAL RISK FACTOR DISTRIBUTIONS WITHIN PATIENTS *)

If[(userriskdata >= 1),

	riskDMpairsel	= Table[Select[disindinv[[riskDMpairinv[[riskind[[r]]]]]], Positive], {r, nrd}];
	DMriskpairsel	= Table[{}, {nd}];
	Do[If[(riskDMpairsel[[r, d1]] == d), DMriskpairsel[[d]] = Flatten[{DMriskpairsel[[d]], r}]],
		{r, nrd}, {d, nd}, {d1, Length[riskDMpairsel[[r]]]}];

	DMriskindsel	= DMriskpairsel;
	Do[If[(riskDMpair[[r1, 1]] == riskind[[r]]) && (riskDMpair[[r1, 2]] == disind[[d]]) && (DMriskpairsel[[d, r2]] == r),
		DMriskindsel[[d, r2]] = r1],
		{d, nd}, {r, nrd}, {r1, Length[riskDMpair]}, {r2, Length[DMriskpairsel[[d]]]}]];


(* --------------------------------------------------
		RELATIVE RISK SELECTIONS
----------------------------------------------------*)

printbug["4."];


RRriskindsel 	= RRriskind0[[riskindd]];
RRriskindsel 	= Table[RRriskindsel[[r, Flatten[{1, disind + 1}]]], {r, nrd}];

RRrisksel 	= RRriskpresel[[riskindd]];

RRdisindsel	= RRdisind[[disind, disind]];
RRcasefatindsel	= RRcasefatind[[disind, disind]];


(* --------------------------------------------------
		DALY AND COSTS SELECTIONS
----------------------------------------------------*)

printbug["5."];

DALYwgtsel 	= DALYwgt1[[disind]]; 
costspatientsel	= costspatient1[[disind]];
costspersonothsel = costsperson1[[29]] - Plus@@costsperson1[[disind]];


(* --------------------------------------------------
  RESULTING NUMBER OF SCENARIOS APPLIED FOR SENSITIVITY ANALYSES 
----------------------------------------------------*)

printbug["6."];

ndrawinput = Switch[analyse,

	(* SENSITIVITY ANALYSES *)

	1,

	1 +						(* REFERENCE SCENARIO *)
	sensparameters[[1]] Plus@@ncrsel +		(* INITIAL (DISCRETE) RISK FACTOR PREVALENCE RATES *)
	sensparameters[[2]] *				(* (DISCRETE) RISK FACTOR TRANSITION RATES *)
		Plus@@Table[Length[transrisksel[[r, 1]]] - 1, {r, nrd}] +
	sensparameters[[3]] 2 nrc +			(* INITIAL (CONTINUOUS) RISK FACTOR PREVALENCE RATES *)
	sensparameters[[4]] 2 nrc +			(* (CONTINUOUS) RISK FACTOR TRANSITION RATES *)
	sensparameters[[5]] nd +			(* DISEASE INCIDENCE RATES UNIFORM OVER AGE*)
	sensparameters[[6]] nd +			(* DISEASE-RELATED EXCESS MORTALITY RATES UNIFORM OVER AGE *)
	sensparameters[[7]] nd +			(* CASE FATALITY RATES UNIFORM OVER AGE *)
	sensparameters[[8]] nrd +			(* DISCRETE RELATIVE RISKS UNIFORM OVER DISEASES AND AGE *)
	sensparameters[[9]] nrc +			(* CONTINUOUS RELATIVE RISKS UNIFORM OVER DISEASES AND AGE *)
	sensparameters[[10]] +				(* TRACKING OF RISK FACTORS *)
	sensparameters[[11]] +				(* RR'S OF ONE DISEASE ON ANOTHER DISEASE INCIDENCE AND PREVALENCE *)
	sensparameters[[12]] +				(* RR's OF ONE DISEASE ON ANOTHER DISEASE CASE FATALITY *)
	sensparameters[[13]] 2 RRsmokduurind +		(* SMOKING RELAPSE REGRESSION COEFFICIENTS *)
	sensparameters[[14]] 2 RRsmokduurind,		(* FORMER SMOKER DISEASE INCIDENCE REGRESSION COEFFICIENTS *)

	(* RISK FACTOR STABILITY ANALYSES *)

	2,

	Max[ncrsel],

	(* OTHER ANALYSES *)

	_,

	1

	];

nscen = nscen0 ndrawinput;


(* --------------------------------------------------
  RISK FACTOR CLASS TRANSITIONS DISTINGUISHED 
----------------------------------------------------*)

printbug["7."];

riskclasstrans 	= Table[Plus@@Flatten[

				Table[If[(transriskindsel[[r, ri, rj]] == trs),
						{ri, rj},
						{0, 0}],
					{ri, ncrsel[[r]]}, {rj, ncrsel[[r]]}],
				1],
			{r, nrd}, {trs, 2, Max[transriskindsel[[r]]]}];


(* --------------------------------------------------
		Write info to Logfile
----------------------------------------------------*)

(* PACKAGE VERSION *)

version = 3.1;

WriteString[logfile, 
		"\t" <> "Package: " <> StringReplace[Evaluate[Context[]], "`Private`"-> ", " ] 
		<> "version " <> ToString[version] <> "\n\n"];

WriteString[logfile,
		"Selected Diseases\n\n", ToString[disnames[[disind]]] <> "\n\n"];	 

WriteString[logfile,
		"Risk factor class transitions distinguished" <> "\n"];
	
	Do[	WriteString[logfile, "\t" <> risknames[[riskindd[[r]]]] <> "\n"];
		Do[	WriteString[logfile, "\t\t" <> ToString[ri + 1] <> ": " <> ToString[riskclasstrans[[r, ri, 1]]] <>
						" to " <> ToString[riskclasstrans[[r, ri, 2]]] <> "\n"],
			{ri, Length[riskclasstrans[[r]]]}],
		{r, nrd}];

End[]


Protect[Evaluate[Context[] <> "*"]]


EndPackage[]
