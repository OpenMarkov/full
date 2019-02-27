package org.openmarkov.full.io;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.inference.MulticriteriaOptions;
import org.openmarkov.core.inference.TemporalOptions;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.*;
import org.openmarkov.core.model.network.constraint.PNConstraint;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunction;
import org.openmarkov.core.model.network.modelUncertainty.UncertainValue;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.UniformPotential;
import org.openmarkov.core.model.network.potential.canonical.ICIPotential;
import org.openmarkov.core.model.network.potential.canonical.MinMaxPotential;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;
import org.openmarkov.io.probmodel.strings.XMLAttributes;

import java.io.*;
import java.util.*;

public class Classificator extends PGMXReader_0_2 {


    public static void main(String args[]) {
        try {
            Classificator classificator = new Classificator(args);
            classificator.testConversionBetweenVersions();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // Attributes
    private static final String defaultPpathToTestFiles="/home/manuel/Redes/OriginalNetworks";
    private static final String defaultPathToNewFiles = "/home/manuel/Redes/NewNetworks";

    private final String V0_2 = "0.2.0";
    private final String V0_5 = "0.5.0";
    private final String V0_7 = "0.7.0";

    private String pathToTestFiles;
    private String pathToNewFiles;

    private String[] networksWithAdvancedFeatures = {

    };

    // Constructor

/** This class performs several operations with files in PGMX format.
     * @param paths Optional String[] parameter. paths[0] = path to files; paths[1] = path to new files. */

    public Classificator(String[] paths) throws IOException {
        setPaths(paths);
        File fileToPathToTestFiles = new File(pathToNewFiles);
        if (!fileToPathToTestFiles.exists() || !fileToPathToTestFiles.isDirectory()) {
            throw new IOException("No test path.");
        } else {
            File testNodeFile = new File(pathToTestFiles);
            List<StringFilter> filters = new ArrayList<>(1);
            filters.add(new PGMXFiles());
            for (FileIterator iterator = new FileIterator(testNodeFile, filters); iterator.hasNext(); ) {
                File originalFile = iterator.next();
                String originalFileName = originalFile.getAbsolutePath();
                String version = null;
                try {
                    version = getVersion(originalFile);
                } catch (ParserException e) {
                    writeExceptionInfo("Can not read file: " + originalFileName, null, e);
                    continue;
                }
                PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
                InputStream networkStream = getClass().getClassLoader().getResourceAsStream(originalFileName);
                ProbNetInfo probNetInfo = null;
                try {
                    probNetInfo = pgmxReader.loadProbNetInfo(originalFileName, networkStream);
                } catch (ParserException e) {
                    e.printStackTrace();
                }
                ProbNet originalProbNet = probNetInfo.getProbNet();
                List<EvidenceCase> originalEvidenceCases = probNetInfo.getEvidence();

                // Test begins here

                    // Write and read probNetInfo in versions 0.2 and 0.7.

//                String pathToNewFile0_2 = getNewPath(originalFileName, V0_2);
//                if (version.matches(V0_2) && !networkNameIsIncludedInListOfAdvancedFeatures(pathToNewFile0_2))) {
//                    // Write 0.2
//                    ProbNetWriter writer02 = new PGMXWriter_0_2();
//                    try {
//                        writer02.writeProbNet(pathToNewFile0_2, originalProbNet, originalEvidenceCases);
//                        // Read 0.2
//                        InputStream networkStream02_bis = getClass().getClassLoader().getResourceAsStream(originalFileName);
//                        ProbNetInfo probNetInfo02_bis = pgmxReader.loadProbNetInfo(pathToNewFile0_2, networkStream02_bis);
//                    } catch (WriterException e) {
//                        e.printStackTrace();
//                    } catch (ParserException e) {
//                        e.printStackTrace();
//                    }
//                }


//                    String pathToNewFile0_5 = getNewPath(originalFile.getAbsolutePath(), V0_5);
//                    ProbNetWriter writer05 = new PGMXWriter_0_5();
//                    writer02.writeProbNet(pathToNewFile0_5, originalProbNet, originalEvidenceCases);


                    // Read the probNetInfo recently written in both versions.
                    // Compare the contents with the original probNetInfo.
                    // Report differences for each network and write message
                // Test ends here
            }
        }
    }

    // Methods
    /**
     * @param pr1
     * @param pr2
     * @return True if networks are equal
     */
    private boolean compareNetworks(ProbNet pr1, ProbNet pr2) {
        boolean notNull = pr1 != null && pr2 != null;
        boolean equals = notNull || (pr1 == null && pr2 == null);
        if (notNull) {
            equals &= equalsMiscelanea(pr1, pr2);
            equals &= equalsListOfConstraints(pr1, pr2);
            equals &= equalsListOfVariables(pr1, pr2);
            equals &= equalsListOfLinks(pr1, pr2);
            equals &= equalsListOfPotentials(pr1, pr2);
        }
        return equals;
    }

    /**
     * Compares list of evidence cases
     * @param evidenceCases1
     * @param evidenceCases2
     * @return
     */
    private boolean equalsListsOfEvidencecases(List<EvidenceCase> evidenceCases1, List<EvidenceCase> evidenceCases2) {
        boolean bothNotNull = evidenceCases1 != null && evidenceCases2 != null;
        boolean equals = (evidenceCases1 == null && evidenceCases2 == null) || bothNotNull;
        if (bothNotNull && equals) {
            int size = evidenceCases1.size();
            equals &= size == evidenceCases2.size();
            for (int i = 0; i < size && equals; i++) {
                equals &= equalsEvidenceCases(evidenceCases1.get(i), evidenceCases2.get(i));
            }
        }
        return equals;
    }

    /**
     *
     * @param evidenceCase1
     * @param evidenceCase2
     * @return
     */
    private boolean equalsEvidenceCases(EvidenceCase evidenceCase1, EvidenceCase evidenceCase2) {
        int numberOfFindings = evidenceCase1.getNumberOfFindings();
        boolean equals = numberOfFindings == evidenceCase2.getNumberOfFindings();
        if (equals) {
            List<Variable> variables1 = evidenceCase1.getVariables(); // Number of findings == number of variables
            for (int i = 0; i < numberOfFindings && equals; i++) {
                Finding findingVariable1 = evidenceCase1.getFinding(variables1.get(i)); // Always not null
                Finding findingVariable2 = evidenceCase2.getFinding(variables1.get(i));  // Same variables in both evidence case, otherwise findingVariable2 == null
                equals &= findingVariable2 != null &&
                          findingVariable1.getState() == findingVariable2.getState() &&
                          findingVariable1.getStateIndex() == findingVariable2.getStateIndex() &&
                          findingVariable1.getNumericalValue() == findingVariable2.getNumericalValue();
            }
        }
        return equals;
    }

    /**
     * Comparison of several data that is not variables, links or potentials
     * @param pr1
     * @param pr2
     * @return
     */
    private boolean equalsMiscelanea(ProbNet pr1, ProbNet pr2) {
        boolean equals = equalsStrings(pr1.getName(), pr2.getName());
        equals &= equalsStrings(pr1.getComment(), pr2.getComment());
        equals &= equalsCycleLength(pr1.getCycleLength(), pr2.getCycleLength());
        equals &= equalsMapsStrings(pr1.additionalProperties, pr2.additionalProperties);
        equals &= equalsAgents(pr1.getAgents(), pr2.getAgents());
        equals &= equalsInferenceOptions(pr1.getInferenceOptions(), pr2.getInferenceOptions());
        return equals;
    }

    private boolean equalsInferenceOptions(InferenceOptions inferenceOptions1, InferenceOptions inferenceOptions2) {
        return inferenceOptions1.discountRate == inferenceOptions2.discountRate &&
               equalsVariables(inferenceOptions1.simulationIndexVariable, inferenceOptions2.simulationIndexVariable) &&
               equalsMultiCriteriaOptions(inferenceOptions1.getMultiCriteriaOptions(), inferenceOptions2.getMultiCriteriaOptions()) &&
               equalsTemporalOptions(inferenceOptions1.getTemporalOptions(), inferenceOptions2.getTemporalOptions());
    }

    private boolean equalsTemporalOptions(TemporalOptions temporalOptions1, TemporalOptions temporalOptions2) {
        return temporalOptions1.getHorizon() == temporalOptions2.getHorizon() &&
               temporalOptions1.getTransition() == temporalOptions2.getTransition();
    }

    private boolean equalsMultiCriteriaOptions(MulticriteriaOptions multicriteria1, MulticriteriaOptions multicriteria2) {
        return multicriteria1.isCeOptionsShowed() == multicriteria2.isCeOptionsShowed() &&
                        multicriteria1.isUnicriterionOptionsShowed() == multicriteria2.isCeOptionsShowed() &&
                        equalsStrings(multicriteria1.getMainUnit(), multicriteria2.getMainUnit()) &&
                        multicriteria1.getMulticriteriaType() == multicriteria2.getMulticriteriaType();
    }

    /**
     * Compares the contents of two list of agents, information must be in the same order in both lists.
     * @param agents1
     * @param agents2
     * @return true if both lists are equal
     */
    private boolean equalsAgents(List<StringWithProperties> agents1, List<StringWithProperties> agents2) {
        boolean notNull = agents1 != null && agents2 != null;
        boolean equals = (agents1 == null && agents2 == null) || notNull;
        if (equals && notNull) {
            int size = agents1.size();
            equals = size == agents2.size();
            // Asumption that contents are in the same order in both lists
            if (equals) {
                int i;
                for (i = 0; i < size && equalsStringsWithProperties(agents1.get(i), agents2.get(i)); i++);
                equals = i == size;
            }
        }
        return equals;
    }

    /**
     *
     * @param cycleLength1
     * @param cycleLength2
     * @return
     */
    private boolean equalsCycleLength(CycleLength cycleLength1, CycleLength cycleLength2) {
        boolean bothNotNull = cycleLength1 != null && cycleLength2 != null;
        boolean bothNull = cycleLength1 == null && cycleLength2 == null;
        return bothNull ||
                        ( bothNotNull &&
                        cycleLength1.getUnit() == cycleLength2.getUnit() &&
                        cycleLength1.getValue() == cycleLength2.getValue() );
    }

    /**
     * Compare two list of constraints, that include the constraints
     * @param pr1
     * @param pr2
     * @return
     */
    private boolean equalsListOfConstraints(ProbNet pr1, ProbNet pr2) {
        List<PNConstraint> constraints1 = pr1.getConstraints();
        List<PNConstraint> constraints2 = pr2.getConstraints();
        int size = constraints1 == null ? 0 : constraints1.size();
        boolean equals = (constraints1 == null && constraints2 == null) || (constraints1 != null && constraints2 != null && size == constraints2.size());
        for (int i = 0; i < size && equals; i++) {
            equals = constraints1.get(i).getClass() == constraints2.get(i).getClass();
        }
        return equals;
    }

    /**
     *
     * @param pr1
     * @param pr2
     * @return
     */
    private boolean equalsListOfVariables(ProbNet pr1, ProbNet pr2) {
        List<Variable> variables1 = pr1.getVariables();
        List<Variable> variables2 = pr2.getVariables();
        int size = variables1.size();
        boolean equals = size == variables2.size();
        for (int i = 0; i < size && equals; i++) {
            equals &= equalsVariables(variables1.get(i), variables2.get(i));
        }
        return equals;
    }

    /**
     * Compares the contents of two variables.
     * @param variable1
     * @param variable2
     * @return
     */
    private boolean equalsVariables(Variable variable1, Variable variable2) {
        boolean bothVariablesNotNull = variable1 != null && variable2 != null;
        boolean bothVariablesNull = variable1 == null && variable2 == null;

        boolean equals = bothVariablesNull || (bothVariablesNotNull && equalsStrings(variable1.getName(), variable2.getName()));
        if (equals && bothVariablesNotNull) {
            equals = equalsListOfStates(Arrays.asList(variable1.getStates()), Arrays.asList(variable2.getStates())) &&
                     equalsStringsWithProperties(variable1.getAgent(), variable2.getAgent()) &&
                     equalsStringsWithProperties(variable1.getUnit(), variable2.getUnit()) &&
                     variable1.getPrecision() == variable2.getPrecision() &&
                     variable1.getTimeSlice() == variable2.getTimeSlice() &&
                     variable1.isTemporal() == variable2.isTemporal() &&
                     variable1.getVariableType() == variable2.getVariableType();
            if (equals) {
                PartitionedInterval interval1 = variable1.getPartitionedInterval();
                PartitionedInterval interval2 = variable2.getPartitionedInterval();
                boolean notNullIntervals = interval1 != null && interval2 != null;
                boolean bothIntervalsNull = interval1 == null && interval2 == null;
                equals = bothIntervalsNull ||
                        ( notNullIntervals && equalsPartitionedIntervals(interval1, interval2) &&
                        variable1.getDecisionCriterion() == variable2.getDecisionCriterion() &&
                        variable1.getTimeSlice() == variable2.getTimeSlice() );
            }
        }
        return equals;
    }

    private boolean equalsPartitionedIntervals(PartitionedInterval interval1, PartitionedInterval interval2) {
        boolean bothNull = interval1 == null && interval2 == null;
        boolean bothNotNull = interval1 != null && interval2 != null;
        return bothNull ||
                ( bothNotNull && interval1.getMin() == interval2.getMin() &&
                interval1.getMax() == interval2.getMax() &&
                interval1.getNumSubintervals() == interval2.getNumSubintervals() &&
                equalsArraysOfBooleans(interval1.getBelongsToLeftSide(), interval2.getBelongsToLeftSide()) &&
                equalsArrayOfDoubles(interval1.getLimits(), interval2.getLimits()) );
    }

    private boolean equalsArraysOfBooleans(boolean[] booleans1, boolean[] booleans2) {
        boolean bothNull = booleans1 == null && booleans2 == null;
        boolean bothNotNull = booleans1 != null && booleans2 != null;
        boolean equals = bothNull || (bothNotNull && booleans1.length == booleans2.length);
        if (equals && bothNotNull) {
            int i;
            for (i = 0; i < booleans1.length && booleans1[i] == booleans2[i]; i++);
            equals = i == booleans1.length;
        }
        return equals;
    }

    /**
     *
     * @param pr1
     * @param pr2
     * @return
     */
    private boolean equalsListOfLinks(ProbNet pr1, ProbNet pr2) {
        List<Link<Node>> links1 = pr1.getLinks();
        List<Link<Node>> links2 = pr2.getLinks();
        int size = links1.size();
        boolean equals = size == links2.size();
        for (int i = 0; i < size && equals; i++) {
            Link<Node> link11 = links1.get(i);
            Node node11 = link11.getNode1();
            Variable variable11 = node11.getVariable();
            String name11 = variable11.getName();

            Node node12 = link11.getNode2();
            Variable variable12 = node12.getVariable();
            String name12 = variable12.getName();

            Link link22 = null;
            try {
                Node node21 = pr2.getNode(name11);
                Node node22 = pr2.getNode(name12);
                equals &= (node21 != null && node22 != null);
                link22 = equals ? pr2.getLink(node21, node22, link11.isDirected()) : null;
                equals &= !(link22 == null);

                // Compare restrictions
                if (equals) {
                    Potential restrictions1 = link11.getRestrictionsPotential();
                    Potential restrictions2 = link22.getRestrictionsPotential();
                    boolean bothNull = restrictions1 == null && restrictions2 == null;
                    boolean bothNotNull = restrictions1 != null && restrictions2 != null;
                    equals &= bothNull || (bothNotNull && restrictions1.getClass() == restrictions2.getClass());
                    if (equals && bothNotNull) {
                        if (restrictions1.getClass() == TablePotential.class) {
                            equals &= equalsTablePotentials((TablePotential)restrictions1, (TablePotential)restrictions2);
                        } else { // At this moment, restrictions are TablePotentials,
                            equals &= equalsCommonPartPotentials(restrictions1, restrictions2);
                        }
                    }
                }
            } catch (NodeNotFoundException e) {
                equals = false;
                break;
            }

            // Checks revealingStates and revealingIntervals
            if (equals && link22 != null) {
                equals = equalsListOfStates(link11.getRevealingStates(), link22.getRevealingStates()) &&
                         equalsListOfRevealingIntervals(link11.getRevealingIntervals(), link22.getRevealingIntervals());
            }
        }

        return equals;
    }

    private boolean equalsListOfRevealingIntervals(List<PartitionedInterval> revealingIntervals1, List<PartitionedInterval> revealingIntervals2) {
        boolean bothEmpty = revealingIntervals1.isEmpty() && revealingIntervals2.isEmpty();
        boolean bothNotEmpty = !revealingIntervals1.isEmpty() && !revealingIntervals2.isEmpty();
        boolean equals = bothEmpty || bothNotEmpty;
        int numStates = revealingIntervals1.size();
        for (int i = 0; i < numStates && equals; i++) {
            equals = equalsPartitionedIntervals(revealingIntervals1.get(i), revealingIntervals2.get(i));
        }
        return equals;
    }

    private boolean equalsListOfStates(List<State> states1, List<State> states2) {
        boolean bothEmpty = states1.isEmpty() && states2.isEmpty();
        boolean bothNotEmpty = !states1.isEmpty() && !states2.isEmpty();
        boolean equals = bothEmpty || bothNotEmpty;
        int numStates = states1.size();
        for (int i = 0; i < numStates && equals; i++) {
            State state11 = states1.get(i);
            State state22 = states2.get(i);
            equals = equalsStrings(state11.getName(), state22.getName()) &&
                     equalsMapsStrings(state11.additionalProperties, state22.additionalProperties);
        }
        return equals;
    }

    private boolean equalsListOfPotentials(ProbNet pr1, ProbNet pr2) {
        int numPotentials = pr1.getNumPotentials();
        boolean equals = numPotentials == pr2.getNumPotentials();
        if (equals && numPotentials > 0) {
            equals = equalsConstantPotentials(pr1.getConstantPotentials(), pr2.getConstantPotentials());

            List<Potential> potentials1 = pr1.getPotentials();
            List<Potential> potentials2 = pr2.getPotentials();
            // Other potentials
            potentials1.removeAll(pr1.getConstantPotentials());
            potentials2.removeAll(pr2.getConstantPotentials());
            int size = potentials1.size();
            for (int i = 0; i < size && equals; i++) {
                Potential potential1 = potentials1.get(i);
                Potential potential2 = potentials2.get(i);
                Class potentialClass = potential1.getClass();
                equals = potentialClass == potential2.getClass();
                if (equals) {
                    if (potentialClass == TablePotential.class) {
                        equals = equalsTablePotentials((TablePotential)potential1, (TablePotential)potential2);
                    } else if (ICIPotential.class.isAssignableFrom(potentialClass)) {
                        equals = equalsICIPotentials((ICIPotential)potential1, (ICIPotential)potential2);
                        if (equals && MinMaxPotential.class.isAssignableFrom(potentialClass)) {
                            equals = equalsMinMaxPotentials((MinMaxPotential)potential1, (MinMaxPotential)potential2);
                        }
                    } else if (potentialClass == UniformPotential.class) {
                        equals = equalsUniformPotentials((UniformPotential)potential1, (UniformPotential)potential2);
                    } else if (TreeADDPotential.class.isAssignableFrom(potentialClass)) {
                        equals = equalsTreeADDPotentials((TreeADDPotential)potential1, (TreeADDPotential)potential2);
                    }
                    // TODO Finish this

                }
            }

        }
        return equals;
    }

    private boolean equalsTreeADDPotentials(TreeADDPotential potential1, TreeADDPotential potential2) {
        boolean equals = equalsCommonPartPotentials(potential1, potential2);
        // TODO
        return equals;
    }

    private boolean equalsUniformPotentials(UniformPotential potential1, UniformPotential potential2) {
        return equalsCommonPartPotentials(potential1, potential2) &&
                potential1.isUncertain() == potential2.isUncertain() &&
                potential1.getDiscreteValue() == potential2.getDiscreteValue();
    }

    private boolean equalsMinMaxPotentials(MinMaxPotential potential1, MinMaxPotential potential2) {
        return equalsVariables(potential1.getPseudoVariable(), potential2.getPseudoVariable()) &&
                equalsTablePotentials(potential1.getCPT(), potential2.getCPT());
    }

    /**
     * Compares the common part of two ICI Potentials
     * @param potential1
     * @param potential2
     * @return
     */
    private boolean equalsICIPotentials(ICIPotential potential1, ICIPotential potential2) {
        boolean equals = equalsCommonPartPotentials(potential1, potential2) &&
                potential1.getModelType() == potential2.getModelType() &&
                potential1.getFamily() == potential2.getFamily() &&
                equalsArrayOfDoubles(potential1.getLeakyParameters(), potential2.getLeakyParameters());
        List<Variable> variables = potential1.getVariables();
        if (equals) {
            for (Variable variable : variables) {
                equals &= equalsArrayOfDoubles(potential1.getNoisyParameters(variable), potential2.getNoisyParameters(variable));
            }
        }
        if (equals) {
            // Compare subpotentials, that include the functional potential and the noisy potentials
            List<TablePotential> subPotentials1 = potential1.getSubpotentials();
            List<TablePotential> subPotentials2 = potential2.getSubpotentials();
            int size = subPotentials1.size();
            equals = size == subPotentials2.size();
            int i;
            for (i = 0; i < size && equalsTablePotentials(subPotentials1.get(i), subPotentials2.get(i)); i++);
            equals = i == size && equalsTablePotentials(potential1.getLeakyPotential(), potential2.getLeakyPotential());
        }

        return equals;
    }

    private boolean equalsConstantPotentials(Set<TablePotential> constantPotentials1, Set<TablePotential> constantPotentials2) {
        boolean bothNotNull = constantPotentials1 != null && constantPotentials2 != null;
        boolean bothNull = constantPotentials1 == null && constantPotentials2 == null;
        boolean equals = bothNull || (bothNotNull && constantPotentials1.size() == constantPotentials2.size());
        if (bothNotNull && equals) {
            int numConstantPotentials = constantPotentials1.size();
            if (numConstantPotentials > 0) {
                List<TablePotential> list1 = new ArrayList<TablePotential>(constantPotentials1);
                List<TablePotential> list2 = new ArrayList<TablePotential>(constantPotentials2);
                for (int i = 0; i < numConstantPotentials && equals; i++) {
                    int j;
                    for (j = 0; j < numConstantPotentials && !equalsTablePotentials(list1.get(i), list2.get(j)); j++);
                    equals = j < numConstantPotentials;
                }
            }
        }
        return equals;
    }

    /**
     * Compare two not null constant potentials
     * @param tablePotential1
     * @param tablePotential2
     * @return
     */
    private boolean equalsTablePotentials(TablePotential tablePotential1, TablePotential tablePotential2) {
        // Common part for all potentials
        boolean equals = equalsCommonPartPotentials(tablePotential1, tablePotential2);

        // Compare values
        equals &= tablePotential1.values.length == tablePotential2.values.length;
        if (equals) {
            int i;
            for (i = 0; i < tablePotential1.values.length && tablePotential1.values[i] == tablePotential2.values[i]; i++);
            equals = i == tablePotential1.values.length;
        }
        equals &= tablePotential1.getInitialPosition() == tablePotential2.getInitialPosition();
        // It does not compare offsets and dimensions because variables are already checked.

        boolean bothNull = tablePotential1.uncertainValues == null && tablePotential2.uncertainValues == null;
        boolean bothNotNull = tablePotential1.uncertainValues != null && tablePotential2.uncertainValues != null;
        equals &= bothNull || (bothNotNull && tablePotential1.uncertainValues.length == tablePotential2.uncertainValues.length);
        if (equals && bothNotNull) {
            int i;
            for (i = 0; i < tablePotential1.uncertainValues.length && equalsUncertainValues(tablePotential1.uncertainValues[i], tablePotential2.uncertainValues[i]); i++);
            equals = i == tablePotential1.uncertainValues.length;
        }

        return equals;
    }

    private boolean equalsUncertainValues(UncertainValue uncertainValue1, UncertainValue uncertainValue2) {
        boolean equals = equalsStrings(uncertainValue1.getName(), uncertainValue2.getName());
        ProbDensFunction probDensFunction1 = uncertainValue1.getProbDensFunction();
        ProbDensFunction probDensFunction2 = uncertainValue2.getProbDensFunction();
        equals &=   equalsArrayOfDoubles(probDensFunction1.getParameters(), probDensFunction2.getParameters()) &&
                    probDensFunction1.getMinimum() == probDensFunction2.getMinimum() &&
                    probDensFunction1.getMaximum() == probDensFunction2.getMaximum() &&
                    probDensFunction1.getMean() == probDensFunction2.getMean() &&
                    probDensFunction1.getStandardDeviation() == probDensFunction2.getStandardDeviation();

        return equals;
    }

    private boolean equalsArrayOfDoubles(double[] parameters1, double[] parameters2) {
        boolean bothNull = parameters1 == null && parameters2 == null;
        boolean bothNotNull = parameters1 != null && parameters2 != null;
        boolean equals = bothNull || (bothNotNull & parameters1.length == parameters2.length);
        int i;
        for (i = 0; equals && bothNotNull && i < parameters1.length && parameters1[i] == parameters2[i]; i++);
        equals = i == parameters1.length;
        return equals;
    }

    /**
     * Compares two potentials
     * @param potential1
     * @param potential2
     * @return
     */
    private boolean equalsCommonPartPotentials(Potential potential1, Potential potential2) {
        // Compare miscelanea attributes
        boolean equals = potential1.getCriterion() == potential2.getCriterion() &&
                potential1.isAdditive() == potential2.isAdditive() &&
                potential1.isUncertain() == potential2.isUncertain() &&
                equalsStrings(potential1.getComment(), potential2.getComment()) &&
                potential1.getPotentialRole() == potential2.getPotentialRole();
        if (equals) { // Compare properties
            Map<String, Object> properties1 = potential1.properties;
            Map<String, Object> properties2 = potential2.properties;
            int numProperties = properties1.size();
            equals &= numProperties == properties2.size();
            if (equals && numProperties > 0) {
                Set<String> keys1 = properties1.keySet();
                for (String key : keys1) {
                    Object object1 = properties1.get(key);
                    Object object2 = properties1.get(key);
                    boolean bothNotNull = object1 != null && object2 != null;
                    boolean bothNull = object1 == null && object2 == null;
                    Class class1 = object1.getClass();
                    Class class2 = object2.getClass();
                    equals &= bothNull || (bothNotNull && class1 == class2);
                    if (bothNotNull && equals && class1 == String.class) {
                        equals = equalsStrings(((String)object1), ((String)object2));
                    }
                }
            }
        }

        // Variables (assumption that variables are in the same order)
        if (equals) {
            List<Variable> variables1 = potential1.getVariables();
            List<Variable> variables2 = potential2.getVariables();
            int numVariables = variables1.size();
            equals = numVariables == variables2.size();
            if (equals) {
                int i;
                for (i = 0; i < numVariables && equals && equalsStrings(variables1.get(i).getName(), variables2.get(i).getName()); i++);
                equals = i == numVariables;
            }
        }

        return equals;
    }

    /**
     * Compare two strings that can be null
     * @param name1
     * @param name2
     * @return
     */
    private boolean equalsStrings(String name1, String name2) {
        return ((name1 == null && name2 == null) || (name1 != null && name2 != null && name1.compareTo(name2) == 0));
    }

    /**
     * Compares two wrappings of a Map with a name.
     * @param swp1
     * @param swp2
     * @return
     */
    private boolean equalsStringsWithProperties(StringWithProperties swp1, StringWithProperties swp2) {
        boolean notNull = swp1 != null && swp2!= null;
        boolean equals = notNull || (swp1 == null && swp2 == null);
        if (equals && notNull) {
            equals = equalsStrings(swp1.getString(), swp2.getString()) &&
                     equalsMapsStrings(swp1.getAdditionalProperties().getInformation(), swp2.getAdditionalProperties().getInformation());
        }
        return equals;
    }

    /**
     * Compares two HashMaps key = String,value = String
     * @param map1
     * @param map2
     * @return
     */
    private boolean equalsMapsStrings(Map<String,String> map1, Map<String,String> map2) {
        boolean notNull = map1 != null && map2 != null;
        boolean equals = notNull || (map1 == null && map2 == null);
        if (equals && notNull) {
            Set<String> set1 = map1.keySet();
            Set<String> set2 = map1.keySet();
            equals = set1.size() == set2.size();
            if (equals) {
                for (String key : set1) {
                    equals = set2.contains(key) ? equalsStrings(map1.get(key), map2.get(key)) : false;
                    if (!equals) break;
                }
            }
        }
        return equals;
    }


    /**
     * Checks if a string is contained in an array of strings
     * @param netName
     * @return
     */
    private boolean networkNameIsIncludedInListOfAdvancedFeatures(String netName) {
        boolean advancedFeatures = false;
        for (int i = 0; i < networksWithAdvancedFeatures.length && !advancedFeatures; i++) {
            advancedFeatures = networksWithAdvancedFeatures[i].compareTo(netName) == 0;
        }
        return advancedFeatures;
    }

    private void writeExceptionInfo(String message, String version, Exception e) {
        System.err.println(message);
        if (version != null) {
            System.err.println("Version: " + version);
        }
        System.err.println(e.getMessage());
        System.err.println(e.getStackTrace());
    }

    private String getNewPath(String absolutePathOld, String version) {
        return pathToNewFiles + File.separator + version + File.separator + absolutePathOld.substring(pathToTestFiles.length());
    }

    /**
     * Read networks from directory 'pathToTestFiles' and writes them in 0.2 and 0.7 version in
     * 'pathToNewFiles/0.2' and 'pathToNewFiles/0.7'.
     */
    public void testConversionBetweenVersions() throws IOException {
        String pathToNewFiles02 = pathToNewFiles + "/0_2";
        String pathToNewFiles07 = pathToNewFiles + "/0_7";
        cleanTestFoldersTree(pathToTestFiles, pathToNewFiles02, pathToNewFiles07);
        createTestFolders(pathToNewFiles02, pathToNewFiles07);

        File testNetsDirectory = new File(pathToTestFiles);
        File[] testNetsFiles = testNetsDirectory.listFiles();
        int numCharsPathToNewFiles = pathToNewFiles.length();
        for (File testFile : testNetsFiles) {
            if (testFile.isDirectory()) {
                // TODO
            } else {
                PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
                ProbNetInfo probNetInfo = null;
                InputStream networkStream = getClass().getClassLoader().getResourceAsStream(testFile.getAbsolutePath());
                try {
                    probNetInfo = pgmxReader.loadProbNetInfo(testFile.getAbsolutePath(), networkStream);
                } catch (ParserException e) {
                    e.printStackTrace();
                }
            }
        }
    }


/** List recursively files in PGMX version and writes its directory, version and name. */

    public void writeTreeFiles(String pathToFiles) throws Exception {
        File directory = new File(pathToFiles);
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                String canonicalPath = file.getCanonicalPath();

                try {
                    FileInputStream stream = new FileInputStream(canonicalPath);
                    // Get root element.
                    SAXBuilder builder = new SAXBuilder();
                    builder.setJDOMFactory( new LocatedJDOMFactory() );
                    Document document = null;
                    try {
                        document = builder.build(stream);
                    } catch ( JDOMException e ) {
                        throw new ParserException( "Can not parse XML document " + canonicalPath + ":" + e.getMessage() );
                    } catch ( IOException e ) {
                        throw new ParserException( "Error trying to open " + canonicalPath + ".\n" + e.getMessage() );
                    }
                    Element root = document.getRootElement();
                    String strVersion = root.getAttributeValue( XMLAttributes.FORMAT_VERSION.toString() );
                    System.out.print(strVersion + " ");
                    System.out.println(canonicalPath);
                }
                catch (FileNotFoundException e ) {
                    System.err.println("The " + canonicalPath + " does not exists.");
                }
            } else if (file.isDirectory()) {
                String absolutePat = file.getAbsolutePath();
                System.out.println();
                System.out.println(file.getAbsolutePath());
                for (int i = 0; i < absolutePat.length(); i++) {
                    System.out.print("-");
                }
                System.out.println();
            }
        }
    }

