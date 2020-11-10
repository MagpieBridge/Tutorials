package task2.taintanalysis;

import java.util.Collection;
import java.util.Set;

import magpiebridge.converter.WalaToSootIRConverter;
import magpiebridge.core.AnalysisResult;
import soot.PackManager;
import soot.Transform;
import soot.options.Options;

public class TaintAnalysisRunner {

	public static Collection<AnalysisResult> doTaintAnalysis(Set<String> srcPath, Set<String> libPath) {
		// Uses the IRConverter to convert WALA IR to Jimple.
		WalaToSootIRConverter converter = new WalaToSootIRConverter(srcPath, libPath, null);
		converter.convert();
		// Create an transformer which performs the actual analysis.
		Options.v().set_ignore_resolving_levels(true);
		SimpleTransformer t = new SimpleTransformer();
		Transform transform = new Transform("jtp.analysis", t);
		PackManager.v().getPack("jtp").add(transform);
		PackManager.v().runBodyPacks();
		Collection<AnalysisResult> results = t.getAnalysisResults();
		return results;
	}
}
