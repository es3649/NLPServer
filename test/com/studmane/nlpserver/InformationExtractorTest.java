package com.studmane.nlpserver.service;

import org.junit.*;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.simple.*;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class InformationExtractorTest {
    public InformationExtractorTest() {
        messages = new ArrayList<String>();
        messages.add("Hey pal, I'm going through the temple in december so like I need some temple rec interviews.");
        messages.add("I require an interview with the bishop");
        messages.add("I would like to see the bishop");
        messages.add("Hey bro, can I get in with the bishop on sunday?");
        messages.add("Can I set up a time to meet with bishop next sunday?");
        messages.add("Hey Eric! Would it be possible to get an app. With the bishop this next week for a temple recommend interview? Mine expires this month.");
        messages.add("I really like ranem noodles with pork and dumplings");
        messages.add("Last week I bought a new car and got accepted to Stanford");
        messages.add("My girlfriends is like a unicorn: beautiful as all get out and imaginary. :(");
        messages.add("Someday, when you are older, you could get hit by a boulder.");
        

    }

    private List<String> messages = null;

    @Test
    public void basicTestInformationExtractor() {
        //String message = "Hey Eric, I need to meet with the bishop this sunday.";
        for (String message : messages) {

            Annotation anno = AnalysisService.annotate(message);
            
            for (CoreMap sentence : anno.get(CoreAnnotations.SentencesAnnotation.class)) {
                Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
                
                for (RelationTriple triple : triples) {
                    System.out.println(triple.confidence + "\t" +
                    triple.subjectLemmaGloss() + "\t<" +
                    triple.relationLemmaGloss() + ">\t" +
                    triple.objectLemmaGloss());
                }
            }
        }
    }

    // @Test
    // public void testDocument() {
    //     Document doc = new Document(message);

    //     for (Sentence sent : doc.sentences()) {
    //         for (RelationTriple triple : sent.openieTriples()) {
    //             System.out.println(triple.confidence + "\t" +
    //                 triple.subjectLemmaGloss() + "\t<" +
    //                 triple.relationLemmaGloss() + ">\t" +
    //                 triple.objectLemmaGloss());
    //         }
    //     }
    // }
}
