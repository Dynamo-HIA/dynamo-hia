(* :Title: CZMImportUserSelections *)

(* :Context: CZMImportData` *)

(* :Author: Rudolf  T. Hoogenveen, Roel G.M. Breuls *)

(* :Summary:
   CZM package with functions to import the user selections of input*)

(* :Copyright: © 2004 by Rudolf Hoogenveen/Roel Breuls *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March, update menus
		3.0 version november 2005 
		3.1 version March 2007 *)

(* :Keywords: user selections, import *)

BeginPackage["CZMImportData`CZMImportUserSelections`",
	{"CZMMain`CZMMain`",
	"CZMInitialization`CZMLogFile`",
	"CZMInitialization`CZMDefaultFileNames`",
	"CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`",
	"CZMDefineScenarios`CZMDefineRuns`"}]

risknames::usage 		= "risknames[[r]]: names of all risk factors distinguished"
nrd0::usage 			= "total number of risk factors distinguished"
nrc0::usage 			= "total number of continuously distributed risk factors distinguished"
riskind::usage 			= "riksind[[r]]: selected risk factors, order is given in the user input file, seel also risknames"
riskindc0::usage 		= "riksindc0[[r]]: continuously distributed risk factors"
riskindcinv::usage		= "riskindcinv[[r]]: order numbers of continuously distributed risk factors" 	

disnamesnoncancers::usage 	= "disnamesnoncancers[[d]]: names of all non-cancer chronic diseases distinguished"
disnamescancers::usage 		= "disnamescancers[[d]]: names of all forms of cancer distinguished"
disnames::usage 		= "disnames[[d]]: names of all implemented diseases, see also disnamesnoncancers and disnamescancers"
ndnoncanc::usage 		= "number of non-cancer diseases"
ndcancers::usage 		= "number of forms of cancer"
nd0::usage 			= "total number of diseases, equals ndnoncanc + ndcancers"
disind0::usage 			= "disind0[[d]]: selected diseases, order is given in the user input file, see also disnames"
disindrisk::usage		= "selection parameter indicating selection of all diseases related to risk factor (1) or not (0)"

agemin::usage			= "selected minimum age value"
agemax::usage			= "selected maximum age value"
agesel::usage 			= "agesel[[a]]: initial age range of the cohort used in the simulations, equals (see) agemin .. agemax"
agesel1::usage 			= "agesel1[[a]]: indicator variable that describes the age-years selected, see agesel"
agerange::usage			= "agerange[[a]]: life course age range of the cohort, equals agemin .. agemax + nstap"
migpopind::usage 		= "selection parameter indicating whether migration is included (1) or not (0)"
birthind::usage 		= "selection parameter indicating whether new borns are included (1) or not (0)"
patientsel::usage		= "restricted to specific disease patient population"

mortothind::usage		= "selection parameter indicating mortality effects through non-modeled disease (1) or not (0)"
excessmortcond::usage		= "selection parameter indicating way of adjusting excess mortality rates for double counting (1,2)"
RRsmokduurind::usage		= "selection parameter indicating use of former smoking RR's dependent on time since cessation (1) or not"
riskcontind::usage		= "selection parameter indicating use of continuously distributed risk factors (1) or not (0)"
trackingind::usage		= "selection parameter indicating use of tracking of individual risk factors (1) or not (0)"
userriskdata::usage		= "selection parameter indicating using user-provided risk factor prevalence data (1) or not"

modelnames::usage		= "modelnames[[m]]: names of different CZM model versions available"
nmodel::usage			= "number of different CZM model versions, see also modelnames"
modelsel::usage			= "modelsel[[m]]: selected CZM model versions, seel also modelnames"
compareselect::usage		= "selection parameter indicating comparing different selections (runs) (1) or not (0)"

nstap::usage 			= "selected number of time steps in the simulation"
nscen0::usage 			= "selected number of user-defined scenarios excluding random parameter drawings"
analyse::usage			= "selected type of analyses: 0: standard, 1: sensitivity analyses, 2: risk factor tracking"
sensparameters::usage		= "model parameters selected for sensitivity analysis"

bugind::usage			= "indicator of printing debugging information (1) or not (0)"
ageclssel::usage		= "ageclssel: fixed age class for presentation over time"

