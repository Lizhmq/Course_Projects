### Lec-5 Entropy Rate

Regard Random Source $X_1,X_2,...,X_t,...$ as Stochastic Process $(X_t)_{t\ge 1}$

$E(l(X_1^T))\in[H(X1,...,X_T),H+1)$

* **Def 1**: The Entropy rate for a random source $X=(X_t)_{t\ge 1}​$

  $H(X)=lim_{T\rarr \inf}\frac1TH(X_1,...,X_T)$

* **Def 2**: $H(X)=lim_{T\rarr \inf}H(X_T|X_1^{T-1})$

  according to Entropy Decomposition

### Lec-6 Differential Entropy

* **Def **: Differential Entropy

  Assume we have a conditional r.v. $X$ with density function $f(x)$

  $h(X)=-\int f(x)log(f(x))dx$

$X$ discretization $\Delta \rarr discrete r.v. S_\Delta$

$h(X)=H(X_\Delta)-log\frac1\Delta$

*Discrete* r.v. $X,a>0,b$

$H(X+b)=H(X)=H(aX)$ 

*Continuous* r.v.

$h(X+b)=h(X)\ne h(aX)$	**Hw1.**

* **Def** : Relative Entropy(KL-divergence)

  $D(f||g):=\int f(x)log\frac{f(x)}{g(x)}dx$	where $f,g$ is density function

$D(f||g)=lim_{\Delta\rarr 0}D(P_\Delta||Q_\Delta)$

**Hw2. Is Entropy finite?**