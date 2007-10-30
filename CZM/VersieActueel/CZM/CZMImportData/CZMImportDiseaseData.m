(* :Title: CZMImportDiseaseData *)

(* :Context: CZMImportData` *)

(* :Author: Rudolf T. Hoogenveen, Roel G.M. Breuls *)

(* :Summary:
   CZM import data routine imports incidences, prevalences, remission,
	case fatality and excess mortality rates of diseases*)

(* :Copyright: © 2004 by Rudolf Hoogenveen/Roel Breuls *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March, partial update data DisMod 2005
		3.0 version november 2005 
		3.1 version March 2007 *)

(* :Keywords: disease data, import *)


BeginPackage["CZMImportData`CZMImportDiseaseData`",
	{"CZMInitialization`CZMLogFile`",
	"CZMInitialization`CZMDefaultFileNames`",
	"CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`",
	"CZMImportData`CZMImportUserSelections`",
	"CZMImportData`CZMImportDemography`"}]


inc0::usage		= "inc0[[d,g,ai]]: empirical disease incidence rates"
rem0::usage		= "rem0[[d,g,ai]]: unique empirical disease remission rates, see also remind0"
pdis0::usage		= "pdis0[[d,g,ai]]: empirical initial disease prevalence rates"
excessmort0::usage	= "excessmort0[[d,g,ai]]: calculated disease-related excess mortality rates"
causemort0::usage	= "causemort0[[d,g,ai]]: initial cause-specific mortality rates"
remind0::usage		= "remind0[[d]]: for each disease d, the pointer to the index in variable rem0, see also rem0"	
casefat0::usage		= "casefat0[[d,g,ai]]: case fatality (1-month mortality) rates for all diseases"	
casefatind0::usage	= "casefatind0[[d]]: pointer to the index in casefat0 for each disease d, see also casefat0"	

nonmodelpdis0		= "nonmodelpdis0[[d,g,ai]]: prevalence rates of diseases not modelled in CZM"
ndoth			= "# non-modeled diseases"


Begin["`Private`"]	


Print["CZMImportDiseaseData package is evaluated"]


(* -----------------------------------------------
           INITIALIZATION
   -----------------------------------------------*)

inc0		= Table[0, {nd0}];
pdis0		= Table[0, {nd0}];
excessmort0	= Table[0, {nd0}, {ng}];
casefat0	= {Table[0, {ng}, {nac[[1]]}]};
casefatind0	= Table[1, {nd0}];
rem0		= {Table[0, {ng}, {nac[[1]]}]};
remind0		= Table[1, {nd0}];
causemort0	= Table[0, {nd0}];


(* -----------------------------------------------
           READ NON-CANCER DISEASE DATA
   -----------------------------------------------*)

currremind	= 1;
currcasefatind	= 1;

readdisdat1[dis_, dat_] := Block[{},

	Skip[dat, Record, RecordSeparators -> {{"(*"}, {"*)"}}];
	Read[dat, Word];
	Do[

(* INCIDENCE DATA *)

		div 	= N[Read[dat, {Table[Word, {2}], Number}][[2]]];
		inc0[[dis[[d]]]] = Partition[Read[dat, Table[Number, {ng nac[[1]]}]], {nac[[1]]}] / div;

(* INITIAL PREVALENCE DATA *)

		div 	= N[Read[dat, {Table[Word, {2}], Number}][[2]]];
		pdis0[[dis[[d]]]] = Partition[Read[dat, Table[Number, {ng nac[[1]]}]], {nac[[1]]}] / div;

(* REMISSION DATA *)

		div 	= N[Read[dat, {Table[Word, {2}], Number}][[2]]];
		currrem = Partition[Read[dat, Table[Number, {ng nac[[1]]}]], {nac[[1]]}] / div;
		If[(Max[currrem] > 0),
			currremind	= currremind + 1;
			rem0		= Join[rem0, {currrem}];
			remind0[[dis[[d]]]] = currremind,
			remind0[[dis[[d]]]] = 1];

(* CASE FATALITY = 1-MONTH MORTALITY) DATA *)

		div 	= N[Read[dat, {Table[Word, {2}], Number}][[2]]];
		currcasefat = Partition[Read[dat, Table[Number, {ng nac[[1]]}]], {nac[[1]]}] / div;
		If[(Max[currcasefat] > 0),
			currcasefatind	= currcasefatind + 1;
			casefat0	= Join[casefat0, {currcasefat}];
			casefatind0[[dis[[d]]]] = currcasefatind,
			casefatind0[[dis[[d]]]] = 1];

(* EXCESS MORTALITY DATA *)

		div 	= N[Read[dat, {Table[Word, {2}], Number}][[2]]];
		excessmort0[[dis[[d]]]] = Partition[Read[dat, Table[Number, {ng nac[[1]]}]], {nac[[1]]}] / div;

(* CBS-REGISTERED CAUSE-SPECIFIC MORTALITY DATA *)

		div 	= N[Read[dat, {Table[Word, {2}], Number}][[2]]];
		causemort0[[dis[[d]]]] = N[Partition[Read[dat, Table[Number, {ng nac[[1]]}]], {nac[[1]]}]];
		causemort0[[dis[[d]]]] = causemort0[[dis[[d]]]] / If[(div < 0), npop1, div],
		{d, Length[dis]}];

	Close[dat];

	]; (* READDISDAT1 *)


