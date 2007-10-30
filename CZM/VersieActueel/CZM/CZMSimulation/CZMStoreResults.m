(* :Title: CZMStoreResults *)

(* :Context: CZMSimulation` *)

(* :Author: Rudolf T. Hoogenveen, Roel G.M. Breuls *)

(* :Summary:
   CZM postprocessing routine stores results *)

(* :Copyright: © 2006 by Rudolf Hoogenveen *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	 3.1 March 2007 *)

(* :Keywords: postprocessing, results, storage, file *)


BeginPackage["CZMSimulation`CZMStoreResults`",
	{"CZMMain`CZMMain`",
	"CZMInitialization`CZMLogFile`",
	"CZMInitialization`CZMConstants`",
	"CZMInitialization`CZMFunctions`", 
	"CZMImportData`CZMImportUserSelections`",
	"CZMImportData`CZMImportDemography`",
	"CZMAdjustData`CZMMakeSelections`",
	"CZMAdjustData`CZMAdjustAfterSelection`",
	"CZMSimulation`CZMSimulationMarginalModelDetermPop`",
	"CZMSimulation`CZMSimulationJointModelDetermPop`",
	"CZMSimulation`CZMSimulationJointModelDetermAge`",
	"CZMSimulation`CZMSimulationJointModelStochInd`",
	"CZMSimulation`CZMSimulationMarginalModelDetermDis`"}]

resmodel	::usage	= "list of CZM model results"
leesprev1	::usage	= "reads data from file with results from marginal model"
leesprevpop	::usage	= "reads data from file with results from joint population model"
leesprevage	::usage	= "reads data from file with results from joint age-cohort model"
leesprevind	::usage	= "reads data from file with results from joint individual model"
leesprevpop1	::usage	= "reads data from file with results from multiple joint population models"
leesprevage1	::usage	= "reads data from file with results from multiple joint age-cohort models"
leesprevind1	::usage	= "reads data from file with results from multiple joint individual models"


Begin["`Private`"]


Print["CZMStoreResults package is evaluated"];
printtijd;

printbug[c_] := If[(bugind == 1), Print[{"CZMStoreResults", c}]];

printbug["1."];


(* --------------------------------------------------
	CONCATENATION OF MODEL RESULTS
----------------------------------------------------*)

appltype	= StringTake[ToString[Global`application], {8, 11}];

If[(appltype != "outp"),

printbug["1.1"];

	(* MARGINAL MODEL RESULTS: MARGINAL MODEL ON DISEASE INCLUDED *)

	If[(appltype == "test"),

		resmodel	= {resmarginalmodeldetermpop, resjointmodeldetermpop, resjointmodeldetermage, resjointmodelstochind,
					resmarginalmodeldetermdis,
					excessmortseladj, mortothsel, RRriskseladj, RMothrisksel},

		resmodel	= {resmarginalmodeldetermpop, resjointmodeldetermpop, resjointmodeldetermage, resjointmodelstochind,
					resmarginalmodeldetermdis,
					0, 0, 0, 0}];

	PutAppend[{riskindd, disind, excessmortcond, mortothind, agemin, agemax, modelsel, nstap, nscen0},
			Global`outputpath <> "outfileresmodelrun.m"];

	Do[PutAppend[resmodel[[m, o, scen, n]], Global`outputpath <> "outfileresmodelrun.m"],
		{m, nmodel}, {o, Length[resmodel[[m]]]}, {scen, nscen}, {n, nstap}];

	Do[PutAppend[resmodel[[m]], Global`outputpath <> "outfileresmodelrun.m"], {m, nmodel + 1, Length[resmodel]}];
	
	(* JOINT POPULATION MODEL RESULTS *)

	If[(modelsel[[2]] == 1),
printbug["1.2"];

	dat	= OpenRead[Global`outputpath <> "jointmodeldetermpopprev.m"];
		hres	= Read[dat];
		PutAppend[hres, Global`outputpath <> "jointmodeldetermpopprev1.m"];
		hnscen	= hres[[1]];
		hnstap	= hres[[3]];
		Do[	PutAppend[Read[dat], Global`outputpath <> "jointmodeldetermpopprev1.m"], {hnscen}, {hnstap}];
		Close[dat]];
		
	(* JOINT AGE-COHORT MODEL RESULTS *)

	If[(modelsel[[3]] == 1),
printbug["1.3"]; 
		dat	= OpenRead[Global`outputpath <> "jointmodeldetermageprev.m"];
		hres	= Read[dat];
		PutAppend[hres, Global`outputpath <> "jointmodeldetermageprev1.m"];
		hnscen	= hres[[1]];
		nagesel	= hres[[2]];
		hnstap	= hres[[3]];
		Do[	PutAppend[ReadList[dat, Expression, hnstap], Global`outputpath <> "jointmodeldetermageprev1.m"],
			{hnscen}, {ng}, {nagesel}];
		Close[dat]];
	
	(* JOINT INDIVIDUAL MODEL RESULTS *)

	If[(modelsel[[4]] == 1),
printbug["1.4"];
		dat	= OpenRead[Global`outputpath <> "jointmodelstochindprev.m"];
		hres	= Read[dat];
		PutAppend[hres, Global`outputpath <> "jointmodelstochindprev1.m"];
		hnscen	= hres[[1]];
		nagesel	= hres[[2]];
		hndraw	= hres[[6]];
		Do[	PutAppend[ReadList[dat, Expression, hndraw], Global`outputpath <> "jointmodelstochindprev1.m"],
			{hnscen}, {ng}, {nagesel}];
		Close[dat]];
