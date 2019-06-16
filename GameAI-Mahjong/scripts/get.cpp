#include <iostream>
#include <vector>
#include <io.h>
#include <iomanip>
#include <fstream>
#include <cstring>

#define HUAPAI 50

using namespace std;


//导入文件信息的函数
void getFiles(string path, vector<string> &files)
{
    //文件句柄
    long hFile = 0;
    //文件信息
    struct _finddata_t fileinfo;
    string p;
    if ((hFile = _findfirst(p.assign(path).append("\\*").c_str(), &fileinfo)) != -1)
    {
        do
        {
            //如果是目录,迭代之
            //如果不是,加入列表
            if ((fileinfo.attrib & _A_SUBDIR))
            {
                if (strcmp(fileinfo.name, ".") != 0 && strcmp(fileinfo.name, "..") != 0)
                    getFiles(p.assign(path).append("\\").append(fileinfo.name), files);
            }
            else
            {
                files.push_back(p.assign(path).append("\\").append(fileinfo.name));
            }
        } while (_findnext(hFile, &fileinfo) == 0);
        _findclose(hFile);
    }
}

int rd=6;
char * outname ;

void myerror(char * out)
{
    cout<<out<<endl<<outname<<endl<<rd<<endl;
    exit(0);
}


//牌的编码: 0-8 : W1-W9, 9-17 : B1-B9, 18-26 : T1-T9, 27-30:F1-F4, 31-33: J1-J3 
int getcard(char *input, int i)//input[i]=''',a 为字母，b为数字
{
    char a = input[i + 1];
    char b = input[i + 2];
    if (a == 'W')
    {
        return b - '1';
    }
    else if (a == 'B')
    {
        return 9 + b - '1';
    }
    else if (a == 'T')
    {
        return 18 + b - '1';
    }
    else if (a == 'F')
    {
        return 27 + b - '1';
    }
    else if (a == 'J')
    {
        return 31 + b - '1';
    }
    else if (a=='H')
    {
        return HUAPAI;
    }
    else
    {
            myerror( "myerror in getpai" );
    }
    return -1;
}

void changecard(int *card, int cardid, int add)
{
    card[cardid] += add;
}

struct handcard
{
    int id;
    int card[34];
    handcard(char *input)
    {
        memset(card, 0, sizeof(card));
        id = input[0] - '0';
        int l = strlen(input);
        for (int i = 0; i < l; i++)
        {
            if (input[i] == '\'')
            {
                changecard(card, (getcard(input, i)), 1);
                i += 4;
            }
        }
    }
    handcard() {}
};

struct showcard
{
    int id;
    int card[34];
    int pnum, cnum, gnum;
    showcard()
    {
        memset(card, 0, sizeof(card));
        pnum = 0;
        cnum = 0;
        gnum = 0;
    }
};

struct hasplayedcard
{
    int id;
    int card[34];
    hasplayedcard()
    {
        memset(card, 0, sizeof(card));
    }
};

int chinese(char *p, int b)//处理中文动作
{
    if (p[2] == -26 && p[3] == -111) //摸牌
        return 0;
    if (p[2] == -26 && p[3] == -119) //打牌
        return 1;
    if (p[2] == -24 && p[3] == -95 && p[5] == -24 && b==9) //补花牌
        return 2;
    if (p[2] == -24 && p[3] == -95 && b == 18) //补花后摸牌
        return 3;
    if (p[2] == -25 && p[3] == -94) //碰
        return 4;
    if (p[2] == -27 && p[3] == -112) //吃
        return 5;
    if (p[2] == -26 && p[3] == -104) //明杠
        return 6;
    if (p[2] == -26 && p[3] == -102) //暗杠
        return 7;
    if (p[2] == -24 && p[3] == -95 && p[5] == -26) //补杠
        return 8;
    if (p[2] == -26 && p[3] == -99) //杠后摸牌
        return 9;
    if (p[2] == -27 && p[3] == -110) //和牌
        return 10;
    myerror( "myerror in action ");
    return -1;
    //exit(0);
}

handcard playerhandcard[4];
showcard playershowcard[4];
hasplayedcard playerhasplayedcard[4];
 
