### Music&Maths Project

#### Tasks

* Generate pitches using "Pythagorean Intonation", "Just Intonation" and "The Well-Tempered Clavier"
* Choose some pieces of music and generate them with "Pythagorean Intonation", "Just Intonation" and "The Well-Tempered Clavier"
* Cut them into pieces of 30 seconds
* Train a classifier on temperaments with this dateset

#### Hierachy

* musics/	music generated with "Pythagorean Intonation", "Just Intonation" and "The Well-Tempered Clavier"
* papers/	related papers
* cut/		music cut to pieces of 30s
* src/		src codes
  * cut.py	cut music into pieces
  * trans.py	transform pieces of music using mfcc
  * classify.py	classifier using mfcc of music
* mfcc/	mfcc of music