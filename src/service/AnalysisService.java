package service;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;

import java.util.*;
import java.io.File;
import java.io.IOException;

/**
 * Analysis service handles the processing of incoming text with Stanford coreNLP
 */
public class AnalysisService {
    // this should probably have a sync lock? we don't really want more than 1 instance of coreNLP running at once
    AnalysisService() {
        
    }

    static {
        // initialize the pipeline
        Properties props = new Properties();
        props.setProperty("annotators","tokenize,ssplit,pos,lemma,ner,depparse");

        pipeline = new StanfordCoreNLP(props);
    }

    
    private static final StanfordCoreNLP pipeline;

    /**
     * parse takes a string and smacks it with coreNLP
     * @param input the input string to deal with
     * @return TODO
     */
    public static void parse(String input) {

        // create a new annotation
        Annotation annotation = new Annotation(input);
        
        // annotate
        pipeline.annotate(annotation);
        
        // parse the output, this is where all the gnarly work happens
        // TODO
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            System.out.println(sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class).toString(SemanticGraph.OutputFormat.LIST));
            
        }
    }
}
