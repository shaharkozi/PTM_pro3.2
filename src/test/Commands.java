package test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Commands {

	// Default IO interface
	public interface DefaultIO{
		public String readText();
		public void write(String text);
		public float readVal();
		public void write(float val);

		// you may add default methods here
	}

	// the default IO to be used in all commands
	DefaultIO dio;
	public Commands(DefaultIO dio) {
		this.dio=dio;
	}

	// you may add other helper classes here



	// the shared state of all commands
	private class SharedState{
		TimeSeries train;
		TimeSeries test;
		int trainlinenum;
		SimpleAnomalyDetector anomalyDet;
	}

	private  SharedState sharedState=new SharedState();


	// Command abstract class
	public abstract class Command{
		protected String description;

		public Command(String description) {
			this.description=description;
		}

		public abstract void execute();
	}

	// Command class for example:
	public class command1 extends Command{

		public command1() {
			super("Please upload your local train CSV file.\n");
		}

		@Override
		public void execute() {
			int status = 0;
			String path = new String();
			sharedState.trainlinenum = -1;
			while(status < 2)
			{
				dio.write(description);
				path = dio.readText();
				String line ="";
				try (BufferedWriter bw = new BufferedWriter(new FileWriter("clientTrain.csv"))) {
					while (!((line = dio.readText()).equals("done"))) {
						if(!(line.equals("done")))
							bw.write(line);
						bw.write("\n");
						sharedState.trainlinenum++;
					}
				}
				catch (IOException e){

				}
				if(line.equals("done"))
				{
					dio.write("Upload complete.\n");
					status++;
				}
				dio.write("Please upload your local test CSV file.\n");
				try (BufferedWriter bw = new BufferedWriter(new FileWriter("clientTest.csv"))) {
					while (!((line = dio.readText()).equals("done"))) {
						if(!(line.equals("done"))) {
							bw.write(line);
							bw.write("\n");
						}
					}
				}
				catch (IOException e){

				}
				if(line.equals("done"))
				{
					dio.write("Upload complete.\n");
					status++;
				}
				status++;
			}
			sharedState.train = new TimeSeries("clientTrain.csv");
			sharedState.test = new TimeSeries("clientTest.csv");
			sharedState.trainlinenum--;
		}
	}
	public class command2 extends Command{

		public command2() {
			super("The current correlation threshold is 0.9\n");
		}

		@Override
		public void execute() {
			sharedState.anomalyDet = new SimpleAnomalyDetector();
			float thresh;
			boolean status = false;
			while(status != true)
			{
				dio.write(description);
				dio.write("Type a new threshold\n");
				thresh = dio.readVal();
				if(thresh > 1 || thresh < 0)
				{
					dio.write("please choose a value between 0 and 1.\n");
				}
				else{
					sharedState.anomalyDet.setThreshold(thresh);
					status = true;
				}
			}
		}
	}
	public class command3 extends Command{

		public command3() {
			super("anomaly detection complete.\n");
		}

		@Override
		public void execute() {
			sharedState.anomalyDet.learnNormal(sharedState.train);
			sharedState.anomalyDet.detect(sharedState.test);
			dio.write(description);
		}
	}
	public class command4 extends Command{

		public command4() {
			super("Done.\n");
		}

		@Override
		public void execute() {
			int size = sharedState.anomalyDet.myreport.size();
			int i = 0;
			while(i< size)
			{
				dio.write(sharedState.anomalyDet.myreport.get(i).timeStep + "\t " + sharedState.anomalyDet.myreport.get(i).description + "\n");
				i++;
			}
			dio.write(description);
		}
	}
	public class command5 extends Command{

		public command5() {
			super("Please upload your local anomalies file.\n" +
					"Upload complete.\n");
		}

		@Override
		public void execute() {

			String line = dio.readText();
			int P = 0;
			int FP = 0;
			int TP = 0;
			int FN = 0;
			int TN = 0;
			int i = 0;
			int j = 1;
			long start;
			long end = -1;
			ArrayList<String> alarms = new ArrayList<>();
			ArrayList<String> exep = new ArrayList<>();
			dio.write(description);
			while(i< sharedState.anomalyDet.myreport.size())
			{
				start = sharedState.anomalyDet.myreport.get(i).timeStep;
				while((j<sharedState.anomalyDet.myreport.size()) && (sharedState.anomalyDet.myreport.get(i).description.equals(sharedState.anomalyDet.myreport.get(j).description)))
				{
					end = sharedState.anomalyDet.myreport.get(j).timeStep;
					j++;
				}
				alarms.add(start + "," + end);
				i = j;
				j++;
			}
			String tmp[] = new String[2];
			int intmp[] = new int[2];
			String time[] = new String[2];
			int diftime[] = new int[2];
			int dif;
			boolean flag;
			int N = sharedState.trainlinenum;
			while (!(line = dio.readText()).equals("done")) {
				exep.add(line);
				tmp = line.split(",");
				intmp[0] = Integer.parseInt(tmp[0]);
				intmp[1] = Integer.parseInt(tmp[1]);
				dif = intmp[1] - intmp[0];
				dif++;
				N -= dif;
				P++;
			}
			for(i = 0; i< alarms.size();i++) {
				flag = false;
				time = alarms.get(i).split(",");
				diftime[0] = Integer.parseInt(time[0]);
				diftime[1] = Integer.parseInt(time[1]);
				for(j = 0; j<exep.size();j++) {
					tmp = exep.get(j).split(",");
					intmp[0] = Integer.parseInt(tmp[0]);
					intmp[1] = Integer.parseInt(tmp[1]);
						if((isBetween(diftime[0],diftime[1],intmp[0],intmp[1])))
						{
							flag = true;
							break;
						}
					}
				if(!flag)
				{
					FP++;
				}
			}
			for(j = 0; j<exep.size();j++) {
				flag = false;
				tmp = exep.get(j).split(",");
				intmp[0] = Integer.parseInt(tmp[0]);
				intmp[1] = Integer.parseInt(tmp[1]);
				for(i = 0; i< alarms.size();i++) {
					time = alarms.get(i).split(",");
					diftime[0] = Integer.parseInt(time[0]);
					diftime[1] = Integer.parseInt(time[1]);
						if((isTruePos(diftime[0],diftime[1],intmp[0],intmp[1])))
						{
							flag = true;
							break;
						}
					}
				if(flag)
				{
					TP++;
				}
			}
			float truePRate = (float) TP/P;
			float falseARate = (float) FP/N;
			truePRate = (float)((int)( truePRate *1000f ))/1000f;
			falseARate = (float)((int)( falseARate *1000f ))/1000f;
			dio.write("True Positive Rate: " + truePRate + "\n");
			dio.write("False Positive Rate: " + falseARate + "\n");
		}
	}
	public class command6 extends Command{

		public command6() {
			super("this is an example of command");
		}

		@Override
		public void execute() {
			dio.write(description);
		}
	}
	boolean isBetween(int num1,int num2, int bet1,int bet2)
	{
		if ((num1 >= bet1 && num1 <= bet2) ||
				(num2 >= bet1 && num2 <= bet2) ||
				(num1 <= bet1 && num2 >= bet2)){
			return true;
		}
		return false;
	}
	boolean isTruePos(int num1,int num2, int bet1,int bet2)
	{
		if ((bet1 >= num1 && bet1 <= num2) ||
				(bet2 >= num1 && bet2 <= num2) ||
				(num1 <= bet1 && num2 >= bet2) || (bet1 <= num1 && bet2 >= num2)){
			return true;
		}
		return false;
	}

}