printbug["1.6"],

	resmodel = (ReadList[Global`outputpath <> resfile])[[1 + 2 run]];

	];

(* --------------------------------------------------
	READ PROCEDURES
----------------------------------------------------*)

(* MARGINAL MODEL *)

leesprev1 := Block[{},

printbug["2.1"];

	dat	= OpenRead[Global`outputpath <> "outfileresmodelrun.m"];
	Read[dat];
	hsel 	= Read[dat]; 
	hnstap	= hsel[[8]];
	hnscen	= hsel[[9]];
	res 	= Table[0., {10}, {hnscen}, {hnstap}];
	Do[	res[[o, scen, n]] += Read[dat], {o, 10}, {scen, hnscen}, {n, hnstap}]; 
	Close[dat];

	{{hnscen, hnstap}, res}];	

(* JOINT MODEL DETERMINISTIC POPULATION *)

leesprevpop := Block[{},

printbug["2.2"];

	dat	= OpenRead[Global`outputpath <> "jointmodeldetermpopprev.m"];
	hres 	= Read[dat];
	hnscen	= hres[[1]];
	hnstap	= hres[[3]];
	hnprev	= Table[0., {hnscen}, {hnstap}];
	Do[hnprev[[scen, n]] += Read[dat], {scen, hnscen}, {n, hnstap}];
	Close[dat];

	{hres, hnprev}];

(* JOINT MODEL DETERMINISTIC AGE COHORTS *)

leesprevage := Block[{},

printbug["2.3"];

	dat	= OpenRead[Global`outputpath <> "jointmodeldetermageprev.m"];

	hres 	= Read[dat];
	hnscen 	= hres[[1]];
	nagesel	= hres[[2]];
	hnstap	= hres[[3]];
	hnz1	= hres[[4]];
	hagemin	= hres[[5]];
	hna	= Max[{na1, hagemin + nagesel + hnstap}];

	hnprev = Table[0., {hnscen}, {hnstap}, {ng}, {hna}, {hnz1}];

	Do[hnprev[[scen, n, g, hagemin + a + n - 2]] += Read[dat], {scen, hnscen}, {g, ng}, {a, nagesel}, {n, hnstap}];

	Close[dat];

	hnprev	= Transpose[hnprev, {2, 3, 4, 1, 5}];

	If[(hna > na1),	hnprev[[na1]] = Plus@@Drop[hnprev, na];
			hnprev	= hnprev[[Range[na1]]]];

	hnprev	= Table[Plus@@hnprev[[Range[5 ai - 4, Min[{5 ai, na1}]]]], {ai, nac[[1]]}];
	hnprev	= Transpose[hnprev, {5, 1, 2, 3, 4}];

	{hres, hnprev}];

(* JOINT MODEL STOCHASTIC INDIVIDUALS *)

leesprevind := Block[{},

printbug["2.4"];

	dat	= OpenRead[Global`outputpath <> "jointmodelstochindprev.m"];
	hres 	= Read[dat];
	hnscen 	= hres[[1]];
	nagesel = hres[[2]];
	hnstap 	= hres[[3]];
	hnz1 	= hres[[4]];
	hagemin = hres[[5]];
	hndraw 	= hres[[6]];

	hnprev 	= Table[0., {hnscen}, {ng}, {nagesel}, {hnstap}, {hnz1}];

	Do[	hresi = Read[dat];
		nstapi = Max[Range[hnstap] Sign[hresi[[4]]]];
		Do[++hnprev[[scen, g, a, n, hresi[[4, n]]]], {n, nstapi}], {scen, hnscen}, {g, ng}, {a, nagesel}, {hndraw}];

	Do[hnprev[[scen, g, a, n]] *= npop0[[g, hagemin + a - 1 ]] / hndraw, {scen, hnscen}, {g, ng}, {a, nagesel}, {n, hnstap}];
	hnprev	= Transpose[hnprev, {3, 4, 1, 2, 5}];
	hnprev1	= Table[0., {na1}, {hnstap}, {hnscen}, {ng}, {hnz1}]; 
	Do[hnprev1[[Min[{hagemin + a + n - 2, na1}], n]] += hnprev[[a, n]], {a, nagesel}, {n, hnstap}];
	hnprev	= Table[Plus@@hnprev1[[Range[5 ai - 4, Min[{5 ai, na1}]]]], {ai, nac[[1]]}];
	hnprev	= Transpose[hnprev, {5, 2, 1, 3, 4}];

	{hres, hnprev}];

(* JOINT MODEL DETERMINISTIC POPULATION, SEVERAL RUNS *)

leesprevpop1 := Block[{},

printbug["2.5"];

	dat	= OpenRead[Global`outputpath <> "jointmodeldetermpopprev1.m"];
	hnrun	= Read[dat][[1]];
	hres	= hnprev = Table[0., {hnrun}];
	Do[	hres[[hrun]]	= Read[dat];
		hnscen		= hres[[hrun, 1]];
		hnstap		= hres[[hrun, 3]];
		hnprev[[hrun]]	= Table[0., {hnscen}, {hnstap}];
		Do[hnprev[[hrun, scen, n]] += Read[dat], {scen, hnscen}, {n, hnstap}],
		{hrun, hnrun}];
	Close[dat];
   
	Table[{hres[[hrun]], hnprev[[hrun]]}, {hrun, hnrun}]];

