package com.studmane.nlpserver.service.discourse;

import java.util.Calendar;

/**
 * DiscourseGenerator generates text given some arguments.
 * It can be loaded from a file, then traversed.
 * 
 * I want these files to encode HMMs (weighted word lattices)
 * which can be traversed to produce coherent text.
 */
class DiscourseGenerator {
    private DiscourseGenerator() {}

    

    /**
     * uses a date and a name to generate a sentence somewhar randomly
     * from the constitutient segments
     * @param date
     * @param name
     * @return
     */
    public String generate(Calendar date, String name) {
        assert false;
        return null;
    }

    static DiscourseGenerator fromFile(String filename) {
        // open a file
        // get json out of it
        // construct the object from the json
        assert false;
        return null;
    }
}