package org.openstack.console;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import jline.UnsupportedTerminal;
import jline.console.ConsoleReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;

public class Console {
	
	private Properties properties;
	
	private ConsoleReader reader;

	private Environment environment;
	
	private static final CommandLineParser PARSER = new GnuParser();
	
	public Console(Environment environment, Properties properties) {
		this.properties = properties;
		this.environment = environment;
	}
	
	public void start() throws IOException {
		if(System.console() == null) {
			reader = new ConsoleReader(System.in, System.out, new UnsupportedTerminal());
		} else {
			reader = new ConsoleReader();
		}
		do {
			try {
				String line = reader.readLine(environment.getPrompt());
				execute(line);
			} catch (Exception pe) {
				pe.printStackTrace();
				System.err.println(pe.getMessage());
			}
		} while(true);
	}
	
	public void execute(String line) throws ParseException {
		String[] tokens = CommandLineHelper.parse(line);
		Command command = environment.commands.get(tokens[0]);
		if(command != null) {
			CommandLine args = Console.PARSER.parse(command.getOptions(), Arrays.copyOfRange(tokens, 1, tokens.length));
			command.execute(this, args);
		}
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	
	public Environment getEnvironment() {
		return this.environment;
	}
	
	/**
	 * @return the properties
	 */
	public String getProperty(String name) {
		return properties.getProperty(name);
	}
	
	/**
	 * @return the properties
	 */
	public void setProperty(String name, Object value) {
		properties.put(name, value);
	}
	
	public void properties() {
		for(Map.Entry<Object, Object> entry : properties.entrySet()) {
			System.out.printf("%25s = %55s",entry.getKey(), entry.getValue());
		}
	}

	public void exit() {
		if(environment.parent == null) {
			System.out.println("Goodbye");
			System.exit(1);
		} else {
			environment = environment.parent;
		}
	}

}