(* JOINT MODEL DETERMINISTIC AGE COHORTS *)

leesprevage1 := Block[{},

printbug["2.6"];

	dat	= OpenRead[Global`outputpath <> "jointmodeldetermageprev1.m"];
	hnrun	= Read[dat][[1]];
	hres	= hnprev = Table[0., {hnrun}];
printbug["2.6.1"];	
	Do[	hres[[hrun]]	= Read[dat];
		hnscen		= hres[[hrun, 1]];
		nagesel		= hres[[hrun, 2]];
		hnstap		= hres[[hrun, 3]];
		hnz1		= hres[[hrun, 4]];
		hagemin		= hres[[hrun, 5]];
		hna		= Max[{na1, hagemin + nagesel + hnstap}];
		hnprev[[hrun]]	= Table[0., {hnscen}, {ng}, {hna}, {hnstap}, {hnz1}];
printbug["2.6.2"];
		Do[	resi	= Read[dat];
			Do[hnprev[[hrun, scen, g, hagemin + a + n - 2, n]] += resi[[n]], {n, hnstap}],
			{scen, hnscen}, {g, ng}, {a, nagesel}]; 
		hnprev[[hrun]]	= Transpose[hnprev[[hrun]], {2, 4, 1, 3, 5}];
printbug["2.6.3"];
		If[(hna > na1),	hnprev[[hrun, na1]] = Plus@@Drop[hnprev[[hrun]], na];
printbug["2.6.4"];
		hnprev[[hrun]]	= hnprev[[hrun, Range[na1]]]];
		hnprev[[hrun]]	= Table[Plus@@hnprev[[hrun, Range[5 ai - 4, Min[{5 ai, na1}]]]], {ai, nac[[1]]}];
		hnprev[[hrun]]	= Transpose[hnprev[[hrun]], {5, 1, 2, 3, 4}],

		{hrun, hnrun}];

	Table[{hres[[hrun]], hnprev[[hrun]]}, {hrun, hnrun}]];

(* JOINT MODEL STOCHASTIC INDIVIDUALS *)

leesprevind1 := Block[{},

printbug["2.7"];

	dat	= OpenRead[Global`outputpath <> "jointmodelstochindprev1.m"];
	hnrun	= Read[dat][[1]];
	hres	= hnprev = Table[0., {hnrun}];
	Do[	hres[[hrun]]	= Read[dat];
		hnscen		= hres[[hrun, 1]];
		nagesel		= hres[[hrun, 2]];
		hnstap 		= hres[[hrun, 3]];
		hnz1 		= hres[[hrun, 4]];
		hagemin 	= hres[[hrun, 5]];
		hndraw 		= hres[[hrun, 6]];
		hnprev[[hrun]] 	= Table[0., {hnscen}, {ng}, {nagesel}, {hnstap}, {hnz1}];
		Do[	hdat 	= Read[dat];
			Do[	hresi = hdat[[i]];
				nstapi 	= Max[Range[hnstap] Sign[hresi[[4]]]];
				Do[++hnprev[[hrun, scen, g, a, n, hresi[[4, n]]]], {n, nstapi}],
				{i, hndraw}],
			{scen, hnscen}, {g, ng}, {a, nagesel}];
		Do[hnprev[[hrun, scen, g, a, n]] *= npop0[[g, hagemin + a - 1]] / hndraw, {scen, hnscen}, {g, ng}, {a, nagesel}, {n, hnstap}];
		hnprev[[hrun]]	= Transpose[hnprev[[hrun]], {3, 4, 1, 2, 5}];
		hnprev1		= Table[0., {na1}, {hnstap}, {hnscen}, {ng}, {hnz1}];
		Do[hnprev1[[Min[{hagemin + a + n - 2, na1}], n]] += hnprev[[hrun, a, n]], {a, nagesel}, {n, hnstap}];
		hnprev[[hrun]]	= Table[Plus@@hnprev1[[Range[5 ai - 4, Min[{5 ai, na1}]]]], {ai, nac[[1]]}];
		hnprev[[hrun]]	= Transpose[hnprev[[hrun]], {5, 2, 1, 3, 4}],

		{hrun, hnrun}];
	
	Table[{hres[[hrun]], hnprev[[hrun]]}, {hrun, hnrun}]];
	
printtijd;


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
