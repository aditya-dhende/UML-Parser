


import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.lang.String;
import java.util.*;


public class Parser {
    static List<String> namesClass;
    static List<String> namesInterface;
    static List<AssociationItem> associationItem;
    static List<ExtendItem> extendList;
    static Set<UseInterfaceItem> useInterfaceList;
    static List<ImplementInterfaceItem> implementInterfaceList;

    static List<String> inpstrClassUML;
    static List<String> inpstrAssociationUML;
    static List<String> inpstrExtendUML;
    static List<String> inpstrInterfaceUML;
    
    
    static String nameClassVisit;
    static List<ClassOrInterfaceType> extendClassVisit;
    static boolean isInterfaceClassVisit;
    static int modifierClassVisitor;
    static List<ClassOrInterfaceType> implementClassVisit;

    
    static List<String> typeMethodVisit;
    static List<List<Parameter>> paramListMethodVisit;
    static List<String> nameMethodVisit;
    static List<Integer> modifierMethodVisit;

    
    static List<String> nameFieldVisit;
    static List<Integer> modifierFieldVisit;
    static List<String> typeFieldVisit;

    
    static List<String> nameConstructorVisit;
    static List<Integer> modifierConstructorVisit;
    static List<List<Parameter>> paramListConstructorVisit;

    
    static ArrayList<String> innerAttributeTypes;

   
    static ArrayList<FieldAccessLocation> fieldAccessVisit;

    
    static ArrayList<SetterGetterLocation> setterGetterLocVisit;
    static ArrayList<String> setGetMethodNameVisit;
    static ArrayList<String> setGetFieldNameVisit;
    static ArrayList<Boolean> setGetIsGetVisit;
    static ArrayList<Boolean> setGetIsSetVisit;


    
    static ArrayList<ReturnStatement> returnStmtVisit;

    class ExtendItem {
        String superClassName;
        String subClassName;
    }

    class AssociationItem {
        String startName;
        String endName;
        String attributeName;
        boolean ifMultiple;
    }

    class UseInterfaceItem {
        String interfaceName;
        String useName;


