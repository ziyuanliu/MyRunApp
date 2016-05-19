package me.ziyuanliu.myruns;

/**
 * Created by ziyuanliu on 5/8/16.
 */
//
//// Generated with Weka 3.8.0
////
//// This code is public domain and comes with no warranty.
////
//// Timestamp: Tue May 17 15:59:41 EDT 2016
//
//        import weka.core.Attribute;
//        import weka.core.Capabilities;
//        import weka.core.Capabilities.Capability;
//        import weka.core.Instance;
//        import weka.core.Instances;
//        import weka.core.RevisionUtils;
//        import weka.classifiers.Classifier;
//        import weka.classifiers.AbstractClassifier;
//
//public class WekaWrapper
//        extends AbstractClassifier {
//
//    /**
//     * Returns only the toString() method.
//     *
//     * @return a string describing the classifier
//     */
//    public String globalInfo() {
//        return toString();
//    }
//
//    /**
//     * Returns the capabilities of this classifier.
//     *
//     * @return the capabilities
//     */
//    public Capabilities getCapabilities() {
//        weka.core.Capabilities result = new weka.core.Capabilities(this);
//
//        result.enable(weka.core.Capabilities.Capability.NOMINAL_ATTRIBUTES);
//        result.enable(weka.core.Capabilities.Capability.NUMERIC_ATTRIBUTES);
//        result.enable(weka.core.Capabilities.Capability.DATE_ATTRIBUTES);
//        result.enable(weka.core.Capabilities.Capability.MISSING_VALUES);
//        result.enable(weka.core.Capabilities.Capability.NOMINAL_CLASS);
//        result.enable(weka.core.Capabilities.Capability.MISSING_CLASS_VALUES);
//
//
//        result.setMinimumNumberInstances(0);
//
//        return result;
//    }
//
//    /**
//     * only checks the data against its capabilities.
//     *
//     * @param i the training data
//     */
//    public void buildClassifier(Instances i) throws Exception {
//        // can classifier handle the data?
//        getCapabilities().testWithFail(i);
//    }
//
//    /**
//     * Classifies the given instance.
//     *
//     * @param i the instance to classify
//     * @return the classification result
//     */
//    public double classifyInstance(Instance i) throws Exception {
//        Object[] s = new Object[i.numAttributes()];
//
//        for (int j = 0; j < s.length; j++) {
//            if (!i.isMissing(j)) {
//                if (i.attribute(j).isNominal())
//                    s[j] = new String(i.stringValue(j));
//                else if (i.attribute(j).isNumeric())
//                    s[j] = new Double(i.value(j));
//            }
//        }
//
//        // set class value to missing
//        s[i.classIndex()] = null;
//
//        return WekaClassifier.classify(s);
//    }
//
//    /**
//     * Returns the revision string.
//     *
//     * @return        the revision
//     */
//    public String getRevision() {
//        return RevisionUtils.extract("1.0");
//    }
//
//    /**
//     * Returns only the classnames and what classifier it is based on.
//     *
//     * @return a short description
//     */
//    public String toString() {
//        return "Auto-generated classifier wrapper, based on weka.classifiers.trees.J48 (generated with Weka 3.8.0).\n" + this.getClass().getName() + "/WekaClassifier";
//    }
//
//    /**
//     * Runs the classfier from commandline.
//     *
//     * @param args the commandline arguments
//     */
//    public static void main(String args[]) {
//        runClassifier(new WekaWrapper(), args);
//    }
//}

class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N107925f60(i);
        return p;
    }
    static double N107925f60(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 103.03179) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 103.03179) {
            p = WekaClassifier.N6cfa2ed01(i);
        }
        return p;
    }
    static double N6cfa2ed01(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 1;
        } else if (((Double) i[64]).doubleValue() <= 20.031278) {
            p = WekaClassifier.N3982a1672(i);
        } else if (((Double) i[64]).doubleValue() > 20.031278) {
            p = 2;
        }
        return p;
    }
    static double N3982a1672(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 71.074484) {
            p = WekaClassifier.N152bcb953(i);
        } else if (((Double) i[2]).doubleValue() > 71.074484) {
            p = WekaClassifier.N5d303f656(i);
        }
        return p;
    }
    static double N152bcb953(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 47.245721) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > 47.245721) {
            p = WekaClassifier.N4ca40a534(i);
        }
        return p;
    }
    static double N4ca40a534(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 52.510344) {
            p = WekaClassifier.N711531b95(i);
        } else if (((Double) i[1]).doubleValue() > 52.510344) {
            p = 1;
        }
        return p;
    }
    static double N711531b95(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 492.23884) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() > 492.23884) {
            p = 1;
        }
        return p;
    }
    static double N5d303f656(Object []i) {
        double p = Double.NaN;
        if (i[18] == null) {
            p = 2;
        } else if (((Double) i[18]).doubleValue() <= 5.982521) {
            p = 2;
        } else if (((Double) i[18]).doubleValue() > 5.982521) {
            p = WekaClassifier.N160c20357(i);
        }
        return p;
    }
    static double N160c20357(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 104.068011) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > 104.068011) {
            p = 1;
        }
        return p;
    }
}