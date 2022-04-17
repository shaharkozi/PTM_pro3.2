package test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {
	List<CorrelatedFeatures> mycor = new ArrayList<>();
	List<AnomalyReport> myreport = new ArrayList<>();
	float Threshold = 0.9f;
	public void setThreshold(float t)
	{
		this.Threshold = t;
	}
	@Override
	public void learnNormal(TimeSeries ts) {
		int len=ts.getValarr(ts.getKey(0)).length;

		float vals[][]=new float[ts.getColumns()][len];
		for(int i=0;i< ts.getColumns();i++){
			for(int j=0;j<ts.getValarr(ts.getKey(0)).length;j++){
				vals[i][j]= ts.getVal(ts.getKey(i),j+1);
			}
		}


		for(int i=0;i<ts.getColumns();i++){
			for(int j=i+1;j<ts.getColumns();j++){
				float p=StatLib.pearson(vals[i],vals[j]);
				if(Math.abs(p)>Threshold){

					Point ps[]=toPoints(ts.getValarr(ts.getKey(i)),ts.getValarr(ts.getKey(j)));
					Line lin_reg=StatLib.linear_reg(ps);
					float threshold=Thresholdcalculator(ps,lin_reg)*1.1f; // 10% increase

					CorrelatedFeatures c=new CorrelatedFeatures(ts.getKey(i), ts.getKey(j), p, lin_reg, threshold);

					this.mycor.add(c);
				}
			}
		}
	}


	@Override
	public List<AnomalyReport> detect(TimeSeries ts) {

		for(CorrelatedFeatures c : mycor) {
			float[] x=ts.getValarr(c.feature1);
			float[] y=ts.getValarr(c.feature2);
			for(int i=0;i<x.length;i++){
				if(Math.abs(y[i] - c.lin_reg.f(x[i]))>c.threshold){
					String d=c.feature1 + "-" + c.feature2;
					myreport.add(new AnomalyReport(d,(i+1)));
				}
			}
		}
		return myreport;
	}
	public List<CorrelatedFeatures> getNormalModel(){
		return this.mycor;
	}

	private void isAcossiate(TimeSeries ts,float p,String f1, String f2,Point[] points){
		if (p >= Threshold){

			Line line = StatLib.linear_reg(points);
			float currentThreshold = Thresholdcalculator(points, line);

			CorrelatedFeatures corrFeature = new CorrelatedFeatures(f1,f2,p,line,currentThreshold); // = {f1, f2, p, line, currentThreshold};
			this.mycor.add(corrFeature);
		}

	}
	private float Thresholdcalculator(Point ps[],Line rl){
		float max=0;
		for(int i=0;i<ps.length;i++){
			float d=Math.abs(ps[i].y - rl.f(ps[i].x));
			if(d>max)
				max=d;
		}
		return max;
	}
	private Point[] toPoints(float[] x, float[] y) {
		Point[] ps=new Point[x.length];
		for(int i=0;i<ps.length;i++)
			ps[i]=new Point(x[i],y[i]);
		return ps;
	}
}


