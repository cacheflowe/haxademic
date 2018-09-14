package com.haxademic.core.draw.filters.pgraphics.archive;

import com.haxademic.core.app.P;

import processing.core.PImage;

public class FastBlurFilter {
	
	// ==================================================
	// Super Fast Blur v1.1
	// by Mario Klingemann 
	// <http://incubator.quasimondo.com>
	// ==================================================
	public static void blur(PImage img, int radius) {
		P.println("Use a shader insted of FastBlurFilter");
		if (radius<1){
			return;
		}
		int w=img.width;
		int h=img.height;
		int wm=w-1;
		int hm=h-1;
		int wh=w*h;
		int div=radius+radius+1;
		int r[]=new int[wh];
		int g[]=new int[wh];
		int b[]=new int[wh];
		int rsum,gsum,bsum,x,y,i,p,p1,p2,yp,yi,yw;
		int vmin[] = new int[P.max(w,h)];
		int vmax[] = new int[P.max(w,h)];
		int[] pix=img.pixels;
		int dv[]=new int[256*div];
		for (i=0;i<256*div;i++){
			dv[i]=(i/div);
		}

		yw=yi=0;

		for (y=0;y<h;y++){
			rsum=gsum=bsum=0;
			for(i=-radius;i<=radius;i++){
				p=pix[yi+P.min(wm,P.max(i,0))];
				rsum+=(p & 0xff0000)>>16;
				gsum+=(p & 0x00ff00)>>8;
				bsum+= p & 0x0000ff;
			}
			for (x=0;x<w;x++){

				r[yi]=dv[rsum];
				g[yi]=dv[gsum];
				b[yi]=dv[bsum];

				if(y==0){
					vmin[x]=P.min(x+radius+1,wm);
					vmax[x]=P.max(x-radius,0);
				}
				p1=pix[yw+vmin[x]];
				p2=pix[yw+vmax[x]];

				rsum+=((p1 & 0xff0000)-(p2 & 0xff0000))>>16;
				gsum+=((p1 & 0x00ff00)-(p2 & 0x00ff00))>>8;
				bsum+= (p1 & 0x0000ff)-(p2 & 0x0000ff);
				yi++;
			}
			yw+=w;
		}

		for (x=0;x<w;x++){
			rsum=gsum=bsum=0;
			yp=-radius*w;
			for(i=-radius;i<=radius;i++){
				yi=P.max(0,yp)+x;
				rsum+=r[yi];
				gsum+=g[yi];
				bsum+=b[yi];
				yp+=w;
			}
			yi=x;
			for (y=0;y<h;y++){
				pix[yi]=0xff000000 | (dv[rsum]<<16) | (dv[gsum]<<8) | dv[bsum];
				if(x==0){
					vmin[y]=P.min(y+radius+1,hm)*w;
					vmax[y]=P.max(y-radius,0)*w;
				}
				p1=x+vmin[y];
				p2=x+vmax[y];

				rsum+=r[p1]-r[p2];
				gsum+=g[p1]-g[p2];
				bsum+=b[p1]-b[p2];

				yi+=w;
			}
		}
	}	
}

