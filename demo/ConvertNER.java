import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.util.StringUtils;

import java.io.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bitbucket.eunjeon.seunjeon.Analyzer;
import org.bitbucket.eunjeon.seunjeon.LNode;

/** This is a demo of calling CRFClassifier programmatically.
 *  <p>
 *  Usage: <code> java -cp "stanford-ner.jar:." NERDemo [serializedClassifier [fileName]]</code>
 *  <p>
 *  If arguments aren't specified, they default to
 *  ner-eng-ie.crf-3-all2006.ser.gz and some hardcoded sample text.
 *  <p>
 *  To use CRFClassifier from the command line:
 *  java -mx400m edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier
 *      [classifier] -textFile [file]
 *  Or if the file is already tokenized and one word per line, perhaps in
 *  a tab-separated value format with extra columns for part-of-speech tag,
 *  etc., use the version below (note the 's' instead of the 'x'):
 *  java -mx400m edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier
 *      [classifier] -testFile [file]
 *
 *  @author Jenny Finkel
 *  @author Christopher Manning
 */

public class ConvertNER {

  static public void main(String[] args) throws IOException {

    String annFile= "training.ann";
    String sourceFile= "training.txt";

    try {
      ////////////////////////////////////////////////////////////////
      BufferedReader annIn = new BufferedReader(new FileReader(annFile));
      BufferedReader sourceIn = new BufferedReader(new FileReader(sourceFile));
      String totalSource = "";
      String s;
      while ((s = sourceIn.readLine()) != null) {
        totalSource += s + "/";
      }
      char[] charTotal = totalSource.toCharArray();
      sourceIn.close();

      ArrayList result = new ArrayList();
      String[] annLine = null;
      int currentPosition = 1;
      while ((s = annIn.readLine()) != null) {
        annLine = s.split("\\s");
        int startPosition = Integer.parseInt(annLine[2]) + 1;
        int endPostion = Integer.parseInt(annLine[3]) + 1;
        if (startPosition <= currentPosition) {
          String imsi = "";
          for (int i = startPosition; i < endPostion; i++) {
            imsi += String.valueOf(charTotal[i]);
            currentPosition = endPostion+1;
          }
          result.add(imsi.toString()+ " " + annLine[1]);
        } else {
          String imsi = "";
          for (int i = currentPosition; i < startPosition; i++) {
            imsi += String.valueOf(charTotal[i]);
          }
          for (LNode node : Analyzer.parseJava(imsi)) {
              if (node.morpheme().surface().contains("/")) {
                  result.add(".  " + "O");
                  result.add(" " + "");
                  result.add(" " + "");
                  if (!node.morpheme().surface().replace("/"," ").equals(" ")) {
                      result.add(node.morpheme().surface().replace("/", " ") + " O");
                  }
              } else {
                  result.add(node.morpheme().surface() + " " + "O");
              }
          }

//          String[]  imsiSplit = imsi.split("\\s");
//          for (int i=0 ; i < imsiSplit.length; i++) {
//              if (!imsiSplit[i].equals("")) {
//                  //result.add(imsiSplit[i] + " " + "O");
//                  if (imsiSplit[i].contains("/")) {
//                      result.add(".  " + "O");
//                      result.add(" " + "");
//                      result.add(" " + "");
//                      if (!imsiSplit[i].replace("/"," ").equals(" ")) {
//                          result.add(imsiSplit[i].replace("/", " ") + " O");
//                      }
//                  } else {
//                      result.add(imsiSplit[i] + " " + "O");
//                  }
//              }
//          }
          imsi = "";
          for (int i = startPosition; i < endPostion; i++) {
            imsi += String.valueOf(charTotal[i]);
            currentPosition = i+1;
          }
          result.add(imsi.toString()+ " " + annLine[1]);
        }
//          result.add(". " + "O");
//          result.add("");

//        if (nextPostion) {
//          for (int i = currentPosition; i < Integer.parseInt(annLine[2]) + 1; i++) {
//            System.out.println(charTotal[i]);
//            currentPosition = i+2;
//            nextPostion = false;
//          }
//        } else {
//          if (Integer.parseInt(annLine[2]) + 1 > currentPosition) {
//            for (int i = Integer.parseInt(annLine[2]) + 1; i < Integer.parseInt(annLine[3]) + 1; i++) {
//              System.out.println(charTotal[i]);
//              currentPosition = i;
//            }
//          } else {
//            nextPostion = true;
//          }
//        }
      }
      annIn.close();

        BufferedWriter out = new BufferedWriter(new FileWriter("trainingset.txt"));
        for(Object result2 : result){
            //String nfd = Normalizer.normalize(result2.toString().trim(), Normalizer.Form.NFD);
            //System.out.println(nfd);
            out.write(result2.toString()); out.newLine();
        }
        out.close();





      //System.out.println(totalSource);
      ////////////////////////////////////////////////////////////////
    } catch (IOException e) {
      System.err.println(e); // 에러가 있다면 메시지 출력
      System.exit(1);
    }

  }

}


