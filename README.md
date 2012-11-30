NeuroNumber
===========

A digit recognition tool based on [Neuroph](http://neuroph.sourceforge.net/).

Requirements
------------
You have to get:
  * the [neuroph jar](http://sourceforge.net/projects/neuroph/files/neuroph%202.6/neuroph-2.6.zip/download) (we are using neuroph-2.6) from the [download section](http://neuroph.sourceforge.net/download.html)
  * and the [jargs jar](https://github.com/downloads/white-gecko/jargs/jargs-2.0-SNAPSHOT.jar) which I have compiled for you.

Usage
-----

You can run the programm in two modes:

**Lear**
  * '-m' or '--mode' is set to 'learn'
  * '-l' or '--loadPath' specifies the directory with the letters to learn
  * '-s' or '--storePath' specifies where the resulting network file (.nnet) should be saved

**Tell**
  * '-m' or '--mode' is set to 'tell'
  * '-l' or '--loadPath' specifies the path to the network file (.nnet) created in *learn mode*
  * '-f' or '--filePath' specifies the image file which sould be recognized

You can always switch the verbose output on with '-v' or '--verbose'.

TODO
----

  * Expose other parameters as command line arguments e.g.:
    * hidden layers
    * input size
    * learning mode (incl.: rate, max error, momentum)
    * TransferFunctionType
  * Find the best configuration
  * GUI to draw characters
    * for saving in the learning set
    * and for recognition
  * Add some more examples for training
  * Presentation Slides
  * Documentation of the functions and methods
