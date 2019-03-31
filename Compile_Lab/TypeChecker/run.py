import os

outfile = 'testout.txt'

with open(outfile, 'a') as f:
    for i in range(100):
        s = "../prog/test%02d.java"%i
        out = os.popen("java Main " + s)
        print(out.read(), file=f)