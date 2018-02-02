Oprop
=====

Oprop verification tool

My tool Oprop is the implementation of object propositions, a formal verification system for object oriented code that I created during my Ph.D. at Carnegie Mellon University.

How to use it
1. First clone the Oprop project from here.
2. Create the jar for this project, called oprop.jar. You will need the third party jars: bcel-5.0.jar, JavaCC.zip and junit.jar, which I have uploaded to this project. I used jre1.8.0_121 to compile this project.
3. The usage of the tool is : java -jar oprop.jar numberOfFiles inputfile1 inputfile2 ... You give the number of files and their names (the files should be in the current directory) and the Oprop tool will generate an output file for each input file. The input files are written in the Oprop language and the output files will be written in the Boogie language. You can see sample input files in this project at Oprop/src/testcases/cager/jexpr/.
4. You need to concatenate the output files together, so that they are in a single file. The content of this single file will be given to the Boogie tool.
5. Copy the contents of that file into the text box at https://rise4fun.com/boogie and press verify! 
