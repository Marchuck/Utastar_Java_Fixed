/*
Copyright (C) 2013 mc_utastar Development Team

This file is part of mc_utastar.

mc_utastar is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

mc_utastar is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with RoomEdit. If not, see <http://www.gnu.org/licenses/>.
*/
package uta;

import core.UtaApp;
import java.util.logging.Level;

public class UtaSolver {

    public UtaSolver(MultiTbl tbl) {
        mdata = tbl;
        mcritNumb = mdata.mcritNumb;
        maltNumb = mdata.maltNumb;
        mthreashold = 0.05;
    }
    
    public double[][] marginalValFuncs() {
        return mmargValFuncs;
    }
    
    public double[] altScoring() {
        return maltScoring;
    }
    
    public void valFuncsOfU() {
        int mult = 0;
        int crit = 1;
        int valInd = 2;
        mvalFuncsOfU = new double[maltNumb][][];
        double[][] tmp = new double[mcritNumb * 2][3];
        for (int j = 0; j < maltNumb; j++) {
            int ind = 0;
            for (int i = 0; i < mcritNumb; i++) {
                if (mdata.mgradeTable[i][j] <= mdata.mlimits[i][0][0]) {
                    mdata.mgradeTable[i][j] = mdata.mlimits[i][0][0];
                    tmp[ind][mult] = 1;
                    tmp[ind][crit] = i + 1;
                    tmp[ind][valInd] = 1;
                    ind++;
                } else if (mdata.mgradeTable[i][j] >= mdata.mlimits[i][mdata.mlimits[i].length - 1][0]) {
                    mdata.mgradeTable[i][j] = mdata.mlimits[i][mdata.mlimits[i].length - 1][0];
                    tmp[ind][mult] = 1;
                    tmp[ind][crit] = i + 1;
                    tmp[ind][valInd] = mdata.mlimits[i].length;
                    ind++;
                } else {
                    for (int k = 1; k < mdata.mlimits[i].length; k++) {
                        if (mdata.mgradeTable[i][j] == mdata.mlimits[i][k][0]) {
                            tmp[ind][mult] = 1;
                            tmp[ind][crit] = i + 1;
                            tmp[ind][valInd] = k + 1;
                            ind++;
                            k = mdata.mlimits[i].length + 1;
                        } else if (mdata.mgradeTable[i][j] < mdata.mlimits[i][k][0]) {
                            double x = (mdata.mgradeTable[i][j] - mdata.mlimits[i][k - 1][0])
                                    / (mdata.mlimits[i][k][0] - mdata.mlimits[i][k - 1][0]);

                            tmp[ind][mult] = 1 - x;
                            tmp[ind][crit] = i + 1;
                            tmp[ind][valInd] = k;
                            ind++;

                            tmp[ind][mult] = x;
                            tmp[ind][crit] = i + 1;
                            tmp[ind][valInd] = k + 1;
                            ind++;

                            k = mdata.mlimits[i].length + 1;
                        }
                    }
                }
            }
            mvalFuncsOfU[j] = new double[ind][3];
            for (ind = 0; ind < mvalFuncsOfU[j].length; ind++) {
                mvalFuncsOfU[j][ind][mult] = tmp[ind][mult];
                mvalFuncsOfU[j][ind][crit] = tmp[ind][crit];
                mvalFuncsOfU[j][ind][valInd] = tmp[ind][valInd];
            }
        }

        String dbgStr = "\n numberOfAlternatives: " + maltNumb
                + "\n numberOfCriteria: " + mcritNumb;
        for (int i = 0; i < maltNumb; i++) {
            dbgStr += "\n\n value function of U (" + i + "):";
            for (int k = 0; k < mvalFuncsOfU[i].length; k++) {
                dbgStr += "\n[" + k + "] " + mvalFuncsOfU[i][k][0] + ", " + mvalFuncsOfU[i][k][1] + ", " + mvalFuncsOfU[i][k][2];
            }
        }
        UtaApp.mlog.log(Level.INFO, dbgStr);
    }

