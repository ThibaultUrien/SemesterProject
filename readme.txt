README v0.0 / 09 JUNE 2016


Performance visualizer.


Introduction


This application was done as a semester project in the LAMP at EPFL.

The purpose of this application is to allow the user visualize intuitively per commit benchmark results.

This project was originally developed to follow the Dotty's git repository, yet it's possible to hook this application with other git projects.


Using this application


Launch sbtrun.bat, sbtrun.sh or open index.htm in a web browser to start the application. Both sbtrun ask sbt to build the application then start it. Started this way, the application will refresh data about the repository and the benchmark before starting. If you start the application with index.htm, the application is going to use datas it have locally. That mean if it's the first time you use this application, you have to start it by using one of the sbtrun script. 


Once the application is started:


You can drag the git network or the performance bar chart to navigate. You can also use the arrow key to move left and right. Using shift with an arrow key move you to the beginning or to the end of the graph.


If you put your mouse over a commit vertex in the network or over a bar in the bar chart, you will see more information about it. If you click on a commit a new tab will be open on the page about this commit on github.


Some tests can be invisible or barely visible. The reason is the scale of the bar chart is automatically determined by the highest displayed test. Use the component on the left to only display the test that interest you.


On the left you should see the list of the test done on this repository. The square before the test name have the same color as the bar representing the test on the chart. Those square are also check box you can click to show or hide some tests. Pressing shift while clicking a check box normally commute the clicked component then switch every other check-boxes to the opposite state (ie. if the check box you clicked was checked, it turn unchecked and all the other check-boxes turn checked independently of their state before you click).

At the bottom of the list you can see a text field named “Filter”. If a string is specified, only the test with a name that contain the specified string will be displayed in this list. This filter does not affect what is displayed in the chart on the right. Note that the shift click will only affect the displayed check box, the check-box hidden by a filter will remain unchanged. 




Click on the time line below the commit network to input a date that you would like to see.






If you spot a bug or have question, you can create an issue on git hub : [https://github.com/ThibaultUrien/SemesterProject https://github.com/ThibaultUrien/SemesterProject]


Installation: 


Before the installation be sure that you have java's JDK installed and that your path variable contain it's location. 

You will also need sbt installed on your computer. Get it at [http://www.scala-sbt.org/ http://www.scala-sbt.org/].


When you have the jdk and sbt installed, start either sbtrun.bat or sbtrun.sh. This script ask sbt to run the program. As provided on git the program is not yet built so sbt take care of doing it. When sbt is done, the application start and you can use it. For further utilization, it's recommended to keep using sbtrun to start the application to be sure that what is displayed is up to date. The second time sbt will see that everything is already built and wont do it again.




Configuration:


There is a file named setting.js that allow you to change number of thing about this application. In particular, you can change the source of the data used by the application and the way the program read the data file you provide it. 


There are four Javascript structure in this file: SharedSetting, NetworkSetting, BarchartSetting and LegendSetting. The three last are mostly used to control visual properties of the application so there isn't much to tell about them.

You are free to add as much parameter in those four structures but don't remove any of those that are already here as it will prevent the application to work.


If your are interested to integrate this application in some web page notice the attributes named

''canvasId'' that allow you to change the canvas that will contain each component. 


SharedSetting is both used by the part that update data and by the part that display them. The part that update data run on the JVM, it's not a javaScript programmer. The extraction of the setting is a bit rougher on the JVM side and can get confused by certain things. That mean it does not make the difference between regular code and comments. Moreover the JVM part will just ignore what it's unable to understand. 

If you don't use either the JVM or the JS part of the application, you don't need the full SharedSetting as both part only react to the field they need.

The JS part use only "defaultTimeScale", "title", "repoUrl", the JVM use all the fields that the JS part doesn't use plus “repoUrl”.


You can find information about certain field of SharedSetting bellow: 


defaultTimeScale: 

the JS part use it.

Its given in pixel per second. Note that the commit on the graph cannot get closer than NetworkSetting.minPointSpace whatever this filed contain.


repoUrl : 


The JS and JVM part use it.

It's url of the git repository. 

The JVM part can use any valid git repository. The JS send the user to repoUrl/commitHash when a commit node is clicked.


mainFileUrl:

The JVM part use it.

This file either contain all the result of your benchmarking or is an index of the files that contain those results.


dataUrlDomain:

The JVM part part use it.

If the main file is used as an index and that the addresses of the indexed file are given with a relative path, those file will be requested relatively to this domain. Leave it empty if the file path are absolute.


mainFileIsIndex:

The JVM part use it.

Put it to true if the main file is an index of the existing test files. Put it to false if the main file contain the data about your tests.


indexFileLocalName: 

The JVM part use it.

The main file is locally copied as ./perf/ContentOfIndexFileLocalName


fileNameRegex: 

The JVM part use it.

It '''is''' a regex.

If your main file is an index, for every match of this regexp, the first capturing group supposed to contain the url of a file containing data about some of your tests. If your main file is not an index this regexp is not used. You can change this regexp depending to be compatible with other index file formats. This pattern is applied on the whole index file as a single string.


repoDir: 

The JVM part use it.

This application need a local copy of the repository it work with. The repository will be cloned in the directory repoDir. If repoDir already contain at .git file, the application will attempt to use it as a local clone of the repository at dataUrlDomain. The JVM part will call pull on this repository.


testSeparator:

The JVM part use it.

It '''is '''a regexp.

This regexp is used to separate distinct tests inside a test file. The file will be split on this regexp, which mean that regexp must not match for a part of the string describing you test as those part would be lost after the splitting. Note that if the split result in some strings that are not test results (like if your file have some header), those string will be verbosely ignored. 


paramSeparator:

The JVM part use it.

It '''is '''a regexp.

This regexp is used to separate distinct parameter inside a single. The string will be split on this regexp, which mean that regexp must not match for a part of the string describing you test as those part would be lost after the splitting.


prameters: 

The JVM part use it.

It is '''not '''a regexp.

Once the string representing a single test is cut, the meaning of each segment will be given from the position of the corresponding keyword in parameters. The string parameter must contain: date, param-test, value, cilo, cihi, units and complete at the same position as the corresponding value in the string representing the test. Param-test is the displayed name of the test, value the displayed value, cilo and cihi are the bound of the confidence interval and complete is the list all the result the iteration this test. You are free to add more parameters. Additional parameter will appear in the dialogue that pop when you put your mouse over a bar. 

Name of parameter each must be unique. The application will skip the full test if unable to find mandatory parameter but will skip user made parameter if unable to find them. Multiple parameter can be called ignore and parameters named this way will be skipped.


groupBegin / groupEnd:

The JVM part use them.

They are '''not''' regexp. They are read as single character. If they are longer than one character, only the first character is read. They can be the empty string. For cutting, the testSeparators between one groupBegin and one groupEnd will be ignored. Note that it's possible to escape groupBegin and group end with a \ before. The parameter complete is supposed to be a group.


completeResultSeparator:

The JVM part use them.

It '''is''' a regexp. It''s used to split the times contained in the parameter complete.




vertexesFile, edgesFile, branchesFile and testesFile:

The JVM part use it.

The information that will be displayed latter by the javaScript application are copied javaSrcipt array contained in these file. Note that the JS part doesn't read this fields. These files must be imported by the html file calling the JS part.











