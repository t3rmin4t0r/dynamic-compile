package org.notmysock.compiler;
import javax.tools.*;
import java.util.*;

public class DynamicCompiler {
  public static void main(String[] args) throws Exception {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    InMemoryFileManager manager = new InMemoryFileManager(compiler
            .getStandardFileManager(null, null, null));
    List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
   
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
    
    jfiles.add(new InMemorySourceFile("org.notmysock.dynamic.OuterClass", sb.toString()));
    
    compiler.getTask(null, manager, null, null,null, jfiles).call();    

    HashMap<String,InMemoryClassFile> classes = manager.getClasses();

    for(Map.Entry pair: classes.entrySet()) {
      InMemoryClassFile klass = (InMemoryClassFile)pair.getValue();      
      System.out.printf("%s, size=%d\n", ((String)pair.getKey()).replace('.','/'), klass.getBytes().length);
    }
  }
}
