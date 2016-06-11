#Performance visualizer.


###README v0.15 / 11 JUNE 2016

- [Introduction](https://github.com/ThibaultUrien/SemesterProject/blob/master/README.md#Introduction)
- [How to use](https://github.com/ThibaultUrien/SemesterProject/blob/master/README.md#)
	- [Building and starting it](#)
	- [Once the application is started](#)
- [Requirement](#)
- [Configuration](#)
	- [repoUrl](#)
	- [mainFileUrl](#)
	- [dataUrlDomain](#)
	- [mainFileIsIndex](#)
	- [indexFileLocalName](#)
	- [fileNameRegex](#)
	- [repoDir](#)
	- [testSeparator](#)
	- [paramSeparator](#)
	- [prameters](#)
	- [groupBegin/ groupEnd](#)
	- [completeResultSeparator](#)
	- [vertexesFile, edgesFile, branchesFile and testsFile:](#)
- [The code:](#)
	- [About he JS part](#)
		- [The class that extends Drawer](#)
		- [The class used as parameter of drawers](#)
		- [The classes used to import data from javascript](#)
		- [The others classes](#)
		- [The files needed by the JS part](#)
	- [About JVM part](#)
		- [The interface with git](#)
		- [The interface with the test server](#)
- [Licence](#)
- [This project used](#)


##Introduction

This application was done as a semester project in the LAMP at the EPFL.
The purpose of this application is to allow the user visualize intuitively per commit benchmark results.
This project was originally developed to follow the Dotty's git repository, yet it's possible to hook this application with other git projects.

##How to use

###Building and starting it

Launch sbtrun.bat, sbtrun.sh or open index.htm in a web browser to start the application. Both sbtrun build the application if needed then start it. Started this way, the application will refresh data about the repository and the benchmark before displaying anything. If you start the application with index.htm, the application is going to use datas it have locally, which mean nothing at all if it's the first time you use this application. In this case you have to start it by using one of the sbtrun script. 

###Once the application is started

You can drag the git network or the performance bar chart to navigate. You can also use the arrow key to move left and right. Using shift with an arrow key move you to the beginning or to the end of the graph.

If you put your mouse over a commit vertex in the network or over a bar in the bar chart, you will see more information about it. If you click on a commit a new tab will be open on the page about this commit on github.

Some tests can be invisible or barely visible. The reason is the scale of the bar chart is automatically determined by the highest displayed test. Use the component on the left to only display the test that interest you.

On the left you should see the list of the test done on this repository. Drag with the mouse to browse it. The square before the test name have the same color as the bar representing the test on the chart. Those square are also check box you can click to show or hide some tests. Pressing shift while clicking a check box commute the clicked component then switch every other check-boxes to the opposite state (ie. if the check box you clicked was checked, it turn unchecked and all the other check-boxes turn checked independently of their state before you click).
At the bottom of the list you can see a text field named “Filter”. If a string is specified, only the test with a name that contain the specified string will be displayed in this list. This filter does not affect what is displayed in the chart on the right. Note that the shift click will only affect the displayed check box, the check-box hidden by a filter will remain unchanged. 


Click on the time line below the commit network to input a date that you would like to see.



##Requirement

Before the installation be sure that you have java's JDK installed and that your path variable contain it's location. 
You will also need sbt installed on your computer. Get it at http://www.scala-sbt.org/.

When you have the jdk and sbt installed, start either sbtrun.bat or sbtrun.sh. This script ask sbt to run the program. As provided on git the program is not yet built so sbt take care of doing it. When sbt is done, the application start and you can use it. For further utilization, it's recommended to keep using sbtrun to start the application to be sure that what is displayed is up to date. The second time sbt will see that everything is already built and wont do it again.


##Configuration

The application work well the way it is confirurated, you don't need to read this part if you just want to see what it does.

There is a file named setting.js that allow you to change number of thing about this application. Especially, you can change the source of the data used by the application and the way the program read the data files you provide it. 

There are four Javascript structure in this file: SharedSetting, NetworkSetting, BarchartSetting and LegendSetting. The three last are mostly used to control visual properties of the application so there isn't much to tell about them.
You are free to add as much parameter in those four structures but don't remove any of those that are already here as it will prevent the application to work.

If your are interested to integrate this application in some web page, I bring your attention on the attributes named
canvasId that allow you to change the canvas that will contain each component. 

SharedSetting is both used by the part that update data and by the part that display them. The part that update data run on the JVM, it's not a javaScript programe. The extraction of the setting is a bit rougher on the JVM side and can get confused by certain things. Id est it does not make the difference between regular code and comments. Moreover the JVM part will just ignore what it's unable to understand. 
If you don't use either the JVM or the JS part of the application, you don't need the full SharedSetting as both part only react to the field they need.
The JS part use only "defaultTimeScale", "title", "repoUrl", the JVM use all the fields that the JS part doesn't use plus “repoUrl”.

You can find information about certain field of SharedSetting bellow. To avoid some unpleasant surprise, you will also find on string parameter if they are used as regexp or not. 

####repoUrl
The JS and JVM part use it.
It's the url of the git repository. 
The JVM part can use any valid git repository but the JS send the user to repoUrl/commitHash when a commit node is clicked. The second behavior will product odd results if your git sever doesn't provide commit pages as github does.

####mainFileUrl
The JVM part use it.
This file either contain all the result of your benchmarking or is an index of the files that contain those results.

####dataUrlDomain
The JVM part part use it.
If the main file is used as an index and the addresses of the indexed file are given with a relative path, those files will be requested relatively to this domain. Leave it empty if the file path are absolute.

####mainFileIsIndex
The JVM part use it.
Put it to true if the main file is an index of the existing test files. Put it to false if the main file contain the data about your tests.

####indexFileLocalName
The JVM part use it.
The main file is locally copied as ./perf/ContentOfIndexFileLocalName

####fileNameRegex
The JVM part use it.
It *is a regex*.
If your main file is an index, for every matches of this regexp, the first capturing group is assumed to contain the url pointing to data about some of your tests. If your main file is not an index this regexp is not used. You can change this regexp to be compatible with other index file formats. This pattern is applied on the whole index file as a single string. The application will send an http request to everything that is captured by this regexp. If it's not a valid url it will be ignored and a message containing the captured string will be generated.

####repoDir
The JVM part use it.
This application need a local copy of the repository it work with. The repository will be cloned in the directory repoDir. If repoDir already contain at .git file, the application will attempt to use it as a local clone of the repository at dataUrlDomain. The JVM part will call pull on this repository.

####testSeparator:
The JVM part use it.
It *is a regexp*.
This regexp is used to separate distinct tests inside a test file. The file will be split on this regexp. That's why regexp must not match for a part of the string describing you test. Those part would be lost after the splitting. Note that if the split result in some strings that are not test results (like if your file have some header), those string will be verbosely ignored. 

####paramSeparator
The JVM part use it.
It *is a regexp*.
This regexp is used to separate distinct parameter inside a single. The string will be split on this regexp, which mean that regexp must not match for a part of the string describing you test as those part would be lost after the splitting.

####prameters
The JVM part use it.
It is *not a regexp*.
Once the string representing a single test is cut, the meaning of each segment will be given by the keyword at the same position in parameters. The string parameter must contain: date, param-test, value, cilo, cihi, units. Distinct keyword in this string can be seprarated with a space of a tabulation. "param-test" is the displayed name of the test, value the displayed value,  cilo and cihi are the bound of the confidence interval and complete is the list all the result the iteration this test. You are free to add more parameters. Additional parameter will appear in the dialogue that pop when you put your mouse over a bar. 
Name of each parameter must be unique. The application will skip the full test if unable to find mandatory parameter but will ignore missing values for user made parameters. 
"ignore" is a special parameter name, all the segments of the test string named ignore will be dumped at parsing.
"hash" is also a special parameter name. If a test have a defined hash parameter, the application will use it to match the test with the git commit that have the same hash. If no hash parameter is provided, as it the case for the test server I have at the date of 11/06/16, the test will be matched with the nearest anterior git commit.

####groupBegin/ groupEnd
The JVM part use them.
They *are not regexps*. They are readen as single characters. If they are longer than one character, only the first character is readen. They can be empty strings. The testSeparators between one groupBegin and one groupEnd will be ignored for splitting a test attributes. Note that it's possible to escape groupBegin and groupEnd with a \ before.  The parameter  complete is supposed to be a group as it have often more than one times.

####completeResultSeparator
The JVM part use them.
It *is a regexp*. It's used to split the times contained in the parameter complete.


####vertexesFile, edgesFile, branchesFile and testsFile
The JVM part use it.
The information that will be displayed latter by the javaScript application are copied javaSrcipt array contained in these file. Note that the JS part doesn't read this fields. These files must be imported by the html file calling the JS part.





##The code

This application is divided in two independent parts: the JVM part and the JS part.
The role JVM aggregate the data from git and from a benchmarking server and put those data in three javaScript file.




###About he JS part

In build.sbt this part is referenced as perfNetJS.

There are three noticeable class categories : 
The classes that draw something, all found package ch.epfl.perfNetwork.drawers and all extending Drawer. The classes that are drawn all found package ch.epfl.perfNetwork.drawn make the second categories. And the last group is the class that are used to import data contained in javaScript files.
There is also some class that have a specific role and don't fall in any of those three categories.


####The class that extends Drawer
If you exclude the field inherited from Drawer that contains mutable objects, the subclass of drawer are immutable and all their fields are directly set by the constructor. The purpose of those objects is to draw something in on canvas. The name of the target canvas stored in the value canvasName. The parameters of the drawers are only the settings provided in the file setting.js, which mean a drawer doesn't wrap anything more complex than a string. 
Those fields does not give any information about what to draw but only about how to draw them (color, size, font, offset …).

All the drawer have a method called draw. The firsts parameters of the method draw are the thing that the drawer have to display. The last parameter is a view object, used by the drawer to convert the absolute coordinate of the object they have to represent in screen coordinate. This allow to easily synchronize what display multiple controler by calling draw on all of them with the same view object. 
Note that even if the View contain a scale, in the current implementation, all the drawer assume the coordinate they use already at the right scale.
Except for the view, a drawer can mutate object given in parameter. As example the network drawer maintain in the Network object a list of the vertex that are currently visible. 
The drawer classes could have been singletons as in this implementation only one instance of each is created. Yet there is absolutely not problem in created multiple instance of any drawer.


####The class used as parameter of drawers

All these class are in the package ch.epfl.perfNetwork.drawers. In this same package you will find class that are not directly drawn but are component of drawn objects. The classes of this package are mostly mutable. Those class aren't mean to do many things. Their goal is to product object that will be used to keep track of the state of the application. In addition with field definitions you will find one methode that create on or many instance of the object and is only called once at the start of the application and always from the class Main (except for PerfBarStack.apply), or some filed accessor that enforce consistency.


####The classes used to import data from javascript

All the class contained in ch.epfl.perfNetwork.jsfacade excepting for JSComponents, there is a good number of those. Yet they are only have a convenience purpose. They are simple wrapper and there isn't much to tell about them. Each class corespound to one kind of structure you can see either in vertexes.js, edges.js, benchmarkdata.js or setting.js.

Those classes are only used as a mean of reading datas stored in javascript file and arn't used anymore once the application is fully initialized. 


####The others classes

You will find them directly in ch.epfl.perfNetwork.webapp.  The two most noticeable class are Main and Control. 
Main is the only class that read the settings of the application, the class defined in ch.epfl.perfNetwork.jsfacade are almost only used in Main. Main effectively handle the initialization process of the application.
The last thing main do is calling Control.apply. This method take care of receiving and processing all the input that come from the viewer. All the listener methods that are registered in a dom component are defined as submethods of Control.apply. Control is also the only class that call the draw method of a drawer. Once the initialization is finished, Control and the drawers are the only class that actually do something.
 Algebra is a convenience class that allow you to use any Tuple2 of double as a mathematical vector of dimension 2. To use it fully, import it this way : import ….webapp.Algebra._ . It include a type alias for (Double,Double) named Vec frequently used in the JS part.


####The files needed by the JS part

You can use the JS part alone if you provide it the arrays and structure it need to work.
What the js part need is : 
	
var vertexes = [
{"name" : String, "time" : Number, "y" : Number,  "comment" : String, "author" : String, "authoringDate" : Number}, … ]
 
“time” is the commit date given in second since epoch. “y” is an int used to determine a commit color and to vertically split them on the network : two commit are horizontally aligned iff their y is the same.
“authoringDate” the date given in second since epoch when the commit was created. It's different from the commit date “time” as the commit date can be modified so that commits sorted by commit date are in topological order, which is not always true if you use the real creation date.

var edges = [{"source" : Number, "target" : Numer}] 

"source" and “target” are both index of vertex in the vertex array.


var benchmarkdata = [{"date" : Number, "testName" : String, "hash" : String, "representativeTime" : Number, "confidenceIntervalLo" : Number, "confidenceIntervalHi" : Number, "allMesures" : Array[Number], "misc" : Array[String]}]

"date" is the date in second since epoch when this test was done, if you define the “hash” parameter, “date” don't do anything. “hash” is the hash of the commit on which this test was run. If you put it to “?” the application will try to guess which commit was tested and chose the one with nearest anterior “authoringDate”. "representativeTime" is the time that will be displayed in the barchart. It's in ms. "misc" is an array of string that will displayed as is in the dialogue that pop when you put your mouse on a bar. The JVM part put here the parameters it doesn't know.

You also have to provide the four structures of the setting.js part. 


###About JVM part

In build.sbt this part is referenced as perfNetJVM.

This part couldn't be compiled as javaScrit as it needed to use the jGit library to communicate with git.

The JVM part is much more simpler in its structure
The JM have three noticeable component: the Main class, the part that get data from git and the part that get data from the test server.

The Main part take care of reading the file named setting.js and get from it all the parameter needed for the application to run. Then it ask to the two other parts to download data.


####The interface with git

As jGit already provide lot of functionality, the class here remain simple. The two printer class are used to provide an adequate formatting to put the data in a JavaScript array. The two other class are the one that call the jgit function and extract the data about the repository.

The attribution of the Y index is done on this part. The reason is that is require to explore the commit tree, so it can end up being quite heavy. The horizontal spreading is also heavy but moving it to the JVM part would force the JS part to have a constant time scale as the notion of "two commit being visually too close" depend on it.


####The interface with the test server

This part is a bit more complex as it handle itself the parsing of the data. The class BenchDataDownloader download the main file and make a copy of it locally. The local copy is not needed but help debugging. Yet as it's implemented, BenchDataDownloader read this local copy. So the dowloading will fail if the local main file cannot be created. If the main file is an index,  BenchDataDownloader will get each file referenced and send them all to a single BenchDataReader that will take care of parsing them. 
The parsing of each file is done in parallel, using a foreach on a parallelized collection.
As the file format of the test seemed me subject to change at the moment I implemented this application, the parsing is done through intensive usage of regexp. It allow to rapidly react to a change in the file format, but it also slow down this part of the application. If this problem never occur for my while testing with the data provided by the ScalaMeter server, for projects with a longer history of testing it might be necessary drop the regexp approach, decide which kind of format is used implement a specialized parsing system.  BenchDataReader is the class that make the more usage of regexp.

It's not simple to design regexp that only capture what you want or that the said regexp might be to expensive to use. The policy in this section of code is that if a string make no sens, it's ignored. This way, we avoid to have to use too much complicated regexp to parse files. 
The regexp must produce valid result for a valid input and no result or an unparsable result for an invalid input. To help in the process of debugging regexp or to be sure that the application doesn't drop anything that matter, a message is created for each string skipped. 
Example: in the test file this application parse as of the 11 June 2016, there is a header that repeat the format of the file. As I use \n to separate each test in a file, the header is first assumed to be a  the result of a test. As the application fail to parse it as a test, it's ignored. Without this behavior, I should use a much more complicated regexp that distinguish on test from the others but also recognize the header get rid of it.




###Licence

This project is distributed under the [BSD 3-Clause License](http://opensource.org/licenses/BSD-3-Clause)




###This project used

Eclipse as an ide and Scala as main programing language.
sbt  : www.scala-sbt.org
sbt-scalajs : www.scala-js.org
jsch : www.jcraft.com
slf4j : www.slf4j.org
jGit : www.jgit.eclipse.org
scalajs-dom : www.scala-js.org
scalajs-jquery : www.doeraene.be

and also jUnit for unit tests.
