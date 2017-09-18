import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.Word;

import edu.stanford.nlp.util.StringUtils;
import org.bitbucket.eunjeon.seunjeon.Analyzer;
import org.bitbucket.eunjeon.seunjeon.Eojeol;
import org.bitbucket.eunjeon.seunjeon.LNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;



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

public class NERDemo {

  public static void main(String[] args) throws IOException {

    String serializedClassifier = "my-classification-model.ser.gz";

    if (args.length > 0) {
      serializedClassifier = args[0];
    }

    AbstractSequenceClassifier classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);

    //classifier.
      /* For either a file to annotate or for the hardcoded text example,
         this demo file shows two ways to process the output, for teaching
         purposes.  For the file, it shows both how to run NER on a String
         and how to run it on a whole file.  For the hard-coded String,
         it shows how to run it on a single sentence, and how to do this
         and produce an inline XML output format.
      */


    if (args.length > 1) {
      String fileContents = StringUtils.slurpFile(args[1]);
      List<List<CoreLabel>> out = classifier.classify(fileContents);
      for (List<CoreLabel> sentence : out) {
        for (CoreLabel word : sentence) {
          System.out.print(word.word() + '/' + word.get(AnswerAnnotation.class) + ' ');
        }
        System.out.println();
      }
      out = classifier.classifyFile(args[1]);
      for (List<CoreLabel> sentence : out) {
        for (CoreLabel word : sentence) {
          System.out.print(word.word() + '/' + word.get(AnswerAnnotation.class) + ' ');
        }
        System.out.println();
      }

    } else {


      String testFile= "testset.txt";

      try {
        String s;

        ////////////////////////////////////////////////////////////////
        BufferedReader annIn = new BufferedReader(new FileReader(testFile));
        while ((s = annIn.readLine()) != null) {
          List<String> token_strs = new ArrayList<String>();
          System.out.println("원문:" + s);
          for (LNode node : Analyzer.parseJava(s)) {
            //String nfd = Normalizer.normalize(node.morpheme().surface(), Normalizer.Form.NFD);
            token_strs.add(node.morpheme().surface());
          }
          Sentence<Word> tokens = Sentence.toSentence(token_strs);
          for (CoreLabel cl : classifier.classifySentence(tokens)) {
            if (!cl.get(AnswerAnnotation.class).equals("O")) {
              System.out.println(cl.get(CoreAnnotations.WordAnnotation.class) + ":" + cl.get(AnswerAnnotation.class));
            }
          }
        }
      } catch (Exception e) {

      }






//      // 형태소 분석
//      for (LNode node : Analyzer.parseJava("(머리를 비우고 마음을 다독이는) 그녀의 첫번째 걷기 여행.")) {
//        System.out.println(node.morpheme().surface());
//      }
//
//      // 어절 분석
//      for (Eojeol eojeol: Analyzer.parseEojeolJava("(머리를 비우고 마음을 다독이는) 그녀의 첫번째 걷기 여행.")) {
//        System.out.println(eojeol);
//        for (LNode node: eojeol.nodesJava()) {
//          System.out.println(node.morpheme().surface());
//        }
//      }
//
//      /**
//       * (머리를 비우고 마음을 다독이는) 그녀의 첫번째 걷기 여행
//       * 사용자 사전 추가
//       * surface,cost
//       *   surface: 단어명. '+' 로 복합명사를 구성할 수 있다.
//       *           '+'문자 자체를 사전에 등록하기 위해서는 '\+'로 입력. 예를 들어 'C\+\+'
//       *   cost: 단어 출연 비용. 작을수록 출연할 확률이 높다.
//       */
//      Analyzer.setUserDict(Arrays.asList("덕후", "버카충,-100", "낄끼+빠빠,-100").iterator());
//      for (LNode node : Analyzer.parseJava("(머리를 비우고 마음을 다독이는) 그녀의 첫번째 걷기 여행")) {
//        System.out.println(node.morpheme().surface());
//      }
//
//      // 활용어 원형
//      for (LNode node : Analyzer.parseJava("(머리를 비우고 마음을 다독이는) 그녀의 첫번째 걷기 여행")) {
//        for (LNode node2: node.deInflectJava()) {
//          System.out.println(node2.morpheme().surface());
//        }
//      }
//
//      // 복합명사 분해
//      for (LNode node : Analyzer.parseJava("(머리를 비우고 마음을 다독이는) 그녀의 첫번째 걷기 여행")) {
//        System.out.println(node);   // 낄끼빠빠
//        for (LNode node2: node.deCompoundJava()) {
//          System.out.println(node2.morpheme().surface());  // 낄끼+빠빠
//        }
//      }

    }

  }
}


