import torch
from torch.utils import data
from Dataset import *
from torch.autograd import Variable
from testPlayer import *



dirname = 'playlabel'

trainfilenum = 10
testfilenum = 2

files = ['data/' + dirname + '/data' + str(i) + '.pt' for i in range(trainfilenum)]
labelfile = 'data/' + dirname  + '/labels.pt'
batchsize =  1024
filesize = 50000
dataset = Dataset(files, labelfile, batchsize, filesize)

testfiles = ['data/' + dirname + '/data' + str(i + trainfilenum) + '.pt' for i in range(testfilenum)]
testlabel = 'data/' + dirname + '/testlabels.pt'
testset = Dataset(testfiles, testlabel, batchsize, filesize)




player = PlayPlayer().cuda()
criterion = nn.CrossEntropyLoss()
optimizer = optim.Adam(filter(lambda p: p.requires_grad, player.parameters()), lr=0.001, betas=(0.9, 0.999))



max_epochs = 100


print_each = 1000
for epoch in range(max_epochs):
    player.training = True
    print('start training epoch ' + str(epoch + 1) + '....')
    running_loss = 0.0
    dataset.reset()
    
    num = 0
    correct = 0
    
    for i, data in enumerate(dataset.getbatch()):
        
        inputs, labels = data
        inputs, labels = torch.tensor(inputs, dtype=torch.uint8).cuda(), torch.tensor(labels).cuda()
        inputs = inputs.float()
        optimizer.zero_grad()
        outputs = player(inputs)
        
        tmp = torch.argmax(outputs, dim=1) == labels
        correct += torch.sum(tmp)
        num += batchsize
        
        loss = criterion(outputs, labels)
        loss.backward()
        optimizer.step()
        
        running_loss += loss.item()
        if (i + 1) % print_each == 0:
            print('[%d, %5d] loss: %.3f' %
                  (epoch + 1, i + 1, running_loss / print_each))
            print('train_acc:  %.2f' % (float(correct) * 100.0 / num))
            correct = 0
            num = 0
            running_loss = 0.0

    torch.save(player.state_dict(), 'testmodel/' + dirname[:-5] + '/model' + str(epoch + 1) + '.pt')
    
    testset.reset()
    testnum = 0
    testcorrect = 0
    with torch.no_grad():
        player.training = False
        for i, data in enumerate(testset.getbatch()):

            inputs, labels = data
            inputs, labels = torch.tensor(inputs, dtype=torch.uint8).cuda(), torch.tensor(labels).cuda()
            inputs = inputs.float()

            optimizer.zero_grad()
            outputs = player(inputs)

            tmp = torch.argmax(outputs, dim=1) == labels
            testcorrect += torch.sum(tmp)
            testnum += batchsize
        print('test_acc:  %.2f' % (float(testcorrect) * 100.0 / testnum))
        print()
        print()
        print()
        
print('Finished Training')
