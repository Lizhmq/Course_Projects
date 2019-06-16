### Variations of Entropy

#### Joint Entropy

r.v. $X,Y$  $P(X=x_i,Y=y_i) \ \  i\in[m], j\in[n]$

$H(X,Y):=\Sigma_{i,j}p_{ij}log_2\frac1{p_{ij}}$

$H(X)=\Sigma_ip_{x_i}log_2\frac1{p_{x_i}}$

$H(Y)=\Sigma_ip_{y_i}log_2\frac1{p_{y_i}}$

$H(X,Y)=H(X)+H(Y)$,	if $X,Y$ independent else $\le$

#### Conditional Entropy

r.v. $X,Y$ $P(X=x_i,Y=y_i)$

Fix $x_i$  $P(Y|X=x_i)$

$H(Y|X=x_i)=\Sigma_jP(Y=y_j|X=x_i)log_2\frac1{P(Y=y_j|X=x_i)}$

$H(Y|X)=\Sigma_iP(X=x_i)H(Y|X=x_i)$

* $H(Y|X)=H(Y)$,	if $X,Y$ independent
* $H(Y|X)=0$,	if $X,Y$ fully dependent

$H(Y|X)$ represents the information of $Y$ given $X$.

$H(X,Y) = H(Y|X)+H(X)$

#### Mutual Entropy

Given Joint Entropy and Cond Entropy, there is:

$H(Y) \ge H(Y|X)$

Define:

​	$I(X;Y):=H(Y)-H(Y|X)$	equals	$H(X)-H(X|Y)$

​	$I(X;Y)=\Sigma_{i,j}P(X=x_i,Y=y_j)log\frac{P(X=x_i,Y=y_j)}{P(X=x_i)P(Y=y_j)}\ge0$

r.v.  $X_1, X_2, ..., X_m;Y_1,Y_2,...,Y_n$	$H(X_1^m,Y_1^n)=\Sigma P(X_1^m,Y_1^n)log\frac1{P(X_1^m,Y_1^n)}$

r.v.  $X_1, X_2, ..., X_m;Y_1,Y_2,...,Y_n$	$H(X|Y)=H(X,Y)-H(Y)$

r.v.  $X_1, X_2, ..., X_m;Y_1,Y_2,...,Y_n$

$$I(X;Y)=H(X)-H(X|Y)=H(Y)-H(Y|X)=H(X)+H(Y)-H(X,Y)$$

#### Decomposition of Joint Entropy

$H(X_1,...,X_n)=H(X_1)+H(X_2|X_1)+...+H(X_n|X_{n-1},...,X_1)$

#### KL-divergence (Relative Entropy)

$P,Q$ are prob distributions	$P=(p_1,...,p_n), Q=(q_1,...,q_n)$

$$D(P||Q):=\Sigma_ip_ilog\frac{p_i}{q_i} = \Sigma_ip_ilog\frac1{q_i}-\Sigma_ip_ilog\frac1{p_i}$$

r.v. $X$, true $P$, estimated $Q$

$I(X;Y)=\Sigma_{x,y}P(X=x,Y=y)log\frac{P(X=x,Y=y)}{P(X=x)P(Y=y)}=D(P(X,Y)||P(X)P(Y))$

$D(P||U_n)=log_2n-H(P)$

**concav**

$P=(p_1,...,p_n)$

$H(P)=H(p_1,...,p_n)$

$H(\lambda P+(1-\lambda)Q)\ge\lambda H(P)+(1-\lambda)H(Q)$

$D(P||Q)$ given $P$, is $D$ convex ? given $Q$, is $D$ convex ?	convexity of relative entropy

**convex**

*Convex:*

​	$f(\lambda x+(1-\lambda)y)\le\lambda f(x)+(1-\lambda)f(y)$

*$\mu $-Strongly Convex:*($\mu$)

​	$f(y)-f(x)\ge<\nabla f(x),y-x>+\frac\mu2||y-x||^2$ holds for $any\;x,y$

**Thm (Pinsker's Ineq):**

​	$D(P||Q)\ge\frac12||P-Q||_1^2$

​	$plog\frac pq+(1-p)log\frac{1-p}{1-q}\ge2(p-q)^2$

*Proof of Pinsker:*

​	$P=(p_1,...,p_n)$	$Q=(q_1,...,q_n)$

​	$\Alpha:=\{i:p_i\ge q_i\}$	$\Beta:=\{i:p_i < q_i\}$

​	reduce $P,Q$ to Bernoulli distribution: $P',Q'$

​	$||P'-Q'||_1=||P-Q||_1$

​	$\Sigma_{i\in\Alpha}p_ilog\frac{p_i}{q_i}+\Sigma_{i\in\Beta}p_ilog\frac{p_i}{q_i}=D(P||Q)\ge D(P'||Q')=\Sigma_{i\in\Alpha} p_ilog\frac{\Sigma p_i}{\Sigma q_i}+\Sigma_{i\in\Beta} p_ilog\frac{\Sigma p_i}{\Sigma q_i}$

**Thm: Negative entropy is 1-strongly convex w.r.t. 1-norm:**

​	$\Sigma p_ilogp_i-\Sigma q_ilogq_i\ge<\nabla_p(\Sigma_ip_ilogp_i),P-Q>+\frac12||P-Q||_1^2$

**Hw​**

1. Convexity of relative entropy
2. Pinsker's Ineq for Bernoulli distribution

#### Data Processing Inequality

*No clever manipulation of the data can improve the inferences that can be made from the data.*

- R.v. $X,Y,Z$, Markov chain $X\rarr Y\rarr Z$;

  $$P(Z|X,Y)=P(Z|Y) \harr P(Z,X|Y)=P(Z|Y)P(X|Y)$$

If $P(Z|X,Y)=P(Z|X)$, then $I(X;Y)\ge I(X;Z)$

$$I(U;V,W)-I(U;V)=\Sigma_{u,v,w}P(u,v,w)log\frac{P(u,v,w)}{P(u)P(v,w)}-\Sigma_{u,v} P(u,v)log\frac{P(u,v)}{P(u)P(v)}=\Sigma_{u,v,w}P(u,v,w)log\frac{P(u,w|v)}{P(u|v)P(w|v)}\ge 0 $$

So $I(X;Y,Z)-I(X;Y)=0$

$I(X;Y,Z)\ge I(X;Y)$

$\rarr I(X;Y)\ge I(X;Z)$