seqPAR::usage			= "seqPAR[[z]]: sequence of scenarios for PAR calculations"


Begin["`Private`"]	


Print["CZMImportUserSelections package is evaluated"];

printbug[c_] 	:= If[(bugind == 1), Print[{"CZMImportUserSelections", c}]];
printtijd	:= If[(bugind == 1), Print[{"CPU time ", TimeUsed[]}]];
bugind 		= 1;

appltype	= StringTake[ToString[Global`application], {8, 11}];

input		= ReadList[Global`inputpath <> userinput, Word, WordSeparators -> {"/t", "="}];

(* s0 to s8 denote the position of the KEYWORDS in the userinput file *)

kop	= {	"DEMOGRAPHIC CONSTANTS SELECTED",
		"EPIDEMIOLOGICAL CONSTANTS SELECTED",
		"MODEL VERSIONS SELECTED",
		"SCENARIO CHARACTERISTICS SELECTED",
		"RISK FACTORS SELECTED",
		"CHRONIC DISEASES SELECTED",
		"NON-CANCER",
		"CANCER",
		"END"};

s0	= Flatten[Position[input, kop[[1]]]][[1]];
s1	= Flatten[Position[input, kop[[2]]]][[1]];
s2	= Flatten[Position[input, kop[[3]]]][[1]];
s3	= Flatten[Position[input, kop[[4]]]][[1]];
s4 	= Flatten[Position[input, kop[[5]]]][[1]];
s5 	= Flatten[Position[input, kop[[6]]]][[1]];
s6 	= Flatten[Position[input, kop[[7]]]][[1]];
s7 	= Flatten[Position[input, kop[[8]]]][[1]];
s8 	= Flatten[Position[input, kop[[9]]]][[1]];


(* -----------------------------------------------
           CONSTANTS
   -----------------------------------------------*)

printbug["1."];


(* DEMOGRAPHIC SELECTIONS *)

printbug["1.1"];

constants1 = ToExpression[Take[input, {s0 + 2, s1 - 1, 2}]];

agemin			= constants1[[1]] + 1;
agemax			= Minc[constants1[[2]] + 1, na1];

migpopind		= constants1[[3]];
birthind		= constants1[[4]];
patientsel		= constants1[[5]];

naam1			= {"agemin", "agemax", "migpopind", "birthind", "patientsel"};


(* EPIDEMIOLOGICAL SELECTIONS *)

printbug["1.2"];

constants2 = ToExpression[Take[input, {s1 + 2, s2 - 1, 2}]];

mortothind		= constants2[[1]];
excessmortcond		= constants2[[2]];
RRsmokduurind		= constants2[[3]];

userriskdata		= constants2[[4]];

riskcontind		= 0;
trackingind		= 0;

If[(excessmortcond == 2),
	Print["if adjusting double-counted mortality numbers using Stat Netherlands mortality data (excessmortcond=2), " <>
		"mortality effect through other causes allowed (thus: mortothind = 1)"];
	mortothind = 1];									(* DEFAULT SELECTION *)

naam2			= {"mortothind", "excessmortcond", "RRsmokduurind", "riskcontind", "trackingind", "userriskdata"};
		

(* MODEL VERSIONS SELECTED *)

printbug["1.3"];

modelnames 		= Take[input, {s2 + 1, s3 - 1, 2}]; 
modelsel		= ToExpression[Take[input, {s2 + 2, s3 - 1, 2}]];

 
If[(modelsel[[5]] == 1) && (patientsel == 0), Print["model 5 is only defined on patient population (thus: modelsel[[5]] = 0)"];
	modelsel[[5]] = 0];									(* DEFAULT SELECTION *)

nmodel			= Length[modelnames];


(* SCENARIO CHARACTERISTICS SELECTED *)

printbug["1.4"];

constants4 = ToExpression[Take[input, {s3 + 2, s4 - 1, 2}]];

nstap			= constants4[[1]];
nscen0			= constants4[[2]];	
analyse			= constants4[[3]];

naam4			= {"nstap", "nscen", "analyse"};


(* -----------------------------------------------
           RISK FACTORS
   -----------------------------------------------*)

printbug["1.5"];

risknames		= Take[input, {s4 + 1, s5 - 1, 2}];
riskind1		= ToExpression[Take[input, {s4 + 2, s5 - 1, 2}]]; 
riskind			= Flatten[Position[ToExpression[riskind1], 1]];
riskindc0		= {2, 3, 4, 5};
riskindcinv		= Table[0, {Length[risknames]}];
Do[riskindcinv[[riskindc0[[r]]]] = r, {r, Length[riskindc0]}];

nrd0			= Length[risknames]; 
nrc0			= Length[riskindc0];


(* -----------------------------------------------
           DISEASES
   -----------------------------------------------*)

printbug["1.6"];
disindrisk		= ToExpression[Take[input, {s5 + 2, s6 - 1, 2}]][[1]]; 

disnamesnoncancers 	= Take[input, {s6 + 1, s7 - 1, 2}]; 
ndnoncanc		= Length[disnamesnoncancers];
disindnoncanc		= ToExpression[Take[input, {s6 + 2, s7 - 1, 2}]]; 

disnamescancers 	= Take[input, {s7 + 1, s8 - 1, 2}]; 
ndcancers		= Length[disnamescancers];
disindcanc		= ToExpression[Take[input, {s7 + 2, s8 - 1, 2}]];

disnames		= Join[disnamesnoncancers, disnamescancers];

disind0			= Join[disindnoncanc, disindcanc];

disind0			= Flatten[Position[ToExpression[disind0], 1]];

nd0			= ndnoncanc + ndcancers;


(* -----------------------------------------------
           EXCLUDING JOINT MODELS IF RISK FACTORS ARE SMOKING AND/OR BMI AND ALL RELATED DISEASES ARE SELECTED
   -----------------------------------------------*)
printbug["1.7"];

If[(disindrisk == 1) && (MemberQ[riskind, 1]||MemberQ[riskind,4]),
	Print["if selecting all smoking and/or BMI related diseases , then all joint models are excluded (thus: modelsel[[2,3,4]]=0)"];
	modelsel[[{2, 3,4}]] = 0];								(* DEFAULT SELECTION *)


(* -----------------------------------------------
           INTERACTIVE PARAMETER INPUT
   -----------------------------------------------*)
			

(* -----------------------------------------------
           OVERRULING SELECTIONS IN EXTRA MODEL RUNS
   -----------------------------------------------*)

If[(appltype != "outp"),

printbug["3."];

	ToExpression[makerun];

printbug["3.1"];

	agesel			= Range[agemin, agemax];
	agesel1 		= Table[0, {na1}]; 
	agesel1[[agesel]] 	= 1; 
	agerange 		= Range[agemin, Minc[agemax + nstap, na1]];

(* DEFAULT INDICATOR VALUES IN CASE OF NON-FULL POPULATION *)

printbug["3.2"];

	If[((agemax - agemin) < 85) || ((Quotient[patientsel, 10] > 0) && (Mod[patientsel, 10] == 0)),
		Print["no migration or newborns in case of restriction of age range or (diseased) population 
				(thus: migpopind = birthind = 0)"];
		migpopind	= 0;								(* DEFAULT SELECTION *)
		birthind	= 0
		];

printbug["3.3"];

	If[(RRsmokduurind == 1),
		Print["risk factor smoking selected in case of duration dependent selected (thus: riskind[[1]] = 1)"];
		riskind = Union[{1}, riskind]];							(* DEFAULT SELECTION *)

	];

(* -----------------------------------------------
           READING SELECTIONS FROM FILE
   -----------------------------------------------*)

printbug["4."];

If[(appltype == "outp"),

printbug["4.1"];

	Do[sel = Read[Global`resfile], {1 + 2 (run - 1) + 1}];
	
printbug["4.2"];

	
	riskindd	= sel[[1]];
	disind		= sel[[2]];
	excessmortcond	= sel[[3]];
	mortothind	= sel[[4]];
	agemin		= sel[[5]];
	agemax		= sel[[6]];
	modelsel	= sel[[7]];
	nstap		= sel[[8]];
	nscen0 = nscen	= sel[[9]];

	nrd		= Length[riskindd];
	nd		= Length[disind];				
	agerange 	= Range[agemin, Minc[agemax + nstap, na1]];
	analyse		= 0;

	Print["standard analysis if only output results are presented from former runs (thus: analyse = 0)"]

	];


(* -----------------------------------------------
           MODEL INPUT PARAMETERS FOR SENSITIVITY ANALYSIS
   -----------------------------------------------*)

printbug["5."];

sensparameters = Table[0, {14}];

If[(analyse == 1),

printbug["5.1"];

	input = ReadList[Global`inputpath <> "sensinput011205.txt", Word, WordSeparators -> {"/t", "="}];

	(* s0 to s1 denote the position of the KEYWORDS in the userinput file *)

	s0	= Flatten[Position[input, "MODEL INPUT PARAMETERS SELECTED"]][[1]];
	s1 	= Flatten[Position[input, "END"]][[1]];

	sensparameters = ToExpression[Take[input, {s0 + 2, s1 - 1, 2}]];

	If[(disindnoncanc[[1]] == 0) && (disindnoncanc[[4]] == 0),
		Print["for sensitivity analysis, no disease selected with case fatality (thus: sensparameters[[{7,12}]] = 0)"];
		sensparameters[[{7, 12}]] *= 0						(* DEFAULT SELECTION *)
		];


	If[(riskcontind == 0),
		Print["for sensitivity analysis, no continuous risk factors selected (thus: sensparameters[[{3,4,9}]] = 0)"];
		sensparameters[[{3, 4, 9}]] = 0						(* DEFAULT SELECTION *)
		];
	
	If[(trackingind == 0),
		Print["for sensitivity analysis, no tracking selected (thus: sensparameters[[10]] = 0)"];
		sensparameters[[10]] = 0						(* DEFAULT SELECTION *)
		];

	If[(RRsmokduurind == 0),
		Print["for sensitivity analysis, no duration dependent smoking events (thus: sensparameters[[{13,14}]] = 0)"];
		sensparameters[[{13, 14}]] *= 0						(* DEFAULT SELECTION *)
		],

	sensparameters = Table[0, {14}]

	];
	

(* --------------------------------------------------
		Write info to Logfile
----------------------------------------------------*)

(* PACKAGE VERSION *)

version = 3.1;

WriteString[logfile, 
		"\t" <> "Package: " <> StringReplace[Evaluate[Context[]], "`Private`"-> ", " ] 
		<> "version " <> ToString[version] <> "\n\n"];

WriteString[logfile,

	"Selected Risk Factors \n\n",

		"\t" <> ToString[risknames[[riskind]]] 			<> "\n\n",

	"Demographic Selections\n\n",

		"\t" <> "agemin: " 		<> ToString[agemin] 		<> "\n",
		"\t" <> "agemax: " 		<> ToString[agemax] 		<> "\n",
		"\t" <> "migration: " 		<> ToString[migpopind] 		<> "\n",
		"\t" <> "birth: " 		<> ToString[birthind] 		<> "\n",
		"\t" <> "restricted to disease: " <> ToString[patientsel] 	<> "\n\n",

	"Epidemiologic Selections\n\n",

		"\t" <> "mortothind: " 		<> ToString[mortothind] 	<> "\n",	
		"\t" <> "excessmortcond: " 	<> ToString[excessmortcond] 	<> "\n",	
		"\t" <> "RRsmokduurind: " 	<> ToString[RRsmokduurind] 	<> "\n",
		"\t" <> "riskcontind: " 	<> ToString[riskcontind] 	<> "\n",
		"\t" <> "trackingind: " 	<> ToString[trackingind] 	<> "\n",
		"\t" <> "userriskdata: " 	<> ToString[userriskdata] 	<> "\n\n",
		
	"Model Version Selections\n\n",

		modelnames[[Select[Range[nmodel] modelsel, Positive]]] 	<> "\n\n",
		
	"Scenario Characteristics Selections\n\n",

		"\t" <> "nstap: " 		<> ToString[nstap] 		<> "\n",
		"\t" <> "nscen0: " 		<> ToString[nscen0] 		<> "\n",
		"\t" <> "analyse: " 		<> ToString[analyse] 		<> "\n\n"
	
]; 

End[]

Protect[Evaluate[Context[] <> "*"]]


EndPackage[]