//生产label
void genlabel(ofstream &ofile, int id, int label, int type, int lastcard, int labelchi)
{
    for(int i=0;i<34;i++)
    {
        for(int j=0;j<4;j++)
        {
            if(playerhandcard[j].card[i]<0)
                myerror ("handcardmyerror");
            if(playershowcard[j].card[i]<0)
                myerror ("showcardmyerror");
            if(playerhasplayedcard[j].card[i]<0)
                myerror("hasplayedcardmyerror");
        }
    }
    //ofile << rd << " "; //调试用
    ofile << "{\"myhandcard\" : [";
    for (int i = 0; i < 33; i++)
        ofile << playerhandcard[id].card[i] << ", ";
    ofile << playerhandcard[id].card[33];
    ofile << "], \"myshowcard\" : [";
    for (int i = 0; i < 33; i++)
        ofile << playershowcard[id].card[i] << ", ";
    ofile << playershowcard[(id + 1) % 4].card[33];
    ofile << "], \"next1showcard\" : [";
    for (int i = 0; i < 33; i++)
        ofile << playershowcard[(id + 1) % 4].card[i] << ", ";
    ofile << playershowcard[(id + 1) % 4].card[33];
    ofile << "], \"next2showcard\" : [";
    for (int i = 0; i < 33; i++)
        ofile << playershowcard[(id + 2) % 4].card[i] << ", ";
    ofile << playershowcard[(id + 2) % 4].card[33];
    ofile << "], \"next3showcard\" : [";
    for (int i = 0; i < 33; i++)
        ofile << playershowcard[(id + 3) % 4].card[i] << ", ";
    ofile << playershowcard[(id + 3) % 4].card[33];
    ofile << "], \"myplayedcard\" : [";
    for (int i = 0; i < 33; i++)
        ofile << playerhasplayedcard[(id + 0) % 4].card[i] << ", ";
    ofile << playerhasplayedcard[(id + 0) % 4].card[33];
    ofile << "], \"next1playedcard\" : [";
    for (int i = 0; i < 33; i++)
        ofile << playerhasplayedcard[(id + 1) % 4].card[i] << ", ";
    ofile << playerhasplayedcard[(id + 1) % 4].card[33];
    ofile << "], \"next2playedcard\" : [";
    for (int i = 0; i < 33; i++)
        ofile << playerhasplayedcard[(id + 2) % 4].card[i] << ", ";
    ofile << playerhasplayedcard[(id + 2) % 4].card[33];
    ofile << "], \"next3playedcard\" : [";
    for (int i = 0; i < 33; i++)
        ofile << playerhasplayedcard[(id + 3) % 4].card[i] << ", ";
    ofile << playerhasplayedcard[(id + 3) % 4].card[33];
    ofile << "], \"mypnum\" : ";
    ofile << playershowcard[id].pnum;
    ofile << ", \"mycnum\" : ";
    ofile << playershowcard[id].cnum;
    ofile << ", \"mygnum\" : ";
    ofile << playershowcard[id].gnum;
    ofile << ", \"next1pnum\" : ";
    ofile << playershowcard[(id + 1) % 4].pnum;
    ofile << ", \"next1cnum\" : ";
    ofile << playershowcard[(id + 1) % 4].cnum;
    ofile << ", \"next1gnum\" : ";
    ofile << playershowcard[(id + 1) % 4].gnum;
    ofile << ", \"next2pnum\" : ";
    ofile << playershowcard[(id + 2) % 4].pnum;
    ofile << ", \"next2cnum\" : ";
    ofile << playershowcard[(id + 2) % 4].cnum;
    ofile << ", \"next2gnum\" : ";
    ofile << playershowcard[(id + 2) % 4].gnum;
    ofile << ", \"next3pnum\" : ";
    ofile << playershowcard[(id + 3) % 4].pnum;
    ofile << ", \"next3cnum\" : ";
    ofile << playershowcard[(id + 3) % 4].cnum;
    ofile << ", \"next3gnum\" : ";
    ofile << playershowcard[(id + 3) % 4].gnum;

    if (type > 0) //碰,吃 杠
    {
        ofile << ", \"lastcard\" : ";
        ofile << lastcard;
    }
    ofile << ", \"label\" : ";
    ofile << label;
    if (type==2)//吃要产生labelchi
    {
        ofile << ", \"labelchi\" : ";
        ofile << labelchi;
    }
    ofile << "}  "<<endl;
}


