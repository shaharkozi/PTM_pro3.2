package test;
import java.lang.Math;

public class StatLib {



	// simple average
	public static float avg(float[] x){
		float sum = 0;
		int size = x.length;
		for (int i = 0; i<size; i++)
		{
			sum += x[i];
		}
		float avg = sum/size;
		return avg;
	}

	// returns the variance of X and Y
	public static float var(float[] x){
		float avg = avg(x);
		int size = x.length;
		float var = 0;
		float dif = 0;
		for (int i = 0; i<size; i++)
		{
			dif = x[i];
			dif -= avg;
			dif = (float)(java.lang.Math.pow(dif, 2));
			var += dif;
		}
		var /= size;
		return var;
	}

	// returns the covariance of X and Y
	public static float cov(float[] x, float[] y){
		int size = x.length;
		float avg_x = avg(x);
		float avg_y = avg(y);
		float sig = 0;
		for(int i =0;i<size; i++)
		{
			sig += ((x[i] - avg_x) * (y[i] - avg_y));
		}
		sig /= size;

		return sig;
	}


	// returns the Pearson correlation coefficient of X and Y
	public static float pearson(float[] x, float[] y){
		float cov = cov(x,y);
		float var_x = var(x);
		float var_y = var(y);
		double root_x = java.lang.Math.sqrt(var_x);
		double root_y = java.lang.Math.sqrt(var_y);
		float s = (float)(root_x * root_y);
		float res = cov/s;
		return res;
	}

	// performs a linear regression and returns the line equation
	public static Line linear_reg(Point[] points){
		float[] xPoints = new float[points.length];
		float[] yPoints = new float[points.length];
		for(int i = 0; i < points.length; i++)
		{
			xPoints[i] = points[i].x;
			yPoints[i] = points[i].y;
		}
		float a = cov(xPoints, yPoints) / var(xPoints);
		float b = avg(yPoints) - a * avg(xPoints);
		return new Line(a,b);
	}

	// returns the deviation between point p and the line equation of the points
	public static float dev(Point p,Point[] points){
		Line l = linear_reg(points);
		float dev = Math.abs(l.f(p.x) - p.y);
		return dev;
	}

	// returns the deviation between point p and the line
	public static float dev(Point p,Line l){
		float dev = l.f(p.x) - p.y;
		if(dev<0)
		{
			dev *= -1;
		}
		return dev;
	}

}
