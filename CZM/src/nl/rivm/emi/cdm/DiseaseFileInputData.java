package nl.rivm.emi.cdm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.CharBuffer;
import java.util.ArrayList;


public class DiseaseFileInputData {

			/**
			 * @param args
			 */
			public static void main(String[] args) {
					if (args.length != 1) {
						System.err.println("Invalid number of arguments: "
								+ args.length);
					} else {
							// YAGNI ArrayList<Integer> resultAR =
							// findDivIndexes(fileContent);
							int startIndex = findContentStart(fileContent);
							int endIndex = findContentEnd(fileContent, startIndex);
							String interestingContent = fileContent.substring(
									startIndex, endIndex);
							interestingContent = processTerug(interestingContent);
							interestingContent = processContent(interestingContent);
							interestingContent = processASPXPopups(interestingContent);
							interestingContent = processJavaScriptPopups(interestingContent);
							interestingContent = processNaarBoven(interestingContent);
							interestingContent = processContentSomeMore(interestingContent);
							System.out.println("Result>>>\n" + interestingContent
									+ "\n<<<");
							String outputFileName = args[0].replace(".html", ".frag");
							File outputFile = new File(outputFileName);
							writeStringIntoFile(outputFile, interestingContent);
							System.out.println("Result>>>\n" + interestingContent
									+ "\n<<<");
						}
					}
					System.out.println("Processing finished.");
			}

//  Skip[dat, Record, RecordSeparators -> {{"(*"}, {"*)"}}];
//  Read[dat, Word];
//  (* INCIDENCE DATA *)
//
//  div 	= N[Read[dat, {Table[Word, {2}], Number}][[2]]];
//  inc0[[dis[[d]]]] = Partition[Read[dat, Table[Number, {ng nac[[1]]}]], {nac[[1]]}] / div;
//
//  (* INITIAL PREVALENCE DATA *)
//
//  div 	= N[Read[dat, {Table[Word, {2}], Number}][[2]]];
//  pdis0[[dis[[d]]]] = Partition[Read[dat, Table[Number, {ng nac[[1]]}]], {nac[[1]]}] / div;
//
//  (* REMISSION DATA *)
//
//  div 	= N[Read[dat, {Table[Word, {2}], Number}][[2]]];
//  currrem = Partition[Read[dat, Table[Number, {ng nac[[1]]}]], {nac[[1]]}] / div;
//  If[(Max[currrem] > 0),
//    currremind	= currremind + 1;
//    rem0		= Join[rem0, {currrem}];
//    remind0[[dis[[d]]]] = currremind,
//    remind0[[dis[[d]]]] = 1];
//
//  (* CASE FATALITY = 1-MONTH MORTALITY) DATA *)
//
//  div 	= N[Read[dat, {Table[Word, {2}], Number}][[2]]];
//  currcasefat = Partition[Read[dat, Table[Number, {ng nac[[1]]}]], {nac[[1]]}] / div;
//  If[(Max[currcasefat] > 0),
//     currcasefatind	= currcasefatind + 1;
//     casefat0	= Join[casefat0, {currcasefat}];
//     casefatind0[[dis[[d]]]] = currcasefatind,
//     casefatind0[[dis[[d]]]] = 1];
//
//  (* EXCESS MORTALITY DATA *)
//
//  div 	= N[Read[dat, {Table[Word, {2}], Number}][[2]]];
//  excessmort0[[dis[[d]]]] = Partition[Read[dat, Table[Number, {ng nac[[1]]}]], {nac[[1]]}] / div;
//
//  (* CBS-REGISTERED CAUSE-SPECIFIC MORTALITY DATA *)
//
//  div 	= N[Read[dat, {Table[Word, {2}], Number}][[2]]];
//  causemort0[[dis[[d]]]] = N[Partition[Read[dat, Table[Number, {ng nac[[1]]}]], {nac[[1]]}]];
//  causemort0[[dis[[d]]]] = causemort0[[dis[[d]]]] / If[(div < 0), npop1, div],
//  {d, Length[dis]}];



