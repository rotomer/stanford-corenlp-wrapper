package com.rotomer.stanford_corenlp_wrapper;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static com.rotomer.stanford_corenlp_wrapper.FileUtils.readFile;
import static com.rotomer.stanford_corenlp_wrapper.FileUtils.writeFile;
import static java.util.stream.Collectors.toList;

/**
 * Reference implementation - https://stanfordnlp.github.io/CoreNLP/openie.html
 */
class OpenIEInvoker {
    private final Properties _props;

    /**
     * Note: This is a very costly ctor: ~15-20 sec
     */
    OpenIEInvoker() {
        System.out.println("Stanford Core NLP - initializing...");
        final Instant start = Instant.now();

        // Create the Stanford CoreNLP pipeline
        _props = new Properties();
        _props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");

        System.out.println("Stanford Core NLP - finished initialization. Duration:" + Duration.between(start, Instant.now()).getSeconds());
    }

    void indexRelations(final String inputFilePathStr, final String outputFolderPathStr) {
        System.out.println("Analyzing: " + inputFilePathStr + "...");
        final Instant start = Instant.now();

        final Path inputFilePath = Paths.get(inputFilePathStr);
        final String content = readFile(inputFilePath);

        final Iterable<String> relations = analyzeRelations(content);
        writeRelationsToFile(relations, outputFolderPathStr, inputFilePath);

        System.out.println("Finished analyzing: " + inputFilePath +
                ". Duration: " + Duration.between(start, Instant.now()).getSeconds());
    }

    private void writeRelationsToFile(final Iterable<String> relations,
                                      final String outputFolderPathStr,
                                      final Path inputFilePath) {
        final String inputFileName = inputFilePath.getFileName().toString();
        final String inputFileBaseName = inputFileName.split("\\.")[0];
        final Path outputFilePath = Paths.get(outputFolderPathStr, inputFileBaseName + ".csv");

        writeFile(outputFilePath, relations);
    }

    private Iterable<String> analyzeRelations(final String fileContent) {

        final StanfordCoreNLP pipeline = new StanfordCoreNLP(_props);

        // Annotate a document
        Annotation doc = new Annotation(fileContent);
        pipeline.annotate(doc);

        // Loop over sentences in the document
        final List<String> relations = new LinkedList<>();
        relations.add("Confidence,Subject,Relation,Object");
        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
            // Get the OpenIE triples for the sentence
            Collection<RelationTriple> triples =
                    sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);

            relations.addAll(triples.stream()
                    .map(triple -> triple.confidence + "," +
                            triple.subjectLemmaGloss() + "," +
                            triple.relationLemmaGloss() + "," +
                            triple.objectLemmaGloss())
                    .collect(toList()));
        }

        return relations;
    }
}
