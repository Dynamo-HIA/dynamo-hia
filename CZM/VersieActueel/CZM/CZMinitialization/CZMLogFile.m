(* :Title: CZMLogFile *)

(* :Context: CZMInitialization`CZMLogFile *)

(* :Author: Rudolf T. Hoogenveen, Roel G.M. Breuls *)

(* :Summary:
   CZM package which initializes a logfile *)

(* :Copyright: © 2004 by Rudolf Hoogenveen/Roel Breuls *)

(* :Package Version: 3.1 *)

(* :Mathematica Version: 5.1 *)

(* :History: 	1.0 first update new implementation CZM July 2004
		2.0 first release CZM 2005, version March
		3.0 version november 2005 
		3.1 version March 2007 *)

(* :Keywords: postprocessing, logfile *)


BeginPackage["CZMInitialization`CZMLogFile`"]


logfile::usage		= "name of the stream to which log information is written, name = logputput <>date <> time"


Begin["`Private`"]


Print["CZMLogFile package is evaluated"];


Do[If[StringTake[$ContextPath[[i]], 3] == "CZM", Unprotect[Evaluate[$ContextPath[[i]] <> "*"]]],
	{i, 1, Length[$ContextPath]}];

Map[Close, Streams[][[Range[3, Length[Streams[]]]]]];


(* FILE NAME, BASED ON CURRENT DATE AND TIME INFORMATION *)

date = ToString[Date[][[3]]] <> "-" <> ToString[Date[][[2]]] <> "-" <> ToString[Date[][[1]]];

time = ToString[Date[][[4]]] <> "h" <> ToString[Date[][[5]]];

logoutputfilename ="logoutput_" <> date <> "_" <> time <> ".txt";


(* OPEN LOGFILE *)

logfile = OpenWrite[Global`logfilepath <> logoutputfilename]


(* WRITE HEADER OF LOGFILE *)

WriteString[logfile, "------------------------------------------\n", 
	"\n", 
	logoutputfilename, 
	"\n \n", 
	"User: ", $UserName, 
	"\n------------------------------------------\n\n"]


End[]


Protect[Evaluate[Context[]<>"*"]]


EndPackage[]


