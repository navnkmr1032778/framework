SWAFTEE 
-------

A Web Driver based Automation Framework using selenium. 

Local setup steps:
------------------

1. Make sure you have Java version 7 or above installed in your machine.
2. Download eclipse [https://eclipse.org/downloads/].
3. Setup TestNG in eclipse [http://goo.gl/GleaWs].
4. Clone the swaftee repo from here[https://stash.solutionstarit.com/projects/QA/repos/swaftee/browse].
5. Open the eclipse in either new workspace or existing one.
6. Import the file project by navigating File->import
7. Once the project is imported, verify the java compiler path is set to jdk by following steps below.
   i) Project -> properties
   ii) Click on Java compiler
   iii) Click on Installed JRE's link
   iv) Verify that the path set is for jdk 1.7 or above, otherwise change the path and save.
8. To download the dependencies
   i) Right click on the project module and select Run As -> Maven Install
   ii) Once it completed, right click on the project module and select Maven -> Update Project


Importing the swaftee as a Jar for Automation development:
----------------------------------------------------------

1. Make sure your project falls under maven org.
2. Add the following Maven dependency information in your pom.xml file 
   [TODO : Update the link]
3. Folder structure:
   i) Create a folder resource parallel to src folder
   ii) Create the following folders
      a. resources -> testdata -- to hold the input files
      b. resources -> drivers -- to hold browser executable files
4. Extend AppTest class for all your test classes and AppPage class for all your page factory classes.