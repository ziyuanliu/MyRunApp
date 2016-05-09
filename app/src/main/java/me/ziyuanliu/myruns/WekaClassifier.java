package me.ziyuanliu.myruns;

/**
 * Created by ziyuanliu on 5/8/16.
 */
class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N6113f9230(i);
        return p;
    }
    static double N6113f9230(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 824.653438) {
            p = WekaClassifier.N1e1561991(i);
        } else if (((Double) i[0]).doubleValue() > 824.653438) {
            p = WekaClassifier.N6227404417(i);
        }
        return p;
    }
    static double N1e1561991(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 156.513681) {
            p = WekaClassifier.N5f5285862(i);
        } else if (((Double) i[0]).doubleValue() > 156.513681) {
            p = WekaClassifier.N4adfd2505(i);
        }
        return p;
    }
    static double N5f5285862(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 0;
        } else if (((Double) i[6]).doubleValue() <= 2.076906) {
            p = 0;
        } else if (((Double) i[6]).doubleValue() > 2.076906) {
            p = WekaClassifier.N15756763(i);
        }
        return p;
    }
    static double N15756763(Object []i) {
        double p = Double.NaN;
        if (i[22] == null) {
            p = 1;
        } else if (((Double) i[22]).doubleValue() <= 0.440722) {
            p = 1;
        } else if (((Double) i[22]).doubleValue() > 0.440722) {
            p = WekaClassifier.N7ff36d9e4(i);
        }
        return p;
    }
    static double N7ff36d9e4(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 1;
        } else if (((Double) i[6]).doubleValue() <= 3.451732) {
            p = 1;
        } else if (((Double) i[6]).doubleValue() > 3.451732) {
            p = 0;
        }
        return p;
    }
    static double N4adfd2505(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 1;
        } else if (((Double) i[12]).doubleValue() <= 31.62932) {
            p = WekaClassifier.N1ea10386(i);
        } else if (((Double) i[12]).doubleValue() > 31.62932) {
            p = 2;
        }
        return p;
    }
    static double N1ea10386(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 17.483805) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > 17.483805) {
            p = WekaClassifier.N3ab512957(i);
        }
        return p;
    }
    static double N3ab512957(Object []i) {
        double p = Double.NaN;
        if (i[23] == null) {
            p = 1;
        } else if (((Double) i[23]).doubleValue() <= 2.322014) {
            p = WekaClassifier.N5ece07c08(i);
        } else if (((Double) i[23]).doubleValue() > 2.322014) {
            p = WekaClassifier.N293e39fb11(i);
        }
        return p;
    }
    static double N5ece07c08(Object []i) {
        double p = Double.NaN;
        if (i[16] == null) {
            p = 1;
        } else if (((Double) i[16]).doubleValue() <= 1.517605) {
            p = WekaClassifier.N6a37131b9(i);
        } else if (((Double) i[16]).doubleValue() > 1.517605) {
            p = 1;
        }
        return p;
    }
    static double N6a37131b9(Object []i) {
        double p = Double.NaN;
        if (i[22] == null) {
            p = 1;
        } else if (((Double) i[22]).doubleValue() <= 1.507588) {
            p = WekaClassifier.N3b0db5b510(i);
        } else if (((Double) i[22]).doubleValue() > 1.507588) {
            p = 2;
        }
        return p;
    }
    static double N3b0db5b510(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 2;
        } else if (((Double) i[5]).doubleValue() <= 7.494539) {
            p = 2;
        } else if (((Double) i[5]).doubleValue() > 7.494539) {
            p = 1;
        }
        return p;
    }
    static double N293e39fb11(Object []i) {
        double p = Double.NaN;
        if (i[15] == null) {
            p = 0;
        } else if (((Double) i[15]).doubleValue() <= 2.393725) {
            p = 0;
        } else if (((Double) i[15]).doubleValue() > 2.393725) {
            p = WekaClassifier.N7e0db1b12(i);
        }
        return p;
    }
    static double N7e0db1b12(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 1;
        } else if (((Double) i[6]).doubleValue() <= 70.308681) {
            p = WekaClassifier.N551daf3713(i);
        } else if (((Double) i[6]).doubleValue() > 70.308681) {
            p = 0;
        }
        return p;
    }
    static double N551daf3713(Object []i) {
        double p = Double.NaN;
        if (i[20] == null) {
            p = 2;
        } else if (((Double) i[20]).doubleValue() <= 2.212718) {
            p = 2;
        } else if (((Double) i[20]).doubleValue() > 2.212718) {
            p = WekaClassifier.N36b083c14(i);
        }
        return p;
    }
    static double N36b083c14(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 40.31737) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() > 40.31737) {
            p = WekaClassifier.N5bc2184b15(i);
        }
        return p;
    }
    static double N5bc2184b15(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 61.032354) {
            p = WekaClassifier.N5a7b282b16(i);
        } else if (((Double) i[5]).doubleValue() > 61.032354) {
            p = 1;
        }
        return p;
    }
    static double N5a7b282b16(Object []i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 1;
        } else if (((Double) i[11]).doubleValue() <= 11.899088) {
            p = 1;
        } else if (((Double) i[11]).doubleValue() > 11.899088) {
            p = 2;
        }
        return p;
    }
    static double N6227404417(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() <= 11.368559) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() > 11.368559) {
            p = 2;
        }
        return p;
    }
}
