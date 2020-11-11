
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.eclipse.lsp4j.jsonrpc.messages.Either;

import magpiebridge.core.IProjectService;
import magpiebridge.core.MagpieServer;
import magpiebridge.core.ServerAnalysis;
import magpiebridge.core.ServerConfiguration;
import magpiebridge.core.ToolAnalysis;
import magpiebridge.projectservice.java.JavaProjectService;
import task1.FirstAnalysis;
import task2.SecondAnalysis;

public class TutorialMain {

	public static void main(String... args) {
		String preparedFile = args[0];
		// launch on Standard I/O. Note later don't use System.out
		// to print text messages to console, it will block the channel.
	
		//createServer(preparedFile).launchOnStdio();

		// launch on Socket, good for debugging
		MagpieServer.launchOnSocketPort(5007, () -> createServer(preparedFile));
	}

	private static MagpieServer createServer(String preparedFile) {
		//Step 1: Create a MagpieServer and configure it
		
		ServerConfiguration config = setConfig();
		MagpieServer server = new MagpieServer(config);

		//Step 2: Create first analysis and add to server. 
		ServerAnalysis firstAnalysis = new FirstAnalysis(preparedFile);
		String language = "java";
		Either<ServerAnalysis, ToolAnalysis> first = Either.forLeft(firstAnalysis);
		server.addAnalysis(first, language);
				
		//Step 3 (Task 2): Add a project service. 
		IProjectService javaProjectService = new JavaProjectService();
		server.addProjectService(language, javaProjectService);
		
		//Step 4 (Task 2): Create second analysis and add to server.
		ServerAnalysis secondAnalysis = new SecondAnalysis();
		Either<ServerAnalysis, ToolAnalysis> second = Either.forLeft(secondAnalysis);
		server.addAnalysis(second, language);

		return server;
	}

	private static ServerConfiguration setConfig() {
		ServerConfiguration config = new ServerConfiguration();
		try {
			// log the communications
			File traceFile = Files.createTempFile("magpie_server_trace", ".lsp").toFile();
			config.setLSPMessageTracer(new PrintWriter(traceFile));
			
			// Task 1
			config.setDoAnalysisByOpen(true);
			
			// Task 2
			//config.setDoAnalysisBySave(true);
			
			// Task 3
			//config.setDoAnalysisByFirstOpen(false);
			//config.setDoAnalysisBySave(false);
			//config.setShowConfigurationPage(true, true); 
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return config;
	}

}
