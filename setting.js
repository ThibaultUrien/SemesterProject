var SharedSetting = {
	
	"defaultTimeScale" : 0.00115740,
	"repoUrl" : "https://github.com/lampepfl/dotty.git",
    "dataUrlDomain" : "https://d-d.me/tnc/dotty/report",
    "mainFileUrl" : "https://d-d.me/tnc/dotty/report/js/ScalaMeter/data.js",
    "mainFileIsIndex" : true,
	"indexFileLocalName" : "ScalaMeter.js",
	"fileNameRegex" : '"file"\s*:\s*"([^"]+)"',
	"repoDir" : "repo",
	"testSeparator" : "\n",
    "paramSeparator" : "\s",
	"prameters" : "date param-test value ignore cilo cihi units complete",
    "groupBegin" : '"',
    "completeResultSeparator" : " ",
    "groupEnd" : '"',
    "testsFile" : "benchmarkdata",
	"vertexesFile" : "vertexes",
	"edgesFile" : "edges",
	"showResultWhenDone" : true,
	"createFakeTests" : true

};
var NetworkSetting = {
	"pointRadius" : 4,
	"arrowHeadLength" : 8,
	"spaceForArow" : 12,
	"arrowBaseHalfWidth" : 4,
	"lineWidth" : 2,
	"verticalLineDistance" : 24,
	"minPointSpace" : 16,
	"bubbleMaxWidth" : 200,
	"scaleFontSize" : 12,
	"bubbleFontSize" : 10,
	"bubbleFontName" : "sans-serif",
	"scaleFontName" : "sans-serif",
	"scaleTextStyle" : "black",
	"bubbleTextStyle" : "black",
	"canvasId" : "network",
	"scaleCanvasId" : "timeline",
	"colorSeed" : 15,
	"maxDialogueWidth" : 400,
    "highlightedPointRadius" : 9,
    "linkedMarkerRadius" : 13,
    "linkColor" : "#B0E0E6",
    "scaleLineStyle" : "darkgrey",
    "scaleLineWidth" : 1,
    "scaleLineLenght" : 10

	
};

var BarchartSetting =  {	
	"lineWidth" : 2,
	"unitPerLine" : 3,
	"barSpacing" : 4,
	"barWidth" : 12,
	"bubbleMaxWidth" : 200,
	"scaleFontSize" : 12,
	"bubbleFontSize" : 10,
	"highlightStrokeWidth" :2,
	"marginBottom" : 5,
	"barBoundLightOffset" : -0.2,
	"bubbleFontName" : "sans-serif",
	"scaleFontName" : "sans-serif",
	"scaleTextStyle" : "lightgray",
	"bubbleTextStyle" : "black",
	"canvasId" : "barchart"
		
};
var LegendSetting ={
	"checkBoxSide" : 20,
	"textSize" : 20,
	"tickThickness" : 3,
	"checkBoxLeftOffset" : 4,
	"legendTextLeftOffset" : 28,
	"fontSize" : 12,
	"interline" : 4,
	"fontName" : "sans-serif",
	"textStyle" : "black",
	"canvasId" : "legend",
	"filterTextFieldId" : "filter"
}	
