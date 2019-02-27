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
import org.openmarkov.core.model.network.potential.treeadd.Threshold;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;
import org.openmarkov.io.probmodel.strings.XMLAttributes;

import java.io.*;
import java.util.*;

/**
 * @author Manuel Arias
 */
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
     *
     * @param probNetInfo1
     * @param probNetInfo2
     * @return
     */
    private boolean sameInfoProbNetsInfo(ProbNetInfo probNetInfo1, ProbNetInfo probNetInfo2) {
        boolean bothNotNull = probNetInfo1 != null && probNetInfo2 != null;
        boolean bothNull = probNetInfo1 == null && probNetInfo2 == null;
        return bothNull ||
                   ( bothNotNull &&
                     sameInfoListsOfEvidencecases(probNetInfo1.getEvidence(), probNetInfo2.getEvidence()) &&
                     sameInfo(probNetInfo1.getProbNet(), probNetInfo2.getProbNet()) );
    }

    /**
     * @param probNet1
     * @param probNet2
     * @return True if networks are equal
     */
    private boolean sameInfo(ProbNet probNet1, ProbNet probNet2) {
        boolean bothNotNull = probNet1 != null && probNet2 != null;
        boolean bothNull = probNet1 == null && probNet2 == null;
        return bothNull ||
                   ( bothNotNull &&
                     sameInfoMiscelanea(probNet1, probNet2) &&
                     sameInfoListOfConstraints(probNet1, probNet2) &&
                     sameInfoListOfVariables(probNet1, probNet2) &&
                     sameInfoListOfLinks(probNet1, probNet2) &&
                     sameInfoListOfPotentials(probNet1, probNet2) );
    }

    /**
     * Compares list of evidence cases
     * @param evidenceCases1
     * @param evidenceCases2
     * @return
     */
    private boolean sameInfoListsOfEvidencecases(List<EvidenceCase> evidenceCases1, List<EvidenceCase> evidenceCases2) {
        boolean bothNotNull = evidenceCases1 != null && evidenceCases2 != null;
        boolean bothNull = evidenceCases1 == null && evidenceCases2 == null;
        boolean same = bothNull || bothNotNull;
        if (bothNotNull && same) {
            int size = evidenceCases1.size();
            if (size == evidenceCases2.size()) {
                int i;
                for (i = 0; i < size && sameInfoEvidenceCases(evidenceCases1.get(i), evidenceCases2.get(i)); i++);
                same = i == size;
            } else {
                same = false;
            }
        }
        return same;
    }

    /**
     *
     * @param evidenceCase1
     * @param evidenceCase2
     * @return
     */
    private boolean sameInfoEvidenceCases(EvidenceCase evidenceCase1, EvidenceCase evidenceCase2) {
        int numberOfFindings = evidenceCase1.getNumberOfFindings();
        boolean same = numberOfFindings == evidenceCase2.getNumberOfFindings();
        if (same) {
            List<Variable> variables1 = evidenceCase1.getVariables(); // Number of findings == number of variables
            for (int i = 0; i < numberOfFindings && same; i++) {
                Finding findingVariable1 = evidenceCase1.getFinding(variables1.get(i)); // Always not null
                Finding findingVariable2 = evidenceCase2.getFinding(variables1.get(i));  // Same variables in both evidence case, otherwise findingVariable2 == null
                same = findingVariable2 != null &&
                         findingVariable1.getState() == findingVariable2.getState() &&
                         findingVariable1.getStateIndex() == findingVariable2.getStateIndex() &&
                         findingVariable1.getNumericalValue() == findingVariable2.getNumericalValue();
            }
        }
        return same;
    }

    /**
     * Comparison of several data that is not variables, links or potentials
     * @param pr1
     * @param pr2
     * @return
     */
    private boolean sameInfoMiscelanea(ProbNet pr1, ProbNet pr2) {
        return sameInfoStrings(pr1.getName(), pr2.getName()) &&
               sameInfoStrings(pr1.getComment(), pr2.getComment()) &&
               sameInfoCycleLength(pr1.getCycleLength(), pr2.getCycleLength()) &&
               sameInfoMapsStrings(pr1.additionalProperties, pr2.additionalProperties) &&
               sameInfoAgents(pr1.getAgents(), pr2.getAgents()) &&
               sameInfoInferenceOptions(pr1.getInferenceOptions(), pr2.getInferenceOptions());
    }

    private boolean sameInfoInferenceOptions(InferenceOptions inferenceOptions1, InferenceOptions inferenceOptions2) {
        return inferenceOptions1.discountRate == inferenceOptions2.discountRate &&
               sameInfoVariables(inferenceOptions1.simulationIndexVariable, inferenceOptions2.simulationIndexVariable) &&
               sameInfoMultiCriteriaOptions(inferenceOptions1.getMultiCriteriaOptions(), inferenceOptions2.getMultiCriteriaOptions()) &&
               sameInfoTemporalOptions(inferenceOptions1.getTemporalOptions(), inferenceOptions2.getTemporalOptions());
    }

    private boolean sameInfoTemporalOptions(TemporalOptions temporalOptions1, TemporalOptions temporalOptions2) {
        return temporalOptions1.getHorizon() == temporalOptions2.getHorizon() &&
               temporalOptions1.getTransition() == temporalOptions2.getTransition();
    }

    private boolean sameInfoMultiCriteriaOptions(MulticriteriaOptions multicriteria1, MulticriteriaOptions multicriteria2) {
        return multicriteria1.isCeOptionsShowed() == multicriteria2.isCeOptionsShowed() &&
               multicriteria1.isUnicriterionOptionsShowed() == multicriteria2.isCeOptionsShowed() &&
               sameInfoStrings(multicriteria1.getMainUnit(), multicriteria2.getMainUnit()) &&
               multicriteria1.getMulticriteriaType() == multicriteria2.getMulticriteriaType();
    }

    /**
     * Compares the contents of two list of agents, information must be in the same order in both lists.
     * @param agents1
     * @param agents2
     * @return true if both lists are equal
     */
    private boolean sameInfoAgents(List<StringWithProperties> agents1, List<StringWithProperties> agents2) {
        boolean notNull = agents1 != null && agents2 != null;
        boolean same = (agents1 == null && agents2 == null) || notNull;
        if (same && notNull) {
            int size = agents1.size();
            // Asumption that contents are in the same order in both lists
            if (size == agents2.size()) {
                int i;
                for (i = 0; i < size && sameInfoStringsWithProperties(agents1.get(i), agents2.get(i)); i++);
                same = i == size;
            } else {
                same = false;
            }
        }
        return same;
    }

    /**
     *
     * @param cycleLength1
     * @param cycleLength2
     * @return
     */
    private boolean sameInfoCycleLength(CycleLength cycleLength1, CycleLength cycleLength2) {
        boolean bothNotNull = cycleLength1 != null && cycleLength2 != null;
        boolean bothNull = cycleLength1 == null && cycleLength2 == null;
        return bothNull ||
               ( bothNotNull &&
                 cycleLength1.getUnit() == cycleLength2.getUnit() &&
                 cycleLength1.getValue() == cycleLength2.getValue() );
    }

    /**
     * Compare two list of constraints, that include the constraints
     * @param probNet1
     * @param probNet2
     * @return
     */
    private boolean sameInfoListOfConstraints(ProbNet probNet1, ProbNet probNet2) {
        List<PNConstraint> constraints1 = probNet1.getConstraints();
        List<PNConstraint> constraints2 = probNet2.getConstraints();
        boolean bothNull = constraints1 == null && constraints2 == null;
        boolean bothNotNull = constraints1 != null && constraints2 != null;
        boolean same = bothNull || bothNotNull;
        if (bothNotNull) {
            int size = constraints1.size();
            if (size == constraints2.size()) {
                int i;
                for (i = 0; i < size && constraints1.get(i).getClass() == constraints2.get(i).getClass(); i++);
                same = i == size;
            } else {
                same = false;
            }
        }
        return same;
    }

    /**
     *
     * @param probNet1
     * @param probNet2
     * @return
     */
    private boolean sameInfoListOfVariables(ProbNet probNet1, ProbNet probNet2) {
        List<Variable> variables1 = probNet1.getVariables();
        List<Variable> variables2 = probNet2.getVariables();
        int size = variables1.size();
        boolean same;
        if (size == variables2.size()) {
            int i;
            for (i = 0; i < size && sameInfoVariables(variables1.get(i), variables2.get(i)); i++);
            same = i == size;
        } else {
            same = false;
        }
        return same;
    }

    /**
     * Compares the contents of two variables.
     * @param variable1
     * @param variable2
     * @return
     */
    private boolean sameInfoVariables(Variable variable1, Variable variable2) {
        boolean bothVariablesNotNull = variable1 != null && variable2 != null;
        boolean bothVariablesNull = variable1 == null && variable2 == null;

        boolean same = bothVariablesNull || (bothVariablesNotNull && sameInfoStrings(variable1.getName(), variable2.getName()));
        if (same && bothVariablesNotNull) {
            same = sameInfoListOfStates(Arrays.asList(variable1.getStates()), Arrays.asList(variable2.getStates())) &&
                     sameInfoStringsWithProperties(variable1.getAgent(), variable2.getAgent()) &&
                     sameInfoStringsWithProperties(variable1.getUnit(), variable2.getUnit()) &&
                     variable1.getPrecision() == variable2.getPrecision() &&
                     variable1.getTimeSlice() == variable2.getTimeSlice() &&
                     variable1.isTemporal() == variable2.isTemporal() &&
                     variable1.getVariableType() == variable2.getVariableType();
            if (same) {
                PartitionedInterval interval1 = variable1.getPartitionedInterval();
                PartitionedInterval interval2 = variable2.getPartitionedInterval();
                boolean notNullIntervals = interval1 != null && interval2 != null;
                boolean bothIntervalsNull = interval1 == null && interval2 == null;
                same = bothIntervalsNull ||
                        ( notNullIntervals && sameInfoPartitionedIntervals(interval1, interval2) &&
                          variable1.getDecisionCriterion() == variable2.getDecisionCriterion() &&
                          variable1.getTimeSlice() == variable2.getTimeSlice() );
            }
        }
        return same;
    }

    private boolean sameInfoPartitionedIntervals(PartitionedInterval interval1, PartitionedInterval interval2) {
        boolean bothNull = interval1 == null && interval2 == null;
        boolean bothNotNull = interval1 != null && interval2 != null;
        return bothNull ||
                ( bothNotNull && interval1.getMin() == interval2.getMin() &&
                interval1.getMax() == interval2.getMax() &&
                interval1.getNumSubintervals() == interval2.getNumSubintervals() &&
                sameInfoArraysOfBooleans(interval1.getBelongsToLeftSide(), interval2.getBelongsToLeftSide()) &&
                sameInfoArrayOfDoubles(interval1.getLimits(), interval2.getLimits()) );
    }

    private boolean sameInfoArraysOfBooleans(boolean[] booleans1, boolean[] booleans2) {
        boolean bothNull = booleans1 == null && booleans2 == null;
        boolean bothNotNull = booleans1 != null && booleans2 != null;
        boolean same = bothNull || (bothNotNull && booleans1.length == booleans2.length);
        if (same && bothNotNull) {
            int i;
            for (i = 0; i < booleans1.length && booleans1[i] == booleans2[i]; i++);
            same = i == booleans1.length;
        }
        return same;
    }

    /**
     *
     * @param probNet1
     * @param probNet2
     * @return
     */
    private boolean sameInfoListOfLinks(ProbNet probNet1, ProbNet probNet2) {
        List<Link<Node>> links1 = probNet1.getLinks();
        List<Link<Node>> links2 = probNet2.getLinks();
        int size = links1.size();
        boolean same = size == links2.size();
        for (int i = 0; i < size && same; i++) {
            Link<Node> link11 = links1.get(i);
            Node node11 = link11.getNode1();
            Variable variable11 = node11.getVariable();
            String name11 = variable11.getName();

            Node node12 = link11.getNode2();
            Variable variable12 = node12.getVariable();
            String name12 = variable12.getName();

            Link link22 = null;
            try {
                Node node21 = probNet2.getNode(name11);
                Node node22 = probNet2.getNode(name12);
                same &= (node21 != null && node22 != null);
                link22 = same ? probNet2.getLink(node21, node22, link11.isDirected()) : null;
                same &= !(link22 == null);

                // Compare restrictions
                if (same) {
                    Potential restrictions1 = link11.getRestrictionsPotential();
                    Potential restrictions2 = link22.getRestrictionsPotential();
                    boolean bothNull = restrictions1 == null && restrictions2 == null;
                    boolean bothNotNull = restrictions1 != null && restrictions2 != null;
                    same &= bothNull || (bothNotNull && restrictions1.getClass() == restrictions2.getClass());
                    if (same && bothNotNull) {
                        if (restrictions1.getClass() == TablePotential.class) {
                            same &= sameInfoTablePotentials((TablePotential)restrictions1, (TablePotential)restrictions2);
                        } else { // At this moment, restrictions are TablePotentials,
                            same &= sameInfoCommonPartPotentials(restrictions1, restrictions2);
                        }
                    }
                }
            } catch (NodeNotFoundException e) {
                same = false;
            }

            // Checks revealingStates and revealingIntervals
            if (same && link22 != null) {
                same = sameInfoListOfStates(link11.getRevealingStates(), link22.getRevealingStates()) &&
                       sameInfoListOfRevealingIntervals(link11.getRevealingIntervals(), link22.getRevealingIntervals());
            }
        }

        return same;
    }

    private boolean sameInfoListOfRevealingIntervals(List<PartitionedInterval> revealingIntervals1, List<PartitionedInterval> revealingIntervals2) {
        boolean bothEmpty = revealingIntervals1.isEmpty() && revealingIntervals2.isEmpty();
        boolean bothNotEmpty = !revealingIntervals1.isEmpty() && !revealingIntervals2.isEmpty();
        boolean same = bothEmpty || bothNotEmpty;
        int numStates = revealingIntervals1.size();
        for (int i = 0; i < numStates && same; i++) {
            same = sameInfoPartitionedIntervals(revealingIntervals1.get(i), revealingIntervals2.get(i));
        }
        return same;
    }

    private boolean sameInfoListOfStates(List<State> states1, List<State> states2) {
        boolean bothEmpty = states1.isEmpty() && states2.isEmpty();
        boolean bothNotEmpty = !states1.isEmpty() && !states2.isEmpty();
        int numStates = states1.size();
        boolean same = bothEmpty || (bothNotEmpty && numStates == states2.size());
        if (same && bothNotEmpty) {
            for (int i = 0; i < numStates && same; i++) {
                State state11 = states1.get(i);
                State state22 = states2.get(i);
                same = sameInfoStrings(state11.getName(), state22.getName()) &&
                       sameInfoMapsStrings(state11.additionalProperties, state22.additionalProperties);
            }
        }
        return same;
    }

    private boolean sameInfoListOfPotentials(ProbNet probNet1, ProbNet probNet2) {
        int numPotentials = probNet1.getNumPotentials();
        boolean same = numPotentials == probNet2.getNumPotentials();
        if (same && numPotentials > 0) {
            same = sameInfoConstantPotentials(probNet1.getConstantPotentials(), probNet2.getConstantPotentials());

            List<Potential> potentials1 = probNet1.getPotentials();
            List<Potential> potentials2 = probNet2.getPotentials();
            // Other potentials
            potentials1.removeAll(probNet1.getConstantPotentials());
            potentials2.removeAll(probNet2.getConstantPotentials());
            int size = potentials1.size();
            for (int i = 0; i < size && same; i++) {
                same = sameInfoPotentials(potentials1.get(i), potentials2.get(i));
            }

        }
        return same;
    }

    private boolean sameInfoPotentials(Potential potential1, Potential potential2) {
        Class potentialClass = potential1.getClass();
        boolean same = potentialClass == potential2.getClass();
        if (same) {
            if (potentialClass == TablePotential.class) {
                same = sameInfoTablePotentials((TablePotential)potential1, (TablePotential)potential2);
            } else if (ICIPotential.class.isAssignableFrom(potentialClass)) {
                same = sameInfoICIPotentials((ICIPotential)potential1, (ICIPotential)potential2);
                if (same && MinMaxPotential.class.isAssignableFrom(potentialClass)) {
                    same = sameInfoMinMaxPotentials((MinMaxPotential)potential1, (MinMaxPotential)potential2);
                }
            } else if (potentialClass == UniformPotential.class) {
                same = sameInfoUniformPotentials((UniformPotential)potential1, (UniformPotential)potential2);
            } else if (TreeADDPotential.class.isAssignableFrom(potentialClass)) {
                same = sameInfoTreeADDPotentials((TreeADDPotential)potential1, (TreeADDPotential)potential2);
            }
            // TODO Finish this

        }
        return same;
    }

    private boolean sameInfoTreeADDPotentials(TreeADDPotential potential1, TreeADDPotential potential2) {
        boolean same = sameInfoCommonPartPotentials(potential1, potential2) &&
                       sameInfoVariables(potential1.getRootVariable(), potential2.getRootVariable());
        List<TreeADDBranch> branches1 = potential1.getBranches();
        List<TreeADDBranch> branches2 = potential2.getBranches();
        same &= sameInfoListOfBranches(branches1, branches2);
        return same;
    }

    private boolean sameInfoListOfBranches(List<TreeADDBranch> branches1, List<TreeADDBranch> branches2) {
        boolean bothNull = branches1 == null && branches2 == null;
        boolean bothNotNull = branches1 != null && branches2 != null;
        int size = branches1.size();
        boolean same = bothNull ||
                       (bothNotNull && size == branches2.size());
        if (same && bothNotNull) {
            int i;
            for (i = 0; i < size && sameInfoBranches(branches1.get(i), branches2.get(i)); i++);
            same = i == size;
        }
        return same;
    }

    private boolean sameInfoBranches(TreeADDBranch treeADDBranch1, TreeADDBranch treeADDBranch2) {
        return sameListOfVariablesNames(treeADDBranch1.getAddableVariables(), treeADDBranch2.getAddableVariables()) &&
               sameInfoStrings(treeADDBranch1.getLabel(), treeADDBranch2.getLabel()) &&
               sameThresholds(treeADDBranch1.getLowerBound(), treeADDBranch2.getLowerBound()) &&
               sameThresholds(treeADDBranch1.getUpperBound(), treeADDBranch2.getUpperBound()) &&
               sameInfoListOfStates(treeADDBranch1.getStates(), treeADDBranch2.getStates()) &&
               sameInfoPotentials(treeADDBranch1.getPotential(), treeADDBranch2.getPotential());
    }

    private boolean sameThresholds(Threshold threshold1, Threshold threshold2) {
        return threshold1.getLimit() == threshold2.getLimit() && threshold1.belongsToLeft() == threshold2.belongsToLeft();
    }

    private boolean sameInfoUniformPotentials(UniformPotential potential1, UniformPotential potential2) {
        return sameInfoCommonPartPotentials(potential1, potential2) &&
                potential1.isUncertain() == potential2.isUncertain() &&
                potential1.getDiscreteValue() == potential2.getDiscreteValue();
    }

    private boolean sameInfoMinMaxPotentials(MinMaxPotential potential1, MinMaxPotential potential2) {
        return sameInfoVariables(potential1.getPseudoVariable(), potential2.getPseudoVariable()) &&
                sameInfoTablePotentials(potential1.getCPT(), potential2.getCPT());
    }

    /**
     * Compares the common part of two ICI Potentials
     * @param potential1
     * @param potential2
     * @return
     */
    private boolean sameInfoICIPotentials(ICIPotential potential1, ICIPotential potential2) {
        boolean same = sameInfoCommonPartPotentials(potential1, potential2) &&
                potential1.getModelType() == potential2.getModelType() &&
                potential1.getFamily() == potential2.getFamily() &&
                sameInfoArrayOfDoubles(potential1.getLeakyParameters(), potential2.getLeakyParameters());
        List<Variable> variables = potential1.getVariables();
        if (same) {
            for (Variable variable : variables) {
                same &= sameInfoArrayOfDoubles(potential1.getNoisyParameters(variable), potential2.getNoisyParameters(variable));
            }
        }
        if (same) {
            // Compare subpotentials, that include the functional potential and the noisy potentials
            List<TablePotential> subPotentials1 = potential1.getSubpotentials();
            List<TablePotential> subPotentials2 = potential2.getSubpotentials();
            int size = subPotentials1.size();
            same = size == subPotentials2.size();
            int i;
            for (i = 0; i < size && sameInfoTablePotentials(subPotentials1.get(i), subPotentials2.get(i)); i++);
            same = i == size && sameInfoTablePotentials(potential1.getLeakyPotential(), potential2.getLeakyPotential());
        }

        return same;
    }

    private boolean sameInfoConstantPotentials(Set<TablePotential> constantPotentials1, Set<TablePotential> constantPotentials2) {
        boolean bothNotNull = constantPotentials1 != null && constantPotentials2 != null;
        boolean bothNull = constantPotentials1 == null && constantPotentials2 == null;
        boolean same = bothNull || (bothNotNull && constantPotentials1.size() == constantPotentials2.size());
        if (bothNotNull && same) {
            int numConstantPotentials = constantPotentials1.size();
            if (numConstantPotentials > 0) {
                List<TablePotential> list1 = new ArrayList<TablePotential>(constantPotentials1);
                List<TablePotential> list2 = new ArrayList<TablePotential>(constantPotentials2);
                for (int i = 0; i < numConstantPotentials && same; i++) {
                    int j;
                    for (j = 0; j < numConstantPotentials && !sameInfoTablePotentials(list1.get(i), list2.get(j)); j++);
                    same = j < numConstantPotentials;
                }
            }
        }
        return same;
    }

    /**
     * Compare two not null constant potentials
     * @param tablePotential1
     * @param tablePotential2
     * @return
     */
    private boolean sameInfoTablePotentials(TablePotential tablePotential1, TablePotential tablePotential2) {
        // Common part for all potentials
        boolean same = sameInfoCommonPartPotentials(tablePotential1, tablePotential2);

        // Compare values
        same &= tablePotential1.values.length == tablePotential2.values.length;
        if (same) {
            int i;
            for (i = 0; i < tablePotential1.values.length && tablePotential1.values[i] == tablePotential2.values[i]; i++);
            same = i == tablePotential1.values.length;
        }
        same &= tablePotential1.getInitialPosition() == tablePotential2.getInitialPosition();
        // It does not compare offsets and dimensions because variables are already checked.

        boolean bothNull = tablePotential1.uncertainValues == null && tablePotential2.uncertainValues == null;
        boolean bothNotNull = tablePotential1.uncertainValues != null && tablePotential2.uncertainValues != null;
        same &= bothNull || (bothNotNull && tablePotential1.uncertainValues.length == tablePotential2.uncertainValues.length);
        if (same && bothNotNull) {
            int i;
            for (i = 0; i < tablePotential1.uncertainValues.length && sameInfoUncertainValues(tablePotential1.uncertainValues[i], tablePotential2.uncertainValues[i]); i++);
            same = i == tablePotential1.uncertainValues.length;
        }

        return same;
    }

    private boolean sameInfoUncertainValues(UncertainValue uncertainValue1, UncertainValue uncertainValue2) {
        boolean same = sameInfoStrings(uncertainValue1.getName(), uncertainValue2.getName());
        if (same) {
            ProbDensFunction probDensFunction1 = uncertainValue1.getProbDensFunction();
            ProbDensFunction probDensFunction2 = uncertainValue2.getProbDensFunction();
            same &= sameInfoArrayOfDoubles(probDensFunction1.getParameters(), probDensFunction2.getParameters()) &&
                    probDensFunction1.getMinimum() == probDensFunction2.getMinimum() &&
                    probDensFunction1.getMaximum() == probDensFunction2.getMaximum() &&
                    probDensFunction1.getMean() == probDensFunction2.getMean() &&
                    probDensFunction1.getStandardDeviation() == probDensFunction2.getStandardDeviation();
        }
        return same;
    }

    /**
     * Compares two potentials
     * @param potential1
     * @param potential2
     * @return
     */
    private boolean sameInfoCommonPartPotentials(Potential potential1, Potential potential2) {
        // Compare miscelanea attributes
        boolean same = potential1.getCriterion() == potential2.getCriterion() &&
                potential1.isAdditive() == potential2.isAdditive() &&
                potential1.isUncertain() == potential2.isUncertain() &&
                sameInfoStrings(potential1.getComment(), potential2.getComment()) &&
                potential1.getPotentialRole() == potential2.getPotentialRole();
        if (same) { // Compare properties
            Map<String, Object> properties1 = potential1.properties;
            Map<String, Object> properties2 = potential2.properties;
            int numProperties = properties1.size();
            same &= numProperties == properties2.size();
            if (same && numProperties > 0) {
                Set<String> keys1 = properties1.keySet();
                for (String key : keys1) {
                    Object object1 = properties1.get(key);
                    Object object2 = properties1.get(key);
                    boolean bothNotNull = object1 != null && object2 != null;
                    boolean bothNull = object1 == null && object2 == null;
                    Class class1 = object1.getClass();
                    Class class2 = object2.getClass();
                    same &= bothNull || (bothNotNull && class1 == class2);
                    if (bothNotNull && same && class1 == String.class) {
                        same = sameInfoStrings(((String)object1), ((String)object2));
                    }
                }
            }
        }

        // Variables (assumption that variables are in the same order)
        same &= sameListOfVariablesNames(potential1.getVariables(), potential2.getVariables());

        return same;
    }

    /**
     * Compares two wrappings of a Map with a name.
     * @param swp1
     * @param swp2
     * @return
     */
    private boolean sameInfoStringsWithProperties(StringWithProperties swp1, StringWithProperties swp2) {
        boolean bothNotNull = swp1 != null && swp2 != null;
        boolean bothNull = swp1 == null && swp2 == null;
        return bothNull ||
               ( bothNotNull &&
                 sameInfoStrings(swp1.getString(), swp2.getString()) &&
                 sameInfoMapsStrings(swp1.getAdditionalProperties().getInformation(), swp2.getAdditionalProperties().getInformation()) );
    }

    /**
     * Compares two HashMaps key = String,value = String
     * @param map1
     * @param map2
     * @return
     */
    private boolean sameInfoMapsStrings(Map<String,String> map1, Map<String,String> map2) {
        boolean notNull = map1 != null && map2 != null;
        boolean same = notNull || (map1 == null && map2 == null);
        if (same && notNull) {
            Set<String> set1 = map1.keySet();
            Set<String> set2 = map1.keySet();
            same = set1.size() == set2.size();
            if (same) {
                for (String key : set1) {
                    same = set2.contains(key) ? sameInfoStrings(map1.get(key), map2.get(key)) : false;
                    if (!same) break;
                }
            }
        }
        return same;
    }

    private boolean sameListOfVariablesNames(List<Variable> variables1, List<Variable> variables2) {
        int numVariables = variables1.size();
        boolean same = numVariables == variables2.size();
        if (same) {
            int i;
            for (i = 0; i < numVariables && same && sameInfoStrings(variables1.get(i).getName(), variables2.get(i).getName()); i++);
            same = i == numVariables;
        }
        return same;
    }

    /**
     *
     * @param parameters1
     * @param parameters2
     * @return
     */
    private boolean sameInfoArrayOfDoubles(double[] parameters1, double[] parameters2) {
        boolean bothNull = parameters1 == null && parameters2 == null;
        boolean bothNotNull = parameters1 != null && parameters2 != null;
        boolean same = bothNull || bothNotNull;
        if (bothNotNull && parameters1.length == parameters2.length) {
            int i;
            for (i = 0; i < parameters1.length && parameters1[i] == parameters2[i]; i++) ;
            same = i == parameters1.length;
        } else {
            same = false;
        }
        return same;
    }

    /**
     * Compare two strings that can be null
     * @param name1
     * @param name2
     * @return
     */
    private boolean sameInfoStrings(String name1, String name2) {
        return ( (name1 == null && name2 == null) || (name1 != null && name2 != null && name1.compareTo(name2) == 0) );
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

    // Auxiliar internal clases and interfaces
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
        public FileIterator(File rootFolder, List<StringFilter>... filters) {
            this.filters = filters != null && filters.length == 1 ? filters[0] : null;
            hasNext = false;
            next = null;

            if (rootFolder.isDirectory()) {
                subNodes = new ArrayList<>();
                File[] rootChildren = rootFolder.listFiles();
                subNodes.add(rootChildren);

                subNodesIndexes = new ArrayList<>();
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
            if (filters != null) {
                for (StringFilter filter : filters) {
                    match &= filter.meetsCondition(file.getName());
                }
            }
            return match;
        }
    }

}
