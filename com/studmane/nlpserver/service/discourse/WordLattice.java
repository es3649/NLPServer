package com.studmane.nlpserver.service.discourse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.studmane.nlpserver.Server;

class WordLattice {
    private String root;
    private Map<String, LatticeNode> lattice;

    private static final String LATTICE_LOC = "./libs/lattices/";

    private WordLattice(String latticeData) {}

    /**
     * Generates a WordLattice from json contained in a file
     * @param filename the name of the file with the lattice
     * @return the WordLattice instance described in the file
     */
    public static WordLattice fromFile(String filename) 
            throws IOException {
        // read teh bytes from the file
        File file = new File(LATTICE_LOC + filename);
        String json = new String(Files.readAllBytes(file.toPath()));

        // prepare the deserializer
        Gson gson = new Gson();
        TypeToken<WordLattice> typTok = new TypeToken<WordLattice>() {};
        
        // // deserialize the json into a WordLattice
        WordLattice wl = gson.fromJson(json, typeTok.getType());

        // normalize the weights in the lattice
        wl.assertValid();
        wl.normalize();
        return wl;
    }

    /**
     * Use randomness to traverse the lattice and build text
     * 
     * It takes a couple parameters so that it can encode relevant information
     * and act personable
     * @param date the date of the appointment
     * @param name the name of the person
     * @return a sentence generated by traversing the lattice, then performing
     *      appropriate formatting using the given name and date
     */
    public String generate(Calendar date, String name) {
        // declare a string builder
        StringBuilder sb = new StringBuilder();

        // start with the root node of the lattice
        LatticeNode cur = lattice.get(root);
        sb.append(cur.v);

        Random generator = new Random(System.currentTimeMillis());

        // as long as there is a place to transition to...
        while (!cur.to.isEmpty()) {
            // move to the next state
            double rand = generator.nextDouble();

            // do a manual multinomial distribution (this is why things really need to be normalized)
            for (int i = 0; i < cur.to.size(); i++) {
                // dinishish rand by the weight
                rand -= cur.w.get(i);

                // if rand is totally diminished, then we have what we are looking for
                if (rand <= 0) {
                    // advance cur
                    cur = lattice.get(cur.to.get(i));
                }
            }

            // append the transition string
            sb.append(cur.v);

        }

        return sb.toString();
    }

    /**
     * Normailzes the weights in the w members of each LatticeNode.
     * the weights will sum to 1 afterward 
     * (up to floating point arithmetic error)
     */
    private void normalize() {
        for (Map.Entry<String,LatticeNode> entry : this.lattice.entrySet()) {
            double sum = 0;
            for (double weight : entry.getValue().w) {
                sum += weight;
            }

            if (sum == 0 && !entry.getValue().w.isEmpty()) {
                // then the sum of weights is 0, but there need to be weights
                // let's default to a uniform distribution
                double weight = 1/entry.getValue().w.size();

                for (int i = 0; i < entry.getValue().w.size(); i++) {
                    entry.getValue().w.set(i, weight);
                }
            } else {
                // then we need to normalize
                for (int i = 0; i < entry.getValue().w.size(); i++) {
                    entry.getValue().w.set(i, entry.getValue().w.get(i)/sum);
                }
            }
        }
    }

    /**
     * Throws an IOException if the word lattice is not valid
     * 
     * Explicitly checks that the lengths of the 'to' and 'w' members of
     * each Lattice node are the same length.
     */
    private void assertValid() throws IOException {
        for (Map.Entry<String, LatticeNode> entry : this.lattice.entrySet()) {
            if (entry.getValue().to.size() != entry.getValue().w.size()) {
                throw new IOException(String.format("List length mismatch on key %s", entry.getKey()));
            }
        }
    }

    /**
     * Lattice node is a node in a WordLattice Digraph.
     * 
     * The field names are shortened so that they are more manageable 
     * to write in json, as most of these json files will have to be 
     * created manually
     */
    class LatticeNode {
        String v;           // the string value in this node
        List<String> to;    // the nodes to which this node can transition
        List<Double> w;      // the weights on those transitions
    }
}