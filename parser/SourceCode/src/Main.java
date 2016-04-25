import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.type.ClassOrInterfaceType;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static String inputfolder;
    public static String outputImage;

    public static void main(String[] args) throws Exception {

        inputfolder = args[0];
        outputImage = args[1];
        String folderPath = "./" + inputfolder + "/";
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        List<String> filePaths = getDotJavaFiles(listOfFiles);

        Parser objJavaParser = new Parser();
        for (String filePath : filePaths) {
            FileInputStream in = new FileInputStream(folderPath + filePath);
            CompilationUnit cu = JavaParser.parse(in);
            in.close();
            new Parser.ClassVisitor().visit(cu, null);

            if (objJavaParser.isInterfaceClassVisit) {
                objJavaParser.namesInterface.add(objJavaParser.nameClassVisit);
            } else {
                objJavaParser.namesClass.add(objJavaParser.nameClassVisit);
            }

            objJavaParser = getExtendsClassList(objJavaParser);
            getImplementsInterfaceList(objJavaParser);
        }

        objJavaParser = createUMLFromJavaFile(folderPath, filePaths, objJavaParser);
        objJavaParser.createAssociationStrUML();
        objJavaParser.createExtendStrUML();
        objJavaParser.createInterfaceStrUML();

        umlClassDiagram.umlGenerator(objJavaParser.inpstrClassUML, objJavaParser.inpstrAssociationUML, objJavaParser.inpstrExtendUML, objJavaParser.inpstrInterfaceUML);
    }

    private static Parser getExtendsClassList(Parser objJavaParser) {
        if (Parser.extendClassVisit != null) {
            for (ClassOrInterfaceType item : Parser.extendClassVisit) {
                Parser.ExtendItem newItem = objJavaParser.new ExtendItem();
                newItem.subClassName = Parser.nameClassVisit;
                newItem.superClassName = item.getName();
                Parser.extendList.add(newItem);
            }
        }
        return objJavaParser;
    }

    private static void getImplementsInterfaceList(Parser objJavaParser) {
        if (Parser.implementClassVisit != null) {
            for (ClassOrInterfaceType item : Parser.implementClassVisit) {
                Parser.ImplementInterfaceItem newItem = objJavaParser.new ImplementInterfaceItem();
                newItem.implementName = Parser.nameClassVisit;
                newItem.interfaceName = item.getName();
                Parser.implementInterfaceList.add(newItem);
            }
        }
    }

    private static ArrayList<String> getDotJavaFiles(File[] listOfFiles) {
        ArrayList<String> filePaths = new ArrayList<String>();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String fileStr = file.getName();
                if (fileStr.endsWith(".java") || fileStr.endsWith(".JAVA"))
                    filePaths.add(fileStr);
            }
        }
        return filePaths;
    }

    private static Parser createUMLFromJavaFile(String folderPath, List<String> filePaths, Parser objJavaParser) throws ParseException, IOException {
        for (String filePath : filePaths) {
            FileInputStream in = new FileInputStream(folderPath + filePath);
            CompilationUnit cu = JavaParser.parse(in);
            in.close();

            new Parser.ClassVisitor().visit(cu, null);
            new Parser.FieldVisitor().visit(cu, null);
            new Parser.MethodVisitor().visit(cu, null);
            new Parser.ConstructorVisitor().visit(cu, null);
            new Parser.VariableDecVisitor().visit(cu, null);

            objJavaParser.createClassStrUML();
            objJavaParser.clearTempStaticClass();
        }
        return objJavaParser;
    }

}
