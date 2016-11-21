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

public class SimplexSolver {

    public SimplexSolver(double[] ofm, double[][] a, int[] b, double[] bVals) {
        mbaseSize = a.length;
        mvarsNumb = a[0].length;

        mmultipliers = new double[mvarsNumb];
        mblocked = new int[mvarsNumb];
        mlimitProfits = new double[mvarsNumb];
        malphaTbl = new double[mbaseSize][mvarsNumb];
        mbaseVector = new int[mbaseSize];
        mbaseVals = new double[mbaseSize];

        for (int i = 0; i < mbaseSize; i++) {
            mbaseVector[i] = b[i];
            mbaseVals[i] = bVals[i];
            System.arraycopy(a[i], 0, malphaTbl[i], 0, mvarsNumb);
        }

        for (int j = 0; j < mvarsNumb; j++) {
            mmultipliers[j] = ofm[j];
            mblocked[j] = 0;
        }

        mstepNumb = 0;
        mlineInd = 0;
        mcolumnInd = 0;
        mprofit = 0;
    }

    public int isVarBlocked(int ind) {
        if (ind < mvarsNumb && ind >= 0) {
            return mblocked[ind];
        } else {
            return 1;
        }
    }

    public double profit() {
        return mprofit;
    }

    public double[] solution() {
        msolution = new double[mvarsNumb];

        for (int j = 0; j < mvarsNumb; j++) {
            msolution[j] = 0;
        }

        for (int i = 0; i < mbaseSize; i++) {
            int baseVarInd = mbaseVector[i];
            msolution[baseVarInd] = mbaseVals[i];
        }
        return msolution;
    }

    public double[] calcLimitProfits() {
        for (int j = 0; j < mvarsNumb; j++) {
            double Zj = 0;
            if (isVarBlocked(j) == 0) {
                for (int i = 0; i < mbaseSize; i++) {
                    int var = mbaseVector[i];
                    Zj = Zj + malphaTbl[i][j] * mmultipliers[var];
                }
                mlimitProfits[j] = mmultipliers[j] - Zj;
            }
        }
        return mlimitProfits;
    }

    public boolean isOptimal() {
        boolean optimal = true;

        for (int j = 0; j < mvarsNumb; j++) {
            if (isVarBlocked(j) == 0) {
                if (mlimitProfits[j] > 0) {
                    optimal = false;
                    j = mvarsNumb + 1;
                }
            }
        }
        return optimal;
    }

    public boolean isFinite() {
        boolean finite = false;

        for (int j = 0; j < mvarsNumb; j++) {
            if (isVarBlocked(j) == 0) {
                if (mlimitProfits[j] > 0) {
                    boolean positive = false;
                    for (int i = 0; i < mbaseSize; i++) {
                        if (malphaTbl[i][j] > 0) {
                            positive = true;
                            i = mbaseSize + 1;
                        }
                    }
                    if (positive == false) {
                        finite = true;
                        j = mvarsNumb + 1;
                    }
                }
            }
        }
        return finite;
    }

    public void findVarIn() {
        double tmp = 0;
        int tmpInd = -1;

        for (int j = 0; j < mvarsNumb; j++) {
            if (isVarBlocked(j) == 0 && mlimitProfits[j] > 0 && mlimitProfits[j] > tmp) {
                tmp = mlimitProfits[j];
                tmpInd = j;
            }
        }
        mcolumnInd = tmpInd;
    }

    public void findVarOut() {
        double tmp = 0;
        int tmpInd = -1;

        for (int i = 0; i < mbaseSize; i++) {
            if (malphaTbl[i][mcolumnInd] > 0) {
                tmp = mbaseVals[i] / malphaTbl[i][mcolumnInd];
                tmpInd = i;
                i = mbaseSize + 1;
            }
        }
        for (int i = tmpInd + 1; i < mbaseSize; i++) {
            if (malphaTbl[i][mcolumnInd] > 0) {
                if (mbaseVals[i] / malphaTbl[i][mcolumnInd] < tmp) {
                    tmp = mbaseVals[i] / malphaTbl[i][mcolumnInd];
                    tmpInd = i;
                }
            }
        }
        mlineInd = tmpInd;
    }

    public void nextSimplexTbl() {
        double guideEl = malphaTbl[mlineInd][mcolumnInd];

        mbaseVector[mlineInd] = mcolumnInd;

        mbaseVals[mlineInd] = mbaseVals[mlineInd] / guideEl;
        for (int j = 0; j < mvarsNumb; j++) {
            malphaTbl[mlineInd][j] = malphaTbl[mlineInd][j] / guideEl;
        }

        for (int i = 0; i < mbaseSize; i++) {
            if (i != mlineInd) {
                double lineHdr = malphaTbl[i][mcolumnInd];
                mbaseVals[i] = mbaseVals[i] - lineHdr * mbaseVals[mlineInd];
                for (int j = 0; j < mvarsNumb; j++) {
                    malphaTbl[i][j] = malphaTbl[i][j] - lineHdr * malphaTbl[mlineInd][j];
                }
            }
        }
    }

    public void calcProfits() {
        mprofit = 0;
        for (int i = 0; i < mbaseVector.length; i++) {
            mprofit = mprofit + mbaseVals[i] * mmultipliers[mbaseVector[i]];
        }
    }

    @SuppressWarnings("empty-statement")
    public double[] findSolution() {
        calcLimitProfits();
        while (true) {
            if (isOptimal() || isFinite()) {
                break;
            } else {
                findVarIn();
                findVarOut();
                mstepNumb++;
                nextSimplexTbl();
                calcProfits();
                calcLimitProfits();
            }
        }
        return solution();
    }
    public int mbaseSize;
    public double[] msolution;
    public int mvarsNumb;
    public double mprofit;
    public int mstepNumb;
    public int[] mbaseVector;
    public double[][] malphaTbl;
    public double[] mmultipliers;
    public double[] mlimitProfits;
    public double[] mbaseVals;
    public int mlineInd;
    public int mcolumnInd;
    public int[] mblocked;
}
