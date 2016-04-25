import net.sourceforge.plantuml.SourceStringReader;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.String;
import java.util.Collection;


public class umlClassDiagram {
    public static void umlGenerator(Collection<String> classStrUML, Collection<String> associationStrUML, Collection<String> extendStrUML, Collection<String> ballsocketStrUML) throws Exception {

        OutputStream png = new FileOutputStream(Main.outputImage);
        String source = "@startuml\n";
        source += "skinparam classAttributeIconSize 0\n";
        source += "skinparam usecaseBackgroundColor #A80036\n";
        source += "skinparam usecaseBorderColor Transparent\n";
        source += "skinparam usecaseFontSize 1\n";
        source += "skinparam usecaseFontColor #A80036\n";

        source = getSources(classStrUML, source);
        source = getSources(associationStrUML, source);
        source = getSources(extendStrUML, source);
        source = getSources(ballsocketStrUML, source);
        source += "@enduml\n";

        SourceStringReader reader = new SourceStringReader(source);
        reader.generateImage(png);
    }

    private static String getSources(Collection<String> associationStrUML, String source) {
        for (String item : associationStrUML) {
            source += item;
        }
        return source;
    }
}
