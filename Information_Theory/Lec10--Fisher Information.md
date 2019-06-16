### Lec10--Fisher Information

##### Fisher Information and Cramer-Rao Inequality

* Sample $X=(X_1,\cdots,X_n)$ (typically X_1,$\cdots$, X_n iid)

  $f(x;\theta)=\Pi_{i=1}^nf(x_i;\theta)$ -- probability density function

  Estimator: $\Phi: X \rarr \theta$

  * unbiased: $E[\Phi(X)]=\theta$
  * the lower bound of variance: $Var(\Phi(X))$

* **Def:** (Score function)  For a sample $X=(X_1,\cdots,X_n)$, let $f(x;\theta)$ be the density function of the sample. The score function is defined as:

  ​	$$S(X;\theta)=\frac\part{\part\theta}ln(f(X;\theta))$$

  ​	$E(S(X;\theta))=\int S(X;\theta)f(X;\theta)dx=\int \frac{\part}{\part\theta}f(X;\theta)dx=\frac\part{\part\theta}\int f(X;\theta)dx=0$

* **Def:** (Fisher Information) The Fisher Information of $\theta$ w.r.t. sample $X$ is defined as $I(\theta):=E[S(X;\theta)^2]=\int(\frac\part{\part\theta}lnf(X;\theta))^2dx$

* **Proposition:** $I(\theta) = -E[\frac{\part^2}{\part\theta^2}ln f(X;\theta)]$

  Proof:	$E[\frac{\part^2}{\part\theta^2}lnf(X;\theta)]=\int \frac{\part^2}{\part\theta^2}lnf(X;\theta)f(X;\theta)dx=\int[\frac{-(\frac{\part}{\part\theta}f(X;\theta))^2}{f^2(X;\theta)}+\frac{\frac{\part^2}{\part\theta^2}f(X;\theta)}{f(X;\theta)}]f(X;\theta)dx=-E(S(X;\theta)^2)$

* **Thm(Cramer-Rao Inequality)**

  For any unbiased estimator $\Phi: X \rarr R$, $Var(\Phi(X)) \ge \frac1{I(\theta)}$

  Proof: $I(\theta)=Var(S(X;\theta))=E[S^2(X;\theta)]$

  ​	Cauchy Inequality:	$Var(\Phi(X))Var(S(X;\theta)) \ge E[(\Phi(X)-E\Phi(X))(S(X;\theta)-ES(X;\theta))]^2=E[\Phi(X)S(X;\theta)]^2$

  $E[\Phi(X)S(X;\theta)]=\int \Phi(X)\frac{\part}{\part\theta}lnf(X;\theta)f(X;\theta)dx=\frac{\part}{\part\theta}E(\Phi(X))=1$

##### Fisher Information for Multiple Parameters

Sample vector $X$

$\hat{\theta}=\phi(X)$	$\hat{\theta} \in R^k$	Estimate $Cov(\phi(X))$

$I(\theta)=E[\nabla_\theta ln(f(X;\theta)\nabla_\theta lnf(X;\theta)^T]=Cov(S(X;\theta))$

* **Thm(Cramer-Rao Inequality)**

  Every unbiased esitimator $\phi$ satisfies:

  ​	$Cov(\phi(X)) \succeq I(\theta)^{-1}$			$A \succeq B$ means $A-B$ is a positive definite matrix.

* A simplified version: Estimate $q(\theta_1,\dots,\theta_k), q:R^k \rarr R$, if $\phi$ is an unbiased estimator or $q(\theta)$, then:

  ​	$Var(\phi(X))\ge \nabla_\theta q(\theta)^TI(\theta)^{-1}\nabla_\theta q(\theta)$

$E[\phi(X)]=q(\theta)$

$\nabla_\theta q(\theta)=\nabla_\theta\int\phi(X)f(X;\theta)dx=\int\phi(X)\frac{\nabla_\theta f(X;\theta)}{f(X;\theta)}f(X;\theta)dx=E[\phi(X)S(X;\theta)]=E[(\phi(X)-E[\phi(X)])S(X;\theta)]$

Then we have:

$\nabla_\theta q(\theta)^TI(\theta)^{-1}\nabla_\theta q(\theta)=\nabla_\theta q(\theta)^TI(\theta)^{-1}S(X;\theta)E[\phi(X)-E[\phi(X)]]$

Cauchy Inequality:

$\le Var(\phi(X))^{1/2}\{\nabla_\theta q(\theta)^TI(\theta)^{-1}S(X;\theta)S(X;\theta)^TI(\theta)^{-1}\nabla_\theta q(\theta))\}^{1/2}=Var(\phi(X))^{1/2}\{\nabla_\theta q(\theta)^TI(\theta)^{-1}\nabla_\theta q(\theta)\}^{1/2}$



**Thm**: (Fano's Inequality)

​	Send message $X$, receive message $Y$.

​	$P_e \ge \frac{H(X|Y)-1}{log|H|}$ 	$H$ is the support

Proof:

​	$H(X|Y,X=g(Y))=0$

​	$H(X|Y,X\ne g(Y)) \le log|H|$

Define r.v. $E$ as $E = 0\ if\ X=g(Y)\ else \ 1$

​	$H(X,E|Y) = H(X|Y)+H(E|X,Y)=H(X|Y)$

​	$H(X,E|Y)=H(E|Y)+H(X|E,Y) \le 1 + P(E=0)H(X|E=0,Y)+P(E=1)H(X|E=1,Y)\le1+P_elog|H|$

Q.E.D.