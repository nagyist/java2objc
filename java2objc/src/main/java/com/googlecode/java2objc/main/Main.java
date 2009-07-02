/*
 * Copyright (C) 2009 Inderjeet Singh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.java2objc.main;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;

import com.googlecode.java2objc.objc.ObjcType;
import com.googlecode.java2objc.objc.SourceCodeWriter;
import com.googlecode.java2objc.util.Preconditions;

public class Main {

  private static class Config {
    private String outputDir = null;

    public void update(String arg) {
      Preconditions.assertTrue(arg.startsWith("--"));
      String[] parts = arg.split("=");
      String name = parts[0];
      String value = parts[1];
      if (name.equals("--outputdir")) {
        outputDir = value;
      }
    }

    public static String availableOptions() {
      return "--outputdir=/tmp";
    }    
  }

  private static void printUsageAndExit() {
    System.err.printf("Usage: java -jar %s java2objc.jar /path/to/MyClass1.java /path/to/second/MyClass2.java\n",
        Config.availableOptions());
    System.exit(-1);
  }

  private final Config config;
  private final Collection<String> javaFiles;
  
  public Main(Config config, Collection<String> javaFiles) {
    this.config = config;
    this.javaFiles = javaFiles;
  }

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      printUsageAndExit();
    }
    Config config = new Config();
    Collection<String> javaFiles = new LinkedList<String>();
    for (String arg : args) {
      if (arg.startsWith("--")) {
        // A configuration setting
        config.update(arg);
      } else { // Probably a Java file
        javaFiles.add(arg);
      }
    }
    Main main = new Main(config, javaFiles);
    main.execute();
  }
  
  public void execute() throws IOException, ParseException {
    for (String javaFileName : javaFiles) { 
      Preconditions.assertTrue(javaFileName.endsWith(".java"));
      FileInputStream in = new FileInputStream(javaFileName);
      parseJavaFileAndWriteObjcType(in);
    }
  }

  private void parseJavaFileAndWriteObjcType(InputStream in) throws ParseException, IOException {
    SourceCodeWriter headerWriter = null;
    SourceCodeWriter implWriter = null;
    try {
      CompilationUnit cu = JavaParser.parse(in);
      TranslateVisitor translateVisitor = new TranslateVisitor();
      GeneratorContext context = new GeneratorContext();
      translateVisitor.visit(cu, context);
      ObjcType currentType = context.getCurrentType();
      File headerFile = new File(config.outputDir, currentType.getHeaderFileName());
      headerWriter = new SourceCodeWriter(new PrintWriter(new FileOutputStream(headerFile)), true);
      headerWriter.append(currentType);
      File implFile = new File(config.outputDir, currentType.getImplFileName());
      implWriter = new SourceCodeWriter(new PrintWriter(new FileOutputStream(implFile)), false);
      implWriter.append(currentType);
    } finally {
      if (in != null) {
        in.close();
      }
      headerWriter.close();
      implWriter.close();
    }
  }
}
