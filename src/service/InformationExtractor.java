package service;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import service.model.Conversation;
import service.model.MessageIntent;
import edu.stanford.nlp.pipeline.Annotation;

/**
 * InformationExtractor takes coreNLP annotation information and
 * attempts to extract relevant information which can be queried
 * using the Extractor's public methods
 */
public class InformationExtractor {

    /**
     * constructor for an information extractor
     */
    public InformationExtractor() {}

    /**
     * Extracts information about the message based on the annotation, then updates the information
     * in the conversation object accorginly, the one thing it does not do is mess with the conversation state.
     * The state is to be modified by the DiscourseManager based on the returned MessageIntent and the current state.
     * 
     * @param annotation a edu.stanford.nlp.pipeline.Annotation with annotations for
     *      a given string
     * @param conversation a conversation model holding memory about the conversation.
     *      this conversation object will be updated based on 
     * @return a MessageIntent indicating the intent of the message
     */
    public MessageIntent updateTemplate(Annotation annotation, Conversation conversation) {

        // This is where the dependency graph annotations are
        // List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        // for (CoreMap sentence : sentences) {
        //     System.out.println(sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class).toString(SemanticGraph.OutputFormat.LIST));
            
        // }

        assert false;
    }

    /**
     * Gets "next wednesday"
     * @param after the reference starting date
     * @return The Wednesday most closely following the given date
     */
    public static Calendar nextWednesday(Calendar after) {
        Calendar start = new GregorianCalendar(after.get(Calendar.YEAR), 
                after.get(Calendar.MONTH), after.get(Calendar.DAY_OF_MONTH) );

        while (start.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY) {
            start.add(Calendar.DAY_OF_WEEK, -1);
        }

        Calendar end = (Calendar) start.clone();
        end.add(Calendar.DAY_OF_MONTH, 7);

        return end;
    }

    /**
     * Gets "next sunday"
     * @param after the reference starting date
     * @return the sunday most closely folloring the given date
     */
    public static Calendar nextSunday(Calendar after) {

        Calendar start = new GregorianCalendar(after.get(Calendar.YEAR), 
                after.get(Calendar.MONTH), after.get(Calendar.DAY_OF_MONTH) );

        while (start.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            start.add(Calendar.DAY_OF_WEEK, -1);
        }

        Calendar end = (Calendar) start.clone();
        end.add(Calendar.DAY_OF_MONTH, 7);

        return end;
    }
    
}