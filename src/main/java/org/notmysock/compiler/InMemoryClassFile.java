package org.notmysock.compiler;

import javax.tools.*;
import java.io.*;
import java.net.*;
import javax.tools.JavaFileObject.Kind;

public class InMemoryClassFile extends SimpleJavaFileObject {
	protected final ByteArrayOutputStream bos = new ByteArrayOutputStream();

	public InMemoryClassFile(String name, Kind kind) {
		super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
	}
	
	@Override
  public OutputStream openOutputStream() throws IOException {
    return bos;
  }
	
  public byte[] getBytes() {
    return bos.toByteArray();
  }

}
