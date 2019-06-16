import torch
from torch.utils import data

class Dataset():

    def __init__(self, files, labelfile, batchsize, filesize):
        'Initialization'
        
        self.files = files
        self.fileindex = 0
        self.filesize = filesize
        self.numfiles = len(files)
        
        self.filedata = None
        
        self.labels = torch.load(labelfile)
        
        self.batchindex = 0
        self.batchsize = batchsize
        
    def __len__(self):
        'Denotes the total number of samples'
        return len(self.labels)
    
    def reset(self):
        self.fileindex = 0

    def getbatch(self):
        'Generates a batch of data'
        
        while True:
            if self.fileindex == 0 or self.batchsize * (self.batchindex + 1) >= self.filesize:
                if self.fileindex >= self.numfiles:
                    break
                self.filedata = torch.load(self.files[self.fileindex])
                self.filesize = len(self.filedata)
                self.fileindex += 1
                self.batchindex = 0
                dataindex = 0
                labelindex = (self.fileindex - 1) * self.filesize + dataindex
                self.batchindex = 1
                yield self.filedata[dataindex:dataindex + self.batchsize],\
                        self.labels[labelindex:labelindex + self.batchsize]
            dataindex = self.batchsize * self.batchindex
            labelindex = (self.fileindex - 1) * self.filesize + dataindex
            X = self.filedata[dataindex:dataindex + self.batchsize]
            y = self.labels[labelindex: labelindex + self.batchsize]
            self.batchindex += 1
            yield X, y
