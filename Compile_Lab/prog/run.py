outfile = 'stdout.txt'

with open(outfile, 'w') as f:
    for i in range(100):
        s="%02d"%i
        with open("test"+s+".java") as file:
            te=0
            for lines in file:
                if not lines.find('// TE')==-1:
                    te=1
            if(te==0):
                print(i, 'Program type checked successfully', file=f)
            else:
                print(i, 'Type error', file=f)