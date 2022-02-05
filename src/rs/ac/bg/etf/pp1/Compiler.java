package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.util.Log4JUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Compiler {

    static {
        DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
        Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
    }

    public static void main(String[] args) throws Exception {
        Logger log = Logger.getLogger(Compiler.class);
        if (args.length < 2) {
            log.error("Not enough arguments supplied! Usage: MJParser <source-file> <obj-file> ");
            return;
        }

        File sourceCode = new File(args[0]);
        if (!sourceCode.exists()) {
            log.error("Source file [" + sourceCode.getAbsolutePath() + "] not found!");
            return;
        }

        log.info("Compiling source file: " + sourceCode.getAbsolutePath());

        System.out.println("=====================LEXICAL AND SYNTAX ANALYSIS=========================");
        try (BufferedReader br = new BufferedReader(new FileReader(sourceCode))) {
            Yylex lexer = new Yylex(br);
            MJParser p = new MJParser(lexer);
            Symbol s = p.parse();

            if (!p.errorOccured()) {
                log.info("");
                Program prog = (Program)(s.value);

                System.out.println("=====================SEMANTIC ANALYSIS=========================");
                System.out.println(prog.toString(" "));

                SymbolTable.init();
                SemanticAnalyzer semanticCheck = new SemanticAnalyzer();
                prog.traverseBottomUp(semanticCheck);

                System.out.println("=====================SYMBOL TABLE CONTENTS=========================");
                SymbolTable.dump();

//                if (!semanticCheck.errorOccured()) {
//
//                    System.out.println("=====================CODE GENERATION=========================");
//
//                    CodeGenerator codeGenerator = new CodeGenerator();
//                    prog.traverseBottomUp(codeGenerator);
//
//                    File objectFile = new File(args[1]);
//                    if (objectFile.exists()) {
//                        objectFile.delete();
//                    }
//
//                    FileOutputStream fileOutputStream = new FileOutputStream(objectFile);
//                    Code.dataSize = codeGenerator.getDataSize();
//                    Code.mainPc = codeGenerator.getMainPc();
//                    Code.write(fileOutputStream);
//                    fileOutputStream.close();
//                }
            }
        }
    }
}
