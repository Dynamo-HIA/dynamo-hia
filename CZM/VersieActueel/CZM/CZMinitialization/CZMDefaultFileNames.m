(* :Title: CZMDefaultFileNames *)

(* :Context: CZMInitialization` *)

(* :Author: Rudolf T. Hoogenveen, Roel G.M. Breuls *)

(* :Summary:
   CZM initialization routine contains the names of the default import data files *)

(* :Copyright: © 2004 by Roel G.M. Breuls/Rudolf Hoogenveen *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)


(* :History: 	1.0 July 2004 (Roel Breuls), First update new implementation CZM
		1.1 September 2004 (Pieter van Baal), Addition of new default file name: allcauseDALYinput 
        	2.0 first release CZM 2005, version March, *010305.txt input files
		3.0 version november 2005 
		3.1 version March 2007; risk factor class distributions within patients (riskdisinput.txt) *)

(* :Keywords: file names, import *)


BeginPackage["CZMInitialization`CZMDefaultFileNames`",
	"CZMInitialization`CZMLogFile`"]


deminput::usage  		= "Name of file containing data on demography"
smokinput::usage 		= "Name of file containing data on smoking prevalence and transition rates"
SBPinput::usage 	 	= "Name of file containing data on SBP prevalence and transition rates"
cholinput::usage  		= "Name of file containing data on cholesterol prevalence and transition rates"
BMIinput::usage 		= "Name of file containing data on BMI prevalence and transition rates"
lichactinput::usage 		= "Name of file containing data on physical activity prevalence and transition rates"
alcoinput::usage 		= "Name of file containing data on alcohol consumption prevalence and transition rates"
verzvetinput::usage 		= "Name of file containing data on satur fat consumption prevalence and transition rates"
transvetinput::usage 		= "Name of file containing data on trans fat consumption prevalence and transition rates"
fruitinput::usage 		= "Name of file containing data on fruit consumption prevalence and transition rates"
groenteinput::usage 		= "Name of file containing data on vegetables consumption prevalence and transition rates"
visinput::usage 		= "Name of file containing data on fish consumption prevalence and transition rates"
HbA1cinput::usage 		= "Name of file containing data on HbA1c prevalence and transition rates"

riskdisinput		::usage	= "name of file containing data on risk factor class distributions within patients"

RRsmokinput::usage 		= "Name of file containing relative risk data for smoking"
smokduurinput::usage		= "Name of file containing parameters values of decrease of former smokers RR's and relapse rate"
RRSBPinput::usage 		= "Name of file containing relative risk data for SBP"
RRcholinput::usage 		= "Name of file containing relative risk data for cholesterol"
RRBMIinput::usage 		= "Name of file containing relative risk data for BMI"
RRlichactinput::usage 		= "Name of file containing relative risk data for physical activity"
RRalcoinput::usage 		= "Name of file containing relative risk data for alcohol consumption"
RRverzvetinput::usage 		= "Name of file containing relative risk data for saturated fat consumption"
RRtransvetinput::usage 		= "Name of file containing relative risk data for trans fatty acid consumption"
RRfruitinput::usage 		= "Name of file containing relative risk data for fruit consumption"
RRgroenteinput::usage 		= "Name of file containing relative risk data for vegetables consumption"
RRvisinput::usage 		= "Name of file containing relative risk data for fish consumption"
RRHbA1cinput::usage 		= "Name of file containing relative risk data for HbA1c"
RRCVDinput::usage 		= "Name of file containing relative risk data for one disease on another disease incidence"
RRcasefatinput::usage		= "Name of file containing relative risk data for one diseasd on another disease case fatality"	

CHDinput::usage		 	= "Name of file containing incidence, prevalence, and excess mortality rates for CHD"
CHFinput::usage 		= "Name of file containing incidence, prevalence, and excess mortality rates for CHF"
CVAinput::usage 		= "Name of file containing incidence, prevalence, and excess mortality rates for CVA"
CARAinput::usage 		= "Name of file containing incidence, prevalence, and excess mortality rates for CARA"
DMinput::usage 			= "Name of file containing incidence, prevalence, and excess mortality rates for DM"
demeninput::usage 		= "Name of file containing incidence, prevalence, and excess mortality rates for dementia"
artrinput::usage 		= "Name of file containing incidence, prevalence, and excess mortality rates for arthritis"
dorsinput::usage 		= "Name of file containing incidence, prevalence, and excess mortality rates for dorsopathy"
osteinput::usage 		= "Name of file containing incidence, prevalence, and excess mortality rates for
					osteoporosis"
nonmodelprevinput ::usage	= "Name of file containing prevalence rates of diseases not modelled in CZM"

cancmortinput::usage 		= "Name of file containing mortality rates for types of cancer"
cancrelsurvinput::usage		= "Name of file containing relative survival rates for types of cancer"
cancincinput::usage 		= "Name of file containing incidence rates for types of cancer"
cancprevinput::usage 		= "Name of file containing prevalence rates for types of cancer"

DALYwgtinput::usage 		= "Name of file containing DALY weights pf diseases modelled in CZM"
nonmodelDALYwgtinput ::usage	= "Name of file containing DALY weights of diseases not modelled in CZM"

costspersoninput::usage	 	= "Name of file containing costs per person data"
costspatientinput::usage	= "Name of file containing costs per patient data"

userinput::usage	 	= "Name of file containing user selections of input"
useroutput::usage	 	= "Name of file containing user selections of output"


Begin["`Private`"]


Print["CZMDefaultFileNames package is evaluated"];

	userinput	= "userinput011205.txt"
	useroutput	= "useroutput011205.txt"

(* DEMOGRAPHY *)

	deminput  	= "demdata111005.txt"

(* RISK FACTORS *)

	smokinput 	= "smokinput010505.txt"
	SBPinput  	= "SBPinput240406.txt"
	cholinput 	= "cholinput240406.txt"
	BMIinput  	= "BMIinput011205.txt"
	lichactinput	= "lichactCBS011205.txt"
	alcoinput	= "alcoinput011205.txt"
	verzvetinput	= "verzvetinput221105.txt"
	transvetinput	= "transvetinput011205.txt"
	groenteinput	= "groenteinput011205.txt"
	fruitinput	= "fruitinput011205.txt"
	visinput	= "visinput011205.txt"
	HbA1cinput	= "HbA1cinput130106.txt"

(* DISCFRETELY DISTRIBUTED RISK FACTORS WITHIN PATIENTS *)

	riskdisinput			= "riskdisinput.txt";

(* RELATIVE RISKS *)

	RRsmokinput 	= "RRsmok090305.txt"
	smokduurinput 	= "smokinputduur010305.txt"
	RRSBPinput  	= "RRSBPinput240406.txt"
	RRcholinput 	= "RRcholinput240406.txt"
	RRBMIinput  	= "RRBMIinput260505.txt"
	RRlichactinput	= "RRlichactinput010305.txt"
	RRalcoinput	= "RRalcoinput010305.txt"
	RRverzvetinput	= "RRverzvetinput010305.txt"
	RRtransvetinput	= "RRtransvetinput010305.txt"
	RRgroenteinput	= "RRgroenteinput010305.txt"
	RRfruitinput	= "RRfruitinput010305.txt"
	RRvisinput	= "RRvisinput010305.txt"
	RRHbA1cinput	= "RRHbA1cinput120405.txt"
	RRCVDinput  	= "RRCVDinput130605.txt"
	RRcasefatinput	= "RRcasefat130605.txt"

(* NONCANCEROUS DISEASE DATA *)

	CHDinput 	= "CHDinput010305.txt"
	CHFinput	= "CHFinput010305.txt"
	CVAinput	= "CVAinput010305.txt"
	CARAinput	= "CARAinput010305.txt"
	DMinput		= "DMinput241105.txt"
	demeninput	= "demeninput010305.txt"
	artrinput	= "artrinput010305.txt"
	dorsinput	= "dorsinput010305.txt"
	osteinput	= "osteinput010305.txt"

	nonmodelprevinput = "nonmodelprev010305.txt"

(* CANCEROUS DISEASE DATA *)

	cancmortinput 	= "cancmort010305.txt" 
	cancrelsurvinput = "cancrelsurv010305.txt"
	cancincinput 	=  "cancinc010305.txt"
	cancprevinput 	= "cancprev010305.txt"

(* DALYS *)

	DALYwgtinput 	= "DALYwgt010305.txt"
	nonmodelDALYwgtinput = "nonmodelDALYwgt010305.txt" 

	
(* COSTS *)

	costspersoninput  = "costsperson180505.txt"
	costspatientinput = "costspatient180505.txt"
	

(* --------------------------------------------------
		Write info to Logfile
----------------------------------------------------*)

(* PACKAGE VERSION *)

version = 3.1;

WriteString[logfile, 
		"\t" <> "Package: " <> StringReplace[Evaluate[Context[]], "`Private`"-> ", " ] 
		<> "version " <> ToString[version] <> "\n\n"];


WriteString[logfile, "Summary of input files used for the simulation: \n\n"];

WriteString[logfile,

"Demography" <> "\n\n", 

	"\t" <> deminput 		<> "\n\n", 

"Risk Factors" <> "\n\n",

	"\t" <> smokinput 		<> "\n", 
	"\t" <> SBPinput 		<> "\n", 
	"\t" <> cholinput 		<> "\n", 
	"\t" <> BMIinput 		<> "\n", 
	"\t" <> lichactinput 		<> "\n", 
	"\t" <> alcoinput 		<> "\n", 
	"\t" <> verzvetinput 		<> "\n", 
	"\t" <> transvetinput 		<> "\n", 
	"\t" <> groenteinput 		<> "\n", 
	"\t" <> fruitinput 		<> "\n", 
	"\t" <> visinput 		<> "\n",
	"\t" <> HbA1cinput 		<> "\n\n", 


"Risk Factor distributions within patients" <> "\n\n",

	"\t" <> riskdisinput 		<> "\n\n",


"Relative Risks" <> "\n\n",

	"\t" <> RRsmokinput 		<> "\n", 
	"\t" <> smokduurinput 		<> "\n", 
	"\t" <> RRSBPinput 		<> "\n", 
	"\t" <> RRcholinput 		<> "\n", 
	"\t" <> RRBMIinput 		<> "\n", 
	"\t" <> RRlichactinput 		<> "\n", 
	"\t" <> RRalcoinput 		<> "\n", 
	"\t" <> RRverzvetinput 		<> "\n", 
	"\t" <> RRtransvetinput 	<> "\n", 
	"\t" <> RRgroenteinput 		<> "\n", 
	"\t" <> RRfruitinput 		<> "\n", 
	"\t" <> RRvisinput 		<> "\n", 
	"\t" <> RRHbA1cinput 		<> "\n", 
	"\t" <> RRCVDinput 		<> "\n",
	"\t" <> RRcasefatinput 		<> "\n\n",

"Diseases" <> "\n\n",

	"\t" <> CHDinput 		<> "\n", 
	"\t" <> CHFinput 		<> "\n", 
	"\t" <> CVAinput 		<> "\n", 
	"\t" <> CARAinput 		<> "\n", 
	"\t" <> DMinput 		<> "\n", 
	"\t" <> demeninput 		<> "\n", 
	"\t" <> artrinput 		<> "\n", 
	"\t" <> dorsinput 		<> "\n", 
	"\t" <> osteinput 		<> "\n", 

	"\t" <> cancmortinput 		<> "\n", 
	"\t" <> cancrelsurvinput 	<> "\n", 
	"\t" <> cancincinput 		<> "\n", 
	"\t" <> cancprevinput 		<> "\n", 

	"\t" <> nonmodelprevinput 	<> "\n\n",

"Costs and DALYs" <> "\n\n",

	"\t" <>	costspersoninput 	<> "\n",
	"\t" <>	costspatientinput 	<> "\n",
	"\t" <> DALYwgtinput 		<> "\n", 
	"\t" <> nonmodelDALYwgtinput 	<> "\n\n",

"User Selections" <> "\n\n",

	"\t" <> userinput 		<> "\n", 
	"\t" <> useroutput 		<> "\n\n"
]


End[]

Protect[Evaluate[Context[] <> "*"]]

EndPackage[]