    private void replicateOriginFolderStructureInOtherFolder(File originFolder, File newFolder) {
        File[] subOriginFiles = originFolder.listFiles();
        for (File subOriginFolderFile : subOriginFiles) {
            if (subOriginFolderFile.isDirectory()) {
                String newSubFolderString = newFolder.getAbsolutePath() + originFolder.getAbsolutePath().substring((int)originFolder.length());
                File newSubFolder = new File(newSubFolderString);
                newSubFolder.mkdir();
                replicateOriginFolderStructureInOtherFolder(subOriginFolderFile, newSubFolder);
            }
        }
    }


/** Replicates from pathToTestFiles a tree of new files in pathToNewFiles/0.2 and pathToNewFiles/0.7. */

    private void createTestFolders(String pathToTestFiles, String pathToNewFiles) {
        String pathToNewFiles02 = pathToNewFiles + "/0_2";
        String pathToNewFiles07 = pathToNewFiles + "/0_7";
        File newFiles02 = new File(pathToNewFiles02);
        File newFiles07 = new File(pathToNewFiles07);
        File originalFiles = new File(pathToTestFiles);
        try {
            // Remove folders from previous tests
            removeContentsFolder(newFiles02);
            removeContentsFolder(newFiles07);
            // Create new folders
            newFiles02.createNewFile();
            replicateOriginFolderStructureInOtherFolder(originalFiles, newFiles02);
            newFiles07.createNewFile();
            replicateOriginFolderStructureInOtherFolder(originalFiles, newFiles07);
        } catch (IOException e) {
            System.err.println("Can not create new folder.\n" + e.getMessage());
            e.printStackTrace();
        }
    }


/**
     * Creates folders to write files for testing. If the folders exist from previous tests, removes contents.
     * @param pathToNewFiles
     * @param pathToNewFiles02
     * @param pathToNewFiles07
     * @throws IOException
     */

