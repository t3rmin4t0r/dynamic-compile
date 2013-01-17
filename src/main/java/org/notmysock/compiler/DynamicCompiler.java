package org.notmysock.compiler;
import javax.tools.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;

public class DynamicCompiler {
  public static void main(String[] args) throws Exception {
   
    StringBuilder sb = new StringBuilder();
    sb.append("package org.notmysock.dynamic;\n");
    sb.append("import org.apache.hadoop.conf.*;\n");
    sb.append("import org.apache.hadoop.fs.*;\n");
    sb.append("import org.apache.hadoop.hdfs.*;\n");
    sb.append("import org.apache.hadoop.io.*;\n");
    sb.append("import org.apache.hadoop.util.*;\n");
    sb.append("import org.apache.hadoop.mapreduce.*;\n");
    sb.append("import org.apache.hadoop.mapreduce.lib.input.*;\n");
    sb.append("import org.apache.hadoop.mapreduce.lib.output.*;\n");
    sb.append("import org.apache.hadoop.mapreduce.lib.reduce.*;\n");
    sb.append("import java.io.*;\n");

    sb.append("public class OuterClass { \n");
    sb.append("  public static class SampleMapper extends Mapper<LongWritable,Text, Text, IntWritable> {\n");
    sb.append("    protected void map(LongWritable offset, Text value, Mapper.Context context) throws IOException, InterruptedException {\n");
    sb.append("      context.write(value, new IntWritable(1));\n");
    sb.append("    }\n");
    sb.append("  }\n");
    sb.append("}\n");

    File jarFile = getJarFile("org.notmysock.dynamic.OuterClass", sb.toString());

    System.out.println("Written to " + jarFile);
  }

  public static File getJarFile(String className, String classSource) throws IOException {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    InMemoryFileManager manager = new InMemoryFileManager(compiler
            .getStandardFileManager(null, null, null));

    List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
    jfiles.add(new InMemorySourceFile(className, classSource));

    compiler.getTask(null, manager, null, null,null, jfiles).call();

    HashMap<String,InMemoryClassFile> classes = manager.getClasses();

    if(classes.isEmpty()) {
      return null;
    }

    for(Map.Entry pair: classes.entrySet()) {
      InMemoryClassFile klass = (InMemoryClassFile)pair.getValue();
      System.out.printf("%s, size=%d\n", ((String)pair.getKey()).replace('.','/'), klass.getBytes().length);
    }

    File tempjar = File.createTempFile("dyncompile", ".jar"); 
    JarOutputStream jar = new JarOutputStream(new FileOutputStream(tempjar));

    for(Map.Entry pair: classes.entrySet()) {
      InMemoryClassFile klass = (InMemoryClassFile)pair.getValue();
      String path = ((String)pair.getKey()).replace('.','/')+(".class");      
      JarEntry entry = new JarEntry(path);
      jar.putNextEntry(entry);
      byte[] bytecode = klass.getBytes();
      jar.write(bytecode, 0, bytecode.length);
      jar.closeEntry();
    }

    jar.close();
    return tempjar;
  }
}
