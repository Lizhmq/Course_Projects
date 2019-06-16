#### Lec1

*Introduction to Information Theory*

#### Lec2

*Probability Theory*

Random Variable: $X$	Expectation: $EX$	Variance: $E(X-EX)^2$

Probability Distribution: Fuction $f$: $f(X)=P(X)$

#### Lec3

**Shortest average encoding length -- Entropy**

**What**

Encode $n$ events with probability $p_1,p_2,...,p_n$ into $n$ different binary (0,1) strings.

Transfer messages for multiple times.

Goal: To minimize Message Length on average -- $E(l) = \Sigma p_il_i$

Constraints: Decoded messages aren't ambiguous. -- sufficient condition(not necessary) - prefix-free codes

**How**

*Kraft Inequality*     For prefix-free code		$\Sigma 2^{-li}\le1$

Given r.v.(random variable) $X$,

pmf(probability math function) ($p_1,p_2,...,p_n$)	$p_i\ge0, \Sigma p_i=1$

$min_{l_1,l_2,...,l_n}\Sigma p_il_i$	s.t.	$\Sigma2^{-li}\le1$ (the same as to be equal)

regard $q_i$ as $2^{-li}$

$min_{l_1,l_2,...,l_n}\Sigma p_ilog\frac1{q_i}$

$\Sigma p_ilog\frac{p_i}{q_i}\ge0$

$\Sigma p_ilog\frac1{q_i}\ge \Sigma p_ilog\frac1{p_i}$

**Entropy**

The lower bound of min code length on average:		$\Sigma p_ilog\frac1{p_i}$

Upper bound:		$Entropy(p)+1$

$H(X)=\Sigma_ip_ilog_2\frac1{p_i} \le log_2n$		-- Jensen's Inequality	(convex)

**Add property**

r.v. $X - (p_1,p_2,...,p_n)$

r.v. $Y - (q_1,q_2)$

r.v. $Z - (p_1,...,p_{n-1},q_1,q_2)$

$H(X)+p_nH(Y)=H(Z)$

**Optimal Code (Huffman Code)**

* Assume $p_i \ge p_j$, then $|c_i|\le|c_j|$
* Kraft Inequality:	$\Sigma_i2^{-|c_i|}=1$
* $|c_n|=|c_{n-1}|$
* $c_1,...,c_{n-1}^{'}$ is also an optimal code of $X$

