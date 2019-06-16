import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim
import numpy as np

class GangPlayer(nn.Module):
    def __init__(self):
        super(GangPlayer, self).__init__()
        self.fc1 = nn.Linear(352, 1024)
        self.fc2 = nn.Linear(1024, 1024)
        self.fc3 = nn.Linear(1024, 1024)
        self.fc4 = nn.Linear(1024, 1024)
        self.fc5 = nn.Linear(1024, 1024)
        self.fc6 = nn.Linear(1024, 100)
        self.fc7 = nn.Linear(100, 2)
        
        
        self.bn1 = nn.BatchNorm1d(1024)
        self.bn2 = nn.BatchNorm1d(1024)
        self.bn3 = nn.BatchNorm1d(1024)
        self.bn4 = nn.BatchNorm1d(1024)
        self.bn5 = nn.BatchNorm1d(1024)
        self.bn6 = nn.BatchNorm1d(100)
        
        
        self.dp1 = nn.Dropout(0.3)
        self.dp2 = nn.Dropout(0.3)
        self.dp3 = nn.Dropout(0.3)
        self.dp4 = nn.Dropout(0.3)
        self.dp5 = nn.Dropout(0.3)
        self.dp6 = nn.Dropout(0.3)
        
        
    def forward(self, x):
        x = self.dp1(F.relu(self.bn1(self.fc1(x))))
        x = self.dp2(F.relu(self.bn2(self.fc2(x))))
        x = self.dp3(F.relu(self.bn3(self.fc3(x))))
        x = self.dp4(F.relu(self.bn4(self.fc4(x))))
        x = self.dp5(F.relu(self.bn5(self.fc5(x))))
        x = self.dp6(F.relu(self.bn6(self.fc6(x))))
        return self.fc7(x)

class TransGangPlayer(nn.Module):
    def __init__(self):
        super(TransGangPlayer, self).__init__()
        self.fc1 = nn.Linear(318, 1024)
        self.fc2 = nn.Linear(1024, 1024)
        self.fc3 = nn.Linear(1024, 1024)
        self.fc4 = nn.Linear(1024, 1024)
        self.bn1 = nn.BatchNorm1d(1024)
        self.bn2 = nn.BatchNorm1d(1024)
        self.bn3 = nn.BatchNorm1d(1024)
        self.bn4 = nn.BatchNorm1d(1024)
        self.dp1 = nn.Dropout(0.3)
        self.dp2 = nn.Dropout(0.3)
        self.dp3 = nn.Dropout(0.3)
        self.dp4 = nn.Dropout(0.3)
        for p in self.parameters():
            p.requires_grad = False
        
        
        self.fc15 = nn.Linear(1024, 1024)
        self.fc16 = nn.Linear(1024, 1024)
        self.fc17 = nn.Linear(1024 + 34, 100)
        self.fc18 = nn.Linear(100, 2)
        self.bn15 = nn.BatchNorm1d(1024)
        self.bn16 = nn.BatchNorm1d(1024)
        self.bn17 = nn.BatchNorm1d(100)
        self.dp15 = nn.Dropout(0.3)
        self.dp16 = nn.Dropout(0.3)
        self.dp17 = nn.Dropout(0.3)
        
    def forward(self, x):
        y = self.dp1(F.relu(self.bn1(self.fc1(x[:,:318]))))
        y = self.dp2(F.relu(self.bn2(self.fc2(y))))
        y = self.dp3(F.relu(self.bn3(self.fc3(y))))
        y = self.dp4(F.relu(self.bn4(self.fc4(y))))
        y = self.dp15(F.relu(self.bn15(self.fc15(y))))
        y = self.dp16(F.relu(self.bn16(self.fc16(y))))
        
        x = torch.cat((y, x[:,318:]), dim=1)
        x = self.dp17(F.relu(self.bn17(self.fc17(x))))
        return self.fc18(x)