int main()
{
    ofstream ofileplay;
    ofileplay.open("D:\\label\\playlabel.txt");
    ofstream ofilepeng;
    ofilepeng.open("D:\\label\\penglabel.txt");
    ofstream ofilechi;
    ofilechi.open("D:\\label\\chilabel.txt");
    ofstream ofileminggang;
    ofileminggang.open("D:\\label\\mingganglabel.txt");
    // ofstream ofileangang;
    // ofileangang.open("C:\\code\\mahjong\\anganglabel.txt");
    // ofstream ofilebugang;
    // ofilebugang.open("C:\\code\\mahjong\\buganglabel.txt");
    for (int i = 0; i < 4; i++)
    {
        playershowcard[i].id = i;
        playerhasplayedcard[i].id = i;
    }
    vector<string> files;
    const char * filePath = "C:\\code\\mahjong\\data";//数据路径
    getFiles(filePath, files);//子文件夹下全部的文件都会存files里去
    char str[30];
    int size = files.size();
    for(int k=0;k<size;k++)
    {
        rd=6;
        const char* name = files[k].c_str();
        outname = (char *)name;
        ifstream ifile;
        ifile.open(name);
        char input[200];
        ifile.getline(input, 200);
        ifile.getline(input, 200);
        for(int i=0;i<4;i++)
        {
            ifile.getline(input, 200);
            playerhandcard[i] = handcard(input);
            playershowcard[i]= showcard();
            playerhasplayedcard[i]=hasplayedcard();
        }
        bool canp = false, canc = false, canmg = false, canag = false, canbg = false;
        int canpid, cancid, canmgid, canagid, canbgid;
        int lastcard;
        bool lastmo=false,lastda=false;
        while (ifile.getline(input, 200))
        {
            rd++;
            int id = input[0] - '0';
            int l = strlen(input);
            int b;
            for (int i = 0; i < l; i++)
            {
                if (input[i] == 91)//input[b]='['
                {
                    b = i;
                    break;
                }
            }
            int action = chinese(input, b);
            if(action ==-1)
            {
                myerror(input);
            }
            if (action ==10)//和牌
            {
                break;
            }
            if(lastda&&lastmo)
            {
                myerror("myerror in mo and da");
            }
            if(lastda)//上一次是打牌，确定这次能否吃碰杠
            {
                int n0=playerhandcard[0].card[lastcard];
                int n1=playerhandcard[1].card[lastcard];
                int n2=playerhandcard[2].card[lastcard];
                int n3=playerhandcard[3].card[lastcard];
                for(int i=0;i<4;i++)
                {
                    int num=playerhandcard[i].card[lastcard];
                    if(num==3)
                    {
                        canmg=true;
                        canmgid=i;
                    }
                    if (num>=2)
                    {
                        canp=true;
                        canpid=i;
                    }
                }
                if(!(canp&&action==4||canmg&&action==6))//如果这一次没有人碰或者杠,开始检测是否可以吃
                {
                    
                    if(((lastcard+2)/9==lastcard/9)&&lastcard<25
                        &&playerhandcard[id].card[lastcard+1]>0 && playerhandcard[id].card[lastcard+2]>0)
                    {
                        canc=true;
                        cancid=id;
                    }
                    if(((lastcard+1)/9==lastcard/9)&&((lastcard-1)/9==lastcard/9)&&lastcard<26&&lastcard>0
                        &&playerhandcard[id].card[lastcard+1]>0 && playerhandcard[id].card[lastcard-1]>0)
                    {
                        canc=true;
                        cancid=id;
                    }
                    if(((lastcard-2)/9==lastcard/9)&&lastcard<27&&lastcard>1
                        &&playerhandcard[id].card[lastcard-1]>0 && playerhandcard[id].card[lastcard-2]>0)
                    {
                        canc=true;
                        cancid=id;
                    }
                }
            }
            // if(lastmo)
            // {
            //     int handnum=playerhandcard[id].card[lastcard];
            //     int shownum=playershowcard[id].card[lastcard];
            //     if(handnum==4)
            //     {
            //         canag=true;
            //         canagid = id;
            //     }
            //     if (shownum==3)
            //     {
            //         canbg=true;
            //         canbgid = id;
            //     }
            // }

            if (canp && action != 4) //可以碰没碰，产生一个碰的0 label
            {
                genlabel(ofilepeng, canpid, 0, 1, lastcard,-1);
            }
            if (canc && action != 5)
            {
                genlabel(ofilechi, cancid, 0, 2, lastcard,-1);
            }
            if (canmg && action != 6)
            {
                genlabel(ofileminggang, canmgid, 0, 3, lastcard,-1);
            }
            // if (canag && action != 7)
            // {
            //     genlabel(ofileangang, canagid, 0, 3, lastcard,-1);
            // }
            // if (canbg && action != 8)
            // {
            //     genlabel(ofilebugang, canbgid, 0, 3, lastcard,-1);
            // }

            canp = false, canc = false, canmg = false, canag = false, canbg = false;

            //接下来还原局面
            if (action == 0) //摸牌
            {
                int card= (getcard(input, b + 1));
                if (card==HUAPAI)//摸到了花牌，接下来自动进入补花牌过程
                {
                    lastmo=false;
                    lastda=false;
                    continue;
                }
                changecard(playerhandcard[id].card, card, 1);
                lastcard=card;
                lastmo=true;
                lastda=false;
            }
            else if (action == 1) //打牌
            {
                int card = getcard(input, b + 1);
                genlabel(ofileplay, id, card, 0, -1,-1);
                changecard(playerhandcard[id].card, card, -1);
                changecard(playerhasplayedcard[id].card, card, 1);
                lastcard=card;
                lastda=true;
                lastmo=false;
            }
            else if (action == 2) //补花牌
                continue;
            else if (action == 3) //补花后摸牌
            {
                int card = getcard(input, b + 1);
                if (card==HUAPAI)
                    continue;
                changecard(playerhandcard[id].card, card, 1);
                lastcard=card;
                lastmo=true;
                lastda=false;
            }
            else if (action == 4) //碰
            {
                int card = getcard(input, b + 1);
                if(card!=lastcard)
                {
                    myerror("pengmyerror");
                }
                genlabel(ofilepeng, id, 1, 1, card,-1);
                changecard(playerhandcard[id].card, card, -2);
                changecard(playershowcard[id].card, card, 3);
                playershowcard[id].pnum++;
                lastmo=false;
                lastda=false;
            }
            else if (action == 5) //吃
            {
                int card1 = getcard(input, b + 1);
                int card2 = getcard(input, b + 6);
                int card3 = getcard(input, b + 11);
                int pos=0;
                if(lastcard==card2)
                    pos=1;
                if (lastcard==card3)
                    pos=2;
                genlabel(ofilechi,id,1,2,lastcard,pos);
                if (card1 != lastcard)
                    changecard(playerhandcard[id].card, card1, -1);
                if (card2 != lastcard)
                    changecard(playerhandcard[id].card, card2, -1);
                if (card3 != lastcard)
                    changecard(playerhandcard[id].card, card3, -1);
                if(card1!=lastcard&&card2!=lastcard&&card3!=lastcard)
                {
                    myerror("myerror in chi");
                }
                changecard(playershowcard[id].card, card1, 1);
                changecard(playershowcard[id].card, card2, 1);
                changecard(playershowcard[id].card, card3, 1);
                playershowcard[id].cnum++;
                lastmo=false;
                lastda=false;
            }
            else if (action==6)//明杠
            {
                int card = getcard(input, b + 1);
                if(card!=lastcard)
                {
                    myerror("minggangmyerror");
                }
                genlabel(ofileminggang, id, 1, 3, card,-1);
                changecard(playerhandcard[id].card, card, -3);
                changecard(playershowcard[id].card, card, 4);
                playershowcard[id].gnum++;
                lastmo=false;
                lastda=false;
            }
            else if (action == 7)//暗杠
            {
                int card = getcard(input, b + 1);

                changecard(playerhandcard[id].card, card, -4);
                //changecard(playershowcard[id].card, card, 4);
                playershowcard[id].gnum++;
                lastmo=false;
                lastda=false;
            }
            else if (action ==8)//补杠
            {
                int card = getcard(input, b + 1);

                changecard(playerhandcard[id].card, card, -1);
                changecard(playershowcard[id].card, card, 1);
                playershowcard[id].gnum++;
                playershowcard[id].pnum--;
                lastmo=false;
                lastda=false;
            }
            else if (action ==9)//杠后摸牌
            {
                int card = getcard(input, b + 1);
                if (card==HUAPAI)
                    continue;
                changecard(playerhandcard[id].card, card, 1);
                lastcard=card;
                lastmo=true;
                lastda=false;
            }
        }
        ifile.close();
    }
    
    return 0;
}
