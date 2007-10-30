(* :Title: CZMSimulationFunctions *)

(* :Context: CZMSimulation` *)

(* :Author: Rudolf T. Hoogenveen *)

(* :Summary:
   CZM simulation routine describes functions to be used in simulation packages *)

(* :Copyright: © 2004 by Rudolf Hoogenveen *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM November 2004
		2.0 first release CZM 2005, version March
		3.0 version November 2005 
		3.1 version March 2007; inital distriubtions; storage of results *)

(* :Keywords: joint model, functions *)


BeginPackage["CZMSimulation`CZMSimulationFunctions`",
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
	"CZMDefineScenarios`CZMDefineScenarios`"}] 


ncz::usage		= "# classes in joint model = product of # classes over risk factors selected * # classes over diseases selected"
nz::usage		= "# state variables of joint model = # risk factors + # diseases selected = nrdsel + nd"
nz1::usage		= "# states of joint model = # classes of all state variables multiplied, see also ncz"
zind::usage		= "zind[[zi,z]]: related risk factor class and disease state values (columns) for joint model states (rows)"
zindrisk::usage		= "zindrisk[[r,ri]]: joint model states for any classri of risk factor r, see also zind"
zinddis::usage		= "zinddis[[d,di]]: joint model states for any state di of disease d, see also zind"
zdist::usage		= "zdist[[z]]: distances between joint model states that differ in only in variable (risk factor or disease) z"
disindsmall::usage	= "disindsmall[[d]]: diseases with prevalence rates smaller than .01"
disindlarge::usage	= "disindlarge[[d]]: complement of disindsmall"
riskclass::usage	= "riskclass[[ri]]: list of all combinations of different joint risk factor classes"
riskset::usage		= "riskset[[ri]]: joint model states for each set of joint risk factor classes, see riskclass"

makezval	::usage	= "makezval[prev]: transforms multi-dimensional state vector to index value"
makezvalinv	::usage	= "inverse function of makezval"

makeprevcurr::usage	=

"routine calculates initial class prevalence rates of joint model, with:\n
 arguments:\n 
  1: prisk: risk factor class prevalence rates\n
  2: pdis: disease prevalence rates\n
  3: RMrisk: disease incidence rate multipliers specified by risk factor class\n
  4: RRdis: disease incidence risks specified by (independent) disease\n
  5: RRcasefat: case fatality relative risks specified by (independent) disease\n
 calculation steps:\n
  1: prevalence rates = product of class prevalence rates over risk factors * disease prevalence rates\n
  2: adjustment for causal relations using disease incidence rate ratios\n
  3: (if selected) overwriting with user-provided data"

makepriskold	::usage =

"routine calculates risk factor class prevalence rates in current population"

changeprevcurrg	::usage	=

"routine calculates change of initial class prevalence rates of joint model according to scenario, with"

changeprevcurr	::usage	=

"routine calculates change of initial class prevalence rates of joint model according to scenario,\n
  (see) changeprevcurrg applied to each gender"

makeincjoint::usage	=

"routine calculates incidence rates for joint model states, with:\n
 arguments:\n
  1: incbase: baseline disease incidence rates\n
  2: RRrisk: disease incidence relative risks specified by risk factor class\n
  3: RRdis: disease incidence risks specified by (independent) disease\n
 calculated by multiplying baseline incidence rates with:\n
  1: relative risk values according to risk factor class\n
  2: relative risk values according to co-morbid disease state"

maketransmarginal::usage =

"routine calculates risk factor class transition rates, with:\n
 arguments:\n
  1: transrisk: (vector of) 1-year risk factor class transition risks\n
  2: transriskind: (matrix of) pointers to transrisk for each pair of risk factor classes\n
  3: r: risk factor class index\n
  4: ncr: # classes of r\n
  5: trackingmult: multiplicator of 1-year transition risk values related to tracking\n
 calculation steps:\n
  1: starts with selected risk factor class transition risks values\n
  2: (if selected) adding up 1-year transition risk values related to tracking\n
  3: for each transition transformation risk to rate values\n
  4: matrix back-transformation from rate to risk values resulting in 1-year state transition probability matrix"

maketransmarginalext::usage = 

"routine calculates risk factor class transition rates (extended version),\n
 i.e. maketransmarginal only applied to selected age range"

maketransjointga::usage	= 

"routine calculates transition probability matrix of joint model specified by gender and age, with:\n
 arguments:\n 
  1: g: gender\n
  2: a: age\n
  3: transrisk: 1-year risk factor class transition risks\n
  4: incbase: baseline incidence rates\n
  5: excessmort: adjusted disease-related excess mortality rates\n
  6: casefat: case fatality rates\n
  7: RRrisk: disease incidence relative risks specified by risk factor class\n
  8: trackingmult: multiplicator of 1-year transition risk values related to tracking\n
  9: RRdis: disease incidence risks specified by (independent) disease\n
  10:RRcasefat: case fatality relative risks specified by (independent) disease\n
 calculation steps:\n
  1: disease incidence rates (see makeincjoint) for each disease-free state, taking account of case fatality\n
  2: disease remission rates for each with-disease state\n
  3: risk factor class transition rates (see maketransmarginal)\n
  4: disease-related excess mortality rates for each with-disease state\n
  5: other causes mortality rates for all states\n
  6: back-transformation to 1-year incidence, state transition, and other causes mortality risks"


maketransjoint::usage	=

"routine calculates transition probability matrix of joint model, i.e\n
 (see) maketransjointga applied to each gender and age range selected"

transformres::usage	=

"routine transforms output specified by age at baseline to current age, with:\n
 arguments: see marginalmodelresults\n 
 calculation steps applied on each argument:\n
  1: numbers specified by year n and baseline age a to year and current age n+a-1\n
  2: for each year age-year specific numbers aggregated to 5-year age class values\n"

marginalmodelresults::usage =

"model results in terms of marginal numbers, with arrays as elements:\n
  1: total population numbers, indexes scen,n,g,ai\n
  2: risk factor class prevalence numbers, indexes scen,n,r,g,ri,ai\n
  3: continuous risk factor distribution characteristics, PM\n
  4: disease prevalence numbers, indexes scen,n,d,g,ai\n
  5: disease incidence numbers, indexes scen,n,d,g,ai\n
  6: cause-specific mortality numbers, indexes scen,n,d,g,ai\n
  7: aggregated age of disease onset, indexes scen,n,d,g,ai\n
  8: aggregated years since smoking cessation of former smokers, indexes scen,n,g,ai\n
  9: all cause mortality numbers per risk factor class, indexes scen,n,r,g,ri,ai\n
  length of indexes: scen: nscen, n: nstap, g: ng, r: nrdsel, ri: ncrsel[[r]], d: nd or nd+2, ai: nac[[1]]"

jointmodelresults::usage = "joint model results with indexes scen (length nscen),n (nstap),g (ng),zi (nz1),ai (nac[[1]]"

maketransrisksmok	::usage	= "routine calculates current state transition risk matrix in case of smoking being duration-dependent"
makemortsmok		::usage	= "routine calculates current mortalit risks in case of smoking being duration-dependent"


Begin["`Private`"]


Print["CZMSimulationFunctions package is evaluated"];
printtijd;

printbug[c_] := If[(bugind == 1), Print[{"CZMSimulationFunctions", c}]];


(*-------------------------------------------------
 CONSTANTS AND SYSTEM PARAMETERS OF JOINT MODEL
---------------------------------------------------*)

printbug["1."];

ncdsel	= Table[2, {nd}];
ncz 	= Flatten[{ncrsel, ncdsel}];
nz 	= Length[ncz];
nz1 	= Times@@ncz;
zdist 	= Table[Times@@ncz[[Range[z]]], {z, 0, nz - 1}];

printbug["1.1"];

If[(Max[Take[modelsel,{2,4}]] > 0),

	If[(nz > 0),
		zind = Transpose[Table[Flatten[Table[Table[zi, {zi, ncz[[z]]}, {zdist[[z]]}], {nz1 / ncz[[z]] / zdist[[z]]}]], {z, nz}]],
		zind = {1}];

printbug["1.2"];

	zindrisk 	= Table[Select[Range[nz1], (zind[[#, r]] == ri) &], {r, nrd}, {ri, ncrsel[[r]]}];
	zinddis		= Table[Select[Range[nz1], (zind[[#, nrd + d]] == di) &], {d, nd}, {di, ncdsel[[d]]}];

(* LIST OF JOINT RISK FACTOR CLASSES AND RELATED JOINT MODEL STATES *)

printbug["1.3"];

	riskclass = If[(nrd > 0),

		Transpose[Partition[Flatten[Table[ri, {r, nrd}, {Times@@Drop[ncrsel, r]}, {ri, ncrsel[[r]]},
							{Times@@ncrsel[[Range[r - 1]]]}]],
				Times@@ncrsel]],
		{}];

printbug["1.4"];

	riskset	= Table[0, {Times@@ncrsel}];

	Do[	set = Range[nz1];

		Do[set = Intersection[set, zindrisk[[r, riskclass[[ri, r]]]]], {r, nrd}];

		riskset[[ri]] = set,

		{ri, Times@@ncrsel}]

	];

(* SELECTION CRITERIA FOR ADJUSTMENT *)

printbug["1.5"];

disindlarge	= Select[Table[If[(Max[pdissel[[d]]] > .01), d, 0], {d, nd}], Positive];
disindsmall 	= Complement[Range[nd], disindlarge];


(*-------------------------------------------------
 TRANSFORMS AND RE-TRANSFORMS MULTI-DIMENSIONAL STATE VECTOR TO INDEX VALUE
---------------------------------------------------*)

makezval[prev_] := 1 + Plus@@Table[(prev[[zi]] - 1) Times@@ncz[[Range[zi - 1]]], {zi, nz}];

makezvalinv[prev_] := Block[{},

	hprev	= prev;
	zval	= 0 Table[prev, {nz}];

	Do[	zval[[nz + 1 - z]] = 1 + Quotient[hprev - 1, Times@@Drop[ncz, z]];
		hprev -= Times@@Drop[ncz, -z] (zval[[nz + 1 - z]] - 1),
		{z, nz}];

	Do[	zval[[z]] *= Sign[prev], {z, nz}];

	Transpose[zval, {3, 1, 2}]];


(*-------------------------------------------------
 CALCULATION OF INITIAL PREVALENCE RATES FOR JOINT MODEL
---------------------------------------------------*)

printbug["2."];

(* REFERENCE VALUES *)

makeprevcurr[prisk_, pdis_, RMrisk_, RRdis_, RRcasefat_] := Block[{},

printbug["2.1"];

	(* INITIAL RISK FACTOR CLASS PREVALENCE RATES *)

	hprevcurr = Table[1, {ng}, {nz1}, {na1}];

printbug["2.1.2"];

	(* CLASS PREVALENCE RATES TIMES CLASS PREVALENCE RATES FOR RISK FACTOR APPLIED TO TOTAL POPULATION *)

	Do[	hprevcurr[[g, zindrisk[[r, ri, z]]]] *= prisk[[r, g, ri]], 
		{g, ng}, {r, nrdpop}, {ri, ncrsel[[r]]}, {z, Length[zindrisk[[r, ri]]]}];

	(* CLASS PREVALENCE RATES TIMES CLASS PREVALENCE RATES FOR DISEASE-RESTRICTED RISK FACTORS (NEW DISEASE CASES) *)

	Do[	disset = Intersection[zindrisk[[nrdpop + r, ri]], zinddis[[disindinv[[disriskindddis[[r]]]], 1]]];
		Do[	hprevcurr[[g, disset[[z]]]] *= priskincsel[[r, g, ri]], {z, Length[disset]}],
		{g, ng}, {r, nrddis}, {ri, ncrsel[[nrdpop + r]]}];
	
	(* CLASS PREVALENCE RATES TIMES CLASS PREVALENCE RATES FOR DISEASE-RESTRICTED RISK FACTORS (PREVALENT DISEASE CASES) *)

	Do[	disset = Intersection[zindrisk[[nrdpop + r, ri]], zinddis[[disindinv[[disriskindddis[[r]]]], 2]]];
		Do[	hprevcurr[[g, disset[[z]]]] *= prisk[[nrdpop + r, g, ri]], {z, Length[disset]}],
		{g, ng}, {r, nrddis}, {ri, ncrsel[[nrdpop + r]]}];

	(* CO-MORBIDITY RATE MULTIPLIERS *)

	RMcomorb = If[(nrd == 0),

			Table[1., {nd}, {nd}, {na1}],

			Table[Times@@Table[Plus@@(	RMrisk[[r, RRriskindsel[[r, d + 1]], g]] *
							RMrisk[[r, RRriskindsel[[r, d1 + 1]], g]] *
							prisksel[[r, g]]),
						{r, nrdpop}],
				{d, nd}, {d1, nd}, {g, ng}]];

printbug["2.1.4"];

	(* INITIAL DISEASE PREVALENCE RATE RATIO VALUE *)

	hRMrisk	= Table[1, {ng}, {nz1}, {nd}, {na1}];

printbug["2.1.5"];

	(* ADJUSTMENT OF DISEASE PREVALENCE RATE RATIO FOR RISK FACTORS APPLIED TO TOTAL POPULATION *)

	Do[	disset	= zindrisk[[r, ri]];
		Do[hRMrisk[[g, disset[[z]], d]] *= RMrisk[[r, RRriskindsel[[r, d + 1]], g, ri]], {z, Length[disset]}, {g, ng}],

		{r, nrdpop}, {ri, ncrsel[[r]]}, {d, nd}];

printbug["2.1.6"];

	(* ADJUSTMENT OF PREVALENCE RATE RATIO FOR RISK FACTORS RESTRICTED TO DISEASES *)

	Do[	d1	= disindinv[[disriskindddis[[r]]]];

		Do[	currdis	= Intersection[zindrisk[[nrdpop + r, ri]], zinddis[[d1, 2]]];
			Do[hRMrisk[[g, currdis[[z]], d]] *= RMrisk[[nrdpop + r, RRriskindsel[[nrdpop + r, d + 1]], g, ri]],
				{z, Length[currdis]}],

			{d, nd}, {g, ng}],

		{r, nrddis}, {ri, ncrsel[[nrdpop + r]]}];

printbug["2.1.6.1"];

		(* ADJUSTMENT OF PREVALENCE RATE RATIO FOR RISK FACTORS WITH EMPIRICAL DATA *)

		If[(userriskdata >= 1), Do[

			r	= DMriskpairsel[[d, r1]];

			Do[	hRMrisk[[Intersection[zindrisk[[r, ri]], zinddis[[d, 2]]], d]] *= RMriskDMinc[[d, r1, g, ri, agerange[[a]]]],
				{ri, ncrsel[[r]]}],

			{d, nd}, {r1, Length[DMriskpairsel[[d]]]}]];

printbug["2.1.7"];

	(* CLASS PREVALENCE RATES TIMES ADJUSTMENT USING CALCULATED PREVALENCE RATE RATIO VALUES *)

	Do[	disset	= zinddis[[d, 1]];
		Do[	hprevcurr[[g, disset[[z]]]] *= 1 - hRMrisk[[g, disset[[z]], d]] pdis[[d, g]], {z, Length[disset]}, {g, ng}];

		disset	= zinddis[[d, 2]];
		Do[	hprevcurr[[g, disset[[z]]]] *= hRMrisk[[g, disset[[z]], d]] pdis[[d, g]], {z, Length[disset]}, {g, ng}],

		{d, nd}];

printbug["2.1.8"];

	(* CLASS PREVALENCE RATES TIMES ADJUSTMENT FOR CAUSALLY RELATED DISEASES *)

	Do[	d1	= dispair[[d, 1]];				(* INDEPENDENT DISEASE *)
		d2	= dispair[[d, 2]];				(* DEPENDENT DISEASE *)

		If[MemberQ[disindlarge, d1] && MemberQ[disindlarge, d2],

			(* RELATIVE RISKS AND MEAN RISKS FOR PAIR OF CAUSALLY RELATED DISEASES d1 & d2 *)

			currRRcasefat	= RRcasefat[[RRcasefatindsel[[d1, d2]], g]];

			currRRdis 	= RRdis[[RRdisindsel[[d1, d2]], g]] *
						( 1 - (currRRcasefat - 1) casefat1[[casefatindsel[[d2]], g]] /
							( 1 + (currRRcasefat - 1) pdissel[[d1, g]] - casefat1[[casefatindsel[[d2]], g]] ) );

			currERRdis	= 1 + (currRRdis - 1) RMcomorb[[d1, d2, g]] pdissel[[d1, g]];

			(* ADJUSTMENT OF PREVALENCE RATES: WITHOUT DISEASES d1 AND d2 *)

			disset	= Intersection[zinddis[[d2, 2]], zinddis[[d1, 1]]];

			Do[	hprev	= hprevcurr[[g, disset, a]] + hprevcurr[[g, disset - zdist[[nrd + d2]], a]];
				hprevcurr[[g, disset, a]] *= 1 / currERRdis[[a]];
				hprevcurr[[g, disset - zdist[[nrd + d2]], a]] = hprev - hprevcurr[[g, disset, a]],

				{a, na1}];

			disset	= Intersection[zinddis[[d2, 2]], zinddis[[d1, 2]]];

			Do[	hprev	= hprevcurr[[g, disset, a]] + hprevcurr[[g, disset - zdist[[nrd + d2]], a]];
				hprevcurr[[g, disset, a]] *= currRRdis[[a]] / currERRdis[[a]];
				hprevcurr[[g, disset - zdist[[nrd + d2]], a]] = hprev - hprevcurr[[g, disset, a]],

				{a, na1}]],

		{d, Length[dispair]}, {g, ng}];

printbug["2.1.10"];

	(* SCALING OF PREVALENCE RATES *)

	somhprevcurr	= Table[Plus@@hprevcurr[[g]], {g, ng}];
	
	Do[hprevcurr[[g, All, agerange[[a]]]] /= somhprevcurr[[g, agerange[[a]]]], {g, ng}, {a, Length[agerange]}];

	hprevcurr

	]; (* END MAKEPREVCURR *)


(* CALCULATION OF RISK FACTOR CLASS PREVALENCE RATES WITHIN CURRENT POPULATION *)

makepriskold[r_, g_] := Block[{},

	priskold	= prisksel[[r, g]];

	If[(patientsel > 0), priskold = RRrisksel[[r, RRriskindsel[[r, disindinv[[Quotient[patientsel, 10]]] + 1]], g]] prisksel[[r, g]]];

	If[(userriskdata >= 1), Do[

		If[(DMriskpairsel[[d, hr]] == r), priskold = RMriskDMinc[[d, hr, g]] prisksel[[r, g]]],

		{d, nd}, {hr, Length[DMriskpairsel[[d]]]}]];

	priskold	= Table[priskold[[ri]] / Plus@@priskold, {ri, ncrsel[[r]]}];

	priskold];


(* CHANGE OF REFERENCE VALUES ACCORDING TO SCENARIO *)

changeprevcurrg[g_, prevcurr_, prisknew_] := Block[{priskold, hprevcurr, disset, currdis}, 

printbug["2.3"];

    hprevcurr = prevcurr;

printbug["2.3.1"];

    disset = Range[nz1];

    If[patientsel > 0, disset = Intersection[disset, zinddis[[disindinv[[Quotient[patientsel,10]]], 2]]]];

    scenpriskind = Table[0, {nrd}];
    Do[If[Max[Abs[prisknew[[r]] - prisksel[[r,g]]]] > 0, scenpriskind[[r]] = 1], {r, nrd}];

printbug["2.3.2"];

    Do[If[(scenpriskind[[r]] > 0),

	(*PERSONS IN RISK FACTOR CLASS*)

printbug["2.3.3"];

        currdis = If[(r > nrdpop), Intersection[disset,zinddis[[disindinv[[disriskindddis[[r - nrdpop]]]], 2]]], disset];
        currdis = Table[Intersection[currdis, zindrisk[[r, ri]]], {ri, ncrsel[[r]]}];

printbug["2.3.4"];

        priskold = makepriskold[r, g];

        Do[transrisk = 
		If[(ncrsel[[r]] == 8),
			makedrisk[r, priskold[[All, agerange[[a]]]],prisknew[[r, All, agerange[[a]]]],1],
			makedrisk[r, priskold[[All, agerange[[a]]]],prisknew[[r, All, agerange[[a]]]], 0]];

printbug["2.3.5"];

          		Do[	If[(rj != ri),
				Do[					
					hprevcurr[[currdis[[ri,zi]] + (rj - ri) zdist[[r]], agerange[[a]]]] +=
						transrisk[[ri, rj]] prevcurr[[currdis[[ri, zi]], agerange[[a]]]];
		                	hprevcurr[[currdis[[ri, zi]], agerange[[a]]]] -= 
						transrisk[[ri, rj]] prevcurr[[currdis[[ri, zi]],agerange[[a]]]],
						{zi, Length[currdis[[ri]]]}]],

					{ri, ncrsel[[r]]}, {rj, ncrsel[[r]]}],
			{a, Length[agerange]}]],
		{r, nrd}];


    hprevcurr];(*END CHANGEPREVCURRG*)

changeprevcurr[prevcurr_, prisk_] :=
                   Table[changeprevcurrg[g, prevcurr[[g]], prisk[[All, g]]],{g, ng}];


(*-------------------------------------------------
 CALCULATION OF INCIDENCE RATES FOR JOINT MODEL
---------------------------------------------------*)

printbug["3."];

makeincjoint[incbase_, RRrisk_, RRdis_] := Block[{},

printbug["3.1"];

	incrate	= Table[0, {ng}, {nd}, {nz1}, {na1}];
	
	Do[	incrate[[g, d, zinddis[[d, 1]]]] = Table[incbase[[d, g]], {Length[zinddis[[d, 1]]]}], {g, ng}, {d, nd}];

	(* ADJUSTMENT FOR POPULATION RISK FACTOR LEVELS *)
	
	Do[	disset	= Intersection[zindrisk[[r, ri]], zinddis[[d, 1]]];

		Do[incrate[[g, d, disset]] *= Table[RRrisk[[r, RRriskindsel[[r, d + 1]], g, ri]], {Length[disset]}],
			{g, ng}],
		{r, nrdpop}, {ri, ncrsel[[r]]}, {d, nd}];

	(* ADJUSTMENT FOR RISK FACTORS ONLY RELEVANT FOR SPECIFIC DISEASES, E.G. HBA1C AND DIABETES *)
	
	Do[	d1 = disindinv[[disriskindddis[[r]]]];

		disset	= Intersection[zindrisk[[r, ri]], zinddis[[d1, 1]]];

		Do[incrate[[g, d1, disset]] *= Table[RRrisk[[nrdpop + r, RRriskindsel[[nrdpop + r, d1 + 1]], g, ri]], {Length[disset]}],
			{g, ng}],

		{r, nrddis}, {ri, ncrsel[[nrdpop + r]]}];

	(* ADJUSTMENT FOR RISK FACTORS ONLY RELEVANT FOR SPECIFIC DISEASES, E.G. HBA1C AND DIABETES *)
	
	Do[	disset	= Intersection[zinddis[[disindinv[[disriskindddis[[r]]]], 2]], zinddis[[d, 1]]];

		Do[	currdis	= Intersection[disset, zindrisk[[nrdpop + r, ri]]];

			Do[incrate[[g, d, currdis]] *=
				Table[RRrisk[[nrdpop + r, RRriskindsel[[nrdpop + r, d + 1]], g, ri]], {Length[currdis]}],
				{g, ng}],

			{ri, ncrsel[[nrdpop + r]]}],

		{r, nrddis}, {d, nd}];

	(* ADJUSTMENT FOR CO-MORBIDITY *)

	Do[	d1		= dispair[[d, 1]];				(* INDEPENDENT DISEASE *)
		d2		= dispair[[d, 2]];				(* DEPENDENT DISEASE *)

		currdis		= Intersection[zinddis[[d1, 2]], zinddis[[d2, 1]]];

		Do[incrate[[g, d2, currdis]] *= Table[RRdis[[RRdisindsel[[d1, d2]], g]], {Length[currdis]}], {g, ng}],

		{d, Length[dispair]}];

	Max0[Min1[incrate]]
	
	]; (* END MAKEINCJOINT *)


(*-------------------------------------------------
 CALCULATION OF RISK FACTOR CLASS TRANSITION RATES FOR MARGINAL MODEL
---------------------------------------------------*)

printbug["4."];

(* ONLY FOR AGERANGE *)

maketransmarginal[transrisk_, transriskind_, r_, ncr_, trackingmult_] := Block[{},

printbug["4.1"];

	transrate = Table[0, {ng}, {na1}, {ncr}, {ncr} ];

	Do[	(* DATA VALUES *)

		htrans	= Table[transrisk[[g, transriskind[[ri, rj]], agerange[[a]]]], {ri, ncr}, {rj, ncr}];

		(* ADJUSTMENT FOR TRACKING *)

		If[(trackingind == 1) && (trstrackingind[[riskindd[[r]]]] == 1),

			Do[htrans[[ri, ri + 1]] += trackingmult trstrackingsel[[r, g, ri, 1, agerange[[a]]]], {ri, ncr - 1}];
			Do[htrans[[ri, ri - 1]] += trackingmult trstrackingsel[[r, g, ri, 2, agerange[[a]]]], {ri, 2, ncr}]];

		(* RISK -> RATE TRANSFORMATION *)

		htrans 	= -Log[1 - htrans];
		
		Do[	htrans[[ri, ri]] = - Plus@@htrans[[ri]], {ri, ncr}];

		(* MATRIX RATE -> RISK TRANSFORMATION *)

		transrate[[g, agerange[[a]]]] = Max0[Min1[DiagonalMatrix[Table[1, {ncr}]] + htrans + .5 htrans.htrans]],

		{g, ng}, {a, Length[agerange]}];

	transrate = Table[transrate[[g, Range[na1], ri, rj]], {g, ng}, {ri, ncr}, {rj, ncr}]
	
	];

(* FROM MINIMUM OF AGERANGE ONWARDS *)

maketransmarginalext[transrisk_, transriskind_, r_, ncr_, trackingmult_] := Block[{},

printbug["4.2"];

	transrate = Table[0, {ng}, {na1}, {ncr}, {ncr} ];

	hagerange = Range[Min[agerange], na1];

	Do[	(* DATA VALUES *)

		htrans	= Table[transrisk[[g, transriskind[[ri, rj]], hagerange[[a]]]], {ri, ncr}, {rj, ncr}];

		(* ADJUSTMENT FOR TRACKING *)

		If[(trackingind == 1) && (trstrackingind[[riskindd[[r]]]] == 1),

			Do[htrans[[ri, ri + 1]] += trackingmult trstrackingsel[[r, g, ri, 1, hagerange[[a]]]], {ri, ncr - 1}];

			Do[htrans[[ri, ri - 1]] += trackingmult trstrackingsel[[r, g, ri, 2, hagerange[[a]]]], {ri, 2, ncr}]	];

		(* RISK -> RATE TRANSFORMATION *)

		htrans 	= -Log[1 - htrans];
		
		Do[	htrans[[ri, ri]] = - Plus@@htrans[[ri]], {ri, ncr}];

		(* MATRIX RATE -> RISK TRANSFORMATION *)

		transrate[[g, hagerange[[a]]]] = Max0[Min1[DiagonalMatrix[Table[1, {ncr}]] + htrans + .5 htrans.htrans]],

		{g, ng}, {a, Length[hagerange]}];

	transrate = Table[transrate[[g, Range[na1], ri, rj]], {g, ng}, {ri, ncr}, {rj, ncr}]
	
	];


(*-------------------------------------------------
 CONSTRUCTION OF TRANSITION PROBABILITY MATRIX SPECIFIED BY GENDER AND AGE
---------------------------------------------------*)

printbug["5."];

maketransjointga[g_, a_, transrisk_, incbase_, excessmort_, casefat_, RRrisk_, trackingmult_, RRdis_, RRcasefat_] := Block[{},

printbug["5.1"];

	transjoint1	= Table[0, {nz1}, {nz1}];
	incrate1	= Table[0, {nd}, {nz1}];

(* EXCESS MORTALITY RATES FOR ONE-DISEASE AND CO-MORBID DISEASE PATIENTS *)

printbug["5.1.1"];	

	excessmortadj 	= Min1[Max0[excessmortseladj + (excessmort - excessmortsel)]];

(* chang151206RH *)
(* DISEASE INCIDENCE RATES *)

printbug["5.1.2"];

	Do[	(* DISEASE-FREE STATES *)

		disset = zinddis[[d, 1]];

		incrate2 = Table[0, {nz1}];

		(* ADJUSTMENT FOR DISCRETELY DISTRIBUTED POPULATION RISK FACTORS AND CO-MORBIDITY *)

		incrate2[[disset]] = incbase[[d, g, a]] *

				Times@@Table[RRrisk[[r, RRriskindsel[[r, d + 1]], g, zind[[disset, r]], a]], {r, nrdpop}] *

				Times@@Table[(2 - zind[[disset, nrd + d1]]) +
							(zind[[disset, nrd + d1]] - 1) RRdis[[RRdisindsel[[d1, d]], g, a]],
						{d1, nd}];

		(* ADJUSTMENT FOR DISCRETELY DISTRIBUTED DISEASE-SPECIFIC POPULATION RISK FACTORS, E.G. HBA1C *)

		Do[	currdis	= Intersection[zinddis[[disindinv[[disriskindddis[[r]]]], 2]], zindrisk[[riskindddis[[r]], ri]], disset];

			incrate2[[currdis]] *= RRrisk[[nrdpop + r, RRriskindsel[[nrdpop + r, d + 1]], g, ri, a]],

			{r, nrddis}, {ri, ncrsel[[nrdpop + r]]}];

		casefat2 = Table[casefat[[casefatindsel[[d]], g, a]] /
					Times@@Table[1 + (RRcasefat[[RRcasefatindsel[[d1, d]], g, a]] - 1) pdissel[[d1, g, a]],
							{d1, nd}],
				{nz1}];

		Do[	currdis = Intersection[disset, zinddis[[d1, 2]]];

			casefat2[[currdis]] *= RRcasefat[[RRcasefatindsel[[d1, d]], g, a]],

			{d1, nd}];

		(* ADJUSTMENT FOR RISK FACTORS ONLY RELEVANT FOR SPECIFIC DISEASES, E.G. HBA1C AND DIABETES *)
	
		Do[	d1 = disindinv[[disriskindddis[[r]]]];

			If[(d == d1),

				Do[	currdis	= Intersection[zindrisk[[r, ri]], disset];

					incrate2[[currdis]] *=
						Table[RRrisk[[nrdpop + r, RRriskindsel[[nrdpop + r, d1 + 1]], g, ri, a]], {Length[currdis]}],

					{ri, ncrsel[[nrdpop + r]]}]],

			{r, nrddis}]; 

		incrate2 = - Log[1 - incrate2];

		incrate1[[d]] = incrate2;

		Do[transjoint1[[disset[[zi]], disset[[zi]] + zdist[[nrd + d]]]] += (1 - casefat2[[zi]]) incrate2[[disset[[zi]]]],
				{zi, Length[disset]}];

		Do[transjoint1[[disset[[zi]], disset[[zi]]]] -= incrate2[[disset[[zi]]]],
				{zi, Length[disset]}],

		{d, nd}];

(* DISEASE REMISSION RATES *)

printbug["5.1.3"];

	Do[	disset	= zinddis[[d, 2]];

		remrate	= - Log[1 - rem1[[remindsel[[d]], g, a]]];

		Do[transjoint1[[disset[[zi]], disset[[zi]] - zdist[[nrd + d]]]] += remrate, {zi, Length[disset]}];

		Do[transjoint1[[disset[[zi]], disset[[zi]]]] -= remrate, {zi, Length[disset]}],

		{d, nd}];

(* RISK FACTOR CLASS TRANSITION RATES *)

printbug["5.1.4"];

	Do[	disset	= zindrisk[[r, ri]];

		htrans	= Table[0, {nz1}];

		Do[	If[(transriskindsel[[r, ri, rj]] > 1),

				htrans[[disset]]	= transrisk[[r, g, transriskindsel[[r, ri, rj]], a]];

				(* ADJUSTMENT FOR TRACKING *)
				
				If[(trackingind == 1) && (trstrackingind[[riskindd[[r]]]] == 1),

					If[(rj == (ri + 1)), htrans[[disset]] += trackingmult trstrackingsel[[r, g, ri, 1, a]]];
				
					If[(rj == (ri - 1)), htrans[[disset]] += trackingmult trstrackingsel[[r, g, ri, 2, a]]]];

				(* TIME-CONSTANT CLASS PREVALENCE RATES IN DISESE-FREE PERSONS FOR DISEASE SPECIFIC RISK FACTORS *)

				If[(r > nrdpop),

					currdis	= Intersection[disset, zinddis[[disindinv[[disriskindddis[[r - nrdpop]]]], 1]]];

					htrans[[currdis]] = 0];

				transrate	= - Log[1 - htrans];

				Do[transjoint1[[disset[[zi]], disset[[zi]] + zdist[[r]] (rj - ri)]] += transrate[[disset[[zi]]]],
						{zi, Length[disset]}];

				Do[transjoint1[[disset[[zi]], disset[[zi]]]] -= transrate[[disset[[zi]]]],
						{zi, Length[disset]}]

				],

			{rj, ncrsel[[r]]}],

		{r, nrd}, {ri, ncrsel[[r]]}];
	
(* DISEASE-RELATED EXCESS MORTALITY RATES *)

printbug["5.1.5"];

	Do[	disset = zinddis[[d, 2]];
		excessmortrate	= - Log[1 - excessmortadj[[d, g, a]]];
		
		Do[transjoint1[[disset[[zi]], disset[[zi]]]] -=  excessmortrate, {zi, Length[disset]}],

		{d, nd}];

(* OTHER CAUSES MORTALITY RATES *)

printbug["5.1.6"];

	mortothrate1 = - Log[1 - mortothsel[[g, a]]] Table[1, {nz1}];

	Do[	currdis = zindrisk[[r, ri]];
		mortothrate1[[currdis]] *= RMothrisksel[[r, g, ri, a]],
		{r, nrdpop}, {ri, ncrsel[[r]]}];

	Do[	disset	= zindrisk[[nrdpop + r, ri]];
		currdis = Intersection[disset, zinddis[[disindinv[[disriskindddis[[r]]]], 1]]];
		mortothrate1[[currdis]] *= RMothrisksel[[r, g, 1, a]];
		currdis = Intersection[disset, zinddis[[disindinv[[disriskindddis[[r]]]], 2]]];
		mortothrate1[[currdis]] *= RMothrisksel[[r, g, ri, a]],		
		{r, nrddis}, {ri, ncrsel[[r]]}];

	Do[transjoint1[[zi, zi]] -= mortothrate1[[zi]], {zi, nz1}];

(* TRANSFORMATION OF RATES TO 1-YEAR RISKS *)

printbug["5.1.7"];

	transjoint1 	= DiagonalMatrix[Table[1, {nz1}]] + transjoint1 + .5 transjoint1.transjoint1;

	incrate1	= Max0[Min1[1 - Exp[-incrate1]]];

	mortothrate1	= Max0[Min1[1 - Exp[-mortothrate1]]];

	{transjoint1, incrate1, mortothrate1}

]; (* END MAKETANSJOINTGA *)


(*-------------------------------------------------
 CONSTRUCTS TRANSITION PROBABILITY MATRIX OVER GENDER AND AGE
---------------------------------------------------*)

maketransjoint[transrisk_, incbase_, excessmort_, casefat_, RRrisk_, trackingmult_, RRdis_, RRcasefat_] := Block[{},

printbug["5.2"];

	transjoint	= Table[0, {ng}, {nz1}, {nz1}, {na1}];
	incrate		= Table[0, {ng}, {nd}, {nz1}, {na1}];
	mortothrate	= Table[0, {ng}, {nz1}, {na1}];

	Do[	rate 	=  maketransjointga[g, agerange[[a]], transrisk, incbase, excessmort, casefat,
						RRrisk, trackingmult, RRdis, RRcasefat];

		transjoint[[g, Range[nz1], Range[nz1], agerange[[a]]]] 	= rate[[1]];

		incrate[[g, Range[nd], Range[nz1], agerange[[a]]]] 	= rate[[2]];

		mortothrate[[g, Range[nz1], agerange[[a]]]] 		= rate[[3]],

		{g, ng}, {a, Length[agerange]}];

	{transjoint, incrate, mortothrate}

]; (* END MAKETRANSJOINT *)


(*-------------------------------------------------
 TRANSFORMS OUTPUT SPECIFIED BY AGE AT BASELINE TO CURRENT AGE
---------------------------------------------------*)

printbug["6."];

transformres[respop_, resrisk_, resdist_, resdis_, resinc_, resmort_, resonsetage_, resduurstop_, resmortrisk_] := Block[{},

(* POPULATION NUMBERS *)

	respop1 = Table[0, {nscen}, {nstap}, {ng}, {nac[[1]]}];

	Do[	hres	= Table[0, {nstap}, {na1 + nstap}];

		Do[hres[[n, agesel[[a]] + n - 1]] += respop[[scen, n, g, a]], {n, nstap}, {a, Length[agesel]}];

		hres	= Table[Flatten[{hres[[n, Range[na]]], Plus@@Drop[hres[[n]], na]}], {n, nstap}];

		Do[respop1[[scen, n, g]] = aggreg[hres[[n]], 1], {n, nstap}],

		{scen, nscen}, {g, ng}];

(* RISK FACTOR CLASS PREVALENCE NUMBERS *)

	resrisk1 = Table[0, {nscen}, {nstap}, {r, nrd}, {ng}, {ncrsel[[r]]}, {nac[[1]]}];

	Do[	hres	= Table[0, {nstap}, {na1 + nstap}];

		Do[hres[[n, agesel[[a]] + n - 1]] += resrisk[[scen, n, r, g, ri, a]], {n, nstap}, {a, Length[agesel]}];

		hres	= Table[Flatten[{hres[[n, Range[na]]], Plus@@Drop[hres[[n]], na]}], {n, nstap}];

		Do[resrisk1[[scen, n, r, g, ri]] = aggreg[hres[[n]], 1], {n, nstap}],

		{scen, nscen}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];

(* DISEASE PREVALENCE NUMBERS *)

	resdis1 = Table[0, {nscen}, {nstap}, {nd}, {ng}, {nac[[1]]}];

	Do[	hres	= Table[0, {nstap}, {na1 + nstap}];

		Do[hres[[n, agesel[[a]] + n - 1]] += resdis[[scen, n, d, g, a]], {n, nstap}, {a, Length[agesel]}];

		hres	= Table[Flatten[{hres[[n, Range[na]]], Plus@@Drop[hres[[n]], na]}], {n, nstap}];

		Do[resdis1[[scen, n, d, g]] = aggreg[hres[[n]], 1], {n, nstap}],

		{scen, nscen}, {d, nd}, {g, ng}];

(* DISEASE INCIDENCE NUMBERS *)

	resinc1 = Table[0, {nscen}, {nstap}, {nd}, {ng}, {nac[[1]]}];

	Do[	hres	= Table[0, {nstap}, {na1 + nstap}];

		Do[hres[[n, agesel[[a]] + n - 1]] += resinc[[scen, n, d, g, a]], {n, nstap}, {a, Length[agesel]}];

		hres	= Table[Flatten[{hres[[n, Range[na]]], Plus@@Drop[hres[[n]], na]}], {n, nstap}];

		Do[resinc1[[scen, n, d, g]] = aggreg[hres[[n]], 1], {n, nstap}],

		{scen, nscen}, {d, nd}, {g, ng}];

(* MORTALITY NUMBERS *)

	resmort1 = Table[0, {nscen}, {nstap}, {nd + 2}, {ng}, {nac[[1]]}];

	Do[	hres	= Table[0, {nstap}, {na1 + nstap}];

		Do[hres[[n, agesel[[a]] + n - 1]] += resmort[[scen, n, d, g, a]], {n, nstap}, {a, Length[agesel]}];

		hres	= Table[Flatten[{hres[[n, Range[na]]], Plus@@Drop[hres[[n]], na]}], {n, nstap}];

		Do[resmort1[[scen, n, d, g]] = aggreg[hres[[n]], 1], {n, nstap}],

		{scen, nscen}, {d, nd + 2}, {g, ng}];

(* SUM OF AGE AT ONSET OF DISEASED PERSONS *)

	resonsetage1 = Table[0, {nscen}, {nstap}, {nd}, {ng}, {nac[[1]]}];

	Do[	hres	= Table[0, {nstap}, {na1 + nstap}];

		Do[hres[[n, agesel[[a]] + n - 1]] += resonsetage[[scen, n, d, g, a]], {n, nstap}, {a, Length[agesel]}];

		hres	= Table[Flatten[{hres[[n, Range[na]]], Plus@@Drop[hres[[n]], na]}], {n, nstap}];

		Do[resonsetage1[[scen, n, d, g]] = aggreg[hres[[n]], 1], {n, nstap}],

		{scen, nscen}, {d, nd}, {g, ng}];

(* SUM OF TIME DURATION SINCE SMOKING CESSATION *)

	If[(RRsmokduurind == 1) && (riskindd[[1]] == 1),

		(* IF DURATION DEPENDENT *)

		resduurstop1 = Table[0, {nscen}, {nstap}, {ng}, {nac[[1]]}];

		Do[	hres	= Table[0, {nstap}, {na1 + nstap}];

			Do[hres[[n, agesel[[a]] + n - 1]] += resduurstop[[scen, n, g, a]], {n, nstap}, {a, Length[agesel]}];

			hres = Table[Flatten[{hres[[n, Range[na]]], Plus@@Drop[hres[[n]], na]}], {n, nstap}];

			Do[resduurstop1[[scen, n, g]] = aggreg[hres[[n]], 1], {n, nstap}],

			{scen, nscen}, {g, ng}],

		(* IF NOT DURATION DEPENDENT *)

		resduurstop1 = resduurstop

		];

(* CHARACTERISTICS OF CONTINUOUSLY DISTRIBUTED RISK FACTORS *)

	If[(riskcontind == 1) && (nrc > 0),

		resdist1 = Table[0, {nscen}, {nstap}, {nrc}, {ng}, {2}, {nac[[1]]}];

		Do[	hres	= Table[0, {nstap}, {na1 + nstap}];

			Do[hres[[n, agesel[[a]] + n - 1]] += resdist[[scen, n, r, g, ri, a]], {n, nstap}, {a, Length[agesel]}];
			hres	= Table[Flatten[{hres[[n, Range[na]]], Plus@@Drop[hres[[n]], na]}], {n, nstap}];
			Do[resdist1[[scen, n, r, g, ri]] = aggreg[hres[[n]], 1], {n, nstap}],
			{scen, nscen}, {r, nrc}, {g, ng}, {ri, 2}],

		resdist1 = resdist

		];	

(* RISK FACTOR CLASS MORTALITY NUMBERS *)

	resmortrisk1 = Table[0, {nscen}, {nstap}, {r, nrd}, {ng}, {ncrsel[[r]]}, {nac[[1]]}];

	Do[	hres	= Table[0, {nstap}, {na1 + nstap}];

		Do[hres[[n, agesel[[a]] + n - 1]] += resmortrisk[[scen, n, r, g, ri, a]], {n, nstap}, {a, Length[agesel]}];

		hres	= Table[Flatten[{hres[[n, Range[na]]], Plus@@Drop[hres[[n]], na]}], {n, nstap}];

		Do[resmortrisk1[[scen, n, r, g, ri]] = aggreg[hres[[n]], 1], {n, nstap}],

		{scen, nscen}, {r, nrd}, {g, ng}, {ri, ncrsel[[r]]}];	

	{respop1, resrisk1, resdist1, resdis1, resinc1, resmort1, resonsetage1, resduurstop1, resmortrisk1}

]; (* END TRANSFORMRES *)


(* --------------------------------------------------
 CALCULATION OF SMOKING CLASS TRANSITION RATES ACCORDING TO GIVEN SCENARIO scen AND FOR GIVEN TIME n
----------------------------------------------------*)

maketransrisksmok[transrisk_, relapserate_] := Block[{},
	
	htransrisksmok = Table[0, {ng}, {ncsmok}, {ncsmok}, {na1}];
	
	Do[	(* TRANSITIONS BETWEEN CLASSES *)			

		htransrisksmok[[g, 1, {2, 3}]]		+= transrisk[[1, g, 1, {2, 3}]];
		htransrisksmok[[g, 2, 3]] 		+= transrisk[[1, g, 2, 3]];
		Do[htransrisksmok[[g, 2 + ri, 2]] 	+= relapserate[[g, ri]], {ri, nstopduur}];

		Do[htransrisksmok[[g, ri, ri]] 		= 1 - Plus@@htransrisksmok[[g, ri]], {ri, ncsmok}];
		Do[htransrisksmok[[g, ri, ri + 1]]	= htransrisksmok[[g, ri, ri]], {ri, 3, ncsmok - 1}];
		Do[htransrisksmok[[g, ri, ri]]		*= 0, {ri, 3, ncsmok - 1}],

		{g, ng}];

	htransrisksmok]; (* MAKETRANSRISKSMOK *)


(* --------------------------------------------------
 CALCULATION OF SMOKING CLASS MORTALITY RATES FOR GIVEN TIME n
----------------------------------------------------*)

makemortsmok[nrisksmok_, RRsmokform_, mortspec_, RMothsmok_, mortoth_] := Block[{},

	hmortsmok = Table[0, {ng}, {ncsmok}, {na1}];	

	(* DISEASE-RELATED EXCESS MORTALITY RATES *)

	ERRsmok = Table[(Plus@@Table[nrisksmok[[g, ri]] RRrisksel[[1, RRriskindsel[[1, d + 1]], g, ri]], {ri, 2}] +
				Plus@@Table[nrisksmok[[g, 2 + ri]] RRsmokform[[RRriskindsel[[1, d + 1]], g, ri]], {ri, nstopduur}]) /
				(Plus@@nrisksmok[[g]] + eps),
				{d, nd}, {g, ng}];

	Do[hmortsmok[[g, ri]] += Plus@@Table[mortspec[[d, g]] RRrisksel[[1, RRriskindsel[[1, d + 1]], g, ri]] / (ERRsmok[[d, g]] + eps),
						{d, nd}],
				{g, ng}, {ri, 2}];

	Do[hmortsmok[[g, 2 + ri]] += Plus@@Table[mortspec[[d, g]] RRsmokform[[RRriskindsel[[1, d + 1]], g, ri]] / (ERRsmok[[d, g]] + eps),
						{d, nd}],
				{g, ng}, {ri, nstopduur}];

	(* OTHER CAUSES MORTALITY RATES *)

	ERMothsmok = Table[Plus@@(RMothsmok[[g]] nrisksmok[[g]]) / (Plus@@nrisksmok[[g]] + eps), {g, ng}];

	Do[hmortsmok[[g, ri]] += RMothsmok[[g, ri]] mortoth[[g]] / (ERMothsmok[[g]] + eps), {g, ng}, {ri, ncsmok}];

	hmortsmok]; (* MAKEMORTSMOK *)



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
