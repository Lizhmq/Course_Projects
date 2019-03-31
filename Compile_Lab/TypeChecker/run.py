import os

outfile = 'testout.txt'

with open(outfile, 'w') as f:
    for i in range(100):
        s = "prog/test%02d.java"%i
        out = os.popen("java Main " + s)
        print(s, file = f)
        print(out.read(), file=f)
    f.close()