class ChiPlayer(nn.Module):
    def __init__(self):
        super(ChiPlayer, self).__init__()
        self.fc1 = nn.Linear(352, 1024)
        self.fc2 = nn.Linear(1024, 1024)
        self.fc3 = nn.Linear(1024, 1024)
        self.fc4 = nn.Linear(1024, 1024)
        self.fc5 = nn.Linear(1024, 1024)
        self.fc6 = nn.Linear(1024, 100)
        self.fc7 = nn.Linear(100, 4)
        
        
        self.bn1 = nn.BatchNorm1d(1024)
        self.bn2 = nn.BatchNorm1d(1024)
        self.bn3 = nn.BatchNorm1d(1024)
        self.bn4 = nn.BatchNorm1d(1024)
        self.bn5 = nn.BatchNorm1d(1024)
        self.bn6 = nn.BatchNorm1d(100)
        
        
        self.dp1 = nn.Dropout(0.3)
        self.dp2 = nn.Dropout(0.3)
        self.dp3 = nn.Dropout(0.3)
        self.dp4 = nn.Dropout(0.3)
        self.dp5 = nn.Dropout(0.3)
        self.dp6 = nn.Dropout(0.3)
        
        
    def forward(self, x):
        x = self.dp1(F.relu(self.bn1(self.fc1(x))))
        x = self.dp2(F.relu(self.bn2(self.fc2(x))))
        x = self.dp3(F.relu(self.bn3(self.fc3(x))))
        x = self.dp4(F.relu(self.bn4(self.fc4(x))))
        x = self.dp5(F.relu(self.bn5(self.fc5(x))))
        x = self.dp6(F.relu(self.bn6(self.fc6(x))))
        return self.fc7(x) 

class PlayPlayer(nn.Module):
    def __init__(self):
        super(PlayPlayer, self).__init__()
        self.fc1 = nn.Linear(318, 1024)
        self.fc2 = nn.Linear(1024, 1024)
        self.fc3 = nn.Linear(1024, 1024)
        self.fc4 = nn.Linear(1024, 1024)
        self.fc5 = nn.Linear(1024, 1024)
        self.fc6 = nn.Linear(1024, 100)
        self.fc7 = nn.Linear(100, 34)
        
        
        self.bn1 = nn.BatchNorm1d(1024)
        self.bn2 = nn.BatchNorm1d(1024)
        self.bn3 = nn.BatchNorm1d(1024)
        self.bn4 = nn.BatchNorm1d(1024)
        self.bn5 = nn.BatchNorm1d(1024)
        self.bn6 = nn.BatchNorm1d(100)
        
        
        self.dp1 = nn.Dropout(0.3)
        self.dp2 = nn.Dropout(0.3)
        self.dp3 = nn.Dropout(0.3)
        self.dp4 = nn.Dropout(0.3)
        self.dp5 = nn.Dropout(0.3)
        self.dp6 = nn.Dropout(0.3)
        
        
    def forward(self, x):
        x = self.dp1(F.relu(self.bn1(self.fc1(x))))
        x = self.dp2(F.relu(self.bn2(self.fc2(x))))
        x = self.dp3(F.relu(self.bn3(self.fc3(x))))
        x = self.dp4(F.relu(self.bn4(self.fc4(x))))
        x = self.dp5(F.relu(self.bn5(self.fc5(x))))
        x = self.dp6(F.relu(self.bn6(self.fc6(x))))
        return self.fc7(x)
    
class PengPlayer(nn.Module):
    def __init__(self):
        super(PengPlayer, self).__init__()
        self.fc1 = nn.Linear(352, 1024)
        self.fc2 = nn.Linear(1024, 1024)
        self.fc3 = nn.Linear(1024, 1024)
        self.fc4 = nn.Linear(1024, 1024)
        self.fc5 = nn.Linear(1024, 1024)
        self.fc6 = nn.Linear(1024, 100)
        self.fc7 = nn.Linear(100, 2)
        
        
        self.bn1 = nn.BatchNorm1d(1024)
        self.bn2 = nn.BatchNorm1d(1024)
        self.bn3 = nn.BatchNorm1d(1024)
        self.bn4 = nn.BatchNorm1d(1024)
        self.bn5 = nn.BatchNorm1d(1024)
        self.bn6 = nn.BatchNorm1d(100)
        
        
        self.dp1 = nn.Dropout(0.3)
        self.dp2 = nn.Dropout(0.3)
        self.dp3 = nn.Dropout(0.3)
        self.dp4 = nn.Dropout(0.3)
        self.dp5 = nn.Dropout(0.3)
        self.dp6 = nn.Dropout(0.3)
        
        
    def forward(self, x):
        x = self.dp1(F.relu(self.bn1(self.fc1(x))))
        x = self.dp2(F.relu(self.bn2(self.fc2(x))))
        x = self.dp3(F.relu(self.bn3(self.fc3(x))))
        x = self.dp4(F.relu(self.bn4(self.fc4(x))))
        x = self.dp5(F.relu(self.bn5(self.fc5(x))))
        x = self.dp6(F.relu(self.bn6(self.fc6(x))))
        return self.fc7(x)