        @Override
        public int hashCode() {
            int hashcode = 0;
            hashcode = interfaceName.hashCode() * 20;
            hashcode += useName.hashCode();
            return hashcode;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof UseInterfaceItem) {
                UseInterfaceItem item = (UseInterfaceItem) obj;
                return (item.interfaceName.equals(this.interfaceName) && item.useName.equals(this.useName));
            } else {
                return false;
            }
        }
    }

    class FieldAccessLocation {
        String fieldname;
        boolean hasGetter;
        boolean hasSetter;
    }

    class ImplementInterfaceItem {
        String interfaceName;
        String implementName;
    }

    class SetterGetterLocation {
        String methodName;
        String fieldName;
        boolean isSetter;
        boolean isGetter;

    }

    class ReturnStatement {
        String returnName;
        int lineNumber;
    }

    Parser() {
        namesClass = new ArrayList<String>();
        namesInterface = new ArrayList<String>();

        associationItem = new ArrayList<AssociationItem>();
        extendList = new ArrayList<ExtendItem>();
        useInterfaceList = new LinkedHashSet<UseInterfaceItem>();
        implementInterfaceList = new ArrayList<ImplementInterfaceItem>();

        inpstrClassUML = new ArrayList<String>();
        inpstrAssociationUML = new ArrayList<String>();
        inpstrExtendUML = new ArrayList<String>();
        inpstrInterfaceUML = new ArrayList<String>();

        extendClassVisit = new ArrayList<ClassOrInterfaceType>();
        implementClassVisit = new ArrayList<ClassOrInterfaceType>();

        nameMethodVisit = new ArrayList<String>();
        modifierMethodVisit = new ArrayList<Integer>();
        typeMethodVisit = new ArrayList<String>();
        paramListMethodVisit = new ArrayList<List<Parameter>>();

        nameFieldVisit = new ArrayList<String>();
        modifierFieldVisit = new ArrayList<Integer>();
        typeFieldVisit = new ArrayList<String>();

        nameConstructorVisit = new ArrayList<String>();
        modifierConstructorVisit = new ArrayList<Integer>();
        paramListConstructorVisit = new ArrayList<List<Parameter>>();

        innerAttributeTypes = new ArrayList<String>();

        fieldAccessVisit = new ArrayList<FieldAccessLocation>();

        setterGetterLocVisit = new ArrayList<SetterGetterLocation>();
        setGetMethodNameVisit = new ArrayList<String>();
        setGetFieldNameVisit = new ArrayList<String>();
        setGetIsGetVisit = new ArrayList<Boolean>();
        setGetIsSetVisit = new ArrayList<Boolean>();

        returnStmtVisit = new ArrayList<ReturnStatement>();
    }


    
    public static class ClassVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(ClassOrInterfaceDeclaration n, Object arg) {

            nameClassVisit = n.getName();
            isInterfaceClassVisit = n.isInterface();
            extendClassVisit = n.getExtends();
            implementClassVisit = n.getImplements();
            modifierClassVisitor = n.getModifiers();
        }
    }


   
    public static class MethodVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(MethodDeclaration n, Object arg) {
            modifierMethodVisit.add(n.getModifiers());
            nameMethodVisit.add(n.getName());
            typeMethodVisit.add(n.getType().toString());
            paramListMethodVisit.add(n.getParameters());
            checkForGetterOrSetter(n);
        }
    }

    private static void checkForGetterOrSetter(MethodDeclaration n) {
        if (n.getName().toUpperCase().indexOf("SET") >= 0) {
            for (String nameItem : nameFieldVisit)
                if (n.getName().toUpperCase().equals(("set" + nameItem).toUpperCase())) {
                    setGetMethodNameVisit.add(n.getName());
                    setGetFieldNameVisit.add(nameItem);
                    setGetIsGetVisit.add(false);
                    setGetIsSetVisit.add(true);
                }
        } else if (n.getName().toUpperCase().indexOf("GET") >= 0) {
            for (String nameItem : nameFieldVisit) {
                if (n.getName().toUpperCase().equals(("get" + nameItem).toUpperCase())) {
                    setGetMethodNameVisit.add(n.getName());
                    setGetFieldNameVisit.add(nameItem);
                    setGetIsGetVisit.add(true);
                    setGetIsSetVisit.add(false);
                }
            }
        }
    }

    public static class FieldVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(FieldDeclaration n, Object arg) {
            typeFieldVisit.add(n.getType().toString());
            String varName = n.getVariables().get(0).toString();
            if (varName.indexOf("=") >= 0) {
                varName = varName.substring(0, varName.indexOf("="));
            }
            nameFieldVisit.add(varName);
            modifierFieldVisit.add(n.getModifiers());
        }
    }

    public static class ConstructorVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(ConstructorDeclaration n, Object arg) {
            modifierConstructorVisit.add(n.getModifiers());
            nameConstructorVisit.add(n.getName());
            paramListConstructorVisit.add(n.getParameters());
        }
    }

    public static class VariableDecVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(VariableDeclarationExpr n, Object arg) {
            innerAttributeTypes.add(n.getType().toString());
        }
    }


    public void createClassStrUML() {

        for (SetterGetterLocation setterGetterItem : setterGetterLocVisit) {
            System.out.println(setterGetterItem.methodName + " fieldName:" + setterGetterItem.fieldName + " getter:" + setterGetterItem.isGetter + "setter:" + setterGetterItem.isSetter);
        }

        String source = "";
        if (isInterfaceClassVisit) {
            source += "interface " + nameClassVisit + " {\n";
        } else {
            if (ModifierSet.isAbstract(modifierClassVisitor)) {
                source += "abstract class " + nameClassVisit + " {\n";
            } else {
                source += "class " + nameClassVisit + " {\n";
            }
        }

        for (String field : nameFieldVisit) {
            int index = nameFieldVisit.indexOf(field);
            String substr1 = "";
            if (typeFieldVisit.get(index).indexOf('[') >= 0) {
                substr1 += typeFieldVisit.get(index).substring(0, typeFieldVisit.get(index).indexOf('['));
            } else if (typeFieldVisit.get(index).contains("Collection") || typeFieldVisit.get(index).contains("List") || typeFieldVisit.get(index).contains("Map") || typeFieldVisit.get(index).contains("Set")) {
                substr1 += typeFieldVisit.get(index).substring(typeFieldVisit.get(index).indexOf('<') + 1, typeFieldVisit.get(index).indexOf('>'));
            }

            if (namesClass.indexOf(typeFieldVisit.get(index)) >= 0 || namesClass.indexOf(substr1) >= 0
                    || namesInterface.indexOf(typeFieldVisit.get(index)) >= 0 || namesInterface.indexOf(substr1) >= 0) {
                AssociationItem associationItem1 = new AssociationItem();
                associationItem1.startName = nameClassVisit;

                if (substr1 != "")
                    associationItem1.endName = substr1;
                else
                    associationItem1.endName = typeFieldVisit.get(index);

                associationItem1.attributeName = field;

                if (substr1 != "")
                    associationItem1.ifMultiple = true;
                else
                    associationItem1.ifMultiple = false;

                associationItem.add(associationItem1);
            } else {
                String typefieldstr = "";
                if (typeFieldVisit.get(index).indexOf('[') >= 0) {
                    typefieldstr += typeFieldVisit.get(index).substring(0, typeFieldVisit.get(index).indexOf('['));
                    typefieldstr += "(*)";
                } else if (typeFieldVisit.get(index).contains("Collection") || typeFieldVisit.get(index).contains("List") || typeFieldVisit.get(index).contains("Map") || typeFieldVisit.get(index).contains("Set")) {
                    typefieldstr += typeFieldVisit.get(index).substring(typeFieldVisit.get(index).indexOf('<') + 1, typeFieldVisit.get(index).indexOf('>'));
                    typefieldstr += "(*)";
                } else {
                    typefieldstr += typeFieldVisit.get(index);
                }

                if (ModifierSet.isPublic(modifierFieldVisit.get(index))) {
                    source += "+" + field + ":" + typefieldstr + "\n";
                } else if (isFieldHasGetterSetter(field)) {
                    source += "+" + field + ":" + typefieldstr + "\n";
                } else if (ModifierSet.isPrivate(modifierFieldVisit.get(index))) {
                    source += "-" + field + ":" + typefieldstr + "\n";

                }
            }
        }

        source += "__\n";

        for (String methodName : nameConstructorVisit) {
            int index = nameConstructorVisit.indexOf(methodName);
            if (ModifierSet.isPublic(modifierConstructorVisit.get(index))) {
                String parameterStr = "";

                for (Parameter parameterSingle : paramListConstructorVisit.get(index)) {
                    String[] parts = parameterSingle.toString().split(" ");
                    parameterStr += parts[1] + ":" + parameterSingle.getType();
                    if (paramListConstructorVisit.get(index).indexOf(parameterSingle) + 1 != paramListConstructorVisit.get(index).size())
                        parameterStr += ",";
                }

                source += "+" + methodName + "(" + parameterStr + ")" + "\n";
            }

            for (Parameter parameterSingle : paramListConstructorVisit.get(index)) {
                String substr1 = "";
                String paramtertype = parameterSingle.getType().toString();

                if (paramtertype.indexOf('[') >= 0) {
                    substr1 += paramtertype.substring(0, paramtertype.indexOf('['));
                } else if (paramtertype.contains("Collection") || paramtertype.contains("List") || paramtertype.contains("Map") || paramtertype.contains("Set")) {
                    substr1 += paramtertype.substring(paramtertype.indexOf('<') + 1, paramtertype.indexOf('>'));
                } else
                    substr1 += paramtertype;


                for (String interfaceName : namesInterface) {
                    if (interfaceName.equals(substr1)) {
                        UseInterfaceItem useInterfaceItem = new UseInterfaceItem();
                        useInterfaceItem.interfaceName = interfaceName;
                        useInterfaceItem.useName = nameClassVisit;

                        if (namesClass.contains(nameClassVisit))
                            useInterfaceList.add(useInterfaceItem);
                    }
                }
            }
        }

        //C. making method UML String
        for (String methodName : nameMethodVisit) {
            int index = nameMethodVisit.indexOf(methodName);
            if ((ModifierSet.isPublic(modifierMethodVisit.get(index)) || namesInterface.contains(nameClassVisit))
                    && !isMethodGetterSetter(methodName)) {
                String parameterStr = "";

                for (Parameter parameterSingle : paramListMethodVisit.get(index)) {
                    String[] parts = parameterSingle.toString().split(" ");
                    parameterStr += parts[1] + ":" + parameterSingle.getType();
                    if (paramListMethodVisit.get(index).indexOf(parameterSingle) + 1 != paramListMethodVisit.get(index).size())
                        parameterStr += ",";
                }

                source += "+" + methodName + "(" + parameterStr + "):" + typeMethodVisit.get(index) + "\n";
            }


            
            createUserInterfaceList(index);


            String substr1 = "";
            String returntype = typeMethodVisit.get(index);
            if (returntype.indexOf('[') >= 0) {
                substr1 += returntype.substring(0, returntype.indexOf('['));
            } else if (returntype.contains("Collection") || returntype.contains("List") || returntype.contains("Map") || returntype.contains("Set")) {
                substr1 += returntype.substring(returntype.indexOf('<') + 1, returntype.indexOf('>'));
            } else
                substr1 += returntype;

            for (String interfaceName : namesInterface) {
                if (interfaceName.equals(substr1)) {
                    UseInterfaceItem useInterfaceItem = new UseInterfaceItem();
                    useInterfaceItem.interfaceName = interfaceName;
                    useInterfaceItem.useName = nameClassVisit;

                    if (namesClass.contains(nameClassVisit))
                        useInterfaceList.add(useInterfaceItem);
                }
            }
        }
        source += "}\n";

        for (String innervarType : innerAttributeTypes) {
            for (String interfaceName : namesInterface) {
                if (interfaceName.equals(innervarType)) {
                    UseInterfaceItem useInterfaceItem = new UseInterfaceItem();
                    useInterfaceItem.interfaceName = interfaceName;
                    useInterfaceItem.useName = nameClassVisit;

                   
                    if (namesClass.contains(nameClassVisit))
                        useInterfaceList.add(useInterfaceItem);
                }
            }
        }
        inpstrClassUML.add(source);
    }

    private void createUserInterfaceList(int index) {
        for (Parameter parameterSingle : paramListMethodVisit.get(index)) {
            String substr1 = "";
            String paramtertype = parameterSingle.getType().toString();

            if (paramtertype.indexOf('[') >= 0) {
                substr1 += paramtertype.substring(0, paramtertype.indexOf('['));
            } else if (paramtertype.contains("Collection") || paramtertype.contains("List") || paramtertype.contains("Map") || paramtertype.contains("Set")) {
                substr1 += paramtertype.substring(paramtertype.indexOf('<') + 1, paramtertype.indexOf('>'));
            } else
                substr1 += paramtertype;


            for (String interfaceName : namesInterface) {
                if (interfaceName.equals(substr1)) {
                    UseInterfaceItem useInterfaceItem = new UseInterfaceItem();
                    useInterfaceItem.interfaceName = interfaceName;
                    useInterfaceItem.useName = nameClassVisit;

                   
                    if (namesClass.contains(nameClassVisit))
                        useInterfaceList.add(useInterfaceItem);
                }
            }
        }
    }

    
    public void createAssociationStrUML() {
        String source = "";
        while (!associationItem.isEmpty()) {
            String class1 = associationItem.get(0).startName;
            String class2 = associationItem.get(0).endName;

            int i = 0;
            for (; i < associationItem.size(); i++) {
                if (associationItem.get(i).startName.equals(class2) && associationItem.get(i).endName.equals(class1)) {
                    break;
                }
            }
            if (i < associationItem.size()) {
                source = removeAssociationItem(source, class1, class2, i);
            } else {
                if (associationItem.get(0).ifMultiple) {
                    if (associationItem.get(0).endName.toUpperCase().equals(associationItem.get(0).attributeName.toUpperCase())) {
                        source += class1 + " --" + "\"*\" " + class2 + "\n";
                    } else {
                        
                        source += class1 + " --" + "\"*\" " + class2 + "\n";
                    }

                } else {
                   
                    source += class1 + " --" + "\"1\" " + class2 + "\n";
                }
                associationItem.remove(0);
            }


        }

        inpstrAssociationUML.add(source);
    }

    private String removeAssociationItem(String source, String class1, String class2, int i) {
        if (associationItem.get(0).ifMultiple && associationItem.get(i).ifMultiple) {
            source += class1 + " \"*\"" + "--" + "\"*\" " + class2 + "\n";
        } else if (associationItem.get(0).ifMultiple) {
            source += class1 + " \"1\"" + " --" + "\"*\" " + class2 + "\n";
        } else if (associationItem.get(i).ifMultiple) {
            source += class1 + " \"*\"" + "-- " + "\"1\" " + class2 + "\n";
        } else {
            source += class1 + " \"1\"" + " -- " + "\"1\" " + class2 + "\n";
        }
        associationItem.remove(i);
        associationItem.remove(0);
        return source;
    }


    
    public void createExtendStrUML() {
        String source = "";
        for (ExtendItem item : extendList) {
            source += item.superClassName + " <|-- " + item.subClassName + "\n";
        }
        inpstrExtendUML.add(source);
    }

   
    public void createInterfaceStrUML() {
        String source = "";
        for (ImplementInterfaceItem item : implementInterfaceList) {
            source += item.interfaceName + " <|.. " + item.implementName + "\n";
        }

        for (UseInterfaceItem item : useInterfaceList) {
            source += item.useName + " ..> " + item.interfaceName + ": use\n";
        }
        inpstrInterfaceUML.add(source);
    }


    public void clearTempStaticClass() {
        nameMethodVisit.clear();
        modifierMethodVisit.clear();
        typeMethodVisit.clear();
        paramListMethodVisit.clear();

        nameFieldVisit.clear();
        modifierFieldVisit.clear();
        typeFieldVisit.clear();

        nameConstructorVisit.clear();
        modifierConstructorVisit.clear();
        paramListConstructorVisit.clear();
        innerAttributeTypes.clear();
        fieldAccessVisit.clear();
        setterGetterLocVisit.clear();
        setGetMethodNameVisit.clear();
        setGetFieldNameVisit.clear();
        setGetIsGetVisit.clear();
        setGetIsSetVisit.clear();
        returnStmtVisit.clear();

    }


    public boolean isFieldHasGetterSetter(String fieldName) {
        boolean hasSetter = false;
        boolean hasGetter = false;
        for (int i = 0; i < setGetFieldNameVisit.size(); i++) {
            if (setGetFieldNameVisit.get(i).equals(fieldName)) {
                if (setGetIsSetVisit.get(i)) hasSetter = true;
                if (setGetIsGetVisit.get(i)) hasGetter = true;
            }
        }
        if (hasSetter && hasGetter)
            return true;

        return false;
    }

    public boolean isMethodGetterSetter(String methodName) {

        for (String methodItem : setGetMethodNameVisit) {
            if (methodItem.equals(methodName)) {
                int index = setGetMethodNameVisit.indexOf(methodItem);
                if (setGetIsSetVisit.get(index) || setGetIsGetVisit.get(index))
                    return true;
            }
        }
        return false;
    }


}
