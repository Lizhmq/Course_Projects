### Lec-7 Kolmogorov Complexity

#### Kolmogorov Complexity

Entropy:	minimum description length for random variables / (deterministic object ?).

* **Def** : Kolmogrov Complexity

  The K-complexity for string $s$ w.r.t. Turing Machine $U$ is

  $K_U(s):=min_{U(p)=s}|p|$

* **Thm** : For any universal TM $u,u'$ and any $s\in\{0,1\}^*$

  $K_U(s)\le K_{U'}(s)+c$

**Hw.**	Turing Machine, Universal TM, Computable, Halting Problem

* **Thm** : K-complexity is not computable

  **Proof** : Assume $\exist$ algorithm that computes K-complexity, so $\exist p$ finds the first string $s^*$ whose K-c $\ge 10^{10}$. Algorithm *p* can be used to describe $s^*$.

#### Maximum Entropy Principle

Estimate probability distribution of a r.v. $X$ -- $EX=\mu, VarX=\sigma^2$

**Hw.**  MaxEnt Distribution	-- $N(0,\sigma^2)$

*After Lec10*:

​	uniform distribution $u$

​	$0 \le D(f||u) = \int f\cdot ln\frac fu = -h(f)-\int flnu=-h(f)-\int ulnu=-h(f)+h(u)$

**Thm**: For random vector $X$, density function $EX=0,Cov(X)=E[XX^T]=\Sigma$, $N(0,\Sigma)$ is the MaxEnt distribution.

**Prove**:

​	$0 \le D(f||g) = \int f\cdot ln\frac fg = -h(f)-\int flng=-h(f)-\int glng=-h(g)+h(u)$

​	$\int flng=\int f(x)[ln(\frac1{(2\pi)^{n/2}|\Sigma|^{1/2}})-\frac12x^T\Sigma^{-1}x]dx$

​	where $\int g(x)x_ix_jdx=\int f(x)x_ix_jdx$

**Thm**: For random nonnegative integer $X$, $X=\mu$

$$max_p\Sigma_{i\ge0}p_ilog\frac1{p_i}\ \  s.t.\ \ \Sigma_{i\ge0}ip_i=\mu\ \ \Sigma_{i\ge0}p_i=1$$

Lagrange:	$p_k \ \propto e^{-ck}$

**Hw.** Exp distribution is MaxEnt.

**Thm**: Concave

$log\ det(\Sigma)$ is a concave function.

**Prove**:	fix $\Sigma_0$, $g(t) = log|\Sigma_0 + t\Sigma|$

​	if $g(t)$ is concave for $t \in [0, 1]$, then $log\ det$ is concave.

​	As $\Sigma_0$ is positive definite, we can decompose $\Sigma_0 = QQ^T$

​	reduce to $g(t) = log|I + tV|$, decompose $V = PAP^T$ where $P$ is orthonormal matrix, elements in $A$ are eigenvalue.

​	reduce to $g(t) = log|I + tA|$

**Prove**:

​	$\Sigma_1, \Sigma_2$ p.d. $\lambda \in [0, 1]$

​	$logdet(\lambda\Sigma_1+(1-\lambda)\Sigma_2) \ge \lambda logdet(\Sigma_1)+(1-\lambda)logdet(\Sigma_2)$

​	Constuct $X_1,X_2$ r.v., $X_1~N(0,\Sigma_1), X_2~N(0,\Sigma_2)$

​	r.v. $K$, $P(K=1)=\lambda, P(K=0)=1-\lambda$

​	$Z=X_1\ if\ K=1\ else\ X_2$

​	So:	$Cov(Z)=\lambda\Sigma_1+(1-\lambda)\Sigma_2$

​				$h(Z) \le \frac12log[(2\pi e)^n|\lambda\Sigma_1+(1-\lambda)\Sigma_2|]$		(MaxEnt of Gaussian Distribution)

​				$h(Z) \ge h(Z|K) = \lambda h(Z|K=1)+(1-\lambda)h(Z|K=0) = \lambda h(X_1)+(1-\lambda)h(X_2)$

​				Q.E.D.				

$X \ N(0,\Sigma)$

$h(X)=\frac12log((2\pi e)^n|\Sigma|)$ bits		$Compute$

### Lec-8 Channel Coding: Algorithms

*Map string* $\{0,1\}^m$ to $\{0,1\}^n$ *with maximum Hamming Distance*

$\{0,1\}^m \rarr \{0,1\}^n$

$N=2^n, M=2^m, V_B=\Sigma_{k=0}^{r/2}\tbinom{n}{k}$

* **Thm** :  Chernoff Bound

  iid. Bernoulli r.v. $X,X_1,...,X_n,\ \ EX=p$

  $P(\frac1n\Sigma_iX_i\ge p+\delta)\le 2^{-nD_B(p+\delta||p)}\ \ \ \  where\ \ \ \  D_B(p+\delta || p)=(p+\delta)log_2\frac{p+\delta}{p}+(1-p-\delta)log_2\frac{1-p-\delta}{1-p}$

  **Proof** :

  ​	1) Chernoff Ineq : r.v. $Y$ $P(Y\ge k)=P(e^{tY} \ge e^{tk})\	$

  ​			Markov Ineq:	$\le inf_{t>0}Ee^{tY}e^{-tk}$

  ​	2) $P(\frac1n\Sigma X_i\ge p+\delta)=P(\Sigma X_i\ge n(p+\delta))  \le inf_{t>0}Ee^{t\Sigma X_i}e^{-nt(p+\delta)}$

  ​		$X_i \ \ iid \rarr $ $Ee^{t\Sigma X_i}=({Ee^{tX}})^n=[pe^t+1-p]^n$

  **Hw.** Chernoff Bound

