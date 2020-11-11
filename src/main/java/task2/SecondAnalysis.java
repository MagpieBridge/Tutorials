package task2;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;

import com.ibm.wala.classLoader.Module;

import magpiebridge.core.AnalysisConsumer;
import magpiebridge.core.AnalysisResult;
import magpiebridge.core.IProjectService;
import magpiebridge.core.MagpieServer;
import magpiebridge.core.ServerAnalysis;
import magpiebridge.core.analysis.configuration.ConfigurationAction;
import magpiebridge.core.analysis.configuration.ConfigurationOption;
import magpiebridge.core.analysis.configuration.OptionType;
import magpiebridge.projectservice.java.JavaProjectService;
import task2.taintanalysis.TaintAnalysisRunner;

/**
 * 
 * @author Linghui Luo
 *
 */
public class SecondAnalysis implements ServerAnalysis {
	private Set<String> srcPath;
	private Set<String> libPath;
	private MagpieServer magpieServer;

	public SecondAnalysis() {

	}

	@Override
	public String source() {
		return "Second Analysis";
	}

	@Override
	public void analyze(Collection<? extends Module> files, AnalysisConsumer server, boolean rerun) {
		if (magpieServer == null)
			magpieServer = (MagpieServer) server;
		if (rerun) {
			setClassPath(magpieServer);
			Collection<AnalysisResult> results = Collections.emptyList();
			if (srcPath != null) {
				results = TaintAnalysisRunner.doTaintAnalysis(srcPath, libPath);
			}
			magpieServer.consume(results, source());
		}
	}

	/**
	 * set up source code path and library path with the project service provided by
	 * the server.
	 *
	 * @param server
	 */
	public void setClassPath(MagpieServer server) {
		if (srcPath == null) {
			Optional<IProjectService> opt = server.getProjectService("java");
			if (opt.isPresent()) {
				JavaProjectService ps = (JavaProjectService) server.getProjectService("java").get();
				Set<Path> sourcePath = ps.getSourcePath();
				if (libPath == null) {
					libPath = new HashSet<>();
					ps.getLibraryPath().stream().forEach(path -> libPath.add(path.toString()));
				}
				if (!sourcePath.isEmpty()) {
					Set<String> temp = new HashSet<>();
					sourcePath.stream().forEach(path -> temp.add(path.toString()));
					srcPath = temp;
				}
			}
		}
	}

	@Override
	public List<ConfigurationOption> getConfigurationOptions() {
		ConfigurationOption op1 = new ConfigurationOption("anotherSetting1", OptionType.checkbox);
		ConfigurationOption op2 = new ConfigurationOption("anothersetting2", OptionType.checkbox);
		List<ConfigurationOption> options = new ArrayList<ConfigurationOption>();
		options.add(op1);
		options.add(op2);
		return options;
	}

	@Override
	public List<ConfigurationAction> getConfiguredActions() {
		List<ConfigurationAction> actions = new ArrayList<ConfigurationAction>();
		ConfigurationAction clearWarnings = new ConfigurationAction("Clear Warnings", () -> {
			final String msg = source() + " warnings are cleared. ";
			this.magpieServer.forwardMessageToClient(new MessageParams(MessageType.Info, msg));
			this.magpieServer.cleanUp();
		});
		actions.add(clearWarnings);
		return actions;
	}

	@Override
	public void configure(List<ConfigurationOption> configuration) {
		// TODO Configure your analysis.
		ServerAnalysis.super.configure(configuration);
	}

	@Override
	public void cleanUp() {
		// TODO clean up when server is shutting down.
		ServerAnalysis.super.cleanUp();
	}

}
