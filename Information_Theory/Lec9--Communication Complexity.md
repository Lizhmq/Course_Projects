### Lec9-Communication Complexity

#### Deterministic Algorithm

Setting: Alice and Bob	compute $f(x,y), x,y \in \{0,1\}^n$

​					    $x \in$ Alice $y \in$ Bob

​					    Communication: # of bits communicated

​	1) protocol design (UB)

​	2) hardness (LB)

$f(x,y)=1 \ if\  x = y\ \ else \ \ 0$	$CC(f_{Eq})\ge\Omega(n)$	--Deterministic protocol

matrix $2^n*2^n$ $x(f)$: minimum number of chronomic rectangles

* **Thm1:** $CC(f)\ge log_2\chi(f)$

1) Lower bound	$\Omega(log_2\chi(f))$

2) Upper bound in terms of $\chi(f)$?

* **Thm2:** $log_2\chi(f) \le CC(f)\le O(log_2^2\chi(f))$

**Proof:** Represent each rectangle w/ $log_2\chi(f)$ bits

Define: 

* For a rectangle $R$, define $K_x(R)=$ # of rectangles have overlap w/ $R$ in rows.

* For a rectangle $R$, define $K_y(R)=$ # of rectangles have overlap w/ $R$ in columns. 

Protocol: For $t=1,2,...$

1. Alice choose a rectangle $R$ such that $x \in R$, and $K_x(R)$ is the smallest among all rectangles still active. Remove all rectangle not overlap w/ $R$ in rows$(M_1)$.
2. Bob choose a rectangle $R'$ such that $y \in R'$, and $K_y(R')$ is the smallest among all rectangles still active. Remove all rectangle not overlap w/ $R'$ in columns$(M_2)$.

$M_1 = N - K_x(R)$	$M_2 = N - K_y(R')$

$K_x(R) + K_y(R) < N$	$K_x(R)\le K_x(R')$	**Q: why?**

so. $max(M_1,M_2) \ge \frac N2$

$Rank(M) \le \chi(f)$	Matrix decomposition -- $Rank(A+B) \le Rank(A) + Rank(B)$

* **Log rank Conjecture**

  $CC(f) \le polylog(Rank(M_f))$

* **Best Upper Bound**

  $CC(f) \le \frac{Rank(M_f)}{log(Rank(M_f))}$

  $CC(f) \le \sqrt{Rank(M_f)}$

**Hw:**

1)

​	$f(x,y)=<x,y>=\bigoplus x_iy_i$

​	$g(x,y)=(-1)^{f(x,y)}$	matrix $g$ is orthogonal.

​	**Walsh-Hadamad matrix**

2)

​	Graph $G=<V,E>$

​	Alice has a clique $C\subseteq G$, Bob has an independent set $I \subseteq V$

​	Goal: Decide if $C\ interset\  I = \empty$

​	Design a protocol: as small number of bits as possible in terms of $n=|V|$.

​	Optional: lower bound

#### Randomized Algorithm

Consider $f(x,y)=1_{x==y}$

$P \in [n^2,n^3]$	Polynomial on $Z_P$

Protocol:

1) Alice uniformly randomly select a $t \in \{0,1,...,p-1\}$ and construct polynomial

$u=x_{n-1}t^{n-1}+x_{n-2}t^{n-2}+...+x_0(mod\ p)$, send $u,t$ to Bob.

2) Bob calculate $v$ and check if $u==v$.

$Root number \le t-1, Error Prob \le \frac{n-1}{p}$

**Extention**

Multi-agent communication: one send message to everyone else.

$f(x,y,z)=MajorityFunction$	$x,y,z \in \{0,1\}^n \ f \in \{0,1\}$

$f_{MJ}(x,y,z)=\bigoplus_{i=1}^nMajorityvote(x_i,y_i,z_i)$

**Hw1.** Majorityvote $CC(f)$

**Hw2.** Number on your forehead setting