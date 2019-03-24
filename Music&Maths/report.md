## 音乐与数学期中研究报告

###### 小组成员：

程晟	1600012909

李拙	1600012911

姜易坤	1600012829

### 歌曲选取

三生三世：	时长4'19''	ssss.midi

童话镇：	时长2'37''	thz.midi

### 三种律制的音级

<b>十二平均律</b>

>在一个八度以内相邻半音之间的声音频率的对数之差均等，即十二个半音的频率组成一个等比数列。

<b>五度相生律</b>
>依靠纯五度音程的$3:2$比例来不断向上（或向下）生成半音，超出八度范围则降八度，如此得到十二个半音的频率和中央C的比值。

<b>纯律</b>
>结合了纯五度音程$3:2$比例和大三度音程的$5:4$比例，计算出其他音程的频率比例，推导出所有半音的频率和中央C的比值。

三种律制各个音的频率关系表如下，频率比是指该音和中央C的频率之比：

| 频率比\律制 | 十二平均律           | 五度相生律               | 纯律              |
| ----------- | -------------------- | ------------------------ | ----------------- |
| C           | 1                    | 1                        | 1                 |
| #C          | $2^{\frac {1}{12}}$  | $\frac {3^{7}}{2^{11}}$  | $\frac {16} {15}$ |
| D           | $2^{\frac {2}{12}}$  | $\frac {3^{2}}{2^{3}}$   | $\frac 9 8$       |
| #D          | $2^{\frac {3}{12}}$  | $\frac {3^{9}}{2^{14}}$  | $\frac 6 5$       |
| E           | $2^{\frac {4}{12}}$  | $\frac {3^{4}}{2^{6}}$   | $\frac 5 4$       |
| F           | $2^{\frac {5}{12}}$  | $\frac {3^{11}}{2^{17}}$ | $\frac 4 3$       |
| #F          | $2^{\frac {6}{12}}$  | $\frac {3^{6}}{2^{9}}$   | $\frac {45} {32}$ |
| G           | $2^{\frac {7}{12}}$  | $\frac {3}{2}$           | $\frac 3 2$       |
| #G          | $2^{\frac {8}{12}}$  | $\frac {3^{8}}{2^{12}}$  | $\frac 8 5$       |
| A           | $2^{\frac {9}{12}}$  | $\frac {3^{3}}{2^{4}}$   | $\frac 5 3$       |
| #A          | $2^{\frac {10}{12}}$ | $\frac {3^{10}}{2^{15}}$ | $\frac 9 5$       |
| B           | $2^{\frac {11}{12}}$ | $\frac {3^{5}}{2^{7}}$   | $\frac {15} 8$    |

### 生成不同律制的音乐

通过修改MIDI文件生成同一段音乐的不同律制实现：

> MIDI(Musical Instrument Digital Interface)文件是一种由描述音乐的标记组成的文件，文件中的主要的记录音乐的方式是记录发出乐音的音符、力量、持续时间等等信息，所以MIDI文件通常很小。将MIDI文件转化为音乐需要利用音乐资源库实现MIDI文件中记录的演奏操作来产生实际音乐的波形。

>MIDI由多种多样的MIDI事件组成，其中有一种MIDI事件叫做Pitch Bend事件，可以对某个音做一些音高上的调整，用专业的术语说叫“弯音”。由于现在默认使用的律制是十二平均律，所以要修改音乐律制只要对某些音的音高做一些偏移即可。

改变音乐的律制的具体实现借用了<a href=https://github.com/Chaser-wind/TemperamentStudio>Github上的资源</a>，这是一个由MIT开发的小的java程序，其中利用`javax.sound.midi`库实现了midi文件的律制转换工作，思想正是在原有的midi文件中添加`javax.sound.midi.ShortMessage.PITCH_BEND`事件。使用这个java程序，我们生成了两首歌曲的不同律制的版本。

| 律制\音乐  | 三生三世   | 童话镇    |
| ---------- | ---------- | --------- |
| 五度相生   | ssss_b.wav | thz_b.wav |
| 纯律       | ssss_p.wav | thz_p.wav |
| 十二平均律 | ssss_a.wav | thz_a.wav |

### 文献总结

 我们阅读了参考文献[1], [2]，总结出现有的基于机器学习的音乐分类流程大致如下：

音乐数据	=>		特征提取	=>		分类器	=>		输出

[1]中提取了Music Signal的MFCC信息，作为SVM或FFN(Feed-Forward Network)的输入训练分类器，结果显示SVM的效果不如FFN；而[2]中通提取了信号的RMS, ZCR, SG, SF和MFCC为特征作为FFN的输入训练分类器。

于是我们也采用相似的思路，提取信号的MFCC特征来训练分类器。

### 实现分类器

#### 数据集准备

我们将三种律制的音乐都切分为时长30s的片段并舍弃不足的片段（cut.py），得到三类律制的音乐的各13个样本；然后读取wav文件的信息，选取窗口长度为0.1s，fft宽度为2205，计算MFCC特征并保存（trans.py）；得到的MFCC记录文件作为训练模型的数据集。

####分类器训练

每个样本生成的MFCC数据为301\*13的特征矩阵，我们直接将它展开为1\*3913，这样，输入数据为3913维；标签用OneHot编码--将第一类编码为[1, 0, 0]，第二类为[0, 1, 0]，以此类推。

FFN分类器的输入为3913维，连接到3维隐藏层，选用sigmoid激活函数，然后用softmax作为输出，用tensorflow实现（classify.py）。

将数据集按8:2划分为训练集和测试集，训练4000轮后进行测试。结果如下（某一次）：

accuracy on train data: 0.8333333333333334
accuracy on test data: 1.0

在此之后，我们也测试了SVM在该问题上的表现，结果并不好。原因可能是：①SVM的表示能力不够强，不适合处理此问题；②数据集太小，SVM还不能较好得学习到样本信息。

### 项目地址

我们将工作上传到了git仓库上，代码和数据都是开源的。地址如下：

https://bitbucket.org/Lzzz/course_projects/src/master/Music%26Maths

### 参考文献

[1] Gursimran Kour, Neha Mehan, Music Genre Classification using MFCC, SVM and BPNN, International Journal of Computer Applications, 112 (2015) no. 6, 12–14.

[2] N. Prabhu, J. Andro-Vasko, D. Bein and W. Bein, Music Genre Classification Using Data Mining and Machine Learning, in “Information Technology – New Generations”, Shahram Latifi ed., Advances in Intelligent Systems and Computing, Vol. 738, Springer International Publishing, 2018, pp. 397–403.