    private void cleanTestFoldersTree(String pathToNewFiles, String pathToNewFiles02, String pathToNewFiles07) throws IOException {
        // Create test directories if they do not exists
        File newFiles = new File(pathToNewFiles);
        newFiles.mkdir(); // Create path; if file already exists do nothing.

        // Create test folders for 0.2 and 0.7 and remove previous contents
        File newFiles02 = new File(pathToNewFiles02);
        newFiles02.mkdir();
        removeContentsFolder(newFiles02);

        File newFiles07 = new File(pathToNewFiles07);
        newFiles07.mkdir();
        removeContentsFolder(newFiles07);
    }


/**
     * Remove recursively all the files and folders of 'folder'
     * @param folder
     */

    private void removeContentsFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    removeContentsFolder(f);
                } else {
                    f.delete();
                }
            }
        }
    }


/**
     * Gets the version of a PGMX file
     * @param pgmxFile
     * @return
     */
    private String getVersion(File pgmxFile) throws ParserException {
        PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
        String absolutePath = pgmxFile.getAbsolutePath();
        InputStream networkStream = getClass().getClassLoader().getResourceAsStream(absolutePath);
        return pgmxReader.getVersion(absolutePath, networkStream);
    }


/**
     * Sets the values of the variables pathToTestFiles and pathToNewFiles
     * @param paths
     * @return
     */
    private void setPaths(String[] paths) {
        pathToTestFiles = null;
        if (paths.length >=1 ) {
            pathToTestFiles = paths[0];
        } else {
            pathToTestFiles = defaultPpathToTestFiles;
        }
        if (paths.length >= 2) {
            pathToNewFiles = paths[1];
        } else {
            pathToNewFiles = defaultPathToNewFiles;
        }
    }

    // Auxiliar clases and interfaces

    private interface Next {
        File next();
        boolean hasNext();
    }

    private interface StringFilter {
        boolean meetsCondition(String string);
    }

    private class PGMXFiles implements StringFilter {
        private final String PGMX_FILES = ".PGMX";

        @Override
        public boolean meetsCondition(String string) {
            return string.toUpperCase().endsWith(PGMX_FILES);
        }
    }

    private class FileIterator implements Next {

        // Attributes
        private List<File[]> subNodes;
        private List<Integer> subNodesIndexes;
        private boolean hasNext;
        private File next;

        private List<StringFilter> filters;

        // Constructor

/**
         * Constructor
         * @param rootFolder
         */

        public FileIterator(File rootFolder, List<StringFilter> filters) {
            this.filters = filters;
            hasNext = false;
            next = null;

            if (rootFolder.isDirectory()) {
                subNodes = new ArrayList<File[]>();
                File[] rootChildren = rootFolder.listFiles();
                subNodes.add(rootChildren);

                subNodesIndexes = new ArrayList<Integer>();
                subNodesIndexes.add(-1);

                next = lookForNext();
            } else {
                hasNext = matches(rootFolder);
                next = hasNext ? rootFolder : null;
            }
        }

        public boolean hasNext() {
            return hasNext;
        }

        public File next() {
            File aux = next;
            next = next == null ? null : lookForNext();
            return aux;
        }


/**
         * Looks for next file in the tree and updates <code>hasNext</code>
         * @return Next File element
         */

        private File lookForNext() {
            int treeDepth = subNodes == null ? 0 : subNodes.size();
            if (treeDepth == 0) {
                hasNext = false;
                next = null;
            } else {
                int treeDepthMinusOne = treeDepth - 1;
                File[] deepestNodes = subNodes.get(treeDepthMinusOne);
                int deepestIndexesNode = subNodesIndexes.get(treeDepthMinusOne);
                subNodesIndexes.set(treeDepthMinusOne, ++deepestIndexesNode);
                if (deepestNodes == null || deepestNodes.length == 0 || (deepestIndexesNode + 1) > deepestNodes.length) {// Empty sub folder or finished folder
                    subNodes.remove(treeDepthMinusOne);
                    subNodesIndexes.remove(treeDepthMinusOne);
                    next = lookForNext();
                } else {
                    File lastFile = deepestNodes[deepestIndexesNode];
                    if (lastFile.isDirectory()) {
                        subNodes.add(lastFile.listFiles());
                        subNodesIndexes.add(-1);
                        next = lookForNext();
                    } else {
                        hasNext = true;
                        next = lastFile;
                    }
                }
            }
            next = (next == null) ? next : matches(next) ? next : lookForNext();
            return next;
        }

        private boolean matches(File file) {
            boolean match = true;
            for (StringFilter filter : filters) {
                match &= filter.meetsCondition(file.getName());
            }
            return match;
        }
    }

}