    public void valFuncsOfW() {
        mvalFuncsOfW = new double[maltNumb][mcritNumb][];

        for (int j = 0; j < maltNumb; j++) {
            for (int i = 0; i < mcritNumb; i++) {
                mvalFuncsOfW[j][i] = new double[mdata.mlimits[i].length - 1];
                for (int k = 0; k < mvalFuncsOfW[j][i].length; k++) {
                    mvalFuncsOfW[j][i][k] = 0;
                }
            }
        }

        for (int j = 0; j < maltNumb; j++) {
            for (int ind = 0; ind < mvalFuncsOfU[j].length; ind++) {
                double mult = mvalFuncsOfU[j][ind][0];
                int crit = (int) mvalFuncsOfU[j][ind][1];
                int valInd = (int) mvalFuncsOfU[j][ind][2];
                for (int k = 0; k < valInd - 1; k++) {
                    mvalFuncsOfW[j][crit - 1][k] =
                            mvalFuncsOfW[j][crit - 1][k] + mult;
                }
            }
        }

        String dbgStr = "";
        for (int j = 0; j < maltNumb; j++) {
            dbgStr += "\n\n value function of W (" + j + "):";
            for (int i = 0; i < mcritNumb; i++) {
                dbgStr += "\n[" + i + "]";
                for (int k = 0; k < mvalFuncsOfW[j][i].length; k++) {
                    dbgStr += mvalFuncsOfW[j][i][k] + ", ";
                }
                // get rid of the last ','
                dbgStr = dbgStr.substring(0, dbgStr.length() - 2);
            }
        }
        UtaApp.mlog.log(Level.INFO, dbgStr);
    }

    public void deltaValFuncs() {
        mdeltaValFuncs = new double[maltNumb - 1][mcritNumb][];

        for (int j = 0; j < maltNumb - 1; j++) {
            for (int i = 0; i < mcritNumb; i++) {
                mdeltaValFuncs[j][i] = new double[mdata.mlimits[i].length - 1];
                for (int k = 0; k < mdeltaValFuncs[j][i].length; k++) {
                    mdeltaValFuncs[j][i][k] = 0;
                }
            }
        }

        for (int j = 0; j < maltNumb - 1; j++) {
            for (int i = 0; i < mcritNumb; i++) {
                for (int k = 0; k < mdeltaValFuncs[j][i].length; k++) {
                    mdeltaValFuncs[j][i][k] = mvalFuncsOfW[j][i][k] - mvalFuncsOfW[j + 1][i][k];
                }
            }
        }


        String dbgStr = "";
        for (int j = 0; j < maltNumb - 1; j++) {
            dbgStr += "\n\n deltaFunctions (" + j + "):";
            for (int i = 0; i < mcritNumb; i++) {
                dbgStr += "\n[" + i + "]";
                for (int k = 0; k < mdeltaValFuncs[j][i].length; k++) {
                    dbgStr += mdeltaValFuncs[j][i][k] + ", ";
                }
                // get rid of the last ','
                dbgStr = dbgStr.substring(0, dbgStr.length() - 2);
            }
        }
        UtaApp.mlog.log(Level.INFO, dbgStr);
    }

