The bank simulator was created using a typical event-firing, discrete-event
simulator utilizing a priority queue.  Whereas, the grocery store simulator
is built with an approach that adds customers to a "line."  If there is a
conflict of servicing a customer, this is added to their wait time, which is 
later used to determine the averages.  Essentially, everything is configured 
upfront when the seconds are iterated through and the probability of a 
customer creation is factored.

Below are the notes for the refactor:

The primary motivation behind the refactoring is to allow for the Makefile to correctly compile the program.  Right now, the use StoreES.cpp and StoreES.h are conflicting during the make, even though it is correctly compiling in the Clion IDE.  The refactoring intended to remedy this lack of build in the Makefile is to integrate all code from the .cpp file into the .h file.  
	Within the StoreES.cpp code, there are deeply, nested loops.  Extracting some of the code into routines with descriptive function names will improve the readability of the code and isolate the functions, which will make future debugging easier.  A concern of mine in this area is that additional variables may need to be created to be passed into the function or they may look like magic numbers on their own, or complex expressions.  This may chain into its own refactoring to make the variables and/or expressions more understandable because the intent is to make the code more readable and easier to debug.
	Another refactor will be the full use of the global variables.  Currently, two of the three classes are using the global variables in one of the other classes.  The third class, while not using the global variables, has local variables that are identical to the global variables, save their names.  Because the classes are not running concurrently, there is no concern that the variables will be rewritten while the functions are running.  
	To ensure the program still functions when finished, I’ll be taking snapshot of the results of the program before the refactoring is done.  After each change, even if small, I’ll do a functionality test on the program to ensure that the results don’t greatly deviate from what is intended.
