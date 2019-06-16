import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim
import numpy as np

class GangPlayer(nn.Module):
    # the same as PengPlayer
    def __init__(self):
        super(GangPlayer, self).__init__()
        self.fc1 = nn.Linear(352, 100)
        self.fc2 = nn.Linear(100, 100)
        self.fc4 = nn.Linear(100, 2)
        
    def forward(self, x):
        x = F.relu(self.fc1(x))
        x = F.relu(self.fc2(x))
#         x = F.relu(self.fc3(x))
        return self.fc4(x)

class TransGangPlayer(nn.Module):
    def __init__(self):
        super(TransGangPlayer, self).__init__()
        self.fc1 = nn.Linear(318, 1024)
        self.fc2 = nn.Linear(1024, 1024)
        self.fc3 = nn.Linear(1024, 100)
        for p in self.parameters():
            p.requires_grad = False
        
        self.fc10 = nn.Linear(100 + 34, 100)
        self.fc11 = nn.Linear(100, 2)
        
    def forward(self, x):
        y = F.relu(self.fc1(x[:,:318]))
        y = F.relu(self.fc2(y))
        y = F.relu(self.fc3(y))
        x = torch.cat((y, x[:,318:]), dim=1)
        x = F.relu(self.fc10(x))
        return self.fc11(x)

class PengPlayer(nn.Module):
    # the same as GangPlayer
    def __init__(self):
        super(PengPlayer, self).__init__()
        self.fc1 = nn.Linear(352, 100)
        self.fc2 = nn.Linear(100, 100)
        self.fc4 = nn.Linear(100, 2)
        
    def forward(self, x):
        x = F.relu(self.fc1(x))
        x = F.relu(self.fc2(x))
#         x = F.relu(self.fc3(x))
        return self.fc4(x)
    
# class GangPlayer(nn.Module):
#     def __init__(self):
            # convolution neural network
#         super(GangPlayer, self).__init__()
#         # x: (batch * length(352))
# #         self.conv1 = nn.Conv2d(4, 16, (3, 3))
# #         self.conv2 = nn.Conv2d(4, 16, (3, 5))
#         self.conv1 = nn.Conv2d(4, 16, (2, 3))
#         self.conv2 = nn.Conv2d(4, 16, (2, 5))
#         self.fc1 = nn.Linear(352 + 512 + 480, 100)
#         self.fc2 = nn.Linear(100, 100)
#         self.fc3 = nn.Linear(100, 2)
# #         self.sm = nn.Softmax(dim=1)
        
#     def forward(self, x):
# #         feature1 = F.relu(self.conv1(x))
# #         feature1 = feature1.view(-1, 16 * 1 * 7)
# #         feature2 = F.relu(self.conv2(x))
# #         feature2 = feature2.view(-1, 16 * 1 * 5)
# #         feature = torch.cat((feature1, feature2), dim=1)
# #         x = x.view(-1, 4 * 3 * 9)
# #         x = torch.cat((x, feature), dim=1)
# #         x = F.relu(self.fc1(x))
# #         x = F.relu(self.fc2(x))
#         feature1 = x[:,:272].reshape(-1, 4, 2, 34)
#         feature1 = F.relu(self.conv1(feature1))
#         feature1 = feature1.view(-1, 16 * 1 * 32)
#         feature2 = x[:,:272].reshape(-1, 4, 2, 34)
#         feature2 = F.relu(self.conv2(feature2))
#         feature2 = feature2.view(-1, 16 * 1 * 30)
#         x = torch.cat((feature1, feature2, x), dim=1)
#         x = F.relu(self.fc1(x))
#         x = F.relu(self.fc2(x))
#         return self.fc3(x)


class ChiPlayer(nn.Module):
    def __init__(self):
        super(ChiPlayer, self).__init__()
        self.fc1 = nn.Linear(352, 1024)
        self.fc3 = nn.Linear(1024, 100)
        self.fc4 = nn.Linear(100, 4)
        
    def forward(self, x):
        x = F.relu(self.fc1(x))
        x = F.relu(self.fc3(x))
        return self.fc4(x)
    
class BigChiPlayer(nn.Module):
    # the same as PengPlayer
    def __init__(self):
        super(BigChiPlayer, self).__init__()
        self.fc1 = nn.Linear(352, 1024)
        self.fc2 = nn.Linear(1024, 1024)
        self.fc3 = nn.Linear(1024, 100)
        self.fc4 = nn.Linear(100, 4)
        
    def forward(self, x):
        x = F.relu(self.fc1(x))
        x = F.relu(self.fc2(x))
        x = F.relu(self.fc3(x))
        return self.fc4(x)    

class PlayPlayer(nn.Module):
    def __init__(self):
        super(PlayPlayer, self).__init__()
        self.fc1 = nn.Linear(318, 1024)
        self.fc3 = nn.Linear(1024, 100)
        self.fc4 = nn.Linear(100, 34)
        
    def forward(self, x):
        x = F.relu(self.fc1(x))
        x = F.relu(self.fc3(x))
        return self.fc4(x)
    
class BigPlayPlayer(nn.Module):
    def __init__(self):
        super(BigPlayPlayer, self).__init__()
        self.fc1 = nn.Linear(318, 1024)
        self.fc2 = nn.Linear(1024, 1024)
        self.fc3 = nn.Linear(1024, 100)
        self.fc4 = nn.Linear(100, 34)
        
    def forward(self, x):
        x = F.relu(self.fc1(x))
        x = F.relu(self.fc2(x))
        x = F.relu(self.fc3(x))
        return self.fc4(x)

class VeryBigPlayPlayer(nn.Module):
    def __init__(self):
        super(VeryBigPlayPlayer, self).__init__()
        self.fc1 = nn.Linear(318, 4096)
        self.fc2 = nn.Linear(4096, 1024)
        self.fc5 = nn.Linear(1024, 1024)
        self.fc3 = nn.Linear(1024, 100)
        self.fc4 = nn.Linear(100, 34)

    def forward(self, x):
        x = F.relu(self.fc1(x))
        x = F.relu(self.fc2(x))
        x = F.relu(self.fc5(x))
        x = F.relu(self.fc3(x))
        return self.fc4(x)