private static ArrayList<Integer> findDivIndexes(String input) {
				ArrayList<Integer> resultAR = new ArrayList<Integer>();
				int startIndex = 0;
				int foundIndex = -1;
				do {
					foundIndex = input.indexOf("<div>", startIndex);
					if (foundIndex == -1) {
						foundIndex = input.indexOf("<DIV>", startIndex);
					}
					if (foundIndex != -1) {
						resultAR.add(new Integer(foundIndex));
						startIndex = foundIndex + 1;
					}
				} while (foundIndex != -1);
				return resultAR;
			}

			private static Integer findContentStart(String input) {
				int startIndex = 0;
				int foundIndex = -1;
				foundIndex = input.indexOf("FirstColumnFloatingImage");
				if (foundIndex != -1) {
					int firstTagIndex = input.indexOf("<H1>", foundIndex);
					if(firstTagIndex == -1){
						firstTagIndex = input.indexOf("<span>", foundIndex);
					}
					foundIndex = input.lastIndexOf("<table", firstTagIndex);
					System.out
							.println("Content start found at position: " + foundIndex);
					System.out.println(input.substring(foundIndex, foundIndex + 50));
				}
				return new Integer(foundIndex);
			}

			private static Integer findContentEnd(String input, int startIndex) {
				int tableStartFoundIndex = -1;
				int tableEndFoundIndex = -1;
				// Works for simple pages... foundIndex = input.indexOf("</td>",
				// startIndex);
				// Works for somewhat more compricated pages... foundIndex =
				// input.indexOf("</table>", startIndex);
				tableStartFoundIndex = input.indexOf("<table", startIndex + 1);
				tableEndFoundIndex = input.indexOf("</table>", startIndex + 1);
				// Not done, nested tables.
				while (/* (tableStartFoundIndex < tableEndFoundIndex)&& */(tableStartFoundIndex != -1)
						&& (tableEndFoundIndex != -1)) {
					tableStartFoundIndex = input.indexOf("<table",
							tableEndFoundIndex + 1);
					tableEndFoundIndex = input.indexOf("</table>",
							tableEndFoundIndex + 1);
				}
				int htmlEndIndex = input.indexOf("</html>");
				int HTMLEndIndex = input.indexOf("</HTML>");
				htmlEndIndex = Math.max(htmlEndIndex, HTMLEndIndex);
				int contentEndIndex = -1;
				if ((htmlEndIndex != -1) && (htmlEndIndex < tableEndFoundIndex)) {
					int menuHorOnderStartIndex = input.indexOf("menuHorizontaalOnder");
					if (menuHorOnderStartIndex != -1) {
						contentEndIndex = menuHorOnderStartIndex - 10;
					} else {
						contentEndIndex = htmlEndIndex;
					}

				} else {
					contentEndIndex = tableEndFoundIndex;
				}
				System.out.println("Content end found at position: " + contentEndIndex);
				System.out.println(input.substring(contentEndIndex - 50,
						contentEndIndex));
				return new Integer(contentEndIndex);
			}

			private static String processContent(String input) {
				input = input.replaceAll("<tr.*?>", "");
				input = input.replaceAll("</tr>", "");
				input = input.replaceAll("<td.*?>", "");
				input = input.replaceAll("</td>", "");
				input = input.replaceAll("<table.*?>", "");
				input = input.replaceAll("</table>", "");

				input = input.replaceAll("<STRONG>", "<h3>");
				input = input.replaceAll("</STRONG>", "</h3>");
				input = input.replaceAll("<H2>", "<h3>");
				input = input.replaceAll("</H2>", "</h3>");
				input = input.replaceAll("<H3>", "<h3>");
				input = input.replaceAll("</H3>", "</h3>");
				input = input.replaceAll("<P>", "<p>");
				input = input.replaceAll("</P>", "</p>");
				input = input.replaceAll("<H1>", "<h2>");
				input = input.replaceAll("</H1>", "</h2>");
				input = input.replaceAll("<BR>", "<br/>");
				input = input.replaceAll("<P>", "<p>");
				input = input.replaceAll("</P>", "</p>");
				input = input.replaceAll("<A", "<a");
				input = input.replaceAll("</A>", "</a>");
				input = input.replaceAll(".nieuw venster.", "");
				input = input.replaceAll("target=_blank", "");

				input = input.replaceAll("<p><h3><br/>", "<h3>");
				input = input.replaceAll("</h3><br/><br/>", "</h3>\n<p>\n");
				input = input.replaceAll("<br/></h3>", "</h3>");
				// To span....
				input = input.replaceAll("<span><h3><br/>", "<h3>");
				input = input.replaceAll("<span><h3>", "<h3>");
				input = input.replaceAll("<span><p>", "<p>");
				input = input.replaceAll("<span>", "<h3>");
				input = input.replaceAll("</span>", "</h3>");

				input = input.replaceAll("<UL>", "<ul>");
				input = input.replaceAll("</UL>", "</ul>");
				input = input.replaceAll("<LI>", "<li>");
				input = input.replaceAll("</LI>", "</li>");

				input = input.replaceAll("</form>", "");
				input = input.replaceAll("</body>", "");
				return input;
			}
			private static String processContentSomeMore(String input) {
				input = input.replaceAll("</p></h3>", "</p>");
				input = input.replaceAll("</ul></h3>", "</ul>");
				return input;
			}

			private static String processASPXPopups(String input) {
				int lastPopupEndIndex = 0;
				int currentPopupIndex;
				// Place to build the result.
				StringBuffer scratchPad = new StringBuffer();
				while ((currentPopupIndex = input.indexOf("Popup.aspx",
						lastPopupEndIndex)) != -1) {
					int openingAngleBracketIndex = input.lastIndexOf("<",
							currentPopupIndex);
					int codeIndex = input.indexOf("code=", currentPopupIndex);
					String codeString = input.substring(codeIndex + 5, codeIndex + 9);
					int closingAngleBracketIndex = input
							.indexOf(">", currentPopupIndex);
					int secondOpeningAngleBracketIndex = input.indexOf("<",
							currentPopupIndex);
					int secondClosingAngleBracketIndex = input.indexOf(">",
							closingAngleBracketIndex + 1);
					scratchPad.append(input.substring(lastPopupEndIndex,
							openingAngleBracketIndex));
					scratchPad.append(input.substring(closingAngleBracketIndex + 1,
							secondOpeningAngleBracketIndex));
					scratchPad
							.append("<a class=\"kb_bh_richtooltip\" href=\"/Patientenrechten/Algemeen/Begrip/?begripId=");
					scratchPad.append(codeString);
					scratchPad
							.append("\">\n<img src=\"/cms/images/vraagteken.gif\" alt=\"uitleg over de ");
					scratchPad.append("@@@@@@");
					scratchPad.append("\" />\n</a>\n");
					lastPopupEndIndex = secondClosingAngleBracketIndex + 1;
				}
				scratchPad.append(input.substring(lastPopupEndIndex));
				return scratchPad.toString();
			}

			private static String processJavaScriptPopups(String input) {
				int lastPopupEndIndex = 0;
				int currentPopupIndex;
				// Place to build the result.
				StringBuffer scratchPad = new StringBuffer();
				// <A href="javascript:showPopup('popup_4153', 0);">CIZ</A>
				while ((currentPopupIndex = input.indexOf("javascript:showPopup",
						lastPopupEndIndex)) != -1) {
					int openingAngleBracketIndex = input.lastIndexOf("<",
							currentPopupIndex);
					int codeIndex = input.indexOf("popup_", currentPopupIndex);
					int quoteAfterCodeIndex = input.indexOf("'", codeIndex);
					int closingAngleBracketIndex = input
							.indexOf(">", currentPopupIndex);
					int secondOpeningAngleBracketIndex = input.indexOf("<",
							currentPopupIndex);
					int secondClosingAngleBracketIndex = input.indexOf(">",
							closingAngleBracketIndex + 1);
					// Begrippen-popup
					// if (quoteAfterCodeIndex - codeIndex == 10) {
					String codeString = input.substring(codeIndex + 6, codeIndex + 10);
					scratchPad.append(input.substring(lastPopupEndIndex,
							openingAngleBracketIndex));
					scratchPad.append(input.substring(closingAngleBracketIndex + 1,
							secondOpeningAngleBracketIndex));
					scratchPad
							.append("<a class=\"kb_bh_richtooltip\" href=\"/Patientenrechten/Algemeen/Begrip/?begripId=");
					scratchPad.append(codeString);
					scratchPad
							.append("\">\n<img src=\"/cms/images/vraagteken.gif\" alt=\"uitleg over de ");
					scratchPad.append("@@@@@@");
					scratchPad.append("\" />\n</a>\n");
					lastPopupEndIndex = secondClosingAngleBracketIndex + 1;
					// }
				}
				scratchPad.append(input.substring(lastPopupEndIndex));
				return scratchPad.toString();
			}

			private static String processNaarBoven(String input) {
				input = input
						.replaceAll(
								"<a print=\"false\" class=\"NaarBoven\" href=\"#\">naar boven"
										+ "<img border=\"0\" alt=\"Naar boven\" src=\"/portal/images/naar_boven.gif\" /></a>",
								"<a class=\"gototop\" title=\"Ga terug naar boven\" href=\"#\">naar boven</a>");
				return input;
			}

			private static String processTerug(String input) {
				int pijlInterneLinkIndex = input.indexOf("pijlInterneLink");
				int openingAngleBracketIndex = input.lastIndexOf("<",
						pijlInterneLinkIndex);
				int closingAngleBracketIndex = input.indexOf("]</span>",
						pijlInterneLinkIndex) + 8;
				StringBuffer resultSB = new StringBuffer();
				resultSB.append(input.substring(0, openingAngleBracketIndex));

				resultSB
						.append("\n<fieldset class=\"kb_buttonbar\">\n<input type=\"submit\""
								+ " title=\"ga terug naar de vorige pagina\" value=\"Terug\""
								+ " class=\"kb_fright sbm_big_active kb_bh_rollover\" />\n</fieldset>");
				resultSB.append(input.substring(closingAngleBracketIndex));
				return resultSB.toString();
			}

	}