    public void simplexTbl() {
        int numbOfW = 0;
        int numbGrEq = 0;
        int grEqInd = 0;

        double[] ofm;
        double[][] aa;
        int[] b;
        double[] bVals;

        int bSize = mdeltaValFuncs.length + 1;

        int varsNumb = 0;
        for (int i = 0; i < mcritNumb; i++) {
            for (int k = 0; k < mdeltaValFuncs[0][i].length; k++) {
                varsNumb++;
                numbOfW++;
            }
        }
        varsNumb = varsNumb + maltNumb * 2;

        for (int j = 0; j < maltNumb - 1; j++) {
            if (mdata.maltPreference[j] != mdata.maltPreference[j + 1]) {
                varsNumb++;
                numbGrEq++;
            }
        }
        varsNumb = varsNumb + maltNumb;

        ofm = new double[varsNumb];
        for (int j = 0; j < varsNumb; j++) {
            ofm[j] = 0;
        }

        aa = new double[bSize][varsNumb];
        for (int i = 0; i < bSize; i++) {
            for (int j = 0; j < varsNumb; j++) {
                aa[i][j] = 0;
            }
        }

        b = new int[bSize];
        for (int i = 0; i < bSize; i++) {
            b[i] = 0;
        }

        bVals = new double[bSize];
        for (int i = 0; i < bSize; i++) {
            bVals[i] = 0;
        }


        for (int j = numbOfW; j < numbOfW + maltNumb * 2; j++) {
            ofm[j] = -1;
        }

        for (int j = numbOfW + maltNumb * 2 + numbGrEq; j < varsNumb; j++) {
            ofm[j] = -2100000000;
        }
        for (int i = 0; i < bSize - 1; i++) {
            int j = 0;
            for (int l = 0; l < mcritNumb; l++) {
                for (int k = 0; k < mdeltaValFuncs[0][l].length; k++) {
                    aa[i][j] = mdeltaValFuncs[i][l][k];
                    j++;
                }
            }
            aa[i][numbOfW + i * 2] = -1;
            aa[i][numbOfW + i * 2 + 1] = 1;
            aa[i][numbOfW + i * 2 + 2] = 1;
            aa[i][numbOfW + i * 2 + 3] = -1;
            if (mdata.maltPreference[i] != mdata.maltPreference[i + 1]) {
                aa[i][numbOfW + maltNumb * 2 + grEqInd] = -1;
                grEqInd++;
            }
            aa[i][numbOfW + maltNumb * 2 + numbGrEq + i] = 1;
        }

        for (int j = 0; j < numbOfW; j++) {
            aa[bSize - 1][j] = 1;
        }
        aa[bSize - 1][varsNumb - 1] = 1;

        for (int i = 0; i < bSize; i++) {
            b[i] = numbOfW + maltNumb * 2 + numbGrEq + i;
        }

        for (int i = 0; i < bSize - 1; i++) {
            if (mdata.maltPreference[i] != mdata.maltPreference[i + 1]) {
                bVals[i] = mthreashold;
            }
        }
        bVals[bSize - 1] = 1;

        mstdForm = new SimplexSolver(ofm, aa, b, bVals);

    }

    public void complementarySimplexTbl(int ind, double el) {
        int numbOfW = 0;
        int numbGrEq = 0;
        int grEqInd = 0;

        double[] ofm;
        double[][] aa;
        int[] b;
        double[] bVals;

        int bSize = mdeltaValFuncs.length + 1 + 1;
        int numbOfVars = 0;
        for (int i = 0; i < mcritNumb; i++) {
            for (int k = 0; k < mdeltaValFuncs[0][i].length; k++) {
                numbOfVars++;
                numbOfW++;
            }
        }
        numbOfVars = numbOfVars + maltNumb * 2;

        for (int j = 0; j < maltNumb - 1; j++) {
            if (mdata.maltPreference[j] != mdata.maltPreference[j + 1]) {
                numbOfVars++;
                numbGrEq++;
            }
        }
        numbOfVars = numbOfVars + maltNumb + 1;

        ofm = new double[numbOfVars];
        for (int j = 0; j < numbOfVars; j++) {
            ofm[j] = 0;
        }

        aa = new double[bSize][numbOfVars];
        for (int i = 0; i < bSize; i++) {
            for (int j = 0; j < numbOfVars; j++) {
                aa[i][j] = 0;
            }
        }

        b = new int[bSize];
        for (int i = 0; i < bSize; i++) {
            b[i] = 0;
        }

        bVals = new double[bSize];
        for (int i = 0; i < bSize; i++) {
            bVals[i] = 0;
        }

        int strt = 0;
        for (int i = 0; i < ind; i++) {
            strt = strt + mdeltaValFuncs[0][i].length;
        }
        int end = strt + mdeltaValFuncs[0][ind].length;
        for (int j = strt; j < end; j++) {
            ofm[j] = 1;
        }

        for (int j = numbOfW + maltNumb * 2 + numbGrEq; j < numbOfVars; j++) {
            ofm[j] = -2100000000;
        }
        for (int i = 0; i < bSize - 1 - 1; i++) {
            int j = 0;
            for (int l = 0; l < mcritNumb; l++) {
                for (int k = 0; k < mdeltaValFuncs[0][l].length; k++) {
                    aa[i][j] = mdeltaValFuncs[i][l][k];
                    j++;
                }
            }
            aa[i][numbOfW + i * 2] = -1;
            aa[i][numbOfW + i * 2 + 1] = 1;
            aa[i][numbOfW + i * 2 + 2] = 1;
            aa[i][numbOfW + i * 2 + 3] = -1;
            if (mdata.maltPreference[i] != mdata.maltPreference[i + 1]) {
                aa[i][numbOfW + maltNumb * 2 + grEqInd] = -1;
                grEqInd++;
            }
            aa[i][numbOfW + maltNumb * 2 + numbGrEq + i] = 1;
        }

        for (int j = 0; j < numbOfW; j++) {
            aa[bSize - 1 - 1][j] = 1;
        }
        aa[bSize - 1 - 1][numbOfVars - 1 - 1] = 1;

        for (int j = numbOfW; j < numbOfW + maltNumb * 2; j++) {
            aa[bSize - 1][j] = 1;
        }
        aa[bSize - 1][numbOfVars - 1] = 1;

        for (int i = 0; i < bSize; i++) {
            b[i] = numbOfW + maltNumb * 2 + numbGrEq + i;
        }

        for (int i = 0; i < bSize - 1 - 1; i++) {
            if (mdata.maltPreference[i] != mdata.maltPreference[i + 1]) {
                bVals[i] = mthreashold;
            }
        }
        bVals[bSize - 1 - 1] = 1;
        bVals[bSize - 1] = mstdForm.profit() + el;

        maltForms[ind] = new SimplexSolver(ofm, aa, b, bVals);

    }

