
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
		// launch the server, here we choose stand I/O. Note later don't use System.out
		// to print text messages to console, it will block the channel.
		//createServer(preparedFile).launchOnStdio();

		// for debugging
		MagpieServer.launchOnSocketPort(5007, () -> createServer(preparedFile));
	}

	private static MagpieServer createServer(String preparedFile) {

		ServerConfiguration config = setConfig();
		MagpieServer server = new MagpieServer(config);

		// define which language you consider and add a project service for this
		// language
		String language = "java";
		IProjectService javaProjectService = new JavaProjectService();

		// add your customized analysis
		ServerAnalysis firstAnalysis = new FirstAnalysis(preparedFile);
		server.addProjectService(language, javaProjectService);

		Either<ServerAnalysis, ToolAnalysis> first = Either.forLeft(firstAnalysis);
		server.addAnalysis(first, language);

		ServerAnalysis secondAnalysis = new SecondAnalysis();
		Either<ServerAnalysis, ToolAnalysis> second = Either.forLeft(secondAnalysis);
		server.addAnalysis(second, language);

		return server;
	}

	private static ServerConfiguration setConfig() {
		// set up configuration for MagpieServer
		ServerConfiguration configuration = new ServerConfiguration();
		try {
			//configuration.setDoAnalysisByOpen(true);// first task
			//configuration.setDoAnalysisBySave(true);// second task
			configuration.setDoAnalysisByFirstOpen(false);// third task
			configuration.setDoAnalysisBySave(false);// third task
			 configuration.setShowConfigurationPage(true, true);//third task
			File traceFile = Files.createTempFile("magpie_server_trace", ".lsp").toFile();
			configuration.setLSPMessageTracer(new PrintWriter(traceFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return configuration;
	}

}
