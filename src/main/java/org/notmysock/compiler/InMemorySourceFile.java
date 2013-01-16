package org.notmysock.compiler;
import javax.tools.*;
import javax.tools.JavaFileObject.Kind;
import java.net.*;

public class InMemorySourceFile extends SimpleJavaFileObject {
	private String content;
	public InMemorySourceFile(String className, String content) {
		super(URI.create("string:///" + className.replace('.', '/')
				+ Kind.SOURCE.extension), Kind.SOURCE);
		this.content = content;
	}
	@Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return content;
    }
}