* **Thm** : Gilert-Vashamov Bound

  If $n\ge \frac{2m}{1-H(\delta)}$ where $H(\delta)=-(\delta log\delta+(1-\delta)log(1-\delta))$	$\delta \in(0,\frac12)$

  then there exists $c_1,c_2,...,c_{2^m} \in \{0,1\}^n$

  such that $d_H(c_i,c_j)\ge \delta n$

  **Proof** : Probabilistic Method

  ​	Uniformly random chooses two strings $\in\{0,1\}^n,S,S'$

  ​		$P(d_H(S,S')\le \delta n) \le 2^{-n(l-H(\delta))}$	// Chernoff Bound

  ​	Uniformly random chooses $2^m$ strings $\in \{0,1\}^n$

  ​		$P(\exist_{i\ne j} i,j\in[2^m], d_H(c_i,c_j)\le\delta n) \le 2^{2m}2^{-n[1-H(\delta)]}$

  ​	When $n\ge \frac{2m}{1-H(\delta)}$, $P<1$	QED.

Decoding: find the nearest neighbor of encoded message.

Codes should share some special structure to design efficient decode algorithm.

* **Hamming Codes(7,4)** : 1) $d_H(c_i,c_j)\ge3 bits$; 2) Coding/Decoding Computationally efficient

  $GF(2)$

  The kernel space (null space) of $H$ $dim(ker(H)) = 7-3=4$

  $|ker(H)|=2^4=16$	$c,c' \in ker(H)$	$d_H(c,c')=|c_i+c_j|_1\ge 3$	$c_i+c_j \in ker(H)-\{0^7\}$
  $$
  \left[
   \begin{matrix}
     0 & 0 & 0 & 1 & 1 & 1 & 1 \\
     0 & 1 & 1 & 0 & 0 & 1 & 1 \\
     1 & 0 & 1 & 0 & 1 & 0 & 1
    \end{matrix}
    \right] \tag{H}
  $$

  * Encoding: $\{0,1\}^4\rarr \{0,1\}^7$	$\{0,1\}^4\rarr ker(H)$

  * Decoding: $\{0,1\}^7\rarr ker(H)$

    $HS=H(c+e_i)=He_i$

  * Encoding: $\{0,1\}^4 \rarr ker(H)$	$H=[P_{3*4}\ \ I_{3*3}]$	$G=[I_{4*4}\ \ P^T]$

    $HG=0$	$c=mG^T \in ker(H)$

