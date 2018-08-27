ofVec3f	ComputeCurl(float	x,	float	y,	float	z)	
{	
  float	eps	=	1.0;	
  float	n1,	n2,	a,	b;	
  ofVec3f	curl;	
  n1	=	noise(x,	y	+	eps,	z);	
  n2	=	noise(x,	y	-	eps,	z);	
  a	=	(n1	-	n2)/(2	*	eps);	

  n1	=	noise(x,	y,	z	+	eps);	
  n2	=	noise(x,	y,	z	-	eps);	
  b	=	(n1	-	n2)/(2	*	eps);	

  curl.x	=	a	-	b;	

  n1	=	noise(x,	y,	z	+	eps);	
  n2	=	noise(x,	y,	z	-	eps);	
  a	=	(n1	-	n2)/(2	*	eps);	

  n1	=	noise(x	+	eps,	y,	z);	
  n2	=	noise(x	+	eps,	y,	z);	
  b	=	(n1	-	n2)/(2	*	eps);	

  curl.y	=	a	-	b;	
  n1	=	noise(x	+	eps,	y,	z);	
  n2	=	noise(x	-	eps,	y,	z);	
  a	=	(n1	-	n2)/(2	*	eps);	

  n1	=	noise(x,	y	+	eps,	z);	
  n2	=	noise(x,	y	-	eps,	z);	
  b	=	(n1	-	n2)/(2	*	eps);	

  curl.z	=	a	-	b;	

  return	curl;	
}	