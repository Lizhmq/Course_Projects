### Lab09

#### quota

* 安装quota--sudo apt install quota quotatool

```bash
sudo apt install quota
```

* 修改fstab使文件系统mount时支持usrquota

```
sudo vim /etc/fstab
```

```txt
UUID=d97f2056-e272-11e8-acbe-0800278c39db / ext4 defaults 0 0
改为==>
UUID=d97f2056-e272-11e8-acbe-0800278c39db / ext4 defaults,usrquota 0 0
```

* 重新挂载

```bash
sudo mount -o remount,usrquota /
```

* 开启quota

```
quotacheck -vagum
```

* 设置用户testuser的软限额和硬限额

```
sudo quotatool -u testuser -bq 8k -l "16 k" /
```

* 查看quota设置情况

```
sudo repquota /
```

```txt
*** Report for user quotas on device /dev/sda2
Block grace time: 7days; Inode grace time: 7days
                        Block limits                File limits
User            used    soft    hard  grace    used  soft  hard  grace
----------------------------------------------------------------------
root      -- 23983768       0       0         142924     0     0
daemon    --      64       0       0              4     0     0
man       --    2128       0       0            141     0     0
systemd-network --      12       0       0              3     0     0
syslog    --   22300       0       0              5     0     0
_apt      --      16       0       0              5     0     0
lxd       --       4       0       0              1     0     0
dnsmasq   --       4       0       0              1     0     0
landscape --       8       0       0              3     0     0
pollinate --       4       0       0              2     0     0
lzzz      -- 19535008       0       0         136634     0     0
testuser  --       8       8      16  	          2     0     0
#62583    --       4       0       0              2     0     0
```

其中testuser软限额为8k，硬限额为16k

* 切换到testuser

新建目录，一个空目录占用一个inode和一个block，4kblock配置下为8k

```bash
mkdir a # 成功
mkdir b # 成功  repquota / 显示grace 7days，已经超过软限额8k达到16k
mkdir c # mkdir: cannot create directory ‘c’: Disk quota exceeded
# 超过硬限额，新建目录失败
```

#### sshfs

FUSE(Filesystem in Userspace)是一个面向类Unix系统的软件接口，它使得无特权的用户创建自己的文件系统而无需修改内核代码。SSHFS就是一种用户空间文件系统，它通过SSH协议访问远程文件系统。

使用sshfs：

```bash
sudo apt install sshfs
sshfs S121600012911@ics9.pku.edu.cn: osplab/lab09/ -p 1122
```

```bash
lzzz@ubuntu:~/osplab/lab09$ ls
corpus_smarty.txt  mycut.py  tst.c  tst.s
```