    public double[][] UtaSolve(double e) {
        valFuncsOfU();
        valFuncsOfW();
        deltaValFuncs();
        simplexTbl();
        maverageWeight = new double[mcritNumb][];
        mmargValFuncs = new double[mcritNumb][];

        maltForms = new SimplexSolver[mcritNumb];
        msensitivity = new double[mcritNumb][];

        for (int i = 0; i < mcritNumb; i++) {
            complementarySimplexTbl(i, e);
            msensitivity[i] = maltForms[i].findSolution();
        }

        double tmp[] = new double[msensitivity[0].length];
        for (int i = 0; i < msensitivity[0].length; i++) {
            tmp[i] = 0;
            for (int j = 0; j < msensitivity.length; j++) {
                tmp[i] = tmp[i] + msensitivity[j][i];
            }
            tmp[i] = tmp[i] / 3;
        }

        for (int i = 0; i < mcritNumb; i++) {
            maverageWeight[i] = new double[mdeltaValFuncs[0][i].length];
        }

        int k = 0;
        for (int i = 0; i < mcritNumb; i++) {
            for (int j = 0; j < mdeltaValFuncs[0][i].length; j++) {
                maverageWeight[i][j] = tmp[k];
                k = k + 1;
            }
        }

        for (int i = 0; i < mcritNumb; i++) {
            mmargValFuncs[i] = new double[mdeltaValFuncs[0][i].length + 1];
        }

        for (int i = 0; i < mcritNumb; i++) {
            tmp[i] = 0;
            int j;
            for (j = 0; j < mdeltaValFuncs[0][i].length; j++) {
                mmargValFuncs[i][j] = tmp[i];
                tmp[i] = tmp[i] + maverageWeight[i][j];
            }
            mmargValFuncs[i][j] = tmp[i];
        }

        maltScoring = new double[maltNumb];

        for (int i = 0; i < maltNumb; i++) {
            maltScoring[i] = 0;
            for (int j = 0; j < mcritNumb; j++) {
                for (k = 0; k < mdeltaValFuncs[0][j].length; k++) {
                    maltScoring[i] = maltScoring[i] + maverageWeight[j][k] * mvalFuncsOfW[i][j][k];
                }
            }
        }

        return maverageWeight;
    }

    private double[][][] mvalFuncsOfU;
    private int mcritNumb;
    private MultiTbl mdata;
    private int maltNumb;
    private double[][][] mdeltaValFuncs;
    private double[][] msensitivity;
    private SimplexSolver[] maltForms;
    private SimplexSolver mstdForm;
    private double maverageWeight[][];
    private double[][][] mvalFuncsOfW;
    private double mmargValFuncs[][];
    private double mthreashold;
    private double maltScoring[];
}
