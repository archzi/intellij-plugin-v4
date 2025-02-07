package org.antlr.intellij.plugin;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.util.ThrowableRunnable;
import com.intellij.util.lang.CompoundRuntimeException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class TestUtils {

	public static void releaseEditorIfNotDisposed(Editor editor) {
		if (!editor.isDisposed()) EditorFactory.getInstance().releaseEditor(editor);
	}

	public static void tearDownIgnoringObjectNotDisposedException(ThrowableRunnable<Exception> delegate) throws Exception {
		try {
			delegate.run();
		} catch (RuntimeException e) {
			// We don't want to release the editor in the Tool Output tool window, so we ignore
			// ObjectNotDisposedExceptions related to this particular editor
			if ( e.getClass().getName().startsWith("com.intellij.openapi.util.TraceableDisposable$")
					|| e instanceof CompoundRuntimeException ) {
				StringWriter stringWriter = new StringWriter();
				e.printStackTrace(new PrintWriter(stringWriter));
				String stack = stringWriter.toString();
				System.out.println(e.getStackTrace()[0].getClassName());
				if ( stack.contains("ANTLRv4PluginController.createToolWindows") ||

						stack.contains("Issue559") || stack.contains("Issue540") || stack.contains("Issue569") ||
						stack.contains("org.antlr.intellij.plugin.preview.InputPanel.createPreviewEditor") ) {
					return;
				}
			}

			throw e;
		}
	}
}


