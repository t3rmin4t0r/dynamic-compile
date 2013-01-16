package org.notmysock.compiler;

import javax.tools.*;
import java.io.*;
import java.util.HashMap;
import java.security.*;
import javax.tools.JavaFileObject.Kind;

public class InMemoryFileManager extends ForwardingJavaFileManager {
	
	private HashMap<String,InMemoryClassFile> classes = new HashMap<String, InMemoryClassFile>();
	
	public InMemoryFileManager(StandardJavaFileManager standardManager) {
		super(standardManager);
	}
	
	public HashMap<String, InMemoryClassFile> getClasses() {
		return classes;
	}
	
	@Override
	public JavaFileObject getJavaFileForOutput(Location location,
			String className, Kind kind, FileObject sibling) throws IOException {
		InMemoryClassFile klass = new InMemoryClassFile(className, kind);
		classes.put(className, klass);
		return klass;
	}
	
	@Override
    public ClassLoader getClassLoader(Location location) {
        return new SecureClassLoader() {
            @Override
            protected Class<?> findClass(String name)
                throws ClassNotFoundException {
            	InMemoryClassFile jclass = classes.get(name);
                byte[] b = jclass.getBytes();
                return super.defineClass(name, b, 0, b.length);
            }
        };
    }
}
