### Lec12 - Rate Distribution Theory

Quantification:

$X \sim U\{a,b,c\}$	$H(X) = log_23 bits$

$d(x,x') = I[x \ne x']$

$D = \Sigma_xp(x)d(x, \phi(x)) = 1/3$

$\phi^{(n)}:=\{a,b,c\}^n->\{a,b,c\}^n$	$|\phi^{(n)}| = 2^n$

**Def:** $\phi^{(n)}$ is a mapping: $\phi^{(n)}:= X^n->X^n$. Say $\phi^{(n)}$ is a $(2^{nR},n)$ rate distribution code if $\phi^{(n)} \le 2^{nR}$.

**Def:** $D:= E[d(X^n,\phi(X^n))]$

Given $D$, find the best encoding method to minimize $R$.

**Thm:** $R^{(I)}(D) = min_{P(X'|X)}I(X;X')$	$E[d(X;X')] \le D$

