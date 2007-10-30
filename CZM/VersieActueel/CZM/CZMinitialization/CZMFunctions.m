(* :Title: CZMFunctions *)

(* :Context: CZMInitialization` *)

(* :Author: Rudolf T. Hoogenveen, Roel G.M. Breuls *)

(* :Summary:
   CZM initialization routine defines a number of general functions and unit vectors *)

(* :Copyright: © 2004 by Roel G.M. Breuls *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March, *010305.txt input files
		3.0 version novermber 2005 
		3.1 version March 2007; input file reading routines *)

(* :Keywords: help functions, smoothing functions, unit vectors *)


BeginPackage["CZMInitialization`CZMFunctions`",
	{"CZMInitialization`CZMLogFile`",
	"CZMInitialization`CZMConstants`",
	"Statistics`NormalDistribution`",
	"Statistics`ContinuousDistributions`"}]


skipcomment	::usage	= "skips comment lines in data file"

Min1::usage 	= "Min1[x]: cuts off values larger than 1: 1 if x>=1 and x if x<1"
Max1::usage 	= "Max1[x]: cuts off values smaller than 1: 1 if x<=1 and x if x>1"
Min0::usage 	= "Min0[x]: Cuts off values smaller than 0: 0 if x>=0 and x if x<0"
Max0::usage 	= "Max0[x]: cuts off values smaller than 0: 0 if x<=0 and x if x>0"
Maxc::usage 	= "Maxc[x,c]: cuts off values smaller than c: c if x<=c and x if x>c"
Minc::usage 	= "Minc[x,c]: cuts off values larger than c: c if x>=c and x if x<c"
roundoff::usage	= "roundoff[x,m]: rounds off x to m decimals"

vect0::usage 	= "vector of length nac[[1]] with values 0"
vect1::usage 	= "vector of length nac[[1]] with values 1"
vect00::usage 	= "vector of length na1 with values 0"
vect11::usage 	= "vector of length na1 with values 1 "
vect12::usage 	= "matrix of dimensions 2xna1 with values 1"


aggreg::usage 	= "aggreg[v,c] aggregates a vector v, according to ageclass distribution c"
aggregc::usage 	= "aggreg[v,c1,c2] redistributes a vector aggregated over ageclass distribution c1 to ageclass distribution c2"
meanaggreg::usage = "meanaggreg[v] calculates mean values per 5 year ageclass from age year data"
enlarge::usage	= "enlarge[v] expands 5 year ageclass data to values specified by age year"
enlargec::usage	= "enlargec[v,c] expands data specified by ageclass distrubtion c to values specified by age year"

subsetn::usage	= "subsetn[v1,v2] calculates age 85+ numbers at the end of year as sum of 84 (v1) and 85+ (v2) numbers at start"
subsetp::usage	= "subsetp[v1,v2,wgt] calculates age 85+ rates at the end of year as weighted sum of 84 (v1) and 85+ (v2) rates at start"


fsmooth::usage 	= "fsmooth[x] interpolates a 18 ageclass vector x by (1) expanding the
		   vector to a 86 years vector by copying each element 5 times, and (2)   
		   smoothing this vector using a penalty matrix."

fsmooth1::usage = "fsmooth1[x] interpolates a 18 ageclass vector x by (1) expanding the 
		   vector to a 86 years vector by copying each element 5 times, (2) applying   
		   a log-transformation to it, (3) smoothing this vector using a penalty matrix,
		   and (4) de-transforming it."

schrijf::usage	= "schrijf[naam, dat] writes array dat to file with name naam"


Begin["`Private`"]


Print["CZMFunctions package is evaluated"];


(* --------------------------------------------------
		INPUT FILE READING ROUTINES
----------------------------------------------------*)

skipcomment[dat_] := Block[{}, 
	Skip[dat, Record, RecordSeparators -> {{"(*"}, {"*)"}}];
	Read[dat, Word]];


(* --------------------------------------------------
		CUT-OFF AND ROUND-OFF ROUTINES
----------------------------------------------------*)

Min1[x_] 	:= Min[x, 1]; 
Max1[x_] 	:= Max[x, 1]; 
Max0[x_] 	:= Max[x, 0]; 
Min0[x_] 	:= Min[x, 0]; 
Maxc[x_, c_] 	:= Max[x, c]; 
Minc[x_, c_] 	:= Min[x, c]; 

roundoff[x_, m_] := 1. Round[m x] / m;

SetAttributes[{Max0, Min0, Max1, Min1, Maxc, Minc, roundoff}, Listable]; 


(* --------------------------------------------------
		UNIT VECTORS AND MATRICES
----------------------------------------------------*)

vect1 		= Table[1, {nac[[1]]}]; 
vect0 		= Table[0, {nac[[1]]}];
vect11 		= Table[1, {na1}]; 
vect12 		= {vect11, vect11}; 
vect00 		= 0 vect11;


(* --------------------------------------------------
		AGECLASS CONVERSION ROUTINES
----------------------------------------------------*)

aggreg[v_, c_] 		:= Table[Plus@@v[[initageclass[[c, ai]] + Range[lengthageclass[[c, ai]]]]], {ai, nac[[c]]}]; 

aggregc[v_, c1_, c2_] 	:= Table[Plus@@v[[Range[
				ageyearclass[[c1, initageclass[[c2, ai]] + 1]],
				ageyearclass[[c1,initageclass[[c2, ai + 1]]]]
				]]],
				{ai, nac[[c2]]}]; 

meanaggreg[v_]		:= aggreg[v, 1] / lengthageclass[[1, Range[nac[[1]]]]]; 

enlarge[v_] 		:= Flatten[Table[v[[ai]], {ai, nac[[1]]}, {lengthageclass[[1, ai]]}]];

enlargec[v_, c_] 	:= Flatten[Table[v[[a]], {a, nac[[c]]}, {lengthageclass[[c, a]]}]];

subsetn[v1_, v2_] 	:= Block[{}, v = Flatten[{v1, v2}]; Flatten[{v[[Range[na]]], Plus@@Take[v, -2]}]]; 

subsetp[v1_, v2_, wgt_] := Block[{}, v = Flatten[{v1, v2}]; Flatten[{v[[Range[na]]], Plus@@(wgt Take[v, -2])}]];


(* --------------------------------------------------
		SMOOTHING ROUTINES
----------------------------------------------------*)

(* Calculation of smoothing matrix. This matrix is based on a penalty matrix:
	n				= 90;
	pen				= Table[Switch[Abs[i - j], 0, 6, 1, -4, 2, 1, _, 0], {i, n}, {j, n}];
	pen[[{1, 2}, {1, 2}]] 		= {{1, -2}, {-2, 5}};
	pen[[{n - 1, n}, {n - 1,n}]] 	= {{5, -2}, {-2, 1}};
	wgt				= Table[0, {n}, {n}];
	Do[wgt[[5 i + 3, 5 i + 3]] 	= 1, {i, 0, nac[[1]] - 1}];
	makehat[c_] 			:= Inverse[wgt + c pen].wgt;
	hat				= makehat[5];
	hat1				= makehat[100];
	Put[hat, Global`inputpath <> "hatmatrix.m"]; *)

hat 	= ReadList[Global`inputpath <> "hatmatrix.m"][[1]];

fsmooth[x_] := Block[{}, 
        x1 = Flatten[Table[x[[i]], {i, nac[[1]]}, {5}]]; 
        Flatten[{Max0[(hat.x1)[[Range[na]]]], x[[nac[[1]]]]}]
	]; 

fsmooth1[x_] := Block[{}, 
	x1 = Flatten[Table[Log[x[[i]] + eps], {i, nac[[1]]}, {5}]]; 
        Flatten[{Exp[(hat.x1)[[Range[na]]]], x[[nac[[1]]]]}]
	]; 


(* --------------------------------------------------
		WRITING TO ASCII FILE
----------------------------------------------------*)

schrijf[naam_, dat_] := Block[{},

	fun1[x_] 	:= {Table[{WriteString[naam, x[[i]]], WriteString[naam, ";"]}, {i, Length[x]}]; WriteString[naam, "\n"]};
	fun2[x_, dep_] 	:= If[(dep == 2), fun1[x], Table[fun2[x[[i]], dep - 1], {i, Length[x]}]];
	OpenWrite[naam];
	fun2[dat, Depth[dat]];
	Close[naam]

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
