package nl.rivm.emi.dynamo.estimation;

/* File: Simplex.java          */


	/* Copyright (C) 1997 K. Ikeda */
/* java applet element remove by Hendriek */
/*	import java.util.*;
	import java.awt.*;
	

	class RN {
		int	n;
		int	d;

		RN() {n=0; d=1;}
		RN(int n) {this.n=n; this.d=1;}
		RN(int n, int d) {this.n=n; this.d=d; reduce();}
		RN(String s) {
			int k = s.indexOf('/');
			if (k>0) {
				d = Integer.valueOf(s.substring(k+1)).intValue();
				s = s.substring(0,k);
			} else
				d = 1;
			n = Integer.valueOf(s).intValue();
			reduce();
		}

		int euclid(int a, int b) {
			int	q,r;

			if (a < 0) a = -a;
			if (b < 0) b = -b;
			if (b == 0)
				if (a==0)
					return -1;
				else
					return a;
			for (;;) {
				q = a / b;
				r = a % b;
				if (r==0)
					break;
				a = b;
				b = r;
			}
			return b;
		}

		boolean reduce() {
			int	c;

			if ((c = euclid(n,d))<0)
				return false;
			if (d<0)
				c *= -1;
			n /= c;
			d /= c;
			return true;
		}

		void set(int n) {this.n=n; this.d=1;}
		void set(int n, int d) {this.n=n; this.d=d;}
		void set(RN a) {n=a.n; d=a.d;}

		void mul(RN a) {
			a.reduce();
			RN aa = new RN(n,a.d);
			RN bb = new RN(a.n,d);
			aa.reduce();
			bb.reduce();
			n = aa.n * bb.n;
			d = aa.d * bb.d;
		}

		void div(RN a) {
			a.reduce();
			RN aa = new RN(n, a.n);
			RN bb = new RN(a.d, d);
			aa.reduce();
			bb.reduce();
			n = aa.n * bb.n;
			d = aa.d * bb.d;
		}

		void inv() {int x; x = n; n = d; d = x; reduce();}

		boolean plus(RN a) {
			int	c,x,y;

			c = euclid(d, a.d);
			if (c < 0)
				return false;
			if ((x = a.d/c*n + d/c*a.n)==0) {
				x = 0;
				y = 1;
			} else
				y = d/c*a.d;
			n = x;
			d = y;
			this.reduce();
			return true;
		}

		boolean minus(RN a) {
			int	c,x,y;

			c = euclid(d, a.d);
			if (c < 0)
				return false;
			if ((x = a.d/c*n - d/c*a.n)==0) {
				x = 0;
				y = 1;
			} else
				y = d/c*a.d;
			n = x;
			d = y;
			this.reduce();
			return true;
		}

		boolean gt(RN a) {RN c=new RN(n,d); c.minus(a); return c.n>0;}
		boolean ge(RN a) {RN c=new RN(n,d); c.minus(a); return c.n>=0;}
		boolean eq(RN a) {RN c=new RN(n,d); c.minus(a); return c.n==0;}
		boolean le(RN a) {RN c=new RN(n,d); c.minus(a); return c.n<=0;}
		boolean lt(RN a) {RN c=new RN(n,d); c.minus(a); return c.n<0;}
	}

	public class Simplex  {
		int	m,n,r,s;
		int	step,cycle;
		RN[][]	a = new RN[10][20];
		int[]	base = new int[10];
		String	message = "";

		
		

		void input_data() {
			m = Integer.parseInt(getParameter("m"));
			n = Integer.parseInt(getParameter("n"));
			String sdat = getParameter("data");
			StringTokenizer st = new StringTokenizer(sdat,",");
			for (int i = 0; i<=m; i++) {
				for (int j=0; j<n; j++)
					a[i][j] = new RN(st.nextToken());
				base[i] = n+i;
				for (int j=n; j<n+m; j++) {
					RN rn = new RN(0);
					if (j == i+n)
						rn.set(1);
					a[i][j] = rn;
				}
				a[i][n+m] = new RN(st.nextToken());
			}
			n += m;
		}
*/
	/*	boolean step1() {		/* search pivot s of (r, s) */
		/*	RN	c = new RN();

			s = 0; r = -1;
			c.set(a[m][s]);
			for (int j=1; j<n; j++)
				if (c.gt(a[m][j])) {
					s = j;
					c.set(a[m][s]);
				}
			if (c.n>=0) {
				s = -1;
				return true;
			} else
				return false;
		}

		boolean step2() {		/* search pivot r of (r, s) */
		/*	RN	t = new RN();
			RN	c = new RN();

			for (int i=0; i<m; i++) {
				if (a[i][s].n<=0)
					continue;
				t.set(a[i][n]);
				t.div(a[i][s]);
				if (r<0 || t.lt(c)) {
					r = i;
					c.set(t);
				}
			}
			if (r<0)
				return true;
			else
				return false;
		}

		void step3() {		/* pivot operation 1 */
	/*		RN	c = new RN();

			base[r] = s;
			c.set(a[r][s]);
			for (int j=0; j<=n; j++)
				a[r][j].div(c);
		}

		void step4() {
			RN	c = new RN();
			RN	t = new RN();

			for (int i=0; i<=m; i++) {
				if (i == r)
					continue;
				c.set(a[i][s]);
				for (int j=0; j<=n; j++) {
					t.set(c);
					t.mul(a[r][j]);
					a[i][j].minus(t);
				}
			}
			r = s = -1;
		}

		
	}
*/

