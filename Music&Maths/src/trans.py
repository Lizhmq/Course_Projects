#!/usr/bin/env python3
import scipy.io.wavfile as wav
import wave
import os
from python_speech_features import mfcc
import numpy as np

readpath = r"../cut/"
writepath = r"../mfcc/"
files = os.listdir(readpath)
infiles = [readpath + f for f in files if f.endswith('.wav')]
outfiles = [writepath + f[-30:-4] + ".txt" for f in files if f.endswith('.wav')]

for i in range(len(infiles)):

	print("Calculating " + outfiles[i] + "...")
	
	fs = wave.open(infiles[i])
	rate = fs.getframerate()
	frames = fs.getnframes()
	audio = fs.readframes(frames)

	# print(fs.getparams())
	tmp = np.fromstring(audio, dtype=np.int16)
	tmp.shape = -1, 2
	inp = tmp[:,0]
	feature_mfcc = mfcc(inp, samplerate=rate, winstep=0.1, nfft=2205)
	# feature_mfcc = mfcc(inp, nfft=8880)
	# f = open(outfiles[i], "w+")
	# f.write(str(feature_mfcc))
	np.savetxt(outfiles[i], feature_mfcc)
