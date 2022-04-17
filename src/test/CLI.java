package test;

import java.util.ArrayList;

import test.Commands.Command;
import test.Commands.DefaultIO;

public class CLI {

	ArrayList<Command> commands;
	DefaultIO dio;
	Commands c;
	
	public CLI(DefaultIO dio) {
		this.dio=dio;
		c=new Commands(dio); 
		commands=new ArrayList<>();
		// example: commands.add(c.new ExampleCommand());
		// implement
	}
	
	public void start() {
		boolean status = false;
		int command;
		commands.add(c.new command1());
		commands.add(c.new command2());
		commands.add(c.new command3());
		commands.add(c.new command4());
		commands.add(c.new command5());
		commands.add(c.new command6());
		while(status != true)
		{
			dio.write("Welcome to the Anomaly Detection Server.\n" +
					"Please choose an option:\n" +
					"1. upload a time series csv file\n" +
					"2. algorithm settings\n" +
					"3. detect anomalies\n" +
					"4. display results\n" +
					"5. upload anomalies and analyze results\n" +
					"6. exit\n");
			command = Math.round(dio.readVal());
			if(command > 5)
			{
				status = true;
			}
			else
			{
				this.commands.get(command -1).execute();
			}
		}
	}
}
