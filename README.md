﻿NeuroNumber
===========

A digit recognition tool based on [Neuroph](http://neuroph.sourceforge.net/).

You can download the current development version of NeuroNumber at the Downloads section ([NeuroNumber.jar](https://github.com/downloads/white-gecko/NeuroNumber/NeuroNumber.jar)).

Requirements
------------
You have to get:
  * Java 7
  * [neuroph 2.7](http://sourceforge.net/projects/neuroph/files/neuroph-2.7/neuroph-2.7.zip/download) (2.6 has different package names) from the [download section](http://neuroph.sourceforge.net/download.html)
    * neuroph-core-2.7.jar
    * neuroph-imgrec-2.7.jar
  * and [jargs](https://github.com/purcell/jargs) (or [my fork](https://github.com/white-gecko/jargs))
    * [jargs-2.0-SNAPSHOT.jar](https://github.com/downloads/white-gecko/jargs/jargs-2.0-SNAPSHOT.jar)
 which I have compiled for you works

Usage
-----
You can always switch the verbose output on with `-v` or `--verbose` and you can get license information with `--license`.

You can run the programm in two modes:

###Learn
  * `-m` or `--mode` is set to `learn`
  * `-l` or `--loadPath` specifies the directory with the letters to learn
  * `-s` or `--storePath` specifies where the resulting network file (.nnet) should be saved

###Tell
  * `-m` or `--mode` is set to `tell`
  * `-l` or `--loadPath` specifies the path to the network file (.nnet) created in *learn mode*
  * `-f` or `--filePath` specifies the image file which sould be recognized

###Advanced Options
  * `-x` or `--imageWidth` and `-y` or `--imageHeight` Set the dimensions to which the input images should be scalled. width × height = size of input layer
  * `--hiddenLayers` Set the configuration of the hidden layers e.g. `50,30,20` would mean, there are 3 hidden layers: 1st with 50 neurons, 2nd with 30 neurons and 3rd with 20 neurons
  * `-r` or `--learningRule` (*experimental*) Set the learning rule to use for training the network e.g. [`org.neuroph.nnet.learning.MomentumBackpropagation`](http://neuroph.sourceforge.net/javadoc/org/neuroph/nnet/learning/MomentumBackpropagation.html). You might get warnings if the selected learning rule is somehow not compatible with the network or if there are problems instanciating the class.

###Analyze
  * `-m` or `--mode` is set to `al`
  * `-p` or `--param` specifies parameter to be changed - hidden(count of hidden neurons-1 layer), layer(count of layers), lrate(learning rate), size(size of training set pro number)
    note that the -param size overrules the --sz option
  * `--lo` specifies the lower bound for given parameters
  * `--hi` specifies the higher bound for given parameters
  * `--st` specifies the step for increase. For -p layer not compulsory(by default 1)
  * `--nr` specifies number of neurons in each hidden layer, compulsory by option -p layer.
  * `--sz` specifies the set size spezifies how many of the images in the training set are used for this run, ignored when used -param size
  * example: (java -jar neuronumber.jar) -m al -p hidden --lo 10 --hi 30 --st 5 would analyze the accuracy with 10,15,..30 neurons(in 1 hidden layer)
  * example: (java -jar neuronumber.jar) -m al -p layer --lo 1 --hi 3 --nr 10 would analyze the accuracy with 1,2,3 hidden layers(having each 10 neurons)
  * example: (java -jar neuronumber.jar) -m al -p size --lo 3 --hi 10 --st 1 would analyze the accuracy with 3 to 10 images pro number taken into learning



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
