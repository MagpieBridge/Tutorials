package task1;

import java.util.ArrayList;

import org.eclipse.lsp4j.DiagnosticSeverity;

import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.util.collections.Pair;

import magpiebridge.core.AnalysisResult;
import magpiebridge.core.Kind;
import magpiebridge.util.SourceCodeReader;

public class FirstResult implements AnalysisResult {

	private final Kind kind;
	private final Position position;
	private final String message;
	private final Iterable<Pair<Position, String>> related;
	private final DiagnosticSeverity severity;
	private final Pair<Position, String> repair;
	private final String code;

	public FirstResult(final JsonResult result, final Position pos) {
		this.kind = Kind.Diagnostic;
		this.severity = DiagnosticSeverity.Error;
		this.message = result.msg;
		this.position = pos;
		this.related = new ArrayList<Pair<Position, String>>();
		this.repair = Pair.make(pos, result.repair);
		try {
			code = SourceCodeReader.getLinesInString(pos);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public Kind kind() {
		return this.kind;
	}

	public Position position() {
		return position;
	}

	public Iterable<Pair<Position, String>> related() {
		return related;
	}

	public DiagnosticSeverity severity() {
		return severity;
	}

	public Pair<Position, String> repair() {
		return repair;
	}

	public String toString(boolean useMarkdown) {
		return message;
	}

	@Override
	public String toString() {
		return "Result [kind=" + kind + ", position=" + position + ", code=" + code + ", message=" + message
				+ ", related=" + related + ", severity=" + severity + ", repair=" + repair + "]";
	}

	@Override
	public String code() {
		return code;
	}
}
