import json
import torch

def process(dirname, save_each):

    keys = ('myplayedcard', 'myshowcard', 'next1playedcard', 'next1showcard', 'next2playedcard', 'next2showcard', 
           'next3playedcard', 'next3showcard', 'mycnum', 'mygnum', 'mypnum', 'next1cnum', 'next1gnum', 'next1pnum',
            'next2cnum', 'next2gnum', 'next2pnum', 'next3cnum', 'next3gnum', 'next3pnum', 'myhandcard')

    with open('../data/' + dirname + '.txt') as lines:
        datas = []
        labels = []
        l = 0
        FileId = 0
        for line in lines:
            data = json.loads(line)
            x = []
            for i in keys:
                if isinstance(data[i], list):
                    x += data[i]
                else:
                    x.append(data[i])
            if dirname != 'playlabel':
                tmp = [0] * 34
                tmp[data['lastcard']] = 1
                x += tmp
            if dirname == 'chilabel':
                label = None
                if data['label'] == 0:
                    label = 0
                else:
                    chilabel = data['labelchi']
                    label = chilabel + 1
            else:
                label = data['label']
            datas.append(x)
            labels.append(label)
            if (l + 1) % save_each == 0:
                torch.save(datas, '../data/' + dirname + '/data' + str(FileId) + '.pt')
                datas = []
                FileId += 1
            l += 1
        torch.save(labels, '../data/' + dirname + '/labels.pt')

#     # num = 88
#     dirname = 'chilabel'
#     files = ['../data/' + dirname + '/data' + str(i) + '.pt' for i in range(70)]
#     filesize = 50000

#     labels = torch.load('../data/chilabel/labels.pt')
#     torch.save(labels[filesize * len(files):], '../data/chilabel/testlabels.pt')

def main():
#     print('processing chi...')
#     process('chilabel', 50000)
#     print('processing gang...')
#     process('mingganglabel', 50000)
#     print('processing peng...')
#     process('penglabel', 50000)
    print('processing play...')
    process('playlabel', 500000)

# def main():
# #     process('chilabel', 50000)     # 19, 4
#     process('mingganglabel', 50000) # 1, 1
#     process('penglabel', 50000)    # 10, 3
# #     process('playlabel', 500000)    # 10, 2
    
main()