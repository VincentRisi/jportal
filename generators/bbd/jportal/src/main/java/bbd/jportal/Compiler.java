/// ------------------------------------------------------------------
/// Copyright (c) from 1996 Vincent Risi 
///                           
/// All rights reserved. 
/// This program and the accompanying materials are made available 
/// under the terms of the Common Public License v1.0 
/// which accompanies this distribution and is available at 
/// http://www.eclipse.org/legal/cpl-v10.html 
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------

package bbd.jportal;

import bbd.jportal.JPortal;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Compiler {
    private static PrintWriter outLog;
    private static String inputs;
    private static String nubDir;
    private static BufferedReader bufferedReader;

    public static int compile(String source, String args[], PrintWriter outLog)
            throws FileNotFoundException, ClassNotFoundException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        String[] pieces = source.split("\\+");
        Database database = null;
        boolean hasErrors = false;
        for (int p = 0; p < pieces.length; p++) {
            Database db = JPortal.run(pieces[p], nubDir, outLog);
            if (db == null) {
                outLog.println("::>" + pieces[p] + "<:: compile has errors");
                outLog.flush();
                hasErrors = true;
                continue;
            }
            if (database == null)
                database = db;
            else {
                for (int t = 0; t < db.tables.size(); t++)
                    database.tables.addElement(db.tables.elementAt(t));
                for (int s = 0; s < db.sequences.size(); s++)
                    database.sequences.addElement(db.sequences.elementAt(s));
                for (int v = 0; v < db.views.size(); v++)
                    database.views.addElement(db.views.elementAt(v));
            }
        }
        if (hasErrors == true)
            return 1;
        outLog.flush();
        String outputDirectory = "";
        Boolean useOldStyleGenerators = true;
        for (int i = 0; i < args.length; i++) {

            if (args[i].equals("-V2")) {
                //The old command-line arguments are a bit limited. V2 supports a new type of Generator,
                //which has better command-line capabilities
                useOldStyleGenerators = Boolean.FALSE;
                break;
            }

            if (args[i].equals("-o")) {
                if (i + 1 < args.length) {
                    outputDirectory = args[++i];
                    char term = '\\';
                    if (outputDirectory.indexOf('/') != -1)
                        term = '/';
                    char ch = outputDirectory.charAt(outputDirectory.length() - 1);
                    if (ch != term)
                        outputDirectory = outputDirectory + term;
                }
                continue;
            } else if (args[i].equals("-l")) {
                if (i + 1 < args.length) {
                    String log = args[++i];
                    OutputStream outFile = new FileOutputStream(log);
                    outLog.flush();
                    outLog = new PrintWriter(outFile);
                }
                continue;
            } else if (args[i].equals("-f")) {
                if (i + 1 < args.length) {
                    String flag = args[++i];
                    database.flags.addElement(flag);
                }
                continue;
            }
        }

        if (useOldStyleGenerators == Boolean.TRUE) {
            //Use old style generators
            outLog.println(args[i]);
            String generatorName = args[i];

            Class<?> c = Class.forName("bbd.jportal." + args[i]);
            Class<?> d[] = {database.getClass(), outputDirectory.getClass(), outLog.getClass()};
            Method m = c.getMethod("generate", d);
            Object o[] = {database, outputDirectory, outLog};
            m.invoke(database, o);
            outLog.flush();
            return 0;
        }


        outLog.println("Using V2 generators...");
        String templateDir = "";
        if (generatorName.indexOf(':') > 0) {
            //If the generatorName is of format generatorName:template,
            // set the template and generator fields
            templateDir = generatorName.substring(generatorName.indexOf(':'));
            generatorName = generatorName.substring(0, generatorName.indexOf(':'));
            outLog.println("Generator Name: " + generatorName);
            outLog.println("Template Name: " + templateDir);
        }

        Map<String, String> params = new HashMap<>();
        params.put("TemplateDir", templateDir);
        File outputDirFH = new File(outputDirectory);
        Class<?> da[] = {database.getClass(), params.getClass(), outputDirFH.getClass(), outLog.getClass()};
        Method ma = c.getMethod("generateAdvanced", da);

        Object o[] = {database, params, outputDirFH, outLog};
        ma.invoke(database, o);
    }

    private static String[] frontSwitches(String[] args) throws IOException {
        String log = "";
        int i = 0;
        while (true) {
            if (args.length > i && args[i].equals("-l")) {
                if (i + 1 < args.length) {
                    log = args[++i];
                    OutputStream outFile = new FileOutputStream(log);
                    outLog.flush();
                    outLog = new PrintWriter(outFile);
                }
                i++;
                continue;
            }
            if (args.length > i && args[i].equals("-n")) {
                if (i + 1 < args.length)
                    nubDir = args[++i];
                i++;
                continue;
            }
            if (args.length > i && args[i].equals("-f")) {
                if (i + 1 < args.length) {
                    String fileName = args[++i];
                    FileReader fileReader = new FileReader(fileName);
                    bufferedReader = new BufferedReader(fileReader);
                    try {
                        String semicolon = inputs.length() > 0 ? ";" : "";
                        while (bufferedReader.ready()) {
                            String line = bufferedReader.readLine();
                            inputs = inputs + semicolon + line;
                            semicolon = ";";
                        }
                    } catch (NullPointerException e2) {
                    }
                }
                i++;
                continue;
            }
            break;
        }
        if (args.length > i && inputs.length() == 0) {
            inputs = args[i];
            i++;
        }
        String[] newargs = new String[args.length - i];
        System.arraycopy(args, i, newargs, 0, newargs.length);
        return newargs;
    }

    private static String abbreviate(String inputs) {
        String[] sources = inputs.split(";");
        if (sources.length > 5)
            return sources[0] + " ... " + sources[sources.length - 1];
        return inputs;
    }

    /**
     * Reads input from stored repository
     */
    public static void main(String args[]) {
        try {
            outLog = new PrintWriter(System.out);
            inputs = "";
            nubDir = "";
            args = frontSwitches(args);
            System.out.println(abbreviate(inputs));
            if (args.length < 1) {
                outLog.println("usage java jportal.Compiler -l log -n nubDir (- f inputs | infile) (generators)+");
                outLog.println("for example to create DDL for Sql Server and Java, VB and Delphi code");
                outLog.println();
                outLog.println(
                        "java jportal.Compiler airline.si -o ./dir1 MSSqlDDL -o ./dir2 JavaCode -o ./dir3 VBCode -o ./dir4 DelphiCode");
                outLog.flush();
                System.exit(1);
            }
            outLog.println(inputs);
            outLog.flush();
            String[] sources = inputs.split(";");
            for (int f = 0; f < sources.length; f++) {
                int rc = compile(sources[f], args, outLog);
                outLog.flush();
                if (rc != 0)
                    System.exit(1);
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            outLog.println("Error: " + e);
            outLog.flush();
            System.exit(1);
        }
    }
}