(* CHD *)

dat 		= OpenRead[Global`inputpath <> CHDinput];
readdisdat1[{1, 2}, dat];


(* CHF *)
	
dat 		= OpenRead[Global`inputpath <> CHFinput]; 
readdisdat1[{3}, dat];


(* CVA *)

dat 		= OpenRead[Global`inputpath <> CVAinput]; 
readdisdat1[{4}, dat];


(* CARA (ASTHMA & COPD) *)

dat 		= OpenRead[Global`inputpath <> CARAinput]; 
readdisdat1[{5, 6}, dat];


(* DIABETES MELLITUS *)

dat 		= OpenRead[Global`inputpath <> DMinput];
readdisdat1[{7}, dat]; 
(*excessmort0[[7]] = Maxc[2.0 excessmort0[[7]], .02];*)


(* DEMENTIA *)

dat 		= OpenRead[Global`inputpath <> demeninput]; 
readdisdat1[{8}, dat];


(* ARTHROSIS OF HIP, KNEE AND OTHER *)

dat 		= OpenRead[Global`inputpath <> artrinput]; 
readdisdat1[{9, 10, 11}, dat];


(* DORSOPATHIES (LOW BACK PAIN) *)

dat 		= OpenRead[Global`inputpath <> dorsinput]; 
readdisdat1[{12}, dat];


(* OSTEOPOROSIS *)

dat 		= OpenRead[Global`inputpath <> osteinput]; 
readdisdat1[{13}, dat];


(* TRANSFORMATION OF RATES TO 1-YEAR RISKS *)

excessmort0[[Range[ndnoncanc]]] =
	1 - ( 1 - excessmort0[[Range[ndnoncanc]]] / 4 )^4;

rem0 = 1 - ( 1 - rem0 / 4 )^4;


(* -----------------------------------------------
           READ CANCER DISEASE DATA
   -----------------------------------------------*)

readdisdat2[dat_] := Block[{},

	Skip[dat, Record, RecordSeparators -> {{"(*"}, {"*)"}}];
	div = N[Read[dat, {Table[Word, {2}], Number}][[2]]];
	res = Table[0, {ndcancers}];
	Do[	Read[dat, Word];
		res[[d]] = Partition[Read[dat, Table[Number, {ng nac[[1]]}]], {nac[[1]]}] / div,
		{d, ndcancers}];
	Close[dat];

	res

	]; (* READDISDAT2 *)

(* INCIDENCE *)

dat 		= OpenRead[Global`inputpath <> cancincinput];
inc0[[ndnoncanc + Range[ndcancers]]] = readdisdat2[dat];
inc0[[ndnoncanc + 6]] = .92 inc0[[ndnoncanc+6]];

(* PREVALENCE *)

dat 		= OpenRead[Global`inputpath <> cancprevinput]; 
pdis0[[ndnoncanc + Range[ndcancers]]] = readdisdat2[dat];

(* REGISTERED MORTALITY NUMBERS *)

dat 		= OpenRead[Global`inputpath <> cancmortinput];
causemort0[[ndnoncanc + Range[ndcancers]]] = readdisdat2[dat];       

(* SURVIVAL PROPORTIONS, TRANSFORMED TO EXCESS MORTALITY RATES *)

dat 		= OpenRead[Global`inputpath <> cancrelsurvinput]; 
cancrelsurv 	= readdisdat2[dat];
cancrelsurv 	= cancrelsurv^.2;
Do[	excessmort0[[ndnoncanc + d, g]] = (1 - morttot0[[g]]) (1 - cancrelsurv[[d, g]]);
	If[(Max[pdis0[[ndnoncanc + d, g]]] < eps),
		excessmort0[[ndnoncanc + d, g]] += causemort0[[ndnoncanc + d, g]],
		excessmort0[[ndnoncanc + d, g]] /= (1 - pdis0[[ndnoncanc + d, g]])],
		{d, ndcancers}, {g, ng}];


(* -----------------------------------------------
           READ DISEASE DATA NON CZM DISEASES
   -----------------------------------------------*)

readfile[dat_, row_, n_] := Block[{},

	OpenRead[dat]; 
    	Skip[dat, Record, RecordSeparators -> {{"(*"}, {"*)"}}]; 
	Skip[dat, Word, 2]; 
      	div = Read[dat, Number]; 
	Table[Read[dat, {Word, Table[Number, {row}, {nac[[1]]}]}][[2]] / div, {n}]

	];

ndoth		= 43;
dat		= Global`inputpath <> nonmodelprevinput;
nonmodelpdis0	= readfile[dat, ng, ndoth];
Close[dat];


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
